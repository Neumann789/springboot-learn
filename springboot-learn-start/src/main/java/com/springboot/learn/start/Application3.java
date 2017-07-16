package com.springboot.learn.start;

import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

@SpringBootApplication  
public class Application3 {  
  
    public static void main(String[] args) {  
    	ApplicationContext ctx = new SpringApplicationBuilder()
    			.sources(Application3.class)
    			.bannerMode(Banner.Mode.OFF)
    			.web(true)
    			.logStartupInfo(false)
    			.registerShutdownHook(false)
    			.headless(false)
    			.run(args);
    			
    }  
}