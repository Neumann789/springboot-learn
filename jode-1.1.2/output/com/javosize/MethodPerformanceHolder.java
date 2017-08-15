/* MethodPerformanceHolder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize;
import java.util.concurrent.atomic.AtomicInteger;

public class MethodPerformanceHolder implements Comparable
{
    private AtomicInteger counter;
    private String method;
    private int depth = 0;
    private StackTraceElement[] stack;
    
    public int getDepth() {
	return depth;
    }
    
    public void setDepth(int depth) {
	this.depth = depth;
    }
    
    public String getMethod() {
	return method;
    }
    
    public void setMethod(String method) {
	this.method = method;
    }
    
    public MethodPerformanceHolder(String method, StackTraceElement[] stack) {
	this.method = method;
	counter = new AtomicInteger(0);
	this.stack = stack;
    }
    
    public AtomicInteger getCounter() {
	return counter;
    }
    
    public void setCounter(AtomicInteger counter) {
	this.counter = counter;
    }
    
    public StackTraceElement[] getStack() {
	return stack;
    }
    
    public void setStack(StackTraceElement[] stack) {
	this.stack = stack;
    }
    
    public int compareTo(MethodPerformanceHolder p2) {
	return (int) (((double) getCounter().get()
		       * Math.pow(2.0, (double) getDepth()))
		      - ((double) p2.getCounter().get()
			 * Math.pow(2.0, (double) getDepth())));
    }
    
    public volatile int compareTo(Object object) {
	return compareTo((MethodPerformanceHolder) object);
    }
}
