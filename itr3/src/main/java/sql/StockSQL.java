package sql;

import entity.StockDayEntity;
import entity.StockEntity;

import java.rmi.RemoteException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zongkan on 2017/6/5.
 */
public class StockSQL {

    long startTime;  //获取开始时间
    long endTime;  //获取结束时间

    private String driverName;
    private String dbURL;
    private String userName;
    private String userPwd;
    private Connection dbConn;
    /**
     * 连接数据库
     */
    private void init() {
        startTime = System.currentTimeMillis();
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
        endTime = System.currentTimeMillis();
        System.out.print("用时：" + (endTime - startTime) + " ms/n");    //输出程序运行时间
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
     * 得到最大值
     * @return 返回最大值
     */
    public String getMaxValue(String valueKey, LocalDate date, String pool) throws RemoteException{
        String outcome = "";
        String d = date.toString();
        String sql = "";
        if(pool.equals("G")) {
            sql = "select max(" + valueKey + ") from [Stock] where date='" + d + "' and left(code,3)='300'";
        } else if( pool.equals("S")) {
            sql = "select max(" + valueKey + ") from [Stock] where date='" + d + "' and left(code,3)='002'";
        } else {
            sql = "select max(" + valueKey + ") from [Stock] where date='" + d + "' and (left(code,3)='000' or left(code,3)='001')";
        }
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery(sql);
            if(rs.next()){
                double result = (double)rs.getFloat(1);
                outcome = result + "";
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        System.out.print("getMaxValue");
        finish();
        return outcome;
    }

    /**
     * 得到最大值entity
     * @return 返回最大值entity
     */
    public StockEntity getMaxEntityForStock(int rank, String valueKey, LocalDate date, String pool) throws RemoteException{
        StockEntity se = null;
        String c = "";
        String d = date.toString();
        String sql = "";
        int index = 0;
//        if(!(valueKey.equals("open") || valueKey.equals("close"))) {
//            if (pool.equals("G")) {
//                sql = "select a.* from [Stock] a where " + valueKey + "= (select max(" + valueKey + ") from [Stock] where date='" + d + "' and left(code,3)='300')";
//            } else if (pool.equals("S")) {
//                sql = "select a.* from [Stock] a where " + valueKey + "= (select max(" + valueKey + ") from [Stock] where date='" + d + "' and left(code,3)='002')";
//            } else {
//                sql = "select a.* from [Stock] a where " + valueKey + "= (select max(" + valueKey + ") from [Stock] where date='" + d + "' and (left(code,3)='000' or left(code,3)='001'))";
//            }
//        } else  if(valueKey.equals("pchange")) {
//            if (pool.equals("G")) {
//                sql = "select a.* from [Stock] a where date='" + d + "' and " + valueKey + "= (select max(" + valueKey + ") from [Stock] where date='" + d + "' and left(code,3)='300')";
//            } else if (pool.equals("S")) {
//                sql = "select a.* from [Stock] a where date='" + d + "' and " + valueKey + "= (select max(" + valueKey + ") from [Stock] where date='" + d + "' and left(code,3)='002')";
//            } else {
//                sql = "select a.* from [Stock] a where date='" + d + "' and " + valueKey + "= (select max(" + valueKey + ") from [Stock] where date='" + d + "' and (left(code,3)='000' or left(code,3)='001'))";
//            }
//        } else {
//            {
//                if(pool.equals("G")) {
//                    sql = "select a.* from [Stock] a where [" + valueKey + "]= (select max([" + valueKey + "]) from [Stock] where date='" + d + "' and left(code,3)='300')";
//                } else if( pool.equals("S")) {
//                    sql = "select a.* from [Stock] a where [" + valueKey + "]= (select max([" + valueKey + "]) from [Stock] where date='" + d + "' and left(code,3)='002')";
//                } else {
//                    sql = "select a.* from [Stock] a where [" + valueKey + "]= (select max([" + valueKey + "]) from [Stock] where date='" + d + "' and (left(code,3)='000' or left(code,3)='001'))";
//                }
//            }
//        }
        if(!(valueKey.equals("open") || valueKey.equals("close"))) {
            if (pool.equals("G")) {
                sql = "select code from [Stock] where date='" + d + "' and left(code,3)='300' order by " + valueKey + " desc";
            } else if (pool.equals("S")) {
                sql = "select code from [Stock] where date='" + d + "' and left(code,3)='002' order by " + valueKey + " desc";
            } else {
                sql = "select code from [Stock] where date='" + d + "' and (left(code,3)='000' or left(code,3)='001') order by " + valueKey + " desc";
            }
        } else {
            if (pool.equals("G")) {
                sql = "select code from [Stock] where date='" + d + "' and left(code,3)='300' order by [" + valueKey + "] desc";
            } else if (pool.equals("S")) {
                sql = "select code from [Stock] where date='" + d + "' and left(code,3)='002' order by [" + valueKey + "] desc";
            } else {
                sql = "select code from [Stock] where date='" + d + "' and (left(code,3)='000' or left(code,3)='001') order by [" + valueKey + "] desc";
            }
        }
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery(sql);
            while(rs.next()){
                index ++;
                if(index > rank) {
                    return null;
                }
                if(index == rank) {
                    c = rs.getString("code");
                    break;
                }
            }
            rs=st.executeQuery("select * from [Stock] where date='" + d + "' and code='" + c + "'");
            if(rs.next()){
                String sCode = rs.getString("code");

                Date sDate = rs.getDate("date");
                String str = sDate.toString();
                String[] array = str.split("-");
                int year = Integer.parseInt(array[0]);
                int month = Integer.parseInt(array[1]);
                int day = Integer.parseInt(array[2]);
                LocalDate sLocalDate = LocalDate.of(year, month, day);

                double sOpen = (double)rs.getFloat("open");
                double sClose = (double)rs.getFloat("close");
                double sHigh = (double)rs.getFloat("high");
                double sLow = (double)rs.getFloat("low");
                double sVolume = (double)rs.getFloat("volume");
                double sAmount = (double)rs.getFloat("amount");
                se = new StockEntity(sCode,sLocalDate,sOpen,sClose,sHigh,sLow,sVolume,sAmount);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        System.out.print("getMaxEntityForStock");
        finish();
        return se;
    }

    /**
     * 得到单只股票的所有实体的列表
     * @param stockCode String型，股票的代码
     * @return 返回单只股票的所有实体的列表
     */
    public ArrayList<StockEntity> getStockList(String stockCode) throws RemoteException{
        ArrayList<StockEntity> list = new ArrayList<StockEntity>();
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select * from [Stock] where code='"+stockCode+"'");
            while(rs.next()){
                String sCode = rs.getString("code");

                Date sDate = rs.getDate("date");
                String str = sDate.toString();
                String[] array = str.split("-");
                int year = Integer.parseInt(array[0]);
                int month = Integer.parseInt(array[1]);
                int day = Integer.parseInt(array[2]);
                LocalDate sLocalDate = LocalDate.of(year, month, day);

                double sOpen = (double)rs.getFloat("open");
                double sClose = (double)rs.getFloat("close");
                double sHigh = (double)rs.getFloat("high");
                double sLow = (double)rs.getFloat("low");
                double sVolume = (double)rs.getFloat("volume");
                double sAmount = (double)rs.getFloat("amount");
                StockEntity se = new StockEntity(sCode,sLocalDate,sOpen,sClose,sHigh,sLow,sVolume,sAmount);
                list.add(se);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        System.out.print("getStockList");
        finish();
        return list;
    }

    /**
     * 得到单只股票在一段时间内的所有实体的列表
     * @param stockCode String型，股票的代码
     * @param start String型，开始时间
     * @param end String型，结束时间
     * @return 返回单只股票一段时间内的所有实体的列表
     */
    public ArrayList<StockEntity> getStockListBetweenDay(String stockCode, String start, String end) throws RemoteException{
        ArrayList<StockEntity> list = new ArrayList<StockEntity>();
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select * from [Stock] where code='"+stockCode+"' and date>='"+start+"' and date<='"+end+"' and volume>0.0 order by date desc");
            while(rs.next()){
                String sCode = rs.getString("code");

                Date sDate = rs.getDate("date");
                String str = sDate.toString();
                String[] array = str.split("-");
                int year = Integer.parseInt(array[0]);
                int month = Integer.parseInt(array[1]);
                int day = Integer.parseInt(array[2]);
                LocalDate sLocalDate = LocalDate.of(year, month, day);

                double sOpen = (double)rs.getFloat("open");
                double sClose = (double)rs.getFloat("close");
                double sHigh = (double)rs.getFloat("high");
                double sLow = (double)rs.getFloat("low");
                double sVolume = (double)rs.getFloat("volume");
                double sAmount = (double)rs.getFloat("amount");
                StockEntity se = new StockEntity(sCode,sLocalDate,sOpen,sClose,sHigh,sLow,sVolume,sAmount);
                list.add(se);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        System.out.print("getStockListBetweenDay");
        finish();
        return list;
    }

    /**
     * 获取某个日期 某个板块所有股票的交易情况
     */
    public List<StockEntity> getBenchStockEntity(String pool, LocalDate date ) throws RemoteException {
        List<StockEntity> list = new ArrayList<StockEntity>();
        String sql = "";
        if(pool.equals("G")) {
            sql = "select * from [Stock] where date='"+date.toString()+"' and left(code,3)='300'";
        } else if( pool.equals("S")) {
            sql = "select * from [Stock] where date='"+date.toString()+"' and left(code,3)='002'";
        } else {
            sql = "select * from [Stock] where date='"+date.toString()+"' and (left(code,3)='000' or left(code,3)='001')";
        }
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery(sql);
            while(rs.next()){
                String sCode = rs.getString("code");

                Date sDate = rs.getDate("date");
                String str = sDate.toString();
                String[] array = str.split("-");
                int year = Integer.parseInt(array[0]);
                int month = Integer.parseInt(array[1]);
                int day = Integer.parseInt(array[2]);
                LocalDate sLocalDate = LocalDate.of(year, month, day);

                double sOpen = (double)rs.getFloat("open");
                double sClose = (double)rs.getFloat("close");
                double sHigh = (double)rs.getFloat("high");
                double sLow = (double)rs.getFloat("low");
                double sVolume = (double)rs.getFloat("volume");
                double sAmount = (double)rs.getFloat("amount");
                StockEntity se = new StockEntity(sCode,sLocalDate,sOpen,sClose,sHigh,sLow,sVolume,sAmount);
                list.add(se);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        System.out.print("getBenchStockEntity");
        finish();
        return list;
    }

    /**
     * 获取某个日期 某个板块所有股票的交易情况
     */
    public List<StockDayEntity> getBenchStockDayEntity(String pool, LocalDate date ) throws RemoteException {
        List<StockDayEntity> list = new ArrayList<StockDayEntity>();
        String sql = "";
        if(pool.equals("G")) {
            sql = "select a.*,b.stockname from [Stock] a,[StockCode] b where date='"+date.toString()+"' and left(code,3)='300' and a.code=b.stockcode";
        } else if( pool.equals("S")) {
            sql = "select a.*,b.stockname from [Stock] a,[StockCode] b where date='"+date.toString()+"' and left(code,3)='002' and a.code=b.stockcode";
        } else {
            sql = "select a.*,b.stockname from [Stock] a,[StockCode] b where date='"+date.toString()+"' and (left(code,3)='000' or left(code,3)='001') and a.code=b.stockcode";
        }
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery(sql);
            while(rs.next()){
                String sCode = rs.getString("code");

                Date sDate = rs.getDate("date");
                String str = sDate.toString();
                String[] array = str.split("-");
                int year = Integer.parseInt(array[0]);
                int month = Integer.parseInt(array[1]);
                int day = Integer.parseInt(array[2]);
                LocalDate sLocalDate = LocalDate.of(year, month, day);

                double sOpen = (double)rs.getFloat("open");
                double sClose = (double)rs.getFloat("close");
                double sHigh = (double)rs.getFloat("high");
                double sLow = (double)rs.getFloat("low");
                double sVolume = (double)rs.getFloat("volume");
                double sAmount = (double)rs.getFloat("amount");
                double sPchange = (double)rs.getFloat("pchange");
                String sName = rs.getString("stockname");
                StockDayEntity se = new StockDayEntity(sCode,sLocalDate,sOpen,sClose,sHigh,sLow,sVolume,sAmount,sName);
                se.setChange(sPchange);
                list.add(se);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        System.out.print("getBenchStockDayEntity");
        finish();
        return list;
    }

    /**
     * 获取某个日期 某个行业所有股票的交易情况
     */
    public List<StockDayEntity> getBenchStockDayEntityIndustry(String benchName,String localDate) throws RemoteException {
        List<StockDayEntity> list = new ArrayList<StockDayEntity>();
        String sql = "select a.*,b.stockname from [Stock] a,[StockCode] b where a.date='" + localDate +"' and b.stockindustry=N'" + benchName + "' and a.code=b.stockcode";
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery(sql);
            while(rs.next()){
                String sCode = rs.getString("code");

                Date sDate = rs.getDate("date");
                String str = sDate.toString();
                String[] array = str.split("-");
                int year = Integer.parseInt(array[0]);
                int month = Integer.parseInt(array[1]);
                int day = Integer.parseInt(array[2]);
                LocalDate sLocalDate = LocalDate.of(year, month, day);

                double sOpen = (double)rs.getFloat("open");
                double sClose = (double)rs.getFloat("close");
                double sHigh = (double)rs.getFloat("high");
                double sLow = (double)rs.getFloat("low");
                double sVolume = (double)rs.getFloat("volume");
                double sAmount = (double)rs.getFloat("amount");
                double sPchange = (double)rs.getFloat("pchange");
                String sName = rs.getString("stockname");
                StockDayEntity se = new StockDayEntity(sCode,sLocalDate,sOpen,sClose,sHigh,sLow,sVolume,sAmount,sName);
                se.setChange(sPchange);
                list.add(se);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        System.out.print("getBenchStockDayEntityIndustry");
        finish();
        return list;
    }

    /**
     * 得到一日所有进行交易的股票
     * @param chosenDate String型，股票的代码
     * @return 返回一日所有进行交易的股票
     */
    public List<StockEntity> getAllStockByDay(String chosenDate) throws RemoteException{
        List<StockEntity> list = new ArrayList<StockEntity>();
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select * from [Stock] where date='"+chosenDate+"'");
            while(rs.next()){
                String sCode = rs.getString("code");

                Date sDate = rs.getDate("date");
                String str = sDate.toString();
                String[] array = str.split("-");
                int year = Integer.parseInt(array[0]);
                int month = Integer.parseInt(array[1]);
                int day = Integer.parseInt(array[2]);
                LocalDate sLocalDate = LocalDate.of(year, month, day);

                double sOpen = (double)rs.getFloat("open");
                double sClose = (double)rs.getFloat("close");
                double sHigh = (double)rs.getFloat("high");
                double sLow = (double)rs.getFloat("low");
                double sVolume = (double)rs.getFloat("volume");
                double sAmount = (double)rs.getFloat("amount");
                StockEntity se = new StockEntity(sCode,sLocalDate,sOpen,sClose,sHigh,sLow,sVolume,sAmount);
                list.add(se);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        System.out.print("getAllStockByDay");
        finish();
        return list;
    }

    /**
     * 得到股票对应数据的排名比例
     */
    public double getStockRank(LocalDate localDate,String code,String type) throws RemoteException{
        double rate = 0;
        double index = 0;
        double right = 0;
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select * from [Stock] where date='"+localDate.toString()+"' and left(code,3)='"+code.substring(0,3)+"' order by '" + type + "' desc");
            while(rs.next()){
                index = index + 1;
                String sCode = rs.getString("code");
                if(sCode.equals(code)) {

                    right = index;

                }
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        System.out.print("getStockRank");
        finish();
        rate = right / index;
        return rate;
    }

    /**
     * 得到单只股票某一天的实体
     * @param stockCode String型，股票的代码
     * @param day 日期
     * @return 返回单只股票某一天的实体
     */
    public StockEntity getStockByCodeAndDate(String stockCode,String day) throws RemoteException{
        StockEntity se = null;
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select * from [Stock] where code='"+stockCode+"' and date='"+day+"'");
            if(rs.next()) {
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
                se = new StockEntity(sCode, sLocalDate, sOpen, sClose, sHigh, sLow, sVolume, sAmount);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        System.out.print("getStockByCodeAndDate");
        finish();
        return se;
    }

    /**
     * 得到涨跌
     */
    public double getPChangeByDayAndCode(LocalDate ld, String code) throws RemoteException{
        double d = 0;
        int index = 0;
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select pchange from [Stock] where code='"+code+"' and date='"+ld.toString()+"'");
            if(rs.next()) {
                d = (double) rs.getFloat("pchange");
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        System.out.print("getPChangeByDayAndCode");
        finish();
        return d;
    }

    /**
     * 判断某一年是否有股票的数据
     * @param pool 股票池，“M”-- ，，“S”--002， “G”--300
     * @param year 年份
     * @return 返回某一年是否有这支股票的数据
     */
    public boolean isStockExistByYear(String pool, int year) throws RemoteException{
        boolean exist = false;
        String start = year + "-01-01";
        String end = year + "-12-31";

        String sql = "";
        if(pool.equals("M")) {
            sql = "select count(*) from [Stock] where left(code,3)<>'002' and left(code,3)<>'300' and date>='"+start+"' and date<='"+end+"'";
        } else if(pool.equals("S")) {
            sql = "select count(*) from [Stock] where left(code,3)='002' and date>='"+start+"' and date<='"+end+"'";
        } else if(pool.equals("G")) {
            sql = "select count(*) from [Stock] where left(code,3)='300' and date>='"+start+"' and date<='"+end+"'";
        }
        int count = 0;
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery(sql);

            if(rs.next()) {
                count=rs.getInt(1);
            }

            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        System.out.print("isStockExistByYear");
        finish();
        if(count >= 1) {
            exist = true;
        }
        return exist;
    }

    /**
     * 得到对应板块所有进行交易的股票
     * @return 返回对应板块所有进行交易的股票
     */
    public List<String> getAllStockByTypeAndYear(String type, int ye) throws RemoteException{
        List<String> list = new ArrayList<String>();
        String start = ye + "-01-01";
        String end = ye + "-12-31";
        String sql = "";
        if(type.equals("M")) {
            sql = "select distinct code from [Stock] where date>='"+start+"' and date<='" +end+"' and (left(code,3)='000' or left(code,3)='001')";
        } else if(type.equals("S")) {
            sql = "select distinct code from [Stock] where date>='"+start+"' and date<='" +end+"' and left(code,3)='002'";
        } else if(type.equals("G")) {
            sql = "select distinct code from [Stock] where date>='"+start+"' and date<='" +end+"' and left(code,3)='300'";
        }
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery(sql);
            while(rs.next()){
                String sCode = rs.getString("code");

//                Date sDate = rs.getDate("date");
//                String str = sDate.toString();
//                String[] array = str.split("-");
//                int year = Integer.parseInt(array[0]);
//                int month = Integer.parseInt(array[1]);
//                int day = Integer.parseInt(array[2]);
//                LocalDate sLocalDate = LocalDate.of(year, month, day);
//
//                double sOpen = (double)rs.getFloat("open");
//                double sClose = (double)rs.getFloat("close");
//                double sHigh = (double)rs.getFloat("high");
//                double sLow = (double)rs.getFloat("low");
//                double sVolume = (double)rs.getFloat("volume");
//                double sAmount = (double)rs.getFloat("amount");
//                StockEntity se = new StockEntity(sCode,sLocalDate,sOpen,sClose,sHigh,sLow,sVolume,sAmount);
                list.add(sCode);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        System.out.print("getAllStockByTypeAndYear");
        finish();
        return list;
    }

}
