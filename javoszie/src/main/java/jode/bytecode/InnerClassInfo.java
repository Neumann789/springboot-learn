package jode.bytecode;

import java.lang.reflect.Modifier;

public class InnerClassInfo
{
  public String inner;
  public String outer;
  public String name;
  public int modifiers;
  
  public InnerClassInfo(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    this.inner = paramString1;
    this.outer = paramString2;
    this.name = paramString3;
    this.modifiers = paramInt;
  }
  
  public String toString()
  {
    return "InnerClassInfo[" + this.inner + "," + this.outer + "," + this.name + "," + Modifier.toString(this.modifiers) + "]";
  }
}


