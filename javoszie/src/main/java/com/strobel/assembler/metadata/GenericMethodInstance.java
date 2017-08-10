 package com.strobel.assembler.metadata;
 
 import com.strobel.core.VerifyArgument;
 import java.util.Collection;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class GenericMethodInstance
   extends MethodReference
   implements IGenericInstance
 {
   private final MethodReference _genericDefinition;
   private final TypeReference _returnType;
   private final ParameterDefinitionCollection _parameters;
   private final List<TypeReference> _typeArguments;
   private TypeReference _declaringType;
   
   GenericMethodInstance(TypeReference declaringType, MethodReference definition, TypeReference returnType, List<ParameterDefinition> parameters, List<TypeReference> typeArguments)
   {
     this._declaringType = ((TypeReference)VerifyArgument.notNull(declaringType, "declaringType"));
     this._genericDefinition = ((MethodReference)VerifyArgument.notNull(definition, "definition"));
     this._returnType = ((TypeReference)VerifyArgument.notNull(returnType, "returnType"));
     this._parameters = new ParameterDefinitionCollection(this);
     this._typeArguments = ((List)VerifyArgument.notNull(typeArguments, "typeArguments"));
     
     this._parameters.addAll((Collection)VerifyArgument.notNull(parameters, "parameters"));
     this._parameters.freeze();
   }
   
   public final boolean hasTypeArguments()
   {
     return !this._typeArguments.isEmpty();
   }
   
   public final List<TypeReference> getTypeArguments()
   {
     return this._typeArguments;
   }
   
   public final IGenericParameterProvider getGenericDefinition()
   {
     return this._genericDefinition;
   }
   
   public final List<GenericParameter> getGenericParameters()
   {
     return this._genericDefinition.getGenericParameters();
   }
   
   public final TypeReference getReturnType()
   {
     return this._returnType;
   }
   
   public final List<ParameterDefinition> getParameters()
   {
     return this._parameters;
   }
   
   public boolean isGenericMethod()
   {
     return hasTypeArguments();
   }
   
   public MethodDefinition resolve()
   {
     return this._genericDefinition.resolve();
   }
   
   public StringBuilder appendErasedSignature(StringBuilder sb)
   {
     return this._genericDefinition.appendErasedSignature(sb);
   }
   
   public final TypeReference getDeclaringType()
   {
     return this._declaringType;
   }
   
   final void setDeclaringType(TypeReference declaringType) {
     this._declaringType = declaringType;
   }
   
   public final String getName()
   {
     return this._genericDefinition.getName();
   }
 }


