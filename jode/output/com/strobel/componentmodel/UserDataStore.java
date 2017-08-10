/* UserDataStore - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.componentmodel;

public interface UserDataStore
{
    public Object getUserData(Key key);
    
    public void putUserData(Key key, Object object);
    
    public Object putUserDataIfAbsent(Key key, Object object);
    
    public boolean replace(Key key, Object object, Object object_0_);
}
