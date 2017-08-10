/* JavaPrimitiveCast - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.assembler.metadata.JvmType;
import com.strobel.core.StringUtilities;

public final class JavaPrimitiveCast
{
    public static Object cast(JvmType targetType, Object input) {
    switch_0_:
	switch (ANONYMOUS CLASS com.strobel.decompiler.languages.java.ast.JavaPrimitiveCast$1.$SwitchMap$com$strobel$assembler$metadata$JvmType[targetType.ordinal()]) {
	case 1:
	label_1642:
	    {
		if (!(input instanceof Boolean)) {
		    if (!(input instanceof Number)) {
		    label_1643:
			{
			    if (!(input instanceof Character)) {
				if (input instanceof String)
				    return (Boolean.valueOf
					    (StringUtilities
						 .isTrue((String) input)));
			    } else {
				if (input == Character.valueOf('\0'))
				    PUSH false;
				else
				    PUSH true;
				break label_1643;
			    }
			    break switch_0_;
			}
			return Boolean.valueOf(POP);
		    }
		    if (input instanceof Float || input instanceof Double)
			break switch_0_;
		    if (((Number) input).longValue() == 0L)
			PUSH false;
		    else
			PUSH true;
		} else
		    return input;
	    }
	    return Boolean.valueOf(POP);
	    break label_1642;
	case 2:
	    if (!(input instanceof Number)) {
		if (!(input instanceof Character)) {
		    if (input instanceof String)
			return Byte.valueOf(Byte.parseByte((String) input));
		} else
		    return Byte.valueOf((byte) ((Character) input)
						   .charValue());
	    } else
		return Byte.valueOf(((Number) input).byteValue());
	    break;
	case 3:
	label_1644:
	    {
		if (!(input instanceof Character)) {
		    if (!(input instanceof Number)) {
			if (!(input instanceof String))
			    break switch_0_;
			String stringValue = (String) input;
			if (stringValue.length() != 0)
			    PUSH stringValue.charAt(0);
			else
			    PUSH false;
		    } else
			return Character.valueOf((char) ((Number) input)
							    .intValue());
		} else
		    return input;
	    }
	    return Character.valueOf(POP);
	    break label_1644;
	case 4:
	    if (!(input instanceof Number)) {
		if (!(input instanceof Character)) {
		    if (input instanceof String)
			return Short.valueOf(Short.parseShort((String) input));
		} else
		    return Short.valueOf((short) ((Character) input)
						     .charValue());
	    } else
		return Short.valueOf(((Number) input).shortValue());
	    break;
	case 5:
	label_1645:
	    {
		if (!(input instanceof Number)) {
		    if (!(input instanceof Boolean)) {
			if (!(input instanceof String)) {
			    if (input instanceof Character)
				return Integer.valueOf(((Character) input)
							   .charValue());
			} else
			    return Integer.valueOf(Integer.parseInt((String)
								    input));
			break switch_0_;
		    }
		    if (!((Boolean) input).booleanValue())
			PUSH false;
		    else
			PUSH true;
		} else
		    return Integer.valueOf(((Number) input).intValue());
	    }
	    return Integer.valueOf(POP);
	    break label_1645;
	case 6:
	    if (!(input instanceof Number)) {
		if (!(input instanceof Character)) {
		    if (input instanceof String)
			return Long.valueOf(Long.parseLong((String) input));
		} else
		    return Long.valueOf((long) ((Character) input)
						   .charValue());
	    } else
		return Long.valueOf(((Number) input).longValue());
	    break;
	case 7:
	    if (!(input instanceof Number)) {
		if (!(input instanceof Character)) {
		    if (input instanceof String)
			return Float.valueOf(Float.parseFloat((String) input));
		} else
		    return Float.valueOf((float) ((Character) input)
						     .charValue());
	    } else
		return Float.valueOf(((Number) input).floatValue());
	    break;
	case 8:
	    if (!(input instanceof Number)) {
		if (!(input instanceof Character)) {
		    if (input instanceof String)
			return Double.valueOf(Double.parseDouble((String)
								 input));
		} else
		    return Double.valueOf((double) ((Character) input)
						       .charValue());
	    } else
		return Double.valueOf(((Number) input).doubleValue());
	    break;
	default:
	    return input;
	}
	throw new ClassCastException();
    }
}
