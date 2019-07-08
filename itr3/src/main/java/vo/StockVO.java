package vo;

import java.util.Date;

/**
 * Created by lienming on 2017/6/3.
 */
public class StockVO {

    public String name ;        //股票名称
    public String code ;        //代码
    public String company ;     //所属公司
    public String area ;        //所属地区
    public Date dateOnBoard ;   //上市日期
    public String mainBusiness ;//主营业务
    public double point ;       //得分
    public int focus ;          //关注度

    public StockVO(String name,String code,String company,String area,
                   Date dateOnBoard,String mainBusiness,double point,int focus){
        this.name=name;
        this.code=code;
        this.company=company;
        this.area=area;
        this.dateOnBoard=dateOnBoard;
        this.mainBusiness=mainBusiness;
        this.point=point;
        this.focus=focus;
    }



}
