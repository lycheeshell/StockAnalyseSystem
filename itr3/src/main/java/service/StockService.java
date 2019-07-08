package service;

import entity.StockDayEntity;
import entity.StockDetailEntity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by lienming on 2017/6/4.
 */
public interface StockService {

    /** 获得个人关注的股票列表 */
    List<StockDetailEntity> getPersonFocus(String userName);

    /** 增加股票到个人关注列表 */
    boolean addFocusStock(List<String> stockCodes, String userName) ;

    /** 删除个人关注列表中的股票 */
    boolean deleteFocusStock(List<String> stockCodes, String userName);

    /** 获得所有股票公司的详细信息 */
    List<StockDetailEntity> getAllStock( ) ;

    /***获得 某个板块 所有股票的详细信息 */
    List<StockDetailEntity> getPoolStock(String pool);

    /** 获得股票公司的详细信息 */
    StockDetailEntity getStockInfo(String code) ;

    /** 获得单支股票的雷达图数据 */
    double[] getStockRada(String code) ;

    /** 获得指定日期所有大盘股票的交易情况 */
    List<StockDayEntity> getMainStock(Date date) ;

    /***
     * 股票评分 100分制
     * 过去5个交易日
     * open close high volume pchange
     * 开盘  收盘  最高  成交量   涨跌幅 在同板块股票中的排名 取均值
     * @param code
     * @return
     */
    double getStockValue(String code) ;

    String getTransferCode(String stockName);

    /***
     *  获得每日推荐股票
     *  5支
     */
    Map<String,Double> getRecommend();

    /** 指定日期 和 概念板块名称，返回该板块当天的交易行情 **/
    List<StockDayEntity> findSDE_forConceptBench ( String benchName,LocalDate localDate );

    /** 今日 和 概念板块名称，返回该板块当天的交易行情 **/
    List<StockDayEntity> getSDE_forConceptBench ( String conceptName) ;

}
