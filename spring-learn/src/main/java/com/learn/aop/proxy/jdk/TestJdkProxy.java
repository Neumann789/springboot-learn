package com.learn.aop.proxy.jdk;

import java.lang.reflect.Proxy;

import com.learn.aop.ForumService;
import com.learn.aop.ForumServiceImpl;

public class TestJdkProxy {
	public static void main(String[] args) {
		
		ForumService target=new ForumServiceImpl();
		
		PerformanceHandler handler=new PerformanceHandler(target);
		
		ForumService  proxy=(ForumService)Proxy.newProxyInstance(target.getClass().getClassLoader(),
				target.getClass().getInterfaces(), handler);
		
		proxy.removeForum(000);
		proxy.removeTopic(111);
		
		/**
			PerformanceMonitor.begin()
			删除forum记录0
			PerformanceMonitor.end,耗时:1001毫秒
			PerformanceMonitor.begin()
			删除topic记录111
			PerformanceMonitor.end,耗时:1000毫秒
	 
		 */
		
	}
}
