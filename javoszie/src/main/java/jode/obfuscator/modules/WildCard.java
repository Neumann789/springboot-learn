package jode.obfuscator.modules;

import java.util.Collection;
import java.util.Iterator;
import jode.obfuscator.Identifier;
import jode.obfuscator.IdentifierMatcher;
import jode.obfuscator.OptionHandler;

public class WildCard
  implements IdentifierMatcher, OptionHandler
{
  String wildcard;
  int firstStar;
  
  public WildCard() {}
  
  public WildCard(String paramString)
  {
    this.wildcard = paramString;
    this.firstStar = this.wildcard.indexOf('*');
  }
  
  public void setOption(String paramString, Collection paramCollection)
  {
    if (paramString.equals("value"))
    {
      if (paramCollection.size() != 1) {
        throw new IllegalArgumentException("Wildcard supports only one value.");
      }
      this.wildcard = ((String)paramCollection.iterator().next());
      this.firstStar = this.wildcard.indexOf('*');
    }
    else
    {
      throw new IllegalArgumentException("Invalid option `" + paramString + "'.");
    }
  }
  
  public String getNextComponent(Identifier paramIdentifier)
  {
    String str = paramIdentifier.getFullName();
    if (str.length() > 0) {
      str = str + ".";
    }
    int i = str.length();
    if (!this.wildcard.startsWith(str)) {
      return null;
    }
    int j = this.wildcard.indexOf('.', i);
    if ((j > 0) && ((j <= this.firstStar) || (this.firstStar == -1))) {
      return this.wildcard.substring(i, j);
    }
    if (this.firstStar == -1) {
      return this.wildcard.substring(i);
    }
    return null;
  }
  
  public boolean matchesSub(Identifier paramIdentifier, String paramString)
  {
    String str = paramIdentifier.getFullName();
    if (str.length() > 0) {
      str = str + ".";
    }
    if (paramString != null) {
      str = str + paramString;
    }
    if ((this.firstStar == -1) || (this.firstStar >= str.length())) {
      return this.wildcard.startsWith(str);
    }
    return str.startsWith(this.wildcard.substring(0, this.firstStar));
  }
  
  public boolean matches(Identifier paramIdentifier)
  {
    String str1 = paramIdentifier.getFullName();
    if (this.firstStar == -1) {
      return this.wildcard.equals(str1);
    }
    if (!str1.startsWith(this.wildcard.substring(0, this.firstStar))) {
      return false;
    }
    str1 = str1.substring(this.firstStar);
    int j;
    for (int i = this.firstStar; (j = this.wildcard.indexOf('*', i + 1)) != -1; i = j)
    {
      String str2 = this.wildcard.substring(i + 1, j);
      while (!str1.startsWith(str2))
      {
        if (str1.length() == 0) {
          return false;
        }
        str1 = str1.substring(1);
      }
      str1 = str1.substring(j - i - 1);
    }
    return str1.endsWith(this.wildcard.substring(i + 1));
  }
  
  public String toString()
  {
    return "Wildcard " + this.wildcard;
  }
}


