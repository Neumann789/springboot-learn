 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.assembler.metadata.JvmType;
 import com.strobel.core.StringUtilities;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class JavaPrimitiveCast
 {
   public static Object cast(JvmType targetType, Object input)
   {
     switch (targetType) {
     case Boolean: 
       if ((input instanceof Boolean)) {
         return input;
       }
       if ((input instanceof Number)) {
         if ((!(input instanceof Float)) && (!(input instanceof Double)))
         {
 
           return Boolean.valueOf(((Number)input).longValue() != 0L); }
       } else {
         if ((input instanceof Character)) {
           return Boolean.valueOf(input != Character.valueOf('\000'));
         }
         if ((input instanceof String)) {
           return Boolean.valueOf(StringUtilities.isTrue((String)input));
         }
       }
       break;
     case Byte: 
       if ((input instanceof Number)) {
         return Byte.valueOf(((Number)input).byteValue());
       }
       if ((input instanceof Character)) {
         return Byte.valueOf((byte)((Character)input).charValue());
       }
       if ((input instanceof String)) {
         return Byte.valueOf(Byte.parseByte((String)input));
       }
       
       break;
     case Character: 
       if ((input instanceof Character)) {
         return input;
       }
       if ((input instanceof Number)) {
         return Character.valueOf((char)((Number)input).intValue());
       }
       if ((input instanceof String)) {
         String stringValue = (String)input;
         return Character.valueOf(stringValue.length() == 0 ? '\000' : stringValue.charAt(0));
       }
       
       break;
     case Short: 
       if ((input instanceof Number)) {
         return Short.valueOf(((Number)input).shortValue());
       }
       if ((input instanceof Character)) {
         return Short.valueOf((short)((Character)input).charValue());
       }
       if ((input instanceof String)) {
         return Short.valueOf(Short.parseShort((String)input));
       }
       
       break;
     case Integer: 
       if ((input instanceof Number)) {
         return Integer.valueOf(((Number)input).intValue());
       }
       if ((input instanceof Boolean)) {
         return Integer.valueOf(((Boolean)input).booleanValue() ? 1 : 0);
       }
       if ((input instanceof String)) {
         return Integer.valueOf(Integer.parseInt((String)input));
       }
       if ((input instanceof Character)) {
         return Integer.valueOf(((Character)input).charValue());
       }
       
       break;
     case Long: 
       if ((input instanceof Number)) {
         return Long.valueOf(((Number)input).longValue());
       }
       if ((input instanceof Character)) {
         return Long.valueOf(((Character)input).charValue());
       }
       if ((input instanceof String)) {
         return Long.valueOf(Long.parseLong((String)input));
       }
       
       break;
     case Float: 
       if ((input instanceof Number)) {
         return Float.valueOf(((Number)input).floatValue());
       }
       if ((input instanceof Character)) {
         return Float.valueOf(((Character)input).charValue());
       }
       if ((input instanceof String)) {
         return Float.valueOf(Float.parseFloat((String)input));
       }
       
       break;
     case Double: 
       if ((input instanceof Number)) {
         return Double.valueOf(((Number)input).doubleValue());
       }
       if ((input instanceof Character)) {
         return Double.valueOf(((Character)input).charValue());
       }
       if ((input instanceof String)) {
         return Double.valueOf(Double.parseDouble((String)input));
       }
       
       break;
     default: 
       return input;
     }
     
     throw new ClassCastException();
   }
 }


