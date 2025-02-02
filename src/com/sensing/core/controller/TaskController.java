package com.sensing.core.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.TextUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.bean.RunningTaskCountReq;
import com.sensing.core.bean.StopTaskReq;
import com.sensing.core.bean.SysUser;
import com.sensing.core.bean.Task;
import com.sensing.core.bean.TaskChannel;
import com.sensing.core.bean.TaskRequest;
import com.sensing.core.bean.TaskResp;
import com.sensing.core.dao.ITaskChannelDAO;
import com.sensing.core.dao.ITaskDAO;
import com.sensing.core.service.ITaskService;
import com.sensing.core.service.impl.CaptureThriftServiceImpl;
import com.sensing.core.thrift.cap.bean.CapReturn;
import com.sensing.core.utils.BaseController;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.ValidationUtils;
import com.sensing.core.utils.results.ResultUtils;
import com.sensing.core.utils.task.HistoryTimerTask;
import com.sensing.core.utils.task.JobsAndTaskTimer;
import com.sensing.core.utils.task.TaskTimerTask;

@Controller
@RequestMapping("/task")
public class TaskController extends BaseController {

	private static final Log log = LogFactory.getLog(TaskController.class);

	@Resource
	public ITaskService taskService;
	@Resource
	TaskTimerTask taskTimerTask;
	@Resource
	JobsAndTaskTimer jobsAndTaskTimer;
	@Resource
	HistoryTimerTask historyTimerTask;
	@Resource
	ITaskChannelDAO taskChannelDAO;
	@Resource
	public ITaskDAO taskDAO;
	@Resource
	public CaptureThriftServiceImpl captureThriftServiceImpl;
	@Resource
	public UtilsController utilsController;

	@ResponseBody
	@RequestMapping("/query")
	public ResponseBean query(HttpServletRequest request, @RequestBody JSONObject p) {

		ResponseBean result = new ResponseBean();
		String uuid = request.getHeader("uuid");
		Pager pager = JSONObject.toJavaObject(p, Pager.class);
		if (pager != null && pager.getF() != null && StringUtils.isNotEmpty(pager.getF().get("name"))) {
			if (ValidationUtils.isSpecialChar(pager.getF().get("name"))) {
				return ResultUtils.error(100, "搜索框内不能含有特殊字符");
			}
		}
		pager.getF().put("createUser", uuid);
		/***** 通过名字查询的接口速度较慢，如果没有 *****/
		if (!TextUtils.isEmpty(pager.getF().get("name"))) {
			pager = taskService.queryListByName(pager);
		} else {
			pager = taskService.query(pager);
		}
		result.getMap().put("pager", pager);
		result.setError(0);
		result.setMessage("successful");
		return result;
	}

	@ResponseBody
	@RequestMapping("/openchannelbychanneluuid")
	public ResponseBean openChannelByChannelUuid(@RequestBody JSONObject p) {
		String channelUuid = p.getString("channelUuid");
		if (StringUtils.isEmptyOrNull(channelUuid)) {
			return ResultUtils.REQUIRED_EMPTY_ERROR();
		}
		CapReturn capReturn = null;
		try {
			capReturn = captureThriftServiceImpl.OpenCloseChannels(Arrays.asList(channelUuid), 1, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (capReturn != null) {
			log.info("根据channeluuid打开对应的通道===res-" + capReturn.res + "--msg--" + capReturn.msg);
		} else {
			log.info("根据channeluuid打开对应的通道发生异常。");
		}
		return ResultUtils.success();
	}

	@ResponseBody
	@RequestMapping("/querylistbyname")
	public ResponseBean queryListByName(HttpServletRequest request, @RequestBody JSONObject p) {
		String uuid = request.getHeader("uuid");
		SysUser su = (SysUser) request.getSession().getAttribute(uuid);
		Pager pager = JSONObject.toJavaObject(p, Pager.class);
		ResponseBean result = new ResponseBean();
		try {
			if (TextUtils.isEmpty(pager.getF().get("name"))) {
				result.setError(101);
				result.setMessage("fail");
				return result;
			}
			pager.getF().put("createUser", su.getUuid());
			pager = taskService.queryListByName(pager);
			result.getMap().put("pager", pager);
			result.setError(0);
			result.setMessage("successful");
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			result.setError(100);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/update")
	public ResponseBean update(@RequestBody JSONObject m) {
		TaskRequest model = JSONObject.toJavaObject(m, TaskRequest.class);
		ResponseBean result = new ResponseBean();
		if (model == null || model.getTask() == null || TextUtils.isEmpty(model.getTask().getUuid())) {
			result.setError(100);
			result.setMessage("business error");
			return result;
		}
//        if (StringUtils.isEmptyOrNull(model.getTask().getRunWeek())) {
//            return ResultUtils.error(100, "运行周期不能为空");
//        }
		if (StringUtils.isNotEmpty(model.getTask().getName())) {
			List<Task> taskByName = taskService.getTaskByName(model.getTask().getName(), model.getTask().getType());
			List<Task> list = taskByName.stream().filter(t -> !t.getUuid().equals(model.getTask().getUuid()))
					.collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(list)) {
				return ResultUtils.error(100, "已存在名称为" + model.getTask().getName());
			}
		}
		// 不删除的情况下，
//		if (model.getTask().getIsDeleted() != null && model.getTask().getIsDeleted().intValue() == 1) {
//		} else {
//			// 判断添加通道时当前通道使用情况
//			ResponseBean bean = utilsController.resourceStatistics(m);
//			Integer remainingChannel = (Integer) bean.getMap().get("remainingChannel");
//			@SuppressWarnings("unchecked")
//			List<String> runningChannelList = (List<String>) bean.getMap().get("runningChannelList");
//			String[] channelUuids = model.getTaskChannel().getChannelUuid().split(",");
//			List<String> taskChanList = Arrays.asList(channelUuids);
//			List<String> tcList = new ArrayList<String>(taskChanList);
//			// 求差集 差集为实时结构化要保存的通道和正在运行的通道的不重合的结果
//			if (runningChannelList != null && runningChannelList.size() > 0) {
//				tcList.removeAll(runningChannelList);
//			}
//			if (remainingChannel == 0 && (tcList != null && tcList.size() > 0)) {
//				return ResultUtils.error(-1, "资源已被全部占用,若需添加,请暂停或删除部分任务");
//			}
//			if (tcList != null && tcList.size() > remainingChannel) {
//				return ResultUtils.error(-1, "您目前最多添加" + remainingChannel + "路通道信息,请重新添加");
//			}
//		}

		TaskRequest request = taskService.updateTask(model);
		if (request == null) {
			return ResultUtils.UNKONW_ERROR();
		}
		if (model.getTask().getType() != null && request.getTask().getIsDeleted() == 0) {
			Task aftertask = taskDAO.getTask(model.getTask().getUuid());
			if (model.getTask().getType() == 1 && request.getTask().getState() != Constants.TASK_STAT_STOP ) {
				// 任务更新之后，执行判断修改任务的状态
				jobsAndTaskTimer.startJobs();
				// 实时结构化任务: 2018/8/17 新建任务要考虑 如果是当前时间是在要执行任务的时间点内，则要立即通知抓拍任务
//				int newState = 0;
//				newState = taskTimerTask.getTaskState(aftertask);
//				if (newState != model.getTask().getState() && newState == Constants.TASK_STAT_RUNNING) {
//					  jobsAndTaskTimer.startJobs();
//                    model.getTask().setNewState(Constants.TASK_STAT_RUNNING);
//                    List<TaskChannelResp> taskChannel = taskChannelDAO.getTaskChannelByTaskIds(Arrays.asList(model.getTask().getUuid()));
//                    taskTimerTask.openTask(Arrays.asList(model.getTask()), taskChannel);
//				}
			} else if (model.getTask().getType() == 2) {
				int newState = 0;
				newState = historyTimerTask.getHistoryState(aftertask);
				if (newState != model.getTask().getState() && newState == Constants.TASK_STAT_RUNNING) {
					model.getTask().setNewState(Constants.TASK_STAT_RUNNING);
					historyTimerTask.callHistoryTask(Arrays.asList(model.getTask()));
				}
			}

			return ResultUtils.success();
		}

		return result;
	}

	@ResponseBody
	@RequestMapping("/updatestate")
	public ResponseBean updateState(@RequestBody JSONObject m) {
		Task model = JSONObject.toJavaObject(m, Task.class);
		ResponseBean result = new ResponseBean();
		if (model != null && model.getUuid() != null && !model.getUuid().equals("")) {
			model.setModifyTime(new Date());
			model = taskService.updateState(model);
			result.getMap().put("model", model);
			result.setError(0);
			result.setMessage("successful");
		} else {
			result.setError(100);
			result.setMessage("business error");
		}
		return result;
	}

	/**
	 * 实时结构化任务保存
	 */
	@ResponseBody
	@RequestMapping("/save")
	public ResponseBean save(HttpServletRequest request, @RequestBody JSONObject m) {

		TaskRequest model = JSONObject.toJavaObject(m, TaskRequest.class);
		if (!avaliableParams(model, 0)) {
			return ResultUtils.REQUIRED_EMPTY_ERROR();
		}
		// 入参构建
		model.getTask().setCreateTime(new Date());
		model.getTask().setCreateUser(getUser().getUuid());
		model.getTaskChannel().setCreateUser(getUser().getUuid());
		model.getTask().setState(Constants.TASK_STAT_WAITSTART);
		// 重名
		List<Task> taskByName = taskService.getTaskByName(model.getTask().getName(), model.getTask().getType());
		if (!CollectionUtils.isEmpty(taskByName)) {
			return ResultUtils.error(-1, "已存在名称为\"" + model.getTask().getName() + "\"的任务");
		}
//		//判断添加通道时当前通道使用情况
//		ResponseBean bean = utilsController.resourceStatistics(m);
//		Integer remainingChannel = (Integer) bean.getMap().get("remainingChannel");
//		@SuppressWarnings("unchecked")
//		List<String> runningChannelList = (List<String>) bean.getMap().get("runningChannelList");
//		String[] channelUuids = model.getTaskChannel().getChannelUuid().split(",");
//		List<String> taskChanList = Arrays.asList(channelUuids);
//		List<String> tcList = new ArrayList<String>(taskChanList);
//		// 求差集 差集为实时结构化要保存的通道和正在运行的通道的不重合的结果
//		if (runningChannelList != null && runningChannelList.size() > 0) {
//			tcList.removeAll(runningChannelList);
//		}
//		if (remainingChannel == 0 && (tcList != null && tcList.size() > 0)) {
//			return ResultUtils.error(-1, "资源已被全部占用,若需添加,请暂停或删除部分任务");
//		}
//		if (tcList != null && tcList.size() > remainingChannel) {
//			return ResultUtils.error(-1, "您目前最多添加" + remainingChannel + "路通道信息,请重新添加");
//		}
		// 保存
		TaskRequest taskResp = taskService.saveNewTask(model);

//		List<TaskChannelResp> taskChannel = taskChannelDAO
//				.getTaskChannelByTaskIds(Arrays.asList(taskResp.getTask().getUuid()));

		// 实时结构化任务， 2018/8/17 新建任务要考虑 如果是当前时间是在要执行任务的时间点内，则要立即通知抓拍任务
		if (model.getTask().getType() == 1) {
			int newState = 0;
			newState = taskTimerTask.getTaskState(taskResp.getTask());
			if (newState != model.getTask().getState() && newState == Constants.TASK_STAT_RUNNING) {
				jobsAndTaskTimer.startJobs();
//                model.getTask().setNewState(Constants.TASK_STAT_RUNNING);
//                taskTimerTask.openTask(Arrays.asList(model.getTask()), taskChannel);
			}
		}
		// 历史结构化任务
		if (model.getTask().getType() == 2) {
			int newState = 0;
			newState = historyTimerTask.getHistoryState(taskResp.getTask());
			if (newState != taskResp.getTask().getState() && newState == Constants.TASK_STAT_RUNNING) {
				//2018/12/11 历史视频
				model.getTask().setNewState(newState);
				historyTimerTask.callHistoryTask(Arrays.asList(model.getTask()));
			}
		}

		return taskResp == null ? ResultUtils.UNKONW_ERROR() : ResultUtils.success();
	}

	/**
	 * @param model
	 * @param type  1:修改 2：新增
	 * @return
	 */
	public boolean avaliableParams(TaskRequest model, int type) {

		if (model == null || model.getTask() == null || model.getTaskChannel() == null) {
			return false;
		}

		Task task = model.getTask();
		if (TextUtils.isEmpty(task.getName()) || task.getType() == null || TextUtils.isEmpty(task.getAnalyType())) {
			return false;
		}

		if (model.getTask().getType() == 1 && TextUtils.isEmpty(task.getRunWeek())) {
			return false;
		}

		TaskChannel taskC = model.getTaskChannel();
		if (TextUtils.isEmpty(taskC.getChannelUuid())) {
			return false;
		}

		return true;
	}

	/**
	 * 任务查看详情
	 *
	 * @param m
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/info")
	public ResponseBean info(@RequestBody JSONObject m) {

		Task model = JSONObject.toJavaObject(m, Task.class);
		ResponseBean result = new ResponseBean();
		if (TextUtils.isEmpty(model.getUuid())) {
			result.setError(2);
			result.setMessage("参数不全");
			return result;
		}
		TaskResp info = taskService.info(model.getUuid());
		if (info == null) {
			result.setError(-1);
			result.setMessage("fail");
		}
		result.getMap().put("model", info);
		result.setError(0);
		result.setMessage("successful");
		return result;
	}

	/**
	 * 获取正在运行的任务个数
	 *
	 * @param req
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getrunningtaskCount")
	public ResponseBean getRunningTask(@RequestBody RunningTaskCountReq req) {
		if (req == null) {
			req = new RunningTaskCountReq();
		}
		req.setUserUuid(getUser().getUuid());
//      int count = taskService.getrunningtaskCount(req);
		int count = 0;
		return ResultUtils.success("runnningCount", count);
	}

	/**
	 * 获取不同任务状态的数量
	 * 
	 * @param req
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getTaskCount")
	public ResponseBean getTaskCount(@RequestBody JSONObject req) {
		Map<String, Object> map = taskService.getTaskCount();
		return ResultUtils.success(map);
	}

	/**
	 * 停止任务
	 *
	 * @param req
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/stoptaskbyuuid")
	public ResponseBean stoptaskByUuId(@RequestBody StopTaskReq req) {
		if (req == null || StringUtils.isEmptyOrNull(req.getTaskUuid()) || req.getState() == null) {
			return ResultUtils.REQUIRED_EMPTY_ERROR();
		}
		req.setUserUuid(getUser().getUuid());
		return taskService.stoptaskByUuId(req);
	}

	/**
	 * 开启任务
	 *
	 * @param req
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/starttaskbyuuid")
	public ResponseBean startTaskByUuId(@RequestBody StopTaskReq req) {
		if (req == null || StringUtils.isEmptyOrNull(req.getTaskUuid()) || req.getState() == null) {
			return ResultUtils.REQUIRED_EMPTY_ERROR();
		}
		req.setUserUuid(getUser().getUuid());

//		// 判断添加通道时当前通道使用情况
//		List<TaskChannel> list = taskChannelDAO.getTaskChannelByTaskId(req.getTaskUuid());
//		List<String> tcList = list.stream().map(a -> a.getChannelUuid()).collect(Collectors.toList());
//		JSONObject jo = new JSONObject();
//		ResponseBean bean = utilsController.resourceStatistics(jo);
//		Integer remainingChannel = (Integer) bean.getMap().get("remainingChannel");
//		@SuppressWarnings("unchecked")
//		List<String> runningChannelList = (List<String>) bean.getMap().get("runningChannelList");
//		// 求差集 差集为实时结构化要保存的通道和正在运行的通道的不重合的结果
//		if (runningChannelList != null && runningChannelList.size() > 0) {
//			tcList.removeAll(runningChannelList);
//		}
//		if (remainingChannel == 0 && (tcList != null && tcList.size() > 0)) {
//			return ResultUtils.error(-1, "资源已被全部占用,若需添加,请暂停或删除部分任务");
//		}
//		if (tcList != null && tcList.size() > remainingChannel) {
//			return ResultUtils.error(-1, "您目前最多添加" + remainingChannel + "路通道信息,请重新添加");
//		}

		return taskService.startTaskByUuId(req);
	}

	/**
	 * 根据id查询任务执行时间（任务的查看结果，从mongo里取数据）
	 *
	 * @param req
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getanalytimebyuuid")
	public Map getAnalyTimeByUuid(@RequestBody StopTaskReq req) {
		return taskService.getAnalyTimeByUuid(req.getTaskUuid());
	}

}
