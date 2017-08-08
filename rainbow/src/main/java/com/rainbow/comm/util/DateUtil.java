package com.rainbow.comm.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.rainbow.comm.exception.BaseException;


/**
 * 日期工具类
 * @author FANGHUABAO
 *
 */
public class DateUtil {

    public static String DATE_YYYYMMDD="yyyyMMdd";
    
    public static String DATE_YYYY_MM_DD="yyyy-MM-dd";

    public static String DATE_YYMMDD="yyMMdd";

    public static String TIME_HHMMSS="HHmmss";
    
    public static String FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

	public static Date getCurrentTime(){
		return new Date();
	}

    /**
     *获取HHmmss格式时间：如163510
     * @return
     */
    public static String getHHMMSS(){

        return new SimpleDateFormat(TIME_HHMMSS).format(new Date());

    }

    /**
     *获取yyyyMMddHHmmss格式时间：如20170227173752
     * @return
     */
    public static String getTimeStamp(){

        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

    }

    /**
     * 将YYYYMMDD格式的日期改成指定格式的日期
     * @param dateRule
     * @param dateStr
     * @return
     */
    public static String getFormatDateStr(String dateRule,String dateStr){
        try {
            Date date=new SimpleDateFormat(DATE_YYYYMMDD).parse(dateStr);
            return new SimpleDateFormat(dateRule).format(date);
        } catch (ParseException e) {

            throw new BaseException("日期格式转换异常");

        }
    }
	
	/**
	 * 获取当天的8位日期
	 * @return
	 */
	public static String getCurrentDateStr(){
		
		String currentDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		
		return currentDate;
		
	}
	
	/**
	 * 获取下一天的8位日期
	 * @return
	 */
	public static String getNextDateStr(){
		
		return getDateStrByOffset(1);
		
	}
	
	
	/**
	 * 获取上一天的8位日期
	 * @return
	 */
	public static String getLastDateStr(){
		
		return getDateStrByOffset(-1);
		
	}
	
	/**
	 * 以当前日期为基准，根据偏移量，获取偏移后的8位日期
	 * @param offset 偏移天数  支持正负
	 * @return
	 */
	public static String getDateStrByOffset(int offset){
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(new Date());
		
		calendar.add(Calendar.DAY_OF_MONTH, offset);
		
		Date date=new Date(calendar.getTimeInMillis());
		
		return new SimpleDateFormat("yyyyMMdd").format(date);
		
	}
	
	/**
	 * 获取指定日期偏移后的日期
	 * @param dateStr    指定日期
	 * @param offset  偏移天数  支持正负
	 * @return
	 */
	public static String getDateStrByOffset(String dateStr,int offset){
		
		Date date = null;
		try {
			
			date = new SimpleDateFormat("yyyyMMdd").parse(dateStr);
			
		} catch (ParseException e) {
			
			throw new RuntimeException(e.getMessage());
			
		}
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(date);
		
		calendar.add(Calendar.DAY_OF_MONTH, offset);
		
		date=new Date(calendar.getTimeInMillis());
		
		return new SimpleDateFormat("yyyyMMdd").format(date);
		
	}

    /**
     * 将YYYYMMDD格式日期转换为偏移后的指定格式日期
     * @param dateStr    指定日期
     * @param offset  偏移天数  支持正负
     * @return
     */
    public static String getFormatDateStrByOffset(String dateStr,int offset,String dateFormat){

        Date date = null;
        try {

            date = new SimpleDateFormat(DATE_YYYYMMDD).parse(dateStr);

        } catch (ParseException e) {

            throw new RuntimeException(e.getMessage());

        }

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);

        calendar.add(Calendar.DAY_OF_MONTH, offset);

        date=new Date(calendar.getTimeInMillis());

        return new SimpleDateFormat(dateFormat).format(date);

    }


    /**
     * formatDateFromStr:字符串转换成指定格式的日期. <br/>
     *
     * @param str
     * @param format
     * @return
     */
    public static Date formatDateFromStr(String dateStr, String format) {
    	if (StringUtils.isEmpty(dateStr)) {
    		return null;
    	}
    	if (StringUtils.isEmpty(format)) {
			format = FORMAT_YYYYMMDDHHMMSS;
		}
    	try {
    		DateFormat dateFormat = new SimpleDateFormat(format);
    		Date descDate = dateFormat.parse(dateStr);
        	
    		// 转换前后值不等 则认为格式不符合要求
        	if (!dateStr.equals(dateFormat.format(descDate))) {
        		throw new RuntimeException("时间格式转换异常");
			}
			return descDate;
		} catch (ParseException e) {
			throw new RuntimeException("时间格式转换异常", e);
		}
    }
    
    /**
     * formatDateFromStr:字符串转换成指定格式的日期. <br/>
     *
     * @param str
     * @return
     */
    public static Date formatDateFromStr(String dateStr) {
    	return formatDateFromStr(dateStr, null);
    }
    
    /**
     * formatDate:日期类型转换成yyyyMMddHHmmss格式字符串. <br/>
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
    	if (date == null) {
    		return null;
    	}
    	
    	DateFormat dateFormat = new SimpleDateFormat(FORMAT_YYYYMMDDHHMMSS);
    	return dateFormat.format(date);
    }

	
    public static void main2(String[] args) {
		
		System.out.println(getDateStrByOffset(15));
		
	}
	
	

}
