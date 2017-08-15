/* FactoryImpl - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.github.jamm;

public class FactoryImpl implements MemoryMeterListener.Factory
{
    private static MemoryMeterListener inst;
    
    public FactoryImpl(MemoryMeterListener inst) {
	if (this != null) {
	    /* empty */
	}
	FactoryImpl.inst = inst;
    }
    
    public MemoryMeterListener newInstance() {
	return inst;
    }
}
