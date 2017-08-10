package jode.util;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class SimpleMap
  extends AbstractMap
{
  private Set backing;
  
  public SimpleMap()
  {
    this.backing = new SimpleSet();
  }
  
  public SimpleMap(int paramInt)
  {
    this.backing = new SimpleSet(paramInt);
  }
  
  public SimpleMap(Set paramSet)
  {
    this.backing = paramSet;
  }
  
  public Set entrySet()
  {
    return this.backing;
  }
  
  public Object put(Object paramObject1, Object paramObject2)
  {
    Iterator localIterator = this.backing.iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (paramObject1.equals(localEntry.getKey())) {
        return localEntry.setValue(paramObject2);
      }
    }
    this.backing.add(new SimpleEntry(paramObject1, paramObject2));
    return null;
  }
  
  public static class SimpleEntry
    implements Map.Entry
  {
    Object key;
    Object value;
    
    public SimpleEntry(Object paramObject1, Object paramObject2)
    {
      this.key = paramObject1;
      this.value = paramObject2;
    }
    
    public Object getKey()
    {
      return this.key;
    }
    
    public Object getValue()
    {
      return this.value;
    }
    
    public Object setValue(Object paramObject)
    {
      Object localObject = this.value;
      this.value = paramObject;
      return localObject;
    }
    
    public int hashCode()
    {
      return this.key.hashCode() ^ this.value.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof Map.Entry))
      {
        Map.Entry localEntry = (Map.Entry)paramObject;
        return (this.key.equals(localEntry.getKey())) && (this.value.equals(localEntry.getValue()));
      }
      return false;
    }
  }
}


