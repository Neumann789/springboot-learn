package com.learn.btrace;

public class BtraceExecute {
	
	public void execute(){
		try {
			Thread.sleep(20000);
			//System.out.println(1/0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("BtraceExecute execute");
	}

}
