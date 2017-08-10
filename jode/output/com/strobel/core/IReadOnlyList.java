/* IReadOnlyList - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.core;
import java.util.ListIterator;
import java.util.RandomAccess;

public interface IReadOnlyList extends Iterable, RandomAccess
{
    public int size();
    
    public int indexOf(Object object);
    
    public int lastIndexOf(Object object);
    
    public boolean isEmpty();
    
    public boolean contains(Object object);
    
    public boolean containsAll(Iterable iterable);
    
    public Object get(int i);
    
    public Object[] toArray();
    
    public Object[] toArray(Object[] objects);
    
    public ListIterator listIterator();
    
    public ListIterator listIterator(int i);
}
