package sql;

import entity.MarketEntity;

import java.rmi.RemoteException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zongkan on 2017/6/5.
 */
public class MarketSQL {

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
     * 得到所选板块对应年份的大盘信息
     * @param year 年份
     * @param type 板块类型
     * @return 返回所选板块对应年份的大盘信息列表
     */
    public List<MarketEntity> getMarketInfoByYearAndType(int year, String type) throws RemoteException {
        List<MarketEntity> list = new ArrayList<MarketEntity>();
        String start = year + "-01-01";
        String end = year + "-12-31";
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select * from [Market] where date>='"+start+"' and date<='"+end+"' and right(code,6)='"+type+"' order by serial");
            while(rs.next()){
                int sSerial = rs.getInt("serial");

                Date sDate = rs.getDate("date");
                String str = sDate.toString();
                String[] array = str.split("-");
                int yearsql = Integer.parseInt(array[0]);
                int month = Integer.parseInt(array[1]);
                int day = Integer.parseInt(array[2]);
                LocalDate sLocalDate = LocalDate.of(yearsql, month, day);

                double sOpen = (double)rs.getFloat("open");
                double sClose = (double)rs.getFloat("close");
                double sHigh = (double)rs.getFloat("high");
                double sLow = (double)rs.getFloat("low");
                double sVolume = (double)rs.getFloat("volume");
                String sCode = rs.getString("code");
                MarketEntity me = new MarketEntity(sSerial,sLocalDate,sOpen,sClose,sHigh,sLow,sVolume,sCode);
                list.add(me);
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
     * 得到所选板块对应时间段的大盘信息
     */
    public ArrayList<MarketEntity> getMarketInfoBetweenDay(LocalDate d1, LocalDate d2) throws RemoteException {
        ArrayList<MarketEntity> list = new ArrayList<MarketEntity>();
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select * from [Market] where date>='"+d1.toString()+"' and date<='"+d2.toString()+"' and code='sh000300' order by date desc");
            while(rs.next()){
                int sSerial = rs.getInt("serial");

                Date sDate = rs.getDate("date");
                String str = sDate.toString();
                String[] array = str.split("-");
                int yearsql = Integer.parseInt(array[0]);
                int month = Integer.parseInt(array[1]);
                int day = Integer.parseInt(array[2]);
                LocalDate sLocalDate = LocalDate.of(yearsql, month, day);

                double sOpen = (double)rs.getFloat("open");
                double sClose = (double)rs.getFloat("close");
                double sHigh = (double)rs.getFloat("high");
                double sLow = (double)rs.getFloat("low");
                double sVolume = (double)rs.getFloat("volume");
                String sCode = rs.getString("code");
                MarketEntity me = new MarketEntity(sSerial,sLocalDate,sOpen,sClose,sHigh,sLow,sVolume,sCode);
                list.add(me);
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

}
