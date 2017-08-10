 package com.strobel.assembler.metadata;
 
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class RawType
   extends TypeReference
 {
   private final TypeReference _genericTypeDefinition;
   
   public RawType(TypeReference genericTypeDefinition)
   {
     this._genericTypeDefinition = ((TypeReference)VerifyArgument.notNull(genericTypeDefinition, "genericTypeDefinition"));
   }
   
   public String getFullName()
   {
     return this._genericTypeDefinition.getFullName();
   }
   
   public String getInternalName()
   {
     return this._genericTypeDefinition.getInternalName();
   }
   
   public TypeReference getDeclaringType()
   {
     return this._genericTypeDefinition.getDeclaringType();
   }
   
   public String getSimpleName()
   {
     return this._genericTypeDefinition.getSimpleName();
   }
   
   public String getPackageName()
   {
     return this._genericTypeDefinition.getPackageName();
   }
   
   public String getName()
   {
     return this._genericTypeDefinition.getName();
   }
   
   public TypeReference getUnderlyingType()
   {
     return this._genericTypeDefinition;
   }
   
   public final <R, P> R accept(TypeMetadataVisitor<P, R> visitor, P parameter)
   {
     return (R)visitor.visitRawType(this, parameter);
   }
   
   public TypeDefinition resolve()
   {
     return getUnderlyingType().resolve();
   }
 }


