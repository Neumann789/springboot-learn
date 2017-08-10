package jode.obfuscator.modules;

import java.util.Iterator;
import jode.obfuscator.Identifier;
import jode.obfuscator.Renamer;

public class UniqueRenamer
  implements Renamer
{
  static int serialnr = 0;
  
  public Iterator generateNames(Identifier paramIdentifier)
  {
    new Iterator()
    {
      public boolean hasNext()
      {
        return true;
      }
      
      public Object next()
      {
        return "xxx" + UniqueRenamer.serialnr++;
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
}


