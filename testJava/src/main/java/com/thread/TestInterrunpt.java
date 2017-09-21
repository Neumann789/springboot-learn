package com.thread;

public class TestInterrunpt {
	
	public static void main(String[] args) throws InterruptedException {
		
		Thread t=new Thread(){
			public void run() {
				
				try {
					Thread.currentThread().sleep(10000);
					System.out.println("线程结束!");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		};

		t.start();
		
		Thread.sleep(15000);
		
		t.interrupt();
		
		System.out.println("主线程结束!");
		
	}

}
