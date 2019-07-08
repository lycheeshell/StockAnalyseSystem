package vo;

import java.time.LocalDate;

/**
 * Created by zongkan on 2017/6/14.
 */
public class NewRadaVO {
    public String code ;       //代码编号  key
    public LocalDate date ;    //记录日期  key
    public double open	;       //开盘指数 后复权
    public double close;       //收盘指数 后复权
    public double high	;       //最高指数 后复权
    public double low  ;       //最低指数 后复权
    public double volume;      //成交量
    public double amount;      //成交总金额
    public double pchange;//涨跌幅
    public String stockName ;
    public double changerate;//换手率
    public double pb ;//市净率
    public double profit;//利润同比(%)
    public double undp;//未分利润
    public double mrq ;//市盈率mrq
    public double totals;//总股本(亿)
    public double outstanding;//流通股本(亿)
    public double esp;//每股收益
    public double bvps;//每股净资
    public double perundp;//每股未分配
    public double rev;//收入同比(%)
    public double gpr;//毛利率(%)

    public NewRadaVO(String co, LocalDate da, double op, double cl, double hi, double lo, double vo, double am,double pc, String name, double cr, double ssspb, double pro, double un, double mr, double to, double outs, double es, double bvp, double perun, double re, double gp) {
        this.code = co;
        this.date = da;
        this.open = op;
        this.close = cl;
        this.high = hi;
        this.low = lo;
        this.volume = vo;
        this.amount = am;
        this.pchange=pc;
        this.stockName = name;
        this.changerate = cr;
        this.pb = ssspb;
        this.profit = pro;
        this.undp = un;
        this.mrq = mr;
        this.totals = to;
        this.outstanding = outs;
        this.esp = es;
        this.bvps = bvp;
        this.perundp = perun;
        this.rev = re;
        this.gpr = gp;
    }
}
