package com.sensing.core.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.alarm.DataInitService;
import com.sensing.core.bean.Channel;
import com.sensing.core.bean.Jobs;
import com.sensing.core.bean.JobsChannelTemp;
import com.sensing.core.bean.job.req.JobListCountReq;
import com.sensing.core.bean.job.req.UpdateJobReq;
import com.sensing.core.bean.job.req.UpdateOperateReq;
import com.sensing.core.dao.IJobsChannelDAO;
import com.sensing.core.dao.IJobsDAO;
import com.sensing.core.service.IJobsService;
import com.sensing.core.utils.BaseController;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.ValidationUtils;
import com.sensing.core.utils.results.ResultUtils;
import com.sensing.core.utils.task.JobsAndTaskTimer;
import com.sensing.core.utils.task.JobsTimerTask;

@Controller
@RequestMapping("/jobs")
public class JobsController extends BaseController {

	private static final Log log = LogFactory.getLog(JobsController.class);

	@Resource
	public IJobsService jobsService;
	/***** 通知alam数据更新，只能放在controller层，service是加了事务的 *****/
	@Resource
	public DataInitService dataInitService;
	@Resource
	public JobsTimerTask jobsTimerTask;
	@Resource
	public JobsAndTaskTimer jobsAndTaskTimer;
	@Resource
	public IJobsDAO jobsDAO;
	@Resource
	public IJobsChannelDAO jobsChannelDAO;
	@Resource
	public UtilsController utilsController;

	/***
	 * 布控管理-布控列表
	 * 
	 * @param p { "pageRows": 10, "pageNo": 1, "pageFlag": "pageFlag",
	 *          "f":{"name":"","state":"","level":"1","querytype":"0"} } type 0：所有列表
	 *          1:自己的申请列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/joblist")
	public ResponseBean jobList(@RequestBody JSONObject p) {
		Pager pager = JSONObject.toJavaObject(p, Pager.class);
		if (pager != null && pager.getF() != null && StringUtils.isNotEmpty(pager.getF().get("name"))) {
			if (ValidationUtils.isSpecialChar(pager.getF().get("name"))) {
				return ResultUtils.error(100, "搜索框内不能含有特殊字符");
			}
		}
		ResponseBean result = new ResponseBean();
		pager.getF().put("uuid", getUser().getUuid());
		pager = jobsService.jobList(pager);
		result.getMap().put("pager", pager);
		result.setError(0);
		result.setMessage("successful");

		return result;
	}

	/**
	 * 查询布控列表简单属性
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/joblistcommon")
	public ResponseBean jobListCommon() {
		return jobsService.jobListCommon();
	}

	/**
	 * 布控：布控审批列表----不同布控状态的个数 申请审批列表红色未读数量
	 *
	 * @param req
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/joblistcount")
	public ResponseBean jobListCount(@RequestBody JobListCountReq req) {
		req.setUserUuid(getUser().getUuid());
		ResponseBean result = jobsService.jobListCount(req);
		return result;
	}

	/**
	 * 布控管理-申请列表
	 *
	 * @param pager { "pageRows":10, "pageNo":1, "pageFlag":"pageFlag", "f":{
	 *              "name":"", "level":"", "startTime":"", "endTime":"",
	 *              "jobsType":"", "ratifyResult":"", "querytype":0 querytype
	 *              0：有审批权限看到的是所有列表 1：当前用户自己的申请列表 } }
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ratifylist")
	public ResponseBean ratifyList(@RequestBody Pager pager) {
		if (pager != null && pager.getF() != null && StringUtils.isNotEmpty(pager.getF().get("name"))) {
			if (ValidationUtils.isSpecialChar(pager.getF().get("name"))) {
				return ResultUtils.error(100, "搜索框内不能含有特殊字符");
			}
		}
		ResponseBean result = new ResponseBean();
		pager.getF().put("uuid", getUser().getUuid());
		Pager pager1 = jobsService.ratifyList(pager);
		result.getMap().put("pager", pager1);
		result.setError(0);
		result.setMessage("successful");

		return result;
	}

	/**
	 * 编辑页面的修改
	 *
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unlikely-arg-type")
	@ResponseBody
	@RequestMapping("/updatecommon")
	public ResponseBean updateCommon(@RequestBody Jobs model) {
		String tips = checkValidJobs(model, true);
		if (StringUtils.isNotEmpty(tips)) {
			return new ResponseBean(-1, tips);
		}

		model.setModifyUser(getUser().getUuid());

//		// 判断添加通道时当前通道使用情况
//		JSONObject jo = new JSONObject();
//		ResponseBean bean = utilsController.resourceStatistics(jo);
//		Integer remainingChannel = (Integer) bean.getMap().get("remainingChannel");
//		@SuppressWarnings("unchecked")
//		List<Channel> runningChannelList = (List<Channel>) bean.getMap().get("runningChannelList");
//		List<String> tcbak = new ArrayList<String>();
//		List<String> tcList = model.getChannelUuIds();
//		tcbak.addAll(tcList);
//		// 求差集 差集为实时结构化要保存的通道和正在运行的通道的不重合的结果
//		if (runningChannelList != null && runningChannelList.size() > 0) {
//			tcList.removeAll(runningChannelList);
//		}
//		if (remainingChannel == 0 && (tcList != null && tcList.size() > 0)) {
//			return ResultUtils.error(-1, "资源已被全部占用,若需添加,请暂停或删除部分布控任务");
//		}
//		if (tcList != null && tcList.size() > remainingChannel) {
//			return ResultUtils.error(-1, "您目前最多添加" + remainingChannel + "路通道信息,请重新添加");
//		}
//		model.setChannelUuIds(tcbak);
		ResponseBean result = jobsService.updateCommon(model);

		// 查询状态
		if (result != null && result.getError() == 0) {
			dataInitService.init();
		}

		return result;
	}

	/**
	 * 新建布控
	 */
	@ResponseBody
	@RequestMapping("/save")
	public ResponseBean save(@RequestBody Jobs jobs) {
		String tips = checkValidJobs(jobs);
		if (StringUtils.isNotEmpty(tips)) {
			return new ResponseBean(-1, tips);
		}

		jobs.setCreateUser(getUser().getUuid());
//		// 判断添加通道时当前通道使用情况
//		JSONObject jo = new JSONObject();
//		ResponseBean bean = utilsController.resourceStatistics(jo);
//		Integer remainingChannel = (Integer) bean.getMap().get("remainingChannel");
//		@SuppressWarnings("unchecked")
//		List<Channel> runningChannelList = (List<Channel>) bean.getMap().get("runningChannelList");
//		List<String> tcList = jobs.getChannelUuIds();
//		List<String> tcbak = new ArrayList<String>();
//		tcbak.addAll(tcList);
//		// 求差集 差集为实时结构化要保存的通道和正在运行的通道的不重合的结果
//		if (runningChannelList != null && runningChannelList.size() > 0) {
//			tcList.removeAll(runningChannelList);
//		}
//		if (remainingChannel == 0 && (tcList != null && tcList.size() > 0)) {
//			return ResultUtils.error(-1, "资源已被全部占用,若需添加,请暂停或删除部分布控任务");
//		}
//		if (tcList != null && tcList.size() > remainingChannel) {
//			return ResultUtils.error(-1, "您目前最多添加" + remainingChannel + "路通道信息,请重新添加");
//		}
//		jobs.setChannelUuIds(tcbak);
		ResponseBean responseBean = jobsService.saveNewJobs(jobs);

		return responseBean;
	}

	private String checkValidJobs(@RequestBody Jobs jobs, boolean... isUpdate) {
		if (isUpdate != null && isUpdate.length == 1 && StringUtils.isEmptyOrNull(jobs.getUuid())) {
			return "布控的uuid为空";
		}
		if (jobs == null || StringUtils.isEmptyOrNull(jobs.getName())) {
			return "布控名称为空";
		}
		if (jobs.getName().length() > 50) {
			return "布控名称限50个字符";
		}
		if (StringUtils.isNotEmpty(jobs.getMemo()) && jobs.getMemo().length() > 300) {
			return "布控描述限300个字符";
		}
		if (jobs.getBeginDate() == null && StringUtils.isEmptyOrNull(jobs.getBeginTime())) {
			return "布控时段异常";
		}

		try {
			java.text.SimpleDateFormat timef = new java.text.SimpleDateFormat("HH:mm:ss");// 设置日期格式
			if (timef.parse(jobs.getBeginTime()).getTime() >= timef.parse(jobs.getEndTime()).getTime()) {
				return "布控时段的结束时间要大于开始时间";
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (StringUtils.isEmptyOrNull(jobs.getRunWeek())) {
			return "布控日期异常";
		}
		if (CollectionUtils.isEmpty(jobs.getChannelUuIds())) {
			return "布控关联通道为空";
		}
		if (CollectionUtils.isEmpty(jobs.getTemplatedbIds()) && StringUtils.isEmptyOrNull(jobs.getPlateNo())) {
			return "布控模板为空";
		}
		if (jobs.getLevel() == null) {
			return "布控等级为空";
		}

		return null;
	}

	/**
	 * 布控：列表页的操作，暂停开启布控任务，取消订阅，删除，申请撤控
	 *
	 * @param
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateoperate")
	public ResponseBean updateOperate(@RequestBody UpdateOperateReq req) {
		if (req == null) {
			return ResultUtils.REQUIRED_EMPTY_ERROR();
		}
		req.setUserUuid(getUser().getUuid());
		if (req.getCancelJobs() != null && req.getCancelJobs().intValue() == 1) {
			if (StringUtils.isEmptyOrNull(req.getCancelJobReason())) {
				return ResultUtils.error(-1, "撤控理由不能为空");
			} else if (req.getCancelJobReason().length() > 300) {
				return ResultUtils.error(-1, "撤控理由限300个字符");
			}
		}
		ResponseBean responseBean = jobsService.updateOperate(req);
		if (responseBean != null && responseBean.getError() == 0) {
			dataInitService.init();
		}

		return responseBean;
	}

	/**
	 * 审批操作
	 *
	 * @param
	 * @return
	 */
	@SuppressWarnings("unlikely-arg-type")
	@ResponseBody
	@RequestMapping("/ratifyjob")
	public ResponseBean ratifyJob(@RequestBody UpdateJobReq req) {
		if (req == null || req.getJobUuid() == null || req.getRatifyResult() == null || req.getRatifyResult() == 0) {
			return ResultUtils.REQUIRED_EMPTY_ERROR();
		}
		if (StringUtils.isNotEmpty(req.getRatifyMemo()) && req.getRatifyMemo().length() > 300) {
			return ResultUtils.error(100, "审批说明限300个字符");
		}
		req.setRatifyUser(getUser().getUuid());
		Jobs jobs = jobsDAO.getJobs(req.getJobUuid());
//		// 布控审批通过时判断添加通道时当前通道使用情况
//		if (jobs.getRatifyType() == 0 && req.getRatifyResult() == 1) {
//
//			// 判断添加通道时当前通道使用情况
//			JSONObject jo = new JSONObject();
//			ResponseBean bean = utilsController.resourceStatistics(jo);
//			Integer remainingChannel = (Integer) bean.getMap().get("remainingChannel");
//			@SuppressWarnings("unchecked")
//			List<Channel> runningChannelList = (List<Channel>) bean.getMap().get("runningChannelList");
//			List<JobsChannelTemp> jobsChannelTemp = jobsChannelDAO
//					.getJobsChannelByJobUuid(Arrays.asList(req.getJobUuid()));
//			List<String> tcList = jobsChannelTemp.stream().map(a -> a.getChannelUuid()).collect(Collectors.toList());
//			// 求差集 差集为实时结构化要保存的通道和正在运行的通道的不重合的结果
//			if (runningChannelList != null && runningChannelList.size() > 0) {
//				tcList.removeAll(runningChannelList);
//			}
//			if (remainingChannel == 0 && (tcList != null && tcList.size() > 0)) {
//				return ResultUtils.error(-1, "资源已被全部占用,若需添加,请暂停或删除部分布控任务");
//			}
//			if (tcList != null && tcList.size() > remainingChannel) {
//				return ResultUtils.error(-1, "您目前最多添加" + remainingChannel + "路通道信息,请重新添加");
//			}
//		}
		ResponseBean responseBean = jobsService.ratifyJob(req);
		if (responseBean != null && responseBean.getError() == 0) {
			dataInitService.init();
		}
		if (req.getRatifyResult() == 1) {
			jobsAndTaskTimer.startJobs();
		}
		return responseBean;
	}

	/**
	 * 详情
	 *
	 * @param
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/info")
	public ResponseBean info(@RequestBody UpdateJobReq req) {
		if (req == null || req.getJobUuid() == null) {
			return ResultUtils.REQUIRED_EMPTY_ERROR();
		}
		req.setRatifyUser(getUser().getUuid());
		ResponseBean responseBean = jobsService.info(req);
		return responseBean;
	}

	/**
	 * 审批和申请列表红点的数量
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getjobsunreadmsgcount")
	public ResponseBean getjobsunreadmsgcount() {
		ResponseBean responseBean = jobsService.getJobsUnReadMsgCount(getUser().getUuid());
		return responseBean;
	}

	@ResponseBody
	@RequestMapping("/getjobschannelbyuuid")
	public ResponseBean getJobsChannelByUuid(@RequestBody UpdateJobReq req) {
		return jobsService.getJobsChannelByUuid(req);
	}

}
