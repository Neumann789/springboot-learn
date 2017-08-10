 package com.strobel.assembler.metadata;
 
 import com.strobel.core.VerifyArgument;
 import java.util.HashMap;
 import java.util.Map;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public enum JvmType
 {
   Boolean, 
   Byte, 
   Character, 
   Short, 
   Integer, 
   Long, 
   Float, 
   Double, 
   Object, 
   Array, 
   TypeVariable, 
   Wildcard, 
   Void;
   
   public final String getDescriptorPrefix() {
     switch (this) {
     case Boolean: 
       return "Z";
     case Byte: 
       return "B";
     case Character: 
       return "C";
     case Short: 
       return "S";
     case Integer: 
       return "I";
     case Long: 
       return "J";
     case Float: 
       return "F";
     case Double: 
       return "D";
     case Object: 
       return "L";
     case Array: 
       return "[";
     case TypeVariable: 
       return "T";
     case Wildcard: 
       return "*";
     case Void: 
       return "V";
     }
     return "L";
   }
   
   public final String getPrimitiveName()
   {
     switch (this) {
     case Boolean: 
       return "boolean";
     case Byte: 
       return "byte";
     case Character: 
       return "char";
     case Short: 
       return "short";
     case Integer: 
       return "int";
     case Long: 
       return "long";
     case Float: 
       return "float";
     case Double: 
       return "double";
     case Void: 
       return "void";
     }
     return null;
   }
   
   public final boolean isPrimitive()
   {
     switch (this) {
     case Object: 
     case Array: 
     case TypeVariable: 
     case Wildcard: 
     case Void: 
       return false;
     }
     return true;
   }
   
   public final boolean isPrimitiveOrVoid()
   {
     switch (this) {
     case Object: 
     case Array: 
     case TypeVariable: 
     case Wildcard: 
       return false;
     }
     return true;
   }
   
   public final int bitWidth()
   {
     switch (this) {
     case Boolean: 
       return 1;
     case Byte: 
       return 8;
     case Character: 
     case Short: 
       return 16;
     case Integer: 
       return 32;
     case Long: 
       return 64;
     case Float: 
       return 32;
     case Double: 
       return 64;
     }
     return 0;
   }
   
   public final int stackSlots()
   {
     switch (this) {
     case Long: 
     case Double: 
       return 2;
     case Void: 
       return 0;
     }
     return 1;
   }
   
   public final boolean isSingleWord()
   {
     switch (this) {
     case Long: 
     case Double: 
     case Void: 
       return false;
     }
     return true;
   }
   
   public final boolean isDoubleWord()
   {
     switch (this) {
     case Long: 
     case Double: 
       return true;
     }
     return false;
   }
   
   public final boolean isNumeric()
   {
     switch (this) {
     case Boolean: 
     case Byte: 
     case Character: 
     case Short: 
     case Integer: 
     case Long: 
     case Float: 
     case Double: 
       return true;
     }
     return false;
   }
   
   public final boolean isIntegral()
   {
     switch (this) {
     case Boolean: 
     case Byte: 
     case Character: 
     case Short: 
     case Integer: 
     case Long: 
       return true;
     }
     
     
     return false;
   }
   
   public final boolean isSubWordOrInt32()
   {
     switch (this) {
     case Boolean: 
     case Byte: 
     case Character: 
     case Short: 
     case Integer: 
       return true;
     }
     return false;
   }
   
   public final boolean isSigned()
   {
     switch (this) {
     case Boolean: 
     case Character: 
    	 return true;
     default: 
       return false;
     }
     
   }
   
   public final boolean isUnsigned()
   {
     switch (this) {
     case Boolean: 
     case Character: 
       return true;
     }
     return false;
   }
   
   public final boolean isFloating()
   {
     switch (this) {
     case Float: 
     case Double: 
       return true;
     }
     return false;
   }
   
   public final boolean isOther()
   {
     switch (this) {
     case Object: 
     case Array: 
     case TypeVariable: 
     case Wildcard: 
     case Void: 
       return true;
     }
     return false;
   }
   
 
 
   private static final Map<Class<?>, JvmType> CLASSES_TO_JVM_TYPES;
   
   static
   {
     HashMap<Class<?>, JvmType> map = new HashMap();
     
     map.put(Void.class, Void);
     map.put(Boolean.class, Boolean);
     map.put(Character.class, Character);
     map.put(Byte.class, Byte);
     map.put(Short.class, Short);
     map.put(Integer.class, Integer);
     map.put(Long.class, Long);
     map.put(Float.class, Float);
     map.put(Double.class, Double);
     
/*  修改前   map.put(Void.TYPE, Void);
     map.put(Boolean.TYPE, Boolean);
     map.put(Character.TYPE, Character);
     map.put(Byte.TYPE, Byte);
     map.put(Short.TYPE, Short);
     map.put(Integer.TYPE, Integer);
     map.put(Long.TYPE, Long);
     map.put(Float.TYPE, Float);
     map.put(Double.TYPE, Double);*/
     
     map.put(void.class, Void);
     map.put(boolean.class, Boolean);
     map.put(char.class, Character);
     map.put(byte.class, Byte);
     map.put(short.class, Short);
     map.put(int.class, Integer);
     map.put(long.class, Long);
     map.put(float.class, Float);
     map.put(double.class, Double);
     
     CLASSES_TO_JVM_TYPES = map;
   }
   
   public static JvmType forClass(Class<?> clazz) {
     VerifyArgument.notNull(clazz, "clazz");
     
     JvmType jvmType = (JvmType)CLASSES_TO_JVM_TYPES.get(clazz);
     
     if (jvmType != null) {
       return jvmType;
     }
     
     return Object;
   }
   
   public static JvmType forValue(Object value, boolean unboxPrimitives) {
     if (value == null) {
       return Object;
     }
     
     Class<?> clazz = value.getClass();
     
     if ((unboxPrimitives) || (clazz.isPrimitive())) {
       JvmType jvmType = (JvmType)CLASSES_TO_JVM_TYPES.get(clazz);
       
       if (jvmType != null) {
         return jvmType;
       }
     }
     
     return Object;
   }
   
   private JvmType() {}
 }


