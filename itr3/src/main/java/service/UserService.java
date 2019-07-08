package service;

import entity.UserEntity;

import java.rmi.RemoteException;

/**
 * Created by lienming on 2017/6/4.
 */
public interface UserService {

    boolean register(String userName, String password) throws RemoteException;

    boolean checkIfNameExist(String userName) throws RemoteException;

    boolean checkIfValid(UserEntity user) throws RemoteException;

    boolean updateInfo(String newPassword, String userName) throws RemoteException; //实际仅仅是密码

    String getPersonInfo(String userName) throws RemoteException;


}
