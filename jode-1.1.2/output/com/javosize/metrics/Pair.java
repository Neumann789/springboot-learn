/* Pair - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.metrics;

public class Pair
{
    private long timestamp;
    private Object object;
    
    public Pair(long timestamp, Object object) {
	this.timestamp = timestamp;
	this.object = object;
    }
    
    public long getTimestamp() {
	return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
	this.timestamp = timestamp;
    }
    
    public Object getObject() {
	return object;
    }
    
    public void setObject(Object object) {
	this.object = object;
    }
}
