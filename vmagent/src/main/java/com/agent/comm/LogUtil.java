package com.agent.comm;

import com.alibaba.fastjson.JSON;

public class LogUtil {
	
	public static void info(Object msg){
		System.out.println(JSON.toJSONString(msg));
	}

}
