package sql;

import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zongkan on 2017/6/5.
 */
public class PersonFocusSQL {

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
     * 按用户ID进行查找返回用户关注的股票代码列表
     *
     * @param id String型，用户id
     * @return 返回对应的用户是否存在
     */
    public List<String> getUserFocusStockCode(String id) throws RemoteException {
        List<String> list = new ArrayList<String>();
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select stockcode from [PersonFocus] where name=N'"+id+"'");
            while(rs.next()){
                String code = rs.getString("stockcode");
                list.add(code);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        finish();
        return list;
    }

    /**
     * 在数据库中增加一个用户的关注的股票
     *
     * @param code String型，新增的用户关注股票
     * @param userid String型，用户账号
     * @return
     */
    public void insert(String code,String userid) throws RemoteException {
        init();
        String sql="insert into [PersonFocus] values(N'"+userid+"','"+code+"')";
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
     * 在数据库中删除一个用户关注的股票
     *
     * @param code String型，新增的用户关注股票
     * @param userid String型，用户账号
     * @return
     */
    public void delete(String code,String userid) throws RemoteException {
        init();
        String sql="delete from [PersonFocus] where name=N'"+userid+"' and stockcode='" + code+"'";
        try {
            Statement st=dbConn.createStatement();
            int res=st.executeUpdate(sql);
            if(res==1){
                System.out.println("删除成功");
            }
            else{
                System.out.println("删除失败");
            }
            st.close();
        } catch (SQLException e) {
            System.out.println("删除失败");
            e.printStackTrace();
        }
        finish();
    }

}
