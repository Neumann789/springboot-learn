 package com.javosize.agent;
 
 import com.javosize.thirdparty.org.objectweb.asm.ClassWriter;
 
 public class JavosizeClassWriter extends ClassWriter
 {
   private ClassLoader classLoader = null;
   
   public JavosizeClassWriter(int arg0)
   {
     super(arg0);
   }
   
 
   protected String getCommonSuperClass(String type1, String type2)
   {
     ClassLoader classLoader;
     if (getClassLoader() != null) {
       classLoader = getClassLoader();
     } else {
       classLoader = getClass().getClassLoader();
     }
     Class<?> c;
     Class<?> d ;
     try
     {
       c = Class.forName(type1.replace('/', '.'), false, classLoader);
       d = Class.forName(type2.replace('/', '.'), false, classLoader);
     } catch (Exception e) {
       throw new RuntimeException(e.toString()); }
    if (c.isAssignableFrom(d)) {
       return type1;
     }
     if (d.isAssignableFrom(c)) {
       return type2;
     }
     if ((c.isInterface()) || (d.isInterface())) {
       return "java/lang/Object";
     }
     do {
       c = c.getSuperclass();
     } while (!c.isAssignableFrom(d));
     return c.getName().replace('.', '/');
   }
   
   public ClassLoader getClassLoader()
   {
     return this.classLoader;
   }
   
   public void setClassLoader(ClassLoader classLoader) {
     this.classLoader = classLoader;
   }
 }


