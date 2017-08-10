package jode.util;

import java.util.AbstractSet;
import java.util.Iterator;
import jode.AssertError;

public class SimpleSet
  extends AbstractSet
  implements Cloneable
{
  Object[] elementObjects;
  int count = 0;
  
  public SimpleSet()
  {
    this(2);
  }
  
  public SimpleSet(int paramInt)
  {
    this.elementObjects = new Object[paramInt];
  }
  
  public int size()
  {
    return this.count;
  }
  
  public boolean add(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException();
    }
    for (int i = 0; i < this.count; i++) {
      if (paramObject.equals(this.elementObjects[i])) {
        return false;
      }
    }
    if (this.count == this.elementObjects.length)
    {
      Object[] arrayOfObject = new Object[(this.count + 1) * 3 / 2];
      System.arraycopy(this.elementObjects, 0, arrayOfObject, 0, this.count);
      this.elementObjects = arrayOfObject;
    }
    this.elementObjects[(this.count++)] = paramObject;
    return true;
  }
  
  public Object clone()
  {
    try
    {
      SimpleSet localSimpleSet = (SimpleSet)super.clone();
      localSimpleSet.elementObjects = ((Object[])this.elementObjects.clone());
      return localSimpleSet;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertError("Clone?");
    }
  }
  
  public Iterator iterator()
  {
    new Iterator()
    {
      int pos = 0;
      
      public boolean hasNext()
      {
        return this.pos < SimpleSet.this.count;
      }
      
      public Object next()
      {
        return SimpleSet.this.elementObjects[(this.pos++)];
      }
      
      public void remove()
      {
        if (this.pos < SimpleSet.this.count) {
          System.arraycopy(SimpleSet.this.elementObjects, this.pos, SimpleSet.this.elementObjects, this.pos - 1, SimpleSet.this.count - this.pos);
        }
        SimpleSet.this.count -= 1;
        this.pos -= 1;
      }
    };
  }
}


