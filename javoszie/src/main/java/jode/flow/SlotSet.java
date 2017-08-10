package jode.flow;

import java.util.AbstractSet;
import java.util.Iterator;
import jode.AssertError;
import jode.decompiler.LocalInfo;

public final class SlotSet
  extends AbstractSet
  implements Cloneable
{
  LocalInfo[] locals;
  int count;
  
  public SlotSet()
  {
    this.locals = null;
    this.count = 0;
  }
  
  public SlotSet(LocalInfo[] paramArrayOfLocalInfo)
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
    LocalInfo localLocalInfo1 = (LocalInfo)paramObject;
    LocalInfo localLocalInfo2 = findSlot(localLocalInfo1.getSlot());
    if (localLocalInfo2 != null)
    {
      localLocalInfo1.combineWith(localLocalInfo2);
      return false;
    }
    grow(1);
    this.locals[(this.count++)] = localLocalInfo1;
    return true;
  }
  
  public final boolean contains(Object paramObject)
  {
    return containsSlot(((LocalInfo)paramObject).getSlot());
  }
  
  public final boolean containsSlot(int paramInt)
  {
    return findSlot(paramInt) != null;
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
    int i = ((LocalInfo)paramObject).getSlot();
    for (int j = 0; j < this.count; j++) {
      if (this.locals[j].getSlot() == i)
      {
        this.locals[j] = this.locals[(--this.count)];
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
        return this.pos < SlotSet.this.count;
      }
      
      public Object next()
      {
        return SlotSet.this.locals[(this.pos++)];
      }
      
      public void remove()
      {
        if (this.pos < SlotSet.this.count) {
          System.arraycopy(SlotSet.this.locals, this.pos, SlotSet.this.locals, this.pos - 1, SlotSet.this.count - this.pos);
        }
        SlotSet.this.count -= 1;
        this.pos -= 1;
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
      SlotSet localSlotSet = (SlotSet)super.clone();
      if (this.count > 0)
      {
        localSlotSet.locals = new LocalInfo[this.count];
        System.arraycopy(this.locals, 0, localSlotSet.locals, 0, this.count);
      }
      return localSlotSet;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertError("Clone?");
    }
  }
  
  public void merge(VariableSet paramVariableSet)
  {
    for (int i = 0; i < this.count; i++)
    {
      LocalInfo localLocalInfo = this.locals[i];
      int j = localLocalInfo.getSlot();
      for (int k = 0; k < paramVariableSet.count; k++) {
        if (localLocalInfo.getSlot() == paramVariableSet.locals[k].getSlot()) {
          localLocalInfo.combineWith(paramVariableSet.locals[k]);
        }
      }
    }
  }
  
  public void mergeKill(SlotSet paramSlotSet)
  {
    grow(paramSlotSet.size());
    Iterator localIterator = paramSlotSet.iterator();
    while (localIterator.hasNext())
    {
      LocalInfo localLocalInfo = (LocalInfo)localIterator.next();
      if (!containsSlot(localLocalInfo.getSlot())) {
        add(localLocalInfo.getLocalInfo());
      }
    }
  }
}


