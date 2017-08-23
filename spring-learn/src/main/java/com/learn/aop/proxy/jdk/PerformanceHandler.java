package com.learn.aop.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.learn.aop.PerformanceMonitor;

public class PerformanceHandler implements InvocationHandler{
	
	private Object target;
	
	public PerformanceHandler(Object target) {
		this.target=target;
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//System.out.println(proxy);
		PerformanceMonitor.begin();
		Object obj=method.invoke(target, args);
		PerformanceMonitor.end();
		
		return obj;
	}

}
