package com.dubbo.xml.zookeeper.dubbo;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ProviderStart {
	
	public static void main(String[] args) throws IOException {
		
		ApplicationContext ac=new ClassPathXmlApplicationContext("classpath:/com/dubbo/xml/zookeeper/dubbo/dubbo-provider.xml");
		
		System.in.read();
		
	}
}
