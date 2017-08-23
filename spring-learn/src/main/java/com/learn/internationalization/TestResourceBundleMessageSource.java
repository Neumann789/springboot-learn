package com.learn.internationalization;

import java.util.GregorianCalendar;
import java.util.Locale;

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestResourceBundleMessageSource {
	public static void main(String[] args) {
		ApplicationContext  ac= new ClassPathXmlApplicationContext("classpath:ac_resource.xml");
		
		MessageSource ms = (MessageSource)ac.getBean("myResource");
		Object[] params={"John",new GregorianCalendar().getTime()};
		String str1=ms.getMessage("greeting.common", params, Locale.US);
		String str2=ms.getMessage("greeting.common", params, Locale.CHINA);
		
		System.out.println(str1);
		System.out.println(str2);
		
		
		
	}
}
