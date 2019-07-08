package service.impl;

import dao.StrategyDao;
import entity.MarketEntity;
import entity.StockEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.ApplyStrategy;
import util.CompareatorProfit;
import util.S;
import util.T;
import vo.DailyYield;
import vo.SelectVO;
import vo.StrategyVO;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by lienming on 2017/5/18.
 */
@Service
public class ApplyStrategyImpl implements ApplyStrategy {

    @Autowired
    private StrategyDao strategyDao;


    private Map<String, Double> rfList;//历年央行存款基准利率


    private void initRfList(){
        rfList=new HashMap<>();
        rfList.put("2005",2.25);
        rfList.put("2006",2.52);
        rfList.put("2007",3.47);
        rfList.put("2008",3.06);
        rfList.put("2009",2.25);
        rfList.put("2010",2.63);
        rfList.put("2011",3.25);
        rfList.put("2012",3.13);
        rfList.put("2013",3.00);
        rfList.put("2014",2.75);
        rfList.put("2015",2.00);
        rfList.put("2016",1.50);
        rfList.put("2017",1.50);
    }

    @Override
    public StrategyVO SelectMomStrategy(boolean type, long money, LocalDate begin, LocalDate end, int holdTime, int formTime, SelectVO vo) throws RemoteException {
long startTime = System.currentTimeMillis();    //获取开始时间
        if(begin .isAfter(end) ){
            return null;
        }
        double Rf=getRf(begin,end);//得到无风险利率
        List<String> pool;//股票池
        /**
         * 确定股票池
         */
        pool=getPool(type,vo,begin ,end);
        /**
         * 重新定位开始日期和结束日期
         */
        begin=resetBegin(begin);
        LocalDate ye=getFormEnd(begin);
        LocalDate oldBegin=begin;
        end=resetEnd(end);
        if(end.isBefore(begin)){
            return null;
        }
        if(vo.list ==null) {
            if (vo.pool.equals("M") && (begin.isBefore(LocalDate.of(2005, 4, 8)) || end.isAfter(LocalDate.of(2017, 6, 12)))) {
                return null;
            }
            if (vo.pool.equals("S") && (begin.isBefore(LocalDate.of(2007, 9, 5)) || end.isAfter(LocalDate.of(2017, 6, 12)))) {
                return null;
            }
            if (vo.pool.equals("G") && (begin.isBefore(LocalDate.of(2010, 6, 2)) || end.isAfter(LocalDate.of(2017, 6, 12)))) {
                return null;
            }
        }
        int offset= getRealPeriod(begin,end);
        int circle=offset/holdTime;//得到换股次数
        if(circle*holdTime <offset ){
            circle+=1;//最后一次不满持有期，但仍然需要换股
        }
        LocalDate formStart=getFormStart(begin, formTime);//得到形成期开始时间
        LocalDate formEnd=getFormEnd(begin);//得到形成期结束时间
        double result = money;//最终的钱,初始化为原始资本
        List<S> holdedS=new ArrayList<>();//当前持有的股票,初始化为空
        int profit=0;//正收益周期数
        int loss=0;//负收益周期数
        List<Double> profitList=new ArrayList<>() ;//记录每次正收益周期
        List<Double> lossList=new ArrayList<>() ;//记录每次负收益周期
        List<DailyYield> benchmarkYield;
        List<DailyYield> bench=new ArrayList<>() ;
        bench.add(new DailyYield(ye,0) );
        if(type==true) {
            List<List<DailyYield>> res=getBenchmarkYield(vo.pool, begin, offset);
            if(res.isEmpty() ||res==null){
                return null;
            }
            benchmarkYield=res.get(0);//记录每日基准收益率
            bench.addAll(res.get(1)); //记录每日基准累计收益率
        }
        else{
            List<List<DailyYield>> res=getBenchmarkYield(vo.list ,begin,offset);
            if(res.isEmpty() ||res==null){
                return null;
            }
            benchmarkYield = res.get(0);
            bench.addAll(res.get(1));
        }
        List<DailyYield > strategyYield=new ArrayList<>() ;//记录每日策略收益率
        List<DailyYield > strategy=new ArrayList<>() ;//记录每日策略累计收益率
        strategy .add(new DailyYield(ye, 0) ) ;
        double oldResult=result;
        /**
         * 开始迭代
         */
        for(int i=0;i<=circle&&begin!=null  ;i++){
            LocalDate yest=getFormEnd(begin);
            if(!holdedS.isEmpty() ){//当前已经持有股票
                for(int j=0;j<holdedS .size() ;j++){
                    S s=holdedS .get(j);
                    StockEntity po=getStockByCodeAndDay(s.getCode(),yest) ;
                    if(po==null){
                        po=getNearestStock(s.getCode() ,yest);
                    }
                    if(po!=null) {
                        s.setEndPrice(po.getAdjClose());//设置持有期结束时的最终价
                    }
                    else{//getNearestStock方法也无法调整,只能假定最终价与初始价相同
                        s.setEndPrice(s.getStartPrice() );
                    }
                    double profitPerCircle=(s.getEndPrice()-s.getStartPrice())*s.getNumber();
                    result+=profitPerCircle ;//加上这只股票在持有期间的收益
                }
                List<List<DailyYield>> res=fillStrategyYield(money ,oldBegin ,yest,holdedS,strategyYield ,strategy);
                strategyYield=res.get(0);//填充一个周期的策略收益率
                strategy=res.get(1) ;//填充一个周期的策略累计收益率
            }
            if(result >=oldResult  ){
                profit++;
                profitList .add((result -oldResult)/oldResult *100 );
            }
            if(result <oldResult ){
                loss++;
                lossList .add((result -oldResult)/oldResult *100 );
            }
            holdedS=changeStock(formStart ,formEnd ,pool,result );//进行换股
            formStart=resetFormStart(formStart, holdTime );//重新定位形成开始时间
            formEnd =resetFormEnd(formEnd , holdTime );//重新定位形成结束时间
            oldBegin =begin;
            begin=reset(begin, holdTime, end );//为开始时间增加一个持有期时间，逐步逼近结束时间
            oldResult =result;
        }
        double maxReturn=0;
        for(int i=0;i<strategy .size() ;i++){//计算最大回撤
            double x=strategy .get(i).getDailyYield() ;
            for(int j=i+1;j<strategy .size() ;j++){
                double y=strategy .get(j).getDailyYield() ;
                if((x-y)>maxReturn ){
                    maxReturn =x-y;
                }
            }
        }
        double annualYield=strategy .get(strategy.size() -1).getDailyYield() /offset *365;//计算年化收益率
        double benchmarkAnnualYield=bench.get(bench.size() -1).getDailyYield() /offset*365;//计算基准年化收益率
        double strategySD=Math.sqrt(getVar(strategyYield,strategyYield));//得到策略收益率的标准差
        double ERp=strategy .get(strategy.size() -1).getDailyYield();//计算策略收益率
        double sharp=(ERp-Rf)/strategySD/100;//计算夏普比率
        double benchmarkVar=getVar(benchmarkYield,benchmarkYield);//得到基准收益率的方差
        double cov=getVar(strategyYield,benchmarkYield);
        double beta=cov/benchmarkVar;//计算贝塔比率
        double alpha =annualYield-Rf-beta*(benchmarkAnnualYield-Rf);//计算阿尔法比率
        StrategyVO svo=new StrategyVO(bench ,strategy,profit ,profitList ,loss ,lossList ,annualYield,benchmarkAnnualYield,alpha ,beta ,sharp ,maxReturn);
long endTime = System.currentTimeMillis();    //获取结束时间
System.out.println("SelectMomStrategy程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        return svo;
    }

    private List<String> getPool(boolean type, SelectVO vo,LocalDate begin, LocalDate end) throws RemoteException {
        List<String> pool;
        if(type==true){//按板块作为范围
            if(vo.hasST){//
                if(strategyDao==null){
                    System.out.println("fuckingggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
                }
                else System.out.println(vo.pool+"*******************"+begin+"************"+end);
                 pool=strategyDao.getPool(vo.pool,true, begin ,end);
            }
            else{
                pool=strategyDao.getPool(vo.pool,false,begin,end);
            }
        }
        else{//自选股票池
            if(vo.list.size()<100){//至少选100只股票
                return null;
            }
            else{
                pool=vo.list;//直接将vo中的股票代码列表赋给pool
            }
        }
        return pool;
    }


    @Override
    public List<Double> forBChart(boolean type, long money, LocalDate begin, LocalDate end, int holdTime, int formTime, SelectVO vo) throws RemoteException {
        if(begin .isAfter(end) ){
            return null;
        }
        double winDays=0;//策略获胜的天数
        List<String> pool;//股票池
        /**
         * 确定股票池
         */
        pool=getPool(type,vo,begin ,end);
        /**
         * 重新定位开始日期和结束日期
         */
        begin=resetBegin(begin);
        LocalDate ye=getFormEnd(begin);
        LocalDate oldBegin=begin;
        end=resetEnd(end);
        if(end.isBefore(begin)){
            return null;
        }
        if(vo.list ==null) {
            if (vo.pool.equals("M") && (begin.isBefore(LocalDate.of(2005, 4, 8)) || end.isAfter(LocalDate.of(2017, 3, 31)))) {
                return null;
            }
            if (vo.pool.equals("S") && (begin.isBefore(LocalDate.of(2007, 9, 5)) || end.isAfter(LocalDate.of(2017, 3, 31)))) {
                return null;
            }
            if (vo.pool.equals("G") && (begin.isBefore(LocalDate.of(2010, 6, 2)) || end.isAfter(LocalDate.of(2017, 3, 31)))) {
                return null;
            }
        }
        int offset= getRealPeriod(begin,end);
        double benchmarkAnnualYield;
        if(type==true) {
            benchmarkAnnualYield = getBenchmarkAnnualYield(vo.pool, end, begin,offset);//获取基准收益率
        }
        else{
            benchmarkAnnualYield=getBenchmarkAnnualYield(vo.list,offset,begin);
        }
        int circle=offset/holdTime;//得到换股次数
        if(circle*holdTime <offset ){
            circle+=1;//最后一次不满持有期，但仍然需要换股
        }
        LocalDate formStart=getFormStart(begin, formTime);//得到形成期开始时间
        LocalDate formEnd=getFormEnd(begin);//得到形成期结束时间
        double result = money;//最终的钱,初始化为原始资本
        double oldResult;
        List<S> holdedS=new ArrayList<>();//当前持有的股票,初始化为空
        /**
         * 开始迭代
         */
        for(int i=0;i<=circle&&begin!=null  ;i++){
            oldResult =result;
            LocalDate yest=getFormEnd(begin);
            if(!holdedS.isEmpty() ){//当前已经持有股票
                for(int j=0;j<holdedS .size() ;j++){
                    S s=holdedS .get(j);
                    StockEntity po=getStockByCodeAndDay(s.getCode(),yest) ;
                    if(po==null){
                        po=getNearestStock(s.getCode() ,yest);
                    }
                    if(po!=null) {
                        s.setEndPrice(po.getAdjClose());//设置持有期结束时的最终价
                    }
                    else{//getNearestStock方法也无法调整,只能假定最终价与初始价相同
                        s.setEndPrice(s.getStartPrice() );
                    }
                    double profitPerCircle=(s.getEndPrice()-s.getStartPrice())*s.getNumber();
                    result+=profitPerCircle ;//加上这只股票在持有期间的收益
                }
                double p1=(result -oldResult)/oldResult *100;
                int interval=getRealPeriod(oldBegin ,yest );
                double p2;
                if(type==true) {
                    p2 = getBenchmarkAnnualYield(vo.pool, yest, oldBegin ,interval );//获取此持有期基准收益率
                }
                else{
                    p2=getBenchmarkAnnualYield(vo.list,interval ,oldBegin );
                }
                if(p1>p2){
                    winDays ++;
                }
            }
            holdedS=changeStock(formStart ,formEnd ,pool,result );//进行换股
            formStart=resetFormStart(formStart, holdTime );//重新定位形成开始时间
            formEnd =resetFormEnd(formEnd , holdTime );//重新定位形成结束时间
            oldBegin =begin;
            begin=reset(begin, holdTime, end );//为开始时间增加一个持有期时间，逐步逼近结束时间
        }
        double annualYield=(result -money )/money *100;//计算年化收益率
        double excessProfit=(annualYield -benchmarkAnnualYield);//计算超额收益率
        //System .out.println(excessProfit);
        double strategyWinRate=(winDays/circle)*100;//计算策略胜率
        List<Double> ld=new ArrayList<>() ;
        ld.add(excessProfit) ;
        ld.add(strategyWinRate) ;
        return ld;
    }

    private StockEntity getNearestStock(String code, LocalDate date) {
        return strategyDao .findNearestStock(code, date);
    }

    /**
     * 计算无风险利率
     * @param begin
     * @param end
     * @return
     */
    private double getRf(LocalDate begin, LocalDate end) {
        initRfList();
        double rf=0;
        int x=begin.getYear();
        int y=end.getYear();
        for(int i=x ;i<=y ;i++){
            rf+=rfList.get(i+"");
        }
        rf/=(y-x+1);
        return rf;
    }

    /**
     * 得到协同方差
     * @param list1
     * @param list2
     * @return
     */
    private double getVar(List<DailyYield> list1, List<DailyYield> list2) {

        int length1=list1.size() ;
        int length2=list2.size();
        int length = 0;
        if(length1 > length2) {
            length = length2;
        } else {
            length = length1;
        }
        double result=0;
        double ave1=0;
        double ave2=0;
        for(int i=0;i<length ;i++){
            ave1+=list1.get(i).getDailyYield()/100 ;
            ave2+=list2.get(i).getDailyYield()/100 ;
        }
        ave1/=(length);
        ave2/=(length);
        for(int i=0;i<length;i++){
            result +=(list1.get(i).getDailyYield()/100-ave1)*(list2.get(i).getDailyYield()/100-ave2);
        }
        result/=(length -1);
        return result;
    }

    /**
     * 填充一个周期的策略收益率
     * @param oldBegin
     * @param begin
     * @param holdedS
     * @return
     */
    private List<List<DailyYield> > fillStrategyYield(long money,LocalDate oldBegin, LocalDate begin, List<S> holdedS, List<DailyYield> list1,List<DailyYield> list2) throws RemoteException {
        //System.out.println("一个周期的策略收益,从"+oldBegin +"到"+begin );
        List<List<DailyYield> > list=new ArrayList<>() ;
        int offset= getRealPeriod(oldBegin ,begin );
        double init=0;
        LocalDate b=strategyDao .findPastDaysByOffset(oldBegin ,1);
        int len1=holdedS .size() ;
        int len2 ;
        if(b==null){
            for(int i=0;i<holdedS .size() ;i++){
                StockEntity po=strategyDao .findStockByCodeAndDay(holdedS .get(i).getCode(),oldBegin );
                if(po!=null) {
                    init += po.getOpen();
                }
                else{
                    len1--;
                }
            }
        }
        else{
            for(int i=0;i<holdedS .size() ;i++){
                StockEntity po=strategyDao .findStockByCodeAndDay(holdedS .get(i).getCode() ,b);
                if(po!=null) {
                    init += po.getAdjClose();
                }
                else{
                    len1--;
                }
            }
        }
        init /=len1 ;
        for(int i=0;i<offset;i++){
            len2=holdedS .size() ;
            double resultMoney=0;
            double endPrice=0;
            for(int j=0;j<holdedS .size() ;j++){
                StockEntity po=strategyDao .findStockByCodeAndOffset(holdedS .get(j).getCode() ,oldBegin  ,i);
                if(po!=null) {
                    endPrice += po.getAdjClose();
                    resultMoney +=po.getAdjClose() *holdedS .get(j).getNumber() ;
                }
                else{
                    len2--;
                    resultMoney +=holdedS .get(j).getStartPrice()*holdedS .get(j).getNumber() ;
                }
            }
            endPrice /=len2 ;
            LocalDate date=strategyDao .findFutureDaysByOffset(oldBegin  ,i);
            DailyYield dy1=new DailyYield(date,(endPrice -init)/init *100 ) ;
            list1.add(dy1) ;
            DailyYield dy2=new DailyYield(date,(resultMoney -money)/money *100 ) ;
            list2.add(dy2) ;
            init =endPrice ;
        }
        list.add(list1);
        list.add(list2);
        return list;
    }

    /**
     * 计算每日基准收益率,不能处理自选股的情况
     * @param begin
     * @param offset
     * @return
     */
    private List<List<DailyYield> > getBenchmarkYield(String pool, LocalDate begin, int offset) {
        List<List<DailyYield> > list=new ArrayList<>() ;
        List<DailyYield > list1=new ArrayList<>();
        List<DailyYield > list2=new ArrayList<>();
        double init;//起始价
        LocalDate b=strategyDao .findPastDaysByOffset(begin ,1);
        if(b==null){
            MarketEntity po=strategyDao.findMarketIndexByDate(pool, begin);
            if(po==null){
                return new ArrayList<List<DailyYield> >() ;
            }
            init=po.getOpen();//不得已只能取开盘价
        }
        else{
            MarketEntity po=strategyDao .findMarketIndexByDate(pool,b);
            if(po==null){
                return new ArrayList<List<DailyYield> >() ;
            }
            init=po.getClose();//取前一日的收盘价
        }
        double keepInit=init;
        for(int i=0;i<offset ;i++) {
            LocalDate date=strategyDao .findFutureDaysByOffset(begin, i);
            MarketEntity mpo=strategyDao.findMarketIndexByOffset(pool, begin, i) ;
            double endPrice = mpo.getClose();
            DailyYield dy1=new DailyYield(date, (endPrice-init)/init*100);
            list1.add(dy1);
            DailyYield dy2=new DailyYield(date, (endPrice-keepInit )/keepInit *100);
            list2.add(dy2);
            init=endPrice;
        }
        list.add(list1);
        list.add(list2);
        return list;
    }

    /**
     * 计算每日基准收益率，专门处理自选股票池
     * @param list
     * @param begin
     * @param offset
     * @return
     */
    private List<List<DailyYield> > getBenchmarkYield(List<String> list, LocalDate begin, int offset) throws RemoteException {
        int length=list.size() ;
        List<List<DailyYield> > dyList =new ArrayList<>() ;
        List<DailyYield > dyList1=new ArrayList<>();
        List<DailyYield > dyList2=new ArrayList<>();
        LocalDate b=strategyDao .findPastDaysByOffset(begin ,1);
        double init=0;
        int reLen=length;
        if(b ==null) {
            for(int i=0;i<length;i++){
                StockEntity po1=strategyDao .findStockByCodeAndDay(list.get(i),begin );
                if(po1!=null) {
                    init += po1.getAdjOpen();
                }
                else{
                    reLen--;//排除停牌股票,减少股票数量
                }
            }
        }
        else{
            for(int i=0;i<length;i++){
                StockEntity po1=strategyDao .findStockByCodeAndDay(list.get(i),b );
                if(po1!=null) {
                    init += po1.getAdjClose();
                }
                else{
                    reLen--;//排除停牌股票,减少股票数量
                }
            }
        }
        init/=reLen;
        int reLength;
        double keepInit=init;
        for(int i=0;i<offset;i++){
            reLength=length;
            double endPrice=0;
            for(int j=0;j<length;j++){
                StockEntity po=strategyDao .findStockByCodeAndOffset(list.get(j), begin, i);
                if(po!=null) {
                    endPrice += po.getAdjClose();
                }
                else{
                    reLength --;//排除停牌股票,减少股票数量
                }
            }
            endPrice /=reLength;
            //System.out.println("总数为"+reLength+"               结束值为"+endPrice);
            LocalDate date=strategyDao .findFutureDaysByOffset(begin, i);
            DailyYield dy1=new DailyYield(date, (endPrice -init)/init *100) ;
            dyList1 .add(dy1);
            DailyYield dy2=new DailyYield(date, (endPrice -keepInit )/keepInit  *100) ;
            //System.out.println("今日值为"+endPrice+"  累计收益率为"+(endPrice -keepInit )/keepInit  *100);
            dyList2 .add(dy2);
            init=endPrice ;//将下一天的init换为今天的endPrice
        }
        dyList .add(dyList1 );
        dyList .add(dyList2 );
        return dyList;
    }

    /**
     * 计算基准收益率,不能处理自选股的情况
     * @param offset
     * @param end
     * @param begin
     * @return
     */
    private double getBenchmarkAnnualYield(String pool, LocalDate end, LocalDate begin,int offset) {
        LocalDate b=strategyDao .findPastDaysByOffset(begin ,1);
        double result;
        if(b ==null) {
            List<MarketEntity> list = strategyDao.getMarketData(pool, end, begin);//list中包含两个po
            result = (list.get(1).getClose() - list.get(0).getOpen())/list.get(0).getOpen()/offset *100;//不得已，只能取开盘价
        }
        else{
            List<MarketEntity> list = strategyDao.getMarketData(pool, end, b);//list中包含两个po
            result = (list.get(1).getClose() - list.get(0).getClose())/list.get(0).getClose()/offset*100;
        }
        return result;
    }

    /**
     * 计算基准收益率,专门处理自选股的情况
     * @param list
     * @param offset
     * @param begin
     * @return
     */
    private double getBenchmarkAnnualYield(List<String> list, int offset, LocalDate begin) throws RemoteException {
        int length=list.size() ;
        LocalDate b=strategyDao .findPastDaysByOffset(begin ,1);
        double init=0;
        double result=0;
        int reLen=length;
        if(b ==null) {
            for(int i=0;i<length;i++){
                StockEntity po1=strategyDao .findStockByCodeAndDay(list.get(i),begin );
                StockEntity po2=strategyDao .findStockByCodeAndOffset(list.get(i),begin,offset);
                if(po1!=null&&po2!=null){
                    init += po1.getOpen();
                    result += po2.getAdjClose();
                }
                else{
                    reLen--;//除去停牌股票,减少股票数量
                }
            }
        }
        else{
            for(int i=0;i<length;i++){
                StockEntity po1=strategyDao .findStockByCodeAndDay(list.get(i),b );
                StockEntity po2=strategyDao .findStockByCodeAndOffset(list.get(i),begin,offset);
                if(po1!=null&&po2!=null) {
                    init += po1.getAdjClose();
                    result += po2.getAdjClose();
                }
                else{
                    reLen--;//除去停牌股票,减少股票数量
                }
            }
        }
        init /=reLen ;
        result /=reLen;
        double rate=(result -init)/init/offset *100;
        return rate;
    }

    /**
     * 增加一个持有期时间，逐步逼近结束时间
     * @param begin
     * @param holdTime
     * @return
     */
    private LocalDate reset(LocalDate begin, int holdTime, LocalDate end){
        LocalDate date=strategyDao .findFutureDaysByOffset(begin,holdTime );
        if(date == null) {
            return null;
        }
        if(date.isAfter(end)){
            return strategyDao .findFutureDaysByOffset(end, 1) ;
        }
        return date;
    }

    /**
     * 重新定位形成开始时间
     * @param formStart
     * @param holdTime
     * @return
     */
    private LocalDate resetFormStart (LocalDate formStart, int holdTime){
        LocalDate date=strategyDao .findFutureDaysByOffset(formStart ,holdTime );
        return date;
    }

    /**
     * 重新定位形成结束时间
     * @param formEnd
     * @param holdTime
     * @return
     */
    private LocalDate resetFormEnd (LocalDate formEnd,int holdTime){
        LocalDate date=strategyDao .findFutureDaysByOffset(formEnd ,holdTime );
        return date;
    }

    /**
     * 换股
     * @param formStart
     * @param formEnd
     * @param pool
     * @param money
     * @return
     */
    private List<S> changeStock(LocalDate formStart,LocalDate formEnd,List<String> pool,double money) throws RemoteException {
long startTime = System.currentTimeMillis();    //获取开始时间
        LocalDate oldFormStart=formStart ;
        Map<String, Double> map=new HashMap<>() ;
        List<S> slist=new ArrayList<S>();
        List<T> tlist=new ArrayList<T>();
        int length=pool.size() ;
        for(int i=0;i<length ;i++){
            formStart=strategyDao .findPastDaysByOffset(oldFormStart, 1);
            StockEntity stockStart=strategyDao .findStockByCodeAndDay(pool .get(i),formStart);
            StockEntity stockEnd=strategyDao .findStockByCodeAndDay(pool.get(i),formEnd  );
            double profit=-100000;//初始化为-100000,如果是停牌股票,其收益就是这个值,不太可能排入前百分之二十
            if(stockStart !=null&&stockEnd !=null) {
                profit = (stockEnd.getAdjClose() - stockStart.getAdjClose()) / stockStart.getAdjClose() * 100;
                map.put(pool.get(i), stockEnd.getAdjClose());
            }
            if(pool.get(i).contains("ST") ){
                profit *=2;//对于ST股票将其涨幅换算为10%涨停的模式
            }
            T t=new T(pool.get(i),profit);
            tlist.add(t);
        }
        CompareatorProfit cp=new CompareatorProfit();
        Collections.sort(tlist,cp);//按利润降序排列
        int len=length/5;
        if(len*5<length){
            len++;
        }
        int reLen=0;//还是谨慎一点,防止停牌股票混入前百分之二十
        for(int i=0;i<len;i++){
            T t=tlist.get(i);
            if(t.getProfit() !=-100000){
                reLen++;
            }
        }
        double avemoney=money/reLen;
        for(int i=0;i<reLen;i++){
            T t=tlist.get(i);
            String code=t.getCode();
            double startPrice=map.get(code);
            double number=avemoney/startPrice;
            slist.add(new S(code,startPrice,number));
        }
long endTime = System.currentTimeMillis();    //获取结束时间
System.out.println("changestockmon程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        return slist;
    }

    /**
     * 按代码和时间得到股票记录
     * @param code
     * @param date
     * @return
     */
    private StockEntity getStockByCodeAndDay (String code, LocalDate date) throws RemoteException {
        StockEntity po=strategyDao .findStockByCodeAndDay(code,date);
        return po;
    }

    /**
     * 得到形成期开始时间
     * @param begin
     * @param formTime
     * @return
     */
    private LocalDate getFormStart (LocalDate begin, int formTime){
        LocalDate date=strategyDao .findPastDaysByOffset(begin,formTime ) ;
        return date;
    }

    /**
     * 得到形成期结束时间
     * @param begin
     * @return
     */
    private LocalDate getFormEnd(LocalDate begin){
        LocalDate date=strategyDao .findPastDaysByOffset(begin ,1);
        return date;
    }


    /**
     * 获取真实的时段，剔除非交易日的影响
     * @param begin
     * @param end
     * @return
     */
    private int getRealPeriod(LocalDate begin,LocalDate end){
        int period=strategyDao .getRealPeriod(begin,end);
        return period;
    }

    /**
     * 重新调整开始日期，使之成为交易日
     * @return
     */
    private LocalDate resetBegin(LocalDate begin){
        LocalDate date=strategyDao .resetBegin(begin);
        return date;
    }

    /**
     * 重新调整结束日期，使之成为交易日
     * @param end
     * @return
     */
    private LocalDate resetEnd(LocalDate end){
        LocalDate date=strategyDao .resetEnd(end);
        return date;
    }





    @Override
    public StrategyVO SelectAveStrategy(boolean type, long money, LocalDate begin, LocalDate end, int holdTime, int formTime, SelectVO vo) throws RemoteException {
 long startTime = System.currentTimeMillis();    //获取开始时间
        if(begin .isAfter(end) ){
            return null;
        }

        //double winDays=0;//策略获胜的天数
        double Rf=getRf(begin,end);//得到无风险利率
        List<String> pool;//股票池
        /**
         * 确定股票池
         */
        pool=getPool(type,vo,begin ,end);
        /**
         * 重新定位开始日期和结束日期
         */
        begin=resetBegin(begin);
        LocalDate ye=getFormEnd(begin);
        LocalDate oldBegin=begin;
        end=resetEnd(end);
        if(end.isBefore(begin)){
            return null;
        }

        int offset= getRealPeriod(begin,end);//交易天数
//        double benchmarkAnnualYield;
//        if(type==true) {
//            benchmarkAnnualYield = getBenchmarkAnnualYield(vo.pool, end, begin,offset)*365;//获取基准年化收益率
//        }
//        else{
//            benchmarkAnnualYield=getBenchmarkAnnualYield(vo.list,offset,begin)*365;
//        }

        int circle = offset/holdTime;//得到换股次数
        if(circle*holdTime < offset ){
            circle+=1;//最后一次不满持有期，但仍然需要换股
        }

        LocalDate formStart = getFormStart(begin, formTime);//得到形成期开始时间
        LocalDate formEnd = getFormEnd(begin);//得到形成期结束时间

        double result = money;//最终的钱,初始化为原始资本
        double lastResult = money;//上一周期的钱，初始为原始资本

        List<S> holdedS = new ArrayList<S>();//当前持有的股票,初始化为空
        int num = 10;//持有的股票数量

        int profit = 0;//正收益周期数
        int loss = 0;//负收益周期数
        List<Double> profitList=new ArrayList<>() ;//记录每次正收益周期
        List<Double> lossList=new ArrayList<>() ;//记录每次负收益周期


        List<DailyYield> benchmarkYield=new ArrayList<>() ;


        List<DailyYield> bench=new ArrayList<>() ;
        //bench.add(new DailyYield(ye,0) );

        if(type==true) {
            List<List<DailyYield>> res=getBenchmarkYield(vo.pool, begin, offset);
            benchmarkYield=res.get(0);//记录每日基准收益率
            bench.addAll(res.get(1)); //记录每日基准累计收益率
        }
        else{
            List<List<DailyYield>> res=getBenchmarkYield(vo.list ,begin,offset);
            benchmarkYield = res.get(0);
            bench.addAll(res.get(1));
        }

        List<DailyYield > strategyYield=new ArrayList<>() ;//记录每日策略收益率
        List<DailyYield > strategy=new ArrayList<>() ;//记录每日策略累计收益率
        strategy .add(new DailyYield(ye, 0) ) ;

        /**
         * 迭代
         */
        for(int i=0;i<=circle&&begin!=null  ;i++){
            LocalDate yest=getFormEnd(begin);
            lastResult = result;
            if(!holdedS.isEmpty() ){//当前已经持有股票
                //result=0;
                for(int j=0;j<holdedS .size() ;j++){
                    S s=holdedS .get(j);
//                    StockEntity po=getStockByCodeAndDay(s.getCode(),begin);
//                    int tempIndex = 0;
//                    while(po == null) {
//                        tempIndex ++;
//System.out.println("getStockByCodeAndDay空指针,之前第" + tempIndex + "天");
//                        po = strategyDao.findStockByCodeAndOffset(s.getCode(),begin, -tempIndex);
//                    }
//                    s.setEndPrice(po.getAdjClose());//设置持有期结束时的最终价
                    StockEntity po=getStockByCodeAndDay(s.getCode(),yest) ;
                    if(po==null){
                        po=getNearestStock(s.getCode() ,yest);
                    }
                    if(po!=null) {
                        s.setEndPrice(po.getAdjClose());//设置持有期结束时的最终价
                    }
                    else{//getNearestStock方法也无法调整,只能假定最终价与初始价相同
                        s.setEndPrice(s.getStartPrice() );
                    }
                    double profitPerCircle=(s.getEndPrice()-s.getStartPrice())*s.getNumber();
                    result+=profitPerCircle ;//加上这只股票在持有期间的收益
                }
                List<List<DailyYield>> res=fillStrategyYield(money ,oldBegin ,yest,holdedS,strategyYield ,strategy);
                strategyYield=res.get(0);//填充一个周期的策略收益率
                strategy=res.get(1) ;//填充一个周期的策略累计收益率
            }
            if(result > lastResult){
                profit++;
                profitList .add((result - lastResult) / lastResult * 100 );
            }
            if(result < lastResult){
                loss++;
                lossList .add((result - lastResult) / lastResult * 100 );
            }

            holdedS=changeStockByAve(formStart , formEnd, pool, num, result);//进行换股

            formStart=resetFormStart(formStart, holdTime );//重新定位形成开始时间
            formEnd =resetFormEnd(formEnd , holdTime );//重新定位形成结束时间
            oldBegin =begin;
            begin=reset(begin, holdTime, end );//为开始时间增加一个持有期时间，逐步逼近结束时间
        }

//        int sizeStra = strategyYield.size();
//        int sizeBenc = benchmarkYield.size();
//System.out.println("strategyYield.size:" + strategyYield.size());
//System.out.println("benchmarkYield.size:" + benchmarkYield.size());
//        while(strategyYield.size() != benchmarkYield.size()) {
//            int len = benchmarkYield.size();
//            benchmarkYield.remove(len-1);
//        }
//        int sizeFor = 0;
//        if(sizeStra > sizeBenc) {
//            sizeFor = sizeBenc;
//        } else {
//            sizeFor = sizeStra;
//        }
//        for(int i=0;i<sizeFor ;i++){
//            if(strategyYield .get(i).getDailyYield()>benchmarkYield.get(i).getDailyYield() ){
//                winDays++;
//            }
//        }
        //for(int i=0;i<strategy.size() ;i++){
        //System.out.println(strategy .get(i).getDate() +"        "+strategy.get(i).getDailyYield() );
        //}
        double maxReturn=0;
        for(int i=0;i<strategy .size() ;i++){//计算最大回撤
            double x=strategy .get(i).getDailyYield() ;
            for(int j=i+1;j<strategy .size() ;j++){
                double y=strategy .get(j).getDailyYield() ;
                if((x-y)>maxReturn ){
                    maxReturn =x-y;
                }
            }
        }
        double annualYield=strategy .get(strategy.size() -1).getDailyYield() /offset *365;//计算年化收益率
        double benchmarkAnnualYield=bench.get(bench.size() -1).getDailyYield() /offset*365;//计算基准年化收益率
        double strategySD=Math.sqrt(getVar(strategyYield,strategyYield));//得到策略收益率的标准差
        double ERp=strategy .get(strategy.size() -1).getDailyYield();//计算策略收益率
        double sharp=(ERp-Rf)/strategySD/100;//计算夏普比率
        double benchmarkVar=getVar(benchmarkYield,benchmarkYield);//得到基准收益率的方差
        double cov=getVar(strategyYield,benchmarkYield);
        double beta=cov/benchmarkVar;//计算贝塔比率
        double alpha =annualYield-Rf-beta*(benchmarkAnnualYield-Rf);//计算阿尔法比率
        //double excessProfit=(annualYield -benchmarkAnnualYield)/365*offset ;//计算超额收益率
        //double strategyWinRate=(winDays /offset) *100;//计算策略胜率
        if(strategy != null && strategy.size() >= 2) {
            strategy.remove(0);
        }
        StrategyVO svo=new StrategyVO(bench ,strategy,profit ,profitList ,loss ,lossList ,annualYield,benchmarkAnnualYield,alpha ,beta ,sharp ,maxReturn );
long endTime = System.currentTimeMillis();    //获取结束时间
System.out.println("SelectAveStrategy程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        return svo;
    }

    @Override
    public List<Double> forBChartAve(boolean type, long money, LocalDate begin, LocalDate end, int holdTime, int formTime, SelectVO vo) throws RemoteException {

        if(begin .isAfter(end) ){
            return null;
        }
        double winDays=0;//策略获胜的天数
        List<String> pool;//股票池
        /**
         * 确定股票池
         */
        pool=getPool(type,vo,begin ,end);
        /**
         * 重新定位开始日期和结束日期
         */
        begin=resetBegin(begin);
        LocalDate ye=getFormEnd(begin);
        LocalDate oldBegin=begin;
        end=resetEnd(end);
        if(end.isBefore(begin)){
            return null;
        }
        int offset= getRealPeriod(begin,end);
        double benchmarkAnnualYield;
        if(type==true) {
            benchmarkAnnualYield = getBenchmarkAnnualYield(vo.pool, end, begin,offset);//获取基准收益率
        }
        else{
            benchmarkAnnualYield=getBenchmarkAnnualYield(vo.list,offset,begin);
        }
        int circle=offset/holdTime;//得到换股次数
        if(circle*holdTime <offset ){
            circle+=1;//最后一次不满持有期，但仍然需要换股
        }
        LocalDate formStart=getFormStart(begin, formTime);//得到形成期开始时间
        LocalDate formEnd=getFormEnd(begin);//得到形成期结束时间
        double result = money;//最终的钱,初始化为原始资本
        double lastResult = money;//上一周期的钱，初始为原始资本
        List<S> holdedS=new ArrayList<>();//当前持有的股票,初始化为空
        int num = 10;
        /**
         * 开始迭代
         */
        for(int i=0;i<=circle&&begin!=null ;i++){
            LocalDate yest=getFormEnd(begin);
            lastResult = result;
            if(!holdedS.isEmpty() ){//当前已经持有股票
                //result=0;
                for(int j=0;j<holdedS .size() ;j++){
                    S s=holdedS .get(j);
                    //                    StockEntity po=getStockByCodeAndDay(s.getCode(),begin);
                    //                    int tempIndex = 0;
                    //                    while(po == null) {
                    //                        tempIndex ++;
                    //System.out.println("getStockByCodeAndDay空指针,之前第" + tempIndex + "天");
                    //                        po = strategyDao.findStockByCodeAndOffset(s.getCode(),begin, -tempIndex);
                    //                    }
                    //                    s.setEndPrice(po.getAdjClose());//设置持有期结束时的最终价
                    StockEntity po=getStockByCodeAndDay(s.getCode(),yest) ;
                    if(po==null){
                        po=getNearestStock(s.getCode() ,yest);
                    }
                    if(po!=null) {
                        s.setEndPrice(po.getAdjClose());//设置持有期结束时的最终价
                    }
                    else{//getNearestStock方法也无法调整,只能假定最终价与初始价相同
                        s.setEndPrice(s.getStartPrice() );
                    }
                    double profitPerCircle=(s.getEndPrice()-s.getStartPrice())*s.getNumber();
                    result+=profitPerCircle ;//加上这只股票在持有期间的收益
                }
                double p1=(result -lastResult)/lastResult *100;
                int interval=getRealPeriod(oldBegin ,yest );
                double p2;
                if(type==true) {
                    p2 = getBenchmarkAnnualYield(vo.pool, yest, oldBegin ,interval );//获取此持有期基准收益率
                }
                else{
                    p2=getBenchmarkAnnualYield(vo.list,interval ,oldBegin );
                }
                if(p1>p2){
                    winDays ++;
                }
            }
            holdedS=changeStockByAve(formStart ,formEnd ,pool,num,result );//进行换股
            formStart=resetFormStart(formStart, holdTime );//重新定位形成开始时间
            formEnd =resetFormEnd(formEnd , holdTime );//重新定位形成结束时间
            oldBegin =begin;
            begin=reset(begin, holdTime, end );//为开始时间增加一个持有期时间，逐步逼近结束时间
        }
        double annualYield=(result -money )/money *100;//计算年化收益率
        double excessProfit=(annualYield -benchmarkAnnualYield);//计算超额收益率
        //System .out.println(excessProfit);
        double strategyWinRate=(winDays/circle)*100;//计算策略胜率
        List<Double> ld=new ArrayList<>() ;
        ld.add(excessProfit) ;
        ld.add(strategyWinRate) ;
        return ld;
    }

    private List<S> changeStockByAve(LocalDate formStart, LocalDate formEnd, List<String> pool, int num, double money) throws RemoteException {
long startTime = System.currentTimeMillis();    //获取开始时间
        List<S> slist=new ArrayList<S>();//返回的选中股票

        double avemoney = money / num;//买每只股票的钱

        int size = pool.size();

        List<Double> avePriceList = new ArrayList<Double>();//形成期每只股票的均价
        List<Double> endPriceList = new ArrayList<Double>();//每只股票的最后收盘价
        List<Double> deviateList = new ArrayList<Double>();//每只股票的偏离度

        int days = (int) (formEnd.toEpochDay() - formStart .toEpochDay() + 1);//形成期天数

        //计算均价和收盘价
        for(int i=0; i<size; i++) {

            double total = 0;
            int valueDays = 0;
            for(int j=0;j<days;j++) {
                StockEntity spo = strategyDao.findStockByCodeAndOffset(pool.get(i), formStart, j);
                if(spo != null) {
                    total = total + spo.getAdjClose();
                    valueDays ++;
                }

            }
            if(valueDays != 0) {
                double average = total / valueDays;
                avePriceList.add(new Double(average));
            } else {
                avePriceList.add(new Double(0));
            }

            StockEntity po = strategyDao.findStockByCodeAndDay(pool.get(i), formEnd);
            if(po == null) {
                endPriceList.add(new Double(0));
            } else {
                double end = po.getAdjClose();
                endPriceList.add(new Double(end));
            }
        }

        //计算偏离
        for(int i=0 ;i<size;i++) {

            double ave = avePriceList.get(i).doubleValue();
            double end = endPriceList.get(i).doubleValue();

            if((Math.abs(end - 0) > 0.01) && (Math.abs(ave - 0) > 0.01)) {
                double deviate = (ave - end) / ave;
                deviateList.add(new Double(deviate));
            } else {
                deviateList.add(new Double(-10000));
            }

        }

//		//从小到大排序偏离度
//        for(int i=0;i<size-1;i++) {
//        	for(int j=0;j<size-1-i;j++){
//        		double d1 = deviateList.get(j).doubleValue();
//        		double d2 = deviateList.get(j+1).doubleValue();
//                if(d1 > d2){
//                  deviateList.set(j, new Double(d2));
//                  deviateList.set(j+1, new Double(d1));
//                }
//            }
//        }

        for(int i=0;i<num;i++) {

            int index = 0;

            double max = deviateList.get(0).doubleValue();

            for(int j=1;j<size-i;j++) {
                double d = deviateList.get(j).doubleValue();
                if(d > max) {
                    max = d;
                    index = j;
                }
            }

            String code = pool.get(index);
            StockEntity spo = strategyDao .findStockByCodeAndDay(code,formEnd);
            if(spo==null){
                spo=getNearestStock(code ,formEnd);
            }
//			int tempIndex = 0;
//			while(spo == null && tempIndex < days) {
//			    tempIndex++;
//System.out.println("迭代得到最后一天股票价格空指针，第" + tempIndex + "次");
//			    spo = strategyDao.findStockByCodeAndOffset(code,formEnd,-tempIndex);
//            }
            if(spo != null) {
                double startPrice = spo.getAdjClose();
                int number = (int) (avemoney/startPrice);
                slist.add(new S(code,startPrice,number));//添加返回值
            } else {//如果连getNearestStock方法都无法取得数据，说明此股票出现了长时间的停牌

                System.out.println("changeStock时,方法findStockByCodeAndDay空指针");
            }

            deviateList.remove(index);

        }
long endTime = System.currentTimeMillis();    //获取结束时间
System.out.println("changestockave程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        return slist;
    }
}
