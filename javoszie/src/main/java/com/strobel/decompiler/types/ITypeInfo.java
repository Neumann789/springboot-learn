package com.strobel.decompiler.types;

import com.strobel.collections.ImmutableList;

abstract interface ITypeInfo
{
  public abstract String getName();
  
  public abstract String getPackageName();
  
  public abstract String getFullName();
  
  public abstract String getCanonicalName();
  
  public abstract String getInternalName();
  
  public abstract String getSignature();
  
  public abstract boolean isArray();
  
  public abstract boolean isPrimitive();
  
  public abstract boolean isPrimitiveOrVoid();
  
  public abstract boolean isVoid();
  
  public abstract boolean isRawType();
  
  public abstract boolean isGenericType();
  
  public abstract boolean isGenericTypeInstance();
  
  public abstract boolean isGenericTypeDefinition();
  
  public abstract boolean isGenericParameter();
  
  public abstract boolean isWildcard();
  
  public abstract boolean isUnknownType();
  
  public abstract boolean isBound();
  
  public abstract boolean isAnonymous();
  
  public abstract boolean isLocal();
  
  public abstract boolean hasConstraints();
  
  public abstract boolean hasSuperConstraint();
  
  public abstract boolean hasExtendsConstraint();
  
  public abstract ITypeInfo getDeclaringType();
  
  public abstract ITypeInfo getElementType();
  
  public abstract ITypeInfo getSuperConstraint();
  
  public abstract ITypeInfo getExtendsConstraint();
  
  public abstract ITypeInfo getSuperClass();
  
  public abstract ImmutableList<ITypeInfo> getSuperInterfaces();
  
  public abstract ImmutableList<ITypeInfo> getGenericParameters();
  
  public abstract ImmutableList<ITypeInfo> getTypeArguments();
  
  public abstract ITypeInfo getGenericDefinition();
  
  public abstract void addListener(ITypeListener paramITypeListener);
  
  public abstract void removeListener(ITypeListener paramITypeListener);
}


