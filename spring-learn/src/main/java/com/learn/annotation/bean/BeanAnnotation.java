package com.learn.annotation.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanAnnotation {

	@Bean
	public Test getTest(){
		return new Test();
	}
	
}

