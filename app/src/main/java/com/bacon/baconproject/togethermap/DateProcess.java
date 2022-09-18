package com.bacon.baconproject.togethermap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateProcess {
    public static String NORMAL_FORMAT = "yyyy/MM/dd hh:mm:ss";

    public String get_date_to_str(String format){
        return get_date_to_str(new Date(), format);
    }

    public String get_date_to_str(Date date, String format){
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        c.add(Calendar.HOUR_OF_DAY, 8);
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }

    public Date get_str_to_date(String date, String format_str) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(format_str, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(format.parse(date));
//        calendar.add(Calendar.HOUR_OF_DAY, -8);

        return calendar.getTime();
    }

    public String get_int_day_to_string(Date start_day, int which_day, int hour, int min){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start_day);
        calendar.add(Calendar.DAY_OF_MONTH, which_day<0 ? 0:which_day);
        if (hour >= 24){
            hour = 23;
            min = 59;
        }else if(hour == 0){
            min = 1;
        }else if(min >= 60){
            min = 59;
        }
//        calendar.add(Calendar.HOUR_OF_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    public int cal_day_interval(long start, long end){
        return ((int)((end - start) / (24 * 3600 * 1000))) + 1;
    }

}
