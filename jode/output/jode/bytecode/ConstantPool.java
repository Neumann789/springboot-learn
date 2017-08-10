/* ConstantPool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.bytecode;
import java.io.DataInputStream;
import java.io.IOException;

public class ConstantPool
{
    public static final int CLASS = 7;
    public static final int FIELDREF = 9;
    public static final int METHODREF = 10;
    public static final int INTERFACEMETHODREF = 11;
    public static final int STRING = 8;
    public static final int INTEGER = 3;
    public static final int FLOAT = 4;
    public static final int LONG = 5;
    public static final int DOUBLE = 6;
    public static final int NAMEANDTYPE = 12;
    public static final int UTF8 = 1;
    int count;
    int[] tags;
    int[] indices1;
    int[] indices2;
    Object[] constants;
    
    public void read(DataInputStream datainputstream) throws IOException {
	count = datainputstream.readUnsignedShort();
	tags = new int[count];
	indices1 = new int[count];
	indices2 = new int[count];
	constants = new Object[count];
	int i = 1;
	for (;;) {
	    IF (i >= count)
		/* empty */
	    int i_0_ = datainputstream.readUnsignedByte();
	    tags[i] = i_0_;
	    switch (i_0_) {
	    case 7:
		indices1[i] = datainputstream.readUnsignedShort();
		break;
	    case 9:
	    case 10:
	    case 11:
		indices1[i] = datainputstream.readUnsignedShort();
		indices2[i] = datainputstream.readUnsignedShort();
		break;
	    case 8:
		indices1[i] = datainputstream.readUnsignedShort();
		break;
	    case 3:
		constants[i] = new Integer(datainputstream.readInt());
		break;
	    case 4:
		constants[i] = new Float(datainputstream.readFloat());
		break;
	    case 5:
		constants[i] = new Long(datainputstream.readLong());
		tags[++i] = -5;
		break;
	    case 6:
		constants[i] = new Double(datainputstream.readDouble());
		tags[++i] = -6;
		break;
	    case 12:
		indices1[i] = datainputstream.readUnsignedShort();
		indices2[i] = datainputstream.readUnsignedShort();
		break;
	    case 1:
		constants[i] = datainputstream.readUTF().intern();
		break;
	    default:
		throw new ClassFormatException("unknown constant tag");
	    }
	    i++;
	}
    }
    
    public int getTag(int i) throws ClassFormatException {
	if (i != 0)
	    return tags[i];
	throw new ClassFormatException("null tag");
    }
    
    public String getUTF8(int i) throws ClassFormatException {
	if (i != 0) {
	    if (tags[i] == 1)
		return (String) constants[i];
	    throw new ClassFormatException("Tag mismatch");
	}
	return null;
    }
    
    public Reference getRef(int i) throws ClassFormatException {
    label_803:
	{
	    if (i != 0) {
		if (tags[i] == 9 || tags[i] == 10 || tags[i] == 11) {
		    if (constants[i] == null) {
			int i_1_ = indices1[i];
			int i_2_ = indices2[i];
			if (tags[i_2_] == 12) {
			    String string = getUTF8(indices2[i_2_]);
			label_801:
			    {
			    label_802:
				{
				    try {
					if (tags[i] == 9) {
					    try {
						TypeSignature
						    .checkTypeSig(string);
						break label_802;
					    } catch (IllegalArgumentException PUSH) {
						/* empty */
					    }
					    break label_801;
					}
				    } catch (IllegalArgumentException PUSH) {
					break label_801;
				    }
				    try {
					TypeSignature
					    .checkMethodTypeSig(string);
				    } catch (IllegalArgumentException PUSH) {
					/* empty */
				    }
				}
				String string_3_ = getClassType(i_1_);
				constants[i]
				    = Reference.getReference(string_3_,
							     getUTF8(indices1
								     [i_2_]),
							     string);
				break label_803;
			    }
			    IllegalArgumentException illegalargumentexception
				= POP;
			    throw new ClassFormatException
				      (illegalargumentexception.getMessage());
			}
			throw new ClassFormatException("Tag mismatch");
		    }
		} else
		    throw new ClassFormatException("Tag mismatch");
	    } else
		return null;
	}
	return (Reference) constants[i];
	break label_803;
    }
    
    public Object getConstant(int i) throws ClassFormatException {
	if (i != 0) {
	    switch (tags[i]) {
	    case 3:
	    case 4:
	    case 5:
	    case 6:
		return constants[i];
	    case 8:
		return getUTF8(indices1[i]);
	    case 7:
		return constants[indices1[i]];
	    default:
		throw new ClassFormatException("Tag mismatch: " + tags[i]);
	    }
	}
	throw new ClassFormatException("null constant");
    }
    
    public String getClassType(int i) throws ClassFormatException {
	String string;
    label_804:
	{
	    if (i != 0) {
		if (tags[i] == 7) {
		    string = getUTF8(indices1[i]);
		    if (string.charAt(0) != '[')
			string = ("L" + string + ';').intern();
		} else
		    throw new ClassFormatException("Tag mismatch");
	    } else
		return null;
	}
	try {
	    TypeSignature.checkTypeSig(string);
	    return string;
	} catch (IllegalArgumentException PUSH) {
	    IllegalArgumentException illegalargumentexception = POP;
	    throw new ClassFormatException(illegalargumentexception
					       .getMessage());
	}
	break label_804;
    }
    
    public String getClassName(int i) throws ClassFormatException {
	if (i != 0) {
	    if (tags[i] == 7) {
		String string = getUTF8(indices1[i]);
		try {
		    TypeSignature.checkTypeSig("L" + string + ";");
		    return string.replace('/', '.').intern();
		} catch (IllegalArgumentException PUSH) {
		    IllegalArgumentException illegalargumentexception = POP;
		    throw new ClassFormatException(illegalargumentexception
						       .getMessage());
		}
	    }
	    throw new ClassFormatException("Tag mismatch");
	}
	return null;
    }
    
    public String toString(int i) {
	switch (tags[i]) {
	case 7:
	    return "Class " + toString(indices1[i]);
	case 8:
	    return "String \"" + toString(indices1[i]) + "\"";
	case 3:
	    return "Int " + constants[i].toString();
	case 4:
	    return "Float " + constants[i].toString();
	case 5:
	    return "Long " + constants[i].toString();
	case 6:
	    return "Double " + constants[i].toString();
	case 1:
	    return constants[i].toString();
	case 9:
	    return ("Fieldref: " + toString(indices1[i]) + "; "
		    + toString(indices2[i]));
	case 10:
	    return ("Methodref: " + toString(indices1[i]) + "; "
		    + toString(indices2[i]));
	case 11:
	    return ("Interfaceref: " + toString(indices1[i]) + "; "
		    + toString(indices2[i]));
	case 12:
	    return ("Name " + toString(indices1[i]) + "; Type "
		    + toString(indices2[i]));
	default:
	    return "unknown tag: " + tags[i];
	}
    }
    
    public int size() {
	return count;
    }
    
    public String toString() {
	StringBuffer stringbuffer = new StringBuffer("[ null");
	int i = 1;
	for (;;) {
	    if (i >= count) {
		stringbuffer.append(" ]");
		return stringbuffer.toString();
	    }
	    stringbuffer.append(", ").append(i).append(" = ")
		.append(toString(i));
	    i++;
	}
    }
}
