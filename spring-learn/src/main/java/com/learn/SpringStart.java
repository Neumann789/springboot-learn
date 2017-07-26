package com.learn;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.learn.annotation.bean.Test;

public class SpringStart {
	
	public static void main(String[] args) throws Exception {
		
		ApplicationContext  ac= new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		
		/*Bean bean=ac.findAnnotationOnBean("getTest", Bean.class);
		System.out.println(bean);*/
		
		Test test=(Test)ac.getBean("getTest");
		test.sayHello("jack");
		System.in.read();
	}

}
