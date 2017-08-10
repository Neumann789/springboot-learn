/* ConstOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.AssertError;
import jode.decompiler.TabbedPrintWriter;
import jode.type.IntegerType;
import jode.type.Type;

public class ConstOperator extends NoArgOperator
{
    Object value;
    boolean isInitializer = false;
    private static final Type tBoolConstInt = new IntegerType(31);
    
    public ConstOperator(Object object) {
	super(Type.tUnknown);
    label_901:
	{
	label_900:
	    {
	    label_899:
		{
		    if (!(object instanceof Boolean)) {
			if (!(object instanceof Integer)) {
			    if (!(object instanceof Long)) {
				if (!(object instanceof Float)) {
				    if (!(object instanceof Double)) {
					if (!(object instanceof String)) {
					    if (object != null)
						throw new IllegalArgumentException
							  ("Illegal constant type: "
							   + object
								 .getClass());
					    updateParentType(Type.tUObject);
					} else
					    updateParentType(Type.tString);
				    } else
					updateParentType(Type.tDouble);
				} else
				    updateParentType(Type.tFloat);
			    } else
				updateParentType(Type.tLong);
			    break label_901;
			}
			int i = ((Integer) object).intValue();
			PUSH this;
			if (i != 0 && i != 1) {
			    if (i >= -32768 && i <= 65535) {
				PUSH new IntegerType;
				DUP
				if (i >= -128) {
				    if (i >= 0) {
					if (i > 127) {
					    if (i > 32767)
						PUSH 6;
					    else
						PUSH 14;
					} else
					    PUSH 30;
				    } else
					PUSH 26;
				} else
				    PUSH 10;
			    } else {
				PUSH Type.tInt;
				break label_900;
			    }
			} else {
			    PUSH tBoolConstInt;
			    break label_900;
			}
		    } else {
			updateParentType(Type.tBoolean);
			PUSH new Integer;
		    label_898:
			{
			    DUP
			    if (!((Boolean) object).booleanValue())
				PUSH false;
			    else
				PUSH true;
			    break label_898;
			}
			((UNCONSTRUCTED)POP).Integer(POP);
			object = POP;
			break label_901;
		    }
		}
		((UNCONSTRUCTED)POP).IntegerType(POP);
	    }
	    ((ConstOperator) POP).updateParentType(POP);
	}
	value = object;
	break label_899;
    }
    
    public Object getValue() {
	return value;
    }
    
    public boolean isOne(Type type) {
    label_902:
	{
	label_903:
	    {
		if (!(type instanceof IntegerType)) {
		    if (type != Type.tLong) {
		    label_904:
			{
			label_905:
			    {
				if (type != Type.tFloat) {
				    if (type != Type.tDouble)
					return false;
				    if (!(value instanceof Double)
					|| (((Double) value).doubleValue()
					    != 1.0))
					PUSH false;
				    else
					PUSH true;
				} else {
				    if (!(value instanceof Float)
					|| (((Float) value).floatValue()
					    != 1.0F))
					PUSH false;
				    else
					PUSH true;
				    break label_904;
				}
			    }
			    return POP;
			    break label_905;
			}
			return POP;
		    }
		    if (!(value instanceof Long)
			|| ((Long) value).longValue() != 1L)
			PUSH false;
		    else
			PUSH true;
		} else {
		    if (!(value instanceof Integer)
			|| ((Integer) value).intValue() != 1)
			PUSH false;
		    else
			PUSH true;
		    break label_902;
		}
	    }
	    return POP;
	    break label_903;
	}
	return POP;
    }
    
    public int getPriority() {
	return 1000;
    }
    
    public boolean opEquals(Operator operator) {
	if (!(operator instanceof ConstOperator))
	    return false;
    label_906:
	{
	    Object object = ((ConstOperator) operator).value;
	    if (value != null)
		PUSH value.equals(object);
	    else if (object != null)
		PUSH false;
	    else
		PUSH true;
	    break label_906;
	}
	return POP;
    }
    
    public void makeInitializer(Type type) {
	isInitializer = true;
    }
    
    private static String quoted(String string) {
	StringBuffer stringbuffer = new StringBuffer("\"");
	int i = 0;
	for (;;) {
	    if (i >= string.length())
		return stringbuffer.append("\"").toString();
	    char c;
	    switch (c = string.charAt(i)) {
	    case '\0':
		stringbuffer.append("\\0");
		break;
	    case '\t':
		stringbuffer.append("\\t");
		break;
	    case '\n':
		stringbuffer.append("\\n");
		break;
	    case '\r':
		stringbuffer.append("\\r");
		break;
	    case '\\':
		stringbuffer.append("\\\\");
		break;
	    case '\"':
		stringbuffer.append("\\\"");
		break;
	    default:
		if (c >= ' ') {
		    if (c < ' ' || c >= '\u007f') {
			String string_0_ = Integer.toHexString(c);
			stringbuffer.append
			    ("\\u0000".substring(0, 6 - string_0_.length()))
			    .append(string_0_);
		    } else
			stringbuffer.append(string.charAt(i));
		} else {
		    String string_1_ = Integer.toOctalString(c);
		    stringbuffer.append
			("\\000".substring(0, 4 - string_1_.length()))
			.append(string_1_);
		}
	    }
	    i++;
	}
    }
    
    public String toString() {
	String string = String.valueOf(value);
    label_908:
	{
	    int i;
	label_907:
	    {
		if (!type.isOfType(Type.tBoolean)) {
		    if (!type.getHint().equals(Type.tChar)) {
			if (!type.equals(Type.tString)) {
			    if (parent == null)
				break label_908;
			    i = parent.getOperatorIndex();
			    if (i >= 13 && i < 24)
				i -= 12;
			} else
			    return quoted(string);
		    } else {
			char c = (char) ((Integer) value).intValue();
			switch (c) {
			case '\0':
			    return "'\\0'";
			case '\t':
			    return "'\\t'";
			case '\n':
			    return "'\\n'";
			case '\r':
			    return "'\\r'";
			case '\\':
			    return "'\\\\'";
			case '\"':
			    return "'\\\"'";
			case '\'':
			    return "'\\''";
			default: {
			    if (c >= ' ') {
				if (c < ' ' || c >= '\u007f') {
				    String string_2_ = Integer.toHexString(c);
				    return (("'\\u0000".substring
					     (0, 7 - string_2_.length()))
					    + string_2_ + "'");
				}
				return "'" + c + "'";
			    }
			    String string_3_ = Integer.toOctalString(c);
			    return ("'\\000".substring(0,
						       5 - string_3_.length())
				    + string_3_ + "'");
			}
			}
		    }
		} else {
		    i = ((Integer) value).intValue();
		    if (i != 0) {
			if (i != 1)
			    throw new AssertError
				      ("boolean is neither false nor true");
			return "true";
		    }
		    return "false";
		}
	    }
	    if (i >= 9 && i < 12) {
		if (!type.isOfType(Type.tUInt)) {
		    if (type.equals(Type.tLong)) {
			long l = ((Long) value).longValue();
			if (l >= -1L)
			    string = "0x" + Long.toHexString(l);
			else
			    string = "~0x" + Long.toHexString(-l - 1L);
		    }
		} else {
		    int i_4_ = ((Integer) value).intValue();
		    if (i_4_ >= -1)
			string = "0x" + Integer.toHexString(i_4_);
		    else
			string = "~0x" + Integer.toHexString(-i_4_ - 1);
		}
	    }
	}
	if (!type.isOfType(Type.tLong)) {
	    if (!type.isOfType(Type.tFloat)) {
		if (!type.isOfType(Type.tDouble)) {
		    if (type.isOfType(Type.tInt)
			|| (!type.getHint().equals(Type.tByte)
			    && !type.getHint().equals(Type.tShort))
			|| isInitializer
			|| (parent instanceof StoreInstruction
			    && parent.getOperatorIndex() != 12
			    && parent.subExpressions[1] == this))
			return string;
		    return "(" + type.getHint() + ") " + string;
		}
		if (!string.equals("NaN")) {
		    if (!string.equals("-Infinity")) {
			if (!string.equals("Infinity"))
			    return string;
			return "Double.POSITIVE_INFINITY";
		    }
		    return "Double.NEGATIVE_INFINITY";
		}
		return "Double.NaN";
	    }
	    if (!string.equals("NaN")) {
		if (!string.equals("-Infinity")) {
		    if (!string.equals("Infinity"))
			return string + "F";
		    return "Float.POSITIVE_INFINITY";
		}
		return "Float.NEGATIVE_INFINITY";
	    }
	    return "Float.NaN";
	}
	return string + "L";
	break label_907;
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.print(toString());
    }
}
