package dao.impl;

import dao.Dao;
import entity.MarketEntity;
import entity.StockEntity;
import org.springframework.stereotype.Repository;
import sql.MarketSQL;
import sql.StockCodeSQL;
import sql.StockSQL;
import util.SDF;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Updated by lienming on 2017/4/2.
 */


@Repository
public class DaoImpl implements Dao {

    private static DaoImpl daoImpl;   //单例模式

    //private BufferedReader br;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    //private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DaoImpl() {
    }

    /**
     * 将股票名称转换为股票编号
     *
     * @param stockName 股票名称
     * @return stockCode
     */
    @Override
    public String getTransferCode(String stockName){
        StockCodeSQL scsql = new StockCodeSQL();
        return scsql.getTransferCode(stockName);
    }

    /**
     * 将股票编号转换为股票名称
     */
    public String getTransferName(String code) throws RemoteException {
        StockCodeSQL scsql = new StockCodeSQL();
        return scsql.getTransferName(code);
    }


    /**
     * 根据股票编号找所属板块的文件路径
     */
    public String findMarket(String stockCode) {
        String num = stockCode.substring(0, 3);
        String file;
        if (num.equals("300"))
            file = "Growth_Enter_Market";
        else if (num.equals("002"))
            file = "Small_Cap_Market";
        else
            file = "Main_Board_Market";
        return file;
    }

    /**
     * 获取开始日期和结束日期之间一支股票的数据，剔除了其中所有交易量为0的日期
     */
    @Override
    public ArrayList<StockEntity> getOneStockInfo(Date startDate, Date endDate, String stock, boolean stockIsName) throws RemoteException {

        ArrayList<StockEntity> list = new ArrayList<StockEntity>();

        if (stockIsName) //将名字转为编号
        {
            stock = getTransferCode(stock);
            if (stock == null)
                return null;
        }


        String startDateString = sdf.format(startDate);
        String endDateString = sdf.format(endDate);

        StockSQL ssql = new StockSQL();
        list = ssql.getStockListBetweenDay(stock, startDateString, endDateString);

        if (list.size() == 0)
            return null;
        return list;
    }


//    /**
//     * 获取能构成开始日期和结束日期之间一支股票均线图的po数据，剔除了其中所有交易量为0的日期
//     * 返回的list中最早的po数据是必然早于给定的开始日期的
//     * 1.如果给定的开始日期是停牌日，那么从开始日期往后推直到找到非停牌日：
//     * a.如果一个也没有则返回null
//     * b.如果存在则从这天计算
//     * 2.如果开始日期是非停牌日，那么从开始日期往前找num-1个非停牌日
//     */
//    public ArrayList<StockEntity> getOneStockInfoForAvg(Date startDate, Date endDate, String stock, boolean stockIsName, int numOfDay) throws RemoteException {
//
//        System.out.println("传说这个方法用不到，直接return null");
//        return null;
//    }

    /**
     * 得到一日所有进行交易的股票
     *
     * @param day
     * @return
     */
    @Override
    public List<StockEntity> getStockByDay(LocalDate day) throws RemoteException {
        List<StockEntity> list = new ArrayList<StockEntity>();
        String dateString = day.toString();

        StockSQL ssql = new StockSQL();
        list = ssql.getAllStockByDay(dateString);

        return list;
    }


    public static Dao getInstance() {
        if (daoImpl == null)
            daoImpl = new DaoImpl();
        return daoImpl;
    }

    /**
     * 获取"sh000300"沪深大盘的交易行情
     * @param begin
     * @param end
     * @return
     * @throws RemoteException
     */
    @Override
    public  ArrayList<MarketEntity>  getOneStockInfoForMainK(Date begin, Date end) throws RemoteException {
        ArrayList<MarketEntity> list = new ArrayList<MarketEntity>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startStr = sdf.format(begin);
        String[] array1 = startStr.split("-");
        int year1= Integer.parseInt(array1[0]);
        int month1 = Integer.parseInt(array1[1]);
        int day1 = Integer.parseInt(array1[2]);
        LocalDate date1 = LocalDate.of(year1, month1, day1);
        String endStr = sdf.format(end);
        String[] array2 = endStr.split("-");
        int year2= Integer.parseInt(array2[0]);
        int month2 = Integer.parseInt(array2[1]);
        int day2 = Integer.parseInt(array2[2]);
        LocalDate date2 = LocalDate.of(year2, month2, day2);
        MarketSQL sql = new MarketSQL();
        try{
            list = sql.getMarketInfoBetweenDay(date1,date2);
        }catch(RemoteException re) {
            re.printStackTrace();
        }
        return list;
    }
}

