 package com.javosize.agent.session;
 
 import com.javosize.agent.CacheManager;
 import com.javosize.agent.Utils;
 import java.lang.management.ManagementFactory;
 import java.text.DecimalFormat;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.WeakHashMap;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.atomic.AtomicInteger;
 
 public class UserThreadSessionTracker
 {
   private static int MAX_CACHE_SIZE = 200;
   
   private static Map<String, String> userBySession = CacheManager.getCacheLRU(MAX_CACHE_SIZE, true);
   private static Map<String, String> sessionByThread = new ConcurrentHashMap();
   private static Map<String, String> appByThread = new ConcurrentHashMap();
   private static Map<String, String> urlByThread = new ConcurrentHashMap();
   private static Map<String, Long> currentTimeByThread = new ConcurrentHashMap();
   private static Map<String, CpuTimeCounter> currentCPUByThread = new ConcurrentHashMap();
   private static Map<String, Long> currentMemoryByThread = new ConcurrentHashMap();
   private static Map<String, CpuTimeCounter> cpuPerSession = CacheManager.getCacheLRU(MAX_CACHE_SIZE, true);
   private static Map<String, CpuTimeCounter> cpuPerUser = CacheManager.getCacheLRU(MAX_CACHE_SIZE, true);
   private static Map<String, CpuTimeCounter> cpuPerApp = CacheManager.getCacheLRU(MAX_CACHE_SIZE, true);
   private static Map<String, Set<String>> urlPerApp = new ConcurrentHashMap();
   private static Map<String, CpuTimeCounter> cpuPerUrl = CacheManager.getCacheLRU(MAX_CACHE_SIZE, true);
   private static Map<String, ClassLoader> classLoaders = CacheManager.getCacheLRU(MAX_CACHE_SIZE, true);
   private static Map<String, Object[]> breakpointsParams = new ConcurrentHashMap();
   
   private static ThreadLocal<Long> simpleMethodTracker = new ThreadLocal();
   private static ThreadLocal<CpuTimeCounter> cpuTracker = new ThreadLocal();
   
   private static Map<String, Object> sessionBySessionId = Collections.synchronizedMap(new WeakHashMap());
   private static Map<String, String> breakpointsStack = new ConcurrentHashMap();
   
   public static void simpleMethodStart() {
     if (simpleMethodTracker.get() != null) {
       return;
     }
     Long startTime = Long.valueOf(System.currentTimeMillis());
     simpleMethodTracker.set(startTime);
   }
   
   public static void purgeSession(String id) {
     if (id == null) {
       return;
     }
     sessionBySessionId.remove(id);
   }
   
   public static long simpleMethodEnd() {
     long start = ((Long)simpleMethodTracker.get()).longValue();
     simpleMethodTracker.remove();
     return System.currentTimeMillis() - start;
   }
   
   public static String getCurrentThreadId() {
     return String.valueOf(Thread.currentThread().getId());
   }
   
   public static void addBreakpointParams(String name, Object[] params) {
     addBreakpointStack(name);
     breakpointsParams.put(name, params);
   }
   
   private static void addBreakpointStack(String name) {
     StackTraceElement[] st = Thread.currentThread().getStackTrace();
     
     breakpointsStack.put(name, Utils.stackTraceToString(st, 4));
   }
   
   public static String getCurrentAllocatedMemoryByThread(String thid) {
     if (currentMemoryByThread.containsKey(thid)) {
       return "" + (CpuTimeCounter.getThreadAllocatedBytes(new Long(thid).longValue()) - ((Long)currentMemoryByThread.get(thid)).longValue());
     }
     return "";
   }
   
   public static String getCurrentCPUByThread(String thid)
   {
     if (currentCPUByThread.containsKey(thid)) {
       return ((CpuTimeCounter)currentCPUByThread.get(thid)).getCurrentUsedCPU();
     }
     return "";
   }
   
   public static void removeBreakpointParams(String name)
   {
     breakpointsParams.remove(name);
   }
   
   public static Object[] getBreakpointParams(String name) {
     return (Object[])breakpointsParams.get(name);
   }
   
   public static String getBreakpointStack(String name) {
     return (String)breakpointsStack.get(name);
   }
   
   public static String getBreakpointParamsSerial(String name) {
     Object[] params = (Object[])breakpointsParams.get(name);
     StringBuffer sb = new StringBuffer();
     int counter = 0;
     for (Object object : params) {
       if (counter == 0) {
         sb.append("This: ");
       } else {
         sb.append("{" + (counter - 1) + "}: ");
       }
       sb.append(String.valueOf(object));
       sb.append("\n");
       counter++;
     }
     return sb.toString();
   }
   
   public static String getCurrentSession() {
     String thid = getCurrentThreadId();
     return (String)sessionByThread.get(thid);
   }
   
   public static String getCurrentUser() {
     String sid = getCurrentSession();
     if (sid == null) {
       return null;
     }
     return (String)userBySession.get(sid);
   }
   
   public static String getCurrentAppName() {
     String thid = getCurrentThreadId();
     return (String)appByThread.get(thid);
   }
   
   public static Set<String> getURLsPerApp(String app) {
     return (Set)urlPerApp.get(app);
   }
   
   public static void startTracking(Thread th, Object session, String sessionId, String userId, String url, String app, Object servlet)
   {
     CpuTimeCounter cpuCounter = new CpuTimeCounter(th.getId());
     cpuTracker.set(cpuCounter);
     cpuCounter.start();
     
     String thid = String.valueOf(th.getId());
     currentTimeByThread.put(thid, Long.valueOf(System.currentTimeMillis()));
     currentCPUByThread.put(thid, cpuCounter);
     currentMemoryByThread.put(thid, Long.valueOf(CpuTimeCounter.getThreadAllocatedBytes(th.getId())));
     
     if (sessionId != null) {
       sessionBySessionId.put(sessionId, session);
       sessionByThread.put(thid, sessionId);
       userBySession.put(sessionId, userId);
       CpuTimeCounter cpuSession = (CpuTimeCounter)cpuPerSession.get(sessionId);
       if (cpuSession == null) {
         cpuSession = new CpuTimeCounter(th.getId());
         cpuPerSession.put(sessionId, cpuSession);
       }
       
       if (userId != null) {
         CpuTimeCounter cpuUser = (CpuTimeCounter)cpuPerUser.get(userId);
         if (cpuUser == null) {
           cpuUser = new CpuTimeCounter(th.getId());
           cpuPerUser.put(userId, cpuUser);
         }
       }
     }
     
     if (app != null) {
       ClassLoader cl = servlet.getClass().getClassLoader();
       classLoaders.put(app, cl);
       CpuTimeCounter cpuApp = (CpuTimeCounter)cpuPerApp.get(app);
       if (cpuApp == null) {
         cpuApp = new CpuTimeCounter(th.getId());
         cpuPerApp.put(app, cpuApp);
       }
     }
     
     if (url != null) {
       CpuTimeCounter cpuUrl = (CpuTimeCounter)cpuPerUrl.get(url);
       if (cpuUrl == null) {
         cpuUrl = new CpuTimeCounter(th.getId());
         cpuPerUrl.put(url, cpuUrl);
       }
     }
     
     if ((app != null) && (url != null)) {
       Set<String> urls = (Set)urlPerApp.get(app);
       if (urls == null) {
         urls = new HashSet();
         urlPerApp.put(app, urls);
       }
       
       if (urls.size() > MAX_CACHE_SIZE) {
         String first = (String)urls.iterator().next();
         urls.remove(first);
       }
       urls.add(url);
     }
     
     appByThread.put(thid, app);
     urlByThread.put(thid, url);
   }
   
 
   public static void stopTracking(Thread th, Object session, String sessionId, String userId, String url, String app)
   {
     String thid = "" + th.getId();
     currentTimeByThread.remove(thid);
     currentCPUByThread.remove(thid);
     currentMemoryByThread.remove(thid);
     sessionByThread.remove(thid);
     appByThread.remove(thid);
     urlByThread.remove(thid);
     
     CpuTimeCounter cpuCounter = (CpuTimeCounter)cpuTracker.get();
     cpuCounter.end();
     
 
     if (sessionId != null) {
       CpuTimeCounter cpuSession = (CpuTimeCounter)cpuPerSession.get(sessionId);
       if (cpuSession != null) {
         cpuSession.aggreateValueOf(cpuCounter);
       }
     }
     if (userId != null) {
       CpuTimeCounter cpuUser = (CpuTimeCounter)cpuPerUser.get(userId);
       if (cpuUser != null) {
         cpuUser.aggreateValueOf(cpuCounter);
       }
     }
     
     if (app != null) {
       CpuTimeCounter cpuApp = (CpuTimeCounter)cpuPerApp.get(app);
       if (cpuApp != null) {
         cpuApp.aggreateValueOf(cpuCounter);
       }
     }
     
 
     if (url != null) {
       CpuTimeCounter cpuUrl = (CpuTimeCounter)cpuPerUrl.get(url);
       if (cpuUrl != null) {
         cpuUrl.aggreateValueOf(cpuCounter);
       }
     }
   }
   
   public static String getCurrentTimeByThread(String thid)
   {
     Long startTime = (Long)currentTimeByThread.get(thid);
     if (startTime == null) {
       return "";
     }
     
     return String.valueOf((System.currentTimeMillis() - startTime.longValue()) / 1000L);
   }
   
   public static ClassLoader getClassLoaderForApp(String appName) {
     if (appName == null) {
       return null;
     }
     return (ClassLoader)classLoaders.get(appName);
   }
   
   public static String getUserForThread(String threadId)
   {
     String sessionId = (String)sessionByThread.get(threadId);
     if (sessionId == null) {
       return "";
     }
     return emptyIfNull((String)userBySession.get(sessionId));
   }
   
   public static String getUserForSession(String sessionId)
   {
     return emptyIfNull((String)userBySession.get(sessionId));
   }
   
   public static String getSessionForThread(String threadId) {
     return emptyIfNull((String)sessionByThread.get(threadId));
   }
   
   public static String getAppByThread(String thid)
   {
     return (String)appByThread.get(thid);
   }
   
   public static String getURLByThread(String thid) {
     return (String)urlByThread.get(thid);
   }
   
   public static Map<String, CpuTimeCounter> getCpuForSessions()
   {
     return cpuPerSession;
   }
   
   public static String getCpuPerSession(String sessionId) {
     if (sessionId != null) {
       CpuTimeCounter cpuSession = (CpuTimeCounter)cpuPerSession.get(sessionId);
       if (cpuSession != null) {
         return cpuSession.getCpu();
       }
     }
     return "";
   }
   
   public static String getCpuPerUser(String userId) {
     if (userId != null) {
       CpuTimeCounter cpuUser = (CpuTimeCounter)cpuPerUser.get(userId);
       if (cpuUser != null) {
         return cpuUser.getCpu();
       }
     }
     return "";
   }
   
   public static String getAvgRtPerUser(String userId) {
     if (userId != null) {
       CpuTimeCounter cpuUser = (CpuTimeCounter)cpuPerUser.get(userId);
       if (cpuUser != null) {
         return cpuUser.getAvgRt();
       }
     }
     return "";
   }
   
   public static String getAvgRtPerURL(String url) {
     if (url != null) {
       CpuTimeCounter cpuURL = (CpuTimeCounter)cpuPerUrl.get(url);
       if (cpuURL != null) {
         return cpuURL.getAvgRt();
       }
     }
     return "";
   }
   
   public static String getHitPerUser(String userId)
   {
     if (userId != null) {
       CpuTimeCounter cpuUser = (CpuTimeCounter)cpuPerUser.get(userId);
       if (cpuUser != null) {
         return cpuUser.getHits();
       }
     }
     return "";
   }
   
   public static String getHitPerURL(String url)
   {
     if (url != null) {
       CpuTimeCounter cpuURL = (CpuTimeCounter)cpuPerUrl.get(url);
       if (cpuURL != null) {
         return cpuURL.getHits();
       }
     }
     return "";
   }
   
   public static String getCpuPerUrl(String url) {
     if (url != null) {
       CpuTimeCounter cpuUser = (CpuTimeCounter)cpuPerUrl.get(url);
       if (cpuUser != null) {
         return cpuUser.getCpu();
       }
     }
     return "";
   }
   
   public static String getCpuPerApp(String app) {
     if (app != null) {
       CpuTimeCounter cpuUser = (CpuTimeCounter)cpuPerApp.get(app);
       if (cpuUser != null) {
         return cpuUser.getCpu();
       }
     }
     return "";
   }
   
   public static String getAvgRtPerApp(String app) {
     if (app != null) {
       CpuTimeCounter cpuUser = (CpuTimeCounter)cpuPerApp.get(app);
       if (cpuUser != null) {
         return cpuUser.getAvgRt();
       }
     }
     return "";
   }
   
   public static String getHitsPerApp(String app)
   {
     if (app != null) {
       CpuTimeCounter cpuUser = (CpuTimeCounter)cpuPerApp.get(app);
       if (cpuUser != null) {
         return cpuUser.getHits();
       }
     }
     return "";
   }
   
   public static Map<String, Object> getAllSessionsMap() { return sessionBySessionId; }
   
   public static Collection<Object> getAllSessions()
   {
     List<Object> result = new ArrayList();
     synchronized (sessionBySessionId) {
       for (Object session : sessionBySessionId.values()) {
         result.add(session);
       }
     }
     return result;
   }
   
   public static Map<String, ClassLoader> getAllApplications() {
     return classLoaders;
   }
   
   public static Object getSessionById(String id) {
     return sessionBySessionId.get(id);
   }
   
   private static String emptyIfNull(String value) {
     if (value == null) {
       return "";
     }
     return value;
   }
   
 
 
 
 
 
 
   public static class CpuTimeCounter
   {
     private long ticks = 0L;
     private long lastRefresh = System.currentTimeMillis();
     private long closeTimeStamp = 0L;
     
     private long threadID = -1L;
     
 
     private AtomicInteger startCount = new AtomicInteger();
     
 
     private static java.lang.management.ThreadMXBean tmxb = ManagementFactory.getThreadMXBean();
     private long accElapsedTime;
     private long accCpuTime;
     private long startTime;
     
     public CpuTimeCounter(long threadID) { this.threadID = threadID; }
     
     public synchronized void aggreateValueOf(CpuTimeCounter cpuSession)
     {
       this.accElapsedTime += cpuSession.getLastElapsedTime();
       this.accCpuTime = ((this.accCpuTime + (long)cpuSession.getLastElapsedCPUTime()));
       this.ticks += 1L;
     }
     
     public synchronized CpuTimeCounter flushAndGetMetrics() {
       CpuTimeCounter result = new CpuTimeCounter(this.threadID);
       result.accElapsedTime = this.accElapsedTime;
       result.accCpuTime = this.accCpuTime;
       result.ticks = this.ticks;
       result.lastRefresh = this.lastRefresh;
       result.closeTimeStamp = System.currentTimeMillis();
       this.lastRefresh = result.closeTimeStamp;
       this.accElapsedTime = 0L;
       this.accCpuTime = 0L;
       this.ticks = 0L;
       return result;
     }
     
     public static long getThreadAllocatedBytes(long thid)
     {
       try
       {
         com.sun.management.ThreadMXBean sunTmxb = (com.sun.management.ThreadMXBean)tmxb;
         return sunTmxb.getThreadAllocatedBytes(thid);
       } catch (Exception e) {}
       return -1L;
     }
     
     private long startCpuTime;
     private long lastElapsedTime;
     private double lastElapsedCPUTime;
     public void start() {
       if (this.startCount.get() != 0) {
         this.startCount.incrementAndGet();
         return;
       }
       this.startCount.incrementAndGet();
       
       this.startTime = System.currentTimeMillis();
       this.startCpuTime = tmxb.getThreadCpuTime(this.threadID);
     }
     
     public String getCurrentUsedCPU() {
       long endTime = System.currentTimeMillis();
       long endCpuTime = tmxb.getThreadCpuTime(this.threadID);
       return "" + new DecimalFormat("###.##").format(100L * (endCpuTime - this.startCpuTime) / (1000000.0D * (endTime - this.startTime)));
     }
     
     public synchronized void end() {
       this.startCount.decrementAndGet();
       if (this.startCount.get() != 0) {
         return;
       }
       long endTime = System.currentTimeMillis();
       long endCpuTime = tmxb.getThreadCpuTime(this.threadID);
       
       this.lastElapsedTime = (endTime - this.startTime);
       this.lastElapsedCPUTime = ((endCpuTime - this.startCpuTime) / 1000000.0D);
       this.accElapsedTime += this.lastElapsedTime;
       this.accCpuTime = ((this.accCpuTime + (long)this.lastElapsedCPUTime));
     }
     
     public long getLastElapsedTime() {
       return this.lastElapsedTime;
     }
     
     public double getLastElapsedCPUTime() {
       return this.lastElapsedCPUTime;
     }
     
     public synchronized String getCpu() {
       if (this.accElapsedTime == 0L) {
         return "0";
       }
       
       long end = 0L;
       if (this.closeTimeStamp == 0L) {
         end = System.currentTimeMillis();
       } else {
         end = this.closeTimeStamp;
       }
       return "" + new DecimalFormat("###.##").format(100L * this.accCpuTime / (1.0D * (end - this.lastRefresh)));
     }
     
     public synchronized String getAvgRt() {
       if (this.accElapsedTime == 0L) {
         return "0";
       }
       return "" + new DecimalFormat("###.##").format(this.accElapsedTime / (1.0D * this.ticks));
     }
     
     public synchronized String getHits() {
       return "" + this.ticks;
     }
   }
   
   public static String getSessionIdFromObject(Object session) {
     synchronized (sessionBySessionId) {
       for (String sessionId : sessionBySessionId.keySet()) {
         if (session.equals(sessionBySessionId.get(sessionId))) {
           return sessionId;
         }
       }
     }
     return null;
   }
   
   public static ClassLoader getFirtAvailableClassLoader() {
     if (classLoaders.isEmpty()) {
       return null;
     }
     return (ClassLoader)classLoaders.values().iterator().next();
   }
 }


