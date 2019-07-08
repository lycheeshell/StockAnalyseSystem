package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DTree {
	private double  THRESHOLD=0.003;//阈值
	private int numOfLeaf=0;//叶节点个数
	//private double loss=0;//损失量
	//private double a=0;//损失参数
	private Node root;
	/*
	public double getLoss(){
		return loss;
	}*/
	/*
	public void setLoss(double d){
		loss=d;
	}*/
	
	public Node getRoot(){
		return root;
	}
	
	public int getNumOfLeaf(){
		return numOfLeaf;
	}
	
	public void setNumOfLeaf(int n){
		numOfLeaf=n;
	}
	public DTree(List<IGRperFeature> feature,List<Stock> data,double aa){
		root=createTree(feature,data,null);
		//a=aa;
		//loss+=a*numOfLeaf;
	}
	/**
	 * 创建决策树，返回根节点
	 * @return
	 */
	public Node createTree(List<IGRperFeature> feature,List<Stock> data,Node father){
		int max=0;
		for(int i=0;i<feature.size();i++){
			if(feature.get(max).getIGR()<feature.get(i).getIGR()){
				max=i;
			}
		}
		Node root=new Node(feature.get(max).getFeature());
		root.setFather(father);//设置此节点的父节点
		//int n=data.size();//样本点数
		/*
		int a=0;
		for(int i=0;i<n;i++){
			Stock stock=data.get(i);
			if(stock.getLabel()){
				a++;
			}
		}
		int b=n-a;
		System.out.println("n 的值是 "+n);*/
		root.setNum(data.size());//此节点包含的数据量
		if(dataIsPure(data)){
			root.setCtg(data.get(0).getLabel());
			numOfLeaf++;
			HashMap<String,Node> list=new HashMap<String,Node>();
			root.setList(list);
			//loss+=ee*n;
			return root;
		}
		if(feature.isEmpty()||feature.get(max).getIGR()<THRESHOLD){
			root.setCtg(decideCategory(data));
			numOfLeaf++;
			HashMap<String,Node> list=new HashMap<String,Node>();
			root.setList(list);
			//loss+=ee*n;
			return root;
		}
		List<IGRperFeature> newFeature=new ArrayList<IGRperFeature>();
		for(int i=0;i<feature.size();i++){
			if(!feature.get(i).getFeature().equals(feature.get(max).getFeature())) {
				newFeature.add(feature.get(i));
			}
		}
		if(newFeature.isEmpty()){
			root.setCtg(decideCategory(data));
			numOfLeaf++;
			HashMap<String,Node> list=new HashMap<String,Node>();
			root.setList(list);
			return root;
		}
		/*
		IGRperFeature f=feature.get(0);
		for(int i=0;i<f.getValue().size();i++){
			List<Stock> subData=divide(f.getFeature(),f.getValue().get(i),data);
			list.put(f.getValue().get(i),createTree(newFeature,subData,root));
		}*/
		List<Stock> s1=new ArrayList<>();
		List<Stock> s2=new ArrayList<>();
		List<Stock> s3=new ArrayList<>();
		List<Stock> s4=new ArrayList<>();
		List<Stock> s5=new ArrayList<>();
		for(int i=0;i<data.size();i++){
			Stock s=data.get(i);
			if(s.getAttributeList().get(feature.get(max).getFeature()).equals("VG")){
				s1.add(s);
			}
			else if(s.getAttributeList().get(feature.get(max).getFeature()).equals("G")){
				s2.add(s);
			}
			else if(s.getAttributeList().get(feature.get(max).getFeature()).equals("N")){
				s3.add(s);
			}
			else if(s.getAttributeList().get(feature.get(max).getFeature()).equals("B")){
				s4.add(s);
			}
			else{
				s5.add(s);
			}
		}
		//root.setCtg(decideCategory(data));
		HashMap<String,Node> hm=new HashMap<>();
		if(s1.size()>0) {
			hm.put("VG", createTree(newFeature, s1, root));
		}
		if(s2.size()>0) {
			hm.put("G", createTree(newFeature, s2, root));
		}
		if(s3.size()>0) {
			hm.put("N", createTree(newFeature, s3, root));
		}
		if(s4.size()>0) {
			hm.put("B", createTree(newFeature, s4, root));
		}
		if(s5.size()>0) {
			hm.put("VB", createTree(newFeature, s5, root));
		}
		root.setList(hm);
		if(hm.size()<5){
			root.setCtg(decideCategory(data));
		}
		return root;
	}
	/**
	 * 按某一特征的不同取值将数据集分割
	 * 比如某个特征名为featureName,有不同的取值fcat1,fcat2...
	 * @param fcat
	 * @return
	 */
	private List<Stock> divide(String featureName,String fcat,List<Stock> data){
		List<Stock> slist=new ArrayList<Stock>();
		for(int i=0;i<data.size();i++){
			Stock stock=data.get(i);
			if(stock.getAttributeList().get(featureName).equals(fcat)){
				slist.add(stock);
			}
		}
		return slist;
	}
	/**
	 * 判断数据集是否纯净
	 * @param data
	 * @return
	 */
	private boolean dataIsPure(List<Stock> data){
		boolean b=data.get(0).getLabel();
		for(int i=1;i<data.size();i++){
			if(b!=data.get(i).getLabel()){
				return false;
			}
		}
		return true;
	}
	/**
	 * 按少数服从多数确定叶节点的类
	 * @param data
	 * @return
	 */
	private boolean decideCategory(List<Stock> data){
		int total=data.size();
		int res=0;
		boolean b=data.get(0).getLabel();
		for(int i=0;i<total;i++){
			if(b==data.get(i).getLabel()){
				res++;
			}
		}
		if(res>=(total/2)){
			return b;
		}
		else{
			return !b;
		}
	}

	private  double log(double value, double base) {
		return Math.log(value) / Math.log(base);
	}
}
