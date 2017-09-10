package com.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestFutureTask {
	public static ExecutorService executor = Executors.newSingleThreadExecutor();  
	public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
		
		FutureTask<String> futureTask=new FutureTask<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				Thread.sleep(10000);
				return "success";
			}
		});
		
		executor.submit(futureTask);
		
		String result=futureTask.get(10, TimeUnit.SECONDS);
		
		System.out.println("future result return :"+result);
		
	}

}
