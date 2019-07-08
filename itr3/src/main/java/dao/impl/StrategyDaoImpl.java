package dao.impl;

import dao.StrategyDao;
import entity.MarketEntity;
import entity.StockEntity;
import entity.StrategyEntity;
import org.springframework.stereotype.Repository;
import sql.*;
import vo.StrategyVO;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StrategyDaoImpl implements StrategyDao{


    private static StrategyDaoImpl strategyDaoImpl;
    private Map<Integer, List<LocalDate >> BDays=new HashMap<>() ;
    private Map<String, Map<String ,String>> marketRecord=new HashMap<>() ;
    private Map<String, Map<Integer ,List<MarketEntity> >> plate=new HashMap<>() ;
    private Map<String, Map<Integer, List <StockEntity> >> m=new HashMap<>() ;//主板股票
    private Map<String, Map<Integer, List <StockEntity> >> s=new HashMap<>() ;//中小板股票
    private Map<String, Map<Integer, List <StockEntity> >> g=new HashMap<>() ;//创业板股票
    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    private StrategyDaoImpl () throws RemoteException{
long startTime = System.currentTimeMillis();    //获取开始时间
        //载入交易日信息
        for(int year=2005;year<2018;year++){
            List<LocalDate > l=new ArrayList<>();
            BDaySQL bdsql = new BDaySQL();
            l = bdsql.getBDays(year);
             BDays.put(year, l);
        }
        //载入股票名称与代码对应关系
        Map<String ,String> m=new HashMap<>() ;
        Map<String ,String> s=new HashMap<>() ;
        Map<String ,String> g=new HashMap<>() ;
        StockCodeSQL scsql = new StockCodeSQL();

        m = scsql.getStockNameCodeMap("main");
        marketRecord .put("M", m) ;
        s = scsql.getStockNameCodeMap("002");
        marketRecord .put("S", s) ;
        g = scsql.getStockNameCodeMap("300");
        marketRecord .put("G", g) ;


        //载入大盘信息
        Map<Integer ,List<MarketEntity> > mm=new HashMap<>() ;
        Map<Integer ,List<MarketEntity> > ss=new HashMap<>() ;
        Map<Integer ,List<MarketEntity> > gg=new HashMap<>() ;
        for(int year=2005;year<2018;year++){
            List <MarketEntity > list=new ArrayList<>() ;
            MarketSQL marsql = new MarketSQL();
            list = marsql.getMarketInfoByYearAndType(year,"000300");
            mm.put(year, list);
        }
        for(int year=2007;year<2018;year++){
            List <MarketEntity > list=new ArrayList<>() ;
            MarketSQL marsql = new MarketSQL();
            list = marsql.getMarketInfoByYearAndType(year,"399005");
            ss.put(year, list);
        }
        for(int year=2010;year<2018;year++){
            List <MarketEntity > list=new ArrayList<>() ;
            MarketSQL marsql = new MarketSQL();
            list = marsql.getMarketInfoByYearAndType(year,"399006");
            gg.put(year, list);
        }
        plate .put("M",mm);
        plate .put("S",ss);
        plate .put("G",gg);

long endTime = System.currentTimeMillis();    //获取结束时间
System.out.println("strategyDao程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
    }

    public static StrategyDao getInstance() throws RemoteException{
        if(strategyDaoImpl==null){
            strategyDaoImpl=new StrategyDaoImpl();
        }
        return strategyDaoImpl;
    }

    @Override
    public LocalDate resetBegin(LocalDate begin) {
        int year=begin.getYear();
        List<LocalDate > list=BDays.get(year);
        for(int i=0;i<list.size();i++){
            LocalDate date=list.get(i);
            if(!date.isBefore(begin)){
                return date;
            }
        }
        year++;
        return BDays.get(year).get(0);
    }

    @Override
    public LocalDate resetEnd(LocalDate end) {
        int year=end.getYear();
        List<LocalDate > list=BDays.get(year);
        if(list.get(0).isAfter(end)){
            year--;
            List<LocalDate > l=BDays.get(year);
            return l.get(l.size()-1);
        }
        else{
            for(int i=0;i<list.size();i++){
                LocalDate date=list.get(i);
                if(date.isEqual(end)){
                    return date;
                }
                if(date.isAfter(end)){
                    return list.get(--i);
                }
            }
            return list.get(list.size()-1);
        }
    }

    @Override
    public int getRealPeriod(LocalDate begin, LocalDate end) {
        int period=0;
        int year1=begin.getYear() ;
        int year2=end.getYear();
        int offset=year2-year1;
        if(offset==0){
            int a=0;
            int b=0;
            List<LocalDate> list=BDays.get(year1);
            for(int i=0;i<list.size() ;i++){
                if(list.get(i).isEqual(begin )){
                    a=i;
                    break;
                }
            }
            for(int i=a;i<list.size() ;i++){
                if(list.get(i).isEqual(end )){
                    b=i;
                    break;
                }
            }
            period=b-a+1;
        }
        else{
            int a=0;
            int b=0;
            List<LocalDate> list1=BDays.get(year1);
            List<LocalDate> list2=BDays.get(year2);
            for(int i=0;i<list1.size();i++){
                if(list1.get(i).isEqual(begin)){
                    a=list1.size()-i;
                    break;
                }
            }
            period+=a;
            for(int i=0;i<list2.size();i++){
                if(list2.get(i).isEqual(end)){
                    b=i+1;
                    break;
                }
            }
            period+=b;
            for(int i=year1+1;i<year2;i++){
                List<LocalDate> list=BDays.get(i);
                period+=list.size();
            }
        }
        return period;
    }

    @Override
    public LocalDate findPastDaysByOffset(LocalDate now, int offset) {
        int year=now.getYear();
        int a=0;
        List<LocalDate > list=BDays.get(year);
        if(list==null){
            return null;
        }
        for(int i=0;i<list.size() ;i++){
            if(list .get(i).isEqual(now) ){
                a=i;
                break;
            }
        }
        if(a>=offset ){
            return list.get(a-offset);
        }
        else{
            offset -=a;
            year--;
            while(true){
                if(year<2005){
                    return null;
                }
                List <LocalDate > l=BDays .get(year);
                if(l.size() >=offset){
                    return BDays .get(year).get(l.size()-offset-1);
                }
                else{
                    offset-=BDays .get(year).size();
                    year--;
                }
            }
        }
    }

    private StockEntity getStockByCodeAndDay(String pool,String code, LocalDate date){
        int year=date.getYear();
        List<StockEntity > list;
        if(pool.equals("M") ){
            list=m.get(code).get(year);
        }
        else if(pool.equals("S") ){
            list=s.get(code).get(year);
        }
        else if(pool.equals("G")){
            list=g.get(code).get(year);
        }
        else{
            return null;
        }
        for(int i=0;i<list.size() ;i++){
            if(list.get(i).getDate() .isEqual(date)){
                return list.get(i);
            }
        }
        return null;
    }


    @Override
    public StockEntity findStockByCodeAndDay(String code, LocalDate date) throws RemoteException {
        String board;

        int year=date.getYear() ;
        String year0101 = year + "-01-01";
        String year1231 = year + "-12-31";

        StockSQL stosql = new StockSQL();

        if(code.startsWith("000")|| code.startsWith("001")) {
            if(m.get(code)!=null&&m.get(code).get(year)!=null ){
                return getStockByCodeAndDay("M",code,date);
            }
            board="M";
        }
        else if(code.startsWith("002")){
            if(s.get(code)!=null&&s.get(code).get(year)!=null ){
                return getStockByCodeAndDay("S",code,date);
            }
            board="S";

        }
        else if(code.startsWith("300")){
            if(g.get(code)!=null&&g.get(code).get(year)!=null ){
                return getStockByCodeAndDay("G",code,date);
            }
            board="G";
        }
        else{
            return null;
        }

        StockEntity poo=stosql.getStockByCodeAndDate(code,date.toString());
        List <StockEntity > list=new ArrayList<>() ;
        list = stosql.getStockListBetweenDay(code, year0101,year1231);

        if(board .equals("M") ){
            if(m.get(code)==null){
                Map<Integer, List<StockEntity >> map=new HashMap<>() ;
                map.put(year, list) ;
                m.put(code, map) ;
            }
            else{
                m.get(code).put(year, list);
            }
        }
        else if(board .equals("S") ){
            if(s.get(code)==null){
                Map<Integer, List<StockEntity >> map=new HashMap<>() ;
                map.put(year, list) ;
                s.put(code, map) ;
            }
            else{
                s.get(code).put(year, list);
            }
        }
        else{
            if(g.get(code)==null){
                Map<Integer, List<StockEntity >> map=new HashMap<>() ;
                map.put(year, list) ;
                g.put(code, map) ;
            }
            else{
                g.get(code).put(year, list);
            }
        }
        return poo;
    }

    @Override
    public LocalDate findFutureDaysByOffset(LocalDate now, int offset) {
        int year=now.getYear();
        int a=0;
        List<LocalDate > list=BDays.get(year);
        if(list==null){
            return null;
        }
        for(int i=0;i<list.size() ;i++){
            if(list .get(i).isEqual(now) ){
                a=i;
                break;
            }
        }
        int b=list.size() -a-1;
        if(b>=offset){
            return list.get(a+offset);
        }
        else{
            offset-=b;
            year++;
            while(true){
                if(year>=2018){
                    return null;
                }
                List <LocalDate > l=BDays .get(year);
                if(l.size() >=offset){
                    return BDays .get(year).get(offset-1);
                }
                else{
                    offset-=BDays .get(year).size();
                    year++;
                }
            }
        }
    }

    @Override
    public List<MarketEntity> getMarketData(String pool, LocalDate end, LocalDate begin) {
        List<MarketEntity> list=new ArrayList<>() ;
        Map<Integer ,List <MarketEntity >> map=plate .get(pool) ;
        List <MarketEntity > l1=map.get(begin .getYear() ) ;
        List <MarketEntity > l2=map.get(end .getYear() ) ;
        for(int i=0;i<l1.size() ;i++){
            if(l1.get(i).getDate() .isEqual(begin ) ){
                list.add(l1.get(i)) ;
                break;
            }
        }
        for(int i=0;i<l2.size() ;i++){
            if(l2.get(i).getDate() .isEqual(end ) ){
                list.add(l2.get(i)) ;
                break;
            }
        }
        return list;
    }

    @Override
    public List<String> getPool(String pool, boolean hasST, LocalDate begin, LocalDate end) throws RemoteException {
        List<String> list=new ArrayList<>() ;

        StockSQL stosql = new StockSQL();

        Map<String,String> map=marketRecord .get(pool) ;
        if(pool.equals("M") || pool.equals("S") || pool.equals("G") ) {
            //NOTHING
        }  else{
            return list;
        }
        if(map==null){
            return list;
        }
        if(hasST ){
            list = stosql.getAllStockByTypeAndYear(pool,begin.getYear());
        }
        else{
            list = stosql.getAllStockByTypeAndYear(pool,begin.getYear());
System.out.println("getallstockyear" + list.size());
            List<String> temp=new ArrayList<>() ;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String code = "";
                if(!entry.getKey().contains("ST")) {
                    code = entry.getValue();
                }
                if(list.contains(code)) {
                    temp.add(code);
                }
            }
            list = temp;
        }
        return list;
    }

    @Override
    public MarketEntity findMarketIndexByDate(String pool, LocalDate date) {
        List <MarketEntity > list=plate .get(pool).get(date.getYear() );
        for(int i=0;i<list .size() ;i++){
            MarketEntity po=list .get(i) ;
            if(po.getDate() .isEqual(date) ){
                return po;
            }
        }
        return null;
    }


    @Override
    public MarketEntity findMarketIndexByOffset(String pool, LocalDate date, int offset) {
        if(offset>0){
            date=findFutureDaysByOffset(date,offset);
        }
        if(offset<0){
            date=findPastDaysByOffset(date,offset);
        }
        if(date==null){
            return null;
        }
        return findMarketIndexByDate(pool, date);
    }

    @Override
    public StockEntity findStockByCodeAndOffset(String code, LocalDate date, int i) throws RemoteException {
        //StockEntity po=findStockByCodeAndDay(code, date);
        LocalDate d;
        if(i>=0){
            d=findFutureDaysByOffset(date ,i);
        }
        else{
            d=findPastDaysByOffset(date ,-i);
        }
        if(d==null){
            return null;
        }
        StockEntity spo=findStockByCodeAndDay(code, d);
        return spo;
    }

    @Override
    public Map<String,Map<String, String> > getStcokSet() {
        return marketRecord ;
    }

    @Override
    public StockEntity findNearestStock(String code, LocalDate date) {
        int year=date.getYear() ;
        List <StockEntity > list=null;
        if(code.startsWith("000")|| code.startsWith("001")) {
            if (m.get(code) != null && m.get(code).get(year) != null){
                list = m.get(code).get(year);
            }
        }
        else if(code.startsWith("002")){
            if (s.get(code) != null && s.get(code).get(year) != null) {
                list = s.get(code).get(year);
            }
        }
        else if(code.startsWith("300")){
            if (g.get(code) != null && g.get(code).get(year) != null) {
                list = g.get(code).get(year);
            }
        }
        else{
            return null;
        }
        if(list==null||list.size()==0){
            return null;
        }
        for(int i=0;i<list .size() ;i++){
            if(list.get(i).getDate() .isBefore(date) ){
                return list.get(i);
            }
        }
        return list.get(list.size() -1);
    }


    /**
     * 查看策略参数
     * @param userName
     * @param code
     * @return
     */
    @Override
    public StrategyEntity viewStrategy(String userName, String code) {
        StrategySQL strsql = new StrategySQL();
        StrategyEntity se = null;
        try {
            se = strsql.getStrategyByUsernameAndCode(userName,code);
        }catch(RemoteException re) {
            re.printStackTrace();
        }
        return se ;
    }

    /**
     * 保存策略
     * @param entity
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean saveStrategy(StrategyEntity entity) throws RemoteException {
        StrategySQL strsql = new StrategySQL();
        strsql.insert(entity);
        return true ;
    }

    /**
     * 获取策略历史
     * @param userName
     * @return
     * @throws RemoteException
     */
    @Override
    public List<StrategyEntity> getStrategyHistory(String userName) throws RemoteException{
        StrategySQL strsql = new StrategySQL();
        List<StrategyEntity> list = strsql.getStrategyHistoryByUsername(userName);
        return list ;
    }

    /**
     * 删除策略历史
     * @param entities
     * @return
     */
    @Override
    public boolean deleteHistoryStrategies(List<StrategyEntity> entities) {
        try {
            StrategySQL strsql = new StrategySQL();
            for (int i = 0; i < entities.size(); i++) {
                strsql.delete(entities.get(i));
            }
        } catch(RemoteException re) {
            re.printStackTrace();
            return false;
        }
        return true ;
    }

    @Override
    public StrategyVO findStrategy(String code) {
        return null;
    }

    /**
     * 得到个人历史策略的数量
     * @param userName
     * @return
     */
    @Override
    public int getHistoryNum(String userName) {
        StrategySQL ssql = new StrategySQL();
        int result = 0;
        try {
            result = ssql.getNumOfStrategy(userName);
        }catch(RemoteException re) {
            re.printStackTrace();
        }
        return result ;
    }
}