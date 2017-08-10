/* IFreezable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.core;

public interface IFreezable
{
    public boolean canFreeze();
    
    public boolean isFrozen();
    
    public void freeze() throws IllegalStateException;
    
    public boolean tryFreeze();
    
    public void freezeIfUnfrozen() throws IllegalStateException;
}
