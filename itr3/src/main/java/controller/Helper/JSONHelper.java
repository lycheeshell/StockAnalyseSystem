package controller.Helper;

import entity.StrategyEntity;
import net.sf.json.JSONObject;
import util.StrTransformer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lienming on 2017/5/29.
 * 将JSONObject视为映射的集合，转换为Map并返回
 */
public class JSONHelper {

   public static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Map convertToMap(JSONObject object){
        Map result = new HashMap<>();
        Set<Map.Entry> sets = object.entrySet();

        for( Map.Entry set : sets ){
            result.put(set.getKey(),set.getValue());
        }
        return result ;
    }

    /** StrategyEntity  */
    public static StrategyEntity jsonToEntity(JSONObject jsonObject){
        String userName = jsonObject.getString("userName") ;
        String type = jsonObject.getString("type");//"MUM","AVE"

        LocalDate startDate = LocalDate.parse(jsonObject.get("startDate").toString(), DATE_FORMAT);
        LocalDate endDate = LocalDate.parse(jsonObject.get("endDate").toString(), DATE_FORMAT);
        System.out.println("bankuai!: " + jsonObject.get("bankuai"));

        int bankuai = jsonObject.getInt("bankuai"); //"1 2 3 4 "

        int hasST = jsonObject.getInt("hasST");


        String[] userChosenStocksArr = jsonObject.getString("userChosenStocks").split(",");
        String userChosenStocks = StrTransformer.ArrayToStr(userChosenStocksArr) ;

        double money = jsonObject.getDouble("money");
        String code = jsonObject.getString("code") ;

        int holdTime = jsonObject.getInt("holdTime");
        int formTime = jsonObject.getInt("formTime");
        StrategyEntity entity = new StrategyEntity(userName,type,startDate,endDate,bankuai,
                hasST,userChosenStocks,money,code,holdTime,formTime);
        return entity ;
    }

}
