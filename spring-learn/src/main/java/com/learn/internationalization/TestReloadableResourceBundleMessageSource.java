package com.learn.internationalization;

import java.util.GregorianCalendar;
import java.util.Locale;

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestReloadableResourceBundleMessageSource {
	public static void main(String[] args) throws Throwable {
		ApplicationContext  ac= new ClassPathXmlApplicationContext("classpath:ac_resource2.xml");
		
		MessageSource ms = (MessageSource)ac.getBean("myResource");
		Object[] params={"John",new GregorianCalendar().getTime()};
		
		/**
		 * 测试发现修改没有生效 TODO
		 */
		while(true){
			String str1=ms.getMessage("greeting.common", params, Locale.US);
			System.out.println(str1);
			Thread.sleep(3000);
		}

		
		
		
	}
}
