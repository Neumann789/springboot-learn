/* FrugalKeyMap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.componentmodel;

public interface FrugalKeyMap
{
    public static final FrugalKeyMap EMPTY = new EmptyKeyMap();
    
    public FrugalKeyMap plus(Key key, Object object);
    
    public FrugalKeyMap minus(Key key);
    
    public Object get(Key key);
    
    public String toString();
    
    public boolean isEmpty();
}
