package service;

import vo.ConceptDayInfo;
import vo.DayInfo;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by lienming on 2017/5/18.
 */
public interface MarketService {
    /**
     * 迭代一 市场温度计
     * @param date
     * @param isToday
     * @return
     * @throws RemoteException
     */
    DayInfo getDayInfo(LocalDate date, boolean isToday) throws RemoteException;

    /**
     * 迭代三 概念板块 （如 "软件产业" ）的市场温度气泡
     * @param date
     * @param isToday
     * @return
     * @throws RemoteException
     */
    List<ConceptDayInfo> getPlateDayInfo(LocalDate date, boolean isToday) throws RemoteException;

}
