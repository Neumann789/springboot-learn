package jode.obfuscator.modules;

import java.util.Collection;
import java.util.Iterator;
import jode.obfuscator.Identifier;
import jode.obfuscator.OptionHandler;
import jode.obfuscator.Renamer;

public class KeywordRenamer
  implements Renamer, OptionHandler
{
  String[] keywords = { "if", "else", "for", "while", "throw", "return", "class", "interface", "implements", "extends", "instanceof", "new", "int", "boolean", "long", "float", "double", "short", "public", "protected", "private", "static", "synchronized", "strict", "transient", "abstract", "volatile", "final", "Object", "String", "Thread", "Runnable", "StringBuffer", "Vector" };
  Renamer backup = new StrongRenamer();
  
  public void setOption(String paramString, Collection paramCollection)
  {
    if (paramString.startsWith("keywords"))
    {
      this.keywords = ((String[])paramCollection.toArray(new String[paramCollection.size()]));
    }
    else if (paramString.startsWith("backup"))
    {
      if (paramCollection.size() != 1) {
        throw new IllegalArgumentException("Only one backup is allowed");
      }
      this.backup = ((Renamer)paramCollection.iterator().next());
    }
    else
    {
      throw new IllegalArgumentException("Invalid option `" + paramString + "'");
    }
  }
  
  public Iterator generateNames(final Identifier paramIdentifier)
  {
    new Iterator()
    {
      int pos = 0;
      Iterator backing = null;
      
      public boolean hasNext()
      {
        return true;
      }
      
      public Object next()
      {
        if (this.pos < KeywordRenamer.this.keywords.length) {
          return KeywordRenamer.this.keywords[(this.pos++)];
        }
        if (this.backing == null) {
          this.backing = KeywordRenamer.this.backup.generateNames(paramIdentifier);
        }
        return this.backing.next();
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
}


