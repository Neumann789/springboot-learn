package jode.bytecode;

import java.util.Iterator;
import jode.util.UnifyHash;

public class Reference
{
  private final String clazz;
  private final String name;
  private final String type;
  private static final UnifyHash unifier = new UnifyHash();
  
  public static Reference getReference(String paramString1, String paramString2, String paramString3)
  {
    int i = paramString1.hashCode() ^ paramString2.hashCode() ^ paramString3.hashCode();
    Iterator localIterator = unifier.iterateHashCode(i);
    while (localIterator.hasNext())
    {
      localReference = (Reference)localIterator.next();
      if ((localReference.clazz.equals(paramString1)) && (localReference.name.equals(paramString2)) && (localReference.type.equals(paramString3))) {
        return localReference;
      }
    }
    Reference localReference = new Reference(paramString1, paramString2, paramString3);
    unifier.put(i, localReference);
    return localReference;
  }
  
  private Reference(String paramString1, String paramString2, String paramString3)
  {
    this.clazz = paramString1;
    this.name = paramString2;
    this.type = paramString3;
  }
  
  public String getClazz()
  {
    return this.clazz;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public String toString()
  {
    return this.clazz + " " + this.name + " " + this.type;
  }
}


