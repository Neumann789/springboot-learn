/* SimpleVerifier - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree.analysis;
import java.util.List;

import com.javosize.thirdparty.org.objectweb.asm.Type;

public class SimpleVerifier extends BasicVerifier
{
    private final Type currentClass;
    private final Type currentSuperClass;
    private final List currentClassInterfaces;
    private final boolean isInterface;
    private ClassLoader loader = this.getClass().getClassLoader();
    /*synthetic*/ static Class class$java$lang$Object
		      = class$("java.lang.Object");
    
    public SimpleVerifier() {
	this(null, null, false);
    }
    
    public SimpleVerifier(Type type, Type type_0_, boolean bool) {
	this(type, type_0_, null, bool);
    }
    
    public SimpleVerifier(Type type, Type type_1_, List list, boolean bool) {
	this(327680, type, type_1_, list, bool);
    }
    
    protected SimpleVerifier(int i, Type type, Type type_2_, List list,
			     boolean bool) {
	super(i);
	currentClass = type;
	currentSuperClass = type_2_;
	currentClassInterfaces = list;
	isInterface = bool;
    }
    
    public void setClassLoader(ClassLoader classloader) {
	loader = classloader;
    }
    
    public BasicValue newValue(Type type) {
    label_518:
	{
	    if (type != null) {
		if (type.getSort() != 9)
		    PUSH false;
		else
		    PUSH true;
	    } else
		return BasicValue.UNINITIALIZED_VALUE;
	}
	boolean bool;
    label_520:
	{
	    bool = POP;
	    if (bool) {
		switch (type.getElementType().getSort()) {
		case 1:
		case 2:
		case 3:
		case 4:
		    return new BasicValue(type);
		}
	    }
	    break label_520;
	}
	BasicValue basicvalue;
    label_519:
	{
	    basicvalue = super.newValue(type);
	    if (BasicValue.REFERENCE_VALUE.equals(basicvalue)) {
		if (!bool)
		    basicvalue = new BasicValue(type);
		else {
		    basicvalue = newValue(type.getElementType());
		    String string = basicvalue.getType().getDescriptor();
		    int i = 0;
		    for (;;) {
			if (i >= type.getDimensions()) {
			    basicvalue = new BasicValue(Type.getType(string));
			    break label_519;
			}
			string = '[' + string;
			i++;
		    }
		}
	    }
	    break label_519;
	}
	return basicvalue;
	break label_518;
    }
    
    protected boolean isArrayValue(BasicValue basicvalue) {
    label_521:
	{
	    Type type = basicvalue.getType();
	    if (type == null || (!"Lnull;".equals(type.getDescriptor())
				 && type.getSort() != 9))
		PUSH false;
	    else
		PUSH true;
	    break label_521;
	}
	return POP;
    }
    
    protected BasicValue getElementValue(BasicValue basicvalue)
	throws AnalyzerException {
    label_522:
	{
	    Type type = basicvalue.getType();
	    if (type != null) {
		if (type.getSort() != 9) {
		    if ("Lnull;".equals(type.getDescriptor()))
			return basicvalue;
		} else
		    return newValue(Type.getType(type.getDescriptor()
						     .substring(1)));
	    }
	    break label_522;
	}
	throw new Error("Internal error");
    }
    
    protected boolean isSubTypeOf(BasicValue basicvalue,
				  BasicValue basicvalue_3_) {
	Type type = basicvalue_3_.getType();
	Type type_4_ = basicvalue.getType();
	switch (type.getSort()) {
	case 5:
	case 6:
	case 7:
	case 8:
	    return type_4_.equals(type);
	case 9:
	case 10:
	    if (!"Lnull;".equals(type_4_.getDescriptor())) {
		if (type_4_.getSort() != 10 && type_4_.getSort() != 9)
		    return false;
		return isAssignableFrom(type, type_4_);
	    }
	    return true;
	default:
	    throw new Error("Internal error");
	}
    }
    
    public BasicValue merge(BasicValue basicvalue, BasicValue basicvalue_5_) {
	if (basicvalue.equals(basicvalue_5_))
	    return basicvalue;
	Type type = basicvalue.getType();
	Type type_6_ = basicvalue_5_.getType();
	if (type == null || type.getSort() != 10 && type.getSort() != 9
	    || type_6_ == null
	    || type_6_.getSort() != 10 && type_6_.getSort() != 9)
	    return BasicValue.UNINITIALIZED_VALUE;
	if (!"Lnull;".equals(type.getDescriptor())) {
	    if (!"Lnull;".equals(type_6_.getDescriptor())) {
		if (!isAssignableFrom(type, type_6_)) {
		    if (!isAssignableFrom(type_6_, type)) {
			for (;;) {
			    if (type != null && !isInterface(type)) {
				type = getSuperClass(type);
				if (isAssignableFrom(type, type_6_))
				    return newValue(type);
			    }
			    return BasicValue.REFERENCE_VALUE;
			}
		    }
		    return basicvalue_5_;
		}
		return basicvalue;
	    }
	    return basicvalue;
	}
	return basicvalue_5_;
    }
    
    protected boolean isInterface(Type type) {
	if (currentClass == null || !type.equals(currentClass))
	    return getClass(type).isInterface();
	return isInterface;
    }
    
    protected Type getSuperClass(Type type) {
    label_523:
	{
	    if (currentClass == null || !type.equals(currentClass)) {
		Class var_class = getClass(type).getSuperclass();
		if (var_class != null)
		    PUSH Type.getType(var_class);
		else
		    PUSH null;
	    } else
		return currentSuperClass;
	}
	return POP;
	break label_523;
    }
    
    protected boolean isAssignableFrom(Type type, Type type_7_) {
    label_524:
	{
	    if (!type.equals(type_7_)) {
		if (currentClass == null || !type.equals(currentClass)) {
		    Class var_class;
		label_526:
		    {
			if (currentClass == null
			    || !type_7_.equals(currentClass)) {
			    var_class = getClass(type);
			    if (var_class.isInterface())
				var_class = class$java$lang$Object;
			} else {
			label_525:
			    {
				if (!isAssignableFrom(type,
						      currentSuperClass)) {
				    if (currentClassInterfaces != null) {
					for (int i = 0;
					     i < currentClassInterfaces.size();
					     i++) {
					    Type type_8_
						= (Type) currentClassInterfaces
							     .get(i);
					    if (!isAssignableFrom(type,
								  type_8_)) {
						/* empty */
					    }
					    return true;
					}
				    }
				} else
				    return true;
			    }
			    return false;
			    break label_525;
			}
		    }
		    return var_class.isAssignableFrom(getClass(type_7_));
		    break label_526;
		}
		if (getSuperClass(type_7_) != null) {
		    if (!isInterface)
			return isAssignableFrom(type, getSuperClass(type_7_));
		    if (type_7_.getSort() != 10 && type_7_.getSort() != 9)
			PUSH false;
		    else
			PUSH true;
		} else
		    return false;
	    } else
		return true;
	}
	return POP;
	break label_524;
    }
    
    protected Class getClass(Type type) {
	try {
	    if (type.getSort() != 9)
		return Class.forName(type.getClassName(), false, loader);
	    return Class.forName(type.getDescriptor().replace('/', '.'), false,
				 loader);
	} catch (ClassNotFoundException PUSH) {
	    ClassNotFoundException classnotfoundexception = POP;
	    throw new RuntimeException(classnotfoundexception.toString());
	}
    }
    
    /*synthetic*/ static Class class$(String string) {
	try {
	    return Class.forName(string);
	} catch (ClassNotFoundException PUSH) {
	    String string_10_ = ((ClassNotFoundException) POP).getMessage();
	    throw new NoClassDefFoundError(string_10_);
	}
    }
}
