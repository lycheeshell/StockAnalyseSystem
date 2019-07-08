package entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * Created by lienming on 2017/6/6.
 */
@Entity
@Table( name = "strategy",schema = "",catalog ="")
public class StrategyEntity {


    String userName ;    //保存该策略的用户名
    String type ;  //该策略用来查找的类型（mum/ave  表示  动量/均值 , ac/b  表示  AC图/B图 )， 共有四种 ： MUM ,  AVE
    LocalDate startDate ;  //开始日期
    LocalDate endDate;   //结束日期
    int bankuai;       // 1 - 主板 ,  2 - 中小板  ,  3 - 创业板  ,  4 - 自选股
    int hasST;          //   0 - 不包含ST  ,   1 - 包含ST
    String userChosenStocks = "";   //自选股所构成的字符串，只有当 板块==4 时才不为""，每6位股票代码用英文的逗号隔开，形式如“000001,000002,000003,000004”
    double money;       //投入的金额
    String code ;        //关键字  编号
    int holdTime;      //持有期
    int formTime;      //形成期

    public StrategyEntity(String un, String ty, LocalDate be, LocalDate en, int ba, int st, String cs, double mo, String co, int h, int f) {
        this.userName = un;
        this.type = ty;
        this.startDate = be;
        this.endDate = en;
        this.bankuai = ba;
        this.hasST = st;
        this.userChosenStocks = cs;
        this.money = mo;
        this.code = co;
        this.holdTime = h;
        this.formTime = f;
    }

    //StrategyVO vo ;      //根据输入的参数，计算生成的vo

//    @Id
//    @Column(name = "code")
//    public String getCode() {
//        return this.code;
//    }
//
//    public void setCode(String code) {
//        this.code = code ;
//    }

    @Basic
    @Column(name = "userName" )
    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName ;
    }

    @Basic
    @Column(name = "type" )
    public String getType() {
        if(this.type.equals("MUM")){
            return "动量策略";
        }
        else{
            return "均值回归";
        }
    }

    public void setType(String type) {
        this.type = type;
    }
    @Basic
    @Column(name = "startDate" )
    public String getStartDate() {
        return this.startDate.toString();
    }

    public void setStartDate(LocalDate std) {
        this.startDate = std;
    }
    @Basic
    @Column(name = "endDate" )
    public String getEndDate() {
        return this.endDate.toString();
    }

    public void setEndDate(LocalDate end) {
        this.endDate = end;
    }

    @Basic
    @Column(name = "bankuai" )
    public String getBankuai() {
        if(this.bankuai==1)
            return "主板";
        else if(this.bankuai==2)
            return "中小板";
        else if(this.bankuai==3)
            return "创业板";
        else
            return "自选股";
    }

    public void setBankuai(int bk) {
        this.bankuai = bk;
    }

    @Basic
    @Column(name = "hasST" )
    public int getHasST() {
        return this.hasST;
    }

    public void setHasST(int st) {
        this.hasST = st;
    }

    @Basic
    @Column(name = "userChosenStocks" )
    public String getUserChosenStocks() {
        return this.userChosenStocks;
    }

    public void setUserChosenStocks(String ucs) {
        this.userChosenStocks = ucs;
    }

    @Basic
    @Column(name = "money" )
    public double getMoney() {
        return this.money;
    }

    public void setmoney(double mo) {
        this.money = mo;
    }

    @Basic
    @Column(name = "code" )
    public String getCode() {
        return this.code;
    }

    public void setCode(String co) {
        this.code = co;
    }

    @Basic
    @Column(name = "holdTime" )
    public int getHoldTime() {
        return this.holdTime;
    }

    public void setHoldTime(int h) {
        this.holdTime = h;
    }

    @Basic
    @Column(name = "formTime" )
    public int getFormTime() {
        return this.formTime;
    }

    public void setFormTime(int f) {
        this.formTime = f;
    }


    public String getRealType(){
        return this.type;
    }

    public void setRealType(String type){
        this.type=type ;
    }

    public int getRealBankuai() {
        return this.bankuai;
    }

    public void setRealBankuai(int b){
        this.bankuai=b;
    }

    public LocalDate getRealStartDate() {
        return this.startDate;
    }

    public LocalDate getRealEndDate() {
        return this.endDate;
    }
//    @Basic
//    @Column(name = "param" )
//    public String getParam() {
//        return this.param;
//    }
//
//    public void setParam(String param) {
//        this.param = param;
//    }
//
//
//    @Basic
//    @Column(name = "vo" )
//    public StrategyVO getStrategyVO() {
//        return this.vo;
//    }
//
//    public void setStrategyVO(StrategyVO vo) {
//        this.vo = vo;
//    }





}
