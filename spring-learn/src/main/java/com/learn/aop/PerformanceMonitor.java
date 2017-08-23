package com.learn.aop;

public class PerformanceMonitor {
	private static ThreadLocal<Long> time=
			new ThreadLocal<>();
	public static void begin(){
		System.out.println("PerformanceMonitor.begin()");
		time.set(System.currentTimeMillis());
	}
	
	public static void end(){
		System.out.println("PerformanceMonitor.end,耗时:"+(System.currentTimeMillis()-time.get())+"毫秒");
	}
}
