 package com.strobel.assembler.metadata.annotations;
 
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class AnnotationParameter
 {
   private final AnnotationElement _value;
   private final String _member;
   
   public AnnotationParameter(String member, AnnotationElement value)
   {
     this._member = ((String)VerifyArgument.notNull(member, "member"));
     this._value = ((AnnotationElement)VerifyArgument.notNull(value, "value"));
   }
   
   public final String getMember() {
     return this._member;
   }
   
   public final AnnotationElement getValue() {
     return this._value;
   }
 }


