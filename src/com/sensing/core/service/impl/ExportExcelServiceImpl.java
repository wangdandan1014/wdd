package com.sensing.core.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sensing.core.bean.Channel;
import com.sensing.core.bean.MotorVehicle;
import com.sensing.core.bean.RpcLog;
import com.sensing.core.bean.alarm.resp.AlarmDetailResp;
import com.sensing.core.bean.alarm.resp.AlarmResp;
import com.sensing.core.bean.result.TrafficCount;
import com.sensing.core.dao.IChannelDAO;
import com.sensing.core.resp.CapResp;
import com.sensing.core.service.IAlarmService;
import com.sensing.core.service.ICapAttrConvertService;
import com.sensing.core.service.IChannelService;
import com.sensing.core.service.IDataOverviewService;
import com.sensing.core.service.IExportExcelService;
import com.sensing.core.service.IRpcLogService;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.POIUtils;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.StringTool;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.props.PropUtils;
import com.sensing.core.utils.time.DateStyle;
import com.sensing.core.utils.time.DateUtil;
import com.sensing.core.utils.time.TransfTimeUtil;

@Service
public class ExportExcelServiceImpl implements IExportExcelService {

	@Autowired
	public IRpcLogService rpcLogService;
	@Autowired
	public IDataOverviewService dataOverviewService;
	@Resource
	private IChannelService channelService;
	@Resource
	private IChannelDAO channelDAO;
	@Resource
	public ICapAttrConvertService capAttrConvertService;
	@Resource
	public IAlarmService alarmService;

	/**
	 * 将统计的告警数据导出到excel
	 * 
	 * @param pagerParams
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2018年12月12日 上午9:57:51
	 */
	public SXSSFWorkbook alarmCountToExcel(Map<String, Object> params) throws Exception {
		// 创建excel对象
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		try {
//			List<Map<String, Object>> alarmCountList = dataOverviewService.alarmCountByDay(params);
			List<Map<String, Object>> alarmCountList = new ArrayList<Map<String, Object>>();
			JSONArray ja = null;
			Object dataObj = params.get("data");
			if (dataObj != null && !"".equals(dataObj.toString())) {
				ja = JSONArray.parseArray(dataObj.toString());
				if (ja != null && ja.size() > 0) {
					for (int i = 0; i < ja.size(); i++) {
						JSONObject jo = JSONObject.parseObject(ja.get(i).toString());
						Map<String, Object> map = new HashMap<String, Object>();

						map.put("date", jo.getString("date"));
						map.put("level1Count", jo.getInteger("level1Count"));
						map.put("level2Count", jo.getInteger("level2Count"));
						map.put("level3Count", jo.getInteger("level3Count"));

						alarmCountList.add(map);
					}
				}
			}

			// 创建excel表单
			SXSSFSheet sheet = workbook.createSheet("表格1");

			// 设置表头样式
			Font topFont = POIUtils.getFont(workbook, "宋体", true, 11);
			CellStyle topCellStyle = POIUtils.getCellStyle(workbook, topFont, 2, 2, "0000");

			// 设置样式
			Font font = POIUtils.getFont(workbook, "宋体", false, 11);
			CellStyle alignCellStyle = POIUtils.getCellStyle(workbook, font, 2, 2, "0000");
			CellStyle leftCellStyle = POIUtils.getCellStyle(workbook, font, 1, 2, "0000");
			// 设置文本自动换行
			alignCellStyle.setWrapText(true);
			leftCellStyle.setWrapText(true);

			// 第一行
			SXSSFRow hssfRow0 = sheet.createRow(0);
			CellRangeAddress region = new CellRangeAddress(0, 0, 1, 3);// 下标从0开始 起始行号，终止行号， 起始列号，终止列号
			sheet.addMergedRegion(region);
//			"endDate":"2018-12-28 17:19:29","startDate":"2018-12-22 00:00:00
			String startDate = (String) params.get("startDate");
			String endDate = (String) params.get("endDate");
			Integer date = (Integer) params.get("date");
			if (date != null) {
				if ("1".equals(date.toString())) {
					POIUtils.setCell(hssfRow0, topCellStyle, "统计时间：今天", 2);
				} else if ("-1".equals(date.toString())) {
					POIUtils.setCell(hssfRow0, topCellStyle, "统计时间：昨天", 2);
				} else if ("-7".equals(date.toString())) {
					POIUtils.setCell(hssfRow0, topCellStyle, "统计时间：最近7天", 2);
				} else if ("-30".equals(date.toString())) {
					POIUtils.setCell(hssfRow0, topCellStyle, "统计时间：最近30天", 2);
				} else {
					POIUtils.setCell(hssfRow0, topCellStyle, "统计时间：最近7天", 2);
				}
			}
			if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
				POIUtils.setCell(hssfRow0, topCellStyle, "统计时间：" + startDate + "至" + endDate, 2);
			}
			// 第二行
			SXSSFRow hssfRow1 = sheet.createRow(1);

			if (alarmCountList != null && alarmCountList.size() > 0) {

				POIUtils.setColumnWidth(sheet, new Integer[] { 5, 20, 15, 15, 15 });

				POIUtils.setCell(hssfRow1, topCellStyle, "序号", 1);
				POIUtils.setCell(hssfRow1, topCellStyle, "日期", 2);
				POIUtils.setCell(hssfRow1, topCellStyle, "一级报警数量", 3);
				POIUtils.setCell(hssfRow1, topCellStyle, "二级报警数量", 4);
				POIUtils.setCell(hssfRow1, topCellStyle, "三级报警数量", 5);

				for (int i = 0; i < alarmCountList.size(); i++) {
					Map<String, Object> rowDataMap = alarmCountList.get(i);
					SXSSFRow row = sheet.createRow((i + 2));

					POIUtils.setCell(row, alignCellStyle, (i + 1) + "", 1);
					POIUtils.setCell(row, alignCellStyle, rowDataMap.get("date").toString(), 2);
					POIUtils.setCell(row, alignCellStyle, rowDataMap.get("level1Count").toString(), 3);
					POIUtils.setCell(row, alignCellStyle, rowDataMap.get("level2Count").toString(), 4);
					POIUtils.setCell(row, alignCellStyle, rowDataMap.get("level3Count").toString(), 5);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workbook;
	}

	/**
	 * 根据查询条件导出日志，获取SXSSFWorkbook对象
	 */
	public SXSSFWorkbook logExportToExcel(Pager pagerParams) {
		// 创建excel对象
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		try {
			// 创建excel表单
			SXSSFSheet sheet = workbook.createSheet("表格1");

			// 设置表头样式
			Font topFont = POIUtils.getFont(workbook, "宋体", true, 11);
			CellStyle topCellStyle = POIUtils.getCellStyle(workbook, topFont, 2, 2, "0000");

			// 设置样式
			Font font = POIUtils.getFont(workbook, "宋体", false, 11);
			CellStyle alignCellStyle = POIUtils.getCellStyle(workbook, font, 2, 2, "0000");
			CellStyle leftCellStyle = POIUtils.getCellStyle(workbook, font, 1, 2, "0000");
			// 设置文本自动换行
			alignCellStyle.setWrapText(true);
			leftCellStyle.setWrapText(true);

			// 第一行
			SXSSFRow hssfRow1 = sheet.createRow(0);

			// 查询要导出的数据
//			pagerParams.addF("type","1");
//			pagerParams.addF("callTimeFrom","2018-11-11 16:39:31");
//			pagerParams.addF("callTimeEnd","2018-11-15 16:39:31");
//			pagerParams.setPageNo(1);
//			pagerParams.setPageRows(20);

			Pager pager = rpcLogService.queryPage(pagerParams);

			Integer type = Integer.parseInt(pagerParams.getF().get("type"));
			if (pager != null && pager.getResultList() != null && pager.getResultList().size() > 0) {
				// 登录日志
				if (type == 1) {
					// 设置列宽
					POIUtils.setColumnWidth(sheet, new Integer[] { 5, 15, 20, 30, 15, 15 });

					POIUtils.setCell(hssfRow1, topCellStyle, "序号", 1);
					POIUtils.setCell(hssfRow1, topCellStyle, "日期时间", 2);
					POIUtils.setCell(hssfRow1, topCellStyle, "IP", 3);
					POIUtils.setCell(hssfRow1, topCellStyle, "用户角色", 4);
					POIUtils.setCell(hssfRow1, topCellStyle, "操作", 5);
					POIUtils.setCell(hssfRow1, topCellStyle, "用户", 6);

					// 设置每行的数据
					for (int i = 0; i < pager.getResultList().size(); i++) {
						RpcLog rpcLog = (RpcLog) pager.getResultList().get(i);
						SXSSFRow row = sheet.createRow((i + 1));

						POIUtils.setCell(row, alignCellStyle, (i + 1) + "", 1);
						POIUtils.setCell(row, alignCellStyle, DateUtil.DateToString(rpcLog.getCallTime()), 2);
						POIUtils.setCell(row, alignCellStyle, rpcLog.getIp(), 3);
						POIUtils.setCell(row, leftCellStyle, rpcLog.getRoleName(), 4);
						POIUtils.setCell(row, leftCellStyle, rpcLog.getTodo(), 5);
						POIUtils.setCell(row, alignCellStyle, rpcLog.getUserName(), 6);

					}
				}

				// 操作日志
				if (type == 2) {
					// 设置列宽
					POIUtils.setColumnWidth(sheet, new Integer[] { 5, 15, 15, 20, 15 });

					POIUtils.setCell(hssfRow1, topCellStyle, "序号", 1);
					POIUtils.setCell(hssfRow1, topCellStyle, "日期时间", 2);
					POIUtils.setCell(hssfRow1, topCellStyle, "模块", 3);
					POIUtils.setCell(hssfRow1, topCellStyle, "用户", 4);
					POIUtils.setCell(hssfRow1, topCellStyle, "内容", 5);

					// 设置每行的数据
					for (int i = 0; i < pager.getResultList().size(); i++) {
						RpcLog rpcLog = (RpcLog) pager.getResultList().get(i);
						SXSSFRow row = sheet.createRow((i + 1));

						POIUtils.setCell(row, alignCellStyle, (i + 1) + "", 1);
						POIUtils.setCell(row, alignCellStyle, DateUtil.DateToString(rpcLog.getCallTime()), 2);
						POIUtils.setCell(row, alignCellStyle, rpcLog.getModule(), 3);
						POIUtils.setCell(row, alignCellStyle, rpcLog.getUserName(), 4);
						POIUtils.setCell(row, leftCellStyle, rpcLog.getTodo(), 5);
					}
				}

				// 运行日志
				if (type == 3) {
					// 设置列宽
					POIUtils.setColumnWidth(sheet, new Integer[] { 5, 15, 40, 15 });

					POIUtils.setCell(hssfRow1, topCellStyle, "序号", 1);
					POIUtils.setCell(hssfRow1, topCellStyle, "日期时间", 2);
//					POIUtils.setCell(hssfRow1, topCellStyle, "用户", 3);
					POIUtils.setCell(hssfRow1, topCellStyle, "内容", 3);
					POIUtils.setCell(hssfRow1, topCellStyle, "类型", 4);

					// 设置每行的数据
					for (int i = 0; i < pager.getResultList().size(); i++) {
						RpcLog rpcLog = (RpcLog) pager.getResultList().get(i);
						SXSSFRow row = sheet.createRow((i + 1));

						POIUtils.setCell(row, alignCellStyle, (i + 1) + "", 1);
						POIUtils.setCell(row, alignCellStyle, DateUtil.DateToString(rpcLog.getCallTime()), 2);
//						POIUtils.setCell(row, alignCellStyle, rpcLog.getCreateUser(), 3);
						POIUtils.setCell(row, leftCellStyle, rpcLog.getErrorMsg(), 3);
						POIUtils.setCell(row, alignCellStyle, rpcLog.getTodo(), 4);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workbook;
	}

	@Override
	public SXSSFWorkbook exportSearchInfoToExcel(Integer capType, List<CapResp> list, int type, String exportType)
			throws Exception {

		if (list != null && list.size() > 0 && list.size() < 300 && list.get(0).getScore() == null) {
			Collections.sort(list);
		}

		// 创建excel对象
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		// 创建excel表单
		SXSSFSheet sheet = workbook.createSheet("表格1");

		// 设置表头样式
		Font topFont = POIUtils.getFont(workbook, "宋体", true, 11);
		CellStyle topCellStyle = POIUtils.getCellStyle(workbook, topFont, 2, 2, "0000");

		// 画图的顶级管理器，一个sheet只能获取一个
		SXSSFDrawing patriarch = sheet.createDrawingPatriarch();

		// 设置样式
		Font font = POIUtils.getFont(workbook, "宋体", false, 11);
		CellStyle alignCellStyle = POIUtils.getCellStyle(workbook, font, 2, 2, "0000");
		CellStyle leftCellStyle = POIUtils.getCellStyle(workbook, font, 1, 2, "0000");
		// 设置文本自动换行
		alignCellStyle.setWrapText(true);
		leftCellStyle.setWrapText(true);

		// 设置超链接样式
		CellStyle linkCellStyle = POIUtils.getLinkCellStyle(workbook);

		// 第一行
		SXSSFRow hssfRow1 = sheet.createRow(0);
		String folder = "pictures/";
		switch (capType) {
		case 1:
			capPeopleToExcel(list, workbook, sheet, topCellStyle, patriarch, alignCellStyle, linkCellStyle, hssfRow1,
					folder, type, exportType);
			break;
		case 3:
			capMotorToExcel(list, workbook, sheet, topCellStyle, patriarch, alignCellStyle, linkCellStyle, hssfRow1,
					folder, type, exportType);
			break;
		case 4:
			capNonmotorToExcel(list, workbook, sheet, topCellStyle, patriarch, alignCellStyle, linkCellStyle, hssfRow1,
					folder, type, exportType);
			break;
		default:
			break;
		}
		return workbook;

	}

	private void capPeopleToExcel(@SuppressWarnings("rawtypes") List list, SXSSFWorkbook workbook, SXSSFSheet sheet,
			CellStyle topCellStyle, SXSSFDrawing patriarch, CellStyle alignCellStyle, CellStyle linkCellStyle,
			SXSSFRow hssfRow1, String folder, int type, String exportType) throws Exception {
		int num = 0;
		int exportTypeTag = 0;
		if (type == 1) {
			POIUtils.setCell(hssfRow1, topCellStyle, "图片（原图）", 2);
			POIUtils.setCell(hssfRow1, topCellStyle, "图片（场景图）", 3);
		} else {
			num = 2;
		}

		POIUtils.setCell(hssfRow1, topCellStyle, "序号", 1);
		POIUtils.setCell(hssfRow1, topCellStyle, "分类", (4 - num));
		if (StringUtils.isNotEmpty(exportType) && "3".equals(exportType)) {
			exportTypeTag = 1;
			POIUtils.setCell(hssfRow1, topCellStyle, "视频名称", (5 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "视频描述", (6 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "上传时间", (7 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "抓拍时间", (8 - num));
		} else {
			POIUtils.setCell(hssfRow1, topCellStyle, "通道", (5 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "地点", (6 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "抓拍时间", (7 - num));
		}
		POIUtils.setCell(hssfRow1, topCellStyle, "性别", (8 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "年龄", (9 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "朝向", (10 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "背包", (11 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "运动状态", (12 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "携带物", (13 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "帽子", (14 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "眼镜", (15 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "口罩", (16 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "上身颜色", (17 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "上身类型", (18 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "上身纹理", (19 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "下身颜色", (20 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "下身类型", (21 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "下身纹理", (22 - num + exportTypeTag));
		if (((CapResp) list.get(0)).getScore() != null) {
			POIUtils.setCell(hssfRow1, topCellStyle, "相似度", (23 - num + exportTypeTag));
			if (PropUtils.getInt("is_export_caplocation") == 1) {
				POIUtils.setCell(hssfRow1, topCellStyle, "坐标位置", (24 - num + exportTypeTag));
			}
		} else {
			if (PropUtils.getInt("is_export_caplocation") == 1) {
				POIUtils.setCell(hssfRow1, topCellStyle, "坐标位置", (23 - num + exportTypeTag));
			}
		}

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				try {
					SXSSFRow row = sheet.createRow((i + 1));
					row.setHeight((short) 1900);
					CapResp bean = (CapResp) list.get(i);
					POIUtils.setCell(row, alignCellStyle, "" + (i + 1), 1);
					POIUtils.setCell(row, alignCellStyle, "行人", (4 - num));
					Channel channel = null;
					if (bean != null && bean.getCapPeople() != null) {
						if (bean.getCapPeople().getDeviceId() != null) {
							channel = channelDAO.getChannelAll(bean.getCapPeople().getDeviceId());
						}
						if (channel != null) {
							if (StringUtils.isNotEmpty(exportType) && "3".equals(exportType)) {
								POIUtils.setCell(row, alignCellStyle,
										channel.getChannelName() == null ? "" : channel.getChannelName(), (5 - num));
								POIUtils.setCell(row, alignCellStyle,
										channel.getChannelArea() == null ? "" : channel.getChannelDescription(),
										(6 - num));
								POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getCapTimeStr(), (7 - num));
								POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getFrameTimeStr(), (8 - num));
							} else {
								POIUtils.setCell(row, alignCellStyle,
										channel.getChannelName() == null ? "" : channel.getChannelName(), (5 - num));
								POIUtils.setCell(row, alignCellStyle,
										channel.getChannelArea() == null ? "" : channel.getChannelArea(), (6 - num));
								POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getCapTimeStr(), (7 - num));
							}
						}
						POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getGenderCodeTag(),
								(8 - num + exportTypeTag));
						POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getAgeTag(),
								(9 - num + exportTypeTag));
						POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getOrientationTag(),
								(10 - num + exportTypeTag));
						POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getBagStyleTag(),
								(11 - num + exportTypeTag));
						POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getMotionTag(),
								(12 - num + exportTypeTag));
						POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getBigBagStyleTag(),
								(13 - num + exportTypeTag));
						POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getCapTag(),
								(14 - num + exportTypeTag));
						POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getGlassTag(),
								(15 - num + exportTypeTag));
						POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getRespiratorTag(),
								(16 - num + exportTypeTag));
						POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getCoatColorTag(),
								(17 - num + exportTypeTag));
						POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getCoatLengthTag(),
								(18 - num + exportTypeTag));
						POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getCoatTextureTag(),
								(19 - num + exportTypeTag));
						POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getTrousersColorTag(),
								(20 - num + exportTypeTag));
						POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getTrousersLenTag(),
								(21 - num + exportTypeTag));
						POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getTrousersTextureTag(),
								(22 - num + exportTypeTag));
						if (bean.getScore() != null) {
							POIUtils.setCell(row, alignCellStyle, bean.getScore() + "", (23 - num + exportTypeTag));
							if (PropUtils.getInt("is_export_caplocation") == 1) {
								POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getCapLocation(),
										(24 - num + exportTypeTag));
							}
						} else {
							if (PropUtils.getInt("is_export_caplocation") == 1) {
								POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getCapLocation(),
										(23 - num + exportTypeTag));
							}
						}
						if (type == 1) {
							if (StringUtils.isNotEmpty(bean.getCapPeople().getCapUrl())) {
								String suffix = bean.getCapPeople().getCapUrl()
										.substring(bean.getCapPeople().getCapUrl().lastIndexOf("."));
								if (PropUtils.getInt("file.store.type") == Constants.FILE_STORE_TYPE_HAIKANG) {
									POIUtils.setLinkCell(workbook, row, linkCellStyle,
											StringTool.fileNamePre0((i + 1) + "-1") + ".jpg",
											folder + StringTool.fileNamePre0((i + 1) + "-1") + ".jpg", 2);
								} else {
									POIUtils.setLinkCell(workbook, row, linkCellStyle,
											StringTool.fileNamePre0((i + 1) + "-1") + suffix,
											folder + StringTool.fileNamePre0((i + 1) + "-1") + suffix, 2);
								}
							}
							if (StringUtils.isNotEmpty(bean.getCapPeople().getSeceneUrl())) {
								String suffix = bean.getCapPeople().getSeceneUrl()
										.substring(bean.getCapPeople().getSeceneUrl().lastIndexOf("."));
								if (PropUtils.getInt("file.store.type") == Constants.FILE_STORE_TYPE_HAIKANG) {
									POIUtils.setLinkCell(workbook, row, linkCellStyle,
											StringTool.fileNamePre0((i + 1) + "-2") + ".jpg",
											folder + StringTool.fileNamePre0((i + 1) + "-2") + ".jpg", 3);
								} else {
									POIUtils.setLinkCell(workbook, row, linkCellStyle,
											StringTool.fileNamePre0((i + 1) + "-2") + suffix,
											folder + StringTool.fileNamePre0((i + 1) + "-2") + suffix, 3);
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}

	}

	private void capMotorToExcel(@SuppressWarnings("rawtypes") List list, SXSSFWorkbook workbook, SXSSFSheet sheet,
			CellStyle topCellStyle, SXSSFDrawing patriarch, CellStyle alignCellStyle, CellStyle linkCellStyle,
			SXSSFRow hssfRow1, String folder, int type, String exportType) throws Exception {
		int num = 0;
		int exportTypeTag = 0;
		if (type == 1) {
			POIUtils.setCell(hssfRow1, topCellStyle, "图片（原图）", 2);
			POIUtils.setCell(hssfRow1, topCellStyle, "图片（场景图）", 3);
		} else {
			num = 2;
		}
		POIUtils.setCell(hssfRow1, topCellStyle, "序号", 1);
		POIUtils.setCell(hssfRow1, topCellStyle, "分类", (4 - num));
		if (StringUtils.isNotEmpty(exportType) && "3".equals(exportType)) {
			exportTypeTag = 1;
			POIUtils.setCell(hssfRow1, topCellStyle, "视频名称", (5 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "视频描述", (6 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "上传时间", (7 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "抓拍时间", (8 - num));
		} else {
			POIUtils.setCell(hssfRow1, topCellStyle, "通道", (5 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "地点", (6 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "抓拍时间", (7 - num));
		}
		POIUtils.setCell(hssfRow1, topCellStyle, "车牌号", (8 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "车辆颜色", (9 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "朝向", (10 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "车牌颜色", (11 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "车牌类型", (12 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "车型", (13 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "品牌", (14 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "子品牌", (15 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "年款", (16 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "年检标", (17 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "纸巾盒", (18 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "挂饰", (19 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "遮阳板", (20 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "主驾驶系安全带", (21 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "副驾驶系安全带", (22 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "主驾驶打手机", (23 - num + exportTypeTag));
		if (((CapResp) list.get(0)).getScore() != null) {
			POIUtils.setCell(hssfRow1, topCellStyle, "相似度", (24 - num + exportTypeTag));
			if (PropUtils.getInt("is_export_caplocation") == 1) {
				POIUtils.setCell(hssfRow1, topCellStyle, "坐标位置", (25 - num + exportTypeTag));
			}
		} else {
			if (PropUtils.getInt("is_export_caplocation") == 1) {
				POIUtils.setCell(hssfRow1, topCellStyle, "坐标位置", (24 - num + exportTypeTag));
			}
		}

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				SXSSFRow row = sheet.createRow((i + 1));
				row.setHeight((short) 1900);
				CapResp bean = (CapResp) list.get(i);
				POIUtils.setCell(row, alignCellStyle, "" + (i + 1), 1);
				POIUtils.setCell(row, alignCellStyle, "机动车", (4 - num));
				Channel channel = null;
				if (bean != null && bean.getMotorVehicle() != null) {
					if (bean.getMotorVehicle().getDeviceId() != null) {
						channel = channelDAO.getChannelAll(bean.getMotorVehicle().getDeviceId());
					}
					if (channel != null) {
						if (StringUtils.isNotEmpty(exportType) && "3".equals(exportType)) {
							POIUtils.setCell(row, alignCellStyle,
									channel.getChannelName() == null ? "" : channel.getChannelName(), (5 - num));
							POIUtils.setCell(row, alignCellStyle,
									channel.getChannelArea() == null ? "" : channel.getChannelDescription(), (6 - num));
							POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getCapTimeStr(), (7 - num));
							POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getFrameTimeStr(), (8 - num));
						} else {
							POIUtils.setCell(row, alignCellStyle,
									channel.getChannelName() == null ? "" : channel.getChannelName(), (5 - num));
							POIUtils.setCell(row, alignCellStyle,
									channel.getChannelArea() == null ? "" : channel.getChannelArea(), (6 - num));
							POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getCapTimeStr(), (7 - num));
						}
					}
					POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getPlateNo(),
							(8 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getVehicleColorTag(),
							(9 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getOrientationTag(),
							(10 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getPlateColorTag(),
							(11 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getPlateClassTag(),
							(12 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getVehicleClassTag(),
							(13 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getVehicleBrandTag(),
							(14 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getVehicleModelTag(),
							(15 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getVehicleStylesTag(),
							(16 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getVehicleMarkerMotTag(),
							(17 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getVehicleMarkerTissueboxTag(),
							(18 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getVehicleMarkerPendantTag(),
							(19 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getSunvisorTag(),
							(20 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getSafetyBeltTag(),
							(21 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getSafetyBeltCopilotTag(),
							(22 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getCallingTag(),
							(23 - num + exportTypeTag));
					if (bean.getScore() != null) {
						POIUtils.setCell(row, alignCellStyle, bean.getScore() + "", (24 - num + exportTypeTag));
						if (PropUtils.getInt("is_export_caplocation") == 1) {
							POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getCapLocation(),
									(25 - num + exportTypeTag));
						}
					} else {
						if (PropUtils.getInt("is_export_caplocation") == 1) {
							POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getCapLocation(),
									(24 - num + exportTypeTag));
						}
					}
					if (type == 1) {
						if (StringUtils.isNotEmpty(bean.getMotorVehicle().getCapUrl())) {
							String suffix = bean.getMotorVehicle().getCapUrl()
									.substring(bean.getMotorVehicle().getCapUrl().lastIndexOf("."));
							if (PropUtils.getInt("file.store.type") == Constants.FILE_STORE_TYPE_HAIKANG) {
								POIUtils.setLinkCell(workbook, row, linkCellStyle,
										StringTool.fileNamePre0((i + 1) + "-1") + ".jpg",
										folder + StringTool.fileNamePre0((i + 1) + "-1") + ".jpg", 2);
							} else {
								POIUtils.setLinkCell(workbook, row, linkCellStyle,
										StringTool.fileNamePre0((i + 1) + "-1") + suffix,
										folder + StringTool.fileNamePre0((i + 1) + "-1") + suffix, 2);
							}
						}
						if (StringUtils.isNotEmpty(bean.getMotorVehicle().getSeceneUrl())) {
							String suffix = bean.getMotorVehicle().getSeceneUrl()
									.substring(bean.getMotorVehicle().getSeceneUrl().lastIndexOf("."));
							if (PropUtils.getInt("file.store.type") == Constants.FILE_STORE_TYPE_HAIKANG) {
								POIUtils.setLinkCell(workbook, row, linkCellStyle,
										StringTool.fileNamePre0((i + 1) + "-2") + ".jpg",
										folder + StringTool.fileNamePre0((i + 1) + "-2") + ".jpg", 3);
							} else {
								POIUtils.setLinkCell(workbook, row, linkCellStyle,
										StringTool.fileNamePre0((i + 1) + "-2") + suffix,
										folder + StringTool.fileNamePre0((i + 1) + "-2") + suffix, 3);
							}
						}
					}
				}
			}
		}
	}

	private void capNonmotorToExcel(@SuppressWarnings("rawtypes") List list, SXSSFWorkbook workbook, SXSSFSheet sheet,
			CellStyle topCellStyle, SXSSFDrawing patriarch, CellStyle alignCellStyle, CellStyle linkCellStyle,
			SXSSFRow hssfRow1, String folder, int type, String exportType) throws Exception {
		int num = 0;
		int exportTypeTag = 0;
		if (type == 1) {
			POIUtils.setCell(hssfRow1, topCellStyle, "图片（原图）", 2);
			POIUtils.setCell(hssfRow1, topCellStyle, "图片（场景图）", 3);
		} else {
			num = 2;
		}
		POIUtils.setCell(hssfRow1, topCellStyle, "序号", 1);
		POIUtils.setCell(hssfRow1, topCellStyle, "分类", (4 - num));
		if (StringUtils.isNotEmpty(exportType) && "3".equals(exportType)) {
			exportTypeTag = 1;
			POIUtils.setCell(hssfRow1, topCellStyle, "视频名称", (5 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "视频描述", (6 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "上传时间", (7 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "抓拍时间", (8 - num));
		} else {
			POIUtils.setCell(hssfRow1, topCellStyle, "通道", (5 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "地点", (6 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "抓拍时间", (7 - num));
		}
		POIUtils.setCell(hssfRow1, topCellStyle, "车型", (8 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "朝向", (9 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "车辆颜色", (10 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "运动状态", (11 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "年龄", (12 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "性别", (13 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "帽子", (14 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "上身颜色", (15 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "上身类型", (16 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "上身纹理", (17 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "眼镜", (18 - num + exportTypeTag));
		POIUtils.setCell(hssfRow1, topCellStyle, "口罩", (19 - num + exportTypeTag));
		if (((CapResp) list.get(0)).getScore() != null) {
			POIUtils.setCell(hssfRow1, topCellStyle, "相似度", (20 - num + exportTypeTag));
			if (PropUtils.getInt("is_export_caplocation") == 1) {
				POIUtils.setCell(hssfRow1, topCellStyle, "坐标位置", (21 - num + exportTypeTag));
			}
		} else {
			if (PropUtils.getInt("is_export_caplocation") == 1) {
				POIUtils.setCell(hssfRow1, topCellStyle, "坐标位置", (20 - num + exportTypeTag));
			}

		}
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				SXSSFRow row = sheet.createRow((i + 1));
				row.setHeight((short) 1900);
				CapResp bean = (CapResp) list.get(i);
				POIUtils.setCell(row, alignCellStyle, "" + (i + 1), 1);
				POIUtils.setCell(row, alignCellStyle, "非机动车", (4 - num));
				Channel channel = null;
				if (bean != null && bean.getCapNonmotor() != null) {
					if (bean.getCapNonmotor().getDeviceId() != null) {
						channel = channelDAO.getChannelAll(bean.getCapNonmotor().getDeviceId());
					}
					if (channel != null) {
						if (StringUtils.isNotEmpty(exportType) && "3".equals(exportType)) {
							POIUtils.setCell(row, alignCellStyle,
									channel.getChannelName() == null ? "" : channel.getChannelName(), (5 - num));
							POIUtils.setCell(row, alignCellStyle,
									channel.getChannelArea() == null ? "" : channel.getChannelDescription(), (6 - num));
							POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getCapTimeStr(), (7 - num));
							POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getFrameTimeStr(), (8 - num));
						} else {
							POIUtils.setCell(row, alignCellStyle,
									channel.getChannelName() == null ? "" : channel.getChannelName(), (5 - num));
							POIUtils.setCell(row, alignCellStyle,
									channel.getChannelArea() == null ? "" : channel.getChannelArea(), (6 - num));
							POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getCapTimeStr(), (7 - num));
						}
					}
					POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getVehicleClassTag(),
							(8 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getOrientationTag(),
							(9 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getVehicleColorTag(),
							(10 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getMotionTag(),
							(11 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getAgeTag(),
							(12 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getGenderCodeTag(),
							(13 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getCapTag(),
							(14 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getCoatColorTag(),
							(15 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getCoatLengthTag(),
							(16 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getCoatTextureTag(),
							(17 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getGlassTag(),
							(18 - num + exportTypeTag));
					POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getRespiratorTag(),
							(19 - num + exportTypeTag));
					if (bean.getScore() != null) {
						POIUtils.setCell(row, alignCellStyle, bean.getScore() + "", (20 - num + exportTypeTag));
						if (PropUtils.getInt("is_export_caplocation") == 1) {
							POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getCapLocation(),
									(21 - num + exportTypeTag));
						}
					} else {
						if (PropUtils.getInt("is_export_caplocation") == 1) {
							POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getCapLocation(),
									(20 - num + exportTypeTag));
						}
					}
					if (type == 1) {
						if (StringUtils.isNotEmpty(bean.getCapNonmotor().getCapUrl())) {
							String suffix = bean.getCapNonmotor().getCapUrl()
									.substring(bean.getCapNonmotor().getCapUrl().lastIndexOf("."));
							if (PropUtils.getInt("file.store.type") == Constants.FILE_STORE_TYPE_HAIKANG) {
								POIUtils.setLinkCell(workbook, row, linkCellStyle,
										StringTool.fileNamePre0((i + 1) + "-1") + ".jpg",
										folder + StringTool.fileNamePre0((i + 1) + "-1") + ".jpg", 2);
							} else {
								POIUtils.setLinkCell(workbook, row, linkCellStyle,
										StringTool.fileNamePre0((i + 1) + "-1") + suffix,
										folder + StringTool.fileNamePre0((i + 1) + "-1") + suffix, 2);
							}
						}
						if (StringUtils.isNotEmpty(bean.getCapNonmotor().getSeceneUrl())) {
							String suffix = bean.getCapNonmotor().getSeceneUrl()
									.substring(bean.getCapNonmotor().getSeceneUrl().lastIndexOf("."));
							if (PropUtils.getInt("file.store.type") == Constants.FILE_STORE_TYPE_HAIKANG) {
								POIUtils.setLinkCell(workbook, row, linkCellStyle,
										StringTool.fileNamePre0((i + 1) + "-2") + ".jpg",
										folder + StringTool.fileNamePre0((i + 1) + "-2") + ".jpg", 3);
							} else {
								POIUtils.setLinkCell(workbook, row, linkCellStyle,
										StringTool.fileNamePre0((i + 1) + "-2") + suffix,
										folder + StringTool.fileNamePre0((i + 1) + "-2") + suffix, 3);
							}
						}
					}
				}

			}
		}
	}

	@Override
	public SXSSFWorkbook exporttrafficCountInfoToExcel(List<Map<String, Object>> mList) {
		// 创建excel对象
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		// 创建excel表单
		SXSSFSheet sheet = workbook.createSheet("表格1");

		// 设置表头样式
		Font topFont = POIUtils.getFont(workbook, "宋体", true, 11);
		CellStyle topCellStyle = POIUtils.getCellStyle(workbook, topFont, 2, 2, "0000");

		// 设置样式
		Font font = POIUtils.getFont(workbook, "宋体", false, 11);
		CellStyle alignCellStyle = POIUtils.getCellStyle(workbook, font, 2, 2, "0000");
		CellStyle leftCellStyle = POIUtils.getCellStyle(workbook, font, 1, 2, "0000");
		// 设置文本自动换行
		alignCellStyle.setWrapText(true);
		leftCellStyle.setWrapText(true);

		// 第一行
		SXSSFRow hssfRow1 = sheet.createRow(0);
		POIUtils.setCell(hssfRow1, topCellStyle, "序号", 1);
		POIUtils.setCell(hssfRow1, topCellStyle, "通道", 2);
		POIUtils.setCell(hssfRow1, topCellStyle, "日期", 3);
		POIUtils.setCell(hssfRow1, topCellStyle, "行人", 4);
		POIUtils.setCell(hssfRow1, topCellStyle, "轿车", 5);
		POIUtils.setCell(hssfRow1, topCellStyle, "面包车", 6);
		POIUtils.setCell(hssfRow1, topCellStyle, "越野车（SUV）", 7);
		POIUtils.setCell(hssfRow1, topCellStyle, "商务车（MPV）", 8);
		POIUtils.setCell(hssfRow1, topCellStyle, "皮卡", 9);
		POIUtils.setCell(hssfRow1, topCellStyle, "小型客车", 10);
		POIUtils.setCell(hssfRow1, topCellStyle, "中型客车", 11);
		POIUtils.setCell(hssfRow1, topCellStyle, "大型客车", 12);
		POIUtils.setCell(hssfRow1, topCellStyle, "微型货车", 13);
		POIUtils.setCell(hssfRow1, topCellStyle, "小型货车", 14);
		POIUtils.setCell(hssfRow1, topCellStyle, "中型货车", 15);
		POIUtils.setCell(hssfRow1, topCellStyle, "重型货车", 16);
		POIUtils.setCell(hssfRow1, topCellStyle, "二轮自行车", 17);
		POIUtils.setCell(hssfRow1, topCellStyle, "二轮电动车/摩托车", 18);
		POIUtils.setCell(hssfRow1, topCellStyle, "三轮摩托车（带棚）", 19);
		POIUtils.setCell(hssfRow1, topCellStyle, "三轮摩托车（车厢封闭）", 20);
		POIUtils.setCell(hssfRow1, topCellStyle, "三轮摩托车（无棚&不封闭）", 21);
		POIUtils.setCell(hssfRow1, topCellStyle, "其他", 22);
		POIUtils.setCell(hssfRow1, topCellStyle, "未知", 23);
		for (int i = 0; i < mList.size(); i++) {
			SXSSFRow row = sheet.createRow((i + 1));// 第n行
			Map<String, Object> map = mList.get(i);
			if (!map.isEmpty() && map.size() > 0) {
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("resultList" + "");
				String time = (String) map.get("time" + "");
				@SuppressWarnings("unchecked")
				List<Channel> channels = (List<Channel>) map.get("channels" + "");
				String names = "";
				if (channels != null) {
					List<String> channelNameList = channels.stream().map(a -> a.getChannelName())
							.collect(Collectors.toList());
					names = StringUtils.listToString(channelNameList);
				}
				row.setHeight((short) 1900);
				POIUtils.setCell(row, alignCellStyle, "" + (i + 1), 1);
				POIUtils.setCell(row, alignCellStyle, names.isEmpty() ? "" : names, 2);
				POIUtils.setCell(row, alignCellStyle, time, 3);
				if (list != null && list.size() > 0) {
					for (int j = 0; j < list.size(); j++) {
						String type = (String) list.get(j).get("type" + "");
						Integer sum = (Integer) list.get(j).get("count" + "");
						String count = String.valueOf(sum);
						switch (type) {
						case "行人":
							POIUtils.setCell(row, topCellStyle, count, 4);
							break;
						case "轿车":
							POIUtils.setCell(row, topCellStyle, count, 5);
							break;
						case "面包车":
							POIUtils.setCell(row, topCellStyle, count, 6);
							break;
						case "越野车（SUV）":
							POIUtils.setCell(row, topCellStyle, count, 7);
							break;
						case "商务车（MPV）":
							POIUtils.setCell(row, topCellStyle, count, 8);
							break;
						case "皮卡":
							POIUtils.setCell(row, topCellStyle, count, 9);
							break;
						case "小型客车":
							POIUtils.setCell(row, topCellStyle, count, 10);
							break;
						case "中型客车":
							POIUtils.setCell(row, topCellStyle, count, 11);
							break;
						case "大型客车":
							POIUtils.setCell(row, topCellStyle, count, 12);
							break;
						case "微型货车":
							POIUtils.setCell(row, topCellStyle, count, 13);
							break;
						case "小型货车":
							POIUtils.setCell(row, topCellStyle, count, 14);
							break;
						case "中型货车":
							POIUtils.setCell(row, topCellStyle, count, 15);
							break;
						case "重型货车":
							POIUtils.setCell(row, topCellStyle, count, 16);
							break;
						case "二轮电动车/摩托车":
							POIUtils.setCell(row, topCellStyle, count, 17);
							break;
						case "三轮摩托车（带棚）":
							POIUtils.setCell(row, topCellStyle, count, 18);
							break;
						case "三轮摩托车（车厢封闭）":
							POIUtils.setCell(row, topCellStyle, count, 19);
							break;
						case "三轮摩托车（无棚&不封闭）":
							POIUtils.setCell(row, topCellStyle, count, 20);
							break;
						case "二轮自行车":
							POIUtils.setCell(row, topCellStyle, count, 21);
							break;
						case "其他":
							POIUtils.setCell(row, topCellStyle, count, 22);
							break;
						case "未知":
							POIUtils.setCell(row, topCellStyle, count, 23);
							break;
						default:
							POIUtils.setCell(row, topCellStyle, "0", j + 4);
							break;
						}
					}
				} else {
					for (int i1 = 0; i1 < 20; i1++) {
						POIUtils.setCell(row, topCellStyle, "0", (i1 + 4));
					}
				}
			}
		}
		return workbook;
	}

	/**
	 * type 特殊资源 1-文本加图片 2-文本
	 */
	@Override
	public SXSSFWorkbook exportAlarmData(List<AlarmResp> list, List<MotorVehicle> motorList, Integer type) {
		// 创建excel对象
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		try {
			// 创建excel表单
			SXSSFSheet sheet = workbook.createSheet("表格1");
			sheet.setColumnWidth(3, 12 * 256);
			sheet.setColumnWidth(4, 12 * 256);
			// 设置表头样式
			Font topFont = POIUtils.getFont(workbook, "宋体", true, 11);
			CellStyle topCellStyle = POIUtils.getCellStyle(workbook, topFont, 2, 2, "0000");

			// 设置样式
			Font font = POIUtils.getFont(workbook, "宋体", false, 11);
			CellStyle alignCellStyle = POIUtils.getCellStyle(workbook, font, 2, 2, "0000");
			CellStyle leftCellStyle = POIUtils.getCellStyle(workbook, font, 1, 2, "0000");
			// 设置文本自动换行
			alignCellStyle.setWrapText(true);
			leftCellStyle.setWrapText(true);

			// 设置超链接样式
			CellStyle linkCellStyle = POIUtils.getLinkCellStyle(workbook);

			// 第一行
			SXSSFRow hssfRow0 = sheet.createRow(0);
			hssfRow0.setHeight((short) 500);
			SXSSFRow hssfRow1 = sheet.createRow(1);
			hssfRow1.setHeight((short) 500);
			if (type == 1) {
				CellRangeAddress region = new CellRangeAddress(0, 0, 17, 23);// 下标从0开始 起始行号，终止行号， 起始列号，终止列号
				sheet.addMergedRegion(region);
				POIUtils.setCell(hssfRow0, topCellStyle, "目标属性", 18);
				// 第二行
				for (int i = 1; i < 18; i++) {
					CellRangeAddress region1 = new CellRangeAddress(0, 1, i - 1, i - 1);// 下标从0开始 起始行号，终止行号， 起始列号，终止列号
					sheet.addMergedRegion(region1);
				}
			} else {
				CellRangeAddress region = new CellRangeAddress(0, 0, 15, 21);// 下标从0开始 起始行号，终止行号， 起始列号，终止列号
				sheet.addMergedRegion(region);
				POIUtils.setCell(hssfRow0, topCellStyle, "目标属性", 16);
				// 第二行
				for (int i = 1; i < 16; i++) {
					CellRangeAddress region1 = new CellRangeAddress(0, 1, i - 1, i - 1);// 下标从0开始 起始行号，终止行号， 起始列号，终止列号
					sheet.addMergedRegion(region1);
				}
			}
			int num = 0;
			if (type == 1) {
				POIUtils.setCell(hssfRow0, topCellStyle, "抓拍图", 2);
				POIUtils.setCell(hssfRow0, topCellStyle, "库图片", 3);
			} else {
				num = 2;
			}
			POIUtils.setCell(hssfRow0, topCellStyle, "序号", 1);
			POIUtils.setCell(hssfRow0, topCellStyle, "报警车牌号", (4 - num));
			POIUtils.setCell(hssfRow0, topCellStyle, "目标车牌号", (5 - num));
			POIUtils.setCell(hssfRow0, topCellStyle, "抓拍时间", (6 - num));
			POIUtils.setCell(hssfRow0, topCellStyle, "报警时间", (7 - num));
			POIUtils.setCell(hssfRow0, topCellStyle, "序列号", (8 - num));
			POIUtils.setCell(hssfRow0, topCellStyle, "布控任务", (9 - num));
			POIUtils.setCell(hssfRow0, topCellStyle, "目标库", (10 - num));
			POIUtils.setCell(hssfRow0, topCellStyle, "布控时间", (11 - num));
			POIUtils.setCell(hssfRow0, topCellStyle, "核查结果", (12 - num));
			POIUtils.setCell(hssfRow0, topCellStyle, "核查描述", (13 - num));
			POIUtils.setCell(hssfRow0, topCellStyle, "布控级别", (14 - num));
			POIUtils.setCell(hssfRow0, topCellStyle, "出现次数", (15 - num));
			POIUtils.setCell(hssfRow0, topCellStyle, "通道名称", (16 - num));
			POIUtils.setCell(hssfRow0, topCellStyle, "通道位置", (17 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "车型", (18 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "车辆颜色", (19 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "车牌颜色", (20 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "车牌类型", (21 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "品牌", (22 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "子品牌", (23 - num));
			POIUtils.setCell(hssfRow1, topCellStyle, "年款", (24 - num));

			String folder = "pictures/";
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					SXSSFRow row = sheet.createRow((i + 2));
					row.setHeight((short) 1900);
					AlarmResp bean = (AlarmResp) list.get(i);
					if (motorList != null && motorList.size() > 0) {
						for (int j = 0; j < motorList.size(); j++) {
							if (bean.getCapUuid().equals(motorList.get(j).getUuid())) {
								MotorVehicle vehicle = capAttrConvertService.motorVehicleConvert(motorList.get(j),null);
								POIUtils.setCell(row, alignCellStyle, TransfTimeUtil.UnixTimeStamp2Date(
										vehicle.getCapTime(), DateStyle.YYYY_MM_DD_HH_MM_SS), (6 - num));
								POIUtils.setCell(row, alignCellStyle, vehicle.getVehicleClassTag(), (18 - num));
								POIUtils.setCell(row, alignCellStyle, vehicle.getVehicleColorTag(), (19 - num));
								POIUtils.setCell(row, alignCellStyle, vehicle.getPlateColorTag(), (20 - num));
								POIUtils.setCell(row, alignCellStyle, vehicle.getPlateClassTag(), (21 - num));
								POIUtils.setCell(row, alignCellStyle, vehicle.getVehicleBrandTag(), (22 - num));
								POIUtils.setCell(row, alignCellStyle, vehicle.getVehicleModelTag(), (23 - num));
								POIUtils.setCell(row, alignCellStyle, vehicle.getVehicleStylesTag(), (24 - num));
//								motorList.remove(j);
//								j--;
								break;
							}
						}
					}
					POIUtils.setCell(row, alignCellStyle, "" + (i + 1), 1);
					POIUtils.setCell(row, alignCellStyle, bean.getAlarmPlateNo(), (4 - num));
					POIUtils.setCell(row, alignCellStyle, bean.getPlateNo(), (5 - num));
					POIUtils.setCell(row, alignCellStyle, bean.getaTime(), (7 - num));
					POIUtils.setCell(row, alignCellStyle, bean.getAlarmId(), (8 - num));
					POIUtils.setCell(row, alignCellStyle, bean.getJobsName(), (9 - num));
					POIUtils.setCell(row, alignCellStyle, bean.getTemplateDbName(), (10 - num));
					AlarmDetailResp resp = alarmService.queryByUuid(bean.getUuid());
					if (resp != null && StringUtils.isNotEmpty(resp.getJobsDate())) {
						POIUtils.setCell(row, alignCellStyle, resp.getJobsDate().replaceAll("<br/>", ""), (11 - num));
					}
					POIUtils.setCell(row, alignCellStyle, bean.getStateTag(), (12 - num));
					POIUtils.setCell(row, alignCellStyle, bean.getStateMemo(), (13 - num));
					POIUtils.setCell(row, alignCellStyle, bean.getLevelTag(), (14 - num));
					POIUtils.setCell(row, alignCellStyle,
							bean.getAppearCount() == null ? "0" : bean.getAppearCount() + "", (15 - num));
					POIUtils.setCell(row, alignCellStyle, bean.getChannelName() == null ? "" : bean.getChannelName(),
							(16 - num));
					POIUtils.setCell(row, alignCellStyle, bean.getDeviceArea() == null ? "" : bean.getDeviceArea(),
							(17 - num));

					if (type == 1) {
						if (StringUtils.isNotEmpty(bean.getCapImgUrl())) {
							String suffix = bean.getCapImgUrl().substring(bean.getCapImgUrl().lastIndexOf("."));
							if (PropUtils.getInt("file.store.type") == Constants.FILE_STORE_TYPE_HAIKANG) {
								POIUtils.setLinkCell(workbook, row, linkCellStyle, (i + 1) + "-1.jpg",
										folder + (i + 1) + "-1.jpg", 2);
							} else {
								POIUtils.setLinkCell(workbook, row, linkCellStyle, (i + 1) + "-1" + suffix,
										folder + (i + 1) + "-1" + suffix, 2);
							}
						}
						if (StringUtils.isNotEmpty(bean.getImgUrl())) {
							String suffix = bean.getImgUrl().substring(bean.getImgUrl().lastIndexOf("."));
							POIUtils.setLinkCell(workbook, row, linkCellStyle, (i + 1) + suffix,
									folder + (i + 1) + suffix, 3);

							if (PropUtils.getInt("file.store.type") == Constants.FILE_STORE_TYPE_HAIKANG) {
								POIUtils.setLinkCell(workbook, row, linkCellStyle, (i + 1) + ".jpg",
										folder + (i + 1) + ".jpg", 3);
							} else {
								POIUtils.setLinkCell(workbook, row, linkCellStyle, (i + 1) + suffix,
										folder + (i + 1) + suffix, 3);
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return workbook;
	}

	@Override
	public SXSSFWorkbook exporttrafficCount(Map<String, Object> map, List<TrafficCount> tcList, String type) {
		// 创建excel对象
		SXSSFWorkbook workbook = new SXSSFWorkbook();

		// 创建excel表单
		SXSSFSheet sheet = workbook.createSheet("表格1");

		// 设置表头样式
		Font topFont = POIUtils.getFont(workbook, "宋体", true, 11);
		CellStyle topCellStyle = POIUtils.getCellStyle(workbook, topFont, 2, 2, "0000");

		// 设置样式
		Font font = POIUtils.getFont(workbook, "宋体", false, 11);
		CellStyle alignCellStyle = POIUtils.getCellStyle(workbook, font, 2, 2, "0000");
		CellStyle leftCellStyle = POIUtils.getCellStyle(workbook, font, 1, 2, "0000");
		// 设置文本自动换行
		alignCellStyle.setWrapText(true);
		leftCellStyle.setWrapText(true);

		// 第一行
		SXSSFRow hssfRow0 = sheet.createRow(0);
		CellRangeAddress region = new CellRangeAddress(0, 0, 1, 4);// 下标从0开始 起始行号，终止行号， 起始列号，终止列号
		sheet.addMergedRegion(region);

		String startTimeStr = (String) map.get("startTimeStr");
		String endTimeStr = (String) map.get("endTimeStr");
		String date = (String) map.get("date");
		if (StringUtils.isNotEmpty(date)) {
			if ("1".equals(date)) {
				POIUtils.setCell(hssfRow0, topCellStyle, "统计时间：今天", 2);
			} else if ("-1".equals(date)) {
				POIUtils.setCell(hssfRow0, topCellStyle, "统计时间：昨天", 2);
			} else if ("-7".equals(date)) {
				POIUtils.setCell(hssfRow0, topCellStyle, "统计时间：最近7天", 2);
			} else if ("-30".equals(date)) {
				POIUtils.setCell(hssfRow0, topCellStyle, "统计时间：最近30天", 2);
			} else {
				POIUtils.setCell(hssfRow0, topCellStyle, "统计时间：最近7天", 2);
			}
		}
		if (StringUtils.isNotEmpty(startTimeStr) && StringUtils.isNotEmpty(endTimeStr)) {
			POIUtils.setCell(hssfRow0, topCellStyle, "统计时间：" + startTimeStr + "至" + endTimeStr, 2);
		}
		// 第二行
		SXSSFRow hssfRow1 = sheet.createRow(1);
		if (tcList != null && tcList.size() > 0) {

			POIUtils.setColumnWidth(sheet, new Integer[] { 5, 20, 15, 15, 15 });

			POIUtils.setCell(hssfRow1, topCellStyle, "序号", 1);
			POIUtils.setCell(hssfRow1, topCellStyle, "日期", 2);
			// 全部-0 行人-1 机动车-2 行人机动车-3
			if ("0".equals(type)) {
				POIUtils.setCell(hssfRow1, topCellStyle, "非机动车", 5);
			}
			if ("2".equals(type)) {
				POIUtils.setCell(hssfRow1, topCellStyle, "机动车", 3);
			}
			if (!"2".equals(type)) {
				POIUtils.setCell(hssfRow1, topCellStyle, "行人", 3);
				if (!"1".equals(type)) {
					POIUtils.setCell(hssfRow1, topCellStyle, "机动车", 4);
				}
			}

			for (int i = 0; i < tcList.size(); i++) {
				TrafficCount trafficCount = tcList.get(i);
				SXSSFRow row = sheet.createRow((i + 2));

				POIUtils.setCell(row, alignCellStyle, (i + 1) + "", 1);
				POIUtils.setCell(row, alignCellStyle, trafficCount.getDate(), 2);
				if ("2".equals(type)) {
					POIUtils.setCell(row, alignCellStyle,
							trafficCount.getMotorCount() == null ? "0" : trafficCount.getMotorCount() + "", 3);
				}
				if (!"2".equals(type)) {
					POIUtils.setCell(row, alignCellStyle,
							trafficCount.getPeopleCount() == null ? "0" : trafficCount.getPeopleCount() + "", 3);
					if (!"1".equals(type)) {
						POIUtils.setCell(row, alignCellStyle,
								trafficCount.getMotorCount() == null ? "0" : trafficCount.getMotorCount() + "", 4);
					}
				}
				if ("0".equals(type)) {
					POIUtils.setCell(row, alignCellStyle,
							trafficCount.getNonmotorCount() == null ? "0" : trafficCount.getNonmotorCount() + "", 5);
				}
			}
		}
		return workbook;
	}

	@Override
	public SXSSFWorkbook alarmCountToExcelNew(Map<String, Object> params) throws Exception {
		// 创建excel对象
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		try {
			List<Map<String, Object>> alarmCountList = new ArrayList<Map<String, Object>>();
			JSONArray ja = null;
			Object dataObj = params.get("data");
			if (dataObj != null && !"".equals(dataObj.toString())) {
				ja = JSONArray.parseArray(dataObj.toString());
				if (ja != null && ja.size() > 0) {
					for (int i = 0; i < ja.size(); i++) {
						JSONObject jo = JSONObject.parseObject(ja.get(i).toString());
						Map<String, Object> map = new HashMap<String, Object>();

						map.put("date", jo.getString("date"));
						map.put("alarmCount", jo.getInteger("alarmCount"));

						alarmCountList.add(map);
					}
				}
			}

			// 创建excel表单
			SXSSFSheet sheet = workbook.createSheet("表格1");

			// 设置表头样式
			Font topFont = POIUtils.getFont(workbook, "宋体", true, 11);
			CellStyle topCellStyle = POIUtils.getCellStyle(workbook, topFont, 2, 2, "0000");

			// 设置样式
			Font font = POIUtils.getFont(workbook, "宋体", false, 11);
			CellStyle alignCellStyle = POIUtils.getCellStyle(workbook, font, 2, 2, "0000");
			CellStyle leftCellStyle = POIUtils.getCellStyle(workbook, font, 1, 2, "0000");
			// 设置文本自动换行
			alignCellStyle.setWrapText(true);
			leftCellStyle.setWrapText(true);

			// 第一行
			SXSSFRow hssfRow0 = sheet.createRow(0);
			CellRangeAddress region = new CellRangeAddress(0, 0, 1, 3);// 下标从0开始 起始行号，终止行号， 起始列号，终止列号
			sheet.addMergedRegion(region);
//						"endDate":"2018-12-28 17:19:29","startDate":"2018-12-22 00:00:00
			String startDate = (String) params.get("startDate");
			String endDate = (String) params.get("endDate");
			Integer date = (Integer) params.get("date");
			if (date != null) {
				if ("1".equals(date.toString())) {
					POIUtils.setCell(hssfRow0, topCellStyle, "统计时间：今天", 2);
				} else if ("-1".equals(date.toString())) {
					POIUtils.setCell(hssfRow0, topCellStyle, "统计时间：昨天", 2);
				} else if ("-7".equals(date.toString())) {
					POIUtils.setCell(hssfRow0, topCellStyle, "统计时间：最近7天", 2);
				} else if ("-30".equals(date.toString())) {
					POIUtils.setCell(hssfRow0, topCellStyle, "统计时间：最近30天", 2);
				} else {
					POIUtils.setCell(hssfRow0, topCellStyle, "统计时间：最近7天", 2);
				}
			}
			if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
				POIUtils.setCell(hssfRow0, topCellStyle, "统计时间：" + startDate + "至" + endDate, 2);
			}
			// 第二行
			SXSSFRow hssfRow1 = sheet.createRow(1);

			if (alarmCountList != null && alarmCountList.size() > 0) {

				POIUtils.setColumnWidth(sheet, new Integer[] { 5, 20, 15, 15, 15 });

				POIUtils.setCell(hssfRow1, topCellStyle, "序号", 1);
				POIUtils.setCell(hssfRow1, topCellStyle, "日期", 2);
				POIUtils.setCell(hssfRow1, topCellStyle, "报警类型", 3);
				POIUtils.setCell(hssfRow1, topCellStyle, "报警数量", 4);

				for (int i = 0; i < alarmCountList.size(); i++) {
					Map<String, Object> rowDataMap = alarmCountList.get(i);
					SXSSFRow row = sheet.createRow((i + 2));

					POIUtils.setCell(row, alignCellStyle, (i + 1) + "", 1);
					POIUtils.setCell(row, alignCellStyle, rowDataMap.get("date").toString(), 2);
					POIUtils.setCell(row, alignCellStyle, "机动车", 3);
					POIUtils.setCell(row, alignCellStyle, rowDataMap.get("alarmCount").toString(), 4);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workbook;
	}

}
