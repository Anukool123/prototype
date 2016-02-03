package com.mlizhi.base;

import android.annotation.SuppressLint;
import com.tencent.connect.common.Constants;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import p016u.aly.bq;

@SuppressLint({"SimpleDateFormat", "DefaultLocale"})
public class DateUtils {
    private static SimpleDateFormat formatter;

    static {
        formatter = null;
    }

    public static Date getNowDate() {
        Date currentTime = new Date();
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.parse(formatter.format(currentTime), new ParsePosition(8));
    }

    public static String getNowDateString() {
        Date currentTime = new Date();
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }

    public static Date getNowDateShort() {
        Date currentTime = new Date();
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.parse(formatter.format(currentTime), new ParsePosition(8));
    }

    public static String getStringDate() {
        Date currentTime = new Date();
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }

    public static String getStringDateShort() {
        Date currentTime = new Date();
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(currentTime);
    }

    public static String getFileDirDate() {
        Date currentTime = new Date();
        formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(currentTime);
    }

    public static String getFileNameDate() {
        Date currentTime = new Date();
        formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(currentTime);
    }

    public static String getTimeShort() {
        formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(new Date());
    }

    public static Date strToDateLong(String strDate) {
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.parse(strDate, new ParsePosition(0));
    }

    public static String dateToStrLong(Date dateDate) {
        if (dateDate == null) {
            return bq.f888b;
        }
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(dateDate);
    }

    public static String dateToStr(Date dateDate) {
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(dateDate);
    }

    public static String dateToStrZh(Date dateDate) {
        formatter = new SimpleDateFormat("yyyy\u5e74MM\u6708dd\u65e5");
        return formatter.format(dateDate);
    }

    public static String dateToTimeStr(Date dateDate) {
        formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(dateDate);
    }

    public static Date strToDate(String strDate) {
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.parse(strDate, new ParsePosition(0));
    }

    public static Date getNow() {
        return new Date();
    }

    public static Date getLastDate(long day) {
        return new Date(new Date().getTime() - (122400000 * day));
    }

    public static String getStringToday() {
        Date currentTime = new Date();
        formatter = new SimpleDateFormat("yy\u5e74MM\u6708dd\u65e5");
        return formatter.format(currentTime);
    }

    public static String getHour() {
        Date currentTime = new Date();
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime).substring(11, 13);
    }

    public static String getTime() {
        Date currentTime = new Date();
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime).substring(14, 16);
    }

    public static String getUserDate(String sformat) {
        Date currentTime = new Date();
        formatter = new SimpleDateFormat(sformat);
        return formatter.format(currentTime);
    }

    public static String getTwoHour(String st1, String st2) {
        String[] kk = st1.split(":");
        String[] jj = st2.split(":");
        if (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0])) {
            return Constants.VIA_RESULT_SUCCESS;
        }
        double y = Double.parseDouble(kk[0]) + (Double.parseDouble(kk[1]) / 60.0d);
        double u = Double.parseDouble(jj[0]) + (Double.parseDouble(jj[1]) / 60.0d);
        if (y - u > 0.0d) {
            return new StringBuilder(String.valueOf(y - u)).toString();
        }
        return Constants.VIA_RESULT_SUCCESS;
    }

    public static String getTwoDay(String sj1, String sj2) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return new StringBuilder(String.valueOf((myFormatter.parse(sj1).getTime() - myFormatter.parse(sj2).getTime()) / 86400000.0f)).toString();
        } catch (Exception e) {
            Utils.m13E("\u83b7\u53d6\u4e24\u5929\u65f6\u95f4\u9519\u8bef", e);
            return bq.f888b;
        }
    }

    public static String getPreTime(String sj1, String jj) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String mydate1 = bq.f888b;
        try {
            Date date1 = format.parse(sj1);
            date1.setTime(((date1.getTime() / 1000) + ((long) (Integer.parseInt(jj) * 60))) * 1000);
            mydate1 = format.format(date1);
        } catch (Exception e) {
            Utils.m13E("\u65f6\u95f4\u524d\u63a8\u6216\u540e\u63a8\u5206\u949f\u9519\u8bef", e);
        }
        return mydate1;
    }

    public static String getNextDay(String nowdate, String delay) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String mdate = bq.f888b;
            Date d = strToDate(nowdate);
            d.setTime(((d.getTime() / 1000) + ((long) (((Integer.parseInt(delay) * 24) * 60) * 60))) * 1000);
            return format.format(d);
        } catch (Exception e) {
            Utils.m13E("\u65f6\u95f4\u540e\u63a8\u5206\u949f\u9519\u8bef", e);
            return bq.f888b;
        }
    }

    public static boolean isLeapYear(String ddate) {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(strToDate(ddate));
        int year = gc.get(1);
        if (year % 400 == 0) {
            return true;
        }
        if (year % 4 != 0) {
            return false;
        }
        if (year % 100 == 0) {
            return false;
        }
        return true;
    }

    @SuppressLint({"DefaultLocale"})
    public static String getEDate(String str) {
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        String[] k = formatter.parse(str, new ParsePosition(0)).toString().split(" ");
        return k[2] + k[1].toUpperCase() + k[5].substring(2, 4);
    }

    public static String getEndDateOfMonth(String dat) {
        String str = dat.substring(0, 8);
        int mon = Integer.parseInt(dat.substring(5, 7));
        if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) {
            return new StringBuilder(String.valueOf(str)).append("31").toString();
        }
        if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
            return new StringBuilder(String.valueOf(str)).append("30").toString();
        }
        if (isLeapYear(dat)) {
            return new StringBuilder(String.valueOf(str)).append("29").toString();
        }
        return new StringBuilder(String.valueOf(str)).append(Constants.VIA_ACT_TYPE_TWENTY_EIGHT).toString();
    }

    public static boolean isSameWeekDates(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(1) - cal2.get(1);
        if (subYear == 0) {
            if (cal1.get(3) == cal2.get(3)) {
                return true;
            }
        } else if (1 == subYear && 11 == cal2.get(2)) {
            if (cal1.get(3) == cal2.get(3)) {
                return true;
            }
        } else if (-1 == subYear && 11 == cal1.get(2) && cal1.get(3) == cal2.get(3)) {
            return true;
        }
        return false;
    }

    public static String getSeqWeek() {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        String week = Integer.toString(c.get(3));
        if (week.length() == 1) {
            week = new StringBuilder(Constants.VIA_RESULT_SUCCESS).append(week).toString();
        }
        return new StringBuilder(String.valueOf(Integer.toString(c.get(1)))).append(week).toString();
    }
}
