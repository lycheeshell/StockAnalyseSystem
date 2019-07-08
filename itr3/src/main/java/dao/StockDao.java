package dao;

import entity.StockDayEntity;
import entity.StockDetailEntity;
import entity.StockEntity;
import vo.NewRadaVO;
import vo.RadaVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by lienming on 2017/6/4.
 */
public interface StockDao {


    /** 获取某个日期 某只股票的交易情况，若不是交易日／没有数据 返回前一个交易日的数据 */
    StockEntity getStockEntity(String code, LocalDate date) ;

    /** 获取某个日期 某个板块所有股票的交易情况，若不是交易日／没有数据 返回前一个交易日的数据
     * pool : M - "000" or "001" , S - "002" , G - "300" 股票代码开头3位数字 */
    List<StockDayEntity> getBenchStockEntity(String pool, LocalDate date ) ;


    /** 获取概念板块（业务板块）中，
     * 按股票数量由多到少排列的
     * 第rank个板块的股票代号(code)列表  (rank=1,2,3,...,20)
     * 最多查到20
     */
    List<String> getConceptBenchList(int rank) ;

    /***
     * 根据上面那个注释的内容，返回概念板块的名称！
     */
    String getConceptBenchName(int rank) ;

    /** 给定 查询日期,股票代号,查询数据类型(open,close,high,low,amount,volume,pchange)
     *  返回 股票在其所在板块的排名 (比如   4/20  )
     */
    double getRank(LocalDate localDate,String code,String type);

   // StockDetailEntity findStock(String stockCode) ;

    /** 获得个人关注的股票列表 */
    List<String> getPersonFocus(String userName) ;

    /** 把stockCodes里面的所有添加进去 */
    boolean setPersonFocus(List<String> stockCodes, String userid);

    /**
     * 删除一支关注股票
     * @param StockCodes
     * @param userName
     * @return
     */
    boolean deleteFocusStock(List<String> StockCodes, String userName);

    /**
     * 获得三个板块（主板、中小板、创业板）的股票详细信息
     * @return
     */
    List<StockDetailEntity> getAllStock( ) ;

    /**
     * 获得其中一个板块（主板、中小板、创业板）的股票详细信息
     * @param pool
     * @return
     */
    List<StockDetailEntity> getPoolStock(String pool) ;

    /**
     * 根据股票编号获取股票详细信息
     * @param code
     * @return
     */
    StockDetailEntity getStockInfo(String code) ;

    /**
     * 根据股票编号获取股票的雷达图信息
     * @param code
     * @return
     */
    RadaVO getStockRada(String code) ;


    /***
     * 获得 指定板块 指定数据的最大值
     * @param valueKey 指定数据，"open" "close" "high" "low" "volume" "amount"
     * @param date  指定日期
     * @param pool  指定板块
     * @return String 格式
     */
    double getMaxValueForStock(String valueKey,LocalDate date,String pool) ;

    /***
     * 获得 指定板块 指定数据的最大值
     * @param pool  指定板块
     * @return String 格式
     */
    double[] getMaxValueForRada(String pool) ;

    /***
     * 判断是不是交易日
     * @param date  指定板块
     * @return 交易日返回true；不是交易日返回false
     */
    boolean isBuyDay(LocalDate date);


    /***
     * 获得 指定板块 指定数据的最大值的股票
     * @param valueKey 指定数据，"open" "close" "high" "low" "volume" "amount","pchange"(涨跌幅)
     * @param date  指定日期
     * @param pool  指定板块
     * @return String 格式
     */
    StockDayEntity getMaxEntityForStock(int rank,String valueKey,LocalDate date,String pool) ;


    /***
     * 获得某日期、给定股票编号的股票的涨幅
     */
    double getStockUp(LocalDate localDate,String stockcode);

    /** 调用的时候，股票关注度+1 / -1 */
    void setStockFocus(boolean isAdd,String stockCode);

    /** 给定日期 和 概念板块名称，返回该板块当天的交易行情 **/
    List<StockDayEntity> get_SDE_forConceptBench(String benchName,LocalDate localDate);

    /**给定日期，返回股票详细信息**/
    Map<String,List<NewRadaVO>> getNewRada(LocalDate localDate);
}
