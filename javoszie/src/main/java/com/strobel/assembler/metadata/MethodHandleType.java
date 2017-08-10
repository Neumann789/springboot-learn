package com.strobel.assembler.metadata;

public enum MethodHandleType
{
  GetField,  GetStatic,  PutField,  PutStatic,  InvokeVirtual,  InvokeStatic,  InvokeSpecial,  NewInvokeSpecial,  InvokeInterface;
  
  private MethodHandleType() {}
}


