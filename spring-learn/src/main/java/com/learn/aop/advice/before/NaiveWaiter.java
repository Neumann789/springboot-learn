package com.learn.aop.advice.before;

public class NaiveWaiter implements Waiter{

	@Override
	public void greetTo(String name) {
		
		System.out.println("greet to"+name+".....");
		
	}

	@Override
	public void serveTo(String name) {
		
		System.out.println("serving"+name+".....");	
	}

}
