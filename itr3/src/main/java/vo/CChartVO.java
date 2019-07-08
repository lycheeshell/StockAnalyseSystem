package vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by lienming on 2017/5/18.
 */
public class CChartVO {
    public int profit;//正收益周期数
    public int loss;//负收益周期数
    public List<Double> profitList;//记录每次正收益
    public List<Double> lossList;//记录每次负收益
    public double strategyWinRate;//策略胜率

    public static double interval = 0.5;

    private double[] bound;

    private int[] proArr;
    private int[] losArr;

//    public CChartVO(){  //test
//        this.profitList=new ArrayList<>();
//        this.lossList=new ArrayList<>();
//    }

    public CChartVO(StrategyVO vo){
        this.profit = vo.profit;
        this.loss=vo.loss;
        this.profitList=vo.profitList;
        this.lossList=vo.lossList;

        System.out.println(this.profit +" , " +this.loss);
        for(int i=0;i<this.profitList.size();i++){
            double d = doubleRound(this.profitList.get(i));
            this.profitList.set(i,d);
            System.out.println(d);
        }
        for(int i=0;i<this.lossList.size();i++){
            double d = doubleRound(this.lossList.get(i));
            this.lossList.set(i,d);
            System.out.println("-:"+d);
        }
        findBound();
        System.out.println("bound:"+getBound()[0]+".."+getBound()[1]);

        divideIntoArray();
    }

    public int[] getProArr(){return this.proArr;}

    public int[] getLosArr(){return this.losArr;}

    public double[] getBound(){return this.bound;}

    public void findBound(){   // 找两个List中数据的最大值，[0]正收益率最大 [1]负收益率最大
        double[] u = {0,0};
        for(double m:profitList)
            if(u[0]<m)
                u[0]=m;
        for(double n:lossList)
            if(u[1]>n)
                u[1]=n;
        this.bound=u;
    }

    public void divideIntoArray(){ //将零散的收益率划分到区间内，拟定 inteval 为一个区间

        int p_len = (int)(this.bound[0]/interval) + 1 ;
        int l_len = -(int)(this.bound[1]/interval) + 1 ;

        //初始化
        int[] proArr = new int[p_len];
        int[] losArr = new int[l_len];
        for(int i=0;i<p_len;i++)
            proArr[i]=0;
        for(int i=0;i<l_len;i++)
            losArr[i]=0;
        //统计
        for(double m:profitList){
            int tmp = (int)(m/interval);
            proArr[tmp]++;
        }
        this.proArr=proArr;
        for(double m:lossList){
            int tmp = -(int)(m/interval);
            losArr[tmp]++;
        }
        this.losArr=losArr;
    }


    private double doubleRound(double d){
        //*******四舍五入为两位小数
        BigDecimal bd1 = new BigDecimal(d);
        double nd1 = bd1.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        //*************
        return nd1;
    }

}
