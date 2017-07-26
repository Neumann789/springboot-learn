package com.springboot.learn.jetty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication  
@ComponentScan(basePackageClasses=Application.class)
public class Application {  
  
    public static void main(String[] args) {  
    	/*SecurityManager s=new SecurityManager();
    	System.setSecurityManager(s);*/
        SpringApplication.run(Application.class, args);  
        
    }  
}