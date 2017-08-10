 package com.strobel.assembler.metadata;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class NullType
   extends TypeDefinition
 {
   static final NullType INSTANCE = new NullType();
   
   private NullType() {
     setName("__Null");
   }
   
   public String getSimpleName()
   {
     return "__Null";
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
     return (R)visitor.visitNullType(this, parameter);
   }
 }


