/* Method - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.javosize.thirdparty.org.objectweb.asm.Type;

public class Method
{
    private final String name;
    private final String desc;
    private static final Map DESCRIPTORS;
    
    public Method(String string, String string_0_) {
	name = string;
	desc = string_0_;
    }
    
    public Method(String string, Type type, Type[] types) {
	this(string, Type.getMethodDescriptor(type, types));
    }
    
    public static Method getMethod(Method method) {
	return new Method(method.getName(), Type.getMethodDescriptor(method));
    }
    
    public static Method getMethod(Constructor constructor) {
	return new Method("<init>",
			  Type.getConstructorDescriptor(constructor));
    }
    
    public static Method getMethod(String string)
	throws IllegalArgumentException {
	return getMethod(string, false);
    }
    
    public static Method getMethod(String string, boolean bool)
	throws IllegalArgumentException {
	int i = string.indexOf(' ');
	int i_1_ = string.indexOf('(', i) + 1;
	int i_2_ = string.indexOf(')', i_1_);
	if (i != -1 && i_1_ != -1 && i_2_ != -1) {
	    String string_3_ = string.substring(0, i);
	    String string_4_ = string.substring(i + 1, i_1_ - 1).trim();
	    StringBuffer stringbuffer = new StringBuffer();
	    stringbuffer.append('(');
	    for (;;) {
		int i_5_;
		String string_6_;
	    label_401:
		{
		    i_5_ = string.indexOf(',', i_1_);
		    if (i_5_ != -1) {
			string_6_
			    = map(string.substring(i_1_, i_5_).trim(), bool);
			i_1_ = i_5_ + 1;
		    } else
			string_6_
			    = map(string.substring(i_1_, i_2_).trim(), bool);
		    break label_401;
		}
		stringbuffer.append(string_6_);
		if (i_5_ == -1) {
		    stringbuffer.append(')');
		    stringbuffer.append(map(string_3_, bool));
		    return new Method(string_4_, stringbuffer.toString());
		}
	    }
	}
	throw new IllegalArgumentException();
    }
    
    private static String map(String string, boolean bool) {
	StringBuffer stringbuffer;
    label_404:
	{
	label_403:
	    {
		String string_7_;
	    label_402:
		{
		    if (!"".equals(string)) {
			stringbuffer = new StringBuffer();
			int i = 0;
			for (;;) {
			    if ((i = string.indexOf("[]", i) + 1) <= 0) {
				string_7_
				    = string.substring(0,
						       (string.length()
							- stringbuffer
							      .length() * 2));
				String string_8_
				    = (String) DESCRIPTORS.get(string_7_);
				if (string_8_ == null) {
				    stringbuffer.append('L');
				    if (string_7_.indexOf('.') >= 0) {
					stringbuffer.append
					    (string_7_.replace('.', '/'));
					break label_403;
				    }
				    if (!bool)
					stringbuffer.append("java/lang/");
				} else {
				    stringbuffer.append(string_8_);
				    break label_404;
				}
				break label_402;
			    }
			    stringbuffer.append('[');
			}
			break label_404;
			break label_403;
		    } else
			return string;
		}
		stringbuffer.append(string_7_);
	    }
	    stringbuffer.append(';');
	}
	return stringbuffer.toString();
	break label_402;
    }
    
    public String getName() {
	return name;
    }
    
    public String getDescriptor() {
	return desc;
    }
    
    public Type getReturnType() {
	return Type.getReturnType(desc);
    }
    
    public Type[] getArgumentTypes() {
	return Type.getArgumentTypes(desc);
    }
    
    public String toString() {
	return name + desc;
    }
    
    public boolean equals(Object object) {
    label_405:
	{
	    if (object instanceof Method) {
		Method method_9_ = (Method) object;
		if (!name.equals(method_9_.name)
		    || !desc.equals(method_9_.desc))
		    PUSH false;
		else
		    PUSH true;
	    } else
		return false;
	}
	return POP;
	break label_405;
    }
    
    public int hashCode() {
	return name.hashCode() ^ desc.hashCode();
    }
    
    static {
	_clinit_();
	DESCRIPTORS = new HashMap();
	DESCRIPTORS.put("void", "V");
	DESCRIPTORS.put("byte", "B");
	DESCRIPTORS.put("char", "C");
	DESCRIPTORS.put("double", "D");
	DESCRIPTORS.put("float", "F");
	DESCRIPTORS.put("int", "I");
	DESCRIPTORS.put("long", "J");
	DESCRIPTORS.put("short", "S");
	DESCRIPTORS.put("boolean", "Z");
    }
    
    /*synthetic*/ static void _clinit_() {
	/* empty */
    }
}
