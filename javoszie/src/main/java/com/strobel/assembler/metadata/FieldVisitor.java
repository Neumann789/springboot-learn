 package com.strobel.assembler.metadata;
 
 import com.strobel.assembler.ir.attributes.SourceAttribute;
 import com.strobel.assembler.metadata.annotations.CustomAnnotation;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract interface FieldVisitor
 {
   public static final FieldVisitor EMPTY = new FieldVisitor()
   {
     public void visitAttribute(SourceAttribute attribute) {}
     
     public void visitAnnotation(CustomAnnotation annotation, boolean visible) {}
     
     public void visitEnd() {}
   };
   
   public abstract void visitAttribute(SourceAttribute paramSourceAttribute);
   
   public abstract void visitAnnotation(CustomAnnotation paramCustomAnnotation, boolean paramBoolean);
   
   public abstract void visitEnd();
 }


