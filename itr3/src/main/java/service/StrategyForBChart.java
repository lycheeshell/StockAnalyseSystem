package service;

import vo.BChartVO;
import vo.SelectVO;

import java.rmi.RemoteException;
import java.time.LocalDate;

/**
 * Created by lienming on 2017/5/18.
 */
public interface StrategyForBChart {
    BChartVO SelectMomStrategy(boolean type, LocalDate begin, LocalDate end, boolean isHoldingPeriod, int Time, SelectVO vo) throws RemoteException;


    BChartVO SelectAveStrategy(boolean type,LocalDate begin, LocalDate end, int time, SelectVO vo) throws RemoteException;
}
