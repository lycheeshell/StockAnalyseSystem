package vo;

/**
 * Created by lienming on 2017/5/18.
 */
public class showStockVO {
    private String stockNum;
    private String stockName;
    public showStockVO(String name,String num)
    {
        this.stockNum=num;
        this.stockName=name;
    }

    public String getStockNum()
    {
        return this.stockNum;
    }

    public String getStockName()
    {
        return this.stockName;
    }

}
