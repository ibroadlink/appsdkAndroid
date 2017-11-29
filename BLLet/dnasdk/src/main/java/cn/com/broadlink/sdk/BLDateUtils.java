package cn.com.broadlink.sdk;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;


/**
 * 
 * 项目名称：BLEControlAppV4 <br>
 * 类名称：BLDateUtils <br>
 * 类描述：时间类 <br>
 * 创建人：YeJing <br>
 * 创建时间：2015-4-14 下午5:06:24 <br>
 * 修改人：Administrator <br>
 * 修改时间：2015-4-14 下午5:06:24 <br>
 * 修改备注：
 * 
 */
@SuppressLint("SimpleDateFormat")
public class BLDateUtils {

    /**
     * 将时间组合为HHmmss格式
     * @param hour
     * @param min
     * @param sec
     * @return
     */
     public static String formatDate(int hour, int min, int sec){
         return String.format("%02d%02d%02d", hour, min, sec);
     }
     
     /**
      * 将时间组合为yyyyMMdd-HHmmss格式
      * @param milliseconds
      *         long型时间
      * @return
      */
      public static String formatDate(long milliseconds){
          Date date = new Date(milliseconds);
          return String.format("%04d%02d%02d-%02d%02d%02d", (date.getYear() + 1900),
                  (date.getMonth() + 1), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
      }
      
   /**
    * 将时间组合为yyyy-MM-dd HH:mm:sss格式
    * @param year
    * @param month
    * @param day
    * @param hour
    * @param min
    * @param sec
    * @return
    */
    public static String formatDate(int year, int month, int day, int hour, int min, int sec){
        return String.format("%04d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, min, sec);
    }
    
    /**
     * 将日期格式格式转为Date【yyyy-MM-dd HH:mm:sss】
     * 
     * @param format
     *          时间格式
     * @return
     */
    public static Date strToDate(String dataString, String format){
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            return formatter.parse(dataString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    /**
     * 将日期格式 yyyy-MM-dd HH:mm:sss 格式转为Date
     * 
     * @param dataString
     *          yyyy-MM-dd HH:mm:sss 格式
     * @return
     */
    public static Date strToYearDate(String dataString){
        return strToDate(dataString, "yyyy-MM-dd HH:mm:sss");
    }
    
    /**
     * 描述：Date类型转化为String类型.
     * 
     * @param date
     *            the date
     * @param format
     *            the format
     * @return String String类型日期时间
     */
    public static String getStringByFormat(Date date, String format) {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
        String strDate = null;
        try {
            strDate = mSimpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }

    /**
     * 描述：获取milliseconds表示的日期时间的字符串.
     * 
     * @param milliseconds
     *            the milliseconds
     * @param format
     *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return String 日期时间字符串
     */
    public static String getStringByFormat(long milliseconds, String format) {
        String thisDateTime = null;
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            thisDateTime = mSimpleDateFormat.format(milliseconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thisDateTime;
    }

    /**
     * 描述：获取表示当前日期时间的字符串.
     * 
     * @param format
     *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return String String类型的当前日期时间
     */
    public static String getCurrentDate(String format) {
        String curDateTime = null;
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            Calendar c = new GregorianCalendar();
            curDateTime = mSimpleDateFormat.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return curDateTime;

    }

    /**
     * 描述：判断是否是闰年()
     * <p>
     * (year能被4整除 并且 不能被100整除) 或者 year能被400整除,则该年为闰年.
     * 
     * @param year
     *            年代（如2012）
     * @return boolean 是否为闰年
     */
    public static boolean isLeapYear(int year) {
        if ((year % 4 == 0 && year % 400 != 0) || year % 400 == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将时间分秒转为HH : NN
     * 
     * @param hour
     * @param min
     * @return
     */
    public static String toTime(int hour, int min) {
        return String.format("%02d:%02d", hour, min);
    }

    /**
     * 将时间分秒转为HH : NN
     * 
     * @param hour
     * @param min
     * @param second
     * @return
     */
    public static String toTime(int hour, int min, int second) {
        return String.format("%02d:%02d:%02d", hour, min, second);
    }

    /**
     * 
     * @return 当前手机年
     * 
     */
    public static final int getCurrrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * 
     * @return 当前手机月份
     * 
     */
    public static final int getCurrrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    /**
     *
     * @return 获取当前的日期
     * 
     */
    public static final int getCurrrentDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 
     * @return 当前手机Hour 0-24
     * 
     */
    public static final int getCurrrentHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 
     * @return 当前手机Minute
     * 
     */
    public static final int getCurrrentMin() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    /**
     * 
     * @return 当前手机Second
     * 
     */
    public static final int getCurrrentSeconds() {
        return Calendar.getInstance().get(Calendar.SECOND);
    }

    /**
     * 
     * 将时间转为long型
     * 
     * @param year
     *            年
     * @param month
     *            月
     * @param date
     *            日
     * @param hour
     *            小时
     * @param min
     *            分钟
     * @param sec
     *            秒
     * @return millisecond
     * 
     */
    public static final long dateToMillis(int year, int month, int date, int hour, int min, int sec) {
        return dateToMillis(String.format("%d-%d-%d %d:%d:%d", year, month, date, hour, min, sec));
    }

    /**
     * 将时间转为long型
     * 
     * @param data
     *            日期格式yyyy-MM-dd HH:mm:ss
     * @return millisecond
     */
    public static final long dateToMillisyyyyMMdd_HHmmss(String data) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
            return df.parse(data).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }
    
    /**
     * 将时间转为long型
     * 
     * @param data
     *            日期格式yyyy-MM-dd HH:mm:ss
     * @return millisecond
     */
    public static final long dateToMillis(String data) {
        return dateToMillis("yyyy-MM-dd HH:mm:ss", data);
    }
   
    public static final long dateToMillis(String format, String data) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(format);
            return df.parse(data).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }
    
    /**
     * 
     * 将时间转为long型
     * 
     * @param year
     *            年
     * @param month
     *            月
     * @param day
     *            日
     * @param hour
     *            小时
     * @param min
     *            分钟
     * @param sec
     *            秒
     * @return millisecond
     * 
     */
    public static final long dateToMill(int year, int month, int day, int hour, int min, int sec) {
        return dateToMillis(year, month, day, hour, min, sec);
    }

    /**
     * 
     * 将时间转为long型
     * 
     * @param hour
     *            小时
     * @param min
     *            分钟
     * @param sec
     *            秒
     * @return millisecond
     * 
     */
    public static final long dateToMillis(int hour, int min,  int sec) {
        return dateToMillis(Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH) + 1,
                Calendar.getInstance().get(Calendar.DATE), hour, min, sec);
    }

    /**
     * 将long型时间转为年月日的数据
     * 
     * @param mill
     *            time
     * 
     * @return <p>
     *         int[0] YEAR
     *         <p>
     *         int[1] MONTH
     *         <p>
     *         int[2] DAY_OF_MONTH
     *         <p>
     *         int[3] HOUR
     *         <p>
     *         int[4] MINUTE
     *         <p>
     *         int[5] SECOND
     */
    public static final int[] millsToDateArray(long mill) {
        int[] dateArray = new int[6];
        Date curDate = new Date(mill);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(curDate);

        dateArray[0] = calendar.get(java.util.Calendar.YEAR);
        dateArray[1] = calendar.get(java.util.Calendar.MONTH) + 1;
        dateArray[2] = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        dateArray[3] = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        dateArray[4] = calendar.get(java.util.Calendar.MINUTE);
        dateArray[5] = calendar.get(java.util.Calendar.SECOND);

        return dateArray;
    }

    /**
     * 获取现在是第九月
     * @return
     */
	public static int getMonthByDate() {
		int[] months = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(System.currentTimeMillis()));
		int week_index = cal.get(Calendar.MONTH);
		if (week_index < 0) {
			week_index = 0;
		}
		return months[week_index];
	}
	

    /**
     * 获取当前第几周
     * @param date
     * @return
     */
	public static int getWeek(Date date) {
		GregorianCalendar g = new GregorianCalendar();
		g.setTime(date);
		return g.get(Calendar.WEEK_OF_YEAR);// 获得周数
	}
	
    /**
     * 根据日期取得星期几
     * 
     * @return 0星期天 1星期一 2星期二 3星期三
     */
    public static int getWeek() {
        int[] weeks = { 0, 1, 2, 3, 4, 5, 6 };
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(System.currentTimeMillis()));
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

    /**
     * 将时间转成手机的当前时区的时间
     * 
     * @param hour
     *            小时
     * @param hour
     *            分钟
     * @return String HH:MM
     * 
     */
    public static String changeDateToPhoneDate(int hour, int min) {
        int newHour = 0;
        int newMin = 0;

        TimeZone tz = TimeZone.getDefault();
        int zoneHour = tz.getRawOffset() / 1000 / 3600;
        int zoneMin = tz.getRawOffset() / 1000 / 60 % 60;
        int dif = hour - (8 - zoneHour);

        if (dif < 0) {
            if (zoneMin == 0) {
                newHour = 24 + dif;
                newMin = min;
            } else {
                if (min >= 30) {
                    newHour = 24 + dif;
                    newMin = min - 30;
                } else {
                    newHour = 24 + dif - 1;
                    newMin = min + 60 - 30;
                }
            }
        } else if (dif < 24) {
            if (zoneMin == 0) {
                newHour = dif;
                newMin = min;
            } else if (zoneMin == 45) {
                if (min >= 15) {
                    newHour = dif + 1;
                    newMin = min - 15;
                } else {
                    newHour = dif;
                    newMin = min + 45;
                }
            } else {
                if (min >= 30) {
                    newHour = dif + 1;
                    newMin = min - 30;
                } else {
                    newHour = dif;
                    newMin = min + 30;
                }
            }
        } else {
            if (zoneMin == 0) {
                newHour = dif - 24;
                newMin = min;
            } else if (zoneMin == 45) {
                if (min >= 15) {
                    newHour = dif - 24 + 1;
                    newMin = min - 15;
                } else {
                    newHour = dif - 24;
                    newMin = min + 45;
                }
            } else {
                if (min >= 30) {
                    newHour = dif - 24 + 1;
                    newMin = min - 30;
                } else {
                    newHour = dif - 24;
                    newMin = min + 30;
                }
            }
        }

        return toTime(newHour, newMin);
    }

    public static int[] getNewWeeksFromPhoneToDevice(int[] weeks, int diffDay) {
        int newWeeks[] = new int[7];
        if (diffDay == 0) {
            return weeks;
        } else if (diffDay == 1) {
            for (int i = 0; i < weeks.length; i++) {
                if (i == 0) {
                    newWeeks[i] = weeks[6];
                } else {
                    newWeeks[i] = weeks[i - 1];
                }
            }

        } else if (diffDay == -1) {
            for (int i = 0; i < weeks.length; i++) {
                if (i == 6) {
                    newWeeks[i] = weeks[0];
                } else {
                    newWeeks[i] = weeks[i + 1];
                }
            }
        }
        return newWeeks;
    }

    public static int[] getNewWeeksFromDeviceToPhone(int[] weeks, int diffDay) {
        int newWeeks[] = new int[7];
        if (diffDay == 0) {
            return weeks;
        } else if (diffDay == 1) {
            for (int i = 0; i < weeks.length; i++) {
                if (i == 6) {
                    newWeeks[i] = weeks[0];
                } else {
                    newWeeks[i] = weeks[i + 1];
                }
            }
        } else if (diffDay == -1) {
            for (int i = 0; i < weeks.length; i++) {
                if (i == 0) {
                    newWeeks[i] = weeks[6];
                } else {
                    newWeeks[i] = weeks[i - 1];
                }
            }
        }
        return newWeeks;
    }

    /**
     * 将周期列表进行时间差转化
     * 
     * @param weekList
     *          手机周期列表
     * @param diffDay
     *          周期差
     * @return
     *          服务器周期列表
     */
    public static ArrayList<Integer> weeksDiffDaySwitch(ArrayList<Integer> weekList, int diffDay){
        if(weekList != null && !weekList.isEmpty() && diffDay != 0){
        	ArrayList<Integer> newWeeks = new ArrayList<Integer>();
            for(int i = 0; i < weekList.size(); i ++){
                int week = weekList.get(i);
                week = week + diffDay;
                if(week > 7){
                    week -= 7;
                }else if(week < 1){
                    week += 7;
                }
                
                newWeeks.add(week);
            }
            
            Collections.sort(newWeeks);
            return newWeeks;
        }
        return weekList;
    }
    
    /**
     * 计算某个时间与现在的时间是否是同一天 即星期几是否一样
     * 
     * @param time1
     *            时间1
     * @param time2
     *            时间2
     * @return
     */
    public static int getDiffDay(long time1, long time2) {
        int time1WeekDay = getWeekDayByMill(time1);
        int time2WeekDay = getWeekDayByMill(time2);
        int mDiffDay = time1WeekDay - time2WeekDay;
        return mDiffDay;
    }

    /**
     * 获取当前mill是周几
     * @param mill
     * @return
     * SUNDAY->0<br/>
     * MONDAY->1<br/>
     * TUESDAY->2<br/>
     * WEDNESDAY->3<br/>
     * THURSDAY->4<br/>
     * FRIDAY->5<br/>
     * SATURDAY->6<br/>
     */
    public static int getWeekDayByMill(long mill) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(mill);
        return c.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static String getCurrentTimeZone()
    {
        TimeZone tz = TimeZone.getDefault();
        return createGmtOffsetString(true,true,tz.getRawOffset());
    }
    private static String createGmtOffsetString(boolean includeGmt, boolean includeMinuteSeparator, int offsetMillis) {
        int offsetMinutes = offsetMillis / 60000;
        char sign = '+';
        if (offsetMinutes < 0) {
            sign = '-';
            offsetMinutes = -offsetMinutes;
        }
        StringBuilder builder = new StringBuilder(9);
        if (includeGmt) {
            builder.append("GMT");
        }
        builder.append(sign);
        appendNumber(builder, 2, offsetMinutes / 60);
        if (includeMinuteSeparator) {
            builder.append(':');
        }
        appendNumber(builder, 2, offsetMinutes % 60);
        return builder.toString();
    }

    private static void appendNumber(StringBuilder builder, int count, int value) {
        String string = Integer.toString(value);
        for (int i = 0; i < count - string.length(); i++) {
            builder.append('0');
        }
        builder.append(string);
    }

    /** 
     * 获取更改时区后的时间 
     * @param date 时间 
     * @param oldZone 旧时区 
     * @param newZone 新时区 
     * @return 时间
     */  
    public static Date changeTimeZone(Date date, TimeZone oldZone, TimeZone newZone){
        Date dateTmp = null;
        if (date != null) {
            int timeOffset = oldZone.getRawOffset() - newZone.getRawOffset();
            dateTmp = new Date(date.getTime() - timeOffset);
        }
        return dateTmp;
    }

    /**
     * 得到某年某周的第一天
     *
     * @param year
     * @param week
     * @return
     */
    public static Date getFirstDayOfWeek(int year, int week) {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, year);
        c.set (Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, 1);
        Calendar cal = (GregorianCalendar) c.clone();
        cal.add(Calendar.DATE, week * 7);
        return getFirstDayOfWeek(cal.getTime());
    }

    /**
     * 得到某年某周的最后一天
     *
     * @param year
     * @param week
     * @return
     */
    public static Date getLastDayOfWeek(int year, int week) {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, 1);
        Calendar cal = (GregorianCalendar) c.clone();
        cal.add(Calendar.DATE , week * 7);
        return getLastDayOfWeek(cal.getTime());
    }

    /**
     * 取得指定日期所在周的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
        return c.getTime();
    }


    /**
     * 取得指定日期所在周的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
        return c.getTime ();
    }

}
