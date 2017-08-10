 package com.strobel.util;
 
 import com.strobel.collections.Cache;
 import com.strobel.core.VerifyArgument;
 import java.lang.reflect.Array;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class EmptyArrayCache
 {
   public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
   public static final char[] EMPTY_CHAR_ARRAY = new char[0];
   public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
   public static final short[] EMPTY_SHORT_ARRAY = new short[0];
   public static final int[] EMPTY_INT_ARRAY = new int[0];
   public static final long[] EMPTY_LONG_ARRAY = new long[0];
   public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
   public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
   public static final String[] EMPTY_STRING_ARRAY = new String[0];
   public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
   public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
   
   private static final Cache<Class<?>, Object> GLOBAL_CACHE = Cache.createTopLevelCache();
   private static final Cache<Class<?>, Object> THREAD_LOCAL_CACHE = Cache.createThreadLocalCache(GLOBAL_CACHE);
   
 
 
   static
   {
     THREAD_LOCAL_CACHE.cache(Boolean.TYPE, EMPTY_BOOLEAN_ARRAY);
     THREAD_LOCAL_CACHE.cache(Character.TYPE, EMPTY_CHAR_ARRAY);
     THREAD_LOCAL_CACHE.cache(Byte.TYPE, EMPTY_BYTE_ARRAY);
     THREAD_LOCAL_CACHE.cache(Short.TYPE, EMPTY_SHORT_ARRAY);
     THREAD_LOCAL_CACHE.cache(Integer.TYPE, EMPTY_INT_ARRAY);
     THREAD_LOCAL_CACHE.cache(Long.TYPE, EMPTY_LONG_ARRAY);
     THREAD_LOCAL_CACHE.cache(Float.TYPE, EMPTY_FLOAT_ARRAY);
     THREAD_LOCAL_CACHE.cache(Double.TYPE, EMPTY_DOUBLE_ARRAY);
     THREAD_LOCAL_CACHE.cache(String.class, EMPTY_STRING_ARRAY);
     THREAD_LOCAL_CACHE.cache(Object.class, EMPTY_OBJECT_ARRAY);
     THREAD_LOCAL_CACHE.cache(Class.class, EMPTY_CLASS_ARRAY);
   }
   
   private EmptyArrayCache() {
     throw ContractUtils.unreachable();
   }
   
   public static <T> T[] fromElementType(Class<T> elementType)
   {
     VerifyArgument.notNull(elementType, "elementType");
     
     T[] cachedArray = (Object[])THREAD_LOCAL_CACHE.get(elementType);
     
     if (cachedArray != null) {
       return cachedArray;
     }
     
     return (Object[])THREAD_LOCAL_CACHE.cache(elementType, Array.newInstance(elementType, 0));
   }
   
   public static Object fromElementOrPrimitiveType(Class<?> elementType) {
     VerifyArgument.notNull(elementType, "elementType");
     
     Object cachedArray = THREAD_LOCAL_CACHE.get(elementType);
     
     if (cachedArray != null) {
       return cachedArray;
     }
     
     return THREAD_LOCAL_CACHE.cache(elementType, Array.newInstance(elementType, 0));
   }
   
   public static <T> T fromArrayType(Class<? extends Object[]> arrayType)
   {
     return fromElementType(((Class)VerifyArgument.notNull(arrayType, "arrayType")).getComponentType());
   }
 }


