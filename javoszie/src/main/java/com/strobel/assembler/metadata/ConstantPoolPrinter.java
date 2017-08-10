 package com.strobel.assembler.metadata;
 
 import com.strobel.assembler.ir.ConstantPool.Entry;
 import com.strobel.assembler.ir.ConstantPool.FieldReferenceEntry;
 import com.strobel.assembler.ir.ConstantPool.FloatConstantEntry;
 import com.strobel.assembler.ir.ConstantPool.InterfaceMethodReferenceEntry;
 import com.strobel.assembler.ir.ConstantPool.InvokeDynamicInfoEntry;
 import com.strobel.assembler.ir.ConstantPool.MethodHandleEntry;
 import com.strobel.assembler.ir.ConstantPool.MethodReferenceEntry;
 import com.strobel.assembler.ir.ConstantPool.MethodTypeEntry;
 import com.strobel.assembler.ir.ConstantPool.NameAndTypeDescriptorEntry;
 import com.strobel.assembler.ir.ConstantPool.ReferenceEntry;
 import com.strobel.assembler.ir.ConstantPool.StringConstantEntry;
 import com.strobel.assembler.ir.ConstantPool.Tag;
 import com.strobel.assembler.ir.ConstantPool.TypeInfoEntry;
 import com.strobel.assembler.ir.ConstantPool.Utf8StringConstantEntry;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilerHelpers;
 import com.strobel.decompiler.DecompilerSettings;
 import com.strobel.decompiler.ITextOutput;
 
 public class ConstantPoolPrinter implements com.strobel.assembler.ir.ConstantPool.Visitor
 {
   private static final int MAX_TAG_LENGTH;
   private final ITextOutput _output;
   private final DecompilerSettings _settings;
   private boolean _isHeaderPrinted;
   
   static
   {
     int maxTagLength = 0;
     
     for (ConstantPool.Tag tag : ConstantPool.Tag.values()) {
       int length = tag.name().length();
       
       if (length > maxTagLength) {
         maxTagLength = length;
       }
     }
     
     MAX_TAG_LENGTH = maxTagLength;
   }
   
 
 
 
   public ConstantPoolPrinter(ITextOutput output)
   {
     this(output, DecompilerSettings.javaDefaults());
   }
   
   public ConstantPoolPrinter(ITextOutput output, DecompilerSettings settings) {
     this._output = ((ITextOutput)VerifyArgument.notNull(output, "output"));
     this._settings = ((DecompilerSettings)VerifyArgument.notNull(settings, "settings"));
   }
   
   protected void printTag(ConstantPool.Tag tag) {
     this._output.writeAttribute(String.format("%1$-" + MAX_TAG_LENGTH + "s  ", new Object[] { tag }));
   }
   
   public void visit(ConstantPool.Entry entry)
   {
     VerifyArgument.notNull(entry, "entry");
     
     if (!this._isHeaderPrinted) {
       this._output.writeAttribute("Constant Pool");
       this._output.write(':');
       this._output.writeLine();
       this._isHeaderPrinted = true;
     }
     
     this._output.indent();
     this._output.writeLiteral(String.format("%1$5d", new Object[] { Integer.valueOf(entry.index) }));
     this._output.write(": ");
     
     printTag(entry.getTag());
     entry.accept(this);
     
     this._output.writeLine();
     this._output.unindent();
   }
   
   public void visitTypeInfo(ConstantPool.TypeInfoEntry info)
   {
     this._output.writeDelimiter("#");
     this._output.writeLiteral(String.format("%1$-14d", new Object[] { Integer.valueOf(info.nameIndex) }));
     this._output.writeComment(String.format("//  %1$s", new Object[] { StringUtilities.escape(info.getName(), false, this._settings.isUnicodeOutputEnabled()) }));
   }
   
   public void visitDoubleConstant(com.strobel.assembler.ir.ConstantPool.DoubleConstantEntry info)
   {
     DecompilerHelpers.writePrimitiveValue(this._output, info.getConstantValue());
   }
   
   public void visitFieldReference(ConstantPool.FieldReferenceEntry info)
   {
     ConstantPool.NameAndTypeDescriptorEntry nameAndTypeInfo = info.getNameAndTypeInfo();
     int startColumn = this._output.getColumn();
     
     this._output.writeDelimiter("#");
     this._output.writeLiteral(Integer.valueOf(info.typeInfoIndex));
     this._output.writeDelimiter(".");
     this._output.writeDelimiter("#");
     this._output.writeLiteral(Integer.valueOf(info.nameAndTypeDescriptorIndex));
     
     int endColumn = this._output.getColumn();
     int padding = 14 - (endColumn - startColumn);
     String paddingText = padding > 0 ? StringUtilities.repeat(' ', padding) : "";
     
     this._output.writeComment(String.format(paddingText + " //  %1$s.%2$s:%3$s", new Object[] { StringUtilities.escape(info.getClassName(), false, this._settings.isUnicodeOutputEnabled()), StringUtilities.escape(nameAndTypeInfo.getName(), false, this._settings.isUnicodeOutputEnabled()), StringUtilities.escape(nameAndTypeInfo.getType(), false, this._settings.isUnicodeOutputEnabled()) }));
   }
   
 
 
 
 
 
 
 
   public void visitFloatConstant(ConstantPool.FloatConstantEntry info)
   {
     DecompilerHelpers.writePrimitiveValue(this._output, info.getConstantValue());
   }
   
   public void visitIntegerConstant(com.strobel.assembler.ir.ConstantPool.IntegerConstantEntry info)
   {
     DecompilerHelpers.writePrimitiveValue(this._output, info.getConstantValue());
   }
   
   public void visitInterfaceMethodReference(ConstantPool.InterfaceMethodReferenceEntry info)
   {
     ConstantPool.NameAndTypeDescriptorEntry nameAndTypeInfo = info.getNameAndTypeInfo();
     int startColumn = this._output.getColumn();
     
     this._output.writeDelimiter("#");
     this._output.writeLiteral(Integer.valueOf(info.typeInfoIndex));
     this._output.writeDelimiter(".");
     this._output.writeDelimiter("#");
     this._output.writeLiteral(Integer.valueOf(info.nameAndTypeDescriptorIndex));
     
     int endColumn = this._output.getColumn();
     int padding = 14 - (endColumn - startColumn);
     String paddingText = padding > 0 ? StringUtilities.repeat(' ', padding) : "";
     
     this._output.writeComment(String.format(paddingText + " //  %1$s.%2$s:%3$s", new Object[] { StringUtilities.escape(info.getClassName(), false, this._settings.isUnicodeOutputEnabled()), StringUtilities.escape(nameAndTypeInfo.getName(), false, this._settings.isUnicodeOutputEnabled()), StringUtilities.escape(nameAndTypeInfo.getType(), false, this._settings.isUnicodeOutputEnabled()) }));
   }
   
 
 
 
 
 
 
 
   public void visitInvokeDynamicInfo(ConstantPool.InvokeDynamicInfoEntry info)
   {
     ConstantPool.NameAndTypeDescriptorEntry nameAndTypeInfo = info.getNameAndTypeDescriptor();
     int startColumn = this._output.getColumn();
     
     this._output.writeLiteral(Integer.valueOf(info.bootstrapMethodAttributeIndex));
     this._output.writeDelimiter(", ");
     this._output.writeDelimiter("#");
     this._output.writeLiteral(Integer.valueOf(nameAndTypeInfo.nameIndex));
     this._output.writeDelimiter(".");
     this._output.writeDelimiter("#");
     this._output.writeLiteral(Integer.valueOf(nameAndTypeInfo.typeDescriptorIndex));
     
     int endColumn = this._output.getColumn();
     int padding = 14 - (endColumn - startColumn);
     String paddingText = padding > 0 ? StringUtilities.repeat(' ', padding) : "";
     
     this._output.writeComment(String.format(paddingText + " //  %1$s:%2$s", new Object[] { StringUtilities.escape(nameAndTypeInfo.getName(), false, this._settings.isUnicodeOutputEnabled()), StringUtilities.escape(nameAndTypeInfo.getType(), false, this._settings.isUnicodeOutputEnabled()) }));
   }
   
 
 
 
 
 
 
   public void visitLongConstant(com.strobel.assembler.ir.ConstantPool.LongConstantEntry info)
   {
     DecompilerHelpers.writePrimitiveValue(this._output, info.getConstantValue());
   }
   
   public void visitNameAndTypeDescriptor(ConstantPool.NameAndTypeDescriptorEntry info)
   {
     int startColumn = this._output.getColumn();
     
     this._output.writeDelimiter("#");
     this._output.writeLiteral(Integer.valueOf(info.nameIndex));
     this._output.writeDelimiter(".");
     this._output.writeDelimiter("#");
     this._output.writeLiteral(Integer.valueOf(info.typeDescriptorIndex));
     
     int endColumn = this._output.getColumn();
     int padding = 14 - (endColumn - startColumn);
     String paddingText = padding > 0 ? StringUtilities.repeat(' ', padding) : "";
     
     this._output.writeComment(String.format(paddingText + " //  %1$s:%2$s", new Object[] { StringUtilities.escape(info.getName(), false, this._settings.isUnicodeOutputEnabled()), StringUtilities.escape(info.getType(), false, this._settings.isUnicodeOutputEnabled()) }));
   }
   
 
 
 
 
 
 
   public void visitMethodReference(ConstantPool.MethodReferenceEntry info)
   {
     ConstantPool.NameAndTypeDescriptorEntry nameAndTypeInfo = info.getNameAndTypeInfo();
     int startColumn = this._output.getColumn();
     
     this._output.writeDelimiter("#");
     this._output.writeLiteral(Integer.valueOf(info.typeInfoIndex));
     this._output.writeDelimiter(".");
     this._output.writeDelimiter("#");
     this._output.writeLiteral(Integer.valueOf(info.nameAndTypeDescriptorIndex));
     
     int endColumn = this._output.getColumn();
     int padding = 14 - (endColumn - startColumn);
     String paddingText = padding > 0 ? StringUtilities.repeat(' ', padding) : "";
     
     this._output.writeComment(String.format(paddingText + " //  %1$s.%2$s:%3$s", new Object[] { StringUtilities.escape(info.getClassName(), false, this._settings.isUnicodeOutputEnabled()), StringUtilities.escape(nameAndTypeInfo.getName(), false, this._settings.isUnicodeOutputEnabled()), StringUtilities.escape(nameAndTypeInfo.getType(), false, this._settings.isUnicodeOutputEnabled()) }));
   }
   
 
 
 
 
 
 
 
   public void visitMethodHandle(ConstantPool.MethodHandleEntry info)
   {
     ConstantPool.ReferenceEntry reference = info.getReference();
     ConstantPool.NameAndTypeDescriptorEntry nameAndTypeInfo = reference.getNameAndTypeInfo();
     int startColumn = this._output.getColumn();
     
     this._output.writeLiteral(info.referenceKind);
     this._output.write(' ');
     this._output.writeDelimiter("#");
     this._output.writeLiteral(Integer.valueOf(reference.typeInfoIndex));
     this._output.writeDelimiter(".");
     this._output.writeDelimiter("#");
     this._output.writeLiteral(Integer.valueOf(reference.nameAndTypeDescriptorIndex));
     
     int endColumn = this._output.getColumn();
     int padding = 28 - (endColumn - startColumn);
     String paddingText = padding > 0 ? StringUtilities.repeat(' ', padding) : "";
     
     this._output.writeComment(String.format(paddingText + " //  %1$s.%2$s:%3$s", new Object[] { StringUtilities.escape(reference.getClassName(), false, this._settings.isUnicodeOutputEnabled()), StringUtilities.escape(nameAndTypeInfo.getName(), false, this._settings.isUnicodeOutputEnabled()), StringUtilities.escape(nameAndTypeInfo.getType(), false, this._settings.isUnicodeOutputEnabled()) }));
   }
   
 
 
 
 
 
 
 
   public void visitMethodType(ConstantPool.MethodTypeEntry info)
   {
     this._output.write("%1$-13s", new Object[] { info.getType() });
   }
   
   public void visitStringConstant(ConstantPool.StringConstantEntry info)
   {
     this._output.writeDelimiter("#");
     this._output.writeLiteral(String.format("%1$-14d", new Object[] { Integer.valueOf(info.stringIndex) }));
     this._output.writeComment(String.format("//  %1$s", new Object[] { StringUtilities.escape(info.getValue(), true, this._settings.isUnicodeOutputEnabled()) }));
   }
   
   public void visitUtf8StringConstant(ConstantPool.Utf8StringConstantEntry info)
   {
     DecompilerHelpers.writePrimitiveValue(this._output, info.getConstantValue());
   }
   
   public void visitEnd() {}
 }


