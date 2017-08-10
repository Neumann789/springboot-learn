 package com.javosize.thirdparty.org.github.jamm;
 
 import java.lang.management.ManagementFactory;
 import java.lang.management.MemoryPoolMXBean;
 import java.lang.management.MemoryUsage;
 import java.lang.management.RuntimeMXBean;
 import java.lang.reflect.Field;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.List;
 import sun.misc.Unsafe;
 
 public abstract class MemoryLayoutSpecification
 {
   static final Unsafe unsafe;
   
   static
   {
     Unsafe tryGetUnsafe;
     try
     {
       Field field = Unsafe.class.getDeclaredField("theUnsafe");
       field.setAccessible(true);
       tryGetUnsafe = (Unsafe)field.get(null);
     } catch (Exception e) {
       tryGetUnsafe = null;
     }
     unsafe = tryGetUnsafe;
   }
   
   public static final MemoryLayoutSpecification SPEC = getEffectiveMemoryLayoutSpecification();
   
 
 
 
 
 
 
 
 
 
 
   public static boolean hasUnsafe()
   {
     return unsafe != null;
   }
   
   public static int sizeOf(Field field)
   {
     return sizeOfField(field.getType());
   }
   
 
 
   public static int sizeOfField(Class<?> type)
   {
     if (!type.isPrimitive())
       return SPEC.getReferenceSize();
     if ((type == Boolean.TYPE) || (type == Byte.TYPE))
       return 1;
     if ((type == Character.TYPE) || (type == Short.TYPE))
       return 2;
     if ((type == Float.TYPE) || (type == Integer.TYPE))
       return 4;
     if ((type == Double.TYPE) || (type == Long.TYPE))
       return 8;
     throw new IllegalStateException();
   }
   
 
 
 
   public static long sizeOf(Object obj)
   {
     Class<?> type = obj.getClass();
     if (type.isArray())
       return sizeOfArray(obj, type);
     return sizeOfInstance(type);
   }
   
 
 
 
   public static long sizeOfWithUnsafe(Object obj)
   {
     Class<?> type = obj.getClass();
     if (type.isArray())
       return sizeOfArray(obj, type);
     return sizeOfInstanceWithUnsafe(type);
   }
   
 
 
   public static long sizeOfInstance(Class<?> type)
   {
     long size = SPEC.getObjectHeaderSize() + sizeOfDeclaredFields(type);
     while (((type = type.getSuperclass()) != Object.class) && (type != null))
       size += roundTo(sizeOfDeclaredFields(type), SPEC.getSuperclassFieldPadding());
     return roundTo(size, SPEC.getObjectPadding());
   }
   
   public static long sizeOfInstanceWithUnsafe(Class<?> type)
   {
     while (type != null)
     {
       long size = 0L;
       for (Field f : declaredFieldsOf(type))
         size = Math.max(size, unsafe.objectFieldOffset(f) + sizeOf(f));
       if (size > 0L)
         return roundTo(size, SPEC.getObjectPadding());
       type = type.getSuperclass();
     }
     return roundTo(SPEC.getObjectHeaderSize(), SPEC.getObjectPadding());
   }
   
   public static long sizeOfArray(Object instance, Class<?> type) {
     return sizeOfArray(java.lang.reflect.Array.getLength(instance), sizeOfField(type.getComponentType()));
   }
   
 
 
 
 
 
   public static long sizeOfArray(int length, Class<?> type)
   {
     return sizeOfArray(length, sizeOfField(type.getComponentType()));
   }
   
 
 
 
 
 
   public static long sizeOfArray(int length, long elementSize)
   {
     return roundTo(SPEC.getArrayHeaderSize() + length * elementSize, SPEC.getObjectPadding());
   }
   
   private static long sizeOfDeclaredFields(Class<?> type) {
     long size = 0L;
     for (Field f : declaredFieldsOf(type))
       size += sizeOf(f);
     return size;
   }
   
   private static Iterable<Field> declaredFieldsOf(Class<?> type) {
     List<Field> fields = new ArrayList();
     for (Field f : type.getDeclaredFields())
     {
       if (!Modifier.isStatic(f.getModifiers()))
         fields.add(f);
     }
     return fields;
   }
   
   private static long roundTo(long x, int multiple) {
     return (x + multiple - 1L) / multiple * multiple;
   }
   
   private static MemoryLayoutSpecification getEffectiveMemoryLayoutSpecification()
   {
     String dataModel = System.getProperty("sun.arch.data.model");
     if ("32".equals(dataModel))
     {
      return new MemoryLayoutSpecification() {
         public int getArrayHeaderSize() {
           return 12;
         }
         
         public int getObjectHeaderSize() {
           return 8;
         }
         
         public int getObjectPadding() {
           return 8;
         }
         
         public int getReferenceSize() {
           return 4;
         }
         
         public int getSuperclassFieldPadding() {
           return 4;
         }
       };
     }
     
     String strVmVersion = System.getProperty("java.vm.version");
     int vmVersion = Integer.parseInt(strVmVersion.substring(0, strVmVersion.indexOf('.')));
     final int alignment = getAlignment();
     if (vmVersion >= 17)
     {
       long maxMemory = 0L;
       for (MemoryPoolMXBean mp : ManagementFactory.getMemoryPoolMXBeans()) {
         maxMemory += mp.getUsage().getMax();
       }
       
       if (maxMemory < 32212254720L)
       {
 
       return  new MemoryLayoutSpecification()
         {
           public int getArrayHeaderSize() {
             return 16;
           }
           
           public int getObjectHeaderSize() {
             return 12;
           }
           
           public int getObjectPadding() {
             return alignment;
           }
           
           public int getReferenceSize() {
             return 4;
           }
           
           public int getSuperclassFieldPadding() {
             return 4;
           }
         };
       }
     }
     
 
 
 
   return   new MemoryLayoutSpecification()
     {
       public int getArrayHeaderSize() {
         return 24;
       }
       
       public int getObjectHeaderSize() {
         return 16;
       }
       
       public int getObjectPadding() {
         return alignment;
       }
       
       public int getReferenceSize() {
         return 8;
       }
       
       public int getSuperclassFieldPadding() {
         return 8;
       }
     };
   }
   
   private static int getAlignment()
   {
     RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
     for (String arg : runtimeMxBean.getInputArguments()) {
       if (arg.startsWith("-XX:ObjectAlignmentInBytes=")) {
         try {
           return Integer.parseInt(arg.substring("-XX:ObjectAlignmentInBytes=".length()));
         } catch (Exception localException) {}
       }
     }
     return 8;
   }
   
   public abstract int getArrayHeaderSize();
   
   public abstract int getObjectHeaderSize();
   
   public abstract int getObjectPadding();
   
   public abstract int getReferenceSize();
   
   public abstract int getSuperclassFieldPadding();
 }


