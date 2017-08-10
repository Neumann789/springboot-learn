package jode.obfuscator;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class LocalIdentifier
  extends Identifier
{
  String name;
  String type;
  
  public LocalIdentifier(String paramString1, String paramString2, MethodIdentifier paramMethodIdentifier)
  {
    super(paramString1);
    this.name = paramString1;
    this.type = paramString2;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public Iterator getChilds()
  {
    return Collections.EMPTY_LIST.iterator();
  }
  
  public Identifier getParent()
  {
    return null;
  }
  
  public String getFullName()
  {
    return this.name;
  }
  
  public String getFullAlias()
  {
    return getAlias();
  }
  
  public boolean conflicting(String paramString)
  {
    return false;
  }
}


