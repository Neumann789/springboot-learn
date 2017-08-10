/* ConstantPoolPrinter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import com.strobel.assembler.ir.ConstantPool;
import com.strobel.core.StringUtilities;
import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.DecompilerHelpers;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.ITextOutput;

public class ConstantPoolPrinter implements ConstantPool.Visitor
{
    private static final int MAX_TAG_LENGTH;
    private final ITextOutput _output;
    private final DecompilerSettings _settings;
    private boolean _isHeaderPrinted;
    
    public ConstantPoolPrinter(ITextOutput output) {
	this(output, DecompilerSettings.javaDefaults());
    }
    
    public ConstantPoolPrinter(ITextOutput output,
			       DecompilerSettings settings) {
	_output = (ITextOutput) VerifyArgument.notNull(output, "output");
	_settings = (DecompilerSettings) VerifyArgument.notNull(settings,
								"settings");
    }
    
    protected void printTag(ConstantPool.Tag tag) {
	_output.writeAttribute(String.format("%1$-" + MAX_TAG_LENGTH + "s  ",
					     new Object[] { tag }));
    }
    
    public void visit(ConstantPool.Entry entry) {
    label_1191:
	{
	    VerifyArgument.notNull(entry, "entry");
	    if (!_isHeaderPrinted) {
		_output.writeAttribute("Constant Pool");
		_output.write(':');
		_output.writeLine();
		_isHeaderPrinted = true;
	    }
	    break label_1191;
	}
	_output.indent();
	_output.writeLiteral(String.format("%1$5d",
					   (new Object[]
					    { Integer
						  .valueOf(entry.index) })));
	_output.write(": ");
	printTag(entry.getTag());
	entry.accept(this);
	_output.writeLine();
	_output.unindent();
    }
    
    public void visitTypeInfo(ConstantPool.TypeInfoEntry info) {
	_output.writeDelimiter("#");
	_output.writeLiteral(String.format("%1$-14d",
					   (new Object[]
					    { Integer.valueOf(info
							      .nameIndex) })));
	_output.writeComment
	    (String.format("//  %1$s",
			   (new Object[]
			    { StringUtilities.escape
			      (info.getName(), false,
			       _settings.isUnicodeOutputEnabled()) })));
    }
    
    public void visitDoubleConstant(ConstantPool.DoubleConstantEntry info) {
	DecompilerHelpers.writePrimitiveValue(_output,
					      info.getConstantValue());
    }
    
    public void visitFieldReference(ConstantPool.FieldReferenceEntry info) {
	ConstantPool.NameAndTypeDescriptorEntry nameAndTypeInfo
	    = info.getNameAndTypeInfo();
	int startColumn = _output.getColumn();
	_output.writeDelimiter("#");
	_output.writeLiteral(Integer.valueOf(info.typeInfoIndex));
	_output.writeDelimiter(".");
	_output.writeDelimiter("#");
	_output.writeLiteral(Integer.valueOf(info.nameAndTypeDescriptorIndex));
	int endColumn = _output.getColumn();
    label_1192:
	{
	    int padding = 14 - (endColumn - startColumn);
	    if (padding <= 0)
		PUSH "";
	    else
		PUSH StringUtilities.repeat(' ', padding);
	    break label_1192;
	}
	String paddingText = POP;
	_output.writeComment
	    (String.format
	     (paddingText + " //  %1$s.%2$s:%3$s",
	      (new Object[]
	       { StringUtilities.escape(info.getClassName(), false,
					_settings.isUnicodeOutputEnabled()),
		 StringUtilities.escape(nameAndTypeInfo.getName(), false,
					_settings.isUnicodeOutputEnabled()),
		 StringUtilities.escape(nameAndTypeInfo.getType(), false,
					_settings
					    .isUnicodeOutputEnabled()) })));
    }
    
    public void visitFloatConstant(ConstantPool.FloatConstantEntry info) {
	DecompilerHelpers.writePrimitiveValue(_output,
					      info.getConstantValue());
    }
    
    public void visitIntegerConstant(ConstantPool.IntegerConstantEntry info) {
	DecompilerHelpers.writePrimitiveValue(_output,
					      info.getConstantValue());
    }
    
    public void visitInterfaceMethodReference
	(ConstantPool.InterfaceMethodReferenceEntry info) {
	ConstantPool.NameAndTypeDescriptorEntry nameAndTypeInfo
	    = info.getNameAndTypeInfo();
	int startColumn = _output.getColumn();
	_output.writeDelimiter("#");
	_output.writeLiteral(Integer.valueOf(info.typeInfoIndex));
	_output.writeDelimiter(".");
	_output.writeDelimiter("#");
	_output.writeLiteral(Integer.valueOf(info.nameAndTypeDescriptorIndex));
	int endColumn = _output.getColumn();
    label_1193:
	{
	    int padding = 14 - (endColumn - startColumn);
	    if (padding <= 0)
		PUSH "";
	    else
		PUSH StringUtilities.repeat(' ', padding);
	    break label_1193;
	}
	String paddingText = POP;
	_output.writeComment
	    (String.format
	     (paddingText + " //  %1$s.%2$s:%3$s",
	      (new Object[]
	       { StringUtilities.escape(info.getClassName(), false,
					_settings.isUnicodeOutputEnabled()),
		 StringUtilities.escape(nameAndTypeInfo.getName(), false,
					_settings.isUnicodeOutputEnabled()),
		 StringUtilities.escape(nameAndTypeInfo.getType(), false,
					_settings
					    .isUnicodeOutputEnabled()) })));
    }
    
    public void visitInvokeDynamicInfo
	(ConstantPool.InvokeDynamicInfoEntry info) {
	ConstantPool.NameAndTypeDescriptorEntry nameAndTypeInfo
	    = info.getNameAndTypeDescriptor();
	int startColumn = _output.getColumn();
	_output
	    .writeLiteral(Integer.valueOf(info.bootstrapMethodAttributeIndex));
	_output.writeDelimiter(", ");
	_output.writeDelimiter("#");
	_output.writeLiteral(Integer.valueOf(nameAndTypeInfo.nameIndex));
	_output.writeDelimiter(".");
	_output.writeDelimiter("#");
	_output.writeLiteral(Integer.valueOf(nameAndTypeInfo
					     .typeDescriptorIndex));
	int endColumn = _output.getColumn();
    label_1194:
	{
	    int padding = 14 - (endColumn - startColumn);
	    if (padding <= 0)
		PUSH "";
	    else
		PUSH StringUtilities.repeat(' ', padding);
	    break label_1194;
	}
	String paddingText = POP;
	_output.writeComment
	    (String.format
	     (paddingText + " //  %1$s:%2$s",
	      (new Object[]
	       { StringUtilities.escape(nameAndTypeInfo.getName(), false,
					_settings.isUnicodeOutputEnabled()),
		 StringUtilities.escape(nameAndTypeInfo.getType(), false,
					_settings
					    .isUnicodeOutputEnabled()) })));
    }
    
    public void visitLongConstant(ConstantPool.LongConstantEntry info) {
	DecompilerHelpers.writePrimitiveValue(_output,
					      info.getConstantValue());
    }
    
    public void visitNameAndTypeDescriptor
	(ConstantPool.NameAndTypeDescriptorEntry info) {
	int startColumn = _output.getColumn();
	_output.writeDelimiter("#");
	_output.writeLiteral(Integer.valueOf(info.nameIndex));
	_output.writeDelimiter(".");
	_output.writeDelimiter("#");
	_output.writeLiteral(Integer.valueOf(info.typeDescriptorIndex));
	int endColumn = _output.getColumn();
    label_1195:
	{
	    int padding = 14 - (endColumn - startColumn);
	    if (padding <= 0)
		PUSH "";
	    else
		PUSH StringUtilities.repeat(' ', padding);
	    break label_1195;
	}
	String paddingText = POP;
	_output.writeComment
	    (String.format
	     (paddingText + " //  %1$s:%2$s",
	      (new Object[]
	       { StringUtilities.escape(info.getName(), false,
					_settings.isUnicodeOutputEnabled()),
		 StringUtilities.escape(info.getType(), false,
					_settings
					    .isUnicodeOutputEnabled()) })));
    }
    
    public void visitMethodReference(ConstantPool.MethodReferenceEntry info) {
	ConstantPool.NameAndTypeDescriptorEntry nameAndTypeInfo
	    = info.getNameAndTypeInfo();
	int startColumn = _output.getColumn();
	_output.writeDelimiter("#");
	_output.writeLiteral(Integer.valueOf(info.typeInfoIndex));
	_output.writeDelimiter(".");
	_output.writeDelimiter("#");
	_output.writeLiteral(Integer.valueOf(info.nameAndTypeDescriptorIndex));
	int endColumn = _output.getColumn();
    label_1196:
	{
	    int padding = 14 - (endColumn - startColumn);
	    if (padding <= 0)
		PUSH "";
	    else
		PUSH StringUtilities.repeat(' ', padding);
	    break label_1196;
	}
	String paddingText = POP;
	_output.writeComment
	    (String.format
	     (paddingText + " //  %1$s.%2$s:%3$s",
	      (new Object[]
	       { StringUtilities.escape(info.getClassName(), false,
					_settings.isUnicodeOutputEnabled()),
		 StringUtilities.escape(nameAndTypeInfo.getName(), false,
					_settings.isUnicodeOutputEnabled()),
		 StringUtilities.escape(nameAndTypeInfo.getType(), false,
					_settings
					    .isUnicodeOutputEnabled()) })));
    }
    
    public void visitMethodHandle(ConstantPool.MethodHandleEntry info) {
	ConstantPool.ReferenceEntry reference = info.getReference();
	ConstantPool.NameAndTypeDescriptorEntry nameAndTypeInfo
	    = reference.getNameAndTypeInfo();
	int startColumn = _output.getColumn();
	_output.writeLiteral(info.referenceKind);
	_output.write(' ');
	_output.writeDelimiter("#");
	_output.writeLiteral(Integer.valueOf(reference.typeInfoIndex));
	_output.writeDelimiter(".");
	_output.writeDelimiter("#");
	_output.writeLiteral(Integer.valueOf(reference
					     .nameAndTypeDescriptorIndex));
	int endColumn = _output.getColumn();
    label_1197:
	{
	    int padding = 28 - (endColumn - startColumn);
	    if (padding <= 0)
		PUSH "";
	    else
		PUSH StringUtilities.repeat(' ', padding);
	    break label_1197;
	}
	String paddingText = POP;
	_output.writeComment
	    (String.format
	     (paddingText + " //  %1$s.%2$s:%3$s",
	      (new Object[]
	       { StringUtilities.escape(reference.getClassName(), false,
					_settings.isUnicodeOutputEnabled()),
		 StringUtilities.escape(nameAndTypeInfo.getName(), false,
					_settings.isUnicodeOutputEnabled()),
		 StringUtilities.escape(nameAndTypeInfo.getType(), false,
					_settings
					    .isUnicodeOutputEnabled()) })));
    }
    
    public void visitMethodType(ConstantPool.MethodTypeEntry info) {
	_output.write("%1$-13s", new Object[] { info.getType() });
    }
    
    public void visitStringConstant(ConstantPool.StringConstantEntry info) {
	_output.writeDelimiter("#");
	_output.writeLiteral
	    (String.format("%1$-14d",
			   (new Object[]
			    { Integer.valueOf(info.stringIndex) })));
	_output.writeComment
	    (String.format("//  %1$s",
			   (new Object[]
			    { StringUtilities.escape
			      (info.getValue(), true,
			       _settings.isUnicodeOutputEnabled()) })));
    }
    
    public void visitUtf8StringConstant
	(ConstantPool.Utf8StringConstantEntry info) {
	DecompilerHelpers.writePrimitiveValue(_output,
					      info.getConstantValue());
    }
    
    public void visitEnd() {
	/* empty */
    }
    
    static {
	int maxTagLength = 0;
	ConstantPool.Tag[] arr$ = ConstantPool.Tag.values();
	int len$ = arr$.length;
	int i$ = 0;
	for (;;) {
	    if (i$ >= len$)
		MAX_TAG_LENGTH = maxTagLength;
	    ConstantPool.Tag tag = arr$[i$];
	label_1190:
	    {
		int length = tag.name().length();
		if (length > maxTagLength)
		    maxTagLength = length;
		break label_1190;
	    }
	    i$++;
	}
    }
}
