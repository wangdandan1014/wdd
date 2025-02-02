package com.sensing.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.sensing.core.bean.ChannelCfg;
import com.sensing.core.bean.ChannelCfgTemp;
import com.sensing.core.bean.ChannelQueryResult;
import com.sensing.core.bean.ReturnMsg;
import com.sensing.core.service.DataService.Iface;
import com.sensing.core.thrift.bean.FaceTemplateDB;
import com.sensing.core.thrift.bean.FaceTemplateDBTemp;
import com.sensing.core.utils.ApplicationUtil;
import com.sensing.core.utils.task.JobsAndTaskTimer;

@Service
public class DataServiceImpl implements Iface{
	
	 private static Logger log = Logger.getLogger(DataServiceImpl.class);
//	 @Resource
//	 private ISysOrgDAO sysOrgDAO;
//	 @Resource
//	 private IChannelCfgDAO channelCfgDAO;
//	 @Resource
//	 private IRpcLogDAO rpcLogDAO;
//	 @Resource
//	 public JobsAndTaskTimer jobsAndTaskTimer;
	/**
	 * 根据区域id查询通道
	 * author dongsl
	 * date 2017年8月17日下午8:21:42
	 */
	@Override
	public ChannelQueryResult QueryChannelsByRegionID(int regionID,int nStartNum, int nCount) throws TException {
		ChannelQueryResult cqr = new ChannelQueryResult();
		try {
			//接收到抓拍重启的通知之后，执行定时任务，开启通知抓拍通道状态
			
			/**
			 * 立即通知抓拍会报给后台异常，延迟5秒就不会有该情况
			 */
			Thread.sleep(5000);
			ApplicationContext context = ApplicationUtil.getApplicationContext();
			JobsAndTaskTimer jobsAndTaskTimer = context.getBean(JobsAndTaskTimer.class);
			jobsAndTaskTimer.startJobsWithInit();
			log.info("已接收到抓拍启动通知QueryChannelsByRegionID，任务通知抓拍已执行。");
		} catch (Exception e) {
			e.printStackTrace();
		}
//		try{
//			Map<String,String> param = new HashMap<String,String>();
//			param.put("regionID",regionID + "");
//			param.put("nStartNum",nStartNum + "");
//			param.put("nCount", nCount + "");
//			String url = "http://" + PropUtils.getString("default.core.ip") + ":" + PropUtils.getInt("default.core.port")+ "/videostructure/channel/queryThriftChannel";
//			String result = HttpDeal.post(url,JSONObject.toJSONString(param, true),3);
//			log.info("调用QueryChannelsByRegionID返回结果：数据长度"+result.length());
//			if(StringUtils.isEmptyOrNull(result)){
//				throw new Exception("网络超时，服务不可用");
//			}else{
//				cqr =  JSONObject.parseObject(result, ChannelQueryResult.class);
//			}
//		}catch(Exception e){
//			log.error("比对调用查询通道方法失败！" + ExpUtil.getExceptionDetail(e));
//		}
		return cqr;
	}
	
//	/**
//	 * 查询所有模板库信息
//	 * author dongsl
//	 * date 2017年8月18日上午9:54:30
//	 */
//	@Override
//	public FaceTemplateDBResult ListAllTDBs() throws TException {
//		FaceTemplateDBResult r = new FaceTemplateDBResult();
//		try{
//			Map<String,String> param = new HashMap<String,String>();
//			String url = "http://" + PropUtils.getString("default.core.ip") + ":" + PropUtils.getInt("default.core.port")+ "/videostructure/faceTemplateDb/queryThriftList";
//			String result = HttpDeal.post(url,JSONObject.toJSONString(param, true),3);
//			log.info("调用ListAllTDBs返回结果："+result);
//			if(StringUtils.isEmptyOrNull(result)){
//				throw new Exception("网络超时，服务不可用");
//			}else{
//				r =  JSONObject.parseObject(result, FaceTemplateDBResult.class);
//			}
//		}catch(Exception e){
//			log.error("比对调用查询模板库方法失败！" + ExpUtil.getExceptionDetail(e));
//		}
//		return r;
//	}
	
	/**
	 * 查询所有的模板信息
	 * author dongsl
	 * date 2017年8月18日下午2:51:28
	 */
//	@Override
//	public FaceTemplateFeaResult listAllFaceT(int nTDBID, int nStartNum,int nCount) throws TException {
//		Map map = new HashMap();
//		FaceTemplateFeaResult ftfr = new FaceTemplateFeaResult();
//		try{
//			Map<String,String> param = new HashMap<String,String>();
//			param.put("nTDBID",nTDBID + "");
//			param.put("nStartNum",nStartNum + "");
//			param.put("nCount", nCount + "");
//			String url = "http://" + PropUtils.getString("default.core.ip") + ":" + PropUtils.getInt("default.core.port")+ "/videostructure/faceTemplate/queryThriftList";
//			String result = HttpDeal.post(url,JSONObject.toJSONString(param, true),3);
//			log.info("调用listAllFaceT返回结果："+result);
//			if(StringUtils.isEmptyOrNull(result)){
//				throw new Exception("网络超时，服务不可用");
//			}else{
//				map = JSONObject.parseObject(result);
//				ftfr.setError(Integer.valueOf(map.get("error")==null?"0":map.get("error").toString()));
//				ftfr.setMsg(map.get("msg")==null?"":map.get("msg").toString());
//				List<FaceTemplateFea> list = new ArrayList<FaceTemplateFea>();
//				List<JSONObject> jsonList = (List<JSONObject>) map.get("ftList");
//				for(JSONObject jo : jsonList){
//					FaceTemplateFea ftf = new FaceTemplateFea();
//					ftf.setUuid(jo.getString("uuid"));
//					ftf.setFt_type(jo.getIntValue("ft_type"));
//					ftf.setFt_fea(jo.getBytes("ft_fea"));
//					list.add(ftf);
//				}
//				ftfr.setData(list);
//			}
//		}catch(Exception e){
//			log.error("比对调用查询模板库方法失败！" + ExpUtil.getExceptionDetail(e));
//		}
//		return ftfr;
//	}
	
	/**
	 * 将channelCfgTemp对象转为ChannelCfg对象返回
	 * author dongsl
	 * date 2017年8月18日下午4:50:51
	 */
	public List<ChannelCfg> convertChannelCfgTempToChannelCfg(List<ChannelCfgTemp> tempList){
		List<ChannelCfg> list = new ArrayList<ChannelCfg>();
		for(ChannelCfgTemp temp:tempList){
			ChannelCfg cc = new ChannelCfg();
			cc.setUuid(temp.getUuid());
			cc.setCap_stat(temp.getCap_stat());
			cc.setChannel_addr(temp.getChannel_addr());
			cc.setChannel_area(temp.getChannel_area());
			cc.setChannel_description(temp.getChannel_description());
			cc.setChannel_direct(temp.getChannel_direct());
			cc.setChannel_guid(temp.getChannel_guid());
			cc.setChannel_latitude(temp.getChannel_latitude());
			cc.setChannel_longitude(temp.getChannel_longitude());
			cc.setChannel_name(temp.getChannel_name());
			cc.setChannel_no(temp.getChannel_no());
			cc.setChannel_port(temp.getChannel_port());
			cc.setChannel_psw(temp.getChannel_psw());
			cc.setChannel_threshold(temp.getChannel_threshold());
			cc.setChannel_type(temp.getChannel_type());
			cc.setChannel_uid(temp.getChannel_uid());
			cc.setChannel_verid(temp.getChannel_verid());
			cc.setImportant(temp.getImportant());
			cc.setMax_pitch(temp.getMax_pitch());
			cc.setMax_roll(temp.getMax_roll());
			cc.setMax_save_distance(temp.getMax_save_distance());
			cc.setMax_yaw(temp.getMax_yaw());
			cc.setMin_cap_distance(temp.getMin_cap_distance());
			cc.setMin_face_width(temp.getMin_face_width());
			cc.setMin_img_quality(temp.getMin_img_quality());
			cc.setRegion_id(temp.getRegion_id());
			cc.setReserved(temp.getReserved());
			cc.setSdk_ver(temp.getSdk_ver());
			list.add(cc);
		}
		return list;
	}
	
	/**
	 * 将FaceTemplateDBTemp对象转为FaceTemplateDB对象返回
	 * author dongsl
	 * date 2017年8月18日下午5:01:10
	 */
	public List<FaceTemplateDB> convertFaceTemplateDBTempToFaceTemplateDB(List<FaceTemplateDBTemp> list){
		List<FaceTemplateDB> resList = new ArrayList<FaceTemplateDB>();
		for(FaceTemplateDBTemp ftdbtemp : list){
			FaceTemplateDB ftdb = new FaceTemplateDB();
			ftdb.setId(ftdbtemp.getId());
			ftdb.setIs_used(ftdbtemp.getIs_used());
			ftdb.setCreate_time(ftdbtemp.getCreate_time());
			ftdb.setTemplate_db_capacity(ftdbtemp.getTemplate_db_capacity());
			ftdb.setTemplate_db_description(ftdbtemp.getTemplate_db_description());
			ftdb.setTemplate_db_name(ftdbtemp.getTemplate_db_name());
			ftdb.setTemplate_db_size(ftdbtemp.getTemplate_db_size());
			ftdb.setTemplate_db_type(ftdbtemp.getTemplate_db_type());
			resList.add(ftdb);
		}
		return resList;
	}
	/**
	 * @Description: 根据orgId查询所有子机构  
	 * @author dongsl
	 * @Date 2018年4月25日下午5:51:31
	 */
	/*
	public List<String> getOrgIdList(String orgId){
		List<String> resList = new ArrayList<String>();
		try {
			resList = sysOrgDAO.getChildList(orgId);
		} catch (Exception e) {
			log.error("查询机构失败");
		}
		return resList;
	}
	*/
	/**
	 * @Description: 查询所有机构  
	 * @author dongsl
	 * @Date 2018年4月25日下午5:51:09
	 */
	/*
	public List<String> getAllOrgList(){
		List<String> resList = new ArrayList<String>();
		try {
			resList = sysOrgDAO.getAllList();
		} catch (Exception e) {
			log.error("查询机构失败");
		}
		return resList;
	}
	*/
	/*
	@Override
	public ReturnMsg ModifyStaticVideoStatic(String uuid, int state) throws TException {
		log.info("++++++++回调修改离线视频状态+++++++uuid:"+uuid+"state:"+state);
		ReturnMsg msg=new ReturnMsg();
		RpcLog rpcLog=new RpcLog();
		try {
			rpcLog.setCallTime(new Date());
			rpcLog.setMode(Constants.INTERFACE_CALL_TYPE_PASS);
			rpcLog.setMemo("uuid="+uuid+"--state="+state);
			rpcLog.setModule(Constants.SEVICE_MODEL_STATICVIDEO);
			rpcLog.setTodo("修改状态");
			List<String> uuids=new ArrayList<String>();
			uuids.add(uuid);
			ITaskService taskService = (ITaskService ) ApplicationUtil.getBean(ITaskService.class);
			List<TaskChannel>tcs=taskService.getTaskChannelByChannelIds(uuids);
			String taskId = null;
			for(TaskChannel tc:tcs)
			{
				taskId=tc.getTaskUuid();
			}
			if(taskId!=null&&!"".equals(taskId))
			{
				Task task=new Task();
				task.setUuid(taskId);
				task.setState(state);
				task.setModifyTime(new Date());
				taskService.updateState(task);
				msg.setMsg("更新成功");
				msg.setRes(0);
				rpcLog.setResult("成功");
				log.info("++++++++回调修改离线视频状态成功+++++++uuid:"+uuid+"state:"+state);
			}else {
				msg.setMsg("通道不存在对应任务");
				msg.setRes(-1);
				rpcLog.setResult("失败");
				rpcLog.setErrorMsg("通道不存在对应任务");
				log.info("++++++++回调修改离线视频状态失败+++++++uuid:"+uuid+"state:"+state);
			}
		} catch (Exception e) {
			msg.setMsg(e.getMessage());
			msg.setRes(-1);
			rpcLog.setResult("异常");
			rpcLog.setErrorMsg(e.getMessage());
			log.info("++++++++回调修改离线视频状态失败+++++++uuid:"+uuid+"state:"+state);
			log.error("++++++++回调修改离线视频状态失败+++++++" + ExpUtil.getExceptionDetail(e));
			return msg;
		}finally {
			IRpcLogService logService = (IRpcLogService ) ApplicationUtil.getBean(IRpcLogService.class);
			logService.saveNewRpcLog(rpcLog);
		}
		return msg;
	}
	*/

	@Override
	public ReturnMsg ModifyStaticVideoStatic(String uuid, int state)
			throws TException {
		return null;
	}
}
