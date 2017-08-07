package com.test;
/**
 * 1 如果监听的程序异常，发现监听程序会挂
 * 2
 * @author fanghuabao
 *
 */
public class BtraceTest {
	
	public static void main(String[] args) {
		/*
		 * 
		 *  -Dcom.sun.btrace.debug=true 
		 *  -Dcom.sun.btrace.probeDescPath=. 
		 *  -Dcom.sun.btrace.dumpClasses=false 
		 *  -Dcom.sun.btrace.debug=false 
		 *  -Dcom.sun.btrace.unsafe=false
		 * 
		 * */
		System.setProperty("com.sun.btrace.debug", "true");
		System.setProperty("com.sun.btrace.probeDescPath", ".");
		System.setProperty("com.sun.btrace.dumpClasses", "false");
		System.setProperty("com.sun.btrace.debug", "false");
		System.setProperty("com.sun.btrace.unsafe", "false");
		
		args=new String[2];
		args[0]="23236";
		args[1]="F:\\git20170716\\springboot-learn\\btrace\\src\\main\\java\\com\\test\\samples\\MethodTimeCost.java";
		com.sun.btrace.client.Main.main(args);
		
	}

}
