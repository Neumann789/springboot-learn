/* ArgFlag - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.fusesource.hawtjni.runtime;

public final class ArgFlag extends Enum
{
    public static final ArgFlag NO_IN = new ArgFlag("NO_IN", 0);
    public static final ArgFlag NO_OUT = new ArgFlag("NO_OUT", 1);
    public static final ArgFlag CRITICAL = new ArgFlag("CRITICAL", 2);
    public static final ArgFlag INIT = new ArgFlag("INIT", 3);
    public static final ArgFlag POINTER_ARG = new ArgFlag("POINTER_ARG", 4);
    public static final ArgFlag BY_VALUE = new ArgFlag("BY_VALUE", 5);
    public static final ArgFlag UNICODE = new ArgFlag("UNICODE", 6);
    public static final ArgFlag SENTINEL = new ArgFlag("SENTINEL", 7);
    public static final ArgFlag CS_OBJECT = new ArgFlag("CS_OBJECT", 8);
    /*synthetic*/ private static final ArgFlag[] $VALUES
		      = { NO_IN, NO_OUT, CRITICAL, INIT, POINTER_ARG, BY_VALUE,
			  UNICODE, SENTINEL, CS_OBJECT };
    
    public static ArgFlag[] values() {
	return (ArgFlag[]) $VALUES.clone();
    }
    
    public static ArgFlag valueOf(String name) {
	return (ArgFlag) Enum.valueOf(ArgFlag.class, name);
    }
    
    private ArgFlag(String string, int i) {
	super(string, i);
    }
}
