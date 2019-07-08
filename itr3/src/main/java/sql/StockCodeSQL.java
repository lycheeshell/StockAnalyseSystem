package sql;

import entity.StockDetailEntity;

import java.rmi.RemoteException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zongkan on 2017/6/5.
 */

public class StockCodeSQL {

    //stockcode 代码
    //stockname 名称
    //stockarea 地区
    //stockmarketdate 上市日期
    //stockindustry 所属行业
    //stockasset 总资产（万）

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
     * 得到所有股票实体
     *
     * @return 返回所有股票实体的列表
     */
    public List<StockDetailEntity> getAllStockCode(String type) throws RemoteException {
        List<StockDetailEntity> list = new ArrayList<StockDetailEntity>();
        String sql = "";
        if(type.equals("M")) {
            sql = "select  distinct b.* from [Stock] a,[StockCode] b where a.code=b.stockcode and (left(stockcode,3)='000' or left(stockcode,3)='001')";
        } else if(type.equals("S")) {
            sql = "select  distinct b.* from [Stock] a,[StockCode] b where a.code=b.stockcode and left(a.code,3)='002'";
        } else {
            sql = "select  distinct b.* from [Stock] a,[StockCode] b where a.code=b.stockcode and left(a.code,3)='300'";
        }
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery(sql);
            while(rs.next()){
                String sCode = rs.getString("stockcode");
                String sName= rs.getString("stockname");
                String sArea = rs.getString("stockarea");

                Date sDate = rs.getDate("stockmarketdate");
                String str = sDate.toString();
                String[] array = str.split("-");
                int year = Integer.parseInt(array[0]);
                int month = Integer.parseInt(array[1]);
                int day = Integer.parseInt(array[2]);
                LocalDate sMarketDate = LocalDate.of(year, month, day);
                String sMarketDatestr = sMarketDate.toString();

                String sIndustry = rs.getString("stockindustry");
                double sAsset = (double)rs.getFloat("stockasset");
                int sFocusheat = rs.getInt("focusheat");
                StockDetailEntity se = new StockDetailEntity(sCode,sName,sArea,sMarketDatestr,sIndustry,sAsset,sFocusheat);

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
     * 得到所选板块的所有股票的名称和代码
     * @param type 板块类型
     * @return 返回所有股票代码的Map
     */
    public Map<String,String> getStockNameCodeMap(String type) throws RemoteException {
        Map<String,String> map = new HashMap<String,String>();
        init();
        try {
            Statement st=dbConn.createStatement();
            String sql ="";
            if(type.equals("002") || type.equals("300")) {
                sql = "select stockname,stockcode from [StockCode] where left(stockcode,3)='"+type+"'";
            } else {
                sql = "select stockname,stockcode from [StockCode] where left(stockcode,3)<>'002' and left(stockcode,3)<>'300'";
            }
            ResultSet rs=st.executeQuery(sql);
            while(rs.next()){
                String name = rs.getString("stockname");
                String code = rs.getString("stockcode");
                map.put(name,code);
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
     * 得到股票数量第N多的板块的行业名称
     */
    public String getStockTypeNameByOrder(int order) throws RemoteException {
        String industry = "";
        init();
        try {
            Statement st=dbConn.createStatement();
            String sql ="select count(stockcode) as c,stockindustry from [Quantour].[dbo].[StockCode] group by stockindustry order by c desc";
            ResultSet rs=st.executeQuery(sql);
            int index = 0;
            while(rs.next()){
                index ++;
                if(index == order) {
                    industry = rs.getString("stockindustry");
                    break;
                }
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        finish();
        return industry;
    }

    /**
     * 得到所选行业的所有股票的代码
     * @param indu 行业
     * @return 返回所有股票代码
     */
    public List<String> getStockNameByIndustry(String indu) throws RemoteException {
        List<String> list = new ArrayList<String>();
        init();
        try {
            Statement st=dbConn.createStatement();
            String sql ="select stockcode from [StockCode] where stockindustry=N'" + indu +"'";
            ResultSet rs=st.executeQuery(sql);
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
     * 将股票名称转换为股票编号
     * @param stockName
     * @return stockCode
     */
    public String getTransferCode(String stockName){
        String stockCode = null ;
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select stockcode from [StockCode] where stockname=N'"+stockName+"'");
            if(rs.next()) {
                stockCode = rs.getString("stockcode");
            }
            if(! (stockCode.length() == 6)) {
                stockCode = null;
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        finish();
        return stockCode;
    }

    /**
     * 将股票编号转换为股票名称
     * */
    public String getTransferName(String code) throws RemoteException{
        String stockName = null ;
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select stockname from [StockCode] where stockcode='"+code+"'");
            if(rs.next()) {
                stockName = rs.getString("stockname");
            }
            if(!(stockName.length() >= 1)) {
                stockName = null;
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        finish();
        return stockName;
    }


    /**
     * 在数据库中增加一个股票记录
     *
     * @param entity StockCodeEntity型，新增的股票
     * @return
     */
    public void insert(StockDetailEntity entity) throws RemoteException {
        init();
        String sql="insert into [StockCode] values('"+entity.getCode()+"',N'"+entity.getName()+"',N'"+entity.getArea()+"','"+entity.getDateOnMarket()+"',N'"+entity.getBusiness()+"','"+entity.getAsset()+"','"+entity.getFocusHeat()+"')";
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
     * 在数据库中删除一个股票代码
     *
     * @param code String型，删除的股票代码
     * @return
     */
    public void delete(String code) throws RemoteException {
        init();
        String sql="delete from [StockCode] where stockcode='"+code+"'";
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
     * 得到单只股票的信息
     * @param code String型，股票的代码
     * @return 返回单只股票信息
     */
    public StockDetailEntity getOneStock(String code) throws RemoteException{
        StockDetailEntity sde = null;
        init();
        try {
            Statement st=dbConn.createStatement();
            ResultSet rs=st.executeQuery("select * from [StockCode] where stockcode='"+code+"'");
            if(rs.next()) {
                String sCode = rs.getString("stockcode");
                String sName = rs.getString("stockname");
                String sArea = rs.getString("stockarea");

                Date sDate = rs.getDate("stockmarketdate");
                String str = sDate.toString();
                String[] array = str.split("-");
                int year = Integer.parseInt(array[0]);
                int month = Integer.parseInt(array[1]);
                int day = Integer.parseInt(array[2]);
                LocalDate sMarketDate = LocalDate.of(year, month, day);
                String sMarketDatestr = sMarketDate.toString();

                String sIndustry = rs.getString("stockindustry");
                double sAsset = (double) rs.getFloat("stockasset");
                int sFocusheat = rs.getInt("focusheat");
                sde = new StockDetailEntity(sCode, sName, sArea, sMarketDatestr, sIndustry, sAsset, sFocusheat);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }
        finish();
        return sde;
    }

    /**
     * 在数据库中更新一个股票关注度
     */
    public void setStockFocus(boolean isAdd,String stockCode) throws RemoteException {
        init();
        String sql="";
        if(isAdd) {
            sql = "update [StockCode] set focusheat=focusheat+1 where stockcode='" + stockCode + "'";
        } else {
            sql = "update [StockCode] set focusheat=focusheat-1 where stockcode='" + stockCode + "'";
        }
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
