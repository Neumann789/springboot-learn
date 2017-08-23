package com.learn;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.learn.bean.MyBean;

public class SpringStart {
	
	public static void main(String[] args) throws Exception {
		
		ApplicationContext  ac= new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		
		/*Bean bean=ac.findAnnotationOnBean("getTest", Bean.class);
		System.out.println(bean);*/
		
		MyBean myBean=(MyBean)ac.getBean("myBean");
		myBean.handle();
		System.in.read();
	}

}
