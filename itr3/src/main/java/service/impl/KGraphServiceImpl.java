package service.impl;

import dao.Dao;
import dao.StrategyDao;
import entity.MarketEntity;
import entity.StockEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.KGraphService;
import util.SDF;
import vo.KGraph;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by lienming on 2017/5/18.
 */
@Service
public class KGraphServiceImpl implements KGraphService {
    @Autowired
    private Dao dao;
    @Autowired
    private StrategyDao strategyDao;


    private ArrayList<StockEntity> stockList;

    private SimpleDateFormat sdf;

    @Override
    public KGraph getKGraph(Date beginDate, Date endDate, String stock, boolean flag)
    {

        KGraph KInfo=new KGraph();
        try {
            stockList=dao.getOneStockInfo(beginDate, endDate, stock, flag);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(stockList ==null){
            return null;
        }
        sdf = new SimpleDateFormat("M/d/yy");

        String[] xDateAxis=new String[stockList.size()];
        int ptr=0;

        Map<String,double[]> stockMap=new HashMap<String, double[]>();
        double[][] stockInfo=new double[stockList.size()][4];//分别对应最高价  开盘价  最低价 收盘价
        LocalDate find=null;
        for(int i=stockList.size()-1;i>=0;i--)
        {
            stockInfo[i][0]=stockList.get(i).getAdjOpen();
            stockInfo[i][1]=stockList.get(i).getAdjClose();
            stockInfo[i][2]=stockList.get(i).getAdjLow();
            stockInfo[i][3]=stockList.get(i).getAdjHigh();

            find=stockList.get(i).getDate();

            xDateAxis[ptr++]=find.toString();

            stockMap.put(find.toString(), stockInfo[i]);
        }
        KInfo.setDateAxis(xDateAxis);
        KInfo.setMap(stockMap);
        return KInfo;
    }

    public KGraph getMainK(Date oneYearAgo , Date today  ) {
        List<MarketEntity> m_list = null  ;
        LocalDate bg = SDF.dateToLocalDate(oneYearAgo) ;
        bg = strategyDao.resetBegin(bg);
       oneYearAgo = SDF.localDateToDate(bg) ;

        LocalDate ed = SDF.dateToLocalDate(today) ;
        ed = strategyDao.resetEnd(ed);
        today = SDF.localDateToDate(ed) ;
        System.out.println("serviceimpl: "+bg+ " , " + ed);

        KGraph KInfo=new KGraph();
        try {
            m_list = dao.getOneStockInfoForMainK(oneYearAgo, today);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(m_list ==null){
            return null;
        }
        //sdf = new SimpleDateFormat("M/d/yy");

        String[] xDateAxis=new String[m_list.size()];
        int ptr=0;

        Map<String,double[]> stockMap=new HashMap<String, double[]>();
        double[][] stockInfo=new double[m_list.size()][4];//分别对应最高价  开盘价  最低价 收盘价
        LocalDate find=null;
        for(int i=m_list.size()-1;i>=0;i--)
        {
            stockInfo[i][0]=m_list.get(i).getOpen();
            stockInfo[i][1]=m_list.get(i).getClose();
            stockInfo[i][2]=m_list.get(i).getLow();
            stockInfo[i][3]=m_list.get(i).getHigh();

            find=m_list.get(i).getDate();

            xDateAxis[ptr++]=find.toString();

            stockMap.put(find.toString(), stockInfo[i]);
        }
        KInfo.setDateAxis(xDateAxis);
        KInfo.setMap(stockMap);
        return KInfo;
    }
}
