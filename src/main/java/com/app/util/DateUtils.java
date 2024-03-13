package com.app.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * 静态工具方法，日期操作
 * 
 * @author chymilk
 * @version 1.0.0 Creation date:Aug 22, 2008 11:26:35 PM
 */
@Slf4j
public class DateUtils {

	public final static String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public final static String FORMAT_14 = "yyyyMMddHHmmss";

	public final static String FORMAT_8 = "yyyyMMdd";

	public final static String FORMAT_10 = "yyyy-MM-dd";

	public final static String FORMAT_6 = "HHmmss";

	public final static String FORMAT_28 = "EEE MMM dd HH:mm:ss zzz yyyy";


	public static String getCurrentTime() {
		return new SimpleDateFormat(DEFAULT_FORMAT).format(new Date());
	}

	/**
	 * 获得当前时间的14位字符串（格式：yyyyMMddHHmmss）
	 *
	 * @return 当前时间14位字符串
	 */
	public static String getCurrentTimeAs14String() {
		return new SimpleDateFormat(FORMAT_14).format(new Date());
	}
	public static String getCurrentTimeAs10String() {
		return new SimpleDateFormat(FORMAT_10).format(new Date());
	}

	public static String getCurrentTimeAs6String() {
		return new SimpleDateFormat(FORMAT_6).format(new Date());
	}
	public static String getCurrentTimeAs8String() {
		return new SimpleDateFormat(FORMAT_8).format(new Date());
	}
	public static String getCurrentTimeAsString(String formatStr) {
		return new SimpleDateFormat(formatStr).format(new Date());
	}
	/**
	 * @Title: compareStrDate
	 * @Description: TODO(描述)  比较两个时间字符串大小
	 * @param dateOne
	 * @return
	 * @author author
	 * @date 2019-10-31 05:02:55
	 */
	public static Boolean compareStrDate(String dateOne) {
		DateFormat dateFormat=new SimpleDateFormat(DEFAULT_FORMAT);
		try {
			Date one = dateFormat.parse(dateOne);
			Date two = new Date();
			if(one.before(two)) {
				return true;
			}else if(one.equals(two)||one.after(two)) {
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String changeString(String str,String formatStr,String toformat){
		if("".equals(str)||str==null){
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat(formatStr, Locale.US);
		Date date = new Date();
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(toformat);
		return simpleDateFormat.format(date);
	}

	public static Date strToDate(String str,String formatStr) {
		if(StringUtils.isEmpty(str)||StringUtils.isEmpty(formatStr)){
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	public static String timeToString(String time,String formatStr) {
		if(StringUtils.isEmpty(time)||StringUtils.isEmpty(formatStr)){
			return null;
		}
		Date date = new Date();
		date.setTime(Long.valueOf(time));
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
		return simpleDateFormat.format(date);
	}

	public static String timeProcess(String dateOne,Integer amount) {
		try {
			DateFormat dateFormat=new SimpleDateFormat(DEFAULT_FORMAT);
			Date date = dateFormat.parse(dateOne);
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(date);
			/*减10分钟*/
			rightNow.add(Calendar.MINUTE, amount);
			/*rightNow.add(Calendar.HOUR,1);//加一个小时
			rightNow.add(Calendar.HOUR,14);//加14个小时，测试能不能加到明天，能不能加到下个月（结果是都可以,而且不受30天、31天的影响）
			rightNow.add(Calendar.DAY_OF_MONTH,1);//加一天
			rightNow.add(Calendar.MONTH,1);//加一个月*/
			return new SimpleDateFormat(DEFAULT_FORMAT).format(rightNow.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}
	public static void main(String[] args) {
		System.out.println(DateUtils.timeProcess("2023-03-27 21:35:00",-15));

	}

}
