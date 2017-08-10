 package com.strobel.assembler.metadata;
 
 import com.strobel.core.StringUtilities;
 import com.strobel.util.ContractUtils;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class FieldReference
   extends MemberReference
 {
   public abstract TypeReference getFieldType();
   
   public boolean containsGenericParameters()
   {
     TypeReference fieldType = getFieldType();
     
     return ((fieldType != null) && (fieldType.containsGenericParameters())) || (super.containsGenericParameters());
   }
   
 
   public boolean isEquivalentTo(MemberReference member)
   {
     if (super.isEquivalentTo(member)) {
       return true;
     }
     
     if ((member instanceof FieldReference)) {
       FieldReference field = (FieldReference)member;
       
       return (StringUtilities.equals(field.getName(), getName())) && (MetadataResolver.areEquivalent(field.getDeclaringType(), getDeclaringType()));
     }
     
 
     return false;
   }
   
   public FieldDefinition resolve() {
     TypeReference declaringType = getDeclaringType();
     
     if (declaringType == null) {
       throw ContractUtils.unsupported();
     }
     
     return declaringType.resolve(this);
   }
   
 
 
   protected abstract StringBuilder appendName(StringBuilder paramStringBuilder, boolean paramBoolean1, boolean paramBoolean2);
   
 
 
   protected StringBuilder appendSignature(StringBuilder sb)
   {
     return getFieldType().appendSignature(sb);
   }
   
   protected StringBuilder appendErasedSignature(StringBuilder sb)
   {
     return getFieldType().appendErasedSignature(sb);
   }
 }


