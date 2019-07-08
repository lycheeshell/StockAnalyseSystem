package dao.impl;

import dao.UserDao;
import entity.UserEntity;
import org.springframework.stereotype.Repository;
import sql.UserSQL;

import java.rmi.RemoteException;

/**
 * Created by lienming on 2017/6/4.
 */

@Repository
public class UserDaoImpl implements UserDao {

    /**
     * 查看数据库是否存在该用户名
     * @param userName
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean checkIfNameExist(String userName) throws RemoteException{

        boolean exist = false;
        UserSQL usql = new UserSQL();
        exist = usql.userExist(userName);
        return exist;
    }

    /**
     * 注册，往数据库插入
     * @param name
     * @param password
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean addNewUser(String name , String password) throws RemoteException{
        UserSQL usql = new UserSQL();
        usql.insert(name,password);
        return true ;
    }

    /**
     * 查看该用户信息是否合法，合法返回true
     * @param user
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean checkIfValid(UserEntity user) throws RemoteException {
        boolean valid = false;
        UserSQL usql = new UserSQL();
        valid = usql.userPasswordRight(user.getName(), user.getPassword());
        return valid ;
    }

    /**
     * 修改密码
     * @param newPassword
     * @param userName
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean updateInfo(String newPassword, String userName) throws RemoteException{
        UserSQL usql = new UserSQL();
        usql.update(userName,newPassword);
        return true ;
    }

    @Override
    /**
     * 得到用户密码
     * @param userName 用户名
     * @return 密码
     */
    public String getPersonInfo(String userName) throws RemoteException {
        String psd = "";
        UserSQL usql = new UserSQL();
        psd = usql.getUserPassword(userName);
        return psd ;
    }


}
