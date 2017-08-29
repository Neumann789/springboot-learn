package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MyController extends BaseController{
	
	
	
	@RequestMapping("/sayHello")
	public String sayHello(){
		
		logger.info("sayHello..");
		
		handle();
		
		return "hello";
		
	}
	
	public void handle(){
		logger.info("handler..");
	}

}
