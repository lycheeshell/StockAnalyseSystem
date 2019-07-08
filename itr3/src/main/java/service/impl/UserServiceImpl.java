package service.impl;

import dao.UserDao;
import entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.UserService;

import java.rmi.RemoteException;

/**
 * Created by lienming on 2017/6/4.
 */
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserDao userDao ;

    /** 注册
     *  假设在web已经检查过输入合法
     * */
    @Override
    public boolean register(String userName, String password) throws RemoteException {
        if( checkIfNameExist(userName) )
            return false;
        return addNewUser(userName,password);
    }

    @Override
    public boolean updateInfo(String newPassword, String userName) throws RemoteException {
        return userDao.updateInfo(newPassword,userName) ;
    }


    @Override
    public String getPersonInfo(String userName) throws RemoteException {
        return userDao.getPersonInfo(userName) ;
    }

    /** 检查用户名是否已存在 */
    @Override
    public boolean checkIfNameExist(String userName) throws RemoteException {
        return userDao.checkIfNameExist(userName) ;
    }

    /** 登录检查密码是否正确 */
    @Override
    public boolean checkIfValid(UserEntity user) throws RemoteException {
        return userDao.checkIfValid(user) ;
    }

    /** 添加新用户 */
    boolean addNewUser(String userName , String password) throws RemoteException {
        return userDao.addNewUser(userName,password);
    }


}
