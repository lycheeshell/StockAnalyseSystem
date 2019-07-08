package util;

/**
 * Created by LZ on 2017/6/15.
 */
public class StockHelper {
    public String code ;       //代码编号  key
    public String stockName ;
    public boolean label;//结果
    public double volume;      //成交量
    public double pchange;//一段时间的涨跌幅
    public double changerate;//换手率
    public double pb ;//市净率
    public double profit;//利润同比(%)
    public double mrq ;//市盈率mrq
    public double caprat;//非流通股本比
    public double esp;//每股收益
    public double bvps;//每股净资
    public double perundp;//每股未分配
    public double rev;//收入同比(%)
    public double gpr;//毛利率(%)

    public StockHelper(String co, double vo,boolean la,double pc, String name, double cr, double ssspb, double pro, double mr, double cap, double es, double bvp, double perun, double re, double gp) {
        this.code = co;
        this.volume = vo;
        this.label=la;
        this.pchange=pc;
        this.stockName = name;
        this.changerate = cr;
        this.pb = ssspb;
        this.profit = pro;
        this.mrq = mr;
        this.caprat=cap;
        this.esp = es;
        this.bvps = bvp;
        this.perundp = perun;
        this.rev = re;
        this.gpr = gp;
    }
}
