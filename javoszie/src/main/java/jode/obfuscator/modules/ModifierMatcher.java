package jode.obfuscator.modules;

import java.util.Collection;
import java.util.Iterator;
import jode.obfuscator.ClassIdentifier;
import jode.obfuscator.FieldIdentifier;
import jode.obfuscator.Identifier;
import jode.obfuscator.IdentifierMatcher;
import jode.obfuscator.MethodIdentifier;
import jode.obfuscator.OptionHandler;

public class ModifierMatcher
  implements IdentifierMatcher, OptionHandler, Cloneable
{
  static final int PUBLIC = 1;
  static final int PROTECTED = 4;
  static final int PRIVATE = 2;
  int[] andMasks;
  int[] xorMasks;
  public static ModifierMatcher denyAll = new ModifierMatcher(new int[0], new int[0]);
  public static ModifierMatcher allowAll = new ModifierMatcher(0, 0);
  
  public ModifierMatcher()
  {
    this(0, 0);
  }
  
  private ModifierMatcher(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    this.andMasks = paramArrayOfInt1;
    this.xorMasks = paramArrayOfInt2;
  }
  
  public ModifierMatcher(int paramInt1, int paramInt2)
  {
    this.andMasks = new int[] { paramInt1 };
    this.xorMasks = new int[] { paramInt2 };
  }
  
  public void setOption(String paramString, Collection paramCollection)
  {
    ModifierMatcher localModifierMatcher = this;
    Iterator localIterator;
    String str;
    int i;
    if (paramString.equals("access"))
    {
      localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        str = (String)localIterator.next();
        i = str.charAt(0) == '<' ? 1 : 0;
        boolean bool = str.charAt(0) == '>';
        if ((i != 0) || (bool)) {
          str = str.substring(1);
        }
        str = str.toUpperCase();
        int k;
        if (i != 0)
        {
          k = str.equals("PRIVATE") ? 0 : str.equals("PACKAGE") ? 4 : str.equals("PROTECTED") ? 1 : -1;
          if (k == -1) {
            throw new IllegalArgumentException("Unknown access modifier " + str);
          }
          localModifierMatcher = localModifierMatcher.forbidAccess(k, true);
        }
        else
        {
          k = str.equals("PRIVATE") ? 2 : str.equals("PACKAGE") ? 0 : str.equals("PROTECTED") ? 4 : str.equals("PUBLIC") ? 1 : -1;
          if (k == -1) {
            throw new IllegalArgumentException("Unknown access " + str);
          }
          localModifierMatcher = localModifierMatcher.forceAccess(k, bool);
        }
      }
    }
    else if (paramString.equals("modifier"))
    {
      localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        str = (String)localIterator.next();
        i = str.charAt(0) == '!' ? 1 : 0;
        if (i != 0) {
          str = str.substring(1);
        }
        str = str.toUpperCase();
        int j = str.equals("VOLATILE") ? 64 : str.equals("TRANSIENT") ? 128 : str.equals("SYNCHRONIZED") ? 32 : str.equals("STRICT") ? 2048 : str.equals("STATIC") ? 8 : str.equals("NATIVE") ? 256 : str.equals("INTERFACE") ? 512 : str.equals("FINAL") ? 16 : str.equals("ABSTRACT") ? 1024 : -1;
        if (j == -1) {
          throw new IllegalArgumentException("Unknown modifier " + str);
        }
        if (i != 0) {
          localModifierMatcher = localModifierMatcher.forbidModifier(j);
        } else {
          localModifierMatcher = localModifierMatcher.forceModifier(j);
        }
      }
    }
    else
    {
      throw new IllegalArgumentException("Invalid option `" + paramString + "'.");
    }
    this.andMasks = localModifierMatcher.andMasks;
    this.xorMasks = localModifierMatcher.xorMasks;
  }
  
  private static boolean implies(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return ((paramInt1 & paramInt3) == paramInt3) && ((paramInt2 & paramInt3) == paramInt4);
  }
  
  private boolean implies(int paramInt1, int paramInt2)
  {
    for (int i = 0; i < this.andMasks.length; i++) {
      if (!implies(this.andMasks[i], this.xorMasks[i], paramInt1, paramInt2)) {
        return false;
      }
    }
    return true;
  }
  
  private boolean impliedBy(int paramInt1, int paramInt2)
  {
    for (int i = 0; i < this.andMasks.length; i++) {
      if (implies(paramInt1, paramInt2, this.andMasks[i], this.xorMasks[i])) {
        return true;
      }
    }
    return false;
  }
  
  private boolean implies(ModifierMatcher paramModifierMatcher)
  {
    for (int i = 0; i < this.andMasks.length; i++) {
      if (!paramModifierMatcher.impliedBy(this.andMasks[i], this.xorMasks[i])) {
        return false;
      }
    }
    return true;
  }
  
  public ModifierMatcher and(ModifierMatcher paramModifierMatcher)
  {
    if (implies(paramModifierMatcher)) {
      return this;
    }
    if (paramModifierMatcher.implies(this)) {
      return paramModifierMatcher;
    }
    ModifierMatcher localModifierMatcher = denyAll;
    for (int i = 0; i < this.andMasks.length; i++) {
      localModifierMatcher = localModifierMatcher.or(paramModifierMatcher.and(this.andMasks[i], this.xorMasks[i]));
    }
    return localModifierMatcher;
  }
  
  public ModifierMatcher or(ModifierMatcher paramModifierMatcher)
  {
    if (implies(paramModifierMatcher)) {
      return paramModifierMatcher;
    }
    if (paramModifierMatcher.implies(this)) {
      return this;
    }
    ModifierMatcher localModifierMatcher = this;
    for (int i = 0; i < paramModifierMatcher.andMasks.length; i++) {
      localModifierMatcher = localModifierMatcher.or(paramModifierMatcher.andMasks[i], paramModifierMatcher.xorMasks[i]);
    }
    return localModifierMatcher;
  }
  
  private ModifierMatcher and(int paramInt1, int paramInt2)
  {
    if (implies(paramInt1, paramInt2)) {
      return this;
    }
    int i = 0;
    label121:
    for (int j = 0; j < this.andMasks.length; j++) {
      if (!implies(paramInt1, paramInt2, this.andMasks[j], this.xorMasks[j]))
      {
        for (int k = 0; k < this.andMasks.length; k++) {
          if ((k != j) && (implies(paramInt1 | this.andMasks[k], paramInt2 | this.xorMasks[k], this.andMasks[j], this.xorMasks[j]))) {
            break label121;
          }
        }
        i++;
      }
    }
    if (i == 0) {
      return new ModifierMatcher(paramInt1, paramInt2);
    }
    int[] arrayOfInt1 = new int[i];
    int[] arrayOfInt2 = new int[i];
    int m = 0;
    label286:
    for (int n = 0; n < i; n++) {
      if (!implies(paramInt1, paramInt2, this.andMasks[n], this.xorMasks[n]))
      {
        for (int i1 = 0; i1 < this.andMasks.length; i1++) {
          if ((i1 != n) && (implies(paramInt1 | this.andMasks[i1], paramInt2 | this.xorMasks[i1], this.andMasks[n], this.xorMasks[n]))) {
            break label286;
          }
        }
        arrayOfInt1[m] = (this.andMasks[n] | paramInt1);
        arrayOfInt2[m] = (this.xorMasks[n] | paramInt2);
        m++;
      }
    }
    return new ModifierMatcher(arrayOfInt1, arrayOfInt2);
  }
  
  private ModifierMatcher or(int paramInt1, int paramInt2)
  {
    int i = -1;
    if (this == denyAll) {
      return new ModifierMatcher(paramInt1, paramInt2);
    }
    for (int j = 0; j < this.andMasks.length; j++)
    {
      if (implies(paramInt1, paramInt2, this.andMasks[j], this.xorMasks[j])) {
        return this;
      }
      if (implies(this.andMasks[j], this.xorMasks[j], paramInt1, paramInt2))
      {
        i = j;
        break;
      }
    }
    int[] arrayOfInt1;
    int[] arrayOfInt2;
    if (i == -1)
    {
      i = this.andMasks.length;
      arrayOfInt1 = new int[i + 1];
      arrayOfInt2 = new int[i + 1];
      System.arraycopy(this.andMasks, 0, arrayOfInt1, 0, i);
      System.arraycopy(this.xorMasks, 0, arrayOfInt2, 0, i);
    }
    else
    {
      arrayOfInt1 = (int[])this.andMasks.clone();
      arrayOfInt2 = (int[])this.xorMasks.clone();
    }
    arrayOfInt1[i] = paramInt1;
    arrayOfInt2[i] = paramInt2;
    return new ModifierMatcher(arrayOfInt1, arrayOfInt2);
  }
  
  public ModifierMatcher forceAccess(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (paramInt == 2) {
        return this;
      }
      if (paramInt == 0) {
        return and(2, 0);
      }
      ModifierMatcher localModifierMatcher = and(1, 1);
      if (paramInt == 4) {
        return localModifierMatcher.or(and(4, 4));
      }
      if (paramInt == 1) {
        return localModifierMatcher;
      }
      throw new IllegalArgumentException("" + paramInt);
    }
    if (paramInt == 0) {
      return and(7, 0);
    }
    return and(paramInt, paramInt);
  }
  
  public ModifierMatcher forbidAccess(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (paramInt == 2) {
        return denyAll;
      }
      if (paramInt == 0) {
        return and(2, 2);
      }
      if (paramInt == 4) {
        return and(5, 0);
      }
      if (paramInt == 1) {
        return and(1, 0);
      }
      throw new IllegalArgumentException("" + paramInt);
    }
    if (paramInt == 0) {
      return and(2, 2).or(and(4, 4)).or(and(1, 1));
    }
    return and(paramInt, 0);
  }
  
  public final ModifierMatcher forceModifier(int paramInt)
  {
    return and(paramInt, paramInt);
  }
  
  public final ModifierMatcher forbidModifier(int paramInt)
  {
    return and(paramInt, 0);
  }
  
  public final boolean matches(int paramInt)
  {
    for (int i = 0; i < this.andMasks.length; i++) {
      if ((paramInt & this.andMasks[i]) == this.xorMasks[i]) {
        return true;
      }
    }
    return false;
  }
  
  public final boolean matches(Identifier paramIdentifier)
  {
    int i;
    if ((paramIdentifier instanceof ClassIdentifier)) {
      i = ((ClassIdentifier)paramIdentifier).getModifiers();
    } else if ((paramIdentifier instanceof MethodIdentifier)) {
      i = ((MethodIdentifier)paramIdentifier).getModifiers();
    } else if ((paramIdentifier instanceof FieldIdentifier)) {
      i = ((FieldIdentifier)paramIdentifier).getModifiers();
    } else {
      return false;
    }
    return matches(i);
  }
  
  public final boolean matchesSub(Identifier paramIdentifier, String paramString)
  {
    return true;
  }
  
  public final String getNextComponent(Identifier paramIdentifier)
  {
    return null;
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new IncompatibleClassChangeError(getClass().getName());
    }
  }
}


