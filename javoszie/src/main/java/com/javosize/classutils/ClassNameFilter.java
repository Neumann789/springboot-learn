 package com.javosize.classutils;
 
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ClassNameFilter
 {
   public static boolean validateClassName(String className, boolean isRegex, String[] filters)
   {
     if ((filters == null) || (filters.length <= 1)) {
       return validateClassName(className, isRegex, (filters != null) && (filters.length == 1) ? filters[0] : null);
     }
     
     for (int i = 0; i < filters.length; i++) {
       if (validateClassName(className, isRegex, filters[i])) {
         return true;
       }
     }
     
     return false;
   }
   
 
 
 
 
 
 
 
 
 
   public static boolean validateClassName(String className, boolean isRegex, String filter)
   {
     if (className.contains("javosize")) {
       return false;
     }
     
     if ((filter == null) || (filter.equals(""))) {
       return true;
     }
     
     if (isRegex) {
       Pattern p = Pattern.compile(filter);
       Matcher m = p.matcher(className);
       return m.matches();
     }
     
     if ((filter.length() > 2) && (filter.startsWith("*")) && (filter.endsWith("*"))) {
       return className.contains(filter.substring(1, filter.length() - 1));
     }
     
     if ((filter.length() >= 2) && (filter.endsWith("*"))) {
       return className.startsWith(filter.substring(0, filter.length() - 1));
     }
     
     if ((filter.length() >= 2) && (filter.startsWith("*"))) {
       return className.endsWith(filter.substring(1, filter.length()));
     }
     
     if (filter.length() >= 1) {
       return className.equals(filter);
     }
     
     return true;
   }
 }


