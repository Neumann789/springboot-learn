package com.ref;

public class TestReflection {
	
	public static void main(String[] args) {
		new B().testB();
	}
}

class A{
	public static void testA(){
		System.out.println(sun.reflect.Reflection.getCallerClass(-1));
		System.out.println(sun.reflect.Reflection.getCallerClass(0));
		System.out.println(sun.reflect.Reflection.getCallerClass(1));
		System.out.println(sun.reflect.Reflection.getCallerClass(2));
		System.out.println(sun.reflect.Reflection.getCallerClass(3));
		System.out.println(sun.reflect.Reflection.getCallerClass(4));
	}
}
class B{
	public static void testB(){
		new A().testA();
	}
}
