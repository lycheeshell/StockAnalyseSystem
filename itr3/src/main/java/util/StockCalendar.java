package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lienming on 2017/6/4.
 */
public class StockCalendar {
    private static Calendar calendar  ;



    public static Date getToday(){
//        return new Date();
        Date today = null;
        try {
            today = new SimpleDateFormat("yyyy-MM-dd").parse("2016-10-20");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return today ;
    }

    public static Date getOneYearAgo(){
//        Date today = new Date();
        Date today = getToday() ;
        calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.YEAR,-1);
        Date oneYearAgo = calendar.getTime();
        return oneYearAgo;
    }

    public static LocalDate DateToLocalDate(Date date ) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        LocalDate localDate = localDateTime.toLocalDate();
        return localDate;
    }



}
