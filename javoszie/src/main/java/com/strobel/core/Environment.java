 package com.strobel.core;
 
 import com.strobel.util.ContractUtils;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Environment
 {
   private static final Logger logger = Logger.getLogger(Environment.class.getName());
   
   private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$([a-zA-Z0-9_]+)", 4);
   
   private static final String OS_NAME = System.getProperty("os.name");
   private static final String OS_NAME_LOWER = OS_NAME.toLowerCase();
   private static final String OS_ARCH = System.getProperty("os.arch");
   private static final String ARCH_DATA_MODEL = System.getProperty("sun.arch.data.model");
   
 
 
   private Environment()
   {
     throw ContractUtils.unreachable();
   }
   
   public static boolean isWindows() {
     return OS_NAME_LOWER.startsWith("windows");
   }
   
   public static boolean isOS2() {
     return (OS_NAME_LOWER.startsWith("os/2")) || (OS_NAME_LOWER.startsWith("os2"));
   }
   
   public static boolean isMac()
   {
     return OS_NAME_LOWER.startsWith("mac");
   }
   
   public static boolean isLinux() {
     return OS_NAME_LOWER.startsWith("linux");
   }
   
   public static boolean isUnix() {
     return (!isWindows()) && (!isOS2());
   }
   
   public static boolean isFileSystemCaseSensitive() {
     return (isUnix()) && (!isMac());
   }
   
   public static boolean is32Bit() {
     return (ARCH_DATA_MODEL == null) || (ARCH_DATA_MODEL.equals("32"));
   }
   
   public static boolean is64Bit()
   {
     return !is32Bit();
   }
   
   public static boolean isAmd64() {
     return "amd64".equals(OS_ARCH);
   }
   
   public static boolean isMacX64() {
     return (isMac()) && ("x86_64".equals(OS_ARCH));
   }
   
 
 
 
 
 
   public static String getVariable(String variable)
   {
     if (variable == null) {
       return "";
     }
     
     String expanded = System.getenv(variable);
     return expanded != null ? expanded : "";
   }
   
 
 
 
 
 
 
 
   public static String expandVariables(String s)
   {
     return expandVariables(s, true);
   }
   
 
 
 
 
 
 
 
   public static String expandVariables(String s, boolean recursive)
   {
     Matcher variableMatcher = VARIABLE_PATTERN.matcher(s);
     
     StringBuffer expanded = null;
     String variable = null;
     try
     {
       while (variableMatcher.find()) {
         int matches = variableMatcher.groupCount();
         
 
         for (int i = 1; i <= matches; i++) {
           variable = variableMatcher.group(i);
           
           if (expanded == null) {
             expanded = new StringBuffer();
           }
           
           String variableValue = getVariable(variable);
           
           variableMatcher.appendReplacement(expanded, (recursive ? expandVariables(variableValue, true) : variableValue).replace("\\", "\\\\"));
         }
       }
       
 
 
 
       if (expanded != null) {
         variableMatcher.appendTail(expanded);
       }
     }
     catch (Throwable t) {
       logger.log(Level.WARNING, String.format("Unable to expand the variable '%s', returning original value: %s", new Object[] { variable, s }), t);
       
 
 
 
 
 
 
 
       return s;
     }
     
     if (expanded != null) {
       return expanded.toString();
     }
     
     return s;
   }
   
   public static int getProcessorCount() {
     return Runtime.getRuntime().availableProcessors();
   }
   
   public static boolean isSingleProcessor() {
     return getProcessorCount() == 1;
   }
 }


