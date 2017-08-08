package com.rainbow.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.rainbow.comm.exception.BaseException;
import com.rainbow.comm.model.RspCode;
import com.rainbow.controller.RainbowController;

public class TestRainbowController {
	@Test
	public void testPushScript(){
		RainbowController rainbowController = new RainbowController();
		
		Map<String, String> map = new HashMap<>();
		
		map.put("pid", "14836");
		
		map.put("script", readContent("F:\\git20170716\\springboot-learn\\btrace\\src\\main\\java\\com\\test\\samples\\MethodTimeCost.java"));
		
		rainbowController.pushScript(map);
	}
	
	
	public static String readContent(String filePath){
		
		BufferedReader br = null;
		
		String buf=null;
		
		StringBuilder content=new StringBuilder();
		
		try {
			
			br= new BufferedReader(new FileReader(new File(filePath)));
			
			while((buf=br.readLine())!=null){
				
				content.append(buf+"\r\n");
				
			}
		} catch (Exception e) {
			
			throw new BaseException(RspCode.VM_SCRPT_READ);
			
		}finally {
			
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return content.toString();
		
	}
}
