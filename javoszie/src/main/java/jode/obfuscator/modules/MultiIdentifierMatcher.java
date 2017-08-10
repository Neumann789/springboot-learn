package jode.obfuscator.modules;

import java.util.Collection;
import jode.obfuscator.Identifier;
import jode.obfuscator.IdentifierMatcher;
import jode.obfuscator.OptionHandler;

public class MultiIdentifierMatcher
  implements IdentifierMatcher, OptionHandler
{
  public static boolean OR = true;
  public static boolean AND = false;
  IdentifierMatcher[] matchers;
  boolean isOr;
  
  public MultiIdentifierMatcher()
  {
    this.matchers = new IdentifierMatcher[0];
  }
  
  public MultiIdentifierMatcher(boolean paramBoolean, IdentifierMatcher[] paramArrayOfIdentifierMatcher)
  {
    this.isOr = paramBoolean;
    this.matchers = paramArrayOfIdentifierMatcher;
  }
  
  public void setOption(String paramString, Collection paramCollection)
  {
    if (paramString.equals("or"))
    {
      this.isOr = true;
      this.matchers = ((IdentifierMatcher[])paramCollection.toArray(new IdentifierMatcher[paramCollection.size()]));
    }
    else if (paramString.equals("and"))
    {
      this.isOr = false;
      this.matchers = ((IdentifierMatcher[])paramCollection.toArray(new IdentifierMatcher[paramCollection.size()]));
    }
    else
    {
      throw new IllegalArgumentException("Invalid option `" + paramString + "'.");
    }
  }
  
  public boolean matches(Identifier paramIdentifier)
  {
    for (int i = 0; i < this.matchers.length; i++) {
      if (this.matchers[i].matches(paramIdentifier) == this.isOr) {
        return this.isOr;
      }
    }
    return !this.isOr;
  }
  
  public boolean matchesSub(Identifier paramIdentifier, String paramString)
  {
    for (int i = 0; i < this.matchers.length; i++) {
      if (this.matchers[i].matchesSub(paramIdentifier, paramString) == this.isOr) {
        return this.isOr;
      }
    }
    return !this.isOr;
  }
  
  public String getNextComponent(Identifier paramIdentifier)
  {
    if (this.isOr == AND)
    {
      for (int i = 0; i < this.matchers.length; i++)
      {
        String str = this.matchers[i].getNextComponent(paramIdentifier);
        if ((str != null) && (matchesSub(paramIdentifier, str))) {
          return str;
        }
      }
      return null;
    }
    Object localObject = null;
    for (int j = 0; j < this.matchers.length; j++) {
      if (matchesSub(paramIdentifier, null))
      {
        if ((localObject != null) && (!this.matchers[j].getNextComponent(paramIdentifier).equals(localObject))) {
          return null;
        }
        localObject = this.matchers[j].getNextComponent(paramIdentifier);
        if (localObject == null) {
          return null;
        }
      }
    }
    return (String)localObject;
  }
}


