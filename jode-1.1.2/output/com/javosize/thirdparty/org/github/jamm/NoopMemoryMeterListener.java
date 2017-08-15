/* NoopMemoryMeterListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.github.jamm;

public final class NoopMemoryMeterListener implements MemoryMeterListener
{
    private static final MemoryMeterListener INSTANCE
	= new NoopMemoryMeterListener();
    public static final MemoryMeterListener.Factory FACTORY
	= new FactoryImpl(INSTANCE);
    
    public void objectMeasured(Object current, long size) {
	/* empty */
    }
    
    public void fieldAdded(Object obj, String fieldName, Object fieldValue) {
	/* empty */
    }
    
    public void done(long size) {
	/* empty */
    }
    
    public void started(Object obj) {
	/* empty */
    }
    
    public void objectCounted(Object current) {
	/* empty */
    }
    
    private NoopMemoryMeterListener() {
	/* empty */
    }
}
