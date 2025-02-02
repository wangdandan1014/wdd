package com.sensing.core.datasave;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.bean.Channel;
import com.sensing.core.bean.KafkaCapMsgM.pbcapturemsg;
import com.sensing.core.bean.MotorVehicle;
import com.sensing.core.bean.NonmotorVehicle;
import com.sensing.core.bean.Person;
import com.sensing.core.bean.Task;
import com.sensing.core.clickhouseDao.IMotorVehicleCKDAO;
import com.sensing.core.clickhouseDao.INonmotorVehicleCKDAO;
import com.sensing.core.clickhouseDao.IPersonCKDAO;
import com.sensing.core.dao.ITaskDAO;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.httputils.HttpDeal;

/**
 * 抓拍的数据保存到ck的实现类
 * <p>Title: DataSaveService</p>
 * <p>Description: 将抓拍的数据保存到clickhouse中</p>
 * <p>Company: www.sensingtech.com</p>
 * 
 * @author mingxingyu
 * @date 2019年05月11日
 * @version 1.0
 */
public class CKDataSaveService implements Runnable {

	private IPersonCKDAO personCKDAO;
	private IMotorVehicleCKDAO motorVehicleCKDAO;
	private INonmotorVehicleCKDAO nonmotorVehicleCKDAO;
	private ITaskDAO taskDAO;
	private pbcapturemsg msg;
	private Log log = LogFactory.getLog(CKDataSaveService.class);

	public CKDataSaveService(pbcapturemsg pbcm) {
		this.msg = pbcm;
	}

	public CKDataSaveService(pbcapturemsg pbcm, IPersonCKDAO personCKDAO, IMotorVehicleCKDAO motorVehicleCKDAO,
			INonmotorVehicleCKDAO nonmotorVehicleCKDAO, ITaskDAO taskDAO) {
		this.msg = pbcm;
		this.personCKDAO = personCKDAO;
		this.motorVehicleCKDAO = motorVehicleCKDAO;
		this.nonmotorVehicleCKDAO = nonmotorVehicleCKDAO;
		this.taskDAO = taskDAO;
	}

	// 将文件保存到文件服务器
	// 前端还要能预览到文件
	
	/**
	 * 
	 * 说明：
	 * 1、历史视频接收的数据保存
	 * 		可将capLocation字段修改为643,499,148,410_0e656bfd48f44dc2ba6d7ca62d0fb04c[任务的uuid]，后台拆分保存，比对服务不保存该字段。
	 * 2、离线视频的抓拍保存
	 * 		通过判断通道的reserved，判断抓拍关联的通道是不是本地视频
	 */
	public void run() {
		
		//历史视频的标记
		boolean recordFlag = false;
		if ( StringUtils.isNotEmpty(msg.getCapLocation()) && msg.getCapLocation().contains("_") ) {
			recordFlag = true;
		}
		
		String deviceId = msg.getDeviceId();
		List<Channel> list = taskDAO.queryDeviceByDeviceId(deviceId);
		Integer reserved = null;
		if (list != null && list.size() > 0) {
			Channel channel = list.get(0);
			reserved = channel.getReserved();
		}
		
		Integer[] capTypesArr = DataSaveCache.deviceIdMap.get(deviceId);
		//历史视频的处理
		String taskUuid = null;
		if ( recordFlag ) {
			capTypesArr = new Integer[1];
			capTypesArr[0] = msg.getCapType();
			taskUuid = msg.getCapLocation().split("_")[1];
		}
		
		log.info("CKDataSaveService;抓拍的uuid:" + msg.getUuid() + ";通道的uuid:" + deviceId + ";该通道支持的抓拍类型:"
				+ ((capTypesArr == null || capTypesArr.length == 0) ? "" : Arrays.toString(capTypesArr)) + "reserved值为:"
				+ reserved + ";抓拍传递的CapType类型为:" + msg.getCapType());
		
		if ((capTypesArr == null || capTypesArr.length == 0 || Arrays.binarySearch(capTypesArr, msg.getCapType()) < 0) && reserved != Constants.CHANNEL_RESERVER_LOCAL) {
			// 删除比对服务中的特征文件
			// 组装请求的参数
			JSONObject params = new JSONObject();
			// 组装请求的参数
			params.put("type", msg.getCapType());
			params.put("uuid", msg.getUuid());
			String cmpResult = HttpDeal.sendPost(Constants.CMP_DELETE_URL, JSONObject.toJSONString(params));

			if (StringUtils.isNotEmpty(cmpResult)) {
				Map<String, Object> resultObject = JSONObject.parseObject(cmpResult);
				if (!resultObject.get("error").equals(0)) {
					log.error("删除比对的特征文件发生错误:" + cmpResult + ";抓拍的uuid:" + msg.getUuid() + ";通道的uuid是:"
							+ msg.getDeviceId() + ";抓拍类型是:" + msg.getCapType());
				}
			}
			log.info("CKDataSaveService;抓拍的uuid:" + msg.getUuid() + ";特征已删除退回");
			return;
		}

		DataSaveCache.shNum.getAndIncrement();

		if (Constants.CAP_ANALY_TYPE_PERSON == msg.getCapType() && ((capTypesArr != null && capTypesArr.length != 0
				&& Arrays.binarySearch(capTypesArr, Constants.CAP_ANALY_TYPE_PERSON) > -1)
				|| reserved == Constants.CHANNEL_RESERVER_LOCAL)) {
			log.info("开始进行保存行人数据：reserved：" + reserved + "抓拍的uuid:" + msg.getUuid() + ";通道的uuid是:" + msg.getDeviceId());
			Person document = new Person();
			document.setUuid(msg.getUuid());
			if (capTypesArr != null && capTypesArr.length > 0) {
				document.setCapFlag(Constants.TASK_TYPE_REALTIME);
			}
			if (reserved == Constants.CHANNEL_RESERVER_LOCAL) {
				document.setCapFlag(Constants.TASK_TYPE_OFFLINE);
			}
			document.setDeviceId(msg.getDeviceId());
			document.setCapTime((long) msg.getCapTime());
			document.setCapDate(new Date());
			document.setFrameTime(msg.getFrameTime());
			document.setAge(msg.getAge());
			document.setGenderCode(msg.getGenderCode());
			document.setBagStyle(msg.getBagStyle());
			document.setBigBagStyle(msg.getBigBagStyle());
			document.setOrientation(msg.getOrientation());
			document.setMotion(msg.getMotion());
			document.setCap(msg.getCap());
			document.setGlass(msg.getGlass());
			document.setCoatColor(msg.getCoatColor());
			document.setCoatLength(msg.getCoatLength());
			document.setCoatTexture(msg.getCoatTexture());
			document.setTrousersColor(msg.getTrousersColor());
			document.setTrousersLen(msg.getTrousersLen());
			document.setTrousersTexture(msg.getTrousersTexture());
			document.setCapLocation(recordFlag?msg.getCapLocation().split("_")[0]:msg.getCapLocation());
			document.setCapUrl(msg.getCapUrl());
			document.setSeceneUrl(msg.getSeceneUrl());
			document.setRespirator(msg.getRespirator());
			try {
				List<String> taskUuids = new ArrayList<>();
				if (StringUtils.isNotEmpty(msg.getDeviceId())) {
					if ( !recordFlag ) {
						//实时和离线的任务需要查询关联任务的uuid。历史的任务的uuid在capLocation中，不需要查询
						List<Task> tasks = taskDAO.queryTaskRunningUuidsByDeviceId(msg.getDeviceId());
						if (tasks != null && tasks.size() > 0) {
							taskUuids = tasks.stream().map(j -> j.getUuid()).collect(Collectors.toList());
						}
					}else{
						taskUuids.add(taskUuid);
					}
				}
				int savePerson = personCKDAO.savePerson(document, taskUuids);
				DataSaveCache.faNum.getAndIncrement();
				log.info("CKDataSaveService;抓拍的uuid:" + msg.getUuid() + ";行人已保存;保存的标记:" + savePerson + ";任务的uuid:"
						+ taskUuids.toString() + ";deviceId:" + deviceId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (Constants.CAP_ANALY_TYPE_MOTOR_VEHICLE == msg.getCapType()
				&& ((capTypesArr != null && capTypesArr.length != 0
						&& Arrays.binarySearch(capTypesArr, Constants.CAP_ANALY_TYPE_MOTOR_VEHICLE) > -1)
						|| reserved == Constants.CHANNEL_RESERVER_LOCAL)) {
			log.info(
					"开始进行保存机动车数据：reserved：" + reserved + "抓拍的uuid:" + msg.getUuid() + ";通道的uuid是:" + msg.getDeviceId());
			MotorVehicle document = new MotorVehicle();
			document.setUuid(msg.getUuid());
			if (capTypesArr != null && capTypesArr.length > 0) {
				document.setCapFlag(Constants.TASK_TYPE_REALTIME);
			}
			if (reserved == Constants.CHANNEL_RESERVER_LOCAL) {
				document.setCapFlag(Constants.TASK_TYPE_OFFLINE);
			}
			document.setDeviceId(msg.getDeviceId());
			document.setCapTime((long) msg.getCapTime());
			document.setCapDate(new Date());
			document.setFrameTime(msg.getFrameTime());
			document.setPlateNo(msg.getPlateNo());
			document.setVehicleColor(msg.getVehicleColor());
			document.setOrientation(msg.getOrientation());
			document.setPlateColor(msg.getPlateColor());
			document.setVehicleClass(msg.getVehicleClass());
			document.setPlateClass(msg.getPlateClass());
			document.setVehicleBrandTag(msg.getVehicleBrandTag());
			document.setVehicleModelTag(msg.getVehicleModelTag());
			document.setVehicleStylesTag(msg.getVehicleStylesTag());
			document.setVehicleMarkerMot(msg.getVehicleMarkerMot());
			document.setVehicleMarkerTissuebox(msg.getVehicleMarkerTissuebox());
			document.setVehicleMarkerPendant(msg.getVehicleMarkerPendant());
			document.setSunvisor(msg.getSunvisor());
			document.setCapLocation(recordFlag?msg.getCapLocation().split("_")[0]:msg.getCapLocation());
			document.setCapUrl(msg.getCapUrl());
			document.setSeceneUrl(msg.getSeceneUrl());

			// 机动车：正面、朝右：无打电话 安全带系 安全带系 背面、朝左：未知 未知 未知
			// 正面、朝右
			if (msg.getOrientation() == 1 || msg.getOrientation() == 4) {
				document.setSafetyBelt(1);
				document.setSafetyBeltCopilot(1);
				document.setCalling(2);
				// 背面、朝左
			} else if (msg.getOrientation() == 2 || msg.getOrientation() == 3) {
				document.setSafetyBelt(0);
				document.setSafetyBeltCopilot(0);
				document.setCalling(0);
			} else {
				document.setSafetyBelt(msg.getSafetyBelt());
				document.setSafetyBeltCopilot(msg.getSafetyBeltCopilot());
				document.setCalling(msg.getCalling());
			}
			try {
				List<String> taskUuids = new ArrayList<>();
				if (StringUtils.isNotEmpty(msg.getDeviceId())) {
					if ( !recordFlag ) {
						List<Task> tasks = taskDAO.queryTaskRunningUuidsByDeviceId(msg.getDeviceId());
						if (tasks != null && tasks.size() > 0) {
							taskUuids = tasks.stream().map(j -> j.getUuid()).collect(Collectors.toList());
						}
					}else{
						taskUuids.add(taskUuid);
					}
				}
				int saveMotorVehicle = motorVehicleCKDAO.saveMotorVehicle(document, taskUuids);
				DataSaveCache.faNum.getAndIncrement();
				log.info("CKDataSaveService;抓拍的uuid:" + msg.getUuid() + ";机动车已保存;保存的标记:" + saveMotorVehicle
						+ ";任务的uuid:" + taskUuids.toString() + ";deviceId:" + deviceId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (Constants.CAP_ANALY_TYPE_NONMOTOR_VEHICLE == msg.getCapType()
				&& ((capTypesArr != null && capTypesArr.length != 0
						&& Arrays.binarySearch(capTypesArr, Constants.CAP_ANALY_TYPE_NONMOTOR_VEHICLE) > -1)
						|| reserved == Constants.CHANNEL_RESERVER_LOCAL)) {
			log.info("开始进行保存非机动车数据：reserved：" + reserved + "抓拍的uuid:" + msg.getUuid() + ";通道的uuid是:"
					+ msg.getDeviceId());
			NonmotorVehicle document = new NonmotorVehicle();
			document.setUuid(msg.getUuid());
			if (capTypesArr != null && capTypesArr.length > 0) {
				document.setCapFlag(Constants.TASK_TYPE_REALTIME);
			}
			if (reserved == Constants.CHANNEL_RESERVER_LOCAL) {
				document.setCapFlag(Constants.TASK_TYPE_OFFLINE);
			}
			document.setDeviceId(msg.getDeviceId());
			document.setCapTime((long) msg.getCapTime());
			document.setCapDate(new Date());
			document.setFrameTime(msg.getFrameTime());
			document.setAge(msg.getAge());
			document.setGenderCode(msg.getGenderCode());
			document.setOrientation(msg.getOrientation());
			document.setVehicleColor(msg.getVehicleColor());
			document.setVehicleClass(msg.getVehicleClass());
			document.setMotion(msg.getMotion());
			document.setCap(msg.getCap());
			document.setGlass(msg.getGlass());
			document.setCoatColor(msg.getCoatColor());
			document.setCoatLength(msg.getCoatLength());
			document.setCoatTexture(msg.getCoatTexture());
			document.setCapLocation(recordFlag?msg.getCapLocation().split("_")[0]:msg.getCapLocation());
			document.setCapUrl(msg.getCapUrl());
			document.setSeceneUrl(msg.getSeceneUrl());
			document.setRespirator(msg.getRespirator());
			try {
				List<String> taskUuids = new ArrayList<>();
				if (StringUtils.isNotEmpty(msg.getDeviceId())) {
					if ( !recordFlag ) {
						List<Task> tasks = taskDAO.queryTaskRunningUuidsByDeviceId(msg.getDeviceId());
						if (tasks != null && tasks.size() > 0) {
							taskUuids = tasks.stream().map(j -> j.getUuid()).collect(Collectors.toList());
						}
					}else{
						taskUuids.add(taskUuid);
					}
				}
				int saveNonmotorVehicle = nonmotorVehicleCKDAO.saveNonmotorVehicle(document, taskUuids);
				DataSaveCache.faNum.getAndIncrement();
				log.info("CKDataSaveService;抓拍的uuid:" + msg.getUuid() + ";非机动车已保存;保存的标记:" + saveNonmotorVehicle
						+ ";任务的uuid:" + taskUuids.toString() + ";deviceId:" + deviceId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
