package jode.obfuscator.modules;

import java.util.Collection;
import jode.obfuscator.ClassIdentifier;
import jode.obfuscator.FieldIdentifier;
import jode.obfuscator.Identifier;
import jode.obfuscator.IdentifierMatcher;
import jode.obfuscator.MethodIdentifier;
import jode.obfuscator.OptionHandler;
import jode.obfuscator.PackageIdentifier;

public class SerializePreserver
  implements IdentifierMatcher, OptionHandler
{
  boolean onlySUID = true;
  
  public void setOption(String paramString, Collection paramCollection)
  {
    if (paramString.equals("all")) {
      this.onlySUID = false;
    } else {
      throw new IllegalArgumentException("Invalid option `" + paramString + "'.");
    }
  }
  
  public final boolean matchesSub(Identifier paramIdentifier, String paramString)
  {
    if ((paramIdentifier instanceof PackageIdentifier)) {
      return true;
    }
    if ((paramIdentifier instanceof ClassIdentifier))
    {
      ClassIdentifier localClassIdentifier = (ClassIdentifier)paramIdentifier;
      return (localClassIdentifier.isSerializable()) && ((!this.onlySUID) || (localClassIdentifier.hasSUID()));
    }
    return false;
  }
  
  public final boolean matches(Identifier paramIdentifier)
  {
    ClassIdentifier localClassIdentifier;
    if ((paramIdentifier instanceof ClassIdentifier)) {
      localClassIdentifier = (ClassIdentifier)paramIdentifier;
    } else if ((paramIdentifier instanceof FieldIdentifier)) {
      localClassIdentifier = (ClassIdentifier)paramIdentifier.getParent();
    } else {
      return false;
    }
    if ((!localClassIdentifier.isSerializable()) || ((this.onlySUID) && (!localClassIdentifier.hasSUID()))) {
      return false;
    }
    if ((paramIdentifier instanceof FieldIdentifier))
    {
      FieldIdentifier localFieldIdentifier = (FieldIdentifier)paramIdentifier;
      if ((localFieldIdentifier.getModifiers() & 0x88) == 0) {
        return true;
      }
      if ((paramIdentifier.getName().equals("serialPersistentFields")) || (paramIdentifier.getName().equals("serialVersionUID"))) {
        return true;
      }
    }
    else if ((paramIdentifier instanceof MethodIdentifier))
    {
      if ((paramIdentifier.getName().equals("writeObject")) && (paramIdentifier.getType().equals("(Ljava.io.ObjectOutputStream)V"))) {
        return true;
      }
      if ((paramIdentifier.getName().equals("readObject")) && (paramIdentifier.getType().equals("(Ljava.io.ObjectInputStream)V"))) {
        return true;
      }
    }
    else if ((paramIdentifier instanceof ClassIdentifier))
    {
      if (!localClassIdentifier.hasSUID()) {
        localClassIdentifier.addSUID();
      }
      return true;
    }
    return false;
  }
  
  public final String getNextComponent(Identifier paramIdentifier)
  {
    return null;
  }
}


