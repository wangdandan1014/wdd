package com.sensing.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sensing.core.utils.Pager;

/**
 * 
 * <p>
 * Title: IDataOverviewDAO
 * </p>
 * <p>
 * Description:数据统计
 * </p>
 * <p>
 * Company: www.sensingtech.com
 * </p>
 * 
 * @author mingxingyu
 * @date 2018年12月5日
 * @version 1.0
 */
public interface IDataOverviewDAO {

	/**
	 * 根据时间查找告警的数量
	 * 
	 * @param sqlParams
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2018年12月5日 上午10:44:06
	 */
	public List<Map<String, Object>> alarmCount(Map<String, Object> sqlParams) throws Exception;

	public List<Map<String, Object>> alarmTypeStatistics(Map<String, Object> sqlParams) throws Exception;

	public List<Map<String, Object>> regionAlarmStatistics(Map<String, Object> sqlParams) throws Exception;

	public int alarmStatistics(Map<String, Object> sqlParams) throws Exception;

	public int alarmStatisticsConfirmed(Map<String, Object> sqlParams) throws Exception;

	public List<Map<String, Object>> alarmCountByDay(Map<String, Object> sqlParams) throws Exception;

}
