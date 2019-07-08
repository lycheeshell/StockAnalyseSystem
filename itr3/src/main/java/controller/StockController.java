package controller;

import entity.StockDetailEntity;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.CompareService;
import service.KGraphService;
import service.StockService;
import util.SDF;
import util.StockCalendar;
import vo.CompareTwoStock;
import vo.KGraph;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by lienming on 2017/6/3.
 */

@Controller
@RequestMapping("/oneStock")
public class StockController {

    @Autowired
    private KGraphService kGraphService ;

    @Autowired
    private StockService stockService ;

    @Autowired
    private CompareService compareService ;

    /** 此股票一年内K线的数据 */
    @RequestMapping(value = "/getKInfo", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getKInfo(String code) {
        Date today = StockCalendar.getToday() ;
        Date oneYearAgo = StockCalendar.getOneYearAgo() ;
        JSONObject jsonK=new JSONObject();
        //  ArrayList<LocalDate> date=new ArrayList<LocalDate>();
        KGraph Kinfo=kGraphService.getKGraph(oneYearAgo,today,code,false);
        Map<String,double[]> KMap=Kinfo.getMap();

        jsonK.put("MainK",KMap);

        jsonK.put("MainKNum",KMap.size());
        // System.out.println(Kinfo.getDataAxis().length);
        jsonK.put("MainKDate",Kinfo.getDataAxis());
        return jsonK;
    }

    /** 获得个人关注的股票列表 */
    @RequestMapping(value = "/getPersonFocus",method = RequestMethod.GET)
    @ResponseBody
    public List<StockDetailEntity> getPersonFocus(String userName){
        return stockService.getPersonFocus(userName);
    }

    /** 获得个人关注的股票列表为个股信息界面特制 */
    @RequestMapping(value = "/getPersonFocusForOne",method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getPersonFocusForOne(String userName){
        JSONObject jo=new JSONObject();
        List<StockDetailEntity> list=getPersonFocus(userName);

        List<String> codelist=new ArrayList<>();

        for(int i=0;i<list.size();i++)
        {
            codelist.add(list.get(i).getCode());
        }
        jo.put("codelist",codelist);
        return jo;
    }

    /** 增加股票到个人关注列表，其实一次就添加一支 */
    @RequestMapping(value = "/addFocusStock", method = RequestMethod.POST)
    @ResponseBody
    public boolean addFocusStock(@RequestBody JSONObject jsonObject){

        List<String> stockCodes = (List<String>) jsonObject.get("stockCodes") ;
        String userName = jsonObject.getString("userName");

        return stockService.addFocusStock(stockCodes,userName);
    }

    /** 删除个人关注列表中的股票 */
    @RequestMapping(value = "/deleteFocusStock",method = RequestMethod.POST)
    @ResponseBody
    public boolean deleteFocusStock(@RequestBody JSONObject jsonObject){

        List<String> stockCodes = (List<String>) jsonObject.get("stockCodes") ;
        String userName = jsonObject.getString("userName");

        return stockService.deleteFocusStock(stockCodes,userName);
    }

    /** 获得所有股票的详细信息
     *  */
    @RequestMapping(value = "/getAllStock", method = RequestMethod.GET)
    @ResponseBody
    public List<StockDetailEntity> getAllStock() {//得到全部股票
        return stockService.getAllStock();
    }

    /***获得 某个板块 所有股票的详细信息
     *
     */
    @RequestMapping(value = "/getPlateAllStock", method = RequestMethod.GET)
    @ResponseBody
    public List<StockDetailEntity> getPoolStock(String pool) {//得到全部股票
        return stockService.getPoolStock(pool);
    }


    /** 得到单支股票的详细信息 */
    @RequestMapping(value = "/getStockInfo", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getStockInfo(String code) {

        JSONObject jo=new JSONObject();
        StockDetailEntity stockDetail=stockService.getStockInfo(code);

        jo.put("name",stockDetail.getName());
        jo.put("area",stockDetail.getArea());
        jo.put("asset",stockDetail.getAsset());
        jo.put("business",stockDetail.getBusiness());
        jo.put("date",stockDetail.getDateOnMarket());
        return jo;
    }

    /** 获得单支股票的雷达图数据 */
    @RequestMapping(value = "/getStockRada", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getStockRada(String code) {
        //System.out.println("Rada Code is"+code);
        double[] db =  stockService.getStockRada(code);
        JSONObject jsonObject = new JSONObject() ;
        jsonObject.put("changerate",db[0]);
        jsonObject.put("pb",db[1]);
        jsonObject.put("profit",db[2]);
        jsonObject.put("undp",db[3]);
        jsonObject.put("mrq",db[4]);
        jsonObject.put("totals",db[5]);
        jsonObject.put("changerate_max",db[6]);
        //System.out.println("db is"+db[6]);
        jsonObject.put("pb_max",db[7]);
        jsonObject.put("profit_max",db[8]);
        jsonObject.put("undp_max",db[9]);
        jsonObject.put("mrq_max",db[10]);
        jsonObject.put("totals_max",db[11]);
        return jsonObject;
    }

    /** 股票评分*/
    @RequestMapping(value = "/value" , method = RequestMethod.GET )
    @ResponseBody
    public double getValue( String code ) {
        return stockService.getStockValue(code) ;
    }

    /** 用户自己指定的评分标准 */
    @RequestMapping(value = "/selfvalue" , method = RequestMethod.POST)
    @ResponseBody
    public double getSelfValue(@RequestBody JSONObject jsonObject) {


        return 0 ;
    }



    /** 迭代一 两只股票比较 */
    @RequestMapping(value = "/compareTwoStock", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getCompare(@RequestBody JSONObject jsonObject){
        Date beginDate = null;
        Date endDate=null;
        try {
            beginDate = SDF.parse(jsonObject.get("beginDate").toString());
            endDate   = SDF.parse(jsonObject.get("endDate").toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String stock1 = jsonObject.getString("stock1") ;
        String stock2 = jsonObject.getString("stock2") ;

        boolean stock1IsName = jsonObject.getBoolean("stock1IsName");
        boolean stock2IsName = jsonObject.getBoolean("stock2IsName");


        CompareTwoStock cts = compareService.getCompareTwoStock(beginDate,endDate,stock1,stock2, stock1IsName,stock2IsName);

        if(cts==null){
            return null;
        }
        double s1low = cts.getLow1() ;
        double s2low = cts.getLow2() ;
        double s1high= cts.getHigh1();
        double s2high= cts.getHigh2();
        double s1change=cts.getRange1();
        double s2change=cts.getRange2();
        double s1cov = cts.getVariance1();
        double s2cov = cts.getVariance2();


        ArrayList<Double> s1 = cts.getSettlement1();  //第一支股票每天的收盘价
        ArrayList<Double> s2 = cts.getSettlement2();   //第二支股票每天的收盘价
        ArrayList<Double> lo1=cts.getLogarithmic1();  //第一支股票每天的对数收益率
        ArrayList<Double> lo2=cts.getLogarithmic2();  //第二支股票每天的对数收益率
        ArrayList<LocalDate> date =cts.getDate() ; //交易的日期
        System.out.println(s1.size()+"  "+s2.size()+"  "+lo1.size()+"  "+lo2.size()+"  "+date.size());
        String p1s1="";
        String p1s2="";
        String p2s1="";
        String p2s2="";
        String p3s1="";
        String p3s2="";
        String p3s1max="";
        String p3s2max="";

        if(stock1IsName){
            stock1=stockService.getTransferCode(stock1);
        }

        if(stock2IsName){
            stock2=stockService.getTransferCode(stock2);
        }

        double[] db1=stockService.getStockRada(stock1);
        double[] db2=stockService.getStockRada(stock2);

        for(int i=0;i<6;i++){
            if(i==0){
                p3s1+="["+db1[i]+",";
                p3s2+="["+db2[i]+",";
            }
            else if(i==5){
                p3s1+=db1[i]+"]";
                p3s2+=db2[i]+"]";
            }
            else{
                p3s1+=db1[i]+",";
                p3s2+=db2[i]+",";
            }
        }

        for(int i=6;i<12;i++){
            if(i==6){
                p3s1max+="["+db1[i]+",";
                p3s2max+="["+db2[i]+",";
            }
            else if(i==11){
                p3s1max+=db1[i]+"]";
                p3s2max+=db2[i]+"]";
            }
            else{
                p3s1max+=db1[i]+",";
                p3s2max+=db2[i]+",";
            }
        }
        if(!p3s1max.equals(p3s2max)){
            p3s1max="shit";
        }
        DecimalFormat df = new DecimalFormat("0.00");
        for(int i=0;i<date.size();i++){
            if(i==date.size()-1){
                p1s1+=date.get(i)+","+df.format(s1.get(i));
                p1s2+=date.get(i)+","+df.format(s2.get(i));
                p2s1+=date.get(i)+","+df.format(lo1.get(i));
                p2s2+=date.get(i)+","+df.format(lo2.get(i));
            }
            else{
                p1s1+=date.get(i)+","+df.format(s1.get(i))+",";
                p1s2+=date.get(i)+","+df.format(s2.get(i))+",";
                p2s1+=date.get(i)+","+df.format(lo1.get(i))+",";
                p2s2+=date.get(i)+","+df.format(lo2.get(i))+",";
            }
        }

        JSONObject o = new JSONObject() ;
        o.put("s1low",df.format(s1low)) ;
        o.put("s2low",df.format(s2low)) ;
        o.put("s1high",df.format(s1high));
        o.put("s2high",df.format(s2high));
        o.put("s1change",df.format(s1change)) ;
        o.put("s2change",df.format(s2change)) ;
        System.out.println(s1cov);
        o.put("s1cov",s1cov) ;
        o.put("s2cov",s2cov) ;
        o.put("p1s1",p1s1);
        o.put("p1s2",p1s2);
        o.put("p2s1",p2s1);
        o.put("p2s2",p2s2);
        o.put("p3s1",p3s1);
        o.put("p3s2",p3s2);
        o.put("p3s1max",p3s1max);
        o.put("p3s2max",p3s2max);
        return o;
    }


}
