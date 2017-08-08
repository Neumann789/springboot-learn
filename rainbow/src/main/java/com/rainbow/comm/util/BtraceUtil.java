package com.rainbow.comm.util;

public class BtraceUtil {
	
	public static void injectJVMScript(String pid,String scriptPath) {
		String[] args;
		try {
			
			System.setProperty("com.sun.btrace.debug", "true");
			System.setProperty("com.sun.btrace.probeDescPath", ".");
			System.setProperty("com.sun.btrace.dumpClasses", "false");
			System.setProperty("com.sun.btrace.debug", "false");
			System.setProperty("com.sun.btrace.unsafe", "false");
			args=new String[2];
			args[0]=pid;
			args[1]=scriptPath;
			com.sun.btrace.client.Main.main(args);
		
		} catch (Throwable e) {
			LoggerUtil.error(e);
		}
	}

}
