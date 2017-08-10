package jode.flow;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import jode.AssertError;
import jode.decompiler.LocalInfo;

public final class VariableSet
  extends AbstractSet
  implements Cloneable
{
  LocalInfo[] locals;
  int count;
  
  public VariableSet()
  {
    this.locals = null;
    this.count = 0;
  }
  
  public VariableSet(LocalInfo[] paramArrayOfLocalInfo)
  {
    this.count = paramArrayOfLocalInfo.length;
    this.locals = paramArrayOfLocalInfo;
  }
  
  public final void grow(int paramInt)
  {
    if (this.locals != null)
    {
      paramInt += this.count;
      if (paramInt > this.locals.length)
      {
        int i = this.locals.length * 2;
        LocalInfo[] arrayOfLocalInfo = new LocalInfo[i > paramInt ? i : paramInt];
        System.arraycopy(this.locals, 0, arrayOfLocalInfo, 0, this.count);
        this.locals = arrayOfLocalInfo;
      }
    }
    else if (paramInt > 0)
    {
      this.locals = new LocalInfo[paramInt];
    }
  }
  
  public boolean add(Object paramObject)
  {
    if (contains(paramObject)) {
      return false;
    }
    grow(1);
    this.locals[(this.count++)] = ((LocalInfo)paramObject);
    return true;
  }
  
  public boolean contains(Object paramObject)
  {
    paramObject = ((LocalInfo)paramObject).getLocalInfo();
    for (int i = 0; i < this.count; i++) {
      if (this.locals[i].getLocalInfo() == paramObject) {
        return true;
      }
    }
    return false;
  }
  
  public final boolean containsSlot(int paramInt)
  {
    return findSlot(paramInt) != null;
  }
  
  public LocalInfo findLocal(String paramString)
  {
    for (int i = 0; i < this.count; i++) {
      if (this.locals[i].getName().equals(paramString)) {
        return this.locals[i];
      }
    }
    return null;
  }
  
  public LocalInfo findSlot(int paramInt)
  {
    for (int i = 0; i < this.count; i++) {
      if (this.locals[i].getSlot() == paramInt) {
        return this.locals[i];
      }
    }
    return null;
  }
  
  public boolean remove(Object paramObject)
  {
    paramObject = ((LocalInfo)paramObject).getLocalInfo();
    for (int i = 0; i < this.count; i++) {
      if (this.locals[i].getLocalInfo() == paramObject)
      {
        this.locals[i] = this.locals[(--this.count)];
        this.locals[this.count] = null;
        return true;
      }
    }
    return false;
  }
  
  public int size()
  {
    return this.count;
  }
  
  public Iterator iterator()
  {
    new Iterator()
    {
      int pos = 0;
      
      public boolean hasNext()
      {
        return this.pos < VariableSet.this.count;
      }
      
      public Object next()
      {
        return VariableSet.this.locals[(this.pos++)];
      }
      
      public void remove()
      {
        if (this.pos < VariableSet.this.count) {
          System.arraycopy(VariableSet.this.locals, this.pos, VariableSet.this.locals, this.pos - 1, VariableSet.this.count - this.pos);
        }
        VariableSet.this.count -= 1;
        this.pos -= 1;
        VariableSet.this.locals[VariableSet.this.count] = null;
      }
    };
  }
  
  public void clear()
  {
    this.locals = null;
    this.count = 0;
  }
  
  public Object clone()
  {
    try
    {
      VariableSet localVariableSet = (VariableSet)super.clone();
      if (this.count > 0)
      {
        localVariableSet.locals = new LocalInfo[this.count];
        System.arraycopy(this.locals, 0, localVariableSet.locals, 0, this.count);
      }
      return localVariableSet;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertError("Clone?");
    }
  }
  
  public VariableSet intersect(VariableSet paramVariableSet)
  {
    VariableSet localVariableSet = new VariableSet();
    localVariableSet.grow(Math.min(this.count, paramVariableSet.count));
    for (int i = 0; i < this.count; i++)
    {
      LocalInfo localLocalInfo = this.locals[i];
      int j = localLocalInfo.getSlot();
      if ((paramVariableSet.containsSlot(j)) && (!localVariableSet.containsSlot(j))) {
        localVariableSet.locals[(localVariableSet.count++)] = localLocalInfo.getLocalInfo();
      }
    }
    return localVariableSet;
  }
  
  public void mergeGenKill(Collection paramCollection, SlotSet paramSlotSet)
  {
    grow(paramCollection.size());
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      LocalInfo localLocalInfo = (LocalInfo)localIterator.next();
      if (!paramSlotSet.containsSlot(localLocalInfo.getSlot())) {
        add(localLocalInfo.getLocalInfo());
      }
    }
  }
}


