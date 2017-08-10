package jode.obfuscator.modules;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import jode.obfuscator.ClassIdentifier;
import jode.obfuscator.FieldIdentifier;
import jode.obfuscator.Identifier;
import jode.obfuscator.LocalIdentifier;
import jode.obfuscator.MethodIdentifier;
import jode.obfuscator.PackageIdentifier;
import jode.obfuscator.Renamer;

public class NameSwapper
  implements Renamer
{
  private Random rand;
  private Set packs;
  private Set clazzes;
  private Set methods;
  private Set fields;
  private Set locals;
  
  public NameSwapper(boolean paramBoolean, long paramLong)
  {
    if (paramBoolean)
    {
      this.packs = (this.clazzes = this.methods = this.fields = this.locals = new HashSet());
    }
    else
    {
      this.packs = new HashSet();
      this.clazzes = new HashSet();
      this.methods = new HashSet();
      this.fields = new HashSet();
      this.locals = new HashSet();
    }
  }
  
  public NameSwapper(boolean paramBoolean)
  {
    this(paramBoolean, System.currentTimeMillis());
  }
  
  public final Collection getCollection(Identifier paramIdentifier)
  {
    if ((paramIdentifier instanceof PackageIdentifier)) {
      return this.packs;
    }
    if ((paramIdentifier instanceof ClassIdentifier)) {
      return this.clazzes;
    }
    if ((paramIdentifier instanceof MethodIdentifier)) {
      return this.methods;
    }
    if ((paramIdentifier instanceof FieldIdentifier)) {
      return this.fields;
    }
    if ((paramIdentifier instanceof LocalIdentifier)) {
      return this.locals;
    }
    throw new IllegalArgumentException(paramIdentifier.getClass().getName());
  }
  
  public final void addIdentifierName(Identifier paramIdentifier)
  {
    getCollection(paramIdentifier).add(paramIdentifier.getName());
  }
  
  public Iterator generateNames(Identifier paramIdentifier)
  {
    return new NameGenerator(getCollection(paramIdentifier));
  }
  
  private class NameGenerator
    implements Iterator
  {
    Collection pool;
    
    NameGenerator(Collection paramCollection)
    {
      this.pool = paramCollection;
    }
    
    public boolean hasNext()
    {
      return true;
    }
    
    public Object next()
    {
      int i = NameSwapper.this.rand.nextInt(this.pool.size());
      Iterator localIterator = this.pool.iterator();
      while (i > 0) {
        localIterator.next();
      }
      return (String)localIterator.next();
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
}


