 package com.strobel.compilerservices;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.util.ContractUtils;
 import java.lang.reflect.Field;
 import sun.misc.Unsafe;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class RuntimeHelpers
 {
   private static Unsafe _unsafe;
   
   private RuntimeHelpers()
   {
     throw ContractUtils.unreachable();
   }
   
   public static void ensureClassInitialized(Class<?> clazz) {
     getUnsafeInstance().ensureClassInitialized((Class)VerifyArgument.notNull(clazz, "clazz"));
   }
   
 
 
 
   private static Unsafe getUnsafeInstance()
   {
     if (_unsafe != null) {
       return _unsafe;
     }
     try
     {
       _unsafe = Unsafe.getUnsafe();
     }
     catch (Throwable ignored) {}
     
     try
     {
       Field instanceField = Unsafe.class.getDeclaredField("theUnsafe");
       instanceField.setAccessible(true);
       _unsafe = (Unsafe)instanceField.get(Unsafe.class);
     }
     catch (Throwable t) {
       throw new IllegalStateException(String.format("Could not load an instance of the %s class.", new Object[] { Unsafe.class.getName() }));
     }
     
 
 
 
 
 
     return _unsafe;
   }
 }


