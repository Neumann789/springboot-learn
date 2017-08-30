package com.timer;

import java.util.Timer;
import java.util.TimerTask;

import javax.management.timer.TimerMBean;

public class TestTimer {
	
	public static void main(String[] args) {
		
		Timer timer=new Timer();
		
		timer.schedule(new MyTimerTask("task-2000"), 1000L, 2000L);
		
		timer.schedule(new MyTimerTask("task-3000"), 1000L, 3000L);
		
		timer.schedule(new MyTimerTask("task-4000"), 1000L, 4000L);
		
		timer.schedule(new MyTimerTask("task-5000"), 1000L, 5000L);
		
		timer.schedule(new MyTimerTask("task-6000"), 1000L, 6000L);
		
	}

}

class MyTimerTask extends TimerTask{
	
	private String taskName;
	
	public MyTimerTask(String taskName) {
		
		this.taskName = taskName;
		
	}

	@Override
	public void run() {
		
		System.out.println(taskName+"==>run()");
		
	}
	
}
