package com.mlizhi.modules.spec.util;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import p016u.aly.bq;

public class DateFormatUtil {
    public static Calendar getBeginDateByWeek(Date date, int weekTh) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(7) - 1;
        if (dayofweek <= 0) {
            dayofweek = 7;
        }
        cal.set(5, cal.get(5) - ((weekTh * 7) + (dayofweek - 1)));
        return cal;
    }

    public static Calendar getBeginDateByMonth(Date date, int monthTh) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month_off = monthTh % 12;
        cal.set(1, cal.get(1) - (monthTh / 12));
        cal.set(2, cal.get(2) - month_off);
        cal.set(5, 1);
        return cal;
    }

    public static String getDatethInWeek(Date date, Context context) {
        String[] weeks = new String[]{context.getString(R.string.week4sun), context.getString(R.string.week4mon), context.getString(R.string.week4tue), context.getString(R.string.week4wed), context.getString(R.string.week4thu), context.getString(R.string.week4fri), context.getString(R.string.week4sat)};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(7) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

    public static int getWeekthInYear(Date currentDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int weekth = cal.get(3);
        getBeginDateByWeek(currentDate, weekth);
        return weekth;
    }

    public static Date getFirstDayOfWeek(Date dNow) {
        Calendar c = Calendar.getInstance();
        c.setTime(dNow);
        c.set(7, 2);
        return c.getTime();
    }

    public static Date getLastDayOfWeek(Date dNow) {
        Calendar c = Calendar.getInstance();
        c.setTime(dNow);
        c.set(7, 1);
        c.add(3, 1);
        return c.getTime();
    }

    public static Date getBeforeDay(Date currentDate) {
        Date dBefore = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(5, -1);
        return calendar.getTime();
    }

    public static Date getNextDay(Date currentDate) {
        Date dAfter = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(5, 1);
        return calendar.getTime();
    }

    @Deprecated
    public static Date getBeforeDay(String currentDate) {
        Date dBefore = new Date();
        Date dNow = str2date(currentDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dNow);
        calendar.add(5, -1);
        return calendar.getTime();
    }

    @Deprecated
    public static Date getNextDay(String currentDate) {
        Date dNow = str2date(currentDate);
        Date dAfter = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dNow);
        calendar.add(5, 1);
        return calendar.getTime();
    }

    public static Date formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        Date fomatDate = null;
        String str = bq.f888b;
        try {
            fomatDate = format.parse(format.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fomatDate;
    }

    public static Date str2date(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String date2str(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static String date2string(Date date, Context context) {
        StringBuffer sb = new StringBuffer();
        sb.append("MM");
        sb.append(context.getString(R.string.date4month));
        sb.append("dd");
        sb.append(context.getString(R.string.date4date));
        return new SimpleDateFormat(sb.toString()).format(date);
    }

    @Deprecated
    public static Date getBeforeWeekCurrentDate(String currentDate) {
        Date dNow = str2date(currentDate);
        Date dAfter = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dNow);
        calendar.add(5, -7);
        return calendar.getTime();
    }

    @Deprecated
    public static Date getNextWeekCurrentDate(String currentDate) {
        Date dNow = str2date(currentDate);
        Date dAfter = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dNow);
        calendar.add(5, 7);
        return calendar.getTime();
    }

    public static Date getBeforeWeekCurrentDate(Date currentDate) {
        Date dAfter = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(5, -7);
        return calendar.getTime();
    }

    public static Date getNextWeekCurrentDate(Date currentDate) {
        Date dAfter = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(5, 7);
        return calendar.getTime();
    }

    @Deprecated
    public static Date getBeforeMonthCurrentDate(String currentDate) {
        Date dNow = str2date(currentDate);
        Date dAfter = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dNow);
        calendar.add(2, -1);
        return calendar.getTime();
    }

    @Deprecated
    public static Date getNextMonthCurrentDate(String currentDate) {
        Date dNow = str2date(currentDate);
        Date dAfter = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dNow);
        calendar.add(2, 1);
        return calendar.getTime();
    }

    public static Date getBeforeMonthCurrentDate(Date currentDate) {
        Date dAfter = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(2, -1);
        return calendar.getTime();
    }

    public static Date getNextMonthCurrentDate(Date currentDate) {
        Date dAfter = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(2, 1);
        return calendar.getTime();
    }

    public static int getWeekOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(2);
        c.setMinimalDaysInFirstWeek(1);
        c.setTime(date);
        return c.get(3);
    }

    public static int getMaxWeekNumOfYear(int year) {
        Calendar c = Calendar.getInstance();
        c.set(year, 11, 31, 23, 59, 59);
        return getWeekOfYear(c.getTime());
    }

    public static Date getFirstDayOfWeek(int year, int week) {
        Calendar c = Calendar.getInstance();
        c.set(1, year);
        c.set(3, week);
        c.set(7, 2);
        c.setFirstDayOfWeek(2);
        return c.getTime();
    }

    public static Date getLastDayOfWeek(int year, int week) {
        Calendar c = Calendar.getInstance();
        c.set(1, year);
        c.set(3, week);
        c.setFirstDayOfWeek(2);
        c.set(7, c.getFirstDayOfWeek() + 6);
        return c.getTime();
    }

    public static Date getFirstDayOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(1, year);
        c.set(2, month - 1);
        c.set(5, c.getActualMinimum(5));
        return c.getTime();
    }

    public static Date getLastDayOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(1, year);
        c.set(2, month - 1);
        c.set(5, c.getActualMaximum(5));
        return c.getTime();
    }

    public static Date getFirstDayOfMonth(Date dNow) {
        Calendar c = Calendar.getInstance();
        c.setTime(dNow);
        c.set(1, c.get(1));
        c.set(2, c.get(2));
        c.set(5, c.getActualMinimum(5));
        return c.getTime();
    }

    public static Date getLastDayOfMonth(Date dNow) {
        Calendar c = Calendar.getInstance();
        c.setTime(dNow);
        c.set(1, c.get(1));
        c.set(2, c.get(2));
        c.set(5, c.getActualMaximum(5));
        return c.getTime();
    }

    public static Date getFirstDayOfQuarter(int year, int quarter) {
        if (quarter > 4) {
            return null;
        }
        return getFirstDayOfMonth(year, ((quarter - 1) * 3) + 1);
    }

    public static Date getLastDayOfQuarter(int year, int quarter) {
        if (quarter > 4) {
            return null;
        }
        return getLastDayOfMonth(year, quarter * 3);
    }

    public static Date getFirstDayOfYear(int year) {
        return getFirstDayOfQuarter(year, 1);
    }

    public static Date getLastDayOfYear(int year) {
        return getLastDayOfQuarter(year, 4);
    }
}
