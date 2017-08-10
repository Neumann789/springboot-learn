 package com.strobel.assembler.metadata;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class BottomType
   extends TypeDefinition
 {
   static final BottomType INSTANCE = new BottomType();
   
   private BottomType() {
     setName("__Bottom");
   }
   
   public String getSimpleName()
   {
     return "__Bottom";
   }
   
   public String getFullName()
   {
     return getSimpleName();
   }
   
   public String getInternalName()
   {
     return getSimpleName();
   }
   
   public final <R, P> R accept(TypeMetadataVisitor<P, R> visitor, P parameter)
   {
     return (R)visitor.visitBottomType(this, parameter);
   }
 }


