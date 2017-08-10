 package com.javosize.compiler;
 
 public class DynamicClassLoader extends ClassLoader
 {
   private CompiledCode cc;
   
   public DynamicClassLoader(ClassLoader parent)
   {
     super(parent);
   }
   
   public void setCode(CompiledCode cc) {
     this.cc = cc;
   }
   
   public byte[] getByteCode() { return this.cc.getByteCode(); }
   
   protected Class<?> findClass(String name)
     throws ClassNotFoundException
   {
     if (this.cc == null) {
       return super.findClass(name);
     }
     byte[] byteCode = this.cc.getByteCode();
     return super.defineClass(name, byteCode, 0, byteCode.length);
   }
 }


