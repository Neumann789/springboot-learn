package com.dubbo.xml.zookeeper.dubbo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConsumerStart {
	
	public static void main(String[] args) throws Throwable {
		
		ApplicationContext ac=new ClassPathXmlApplicationContext("classpath:/com/dubbo/xml/zookeeper/dubbo/dubbo-consumer.xml");
		TestService testService=(TestService)ac.getBean("testService");
		for(int i=0;i<1000;i++){
			testService.test();
			Thread.sleep(1000);
		}
		System.out.println("执行结束");
		
		
	}

}
