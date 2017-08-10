/* NameSwapper - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
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

public class NameSwapper implements Renamer
{
    private Random rand;
    private Set packs;
    private Set clazzes;
    private Set methods;
    private Set fields;
    private Set locals;
    
    private class NameGenerator implements Iterator
    {
	Collection pool;
	
	NameGenerator(Collection collection) {
	    super();
	    pool = collection;
	}
	
	public boolean hasNext() {
	    return true;
	}
	
	public Object next() {
	    int i = rand.nextInt(pool.size());
	    Iterator iterator = pool.iterator();
	    for (;;) {
		if (i <= 0)
		    return (String) iterator.next();
		iterator.next();
	    }
	}
	
	public void remove() {
	    throw new UnsupportedOperationException();
	}
    }
    
    public NameSwapper(boolean bool, long l) {
	if (!bool) {
	    packs = new HashSet();
	    clazzes = new HashSet();
	    methods = new HashSet();
	    fields = new HashSet();
	    locals = new HashSet();
	} else
	    packs = clazzes = methods = fields = locals = new HashSet();
	return;
    }
    
    public NameSwapper(boolean bool) {
	this(bool, System.currentTimeMillis());
    }
    
    public final Collection getCollection(Identifier identifier) {
	if (!(identifier instanceof PackageIdentifier)) {
	    if (!(identifier instanceof ClassIdentifier)) {
		if (!(identifier instanceof MethodIdentifier)) {
		    if (!(identifier instanceof FieldIdentifier)) {
			if (!(identifier instanceof LocalIdentifier))
			    throw new IllegalArgumentException(identifier
								   .getClass
								   ()
								   .getName());
			return locals;
		    }
		    return fields;
		}
		return methods;
	    }
	    return clazzes;
	}
	return packs;
    }
    
    public final void addIdentifierName(Identifier identifier) {
	getCollection(identifier).add(identifier.getName());
    }
    
    public Iterator generateNames(Identifier identifier) {
	return new NameGenerator(getCollection(identifier));
    }
}
