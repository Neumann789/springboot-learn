 package com.strobel.assembler.ir.attributes;
 
 import com.strobel.assembler.metadata.annotations.AnnotationElement;
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class AnnotationDefaultAttribute
   extends SourceAttribute
 {
   private final AnnotationElement _defaultValue;
   
   public AnnotationDefaultAttribute(int length, AnnotationElement defaultValue)
   {
     super("AnnotationDefault", length);
     this._defaultValue = ((AnnotationElement)VerifyArgument.notNull(defaultValue, "defaultValue"));
   }
   
   public AnnotationElement getDefaultValue() {
     return this._defaultValue;
   }
 }


