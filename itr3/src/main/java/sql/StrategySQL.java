package sql;

import entity.StrategyEntity;

import java.rmi.RemoteException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zongkan on 2017/6/7.
 */
public class StrategySQL {

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
     * 在数据库中增加一个用户的策略记录
     *
     * @param po StrategyEntity型，新增的策略记录
     * @return
     */
    public void insert(StrategyEntity po) throws RemoteException {
        init();
        System.out.println("name:" + po.getUserName()
        +", type:" + po.getType()
        +", startdate:" +po.getStartDate().toString()
        +", enddate:" + po.getEndDate().toString()
        +", bankuai:" + po.getBankuai()
        +", hasst:" + po.getHasST()
        +", userchosenstocks: "+po.getUserChosenStocks()
        +", money: " + po.getMoney()
        +", code:" + po.getCode()
        +",holdtime:" + po.getHoldTime()
        +",formtime:"+ po.getFormTime());
        String sql="insert into [Strategy] values(N'"+po.getUserName()+"','"+po.getRealType()+"','"+po.getStartDate()+"','"+po.getEndDate()+"','"+po.getRealBankuai()+"','"+po.getHasST()+"','"+po.getUserChosenStocks()+"','"+po.getMoney()+"','"+po.getCode()+"','"+po.getHoldTime()+"','"+po.getFormTime()+"')";
        if(po.getUserChosenStocks().equals("")) {
            sql="insert into [Strategy] values(N'"+po.getUserName()+"','"+po.getRealType()+"','"+po.getStartDate()+"','"+po.getEndDate()+"','"+po.getRealBankuai()+"','"+po.getHasST()+"',null,'"+po.getMoney()+"','"+po.getCode()+"','"+po.getHoldTime()+"','"+po.getFormTime()+"')";
        }
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
     * 在数据库中删除一个历史策略
     *
     * @param po StrateEntity型，删除的历史策略
     * @return
     */
    public void delete(StrategyEntity po) throws RemoteException {
        init();
        String sql="delete from [Strategy] where username=N'"+po.getUserName()+"' and code='" + po.getCode()+"'";
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

    /**
     * 得到用户的所有历史策略的参数的列表
     * @param name String型，股票的代码
     * @return 返回用户的所有历史策略的参数的列表
     */
    public List<StrategyEntity> getStrategyHistoryByUsername(String name) throws RemoteException{
        List<StrategyEntity> list = new ArrayList<StrategyEntity>();
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select * from [Strategy] where username=N'"+name+"'");
            while(rs.next()){
                String sName = rs.getString("username");
                String sType = rs.getString("type");

                Date sSDate = rs.getDate("startdate");
                String str1 = sSDate.toString();
                String[] array1 = str1.split("-");
                int year1 = Integer.parseInt(array1[0]);
                int month1 = Integer.parseInt(array1[1]);
                int day1 = Integer.parseInt(array1[2]);
                LocalDate sStartDate = LocalDate.of(year1, month1, day1);

                Date sEDate = rs.getDate("enddate");
                String str2 = sEDate.toString();
                String[] array2 = str2.split("-");
                int year2 = Integer.parseInt(array2[0]);
                int month2 = Integer.parseInt(array2[1]);
                int day2 = Integer.parseInt(array2[2]);
                LocalDate sEndDate = LocalDate.of(year2, month2, day2);

                int sBankuai = rs.getInt("bankuai");
                int sHasST = rs.getInt("hasst");
                String sUserChosenStocks = rs.getString("userchosenstocks");
                double sMoney= (double)rs.getFloat("money");
                String sCode = rs.getString("code");
                int sHoldTime = rs.getInt("holdtime");
                int sFormTime = rs.getInt("formtime");


                StrategyEntity se = new StrategyEntity(sName, sType, sStartDate, sEndDate, sBankuai, sHasST, sUserChosenStocks, sMoney, sCode, sHoldTime, sFormTime);
                list.add(se);
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
     * 得到用户的所有历史策略的参数的列表
     */
    public StrategyEntity getStrategyByUsernameAndCode(String name, String code) throws RemoteException{
        StrategyEntity se = null;
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select * from [Strategy] where username=N'"+name+"' and code='" + code + "'");
            if(rs.next()){
                String sName = rs.getString("username");
                String sType = rs.getString("type");

                Date sSDate = rs.getDate("startdate");
                String str1 = sSDate.toString();
                String[] array1 = str1.split("-");
                int year1 = Integer.parseInt(array1[0]);
                int month1 = Integer.parseInt(array1[1]);
                int day1 = Integer.parseInt(array1[2]);
                LocalDate sStartDate = LocalDate.of(year1, month1, day1);

                Date sEDate = rs.getDate("enddate");
                String str2 = sEDate.toString();
                String[] array2 = str2.split("-");
                int year2 = Integer.parseInt(array2[0]);
                int month2 = Integer.parseInt(array2[1]);
                int day2 = Integer.parseInt(array2[2]);
                LocalDate sEndDate = LocalDate.of(year2, month2, day2);

                int sBankuai = rs.getInt("bankuai");
                int sHasST = rs.getInt("hasst");
                String sUserChosenStocks = rs.getString("userchosenstocks");
                double sMoney= (double)rs.getFloat("money");
                String sCode = rs.getString("code");
                int sHoldTime = rs.getInt("holdtime");
                int sFormTime = rs.getInt("formtime");


                se = new StrategyEntity(sName, sType, sStartDate, sEndDate, sBankuai, sHasST, sUserChosenStocks, sMoney, sCode, sHoldTime, sFormTime);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        finish();
        return se;
    }

    /**
     * 得到用户的所有历史策略的num
     * @param name String型，user
     * @return 返回用户的所有历史策略的num
     */
    public int getNumOfStrategy(String name) throws RemoteException{
        int num = 0;
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select count(*) from [Strategy] where username=N'"+name+"'");
            if(rs.next()){

                num = rs.getInt(1);

            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        finish();
        return num;
    }

}
