package com.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
/**
 * 
 * ClassName: TestThreadPool <br/>
 * Function: 线程池测试类. <br/>
 * Date: 2017年11月1日 上午9:12:00 <br/>
 * 参考地址：
 * http://blog.csdn.net/a314773862/article/details/53898452
 *  3.1 FixedThreadPool 适用为了满足资源管理的需求，需要限制线程数的场景，适用于fu'z负载比较重的服务器
	3.2 SingleThreadPool 需要保证顺序地执行各个任务；并且在任务时间点，不会有多线程获得场景。
	3.3 CacheThreadPool 大小无界的线程池，适用于执行很多的短期异步任务的小任务，或者负载较轻的服务器。
	3.4newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行。
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public class TestThreadPool {
	
	public static void main(String[] args) {
		
		testCacheThreadPool();
		
	}
	
	public void testThreadPoolExecutor(){
		
		//ThreadPoolExecutor tpe = new ThreadPoolExecutor();
		
	}
	
	public static void testCacheThreadPool(){
		
		ExecutorService threadPool = Executors.newCachedThreadPool();
		
		threadPool.execute(new Runnable() {
			
			@Override
			public void run() {
				
				System.out.println("threadPool.execute(new Runnable()");
				
			}
		});
		
	}

}
