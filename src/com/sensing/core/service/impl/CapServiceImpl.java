package com.sensing.core.service.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.stereotype.Service;

import com.sensing.core.bean.MotorVehicle;
import com.sensing.core.bean.NonmotorVehicle;
import com.sensing.core.bean.Person;
import com.sensing.core.service.CaptureThriftService;
import com.sensing.core.service.ICapAttrConvertService;
import com.sensing.core.service.ICapService;
import com.sensing.core.thrift.cap.bean.CapFaceDetectResult;
import com.sensing.core.thrift.cap.bean.CapFacesSet;
import com.sensing.core.thrift.cap.bean.CapReturn;
import com.sensing.core.thrift.cmp.bean.FeatureInfo;
import com.sensing.core.thrift.cap.bean.CapFeaturesSet;
import com.sensing.core.thrift.cap.bean.CapMotorResult;
import com.sensing.core.thrift.cap.bean.CapNonmotorResult;
import com.sensing.core.thrift.cap.bean.CapPeopleResult;
import com.sensing.core.utils.Exception.BussinessException;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.props.RemoteInfoUtil;

/**
 * 调用抓拍服务的工具类
 * <p>Title: CapServiceImpl</p>
 * <p>Description:</p>
 * <p>Company: www.sensingtech.com</p> 
 * @author	mingxingyu
 * @date	2018年9月13日
 * @version 1.0
 */
@Service
public class CapServiceImpl implements ICapService {

	@Resource
	private ICapAttrConvertService capAttrConvertService;
	@Resource
	public CaptureThriftService captureThritService;

	private static final Log log = LogFactory.getLog(ICapService.class);


	/**
	 * 打开通道画框
	 * @param channelUuid  通道的uuid
	 * @return	接口调用返回值
	 * @throws Exception
	 * @author mingxingyu
	 * @date   2018年12月6日 下午3:35:54
	 */
	public Integer openChannelFrame(String channelUuid)throws Exception{
		try {
			String param = "{\"uuid\":\""+channelUuid+"\"}";
			CapReturn capReturn = captureThritService.NotifyMessage(104,param);
			int res = capReturn.res;
			return res;
		} catch (Exception e) {
			log.error("调用抓拍打开通道画框发生异常."+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 根据图片的byte数组获取图片中的抓拍信息
	 * @param imgUrl
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date   2018年9月13日 上午10:31:07
	 */
	public Map<String, Object> queryCap(byte[] imgBytes) throws Exception {
		Map<String, Object> countMap = new HashMap<String, Object>(); // 最终要返回的结果集

		// step1:调用抓拍接口，获取特征信息
		List<CapFeaturesSet> features = null;// 特征列表
		ByteBuffer byteBuffer = ByteBuffer.wrap(imgBytes);
		List<Object> resultList = new ArrayList<Object>();
		// 开启抓拍
		TTransport transport = new TSocket(RemoteInfoUtil.CAP_SERVER_IP,RemoteInfoUtil.CAP_SERVER_PORT,
				RemoteInfoUtil.CAP_TIMEOUT);
		com.sensing.core.thrift.cap.service.CaptureServer.Client client = null;
		if (!transport.isOpen()) {
			try {
				transport.open();
			} catch (TTransportException e) {
				log.error("抓拍服务连接异常！", e.fillInStackTrace());
				e.printStackTrace();
				if (transport.isOpen()) {
					transport.close();
				}
				throw new Exception("抓拍服务连接异常！");
			}
		}
		// 调用抓拍
		TProtocol protocol = new TBinaryProtocol(transport);
		client = new com.sensing.core.thrift.cap.service.CaptureServer.Client(protocol);
		try {
			features = client.DetectFeatures(byteBuffer);// 调用抓拍,返回特征值列表

			for (CapFeaturesSet capFeaturesSet : features) {
				List<CapPeopleResult> lstPeople = capFeaturesSet.getLstPeople();
				List<CapMotorResult> lstMotors = capFeaturesSet.getLstMotors();
				List<CapNonmotorResult> lstNonmotors = capFeaturesSet.getLstNonmotors();
				if (!lstPeople.isEmpty()) {
					for (CapPeopleResult capPeopleResult : lstPeople) {
						Person people = new Person(capPeopleResult);
						Person capPeopleConvert = capAttrConvertService.personConvert(people);
						resultList.add(capPeopleConvert);
					}
				}
				if (!lstMotors.isEmpty()) {
					for (CapMotorResult capMotorResult : lstMotors) {
						MotorVehicle capMotor = new MotorVehicle(capMotorResult);
						MotorVehicle capMotorConvert = capAttrConvertService.motorVehicleConvert(capMotor,null);
						resultList.add(capMotorConvert);
					}
				}
				if (!lstNonmotors.isEmpty()) {
					for (CapNonmotorResult capNonmotorResult : lstNonmotors) {
						NonmotorVehicle capNonmotor = new NonmotorVehicle(capNonmotorResult);
						NonmotorVehicle capNonmotorConvert = capAttrConvertService.nonmotorVehicleConvert(capNonmotor);
						resultList.add(capNonmotorConvert);
					}
				}
			}

			if (transport.isOpen()) {
				transport.close();
			}
		} catch (Exception e) {
			log.error("抓拍信息处理发生异常", e);
			e.getStackTrace();
			throw new Exception("抓拍信息处理发生异常！");
		}
		countMap.put("resultList", resultList);
		return countMap;
	}

	
	/**
	 * 根据图片的base64，获取该图片的特征文件
	 * @param base64
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date   2018年9月11日 上午11:25:20
	 */
	public FeatureInfo getFeaByImgBase(String base64) throws Exception {
		byte[] bs = StringUtils.base64String2ByteFun(base64);
		return getFeaByImgBytes(bs);
	}
	
	/**
	 * 根据图片的byte数组，获取该图片的特征文件
	 * @param imageBytes
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date   2018年9月11日 上午11:25:20
	 */
	public FeatureInfo getFeaByImgBytes(byte[] imageBytes) throws Exception {

		if (imageBytes == null || imageBytes.length == 0) {
			throw new BussinessException("图片信息不存在。");
		}

		List<ByteBuffer> lstImgs = new ArrayList<ByteBuffer>();
		List<CapFacesSet> detectFaces = new ArrayList<CapFacesSet>();
		ByteBuffer feature = null;
		ByteBuffer byteBuffer = ByteBuffer.wrap(imageBytes);
		if (byteBuffer != null) {
			lstImgs.add(byteBuffer);
		} else {
			throw new BussinessException("获取图片特征文件的时候，图片传递有误，请确认！");
		}
		
		TTransport transport = null;
		transport = new TSocket(RemoteInfoUtil.CAP_SERVER_IP,RemoteInfoUtil.CAP_SERVER_PORT, RemoteInfoUtil.CAP_TIMEOUT);
		com.sensing.core.thrift.cap.service.CaptureServer.Client client = null;
		if (!transport.isOpen()) {
			try {
				transport.open();
			} catch (TTransportException e) {
				log.error("抓拍服务连接异常！", e.fillInStackTrace());
				e.printStackTrace();
				if (transport.isOpen()) {
					transport.close();
				}
				throw new Exception("抓拍服务连接异常！");
			}
		}
		TProtocol protocol = new TBinaryProtocol(transport);
		client = new com.sensing.core.thrift.cap.service.CaptureServer.Client(protocol);;
		try {
			detectFaces = client.DetectFaces(lstImgs);// 调用抓拍
			if (transport.isOpen()) { 
				transport.close();
			}
		} catch (Exception e) {
			log.error("抓拍信息处理发生异常", e);
			e.getStackTrace();
			throw new Exception("抓拍信息处理发生异常！");
		}

		// 赋特征值：如果有两个特征值，则获取最大的一个；计算方法：人脸特征的 脸宽度 X人脸高度 作为比较依据
		if (detectFaces != null && detectFaces.size() > 0) {
			// 判断是否有返回值
			CapFacesSet facesInfo = detectFaces.get(0);
			if (facesInfo != null && facesInfo.getLstFaces().size() > 0) {
				/*
				 * 需要调用抓拍服务的接口；获取ft_fea(模板特征)、face_x(模版人脸X坐标)、face_y(模版人脸Y坐标)、
				 * face_cx(模版人脸宽度)、face_cy(模版人脸高度)、ft_quality(模版质量)。
				 */
				// 判断返回的特征值有几个
				if (facesInfo.getLstFaces().size() == 1) {
					feature = ByteBuffer.wrap(facesInfo.getLstFaces().get(0)
							.getBinFea());
				} else {
					// 特征值大于1个，需要计算获取人脸面积最大的一个特征。
					List<CapFaceDetectResult> list = facesInfo.getLstFaces();// 查询出的特征值
					Map<Integer, Object> facesMap = new HashMap<Integer, Object>();// 过滤多个特征值

					for (CapFaceDetectResult _capFaceDetectResult : list) {
						int right = _capFaceDetectResult.getRcFace().getRight();
						int left = _capFaceDetectResult.getRcFace().getLeft();
						int bottom = _capFaceDetectResult.getRcFace()
								.getBottom();
						int top = _capFaceDetectResult.getRcFace().getTop();
						int Integer = (right - left) * (bottom - top);
						facesMap.put(Integer, _capFaceDetectResult);
					}
					// 选择出最大的一个对象。
					CapFaceDetectResult maxCapFaceDetect = (CapFaceDetectResult) getMaxKey(facesMap);
					if (maxCapFaceDetect == null) {
						// 处理失败
						log.error("获取在多个特征值中最大特征值失败！");
						throw new Exception("获取在多个特征值中最大特征值失败！");
					} else {
						// 赋值
						feature = ByteBuffer.wrap(maxCapFaceDetect.getBinFea());
					}
				}
			} else {
				log.error("抓拍服务没有找到特征信息!");
				throw new BussinessException("抓拍服务没有找到特征信息！");
			}
		} else {
			log.error("抓拍服务没有找到特征信息!");
			throw new BussinessException("抓拍服务没有找到特征信息！");
		}
		
		FeatureInfo featureInfo = new FeatureInfo();
		featureInfo.setFeture(feature);
		return featureInfo;
	}
	
	/**
	 * 获取map中key值最大的对象
	 * 
	 * @param map
	 * @return
	 * @author mingxingyu
	 * @date 2017年8月22日上午10:30:03
	 */
	private Object getMaxKey(Map<Integer, Object> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		int maxValue = Integer.MIN_VALUE;
		Set<Integer> keys = map.keySet();
		for (Integer key : keys) {
			if (maxValue < key) {
				maxValue = key;
			}
		}
		return map.get(maxValue);
	}
}
