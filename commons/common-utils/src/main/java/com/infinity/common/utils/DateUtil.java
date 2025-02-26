package com.infinity.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    // 每天重置 时间,5点
    public static final int ResetTime = 5;

    // 一小时
    public static final int OneHourSec = 60 * 60;

    // 一天的 秒
    public static final int OneDaySec = 24 * 60 * 60;

    public static final int OneMinuteMs = 60 * 1000;

    private static final String format = "yyyy/MM/dd HH:mm:ss";
    private static final String formate = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);


    private static SimpleDateFormat SDF() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }


    /**
     * 零点
     */
    public static long zeroTime(long tm) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tm);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取今天5点的 时间戳/1000, 凌晨0:00-4.59分, 算昨天; 秒单位时间戳
     *
     * @return
     */
    public static int getNowResetDayTime() {
        // 取今天5点时间,秒单位
        long second = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusHours(ResetTime).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        // 取当前时间戳  < 5点的时间戳,当做 昨天算
        if (System.currentTimeMillis() < second * 1000) {
            second -= OneDaySec;
        }
        return (int) second;
    }

    /**
     * 下次 跨日重置 时间;秒单位时间戳
     *
     * @return
     */
    public static int nextDayReset() {
        return getNowResetDayTime() + OneDaySec;
    }

    /**
     * 下次 周重置的时间;秒单位时间戳
     *
     * @return
     */
    public static int nextWeek1Reset() {
        // 下周一重置时间  5点
        long nextWeek = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusWeeks(1).plusHours(ResetTime).with(DayOfWeek.MONDAY).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        // 计算当前时间到下周 5点是否 小于 7 * 24 * 60 * 60; 如果成立,表示当前时间为  本周一 0点-4.59;
        long nowTime = System.currentTimeMillis() / 1000;
        if (nowTime + 7 * 24 * 60 * 60 < nextWeek) {
            // 本周一 5点;
            return nowWeek1Reset();
        } else {
            return (int) (nextWeek);
        }
    }

    /**
     * 本周1  5点;
     *
     * @return
     */
    public static int nowWeek1Reset() {
        int nowTime = (int) (LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 0, 0)).with(DayOfWeek.MONDAY).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
        return nowTime;
    }


    /**
     * 下次 月重置时间;秒单位时间戳
     *
     * @return
     */
    public static int nextMonthReset() {
        // 当月第一天5点的时间戳
        int nowMonthReset = nowMonthReset();
        // 如果当前时间,小于本月 1号5点
        if (System.currentTimeMillis() / 1000 < nowMonthReset) {
            return nowMonthReset;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, ResetTime);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    /**
     * 当月1号5点时间 秒单位时间戳
     *
     * @return
     */
    private static int nowMonthReset() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, ResetTime);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    /**
     * 获取当前 时分秒  "yyyy-MM-dd HH:mm:ss"
     *
     * @return
     */
    public static String getNowTimeStr() {
        return SDF().format(new Date());
    }


    /**
     * 判断当前时间是否在传入的时间段内
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return true 时间段内  FALSE 不在时间段内
     */
    public static boolean timePeriod(String startTime, String endTime) {
        if (startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)) {
            long start = stringToTimeStamp(startTime);
            long end = stringToTimeStamp(endTime);
            long now = Instant.now().toEpochMilli();
            return now >= (start) && now <= (end);
        }
        return true;
    }

    /**
     * 判断当前时间是否在传入的时间段内
     *
     * @param startTime 开始时间 13位时间戳
     * @param endTime   结束时间 13位时间戳
     * @return true 时间段内  FALSE 不在时间段内
     */
    public static boolean timePeriod(long startTime, long endTime) {
        long now = Instant.now().toEpochMilli();
        return now >= (startTime) && now <= (endTime);
    }

    /**
     * 转化为13位时间戳
     *
     * @param dateStr yyyy/MM/dd HH:mm:ss 格式的时间
     * @return 13位时间戳
     */
    public static long stringToTimeStamp(String dateStr) {
        return LocalDateTime.parse(dateStr, dateTimeFormatter).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 判断传入的时间戳是否在今天零点之前
     *
     * @param timestamp 13位时间戳
     * @return true 在今天之前  false 不在今天之前
     */
    public static boolean isTodayBefore(long timestamp) {
        //今天零点的时间戳
        long now = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return timestamp < now;
    }

    /**
     * 判断传入的时间戳是否在本周一零点之前
     *
     * @param timestamp 13位时间戳
     * @return true 在本周一零点之前  false 不在本周一零点之前
     */
    public static boolean isWeekBefore(long timestamp) {
        //本周一零点的时间戳
        long monday = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).with(DayOfWeek.MONDAY).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return timestamp < monday;
    }

    /**
     * 判断传入的时间戳是否在本月1日零点之前
     *
     * @param timestamp 13位时间戳
     * @return true 在本月1日零点之前  false 不在本月1日零点之前
     */
    public static boolean isMonthBefore(long timestamp) {
        //本月1日零点的时间戳
        long monday = LocalDateTime.of(LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth())), LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return timestamp < monday;
    }

    /**
     * 获取当前时间的下一天零点的时间
     *
     * @return 10位时间戳时间
     */
    public static long currDay() {
        //本月1日零点的时间戳
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 获取当前时间的下一天零点的时间
     *
     * @return 10位时间戳时间
     */
    public static long nextDay() {
        //本月1日零点的时间戳
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusDays(1).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 获取当前时间的下周一零点的时间
     *
     * @return 10位时间戳时间
     */
    public static long nextWeek() {
        //下周一零点的时间戳
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusWeeks(1).with(DayOfWeek.MONDAY).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 获取当前时间的下月一日 零点的时间
     *
     * @return 10位时间戳时间
     */
    public static long nextMonth() {
        //下月一日零点的时间戳
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }


    /**
     * 获取当前时间的下一天5点的时间
     *
     * @return 10位时间戳时间
     */
    public static long nextDayOfFive() {
        //本月1日零点的时间戳
        return LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 0, 0)).plusDays(1).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 获取当前时间的下一天5点的时间
     *
     * @return 10位时间戳时间
     */
    public static long dayOfFive(int plusDay) {
        return LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 0, 0)).plusDays(plusDay).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 获取当前时间的下周一5点的时间
     *
     * @return 10位时间戳时间
     */
    public static long nextWeekOfFive() {
        //下周一零点的时间戳
        return LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 0, 0)).plusWeeks(1).with(DayOfWeek.MONDAY).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 获取当前时间的下月一日 5点的时间
     *
     * @return 10位时间戳时间
     */
    public static long nextMonthOfFive() {
        //下月一日零点的时间戳
        return LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 0, 0)).plusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }


    /**
     * 获取当前天5点的时间
     *
     * @return 10位时间戳时间
     */
    public static long toDayOfFive() {
        //本月1日零点的时间戳
        return LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 0, 0)).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 获取当前时间的下周一5点的时间
     *
     * @return 10位时间戳时间
     */
    public static long weekOfFive() {
        //下周一零点的时间戳
        return LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 0, 0)).with(DayOfWeek.MONDAY).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }


    /**
     * 获取当前时间的下月一日 5点的时间
     *
     * @return 10位时间戳时间
     */
    public static long monthOfFive() {
        //下月一日零点的时间戳
        return LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 0, 0)).with(TemporalAdjusters.firstDayOfMonth()).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    public static Date toDate(String dateStr) {
        return Date.from(LocalDateTime.parse(dateStr, dateTimeFormatter).atZone(ZoneId.systemDefault()).toInstant());
    }


    public static long dayBetween(long time1, long time2) {
        return (zeroTime(time2) - zeroTime(time1)) / (1000 * 3600 * 24);
    }

    /**
     * 判断两个时间是否是同一天
     *
     * @param time1 时间1
     * @param time2 时间2
     * @return 是同一天返回true ，否则返回false
     */
    public static boolean isSameDay(Date time1, Date time2) {
        // 目前两个时间差距不超过24小时，并且时间1的日期和时间2的日期一致
        return Math.abs(time1.getTime() - time2.getTime()) < 24 * 60 * 60 * 1000 && time1.getDay() == time2.getDay();
    }

    //时间戳转成DATE
    public static Date getTime(long time) {
        String loginTime = new SimpleDateFormat(formate).format(time);
        return Date.from(LocalDateTime.parse(loginTime, DateTimeFormatter.ofPattern(formate))
                .atZone(ZoneId.systemDefault()).toInstant());
    }

    public static boolean isSameDay(long time1, long time2) {
        if (time1 <= 0 || time2 <= 0) return false;
        return isSameDay(getTime(time1), getTime(time2));
    }

    public static Date parse(String dateStr) {
        try {
            return SDF().parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isSameMonth(LocalDate date1, LocalDate date2) {
        return date1.getYear() == date2.getYear() && date1.getMonthValue() == date2.getMonthValue();
    }
}
