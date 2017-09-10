package com.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestFuture {
	
	public static ExecutorService executor = Executors.newSingleThreadExecutor();  
	
	public static void main(String[] args) throws Throwable, ExecutionException, TimeoutException {
		
		
		
		Future<String> future=executor.submit(new Callable<String>() {
			
			@Override
			public String call() throws Exception {
				Thread.sleep(40000);
				return "success";
			}
		});
		
		
		String result=future.get(30, TimeUnit.SECONDS);
		
		System.out.println("future result return :"+result);
		
	}

}
