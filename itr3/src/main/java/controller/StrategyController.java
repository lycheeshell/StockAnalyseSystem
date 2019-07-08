package controller;

import controller.Helper.JSONHelper;
import entity.StrategyEntity;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.ApplyStrategy;
import service.StrategyService;
import vo.DailyYield;
import vo.SelectVO;
import vo.StrategyVO;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lienming on 2017/6/3.
 */
@Controller
@RequestMapping("/strategy")
public class StrategyController {

    @Autowired
    private StrategyService strategyService ;

    @Autowired
    private ApplyStrategy applyStrategy;

    /** 根据策略代码查看个人的策略参数
     */
    @RequestMapping(value = "/viewStrategy",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject viewStrategy(@RequestBody JSONObject jsonObject) throws RemoteException {
        String userName = jsonObject.getString("userName");
        String code = jsonObject.getString("strategyCode");
        StrategyEntity se= strategyService.viewStrategy( userName ,code );
        JSONObject jojo=new JSONObject();
        String pool="";
        if(se.getBankuai().equals("主板")){
            pool="M";
        }
        else if(se.getBankuai().equals("中小板")){
            pool="S";
        }
        else if(se.getBankuai().equals("创业板")){
            pool="G";
        }
        jojo.put("pool",pool);
        jojo.put("hasST",se.getHasST());
        jojo.put("list",se.getUserChosenStocks());
        if(se.getUserChosenStocks()==null||!se.getUserChosenStocks().contains(",")){
            jojo.put("list","");
        }
        else{
            jojo.put("list",se.getUserChosenStocks());
        }
        jojo.put("begin",se.getStartDate());
        jojo.put("end",se.getEndDate());
        jojo.put("holdTime",se.getHoldTime());
        jojo.put("formTime",se.getFormTime());
        String type=se.getType();
        jojo.put("strategyName",type);
        StrategyVO svo;
        boolean t;
        if(pool.equals("")){
            t=false;
        }
        else{
            t=true;
        }
        long money=1000000;
        LocalDate b=se.getRealStartDate();
        LocalDate e=se.getRealEndDate();
        boolean hasST;
        if(se.getHasST()==1){
            hasST=true;
        }
        else{
            hasST=false;
        }
        List<String> l=new ArrayList<>();
        if(se.getUserChosenStocks()!=null) {
            String[] array = se.getUserChosenStocks().split(",");
            for (int i = 0; i < array.length; i++) {
                l.add(array[i]);
            }
        }
        SelectVO vo=new SelectVO(pool,hasST,l);
        if(type.contains("动量策略")){
            svo=applyStrategy.SelectMomStrategy(t,money,b,e,se.getHoldTime(),se.getFormTime(),vo);
        }
        else{
            svo=applyStrategy.SelectAveStrategy(t,money,b,e,se.getHoldTime(),se.getFormTime(),vo);
        }
        List<DailyYield> bb=svo.benchmarkYieldList;
        List<DailyYield> d=svo.dailyYieldList;
        String s1="";
        String s2="";
        for(int i=0;i<bb.size();i++){
            DailyYield dy=bb.get(i);
            if(i==bb.size()-1){
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
        /*
        data={
                    "pool":"M",
                    "hasST":1,
                    "list":"",
                    "begin":"2011-11-13",
                    "end":"2011-12-05",
                    "holdTime":5,
                    "formTime":7,
                    "strategyName":"动量策略",
                    "annualYield":43.2,
                    "benchmarkAnnualYield" :22.3,
                    "alpha":3.2,
                    "beta":2.5,
                    "sharp":8.7,
                    "maxDrawdown":54.3,
                    "benchmarkYieldList":[
                    [Date.UTC(1970, 9, 27), 0],
                    [Date.UTC(1970, 10, 10), 0.6],
                    [Date.UTC(1970, 11, 2), -0.8]
                ],
                    "dailyYieldList":[
                    [Date.UTC(1970, 9, 18), 0],
                    [Date.UTC(1970, 9, 26), 0.2],
                    [Date.UTC(1970, 11, 25), -1.38]
                ],
                "profitList":[1,3,5,7,9],
                "lossList":[-2,-4,-6,-8,-10]
            };
         */
    }

    /** 保存个人策略，
     *  JSONObject里面保存的是板块信息、开始/结束日期、持有期、形成期、是否包含ST股、金额 这些生成StrategyVO的信息
     *  @return boolean
     *  */
    @RequestMapping(value = "/saveStrategy" , method = RequestMethod.POST)
    @ResponseBody
    public boolean saveStrategy(@RequestBody JSONObject jsonObject) throws RemoteException {
        return strategyService.saveStrategy( JSONHelper.jsonToEntity(jsonObject) );
    }

    /** 获得个人的历史策略列表 */
    @RequestMapping(value = "/getStrategyHistory",method = RequestMethod.GET)
    @ResponseBody
    public List<StrategyEntity> getStrategyHistory(String userName) throws RemoteException {
        return strategyService.getStrategyHistory(userName);
    }

    /***
     * 获得个人策略个数
     */
    @RequestMapping(value = "/getHistoryNum",method = RequestMethod.POST)
    @ResponseBody
    public int getHistoryNum(String userName) {
        return strategyService.getHistoryNum(userName);
    }

    /** 删除个人历史策略 */
    @RequestMapping(value = "/deleteHistoryStrategies",method = RequestMethod.POST)
    @ResponseBody
    public boolean deleteHistoryStrategies(@RequestBody JSONObject jsonObject) throws RemoteException {
        List<StrategyEntity> entities = (List<StrategyEntity>) jsonObject.get("list");
        return strategyService.deleteHistoryStrategies(entities);
    }



}

