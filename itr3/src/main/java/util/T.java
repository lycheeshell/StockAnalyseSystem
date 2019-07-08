package util;

/**
 * Created by LZ on 2017/4/2.
 * 辅助类,用来记录形成期间的收益
 */
public class T {
    private String code;//代码编号
    private double profit;//涨跌幅
    public T(String c,double p){
        code=c;
        profit=p;
    }
    public String getCode(){
        return code;
    }

    public double getProfit(){
        return profit;
    }
}
