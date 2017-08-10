package org.fusesource.hawtjni.runtime;

public enum MethodFlag
{
  METHOD_SKIP,  DYNAMIC,  CONSTANT_GETTER,  CAST,  JNI,  ADDRESS,  CPP_METHOD,  CPP_NEW,  CPP_DELETE,  CS_NEW,  CS_OBJECT,  SETTER,  GETTER,  ADDER,  POINTER_RETURN,  CONSTANT_INITIALIZER;
  
  private MethodFlag() {}
}


