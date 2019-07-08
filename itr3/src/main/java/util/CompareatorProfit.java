package util;

import java.util.Comparator;

/**
 * Created by LZ on 2017/4/2.
 * 辅助类,用来对收益进行降序排列
 */
public class CompareatorProfit implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        T t1=(T) o1;
        T t2=(T) o2;
        if(t1.getProfit()<t2.getProfit()){
            return 1;
        }
        else if(t1.getProfit()>t2.getProfit()){
            return -1;
        }
        else{
            return 0;
        }
    }

}
