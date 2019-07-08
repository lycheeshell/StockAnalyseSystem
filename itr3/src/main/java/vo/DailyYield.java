package vo;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.LocalDate;

/**
 * Created by lienming on 2017/5/18.
 */
public class DailyYield implements Serializable {
    private LocalDate date;
    private double dailyYield;

    public DailyYield(LocalDate d, double dy){
        DecimalFormat df = new DecimalFormat("#.00");
        date=d;
        dailyYield =Double.valueOf(df.format(dy));
    }

    public LocalDate getDate (){
        return date;
    }

    public double getDailyYield (){
        return dailyYield ;
    }
}