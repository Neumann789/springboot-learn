/* StaticVariableSize - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.agent.memory;

public class StaticVariableSize implements Comparable
{
    private String className;
    private String variableName;
    private String type;
    private long size;
    
    public StaticVariableSize(String className, String variableName,
			      String type, long size) {
	this.className = className;
	this.variableName = variableName;
	this.type = type;
	this.size = size;
    }
    
    public String getClassName() {
	return className;
    }
    
    public void setClassName(String className) {
	this.className = className;
    }
    
    public String getVariableName() {
	return variableName;
    }
    
    public void setVariableName(String variableName) {
	this.variableName = variableName;
    }
    
    public String getType() {
	return type;
    }
    
    public void setType(String type) {
	this.type = type;
    }
    
    public long getSize() {
	return size;
    }
    
    public void setSize(long size) {
	this.size = size;
    }
    
    public int compareTo(StaticVariableSize o) {
	if (o.getSize() < size)
	    return 1;
	if (o.getSize() > size)
	    return -1;
	return 0;
    }
    
    public String toString() {
	return new StringBuilder().append("").append(getClassName()).append
		   (".").append
		   (getVariableName()).append
		   ("[").append
		   (getType()).append
		   ("]: ").append
		   (getSize()).append
		   ("bytes\n").toString();
    }
    
    public volatile int compareTo(Object object) {
	return compareTo((StaticVariableSize) object);
    }
}
