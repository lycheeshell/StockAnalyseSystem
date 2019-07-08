package vo;

/**
 * Created by lienming on 2017/6/3.
 */
public class RadaVO {
    public String stockName ;
    public String stockCode ;

    public double changerate;//换手率
    public double pb ;//市净率
    public double profit;//利润同比(%)
    public double undp;//未分利润
    public double mrq ;//市盈率mrq
    public double totals;//总股本(亿)

    public RadaVO(String code, String name, double cr, double ssspb, double pro, double un, double mr, double to) {
        this.stockCode = code;
        this.stockName = name;
        this.changerate = cr;
        this.pb = ssspb;
        this.profit = pro;
        this.undp = un;
        this.mrq = mr;
        this.totals = to;
    }


}
