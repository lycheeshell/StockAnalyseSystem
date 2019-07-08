package vo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lienming on 2017/5/18.
 */
public class SelectVO implements Serializable {
    public String pool;//股票池范围,"M"表示主板,"S"表示中小版,"G"表示创业版
    public boolean hasST;//是否含ST股票
    public List<String> list;//自选股票池,不选则即股票代码列表,初始化为null

    public SelectVO(String p,boolean h, List<String> l){
        pool =p;
        hasST=h;
        list=l;
    }
}
