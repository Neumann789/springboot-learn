package com.method;

public class TestMethod {
	public static void main(String[] args) {
		
		A a=new A();
		a.test();
		a.test("aaa");
		a.test(new String[]{"aa","bb"});
		
	}
}

class A{
	
	public void test(String... ss){
		System.out.println(ss);
	}
	
}
