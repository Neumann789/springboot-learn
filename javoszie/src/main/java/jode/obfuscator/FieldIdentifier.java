package jode.obfuscator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import jode.bytecode.FieldInfo;

public class FieldIdentifier
  extends Identifier
{
  FieldInfo info;
  ClassIdentifier clazz;
  String name;
  String type;
  private boolean notConstant;
  private Object constant;
  private Collection fieldListeners;
  
  public FieldIdentifier(ClassIdentifier paramClassIdentifier, FieldInfo paramFieldInfo)
  {
    super(paramFieldInfo.getName());
    this.name = paramFieldInfo.getName();
    this.type = paramFieldInfo.getType();
    this.info = paramFieldInfo;
    this.clazz = paramClassIdentifier;
    this.constant = paramFieldInfo.getConstant();
  }
  
  public void setSingleReachable()
  {
    super.setSingleReachable();
    Main.getClassBundle().analyzeIdentifier(this);
  }
  
  public void setSinglePreserved()
  {
    super.setSinglePreserved();
    setNotConstant();
  }
  
  public void analyze()
  {
    String str = getType();
    int i = str.indexOf('L');
    if (i != -1)
    {
      int j = str.indexOf(';', i);
      Main.getClassBundle().reachableClass(str.substring(i + 1, j).replace('/', '.'));
    }
  }
  
  public Identifier getParent()
  {
    return this.clazz;
  }
  
  public String getFullName()
  {
    return this.clazz.getFullName() + "." + getName() + "." + getType();
  }
  
  public String getFullAlias()
  {
    return this.clazz.getFullAlias() + "." + getAlias() + "." + Main.getClassBundle().getTypeAlias(getType());
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public int getModifiers()
  {
    return this.info.getModifiers();
  }
  
  public Iterator getChilds()
  {
    return Collections.EMPTY_LIST.iterator();
  }
  
  public boolean isNotConstant()
  {
    return this.notConstant;
  }
  
  public Object getConstant()
  {
    return this.constant;
  }
  
  public void addFieldListener(Identifier paramIdentifier)
  {
    if (paramIdentifier == null) {
      throw new NullPointerException();
    }
    if (this.fieldListeners == null) {
      this.fieldListeners = new HashSet();
    }
    if (!this.fieldListeners.contains(paramIdentifier)) {
      this.fieldListeners.add(paramIdentifier);
    }
  }
  
  public void setNotConstant()
  {
    if (this.notConstant) {
      return;
    }
    this.notConstant = true;
    if (this.fieldListeners == null) {
      return;
    }
    Iterator localIterator = this.fieldListeners.iterator();
    while (localIterator.hasNext()) {
      Main.getClassBundle().analyzeIdentifier((Identifier)localIterator.next());
    }
    this.fieldListeners = null;
  }
  
  public String toString()
  {
    return "FieldIdentifier " + getFullName();
  }
  
  public boolean conflicting(String paramString)
  {
    return this.clazz.fieldConflicts(this, paramString);
  }
  
  public void doTransformations()
  {
    this.info.setName(getAlias());
    this.info.setType(Main.getClassBundle().getTypeAlias(this.type));
  }
}


