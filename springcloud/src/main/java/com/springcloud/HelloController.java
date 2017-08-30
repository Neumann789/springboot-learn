package com.springcloud;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	 //@Value("${config.name}")
	  String name = "World";

	  @RequestMapping("/hello")
	  public String home() {
	    return "Hello " + name;
	  }
}
