package com.sensing.core.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.bean.Channel;
import com.sensing.core.bean.ChannelCfg;
import com.sensing.core.bean.ChannelCfgTemp;
import com.sensing.core.bean.ChannelQueryResult;
import com.sensing.core.bean.Jobs;
import com.sensing.core.bean.JobsChannelTemp;
import com.sensing.core.bean.Regions;
import com.sensing.core.bean.RpcLog;
import com.sensing.core.dao.IChannelCfgDAO;
import com.sensing.core.dao.IChannelDAO;
import com.sensing.core.dao.IJobsChannelDAO;
import com.sensing.core.dao.IJobsDAO;
import com.sensing.core.dao.IRegionsDAO;
import com.sensing.core.dao.IRpcLogDAO;
import com.sensing.core.service.CaptureThriftService;
import com.sensing.core.service.IChannelService;
import com.sensing.core.service.IRegionsService;
import com.sensing.core.service.ITaskChannelService;
import com.sensing.core.thrift.cap.bean.CapChannelConfig;
import com.sensing.core.thrift.cap.bean.CapReturn;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.UuidUtil;
import com.sensing.core.utils.Exception.BussinessException;
import com.sensing.core.utils.Exception.CapServerException;
import com.sensing.core.utils.Exception.ExpUtil;
import com.sensing.core.utils.httputils.HttpDeal;
import com.sensing.core.utils.props.PropUtils;
import com.sensing.core.utils.props.RemoteInfoUtil;
import com.sensing.core.utils.task.JobsAndTaskTimer;
import com.sensing.core.utils.time.DateUtil;

/**
 * @author wenbo
 */
@Service
@SuppressWarnings("all")
public class ChannelServiceImpl implements IChannelService {

	private static final Log log = LogFactory.getLog(IChannelService.class);

	@Resource
	public IChannelDAO channelDAO;
	@Resource
	public IRegionsService regionsService;
	@Resource
	public IRegionsDAO regionsDAO;
	@Resource
	private IChannelCfgDAO channelCfgDAO;
	@Resource
	private ITaskChannelService taskChannelService;
	@Autowired
	CaptureThriftService capThriftService;
	@Resource
	public IRpcLogDAO rpcLogDAO;
	@Resource
	public IJobsDAO jobsDAO;
	@Resource
	public IJobsChannelDAO jobsChannelDAO;
	@Resource
	public JobsAndTaskTimer jobsAndTaskTimer;

	/**
	 * 查询通道的数量
	 * 
	 * @param pager
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2018年7月27日 上午10:12:03
	 */
	public int queryChannelCount(Pager pager) throws Exception {
		try {
			return channelDAO.selectCount(pager);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
	}

	/**
	 * 保存通道方法
	 */
	public Channel saveNewChannel(Channel channel) throws Exception {
		try {
			String id = UuidUtil.getUuid();
			if (StringUtils.isEmptyOrNull(channel.getUuid())) {
				channel.setUuid(id);
			}
			channel.setCapStat(Constants.CAP_STAT_CLOSE);
			channel.setCreateTime(new Date().getTime() / 1000);
			channel.setLastTimestamp(new Date());
			channel.setIsDel(Constants.DELETE_NO); // 默认未删除状态
			channel.setSearchCode(this.getSeachCodeByRegionId(channel.getRegionId())); // 设置searchCode值
			channelDAO.saveChannel(channel);
			// 调用抓拍服务接口
			CallAddChannel(channel);
			// callAddStreamMedia(channel.getChannelAddr());
		} catch (Exception e) {
			log.error("调用ChannelService.saveNewChannel(Channel channel)方法异常！" + ExpUtil.getExceptionDetail(e));
			throw ExpUtil.dealException(e);
		}
		return channel;
	}

	/**
	 * 通知调用流媒体服务添加通道
	 * 
	 * @param channelAddr
	 * @author mingxingyu
	 * @date 2018年8月4日 下午12:00:09
	 */
	private void callAddStreamMedia(String channelAddr) {
		try {
			// 请求参数
			String param = "{\"videoUrl\":\"#videoUrl\"}";
			if (channelAddr != null && !"".equals(channelAddr) && channelAddr.startsWith("rtsp://")) {
				param = param.replaceAll("#videoUrl", channelAddr);

			} else {
				log.error("通知流媒体服务添加通道发生异常。channelAddr地址为空或者不是为rtsp://开头！");
				return;
			}

			String url = "http://" + PropUtils.getString("streamMedia.ip") + ":"
					+ PropUtils.getString("streamMedia.port") + "/mediaserver/proxy/add";

			String post = HttpDeal.post(url, param, 1);

			JSONObject resultJson = JSONObject.parseObject(post);
			if (resultJson != null && resultJson.getString("error") != null
					&& resultJson.getString("message") != null) {
				if ("200".equals(resultJson.getString("error"))) {
					log.info("流媒体服务添加通道调用正常，返回结果message:" + resultJson.getString("message"));
				} else {
					log.error("流媒体服务添加通道调用失败，返回错误码error:" + resultJson.getString("error") + ";返回结果message:"
							+ resultJson.getString("message"));
				}
			}
		} catch (Exception e) {
			log.error("调用流媒体服务添加通道发生异常." + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 通知调用流媒体服务删除通道
	 * 
	 * @param channelAddr
	 * @author mingxingyu
	 * @date 2018年8月4日 下午12:00:09
	 */
	private void callRemoveStreamMedia(String channelAddr) {
		try {
			// 请求参数
			String param = "{\"videoUrl\":\"#videoUrl\"}";

			if (channelAddr != null && !"".equals(channelAddr) && channelAddr.startsWith("rtsp://")) {
				param = param.replaceAll("#videoUrl", channelAddr);
			} else {
				log.error("通知流媒体服务删除通道发生异常。channelAddr地址为空或者不是为rtsp://开头！");
				return;
			}

			String url = "http://" + PropUtils.getString("streamMedia.ip") + ":"
					+ PropUtils.getString("streamMedia.port") + "/mediaserver/proxy/delete";

			String post = HttpDeal.post(url, param, 1);

			JSONObject resultJson = JSONObject.parseObject(post);
			if (resultJson != null && resultJson.getString("error") != null
					&& resultJson.getString("message") != null) {
				if ("200".equals(resultJson.getString("error"))) {
					log.info("流媒体服务删除通道调用正常，返回结果message:" + resultJson.getString("message"));
				} else {
					log.error("流媒体服务删除通道调用失败，返回错误码error:" + resultJson.getString("error") + ";返回结果message:"
							+ resultJson.getString("message"));
				}
			}
		} catch (Exception e) {
			log.error("调用流媒体服务删除通道发生异常." + e.getMessage());
			e.printStackTrace();
		}
	}

	private void chooseStreamMethod(String orignChannelAddr, String newChannelAddr) {

		if (StringUtils.isEmptyOrNull(orignChannelAddr) && StringUtils.isEmptyOrNull(newChannelAddr)) {
			return;
		} else if (StringUtils.isEmptyOrNull(orignChannelAddr) && StringUtils.isNotEmpty(newChannelAddr)) {
			callAddStreamMedia(newChannelAddr);
		} else if (StringUtils.isNotEmpty(orignChannelAddr) && StringUtils.isEmptyOrNull(newChannelAddr)) {
			callRemoveStreamMedia(orignChannelAddr);
		} else if (StringUtils.isNotEmpty(orignChannelAddr) && StringUtils.isNotEmpty(newChannelAddr)
				&& !orignChannelAddr.equals(newChannelAddr)) {
			callUpdateStreamMedia(orignChannelAddr, newChannelAddr);
		}
	}

	/**
	 * 通知调用流媒体服务更新通道
	 * 
	 * @param channelAddr
	 * @author mingxingyu
	 * @date 2018年8月4日 下午12:00:09
	 */
	private void callUpdateStreamMedia(String orignChannelAddr, String newChannelAddr) {
		try {
			// 请求参数
			String param = "{\"videoUrl\":\"#videoUrl\",\"updateVideoUrl\":\"#updateVideoUrl\"}";

			if (newChannelAddr != null && !"".equals(newChannelAddr) && newChannelAddr.startsWith("rtsp://")) {
				param = param.replaceAll("#videoUrl", orignChannelAddr);
				param = param.replaceAll("#updateVideoUrl", newChannelAddr);
			} else {
				log.error("通知流媒体服务修改通道发生异常。channelAddr地址为空或者不是为rtsp://开头！");
				return;
			}

			String url = "http://" + PropUtils.getString("streamMedia.ip") + ":"
					+ PropUtils.getString("streamMedia.port") + "/mediaserver/proxy/update";
			String post = HttpDeal.post(url, param, 1);

			JSONObject resultJson = JSONObject.parseObject(post);
			if (resultJson != null && resultJson.getString("error") != null
					&& resultJson.getString("message") != null) {
				if ("200".equals(resultJson.getString("error"))) {
					log.info("流媒体服务修改通道调用正常，返回结果message:" + resultJson.getString("message"));
				} else {
					log.error("流媒体服务修改通道调用失败，返回错误码error:" + resultJson.getString("error") + ";返回结果message:"
							+ resultJson.getString("message"));
				}
			}
		} catch (Exception e) {
			log.error("调用流媒体服务修改通道发生异常." + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 修改通道 author dongsl date 2017年8月21日下午3:13:57
	 */
	public Channel updateChannel(Channel channel) throws Exception {

		try {
			Channel channelNowInDb = channelDAO.getChannel(channel.getUuid(), Constants.DELETE_NO.toString());
			if (channelNowInDb != null) {
				// 将数据库的reserved字段的值查询出来
				if (channel.getReserved() == null || channel.getReserved() == 0) {
					if (channelNowInDb.getReserved() != null) {
						channel.setReserved(channelNowInDb.getReserved());
					} else {
						channel.setReserved(1);
					}
				}
				if (channel.getIsDel() == null) {
					channel.setIsDel(Constants.DELETE_NO);
				}
				if (StringUtils.isNotEmpty(channelNowInDb.getChannelRtmp())) {
					channel.setChannelRtmp(channelNowInDb.getChannelRtmp());
				}
				channel.setModifyTime(new Date().getTime() / 1000);
				channel.setLastTimestamp(new Date());
				channelDAO.updateChannel(channel);
				// 通知调用流媒体服务修改通道
				// chooseStreamMethod(channelNowInDb.getChannelAddr(),channel.getChannelAddr());
//				if (!(channel.getCapStat().equals(channelNowInDb.getCapStat()) && channel.getAnalysisType() != null
//						&& channel.getAnalysisType().equals(channelNowInDb.getAnalysisType())
//						&& channel.getChannelAddr().equals(channelNowInDb.getChannelAddr()))) {
				CallModifyChannel(channel);
//				}
			} else {
				log.error("修改通道时未能查到通道信息，通道uuid为：" + channel.getUuid());
				throw new BussinessException("修改通道时未能查到通道信息!");
			}
		} catch (Exception e) {
			log.error("执行IChannelService.updateChannel方法异常！" + ExpUtil.getExceptionDetail(e));
			throw ExpUtil.dealException(e);
		}
		return channel;
	}

	/**
	 * 查询通道，默认查询未删除状态
	 */
	@Override
	public Channel findChannelById(String uuid) throws Exception {
		try {
			return channelDAO.getChannel(uuid, String.valueOf(Constants.DELETE_NO));
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
	}

	/**
	 * id ： 通道的uuid uuid ： 操作用户的uuid
	 */
	public int removeChannel(String id, String uuid) throws Exception {
		int c = 0;
		try {
			Channel channel = this.findChannelById(id);
			if (channel == null)
				return c;
			channel.setModifyUser(uuid);
			channel.setModifyTime(new Date().getTime() / 1000);
			channel.setCapStat(Constants.CAP_STAT_CLOSE);
			channel.setIsDel(Constants.DELETE_YES);
			c = channelDAO.removeChannel(channel);
			// 调用抓拍服务接口
			CallRemoveChannel(id);
			// 通知调用流媒体服务修改通道
			// callRemoveStreamMedia(channel.getChannelAddr());
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
		return c;
	}

	/**
	 * @Description:调用抓拍比对接口，返回结果
	 * @param uuid
	 * @return void
	 * @author dongsl
	 * @Date 2017年8月29日下午1:32:12
	 */
	public void CallRemoveChannel(String uuid) {
		CapReturn capReturn = null;
		RpcLog rl = new RpcLog();
		try {
			rl.setCallTime(new Date());
			rl.setMode(Constants.INTERFACE_CALL_TYPE_INIT);
			rl.setModule(Constants.SEVICE_MODEL_ZPFW);
			rl.setTodo("删除通道");
			rl.setName("DelChannel");
			rl.setIp(RemoteInfoUtil.CAP_SERVER_IP);
			rl.setPort(RemoteInfoUtil.CAP_SERVER_PORT);
			rl.setRpcType("thrift");
			rl.setMemo(uuid);
			rl.setType(Constants.RPC_LOG_TYPE_CAPSERVER);
			Date d1 = new Date();
			capReturn = capThriftService.DelChannel(uuid);
			Date d2 = new Date();
			rl.setMs((int) (d2.getTime() - d1.getTime()));
			if (capReturn.getRes() < 0) {
				rl.setResult("失败");
				rl.setErrorMsg(capReturn.getMsg());
				log.error("调用抓拍删除通道接口DelChannel(capChannel);失败!失败原因：" + capReturn.getMsg());
			} else {
				rl.setResult("成功");
			}
			try {
				rpcLogDAO.saveRpcLog(rl);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			rl.setType(Constants.RPC_LOG_TYPE_CAPSERVER);
			rl.setMs(0);
			rl.setResult("失败");
			rl.setErrorMsg(e.toString());
			try {
				rpcLogDAO.saveRpcLog(rl);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			log.error("调用抓拍删除通道接口DelChannel(capChannel);异常!", e.fillInStackTrace());
		}
	}

	/**
	 * @Description:调用抓拍服务接口 接口返回参数
	 * @param channel
	 * @return void
	 * @author dongsl
	 * @Date 2017年8月29日下午1:29:11
	 */
	private void CallModifyChannel(Channel channel) {

		CapChannelConfig capChannel = new CapChannelConfig();
		// 设置默认值
		capChannel.setFace_merge(true);
		capChannel.setMerge_all(true);
		capChannel.setMerge_threshold(75);
		capChannel.setZoom(960);
		capChannel.setMax_face_count(100);
		capChannel.setMin_confidence(0.6);
		if (channel.getUuid() != null && !(channel.getUuid().isEmpty())) {
			capChannel.setUuid(channel.getUuid());
		}
		if (channel.getChannelUid() != null && !(channel.getChannelUid().isEmpty())) {
			capChannel.setChannel_uid(channel.getChannelUid());// 登录视频源用户名
		}
		if (channel.getChannelPsw() != null && !(channel.getChannelPsw().isEmpty())) {
			capChannel.setChannel_psw(channel.getChannelPsw());// 登录视频源密码
		}
		if (channel.getChannelName() != null && !(channel.getChannelName().isEmpty())) {
			capChannel.setChannel_name(channel.getChannelName());// 视频源名称
		}
		if (channel.getChannelAddr() != null && !(channel.getChannelAddr().isEmpty())) {
			capChannel.setChannel_addr(channel.getChannelAddr());// 视频源设备地址
		}
		if (channel.getChannelType() != null) {
			capChannel.setChannel_type(channel.getChannelType());// 视频源类型
		}
		if (channel.getChannelPort() != null) {
			capChannel.setChannel_port(channel.getChannelPort());// 视频源设备端口
		}
		if (channel.getChannelNo() != null && !(channel.getChannelNo().isEmpty())) {
			capChannel.setChannel_no(channel.getChannelNo());// 视频源通道号
		}
		if (channel.getChannelGuid() != null) {
			capChannel.setChannel_guid(channel.getChannelGuid());// 视频源GUID
		}
		if (channel.getChannelLatitude() != null) {
			capChannel.setChannel_latitude(channel.getChannelLatitude());// 视频源经度
		}
		if (channel.getChannelLongitude() != null) {
			capChannel.setChannel_longitude(channel.getChannelLongitude());// 视频源纬度
		}
		if (channel.getChannelDirect() != null) {
			capChannel.setChannel_direct(channel.getChannelDirect());// 视频源方向
		}
		if (channel.getChannelArea() != null) {
			capChannel.setChannel_area(channel.getChannelArea());// 位置描述);// 位置描述
		}
		if (channel.getReserved() != null) {
			capChannel.setProtocol(channel.getReserved());// 保留
		}
		if (channel.getMinFaceWidth() != null) {
			capChannel.setMin_face_width(channel.getMinFaceWidth());// 最小有效人脸宽度
		}
		if (channel.getMinImgQuality() != null) {
			capChannel.setMin_img_quality(channel.getMinImgQuality());// 最小有效图像质量
		}
		if (channel.getMinCapDistance() != null) {
			capChannel.setMin_cap_distance(channel.getMinCapDistance());// 最小采集帧间隔
		}
		if (channel.getMaxYaw() != null) {
			capChannel.setMax_yaw(channel.getMaxYaw());// 围绕X轴旋转
		}
		if (channel.getMaxPitch() != null) {
			capChannel.setMax_pitch(channel.getMaxPitch());// 围绕Y轴旋转
		}
		if (channel.getMaxRoll() != null) {
			capChannel.setMax_roll(channel.getMaxRoll());// 围绕Z轴旋转
		}
		if (channel.getChannelThreshold() != null) {
			capChannel.setChannel_threshold(channel.getChannelThreshold());// 视频源阈值
		}
		if (channel.getCapStat() != null) {
			capChannel.setCap_stat(channel.getCapStat());
		}
		if (channel.getImportant() != null) {
			capChannel.setImportant(channel.getImportant());
		}

		// 通知抓拍类型
		if (StringUtils.isNotEmpty(channel.getAnalysisType())) {
			capChannel.setStrReserve("{\n" + "  \"structure\" :\n" + "  {\n" + "    \"detectParam\" :\n" + "    {\n"
					+ "      \"enablePerson\" : "
					+ (channel.getAnalysisType().contains(Constants.CAP_ANALY_TYPE_PERSON + "") ? true : false) + ",\n"
					+ "      \"enableVehicle\" : "
					+ (channel.getAnalysisType().contains(Constants.CAP_ANALY_TYPE_MOTOR_VEHICLE + "") ? true : false)
					+ ",\n" + "      \"enableNonmotor\" : "
					+ (channel.getAnalysisType().contains(Constants.CAP_ANALY_TYPE_NONMOTOR_VEHICLE + "") ? true
							: false)
					+ "\n" + "    },\n" + "    \"pictureParam\" :\n" + "    {\n" + "      \"writeTarget\" : true,\n"
					+ "      \"writeScene\" : true\n" + "    },\n" + "    \"mongoParam\" :\n" + "    {\n"
					+ "      \"enableWrite\" : false\n" + "    }\n" + "  }\n" + "}");
		}

		CapReturn capReturn = null;

		RpcLog rl = new RpcLog();
		try {
			rl.setCallTime(new Date());
			rl.setMode(Constants.INTERFACE_CALL_TYPE_INIT);
			rl.setModule(Constants.SEVICE_MODEL_ZPFW);
			if (StringUtils.isNotEmpty(channel.getNodeType()) && channel.getCapStat() == Constants.CAP_STAT_CLOSE) {
				rl.setTodo("关闭通道");
			} else if (StringUtils.isNotEmpty(channel.getNodeType())
					&& channel.getCapStat() == Constants.CAP_STAT_OPEN) {
				rl.setTodo("开启通道");
			} else {
				rl.setTodo("修改通道");
			}
			rl.setName("ModifyChannel");
			rl.setIp(RemoteInfoUtil.CAP_SERVER_IP);
			rl.setPort(RemoteInfoUtil.CAP_SERVER_PORT);
			rl.setRpcType("thrift");
			rl.setMemo(channel.toString());
			rl.setType(Constants.RPC_LOG_TYPE_CAPSERVER);
			Date d1 = new Date();
			capReturn = capThriftService.ModifyChannel(capChannel);
			Date d2 = new Date();
			rl.setMs((int) (d2.getTime() - d1.getTime()));
			if (capReturn != null && capReturn.getRes() < 0) {
				rl.setResult("失败");
				rl.setErrorMsg(capReturn.getMsg());
				log.error("抓拍服务处理失败！失败原因：" + capReturn.getMsg());
			} else {
				rl.setResult("成功");
			}
			try {
				rpcLogDAO.saveRpcLog(rl);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (TException e) {
			rl.setMs(0);
			rl.setResult("失败");
			rl.setErrorMsg(e.toString());
			rl.setType(Constants.RPC_LOG_TYPE_CAPSERVER);
			try {
				rpcLogDAO.saveRpcLog(rl);
			} catch (Exception ed) {
				ed.printStackTrace();
			}
			log.error("调用抓拍修改通道接口ModifyChannel(capChannel);异常!", e.fillInStackTrace());
			e.printStackTrace();
			throw new CapServerException();
		}
	}

	/**
	 * 默认查询未删除状态的通道
	 */
	@Override
	public Pager queryPage(Pager pager) throws Exception {
		try {
			pager.getF().put("isDel", String.valueOf(Constants.DELETE_NO));// 默认查询未删除状态
			List<Channel> list = channelDAO.queryList(pager);
			int totalCount = channelDAO.selectCount(pager);
			pager.setTotalCount(totalCount);
			pager.setResultList(list);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
		return pager;
	}

	/**
	 * 根据channel_no查询通道
	 */
	public List<Channel> queryChannelByChannelNoAndAddr(String channelNo, String channelAddr) throws Exception {
		return channelDAO.queryChannelByChannelNoAndAddr(channelNo, channelAddr, Constants.DELETE_NO);
	}

	/**
	 * 根据通道id查询一条通道信息
	 */
	public Channel getOneChannelByUuid(String channelId, String isDel) throws Exception {
		Channel c = channelDAO.getOneChannelByUuid(channelId, isDel);
		return c;
	}

	/**
	 * 调用抓拍服务接口 接口返回参数
	 * 
	 * @Description:
	 * @param channel
	 * @return void
	 * @author dongsl
	 * @throws Exception
	 * @Date 2017年8月29日下午1:26:42
	 */
	private void CallAddChannel(Channel channel) {
		CapChannelConfig capChannel = new CapChannelConfig();
		// 设置默认值
		capChannel.setFace_merge(true);
		capChannel.setMerge_all(true);
		capChannel.setMerge_threshold(75);
		capChannel.setZoom(960);
		capChannel.setMax_face_count(100);
		capChannel.setMin_confidence(0.6);
		if (channel.getUuid() != null && !(channel.getUuid().isEmpty())) {
			capChannel.setUuid(channel.getUuid());
		}
		if (channel.getChannelUid() != null && !(channel.getChannelUid().isEmpty())) {
			capChannel.setChannel_uid(channel.getChannelUid());// 登录视频源用户名
		}
		if (channel.getChannelPsw() != null && !(channel.getChannelPsw().isEmpty())) {
			capChannel.setChannel_psw(channel.getChannelPsw());// 登录视频源密码
		}
		if (channel.getChannelName() != null && !(channel.getChannelName().isEmpty())) {
			capChannel.setChannel_name(channel.getChannelName());// 视频源名称
		}
		/*
		 * if (channel.getChannelDescription() != null &&
		 * !(channel.getChannelDescription().isEmpty())) {
		 * capChannel.setChannel_description(channel.getChannelDescription());// 视频源描述 }
		 */
		if (channel.getChannelAddr() != null && !(channel.getChannelAddr().isEmpty())) {
			capChannel.setChannel_addr(channel.getChannelAddr());// 视频源设备地址
		}
		if (channel.getChannelType() != null) {
			capChannel.setChannel_type(channel.getChannelType());// 视频源类型
		}
		if (channel.getChannelPort() != null) {
			capChannel.setChannel_port(channel.getChannelPort());// 视频源设备端口
		}
		/*
		 * if(channel.getRegionId()!=null){
		 * capChannel.setRegion_id(channel.getRegionId());// 工作区ID }
		 */
		if (channel.getChannelNo() != null && !(channel.getChannelNo().isEmpty())) {
			capChannel.setChannel_no(channel.getChannelNo());// 视频源通道号
		}
		if (channel.getChannelGuid() != null) {
			capChannel.setChannel_guid(channel.getChannelGuid());// 视频源GUID
		}
		/*
		 * 暂不使用此字段 if(!(cfg.getChannel_guid().isEmpty())){
		 * capChannel.setChannel_guid(cfg.getChannel_guid());// 视频源GUID }
		 */
		if (channel.getChannelLatitude() != null) {
			capChannel.setChannel_latitude(channel.getChannelLatitude());// 视频源经度
		}
		if (channel.getChannelLongitude() != null) {
			capChannel.setChannel_longitude(channel.getChannelLongitude());// 视频源纬度
		}

		if (channel.getChannelDirect() != null) {
			capChannel.setChannel_direct(channel.getChannelDirect());// 视频源方向
		}
		capChannel.setChannel_area(channel.getChannelArea());// 位置描述
		if (channel.getReserved() != null) {
			capChannel.setProtocol(channel.getReserved());// 保留
		}
		/*
		 * if (channel.getSdkVer() != null) {
		 * capChannel.setSdk_ver(channel.getSdkVer());// 人脸识别Sdk版本 }
		 */
		if (channel.getMinFaceWidth() != null) {
			capChannel.setMin_face_width(channel.getMinFaceWidth());// 最小有效人脸宽度
		}
		if (channel.getMinImgQuality() != null) {
			capChannel.setMin_img_quality(channel.getMinImgQuality());// 最小有效图像质量
		}
		if (channel.getMinCapDistance() != null) {
			capChannel.setMin_cap_distance(channel.getMinCapDistance());// 最小采集帧间隔
		}
		/*
		 * if (channel.getMaxSaveDistance() != null) {
		 * capChannel.setMax_save_distance(channel.getMaxSaveDistance());// 最大人脸存储间隔 }
		 */
		if (channel.getMaxYaw() != null) {
			capChannel.setMax_yaw(channel.getMaxYaw());// 围绕X轴旋转
		}
		if (channel.getMaxPitch() != null) {
			capChannel.setMax_pitch(channel.getMaxPitch());// 围绕Y轴旋转
		}
		if (channel.getMaxRoll() != null) {
			capChannel.setMax_roll(channel.getMaxRoll());// 围绕Z轴旋转
		}
		if (channel.getChannelThreshold() != null) {
			capChannel.setChannel_threshold(channel.getChannelThreshold());// 视频源阈值
		}
		if (channel.getCapStat() != null) {
			capChannel.setCap_stat(channel.getCapStat());
		}
		if (channel.getImportant() != null) {
			capChannel.setImportant(channel.getImportant());
		}

//		String strReserveParams = "{\"structure\":{\"detectParam\":{\"enablePerson\":"
//				+( ArrayUtils.indexOf(capTypeArr,Constants.CAP_ANALY_TYPE_PERSON)>-1?true:false )+",\"enableVehicle\":"
//				+( ArrayUtils.indexOf(capTypeArr,Constants.CAP_ANALY_TYPE_MOTOR_VEHICLE)>-1?true:false )+",\"enableNonmotor\":"
//				+( ArrayUtils.indexOf(capTypeArr,Constants.CAP_ANALY_TYPE_NONMOTOR_VEHICLE)>-1?true:false )+"},\"pictureParam\":{\"writeTarget\":true,\"writeScene\":true},\"mongoParam\":{\"enableWrite\":true}}}";
//		capChannel.setStrReserve(strReserveParams);

		CapReturn capReturn = null;
		RpcLog rl = new RpcLog();
		try {
			// 记录日志
			rl.setCallTime(new Date());
			rl.setMode(Constants.INTERFACE_CALL_TYPE_INIT);
			rl.setModule(Constants.SEVICE_MODEL_ZPFW);
			rl.setTodo("新增通道");
			rl.setName("AddChannel");
			rl.setIp(RemoteInfoUtil.CAP_SERVER_IP);
			rl.setPort(RemoteInfoUtil.CAP_SERVER_PORT);
			rl.setRpcType("thrift");
			rl.setType(Constants.RPC_LOG_TYPE_CAPSERVER);
			rl.setMemo(channel.toString());
			Date d1 = new Date();
			capReturn = capThriftService.AddChannel(capChannel);
			Date d2 = new Date();
			rl.setMs((int) (d2.getTime() - d1.getTime()));
			if (capReturn != null && capReturn.getRes() < 0) {
				rl.setResult("失败");
				rl.setErrorMsg(capReturn.getMsg());
				log.error("抓拍服务处理失败！失败原因：" + capReturn.getMsg());
			} else {
				rl.setResult("成功");
			}
			try {
				rpcLogDAO.saveRpcLog(rl);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			rl.setMs(0);
			rl.setType(Constants.RPC_LOG_TYPE_CAPSERVER);
			rl.setResult("异常");
			rl.setErrorMsg(e.getMessage().toString());
			try {
				rpcLogDAO.saveRpcLog(rl);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			log.error("调用抓拍添加通道接口addChannel(capChannel);异常!", e.fillInStackTrace());
			e.printStackTrace();
			throw new CapServerException();
		}

	}

	@Override
	public List<Channel> queryChannelByChannelNameAndRegionId(String channelName, Integer regionId, short isDel)
			throws Exception {
		return channelDAO.queryChannelByChannelNameAndRegionId(channelName, regionId, isDel);
	}

	public List<Channel> queryChannelList() throws Exception {
		return channelDAO.selectChannelList();
	}

	/**
	 * @Description: 根据区域id查询所有子通道列表
	 * @param regionId
	 * @return
	 * @throws Exception
	 * @return List<String>
	 * @author dongsl
	 * @Date 2017年10月24日下午12:59:42
	 */
	public List<String> queryChannelListByRegionId(Integer regionId) throws Exception {
		Regions region = regionsDAO.getRegions(regionId);
		return channelDAO.queryChannelListByRegionSearchCode(region.getSearchCode());
	}

	/**
	 * @Description: 根据区域id计算通道searchCode值
	 * @author dongsl
	 * @Date 2018年4月24日上午10:22:21
	 */
	private String getSeachCodeByRegionId(Integer regionId) {
		String searchCode = "";
		try {
			if (regionId != null) {
				Regions region = regionsService.findRegionsById(regionId);
				if (region != null) {
					int level = region.getRegionLevel() + 1;
					String parentRegionSearchCode = region.getSearchCode();
					String maxCode = channelDAO.queryMaxSearchCodeByCode(parentRegionSearchCode);

					DecimalFormat f;
					String StrForm = "0000";
					f = new DecimalFormat(StrForm);
					String newSeachCode = "";
					if (maxCode == null) {
						newSeachCode = parentRegionSearchCode + "0001";
					} else {
						String last4Number = maxCode.substring(maxCode.length() - 4);
						newSeachCode = maxCode.substring(0, maxCode.length() - 4)
								+ f.format(Integer.parseInt(last4Number) + 1);
					}
					searchCode = newSeachCode;
				} else {
					log.error("创建通道时，根据区域id计算通道searchCode异常，根据id查询不到区域！");
				}

			} else {
				log.error("创建通道时，根据区域id计算通道searchCode异常，区域id为空！");
			}
		} catch (Exception e) {
			log.error("创建通道时，根据区域id计算通道searchCode异常，区域id--" + regionId);
		}
		return searchCode;
	}

	/**
	 * 根据区域id查询通道 author dongsl date 2017年8月17日下午8:21:42
	 */
	@Override
	public ChannelQueryResult QueryChannelsByRegionID(Integer regionID, Integer nStartNum, Integer nCount)
			throws Exception {
		ChannelQueryResult cqr = new ChannelQueryResult();
		List<ChannelCfg> list = new ArrayList<ChannelCfg>();
		List<ChannelCfgTemp> tempList = new ArrayList<ChannelCfgTemp>();
		try {
			Date d1 = new Date();
			List<String> idList = new ArrayList<String>();
			log.info("调用初始化通道方法，参数：regionId--" + regionID + ",startNum--" + nStartNum + ",nCount--" + nCount
					+ ",orgIdList:" + idList.toString());
			tempList = channelCfgDAO.QueryChannelsByRegionID(String.valueOf(regionID), nStartNum, nCount, idList);
			Date d2 = new Date();

			list = convertChannelCfgTempToChannelCfg(tempList);
			tempList.clear();
			cqr.setError(0);
			cqr.setData(list);
			log.info("~~~~~~ QueryChannelsByRegionID1 ~~~~~~" + DateUtil.DateToString(new Date()));

			// 启动刷新通道类型
			startJobTimer();

		} catch (Exception e) {
			log.error("根据区域id查询通道失败-->" + StringUtils.getStackTrace(e));
			cqr.setError(-1);
			cqr.setMsg("根据区域id查询通道失败！");
		}
		return cqr;
	}

	public void startJobTimer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
					jobsAndTaskTimer.startJobs();
				} catch (InterruptedException e) {
					e.printStackTrace();

				}
			}
		}).start();
	}

	/**
	 * 将channelCfgTemp对象转为ChannelCfg对象返回 author dongsl date 2017年8月18日下午4:50:51
	 */
	private List<ChannelCfg> convertChannelCfgTempToChannelCfg(List<ChannelCfgTemp> tempList) {
		List<ChannelCfg> list = new ArrayList<ChannelCfg>();
		for (ChannelCfgTemp temp : tempList) {
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

	@Override
	public Channel saveNewVideo(Channel fsd) {
		try {
			String id = UuidUtil.getUuid();
			if (StringUtils.isEmptyOrNull(fsd.getUuid())) {
				fsd.setUuid(id);
			}
			Integer regionsId = PropUtils.getInt("id_video");
			fsd.setIsDel(Constants.DELETE_YES); // 默认删除状态
			fsd.setReserved(9);
			fsd.setRegionId(regionsId);
			channelDAO.saveChannel(fsd);
		} catch (Exception e) {
			log.error("调用ChannelService.saveNewChannel(Channel channel)方法异常！" + ExpUtil.getExceptionDetail(e));
			throw new BussinessException(e);
		}
		return fsd;
	}

	@Override
	public List<Channel> getChannelByIds(String... ids) {
		List<Channel> list = channelDAO.getChannelByIds(ids);
		return list;
	}

	@Override
	public List<Channel> getChannelByUuIds(List<String> list) {
		List<Channel> clist = channelDAO.getChannelByUuIds(list);
		return clist;
	}

	@Override
	public int queryVideoByChannelName(String fileName) {
		return channelDAO.queryVideoByChannelName(fileName);
	}

	/**
	 * 初始化channel的状态，将通道的抓拍状态置成0
	 */
	@Override
	public void initChannelState() {
		channelDAO.initChannelState();
	}

	@Override
	public Map<String, Object> queryChannelStatCount(Map<String, Object> channelStatMap) throws Exception {
		List<Channel> list = channelDAO.selectAllChannelList();
		if (list != null && list.size() > 0) {
			// 查出来处在执行中的任务
			List<Channel> openList = new ArrayList<>();
			List<Channel> closeList = new ArrayList<>();
			List<Jobs> jobs = jobsDAO.getUpdateStateJob(Arrays.asList(Constants.JOB_STATE_RUNNING));
			List<String> chanIds = new ArrayList<>();
			if (!CollectionUtils.isEmpty(jobs)) {
				List<JobsChannelTemp> resps = jobsChannelDAO
						.getJobsChannelByJobUuid(jobs.stream().map(t -> t.getUuid()).collect(Collectors.toList()));
				List<String> clist = resps.stream().map(a -> a.getChannelUuid()).collect(Collectors.toList());
				chanIds.addAll(clist);
			}
			for (Channel channel : list) {
				if (chanIds != null && chanIds.size() > 0) {
					if (chanIds.contains(channel.getUuid())) {
						openList.add(channel);
					} else {
						closeList.add(channel);
					}
				}
			}
			channelStatMap.put("openCount", openList == null ? 0 : openList.size());
			channelStatMap.put("closeCount", closeList == null ? 0 : closeList.size());
		} else {
			channelStatMap.put("openCount", 0);
			channelStatMap.put("closeCount", 0);
		}
		return channelStatMap;
	}

	@Override
	public Pager queryPageNew(Pager pager) {
		try {
			pager.getF().put("isDel", String.valueOf(Constants.DELETE_NO));// 默认查询未删除状态
			String regionId = pager.getF().get("regionId");
			List<String> list = new ArrayList<>();
			if (StringUtils.isNotEmpty(regionId)) {
				List<Regions> childList = regionsDAO.queryAllRegions();
				list = getChildList(childList, Integer.parseInt(regionId), list);
				list.add(regionId);
//				String regionIds = StringUtils.listToString(list);
//				pager.getF().put("regionIds", regionIds);
			}
			pager.setResultList(list);
			List<Channel> list1 = channelDAO.queryListNew(pager);
			int totalCount = channelDAO.selectCountNew(pager);
			pager.setTotalCount(totalCount);
			pager.setResultList(list1);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
		return pager;
	}

	/**
	 * 获取某个父节点下面的所有子节点
	 * 
	 * @param menuList
	 * @param pid
	 * @return
	 */

	public static List<String> getChildList(List<Regions> childList, int pid, List<String> list) {
		for (Regions r : childList) {
			// 遍历出父id等于参数的id，add进子节点集合
			if (Integer.valueOf(r.getParentId()) == pid) {
				// 递归遍历下一级
				getChildList(childList, Integer.valueOf(r.getId()), list);
				list.add(r.getId().toString());
			}
		}
		return list;
	}

	/**
	 * 根据区域id查询该区域下的通道
	 */
	@Override
	public List<Channel> queryChannelListUnderRegionByRegionId(Integer regionId) {
		List<Channel> list = channelDAO.queryChannelListUnderRegionByRegionId(regionId);
		return list;
	}

}
