package vo;

import java.util.List;

/**
 * Created by lienming on 2017/5/18.
 */
public class BChartVO {
    public boolean isHoldingPeriod;//是否是持有期，用于判断横轴的变量是持有期还是形成期
    public List<BItem> list;

    public BChartVO(boolean i, List<BItem> l){
        isHoldingPeriod =i;
        list =l;
    }
}
