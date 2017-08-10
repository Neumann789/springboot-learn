/* Callback - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.fusesource.hawtjni.runtime;

public class Callback
{
    Object object;
    String method;
    String signature;
    int argCount;
    long address;
    long errorResult;
    boolean isStatic;
    boolean isArrayBased;
    static final String PTR_SIGNATURE = "J";
    static final String SIGNATURE_0 = getSignature(0);
    static final String SIGNATURE_1 = getSignature(1);
    static final String SIGNATURE_2 = getSignature(2);
    static final String SIGNATURE_3 = getSignature(3);
    static final String SIGNATURE_4 = getSignature(4);
    static final String SIGNATURE_N = "([J)J";
    
    public Callback(Object object, String method, int argCount) {
	this(object, method, argCount, false);
    }
    
    public Callback(Object object, String method, int argCount,
		    boolean isArrayBased) {
	this(object, method, argCount, isArrayBased, 0L);
    }
    
    public Callback(Object object, String method, int argCount,
		    boolean isArrayBased, long errorResult) {
	this.object = object;
	this.method = method;
	this.argCount = argCount;
	isStatic = object instanceof Class;
	this.isArrayBased = isArrayBased;
    label_1881:
	{
	    this.errorResult = errorResult;
	    if (!isArrayBased) {
		switch (argCount) {
		case 0:
		    signature = SIGNATURE_0;
		    break label_1881;
		case 1:
		    signature = SIGNATURE_1;
		    break;
		case 2:
		    signature = SIGNATURE_2;
		    break;
		case 3:
		    signature = SIGNATURE_3;
		    break;
		case 4:
		    signature = SIGNATURE_4;
		    break;
		default:
		    signature = getSignature(argCount);
		}
	    } else
		signature = "([J)J";
	    break label_1881;
	}
	address = bind(this, object, method, signature, argCount, isStatic,
		       isArrayBased, errorResult);
    }
    
    static synchronized native long bind(Callback callback, Object object,
					 String string, String string_0_,
					 int i, boolean bool, boolean bool_1_,
					 long l);
    
    public void dispose() {
	if (object != null) {
	    unbind(this);
	    object = method = signature = null;
	    address = 0L;
	}
	return;
    }
    
    public long getAddress() {
	return address;
    }
    
    public static native String getPlatform();
    
    public static native int getEntryCount();
    
    static String getSignature(int argCount) {
	String signature = "(";
	int i = 0;
	for (;;) {
	    if (i >= argCount) {
		signature += ")J";
		return signature;
	    }
	    signature += "J";
	    i++;
	}
    }
    
    public static final synchronized native void setEnabled(boolean bool);
    
    public static final synchronized native boolean getEnabled();
    
    public static final synchronized native void reset();
    
    static final synchronized native void unbind(Callback callback);
}
