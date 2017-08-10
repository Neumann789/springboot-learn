 package com.strobel.assembler.metadata.annotations;
 
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.VerifyArgument;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class CustomAnnotation
 {
   private final TypeReference _annotationType;
   private final List<AnnotationParameter> _parameters;
   
   public CustomAnnotation(TypeReference annotationType, List<AnnotationParameter> parameters)
   {
     this._annotationType = ((TypeReference)VerifyArgument.notNull(annotationType, "annotationType"));
     this._parameters = ((List)VerifyArgument.notNull(parameters, "parameters"));
   }
   
   public TypeReference getAnnotationType() {
     return this._annotationType;
   }
   
   public boolean hasParameters() {
     return !this._parameters.isEmpty();
   }
   
   public List<AnnotationParameter> getParameters() {
     return this._parameters;
   }
 }


