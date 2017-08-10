package jode.obfuscator;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import jode.GlobalOptions;

public abstract class Identifier
{
  private Identifier right = null;
  private Identifier left = null;
  private boolean reachable = false;
  private boolean preserved = false;
  private String alias = null;
  private boolean wasAliased = false;
  static int serialnr = 0;
  
  public Identifier(String paramString)
  {
    this.alias = paramString;
  }
  
  public final boolean isReachable()
  {
    return this.reachable;
  }
  
  public final boolean isPreserved()
  {
    return this.preserved;
  }
  
  protected void setSinglePreserved() {}
  
  protected void setSingleReachable()
  {
    if (getParent() != null) {
      getParent().setReachable();
    }
  }
  
  public void setReachable()
  {
    if (!this.reachable)
    {
      this.reachable = true;
      setSingleReachable();
    }
  }
  
  public void setPreserved()
  {
    if (!this.preserved)
    {
      this.preserved = true;
      for (Identifier localIdentifier = this; localIdentifier != null; localIdentifier = localIdentifier.left) {
        localIdentifier.setSinglePreserved();
      }
      for (localIdentifier = this.right; localIdentifier != null; localIdentifier = localIdentifier.right) {
        localIdentifier.setSinglePreserved();
      }
    }
  }
  
  public Identifier getRepresentative()
  {
    for (Identifier localIdentifier = this; localIdentifier.left != null; localIdentifier = localIdentifier.left) {}
    return localIdentifier;
  }
  
  public final boolean isRepresentative()
  {
    return this.left == null;
  }
  
  public final boolean wasAliased()
  {
    return getRepresentative().wasAliased;
  }
  
  public final void setAlias(String paramString)
  {
    if (paramString != null)
    {
      Identifier localIdentifier = getRepresentative();
      localIdentifier.wasAliased = true;
      localIdentifier.alias = paramString;
    }
  }
  
  public final String getAlias()
  {
    return getRepresentative().alias;
  }
  
  public void addShadow(Identifier paramIdentifier)
  {
    if ((isPreserved()) && (!paramIdentifier.isPreserved())) {
      paramIdentifier.setPreserved();
    } else if ((!isPreserved()) && (paramIdentifier.isPreserved())) {
      setPreserved();
    }
    for (Identifier localIdentifier1 = this; localIdentifier1.right != null; localIdentifier1 = localIdentifier1.right) {}
    for (Identifier localIdentifier2 = paramIdentifier; localIdentifier2.right != null; localIdentifier2 = localIdentifier2.right) {}
    if (localIdentifier2 == localIdentifier1) {
      return;
    }
    while (paramIdentifier.left != null) {
      paramIdentifier = paramIdentifier.left;
    }
    localIdentifier1.right = paramIdentifier;
    paramIdentifier.left = localIdentifier1;
  }
  
  public void buildTable(Renamer paramRenamer)
  {
    if ((!isReachable()) && ((Main.stripping & 0x1) != 0)) {
      return;
    }
    if (isPreserved())
    {
      if (GlobalOptions.verboseLevel > 4) {
        GlobalOptions.err.println(toString() + " is preserved");
      }
    }
    else
    {
      localObject1 = getRepresentative();
      if (!((Identifier)localObject1).wasAliased)
      {
        ((Identifier)localObject1).wasAliased = true;
        ((Identifier)localObject1).alias = "";
        Iterator localIterator = paramRenamer.generateNames(this);
        String str = (String)localIterator.next();
        for (Object localObject2 = localObject1;; localObject2 = ((Identifier)localObject2).right)
        {
          if (localObject2 == null) {
            break label134;
          }
          if (((Identifier)localObject2).conflicting(str)) {
            break;
          }
        }
        label134:
        setAlias(str.toString());
      }
    }
    Object localObject1 = getChilds();
    while (((Iterator)localObject1).hasNext()) {
      ((Identifier)((Iterator)localObject1).next()).buildTable(paramRenamer);
    }
  }
  
  public void writeTable(Map paramMap, boolean paramBoolean)
  {
    if ((!isReachable()) && ((Main.stripping & 0x1) != 0)) {
      return;
    }
    if (getAlias().length() != 0)
    {
      localObject = getName();
      for (Identifier localIdentifier = getParent(); (localIdentifier != null) && (localIdentifier.getAlias().length() == 0); localIdentifier = localIdentifier.getParent()) {
        if (localIdentifier.getName().length() > 0) {
          localObject = localIdentifier.getName() + "." + (String)localObject;
        }
      }
      if (paramBoolean) {
        paramMap.put(getFullAlias(), localObject);
      } else {
        paramMap.put(getFullName(), getAlias());
      }
    }
    Object localObject = getChilds();
    while (((Iterator)localObject).hasNext()) {
      ((Identifier)((Iterator)localObject).next()).writeTable(paramMap, paramBoolean);
    }
  }
  
  public void readTable(Map paramMap)
  {
    Identifier localIdentifier = getRepresentative();
    if (!localIdentifier.wasAliased)
    {
      localObject = (String)paramMap.get(getFullName());
      if (localObject != null)
      {
        localIdentifier.wasAliased = true;
        localIdentifier.setAlias((String)localObject);
      }
    }
    Object localObject = getChilds();
    while (((Iterator)localObject).hasNext()) {
      ((Identifier)((Iterator)localObject).next()).readTable(paramMap);
    }
  }
  
  public void applyPreserveRule(IdentifierMatcher paramIdentifierMatcher)
  {
    if (paramIdentifierMatcher.matches(this))
    {
      System.err.println("preserving: " + this);
      setReachable();
      for (localObject = this; localObject != null; localObject = ((Identifier)localObject).getParent()) {
        ((Identifier)localObject).setPreserved();
      }
    }
    Object localObject = getChilds();
    while (((Iterator)localObject).hasNext()) {
      ((Identifier)((Iterator)localObject).next()).applyPreserveRule(paramIdentifierMatcher);
    }
  }
  
  public abstract Iterator getChilds();
  
  public abstract Identifier getParent();
  
  public abstract String getName();
  
  public abstract String getType();
  
  public abstract String getFullName();
  
  public abstract String getFullAlias();
  
  public abstract boolean conflicting(String paramString);
  
  public void analyze() {}
}


