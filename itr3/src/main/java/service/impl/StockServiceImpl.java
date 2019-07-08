package service.impl;

import dao.Dao;
import dao.StockDao;
import dao.StrategyDao;
import entity.StockDayEntity;
import entity.StockDetailEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.StockService;
import util.NormalDistribution;
import util.SDF;
import util.StockCalendar;
import util.StockPool;
import util.*;
import vo.NewRadaVO;
import vo.RadaVO;

import java.time.LocalDate;
import java.util.*;

/**
 * Created by lienming on 2017/5/18.
 */
@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockDao stockDao ;

    @Autowired
    private StrategyDao strategyDao ;

    @Autowired
    private Dao dao ;

    private static LocalDate ld=LocalDate.now();

    private static Map maps = new HashMap<>() ;

    private static double a = 0.1;//损失参数，防止过拟合，要达到最优的平衡还需要调整

    @Override
    public List<StockDayEntity> getMainStock(Date date) {

        LocalDate local_date = StockCalendar.DateToLocalDate(date) ;

        LocalDate op_day = strategyDao.resetEnd(local_date);

        List<StockDayEntity> list = stockDao.getBenchStockEntity("M",op_day) ;

        list.addAll(stockDao.getBenchStockEntity("S",op_day));

        list.addAll(stockDao.getBenchStockEntity("G",op_day));

        return list ;
    }

    @Override
    public List<StockDetailEntity> getPersonFocus(String userName){
        List<String> stockList = stockDao.getPersonFocus(userName) ;
        List<StockDetailEntity> list = new ArrayList<>() ;
        for(String code : stockList )
        {
            //System.out.println("fuckingggggggggggg"+stockDao.getStockInfo(code).getPoint());\
            list.add(stockDao.getStockInfo(code));
        }
        return list ;
    }

    @Override
    public boolean addFocusStock(List<String> stockCodes, String userName) {
        List<String> ori_stockList = stockDao.getPersonFocus(userName) ;

        for(String code : stockCodes )
        {
            if(ori_stockList.contains(code))
                stockCodes.remove(code);
        }

        return stockDao.setPersonFocus(stockCodes,userName) ;

    }

    @Override
    public boolean deleteFocusStock(List<String> stockCodes, String userName){
        return stockDao.deleteFocusStock(stockCodes,userName) ;
    }

    @Override
    public List<StockDetailEntity> getAllStock( ) {
        return stockDao.getAllStock( ) ;
    }

    @Override
    public List<StockDetailEntity> getPoolStock(String pool)  {
        return stockDao.getPoolStock(pool) ;
    }

    @Override
    public StockDetailEntity getStockInfo(String code) {
        return stockDao.getStockInfo(code) ;
    }

    /**
     *
     * @param code
     * @return
     */
    @Override
    public double[] getStockRada(String code) {

        String pool = StockPool.getPool(code) ;//设置查询板块

        //设置查询日期
        Date today = StockCalendar.getToday() ;
        LocalDate local_today = StockCalendar.DateToLocalDate(today) ;
        LocalDate op_day = strategyDao.resetEnd(local_today);

        // 以op_day为指定日期，执行查询，找到当天与该股票同板块的
        // 换手率、每股现金流、涨跌幅、流通市值比、市盈率mrq、成交量 的最值

        RadaVO radaVO = stockDao.getStockRada(code) ;
        System.out.println(pool);
        double[] maxArr = stockDao.getMaxValueForRada(pool) ;

        double[] res = new double[12] ;

        res[0] = radaVO.changerate ;
        res[1] = radaVO.pb ;
        res[2] = radaVO.profit ;
        res[3] = radaVO.undp ;
        res[4] = radaVO.mrq ;
        res[5] = radaVO.totals ;

        for(int i=6;i<12;i++)
            res[i]=maxArr[i-6];
        return res  ;
    }


    @Override
    public double getStockValue(String code) {

        LocalDate today = StockCalendar.DateToLocalDate(StockCalendar.getToday());

        //权重可以给用户设置。
        double[] factors = { 0.1 , 0.1 , 0.6, 0.1 , 0.1 } ;



        double total = 0.0 ;
        for( int i = 0 ; i < 5 ; i ++ ) {

            LocalDate op_day = strategyDao.resetEnd(today) ;
            today = op_day ;

            double d1 = stockDao.getRank(op_day,code,"high") ;

            double d2 = stockDao.getRank(op_day,code,"volume") ;

            double d3 = stockDao.getRank(op_day,code,"pchange");

            double d4 = stockDao.getRank(op_day,code,"open") ;

            double d5 = stockDao.getRank(op_day,code,"close") ;



            double thisday_p = valueRank(d1) * factors[0] + valueRank(d2) * factors[1] +
                    valueRank(d3) * factors[2] + valueRank(d4) * factors[3] + valueRank(d5) * factors[4] ;

            total = total + thisday_p ;

        }

        total = total / 500 ;

        return total;
    }

    /**正态分布评分*/
    static double valueRank(double rank) {
        double tmp = 1.0f - rank;
        double x = NormalDistribution.getPoint(tmp);

        return 75 + x * (100-50)/7.8 ;
    }



    @Override
    public String getTransferCode(String stockName) {
        return dao.getTransferCode(stockName);
    }

    @Override
    public Map<String,Double> getRecommend(){
        LocalDate l=LocalDate.now();
        if(maps.size()==0||l.isAfter(ld)) {
            ld=l;
            System.out.println("fuckingggggggggggg tree");
            maps=new HashMap();
            int numberOfKeyChar = 6;//用来建树的关键特征数
            Map<String, List<NewRadaVO>> list=stockDao.getNewRada(ld);//得到股票的原始数据
            List<Stock> data = new ArrayList<>();//所有股票的抽象数据，一开始是空队列
            List<StockHelper> dataHelper1=new ArrayList<>();//主板辅助队列
            List<StockHelper> dataHelper2=new ArrayList<>();//中小板辅助队列
            List<StockHelper> dataHelper3=new ArrayList<>();//创业板辅助队列
            for (String key : list.keySet()) {

                List<NewRadaVO> value = list.get(key);

                if(value.size()>1){//否则参考价值不大
                    int length=value.size();
                    String code=key ;       //代码编号  key
                    String stockName=value.get(0).stockName ;
                    boolean label;//结果
                    if(value.get(length-1).pchange>=0){
                        label=true;
                    }
                    else{
                        label=false;
                    }
                    double volume;      //成交量
                    double total=0;
                    for(int i=0;i<length;i++){
                        total+=value.get(i).volume;
                    }
                    volume=total/length;
                    double pchange=(value.get(length-2).close-value.get(0).open)/value.get(0).open;//一段时间的涨跌幅，粗略计算
                    double changerate=value.get(0).changerate;//换手率
                    double pb=value.get(0).pb ;//市净率
                    double profit=value.get(0).profit;//利润同比(%)
                    double mrq=value.get(0).mrq ;//市盈率mrq
                    double caprat=(value.get(0).totals-value.get(0).outstanding)/value.get(0).totals;//非流通股本比
                    double esp=value.get(0).esp;//每股收益
                    double bvps=value.get(0).bvps;//每股净资
                    double perundp=value.get(0).perundp;//每股未分配
                    double rev=value.get(0).rev;//收入同比(%)
                    double gpr=value.get(0).gpr;//毛利率(%)
                    StockHelper sh=new StockHelper(code,volume,label,pchange,stockName,changerate,pb,profit,mrq,caprat,esp,bvps,perundp,rev,gpr);
                    if(key.startsWith("000")||key.startsWith("001")) {
                        dataHelper1.add(sh);
                    }
                    if(key.startsWith("002")) {
                        dataHelper2.add(sh);
                    }
                    if(key.startsWith("300")) {
                        dataHelper3.add(sh);
                    }
                }
            }
            //现在辅助队列已经填充完毕
            int length1=dataHelper1.size();
            int length2=dataHelper2.size();
            int length3=dataHelper3.size();
            int[] rank = new int[12];
            for(int i=0;i<12;i++){
                rank[i]=1;
            }
            for(int i=0;i<length1;i++){
                StockHelper sh=dataHelper1.get(i);
                for(int j=0;j<length1;j++){
                    if(sh.volume<dataHelper1.get(j).volume){
                        rank[0]++;
                    }
                    if(sh.pchange<dataHelper1.get(j).pchange){
                        rank[1]++;
                    }
                    if(sh.changerate<dataHelper1.get(j).changerate){
                        rank[2]++;
                    }
                    if(sh.pb<dataHelper1.get(j).pb){
                        rank[3]++;
                    }
                    if(sh.profit<dataHelper1.get(j).profit){
                        rank[4]++;
                    }
                    if(sh.mrq<dataHelper1.get(j).mrq){
                        rank[5]++;
                    }
                    if(sh.caprat<dataHelper1.get(j).caprat){
                        rank[6]++;
                    }
                    if(sh.esp<dataHelper1.get(j).esp){
                        rank[7]++;
                    }
                    if(sh.bvps<dataHelper1.get(j).bvps){
                        rank[8]++;
                    }
                    if(sh.perundp<dataHelper1.get(j).perundp){
                        rank[9]++;
                    }
                    if(sh.rev<dataHelper1.get(j).rev){
                        rank[10]++;
                    }
                    if(sh.gpr<dataHelper1.get(j).gpr){
                        rank[11]++;
                    }
                }
                HashMap<String,String> attri=new HashMap<String,String>();
                if(rank[0]<=(length1/5)){attri.put("volume","VG");}
                else if(rank[0]<=(length1*2/5)){attri.put("volume","G");}
                else if(rank[0]<=(length1*3/5)){attri.put("volume","N");}
                else if(rank[0]<=(length1*4/5)){attri.put("volume","B");}
                else{attri.put("volume","VB");}
                if(rank[1]<=(length1/5)){attri.put("pchange","VG");}
                else if(rank[1]<=(length1*2/5)){attri.put("pchange","G");}
                else if(rank[1]<=(length1*3/5)){attri.put("pchange","N");}
                else if(rank[1]<=(length1*4/5)){attri.put("pchange","B");}
                else{attri.put("pchange","VB");}
                if(rank[2]<=(length1/5)){attri.put("changerate","VG");}
                else if(rank[2]<=(length1*2/5)){attri.put("changerate","G");}
                else if(rank[2]<=(length1*3/5)){attri.put("changerate","N");}
                else if(rank[2]<=(length1*4/5)){attri.put("changerate","B");}
                else{attri.put("changerate","VB");}
                if(rank[3]<=(length1/5)){attri.put("pb","VG");}
                else if(rank[3]<=(length1*2/5)){attri.put("pb","G");}
                else if(rank[3]<=(length1*3/5)){attri.put("pb","N");}
                else if(rank[3]<=(length1*4/5)){attri.put("pb","B");}
                else{attri.put("pb","VB");}
                if(rank[4]<=(length1/5)){attri.put("profit","VG");}
                else if(rank[4]<=(length1*2/5)){attri.put("profit","G");}
                else if(rank[4]<=(length1*3/5)){attri.put("profit","N");}
                else if(rank[4]<=(length1*4/5)){attri.put("profit","B");}
                else{attri.put("profit","VB");}
                if(rank[5]<=(length1/5)){attri.put("mrq","VG");}
                else if(rank[5]<=(length1*2/5)){attri.put("mrq","G");}
                else if(rank[5]<=(length1*3/5)){attri.put("mrq","N");}
                else if(rank[5]<=(length1*4/5)){attri.put("mrq","B");}
                else{attri.put("mrq","VB");}
                if(rank[6]<=(length1/5)){attri.put("caprat","VG");}
                else if(rank[6]<=(length1*2/5)){attri.put("caprat","G");}
                else if(rank[6]<=(length1*3/5)){attri.put("caprat","N");}
                else if(rank[6]<=(length1*4/5)){attri.put("caprat","B");}
                else{attri.put("caprat","VB");}
                if(rank[7]<=(length1/5)){attri.put("esp","VG");}
                else if(rank[7]<=(length1*2/5)){attri.put("esp","G");}
                else if(rank[7]<=(length1*3/5)){attri.put("esp","N");}
                else if(rank[7]<=(length1*4/5)){attri.put("esp","B");}
                else{attri.put("esp","VB");}
                if(rank[8]<=(length1/5)){attri.put("bvps","VG");}
                else if(rank[8]<=(length1*2/5)){attri.put("bvps","G");}
                else if(rank[8]<=(length1*3/5)){attri.put("bvps","N");}
                else if(rank[8]<=(length1*4/5)){attri.put("bvps","B");}
                else{attri.put("bvps","VB");}
                if(rank[9]<=(length1/5)){attri.put("perundp","VG");}
                else if(rank[9]<=(length1*2/5)){attri.put("perundp","G");}
                else if(rank[9]<=(length1*3/5)){attri.put("perundp","N");}
                else if(rank[9]<=(length1*4/5)){attri.put("perundp","B");}
                else{attri.put("perundp","VB");}
                if(rank[10]<=(length1/5)){attri.put("rev","VG");}
                else if(rank[10]<=(length1*2/5)){attri.put("rev","G");}
                else if(rank[10]<=(length1*3/5)){attri.put("rev","N");}
                else if(rank[10]<=(length1*4/5)){attri.put("rev","B");}
                else{attri.put("rev","VB");}
                if(rank[11]<=(length1/5)){attri.put("gpr","VG");}
                else if(rank[11]<=(length1*2/5)){attri.put("gpr","G");}
                else if(rank[11]<=(length1*3/5)){attri.put("gpr","N");}
                else if(rank[11]<=(length1*4/5)){attri.put("gpr","B");}
                else{attri.put("gpr","VB");}
                Stock s=new Stock(sh.stockName,sh.code,sh.pchange,sh.label,attri);
                data.add(s);
                for(int k=0;k<12;k++){
                    rank[k]=1;
                }
            }
            for(int i=0;i<length2;i++){
                StockHelper sh=dataHelper2.get(i);
                for(int j=0;j<length2;j++){
                    if(sh.volume<dataHelper2.get(j).volume){
                        rank[0]++;
                    }
                    if(sh.pchange<dataHelper2.get(j).pchange){
                        rank[1]++;
                    }
                    if(sh.changerate<dataHelper2.get(j).changerate){
                        rank[2]++;
                    }
                    if(sh.pb<dataHelper2.get(j).pb){
                        rank[3]++;
                    }
                    if(sh.profit<dataHelper2.get(j).profit){
                        rank[4]++;
                    }
                    if(sh.mrq<dataHelper2.get(j).mrq){
                        rank[5]++;
                    }
                    if(sh.caprat<dataHelper2.get(j).caprat){
                        rank[6]++;
                    }
                    if(sh.esp<dataHelper2.get(j).esp){
                        rank[7]++;
                    }
                    if(sh.bvps<dataHelper2.get(j).bvps){
                        rank[8]++;
                    }
                    if(sh.perundp<dataHelper2.get(j).perundp){
                        rank[9]++;
                    }
                    if(sh.rev<dataHelper2.get(j).rev){
                        rank[10]++;
                    }
                    if(sh.gpr<dataHelper2.get(j).gpr){
                        rank[11]++;
                    }
                }
                HashMap<String,String> attri=new HashMap<String,String>();
                if(rank[0]<=(length2/5)){attri.put("volume","VG");}
                else if(rank[0]<=(length2*2/5)){attri.put("volume","G");}
                else if(rank[0]<=(length2*3/5)){attri.put("volume","N");}
                else if(rank[0]<=(length2*4/5)){attri.put("volume","B");}
                else{attri.put("volume","VB");}
                if(rank[1]<=(length2/5)){attri.put("pchange","VG");}
                else if(rank[1]<=(length2*2/5)){attri.put("pchange","G");}
                else if(rank[1]<=(length2*3/5)){attri.put("pchange","N");}
                else if(rank[1]<=(length2*4/5)){attri.put("pchange","B");}
                else{attri.put("pchange","VB");}
                if(rank[2]<=(length2/5)){attri.put("changerate","VG");}
                else if(rank[2]<=(length2*2/5)){attri.put("changerate","G");}
                else if(rank[2]<=(length2*3/5)){attri.put("changerate","N");}
                else if(rank[2]<=(length2*4/5)){attri.put("changerate","B");}
                else{attri.put("changerate","VB");}
                if(rank[3]<=(length2/5)){attri.put("pb","VG");}
                else if(rank[3]<=(length2*2/5)){attri.put("pb","G");}
                else if(rank[3]<=(length2*3/5)){attri.put("pb","N");}
                else if(rank[3]<=(length2*4/5)){attri.put("pb","B");}
                else{attri.put("pb","VB");}
                if(rank[4]<=(length2/5)){attri.put("profit","VG");}
                else if(rank[4]<=(length2*2/5)){attri.put("profit","G");}
                else if(rank[4]<=(length2*3/5)){attri.put("profit","N");}
                else if(rank[4]<=(length2*4/5)){attri.put("profit","B");}
                else{attri.put("profit","VB");}
                if(rank[5]<=(length2/5)){attri.put("mrq","VG");}
                else if(rank[5]<=(length2*2/5)){attri.put("mrq","G");}
                else if(rank[5]<=(length2*3/5)){attri.put("mrq","N");}
                else if(rank[5]<=(length2*4/5)){attri.put("mrq","B");}
                else{attri.put("mrq","VB");}
                if(rank[6]<=(length2/5)){attri.put("caprat","VG");}
                else if(rank[6]<=(length2*2/5)){attri.put("caprat","G");}
                else if(rank[6]<=(length2*3/5)){attri.put("caprat","N");}
                else if(rank[6]<=(length2*4/5)){attri.put("caprat","B");}
                else{attri.put("caprat","VB");}
                if(rank[7]<=(length2/5)){attri.put("esp","VG");}
                else if(rank[7]<=(length2*2/5)){attri.put("esp","G");}
                else if(rank[7]<=(length2*3/5)){attri.put("esp","N");}
                else if(rank[7]<=(length2*4/5)){attri.put("esp","B");}
                else{attri.put("esp","VB");}
                if(rank[8]<=(length2/5)){attri.put("bvps","VG");}
                else if(rank[8]<=(length2*2/5)){attri.put("bvps","G");}
                else if(rank[8]<=(length2*3/5)){attri.put("bvps","N");}
                else if(rank[8]<=(length2*4/5)){attri.put("bvps","B");}
                else{attri.put("bvps","VB");}
                if(rank[9]<=(length2/5)){attri.put("perundp","VG");}
                else if(rank[9]<=(length2*2/5)){attri.put("perundp","G");}
                else if(rank[9]<=(length2*3/5)){attri.put("perundp","N");}
                else if(rank[9]<=(length2*4/5)){attri.put("perundp","B");}
                else{attri.put("perundp","VB");}
                if(rank[10]<=(length2/5)){attri.put("rev","VG");}
                else if(rank[10]<=(length2*2/5)){attri.put("rev","G");}
                else if(rank[10]<=(length2*3/5)){attri.put("rev","N");}
                else if(rank[10]<=(length2*4/5)){attri.put("rev","B");}
                else{attri.put("rev","VB");}
                if(rank[11]<=(length2/5)){attri.put("gpr","VG");}
                else if(rank[11]<=(length2*2/5)){attri.put("gpr","G");}
                else if(rank[11]<=(length2*3/5)){attri.put("gpr","N");}
                else if(rank[11]<=(length2*4/5)){attri.put("gpr","B");}
                else{attri.put("gpr","VB");}
                Stock s=new Stock(sh.stockName,sh.code,sh.pchange,sh.label,attri);
                data.add(s);
                for(int k=0;k<12;k++){
                    rank[k]=1;
                }
            }
            for(int i=0;i<length3;i++){
                StockHelper sh=dataHelper3.get(i);
                for(int j=0;j<length3;j++){
                    if(sh.volume<dataHelper3.get(j).volume){
                        rank[0]++;
                    }
                    if(sh.pchange<dataHelper3.get(j).pchange){
                        rank[1]++;
                    }
                    if(sh.changerate<dataHelper3.get(j).changerate){
                        rank[2]++;
                    }
                    if(sh.pb<dataHelper3.get(j).pb){
                        rank[3]++;
                    }
                    if(sh.profit<dataHelper3.get(j).profit){
                        rank[4]++;
                    }
                    if(sh.mrq<dataHelper3.get(j).mrq){
                        rank[5]++;
                    }
                    if(sh.caprat<dataHelper3.get(j).caprat){
                        rank[6]++;
                    }
                    if(sh.esp<dataHelper3.get(j).esp){
                        rank[7]++;
                    }
                    if(sh.bvps<dataHelper3.get(j).bvps){
                        rank[8]++;
                    }
                    if(sh.perundp<dataHelper3.get(j).perundp){
                        rank[9]++;
                    }
                    if(sh.rev<dataHelper3.get(j).rev){
                        rank[10]++;
                    }
                    if(sh.gpr<dataHelper3.get(j).gpr){
                        rank[11]++;
                    }
                }
                HashMap<String,String> attri=new HashMap<String,String>();
                if(rank[0]<=(length3/5)){attri.put("volume","VG");}
                else if(rank[0]<=(length3*2/5)){attri.put("volume","G");}
                else if(rank[0]<=(length3*3/5)){attri.put("volume","N");}
                else if(rank[0]<=(length3*4/5)){attri.put("volume","B");}
                else{attri.put("volume","VB");}
                if(rank[1]<=(length3/5)){attri.put("pchange","VG");}
                else if(rank[1]<=(length3*2/5)){attri.put("pchange","G");}
                else if(rank[1]<=(length3*3/5)){attri.put("pchange","N");}
                else if(rank[1]<=(length3*4/5)){attri.put("pchange","B");}
                else{attri.put("pchange","VB");}
                if(rank[2]<=(length3/5)){attri.put("changerate","VG");}
                else if(rank[2]<=(length3*2/5)){attri.put("changerate","G");}
                else if(rank[2]<=(length3*3/5)){attri.put("changerate","N");}
                else if(rank[2]<=(length3*4/5)){attri.put("changerate","B");}
                else{attri.put("changerate","VB");}
                if(rank[3]<=(length3/5)){attri.put("pb","VG");}
                else if(rank[3]<=(length3*2/5)){attri.put("pb","G");}
                else if(rank[3]<=(length3*3/5)){attri.put("pb","N");}
                else if(rank[3]<=(length3*4/5)){attri.put("pb","B");}
                else{attri.put("pb","VB");}
                if(rank[4]<=(length3/5)){attri.put("profit","VG");}
                else if(rank[4]<=(length3*2/5)){attri.put("profit","G");}
                else if(rank[4]<=(length3*3/5)){attri.put("profit","N");}
                else if(rank[4]<=(length3*4/5)){attri.put("profit","B");}
                else{attri.put("profit","VB");}
                if(rank[5]<=(length3/5)){attri.put("mrq","VG");}
                else if(rank[5]<=(length3*2/5)){attri.put("mrq","G");}
                else if(rank[5]<=(length3*3/5)){attri.put("mrq","N");}
                else if(rank[5]<=(length3*4/5)){attri.put("mrq","B");}
                else{attri.put("mrq","VB");}
                if(rank[6]<=(length3/5)){attri.put("caprat","VG");}
                else if(rank[6]<=(length3*2/5)){attri.put("caprat","G");}
                else if(rank[6]<=(length3*3/5)){attri.put("caprat","N");}
                else if(rank[6]<=(length3*4/5)){attri.put("caprat","B");}
                else{attri.put("caprat","VB");}
                if(rank[7]<=(length3/5)){attri.put("esp","VG");}
                else if(rank[7]<=(length3*2/5)){attri.put("esp","G");}
                else if(rank[7]<=(length3*3/5)){attri.put("esp","N");}
                else if(rank[7]<=(length3*4/5)){attri.put("esp","B");}
                else{attri.put("esp","VB");}
                if(rank[8]<=(length3/5)){attri.put("bvps","VG");}
                else if(rank[8]<=(length3*2/5)){attri.put("bvps","G");}
                else if(rank[8]<=(length3*3/5)){attri.put("bvps","N");}
                else if(rank[8]<=(length3*4/5)){attri.put("bvps","B");}
                else{attri.put("bvps","VB");}
                if(rank[9]<=(length3/5)){attri.put("perundp","VG");}
                else if(rank[9]<=(length3*2/5)){attri.put("perundp","G");}
                else if(rank[9]<=(length3*3/5)){attri.put("perundp","N");}
                else if(rank[9]<=(length3*4/5)){attri.put("perundp","B");}
                else{attri.put("perundp","VB");}
                if(rank[10]<=(length3/5)){attri.put("rev","VG");}
                else if(rank[10]<=(length3*2/5)){attri.put("rev","G");}
                else if(rank[10]<=(length3*3/5)){attri.put("rev","N");}
                else if(rank[10]<=(length3*4/5)){attri.put("rev","B");}
                else{attri.put("rev","VB");}
                if(rank[11]<=(length3/5)){attri.put("gpr","VG");}
                else if(rank[11]<=(length3*2/5)){attri.put("gpr","G");}
                else if(rank[11]<=(length3*3/5)){attri.put("gpr","N");}
                else if(rank[11]<=(length3*4/5)){attri.put("gpr","B");}
                else{attri.put("gpr","VB");}
                Stock s=new Stock(sh.stockName,sh.code,sh.pchange,sh.label,attri);
                data.add(s);
                for(int k=0;k<12;k++){
                    rank[k]=1;
                }
            }
            //现在抽象队列已经填充完毕
            IGR igr = new IGR(numberOfKeyChar, data);
            List<IGRperFeature> feature = new ArrayList<>();//初始特征集，一开始是空队列
            List<String> v1=new ArrayList<>();
            List<String> v2=new ArrayList<>();
            List<String> v3=new ArrayList<>();
            List<String> v4=new ArrayList<>();
            List<String> v5=new ArrayList<>();
            List<String> v6=new ArrayList<>();
            List<String> v7=new ArrayList<>();
            List<String> v8=new ArrayList<>();
            List<String> v9=new ArrayList<>();
            List<String> v10=new ArrayList<>();
            List<String> v11=new ArrayList<>();
            List<String> v12=new ArrayList<>();
            for(int i=0;i<data.size();i++){
                v1.add(data.get(i).getAttributeList().get("volume"));
                v2.add(data.get(i).getAttributeList().get("pchange"));
                v3.add(data.get(i).getAttributeList().get("changerate"));
                v4.add(data.get(i).getAttributeList().get("pb"));
                v5.add(data.get(i).getAttributeList().get("profit"));
                v6.add(data.get(i).getAttributeList().get("mrq"));
                v7.add(data.get(i).getAttributeList().get("caprat"));
                v8.add(data.get(i).getAttributeList().get("esp"));
                v9.add(data.get(i).getAttributeList().get("bvps"));
                v10.add(data.get(i).getAttributeList().get("perundp"));
                v11.add(data.get(i).getAttributeList().get("rev"));
                v12.add(data.get(i).getAttributeList().get("gpr"));
            }
            feature.add(new IGRperFeature("volume",0,v1));
            feature.add(new IGRperFeature("pchange",0,v2));
            feature.add(new IGRperFeature("changerate",0,v3));
            feature.add(new IGRperFeature("pb",0,v4));
            feature.add(new IGRperFeature("profit",0,v5));
            feature.add(new IGRperFeature("mrq",0,v6));
            feature.add(new IGRperFeature("caprat",0,v7));
            feature.add(new IGRperFeature("esp",0,v8));
            feature.add(new IGRperFeature("bvps",0,v9));
            feature.add(new IGRperFeature("perundp",0,v10));
            feature.add(new IGRperFeature("rev",0,v11));
            feature.add(new IGRperFeature("gpr",0,v12));
            List<IGRperFeature> f = igr.getFeature(feature);//传入初始特征集，得到关键特征集
            for(int i=0;i<f.size();i++){
                System.out.println("***"+f.get(i).getFeature()+"***信息增益比是"+f.get(i).getIGR());
            }
            DTree tree = new DTree(f, data, a);//建立决策树
            //cart(tree, tree.getRoot(), a);//剪枝
            Node root = tree.getRoot();//得到根节点
            for(int i=0;i<data.size();i++) {
                Stock stock=data.get(i);
                boolean result = forecast(stock, root);//进行预测，得到结果，true说明值得推荐
                if(result){
                    if(stock.getLabel()) {
                        System.out.println("*****" + stock.getCode() + "*********");
                        maps.put(stock.getName() + ":" + stock.getCode(), list.get(stock.getCode()).get(list.get(stock.getCode()).size()-1).pchange);
                    }
                }
            }
        }
        System.out.println("有"+maps.size()+"只推荐股票");
        return maps;
    }

    @Override
    public List<StockDayEntity> getSDE_forConceptBench ( String benchName) {
        LocalDate today = SDF.dateToLocalDate(StockCalendar.getToday());
        today = strategyDao.resetEnd(today) ;
        return stockDao.get_SDE_forConceptBench(benchName,today);
    }

    @Override
    public List<StockDayEntity> findSDE_forConceptBench ( String benchName,LocalDate localDate ) {

        return stockDao.get_SDE_forConceptBench(benchName,localDate);
    }

    /**
     * 返回预测结果
     * @param stock
     * @param root
     * @param
     * @return
     */
    private boolean forecast(Stock stock, Node root) {
        String value=stock.getAttributeList().get(root.getName());
        if(root.getList().size()<5){
            if(root.getList().get(value)==null) {
                return root.getCtg();
            }
            else{
                Node node=root.getList().get(value);
                return forecast(stock,node);
            }
        }
        else{
            Node node=root.getList().get(value);
            return forecast(stock,node);
        }
    }
}
