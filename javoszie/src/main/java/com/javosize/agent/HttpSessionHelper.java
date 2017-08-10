 package com.javosize.agent;
 
 import java.util.Date;
 import java.util.Enumeration;
 import javax.servlet.http.HttpSession;
 
 public class HttpSessionHelper
 {
   public static String getActiveDate(Object session)
   {
     HttpSession casted = (HttpSession)session;
     return new Date(casted.getCreationTime()).toString();
   }
   
   public static String getLastAccessedTime(Object session) {
     HttpSession casted = (HttpSession)session;
     return new Date(casted.getLastAccessedTime()).toString();
   }
   
   public static Enumeration<String> getAttributes(Object session) {
     HttpSession casted = (HttpSession)session;
     return casted.getAttributeNames();
   }
   
   public static String getAttribute(Object session, String name) {
     HttpSession casted = (HttpSession)session;
     return casted.getAttribute(name).toString();
   }
   
   public static String getNumberOfElements(Object session)
   {
     HttpSession casted = (HttpSession)session;
     int counter = 0;
     Enumeration<String> enu = casted.getAttributeNames();
     while (enu.hasMoreElements()) {
       enu.nextElement();
       counter++;
     }
     return "" + counter;
   }
 }


