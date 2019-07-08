package vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

/**
 * Created by lienming on 2017/5/18.
 */
public class KGraph  implements Serializable {
    private static final long serialVersionUID = 1L;
    private double beginPrice;
    private double endPrice;
    private double highPrice;
    private double leastPrice;
    private String[] dateAxis;
    private Map<String,double[]> map;//每天对应一个K线图,逻辑层得到日期后把数据填进map中

    public void setMap(Map<String,double[]>maps)
    {
        this.map=maps;
    }

    public Map<String,double[]> getMap()
    {
        return map;
    }

    public void setBegin(double begin)
    {
        this.beginPrice=begin;
    }

    public double getBegin()
    {
        return beginPrice;
    }

    public void setEnd(double end)
    {
        this.endPrice=end;
    }

    public double getEnd()
    {
        return endPrice;
    }
    public void setHigh(double high)
    {
        this.highPrice=high;
    }

    public double getHigh()
    {
        return highPrice;
    }

    public void setLeast(double least)
    {
        this.leastPrice=least;
    }

    public double getleast()
    {
        return leastPrice;
    }

    public void setDateAxis(String[]dateAxis)
    {
        this.dateAxis=dateAxis;
    }

    public String[] getDataAxis(){ return this.dateAxis; }
}