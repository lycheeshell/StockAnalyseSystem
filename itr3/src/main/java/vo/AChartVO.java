package vo;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by lienming on 2017/5/18.
 */
public class AChartVO {
    LocalDate beginDate;            //开始日期，时间区间
    LocalDate endDate;              //结束日期
    int formPeriod;                 //形成期天数
    int holdPeriod;                 //持有期天数

    List<DailyYield> benchmarkYieldList  ;       //基准每日收益
    List<DailyYield> dailyYieldList      ;       //策略每日收益

    public double annualYield;             //年化收益率
    public double benchmarkAnnualYield;    //基准年化收益率
    public double alpha;                   //阿尔法
    public double beta;                    //贝塔
    public double sharp;                   //夏普比率
    public double maxDrawdown;             //最大回撤

    public AChartVO(StrategyVO vo){
        this.benchmarkYieldList=vo.benchmarkYieldList;
        this.dailyYieldList=vo.dailyYieldList;

        this.annualYield=vo.annualYield;
        this.benchmarkAnnualYield=vo.benchmarkAnnualYield;
        this.alpha=vo.alpha;
        this.beta=vo.beta;
        this.sharp=vo.sharp;
        this.maxDrawdown=vo.maxDrawdown;
    }


    public List<DailyYield> getBaseList(){ return this.benchmarkYieldList;}

    public List<DailyYield> getStrategyList(){ return this.dailyYieldList; }

    public double[] getYBound(){
        double max=0,min=0;
        double max_tmp=0,min_tmp=0;
        int size1 = benchmarkYieldList.size();
        int size2 = dailyYieldList.size();
        int sizeFor = 0;
        if(size1 > size2) {
            sizeFor = size2;
        } else {
            sizeFor = size1;
        }
        for(int i=0;i<sizeFor;i++){
            double base = benchmarkYieldList.get(i).getDailyYield();
            double strategy = dailyYieldList.get(i).getDailyYield();
            if(base>strategy) {
                max_tmp = base;
                min_tmp = strategy;
            }
            else {
                max_tmp = strategy;
                min_tmp = base;
            }
            if(max_tmp>max)
                max = max_tmp;
            if(min_tmp<min)
                min = min_tmp;
        }
        double[] ret = {max,min};
        return ret;
    }
}
