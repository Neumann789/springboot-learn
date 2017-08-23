package com.learn.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
	public static void main(String[] args) {
		
		ApplicationContext ac= new ClassPathXmlApplicationContext("classpath:ac_event.xml");
		MailSender ms=(MailSender)ac.getBean("mailSender");
		ms.sendMail("zhairuiping");
		
	}
}
