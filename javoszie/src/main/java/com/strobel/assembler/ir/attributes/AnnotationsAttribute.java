 package com.strobel.assembler.ir.attributes;
 
 import com.strobel.assembler.metadata.annotations.CustomAnnotation;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class AnnotationsAttribute
   extends SourceAttribute
 {
   private final CustomAnnotation[] _annotations;
   
   public AnnotationsAttribute(String name, int length, CustomAnnotation[] annotations)
   {
     super(name, length);
     this._annotations = annotations;
   }
   
   public CustomAnnotation[] getAnnotations() {
     return this._annotations;
   }
 }


