package com.learn.internationalization;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 
 * ClassName: TestResouceBoundle <br/>
 * Function: 国际化. <br/>
 * Date: 2017年8月23日 下午1:38:48 <br/>
 *<资源名>_<语言代码>_<国家/地区代码>.properties
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public class TestResouceBoundle {
	public static void main(String[] args) {
		//com/learn/internationalization/resources/resource  
		// 即 路径+资源名
		ResourceBundle rb1 = ResourceBundle
				.getBundle("com/learn/internationalization/resources/resource",Locale.US);
		
		ResourceBundle rb2 = ResourceBundle
				.getBundle("com/learn/internationalization/resources/resource",Locale.CHINA);
		
		System.out.println(rb1.getString("greeting.common"));
		System.out.println(rb2.getString("greeting.common"));
	}
}
