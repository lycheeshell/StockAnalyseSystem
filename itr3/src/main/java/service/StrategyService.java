package service;

import entity.StrategyEntity;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by lienming on 2017/6/4.
 */
public interface StrategyService {

    /**
     * 查看策略参数
     * @param userName
     * @param code
     * @return
     */
    StrategyEntity viewStrategy(String userName, String code);

    /**
     * 保存策略
     * @param entity
     * @return
     * @throws RemoteException
     */
    boolean saveStrategy(StrategyEntity entity) throws RemoteException;

    /**
     * 获取策略历史
     * @param userName
     * @return
     * @throws RemoteException
     */
    List<StrategyEntity> getStrategyHistory(String userName) throws RemoteException;

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
    boolean deleteHistoryStrategies(List<StrategyEntity> entities) throws RemoteException;

    /** 迭代二 内容在 ApplyStrategy、StrategyForBChart */


}
