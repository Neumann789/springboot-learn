package jode.decompiler;

import jode.type.Type;

public class LocalVarEntry
{
  String name;
  Type type;
  int startAddr;
  int endAddr;
  LocalVarEntry next;
  
  public LocalVarEntry(int paramInt1, int paramInt2, String paramString, Type paramType)
  {
    this.startAddr = paramInt1;
    this.endAddr = paramInt2;
    this.name = paramString;
    this.type = paramType;
    this.next = null;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public Type getType()
  {
    return this.type;
  }
}


