package service.impl;

import dao.Dao;
import dao.StrategyDao;
import entity.StockEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.CompareService;
import vo.CompareTwoStock;

import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lienming on 2017/5/18.
 */
@Service
public class CompareServiceImpl implements CompareService {
    @Autowired
    private Dao dao;
    @Autowired
    private StrategyDao strategyDao ;

    private ArrayList<StockEntity> stockList1;
    private ArrayList<StockEntity> stockList2;

    @Override
    public CompareTwoStock getCompareTwoStock(Date beginDate, Date endDate, String stock1, String stock2, boolean flg1,
                                              boolean flg2) {

        CompareTwoStock compareTwoStock = new CompareTwoStock();

        try {
            stockList1 = dao.getOneStockInfo(beginDate, endDate, stock1, flg1);
            stockList2 = dao.getOneStockInfo(beginDate, endDate, stock2, flg2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if(stockList1 == null || stockList2 == null) {
            return null;
        }
        //stockList倒过来
        ArrayList<StockEntity> l1 = new ArrayList<>();
        ArrayList<StockEntity> l2 = new ArrayList<>();
        for(int i=stockList1.size()-1 ; i>=0 ; i-- )
            l1.add(stockList1.get(i));
        for(int i=stockList2.size()-1 ; i>=0 ; i-- )
            l2.add(stockList2.get(i));
        stockList1 = l1 ;
        stockList2 = l2 ;


//        ArrayList<StockEntity> temp1 = new ArrayList<StockEntity>();
//        ArrayList<StockEntity> temp2 = new ArrayList<StockEntity>();
//        for(int i=stockList1.size()-1;i>=0;i--) {
//            temp1.add(stockList1.get(i));
//        }
//        for(int i=stockList2.size()-1;i>=0;i--) {
//            temp2.add(stockList2.get(i));
//        }
//        stockList1 = temp1;
//        stockList2 = temp2;
        int days = 0;
        if(stockList1.size() >= stockList2.size()) {
            days = stockList1.size();
            for(int i=0;i<days&&stockList2.size()<days;i++) {
                if(!stockList1.get(i).getDate().isEqual(stockList2.get(i).getDate())) {
                    if(i>=1) {
                        stockList2.add(i, stockList2.get(i - 1));
                    } else {
                        stockList2.add(i, stockList2.get(0));
                    }
                }
            }
        } else {
            days = stockList2.size();
            for(int i=0;i<days&&stockList1.size()<days;i++) {
                if(!stockList2.get(i).getDate().isEqual(stockList1.get(i).getDate())) {
                    if(i>=1) {
                        stockList1.add(i, stockList1.get(i - 1));
                    } else {
                        stockList1.add(i, stockList1.get(0));
                    }
                }
            }
        }

        double low1 = stockList1.get(0).getLow();
        double low2 = stockList2.get(0).getLow();
        double high1 = stockList1.get(0).getHigh();
        double high2 = stockList2.get(0).getHigh();
        for(int i=1;i<days;i++) {
            double tempLow1 = stockList1.get(i).getLow();
            if(tempLow1 < low1) {
                low1 = tempLow1;
            }
            double tempHigh1 = stockList1.get(i).getHigh();
            if(tempHigh1 > high1) {
                high1 = tempHigh1;
            }
        }
        for(int i=1;i<stockList2.size();i++) {
            double tempLow2 = stockList2.get(i).getLow();
            if(tempLow2 < low2) {
                low2 = tempLow2;
            }
            double tempHigh2 = stockList2.get(i).getHigh();
            if(tempHigh2 > high2) {
                high2 = tempHigh2;
            }
        }
        compareTwoStock.setLow1(low1);
        compareTwoStock.setLow2(low2);
        compareTwoStock.setHigh1(high1);
        compareTwoStock.setHigh2(high2);

        double firstDayPrice1 = stockList1.get(0).getClose();
        double lastDayPrice1 = stockList1.get(days-1).getClose();
        double firstDayPrice2 = stockList2.get(0).getClose();
        double lastDayPrice2 = stockList2.get(stockList2.size()-1).getClose();

        double range1 = (lastDayPrice1 - firstDayPrice1) / firstDayPrice1;
        double range2 = (lastDayPrice2 - firstDayPrice2) / firstDayPrice2;

        DecimalFormat df = new DecimalFormat("#.00");
        range1 = Double.valueOf(df.format(range1));
        range2 = Double.valueOf(df.format(range2));

        compareTwoStock.setRange1(range1);
        compareTwoStock.setRange2(range2);

        ArrayList<Double> settlement1 = new ArrayList<Double>();
        ArrayList<Double> settlement2 = new ArrayList<Double>();
        for(int i=1;i<days;i++) {
            Double endPrice1 = new Double(stockList1.get(i).getClose());
            settlement1.add(endPrice1);
        }
        for(int i=1;i<stockList2.size();i++) {
            Double endPrice2 = new Double(stockList2.get(i).getClose());
            settlement2.add(endPrice2);
        }
        compareTwoStock.setSettlement1(settlement1);
        compareTwoStock.setSettlement2(settlement2);

        ArrayList<Double> logarithmic1 = new ArrayList<Double>();
        ArrayList<Double> logarithmic2 = new ArrayList<Double>();
        for(int i=1;i<days;i++) {
            double d1 = Math.log(stockList1.get(i).getClose() / stockList1.get(i-1).getClose());
            Double dd1 = new Double(d1);
            logarithmic1.add(dd1);
        }
        for(int i=1;i<stockList2.size();i++) {
            double d2 = Math.log(stockList2.get(i).getClose() / stockList2.get(i-1).getClose());
            Double dd2 = new Double(d2);
            logarithmic2.add(dd2);
        }
        compareTwoStock.setLogarithmic1(logarithmic1);
        compareTwoStock.setLogarithmic2(logarithmic2);

        double average1 = 0;
        double average2 = 0;
        for(int i=0;i < logarithmic1.size();i++) {
            average1 = average1 + logarithmic1.get(i).doubleValue();
        }
        for(int i=0;i < logarithmic2.size();i++) {
            average2 = average2 + logarithmic2.get(i).doubleValue();
        }
        average1 = average1 / logarithmic1.size();
        average2 = average2 / logarithmic2.size();
        double all1 = 0;
        double all2 = 0;
        for(int i=0;i<logarithmic1.size();i++) {
            all1 = all1 + Math.pow(logarithmic1.get(i).doubleValue() - average1,2);
        }
        for(int i=0;i<logarithmic2.size();i++){
            all2 = all2 + Math.pow(logarithmic2.get(i).doubleValue() - average2,2);
        }
        double variance1 = all1 / logarithmic1.size();
        double variance2 = all2 / logarithmic2.size();
        compareTwoStock.setVariance1(variance1);
        compareTwoStock.setVariance2(variance2);

        ArrayList<LocalDate> dates = new ArrayList<LocalDate>();
        for(int i=1;i<days;i++) {
            dates.add(stockList1.get(i).getDate());
        }
        compareTwoStock.setDate(dates);

        return compareTwoStock;
    }


}
