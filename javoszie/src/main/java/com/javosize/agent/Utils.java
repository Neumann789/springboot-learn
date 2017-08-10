 package com.javosize.agent;
 
 import com.javosize.log.Log;
 import java.io.UnsupportedEncodingException;
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.net.URL;
 import java.net.URLDecoder;
 import java.nio.charset.Charset;
 import java.security.CodeSource;
 
 public class Utils
 {
   private static Log log = new Log(Utils.class.getName());
   
   public static boolean matchesIfNull(String value, String regexp)
   {
     if (value == null) {
       value = "null";
     }
     return value.matches(regexp);
   }
   
   public static String stackTraceToString(StackTraceElement[] st, int skip) {
     StringBuilder sb = new StringBuilder();
     int i = 0;
     for (StackTraceElement element : st)
       if (i < skip) {
         i++;
       }
       else {
         sb.append(element.toString());
         sb.append("\n");
         i++;
       }
     return sb.toString();
   }
   
   public static String getInterceptionFromBKName(String bkName) {
     return "BK_" + bkName;
   }
   
   public static int getCurrentVMPid()
   {
     try {
       java.lang.management.RuntimeMXBean runtime = java.lang.management.ManagementFactory.getRuntimeMXBean();
       Field jvm = runtime.getClass().getDeclaredField("jvm");
       jvm.setAccessible(true);
       Object mgmt = jvm.get(runtime);
       Method pid_method = mgmt.getClass().getDeclaredMethod("getProcessId", new Class[0]);
       pid_method.setAccessible(true);
       return ((Integer)pid_method.invoke(mgmt, new Object[0])).intValue();
     } catch (Throwable th) {}
     return -1;
   }
   
 
 
 
 
 
 
 
   public static String getFileFromURL(URL location)
   {
     return getFileFromURL(location, false);
   }
   
   public static String getFileFromURL(URL location, boolean checkEclipseJarInJar) {
     String fileName = "NULL";
     
     if (location != null) {
       fileName = location.toString();
       
       if ((checkEclipseJarInJar) && (fileName.startsWith("jar:rsrc")) && (fileName.endsWith("!/"))) {
         fileName = getEclipseJarInJarLoaderPath(fileName);
       } else {
         int idx = location.toString().indexOf('!');
         if (idx != -1) {
           try {
             fileName = URLDecoder.decode(location.toString().substring("jar:file:".length(), idx), Charset.defaultCharset().name());
           }
           catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
         }
       }
     }
     return fileName;
   }
   
 
 
 
 
 
 
 
   private static String getEclipseJarInJarLoaderPath(String fileName)
   {
     if ((fileName.startsWith("jar:rsrc")) && (fileName.endsWith("!/"))) {
       try {
         Class eclipseClass = Tools.getClassFromInstrumentation("org.eclipse.jdt.internal.jarinjarloader.JarRsrcLoader.class");
         if (eclipseClass != null) {
           CodeSource src = eclipseClass.getProtectionDomain().getCodeSource();
           if (src != null) {
             URL location = src.getLocation();
             String eclipseJar = getFileFromURL(location, false);
             if (eclipseJar.startsWith("file:")) {
               fileName = eclipseJar + "!" + fileName.substring("jar:rsrc:".length(), fileName.length() - 2);
             }
           }
         }
       } catch (Throwable th) {
         log.debug("Error obtaining Jar file name for class org.eclipse.jdt.internal.jarinjarloader.JarRsrcLoader.class: " + th, th);
       }
     }
     return fileName;
   }
   
 
 
 
 
 
 
 
   public static String getPackageName(String classFQName)
   {
     if (classFQName == null) {
       return null;
     }
     classFQName = classFQName.trim();
     if (classFQName.endsWith(".class")) {
       classFQName = classFQName.substring(0, classFQName.indexOf(".class"));
     }
     if (classFQName.contains(".")) {
       return classFQName.substring(0, classFQName.lastIndexOf("."));
     }
     return classFQName;
   }
 }


