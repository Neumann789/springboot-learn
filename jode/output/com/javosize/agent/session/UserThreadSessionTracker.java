/* UserThreadSessionTracker - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.agent.session;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.javosize.agent.CacheManager;
import com.javosize.agent.Utils;

public class UserThreadSessionTracker
{
    private static int MAX_CACHE_SIZE = 200;
    private static Map userBySession
	= CacheManager.getCacheLRU(MAX_CACHE_SIZE, true);
    private static Map sessionByThread = new ConcurrentHashMap();
    private static Map appByThread = new ConcurrentHashMap();
    private static Map urlByThread = new ConcurrentHashMap();
    private static Map currentTimeByThread = new ConcurrentHashMap();
    private static Map currentCPUByThread = new ConcurrentHashMap();
    private static Map currentMemoryByThread = new ConcurrentHashMap();
    private static Map cpuPerSession
	= CacheManager.getCacheLRU(MAX_CACHE_SIZE, true);
    private static Map cpuPerUser
	= CacheManager.getCacheLRU(MAX_CACHE_SIZE, true);
    private static Map cpuPerApp
	= CacheManager.getCacheLRU(MAX_CACHE_SIZE, true);
    private static Map urlPerApp = new ConcurrentHashMap();
    private static Map cpuPerUrl
	= CacheManager.getCacheLRU(MAX_CACHE_SIZE, true);
    private static Map classLoaders
	= CacheManager.getCacheLRU(MAX_CACHE_SIZE, true);
    private static Map breakpointsParams = new ConcurrentHashMap();
    private static ThreadLocal simpleMethodTracker = new ThreadLocal();
    private static ThreadLocal cpuTracker = new ThreadLocal();
    private static Map sessionBySessionId
	= Collections.synchronizedMap(new WeakHashMap());
    private static Map breakpointsStack = new ConcurrentHashMap();
    
    public static class CpuTimeCounter
    {
	private long accElapsedTime;
	private long accCpuTime;
	private long startTime;
	private long startCpuTime;
	private long lastElapsedTime;
	private double lastElapsedCPUTime;
	private long ticks = 0L;
	private long lastRefresh = System.currentTimeMillis();
	private long closeTimeStamp = 0L;
	private long threadID = -1L;
	private static ThreadMXBean tmxb = null;
	private AtomicInteger startCount = new AtomicInteger();
	
	public CpuTimeCounter(long threadID) {
	    this.threadID = threadID;
	}
	
	public synchronized void aggreateValueOf(CpuTimeCounter cpuSession) {
	    accElapsedTime += cpuSession.getLastElapsedTime();
	    accCpuTime += cpuSession.getLastElapsedCPUTime();
	    ticks++;
	}
	
	public synchronized CpuTimeCounter flushAndGetMetrics() {
	    CpuTimeCounter result = new CpuTimeCounter(threadID);
	    result.accElapsedTime = accElapsedTime;
	    result.accCpuTime = accCpuTime;
	    result.ticks = ticks;
	    result.lastRefresh = lastRefresh;
	    result.closeTimeStamp = System.currentTimeMillis();
	    lastRefresh = result.closeTimeStamp;
	    accElapsedTime = 0L;
	    accCpuTime = 0L;
	    ticks = 0L;
	    return result;
	}
	
	public static long getThreadAllocatedBytes(long thid) {
	    long l;
	    try {
		com.sun.management.ThreadMXBean sunTmxb
		    = (com.sun.management.ThreadMXBean) tmxb;
		l = sunTmxb.getThreadAllocatedBytes(thid);
	    } catch (Exception e) {
		return -1L;
	    }
	    return l;
	}
	
	public void start() {
	    if (startCount.get() != 0)
		startCount.incrementAndGet();
	    else {
		startCount.incrementAndGet();
		startTime = System.currentTimeMillis();
		startCpuTime = tmxb.getThreadCpuTime(threadID);
	    }
	}
	
	public String getCurrentUsedCPU() {
	    long endTime = System.currentTimeMillis();
	    long endCpuTime = tmxb.getThreadCpuTime(threadID);
	    return new StringBuilder().append("").append
		       (new DecimalFormat("###.##").format
			((double) (100L * (endCpuTime - startCpuTime))
			 / (1000000.0 * (double) (endTime - startTime))))
		       .toString();
	}
	
	public synchronized void end() {
	    startCount.decrementAndGet();
	    if (startCount.get() == 0) {
		long endTime = System.currentTimeMillis();
		long endCpuTime = tmxb.getThreadCpuTime(threadID);
		lastElapsedTime = endTime - startTime;
		lastElapsedCPUTime
		    = (double) (endCpuTime - startCpuTime) / 1000000.0;
		accElapsedTime += lastElapsedTime;
		accCpuTime += lastElapsedCPUTime;
	    }
	}
	
	public long getLastElapsedTime() {
	    return lastElapsedTime;
	}
	
	public double getLastElapsedCPUTime() {
	    return lastElapsedCPUTime;
	}
	
	public synchronized String getCpu() {
	    if (accElapsedTime == 0L)
		return "0";
	    long end = 0L;
	    if (closeTimeStamp == 0L)
		end = System.currentTimeMillis();
	    else
		end = closeTimeStamp;
	    return new StringBuilder().append("").append
		       (new DecimalFormat("###.##").format
			((double) (100L * accCpuTime)
			 / (1.0 * (double) (end - lastRefresh))))
		       .toString();
	}
	
	public synchronized String getAvgRt() {
	    if (accElapsedTime == 0L)
		return "0";
	    return new StringBuilder().append("").append
		       (new DecimalFormat("###.##").format
			((double) accElapsedTime / (1.0 * (double) ticks)))
		       .toString();
	}
	
	public synchronized String getHits() {
	    return new StringBuilder().append("").append(ticks).toString();
	}
	
	static {
	    tmxb = ManagementFactory.getThreadMXBean();
	}
    }
    
    public static void simpleMethodStart() {
	if (simpleMethodTracker.get() == null) {
	    Long startTime = Long.valueOf(System.currentTimeMillis());
	    simpleMethodTracker.set(startTime);
	}
    }
    
    public static void purgeSession(String id) {
	if (id != null)
	    sessionBySessionId.remove(id);
    }
    
    public static long simpleMethodEnd() {
	long start = ((Long) simpleMethodTracker.get()).longValue();
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
	if (currentMemoryByThread.containsKey(thid))
	    return new StringBuilder().append("").append
		       ((CpuTimeCounter.getThreadAllocatedBytes
			 (new Long(thid).longValue()))
			- ((Long) currentMemoryByThread.get(thid)).longValue())
		       .toString();
	return "";
    }
    
    public static String getCurrentCPUByThread(String thid) {
	if (currentCPUByThread.containsKey(thid))
	    return ((CpuTimeCounter) currentCPUByThread.get(thid))
		       .getCurrentUsedCPU();
	return "";
    }
    
    public static void removeBreakpointParams(String name) {
	breakpointsParams.remove(name);
    }
    
    public static Object[] getBreakpointParams(String name) {
	return (Object[]) breakpointsParams.get(name);
    }
    
    public static String getBreakpointStack(String name) {
	return (String) breakpointsStack.get(name);
    }
    
    public static String getBreakpointParamsSerial(String name) {
	Object[] params = (Object[]) breakpointsParams.get(name);
	StringBuffer sb = new StringBuffer();
	int counter = 0;
	Object[] objects = params;
	int i = objects.length;
	for (int i_0_ = 0; i_0_ < i; i_0_++) {
	    Object object = objects[i_0_];
	    if (counter == 0)
		sb.append("This: ");
	    else
		sb.append(new StringBuilder().append("{").append
			      (counter - 1).append
			      ("}: ").toString());
	    sb.append(String.valueOf(object));
	    sb.append("\n");
	    counter++;
	}
	return sb.toString();
    }
    
    public static String getCurrentSession() {
	String thid = getCurrentThreadId();
	return (String) sessionByThread.get(thid);
    }
    
    public static String getCurrentUser() {
	String sid = getCurrentSession();
	if (sid == null)
	    return null;
	return (String) userBySession.get(sid);
    }
    
    public static String getCurrentAppName() {
	String thid = getCurrentThreadId();
	return (String) appByThread.get(thid);
    }
    
    public static Set getURLsPerApp(String app) {
	return (Set) urlPerApp.get(app);
    }
    
    public static void startTracking(Thread th, Object session,
				     String sessionId, String userId,
				     String url, String app, Object servlet) {
	CpuTimeCounter cpuCounter = new CpuTimeCounter(th.getId());
	cpuTracker.set(cpuCounter);
	cpuCounter.start();
	String thid = String.valueOf(th.getId());
	currentTimeByThread.put(thid,
				Long.valueOf(System.currentTimeMillis()));
	currentCPUByThread.put(thid, cpuCounter);
	Map map = currentMemoryByThread;
	String string = thid;
	if (cpuCounter != null) {
	    /* empty */
	}
	map.put(string,
		Long.valueOf(CpuTimeCounter
				 .getThreadAllocatedBytes(th.getId())));
	if (sessionId != null) {
	    sessionBySessionId.put(sessionId, session);
	    sessionByThread.put(thid, sessionId);
	    userBySession.put(sessionId, userId);
	    CpuTimeCounter cpuSession
		= (CpuTimeCounter) cpuPerSession.get(sessionId);
	    if (cpuSession == null) {
		cpuSession = new CpuTimeCounter(th.getId());
		cpuPerSession.put(sessionId, cpuSession);
	    }
	    if (userId != null) {
		CpuTimeCounter cpuUser
		    = (CpuTimeCounter) cpuPerUser.get(userId);
		if (cpuUser == null) {
		    cpuUser = new CpuTimeCounter(th.getId());
		    cpuPerUser.put(userId, cpuUser);
		}
	    }
	}
	if (app != null) {
	    ClassLoader cl = servlet.getClass().getClassLoader();
	    classLoaders.put(app, cl);
	    CpuTimeCounter cpuApp = (CpuTimeCounter) cpuPerApp.get(app);
	    if (cpuApp == null) {
		cpuApp = new CpuTimeCounter(th.getId());
		cpuPerApp.put(app, cpuApp);
	    }
	}
	if (url != null) {
	    CpuTimeCounter cpuUrl = (CpuTimeCounter) cpuPerUrl.get(url);
	    if (cpuUrl == null) {
		cpuUrl = new CpuTimeCounter(th.getId());
		cpuPerUrl.put(url, cpuUrl);
	    }
	}
	if (app != null && url != null) {
	    Set urls = (Set) urlPerApp.get(app);
	    if (urls == null) {
		urls = new HashSet();
		urlPerApp.put(app, urls);
	    }
	    if (urls.size() > MAX_CACHE_SIZE) {
		String first = (String) urls.iterator().next();
		urls.remove(first);
	    }
	    urls.add(url);
	}
	appByThread.put(thid, app);
	urlByThread.put(thid, url);
    }
    
    public static void stopTracking(Thread th, Object session,
				    String sessionId, String userId,
				    String url, String app) {
	String thid
	    = new StringBuilder().append("").append(th.getId()).toString();
	currentTimeByThread.remove(thid);
	currentCPUByThread.remove(thid);
	currentMemoryByThread.remove(thid);
	sessionByThread.remove(thid);
	appByThread.remove(thid);
	urlByThread.remove(thid);
	CpuTimeCounter cpuCounter = (CpuTimeCounter) cpuTracker.get();
	cpuCounter.end();
	if (sessionId != null) {
	    CpuTimeCounter cpuSession
		= (CpuTimeCounter) cpuPerSession.get(sessionId);
	    if (cpuSession != null)
		cpuSession.aggreateValueOf(cpuCounter);
	}
	if (userId != null) {
	    CpuTimeCounter cpuUser = (CpuTimeCounter) cpuPerUser.get(userId);
	    if (cpuUser != null)
		cpuUser.aggreateValueOf(cpuCounter);
	}
	if (app != null) {
	    CpuTimeCounter cpuApp = (CpuTimeCounter) cpuPerApp.get(app);
	    if (cpuApp != null)
		cpuApp.aggreateValueOf(cpuCounter);
	}
	if (url != null) {
	    CpuTimeCounter cpuUrl = (CpuTimeCounter) cpuPerUrl.get(url);
	    if (cpuUrl != null)
		cpuUrl.aggreateValueOf(cpuCounter);
	}
    }
    
    public static String getCurrentTimeByThread(String thid) {
	Long startTime = (Long) currentTimeByThread.get(thid);
	if (startTime == null)
	    return "";
	return String.valueOf((System.currentTimeMillis()
			       - startTime.longValue()) / 1000L);
    }
    
    public static ClassLoader getClassLoaderForApp(String appName) {
	if (appName == null)
	    return null;
	return (ClassLoader) classLoaders.get(appName);
    }
    
    public static String getUserForThread(String threadId) {
	String sessionId = (String) sessionByThread.get(threadId);
	if (sessionId == null)
	    return "";
	return emptyIfNull((String) userBySession.get(sessionId));
    }
    
    public static String getUserForSession(String sessionId) {
	return emptyIfNull((String) userBySession.get(sessionId));
    }
    
    public static String getSessionForThread(String threadId) {
	return emptyIfNull((String) sessionByThread.get(threadId));
    }
    
    public static String getAppByThread(String thid) {
	return (String) appByThread.get(thid);
    }
    
    public static String getURLByThread(String thid) {
	return (String) urlByThread.get(thid);
    }
    
    public static Map getCpuForSessions() {
	return cpuPerSession;
    }
    
    public static String getCpuPerSession(String sessionId) {
	if (sessionId != null) {
	    CpuTimeCounter cpuSession
		= (CpuTimeCounter) cpuPerSession.get(sessionId);
	    if (cpuSession != null)
		return cpuSession.getCpu();
	}
	return "";
    }
    
    public static String getCpuPerUser(String userId) {
	if (userId != null) {
	    CpuTimeCounter cpuUser = (CpuTimeCounter) cpuPerUser.get(userId);
	    if (cpuUser != null)
		return cpuUser.getCpu();
	}
	return "";
    }
    
    public static String getAvgRtPerUser(String userId) {
	if (userId != null) {
	    CpuTimeCounter cpuUser = (CpuTimeCounter) cpuPerUser.get(userId);
	    if (cpuUser != null)
		return cpuUser.getAvgRt();
	}
	return "";
    }
    
    public static String getAvgRtPerURL(String url) {
	if (url != null) {
	    CpuTimeCounter cpuURL = (CpuTimeCounter) cpuPerUrl.get(url);
	    if (cpuURL != null)
		return cpuURL.getAvgRt();
	}
	return "";
    }
    
    public static String getHitPerUser(String userId) {
	if (userId != null) {
	    CpuTimeCounter cpuUser = (CpuTimeCounter) cpuPerUser.get(userId);
	    if (cpuUser != null)
		return cpuUser.getHits();
	}
	return "";
    }
    
    public static String getHitPerURL(String url) {
	if (url != null) {
	    CpuTimeCounter cpuURL = (CpuTimeCounter) cpuPerUrl.get(url);
	    if (cpuURL != null)
		return cpuURL.getHits();
	}
	return "";
    }
    
    public static String getCpuPerUrl(String url) {
	if (url != null) {
	    CpuTimeCounter cpuUser = (CpuTimeCounter) cpuPerUrl.get(url);
	    if (cpuUser != null)
		return cpuUser.getCpu();
	}
	return "";
    }
    
    public static String getCpuPerApp(String app) {
	if (app != null) {
	    CpuTimeCounter cpuUser = (CpuTimeCounter) cpuPerApp.get(app);
	    if (cpuUser != null)
		return cpuUser.getCpu();
	}
	return "";
    }
    
    public static String getAvgRtPerApp(String app) {
	if (app != null) {
	    CpuTimeCounter cpuUser = (CpuTimeCounter) cpuPerApp.get(app);
	    if (cpuUser != null)
		return cpuUser.getAvgRt();
	}
	return "";
    }
    
    public static String getHitsPerApp(String app) {
	if (app != null) {
	    CpuTimeCounter cpuUser = (CpuTimeCounter) cpuPerApp.get(app);
	    if (cpuUser != null)
		return cpuUser.getHits();
	}
	return "";
    }
    
    public static Map getAllSessionsMap() {
	return sessionBySessionId;
    }
    
    public static Collection getAllSessions() {
	java.util.List result = new ArrayList();
	Map map;
	MONITORENTER (map = sessionBySessionId);
	MISSING MONITORENTER
	synchronized (map) {
	    Iterator iterator = sessionBySessionId.values().iterator();
	    while (iterator.hasNext()) {
		Object session = iterator.next();
		result.add(session);
	    }
	}
	return result;
    }
    
    public static Map getAllApplications() {
	return classLoaders;
    }
    
    public static Object getSessionById(String id) {
	return sessionBySessionId.get(id);
    }
    
    private static String emptyIfNull(String value) {
	if (value == null)
	    return "";
	return value;
    }
    
    public static String getSessionIdFromObject(Object session) {
	object = object_2_;
	break while_5_;
    }
    
    public static ClassLoader getFirtAvailableClassLoader() {
	if (classLoaders.isEmpty())
	    return null;
	return (ClassLoader) classLoaders.values().iterator().next();
    }
}
