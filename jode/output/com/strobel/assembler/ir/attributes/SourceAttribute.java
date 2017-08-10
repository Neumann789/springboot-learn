/* SourceAttribute - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir.attributes;
import java.util.Iterator;
import java.util.List;

import com.strobel.assembler.ir.AnnotationReader;
import com.strobel.assembler.metadata.Buffer;
import com.strobel.assembler.metadata.IMetadataResolver;
import com.strobel.assembler.metadata.IMetadataScope;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.assembler.metadata.annotations.AnnotationElement;
import com.strobel.assembler.metadata.annotations.CustomAnnotation;
import com.strobel.core.VerifyArgument;
import com.strobel.util.ContractUtils;

public class SourceAttribute
{
    private final String _name;
    private final int _length;
    
    public final String getName() {
	return _name;
    }
    
    public final int getLength() {
	return _length;
    }
    
    protected SourceAttribute(String name, int length) {
	_name = name;
	_length = length;
    }
    
    public static SourceAttribute create(String name) {
	return new SourceAttribute((String) VerifyArgument.notNull(name,
								   "name"),
				   0);
    }
    
    public static transient SourceAttribute find
	(String name, SourceAttribute[] attributes) {
	VerifyArgument.notNull(name, "name");
	VerifyArgument.noNullElements(attributes, "attributes");
	SourceAttribute[] arr$ = attributes;
	int len$ = arr$.length;
	int i$ = 0;
	for (;;) {
	    if (i$ >= len$)
		return null;
	    SourceAttribute attribute = arr$[i$];
	    if (!name.equals(attribute.getName()))
		i$++;
	    return attribute;
	}
    }
    
    public static SourceAttribute find(String name, List attributes) {
	VerifyArgument.notNull(name, "name");
	VerifyArgument.noNullElements(attributes, "attributes");
	Iterator i$ = attributes.iterator();
	for (;;) {
	    if (!i$.hasNext())
		return null;
	    SourceAttribute attribute = (SourceAttribute) i$.next();
	    IF (!name.equals(attribute.getName()))
		/* empty */
	    return attribute;
	}
    }
    
    public static void readAttributes(IMetadataResolver resolver,
				      IMetadataScope scope, Buffer input,
				      SourceAttribute[] attributes) {
	int i = 0;
	for (;;) {
	    IF (i >= attributes.length)
		/* empty */
	    attributes[i] = readAttribute(resolver, scope, input);
	    i++;
	}
    }
    
    public static SourceAttribute readAttribute(IMetadataResolver resolver,
						IMetadataScope scope,
						Buffer buffer) {
	int nameIndex = buffer.readUnsignedShort();
	int length = buffer.readInt();
	String name = (String) scope.lookupConstant(nameIndex);
	if (length != 0) {
	    String string = name;
	    int i = -1;
	    switch (string.hashCode()) {
	    case 881600599:
		if (string.equals("SourceFile"))
		    i = 0;
		break;
	    case -1968073715:
		if (string.equals("ConstantValue"))
		    i = 1;
		break;
	    case 2105869:
		if (string.equals("Code"))
		    i = 2;
		break;
	    case 679220772:
		if (string.equals("Exceptions"))
		    i = 3;
		break;
	    case 1698628945:
		if (string.equals("LineNumberTable"))
		    i = 4;
		break;
	    case 1690786087:
		if (string.equals("LocalVariableTable"))
		    i = 5;
		break;
	    case 647494029:
		if (string.equals("LocalVariableTypeTable"))
		    i = 6;
		break;
	    case 1372865485:
		if (string.equals("EnclosingMethod"))
		    i = 7;
		break;
	    case 2061183248:
		if (string.equals("InnerClasses"))
		    i = 8;
		break;
	    case -528253654:
		if (string.equals("RuntimeVisibleAnnotations"))
		    i = 9;
		break;
	    case 1971868943:
		if (string.equals("RuntimeInvisibleAnnotations"))
		    i = 10;
		break;
	    case -918183819:
		if (string.equals("RuntimeVisibleParameterAnnotations"))
		    i = 11;
		break;
	    case -864757200:
		if (string.equals("RuntimeInvisibleParameterAnnotations"))
		    i = 12;
		break;
	    case 1181327346:
		if (string.equals("AnnotationDefault"))
		    i = 13;
		break;
	    case -1217415016:
		if (string.equals("Signature"))
		    i = 14;
		break;
	    }
	    switch (i) {
	    case 0: {
		int token = buffer.readUnsignedShort();
		String sourceFile = (String) scope.lookupConstant(token);
		return new SourceFileAttribute(sourceFile);
	    }
	    case 1: {
		int token = buffer.readUnsignedShort();
		Object constantValue = scope.lookupConstant(token);
		return new ConstantValueAttribute(constantValue);
	    }
	    case 2: {
		int maxStack = buffer.readUnsignedShort();
		int maxLocals = buffer.readUnsignedShort();
		int codeOffset = buffer.position();
		int codeLength = buffer.readInt();
		byte[] code = new byte[codeLength];
		buffer.read(code, 0, codeLength);
		int exceptionTableLength = buffer.readUnsignedShort();
		ExceptionTableEntry[] exceptionTable
		    = new ExceptionTableEntry[exceptionTableLength];
		int k = 0;
		for (;;) {
		    if (k >= exceptionTableLength) {
			int attributeCount = buffer.readUnsignedShort();
			SourceAttribute[] attributes
			    = new SourceAttribute[attributeCount];
			readAttributes(resolver, scope, buffer, attributes);
			return new CodeAttribute(length, maxStack, maxLocals,
						 codeOffset, codeLength,
						 buffer, exceptionTable,
						 attributes);
		    }
		    int startOffset = buffer.readUnsignedShort();
		    int endOffset = buffer.readUnsignedShort();
		    int handlerOffset = buffer.readUnsignedShort();
		    TypeReference catchType;
		label_1172:
		    {
			int catchTypeToken = buffer.readUnsignedShort();
			if (catchTypeToken != 0)
			    catchType
				= (resolver.lookupType
				   ((String)
				    scope.lookupConstant(catchTypeToken)));
			else
			    catchType = null;
			break label_1172;
		    }
		    exceptionTable[k]
			= new ExceptionTableEntry(startOffset, endOffset,
						  handlerOffset, catchType);
		    k++;
		}
	    }
	    case 3: {
		int exceptionCount = buffer.readUnsignedShort();
		TypeReference[] exceptionTypes
		    = new TypeReference[exceptionCount];
		int i_0_ = 0;
		for (;;) {
		    if (i_0_ >= exceptionTypes.length)
			return new ExceptionsAttribute(exceptionTypes);
		    exceptionTypes[i_0_]
			= scope.lookupType(buffer.readUnsignedShort());
		    i_0_++;
		}
	    }
	    case 4: {
		int entryCount = buffer.readUnsignedShort();
		LineNumberTableEntry[] entries
		    = new LineNumberTableEntry[entryCount];
		int i_1_ = 0;
		for (;;) {
		    if (i_1_ >= entries.length)
			return new LineNumberTableAttribute(entries);
		    entries[i_1_]
			= new LineNumberTableEntry(buffer.readUnsignedShort(),
						   buffer.readUnsignedShort());
		    i_1_++;
		}
	    }
	    case 5:
	    case 6: {
		int entryCount = buffer.readUnsignedShort();
		LocalVariableTableEntry[] entries
		    = new LocalVariableTableEntry[entryCount];
		int i_2_ = 0;
		for (;;) {
		    if (i_2_ >= entries.length)
			return new LocalVariableTableAttribute(name, entries);
		    int scopeOffset = buffer.readUnsignedShort();
		    int scopeLength = buffer.readUnsignedShort();
		    String variableName
			= ((String)
			   scope.lookupConstant(buffer.readUnsignedShort()));
		    String descriptor
			= ((String)
			   scope.lookupConstant(buffer.readUnsignedShort()));
		    int variableIndex = buffer.readUnsignedShort();
		    entries[i_2_] = (new LocalVariableTableEntry
				     (variableIndex, variableName,
				      resolver.lookupType(descriptor),
				      scopeOffset, scopeLength));
		    i_2_++;
		}
	    }
	    case 7: {
		int typeToken = buffer.readUnsignedShort();
		int methodToken = buffer.readUnsignedShort();
		PUSH new EnclosingMethodAttribute;
		DUP
	    label_1173:
		{
		    PUSH scope.lookupType(typeToken);
		    if (methodToken <= 0)
			PUSH null;
		    else
			PUSH scope.lookupMethod(typeToken, methodToken);
		    break label_1173;
		}
		((UNCONSTRUCTED)POP).EnclosingMethodAttribute(POP, POP);
		return POP;
	    }
	    case 8:
		throw ContractUtils.unreachable();
	    case 9:
	    case 10: {
		CustomAnnotation[] annotations
		    = new CustomAnnotation[buffer.readUnsignedShort()];
		int i_3_ = 0;
		for (;;) {
		    if (i_3_ >= annotations.length)
			return new AnnotationsAttribute(name, length,
							annotations);
		    annotations[i_3_] = AnnotationReader.read(scope, buffer);
		    i_3_++;
		}
	    }
	    case 11:
	    case 12: {
		CustomAnnotation[][] annotations
		    = new CustomAnnotation[buffer.readUnsignedShort()][];
		int i_4_ = 0;
		for (;;) {
		    if (i_4_ >= annotations.length)
			return new ParameterAnnotationsAttribute(name, length,
								 annotations);
		    CustomAnnotation[] parameterAnnotations
			= new CustomAnnotation[buffer.readUnsignedShort()];
		    int j = 0;
		    for (;;) {
			if (j >= parameterAnnotations.length) {
			    annotations[i_4_] = parameterAnnotations;
			    i_4_++;
			}
			parameterAnnotations[j]
			    = AnnotationReader.read(scope, buffer);
			j++;
		    }
		}
	    }
	    case 13: {
		AnnotationElement defaultValue
		    = AnnotationReader.readElement(scope, buffer);
		return new AnnotationDefaultAttribute(length, defaultValue);
	    }
	    case 14: {
		int token = buffer.readUnsignedShort();
		String signature = (String) scope.lookupConstant(token);
		return new SignatureAttribute(signature);
	    }
	    default: {
		int offset = buffer.position();
		byte[] blob = new byte[length];
		buffer.read(blob, 0, blob.length);
		return new BlobAttribute(name, blob, offset);
	    }
	    }
	}
	return create(name);
    }
}
