package vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by lienming on 2017/5/18.
 */
public class CompareTwoStock  implements Serializable {
    private static final long serialVersionUID = 1L;
    private double low1;  // 第一支股票这段时间的最低值
    private double low2;  // 第二支股票这段时间的最低值
    private double high1;  // 第一支股票这段时间的最高值
    private double high2;  // 第二支股票这段时间的最高值
    private double range1;  //第一支股票这段时间的涨/跌幅
    private double range2;  //第二支股票这段时间的涨/跌幅
    private ArrayList<Double> settlement1;  //第一支股票每天的收盘价
    private ArrayList<Double> settlement2;  //第二支股票每天的收盘价
    private ArrayList<Double> logarithmic1;  //第一支股票每天的对数收益率
    private ArrayList<Double> logarithmic2;  //第二支股票每天的对数收益率
    private double variance1;  //第一支股票的对数收益率方差
    private double variance2;  //第二支股票的对数收益率方差
    private ArrayList<LocalDate> date; //交易的日期

    public void setLow1(double l1) {
        low1 = l1;
    }

    public void setLow2(double l2) {
        low2 = l2;
    }

    public void setHigh1(double h1) {
        high1 = h1;
    }

    public void setHigh2(double h2) {
        high2 = h2;
    }

    public void setRange1(double r1) {
        range1 = r1;
    }

    public void setRange2(double r2) {
        range2 = r2;
    }

    public void setSettlement1(ArrayList<Double> s1) {
        settlement1 = s1;
    }

    public void setSettlement2(ArrayList<Double> s2) {
        settlement2 = s2;
    }

    public void setLogarithmic1(ArrayList<Double> loga1) {
        logarithmic1 = loga1;
    }

    public void setLogarithmic2(ArrayList<Double> loga2) {
        logarithmic2 = loga2;
    }

    public void setVariance1(double v1) {
        variance1 = v1;
    }

    public void setVariance2(double v2) {
        variance2 = v2;
    }

    public void setDate(ArrayList<LocalDate> dt) {
        date = dt;
    }


    public double getLow1() {
        return low1;
    }

    public double getLow2() {
        return low2;
    }

    public double getHigh1() {
        return high1;
    }

    public double getHigh2() {
        return high2;
    }

    public double getRange1() {
        return range1;
    }

    public double getRange2() {
        return range2;
    }

    public ArrayList<Double> getSettlement1() {
        return settlement1;
    }

    public ArrayList<Double> getSettlement2() {
        return settlement2;
    }

    public ArrayList<Double> getLogarithmic1() {
        return logarithmic1;
    }

    public ArrayList<Double> getLogarithmic2() {
        return logarithmic2;
    }

    public double getVariance1() {
        return variance1;
    }

    public double getVariance2() {
        return variance2;
    }

    public ArrayList<LocalDate> getDate() {
        return date;
    }
}