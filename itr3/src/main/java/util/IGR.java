package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IGR {
	private int numberOfKeyChar;
	private List<Stock> data;
	public IGR(int a,List<Stock> d){
		numberOfKeyChar=a;
		data=d;
	}

	/**
	 * 按优先级返回最关键的n个特征
	 * @param a 特征队列，但是每个特征都缺少信息增量比(当初都初始化为0)
	 * @return
	 */
	public ArrayList<IGRperFeature> getFeature(List<IGRperFeature> a){
		int n=numberOfKeyChar;//只选取最关键的numberOfKeyChar个特征
		ArrayList<IGRperFeature> al=new ArrayList<IGRperFeature>();
		for (int i=0;i<a.size();i++) {
			String key = a.get(i).getFeature();
			List<String> value = a.get(i).getValue();
			a.get(i).setIGR(getIGR(key,value));
		}
		ComparatorIGR c=new ComparatorIGR();
		Collections.sort(a,c);//按信息增益比降序排列
		for(int i=0;i<a.size();i++){
			System.out.print("       "+a.get(i).getIGR()+"        ");
		}
		System.out.println();
		for(int i=0;i<n;i++){
			al.add(a.get(i));
		}
		return al;
	}
	/**
	 * 计算信息增益比
	 * @return
	 */
	private double getIGR(String featureName,List<String> ctg){
		double igr=getG(featureName,ctg)/getHA(ctg);
		return igr;
	}
	/**
	 * 计算信息增益
	 * @return
	 */
	private double getG(String featureName,List<String> ctg){
		double g=0;
		g=getHD()-getHDA(featureName,ctg);
		return g;
	}
	/**
	 * 计算特征A的熵
	 * @return
	 */
	private double getHA(List<String> ctg){
		double total=ctg.size();
		double p=0;
		double res=0;
		List<String> list=new ArrayList<String>();
		list.add("VG");
		list.add("G");
		list.add("N");
		list.add("B");
		list.add("VB");
		for(int i=0;i<list.size();i++){
			for(int j=0;j<total;j++){
				if(ctg.get(j).equals(list.get(i))){
					p++;
				}
			}
			double part=p/total;
			res-=(part*log(part,2));
		}
		return res;
	}
	/**
	 * 计算总数据集D的经验熵
	 * @return
	 */
	private double getHD(){
		double total=data.size();
		double yes=0;
		double no=0;
		for(int i=0;i<data.size();i++){
			if(data.get(i).getLabel()){
				yes++;
			}
			else{
				no++;
			}
		}
		double a=yes/total;
		double b=no/total;
		double hd=-(a*log(a,2)+b*log(b,2));
		return hd;
	}
	/**
	 * 计算特征A对总数据集D的经验条件熵
	 * @return
	 */
	private double getHDA(String featureName,List<String> ctg){
		double hda=0;
		List<String> list=new ArrayList<String>();
		list.add("VG");
		list.add("G");
		list.add("N");
		list.add("B");
		list.add("VB");
		for(int i=0;i<list.size();i++){
			String name=list.get(i);
			hda+=(getNumberOfD(featureName,name)*getSubsetHD(featureName,name));
		}
		return hda;
	}
	/**
	 * 计算某一样本子集的经验熵
	 * @param name
	 * @return
	 */
	private double getSubsetHD(String featureName,String name){
		double part=0;
		double a=0;
		double b=0;
		for(int i=0;i<data.size();i++){
			if(data.get(i).getAttributeList().get(featureName).equals(name)){
				part++;
				if(data.get(i).getLabel()){
					a++;
				}
				else{
					b++;
				}
			}
		}
		double p1=a/part;
		double p2=b/part;
		double res=-(p1*log(p1,2)+p2*log(p2,2));
		return res;
	}
	/**
	 * 得到属于某一特征的某一取值的样本总数占总数据集的比值
	 * @param featureName
	 * @param name
	 * @return
	 */
	private double getNumberOfD(String featureName,String name){
		double total=data.size();
		double part=0;
		for(int i=0;i<total;i++){
			if(data.get(i).getAttributeList().get(featureName).equals(name)){
				part++;
			}
		}
		double res=part/total;
		return res;
	}

	private  double log(double value, double base) {
		return Math.log(value) / Math.log(base);
	}
}
