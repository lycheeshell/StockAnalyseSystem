package sql;

import vo.NewRadaVO;
import vo.RadaVO;

import java.rmi.RemoteException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zongkan on 2017/6/10.
 */
public class RadaSQL {

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
     * 得到单只股票的信息
     * @param code String型，股票的代码
     * @return 返回单只股票信息
     */
    public RadaVO getOneStock(String code) throws RemoteException {
        RadaVO rvo = null;
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select * from [Rada] where stockcode='"+code+"'");
            if(rs.next()) {
                String sCode = rs.getString("stockcode");
                String sName = rs.getString("stockname");
                double sChangeRate = (double)rs.getFloat("changerate");
                double sPb= (double)rs.getFloat("pb");
                double sProfit= (double)rs.getFloat("profit");
                double sUndp= (double)rs.getFloat("undp");
                double sMrq= (double)rs.getFloat("mrq");
                double sTotals= (double)rs.getFloat("totals");

                rvo = new RadaVO(sCode, sName, sChangeRate, sPb, sProfit, sUndp, sMrq, sTotals);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        finish();
        return rvo;
    }

    /**
     * 得到NewRadaVO
     */
    public Map<String,List<NewRadaVO>> getNewRada(LocalDate date){
        Map<String,List<NewRadaVO>> map = new HashMap<String,List<NewRadaVO>>();
        List<NewRadaVO> list = new ArrayList<NewRadaVO>();
        String code = "";
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select a.*,b.* from [Stock] a,[Rada] b where a.code=b.stockcode and a.date>='2017-06-02' order by a.code");
            while(rs.next()) {
                String sCode = rs.getString("code");

                Date sDate = rs.getDate("date");
                String str = sDate.toString();
                String[] array = str.split("-");
                int year = Integer.parseInt(array[0]);
                int month = Integer.parseInt(array[1]);
                int daysql = Integer.parseInt(array[2]);
                LocalDate sLocalDate = LocalDate.of(year, month, daysql);

                double sOpen = (double) rs.getFloat("open");
                double sClose = (double) rs.getFloat("close");
                double sHigh = (double) rs.getFloat("high");
                double sLow = (double) rs.getFloat("low");
                double sVolume = (double) rs.getFloat("volume");
                double sAmount = (double) rs.getFloat("amount");

                double pchange =(double)rs.getFloat("pchange");
                String sName = rs.getString("stockname");
                double sChangeRate = (double)rs.getFloat("changerate");
                double sPb= (double)rs.getFloat("pb");
                double sProfit= (double)rs.getFloat("profit");
                double sUndp= (double)rs.getFloat("undp");
                double sMrq= (double)rs.getFloat("mrq");
                double sTotals= (double)rs.getFloat("totals");
                double sOutStanding= (double)rs.getFloat("outstanding");
                double sEsp= (double)rs.getFloat("esp");
                double sBvps= (double)rs.getFloat("bvps");
                double sPerundp= (double)rs.getFloat("perundp");
                double sRev= (double)rs.getFloat("rev");
                double sGpr= (double)rs.getFloat("gpr");

                NewRadaVO vo = new NewRadaVO(sCode, sLocalDate, sOpen, sClose, sHigh, sLow, sVolume, sAmount,pchange, sName, sChangeRate, sPb, sProfit, sUndp, sMrq, sTotals, sOutStanding, sEsp, sBvps, sPerundp, sRev, sGpr);

                if(sCode.equals(code)) {
                    list.add(vo);
                } else {
                    map.put(code,list);
                    code = sCode;
                    list=new ArrayList<>();
                    list.add(vo);
                }
                map.remove("");
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        finish();
        return map;
    }

    /**
     * 得到股票池的信息
     * @param pool String型，股票的代码
     * @return 返回股票池信息
     */
    public double[] getPoolStock(String pool) throws RemoteException {
        double[] result = new double[6];
        String sqlpart = "";
        if(pool.equals("G")) {
            sqlpart = " where left(stockcode,3)='300'";
        } else if(pool.equals("S")) {
            sqlpart = " where left(stockcode,3)='002'";
        } else {
            sqlpart = " where left(stockcode,3)='000' or left(stockcode,3)='001'";
        }
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select max(changerate) from [Rada] " + sqlpart);
            if(rs.next()) {

                double chanS = (double)rs.getFloat(1);

                result[0] = chanS;
            }
            rs.close();
            rs=st.executeQuery("select max(pb) from [Rada] " + sqlpart);
            if(rs.next()) {

                double pbS = (double)rs.getFloat(1);

                result[1] = pbS;
            }
            rs.close();
            rs=st.executeQuery("select max(profit) from [Rada] " + sqlpart);
            if(rs.next()) {

                double proS = (double)rs.getFloat(1);

                result[2] = proS;
            }
            rs.close();
            rs=st.executeQuery("select max(undp) from [Rada] " + sqlpart);
            if(rs.next()) {

                double undpS = (double)rs.getFloat(1);

                result[3] = undpS;
            }
            rs.close();
            rs=st.executeQuery("select max(mrq) from [Rada] " + sqlpart);
            if(rs.next()) {

                double mrqS = (double)rs.getFloat(1);

                result[4] = mrqS;
            }
            rs.close();
            rs=st.executeQuery("select max(totals) from [Rada] " + sqlpart);
            if(rs.next()) {

                double toS = (double)rs.getFloat(1);

                result[5] = toS;
            }
            rs.close();

            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        finish();
        return result;
    }

}
