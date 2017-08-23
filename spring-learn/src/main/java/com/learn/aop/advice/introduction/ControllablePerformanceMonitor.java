package com.learn.aop.advice.introduction;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

import com.learn.aop.PerformanceMonitor;

public class ControllablePerformanceMonitor 
			extends DelegatingIntroductionInterceptor implements Monitorable{

	private ThreadLocal<Boolean> monitorStatusMap = new ThreadLocal<>();
	@Override
	public void setMonitorActive(boolean active) {
		
		monitorStatusMap.set(active);
		
	}
	
	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		
		Object obj = null;
		
		if(monitorStatusMap.get()!=null&&monitorStatusMap.get()){
			PerformanceMonitor.begin();
			obj=super.invoke(mi);
			PerformanceMonitor.end();
		}else{
			obj=super.invoke(mi);
		}
		
		return obj;
	}

}
