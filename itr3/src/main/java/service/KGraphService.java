package service;

import vo.KGraph;

import java.util.Date;

/**
 * Created by lienming on 2017/5/18.
 */
public interface KGraphService {

    KGraph getKGraph(Date beginDate, Date endDate, String stock, boolean flag);

    /***
     * 获取大盘k线
     * @param oneYearAgo
     * @param today
     * @return
     */
    KGraph getMainK(Date oneYearAgo , Date today ) ;
}
