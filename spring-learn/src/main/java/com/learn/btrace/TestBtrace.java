package com.learn.btrace;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class TestBtrace {
	
	public static void main(String[] args) throws Throwable {
		System.out.println("当前进程号PID:"+getProcessID());
		//Thread.sleep(20000);
		BtraceExecute be=new BtraceExecute();
		while(true){
			Thread.sleep(1000);
			System.out.println("等待1000ms");
			be.execute();
		}
	}
	
    public static final int getProcessID() {  
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
      //  System.out.println(runtimeMXBean.getName());
        return Integer.valueOf(runtimeMXBean.getName().split("@")[0])  
                .intValue();  
    } 

}
