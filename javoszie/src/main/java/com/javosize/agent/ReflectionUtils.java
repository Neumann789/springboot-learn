 package com.javosize.agent;
 
 import java.io.PrintStream;
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.util.Map;
 
 
 
 
 
 
 
 
 
 
 
 public class ReflectionUtils
 {
   static Map methodsCache = CacheManager.getCache(100, true);
   
   static Map fieldCache = CacheManager.getCache(100, true);
   
   public static Object invoke(Object called, String method) {
     return invoke(called, method, null, null);
   }
   
   public static Object invoke(Object called, String method, Object arg) {
     return invoke(called, method, new Class[] { arg.getClass() }, new Object[] { arg });
   }
   
   public static Object invoke(Object called, String methodName, Class[] argTypes, Object[] argz)
   {
     Object value = null;
     try
     {
       Class clazz = called.getClass();
       String key = getKey(clazz.hashCode(), clazz.getName(), methodName, argTypes);
       Method method = (Method)methodsCache.get(key);
       if (method == null) {
         method = getMethod(clazz, methodName, argTypes);
         if (method != null) {
           method.setAccessible(true);
           methodsCache.put(key, method);
           value = method.invoke(called, argz);
         }
       } else {
         value = method.invoke(called, argz);
       }
     } catch (Throwable th) {
       System.out.println("ERROR: " + th);
       th.printStackTrace();
       value = null;
     }
     return value;
   }
   
 
   private static Method getMethod(Class classDef, String methodName, Class[] argTypes)
   {
     Method method = null;
     if (classDef != null) {
       try {
         method = classDef.getDeclaredMethod(methodName, argTypes);
       } catch (SecurityException e) {
         method = null;
       } catch (NoSuchMethodException e) {
         method = null;
       }
       if (method == null) {
         Class parent = classDef.getSuperclass();
         method = getMethod(parent, methodName, argTypes);
       }
     }
     return method;
   }
   
   private static Field getFieldAccesible(Class classDef, String propertyName)
   {
     Field field = getField(classDef, propertyName);
     if ((field != null) && (!field.isAccessible())) {
       field.setAccessible(true);
     }
     return field;
   }
   
 
   private static Field getField(Class classDef, String propertyName)
   {
     Field field = null;
     if (classDef != null) {
       try {
         field = classDef.getDeclaredField(propertyName);
       } catch (NoSuchFieldException e) {
         field = null;
       }
       if (field == null) {
         Class parent = classDef.getSuperclass();
         field = getField(parent, propertyName);
       }
     }
     return field;
   }
   
   private static String getKey(int hashcode, String called, String methodName, Class[] argTypes)
   {
     StringBuffer ret = new StringBuffer();
     ret.append(hashcode);
     ret.append(":");
     ret.append(called);
     ret.append(":");
     ret.append(methodName);
     if (argTypes != null) {
       ret.append(ClassArrayToString(argTypes));
     }
     return ret.toString();
   }
   
   private static String ClassArrayToString(Class[] classArray)
   {
     StringBuffer ret = new StringBuffer();
     if (classArray != null) {
       for (int i = 0; i < classArray.length; i++) {
         ret.append(":");
         ret.append(classArray[i].getName());
       }
     }
     return ret.toString();
   }
   
   public static Object clone(Object o)
   {
     Object clone = null;
     try
     {
       clone = o.getClass().newInstance();
     } catch (InstantiationException e) {
       e.printStackTrace();
     } catch (IllegalAccessException e) {
       e.printStackTrace();
     }
     
 
     for (Class obj = o.getClass(); !obj.equals(Object.class); obj = obj.getSuperclass()) {
       Field[] fields = obj.getDeclaredFields();
       for (int i = 0; i < fields.length; i++) {
         fields[i].setAccessible(true);
         
         try
         {
           fields[i].set(clone, fields[i].get(o));
         }
         catch (IllegalArgumentException localIllegalArgumentException) {}catch (IllegalAccessException localIllegalAccessException1) {}
       }
     }
     
     return clone;
   }
   
 
 
 
   public static Object invokeProperty(Object called, String propertyName, boolean useCache)
   {
     Object value = null;
     try
     {
       Class classDef = called.getClass();
       Field field; 
       if (useCache) {
         String key = getKey(called.hashCode(), classDef.getName(), propertyName, null);
         field = (Field)fieldCache.get(key);
         if (field == null) {
           field = getFieldAccesible(classDef, propertyName);
           fieldCache.put(key, field);
         }
       } else {
         field = getFieldAccesible(classDef, propertyName);
       }
       if (field != null) {
         value = field.get(called);
       }
     } catch (SecurityException e) {
       value = null;
     } catch (IllegalArgumentException e) {
       value = null;
     } catch (IllegalAccessException e) {
       value = null;
     }
     return value;
   }
   
   public static Object invokeStaticMethod(Class clazz, String methodName, Class[] argTypes, Object[] argz)
   {
     Object value = null;
     try
     {
       String key = getKey(clazz.hashCode(), clazz.getName(), methodName, argTypes);
       Method method = (Method)methodsCache.get(key);
       if (method == null) {
         method = getMethod(clazz, methodName, argTypes);
         if (method != null) {
           method.setAccessible(true);
           methodsCache.put(key, method);
           value = method.invoke(clazz, argz);
         }
       } else {
         value = method.invoke(clazz, argz);
       }
     } catch (Throwable th) {
       value = null;
     }
     return value;
   }
   
 
 
 
   public static Object invokeStaticProperty(Class classDef, String propertyName, boolean useCache)
   {
     Object value = null;
     try {
       Field field;
       if (useCache) {
         String key = getKey(classDef.hashCode(), classDef.getName(), propertyName, null);
         field = (Field)fieldCache.get(key);
         if (field == null) {
           field = getFieldAccesible(classDef, propertyName);
           fieldCache.put(key, field);
         }
       } else {
         field = getFieldAccesible(classDef, propertyName);
       }
       if (field != null) {
         value = field.get(null);
       }
     } catch (SecurityException e) {
       value = null;
     } catch (IllegalArgumentException e) {
       value = null;
     } catch (IllegalAccessException e) {
       value = null;
     }
     return value;
   }
 }


