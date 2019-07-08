package service;

import vo.SelectVO;
import vo.StrategyVO;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by lienming on 2017/5/18.
 */
public interface ApplyStrategy   {
    /** 动量策略ac图*/
    StrategyVO SelectMomStrategy(boolean type, long money, LocalDate begin, LocalDate end, int holdTime, int formTime, SelectVO vo) throws RemoteException;

    /** 动量策略b图*/
    List<Double> forBChart(boolean type, long money, LocalDate begin, LocalDate end, int holdTime, int formTime, SelectVO vo) throws RemoteException;

    /** 均值策略ac图*/
    StrategyVO SelectAveStrategy(boolean type, long money, LocalDate begin, LocalDate end, int holdTime, int formTime, SelectVO vo) throws RemoteException;

    /** 均值策略b图*/
    List<Double> forBChartAve(boolean type, long money, LocalDate begin, LocalDate end, int holdTime, int formTime, SelectVO vo) throws RemoteException;

}
