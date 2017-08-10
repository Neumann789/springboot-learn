 package com.strobel.assembler.metadata;
 
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class ParameterizedType
   extends TypeReference
   implements IGenericInstance
 {
   private final TypeReference _genericDefinition;
   private final List<TypeReference> _typeParameters;
   
   ParameterizedType(TypeReference genericDefinition, List<TypeReference> typeParameters)
   {
     this._genericDefinition = genericDefinition;
     this._typeParameters = typeParameters;
   }
   
   public String getName()
   {
     return this._genericDefinition.getName();
   }
   
   public String getPackageName()
   {
     return this._genericDefinition.getPackageName();
   }
   
   public String getFullName()
   {
     return this._genericDefinition.getFullName();
   }
   
   public String getInternalName()
   {
     return this._genericDefinition.getInternalName();
   }
   
   public TypeReference getDeclaringType()
   {
     return this._genericDefinition.getDeclaringType();
   }
   
   public String getSimpleName()
   {
     return this._genericDefinition.getSimpleName();
   }
   
   public boolean isGenericDefinition()
   {
     return false;
   }
   
   public List<GenericParameter> getGenericParameters()
   {
     if (!this._genericDefinition.isGenericDefinition()) {
       TypeDefinition resolvedDefinition = this._genericDefinition.resolve();
       
       if (resolvedDefinition != null) {
         return resolvedDefinition.getGenericParameters();
       }
     }
     
     return this._genericDefinition.getGenericParameters();
   }
   
   public boolean hasTypeArguments()
   {
     return true;
   }
   
   public List<TypeReference> getTypeArguments()
   {
     return this._typeParameters;
   }
   
   public IGenericParameterProvider getGenericDefinition()
   {
     return this._genericDefinition;
   }
   
   public TypeReference getUnderlyingType()
   {
     return this._genericDefinition;
   }
   
   public final <R, P> R accept(TypeMetadataVisitor<P, R> visitor, P parameter)
   {
     return (R)visitor.visitParameterizedType(this, parameter);
   }
   
   public TypeDefinition resolve()
   {
     return this._genericDefinition.resolve();
   }
   
   public FieldDefinition resolve(FieldReference field)
   {
     return this._genericDefinition.resolve(field);
   }
   
   public MethodDefinition resolve(MethodReference method)
   {
     return this._genericDefinition.resolve(method);
   }
   
   public TypeDefinition resolve(TypeReference type)
   {
     return this._genericDefinition.resolve(type);
   }
 }


