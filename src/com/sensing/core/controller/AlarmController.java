package com.sensing.core.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.bean.Alarm;
import com.sensing.core.bean.alarm.req.AlarmReq;
import com.sensing.core.bean.alarm.resp.AlarmDetailResp;
import com.sensing.core.service.IAlarmService;
import com.sensing.core.service.ISysSubscribeService;
import com.sensing.core.utils.BaseController;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.ValidationUtils;
import com.sensing.core.utils.results.ResultEnum;
import com.sensing.core.utils.results.ResultUtils;

/**
 * 报警查询与检索
 * 
 * @author wangdandan
 *
 */
@Controller
@RequestMapping("/alarm")
public class AlarmController extends BaseController {

	private static final Log log = LogFactory.getLog(AlarmController.class);

	@Resource
	public IAlarmService alarmService;
	@Resource
	public ISysSubscribeService sysSubscribeService;

	/**
	 * 统计告警数量 单日告警的数量和未审核的告警数量
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2018年12月10日 下午6:28:25
	 */
	@ResponseBody
	@RequestMapping("/queryAlarmStatistics")
	public ResponseBean queryAlarmStatistics(@RequestBody JSONObject p) {
		log.info("开始查询报警信息，调用 alarm/queryAlarmStatistics接口，传递参数为: " + p);
		ResponseBean result = new ResponseBean();
		try {
			Map<String, Object> statisticsMap = alarmService.queryAlarmStatistics();

			if (statisticsMap != null && statisticsMap.size() > 0) {
				result = ResultUtils.success("result", statisticsMap);
			} else {
				result = ResultUtils.REQUIRED_EMPTY_ERROR();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			result = ResultUtils.error(100, e.getMessage());
		}
		return result;
	}

	/**
	 * 首页的最新四条告警信息查询
	 * 
	 * @return
	 * @author mingxingyu
	 * @date 2018年12月7日 下午4:31:07
	 */
	@ResponseBody
	@RequestMapping("/queryAlarmHomePage")
	public ResponseBean queryAlarmHomePage(@RequestBody JSONObject p) {
		log.info("开始查询报警信息，调用 alarm/queryAlarmHomePage接口，传递参数为: " + p);
		ResponseBean result = new ResponseBean();
		try {

			Integer pageRows = p.getInteger("pageRows");
			String userUuid = getUuid();

			if (pageRows == null || userUuid == null) {
				result = ResultUtils.REQUIRED_EMPTY_ERROR();
			} else {
				Pager countPager = new Pager();
				countPager.getF().put("uid", userUuid);
				int queryCount = sysSubscribeService.queryCount(countPager);
				if (queryCount <= 0) {
					result = ResultUtils.error(-5, "您还未订阅报警，暂无信息");
				} else {
					List<Map<String, Object>> alarmList = alarmService.queryAlarmHomePage(userUuid, pageRows);
					result = ResultUtils.success("resultList", alarmList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			result = ResultUtils.error(100, e.getMessage());
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/query")
	public ResponseBean query(@RequestBody JSONObject p) {
		log.info("开始查询报警信息，调用 alarm/query 接口，传递参数为: " + p);
		long l1 = new Date().getTime();
		AlarmReq pager = JSONObject.toJavaObject(p, AlarmReq.class);
		ResponseBean result = new ResponseBean();
		try {
			pager = alarmService.queryPage(pager);
			long l2 = new Date().getTime();
			log.info("报警检索耗时----------" + (l2 - l1) + "ms");
			result = ResultUtils.success("pager", pager);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			result = ResultUtils.error(100, e.getMessage());
		}
		return result;
	}

	/**
	 * 通过报警序列号模糊检索结果
	 * 
	 * @param json
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAlarmId")
	public ResponseBean queryAlarmId(@RequestBody JSONObject p) {
		log.info("开始查询报警报警序列号，调用 alarm/queryAlarmId 接口，传递参数为: " + p);
		AlarmReq pager = JSONObject.toJavaObject(p, AlarmReq.class);
		ResponseBean result = new ResponseBean();
		try {
			pager = alarmService.queryByAlarmId(pager);
			result = ResultUtils.success("pager", pager);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			result = ResultUtils.error(100, e.getMessage());
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/detail")
	public ResponseBean detail(@RequestBody JSONObject json) {
		log.info("开始查询报警详情信息，调用 alarm/detail 接口，传递参数为: " + json);
		ResponseBean result = new ResponseBean();
		if (json == null || json.isEmpty()) {
			log.error("请求参数非法");
			return ResultUtils.error(result, 100, "请求参数非法");
		}
		String uuid = json.getString("uuid");
		long l1 = new Date().getTime() / 1000;
		try {
			AlarmDetailResp alarmDetail = alarmService.queryByUuid(uuid);
			long l2 = new Date().getTime() / 1000;
			log.info("报警检索耗时----------" + (l2 - l1) + "s");
			result = ResultUtils.success("model", alarmDetail);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			result = ResultUtils.error(100, e.getMessage());
		}
		return result;
	}

	/**
	 * 获取下一个报警详情
	 * 
	 * @param json
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/nextDetail")
	public ResponseBean nextDetail(@RequestBody JSONObject p) {
		log.info("开始查询报警详情信息，调用 alarm/detail 接口，传递参数为: " + p);
		String alarmId = (String) p.get("alarmId");
		AlarmReq pager = JSONObject.toJavaObject(p, AlarmReq.class);
		pager.getF().put("alarmId", alarmId);
		ResponseBean result = new ResponseBean();
		try {
			String uuid = alarmService.nextUuid(pager);
			if (uuid == null) {
				return ResultUtils.error(10, "当前数据已是最后一条");
			} else {
				result = ResultUtils.success("uuid", uuid);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			result = ResultUtils.error(100, e.getMessage());
		}
		return result;
	}

	/**
	 * 查询上一个报警详情
	 * 
	 * @param p
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/prevDetail")
	public ResponseBean prevDetail(@RequestBody JSONObject p) {
		log.info("开始查询报警详情信息，调用 alarm/detail 接口，传递参数为: " + p);
		String alarmId = (String) p.get("alarmId");
		AlarmReq pager = JSONObject.toJavaObject(p, AlarmReq.class);
		pager.getF().put("alarmId", alarmId);
		ResponseBean result = new ResponseBean();
		try {
			String uuid = alarmService.prevUuid(pager);
			if (uuid == null) {
				return ResultUtils.error(10, "当前数据已是第一条");
			} else {
				result = ResultUtils.success("uuid", uuid);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			result = ResultUtils.error(100, e.getMessage());
		}
		return result;
	}

	// verifyFeedback 核查反馈

	@ResponseBody
	@RequestMapping("/update")
	public ResponseBean update(@RequestBody JSONObject m) {
		Alarm model = JSONObject.toJavaObject(m, Alarm.class);
		ResponseBean result = new ResponseBean();
		String validateInfo = validateData(model);
		if (validateInfo != null) {
			return ResultUtils.error(result, ResultEnum.DATA_ERROR.getCode(), validateInfo);
		}
		model = alarmService.updateAlarm(model);
		result = ResultUtils.success("model", model);
		return result;
	}

	private String validateData(Alarm model) {
		if (model == null) {
			return "数据转换失败";
		}
		// 唯一标识校验
		if (!StringUtils.isNotEmpty(model.getUuid())) {
			return "唯一标识不能为空";
		}
		// 核查类型校验
		if (model.getState() == null
				|| !ValidationUtils.checkValueRange(model.getState() - 0, new Integer[] { 10, 20, 30, 40 })) {
			return "核查类型校验失败，不能为空，且数值需在指定范围内";
		}
		// 描述校验，长度不超过300字符
		if (StringUtils.isNotEmpty(model.getStateMemo())
				&& !ValidationUtils.checkStrLengthLess(model.getStateMemo(), 300)) {
			return "核查描述长度限300个字符";
		}
		return null;
	}

	/**
	 * save data
	 */
	@ResponseBody
	@RequestMapping("/save")
	public ResponseBean save(@RequestBody JSONObject m) {
		Alarm model = JSONObject.toJavaObject(m, Alarm.class);
		ResponseBean result = new ResponseBean();
		try {
			model = alarmService.saveNewAlarm(model);
			result.getMap().put("model", model);
			result.setError(0);
			result.setMessage("successful");
		} catch (Exception e) {
			log.error(e);
			result.setError(100);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/delete")
	public ResponseBean delete(@RequestBody String[] idarr) {
		ResponseBean result = new ResponseBean();
		try {
			for (int i = 0; idarr != null && i < idarr.length; i++) {
				alarmService.removeAlarm(idarr[i]);
			}
			result.setError(0);
			result.setMessage("successful");
		} catch (Exception e) {
			log.error(e);
			result.setError(100);
			result.setMessage(e.getMessage());
		}
		return result;
	}
}
