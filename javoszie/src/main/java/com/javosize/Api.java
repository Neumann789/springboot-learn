 package com.javosize;
 
 import com.javosize.agent.session.UserThreadSessionTracker;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.atomic.AtomicInteger;
 
 public class Api
 {
   public static String findSlowMethodsBySession(String sessionId, int delay, int slowerThan)
   {
     long startTime = System.currentTimeMillis();
     Thread[] threads = getAllThreads();
     
 
     boolean found = false;
     StringBuffer results = new StringBuffer();
     while (System.currentTimeMillis() < startTime + delay) {
       for (Thread thread : threads) {
         if (thread != null)
         {
 
           String id = String.valueOf(thread.getId());
           if (sessionId.equals(
             UserThreadSessionTracker.getSessionForThread(id))) {
             String thId = "" + thread.getId();
             String appName = null;
             String url = null;
             
 
             for (int i = 0; (i < 4) && (appName == null); i++) {
               appName = UserThreadSessionTracker.getAppByThread(thId);
               url = UserThreadSessionTracker.getURLByThread(thId);
               try {
                 Thread.sleep(50L);
               }
               catch (InterruptedException localInterruptedException) {}
             }
             
             String value = trackThread(thread, startTime, slowerThan, sessionId, delay);
             if ((value != null) && (!"".equals(value))) {
               results.append("App: " + appName);
               results.append(" URL: " + url);
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
     if (found) {
       return results.toString();
     }
     return "No Thread activity registered for session: " + sessionId + " within the specified time: " + delay + " ms";
   }
   
 
 
 
   public static String findSlowMethodsByThread(String threadId, int delay, int slowerThan)
   {
     long startTime = System.currentTimeMillis();
     Thread[] threads = getAllThreads();
     
     boolean found = false;
     StringBuffer results = new StringBuffer();
     while (System.currentTimeMillis() < startTime + delay) {
       for (Thread thread : threads) {
         if (thread != null)
         {
 
           String id = String.valueOf(thread.getId());
           if (threadId.equals(id)) {
             String thId = "" + thread.getId();
             String appName = null;
             String url = null;
             
             String value = trackThread(thread, startTime, slowerThan, null, delay);
             if ((value != null) && (!"".equals(value))) {
               results.append("App: " + appName);
               results.append(" URL: " + url);
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
     if (found) {
       return results.toString();
     }
     return "No Thread activity registered for Thread: " + threadId + " within the specified time: " + delay + " ms";
   }
   
 
 
 
   public static String findSlowMethodsByThreadExecutingClass(String classRegexp, int delay, int slowerThan)
   {
     long startTime = System.currentTimeMillis();
     
     Thread[] threads = getAllThreads();
     
     boolean found = false;
     StringBuffer results = new StringBuffer();
     while (System.currentTimeMillis() < startTime + delay) {
       for (Thread thread : threads) {
         if (thread != null)
         {
 
           if (threadStackMatches(thread, classRegexp))
           {
             String thId = "" + thread.getId();
             String appName = null;
             String url = null;
             
             appName = UserThreadSessionTracker.getAppByThread(thId);
             url = UserThreadSessionTracker.getURLByThread(thId);
             
             String value = trackThread(thread, startTime, slowerThan, null, delay);
             
             if ((value != null) && (!"".equals(value))) {
               results.append("App: " + appName);
               results.append(" URL: " + url);
               results.append(" ThreadId: " + thId);
               
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
     if (found) {
       return results.toString();
     }
     return "No Thread activity registered accesing package: " + classRegexp + " within the specified time: " + delay + " ms";
   }
   
 
 
 
 
   private static Thread[] getAllThreads()
   {
     ThreadGroup g = Thread.currentThread().getThreadGroup();
     for (;;) {
       ThreadGroup g2 = g.getParent();
       if (g2 == null) {
         break;
       }
       g = g2;
     }
     
 
     int size = 256;
     Thread[] threads;
     for (;;) { threads = new Thread[size];
       if (g.enumerate(threads) < size) {
         break;
       }
       size *= 2;
     }
     
     return threads;
   }
   
   private static boolean threadStackMatches(Thread thread, String classRegexp)
   {
     StackTraceElement[] stack = thread.getStackTrace();
     for (StackTraceElement ste : stack) {
       if (ste.getClassName().matches(classRegexp)) {
         return true;
       }
     }
     return false;
   }
   
 
   private static String trackThread(Thread toTrackThread, long startTime, int slowerThan, String sessionId, int delay)
   {
     List<StackTraceElement[]> samples = new ArrayList();
     String threadId = String.valueOf(toTrackThread.getId());
     
     int samplingTime = 20;
     
     long totalTime = 0L;
     
 
 
     while ((System.currentTimeMillis() < startTime + delay) && (
       (sessionId == null) || (sessionId.equals(
       UserThreadSessionTracker.getSessionForThread(threadId)))))
     {
 
 
 
       long start = System.currentTimeMillis();
       samples.add(toTrackThread.getStackTrace());
       try {
         Thread.sleep(samplingTime);
       }
       catch (InterruptedException localInterruptedException) {}
       totalTime += System.currentTimeMillis() - start;
     }
     return displayResults(samples, samplingTime, totalTime, slowerThan);
   }
   
   private static String displayResults(List<StackTraceElement[]> samples, int samplingTime, long totalTime, int slowerThan)
   {
     Map<String, MethodPerformanceHolder> methodStats = new java.util.HashMap();
     
     for (int i = 0; i < samples.size() - 2; i++) {
       int n = 0;
       if (((StackTraceElement[])samples.get(i))[n].toString().equals(
         ((StackTraceElement[])samples.get(i + 1))[n].toString())) {
         MethodPerformanceHolder p = (MethodPerformanceHolder)methodStats.get(((StackTraceElement[])samples.get(i))[n].toString());
         if (p == null) {
           p = new MethodPerformanceHolder(((StackTraceElement[])samples.get(i))[n].toString(), (StackTraceElement[])samples.get(i));
           methodStats.put(((StackTraceElement[])samples.get(i))[n].toString(), p);
         }
         p.getCounter().incrementAndGet();
       }
     }
     
     boolean boottleNeckFound = false;
     StringBuffer result = new StringBuffer();
     List<MethodPerformanceHolder> list = new ArrayList(methodStats.values());
     Collections.sort(list, Collections.reverseOrder());
     for (MethodPerformanceHolder performanceMethodObject : list) {
       int methodTime = performanceMethodObject.getCounter().get() * samplingTime;
       if (methodTime >= slowerThan) {
         boottleNeckFound = true;
         result.append("Bottleneck: ");
         result.append(performanceMethodObject.getCounter().get() * samplingTime);
         result.append(" ms. spent at ");
         result.append(performanceMethodObject.getMethod());
         result.append(" method sensitivity: ");
         result.append(totalTime / samples.size());
         result.append(" ms ");
         result.append(". StackTrace: \n");
         for (StackTraceElement ste : performanceMethodObject.getStack()) {
           result.append("\t");
           result.append(ste.toString());
           result.append("\n");
         }
         result.append("\n");
       }
     }
     
     if (boottleNeckFound) {
       return result.toString();
     }
     return findSlowestLowestMethods(samples, samplingTime, totalTime, slowerThan);
   }
   
 
 
   private static String findSlowestLowestMethods(List<StackTraceElement[]> samples, int samplingTime, long totalTime, int slowerThan)
   {
     Map<String, MethodPerformanceHolder> methodStats = new java.util.HashMap();
     MethodPerformanceHolder p;
     for (int i = 0; i < samples.size() - 2; i++) {
       for (int n = Math.min(((StackTraceElement[])samples.get(i)).length, ((StackTraceElement[])samples.get(i + 1)).length) - 1; n >= 0; n--) {
         if (((StackTraceElement[])samples.get(i))[n].toString().equals(
           ((StackTraceElement[])samples.get(i + 1))[n].toString())) {
           p = (MethodPerformanceHolder)methodStats.get(((StackTraceElement[])samples.get(i))[n].toString());
           if (p == null) {
             p = new MethodPerformanceHolder(((StackTraceElement[])samples.get(i))[n].toString(), (StackTraceElement[])samples.get(i));
             methodStats.put(((StackTraceElement[])samples.get(i))[n].toString(), p);
           }
           p.getCounter().incrementAndGet();
           p.setDepth(n);
         }
       }
     }
     
 
 
     StringBuffer result = new StringBuffer();
     List<MethodPerformanceHolder> list = new ArrayList(methodStats.values());
     Collections.sort(list, Collections.reverseOrder());
     for (MethodPerformanceHolder performanceMethodObject : list) {
       int spentTime = performanceMethodObject.getCounter().get() * samplingTime;
       if (spentTime > slowerThan) {
         result.append("Slowest Lowest Method: ");
         result.append(spentTime);
         result.append(" ms. (total time)  spent at ");
         result.append(performanceMethodObject.getMethod());
         result.append(" method sensitivity: ");
         result.append(totalTime / samples.size());
         result.append(" ms ");
         result.append(". StackTrace: \n");
         for (StackTraceElement ste : performanceMethodObject.getStack()) {
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


