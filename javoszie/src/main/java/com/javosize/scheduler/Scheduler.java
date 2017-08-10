package com.javosize.scheduler;

import com.javosize.cli.preferences.JavOSizePreferences;
import com.javosize.log.Log;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class Scheduler extends TimerTask implements java.io.Serializable {
	private static final long serialVersionUID = 591636657888981009L;
	private Map<String, Schedule> schedules = new ConcurrentHashMap();
	private static volatile transient Scheduler scheduler = null;
	private static Timer timer;
	private static Log log = new Log(Scheduler.class.getName());

	static {
		try {
			if (com.javosize.cli.Main.isBackgroundMode()) {
				timer = new Timer();

				timer.schedule(getScheduler(), 0L, 5000L);
				log.info("Scheduler started sucessfully");
			}
		} catch (Throwable th) {
			log.error("Loading Scheduler failed: " + th, th);
		}
	}

	public static void stop() {
		try {
			if (timer != null) {
				timer.cancel();
			}
		} catch (Exception localException) {
		}
	}

	public static synchronized Scheduler getScheduler() throws IOException, ClassNotFoundException {
		return getScheduler(false);
	}

	public static synchronized Scheduler getScheduler(boolean forceReload) throws IOException, ClassNotFoundException {
		if ((scheduler != null) && (!forceReload)) {
			return scheduler;
		}
		scheduler = JavOSizePreferences.loadScheduler();
		return scheduler;
	}

	public static synchronized void addSchedule(Schedule r) throws IOException, ClassNotFoundException {
		getScheduler(true).getSchedules().put(r.getName(), r);
		JavOSizePreferences.persistScheduler(getScheduler(false));
	}

	public static synchronized void removeSchedule(Schedule r) throws IOException, ClassNotFoundException {
		getScheduler(true).getSchedules().remove(r.getName());
		JavOSizePreferences.persistScheduler(getScheduler(false));
	}

	public static Schedule getSchedule(String name) throws IOException, ClassNotFoundException {
		return (Schedule) getScheduler(true).getSchedules().get(name);
	}

	public Map<String, Schedule> getSchedules() {
		return this.schedules;
	}

	public void run() {
		Map<String, Schedule> schedulesCopy = new ConcurrentHashMap();
		try {
			for (Schedule s : getScheduler(true).getSchedules().values()) {
				schedulesCopy.put(s.getName(), s);
			}
		} catch (Throwable e) {
			log.error("Problem reloading schedules", (Throwable) e);
		}

		for (Iterator<Schedule> e = schedulesCopy.values().iterator(); ((Iterator) e).hasNext();) {
			Schedule s = (Schedule) ((Iterator) e).next();
			if (s.triggerIfRequired()) {
				try {
					Schedule executedTask = (Schedule) getScheduler(true).getSchedules().get(s.getName());

					if (executedTask != null) {
						executedTask.setLastExecuted(s.getLastExecuted());
						executedTask.setLastExecutionResult(s.getLastExecutionResult());
						JavOSizePreferences.persistScheduler(getScheduler(false));
					}
				} catch (Throwable th) {
					log.error("Error persisiting scheduler info after task execution: " + th, th);
				}
			}
		}
	}
}
