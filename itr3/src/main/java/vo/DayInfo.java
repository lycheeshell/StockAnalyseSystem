package vo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lienming on 2017/5/18.
 */
public class DayInfo  implements Serializable {
    private static final long serialVersionUID = 1L;

    public String concept_bench ;  //概念板块名称 可能存在
    public List<String> stocks ;   //概念板块包含的股票列表

    public long totalVolume;//当日总交易量
    public long limitUp;//涨停股票数
    public long limitDown;//跌停股票数
    public long notBad;//涨幅超过5%的股票数
    public long notGood;//跌幅超过 5%的股票数
    public long loss;//开盘‐收盘大于 5 % *上一个交易日收盘价的股票个数
    public long profit;//开盘‐收盘小于‐ 5%*上一个交易日收盘价的股票个数
    public DayInfo(long TotalVolume,long LimitUp,long LimitDown,long NotBad,long NotGood,long Loss,long Profit){
        totalVolume=TotalVolume;
        limitUp=LimitUp;
        limitDown=LimitDown;
        notBad=NotBad;
        notGood=NotGood;
        loss=Loss;
        profit=Profit;
    }






}
