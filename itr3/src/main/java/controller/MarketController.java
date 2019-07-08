package controller;

import entity.StockDayEntity;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.MarketService;
import service.StockService;
import util.SDF;
import util.StockCalendar;
import vo.ConceptDayInfo;
import vo.DayInfo;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lienming on 2017/6/3.
 */
@Controller
@RequestMapping("/marketTemper")
public class MarketController {
    @Autowired
    private MarketService marketService ;
    @Autowired
    private StockService stockService ;

    /** 获得指定日期 大盘 市场温度计
     * 得到当天涨停跌停股的个数，看迭代一
     * 如果总交易量是0 说明当日不是交易日
     * */
    @RequestMapping(value = "/getMarketTemper" , method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getDayInfo( ) throws RemoteException {

        LocalDate date = StockCalendar.DateToLocalDate(StockCalendar.getToday()) ;

        DayInfo di = marketService.getDayInfo(date,true) ;

        JSONObject jo = new JSONObject() ;

        jo.put("limitUp",(int)di.limitUp);
        jo.put("limitDown",(int)di.limitDown);
        jo.put("notBad",(int)di.notBad);
        jo.put("notGood",(int)di.notGood);
        jo.put("totalVolume",di.totalVolume);

        return jo ;
    }

    /***
     * 得到当天各个概念板块的涨停跌停股的个数，板块数量=20
     */
    @RequestMapping(value = "/getPlateMarketTemper" , method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getPlateDayInfo( ) throws RemoteException {

        LocalDate date = StockCalendar.DateToLocalDate(StockCalendar.getToday());

        List<ConceptDayInfo> list = marketService.getPlateDayInfo(date,true) ;

        List<String> name = new ArrayList<>();
        List<Boolean> isUp = new ArrayList<>();
        List<Integer> up = new ArrayList<>();
        List<Integer> down = new ArrayList<>();

        for(ConceptDayInfo cdi : list) {
            name.add(cdi.name);
            isUp.add(cdi.isUp);
            up.add(cdi.up_num);
           down.add(cdi.down_num);
        }

        JSONObject jo = new JSONObject();

        jo.put("name",name);
        jo.put("isUp",isUp);
        jo.put("upNum",up);
        jo.put("downNum",down);

        return jo ;
    }

    /**指定日期
     *
     */
    @RequestMapping(value = "/findMarketTemper" , method = RequestMethod.GET)
    @ResponseBody
    public JSONObject findDayInfo (String datest) throws RemoteException, ParseException {

        LocalDate date = SDF.dateToLocalDate(SDF.parse(datest));
        DayInfo di = marketService.getDayInfo(date,true) ;

        JSONObject jo = new JSONObject() ;

        jo.put("limitUp",(int)di.limitUp);
        jo.put("limitDown",(int)di.limitDown);
        jo.put("notBad",(int)di.notBad);
        jo.put("notGood",(int)di.notGood);
        jo.put("totalVolume",di.totalVolume);

        return jo ;
    }

    /***
     * 指定日期 概念板块
     */
    @RequestMapping(value = "/findPlateMarketTemper" , method = RequestMethod.GET)
    @ResponseBody
    public JSONObject findPlateDayInfo(String datest) throws RemoteException, ParseException {

        LocalDate date = SDF.dateToLocalDate(SDF.parse(datest));

        List<ConceptDayInfo> list = marketService.getPlateDayInfo(date,true) ;

        List<String> name = new ArrayList<>();
        List<Boolean> isUp = new ArrayList<>();
        List<Integer> up = new ArrayList<>();
        List<Integer> down = new ArrayList<>();

        for(ConceptDayInfo cdi : list) {
            name.add(cdi.name);
            isUp.add(cdi.isUp);
            up.add(cdi.up_num);
            down.add(cdi.down_num);
        }

        JSONObject jo = new JSONObject();

        jo.put("name",name);
        jo.put("isUp",isUp);
        jo.put("upNum",up);
        jo.put("downNum",down);

        return jo ;
    }

    @RequestMapping(value = "/getSDE_fromConceptBench" , method = RequestMethod.POST)
    @ResponseBody
    public List<StockDayEntity> getSDE_forConceptBench (String conceptName) {

        return stockService.getSDE_forConceptBench(conceptName);
    }

    @RequestMapping(value = "/findSDE_fromConceptBench" , method = RequestMethod.POST)
    @ResponseBody
    public List<StockDayEntity> findSDE_forConceptBench (String conceptName,String datestr) {
        LocalDate date = null;
        try {
            date = SDF.dateToLocalDate(SDF.parse(datestr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return stockService.findSDE_forConceptBench(conceptName,date);
    }

}
