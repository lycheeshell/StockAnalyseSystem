package service.impl;

import dao.Dao;
import dao.StockDao;
import dao.StrategyDao;
import entity.StockDayEntity;
import entity.StockEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.MarketService;
import vo.ConceptDayInfo;
import vo.DayInfo;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lienming on 2017/5/18.
 */
@Service
public class MarketServiceImpl implements MarketService {
    @Autowired
    private Dao dao;
    @Autowired
    private StrategyDao strategyDao ;
    @Autowired
    private StockDao stockDao ;

    private List<StockEntity> stockList;

    @Override
    /**
     * 得到每日大盘行情
     * @param date
     * @return
     */
    public DayInfo getDayInfo(LocalDate date, boolean isToday) throws RemoteException {
        //date = strategyDao.resetEnd(date);
        try {
            stockList=dao.getStockByDay(date);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return function_body(stockList,date,isToday);
    }

    /** lienming
     * 得到当天各个概念板块的涨停跌停股的个数，板块数量=20
     * @param date
     * @param isToday
     * @return
     * @throws RemoteException
     */
    public List<ConceptDayInfo> getPlateDayInfo( LocalDate date, boolean isToday) throws RemoteException {
        List<ConceptDayInfo> dayInfo_list = new ArrayList<>();
        List<String> name_list = new ArrayList<>();
        date = strategyDao.resetEnd(date);
        for(int i=1;i<=20;i++) {
            //reset
            name_list = stockDao.getConceptBenchList(i);
            String bench_name = stockDao.getConceptBenchName(i);
            //clear
            this.stockList=new ArrayList<>();
            List<String> remove_list = new ArrayList<>();

            List<StockDayEntity> l = stockDao.get_SDE_forConceptBench(bench_name,date) ;

            LocalDate yestoday = strategyDao.findPastDaysByOffset(date,1);
            List<StockDayEntity> yestoday_l = stockDao.get_SDE_forConceptBench(bench_name,yestoday);

            ConceptDayInfo cdi = null;

            cdi = function_body_forPlate(l,yestoday_l );

            cdi.name=bench_name;
            name_list.removeAll(remove_list);
            cdi.list=name_list;

            dayInfo_list.add(cdi);
        }


        return dayInfo_list;
    }


    DayInfo function_body ( List<StockEntity> stockList,LocalDate date, boolean isToday )throws RemoteException  {
      //  date = strategyDao.resetEnd(date);
        long totalVolume=0;//当日总交易量
        long limitUp=0;//涨停股票数
        long limitDown=0;//跌停股票数
        long notBad=0;//涨幅超过5%的股票数
        long notGood=0;//跌幅超过 5%的股票数
        long loss=0;//开盘‐收盘大于 5 % *上一个交易日收盘价的股票个数
        long profit=0;//开盘‐收盘小于‐ 5%*上一个交易日收盘价的股票个数
        for(int i=0;i<stockList.size();i++){
            totalVolume+=stockList.get(i).getVolume();
            if(isToday) {
                StockEntity lastDayStock = strategyDao.findStockByCodeAndOffset(stockList.get(i).getCode(),date,-1);
                if (lastDayStock != null) {
                    double this_close = stockList.get(i).getAdjClose() ;
                    double last_close = lastDayStock.getAdjClose() ;
                    double profitOrLoss = ( (this_close - last_close) / last_close ) * 100;
                    double b=10.00-0.01*100 / last_close;

                    double this_volume =  stockList.get(i).getVolume() ;
                    double this_open = stockList.get(i).getOpen() ;

                    if (profitOrLoss>=b) {
                        limitUp++;
                    }
                    if (profitOrLoss<=-b) {
                        limitDown++;
                    }
                    if (profitOrLoss > 5) {
                        if (profitOrLoss < 11) {
                            notBad++;
                        } else {
                            totalVolume -= this_volume;
                        }
                    }
                    if (profitOrLoss < -5) {
                        if (profitOrLoss > -11) {
                            notGood++;
                        } else {
                            totalVolume -= this_volume;
                        }
                    }
                    double bd = 0.05 * lastDayStock.getClose();
                    if ((this_open-this_close) > bd) {
                        profit++;
                    }
                    if (this_open-this_close < -bd) {
                        loss++;
                    }
                }
            }
        }
        DayInfo dayInfo=new DayInfo(totalVolume,limitUp,limitDown,notBad,notGood,loss,profit);
        return dayInfo ;
    }

    ConceptDayInfo function_body_forPlate (List<StockDayEntity> l,List<StockDayEntity> ye  ) throws RemoteException  {

        int up = 0;
        int down = 0;
        for (StockDayEntity sde : l) {
            String name = sde.getCode() ;
            double this_close = sde.getClose();
            for(StockDayEntity sde2 : ye) {
                if(name.equals(sde2.getCode()))
                {
                    double last_close = sde2.getClose();
                    double profitOrLoss = 100 * (this_close-last_close)/last_close ;
                    if (profitOrLoss >= 0)
                        up++;
                    else
                        down++;
                    break;
                }
            }
            System.out.println("tc : "+this_close +","+up+","+down);
        }

        boolean isUp;
        if (up >= down)
            isUp = true;
        else isUp = false;
        ConceptDayInfo cdi = new ConceptDayInfo();
        cdi.isUp = isUp;
        cdi.up_num = up;
        cdi.down_num = down;
        return cdi;
    }

}
