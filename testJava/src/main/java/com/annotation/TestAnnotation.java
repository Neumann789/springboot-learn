package com.annotation;

/**
 * 
 * ClassName: TestAnnotation <br/>
 * Function: TODO (描述这个类的作用). <br/>
 * Date: 2017年8月24日 上午6:46:48 <br/>
 *p1: Package annotations must be in file package-info.java
 *
 *
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public class TestAnnotation {
	public void test(){
		
		@LocalVariableAnnotation
		String name="tom";
		
		System.out.println(name);
		
	}
}
