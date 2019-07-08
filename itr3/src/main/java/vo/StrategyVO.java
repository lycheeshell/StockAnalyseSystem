package vo;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by lienming on 2017/5/18.
 */
public class StrategyVO   implements Serializable {
    public List<DailyYield> benchmarkYieldList;//基准每日收益
    public List<DailyYield> dailyYieldList ;//策略每日收益
    public int profit;//正收益周期数
    public List<Double> profitList;//记录每次正收益
    public int loss;//负收益周期数
    public List<Double> lossList;//记录每次负收益
    public double annualYield;//年化收益率
    public double benchmarkAnnualYield;//基准年化收益率
    public double alpha;//阿尔法
    public double beta;//贝塔
    public double sharp;//夏普
    public double maxDrawdown;//最大回撤


    public StrategyVO(List<DailyYield> bl,List<DailyYield> li, int p, List<Double> pList, int l, List<Double> lList, double a, double b, double aa, double bb, double s, double m){
        DecimalFormat df = new DecimalFormat("#.00");
        benchmarkYieldList=bl;
        dailyYieldList=li;
        profit=p;
        profitList =pList ;
        loss=l;
        lossList =lList ;
        annualYield=Double.valueOf(df.format(a));
        benchmarkAnnualYield=Double.valueOf(df.format(b));
        alpha=Double.valueOf(df.format(aa));
        beta=Double.valueOf(df.format(bb));
        sharp=Double.valueOf(df.format(s));
        maxDrawdown=Double.valueOf(df.format(m));
    }
}
