 package com.javosize;
 
 import java.util.concurrent.atomic.AtomicInteger;
 
 public class MethodPerformanceHolder implements Comparable<MethodPerformanceHolder> {
   private AtomicInteger counter;
   private String method;
   private int depth = 0;
   private StackTraceElement[] stack;
   
   public int getDepth() { return this.depth; }
   
   public void setDepth(int depth)
   {
     this.depth = depth;
   }
   
   public String getMethod() {
     return this.method;
   }
   
   public void setMethod(String method) {
     this.method = method;
   }
   
   public MethodPerformanceHolder(String method, StackTraceElement[] stack)
   {
     this.method = method;
     this.counter = new AtomicInteger(0);
     this.stack = stack;
   }
   
   public AtomicInteger getCounter() {
     return this.counter;
   }
   
   public void setCounter(AtomicInteger counter) { this.counter = counter; }
   
   public StackTraceElement[] getStack() {
     return this.stack;
   }
   
   public void setStack(StackTraceElement[] stack) { this.stack = stack; }
   
 
 
   public int compareTo(MethodPerformanceHolder p2)
   {
     return (int)(getCounter().get() * Math.pow(2.0D, getDepth()) - p2.getCounter().get() * Math.pow(2.0D, getDepth()));
   }
 }


