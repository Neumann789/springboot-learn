/* AlwaysEmptySet - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.github.jamm;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;

public class AlwaysEmptySet implements Set
{
    public static final Set EMPTY_SET = new AlwaysEmptySet();
    
    private AlwaysEmptySet() {
	/* empty */
    }
    
    public static Set create() {
	return EMPTY_SET;
    }
    
    public static Callable provider() {
	return new Callable() {
	    public Set call() throws Exception {
		return create();
	    }
	    
	    public volatile Object call() throws Exception {
		return call();
	    }
	};
    }
    
    public int size() {
	return 0;
    }
    
    public boolean isEmpty() {
	return true;
    }
    
    public boolean contains(Object o) {
	return false;
    }
    
    public Iterator iterator() {
	return Collections.emptySet().iterator();
    }
    
    public Object[] toArray() {
	return new Object[0];
    }
    
    public Object[] toArray(Object[] a) {
	return Collections.emptySet().toArray();
    }
    
    public boolean add(Object t) {
	return false;
    }
    
    public boolean remove(Object o) {
	return false;
    }
    
    public boolean containsAll(Collection c) {
	return false;
    }
    
    public boolean addAll(Collection c) {
	return false;
    }
    
    public boolean retainAll(Collection c) {
	return false;
    }
    
    public boolean removeAll(Collection c) {
	return false;
    }
    
    public void clear() {
	/* empty */
    }
}
