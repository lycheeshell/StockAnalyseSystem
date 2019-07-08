package dao;

import entity.MarketEntity;
import entity.StockEntity;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lienming on 2017/5/30.
 */
public interface Dao {

    /**
     * 将股票名称转换为股票编号
     *
     * @param stockName 股票名称
     * @return stockCode
     */
    String getTransferCode(String stockName);

    /**
     * 获取开始日期和结束日期之间一支股票的数据，剔除了其中所有交易量为0的日期
     */
    ArrayList<StockEntity> getOneStockInfo(Date startDate, Date endDate, String stock, boolean stockIsName) throws RemoteException;

    /**
     * 得到一日所有进行交易的股票
     * @param day
     * @return
     */
    List<StockEntity> getStockByDay(LocalDate day) throws RemoteException;

    //StockEntity getLastStock(int serial,int code,int year);

    /**
     * 获取"sh000300"沪深大盘的交易行情
     * @param begin
     * @param end
     * @return
     * @throws RemoteException
     */
    ArrayList<MarketEntity>  getOneStockInfoForMainK(Date begin, Date end) throws RemoteException;
}
