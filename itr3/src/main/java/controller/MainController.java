package controller;

import entity.StockDayEntity;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.KGraphService;
import service.StockService;
import util.StockCalendar;
import vo.KGraph;

import java.util.*;

/**
 * Created by lienming on 2017/5/15.
 * 首页
 */
@Controller
@RequestMapping("/main")
public class MainController {



    @Autowired
    private KGraphService kGraphService ;

    @Autowired
    private StockService stockService ;



    /** 获得首页推荐股票 5支
     * */
    @RequestMapping(value="/getRecommendStock",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getRecommendStock(){
        Map<String,Double> maps =  stockService.getRecommend();
        JSONObject jo = new JSONObject();
        Set<Map.Entry<String,Double>> sets =  maps.entrySet() ;
        List<String> na = new ArrayList<>();
        List<Double> db = new ArrayList<>();
        for(Map.Entry<String,Double> set : sets) {
            na.add(set.getKey());
            db.add(set.getValue());
        }
        jo.put("name",na);
        jo.put("up",db);

        return jo;
    }

    /** 返回从今日起，往前一年时间长度的大盘K线图
     * param "String stock , boolean stockIsName"
     * @return KGraph
     *  */
    @RequestMapping(value="/getMainK",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getMainK(){
        Date today = StockCalendar.getToday() ;

//        Date today = null;
//        try {
//            today = new SimpleDateFormat("yyyy-MM-dd").parse("2016-10-20");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        Date oneYearAgo = StockCalendar.getOneYearAgo() ;
        JSONObject jsonK=new JSONObject();
      //  ArrayList<LocalDate> date=new ArrayList<LocalDate>();
        KGraph Kinfo=kGraphService.getMainK(oneYearAgo,today);
        Map<String,double[]> KMap=Kinfo.getMap();

        jsonK.put("MainK",KMap);

        jsonK.put("MainKNum",KMap.size());
       // System.out.println(Kinfo.getDataAxis().length);
        jsonK.put("MainKDate",Kinfo.getDataAxis());

        return jsonK ;
    }


//    @RequestMapping(value="/getTest",method = RequestMethod.GET)
//    @ResponseBody
//    public String getTest(@RequestParam("canshu")String userName, @RequestParam("canshu2")String password){
//            return "main success";
//    }

    /** 获得今日的大盘股票交易情况
     *  如果没有今日数据，返回前一个交易日
     * @return
     */
    @RequestMapping(value = "/getMainStockToday" , method = RequestMethod.POST )
    @ResponseBody
    public List<StockDayEntity> getMainStockToday() {

        Date today = StockCalendar.getToday() ;

        return stockService.getMainStock(today) ;
    }


}
