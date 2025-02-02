package com.sensing.core.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sensing.core.utils.Exception.CapServerException;
import com.sensing.core.utils.results.ResultUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.alarm.DataInitService;
import com.sensing.core.bean.Channel;
import com.sensing.core.bean.Channel1;
import com.sensing.core.bean.Channel2;
import com.sensing.core.bean.ChannelQueryResult;
import com.sensing.core.bean.JobsChannel;
import com.sensing.core.bean.Regions;
import com.sensing.core.bean.TaskChannel;
import com.sensing.core.service.IAuthorizationService;
import com.sensing.core.service.IChannelService;
import com.sensing.core.service.IJobsChannelService;
import com.sensing.core.service.IRegionsService;
import com.sensing.core.service.ISysUserService;
import com.sensing.core.service.ITaskChannelService;
import com.sensing.core.service.ITaskService;
import com.sensing.core.utils.BaseController;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.ExcelHelper;
import com.sensing.core.utils.Exception.ExpUtil;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.ValidationUtils;
import com.sensing.core.utils.props.PropUtils;

/**
 * 通道管理
 *
 * @author admin
 */
@Controller
@RequestMapping("/channel")
@SuppressWarnings("all")
public class ChannelController extends BaseController {

	private static final Log log = LogFactory.getLog(ChannelController.class);

	@Resource
	public IAuthorizationService authorizationService;
	@Resource
	public IChannelService channelService;
	@Resource
	public IRegionsService regionsService;
	@Resource
	public ISysUserService sysUserService;
	@Resource
	public ITaskService taskService;
	@Resource
	public IJobsChannelService jobsChannelService;
	@Resource
	public ITaskChannelService taskChannelService;
	@Resource
	public DataInitService dataInitService;

	@ResponseBody
	@RequestMapping("/query")
	public ResponseBean query(@RequestBody JSONObject p, HttpServletRequest request) {
		Pager pager = JSONObject.toJavaObject(p, Pager.class);
		ResponseBean result = new ResponseBean();
		try {
			if (pager != null && pager.getF() != null && StringUtils.isNotEmpty(pager.getF().get("channelName"))) {
				if (ValidationUtils.isSpecialChar(pager.getF().get("channelName"))) {
					return ResultUtils.error(100, "搜索框内不能含有特殊字符");
				}
			}
			// 如果regionId为－1，表示查询所有通道
			if (pager.getF().get("regionId") != null && pager.getF().get("regionId").equals("-1")) {
				pager.getF().put("regionId", "");
			}
			String token = request.getHeader("token");
			pager = channelService.queryPageNew(pager);
			result.getMap().put("pager", pager);
			result.setError(0);
			result.setMessage("successful");
		} catch (Exception e) {
			log.error(e);
			result.setError(-1);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 查询通道的数量
	 *
	 * @param p
	 * @return
	 * @author mingxingyu
	 * @date 2018年7月27日 上午10:14:01
	 */
	@ResponseBody
	@RequestMapping("/queryChannelCount")
	public ResponseBean queryChannelCount(@RequestBody JSONObject p, HttpServletRequest request) {
		Pager pager = JSONObject.toJavaObject(p, Pager.class);
		ResponseBean result = new ResponseBean();
		try {
			// 如果regionId为－1，表示查询所有通道
			if (pager.getF().get("regionId") != null && pager.getF().get("regionId").equals("-1")) {
				pager.getF().put("regionId", "");
			}
			String token = request.getHeader("token");
			pager.getF().put("token", token);
			int channelCount = channelService.queryChannelCount(pager);
			result.getMap().put("channelCount", channelCount);
			result.setError(0);
			result.setMessage("successful");
		} catch (Exception e) {
			log.error(e);
			result.setError(-1);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 查询全通道信息
	 *
	 * @param json
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryChannels")
	public ResponseBean queryChannels(@RequestBody JSONObject json) {
		ResponseBean result = new ResponseBean();
		List<Channel> channelList = null;
		try {
			channelList = channelService.queryChannelList();
		} catch (Exception e) {
			log.error("查询通道信息异常", e);
			result.setError(-1);
			result.setMessage("查询通道信息异常");
			return result;
		}
		if (channelList != null && channelList.size() > 0) {
			result.setError(0);
			result.setMessage("success");
			result.getMap().put("channelList", channelList);
			return result;
		} else {
			log.info("查询到的数据库通道信息为空");
			result.setError(100);
			result.setMessage("fail");
			return result;
		}

	}

	/**
	 * 查询通道打开和关闭状态的总数
	 * 
	 * @param json
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryChannelStatCount")
	public ResponseBean queryChannelStatCount(@RequestBody JSONObject json) {
		ResponseBean result = new ResponseBean();
		Map<String, Object> channelStatMap = new HashMap<String, Object>();
		try {
			channelStatMap = channelService.queryChannelStatCount(channelStatMap);
			result = ResultUtils.success(channelStatMap);
		} catch (Exception e) {
			log.error("查询通道信息异常", e);
			result = ResultUtils.error(-1, "查询通道信息异常");
		}
		return result;

	}

	/**
	 * @param json
	 * @return ResponseBean
	 * @Description: 根据通道id查询通道信息
	 * @author dongsl
	 * @Date 2017年9月5日上午9:35:28
	 */
	@ResponseBody
	@RequestMapping("/queryById")
	public ResponseBean queryById(@RequestBody JSONObject json) {
		long long1 = System.currentTimeMillis();
		ResponseBean result = new ResponseBean();
		if (json == null || json.isEmpty()) {
			log.error("请求参数非法");
			result.setError(100);
			result.setMessage("请求参数非法");
			return result;
		}
		String channelId = json.getString("channelId");
		Channel channel = null;
		try {
			channel = channelService.getOneChannelByUuid(channelId, Constants.DELETE_NO.toString());
		} catch (Exception e) {
			log.error("查询通道信息异常", e);
			result.setError(-1);
			result.setMessage("查询通道信息异常");
			return result;
		}
		result.setError(0);
		result.getMap().put("model", channel);
		result.setMessage("success");
		return result;
	}

	/**
	 * 修改通道 author dongsl date 2017年8月8日下午1:41:48
	 *
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/update")
	public ResponseBean update(@RequestBody JSONObject json, HttpServletRequest request) {
		String uuid = request.getHeader("uuid");
		ResponseBean result = new ResponseBean();
		if (json == null || json.isEmpty()) {
			log.error("请求参数非法");
			result.setError(100);
			result.setMessage("请求参数非法");
			return result;
		}
		Channel model = JSONObject.toJavaObject(json, Channel.class);
		String valRes = null;
		try {
			valRes = validateParam(model, 2);
		} catch (Exception e) {
			log.error("请求参数非法", e);
			result.setError(-1);
			result.setMessage("请求参数非法");
			return result;
		}
		// 1表示验证添加方法，2表示验证修改方法
		if (StringUtils.isNotEmpty(valRes)) { // 如果验证通过，则进行下一步，否则返回错误信息
			result.setError(2002);
			result.setMessage(valRes);
		} else {
			try {
				Channel channelDb = channelService.getOneChannelByUuid(model.getUuid(), Constants.DELETE_NO.toString());

				// 从rtsp地址增加rtmp流地址
				if ((model.getChannelRtmp() == null || "".equals(model.getChannelRtmp())) && model.getChannelAddr() != null
						&& !"".equals(model.getChannelAddr())) {
					if (model.getChannelAddr().startsWith("rtsp://")) {
//							model.setChannelRtmp(model.getChannelAddr().replaceAll("rtsp://","rtmp://"+PropUtils.getString("streamMedia.ip")+"/live/"));
						model.setChannelRtmp(model.getChannelAddr().replaceAll("rtsp://",
								"rtmp://" + PropUtils.getString("streamMedia.ip") + "/live/"));
					}
					if (model.getChannelAddr().startsWith("gb://")) {
						model.setChannelRtmp(model.getChannelAddr().replaceAll("gb://",
								"rtmp://" + PropUtils.getString("streamMedia.ip") + "/live/"));
					}
				}
				
				// 设置数据的修改记录
				model.setModifyUser(uuid);
				model.setAnalysisType(channelDb.getAnalysisType());
				model = channelService.updateChannel(model);
				// 通知更新告警的缓存
				dataInitService.init();
				result.getMap().put("model", model);
				result.setError(0);
				result.setMessage("successful");
			} catch (Exception e) {
				log.error(StringUtils.getStackTrace(e));
				result = ResultUtils.UNKONW_ERROR();
				if (e instanceof CapServerException) {
					result = ResultUtils.error(-1, "通知抓拍服务异常");
				}
			}
		}
		return result;
	}

	/**
	 * 校验是否能添加通道
	 * 
	 * @param json
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkAddChannel")
	public ResponseBean checkAddChannel(@RequestBody JSONObject json, HttpServletRequest request) {
		ResponseBean result = new ResponseBean();
		String regionId = json.getString("regionId");
		List<Regions> list = null;
		try {
			list = regionsService.queryRegionByParentId(regionId, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(StringUtils.getStackTrace(e));
			return ResultUtils.UNKONW_ERROR();
		}
		if (list != null && list.size() > 0) {
			return ResultUtils.error(100, "该分组下仍有分组，请重新选择分组");
		} else {
			return ResultUtils.success();
		}
	}

	/**
	 * 通道保存 author dongsl date 2017年8月8日上午11:41:39
	 *
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/save")
	public ResponseBean save(@RequestBody JSONObject json, HttpServletRequest request) {
		ResponseBean result = new ResponseBean();
		String uuid = request.getHeader("uuid");
		Long d1 = new Date().getTime();
		if (json == null || json.isEmpty()) {
			result.setError(100);
			result.setMessage("请求参数非法,参数传递为空");
			return result;
		}
		Channel model = JSONObject.toJavaObject(json, Channel.class);
		String valRes = validateParam(model, 1);
		if (StringUtils.isNotEmpty(valRes)) { // 如果验证通过，则进行下一步，否则返回错误信息
			result.setError(100);
			result.setMessage(valRes);
			return result;
		}
		try {
			if (model.getReserved() == null) {
				model.setReserved(1);
			}
			// 从rtsp地址增加rtmp流地址
			if ((model.getChannelRtmp() == null || "".equals(model.getChannelRtmp())) && model.getChannelAddr() != null
					&& !"".equals(model.getChannelAddr())) {
				if (model.getChannelAddr().startsWith("rtsp://")) {
//						model.setChannelRtmp(model.getChannelAddr().replaceAll("rtsp://","rtmp://"+PropUtils.getString("streamMedia.ip")+"/live/"));
					model.setChannelRtmp(model.getChannelAddr().replaceAll("rtsp://",
							"rtmp://" + PropUtils.getString("streamMedia.ip") + "/live/"));
				}
				if (model.getChannelAddr().startsWith("gb://")) {
					model.setChannelRtmp(model.getChannelAddr().replaceAll("gb://",
							"rtmp://" + PropUtils.getString("streamMedia.ip") + "/live/"));
				}
			}
			//  2019/1/18 lxh 视频类型增加多个后，将之前的先注释掉
			// 从rtsp地址增加rtmp流地址
//            if ((model.getChannelRtmp() == null || "".equals(model.getChannelRtmp())) && model.getChannelAddr() != null && !"".equals(model.getChannelAddr())) {
//                if (model.getChannelAddr().startsWith("rtsp://")) {
//                    String[] addrArr = model.getChannelAddr().split("/");
//                    if (addrArr != null && addrArr.length >= 2) {
//                        String ip = addrArr[2];
//                        //rtsp的ip没有554的情况下，需要添加该端口
//                        String addr = model.getChannelAddr();
//                        if (!ip.contains(":") && !ip.contains("554")) {
//                            addr = addr.replaceAll(ip, ip + ":554");
//                        }
//                        addr = addr.replaceAll("rtsp://", "rtmp://" + PropUtils.getString("streamMedia.ip") + "/live/");
//                        model.setChannelRtmp(addr);
//                    }
//                }
//            }

			model.setCreateUser(uuid);
			model = channelService.saveNewChannel(model);
			// 通知更新告警的缓存
			dataInitService.init();
			result.getMap().put("model", model);
			result.setError(0);
			result.setMessage("successful");
		} catch (Exception e) {
			if (e instanceof CapServerException) {
				return ResultUtils.error(100, "通知抓拍保存通道失败");
			} else {
				log.error(ExpUtil.getExceptionDetail(e));
				return ResultUtils.UNKONW_ERROR();
			}
		}

		return result;
	}

	/**
	 * 保存通道信息之前验证 type=11验证save方法，type=2验证update方法 author dongsl date
	 * 2017年8月8日上午11:42:40
	 */
	public String validateParam(Channel channel, int type) {
		try {
			// 更新通道校验
			if (type == 2) {
				if (StringUtils.isNotEmpty(channel.getChannelName())
						&& ValidationUtils.withSpecialChar(channel.getChannelName())) {
					return "通道名称不能含有特殊字符";
				}
				// 通道uuid是否存在
				if (!StringUtils.isNotEmpty(channel.getUuid())) {
					return "通道uuid不能为空";
				}
				Channel oldChannel = channelService.findChannelById(channel.getUuid());
				// 当修改通道时，查看通道名称是否和已存在的其它通道重复，重复则不允许修改。
				if (StringUtils.isNotEmpty(oldChannel.getChannelName())
						&& !oldChannel.getChannelName().equals(channel.getChannelName())) {
					List listt = channelService.queryChannelByChannelNameAndRegionId(channel.getChannelName(),
							channel.getRegionId(), Constants.DELETE_NO);
					if (listt != null && listt.size() > 0) {
						return "该分组已存在名为：" + channel.getChannelName() + "的通道";
					}
				}
				// 当修改通道时，查看通道号和通道地址是否和已存在的其它通道重复，重复则不允许修改。
				if (StringUtils.isNotEmpty(oldChannel.getChannelNo())
						&& !oldChannel.getChannelNo().equals(channel.getChannelNo())) {
					if (!channel.getChannelAddr().contains("rtsp:")) {
						List list = channelService.queryChannelByChannelNoAndAddr(channel.getChannelNo(),
								channel.getChannelAddr());
						if (list != null && list.size() > 0) {
							return "通道号和通道地址已存在";
						}
					}
				}
				// 当修改通道时，查看通道号和通道地址是否和已存在的其它通道重复，重复则不允许修改。
				if (StringUtils.isNotEmpty(oldChannel.getChannelAddr())
						&& !oldChannel.getChannelAddr().equals(channel.getChannelAddr())) {

					if (!channel.getChannelAddr().contains("rtsp:")) {
						List list = channelService.queryChannelByChannelNoAndAddr(channel.getChannelNo(),
								channel.getChannelAddr());
						if (list != null && list.size() > 0) {
							return "通道号和通道地址已存在";
						}
					}
				}
				// 添加通道校验
			} else {
				// 通道号是否重复
				if (channel.getChannelNo() != null && !"".equals(channel.getChannelNo())) {
					List list = channelService.queryChannelByChannelNoAndAddr(channel.getChannelNo(),
							channel.getChannelAddr());
					if (list != null && list.size() > 0) {
						return "通道号和通道地址已存在";
					}
				}
				List listt = channelService.queryChannelByChannelNameAndRegionId(channel.getChannelName(),
						channel.getRegionId(), Constants.DELETE_NO);
				if (listt != null && listt.size() > 0) {
					return "该分组已存在名为：" + channel.getChannelName() + "的通道";
				}
			}
			// 通道名称是否为空
			if (!StringUtils.isNotEmpty(channel.getChannelName())) {
				return "通道名称不能为空";
			}
			if (ValidationUtils.withSpecialChar(channel.getChannelName())) {
				return "通道名称不能含有特殊字符";
			}
			// 区域id是否为空
			if (channel.getRegionId() == null) {
				return "通道必须属于某个分组下";
			} else {
				Regions region = regionsService.findRegionsById(channel.getRegionId());
				if (region == null) {
					return "所属分组不存在";
				}
			}
			// 判断分组是否是最后一级分组
			List<Regions> list = regionsService.queryRegionByParentId(String.valueOf(channel.getRegionId()), null,
					null);
			if (list != null && list.size() > 0) {
				return "该分组下仍有分组，请重新选择分组";
			}
			if (channel.getRegionId() == Constants.REGIONS_ROOT_ID) {
				return "根分组下不能直接添加通道，请重新选择分组";
			}
			// 通道名称长度不超过50
			if (!ValidationUtils.checkStrLength(channel.getChannelName(), 1, 50)) {
				return "通道名称限50个字符";
			}
			// 通道Guid是否为空
			if (!StringUtils.isNotEmpty(channel.getChannelName())) {
				return "通道名称不能为空";
			}
			if (!StringUtils.isNotEmpty(channel.getChannelAddr())) {
				return "视频地址不能为空";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelVerid(), 0, 64)) {
				return "通道verid限64个字符";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelDescription(), 0, 512)) {
				return "通道描述限512个字符";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelAddr(), 0, 512)) {
				return "通道地址限512个字符";
			}
//			if (!StringUtils.isNotEmpty(channel.getPreviewAddr())) {
//				return "视频预览地址不能为空";
//			}
//            if (!(channel.getReserved() == null) && channel.getReserved() == 2) {
//                if (!ValidationUtils.checkStrByPattern("(rtsp):\\/\\/[^\\u4e00-\\u9fa5]+", channel.getChannelAddr())) {
//                    return "通道地址必须rtsp://开头的非中文组合";
//                }
//            }
//            if (!(channel.getReserved() == null) && channel.getReserved() == 6) {
//                if (!ValidationUtils.checkStrByPattern("(gb):\\/\\/[^\\u4e00-\\u9fa5]+", channel.getChannelAddr())) {
//                    return "通道地址必须gb://开头的非中文组合";
//                }
//            }
			// 通道位置长度不超过120
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelArea(), 0, 512)) {
				return "通道位置限512个字符";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelNo(), 0, 30)) {
				return "国标编码限30个字符";
			}
			if (StringUtils.isNotEmpty(channel.getChannelNo()) && !StringUtils.isNumeric(channel.getChannelNo())) {
				return "国标编码应只包含数字";
			}
			if (channel.getChannelPort() != null && !StringUtils.isNumeric(channel.getChannelPort().toString())) {
				return "视频端口应只包含数字";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelUid(), 0, 20)) {
				return "账号长度限20个字符";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelPsw(), 0, 20)) {
				return "视频密码限20个字符";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getSdkVer(), 0, 32)) {
				return "channelSdkVer限32个字符";
			}
			if (channel.getChannelLatitude() != null
					&& !ValidationUtils.checkValueScope(channel.getChannelLatitude(), -90.00, 90.00)) {
				return "纬度范围越界";
			}
			if (channel.getChannelLongitude() != null
					&& !ValidationUtils.checkValueScope(channel.getChannelLongitude(), -180.00, 180.00)) {
				return "经度范围越界";
			}
		} catch (Exception e) {
			log.error("通道校验失败" + ExpUtil.getExceptionDetail(e));
		}
		return "";
	}

	/**
	 * 删除通道信息(逻辑删除) author dongsl date 2017年8月8日下午1:42:39
	 */
	@ResponseBody
	@RequestMapping("/delete")
	public ResponseBean delete(@RequestBody JSONObject json, HttpServletRequest request, HttpServletResponse response) {
		ResponseBean result = new ResponseBean();
		String id = json.getString("id");
		String uuid = request.getHeader("uuid");
		if (id == null || "".equals(id)) {
			return ResultUtils.error(2003, "所删除的通道不存在");
		}
		try {
			Channel channel = channelService.findChannelById(id);
			if (channel == null) {
				return ResultUtils.error(100, "通道不存在或已删除");
			}
			// 删除时校验是否有布控任务或者实时结构化任务，有的话不能删除
			List<JobsChannel> jobsList = jobsChannelService.getJobsChannelByChannelUuid(id);
			if (jobsList != null && jobsList.size() > 0) {
				jobsChannelService.removeJobsChannel(id);
//				return ResultUtils.error(100, "该通道有布控任务，不可删除");
			}
//			List<TaskChannel> taskList = taskChannelService.getTaskChannelByChannelUuid(id);
//			if (taskList != null && taskList.size() > 0) {
//				taskChannelService.deleteByChannelId(id);
////				return ResultUtils.error(100, "该通道正在执行任务,请删除任务后删除");
//			}
			int c = channelService.removeChannel(id, uuid);
			if (c == 1) {
				// 通知更新告警的缓存
				dataInitService.init();
				result = ResultUtils.success("删除成功");
			} else {
				result = ResultUtils.error(100, "删除失败");
			}
		} catch (Exception e) {
			log.error(e);
			result = ResultUtils.error(-1, e.getMessage());
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/queryThriftChannel")
	public ChannelQueryResult queryThriftChannel(@RequestBody JSONObject json) {
		Integer regionID = json.getInteger("regionID");
		Integer nStartNum = json.getInteger("nStartNum");
		Integer nCount = json.getInteger("nCount");
		if (nStartNum == null) {
			nStartNum = 0;
		}
		if (nCount == null) {
			nCount = 10000000;
		}
		ChannelQueryResult cqr = new ChannelQueryResult();
		try {
			cqr = channelService.QueryChannelsByRegionID(regionID, nStartNum, nCount);
		} catch (Exception e) {
			log.error("查询通道信息异常", e);
			e.printStackTrace();
			return cqr;
		}
		return cqr;
	}

	/**
	 * 通道信息导出
	 * 
	 * @throws IOException
	 */
	@RequestMapping("/exportChannel")
	@ResponseBody
	public void exportChannel(HttpServletResponse response) throws IOException {
		List<String> nameList = new ArrayList<String>();
		ExcelHelper<Channel> util = new ExcelHelper(Channel.class);
		List<Channel> datas = new ArrayList<Channel>();
		String fileName = "通道模板.xlsx";
		fileName = new String(fileName.getBytes("GBK"), "ISO-8859-1");
		response.reset();
		response.setContentType("application/ms-excel;charset=UTF-8");
		response.setHeader("Content-disposition", "attachment;filename=" + fileName);
		OutputStream output = response.getOutputStream();
		util.exportExcel(datas, "模板项", 60000, output);
	}

	// 通道模版下载
	@RequestMapping("/downloadChannelTemplate")
	@ResponseBody
	public ResponseBean downloadChannelTemplate(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) throws Exception {
		ResponseBean result = new ResponseBean();
		String type = request.getParameter("type");
		String filePath = "";
		if (StringUtils.isNotEmpty(type)) {
			if (Integer.parseInt(type) == 1) {
				filePath = PropUtils.getString("watch_download") + "/excelTemplate/" + "RTSP直连批量上传.xlsx";
			} else if (Integer.parseInt(type) == 2) {
				filePath = PropUtils.getString("watch_download") + "/excelTemplate/" + "GB-T28181-2011批量上传.xlsx";
			} else if (Integer.parseInt(type) == 3) {
				filePath = PropUtils.getString("watch_download") + "/excelTemplate/" + "GB-T29191-2016批量上传.xlsx";
			} else if (Integer.parseInt(type) == 4) {
				filePath = PropUtils.getString("watch_download") + "/excelTemplate/" + "GB-T28181-SVAC批量上传.xlsx";
			}
		}
		result.setError(0);
		result.setMessage("successful");
		result.getMap().put("filePath", filePath);
		return result;
	}

	/**
	 * 批量导入通道
	 * 
	 * @param file
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/importExcel", method = { RequestMethod.POST })
	public ResponseBean ImportExcel(@RequestParam(value = "uploadFile") MultipartFile file,
			HttpServletRequest request) {
		log.info("开始进行批量导入------------");
		long l1 = new Date().getTime() / 1000;
		ResponseBean result = new ResponseBean();
		Map map = new HashMap<String, Object>();
		String errMsg = "";
		try {
			String uuid = request.getHeader("uuid");
			String regionId = request.getParameter("regionId");
			// 区域id是否为空
			if (regionId == null) {
				result = ResultUtils.error(100, "通道必须属于某个分组下");
				return result;
			} else {
				Regions region = regionsService.findRegionsById(Integer.parseInt(regionId));
				if (region == null) {
					result = ResultUtils.error(100, "所属分组不存在");
					return result;
				}
				// 判断分组是否是最后一级分组
				List<Regions> list = regionsService.queryRegionByParentId(regionId, null, null);
				if (list != null && list.size() > 0) {
					result = ResultUtils.error(100, "该分组下仍有分组，请重新选择分组");
					return result;
				}
			}
			String filename = file.getOriginalFilename();
//			reserved 1 视频平台	2RTSP直连 3海康抓拍机(测试) 4大华抓拍机(测试) 6 GB/T 28181(2011) 	7 GB/T 28181(2016) 8 GB/T 28181(SVAC)
			Integer reserved = 1;
			List<Channel> list = new ArrayList<>();
			if (StringUtils.isNotEmpty(filename)) {
				if (filename.contains("RTSP")) {
					ExcelHelper<Channel> util = new ExcelHelper(Channel.class);
					list = util.importExcel("模板项", file);
					reserved = 2;
				} else if (filename.contains("2011")) {
					excelToList(file, list);
					reserved = 6;
				} else if (filename.contains("2016")) {
					excelToList1(file, list);
					reserved = 7;
				} else if (filename.contains("SVAC")) {
					excelToList(file, list);
					reserved = 8;
				}
			}
			if (list != null && list.size() > 0) {
				for (int j = 0; j < list.size(); j++) {
					Channel channel = list.get(j);
					if (!StringUtils.checkObjFieldIsNotNull(channel)) {
						continue;
					}
					channel = convertParam(channel);
					channel.setReserved(reserved);
					channel.setRegionId(Integer.parseInt(regionId));
					int k = j + 2;
					String valRes = validateParamWithRows(channel, k);
					if (reserved != 2) {
						if (!StringUtils.isNotEmpty(channel.getChannelNo())) {
							valRes += "第" + k + "行数据错误：国标编码不能为空;";
						}
						if (StringUtils.isNotEmpty(channel.getChannelAddr())
								&& !ValidationUtils.ipCheck(channel.getChannelAddr())) {
							valRes += "第" + k + "行数据错误：视频地址校验失败;";
						}
						if (channel.getChannelPort() == null) {
							valRes += "第" + k + "行数据错误：视频端口不能为空;";
						} else {
							if (channel.getChannelPort() == 0) {
								valRes += "第" + k + "行数据错误：视频端口不能为0;";
							}
							if (channel.getChannelPort() == 1111111111) {
								valRes += "第" + k + "行数据错误：视频端口超过最大设置长度;";
							}
							if (channel.getChannelPort() == 999999999) {
								valRes += "第" + k + "行数据错误：视频端口只能为数字;";
							}
						}
						if (StringUtils.isNotEmpty(channel.getPreviewAddr())) {
							if (channel.getPreviewAddr().startsWith("rtsp://")
									|| channel.getPreviewAddr().startsWith("gb://")) {
							} else {
								valRes += "第" + k + "行数据错误：视频预览格式错误;";
							}
							if (channel.getPreviewAddr().length() > 99) {
								valRes += "第" + k + "行数据错误：视频预览地址超过最大设置长度;";
							}
						}
					}
					if (StringUtils.isNotEmpty(valRes)) { // 如果验证通过，则进行下一步，否则返回错误信息
						errMsg += valRes;
						continue;
					} else {
						if (channel.getReserved() == null) {
							channel.setReserved(1);
						}
						// 从rtsp地址增加rtmp流地址
						if ((channel.getChannelRtmp() == null || "".equals(channel.getChannelRtmp()))
								&& channel.getChannelAddr() != null && !"".equals(channel.getChannelAddr())) {
							if (channel.getChannelAddr().startsWith("rtsp://")) {
								channel.setChannelRtmp(channel.getChannelAddr().replaceAll("rtsp://",
										"rtmp://" + PropUtils.getString("streamMedia.ip") + "/live/"));
							}
							if (channel.getChannelAddr().startsWith("gb://")) {
								channel.setChannelRtmp(channel.getChannelAddr().replaceAll("gb://",
										"rtmp://" + PropUtils.getString("streamMedia.ip") + "/live/"));
							}
						}
						if (channel.getChannelLongitude() != null) {
							Double longitude = channel.getChannelLongitude();
							double longValue = new BigDecimal(String.valueOf(longitude))
									.setScale(12, BigDecimal.ROUND_DOWN).doubleValue();
							channel.setChannelLongitude(longValue);
						}
						if (channel.getChannelLatitude() != null) {
							Double latitude = channel.getChannelLatitude();
							double latiValue = new BigDecimal(String.valueOf(latitude))
									.setScale(12, BigDecimal.ROUND_DOWN).doubleValue();
							channel.setChannelLatitude(latiValue);
						}
						channel.setCreateUser(uuid);
						channel.setMinFaceWidth(40);
						channel.setMinImgQuality(0);
						channel.setMinCapDistance(3);
						channel.setMaxYaw(30);
						channel.setMaxRoll(30);
						channel.setMaxPitch(30);

						// 国际编码和视频地址已存在并且与数据库数据不相同时，覆盖原先数据
						Channel channel1 = null;
						if (channel.getChannelNo() != null && !"".equals(channel.getChannelNo())) {
							List<Channel> clist = channelService.queryChannelByChannelNoAndAddr(channel.getChannelNo(),
									channel.getChannelAddr());
							if (clist != null && clist.size() > 0) {
								channel1 = (Channel) clist.get(0);
								channel.setUuid(channel1.getUuid());
								// equals没起作用
								if (!channel.equals(channel1)) {
									channelService.updateChannel(channel);
									dataInitService.init();
								}
							}
						}
						// 通道名称已存在并且与数据库数据不相同时，覆盖原先数据
						List<Channel> listt = channelService.queryChannelByChannelNameAndRegionId(
								channel.getChannelName(), channel.getRegionId(), Constants.DELETE_NO);
						Channel channel2 = null;
						if (listt != null && listt.size() > 0) {
							channel2 = (Channel) listt.get(0);
							channel.setUuid(channel2.getUuid());
							// equals没起作用
							if (!channel.equals(channel2)) {
								channelService.updateChannel(channel);
								dataInitService.init();
							}
						}
						if (channel1 == null && channel2 == null) {
							channelService.saveNewChannel(channel);
							// 通知更新告警的缓存
							dataInitService.init();
						}
					}
				}
				if (errMsg != "") {
					result = ResultUtils.error(-1, errMsg + " 请重新上传 ");
				} else {
					result = ResultUtils.success("导入成功");
				}
			} else {
				result = ResultUtils.error(-1, "批量导入数据为空,请重新上传 ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("批量导入通道失败，失败信息为：" + e.getMessage());
		}
		long l2 = new Date().getTime() / 1000;
		log.info("批量导入通道耗時：" + (l2 - l1) + "s");

		return result;
	}

	private String validateParamWithRows(Channel channel, int k) {
		try {
			// 通道号是否重复
//			if (channel.getChannelNo() != null && !"".equals(channel.getChannelNo())) {
//				List list = channelService.queryChannelByChannelNoAndAddr(channel.getChannelNo(),
//						channel.getChannelAddr());
//				if (list != null && list.size() > 0) {
//					return "第" + k + "行数据错误：国际编码和视频地址已存在";
//				}
//			}
//			List listt = channelService.queryChannelByChannelNameAndRegionId(channel.getChannelName(),
//					channel.getRegionId(), Constants.DELETE_NO);
//			if (listt != null && listt.size() > 0) {
//				return "第" + k + "行数据错误：该分组已存在名为：" + channel.getChannelName() + "的通道";
//			}
			// 通道名称是否为空
			if (!StringUtils.isNotEmpty(channel.getChannelName())) {
				return "第" + k + "行数据错误：通道名称不能为空;";
			}
			if (ValidationUtils.withSpecialChar(channel.getChannelName())) {
				return "第" + k + "行数据错误：通道名称不能含有特殊字符;";
			}
			// 区域id是否为空
			if (channel.getRegionId() == null) {
				return "第" + k + "行数据错误：通道必须属于某个分组下;";
			} else {
				Regions region = regionsService.findRegionsById(channel.getRegionId());
				if (region == null) {
					return "第" + k + "行数据错误：所属分组不存在;";
				}
			}
			// 判断分组是否是最后一级分组
			List<Regions> list = regionsService.queryRegionByParentId(String.valueOf(channel.getRegionId()), null,
					null);
			if (list != null && list.size() > 0) {
				return "第" + k + "行数据错误：该分组下仍有分组，请重新选择分组 ";
			}
			if (channel.getRegionId() == Constants.REGIONS_ROOT_ID) {
				return "第" + k + "行数据错误：根分组下不能直接添加通道，请重新选择分组 ";
			}
			// 通道名称长度不超过50
			if (!ValidationUtils.checkStrLength(channel.getChannelName(), 1, 50)) {
				return "第" + k + "行数据错误：通道名称限50个字符;";
			}
			// 通道Guid是否为空
			if (!StringUtils.isNotEmpty(channel.getChannelName())) {
				return "第" + k + "行数据错误：通道名称不能为空;";
			}
			if (!StringUtils.isNotEmpty(channel.getChannelAddr())) {
				return "第" + k + "行数据错误：视频地址不能为空;";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelVerid(), 0, 64)) {
				return "第" + k + "行数据错误：通道verid限64个字符;";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelDescription(), 0, 512)) {
				return "第" + k + "行数据错误：通道描述信息限512个字符;";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelAddr(), 0, 512)) {
				return "第" + k + "行数据错误：通道地址限512个字符;";
			}
			// 通道位置长度不超过120
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelArea(), 0, 512)) {
				return "第" + k + "行数据错误：通道位置限512个字符;";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelNo(), 0, 30)) {
				return "第" + k + "行数据错误：国标编码限30个字符;";
			}
			if (StringUtils.isNotEmpty(channel.getChannelNo()) && !StringUtils.isNumeric(channel.getChannelNo())) {
				return "第" + k + "行数据错误：国标编码应只包含数字;";
			}
			if (channel.getChannelPort() != null && !StringUtils.isNumeric(channel.getChannelPort().toString())) {
				return "第" + k + "行数据错误：视频端口应只包含数字;";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelUid(), 0, 20)) {
				return "第" + k + "行数据错误：账号限20个字符;";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelPsw(), 0, 20)) {
				return "第" + k + "行数据错误：视频密码限20个字符;";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getSdkVer(), 0, 32)) {
				return "第" + k + "行数据错误：channelSdkVer限32个字符;";
			}
			if (channel.getChannelLatitude() != null
					&& !ValidationUtils.checkValueScope(channel.getChannelLatitude(), -90.00, 90.00)) {
				return "第" + k + "行数据错误：纬度范围越界;";
			}
			if (channel.getChannelLongitude() != null
					&& !ValidationUtils.checkValueScope(channel.getChannelLongitude(), -180.00, 180.00)) {
				return "第" + k + "行数据错误：经度范围越界;";
			}
		} catch (Exception e) {
			log.error("通道校验失败" + ExpUtil.getExceptionDetail(e));
		}
		return "";
	}

	private void excelToList1(MultipartFile file, List<Channel> list) {
		ExcelHelper<Channel2> util = new ExcelHelper(Channel2.class);
		List<Channel2> list1 = util.importExcel("模板项", file);
		if (list1 != null && list1.size() > 0) {
			for (Channel2 channel1 : list1) {
				Channel channel = new Channel();
				channel.setChannelName(channel1.getChannelName());
				channel.setChannelNo(channel1.getChannelNo());
				channel.setChannelAddr(channel1.getChannelAddr());
				channel.setChannelPort(channel1.getChannelPort());
				channel.setSipNetTypeTag(channel1.getSipNetTypeTag());
				channel.setChannelUid(channel1.getChannelUid());
				channel.setChannelPsw(channel1.getChannelPsw());
				channel.setPreviewAddr(channel1.getPreviewAddr());
				channel.setChannelDirectTag(channel1.getChannelDirectTag());
				if (channel1.getChannelLongitude() != null) {
					Double longitude = channel1.getChannelLongitude();
					double longValue = new BigDecimal(String.valueOf(longitude)).setScale(12, BigDecimal.ROUND_DOWN)
							.doubleValue();
					channel.setChannelLongitude(longValue);
				}
				if (channel1.getChannelLatitude() != null) {
					Double latitude = channel1.getChannelLatitude();
					double latiValue = new BigDecimal(String.valueOf(latitude)).setScale(12, BigDecimal.ROUND_DOWN)
							.doubleValue();
					channel.setChannelLatitude(latiValue);
				}
				channel.setChannelArea(channel1.getChannelArea());
				channel.setChannelDescription(channel1.getChannelDescription());
				if (StringUtils.isNotEmpty(channel1.getChannelTypeTag())) {
					if (channel1.getChannelTypeTag().equals("AUTO")) {
						channel.setChannelType(0);
					} else if (channel1.getChannelTypeTag().equals("H.264")) {
						channel.setChannelType(1);
					} else if (channel1.getChannelTypeTag().equals("H.265")) {
						channel.setChannelType(2);
					} else if (channel1.getChannelTypeTag().equals("MPEG-4")) {
						channel.setChannelType(3);
					} else if (channel1.getChannelTypeTag().equals("SVAC")) {
						channel.setChannelType(4);
					} else if (channel1.getChannelTypeTag().equals("DAHUA")) {
						channel.setChannelType(5);
					} else {
						channel.setChannelType(0);
					}
				}
				list.add(channel);
			}
		}
	}

	private void excelToList(MultipartFile file, List<Channel> list) {
		ExcelHelper<Channel1> util = new ExcelHelper(Channel1.class);
		List<Channel1> list1 = util.importExcel("模板项", file);
		if (list1 != null && list1.size() > 0) {
			for (Channel1 channel1 : list1) {
				Channel channel = new Channel();
				channel.setChannelName(channel1.getChannelName());
				channel.setChannelNo(channel1.getChannelNo());
				channel.setChannelAddr(channel1.getChannelAddr());
				channel.setChannelPort(channel1.getChannelPort());
				channel.setSipNetTypeTag(channel1.getSipNetTypeTag());
				channel.setChannelUid(channel1.getChannelUid());
				channel.setChannelPsw(channel1.getChannelPsw());
				channel.setPreviewAddr(channel1.getPreviewAddr());
				channel.setChannelDirectTag(channel1.getChannelDirectTag());
				if (channel1.getChannelLongitude() != null) {
					Double longitude = channel1.getChannelLongitude();
					double longValue = new BigDecimal(String.valueOf(longitude)).setScale(12, BigDecimal.ROUND_DOWN)
							.doubleValue();
					channel.setChannelLongitude(longValue);
				}
				if (channel1.getChannelLatitude() != null) {
					Double latitude = channel1.getChannelLatitude();
					double latiValue = new BigDecimal(String.valueOf(latitude)).setScale(12, BigDecimal.ROUND_DOWN)
							.doubleValue();
					channel.setChannelLatitude(latiValue);
				}
				channel.setChannelArea(channel1.getChannelArea());
				channel.setChannelDescription(channel1.getChannelDescription());
				if (StringUtils.isNotEmpty(channel1.getChannelTypeTag())) {
					if (channel1.getChannelTypeTag().equals("AUTO")) {
						channel.setChannelType(0);
					} else if (channel1.getChannelTypeTag().equals("H.264")) {
						channel.setChannelType(1);
					} else if (channel1.getChannelTypeTag().equals("H.265")) {
						channel.setChannelType(2);
					} else if (channel1.getChannelTypeTag().equals("MPEG-4")) {
						channel.setChannelType(3);
					} else if (channel1.getChannelTypeTag().equals("SVAC")) {
						channel.setChannelType(4);
					} else if (channel1.getChannelTypeTag().equals("DAHUA")) {
						channel.setChannelType(5);
					} else {
						channel.setChannelType(0);
					}
				}
				list.add(channel);
			}
		}
	}

	private Channel convertParam(Channel channel) {
		if (channel != null) {
			// 视频源方向，1-东，2-南，3-西，4-北，5-东南，6-东北，7-西南，8-西北
			if (StringUtils.isNotEmpty(channel.getChannelDirectTag())) {
				if (channel.getChannelDirectTag().equals("东")) {
					channel.setChannelDirect(1);
				} else if (channel.getChannelDirectTag().equals("南")) {
					channel.setChannelDirect(2);
				} else if (channel.getChannelDirectTag().equals("西")) {
					channel.setChannelDirect(3);
				} else if (channel.getChannelDirectTag().equals("北")) {
					channel.setChannelDirect(4);
				} else if (channel.getChannelDirectTag().equals("东南")) {
					channel.setChannelDirect(5);
				} else if (channel.getChannelDirectTag().equals("东北")) {
					channel.setChannelDirect(6);
				} else if (channel.getChannelDirectTag().equals("西南")) {
					channel.setChannelDirect(7);
				} else if (channel.getChannelDirectTag().equals("西北")) {
					channel.setChannelDirect(8);
				} else {
					channel.setChannelDirect(0);
				}
			}
			if (StringUtils.isNotEmpty(channel.getSipNetTypeTag())) {
				if (channel.getSipNetTypeTag().equals("TCP")) {
					channel.setSipNetType(0);
				} else if (channel.getSipNetTypeTag().equals("UDP")) {
					channel.setSipNetType(1);
				} else {
					channel.setSipNetType(0);
				}
			}
			if (StringUtils.isNotEmpty(channel.getChannelTypeTag())) {
				if (channel.getChannelTypeTag().equals("AUTO")) {
					channel.setChannelType(0);
				} else if (channel.getChannelTypeTag().equals("H.264")) {
					channel.setChannelType(1);
				} else if (channel.getChannelTypeTag().equals("H.265")) {
					channel.setChannelType(2);
				} else if (channel.getChannelTypeTag().equals("MPEG-4")) {
					channel.setChannelType(3);
				} else if (channel.getChannelTypeTag().equals("SVAC")) {
					channel.setChannelType(4);
				} else if (channel.getChannelTypeTag().equals("DAHUA")) {
					channel.setChannelType(5);
				} else {
					channel.setChannelType(0);
				}
			}
			return channel;
		} else {
			return null;
		}
	}

}
