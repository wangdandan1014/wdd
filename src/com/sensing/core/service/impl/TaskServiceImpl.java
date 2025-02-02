package com.sensing.core.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.TextUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Joiner;
import com.sensing.core.aop.RequestInfoLog;
import com.sensing.core.bean.ReturnMsg;
import com.sensing.core.bean.RpcLog;
import com.sensing.core.bean.RunningTaskCountReq;
import com.sensing.core.bean.StopTaskReq;
import com.sensing.core.bean.Task;
import com.sensing.core.bean.TaskChannel;
import com.sensing.core.bean.TaskChannelResp;
import com.sensing.core.bean.TaskRequest;
import com.sensing.core.bean.TaskResp;
import com.sensing.core.bean.TaskRespList;
import com.sensing.core.bean.TaskRespTempList;
import com.sensing.core.dao.IChannelDAO;
import com.sensing.core.dao.ISysUserDAO;
import com.sensing.core.dao.ITaskChannelDAO;
import com.sensing.core.dao.ITaskDAO;
import com.sensing.core.service.IChannelService;
import com.sensing.core.service.IRpcLogService;
import com.sensing.core.service.ITaskService;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.UuidUtil;
import com.sensing.core.utils.Exception.ExpUtil;
import com.sensing.core.utils.results.ResultUtils;
import com.sensing.core.utils.task.HistoryTimerTask;
import com.sensing.core.utils.task.JobsAndTaskTimer;
import com.sensing.core.utils.task.TaskTimerTask;
import com.sensing.core.utils.time.DateStyle;
import com.sensing.core.utils.time.DateUtil;
import com.sensing.core.utils.time.QueryDateUtils;
import com.sensing.core.utils.time.TransfTimeUtil;

/**
 * @author wenbo
 */
@Service
public class TaskServiceImpl implements ITaskService {

	private static final Log log = LogFactory.getLog(ITaskService.class);

	@Resource
	public ITaskDAO taskDAO;
	@Resource
	public ITaskChannelDAO taskChannelDAO;
	@Resource
	public IChannelDAO channelDAO;
	@Resource
	public ISysUserDAO sysUserDAO;
	@Resource
	private IRpcLogService rcpLogService;
	@Resource
	public TaskTimerTask taskTimerTask;
	@Resource
	public JobsAndTaskTimer jobsAndTaskTimer;
	@Resource
	public HistoryTimerTask historyTimerTask;
	@Resource
	public CaptureServiceImpl captureServiceImpl;
	@Resource
	public RequestInfoLog requestInfoLog;
	@Resource
	public IRpcLogService rpcLogService;
	@Resource
	public IChannelService channelService;

	public TaskServiceImpl() {
		super();
	}
	
	/**
	 * 历史视频通道处理完毕的回调处理
	 * @param deviceId	通道的uuid
	 * @param taskId	任务的uuid
	 * @param status	回调状态码
	 * @return
	 * @author mingxingyu
	 * @date   2019年6月11日 下午4:52:19
	 */
	public ResponseBean callRecordVideo(String deviceId,String taskId,Integer status){
		try {
			//设置该任务关联的通道的处理结果的状态
			TaskChannel taskChannel = null;
			Pager pager = new Pager();
			pager.getF().put("taskUuid",taskId);
			pager.getF().put("channelUuid",deviceId);
			pager.getF().put("pageFlag","all");
			List<TaskChannel> list = taskChannelDAO.queryList(pager);
			if ( list != null && list.size() > 0 ) {
				taskChannel = list.get(0);
			}
			
			if ( taskChannel == null ) {
				return ResultUtils.error(-1,"根据通道和任务，未查找到关联信息");
			}
			taskChannel.setFinishState(status);
			taskChannelDAO.updateTaskChannel(taskChannel);
			
			//查询该任务关联下的通道，是否都已被通知到
			//都已被通知到的情况下，修改任务的状态为已完成
			
			/**
			 * 实现逻辑：
			 * 关联多个通道，一个个通道的被通知
			 * 所有的通道都被通知了，即finishState字段都有值。有一个通道是已完成的状态，该任务即已完成。所有的通道都是未完成的状态，该任务即失败。
			 */
			//处理完成的标识
			boolean finishState = false;
			//是否修改任务的状态标识
			boolean updateState = true;
			List<TaskChannel> taskChannelList = taskChannelDAO.getTaskChannelByTaskId(taskId);
			for (TaskChannel tc : taskChannelList) {
				if ( tc.getFinishState() != null ) {
					// 历史视频的抓拍回调的
					if ( tc.getFinishState() == 0 ) {//处理成功
						finishState = true;
					}
				}else{
					updateState = false;
					break;
				}
			}
			//任务已完成，成功的标识
			if ( updateState && finishState ) {
				Task task = taskDAO.getTask(taskId);
				task.setState(Constants.TASK_STAT_DONE);
				taskDAO.updateState(task);
			}
			//任务已完成，失败的标识
			if ( updateState && !finishState ) {
				Task task = taskDAO.getTask(taskId);
				task.setState(Constants.TASK_STAT_FAILEE);
				taskDAO.updateState(task);
			}
			return ResultUtils.success("处理成功。任务是否完成："+finishState);
		} catch (Exception e) {
			log.error("历史视频通道处理完毕，抓拍回调处理过程中发生异常："+e.getMessage());
			e.printStackTrace();
			return ResultUtils.error(-1,"后台处理发生异常："+e.getLocalizedMessage());
		}
	}

	@Override
	public TaskRequest saveNewTask(TaskRequest request) {

		// 保存task
		String taskUuid = UuidUtil.getUuid();
		request.getTask().setUuid(taskUuid);
		int code = taskDAO.saveTask(request.getTask());
		// 保存taskchannel 可能多条
		String[] split = request.getTaskChannel().getChannelUuid().split(",");
		for (int i = 0; i < split.length; i++) {
			request.getTaskChannel().setChannelUuid(split[i]);
			request.getTaskChannel().setUuid(UuidUtil.getUuid());
			request.getTaskChannel().setTaskUuid(taskDAO.getTask(taskUuid).getUuid());
			taskChannelDAO.saveTaskChannel(request.getTaskChannel());
		}

		return code > 0 ? request : null;
	}

	@Override
	public TaskRequest updateTask(TaskRequest request) {

		Task originalTask = taskDAO.getTask(request.getTask().getUuid());
		if (originalTask == null) {
			return null;
		}
		if (request.getTask().getIsDeleted() != null && request.getTask().getIsDeleted().intValue() == 1) {
			// 删除
			return delTask(originalTask, request);
		} else {
			// 修改
			return modifyTask(request);
		}

	}

	/**
	 * 离线视频任务处理完成之后，抓拍通知修改库状态
	 * 
	 * @param deviceUuid 通道的uuid
	 * @param status     抓拍完成情况的状态值
	 * @return
	 * @author mingxingyu
	 * @date 2019年3月14日 下午1:46:02
	 */
	public ReturnMsg callStaticVideo(String deviceUuid, Integer status) {

		ReturnMsg msg = new ReturnMsg();
		RpcLog rpcLog = new RpcLog();
		try {
			rpcLog.setCallTime(new Date());
			rpcLog.setMode(Constants.INTERFACE_CALL_TYPE_PASS);
			rpcLog.setMemo("deviceUuid:" + deviceUuid + ",state:" + status);
			rpcLog.setModule(Constants.SEVICE_MODEL_STATICVIDEO);
			rpcLog.setTodo("修改状态");

			List<String> deviceUuidList = new ArrayList<String>();
			deviceUuidList.add(deviceUuid);
			List<TaskChannel> taskChannelList = getTaskChannelByChannelIds(deviceUuidList);

			String taskId = null;
			for (TaskChannel taskChannel : taskChannelList) {
				taskId = taskChannel.getTaskUuid();
			}

			if (StringUtils.isNotEmpty(taskId)) {
				Task task = new Task();
				task.setUuid(taskId);
				if (status == 0) {
					task.setState(Constants.TASK_STAT_DONE);
				} else if (status == -1) {
					task.setState(Constants.TASK_STAT_FAILEE);
				} else if (status == -2) {
					task.setState(Constants.TASK_STAT_FAILEE);
				}
				task.setModifyTime(new Date());
				updateState(task);
//				channelService.removeChannel(deviceUuid, null);
				channelService.CallRemoveChannel(deviceUuid);
				log.info("抓拍调用callStaticVideo接口删除通道成功！通道id为" + deviceUuid);

				msg.setMsg("更新成功");
				msg.setRes(0);
				rpcLog.setResult("成功");
			} else {
				msg.setMsg("通道不存在对应任务");
				msg.setRes(-1);
				rpcLog.setResult("失败");
				rpcLog.setErrorMsg("通道不存在对应任务");
			}
		} catch (Exception e) {
			msg.setMsg(e.getMessage());
			msg.setRes(-1);
			rpcLog.setResult("异常");
			rpcLog.setErrorMsg(e.getMessage());
			log.error("离线视频回调修改状态失败:" + ExpUtil.getExceptionDetail(e));
			return msg;
		} finally {
			rpcLogService.saveNewRpcLog(rpcLog);
		}
		return msg;
	}

	private TaskRequest delTask(Task originalTask, TaskRequest request) {
		// 处理中不能删除 失败一般都是和抓拍通信失败导致失败，如果删除会导致，通道状态异常
		if (originalTask.getState().intValue() == Constants.TASK_STAT_RUNNING
				|| originalTask.getState() == Constants.TASK_STAT_FAILEE) {
			return null;
		}
		// 处理中的任务不可删除，其他状态，删除时不需要通知通道关闭
		taskDAO.updateTask(request.getTask());
		taskChannelDAO.removeTaskChannelByTaskId(request.getTask().getUuid());
		return request;
	}

	private TaskRequest modifyTask(TaskRequest request) {
		taskDAO.updateTask(request.getTask());
		if (request.getTaskChannel() != null && !TextUtils.isEmpty(request.getTaskChannel().getChannelUuid())) {
			// 关联通道做硬删除
			taskChannelDAO.removeTaskChannelByTaskId(request.getTask().getUuid());
			// 保存taskchannel 可能多条
			String[] split = request.getTaskChannel().getChannelUuid().split(",");
			for (int i = 0; i < split.length; i++) {
				request.getTaskChannel().setChannelUuid(split[i]);
				request.getTaskChannel().setUuid(UuidUtil.getUuid());
				request.getTaskChannel().setTaskUuid(request.getTask().getUuid());
				taskChannelDAO.saveTaskChannel(request.getTaskChannel());
			}
		}
		return request;
	}

	@Override
	public Task updateState(Task task) {
		taskDAO.updateState(task);
		return task;
	}

	@Override
	public Task findTaskById(java.lang.String uuid) {
		return taskDAO.getTask(uuid);
	}

	@Override
	public void removeTask(String uuid) {
		taskDAO.removeTask(uuid);
		taskChannelDAO.removeTaskChannelByTaskId(uuid);
	}

	@Override
	public List<TaskChannel> getTaskChannelByChannelIds(List<String> list) {
		List<TaskChannel> task = taskChannelDAO.getTaskChannelByChannelIds(list);
		return task;
	}

	@Override
	public Pager query(Pager pager) {
		List<Task> list = taskDAO.queryList(pager);
		int totalCount = taskDAO.selectCount(pager);
		List<String> taskUuids = list.stream().map(a -> a.getUuid()).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(taskUuids)) {
			pager.setTotalCount(0);
			return pager;
		}
		// task_channel
		List<TaskChannelResp> taskChannelList = taskChannelDAO.getTaskChannelByTaskIds(taskUuids);
		/***** 返回数据 ****/
		List<TaskRespTempList> resultList = new ArrayList<>();
		TaskRespTempList l;
		for (Task t : list) {
			l = new TaskRespTempList();
			List<String> channel = taskChannelList.stream().filter(a -> a.getTaskUuid().equals(t.getUuid()))
					.map(b -> b.getChannel_name()).collect(Collectors.toList());

			l.setChannelNames(Joiner.on(",").join(channel));
			l.setChannelList(channel);
			l.setAnaly_type(t.getAnalyType());
			l.setBegin_date(t.getBeginDate());
			l.setBegin_date_str(t.getBeginDateStr());
			l.setEnd_date(t.getEndDate());
			l.setEnd_date_str(t.getEndDateStr());
			l.setStart_time(t.getStartTime());
			l.setEnd_time(t.getEndTime());
			l.setCreateUserUuid(t.getCreateUserUuid());
			l.setCreateUser(t.getCreateUser());
			l.setState(t.getState());
			l.setAnaly_start_time(t.getAnalyStartTime());
			l.setAnaly_end_time(t.getAnalyEndTime());
			l.setTaskName(t.getName());
			l.setTaskUuid(t.getUuid());
			l.setType(t.getType());
			l.setIsDel(t.getIsDeleted() == null ? 0 : t.getIsDeleted().intValue());
			l.setCreateUser(t.getCreateUser());
			l.setRunWeek(t.getRunWeek());
			l.setAnaly_begin_date(t.getAnalyBeginDate());
			l.setAnaly_end_date(t.getAnalyEndDate());
			l.setRunWeek(t.getRunWeek());

			resultList.add(l);
		}
		pager.setTotalCount(totalCount);
		/***** 给前端拼接字段 ****/
		pager.setResultList(fillTaskList2(resultList));
		return pager;
	}

	@Override
	public Pager queryListByName(Pager pager) {
		List<TaskRespTempList> list = taskDAO.queryListByName(pager);
		int count = taskDAO.selectCountByName(pager);
		pager.setTotalCount(count);
		/***** 给前端拼接字段 ****/
		pager.setResultList(fillTaskList2(list));
		return pager;
	}

	/**
	 * 返回前段可以直接使用的数据集合
	 *
	 * @param taskResps
	 * @return
	 */

	public List<TaskRespList> fillTaskList2(List<TaskRespTempList> taskResps) {
		List<TaskRespList> list = new ArrayList<>();
		TaskRespList l = null;
		for (TaskRespTempList t : taskResps) {
			l = new TaskRespList();

			l.setTaskUuid(t.getTaskUuid());
			l.setTaskName(t.getTaskName());
			l.setState(t.getState());
			l.setCreateUser(t.getCreateUser());
			l.setIsDel(t.getIsDel());
			l.setType(t.getType());
			//任务的状态
			int type = t.getType() != null ? t.getType() : 0;
			/***** 通道 "名字A，名字CB，名字C，" ****/
//			if (t.getChannelList() != null && t.getChannelList().size() > 0) {
			if (StringUtils.isNotEmpty(t.getChannelNames())) {
				List<String> cList = Arrays.asList(t.getChannelNames().split(","));
//				List<String> cList = t.getChannelList();
				String channelDesc = "";
				if (cList.size() == 1) {
					channelDesc = cList.get(0) + " ";
				} else if (cList.size() == 2) {
					channelDesc = cList.get(0) + "、" + cList.size() + " ";
				} else if (cList.size() >= 3) {
					channelDesc = cList.get(0) + "、" + cList.size() + "等";
				}
				channelDesc = channelDesc + cList.size() + "个通道";
				l.setChannelDesc(channelDesc);
				l.setChannelDescAll(t.getChannelNames());
				l.setChannelCount(cList.size());
			} else {
				l.setChannelDesc("暂无关联通道");
			}

			String analyType = t.getAnaly_type();
			if (!TextUtils.isEmpty(analyType)) {
				List<String> strings = Arrays.asList(analyType.split(","));
				List<String> anayTypes = strings.stream().map(a -> Constants.CAP_ANALY_TYPE.get(Integer.parseInt(a)))
						.filter(x -> StringUtils.isNotEmpty(x)).collect(Collectors.toList());
				l.setTaskType(Joiner.on("、").join(anayTypes));
				l.setTaskTypeValue(analyType);
			}
			/***** 任务时段 ****/
			String taskTimeTop = "";
			String taskTimeCenter = "";
			String taskTimeBottom = "";
			if (type == Constants.TASK_TYPE_REALTIME) {
				// 如果是永久任务则beginDate和endDate是为空的，都有值则是按时间段执行的任务
				if (t.getBegin_date() == null && t.getEnd_date() == null) {
					taskTimeTop = "永久任务";
				} else if (t.getBegin_date() != null && t.getEnd_date() != null) {
					taskTimeTop = t.getBegin_date_str() + "~" + t.getEnd_date_str() + "";
				}
				if (StringUtils.isEmptyOrNull(t.getRunWeek())) {
					taskTimeCenter = "每天";
				} else {
					taskTimeCenter = StringUtils.getWeekString(t.getRunWeek(), "、");
				}

			} else if (type == Constants.TASK_TYPE_HISTORY) {
				taskTimeTop = t.getAnaly_begin_date() + "~" + t.getAnaly_end_date() + "";
			}
			l.setTaskTimeTop(taskTimeTop);
			l.setTaskTimeCenter(taskTimeCenter);
			l.setTaskTimeBottom(taskTimeBottom);

			/***** 分析时段 ****/

			String analyTime = "";
			if (type == Constants.TASK_TYPE_REALTIME) {
				analyTime = t.getAnaly_start_time() + "~" + t.getAnaly_end_time();
			} else if (type == Constants.TASK_TYPE_HISTORY) {
				analyTime =  t.getAnaly_start_time();
			}
			l.setAnalyTime(analyTime);
			/***** 状态 ****/
			l.setTaskState(Constants.TASK_STAT_MAP.get(t.getState()));
			l.setCreateUser(t.getCreateUser());
			l.setCreateUserUuid(t.getCreateUserUuid());
			list.add(l);
		}
		return list;
	}

	@Override
	public TaskResp info(String uuid) {

		Task task = taskDAO.getTask(uuid);
		if (task == null)
			return null;
		Long endTime = null;
		Long startTime = null;
		if (task != null && task.getEndDate() != null && task.getEndDate().compareTo(new Date()) < 0) {
			Date endDate = task.getEndDate();
			endTime = endDate.getTime() / 1000 + 60 * 60 * 24 - 1;
			startTime = endTime - (6 * 24 * 60 * 60) + 1;
		} else {
			Date[] dates = QueryDateUtils.get7Day();
			endTime = dates[0].getTime() / 1000;
			startTime = dates[1].getTime() / 1000;
		}
		if (StringUtils.isEmptyOrNull(task.getStartTime())) {
			task.setStartTime(TransfTimeUtil.UnixTimeStamp2Date(startTime, DateStyle.YYYY_MM_DD_HH_MM_SS));
		}
		if (StringUtils.isEmptyOrNull(task.getEndTime())) {
			task.setEndTime(TransfTimeUtil.UnixTimeStamp2Date(endTime, DateStyle.YYYY_MM_DD_HH_MM_SS));
		}
		// task_channel
		List<TaskChannelResp> taskChannelList = taskChannelDAO.getTaskChannelRespByTaskId(task.getUuid());
		taskChannelList = StringUtils.removeDuplicate(taskChannelList);
		TaskResp taskResp = new TaskResp();
		taskResp.setTask(task);
		taskResp.setTaskChannel(taskChannelList);
		return taskResp;

	}

	@Override
	public List<Task> getUpdateStateTask(List<Integer> list) {

		List<Task> rest = taskDAO.getUpdateStateTask(list);
		return rest;
	}

	@Override
	public int getrunningtaskCount(RunningTaskCountReq req) {
//        int count = taskDAO.getrunningtaskCount(req);
		//  2019/1/18 lxh 个数限制先去掉
		int count = 0;
		return count;
	}

	/**
	 * 停止任务（待启动和处理中才能停止） 2018/9/21
	 *
	 * @param req
	 * @return
	 */
	@Override
	public ResponseBean stoptaskByUuId(StopTaskReq req) {
		ResponseBean responseBean = new ResponseBean();
		int result = 1;
		Task task = taskDAO.getTask(req.getTaskUuid());
		if ((task == null) || task != null && task.getState().intValue() != req.getState().intValue()) {
			return ResultUtils.error(-1, "请刷新页面后重试");
		}

		Integer preState = task.getState();
		// 设置成已完成即可
		task.setNewState(Constants.TASK_STAT_STOP);
		log.info("[" + task.getName() + "]任务在"
				+ TransfTimeUtil.UnixTimeStamp2Date(new Date().getTime() / 1000, DateStyle.YYYY_MM_DD_HH_MM_SS)
				+ "时刻停止------------");
		/*** 正在进行中的任务要关闭通道 *****/
		responseBean = jobsAndTaskTimer.startJobWithPre(req.getTaskUuid(),false);

		return responseBean;
	}

	/**
	 * 开启任务(已停止的任务启动，待启动和处理中才能停止) 2018/9/21
	 *
	 * @param req
	 * @return
	 */
	@Override
	public ResponseBean startTaskByUuId(StopTaskReq req) {
		int result = 1;
		Task task = taskDAO.getTask(req.getTaskUuid());
		int preState = task.getState();
		if ((task == null) || task != null && task.getState().intValue() != req.getState().intValue()) {
			return ResultUtils.error(-1, "请刷新页面后重试");
		}
		//2018/12/10 lxh 遗留bug， 处理中停止，在开启 变成的状态 休息中（理论上是：休息中和待启动（查mongo里该任务））
		task.setNewState(Constants.TASK_STAT_INREST);// 将原状态变成处理中，然后在调取方法得到新值
		taskDAO.setUpdateStateTask(task);
		log.info("[" + task.getName() + "]任务在"
				+ TransfTimeUtil.UnixTimeStamp2Date(new Date().getTime() / 1000, DateStyle.YYYY_MM_DD_HH_MM_SS)
				+ "时刻开启------------");
		jobsAndTaskTimer.startJobs();
//        int newState = taskTimerTask.getTaskState(task);
//        List<TaskChannelResp> taskChannel = taskChannelDAO.getTaskChannelByTaskIds(Arrays.asList(task.getUuid()));
//        task.setNewState(newState);
//        if (newState == Constants.TASK_STAT_RUNNING) {
//            result = taskTimerTask.openTask(Arrays.asList(task), taskChannel);
//        } else if (newState == Constants.TASK_STAT_INREST || newState == Constants.TASK_STAT_DONE) {
//            result = taskTimerTask.takeRestAndDoneTask(Arrays.asList(task), taskChannel);
//        } else {
//            task.setNewState(Constants.TASK_STAT_INREST);
//            taskDAO.setUpdateStateTask(task);
//            taskTimerTask.setTaskRpcLog(Arrays.asList(task), taskChannel);
//        }
		return result > 0 ? ResultUtils.success() : ResultUtils.UNKONW_ERROR();
	}

	@Override
	public List<Task> getTaskByName(String name, Integer type) {
		List<Task> taskByName = taskDAO.getTaskByName(name, type);
		return taskByName;
	}

	@Override
	public Map getAnalyTimeByUuid(String taskUuid) {
		Task task = taskDAO.getTask(taskUuid);
		if (task == null) {
			return null;
		}
		Map<Long, Long> timeMap = new TreeMap<>(new Comparator<Long>() {
			@Override
			public int compare(Long o1, Long o2) {
				long l = o1.longValue() - o2.longValue();
				if (l > 0) {
					return 1;
				} else {
					return -1;
				}
			}
		});

		Date starDate = null;
		Date endDate = null;

		if (task.getBeginDate() == null && task.getEndDate() == null) {
			starDate = task.getCreateTime();
			endDate = new Date();
			// 去掉时分秒的限制
			starDate = DateUtil.removeHMS(starDate);
			endDate = DateUtil.removeHMS(endDate);
		} else if (task.getBeginDate() != null && task.getEndDate() != null) {
			/*******************************************
			 * 指定时间段任务 start
			 **************************************************************/
			starDate = task.getBeginDate();
			endDate = task.getEndDate();
		}
		try {
			List<Integer> runWeek = Arrays.asList(task.getRunWeek().split(",")).stream().map(a -> Integer.parseInt(a))
					.collect(Collectors.toList());
			for (Integer w : runWeek) {
				timeMap.putAll(getWeekDate(starDate, endDate, w, task));
			}

			//  2018/9/26 lxh 如果运行周期是连续的周一到周日，时间是分段的，会影响查询效率，后期考虑是否合成一个
//            conbainTime(timeMap);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		log.info("查询任务的时间段==map==" + timeMap.toString());

		return timeMap;

	}

	public static Map<Long, Long> getWeekDate(Date sd, Date ed, int dayOfWeek, Task task) throws ParseException {

		String analyStart = task.getAnalyStartTime();
		String analyEnd = task.getAnalyEndTime();
		Date createTime = task.getCreateTime();

		if (task.getBeginDate() != null && task.getEndDate() != null) {
			/*******************************************
			 * 指定时间段任务 start
			 **************************************************************/
//            starDate = task.getBeginDate();
//            endDate = task.getEndDate();
		}

		java.text.SimpleDateFormat dayf = new java.text.SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		//2018/9/26 lxh 23:59:00,要改成 23：59：59这种样式
		analyEnd = analyEnd.substring(0, analyEnd.lastIndexOf(":") + 1) + "59";
		Map<Long, Long> map = new TreeMap<>();
		Calendar c = Calendar.getInstance();
		c.setTime(sd);
		int day = c.get(Calendar.DAY_OF_WEEK) - 1; // 找出开始日期是星期几，需求不一样可修改
		List<Date> dateList = new ArrayList<Date>();
		if (day != dayOfWeek) {
			int dif = dayOfWeek < day ? (dayOfWeek - day + 7) : (dayOfWeek - day); // 算出跟参数相差的星期的天数
			c.add(Calendar.DATE, dif);
		}
		long end1;
		long start1;
		while (!c.getTime().after(ed)) {
			dateList.add(c.getTime());

			Date time1 = DateUtil.StringToDate(dayf.format(c.getTime()) + " " + analyStart);
			if (time1.getTime() < createTime.getTime()) {
				time1 = createTime;
			}
			Date time2 = DateUtil.StringToDate(dayf.format(c.getTime()) + " " + analyEnd);

			log.info("查询任务的时间段" + DateUtil.DateToString(time1) + "~" + DateUtil.DateToString(time2));

			start1 = time1.getTime() / 1000;
			end1 = time2.getTime() / 1000;

			map.put(start1, end1);

			c.add(Calendar.DATE, 7);
		}
		return map;
	}

	@Override
	public Map<String, Object> getTaskCount() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Task> list = taskDAO.getTaskCount();
		if (list != null && list.size() > 0) {
			List<Task> runningList = list.stream().filter(a -> a.getState() == 1).collect(Collectors.toList());
			List<Task> doneList = list.stream().filter(a -> a.getState() == 5).collect(Collectors.toList());
			List<Task> failList = list.stream().filter(a -> a.getState() == 6).collect(Collectors.toList());
			if (runningList != null && runningList.size() > 0) {
				map.put("runCount", runningList.size());
			} else {
				map.put("runCount", 0);
			}
			if (doneList != null && doneList.size() > 0) {
				map.put("doneCount", doneList.size());
			} else {
				map.put("doneCount", 0);
			}
			if (failList != null && failList.size() > 0) {
				map.put("failCount", failList.size());
			} else {
				map.put("failCount", 0);
			}
			map.put("allCount", list.size());
		} else {
			map.put("runCount", 0);
			map.put("doneCount", 0);
			map.put("failCount", 0);
			map.put("allCount", 0);
		}
		return map;
	}

}