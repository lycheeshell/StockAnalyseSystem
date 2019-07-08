package entity;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by lienming on 2017/5/18.
 * 某支股票在某个交易日的情况
 */

@Entity
@Table( name = "stock",schema = "",catalog ="")  //未完成

public class StockEntity {

    private String code ;       //代码编号  key
    private LocalDate date ;    //记录日期  key

    private double open	;       //开盘指数 后复权
    private double close;       //收盘指数 后复权
    private double high	;       //最高指数 后复权
    private double low  ;       //最低指数 后复权
    private double volume;      //成交量
    private double amount;      //成交总金额


    public StockEntity(String co, LocalDate da, double op, double cl, double hi, double lo, double vo, double am) {
        this.code = co;
        this.date = da;
        this.open = op;
        this.close = cl;
        this.high = hi;
        this.low = lo;
        this.volume = vo;
        this.amount = am;
    }

    @Id
    @Column(name = "code")
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Id
    @Column(name = "date" )
    public void setDate(LocalDate Date){
        date=Date;
    }

    public LocalDate getDate(){
        return date;
    }


    @Basic
    @Column(name = "open" )
    public double getOpen() {
        return this.open;
    }

    public void setOpen(double open){
        this.open = open ;
    }

    @Basic
    @Column(name = "close" )
    public void setClose(double Close){
        close=Close;
    }

    public double getClose(){
        return close;
    }

    @Basic
    @Column(name = "high" )
    public void setHigh(double High){
        high=High;
    }

    public double getHigh(){
        return high;
    }

    @Basic
    @Column(name = "low" )
    public void setLow(double Low){
        low=Low;
    }

    public double getLow(){
        return low;
    }


    @Basic
    @Column(name = "volume" )
    public void setVolume(double Volume){
        volume=Volume;
    }

    public double getVolume(){
        return volume;
    }

    @Basic
    @Column(name = "amount" )
    public void setAmount(double Amount){ amount=Amount; }

    public double getAmount(){ return amount; }

        public void setAdjOpen(double Open){
        open=Open;
    }

    public double getAdjOpen(){
        return open;
    }

    public void setAdjHigh(double High){
        high=High;
    }

    public double getAdjHigh(){
        return high;
    }

    public void setAdjLow(double Low){
        low=Low;
    }

    public double getAdjLow(){
        return low;
    }

    public void setAdjClose(double Close){
        close=Close;
    }

    public double getAdjClose(){
        return close;
    }

}



//

//
//    private double open_bfq;//开盘指数 不复权
//    private double low_bfq ;//最低指数 不复权
//    private double high_bfq;//最高指数 不复权
//    private double close_bfq;//收盘指数 不复权
