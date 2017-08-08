package com.rainbow.start;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.rainbow.comm.util.VmUtil;
import com.rainbow.entity.MachineInfo;
/**
 * 
 * ClassName: RainbowStart <br/>
 * Function: 彩虹启动入口类. <br/>
 * Date: 2017年8月7日 上午11:53:14 <br/>
 *
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan("com.rainbow")
public class RainbowStart{
	
	private final static Logger logger = LoggerFactory.getLogger(RainbowStart.class);
	
	
	public static void main(String[] args) {
		
		long start=System.currentTimeMillis();
		
		 ApplicationContext ctx = new SpringApplicationBuilder()
	               .sources(RainbowStart.class)
	               .web(true)  //开启web服务
	               .run(args);
		 
			long end=System.currentTimeMillis();
			
			logger.info("mgw启动成功,耗时：{} ms",end-start);
		
	}
	
	public static MachineInfo genMachineInfo(){
		
		MachineInfo machineInfo=new MachineInfo();
		machineInfo.setIp("192.168.7.215");
		machineInfo.setMachineName("工作机器");
		machineInfo.setJvmInfoList(VmUtil.getJvmInfoList());

		return machineInfo;
	}
	
	
	
	
}
