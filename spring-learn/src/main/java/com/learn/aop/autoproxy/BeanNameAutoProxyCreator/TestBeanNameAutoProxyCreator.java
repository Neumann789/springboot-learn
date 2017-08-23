package com.learn.aop.autoproxy.BeanNameAutoProxyCreator;

import java.lang.reflect.Method;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TestBeanNameAutoProxyCreator {
	public static void main(String[] args) {
		ApplicationContext ac = new ClassPathXmlApplicationContext(
				"classpath:com/learn/aop/autoproxy/BeanNameAutoProxyCreator/ac_advice_BeanNameAutoProxyCreator.xml");
		
		Waiter waiter=(Waiter)ac.getBean("waiter");
		Seller seller=(Seller)ac.getBean("seller");
		
		waiter.sayHello();
		seller.syaBye();
	}
}

class Waiter{
	
	public void sayHello(){
		System.out.println("Waiter.sayHello()");
	}
	
}

class Seller{
	
	public void syaBye(){
		
		System.out.println("Seller.syaBye()");
		
	}
	
}

class GreetingBeforeAdvice implements MethodBeforeAdvice{

	@Override
	public void before(Method method, Object[] args, Object target) throws Throwable {
		System.out.println("GreetingBeforeAdvice.before");
	}
	
}
