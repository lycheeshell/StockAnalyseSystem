package dao;


import entity.MarketEntity;
import entity.StockEntity;
import entity.StrategyEntity;
import vo.StrategyVO;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by LZ on 2017/4/3.
 */
public interface StrategyDao {


    public LocalDate resetBegin(LocalDate begin);

    LocalDate resetEnd(LocalDate end);

    int getRealPeriod(LocalDate begin, LocalDate end);

    LocalDate findPastDaysByOffset(LocalDate begin, int i);

    StockEntity findStockByCodeAndDay(String code, LocalDate date) throws RemoteException;

    LocalDate findFutureDaysByOffset(LocalDate formEnd, int holdTime);

    List<MarketEntity> getMarketData(String pool, LocalDate end, LocalDate begin);

    List<String> getPool(String pool, boolean b,LocalDate begin, LocalDate end) throws RemoteException;//取股票池

    MarketEntity findMarketIndexByDate(String pool, LocalDate date);

    MarketEntity findMarketIndexByOffset(String pool, LocalDate date, int offset);

    StockEntity findStockByCodeAndOffset(String code, LocalDate date, int i) throws RemoteException;

    Map<String,Map<String, String>> getStcokSet();

    StockEntity findNearestStock(String code, LocalDate date);




    /**
     * 保存策略
     * @param entity
     * @return
     * @throws RemoteException
     */
    boolean saveStrategy(StrategyEntity entity) throws RemoteException ;

    /**
     * 查看策略参数
     * @param userName
     * @param code
     * @return
     */
    StrategyEntity viewStrategy(String userName, String code) ;

    /**
     * 获取策略历史
     * @param userName
     * @return
     * @throws RemoteException
     */
    List<StrategyEntity> getStrategyHistory(String userName) throws RemoteException ;

    /**
     * 得到个人历史策略的数量
     * @param userName
     * @return
     */
    int getHistoryNum(String userName) ;

    /**
     * 删除策略历史
     * @param entities
     * @return
     */
    boolean deleteHistoryStrategies(List<StrategyEntity> entities) throws RemoteException ;

    StrategyVO findStrategy(String code) ;
}