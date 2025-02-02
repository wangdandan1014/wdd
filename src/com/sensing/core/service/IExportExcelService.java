package com.sensing.core.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.sensing.core.bean.MotorVehicle;
import com.sensing.core.bean.alarm.resp.AlarmResp;
import com.sensing.core.bean.result.TrafficCount;
import com.sensing.core.resp.CapResp;
import com.sensing.core.utils.Pager;

public interface IExportExcelService {

	/**
	 * 将统计的告警数据导出到excel
	 * 
	 * @param pagerParams
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2018年12月12日 上午9:57:51
	 */
	public SXSSFWorkbook alarmCountToExcel(Map<String, Object> params) throws Exception;

	SXSSFWorkbook exportSearchInfoToExcel(Integer capType, List<CapResp> list, int type, String exportType)
			throws Exception;

	SXSSFWorkbook exporttrafficCountInfoToExcel(List<Map<String, Object>> mList);

	/**
	 * 日志信息导出到excel
	 * 
	 * @param pagerParams 查询条件
	 * @return
	 * @author mingxingyu
	 * @date 2018年11月16日 下午4:50:06
	 */
	public SXSSFWorkbook logExportToExcel(Pager pagerParams);

	SXSSFWorkbook exportAlarmData(List<AlarmResp> list, List<MotorVehicle> motorList, Integer type);

	public SXSSFWorkbook exporttrafficCount(Map<String, Object> map, List<TrafficCount> tcList, String type);

	public SXSSFWorkbook alarmCountToExcelNew(Map<String, Object> params) throws Exception;

}
