package com.agent.comm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolUtil {
	
	public static final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	
	public static ExecutorService getCachedThreadPool(){
		
		return cachedThreadPool;
		
	}

}
