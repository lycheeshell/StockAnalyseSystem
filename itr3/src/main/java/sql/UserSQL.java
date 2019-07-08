package sql;

import java.rmi.RemoteException;
import java.sql.*;

/**
 * Created by zongkan on 2017/6/5.
 */
public class UserSQL {

    private String driverName;
    private String dbURL;
    private String userName;
    private String userPwd;
    private Connection dbConn;
    /**
     * 连接数据库
     */
    private void init() {
        driverName="com.microsoft.sqlserver.jdbc.SQLServerDriver";
        dbURL="jdbc:sqlserver://localhost:1433;DatabaseName=Quantour";
        userName="sa";
        userPwd="123456";
        try{
            Class.forName(driverName);
            dbConn= DriverManager.getConnection(dbURL,userName,userPwd);
            System.out.println("连接数据库成功");
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("连接失败");
        }
    }
    /**
     * 关闭数据库的连接
     */
    private void finish(){
        if(dbConn!=null){
            try {
                dbConn.close();
                System.out.println("关闭成功");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("关闭失败");
            }
        }
    }

    /**
     * 按用户ID进行查找返回是否存在该用户
     *
     * @param id String型，用户id
     * @return 返回对应的用户是否存在
     */
    public boolean userExist(String id) throws RemoteException {
        boolean exist = false;
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select * from [User] where name=N'"+id+"'");
            int index = 0;
            while(rs.next()) {
                index ++;
                String userName = "";
                String userPassword = "";
                userName = rs.getString("name");
                userPassword = rs.getString("password");
                if (userName.length() >= 1 && userPassword.length() >= 1) {
                    exist = true;
                }
            }
            if(index == 0) {
                return false;
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("不存在");
            e.printStackTrace();
            return false;
        }
        finish();
        return exist;
    }

    /**
     * 按用户ID进行查找密码返回是否密码正确,前提是已经确定帐号正确
     *
     * @param id String型，用户id
     * @param psd String型，用户输入的密码
     * @return 返回是否密码正确
     */
    public boolean userPasswordRight(String id, String psd) throws RemoteException {
        boolean right = false;
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select * from [User] where name=N'"+id+"'");
            if(rs.next()) {
                String userName = "";
                String userPassword = "";
                userName = rs.getString("name");
                userPassword = rs.getString("password");
                if (userName.length() >= 1 && userPassword.equals(psd)) {
                    right = true;
                }
            } else {
                return false;
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
            return false;
        }
        finish();
        return right;
    }

    /**
     * 在数据库中增加一个用户记录
     *
     * @param name String型，用户名
     * @param password String型，密码
     * @return
     */
    public void insert(String name, String password) throws RemoteException {
        init();
        String sql="insert into [User] values(N'"+name+"','"+password+"')";
        try {
            Statement st=dbConn.createStatement();
            int res=st.executeUpdate(sql);
            if(res==1){
                System.out.println("插入成功");
            }
            else{
                System.out.println("插入失败");
            }
            st.close();
        } catch (SQLException e) {
            System.out.println("插入失败");
            e.printStackTrace();
        }
        finish();
    }

    /**
     * 在数据库中更新一个用户记录
     *
     * @param name String型，用户名
     * @param password String型，密码
     * @return
     */
    public void update(String name, String password) throws RemoteException {
        init();
        String sql="update [User] set password='"+password+"' where name=N'"+name+"'";
        try {
            Statement st=dbConn.createStatement();
            int res=st.executeUpdate(sql);
            if(res==1){
                System.out.println("更新成功");
            }
            else{
                System.out.println("更新失败");
            }
            st.close();
        } catch (SQLException e) {
            System.out.println("更新失败");
            e.printStackTrace();
        }
        finish();
    }

    /**
     * 按用户ID进行查找密码
     *
     * @param id String型，用户id
     * @return 返回是否密码正确
     */
    public String getUserPassword(String id) throws RemoteException {
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select * from [User] where name=N'"+id+"'");
            if(rs.next()) {
                String userName = "";
                String userPassword = "";
                userName = rs.getString("name");
                userPassword = rs.getString("password");
                if (userName.length() >= 1) {
                    return userPassword;
                }
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        finish();
        return null;
    }

}
