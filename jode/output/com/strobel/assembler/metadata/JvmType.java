/* JvmType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import java.util.HashMap;
import java.util.Map;

import com.strobel.core.VerifyArgument;

public final class JvmType extends Enum
{
    public static final JvmType Boolean = new JvmType("Boolean", 0);
    public static final JvmType Byte = new JvmType("Byte", 1);
    public static final JvmType Character = new JvmType("Character", 2);
    public static final JvmType Short = new JvmType("Short", 3);
    public static final JvmType Integer = new JvmType("Integer", 4);
    public static final JvmType Long = new JvmType("Long", 5);
    public static final JvmType Float = new JvmType("Float", 6);
    public static final JvmType Double = new JvmType("Double", 7);
    public static final JvmType Object = new JvmType("Object", 8);
    public static final JvmType Array = new JvmType("Array", 9);
    public static final JvmType TypeVariable = new JvmType("TypeVariable", 10);
    public static final JvmType Wildcard = new JvmType("Wildcard", 11);
    public static final JvmType Void = new JvmType("Void", 12);
    private static final Map CLASSES_TO_JVM_TYPES;
    /*synthetic*/ private static final JvmType[] $VALUES
		      = { Boolean, Byte, Character, Short, Integer, Long,
			  Float, Double, Object, Array, TypeVariable, Wildcard,
			  Void };
    
    public static JvmType[] values() {
	return (JvmType[]) $VALUES.clone();
    }
    
    public static JvmType valueOf(String name) {
	return (JvmType) Enum.valueOf(JvmType.class, name);
    }
    
    private JvmType(String string, int i) {
	super(string, i);
    }
    
    public final String getDescriptorPrefix() {
	switch (ANONYMOUS CLASS com.strobel.assembler.metadata.JvmType$1.$SwitchMap$com$strobel$assembler$metadata$JvmType[ordinal()]) {
	case 1:
	    return "Z";
	case 2:
	    return "B";
	case 3:
	    return "C";
	case 4:
	    return "S";
	case 5:
	    return "I";
	case 6:
	    return "J";
	case 7:
	    return "F";
	case 8:
	    return "D";
	case 9:
	    return "L";
	case 10:
	    return "[";
	case 11:
	    return "T";
	case 12:
	    return "*";
	case 13:
	    return "V";
	default:
	    return "L";
	}
    }
    
    public final String getPrimitiveName() {
	switch (ANONYMOUS CLASS com.strobel.assembler.metadata.JvmType$1.$SwitchMap$com$strobel$assembler$metadata$JvmType[ordinal()]) {
	case 1:
	    return "boolean";
	case 2:
	    return "byte";
	case 3:
	    return "char";
	case 4:
	    return "short";
	case 5:
	    return "int";
	case 6:
	    return "long";
	case 7:
	    return "float";
	case 8:
	    return "double";
	case 13:
	    return "void";
	default:
	    return null;
	}
    }
    
    public final boolean isPrimitive() {
	switch (ANONYMOUS CLASS com.strobel.assembler.metadata.JvmType$1.$SwitchMap$com$strobel$assembler$metadata$JvmType[ordinal()]) {
	case 9:
	case 10:
	case 11:
	case 12:
	case 13:
	    return false;
	default:
	    return true;
	}
    }
    
    public final boolean isPrimitiveOrVoid() {
	switch (ANONYMOUS CLASS com.strobel.assembler.metadata.JvmType$1.$SwitchMap$com$strobel$assembler$metadata$JvmType[ordinal()]) {
	case 9:
	case 10:
	case 11:
	case 12:
	    return false;
	default:
	    return true;
	}
    }
    
    public final int bitWidth() {
	switch (ANONYMOUS CLASS com.strobel.assembler.metadata.JvmType$1.$SwitchMap$com$strobel$assembler$metadata$JvmType[ordinal()]) {
	case 1:
	    return 1;
	case 2:
	    return 8;
	case 3:
	case 4:
	    return 16;
	case 5:
	    return 32;
	case 6:
	    return 64;
	case 7:
	    return 32;
	case 8:
	    return 64;
	default:
	    return 0;
	}
    }
    
    public final int stackSlots() {
	switch (ANONYMOUS CLASS com.strobel.assembler.metadata.JvmType$1.$SwitchMap$com$strobel$assembler$metadata$JvmType[ordinal()]) {
	case 6:
	case 8:
	    return 2;
	case 13:
	    return 0;
	default:
	    return 1;
	}
    }
    
    public final boolean isSingleWord() {
	switch (ANONYMOUS CLASS com.strobel.assembler.metadata.JvmType$1.$SwitchMap$com$strobel$assembler$metadata$JvmType[ordinal()]) {
	case 6:
	case 8:
	case 13:
	    return false;
	default:
	    return true;
	}
    }
    
    public final boolean isDoubleWord() {
	switch (ANONYMOUS CLASS com.strobel.assembler.metadata.JvmType$1.$SwitchMap$com$strobel$assembler$metadata$JvmType[ordinal()]) {
	case 6:
	case 8:
	    return true;
	default:
	    return false;
	}
    }
    
    public final boolean isNumeric() {
	switch (ANONYMOUS CLASS com.strobel.assembler.metadata.JvmType$1.$SwitchMap$com$strobel$assembler$metadata$JvmType[ordinal()]) {
	case 1:
	case 2:
	case 3:
	case 4:
	case 5:
	case 6:
	case 7:
	case 8:
	    return true;
	default:
	    return false;
	}
    }
    
    public final boolean isIntegral() {
	switch (ANONYMOUS CLASS com.strobel.assembler.metadata.JvmType$1.$SwitchMap$com$strobel$assembler$metadata$JvmType[ordinal()]) {
	case 1:
	case 2:
	case 3:
	case 4:
	case 5:
	case 6:
	    return true;
	default:
	    return false;
	}
    }
    
    public final boolean isSubWordOrInt32() {
	switch (ANONYMOUS CLASS com.strobel.assembler.metadata.JvmType$1.$SwitchMap$com$strobel$assembler$metadata$JvmType[ordinal()]) {
	case 1:
	case 2:
	case 3:
	case 4:
	case 5:
	    return true;
	default:
	    return false;
	}
    }
    
    public final boolean isSigned() {
	switch (ANONYMOUS CLASS com.strobel.assembler.metadata.JvmType$1.$SwitchMap$com$strobel$assembler$metadata$JvmType[ordinal()]) {
	default:
	    return false;
	case 2:
	case 4:
	case 5:
	case 6:
	case 7:
	case 8:
	    return true;
	}
    }
    
    public final boolean isUnsigned() {
	switch (ANONYMOUS CLASS com.strobel.assembler.metadata.JvmType$1.$SwitchMap$com$strobel$assembler$metadata$JvmType[ordinal()]) {
	case 1:
	case 3:
	    return true;
	default:
	    return false;
	}
    }
    
    public final boolean isFloating() {
	switch (ANONYMOUS CLASS com.strobel.assembler.metadata.JvmType$1.$SwitchMap$com$strobel$assembler$metadata$JvmType[ordinal()]) {
	case 7:
	case 8:
	    return true;
	default:
	    return false;
	}
    }
    
    public final boolean isOther() {
	switch (ANONYMOUS CLASS com.strobel.assembler.metadata.JvmType$1.$SwitchMap$com$strobel$assembler$metadata$JvmType[ordinal()]) {
	case 9:
	case 10:
	case 11:
	case 12:
	case 13:
	    return true;
	default:
	    return false;
	}
    }
    
    public static JvmType forClass(Class clazz) {
	VerifyArgument.notNull(clazz, "clazz");
	JvmType jvmType = (JvmType) CLASSES_TO_JVM_TYPES.get(clazz);
	if (jvmType == null)
	    return Object;
	return jvmType;
    }
    
    public static JvmType forValue(Object value, boolean unboxPrimitives) {
    label_1297:
	{
	    if (value != null) {
		Class clazz = value.getClass();
		if (unboxPrimitives || clazz.isPrimitive()) {
		    JvmType jvmType
			= (JvmType) CLASSES_TO_JVM_TYPES.get(clazz);
		    if (jvmType != null)
			return jvmType;
		}
	    } else
		return Object;
	}
	return Object;
	break label_1297;
    }
    
    static {
	HashMap map = new HashMap();
	map.put(Void.class, Void);
	map.put(Boolean.class, Boolean);
	map.put(Character.class, Character);
	map.put(Byte.class, Byte);
	map.put(Short.class, Short);
	map.put(Integer.class, Integer);
	map.put(Long.class, Long);
	map.put(Float.class, Float);
	map.put(Double.class, Double);
	map.put(Void.TYPE, Void);
	map.put(Boolean.TYPE, Boolean);
	map.put(Character.TYPE, Character);
	map.put(Byte.TYPE, Byte);
	map.put(Short.TYPE, Short);
	map.put(Integer.TYPE, Integer);
	map.put(Long.TYPE, Long);
	map.put(Float.TYPE, Float);
	map.put(Double.TYPE, Double);
	CLASSES_TO_JVM_TYPES = map;
    }
}
