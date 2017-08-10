package jode.util;

import java.util.Enumeration;

public class ArrayEnum
  implements Enumeration
{
  int index = 0;
  int size;
  Object[] array;
  
  public ArrayEnum(int paramInt, Object[] paramArrayOfObject)
  {
    this.size = paramInt;
    this.array = paramArrayOfObject;
  }
  
  public boolean hasMoreElements()
  {
    return this.index < this.size;
  }
  
  public Object nextElement()
  {
    return this.array[(this.index++)];
  }
}


