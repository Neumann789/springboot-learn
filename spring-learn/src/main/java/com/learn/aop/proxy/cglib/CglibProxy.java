package com.learn.aop.proxy.cglib;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import com.learn.aop.PerformanceMonitor;


public class CglibProxy implements MethodInterceptor{
	
	private Enhancer enhancer=new Enhancer();
	
	public Object getProxy(Class clazz){
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(this);
		return enhancer.create();
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		
		PerformanceMonitor.begin();
		
		Object result=proxy.invokeSuper(obj, args);
		
		PerformanceMonitor.end();
		
		
		return result;
	}


}
