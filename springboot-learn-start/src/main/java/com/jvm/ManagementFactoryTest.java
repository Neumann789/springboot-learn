package com.jvm;

import java.lang.management.ManagementFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * JVM之ManagementFactory
 * @author fanghuabao
 *
 */
public class ManagementFactoryTest {
	
	public static void main(String[] args) {
		System.out.println(JSON.toJSONString(ManagementFactory.getRuntimeMXBean(), SerializerFeature.PrettyFormat));
	}
	
	

}
