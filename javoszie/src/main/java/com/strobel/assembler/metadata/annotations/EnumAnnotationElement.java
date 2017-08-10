 package com.strobel.assembler.metadata.annotations;
 
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class EnumAnnotationElement
   extends AnnotationElement
 {
   private final TypeReference _enumType;
   private final String _enumConstantName;
   
   public EnumAnnotationElement(TypeReference enumType, String enumConstantName)
   {
     super(AnnotationElementType.Enum);
     this._enumType = ((TypeReference)VerifyArgument.notNull(enumType, "enumType"));
     this._enumConstantName = ((String)VerifyArgument.notNull(enumConstantName, "enumConstantName"));
   }
   
   public TypeReference getEnumType() {
     return this._enumType;
   }
   
   public String getEnumConstantName() {
     return this._enumConstantName;
   }
 }


