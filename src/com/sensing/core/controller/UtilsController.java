package com.sensing.core.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.bean.Channel;
import com.sensing.core.bean.ReturnMsg;
import com.sensing.core.bean.alarm.req.AlarmReq;
import com.sensing.core.cacahes.PreviewCache;
import com.sensing.core.cacahes.SessionCache;
import com.sensing.core.dao.IAlarmDAO;
import com.sensing.core.dao.IChannelDAO;
import com.sensing.core.dao.IJobsDAO;
import com.sensing.core.dao.ISysMessageLogDAO;
import com.sensing.core.dao.ITaskDAO;
import com.sensing.core.service.ICapAttrConvertService;
import com.sensing.core.service.ICapService;
import com.sensing.core.service.IExportExcelService;
import com.sensing.core.service.IJobsService;
import com.sensing.core.service.IPreviewService;
import com.sensing.core.service.IRpcLogService;
import com.sensing.core.service.ITaskService;
import com.sensing.core.service.impl.ChannelServiceImpl;
import com.sensing.core.sipnotify.SipDeviceCache;
import com.sensing.core.utils.BaseController;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.RemoteShellExecutor;
import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.props.PropUtils;
import com.sensing.core.utils.results.ResultUtils;
import com.sensing.core.utils.task.JobsAndTaskTimer;
import com.sensing.core.utils.time.DateUtil;

/**
 * 通知调试工具类
 * <p>
 * Title: UtilsController
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company: www.sensingtech.com
 * </p>
 * 
 * @author mingxingyu
 * @date 2019年6月11日
 * @version 1.0
 */
@Controller
@RequestMapping("/utils")
@SuppressWarnings("all")
public class UtilsController extends BaseController {

	private static final Log log = LogFactory.getLog(UtilsController.class);

	@Autowired
	public ICapAttrConvertService capAttrConvertService;
	@Autowired
	public IRpcLogService rpcLogService;
	@Autowired
	public ICapService capService;
	@Resource
	public IExportExcelService exportExcelService;
	@Resource
	public IPreviewService previewService;
	@Resource
	public JobsAndTaskTimer jobsAndTaskTimer;
	@Resource
	public ChannelServiceImpl channelServiceImpl;
	@Resource
	public ITaskService taskService;
	@Resource
	public IJobsDAO jobsDAO;
	@Resource
	public IAlarmDAO alarmDAO;
	@Resource
	public IChannelDAO channelDAO;
	@Resource
	public ITaskDAO taskDAO;
	@Resource
	public ISysMessageLogDAO sysMessageLogDAO;
	@Resource
	public SipDeviceCache sipDeviceCache;

	/**
	 * callRecordVideo历史结构化任务抓拍通知方法
	 * 
	 * @param p
	 * @return
	 * @author mingxingyu
	 * @date 2019年5月20日 下午3:39:37
	 */
	@ResponseBody
	@RequestMapping("/callRecordVideo")
	public ResponseBean callRecordVideo(@RequestBody JSONObject p) {
		ResponseBean result = new ResponseBean();
		log.info("接收到callRecordVideo参数：" + p.toString());

		// 参数的解析与校验
		String uuid = p.getString("uuid");
		Integer status = p.getInteger("status");
		if (StringUtils.isEmpty(uuid) || uuid.split("_").length != 2 || status == null) {
			return ResultUtils.REQUIRED_EMPTY_ERROR();
		}
		return taskService.callRecordVideo(uuid.split("_")[0], uuid.split("_")[1], status);
	}

	/**
	 * sipserver定时更新通道缓存
	 * 
	 * @param p
	 * @return
	 * @author mingxingyu
	 * @date 2019年5月20日 下午3:39:37
	 */
	@ResponseBody
	@RequestMapping("/sipNotify")
	public ResponseBean sipNotify(@RequestBody JSONObject p) {
		ResponseBean result = new ResponseBean();
		try {
			log.info("接收到sipserver传来的通道更新数据。" + p.toString());
			if (StringUtils.isNotEmpty(p.toString())) {
				return sipDeviceCache.sipNotify(p);
			} else {
				return ResultUtils.REQUIRED_EMPTY_ERROR();
			}
		} catch (Exception e) {
			log.error(e);
			result.setError(100);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 离线视频完成之后，被抓拍调用通知修改db中的状态
	 * 
	 * @param p
	 * @return
	 * @author mingxingyu
	 * @date 2019年3月14日 下午1:34:54
	 */
	@ResponseBody
	@RequestMapping("/callStaticVideo")
	public ResponseBean callStaticVideo(@RequestBody JSONObject p) {
		ResponseBean result = new ResponseBean();
		try {
			String deviceUuid = p.getString("deviceId");
			Integer status = p.getInteger("status");// 0:完成 -1：失败 2：无法打开
			ReturnMsg msg = taskService.callStaticVideo(deviceUuid, status);

			result.setError(msg.getRes());
			result.setMessage(msg.getMsg());
		} catch (Exception e) {
			log.error(e);
			result.setError(100);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 用户调用该接口，记录操作的日志
	 * 
	 * @param p
	 * @return
	 * @author mingxingyu
	 * @date 2019年3月5日 上午10:28:25
	 */
	@ResponseBody
	@RequestMapping("/logTag")
	public ResponseBean logTag(@RequestBody JSONObject p) {
		ResponseBean result = new ResponseBean();
		try {
			result = ResultUtils.success();
		} catch (Exception e) {
			log.error(e);
			result.setError(100);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 前端视频预览关闭的通知
	 *
	 * @param p
	 * @return
	 * @author mingxingyu
	 * @date 2019年1月30日 下午3:10:29
	 */
	@ResponseBody
	@RequestMapping("/previewClosed")
	public ResponseBean previewClosed(@RequestBody JSONObject p) {
		ResponseBean result = new ResponseBean();
		try {
			String userId = getUuid();
			if (StringUtils.isBlank(userId)) {
				result = ResultUtils.REQUIRED_EMPTY_ERROR();
			}
//    		previewService.delDevice(userId);
			result = ResultUtils.success();
		} catch (Exception e) {
			log.error(e);
			result.setError(100);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 前端视频预览定时通讯
	 *
	 * @param p
	 * @return
	 * @author mingxingyu
	 * @date 2019年1月30日 下午3:10:29
	 */
	@ResponseBody
	@RequestMapping("/previewOnline")
	public ResponseBean previewOnline(@RequestBody JSONObject p) {
		ResponseBean result = new ResponseBean();
		try {
			String userId = getUuid();
			String deviceId = p.getString("deviceId");
			if (StringUtils.isBlank(userId) || StringUtils.isBlank(deviceId)) {
				result = ResultUtils.REQUIRED_EMPTY_ERROR();
			}
			result = previewService.updateData(userId, deviceId);
		} catch (Exception e) {
			log.error("previewOnline,打开画框" + com.sensing.core.utils.StringUtils.getStackTrace(e));
			result.setError(100);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 查询在线用户的数量
	 *
	 * @param p
	 * @return
	 * @author mingxingyu
	 * @date 2018年12月18日 下午4:30:11
	 */
	@ResponseBody
	@RequestMapping("/queryOnlineUserCount")
	public ResponseBean queryOnlineUserCount(@RequestBody JSONObject p) {
		ResponseBean result = new ResponseBean();
		try {
			result = ResultUtils.success("userOnlineCount", SessionCache.getSessionSize());
		} catch (Exception e) {
			log.error(e);
			result.setError(100);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 打开抓拍通道画框的接口
	 *
	 * @param p
	 * @return
	 * @author mingxingyu
	 * @date 2018年12月6日 下午4:01:49
	 */
	@ResponseBody
	@RequestMapping("/openChannelFrame")
	public ResponseBean openChannelFrame(@RequestBody JSONObject p) {
		ResponseBean result = new ResponseBean();
		try {
			String channelUuid = p.getString("channelUuid");
			if (StringUtils.isNotBlank(channelUuid)) {
				// 打开通道后，要延迟2s打开画框，否则报错-4104，尝试3次
				Integer integer = openFrame(channelUuid, 3);
				if (integer == 1) {
					result = ResultUtils.success();
				} else {
					result = ResultUtils.error(-1, "openChannelFrame调用失败");
				}
			} else {
				result = ResultUtils.REQUIRED_EMPTY_ERROR();
			}

		} catch (Exception e) {
			log.error(com.sensing.core.utils.StringUtils.getStackTrace(e));
			result.setError(100);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	public Integer openFrame(String channelUuid, int count) throws Exception {
		int result = 0;
		while (true) {
			Integer integer = capService.openChannelFrame(channelUuid);
			if (integer == null || integer < 0) {
				Thread.sleep(2000);
				count--;
				if (count == 0) {
					result = -1;
					break;
				}
			} else {
				log.info("openFrame失败,重试次数为" + count);
				result = 1;
				break;
			}
		}

		return result;
	}

	/**
	 * 获取显卡信息
	 *
	 * @param p
	 * @return
	 * @author mingxingyu
	 * @date 2018年12月3日 上午11:10:55
	 */
	@ResponseBody
	@RequestMapping("/getGraphicsCardInfo")
	public ResponseBean getGraphicsCardInfo(@RequestBody JSONObject p) {
		ResponseBean result = new ResponseBean();
		try {
			int node = PropUtils.getInt("cap_server_node_count");
			List<String> gcList = new ArrayList<String>();
			for (int k = 0; k < node; k++) {

				RemoteShellExecutor executor = new RemoteShellExecutor(PropUtils.getString("remote.shell.ip" + (k + 1)),
						PropUtils.getInt("remote.shell.port" + (k + 1), 22),
						PropUtils.getString("remote.shell.username" + (k + 1)),
						PropUtils.getString("remote.shell.password" + (k + 1)), "utf-8");
				String resultExe = executor.exec("nvidia-smi");

				String[] resultStr = resultExe.split("\n");
				int i = 0;
				boolean flag = false;

				StringBuffer str = null;
				for (String resultRow : resultStr) {
					if (resultRow.contains(" " + i + " ")) {
						String[] gcRowArr = resultRow.split("\\|");
						String gcStr = gcRowArr[1].trim();
						String gcInfo = gcStr.substring(gcStr.indexOf(" "), gcStr.lastIndexOf(" ")).trim();
						str = new StringBuffer(gcInfo);
						i++;
						flag = true;
						continue;
					}
					if (flag) {
						String[] memoryArr = resultRow.split("\\|");
						String memoryStr = memoryArr[2].trim();
						String memoryInfo = memoryStr.replaceAll(" ", "");
						str.append(" " + memoryInfo);
						gcList.add(str.toString());
						flag = false;
					}
					if (resultRow.contains("Processes")) {
						break;
					}
				}
			}

			result.getMap().put("gcList", gcList);
			result.setError(0);
			result.setMessage("successful");
		} catch (Exception e) {
			log.error(e);
			result.setError(100);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * (jenkins启动tomcat以后，测试心跳的接口)
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/tomcatheart")
	public ResponseBean tomcatHeart(@RequestBody JSONObject p) {
		return new ResponseBean();
	}

	/**
	 * 获得flash的存放地址
	 *
	 * @param p
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getflashplayer")
	public ResponseBean getFlashPlayer(@RequestBody JSONObject p) {
		String requestPath = PropUtils.getString("watch_download") + "/install_flash_player_ax.exe";
		return new ResultUtils().success(requestPath);
	}

	/**
	 * 获得chrome的存放地址
	 *
	 * @param p
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getchromesetup")
	public ResponseBean gethromesSetup(@RequestBody JSONObject p) {
		String requestPath = PropUtils.getString("watch_download") + "/ChromeSetup.exe";
		return new ResultUtils().success(requestPath);
	}

	@ResponseBody
	@RequestMapping("/cleandevice")
	public ResponseBean cleandevice(@RequestBody JSONObject p) {
		PreviewCache.deviceTimeMap = new ConcurrentHashMap<String, Long>();
		return new ResultUtils().success(PreviewCache.deviceTimeMap);
	}

	@ResponseBody
	@RequestMapping("/jobsandtasktimer")
	public ResponseBean jobsAndTaskTimer(@RequestBody JSONObject p) {
		jobsAndTaskTimer.startJobs();
		return new ResultUtils().success();
	}

	/**
	 * 获取系统信息
	 * 
	 * @param p
	 * @return
	 */
	/*
	 * @ResponseBody
	 * 
	 * @RequestMapping("/getSystemInfo") public ResponseBean
	 * getSystemInfo(@RequestBody JSONObject p) { Map<String, String> map = new
	 * HashMap<>(); String name = Constants.SYSTEM_NAME; String version =
	 * Constants.SYSTEM_VERSION; String systemName = null; String systemVersion =
	 * null; try { systemName = new String(name.getBytes("ISO-8859-1"), "UTF-8");
	 * systemVersion = new String(version.getBytes("ISO-8859-1"), "UTF-8");
	 * map.put("name", systemName); map.put("version", systemVersion); } catch
	 * (UnsupportedEncodingException e) { e.printStackTrace();
	 * log.error("系统信息编码转换失败，失败信息为：" + e.getMessage()); return new
	 * ResultUtils().error(100, "编码转换失败"); } return new ResultUtils().success(map);
	 * }
	 */

	@ResponseBody
	@RequestMapping("/getnowpreview")
	public ResponseBean getnowpreview(@RequestBody JSONObject p) {

		StringBuilder builder = new StringBuilder();
		Set<String> keys = PreviewCache.deviceTimeMap.keySet();
		if (keys != null && keys.size() > 0) {
			List<String> ids = new ArrayList();
			for (String deviceId : keys) {
				ids.add(deviceId);
			}
			List<Channel> list = channelServiceImpl.getChannelByUuIds(ids);
			for (String deviceId : keys) {
				Long time = PreviewCache.deviceTimeMap.get(deviceId);
				String date = DateUtil.stampToDate(time * 1000 + "");
				List<Channel> clist = list.stream().filter(a -> a.getUuid().equals(deviceId))
						.collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(clist)) {
					String s = DateUtil.DateToString(new Date()) + "--通道uuid--" + deviceId + "---通道名称---"
							+ clist.get(0).getChannelName() + "--通道更新时间--" + date;
					builder.append(s);
				}
			}
		}

		return new ResultUtils().success(new String(builder));
	}

	/**
	 * 首页获取报警未核查数量 布控的数量(包含布控中、暂停中、待启动、休息中 ) 和布控告警模块待我审批的待审批数量
	 * 
	 * @param p
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getTaskAndJobsAndAuditingCount")
	public ResponseBean getTaskAndJobsAndAuditingCount(@RequestBody JSONObject p) {
		ResponseBean result = new ResponseBean();
		Map<String, Object> mapCount = new HashMap<String, Object>();
		Pager pager = new Pager();
		try {
			String userUuid = getUuid();
			pager.getF().put("querytype", 0 + "");
			pager.getF().put("uuid", userUuid);
			pager.getF().put("ratifyResult", 0 + "");
			AlarmReq req = new AlarmReq();
			req.getF().put("state", 10 + "");
			req.getF().put("jobsType", 3 + "");
			Integer alarmCount = alarmDAO.selectCount(req);// 报警数量
			Integer auditingCount = jobsDAO.ratifyListCount(pager);// 审批数量
			// 10:待启动 20:布控中 30:暂停中 40:休息中 四种状态的和为布控数量
			pager.getF().put("state", 10 + "");
			Integer jobCount1 = jobsDAO.jobListCount(pager);// 布控待启动数量
			pager.getF().put("state", 20 + "");
			Integer jobCount2 = jobsDAO.jobListCount(pager);// 布控中数量
			pager.getF().put("state", 30 + "");
			Integer jobCount3 = jobsDAO.jobListCount(pager);// 布控暂停中数量
			pager.getF().put("state", 40 + "");
			Integer jobCount4 = jobsDAO.jobListCount(pager);// 布控休息中数量
			mapCount.put("auditingCount", auditingCount);// 审批
			mapCount.put("taskCount", jobCount1 + jobCount2 + jobCount3 + jobCount4);// 布控
			mapCount.put("jobsCount", alarmCount);// 报警
			result.setMap(mapCount);
			result = ResultUtils.success(mapCount);
		} catch (Exception e) {
			log.error("首页获取报警、布控和审批数量报错：" + com.sensing.core.utils.StringUtils.getStackTrace(e));
			result.setError(100);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 资源统计
	 * 
	 * @param p
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/resourceStatistics")
	public ResponseBean resourceStatistics(@RequestBody JSONObject p) {
		ResponseBean result = new ResponseBean();
		Map<String, Object> mapCount = new HashMap<String, Object>();
		Pager pager = new Pager();
		try {
			ResponseBean bean = getGraphicsCardInfo(p);
			List<String> gcList = (List<String>) bean.getMap().get("gcList");
			Integer allChannelCount = gcList.size() * 15;
			pager.getF().put("isDel", 0 + "");
			pager.getF().put("capStat", 1 + "");
			// 处理中的通道
			Integer runningChannelCount = channelDAO.selectCount(pager);
			pager.setPageFlag(null);
			List<Channel> runningChannelList = channelDAO.queryListNew(pager);
			List<String> runningChannels = new ArrayList<String>();
			if (runningChannelList != null && runningChannelList.size() > 0) {
				runningChannels = runningChannelList.stream().map(a -> a.getUuid()).collect(Collectors.toList());
			}
			// 添加离线处理中的任务个数
			Integer offlineCount = taskDAO.getOfflineVideoCount();
			// 实时任务 休息中占用资源
			pager.getF().put("type", 1 + "");
			pager.getF().put("state", 2 + "");
			int sleepingTaskCount = taskDAO.selectCount(pager);
			// 布控任务 休息中占用资源
			pager.getF().put("state", 40 + "");
			int sleepingJobsCount = jobsDAO.jobListCount(pager);
			runningChannelCount = runningChannelCount + offlineCount + sleepingJobsCount + sleepingTaskCount;
			if (allChannelCount != null && allChannelCount > 0) {
				double c = (double) runningChannelCount / (double) allChannelCount;
				if (c < 0.66) {
					mapCount.put("stateTag", "宽松");// 状态：1：宽松:2：拥挤:3：满载
				} else if (c >= 0.66 && c < 1) {
					mapCount.put("stateTag", "拥挤");// 状态：1：宽松:2：拥挤:3：满载
				} else if (c == 1) {
					mapCount.put("stateTag", "满载");// 状态：1：宽松:2：拥挤:3：满载
				}
			} else {
				mapCount.put("stateTag", 1);// 状态：1：宽松:2：拥挤:3：满载
			}
			mapCount.put("allChannel", allChannelCount);// 所有通道
			mapCount.put("runningChannel", runningChannelCount);// 已用通道
			mapCount.put("runningChannelList", runningChannels);// 已用通道list
			mapCount.put("remainingChannel",
					(allChannelCount - runningChannelCount) < 0 ? 0 : allChannelCount - runningChannelCount);// 可用通道
			result.setMap(mapCount);
			result = ResultUtils.success(mapCount);

		} catch (Exception e) {
			log.error("资源统计报错：" + com.sensing.core.utils.StringUtils.getStackTrace(e));
			result.setError(100);
			result.setMessage(e.getMessage());
		}
		return result;
	}
}
