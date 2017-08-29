package com.string;

public class TestString {
	public static void main(String[] args) {
		
		System.out.println(A.name==B.name);
		
		System.out.println(new A().address==new B().address);
		
	}
}


class A{
	
	public static String name="tom";
	
	public String address="china";
	
}

class B{
	
	public static String name="tom";
	
	public String address="china";
}

