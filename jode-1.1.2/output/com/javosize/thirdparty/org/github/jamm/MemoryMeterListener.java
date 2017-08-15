/* MemoryMeterListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.github.jamm;

public interface MemoryMeterListener
{
    public static interface Factory
    {
	public MemoryMeterListener newInstance();
    }
    
    public void started(Object object);
    
    public void fieldAdded(Object object, String string, Object object_0_);
    
    public void objectMeasured(Object object, long l);
    
    public void objectCounted(Object object);
    
    public void done(long l);
}
