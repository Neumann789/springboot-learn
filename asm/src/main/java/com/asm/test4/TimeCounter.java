package com.asm.test4;

public class TimeCounter {
	
	public static void main(String[] args) {
		
		while(true){
			
			try {
				operate();
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public static void operate(){
		System.out.println("operate ......");
	}

}
