package com.test;

public class Test {
	
	private static String PRE_SAY="HELLO:";
	
	public static int add(int a,int b){
		
		return a + b;
		
	}
	
	public String sayHello(String name){
		
		return PRE_SAY+name;
		
	}
	
	public long operate(long a,long b){
		return a+b;
	}
	
	
	
	public static int oprete(int a,int b){
		
		int c=add(a, b);
		
		return c;
		
	}
	
	

}
