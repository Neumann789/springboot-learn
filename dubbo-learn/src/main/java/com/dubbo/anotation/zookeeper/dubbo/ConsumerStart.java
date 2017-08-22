package com.dubbo.anotation.zookeeper.dubbo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConsumerStart {
	
	public static void main(String[] args) {
		
		ApplicationContext ac=new ClassPathXmlApplicationContext("classpath:/com/dubbo/xml/zookeeper/dubbo/dubbo-consumer.xml");
		TestService testService=(TestService)ac.getBean("testService");
		testService.test();
		System.out.println("执行结束");
		
		
	}

}
