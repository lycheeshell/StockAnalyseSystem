package util;

/**
 * Created by LZ on 2017/4/2.
 * 股票抽象类
 */
public class S {
    private String code;//股票代码
    private double startPrice;//持有时间段的起始价
    private double endPrice;//持有时间段的最终价
    private double number;//持有的股数
    public S(String c,double s,double n){
        code=c;
        startPrice=s;
        number=n;
    }

    public void setCode(String c){
        code=c;
    }

    public String getCode(){
        return code;
    }

    public void setStartPrice(double d){
        startPrice=d;
    }

    public double getStartPrice(){
        return startPrice;
    }

    public void setEndPrice(double d){
        endPrice=d;
    }

    public double getEndPrice(){
        return endPrice;
    }

    public void setNumber(int n){
        number=n;
    }

    public double getNumber(){
        return number;
    }
}
