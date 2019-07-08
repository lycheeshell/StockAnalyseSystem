package entity;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by lienming on 2017/5/18.
 * 某个板块在某个交易日的情况
 */
@Entity
@Table(name = "market",schema = "",catalog = "")
public class MarketEntity {
    private int serial;
    private LocalDate date;
    private double open;
    private double close;
    private double high;
    private double low;
    private double volume;
    private String code;

    public MarketEntity(int se, LocalDate da, double op, double cl, double hi, double lo, double vo, String co) {
        this.serial = se;
        this.date = da;
        this.open = op;
        this.close = cl;
        this.high = hi;
        this.low = lo;
        this.volume = vo;
        this.code = co;
    }

    @Id
    @Column(name="serial")
    public void setSerial (int s){
        serial =s;
    }

    public int getSerial (){
        return serial ;
    }

    @Basic
    @Column(name="date")
    public void setDate (LocalDate d){
        date=d;
    }

    public LocalDate getDate (){
        return date;
    }

    @Basic
    @Column(name="open")
    public void setOpen (double o){
        open =o;
    }

    public double getOpen (){
        return open ;
    }

    @Basic
    @Column(name="close")
    public void setClose (double c){
        close=c;
    }

    public double getClose (){
        return close;
    }

    @Basic
    @Column(name="high")
    public void setHigh (double h){
        high =h;
    }

    public double getHigh (){
        return high;
    }

    @Basic
    @Column(name="low")
    public void setLow(double l){
        low=l;
    }

    public double getLow (){
        return low;
    }

    @Basic
    @Column(name="volume")
    public void setVolume (double v){
        volume =v;
    }

    public double getVolume (){
        return volume ;
    }

    @Basic
    @Column(name="code")
    public void setCode (String c){
        code=c;
    }

    public String getCode (){
        return code;
    }


}

