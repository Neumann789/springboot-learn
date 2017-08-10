/* BreakPoint - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.breakpoints;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.javosize.actions.RmInterceptionAction;
import com.javosize.agent.Interception;
import com.javosize.remote.Controller;

public class BreakPoint
{
    private String name;
    private AtomicInteger waiters = new AtomicInteger(0);
    private AtomicInteger executed = new AtomicInteger(0);
    private AtomicInteger executing = new AtomicInteger(0);
    private String sessionRegexp;
    private String userRegexp;
    private String urlRegexp;
    private String threadRegexp;
    private String classNameRegexp;
    private String methodRegexp;
    private Interception interception;
    private BreakPointServer server;
    
    public String getClassNameRegexp() {
	return classNameRegexp;
    }
    
    public void setClassNameRegexp(String classNameRegexp) {
	this.classNameRegexp = classNameRegexp;
    }
    
    public String getMethodRegexp() {
	return methodRegexp;
    }
    
    public void setMethodRegexp(String methodRegexp) {
	this.methodRegexp = methodRegexp;
    }
    
    public BreakPoint() throws IOException {
	server = new BreakPointServer(this);
	server.start();
    }
    
    public void next() {
	server.next();
    }
    
    public void finish() {
	server.finish();
	RmInterceptionAction ria
	    = new RmInterceptionAction(interception.getId());
	Controller.getInstance().execute(ria);
    }
    
    public int getPort() {
	return server.getPort();
    }
    
    public String getName() {
	return name;
    }
    
    public void setName(String name) {
	this.name = name;
    }
    
    public int getWaiters() {
	return waiters.get();
    }
    
    public int getExecuted() {
	return executed.get();
    }
    
    public String getSessionRegexp() {
	return sessionRegexp;
    }
    
    public void setSessionRegexp(String sessionRegexp) {
	this.sessionRegexp = sessionRegexp;
    }
    
    public String getUserRegexp() {
	return userRegexp;
    }
    
    public void setUserRegexp(String userRegexp) {
	this.userRegexp = userRegexp;
    }
    
    public String getUrlRegexp() {
	return urlRegexp;
    }
    
    public void setUrlRegexp(String urlRegexp) {
	this.urlRegexp = urlRegexp;
    }
    
    public String getThreadRegexp() {
	return threadRegexp;
    }
    
    public void setThreadRegexp(String threadRegexp) {
	this.threadRegexp = threadRegexp;
    }
    
    public void addWaiter() {
	waiters.incrementAndGet();
    }
    
    public void addExecuting() {
	waiters.decrementAndGet();
	executing.incrementAndGet();
    }
    
    public void addExecuted() {
	executing.decrementAndGet();
	executed.incrementAndGet();
    }
    
    public int getExecuting() {
	return executing.get();
    }
    
    public void setInterception(Interception interception) {
	this.interception = interception;
    }
}
