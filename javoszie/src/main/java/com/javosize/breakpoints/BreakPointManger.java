 package com.javosize.breakpoints;
 
 import java.util.Collection;
 import java.util.Map;
 
 public class BreakPointManger
 {
   private static Map<String, BreakPoint> breakpoints = new java.util.Hashtable();
   
   public static void main(String[] args) throws Exception {
     BreakPoint bp = new BreakPoint();
     System.out.println("Listening on port bp: " + bp.getPort());
     Thread.sleep(20000L);
     bp.next();
     Thread.sleep(20000L);
     bp.finish();
   }
   
   public static void addBreakPoint(BreakPoint bp) {
     breakpoints.put(bp.getName(), bp);
   }
   
   public static BreakPoint getBreakPoint(String breakpointName) {
     return (BreakPoint)breakpoints.get(breakpointName);
   }
   
   public static boolean rmBreakPoint(String breakpointName) {
     BreakPoint bp = (BreakPoint)breakpoints.get(breakpointName);
     if (bp != null) {
       bp.finish();
       breakpoints.remove(breakpointName);
       return true;
     }
     return false;
   }
   
   public static Collection<BreakPoint> getBreakPoints() { return breakpoints.values(); }
 }


