 package com.strobel.assembler.metadata;
 
 import com.strobel.assembler.metadata.annotations.CustomAnnotation;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class MemberReference
   implements IAnnotationsProvider, IMetadataTypeMember
 {
   public boolean isSpecialName()
   {
     return false;
   }
   
   public boolean isDefinition() {
     return false;
   }
   
   public boolean containsGenericParameters() {
     TypeReference declaringType = getDeclaringType();
     
     return (declaringType != null) && (declaringType.containsGenericParameters());
   }
   
   public abstract TypeReference getDeclaringType();
   
   public boolean isEquivalentTo(MemberReference member)
   {
     return member == this;
   }
   
 
 
   public boolean hasAnnotations()
   {
     return !getAnnotations().isEmpty();
   }
   
   public List<CustomAnnotation> getAnnotations()
   {
     return Collections.emptyList();
   }
   
 
 
   public abstract String getName();
   
 
   public String getFullName()
   {
     StringBuilder name = new StringBuilder();
     appendName(name, true, false);
     return name.toString();
   }
   
 
 
   public String getSignature()
   {
     return appendSignature(new StringBuilder()).toString();
   }
   
 
 
 
   public String getErasedSignature()
   {
     return appendErasedSignature(new StringBuilder()).toString();
   }
   
   protected abstract StringBuilder appendName(StringBuilder paramStringBuilder, boolean paramBoolean1, boolean paramBoolean2);
   
   protected abstract StringBuilder appendSignature(StringBuilder paramStringBuilder);
   
   protected abstract StringBuilder appendErasedSignature(StringBuilder paramStringBuilder);
   
   public String toString() { return getFullName() + ":" + getSignature(); }
 }


