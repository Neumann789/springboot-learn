/* Method - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import java.lang.reflect.Constructor;
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
    
    public static Method getMethod(java.lang.reflect.Method method) {
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
	if (i == -1 || i_1_ == -1 || i_2_ == -1)
	    throw new IllegalArgumentException();
	String string_3_ = string.substring(0, i);
	String string_4_ = string.substring(i + 1, i_1_ - 1).trim();
	StringBuffer stringbuffer = new StringBuffer();
	stringbuffer.append('(');
	int i_5_;
	do {
	    i_5_ = string.indexOf(',', i_1_);
	    String string_6_;
	    if (i_5_ == -1)
		string_6_ = map(string.substring(i_1_, i_2_).trim(), bool);
	    else {
		string_6_ = map(string.substring(i_1_, i_5_).trim(), bool);
		i_1_ = i_5_ + 1;
	    }
	    stringbuffer.append(string_6_);
	} while (i_5_ != -1);
	stringbuffer.append(')');
	stringbuffer.append(map(string_3_, bool));
	return new Method(string_4_, stringbuffer.toString());
    }
    
    private static String map(String string, boolean bool) {
	if ("".equals(string))
	    return string;
	StringBuffer stringbuffer = new StringBuffer();
	int i = 0;
	while ((i = string.indexOf("[]", i) + 1) > 0)
	    stringbuffer.append('[');
	String string_7_
	    = string.substring(0, string.length() - stringbuffer.length() * 2);
	String string_8_ = (String) DESCRIPTORS.get(string_7_);
	if (string_8_ != null)
	    stringbuffer.append(string_8_);
	else {
	    stringbuffer.append('L');
	    if (string_7_.indexOf('.') < 0) {
		if (!bool)
		    stringbuffer.append("java/lang/");
		stringbuffer.append(string_7_);
	    } else
		stringbuffer.append(string_7_.replace('.', '/'));
	    stringbuffer.append(';');
	}
	return stringbuffer.toString();
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
	if (!(object instanceof Method))
	    return false;
	Method method_9_ = (Method) object;
	return name.equals(method_9_.name) && desc.equals(method_9_.desc);
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
