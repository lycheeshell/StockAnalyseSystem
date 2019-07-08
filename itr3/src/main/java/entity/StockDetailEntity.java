package entity;

import javax.persistence.*;

/**
 * Created by lienming on 2017/6/6.
 * 一支股票的名称、代码、所属公司、所属地区、上市日期、主营业务、得分、关注度
 */
@Entity
@Table(name = "stockDetail",schema = "",catalog ="")
public class StockDetailEntity {

    String code ;
    String name ;
    String area ;
    String dateOnMarket ;
    String business ;
    double asset ;
    int    focusHeat ;

    public StockDetailEntity(String co,String na, String ar, String da, String bu, double as, int fo) {
        this.code = co;
        this.name = na;
        this.area = ar;
        this.dateOnMarket = da;
        this.business = bu;
        this.asset = as;
        this.focusHeat=fo;
    }

    @Id
    @Column(name = "code")
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "name")
    public void setName(String name){
        this.name = name ;
    }

    public String getName(){
        return this.name ;
    }


    @Basic
    @Column(name = "area")
    public void setArea(String area){
        this.area = area ;
    }

    public String getArea(){
        return this.area ;
    }

    @Basic
    @Column(name = "dateOnMarket")
    public void setDateOnMarket(String date){
        this.dateOnMarket = date ;
    }

    public String getDateOnMarket(){
        return this.dateOnMarket ;
    }

    @Basic
    @Column(name = "business")
    public void setBusiness(String business){
        this.business = business ;
    }

    public String getBusiness(){
        return this.business ;
    }

    @Basic
    @Column(name = "asset")
    public void setAsset(double asset){
        this.asset = asset ;
    }

    public double getAsset(){
        return this.asset;
    }

//    @Basic
//    @Column(name = "point")
//    public void setPoint(String point){
//        this.point = point ;
//    }
//
//    public String getPoint(){
//        return this.point;
//    }

    @Basic
    @Column(name = "focusHeat")
    public void setFocusHeat(int f){
        this.focusHeat=f;
    }

    public int getFocusHeat(){
        return this.focusHeat;
    }


}
