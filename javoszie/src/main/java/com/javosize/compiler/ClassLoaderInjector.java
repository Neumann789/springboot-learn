 package com.javosize.compiler;
 
 import java.io.ByteArrayOutputStream;
 import java.io.InputStream;
 import java.lang.reflect.Method;
 import java.util.List;
 import java.util.Map;
 import java.util.Map.Entry;
 
 public class ClassLoaderInjector
 {
   public static Class injectClassInClassLoader(String classname, ClassLoader targetClassLoader) throws Throwable
   {
     String classNamePath = classname.replace(".", "/") + ".class";
     
 
     InputStream is = targetClassLoader.getResourceAsStream(classNamePath);
     if (is == null) {
       return null;
     }
     
 
     Class<?>[] paramTypes = { String.class, byte[].class, Integer.TYPE, Integer.TYPE };
     Method defineMethod = ClassLoader.class.getDeclaredMethod("defineClass", paramTypes);
     defineMethod.setAccessible(true);
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     
 
     byte[] buffer = new byte['Ѐ'];
     int read = 0;
     while ((read = is.read(buffer)) > 0) {
       baos.write(buffer, 0, read);
     }
     byte[] classBytes = baos.toByteArray();
     
 
     Object[] params = { classname, classBytes, Integer.valueOf(0), Integer.valueOf(classBytes.length) };
     Class result = (Class)defineMethod.invoke(targetClassLoader, params);
     return result;
   }
   
   public static List<Class> injectClassesInClassLoader(Map<String, byte[]> classes, ClassLoader targetClassLoader) throws Throwable {
     List<Class> results = new java.util.ArrayList();
     for (Map.Entry<String, byte[]> clazz : classes.entrySet()) {
       results.add(injectClassInClassLoader((byte[])clazz.getValue(), (String)clazz.getKey(), targetClassLoader));
     }
     return results;
   }
   
 
   public static Class injectClassInClassLoader(byte[] classBytes, String classname, ClassLoader targetClassLoader)
     throws Throwable
   {
     Class<?>[] paramTypes = { String.class, byte[].class, Integer.TYPE, Integer.TYPE };
     Method defineMethod = ClassLoader.class.getDeclaredMethod("defineClass", paramTypes);
     defineMethod.setAccessible(true);
     
 
     Object[] params = { classname, classBytes, Integer.valueOf(0), Integer.valueOf(classBytes.length) };
     Class result = (Class)defineMethod.invoke(targetClassLoader, params);
     return result;
   }
 }


