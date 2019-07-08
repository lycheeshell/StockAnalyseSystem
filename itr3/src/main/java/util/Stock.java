package util;

import java.util.HashMap;

public class Stock {
	private String name;
	private String code;
	private double pchange;
	private boolean label;//是否盈利
	private HashMap<String,String> attributeList;//属性列表
	public Stock(String n,String c,double p,boolean l,HashMap<String,String> a){
		name=n;
		code=c;
		pchange=p;
		label=l;
		attributeList=a;
	}

	public String getCode(){return code;}

	public String getName(){return name;}

	public double getPchange(){return pchange;}

	public boolean getLabel(){
		return label;
	}
	/**
	 * 得到属性列表，含有涨跌幅，换手率，成交量，流通市值比，主营业务收入增长率
	 * 净资产收益率，每股现金流，净利润增长率，市盈率mrq(扣除非经常性损益)等等
	 * 需要注意，此处的属性不是本来的数值，而是经排序分类后的类值
	 * 比如<涨跌幅，非常好>则意味着这个股票在过去n天内的涨幅排入了
	 * 所有股票的前20%
	 * 比如<换手率，差>则意味着这个股票的换手率排入了所有股票的20%~40%
	 * 比如<市盈率mrq，一般>则意味着这个股票的市盈率mrq排入了所有股票的40%~60%
	 * 所以这个stock类不能简单的看作是一条记录，而是对其过往信息抽象化的股票类
	 * @return
	 */
	public HashMap<String,String> getAttributeList(){
		return attributeList;
	}
}
