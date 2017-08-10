 package com.strobel.assembler.metadata;
 
 import com.strobel.util.ContractUtils;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class CommonTypeReferences
 {
   public static final TypeReference Object;
   public static final TypeReference String;
   public static final TypeReference Serializable;
   public static final TypeReference Number;
   public static final TypeReference Void;
   public static final TypeReference Boolean;
   public static final TypeReference Character;
   public static final TypeReference Byte;
   public static final TypeReference Short;
   public static final TypeReference Integer;
   public static final TypeReference Long;
   public static final TypeReference Float;
   public static final TypeReference Double;
   
   static
   {
     MetadataParser parser = new MetadataParser(MetadataSystem.instance());
     
     Object = parser.parseTypeDescriptor("java/lang/Object");
     String = parser.parseTypeDescriptor("java/lang/String");
     Serializable = parser.parseTypeDescriptor("java/lang/Serializable");
     
     Number = parser.parseTypeDescriptor("java/lang/Number");
     
     Void = parser.parseTypeDescriptor("java/lang/Void");
     Boolean = parser.parseTypeDescriptor("java/lang/Boolean");
     Character = parser.parseTypeDescriptor("java/lang/Character");
     Byte = parser.parseTypeDescriptor("java/lang/Byte");
     Short = parser.parseTypeDescriptor("java/lang/Short");
     Integer = parser.parseTypeDescriptor("java/lang/Integer");
     Long = parser.parseTypeDescriptor("java/lang/Long");
     Float = parser.parseTypeDescriptor("java/lang/Float");
     Double = parser.parseTypeDescriptor("java/lang/Double");
   }
   
   private CommonTypeReferences() {
     throw ContractUtils.unreachable();
   }
 }


