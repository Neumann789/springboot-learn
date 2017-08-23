package com.learn.annotation.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

public class TestValue {
	public static void main(String[] args) {
		
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		
		Manager manager=(Manager)ac.getBean("manager");
		
		manager.print();
	}
}

@Component("manager")
class Manager{
	
	@Value("#{config.name}")
	private String name;
	@Value("#{config.age}")
	private int age;
	
	public void print(){
		
		System.out.println(name+"=="+age);
		
	}
	
}


@Component("config")
class Config{
	
	private String name="jack";
	
	private int age=100;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	
	
	
}
