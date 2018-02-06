package com.thread;

public class TestThread2 {
	
	public static void main(String[] args) {
		
		int num = 4;
		
		for(int i=0;i<num;i++){
			final int j = i;
			createThread("hell", new Runnable() {
				
				@Override
				public void run() {
					System.out.println(j);
				}
			});
		}
		
	}
	
	
	public static void createThread(String threadName,Runnable run){
		
		Thread t = new Thread(run);
		
		t.setName(threadName);
		
		t.start();
		
		
	}
}
