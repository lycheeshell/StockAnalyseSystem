package dao;

import entity.UserEntity;

import java.rmi.RemoteException;

/**
 * Created by lienming on 2017/6/4.
 */
public interface UserDao {

    /**
     * 查看数据库是否存在该用户名
     * @param userName
     * @return
     * @throws RemoteException
     */
    boolean checkIfNameExist(String userName) throws RemoteException;

    /**
     * 查看该用户信息是否合法，合法返回true
     * @param user
     * @return
     * @throws RemoteException
     */
    boolean checkIfValid(UserEntity user) throws RemoteException ;

    /**
     * 注册，往数据库插入
     * @param name
     * @param password
     * @return
     * @throws RemoteException
     */
    boolean addNewUser(String name , String password) throws RemoteException ;

    /**
     * 修改密码
     * @param newPassword
     * @param userName
     * @return
     * @throws RemoteException
     */
    boolean updateInfo(String newPassword, String userName) throws RemoteException ;

    /**
     * 得到用户密码
     * @param userName 用户名
     * @return 密码
     */
    String getPersonInfo(String userName) throws RemoteException;

}
