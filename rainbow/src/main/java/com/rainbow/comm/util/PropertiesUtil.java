package com.rainbow.comm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * ClassName: PropertiesUtil <br/>
 * Function: 配置文件加载. <br/>
 * Date: 2017年6月25日 下午5:36:14 <br/>
 *
 * @author FANGHUABAO
 * @version 
 * @since JDK 1.7
 */
public class PropertiesUtil {
	
	private static Properties prop = new Properties();
	
	
	static{
		
		loadProp("/rainbow.properties");
	}
	
	
	private static void loadPropByFilePath(String propPath){
		
		InputStream inStream = null;
		
		try {
			
			inStream = new FileInputStream(new File(propPath));
			
			prop.load(inStream);
			
		} catch (Exception e) {
			
			LoggerUtil.info("配置文件加载失败："+propPath);
			
		}
		
	}
	
	public static void loadProp(String propFilePath){
		
		InputStream inStream = null;
		
		try {
			
			inStream=PropertiesUtil.class.getResourceAsStream(propFilePath);
			
			prop.load(inStream);
			
		} catch (Exception e) {
			LoggerUtil.error("配置文件加载失败："+propFilePath, e);
			
		}
		
	}
	
	public static String getProp(String key) {
		
		return (String)prop.get(key);
		
	}
	
	public static void main(String[] args) {
		System.out.println(PropertiesUtil.getProp("OS_TYPE"));
	}

}
