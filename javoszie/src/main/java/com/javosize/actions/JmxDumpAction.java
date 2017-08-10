 package com.javosize.actions;
 
 import java.lang.management.ManagementFactory;
 import javax.management.MBeanServer;
 import javax.management.ObjectName;
 
 public class JmxDumpAction extends Action
 {
   private static final long serialVersionUID = -2368200046848456461L;
   
   private String dumpMbeans()
   {
     try
     {
       MBeanServer server = ManagementFactory.getPlatformMBeanServer();
       java.util.Set<ObjectName> mbeans = server.queryNames(null, null);
       StringBuffer sb = new StringBuffer();
       for (ObjectName mbean : mbeans) {
         sb.append(mbean);
         sb.append("\n");
       }
       return sb.toString();
     } catch (Throwable th) {
       return "Remote ERROR listing the MBeans: " + th.toString();
     }
   }
   
   public String execute()
   {
     return dumpMbeans();
   }
 }


