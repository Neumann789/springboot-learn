 package com.strobel.assembler.metadata;
 
 import com.strobel.core.VerifyArgument;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class RawMethod
   extends MethodReference
   implements IGenericInstance
 {
   private final MethodReference _baseMethod;
   private final TypeReference _returnType;
   private final ParameterDefinitionCollection _parameters;
   private TypeReference _declaringType;
   
   public RawMethod(MethodReference baseMethod)
   {
     VerifyArgument.notNull(baseMethod, "baseMethod");
     
     TypeReference declaringType = baseMethod.getDeclaringType();
     
     this._baseMethod = baseMethod;
     this._declaringType = MetadataHelper.eraseRecursive(declaringType);
     this._returnType = MetadataHelper.eraseRecursive(baseMethod.getReturnType());
     this._parameters = new ParameterDefinitionCollection(this);
     
     for (ParameterDefinition parameter : baseMethod.getParameters()) {
       if (parameter.hasName()) {
         this._parameters.add(new ParameterDefinition(parameter.getSlot(), parameter.getName(), MetadataHelper.eraseRecursive(parameter.getParameterType())));
 
 
 
       }
       else
       {
 
 
         this._parameters.add(new ParameterDefinition(parameter.getSlot(), MetadataHelper.eraseRecursive(parameter.getParameterType())));
       }
     }
     
 
 
 
 
 
     this._parameters.freeze();
   }
   
   public final MethodReference getBaseMethod() {
     return this._baseMethod;
   }
   
   public final boolean hasTypeArguments()
   {
     return false;
   }
   
   public final List<TypeReference> getTypeArguments()
   {
     return Collections.emptyList();
   }
   
   public final IGenericParameterProvider getGenericDefinition()
   {
     return (this._baseMethod instanceof IGenericInstance) ? ((IGenericInstance)this._baseMethod).getGenericDefinition() : null;
   }
   
 
   public final List<GenericParameter> getGenericParameters()
   {
     return Collections.emptyList();
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
     return this._baseMethod.resolve();
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
     return this._baseMethod.getName();
   }
 }


