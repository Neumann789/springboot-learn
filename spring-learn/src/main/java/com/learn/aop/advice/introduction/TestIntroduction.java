package com.learn.aop.advice.introduction;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.learn.aop.ForumService;

public class TestIntroduction {
	public static void main(String[] args) {
		
		ApplicationContext ac = new ClassPathXmlApplicationContext(
				"classpath:com/learn/aop/advice/introduction/ac_advice_introduction.xml");
		
		ForumService f=(ForumService)ac.getBean("forumServiceTargetProxy");
		f.removeForum(000);
		f.removeTopic(111);
		
		Monitorable m=(Monitorable)f;
		m.setMonitorActive(true);
		
		f.removeForum(000);
		f.removeTopic(111);
		/**测试结果如下：
		 删除forum记录0
		删除topic记录111
		PerformanceMonitor.begin()
		删除forum记录0
		PerformanceMonitor.end,耗时:1000毫秒
		PerformanceMonitor.begin()
		删除topic记录111
		PerformanceMonitor.end,耗时:1000毫秒
		 */
		
	}
}
