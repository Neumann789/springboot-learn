package com.learn.annotation.bean;

public class Test{
	
	private TestDao dao;
	
	public void sayHello(String msg){
		
		System.out.println("hello:"+msg);
		
		dao.insert();
		
	}
	
	
	
}
