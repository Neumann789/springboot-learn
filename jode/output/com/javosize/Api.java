/* Api - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.javosize.agent.session.UserThreadSessionTracker;

public class Api
{
    public static String findSlowMethodsBySession(String sessionId, int delay,
						  int slowerThan) {
	long startTime = System.currentTimeMillis();
	Thread[] threads = getAllThreads();
	boolean found = false;
	StringBuffer results = new StringBuffer();
	while (System.currentTimeMillis() < startTime + (long) delay) {
	    Thread[] threads_0_ = threads;
	    int i = threads_0_.length;
	    for (int i_1_ = 0; i_1_ < i; i_1_++) {
		Thread thread = threads_0_[i_1_];
		if (thread != null) {
		    String id = String.valueOf(thread.getId());
		    if (sessionId.equals(UserThreadSessionTracker
					     .getSessionForThread(id))) {
			String thId = new StringBuilder().append("").append
					  (thread.getId()).toString();
			String appName = null;
			String url = null;
			for (int i_2_ = 0; i_2_ < 4 && appName == null;
			     i_2_++) {
			    appName = UserThreadSessionTracker
					  .getAppByThread(thId);
			    url = UserThreadSessionTracker
				      .getURLByThread(thId);
			    try {
				Thread.sleep(50L);
			    } catch (InterruptedException interruptedexception) {
				/* empty */
			    }
			}
			String value
			    = trackThread(thread, startTime, slowerThan,
					  sessionId, delay);
			if (value != null && !"".equals(value)) {
			    results.append(new StringBuilder().append
					       ("App: ").append
					       (appName).toString());
			    results.append(new StringBuilder().append
					       (" URL: ").append
					       (url).toString());
			    results.append("\n");
			    results.append(value);
			    results.append("\n");
			}
			found = true;
			break;
		    }
		}
	    }
	}
	if (found)
	    return results.toString();
	return new StringBuilder().append
		   ("No Thread activity registered for session: ").append
		   (sessionId).append
		   (" within the specified time: ").append
		   (delay).append
		   (" ms").toString();
    }
    
    public static String findSlowMethodsByThread(String threadId, int delay,
						 int slowerThan) {
	long startTime = System.currentTimeMillis();
	Thread[] threads = getAllThreads();
	boolean found = false;
	StringBuffer results = new StringBuffer();
	while (System.currentTimeMillis() < startTime + (long) delay) {
	    Thread[] threads_3_ = threads;
	    int i = threads_3_.length;
	    for (int i_4_ = 0; i_4_ < i; i_4_++) {
		Thread thread = threads_3_[i_4_];
		if (thread != null) {
		    String id = String.valueOf(thread.getId());
		    if (threadId.equals(id)) {
			String thId = new StringBuilder().append("").append
					  (thread.getId()).toString();
			String appName = null;
			String url = null;
			String value = trackThread(thread, startTime,
						   slowerThan, null, delay);
			if (value != null && !"".equals(value)) {
			    results.append(new StringBuilder().append
					       ("App: ").append
					       (appName).toString());
			    results.append(new StringBuilder().append
					       (" URL: ").append
					       (url).toString());
			    results.append("\n");
			    results.append(value);
			    results.append("\n");
			}
			found = true;
			break;
		    }
		}
	    }
	}
	if (found)
	    return results.toString();
	return new StringBuilder().append
		   ("No Thread activity registered for Thread: ").append
		   (threadId).append
		   (" within the specified time: ").append
		   (delay).append
		   (" ms").toString();
    }
    
    public static String findSlowMethodsByThreadExecutingClass
	(String classRegexp, int delay, int slowerThan) {
	long startTime = System.currentTimeMillis();
	Thread[] threads = getAllThreads();
	boolean found = false;
	StringBuffer results = new StringBuffer();
	while (System.currentTimeMillis() < startTime + (long) delay) {
	    Thread[] threads_5_ = threads;
	    int i = threads_5_.length;
	    for (int i_6_ = 0; i_6_ < i; i_6_++) {
		Thread thread = threads_5_[i_6_];
		if (thread != null
		    && threadStackMatches(thread, classRegexp)) {
		    String thId = new StringBuilder().append("").append
				      (thread.getId()).toString();
		    String appName = null;
		    String url = null;
		    appName = UserThreadSessionTracker.getAppByThread(thId);
		    url = UserThreadSessionTracker.getURLByThread(thId);
		    String value = trackThread(thread, startTime, slowerThan,
					       null, delay);
		    if (value != null && !"".equals(value)) {
			results.append(new StringBuilder().append("App: ")
					   .append
					   (appName).toString());
			results.append(new StringBuilder().append(" URL: ")
					   .append
					   (url).toString());
			results.append(new StringBuilder().append
					   (" ThreadId: ").append
					   (thId).toString());
			results.append("\n");
			results.append(value);
			results.append("\n");
		    }
		    found = true;
		    break;
		}
	    }
	}
	if (found)
	    return results.toString();
	return new StringBuilder().append
		   ("No Thread activity registered accesing package: ").append
		   (classRegexp).append
		   (" within the specified time: ").append
		   (delay).append
		   (" ms").toString();
    }
    
    private static Thread[] getAllThreads() {
	ThreadGroup g = Thread.currentThread().getThreadGroup();
	for (;;) {
	    ThreadGroup g2 = g.getParent();
	    if (g2 == null)
		break;
	    g = g2;
	}
	int size = 256;
	Thread[] threads;
	for (;;) {
	    threads = new Thread[size];
	    if (g.enumerate(threads) < size)
		break;
	    size *= 2;
	}
	return threads;
    }
    
    private static boolean threadStackMatches(Thread thread,
					      String classRegexp) {
	StackTraceElement[] stack = thread.getStackTrace();
	StackTraceElement[] stacktraceelements = stack;
	int i = stacktraceelements.length;
	for (int i_7_ = 0; i_7_ < i; i_7_++) {
	    StackTraceElement ste = stacktraceelements[i_7_];
	    if (ste.getClassName().matches(classRegexp))
		return true;
	}
	return false;
    }
    
    private static String trackThread(Thread toTrackThread, long startTime,
				      int slowerThan, String sessionId,
				      int delay) {
	List samples = new ArrayList();
	String threadId = String.valueOf(toTrackThread.getId());
	int samplingTime = 20;
	long totalTime = 0L;
	while (System.currentTimeMillis() < startTime + (long) delay
	       && (sessionId == null
		   || sessionId.equals(UserThreadSessionTracker
					   .getSessionForThread(threadId)))) {
	    long start = System.currentTimeMillis();
	    samples.add(toTrackThread.getStackTrace());
	    try {
		Thread.sleep((long) samplingTime);
	    } catch (InterruptedException interruptedexception) {
		/* empty */
	    }
	    totalTime += System.currentTimeMillis() - start;
	}
	return displayResults(samples, samplingTime, totalTime, slowerThan);
    }
    
    private static String displayResults(List samples, int samplingTime,
					 long totalTime, int slowerThan) {
	java.util.Map methodStats = new HashMap();
	for (int i = 0; i < samples.size() - 2; i++) {
	    int n = 0;
	    if (((StackTraceElement[]) samples.get(i))[n].toString().equals
		(((StackTraceElement[]) samples.get(i + 1))[n].toString())) {
		MethodPerformanceHolder p
		    = ((MethodPerformanceHolder)
		       methodStats.get(((StackTraceElement[]) samples.get(i))
					   [n].toString()));
		if (p == null) {
		    p = new MethodPerformanceHolder(((StackTraceElement[])
						     samples.get(i))
							[n].toString(),
						    ((StackTraceElement[])
						     samples.get(i)));
		    methodStats.put(((StackTraceElement[]) samples.get(i))
					[n].toString(),
				    p);
		}
		p.getCounter().incrementAndGet();
	    }
	}
	boolean boottleNeckFound = false;
	StringBuffer result = new StringBuffer();
	List list = new ArrayList(methodStats.values());
	Collections.sort(list, Collections.reverseOrder());
	Iterator iterator = list.iterator();
	while (iterator.hasNext()) {
	    MethodPerformanceHolder performanceMethodObject
		= (MethodPerformanceHolder) iterator.next();
	    int methodTime
		= performanceMethodObject.getCounter().get() * samplingTime;
	    if (methodTime >= slowerThan) {
		boottleNeckFound = true;
		result.append("Bottleneck: ");
		result.append(performanceMethodObject.getCounter().get()
			      * samplingTime);
		result.append(" ms. spent at ");
		result.append(performanceMethodObject.getMethod());
		result.append(" method sensitivity: ");
		result.append(totalTime / (long) samples.size());
		result.append(" ms ");
		result.append(". StackTrace: \n");
		StackTraceElement[] stacktraceelements
		    = performanceMethodObject.getStack();
		int i = stacktraceelements.length;
		for (int i_8_ = 0; i_8_ < i; i_8_++) {
		    StackTraceElement ste = stacktraceelements[i_8_];
		    result.append("\t");
		    result.append(ste.toString());
		    result.append("\n");
		}
		result.append("\n");
	    }
	}
	if (boottleNeckFound)
	    return result.toString();
	return findSlowestLowestMethods(samples, samplingTime, totalTime,
					slowerThan);
    }
    
    private static String findSlowestLowestMethods
	(List samples, int samplingTime, long totalTime, int slowerThan) {
	java.util.Map methodStats = new HashMap();
	for (int i = 0; i < samples.size() - 2; i++) {
	    for (int n = Math.min(((StackTraceElement[])
				   samples.get(i)).length,
				  ((StackTraceElement[])
				   samples.get(i + 1)).length) - 1;
		 n >= 0; n--) {
		if (((StackTraceElement[]) samples.get(i))[n].toString().equals
		    (((StackTraceElement[]) samples.get(i + 1))[n]
			 .toString())) {
		    MethodPerformanceHolder p
			= ((MethodPerformanceHolder)
			   methodStats.get(((StackTraceElement[])
					    samples.get(i))
					       [n].toString()));
		    if (p == null) {
			p = new MethodPerformanceHolder(((StackTraceElement[])
							 samples.get(i))
							    [n].toString(),
							((StackTraceElement[])
							 samples.get(i)));
			methodStats.put(((StackTraceElement[]) samples.get(i))
					    [n].toString(),
					p);
		    }
		    p.getCounter().incrementAndGet();
		    p.setDepth(n);
		}
	    }
	}
	StringBuffer result = new StringBuffer();
	List list = new ArrayList(methodStats.values());
	Collections.sort(list, Collections.reverseOrder());
	Iterator iterator = list.iterator();
	while (iterator.hasNext()) {
	    MethodPerformanceHolder performanceMethodObject
		= (MethodPerformanceHolder) iterator.next();
	    int spentTime
		= performanceMethodObject.getCounter().get() * samplingTime;
	    if (spentTime > slowerThan) {
		result.append("Slowest Lowest Method: ");
		result.append(spentTime);
		result.append(" ms. (total time)  spent at ");
		result.append(performanceMethodObject.getMethod());
		result.append(" method sensitivity: ");
		result.append(totalTime / (long) samples.size());
		result.append(" ms ");
		result.append(". StackTrace: \n");
		StackTraceElement[] stacktraceelements
		    = performanceMethodObject.getStack();
		int i = stacktraceelements.length;
		for (int i_9_ = 0; i_9_ < i; i_9_++) {
		    StackTraceElement ste = stacktraceelements[i_9_];
		    result.append("\t");
		    result.append(ste.toString());
		    result.append("\n");
		}
		result.append("\n");
	    }
	}
	return result.toString();
    }
}
