package com.deloitte.bdh.common.date;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期工具类，用于日期时间与字符串之间的转换
 *
 * @author dahpeng
 */
public class DateUtils {

    /**
     * 格式：yyyy-MM-dd HH:mm:ss.SSS
     */
    public static final String FULL_STANDARD_DATE_TIME = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    public static final String STANDARD_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    /**
     * 格式：yyyy-MM-dd HH:mm
     */
    public static final String INCOMPLETE_DATE_TIME = "yyyy-MM-dd HH:mm";

    /**
     * 格式：yyyyMMddHHmmssSSS
     */
    public static final String FULL_SHORT_DATE_TIME = "yyyyMMddHHmmssSSS";

    /**
     * 格式：yyyyMMddHHmmss
     */
    public static final String SHORT_DATE_TIME = "yyyyMMddHHmmss";

    /**
     * 格式：yyyy-MM-dd
     */
    public static final String STANDARD_DATE = "yyyy-MM-dd";

    /**
     * 格式：yyyyMMdd
     */
    public static final String SHORT_DATE = "yyyyMMdd";

    /**
     * 格式：MMdd
     */
    public static final String SHORT_MMDD = "MMdd";

    /**
     * 格式：HHmmss
     */
    public static final String SHORT_TIME = "HHmmss";

    /**
     * 格式: yyyy-MM-dd'T'HH:mm:ss'Z'
     */
    public static final String UTC_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private static DateFormatThreadLocal fullStandardDateTime = new DateFormatThreadLocal(
            FULL_STANDARD_DATE_TIME);

    private static DateFormatThreadLocal standardDateTime = new DateFormatThreadLocal(
            STANDARD_DATE_TIME);

    private static DateFormatThreadLocal incompleteDateTime = new DateFormatThreadLocal(
            INCOMPLETE_DATE_TIME);

    private static DateFormatThreadLocal fullShortDateTime = new DateFormatThreadLocal(
            FULL_SHORT_DATE_TIME);

    private static DateFormatThreadLocal shortDateTime = new DateFormatThreadLocal(SHORT_DATE_TIME);

    private static DateFormatThreadLocal standardDate = new DateFormatThreadLocal(STANDARD_DATE);

    private static DateFormatThreadLocal shortDate = new DateFormatThreadLocal(SHORT_DATE);

    private static DateFormatThreadLocal shortMMDD = new DateFormatThreadLocal(SHORT_MMDD);

    private static DateFormatThreadLocal shortTime = new DateFormatThreadLocal(SHORT_TIME);

    private static DateFormatThreadLocal utcDateTime = new DateFormatThreadLocal(UTC_DATE_TIME);

    private DateUtils() {
    }

    /**
     * 格式化日期时间（格式：yyyy-MM-dd HH:mm:ss.SSS）
     *
     * @param date 日期时间
     * @return 日期时间字符串
     */
    public static String formatFullStandardDateTime(Date date) {
        return fullStandardDateTime.get().format(date);
    }

    /**
     * 解析日期时间字符串（格式：yyyy-MM-dd HH:mm:ss.SSS）
     *
     * @param dateTime 日期时间字符串
     * @return 日期时间
     */
    public static Date parseFullStandardDateTime(String dateTime) throws ParseException {
        return fullStandardDateTime.get().parse(dateTime);
    }

    /**
     * 格式化日期时间（格式：yyyy-MM-dd HH:mm:ss）
     *
     * @param date 日期时间
     * @return 日期时间字符串
     */
    public static String formatStandardDateTime(Date date) {
        return standardDateTime.get().format(date);
    }

    /**
     * 解析日期时间字符串（格式：yyyy-MM-dd HH:mm:ss）
     *
     * @param dateTime 日期时间字符串
     * @return 日期时间
     */
    public static Date parseStandardDateTime(String dateTime) throws ParseException {
        return standardDateTime.get().parse(dateTime);
    }

    /**
     * 格式化日期时间字符串（格式：yyyy-MM-dd HH:mm）
     *
     * @param date 日期时间
     * @return 日期时间字符串
     */
    public static String formatIncompleteDateTime(Date date) {
        return incompleteDateTime.get().format(date);
    }

    /**
     * 解析日期时间字符串（格式：yyyy-MM-dd HH:mm）
     *
     * @param dateTime 日期时间字符串
     * @return 日期时间
     */
    public static Date parseIncompleteDateTime(String dateTime) throws ParseException {
        return incompleteDateTime.get().parse(dateTime);
    }

    /**
     * 格式化日期时间字符串（格式：yyyyMMddHHmmssSSS）
     *
     * @param date 日期时间
     * @return 日期时间字符串
     */
    public static String formatFullShortDateTime(Date date) {
        return fullShortDateTime.get().format(date);
    }

    /**
     * 解析日期时间字符串（格式：yyyyMMddHHmmssSSS）
     *
     * @param dateTime 日期时间字符串
     * @return 日期时间
     */
    public static Date parseFullShortDateTime(String dateTime) throws ParseException {
        return fullShortDateTime.get().parse(dateTime);
    }

    /**
     * 格式化日期时间（格式：yyyyMMddHHmmss）
     *
     * @param date 日期时间
     * @return 日期时间字符串
     */
    public static String formatShortDateTime(Date date) {
        return shortDateTime.get().format(date);
    }

    /**
     * 解析日期时间字符串（格式：yyyyMMddHHmmss）
     *
     * @param dateTime 日期时间字符串
     * @return 日期时间
     */
    public static Date parseShortDateTime(String dateTime) throws ParseException {
        return shortDateTime.get().parse(dateTime);
    }

    /**
     * 格式化日期时间（格式：yyyyMMddHHmmss）
     *
     * @param date 日期时间
     * @param zone 时区
     * @return 日期时间字符串
     */
    public static String formatShortDateTime(Date date, TimeZone zone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SHORT_DATE_TIME);
        dateFormat.setTimeZone(zone);
        return dateFormat.format(date);
    }

    /**
     * 解析日期时间字符串（格式：yyyyMMddHHmmss）
     *
     * @param dateTime 日期时间字符串
     * @param zone     时区
     * @return 日期时间
     */
    public static Date parseShortDateTime(String dateTime, TimeZone zone) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SHORT_DATE_TIME);
        dateFormat.setTimeZone(zone);
        return dateFormat.parse(dateTime);
    }

    /**
     * 格式化日期（格式：yyyy-MM-dd）
     *
     * @param date 日期
     * @return 日期字符串
     */
    public static String formatStandardDate(Date date) {
        return standardDate.get().format(date);
    }

    /**
     * 解析日期字符串（格式：yyyy-MM-dd）
     *
     * @param date 日期字符串
     * @return 日期
     */
    public static Date parseStandardDate(String date) throws ParseException {
        return standardDate.get().parse(date);
    }

    /**
     * 格式化日期（格式：yyyyMMdd）
     *
     * @param date 日期
     * @return 日期字符串
     */
    public static String formatShortDate(Date date) {
        return shortDate.get().format(date);
    }

    /**
     * 解析日期字符串（格式：yyyyMMdd）
     *
     * @param date 日期字符串
     * @return 日期
     */
    public static Date parseShortDate(String date) throws ParseException {
        return shortDate.get().parse(date);
    }

    /**
     * 格式化日期（格式：yyyyMMdd）
     *
     * @param date 日期
     * @param zone 时区
     * @return 日期字符串
     */
    public static String formatShortDate(Date date, TimeZone zone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SHORT_DATE);
        dateFormat.setTimeZone(zone);
        return dateFormat.format(date);
    }

    /**
     * 解析日期字符串（格式：yyyyMMdd）
     *
     * @param date 日期字符串
     * @param zone 时区
     * @return 日期
     */
    public static Date parseShortDate(String date, TimeZone zone) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SHORT_DATE);
        dateFormat.setTimeZone(zone);
        return dateFormat.parse(date);
    }

    /**
     * 字符串转成日期
     *
     * @param cur 日期字符串
     * @param fm  format格式
     * @return
     */
    public static Date stringToDate(String cur, String fm) {
        if (StringUtils.isEmpty(cur))
            return null;

        if (StringUtils.isEmpty(fm))
            fm = FULL_STANDARD_DATE_TIME;

        try {
            return new SimpleDateFormat(fm).parse(cur);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 格式化日期（格式：MMdd）
     *
     * @param date 日期
     * @return 日期字符串
     */
    public static String formatShortMMDDDate(Date date) {
        return shortMMDD.get().format(date);
    }

    /**
     * 解析日期字符串（格式：MMdd）
     *
     * @param date 日期字符串
     * @return 日期
     */
    public static Date parseShortMMDDDate(String date) throws ParseException {
        return shortMMDD.get().parse(date);
    }

    /**
     * 格式化时间（格式：HHmmss）
     *
     * @param time 时间
     * @return 时间字符串
     */
    public static String formatShortTime(Date time) {
        return shortTime.get().format(time);
    }

    /**
     * 解析时间字符串（格式：HHmmss）
     *
     * @param time 时间字符串
     * @return 时间
     */
    public static Date parseShortTime(String time) throws ParseException {
        return shortTime.get().parse(time);
    }

    /**
     * 格式化日期时间（格式: yyyy-MM-dd'T'HH:mm:ss'Z'），0时区。
     *
     * @param dateTime 日期时间
     * @return 日期时间字符串
     */
    public static String formatUTCDateTime(Date dateTime) {
        utcDateTime.get().setTimeZone(TimeZone.getTimeZone("GMT-0:00"));
        return utcDateTime.get().format(dateTime);
    }

    /**
     * 解析日期时间字符串（格式: yyyy-MM-dd'T'HH:mm:ss'Z'），0时区。
     *
     * @param dateTime 日期时间字符串
     * @return 日期时间
     */
    public static Date parseUTCDateTime(String dateTime) throws ParseException {
        utcDateTime.get().setTimeZone(TimeZone.getTimeZone("GMT-0:00"));
        return utcDateTime.get().parse(dateTime);
    }

    /**
     * 获取传入日期的0时0分0秒
     *
     * @param date 日期
     * @return 日期的0时0分0秒
     */
    public static Date getStartTimeOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取传入日期的23时59分59秒
     *
     * @param date 日期
     * @return 日期的23时59分59秒
     */
    public static Date getEndTimeOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * 获取传入日期之前或之后的日期
     *
     * @param date   日期
     * @param amount 之前或之后的天数，负数为之前，正数为之后。
     * @return 之前或之后的日期
     */
    public static Date getPreviousOrNextDate(Date date, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, amount);
        return cal.getTime();
    }

    public static String getLastOfDay(int amount) {
        return formatStandardDateTime(getStartTimeOfDay(addDays(new Date(), amount)));
    }


    public static Date addDays(Date date, int amount) {
        return add(date, 5, amount);
    }

    public static String stampToDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(STANDARD_DATE_TIME);
        String time_Date = sdf.format(new Date(Long.parseLong(time)));
        return time_Date;
    }

    public static String stampToDateOfYear(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String time_Date = sdf.format(new Date(Long.parseLong(time)));
        return time_Date;
    }

    public static String stampToDateOfYear(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String time_Date = sdf.format(date);
        return time_Date;
    }

    private static Date add(Date date, int calendarField, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(calendarField, amount);
            return c.getTime();
        }
    }


    /**
     * 动态解析日期格式
     *
     * @param date 日期字符串
     * @return 日期
     */
    public static Date parseDateDynamic(String date) throws ParseException {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        boolean simple = true;

        if (date.indexOf("-") > 0) {
            String[] args = date.split("-");
            if (args.length != 3) {
                throw new IllegalArgumentException("Incorrect format");
            }
            if (args[0].length() <= 2) {
                date = args[2] + "-" + args[1] + "-" + args[0];
            }
            simple = false;
        }
        if (date.indexOf("/") > 0) {
            String[] args = date.split("/");
            if (args.length != 3) {
                throw new IllegalArgumentException("Incorrect format");
            }
            if (args[0].length() <= 2) {
                date = args[2] + "-" + args[1] + "-" + args[0];
            } else {
                date = args[0] + "-" + args[1] + "-" + args[2];
            }
            simple = false;
        }
        if (date.indexOf(".") > 0) {
            String[] args = date.split("\\.");
            if (args.length != 3) {
                throw new IllegalArgumentException("Incorrect format");
            }
            if (args[0].length() <= 2) {
                date = args[2] + "-" + args[1] + "-" + args[0];
            } else {
                date = args[0] + "-" + args[1] + "-" + args[2];
            }
            simple = false;
        }
        if (date.contains("年") && date.contains("月") && date.contains("日")) {
            String year = date.substring(date.indexOf("年") - 4, date.indexOf("年"));
            String month = date.substring(date.indexOf("月") - 2, date.indexOf("月"));
            String day = date.substring(date.indexOf("日") - 2, date.indexOf("日"));
            date = year + "-" + month + "-" + day;
            simple = false;
        }
        if (simple && date.length() == 8) {
            return shortDate.get().parse(date);
        }
        return standardDate.get().parse(date);
    }


    /**
     * 动态解析日期时间格式
     *
     * @param dateTime 日期字符串
     * @return 日期
     */
    public static Date parseDateTimeDynamic(String dateTime) throws ParseException {
        if (dateTime == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        //"yyyy-MM-dd HH:mm:ss.SSS"
        if (dateTime.length() == 23) {
            return new SimpleDateFormat(FULL_STANDARD_DATE_TIME).parse(dateTime);
        }
        //yyyy-MM-dd HH:mm:ss
        if (dateTime.length() == 19) {
            return new SimpleDateFormat(STANDARD_DATE_TIME).parse(dateTime);
        }
        //yyyy-MM-dd HH:mm
        if (dateTime.length() == 16) {
            return new SimpleDateFormat(INCOMPLETE_DATE_TIME).parse(dateTime);
        }
        //yyyyMMddHHmmssSSS
        if (dateTime.length() == 17) {
            return new SimpleDateFormat(FULL_SHORT_DATE_TIME).parse(dateTime);
        }
        //yyyyMMddHHmmss
        if (dateTime.length() == 14) {
            return new SimpleDateFormat(SHORT_DATE_TIME).parse(dateTime);
        }
        //yyyy-MM-dd'T'HH:mm:ss'Z'
        if (dateTime.length() == 24) {
            return new SimpleDateFormat(UTC_DATE_TIME).parse(dateTime);
        }
        return null;
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(parseDateTimeDynamic("20020101121212"));
    }
}
