package vo;

import javax.persistence.*;
import java.text.DecimalFormat;

/**
 * Created by lienming on 2017/5/18.
 */
@Entity
@Table(name = "BItem",schema = "",catalog ="")
public class BItem {
    public int circle;//周期天数
    public double excessEarnings;//超额收益
    public double annualWinRate;//一年胜率

    public BItem(int c,double e,double a){
        DecimalFormat df = new DecimalFormat("#.00");
        circle =c;
        excessEarnings =Double.valueOf(df.format(e));
        annualWinRate =Double.valueOf(df.format(a));
    }

    @Id
    @Column(name = "circle")
    public int getCircle(){
        return circle;
    }

    public void setCircle(int circle){
        this.circle=circle;
    }

    @Id
    @Column(name = "excessEarnings")
    public double getExcessEarnings(){
        return excessEarnings;
    }

    public void setExcessEarnings(double ee){
        this.excessEarnings=ee;
    }

    @Id
    @Column(name = "annualWinRate")
    public double getAnnualWinRate(){
        return annualWinRate;
    }

    public void setAnnualWinRate(double wr){
        this.annualWinRate=wr;
    }

}
