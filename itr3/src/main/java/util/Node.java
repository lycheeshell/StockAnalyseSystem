package util;

import java.util.HashMap;

public class Node {
	private boolean ctg;//标记
	private String name ;//名字
	private HashMap<String,Node> list;//子节点集合,key为特征值
	private int num;//含有的股票数
	private Node father;//父节点
	public Node(String n){
		name=n;
		list=new HashMap<String,Node>();
		num=0;
	}

	public boolean getCtg(){
		return ctg;
	}

	public void setCtg(boolean b){
		ctg=b;
	}
	public Node getFather(){
		return father;
	}
	
	public void setFather(Node n){
		father=n;
	}

	public int getNum(){
		return num;
	}
	
	public void setNum(int n){
		num=n;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String n){
		name=n;
	}
	
	public HashMap<String,Node> getList(){
		return list;
	}
	
	public void setList(HashMap<String,Node> l){
		list=l;
	}
}
