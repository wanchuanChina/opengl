package com.wanchuan.opencvdemo.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间类工具类
 */
public class TimeUtils {

    /**
     * 是否在时间段内
     * @param time  00:00-02:00
     * @return
     */
    public static boolean isBetweenTime(String time) {
        //  00:00-02:00
        if (!TextUtils.isEmpty(time) && time.contains("-")) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            int now = hour * 60 + min;
            int sTime = 0;
            int eTime = 0;
            String[] duringTime = time.split("-");
            if (duringTime != null && duringTime.length == 2) {
                String[] startTime = duringTime[0].split(":");
                String[] endTime = duringTime[1].split(":");
                if (startTime != null && startTime.length == 2) {
                    int sHour = Integer.parseInt(startTime[0]);
                    int sMinute = Integer.parseInt(startTime[1]);
                    sTime= sHour * 60 + sMinute;
                }
                if (endTime != null && endTime.length == 2) {
                    int eHour = Integer.parseInt(endTime[0]);
                    int eMinute = Integer.parseInt(endTime[1]);
                    eTime= eHour * 60 + eMinute;
                }
                if (now >= sTime && now <= eTime){
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 获取当前时间
     */
    public static String[] formatDate() {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String s = format.format(date);
        String[] split = s.split(" ");
        return split;
    }

    /**
     * 获取当前时间
     */
    public static String getCurrentTime(String rule) {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(rule);
        String s = format.format(date);
        return s;
    }

    /**
     * 获取当前时间
     */
    public static String getCurrentTime(long time, String rule) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(rule);
        String s = format.format(date);
        return s;
    }

    /**
     * 星期
     */
    public static String getDayOfWeek() {
        Calendar c1 = Calendar.getInstance();
        int day = c1.get(Calendar.DAY_OF_WEEK);
        String week = "";
        switch (day) {
            case 1:
                week = "星期日";
                break;
            case 2:
                week = "星期一";
                break;
            case 3:
                week = "星期二";
                break;
            case 4:
                week = "星期三";
                break;
            case 5:
                week = "星期四";
                break;
            case 6:
                week = "星期五";
                break;
            case 7:
                week = "星期六";
                break;
        }
        return week;
    }

}
