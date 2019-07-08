package vo;

import java.util.List;

/**
 * Created by lienming on 2017/6/12.
 */
public class ConceptDayInfo {
    public String name ;

    public List<String> list ;

    public boolean isUp ; //涨 = true ,跌 = false

    public int up_num ; //涨或跌停股票数量

    public int down_num ; //涨或跌超过5%的股票数

}
