/* MetadataReader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir;
import java.util.List;

import com.strobel.assembler.ir.attributes.AnnotationDefaultAttribute;
import com.strobel.assembler.ir.attributes.AnnotationsAttribute;
import com.strobel.assembler.ir.attributes.BlobAttribute;
import com.strobel.assembler.ir.attributes.BootstrapMethodsAttribute;
import com.strobel.assembler.ir.attributes.BootstrapMethodsTableEntry;
import com.strobel.assembler.ir.attributes.CodeAttribute;
import com.strobel.assembler.ir.attributes.ConstantValueAttribute;
import com.strobel.assembler.ir.attributes.EnclosingMethodAttribute;
import com.strobel.assembler.ir.attributes.ExceptionTableEntry;
import com.strobel.assembler.ir.attributes.ExceptionsAttribute;
import com.strobel.assembler.ir.attributes.LineNumberTableAttribute;
import com.strobel.assembler.ir.attributes.LineNumberTableEntry;
import com.strobel.assembler.ir.attributes.LocalVariableTableAttribute;
import com.strobel.assembler.ir.attributes.LocalVariableTableEntry;
import com.strobel.assembler.ir.attributes.MethodParameterEntry;
import com.strobel.assembler.ir.attributes.MethodParametersAttribute;
import com.strobel.assembler.ir.attributes.ParameterAnnotationsAttribute;
import com.strobel.assembler.ir.attributes.SignatureAttribute;
import com.strobel.assembler.ir.attributes.SourceAttribute;
import com.strobel.assembler.ir.attributes.SourceFileAttribute;
import com.strobel.assembler.metadata.Buffer;
import com.strobel.assembler.metadata.BuiltinTypes;
import com.strobel.assembler.metadata.IMetadataScope;
import com.strobel.assembler.metadata.MetadataParser;
import com.strobel.assembler.metadata.MethodDefinition;
import com.strobel.assembler.metadata.MethodReference;
import com.strobel.assembler.metadata.ParameterDefinition;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.assembler.metadata.annotations.AnnotationElement;
import com.strobel.assembler.metadata.annotations.CustomAnnotation;
import com.strobel.core.ArrayUtilities;
import com.strobel.core.VerifyArgument;

public abstract class MetadataReader
{
    protected MetadataReader() {
	/* empty */
    }
    
    protected abstract IMetadataScope getScope();
    
    protected abstract MetadataParser getParser();
    
    public void readAttributes(Buffer input, SourceAttribute[] attributes) {
	int i = 0;
	for (;;) {
	    IF (i >= attributes.length)
		/* empty */
	    attributes[i] = readAttribute(input);
	    i++;
	}
    }
    
    public SourceAttribute readAttribute(Buffer buffer) {
	int nameIndex = buffer.readUnsignedShort();
	int length = buffer.readInt();
	IMetadataScope scope = getScope();
	String name = (String) scope.lookupConstant(nameIndex);
	return readAttributeCore(name, buffer, -1, length);
    }
    
    protected SourceAttribute readAttributeCore(String name, Buffer buffer,
						int originalOffset,
						int length) {
	IMetadataScope scope = getScope();
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
	    case -528253654:
		if (string.equals("RuntimeVisibleAnnotations"))
		    i = 8;
		break;
	    case 1971868943:
		if (string.equals("RuntimeInvisibleAnnotations"))
		    i = 9;
		break;
	    case -918183819:
		if (string.equals("RuntimeVisibleParameterAnnotations"))
		    i = 10;
		break;
	    case -864757200:
		if (string.equals("RuntimeInvisibleParameterAnnotations"))
		    i = 11;
		break;
	    case 1181327346:
		if (string.equals("AnnotationDefault"))
		    i = 12;
		break;
	    case -1217415016:
		if (string.equals("Signature"))
		    i = 13;
		break;
	    case 302571908:
		if (string.equals("BootstrapMethods"))
		    i = 14;
		break;
	    case -1682911797:
		if (string.equals("MethodParameters"))
		    i = 15;
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
		int codeLength = buffer.readInt();
	    label_1158:
		{
		    int relativeOffset = buffer.position();
		    if (originalOffset < 0)
			PUSH relativeOffset;
		    else
			PUSH originalOffset - 2 + relativeOffset;
		    break label_1158;
		}
		int codeOffset = POP;
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
			readAttributes(buffer, attributes);
			return new CodeAttribute(length, maxStack, maxLocals,
						 codeOffset, codeLength,
						 buffer, exceptionTable,
						 attributes);
		    }
		    int startOffset = buffer.readUnsignedShort();
		    int endOffset = buffer.readUnsignedShort();
		    int handlerOffset = buffer.readUnsignedShort();
		    TypeReference catchType;
		label_1159:
		    {
			int catchTypeToken = buffer.readUnsignedShort();
			if (catchTypeToken != 0)
			    catchType = scope.lookupType(catchTypeToken);
			else
			    catchType = null;
			break label_1159;
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
		    int nameToken = buffer.readUnsignedShort();
		    int typeToken = buffer.readUnsignedShort();
		    int variableIndex = buffer.readUnsignedShort();
		    String variableName
			= (String) scope.lookupConstant(nameToken);
		    String descriptor
			= (String) scope.lookupConstant(typeToken);
		    entries[i_2_]
			= (new LocalVariableTableEntry
			   (variableIndex, variableName,
			    getParser().parseTypeSignature(descriptor),
			    scopeOffset, scopeLength));
		    i_2_++;
		}
	    }
	    case 7: {
		int typeToken = buffer.readUnsignedShort();
		int methodToken = buffer.readUnsignedShort();
		PUSH new EnclosingMethodAttribute;
		DUP
	    label_1160:
		{
		    PUSH scope.lookupType(typeToken);
		    if (methodToken <= 0)
			PUSH null;
		    else
			PUSH scope.lookupMethod(typeToken, methodToken);
		    break label_1160;
		}
		((UNCONSTRUCTED)POP).EnclosingMethodAttribute(POP, POP);
		return POP;
	    }
	    case 8:
	    case 9: {
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
	    case 10:
	    case 11: {
		CustomAnnotation[][] annotations
		    = new CustomAnnotation[buffer.readUnsignedByte()][];
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
	    case 12: {
		AnnotationElement defaultValue
		    = AnnotationReader.readElement(scope, buffer);
		return new AnnotationDefaultAttribute(length, defaultValue);
	    }
	    case 13: {
		int token = buffer.readUnsignedShort();
		String signature = (String) scope.lookupConstant(token);
		return new SignatureAttribute(signature);
	    }
	    case 14: {
		BootstrapMethodsTableEntry[] methods
		    = (new BootstrapMethodsTableEntry
		       [buffer.readUnsignedShort()]);
		int i_5_ = 0;
		for (;;) {
		    if (i_5_ >= methods.length)
			return new BootstrapMethodsAttribute(methods);
		    MethodReference bootstrapMethod
			= scope.lookupMethod(buffer.readUnsignedShort());
		    Object[] arguments
			= new Object[buffer.readUnsignedShort()];
		    List parameters;
		label_1161:
		    {
			parameters = bootstrapMethod.getParameters();
			if (parameters.size() != arguments.length + 3) {
			    MethodDefinition resolved
				= bootstrapMethod.resolve();
			    if (resolved == null || !resolved.isVarArgs()
				|| parameters.size() >= arguments.length + 3)
				throw Error.invalidBootstrapMethodEntry
					  (bootstrapMethod, parameters.size(),
					   arguments.length);
			}
			break label_1161;
		    }
		    int j = 0;
		    for (;;) {
			if (j >= arguments.length) {
			    methods[i_5_] = (new BootstrapMethodsTableEntry
					     (bootstrapMethod, arguments));
			    i_5_++;
			}
			int token = buffer.readUnsignedShort();
			TypeReference parameterType;
		    label_1162:
			{
			    int parameterIndex = j + 3;
			    if (parameterIndex >= parameters.size())
				parameterType = BuiltinTypes.Object;
			    else
				parameterType
				    = ((ParameterDefinition)
				       parameters.get(parameterIndex))
					  .getParameterType();
			    break label_1162;
			}
			String string_6_ = parameterType.getInternalName();
			int i_7_ = -1;
			switch (string_6_.hashCode()) {
			case 85913708:
			    if (string_6_
				    .equals("java/lang/invoke/MethodHandle"))
				i_7_ = 0;
			    break;
			case 358011518:
			    if (string_6_
				    .equals("java/lang/invoke/MethodType"))
				i_7_ = 1;
			    break;
			}
			switch (i_7_) {
			case 0:
			    arguments[j] = scope.lookupMethodHandle(token);
			    break;
			case 1:
			    arguments[j] = scope.lookupMethodType(token);
			    break;
			default:
			    arguments[j] = scope.lookup(token);
			}
			j++;
		    }
		}
	    }
	    case 15: {
		int methodParameterCount = buffer.readUnsignedByte();
		int computedCount = (length - 1) / 4;
		MethodParameterEntry[] entries
		    = new MethodParameterEntry[methodParameterCount];
		int i_8_ = 0;
		for (;;) {
		    int nameIndex;
		    int flags;
		label_1163:
		    {
			if (i_8_ >= entries.length)
			    return (new MethodParametersAttribute
				    (ArrayUtilities
					 .asUnmodifiableList(entries)));
			if (i_8_ >= computedCount) {
			    nameIndex = 0;
			    flags = 0;
			} else {
			    nameIndex = buffer.readUnsignedShort();
			    flags = buffer.readUnsignedShort();
			}
			break label_1163;
		    }
		    PUSH entries;
		    PUSH i_8_;
		    PUSH new MethodParameterEntry;
		label_1164:
		    {
			DUP
			if (nameIndex == 0)
			    PUSH null;
			else
			    PUSH (String) getScope().lookupConstant(nameIndex);
			break label_1164;
		    }
		    ((UNCONSTRUCTED)POP).MethodParameterEntry(POP, flags);
		    POP[POP] = POP;
		    i_8_++;
		}
	    }
	    default: {
		byte[] blob = new byte[length];
		int offset = buffer.position();
		buffer.read(blob, 0, blob.length);
		return new BlobAttribute(name, blob, offset);
	    }
	    }
	}
	return SourceAttribute.create(name);
    }
    
    protected void inflateAttributes(SourceAttribute[] attributes) {
	VerifyArgument.noNullElements(attributes, "attributes");
	if (attributes.length != 0) {
	    Buffer buffer = null;
	    for (int i = 0; i < attributes.length; i++) {
		SourceAttribute attribute = attributes[i];
	    label_1166:
		{
		label_1165:
		    {
			if (attribute instanceof BlobAttribute) {
			    if (buffer == null)
				buffer = new Buffer(attribute.getLength());
			    break label_1165;
			}
			break label_1166;
		    }
		    attributes[i] = inflateAttribute(buffer, attribute);
		}
	    }
	}
	return;
    }
    
    protected final SourceAttribute inflateAttribute
	(SourceAttribute attribute) {
	return inflateAttribute(new Buffer(0), attribute);
    }
    
    protected final SourceAttribute inflateAttribute
	(Buffer buffer, SourceAttribute attribute) {
	if (!(attribute instanceof BlobAttribute))
	    return attribute;
	buffer.reset(attribute.getLength());
	BlobAttribute blobAttribute = (BlobAttribute) attribute;
	System.arraycopy(blobAttribute.getData(), 0, buffer.array(), 0,
			 attribute.getLength());
	return readAttributeCore(attribute.getName(), buffer,
				 blobAttribute.getDataOffset(),
				 attribute.getLength());
    }
    
    protected void inflateAttributes(List attributes) {
	VerifyArgument.noNullElements(attributes, "attributes");
	if (!attributes.isEmpty()) {
	    Buffer buffer = null;
	    for (int i = 0; i < attributes.size(); i++) {
		SourceAttribute attribute
		    = (SourceAttribute) attributes.get(i);
	    label_1168:
		{
		label_1167:
		    {
			if (attribute instanceof BlobAttribute) {
			    if (buffer != null) {
				if (buffer.size() >= attribute.getLength())
				    buffer.position(0);
				else
				    buffer.reset(attribute.getLength());
			    } else
				buffer = new Buffer(attribute.getLength());
			    break label_1167;
			}
			break label_1168;
		    }
		    BlobAttribute blobAttribute = (BlobAttribute) attribute;
		    System.arraycopy(blobAttribute.getData(), 0,
				     buffer.array(), 0, attribute.getLength());
		    attributes.set(i,
				   readAttributeCore(attribute.getName(),
						     buffer,
						     blobAttribute
							 .getDataOffset(),
						     attribute.getLength()));
		}
	    }
	}
	return;
    }
}
