package sql;

import java.rmi.RemoteException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zongkan on 2017/6/6.
 */
public class BDaySQL {

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
     * 得到这一年的所有交易日
     *
     * @return 返回一年所有的交易日
     */
    public List<LocalDate> getBDays(int year) throws RemoteException {
        List<LocalDate> list = new ArrayList<LocalDate>();
        String start = year + "-01-01";
        String end = year + "-12-31";
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select bdate from [BDay] where bdate>='"+start+"' and bdate<='"+end+"'");
            while(rs.next()){
                Date sDate = rs.getDate("bdate");
                String str = sDate.toString();
                String[] array = str.split("-");
                int yearsql = Integer.parseInt(array[0]);
                int month = Integer.parseInt(array[1]);
                int day = Integer.parseInt(array[2]);
                LocalDate sLocalDate = LocalDate.of(yearsql, month, day);

                list.add(sLocalDate);
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
     * 是否是交易日
     */
    public boolean isBDays(LocalDate day) throws RemoteException {
        boolean is = false;
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select * from [BDay] where bdate='"+day.toString()+"'");
            if(rs.next()){
                is = true;
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        finish();
        return is;
    }

    /**
     * 确定交易日
     */
    public LocalDate rightBDay(LocalDate day) throws RemoteException {
        LocalDate ld = null;
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select * from [BDay] where bdate<='"+day.toString()+"' order by bdate desc");
            if(rs.next()){
                Date sDate = rs.getDate("bdate");
                String str = sDate.toString();
                String[] array = str.split("-");
                int yearsql = Integer.parseInt(array[0]);
                int month = Integer.parseInt(array[1]);
                int daysql = Integer.parseInt(array[2]);
                ld = LocalDate.of(yearsql, month, daysql);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        finish();
        return ld;
    }

}
