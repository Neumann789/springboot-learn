package com.strobel.assembler.metadata;

public abstract interface IMemberDefinition
{
  public abstract String getName();
  
  public abstract String getFullName();
  
  public abstract boolean isSpecialName();
  
  public abstract TypeReference getDeclaringType();
  
  public abstract long getFlags();
  
  public abstract int getModifiers();
  
  public abstract boolean isFinal();
  
  public abstract boolean isNonPublic();
  
  public abstract boolean isPrivate();
  
  public abstract boolean isProtected();
  
  public abstract boolean isPublic();
  
  public abstract boolean isStatic();
  
  public abstract boolean isSynthetic();
  
  public abstract boolean isDeprecated();
  
  public abstract boolean isPackagePrivate();
  
  public abstract String getBriefDescription();
  
  public abstract String getDescription();
  
  public abstract String getErasedDescription();
  
  public abstract String getSimpleDescription();
}


