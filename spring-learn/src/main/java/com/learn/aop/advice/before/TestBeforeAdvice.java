package com.learn.aop.advice.before;

import org.springframework.aop.BeforeAdvice;
import org.springframework.aop.framework.ProxyFactory;

/**
 * 
 * ClassName: TestBeforeAdvice <br/>
 * Function: 测试前置通知. <br/>
 * Date: 2017年8月23日 下午5:00:21 <br/>
 *
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public class TestBeforeAdvice {
	
	public static void main(String[] args) {
		
		Waiter target= new NaiveWaiter();
		
		BeforeAdvice advice=new GreetingBeforeAdvice();
		
		//1 spring提供的代理工厂
		ProxyFactory pf = new ProxyFactory();
		
		pf.setTarget(target);
		
		pf.addAdvice(advice);
		
		Waiter proxy = (Waiter)pf.getProxy();
		
		
		
		proxy.greetTo("John");
		
		proxy.serveTo("Tom");
		
	}

}
