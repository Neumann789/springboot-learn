/* BreakPointManger - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.breakpoints;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

public class BreakPointManger
{
    private static Map breakpoints = new Hashtable();
    
    public static void main(String[] args) throws Exception {
	BreakPoint bp = new BreakPoint();
	System.out.println(new StringBuilder().append
			       ("Listening on port bp: ").append
			       (bp.getPort()).toString());
	Thread.sleep(20000L);
	bp.next();
	Thread.sleep(20000L);
	bp.finish();
    }
    
    public static void addBreakPoint(BreakPoint bp) {
	breakpoints.put(bp.getName(), bp);
    }
    
    public static BreakPoint getBreakPoint(String breakpointName) {
	return (BreakPoint) breakpoints.get(breakpointName);
    }
    
    public static boolean rmBreakPoint(String breakpointName) {
	BreakPoint bp = (BreakPoint) breakpoints.get(breakpointName);
	if (bp != null) {
	    bp.finish();
	    breakpoints.remove(breakpointName);
	    return true;
	}
	return false;
    }
    
    public static Collection getBreakPoints() {
	return breakpoints.values();
    }
}
