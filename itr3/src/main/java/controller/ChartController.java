package controller;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.ApplyStrategy;
import service.StrategyForBChart;
import util.SDF;
import vo.BChartVO;
import vo.DailyYield;
import vo.SelectVO;
import vo.StrategyVO;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lienming on 2017/6/4.
 * 迭代二内容：生成A、B、C图
 */
@Controller
@RequestMapping("/chart")
public class ChartController {

    @Autowired
    private ApplyStrategy applyStrategy;

    @Autowired
    private StrategyForBChart strategyForBChart;

    /***
     * 接收的是一个StrategyEntity
     * @param jsonObject
     * @return
     * @throws RemoteException
     */
    @RequestMapping(value = "/mum_ac" , method = RequestMethod.POST)
    @ResponseBody
    public JSONObject get_mum_ac(@RequestBody JSONObject jsonObject) throws RemoteException {

        boolean type = jsonObject.getBoolean("type") ;
        long money = jsonObject.getLong("money") ;

        LocalDate begin = SDF.fromJSON(jsonObject,"begin");
        LocalDate end   = SDF.fromJSON(jsonObject,"end");

        int holdTime = jsonObject.getInt("holdTime") ;
        int formTime = jsonObject.getInt("formTime") ;

        JSONObject o = (JSONObject) jsonObject.get("vo") ;
        String pool = o.getString("pool");
        boolean hasST = o.getBoolean("hasST");
        List<String> list = (List<String>)o.get("list") ;
        SelectVO vo = new SelectVO(pool,hasST,list) ;
        StrategyVO svo=applyStrategy.SelectMomStrategy(type,money,begin,end,holdTime,formTime,vo);
        List<DailyYield> b=svo.benchmarkYieldList;
        List<DailyYield> d=svo.dailyYieldList;
        String s1="";
        String s2="";
        for(int i=0;i<b.size();i++){
            DailyYield dy=b.get(i);
            if(i==b.size()-1){
                s1+=dy.getDate()+","+dy.getDailyYield();
            }
            else{
                s1+=dy.getDate()+","+dy.getDailyYield()+",";
            }
        }
        for(int i=0;i<d.size();i++){
            DailyYield dy=d.get(i);
            if(i==d.size()-1){
                s2+=dy.getDate()+","+dy.getDailyYield();
            }
            else{
                s2+=dy.getDate()+","+dy.getDailyYield()+",";
            }
        }
        JSONObject jojo = new JSONObject() ;
        jojo.put("annualYield",svo.annualYield);
        jojo.put("benchmarkAnnualYield",svo.benchmarkAnnualYield);
        jojo.put("alpha",svo.alpha);
        jojo.put("beta",svo.beta);
        jojo.put("maxDrawdown",svo.maxDrawdown);
        jojo.put("loss",svo.loss);
        jojo.put("profit",svo.profit);
        jojo.put("profitList",svo.profitList);
        jojo.put("lossList",svo.lossList);
        jojo.put("sharp",svo.sharp);
        jojo.put("benchmarkYieldList",s1);
        jojo.put("dailyYieldList",s2);
        return jojo;
    }

    @RequestMapping(value = "/mum_b" , method = RequestMethod.POST)
    @ResponseBody
    public JSONObject get_mum_b(@RequestBody JSONObject jsonObject) throws RemoteException {

        boolean type = jsonObject.getBoolean("type") ;

        LocalDate begin = SDF.fromJSON(jsonObject,"begin");
        LocalDate end   = SDF.fromJSON(jsonObject,"end");

        boolean isHoldingPeriod = jsonObject.getBoolean("isHoldingPeriod");
        int time = jsonObject.getInt("time") ;

        JSONObject o = (JSONObject) jsonObject.get("vo") ;
        String pool = o.getString("pool");
        boolean hasST = o.getBoolean("hasST");
        List<String> list = (List<String>)o.get("list") ;
        SelectVO vo = new SelectVO(pool,hasST,list) ;

        BChartVO bvo= strategyForBChart.SelectMomStrategy(type,begin,end,isHoldingPeriod, time,vo);
        JSONObject jojo = new JSONObject() ;

        jojo.put("BItemList",bvo.list);
        jojo.put("isHoldingPeriod",bvo.isHoldingPeriod);

        List<Integer> circleList=new ArrayList<>();
        for(int i=0;i<bvo.list.size();i++){
            circleList.add(bvo.list.get(i).getCircle());
        }
        List<Double> p1=new ArrayList<>();
        for(int i=0;i<bvo.list.size();i++){
            p1.add(bvo.list.get(i).getExcessEarnings());
        }
        List<Double> p2=new ArrayList<>();
        for(int i=0;i<bvo.list.size();i++){
            p2.add(bvo.list.get(i).getAnnualWinRate());
        }
        jojo.put("circleList",circleList);
        jojo.put("p1",p1);
        jojo.put("p2",p2);
        return jojo;
    }

    @RequestMapping(value = "/ave_ac" , method = RequestMethod.POST)
    @ResponseBody
    public JSONObject get_ave_ac(@RequestBody  JSONObject jsonObject ) throws RemoteException {

        boolean type = jsonObject.getBoolean("type") ;
        long money = jsonObject.getLong("money") ;

        LocalDate begin = SDF.fromJSON(jsonObject,"begin");
        LocalDate end   = SDF.fromJSON(jsonObject,"end");

        int holdTime = jsonObject.getInt("holdTime") ;
        int formTime = jsonObject.getInt("formTime") ;

        JSONObject o = (JSONObject) jsonObject.get("vo") ;
        String pool = o.getString("pool");
        boolean hasST = o.getBoolean("hasST");
        List<String> list = (List<String>)o.get("list") ;
        SelectVO vo = new SelectVO(pool,hasST,list) ;
        StrategyVO svo=applyStrategy.SelectAveStrategy(type,money,begin,end,holdTime,formTime,vo);
        List<DailyYield> b=svo.benchmarkYieldList;
        List<DailyYield> d=svo.dailyYieldList;
        String s1="";
        String s2="";
        for(int i=0;i<b.size();i++){
            DailyYield dy=b.get(i);
            if(i==b.size()-1){
                s1+=dy.getDate()+","+dy.getDailyYield();
            }
            else{
                s1+=dy.getDate()+","+dy.getDailyYield()+",";
            }
        }
        for(int i=0;i<d.size();i++){
            DailyYield dy=d.get(i);
            if(i==d.size()-1){
                s2+=dy.getDate()+","+dy.getDailyYield();
            }
            else{
                s2+=dy.getDate()+","+dy.getDailyYield()+",";
            }
        }
        JSONObject jojo = new JSONObject() ;
        jojo.put("annualYield",svo.annualYield);
        jojo.put("benchmarkAnnualYield",svo.benchmarkAnnualYield);
        jojo.put("alpha",svo.alpha);
        jojo.put("beta",svo.beta);
        jojo.put("maxDrawdown",svo.maxDrawdown);
        jojo.put("loss",svo.loss);
        jojo.put("profit",svo.profit);
        jojo.put("profitList",svo.profitList);
        jojo.put("lossList",svo.lossList);
        jojo.put("sharp",svo.sharp);
        jojo.put("benchmarkYieldList",s1);
        jojo.put("dailyYieldList",s2);
        return jojo;
    }

    @RequestMapping(value = "/ave_b" , method = RequestMethod.POST)
    @ResponseBody
    public JSONObject get_ave_b(@RequestBody  JSONObject jsonObject ) throws RemoteException {

        boolean type = jsonObject.getBoolean("type") ;

        LocalDate begin = SDF.fromJSON(jsonObject,"begin");
        LocalDate end   = SDF.fromJSON(jsonObject,"end");

        int time = jsonObject.getInt("time") ;

        JSONObject o = (JSONObject) jsonObject.get("vo") ;
        String pool = o.getString("pool");
        boolean hasST = o.getBoolean("hasST");
        List<String> list = (List<String>)o.get("list") ;
        SelectVO vo = new SelectVO(pool,hasST,list) ;

        BChartVO bvo= strategyForBChart.SelectAveStrategy(type,begin,end,time,vo);
        JSONObject jojo = new JSONObject() ;

        jojo.put("BItemList",bvo.list);
        jojo.put("isHoldingPeriod",bvo.isHoldingPeriod);

        List<Integer> circleList=new ArrayList<>();
        for(int i=0;i<bvo.list.size();i++){
            circleList.add(bvo.list.get(i).getCircle());
        }
        List<Double> p1=new ArrayList<>();
        for(int i=0;i<bvo.list.size();i++){
            p1.add(bvo.list.get(i).getExcessEarnings());
        }
        List<Double> p2=new ArrayList<>();
        for(int i=0;i<bvo.list.size();i++){
            p2.add(bvo.list.get(i).getAnnualWinRate());
        }
        jojo.put("circleList",circleList);
        jojo.put("p1",p1);
        jojo.put("p2",p2);
        return jojo;
    }
}
