package dao.impl;

import dao.StockDao;
import dao.StrategyDao;
import entity.StockDayEntity;
import entity.StockDetailEntity;
import entity.StockEntity;
import org.springframework.stereotype.Repository;
import sql.*;
import util.SDF;
import vo.NewRadaVO;
import vo.RadaVO;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lienming on 2017/6/4.
 */
@Repository
public class StockDaoImpl implements StockDao {

    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

    private static LocalDate ld=LocalDate.now();

    private static Map<String, List<NewRadaVO>> map=new HashMap();

    /***
     * 根据股票编号和指定日期获取交易行情
     */
    @Override
    public StockEntity getStockEntity(String code, LocalDate date) {
        StockEntity se = null;
        String theDay = SDF.format(SDF.localDateToDate(date));
        StockSQL sql = new StockSQL();
        try {
            se = sql.getStockByCodeAndDate(code,theDay);
        }catch(RemoteException re) {
            re.printStackTrace();
        }
        if(se == null) {
            try {
                StrategyDao sdao = StrategyDaoImpl.getInstance();
                String[] array = theDay.split("-");
                int year = Integer.parseInt(array[0]);
                int month = Integer.parseInt(array[1]);
                int daysql = Integer.parseInt(array[2]);
                LocalDate sLocalDate = LocalDate.of(year, month, daysql);
                LocalDate ld =  sdao.resetEnd(sLocalDate);
                se = sql.getStockByCodeAndDate(code,ld.toString());
            } catch(RemoteException re) {
                re.printStackTrace();
            }
        }
        return se ;
    }

    /***
     * 根据股票板块和指定日期获取交易行情
     * @param pool
     * @param date
     * @return
     */
    @Override
    public List<StockDayEntity> getBenchStockEntity(String pool, LocalDate date ) {

        StockSQL sql = new StockSQL();
        List<StockDayEntity>  li = new ArrayList<StockDayEntity>();
        try {
            li = sql.getBenchStockDayEntity(pool, date);
        }catch (RemoteException re) {
            re.printStackTrace();
        }

        return li;

    }

    /** 获取概念板块（业务板块）中，
     * 按股票数量由多到少排列的
     * 第rank个板块的股票代号(code)列表  (rank=1,2,3,...,20)
     * 最多查到20
     */
    @Override
    public List<String> getConceptBenchList(int rank) {
        List<String> list = new ArrayList<String>();
        StockCodeSQL sql = new StockCodeSQL();
        try{
            String indus = sql.getStockTypeNameByOrder(rank);
            list = sql.getStockNameByIndustry(indus);
        }catch(RemoteException re) {
            re.printStackTrace();
        }
        return list ;
    }

    /**
     * 根据概念板块数量排名
     * @param rank
     * @return
     */
    public String getConceptBenchName(int rank) {
        String indus = null;
        StockCodeSQL sql = new StockCodeSQL();
        try{
            indus = sql.getStockTypeNameByOrder(rank);
        }catch(RemoteException re) {
            re.printStackTrace();
        }
        return indus;
    }

    /** 给定 查询日期,股票代号,查询数据类型(open,close,high,low,amount,volume)
     *  返回 股票在其所在板块的排名百分比 (1/2/3/4/...)
     */
    @Override

    public double getRank(LocalDate localDate,String code,String type) {
        double rate = 0;
        LocalDate date = null;
        BDaySQL bdsql = new BDaySQL();
        StockSQL ssql = new StockSQL();
        try{
            date = bdsql.rightBDay(localDate);
            rate = ssql.getStockRank(date, code, type);
        } catch(RemoteException re) {
            re.printStackTrace();
        }

        return rate ;
    }

//    @Override
//    public StockDetailEntity findStock(String stockCode) {
//        StockDetailEntity sde = null;
//        try {
//            StockCodeSQL scsql = new StockCodeSQL();
//            sde = scsql.getOneStock(stockCode);
//        }catch(RemoteException re) {
//            re.printStackTrace();
//        }
//        return sde ;
//    }

    /**
     * 获得个人关注股票列表
     * @param userName
     * @return
     */
    @Override
    public List<String> getPersonFocus(String userName){
        List<String> list = null;
        try {
            PersonFocusSQL pfsql = new PersonFocusSQL();
            list =pfsql.getUserFocusStockCode(userName);
        }catch(RemoteException re) {
            re.printStackTrace();
        }
        return list ;
    }

    @Override
    /** 把stockCodes里面的所有添加进去 */
    public boolean setPersonFocus(List<String> stockCodes, String userid){
        try {
            PersonFocusSQL pfsql = new PersonFocusSQL();
            for(int i=0;i<stockCodes.size();i++) {
                pfsql.insert(stockCodes.get(i), userid);
                 //stockDetailEntity 关注度+1
                setStockFocus(true,stockCodes.get(i));
            }
        }catch(RemoteException re) {
            re.printStackTrace();
            return false;
        }
        return true ;
    }

    /**
     * 删除一支关注股票
     * @param StockCodes
     * @param userName
     * @return
     */
    @Override
    public boolean deleteFocusStock(List<String> StockCodes, String userName){
        try {
            PersonFocusSQL pfsql = new PersonFocusSQL();
            for(int i=0;i<StockCodes.size();i++) {
                pfsql.delete(StockCodes.get(i), userName);
                //stockDetailEntity 关注度+1
                setStockFocus(false,StockCodes.get(i));
            }
        }catch(RemoteException re) {
            re.printStackTrace();
        }
        return true ;
    }

    /**
     * 获得三个板块（主板、中小板、创业板）的股票详细信息
     * @return
     */
    @Override
    public List<StockDetailEntity> getAllStock( ) {
        List<StockDetailEntity> list = new ArrayList<>() ;
        list.addAll(getPoolStock("M"));
        list.addAll(getPoolStock("S"));
        list.addAll(getPoolStock("G"));
        return list ;
    }

    /**
     * 获得其中一个板块（主板、中小板、创业板）的股票详细信息
     * @param pool
     * @return
     */
    @Override
    public  List<StockDetailEntity> getPoolStock(String pool)  {

        List<StockDetailEntity> list = new ArrayList<>() ;
        try {
            StockCodeSQL scsql = new StockCodeSQL();
            list = scsql.getAllStockCode(pool);
        }catch(RemoteException re) {
            re.printStackTrace();
        }

        return list ;
    }

    /**
     * 根据股票编号获取股票详细信息
     * @param code
     * @return
     */
    @Override
    public StockDetailEntity getStockInfo(String code) {
        StockDetailEntity sde = null;
        try {
            StockCodeSQL scsql = new StockCodeSQL();
            sde = scsql.getOneStock(code);
        }catch(RemoteException re) {
            re.printStackTrace();
        }
        return sde ;
    }

    /**
     * 根据股票编号获取股票的雷达图信息
     * @param code
     * @return
     */
    @Override
    public RadaVO getStockRada(String code) {
        RadaSQL rsql = new RadaSQL();
        RadaVO vo = null;
        try {
            vo = rsql.getOneStock(code);
        }catch(RemoteException re) {
            re.printStackTrace();
            return null;
        }
        return vo ;
    }

    /**
     * 获取某个股票板块（主板、中小板、创业板），指定日期，指定数据的最大值
     * @param valueKey 指定数据，"open" "close" "high" "low" "volume" "amount"
     * @param date  指定日期
     * @param pool  指定板块
     * @return
     */
    @Override
    public double getMaxValueForStock(String valueKey,LocalDate date,String pool ) {
        StockSQL ssql = new StockSQL();
        String result = "";
        try {
            result = ssql.getMaxValue(valueKey, date, pool);
        }catch(RemoteException re) {
            re.printStackTrace();
        }
        double d = Double.parseDouble(result);
        return d;
    }

    /**
     * 获取指定板块（主板、中小板、创业板）在雷达图某维数据上的最大值
     * @param pool  指定板块
     * @return
     */
    @Override
    public double[] getMaxValueForRada(String pool)  {
        double[] result = null;
        RadaSQL rsql = new RadaSQL();
        try{
            result = rsql.getPoolStock(pool);
        } catch (RemoteException re) {
            re.printStackTrace();
        }

        return result;
    }

    /***
     * 判断一个日期是否是交易日
     * @param date  指定板块
     * @return
     */
    @Override
    public boolean isBuyDay(LocalDate date) {
        boolean result = false;
        BDaySQL sql = new BDaySQL();
        try {
            result = sql.isBDays(date);
        }catch(RemoteException re) {
            re.printStackTrace();
        }
        return result;
    }

    /**
     * 给定 需要数据的排名rank，指定数据，日期，股票板块获得一条交易行情
     * @param rank
     * @param valueKey 指定数据，"open" "close" "high" "low" "volume" "amount","pchange"(涨跌幅)
     * @param date  指定日期
     * @param pool  指定板块
     * @return
     */
    @Override
    public StockDayEntity getMaxEntityForStock(int rank , String valueKey,LocalDate date,String pool){
        StockEntity se = null;
        StockDayEntity sde = null;
        StockSQL ssql = new StockSQL();
        StockCodeSQL scsql = new StockCodeSQL();
        try{
            se = ssql.getMaxEntityForStock(rank,valueKey,date,pool);

            String name = scsql.getTransferName(se.getCode());
            sde = new StockDayEntity(se.getCode(),se.getDate(),se.getOpen(),se.getClose(),se.getHigh(),se.getLow(),se.getVolume(),se.getAmount(),name);
        } catch(RemoteException re) {
            re.printStackTrace();
        }
        return sde ;
    }

    /**
     * 获取指定日期 指定股票编号的 涨跌幅
     * @param localDate
     * @param stockcode
     * @return
     */
    @Override
    public double getStockUp(LocalDate localDate,String stockcode) {

        StockSQL ssql = new StockSQL();
        double d = 0;
        try {
            d = ssql.getPChangeByDayAndCode(localDate,stockcode);
        }catch(RemoteException re) {
            re.printStackTrace();
        }
        return d;
    }

    /**
     * 修改某个股票的关注度
     * @param isAdd
     * @param stockCode
     */
    @Override
    public void setStockFocus(boolean isAdd,String stockCode) {
        StockCodeSQL sql = new StockCodeSQL();
        try {
            sql.setStockFocus(isAdd, stockCode);
        }catch(RemoteException re) {
            re.printStackTrace();
        }
    }

    /**
     * 根据概念板块的名字，指定日期，获得其所含所有股票当天的交易行情
     * @param benchName
     * @param localDate
     * @return
     */
    @Override
    public List<StockDayEntity> get_SDE_forConceptBench(String benchName,LocalDate localDate){
        StockSQL sql = new StockSQL();
        List<StockDayEntity>  li = new ArrayList<StockDayEntity>();
        try {
            li = sql.getBenchStockDayEntityIndustry(benchName, localDate.toString());
        }catch (RemoteException re) {
            re.printStackTrace();
        }

        return li;
    }

    /**
     * 给定日期，返回股票详细信息
     * @param localDate
     * @return
     */
    @Override
    public Map<String, List<NewRadaVO>> getNewRada(LocalDate localDate) {
        if(map.size()==0||localDate.isAfter(ld)){
            ld=localDate;
            RadaSQL rsql = new RadaSQL();
            map=rsql.getNewRada(localDate);
            System.out.println("该死，需要重新获取股票信息！");
        }
        return map;
    }
}
