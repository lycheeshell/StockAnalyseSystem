package service.impl;

import dao.StrategyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.ApplyStrategy;
import service.StrategyForBChart;
import vo.BChartVO;
import vo.BItem;
import vo.SelectVO;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lienming on 2017/5/18.
 */
@Service
public class StrategyForBChartImpl implements StrategyForBChart {

    @Autowired
    private StrategyDao strategyDao ;
    @Autowired
    private ApplyStrategy as;

    @Override
    public BChartVO SelectMomStrategy(boolean type, LocalDate begin, LocalDate end, boolean isHoldingPeriod, int time, SelectVO vo) throws RemoteException {
        List<BItem> list=new ArrayList<>() ;
//        try {
          //  ApplyStrategy as=new ApplyStrategyImpl();
            if(isHoldingPeriod ){
                for(int holdTime=1;holdTime<=100;holdTime++){
                    //long t=System.currentTimeMillis() ;
                    List<Double> l=as.forBChart(type, 1000000,begin ,end,holdTime ,time ,vo);
                    if(l!=null&&(!l.isEmpty())) {
                        BItem bitem = new BItem(holdTime, l.get(0), l.get(1));
                        list.add(bitem);
                    }
                    //System .out.print("时间跨度为"+holdTime +"的迭代耗时为");
                    //System .out.println(System .currentTimeMillis() -t);
                }
            }
            else{
                for(int formTime=1;formTime<=100;formTime++){
                    //long t=System.currentTimeMillis() ;
                    List<Double> l=as.forBChart(type, 1000000,begin ,end,time ,formTime ,vo);
                    if(l!=null&&(!l.isEmpty())) {
                        BItem bitem = new BItem(formTime, l.get(0), l.get(1));
                        list.add(bitem);
                    }
                    //System .out.print("时间跨度为"+formTime +"的迭代耗时为");
                    //System .out.println(System .currentTimeMillis() -t);
                }
            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        BChartVO bvo=new BChartVO(isHoldingPeriod ,list) ;
        return bvo;
    }

    @Override
    public BChartVO SelectAveStrategy(boolean type,LocalDate begin, LocalDate end, int time, SelectVO vo) throws RemoteException {
        List<BItem> list=new ArrayList<>() ;
//        try {
          //  ApplyStrategy as=new ApplyStrategyImpl();
            for(int holdTime=1;holdTime<=100;holdTime++){
                List<Double> l=as.forBChartAve(type, 1000000,begin ,end,holdTime ,time ,vo);
                if(l!=null&&(!l.isEmpty())) {
                    BItem bitem = new BItem(holdTime, l.get(0), l.get(1));
                    list.add(bitem);
                }
            }

//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        BChartVO bvo=new BChartVO(false ,list) ; //表明是形成期，以持有期为横轴画图
        return bvo;
    }
}
