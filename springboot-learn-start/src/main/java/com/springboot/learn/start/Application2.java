package com.springboot.learn.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  
public class Application2 {  
  
    public static void main(String[] args) {  
    	SpringApplication springApplication=new SpringApplication(Application2.class);
    	springApplication.setShowBanner(false);
    	springApplication.setLogStartupInfo(false);
    	springApplication.setHeadless(false);
    	springApplication.setRegisterShutdownHook(false);
    	springApplication.run(args);
    }  
}