package com.learn.aop.advice.before;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestBeforeAdviceBySpringXml {
	public static void main(String[] args) {
		ApplicationContext ac = new ClassPathXmlApplicationContext(
				"classpath:com/learn/aop/advice/before/ac_advice_before.xml");
		
		Waiter waiter=(Waiter)ac.getBean("waiterProxy");
		waiter.serveTo("tom");
		
//		new ProxyFactoryBean().sett
		
	}
}
