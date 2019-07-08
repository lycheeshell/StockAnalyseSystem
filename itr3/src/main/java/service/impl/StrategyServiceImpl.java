package service.impl;

import dao.StrategyDao;
import entity.StrategyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.StrategyService;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by lienming on 2017/6/4.
 */
@Service
public class StrategyServiceImpl implements StrategyService {

    @Autowired
    private StrategyDao strategyDao ;

    @Override
    public List<StrategyEntity> getStrategyHistory(String userName) throws RemoteException {
        return strategyDao.getStrategyHistory(userName) ;
    }

    @Override
    public int getHistoryNum(String userName) {
        return strategyDao.getHistoryNum(userName);
    }

    @Override
    public StrategyEntity viewStrategy(String userName, String code){
        return strategyDao.viewStrategy(userName,code) ;
    }

    @Override
    public boolean saveStrategy(StrategyEntity entity) throws RemoteException {
        return strategyDao.saveStrategy(entity) ;
    }

    @Override
    public boolean deleteHistoryStrategies(List<StrategyEntity> entities) throws RemoteException{
        return strategyDao.deleteHistoryStrategies(entities);
    }
}
