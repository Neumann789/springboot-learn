/* Remapper - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import com.javosize.thirdparty.org.objectweb.asm.Handle;
import com.javosize.thirdparty.org.objectweb.asm.Type;
import com.javosize.thirdparty.org.objectweb.asm.signature.SignatureReader;
import com.javosize.thirdparty.org.objectweb.asm.signature.SignatureVisitor;
import com.javosize.thirdparty.org.objectweb.asm.signature.SignatureWriter;

public abstract class Remapper
{
    public String mapDesc(String string) {
	Type type = Type.getType(string);
	switch (type.getSort()) {
	case 9: {
	    String string_0_ = mapDesc(type.getElementType().getDescriptor());
	    int i = 0;
	    for (;;) {
		if (i >= type.getDimensions())
		    return string_0_;
		string_0_ = '[' + string_0_;
		i++;
	    }
	}
	case 10: {
	    String string_1_ = map(type.getInternalName());
	    IF (string_1_ == null)
		/* empty */
	    return 'L' + string_1_ + ';';
	}
	    /* fall through */
	default:
	    return string;
	}
    }
    
    private Type mapType(Type type) {
	switch (type.getSort()) {
	case 9: {
	    String string = mapDesc(type.getElementType().getDescriptor());
	    int i = 0;
	    for (;;) {
		if (i >= type.getDimensions())
		    return Type.getType(string);
		string = '[' + string;
		i++;
	    }
	}
	case 10:
	label_406:
	    {
		String string = map(type.getInternalName());
		if (string == null)
		    PUSH type;
		else
		    PUSH Type.getObjectType(string);
		break label_406;
	    }
	    return POP;
	case 11:
	    return Type.getMethodType(mapMethodDesc(type.getDescriptor()));
	default:
	    return type;
	}
    }
    
    public String mapType(String string) {
	if (string != null)
	    return mapType(Type.getObjectType(string)).getInternalName();
	return null;
    }
    
    public String[] mapTypes(String[] strings) {
	String[] strings_2_ = null;
	boolean bool = false;
	int i = 0;
    label_411:
	{
	    for (;;) {
		if (i >= strings.length) {
		    if (!bool)
			PUSH strings;
		    else
			PUSH strings_2_;
		    break label_411;
		}
		String string = strings[i];
	    label_410:
		{
		    String string_3_;
		label_408:
		    {
			string_3_ = map(string);
			if (string_3_ != null && strings_2_ == null) {
			label_407:
			    {
				strings_2_ = new String[strings.length];
				if (i > 0)
				    System.arraycopy(strings, 0, strings_2_, 0,
						     i);
				break label_407;
			    }
			    bool = true;
			}
			break label_408;
		    }
		    if (bool) {
			PUSH strings_2_;
		    label_409:
			{
			    PUSH i;
			    if (string_3_ != null)
				PUSH string_3_;
			    else
				PUSH string;
			    break label_409;
			}
			POP[POP] = POP;
		    }
		    break label_410;
		}
		i++;
	    }
	}
	return POP;
	break label_411;
    }
    
    public String mapMethodDesc(String string) {
	if (!"()V".equals(string)) {
	    Type[] types = Type.getArgumentTypes(string);
	    StringBuffer stringbuffer = new StringBuffer("(");
	    int i = 0;
	    for (;;) {
		if (i >= types.length) {
		    Type type = Type.getReturnType(string);
		    if (type != Type.VOID_TYPE) {
			stringbuffer.append(')')
			    .append(mapDesc(type.getDescriptor()));
			return stringbuffer.toString();
		    }
		    stringbuffer.append(")V");
		    return stringbuffer.toString();
		}
		stringbuffer.append(mapDesc(types[i].getDescriptor()));
		i++;
	    }
	}
	return string;
    }
    
    public Object mapValue(Object object) {
	if (!(object instanceof Type)) {
	    if (!(object instanceof Handle))
		return object;
	    Handle handle = (Handle) object;
	    return new Handle(handle.getTag(), mapType(handle.getOwner()),
			      mapMethodName(handle.getOwner(),
					    handle.getName(),
					    handle.getDesc()),
			      mapMethodDesc(handle.getDesc()));
	}
	return mapType((Type) object);
    }
    
    public String mapSignature(String string, boolean bool) {
	SignatureWriter signaturewriter;
    label_412:
	{
	    if (string != null) {
		SignatureReader signaturereader = new SignatureReader(string);
		signaturewriter = new SignatureWriter();
		SignatureVisitor signaturevisitor
		    = createRemappingSignatureAdapter(signaturewriter);
		if (!bool)
		    signaturereader.accept(signaturevisitor);
		else
		    signaturereader.acceptType(signaturevisitor);
	    } else
		return null;
	}
	return signaturewriter.toString();
	break label_412;
    }
    
    protected SignatureVisitor createRemappingSignatureAdapter
	(SignatureVisitor signaturevisitor) {
	return new RemappingSignatureAdapter(signaturevisitor, this);
    }
    
    public String mapMethodName(String string, String string_4_,
				String string_5_) {
	return string_4_;
    }
    
    public String mapInvokeDynamicMethodName(String string, String string_6_) {
	return string;
    }
    
    public String mapFieldName(String string, String string_7_,
			       String string_8_) {
	return string_7_;
    }
    
    public String map(String string) {
	return string;
    }
}
