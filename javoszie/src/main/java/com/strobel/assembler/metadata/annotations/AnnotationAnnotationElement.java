 package com.strobel.assembler.metadata.annotations;
 
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class AnnotationAnnotationElement
   extends AnnotationElement
 {
   private final CustomAnnotation _annotation;
   
   public AnnotationAnnotationElement(CustomAnnotation annotation)
   {
     super(AnnotationElementType.Annotation);
     this._annotation = ((CustomAnnotation)VerifyArgument.notNull(annotation, "annotation"));
   }
   
   public CustomAnnotation getAnnotation() {
     return this._annotation;
   }
 }


