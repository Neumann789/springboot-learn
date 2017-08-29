package com.thread;

public class TestThread {
	
	public static void main(String[] args) {
		
		System.out.println("main() start");
		
		
		Thread t=new Thread(){
			public void run() {
				boolean isStart=true;
				while(isStart){
					try {
						System.out.println("休息1s钟");
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("Thread end!");
				
			};
		};
		
		t.setDaemon(true);
		
		t.start();
		
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
