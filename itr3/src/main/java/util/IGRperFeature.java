package util;

import java.util.List;

/**
 * 每个特征的名字和信息增量比
 * @author LZ
 *
 */
public class IGRperFeature {
	private String feature;//特征名
	private double igr;//信息增量比，都初始化为0
	private List<String> value;//所有股票的该特征的抽象值
	//比如[非常好](前20%),[好],[一般],[差],[非常差](最后20%)
	public IGRperFeature(String f,double i,List<String> v){
		feature=f;
		igr=i;
		value=v;
	}
	
	public String getFeature(){
		return feature;
	}
	
	public double getIGR(){
		return igr;
	}
	
	public void setIGR(double d){
		igr=d;
	}
	
	public List<String> getValue(){
		return value;
	}
}
