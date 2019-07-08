package service;

import vo.CompareTwoStock;

import java.util.Date;

/**
 * Created by lienming on 2017/5/18.
 */
public interface CompareService {
    /** 股票比较 */
    CompareTwoStock getCompareTwoStock(Date beginDate, Date endDate, String stock1, String stock2,
                                       boolean stock1IsName, boolean stock2IsName);
}
