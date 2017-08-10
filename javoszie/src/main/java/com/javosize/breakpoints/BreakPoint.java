 package com.javosize.breakpoints;
 
 import com.javosize.actions.RmInterceptionAction;
 import com.javosize.agent.Interception;
 import com.javosize.remote.Controller;
 import java.io.IOException;
 import java.util.concurrent.atomic.AtomicInteger;
 
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
   
   public String getClassNameRegexp() { return this.classNameRegexp; }
   
   public void setClassNameRegexp(String classNameRegexp)
   {
     this.classNameRegexp = classNameRegexp;
   }
   
   public String getMethodRegexp() {
     return this.methodRegexp;
   }
   
   public void setMethodRegexp(String methodRegexp) {
     this.methodRegexp = methodRegexp;
   }
   
   public BreakPoint()
     throws IOException
   {
     this.server = new BreakPointServer(this);
     this.server.start();
   }
   
   public void next() {
     this.server.next();
   }
   
   public void finish() {
     this.server.finish();
     
     RmInterceptionAction ria = new RmInterceptionAction(this.interception.getId());
     Controller.getInstance().execute(ria);
   }
   
   public int getPort() {
     return this.server.getPort();
   }
   
   public String getName() { return this.name; }
   
   public void setName(String name)
   {
     this.name = name;
   }
   
   public int getWaiters() {
     return this.waiters.get();
   }
   
   public int getExecuted() {
     return this.executed.get();
   }
   
   public String getSessionRegexp() {
     return this.sessionRegexp;
   }
   
   public void setSessionRegexp(String sessionRegexp) {
     this.sessionRegexp = sessionRegexp;
   }
   
   public String getUserRegexp() {
     return this.userRegexp;
   }
   
   public void setUserRegexp(String userRegexp) {
     this.userRegexp = userRegexp;
   }
   
   public String getUrlRegexp() {
     return this.urlRegexp;
   }
   
   public void setUrlRegexp(String urlRegexp) {
     this.urlRegexp = urlRegexp;
   }
   
   public String getThreadRegexp() {
     return this.threadRegexp;
   }
   
   public void setThreadRegexp(String threadRegexp) {
     this.threadRegexp = threadRegexp;
   }
   
   public void addWaiter() {
     this.waiters.incrementAndGet();
   }
   
   public void addExecuting() {
     this.waiters.decrementAndGet();
     this.executing.incrementAndGet();
   }
   
   public void addExecuted() {
     this.executing.decrementAndGet();
     this.executed.incrementAndGet();
   }
   
   public int getExecuting() {
     return this.executing.get();
   }
   
   public void setInterception(Interception interception) {
     this.interception = interception;
   }
 }


