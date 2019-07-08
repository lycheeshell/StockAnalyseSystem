package vo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lienming on 2017/5/18.
 */
public class AverageGraph {
    private static final long serialVersionUID = 1L;

    private int dataLength ;
    private int averageNumber ;
    private double[] avg;
    private String[] dateAxis;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    public AverageGraph(double[] db, Date[] dates, int avgNumber)
    {
        this.dataLength = dates.length;
        this.dateAxis = new String[dataLength];
        this.averageNumber = avgNumber ;
        for(int i=0;i<dataLength;i++){
            dateAxis[i] = sdf.format(dates[i]);
        }
        this.avg = db;
    }

    public double[] getAverage(){
        return this.avg;
    }

    public String[] getDataAxis(){ return this.dateAxis; }

    public int getDayNumber() { return this.dataLength; }

    public int getAverageNumber(){ return this.averageNumber; }

    public double getHighestYAxis(){
        double tmp = 0 ;
        for(int i=0;i<avg.length;i++)
        {
            if(avg[i]>tmp)
                tmp = avg[i] ;
        }
        return tmp;
    }
}
