package com.strobel.assembler.metadata;

public abstract interface IMetadataScope
{
  public abstract TypeReference lookupType(int paramInt);
  
  public abstract FieldReference lookupField(int paramInt);
  
  public abstract MethodReference lookupMethod(int paramInt);
  
  public abstract MethodHandle lookupMethodHandle(int paramInt);
  
  public abstract IMethodSignature lookupMethodType(int paramInt);
  
  public abstract DynamicCallSite lookupDynamicCallSite(int paramInt);
  
  public abstract FieldReference lookupField(int paramInt1, int paramInt2);
  
  public abstract MethodReference lookupMethod(int paramInt1, int paramInt2);
  
  public abstract <T> T lookupConstant(int paramInt);
  
  public abstract Object lookup(int paramInt);
}


