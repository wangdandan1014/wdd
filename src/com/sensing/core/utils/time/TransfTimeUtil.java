package com.sensing.core.utils.time;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * <p>Title: TransfTimeUtil</p>
 * <p>Description: 日期格式转换类</p>
 * <p>Company: www.sensingtech.com</p>
 * 
 * @author haowenfeng
 * @date 2017年8月9日下午5:15:19
 * @version 1.0
 */
public class TransfTimeUtil implements Serializable {

	private static final Log log = LogFactory.getLog(TransfTimeUtil.class);

	/**
	 * 
	 * @author haowenfeng
	 * @date 2017年11月2日 下午5:51:31
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Java将Unix时间戳(精确到秒)转换成指定格式日期字符串
	 * 
	 * @param timestampString 时间戳 如："1473048265";
	 * @param formats         要格式化的格式 默认："yyyy-MM-dd HH:mm:ss";
	 *
	 * @return 返回结果 如："2017-07-12 16:06:42";
	 */
	public static String UnixTimeStamp2Date(long timestamp, DateStyle dateStyle) {
		String strDate = null;
		if (dateStyle != null) {
			timestamp = timestamp * 1000;
			strDate = new SimpleDateFormat(dateStyle.getValue(), Locale.CHINA).format(new Date(timestamp));
		}
		return strDate;
	}
	
	public static String getHMS(int sec) {
		int hour = sec / 3600;
		int min = (sec % 3600) / 60;
		int second = sec - hour * 3600 - min * 60;
		String time = (hour < 10 ? ("0" + hour) : hour) + ":" + (min < 10 ? ("0" + min) : min) + ":"
				+ (second < 10 ? ("0" + second) : second);
		return time;
	}
	/**
	 * 类型为HH:mm:ss时，去掉八小时时差
	 * 
	 * @param timestamp
	 * @param dateStyle
	 * @return
	 */
//	public static String UnixTimeStamp2DateWithTimeZone(long timestamp, DateStyle dateStyle) {
//		TimeZone tz = TimeZone.getTimeZone("ETC/GMT-8");
//		TimeZone.setDefault(tz);
//		String strDate = null;
//		if (dateStyle != null) {
//			timestamp = timestamp * 1000;
//			strDate = new SimpleDateFormat(dateStyle.getValue(), Locale.CHINA).format(new Date(timestamp));
//		}
//		return strDate;
//	}

	/**
	 * Java将Unix时间戳(精确到毫秒)转换成指定格式日期字符串
	 * 
	 * @param timestamp 时间戳 如："1503452839453";
	 * @param formats   要格式化的格式 默认："yyyy-MM-dd HH:mm:ss";
	 * @return 返回结果 如："2017年08月23日 09:47:19";
	 */
	public static String UnixTimeStampMilli2Date(long timestamp, DateStyle dateStyle) {
		String strDate = null;
		if (dateStyle != null) {
			strDate = new SimpleDateFormat(dateStyle.getValue(), Locale.CHINA).format(timestamp);
		}
		return strDate;
	}

	/**
	 * 
	 * <p>
	 * Title: TimeStamp2Date
	 * </p>
	 * <p>
	 * Description:Java将系统时间戳转换成指定格式日期字符串
	 * </p>
	 * 
	 * @param timestampString 时间戳 如："1473048265";
	 * @param dateStyle       要格式化的格式 默认："yyyy-MM-dd HH:mm:ss";
	 * @return 返回结果 如："2017-07-12 17:49:03";
	 */
	public static String TimeStamp2Date(long timestamp, DateStyle dateStyle) {
		String strDate = null;
		if (dateStyle != null) {
			strDate = new SimpleDateFormat(dateStyle.getValue(), Locale.CHINA).format(new Date(timestamp));

		}
		return strDate;
	}

	/**
	 * 日期格式字符串转换成时间戳
	 *
	 * @param dateStr 字符串日期
	 * @param format  如：yyyy-MM-dd HH:mm:ss
	 *
	 * @return 时间戳 (精确到秒)
	 */
	public static String Date2TimeStampReturnString(String dateStr, DateStyle dateStyle) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateStyle.getValue());
			return String.valueOf(sdf.parse(dateStr).getTime() / 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 日期格式字符串转换成时间戳
	 *
	 * @param dateStr 字符串日期
	 * @param format  如：yyyy-MM-dd HH:mm:ss
	 *
	 * @return 时间戳 (精确到秒)
	 */
	public static Long Date2TimeStampReturnLong(String dateStr, DateStyle dateStyle) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateStyle.getValue());
			return Long.parseLong(String.valueOf(sdf.parse(dateStr).getTime() / 1000));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0L;
	}

	/**
	 * 日期格式字符串转换成时间戳
	 *
	 * @param dateStr 字符串日期
	 * @param format  如：yyyy-MM-dd HH:mm:ss
	 *
	 * @return 时间戳 (精确到毫秒)
	 */
	public static Long Date2TimeStampMilliReturnLong(String dateStr, DateStyle dateStyle) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateStyle.getValue());
			return Long.parseLong(String.valueOf(sdf.parse(dateStr).getTime()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0L;
	}

	/**
	 * 
	 * <p>
	 * Title: TimeStamp2Date
	 * </p>
	 * <p>
	 * Description:Java将字符串时间格式转换成另一种字符串格式
	 * </p>
	 * 
	 * @param dateStr      字符串时间 如："2017-07-12 17:49";
	 * @param oldDateStyle 旧的时间格式 如 "yyyy-MM-dd HH:mm"
	 * @param newDateStyle 旧的时间格式 如"yyyy-MM-dd HH:mm:ss"
	 * @return 返回结果 如："2017-07-12 17:49:00";
	 */
	public static String TimeStamp2DateByString(String dateStr, DateStyle oldDateStyle, DateStyle newDateStyle)
			throws Exception {
		String strDate = null;
		if (dateStr != null) {
			Long datelong = Date2TimeStampMilliReturnLong(dateStr, oldDateStyle);
			if (datelong == 0L) {
				throw new Exception("时间格式错误！");
			}
			strDate = TimeStamp2Date(datelong, newDateStyle);

		} else {
			throw new Exception("时间为空！");
		}
		return strDate;
	}

	/**
	 * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param dateDate
	 * @return
	 */
	public static String dateToStrLong(java.util.Date dateDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(dateDate);
		return dateString;
	}

	/**
	 * 
	 * 获取接口耗时
	 * 
	 * @param startTime 开始毫秒值
	 * @param endTime   结束毫秒值
	 * @param describe  描述
	 * @return
	 */
	public static Long getInterfaceUsedTime(Long startTime, Long endTime, String describe) {
		Long waste = endTime - startTime;
		log.info(describe + " " + waste + " ms");
		return waste;
	}

	/**
	 * 标准化时间显示 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param dateStr 例如 "Wed Jul 25 00:00:00 CST 2018"
	 * @return 2018-07-25 00:00:00
	 */
	public static String formatDateToStr(String dateStr) {
		String[] aStrings = dateStr.split(" ");
		// 5
		if (aStrings[1].equals("Jan")) {
			aStrings[1] = "01";
		}
		if (aStrings[1].equals("Feb")) {
			aStrings[1] = "02";
		}
		if (aStrings[1].equals("Mar")) {
			aStrings[1] = "03";
		}
		if (aStrings[1].equals("Apr")) {
			aStrings[1] = "04";
		}
		if (aStrings[1].equals("May")) {
			aStrings[1] = "05";
		}
		if (aStrings[1].equals("Jun")) {
			aStrings[1] = "06";
		}
		if (aStrings[1].equals("Jul")) {
			aStrings[1] = "07";
		}
		if (aStrings[1].equals("Aug")) {
			aStrings[1] = "08";
		}
		if (aStrings[1].equals("Sep")) {
			aStrings[1] = "09";
		}
		if (aStrings[1].equals("Oct")) {
			aStrings[1] = "10";
		}
		if (aStrings[1].equals("Nov")) {
			aStrings[1] = "11";
		}
		if (aStrings[1].equals("Dec")) {
			aStrings[1] = "12";
		}
		return aStrings[5] + "-" + aStrings[1] + "-" + aStrings[2] + " " + aStrings[3];
	}

}