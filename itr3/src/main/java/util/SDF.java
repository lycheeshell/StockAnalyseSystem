package util;

import net.sf.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by lienming on 2017/6/1.
 * 日期格式工具类
 */
public class SDF {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** Date <-> String*/
    public static Date parse(String dateString) throws ParseException {
        return sdf.parse(dateString) ;
    }

    public static String format(Date date){
        return sdf.format(date) ;
    }


    /** Date <-> LocalDate */
    public static LocalDate dateToLocalDate(Date date) {
        return LocalDate.parse(format(date), DATE_FORMAT);
    }

    public static Date localDateToDate(LocalDate localDate) {
        Date date = null ;
        try {
            date = sdf.parse(localDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date ;
    }

    /** JSONObject.getLocalDate */
    public static LocalDate fromJSON(JSONObject jsonObject,String key) {
        return LocalDate.parse(jsonObject.get(key).toString(), DATE_FORMAT);
    }


}
