 package com.javosize.actions;
 
 import com.javosize.print.TextReport;
 import java.lang.management.ThreadInfo;
 import java.lang.management.ThreadMXBean;
 
 public class FullThreadDumpAction extends Action
 {
   private static final long serialVersionUID = -2368200046848456461L;
   private transient ThreadMXBean tmxb;
   private static final String INDENT = "    ";
   
   private String dumpThreadDetails()
   {
     try
     {
       if (this.tmxb == null) {
         this.tmxb = java.lang.management.ManagementFactory.getThreadMXBean();
       }
       TextReport report = new TextReport();
       report.addSection("Full Thread Dump", new String[] { getFullThreadDump(this.tmxb) });
       
       return report.toString();
     }
     catch (Throwable th) {
       return "Remote ERROR getting full thread dump: " + th.toString();
     }
   }
   
   private String getFullThreadDump(ThreadMXBean tmxb) {
     ThreadDetailAction tda = new ThreadDetailAction();
     ThreadInfo[] tinfos = tmxb.dumpAllThreads(true, true);
     StringBuilder sb = new StringBuilder("");
     boolean first = true;
     for (ThreadInfo ti : tinfos) {
       if (ti != null) {
         if (first) {
           sb.append(tda.getDumpForThread(ti) + "\n");
           first = false;
         } else {
           sb.append("    " + tda.getDumpForThread(ti) + "\n");
         }
       }
     }
     return sb.toString();
   }
   
   public String execute()
   {
     return dumpThreadDetails();
   }
 }


