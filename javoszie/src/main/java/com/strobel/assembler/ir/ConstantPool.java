 package com.strobel.assembler.ir;
 
 import com.strobel.assembler.metadata.Buffer;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.Freezable;
 import com.strobel.core.HashUtilities;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import java.io.DataOutputStream;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 
 
 
 
 
 
 
 
 public final class ConstantPool
   extends Freezable
   implements Iterable<Entry>
 {
   private final ArrayList<Entry> _pool;
   private final HashMap<Key, Entry> _entryMap;
   private final Key _lookupKey;
   private final Key _newKey;
   private int _size;
   
   public ConstantPool()
   {
     this._pool = new ArrayList();
     this._entryMap = new HashMap();
     this._lookupKey = new Key(null);
     this._newKey = new Key(null);
   }
   
 
   public Iterator<Entry> iterator()
   {
     return this._pool.iterator();
   }
   
   public void accept(Visitor visitor) {
     VerifyArgument.notNull(visitor, "visitor");
     
     for (Entry entry : this._pool) {
       if (entry != null) {
         visitor.visit(entry);
       }
     }
   }
   
   public void write(Buffer stream) {
     stream.writeShort(this._size + 1);
     accept(new Writer(stream, null));
   }
   
   public <T extends Entry> T getEntry(int index)
   {
     VerifyArgument.inRange(0, this._size + 1, index, "index");
     
     Entry info = (Entry)this._pool.get(index - 1);
     
     if (info == null) {
       throw new IndexOutOfBoundsException();
     }
     
     return info;
   }
   
   public Entry get(int index) {
     VerifyArgument.inRange(0, this._size + 1, index, "index");
     
     Entry info = (Entry)this._pool.get(index - 1);
     
     if (info == null) {
       throw new IndexOutOfBoundsException();
     }
     
     return info;
   }
   
   public Entry get(int index, Tag expectedType) {
     VerifyArgument.inRange(0, this._size + 1, index, "index");
     
     Entry entry = get(index);
     Tag actualType = entry.getTag();
     
     if (actualType != expectedType) {
       throw new IllegalStateException(String.format("Expected type '%s' but found type '%s'.", new Object[] { expectedType, actualType }));
     }
     
 
 
 
 
 
 
     return entry;
   }
   
   public String lookupStringConstant(int index) {
     StringConstantEntry entry = (StringConstantEntry)get(index, Tag.StringConstant);
     return entry.getValue();
   }
   
   public String lookupUtf8Constant(int index) {
     Utf8StringConstantEntry entry = (Utf8StringConstantEntry)get(index, Tag.Utf8StringConstant);
     return entry.value;
   }
   
   public <T> T lookupConstant(int index)
   {
     ConstantEntry entry = (ConstantEntry)get(index);
     return (T)entry.getConstantValue();
   }
   
   public int lookupIntegerConstant(int index) {
     IntegerConstantEntry entry = (IntegerConstantEntry)get(index, Tag.IntegerConstant);
     return entry.value;
   }
   
   public long lookupLongConstant(int index) {
     LongConstantEntry entry = (LongConstantEntry)get(index, Tag.LongConstant);
     return entry.value;
   }
   
   public float lookupFloatConstant(int index) {
     FloatConstantEntry entry = (FloatConstantEntry)get(index, Tag.FloatConstant);
     return entry.value;
   }
   
   public double lookupDoubleConstant(int index) {
     DoubleConstantEntry entry = (DoubleConstantEntry)get(index, Tag.DoubleConstant);
     return entry.value;
   }
   
   public Utf8StringConstantEntry getUtf8StringConstant(String value) {
     this._lookupKey.set(value);
     Entry entry = (Entry)this._entryMap.get(this._lookupKey);
     if (entry == null) {
       if (isFrozen()) {
         return null;
       }
       entry = new Utf8StringConstantEntry(this, value);
     }
     this._lookupKey.clear();
     return (Utf8StringConstantEntry)entry;
   }
   
   public StringConstantEntry getStringConstant(String value) {
     Utf8StringConstantEntry utf8Constant = getUtf8StringConstant(value);
     this._lookupKey.set(Tag.StringConstant, utf8Constant.index);
     Entry entry = (Entry)this._entryMap.get(this._lookupKey);
     if (entry == null) {
       if (isFrozen()) {
         return null;
       }
       entry = new StringConstantEntry(this, utf8Constant.index);
     }
     this._lookupKey.clear();
     return (StringConstantEntry)entry;
   }
   
   public IntegerConstantEntry getIntegerConstant(int value) {
     this._lookupKey.set(value);
     Entry entry = (Entry)this._entryMap.get(this._lookupKey);
     if (entry == null) {
       if (isFrozen()) {
         return null;
       }
       entry = new IntegerConstantEntry(this, value);
     }
     this._lookupKey.clear();
     return (IntegerConstantEntry)entry;
   }
   
   public FloatConstantEntry getFloatConstant(float value) {
     this._lookupKey.set(value);
     Entry entry = (Entry)this._entryMap.get(this._lookupKey);
     if (entry == null) {
       if (isFrozen()) {
         return null;
       }
       entry = new FloatConstantEntry(this, value);
     }
     this._lookupKey.clear();
     return (FloatConstantEntry)entry;
   }
   
   public LongConstantEntry getLongConstant(long value) {
     this._lookupKey.set(value);
     Entry entry = (Entry)this._entryMap.get(this._lookupKey);
     if (entry == null) {
       if (isFrozen()) {
         return null;
       }
       entry = new LongConstantEntry(this, value);
     }
     this._lookupKey.clear();
     return (LongConstantEntry)entry;
   }
   
   public DoubleConstantEntry getDoubleConstant(double value) {
     this._lookupKey.set(value);
     Entry entry = (Entry)this._entryMap.get(this._lookupKey);
     if (entry == null) {
       if (isFrozen()) {
         return null;
       }
       entry = new DoubleConstantEntry(this, value);
     }
     this._lookupKey.clear();
     return (DoubleConstantEntry)entry;
   }
   
   public TypeInfoEntry getTypeInfo(TypeReference type) {
     Utf8StringConstantEntry name = getUtf8StringConstant(type.getInternalName());
     this._lookupKey.set(Tag.TypeInfo, name.index);
     Entry entry = (Entry)this._entryMap.get(this._lookupKey);
     if (entry == null) {
       if (isFrozen()) {
         return null;
       }
       entry = new TypeInfoEntry(this, name.index);
     }
     this._lookupKey.clear();
     return (TypeInfoEntry)entry;
   }
   
   public FieldReferenceEntry getFieldReference(FieldReference field) {
     TypeInfoEntry typeInfo = getTypeInfo(field.getDeclaringType());
     NameAndTypeDescriptorEntry nameAndDescriptor = getNameAndTypeDescriptor(field.getName(), field.getErasedSignature());
     
 
 
     this._lookupKey.set(Tag.FieldReference, typeInfo.index, nameAndDescriptor.index);
     Entry entry = (Entry)this._entryMap.get(this._lookupKey);
     if (entry == null) {
       if (isFrozen()) {
         return null;
       }
       entry = new FieldReferenceEntry(this, typeInfo.index, nameAndDescriptor.index);
     }
     this._lookupKey.clear();
     return (FieldReferenceEntry)entry;
   }
   
   public MethodReferenceEntry getMethodReference(MethodReference method) {
     TypeInfoEntry typeInfo = getTypeInfo(method.getDeclaringType());
     NameAndTypeDescriptorEntry nameAndDescriptor = getNameAndTypeDescriptor(method.getName(), method.getErasedSignature());
     
 
 
     this._lookupKey.set(Tag.MethodReference, typeInfo.index, nameAndDescriptor.index);
     Entry entry = (Entry)this._entryMap.get(this._lookupKey);
     if (entry == null) {
       if (isFrozen()) {
         return null;
       }
       entry = new MethodReferenceEntry(this, typeInfo.index, nameAndDescriptor.index);
     }
     this._lookupKey.clear();
     return (MethodReferenceEntry)entry;
   }
   
   public InterfaceMethodReferenceEntry getInterfaceMethodReference(MethodReference method) {
     TypeInfoEntry typeInfo = getTypeInfo(method.getDeclaringType());
     NameAndTypeDescriptorEntry nameAndDescriptor = getNameAndTypeDescriptor(method.getName(), method.getErasedSignature());
     
 
 
     this._lookupKey.set(Tag.InterfaceMethodReference, typeInfo.index, nameAndDescriptor.index);
     Entry entry = (Entry)this._entryMap.get(this._lookupKey);
     if (entry == null) {
       if (isFrozen()) {
         return null;
       }
       entry = new InterfaceMethodReferenceEntry(this, typeInfo.index, nameAndDescriptor.index);
     }
     this._lookupKey.clear();
     return (InterfaceMethodReferenceEntry)entry;
   }
   
   NameAndTypeDescriptorEntry getNameAndTypeDescriptor(String name, String typeDescriptor) {
     Utf8StringConstantEntry utf8Name = getUtf8StringConstant(name);
     Utf8StringConstantEntry utf8Descriptor = getUtf8StringConstant(typeDescriptor);
     this._lookupKey.set(Tag.NameAndTypeDescriptor, utf8Name.index, utf8Descriptor.index);
     Entry entry = (Entry)this._entryMap.get(this._lookupKey);
     if (entry == null) {
       if (isFrozen()) {
         return null;
       }
       entry = new NameAndTypeDescriptorEntry(this, utf8Name.index, utf8Descriptor.index);
     }
     this._lookupKey.clear();
     return (NameAndTypeDescriptorEntry)entry;
   }
   
   MethodHandleEntry getMethodHandle(ReferenceKind referenceKind, int referenceIndex) {
     this._lookupKey.set(Tag.MethodHandle, referenceIndex, referenceKind);
     Entry entry = (Entry)this._entryMap.get(this._lookupKey);
     if (entry == null) {
       if (isFrozen()) {
         return null;
       }
       entry = new MethodHandleEntry(this, referenceKind, referenceIndex);
     }
     this._lookupKey.clear();
     return (MethodHandleEntry)entry;
   }
   
   MethodTypeEntry getMethodType(int descriptorIndex) {
     this._lookupKey.set(Tag.MethodType, descriptorIndex);
     Entry entry = (Entry)this._entryMap.get(this._lookupKey);
     if (entry == null) {
       if (isFrozen()) {
         return null;
       }
       entry = new MethodTypeEntry(this, descriptorIndex);
     }
     this._lookupKey.clear();
     return (MethodTypeEntry)entry;
   }
   
 
   InvokeDynamicInfoEntry getInvokeDynamicInfo(int bootstrapMethodAttributeIndex, int nameAndTypeDescriptorIndex)
   {
     this._lookupKey.set(Tag.InvokeDynamicInfo, bootstrapMethodAttributeIndex, nameAndTypeDescriptorIndex);
     Entry entry = (Entry)this._entryMap.get(this._lookupKey);
     if (entry == null) {
       if (isFrozen()) {
         return null;
       }
       entry = new InvokeDynamicInfoEntry(this, bootstrapMethodAttributeIndex, nameAndTypeDescriptorIndex);
     }
     this._lookupKey.clear();
     return (InvokeDynamicInfoEntry)entry;
   }
   
   public static ConstantPool read(Buffer b) {
     boolean skipOne = false;
     
     ConstantPool pool = new ConstantPool();
     int size = b.readUnsignedShort();
     Key key = new Key(null);
     
     for (int i = 1; i < size; i++) {
       if (skipOne) {
         skipOne = false;
       }
       else
       {
         key.clear();
         
         Tag tag = Tag.fromValue(b.readUnsignedByte());
         
         switch (tag) {
         case Utf8StringConstant: 
           new Utf8StringConstantEntry(pool, b.readUtf8());
           break;
         case IntegerConstant: 
           new IntegerConstantEntry(pool, b.readInt());
           break;
         case FloatConstant: 
           new FloatConstantEntry(pool, b.readFloat());
           break;
         case LongConstant: 
           new LongConstantEntry(pool, b.readLong());
           skipOne = true;
           break;
         case DoubleConstant: 
           new DoubleConstantEntry(pool, b.readDouble());
           skipOne = true;
           break;
         case TypeInfo: 
           new TypeInfoEntry(pool, b.readUnsignedShort());
           break;
         case StringConstant: 
           new StringConstantEntry(pool, b.readUnsignedShort());
           break;
         case FieldReference: 
           new FieldReferenceEntry(pool, b.readUnsignedShort(), b.readUnsignedShort());
           break;
         case MethodReference: 
           new MethodReferenceEntry(pool, b.readUnsignedShort(), b.readUnsignedShort());
           break;
         case InterfaceMethodReference: 
           new InterfaceMethodReferenceEntry(pool, b.readUnsignedShort(), b.readUnsignedShort());
           break;
         case NameAndTypeDescriptor: 
           new NameAndTypeDescriptorEntry(pool, b.readUnsignedShort(), b.readUnsignedShort());
           break;
         case MethodHandle: 
           new MethodHandleEntry(pool, ReferenceKind.fromTag(b.readUnsignedByte()), b.readUnsignedShort());
           break;
         case MethodType: 
           new MethodTypeEntry(pool, b.readUnsignedShort());
           break;
         case InvokeDynamicInfo: 
           new InvokeDynamicInfoEntry(pool, b.readUnsignedShort(), b.readUnsignedShort());
         }
         
       }
     }
     return pool;
   }
   
 
   public static abstract class Entry
   {
     public final int index;
     protected final ConstantPool owner;
     
     Entry(ConstantPool owner)
     {
       this.owner = owner;
       this.index = (owner._size + 1);
       owner._pool.add(this);
       ConstantPool.access$212(owner, size());
       for (int i = 1; i < size(); i++) {
         owner._pool.add(null);
       }
     }
     
 
     abstract void fixupKey(ConstantPool.Key paramKey);
     
 
     public abstract ConstantPool.Tag getTag();
     
     public int size()
     {
       return 1;
     }
     
     public abstract int byteLength();
     
     public abstract void accept(ConstantPool.Visitor paramVisitor);
   }
   
   public static abstract class ConstantEntry extends ConstantPool.Entry {
     ConstantEntry(ConstantPool owner) {
       super();
     }
     
 
 
     public abstract Object getConstantValue();
   }
   
 
   public static enum ReferenceKind
   {
     GetField(1, "getfield"), 
     GetStatic(2, "getstatic"), 
     PutField(3, "putfield"), 
     PutStatic(4, "putstatic"), 
     InvokeVirtual(5, "invokevirtual"), 
     InvokeStatic(6, "invokestatic"), 
     InvokeSpecial(7, "invokespecial"), 
     NewInvokeSpecial(8, "newinvokespecial"), 
     InvokeInterface(9, "invokeinterface");
     
     public final int tag;
     public final String name;
     
     private ReferenceKind(int tag, String name) {
       this.tag = tag;
       this.name = name;
     }
     
     static ReferenceKind fromTag(int tag) {
       switch (tag) {
       case 1: 
         return GetField;
       case 2: 
         return GetStatic;
       case 3: 
         return PutField;
       case 4: 
         return PutStatic;
       case 5: 
         return InvokeVirtual;
       case 6: 
         return InvokeStatic;
       case 7: 
         return InvokeSpecial;
       case 8: 
         return NewInvokeSpecial;
       case 9: 
         return InvokeInterface;
       }
       return null;
     }
   }
   
 
 
 
 
   public static enum Tag
   {
     Utf8StringConstant(1), 
     IntegerConstant(3), 
     FloatConstant(4), 
     LongConstant(5), 
     DoubleConstant(6), 
     TypeInfo(7), 
     StringConstant(8), 
     FieldReference(9), 
     MethodReference(10), 
     InterfaceMethodReference(11), 
     NameAndTypeDescriptor(12), 
     MethodHandle(15), 
     MethodType(16), 
     InvokeDynamicInfo(18);
     
     public final int value;
     private static final Tag[] lookup;
     
     private Tag(int value) { this.value = value; }
     
     public static Tag fromValue(int value)
     {
       VerifyArgument.inRange(Utf8StringConstant.value, InvokeDynamicInfo.value, value, "value");
       return lookup[value];
     }
     
 
     static
     {
       Tag[] values = values();
       
       lookup = new Tag[InvokeDynamicInfo.value + 1];
       
       for (Tag tag : values) {
         lookup[tag.value] = tag;
       }
     }
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public static abstract interface Visitor
   {
     public static final Visitor EMPTY = new Visitor()
     {
       public void visit(ConstantPool.Entry entry) {}
       
 
       public void visitTypeInfo(ConstantPool.TypeInfoEntry info) {}
       
 
       public void visitDoubleConstant(ConstantPool.DoubleConstantEntry info) {}
       
       public void visitFieldReference(ConstantPool.FieldReferenceEntry info) {}
       
       public void visitFloatConstant(ConstantPool.FloatConstantEntry info) {}
       
       public void visitIntegerConstant(ConstantPool.IntegerConstantEntry info) {}
       
       public void visitInterfaceMethodReference(ConstantPool.InterfaceMethodReferenceEntry info) {}
       
       public void visitInvokeDynamicInfo(ConstantPool.InvokeDynamicInfoEntry info) {}
       
       public void visitLongConstant(ConstantPool.LongConstantEntry info) {}
       
       public void visitNameAndTypeDescriptor(ConstantPool.NameAndTypeDescriptorEntry info) {}
       
       public void visitMethodReference(ConstantPool.MethodReferenceEntry info) {}
       
       public void visitMethodHandle(ConstantPool.MethodHandleEntry info) {}
       
       public void visitMethodType(ConstantPool.MethodTypeEntry info) {}
       
       public void visitStringConstant(ConstantPool.StringConstantEntry info) {}
       
       public void visitUtf8StringConstant(ConstantPool.Utf8StringConstantEntry info) {}
       
       public void visitEnd() {}
     };
     
     public abstract void visit(ConstantPool.Entry paramEntry);
     
     public abstract void visitTypeInfo(ConstantPool.TypeInfoEntry paramTypeInfoEntry);
     
     public abstract void visitDoubleConstant(ConstantPool.DoubleConstantEntry paramDoubleConstantEntry);
     
     public abstract void visitFieldReference(ConstantPool.FieldReferenceEntry paramFieldReferenceEntry);
     
     public abstract void visitFloatConstant(ConstantPool.FloatConstantEntry paramFloatConstantEntry);
     
     public abstract void visitIntegerConstant(ConstantPool.IntegerConstantEntry paramIntegerConstantEntry);
     
     public abstract void visitInterfaceMethodReference(ConstantPool.InterfaceMethodReferenceEntry paramInterfaceMethodReferenceEntry);
     
     public abstract void visitInvokeDynamicInfo(ConstantPool.InvokeDynamicInfoEntry paramInvokeDynamicInfoEntry);
     
     public abstract void visitLongConstant(ConstantPool.LongConstantEntry paramLongConstantEntry);
     
     public abstract void visitNameAndTypeDescriptor(ConstantPool.NameAndTypeDescriptorEntry paramNameAndTypeDescriptorEntry);
     
     public abstract void visitMethodReference(ConstantPool.MethodReferenceEntry paramMethodReferenceEntry);
     
     public abstract void visitMethodHandle(ConstantPool.MethodHandleEntry paramMethodHandleEntry);
     
     public abstract void visitMethodType(ConstantPool.MethodTypeEntry paramMethodTypeEntry);
     
     public abstract void visitStringConstant(ConstantPool.StringConstantEntry paramStringConstantEntry);
     
     public abstract void visitUtf8StringConstant(ConstantPool.Utf8StringConstantEntry paramUtf8StringConstantEntry);
     
     public abstract void visitEnd();
   }
   
   private static final class Writer
     implements ConstantPool.Visitor
   {
     private final Buffer codeStream;
     
     private Writer(Buffer codeStream)
     {
       this.codeStream = ((Buffer)VerifyArgument.notNull(codeStream, "codeStream"));
     }
     
     public void visit(ConstantPool.Entry entry)
     {
       entry.accept(this);
     }
     
     public void visitTypeInfo(ConstantPool.TypeInfoEntry info)
     {
       this.codeStream.writeByte(info.getTag().value);
       this.codeStream.writeShort(info.nameIndex);
     }
     
     public void visitDoubleConstant(ConstantPool.DoubleConstantEntry info)
     {
       this.codeStream.writeByte(info.getTag().value);
       this.codeStream.writeDouble(info.value);
     }
     
     public void visitFieldReference(ConstantPool.FieldReferenceEntry info)
     {
       this.codeStream.writeByte(info.getTag().value);
       this.codeStream.writeShort(info.typeInfoIndex);
       this.codeStream.writeShort(info.nameAndTypeDescriptorIndex);
     }
     
     public void visitFloatConstant(ConstantPool.FloatConstantEntry info)
     {
       this.codeStream.writeByte(info.getTag().value);
       this.codeStream.writeFloat(info.value);
     }
     
     public void visitIntegerConstant(ConstantPool.IntegerConstantEntry info)
     {
       this.codeStream.writeByte(info.getTag().value);
       this.codeStream.writeInt(info.value);
     }
     
     public void visitInterfaceMethodReference(ConstantPool.InterfaceMethodReferenceEntry info)
     {
       this.codeStream.writeByte(info.getTag().value);
       this.codeStream.writeShort(info.typeInfoIndex);
       this.codeStream.writeShort(info.nameAndTypeDescriptorIndex);
     }
     
     public void visitInvokeDynamicInfo(ConstantPool.InvokeDynamicInfoEntry info)
     {
       this.codeStream.writeByte(info.getTag().value);
       this.codeStream.writeShort(info.bootstrapMethodAttributeIndex);
       this.codeStream.writeShort(info.nameAndTypeDescriptorIndex);
     }
     
     public void visitLongConstant(ConstantPool.LongConstantEntry info)
     {
       this.codeStream.writeByte(info.getTag().value);
       this.codeStream.writeLong(info.value);
     }
     
     public void visitNameAndTypeDescriptor(ConstantPool.NameAndTypeDescriptorEntry info)
     {
       this.codeStream.writeByte(info.getTag().value);
       this.codeStream.writeShort(info.nameIndex);
       this.codeStream.writeShort(info.typeDescriptorIndex);
     }
     
     public void visitMethodReference(ConstantPool.MethodReferenceEntry info)
     {
       this.codeStream.writeByte(info.getTag().value);
       this.codeStream.writeShort(info.typeInfoIndex);
       this.codeStream.writeShort(info.nameAndTypeDescriptorIndex);
     }
     
     public void visitMethodHandle(ConstantPool.MethodHandleEntry info)
     {
       this.codeStream.writeByte(info.getTag().value);
       this.codeStream.writeShort(info.referenceKind.ordinal());
       this.codeStream.writeShort(info.referenceIndex);
     }
     
     public void visitMethodType(ConstantPool.MethodTypeEntry info)
     {
       this.codeStream.writeByte(info.getTag().value);
       this.codeStream.writeShort(info.descriptorIndex);
     }
     
     public void visitStringConstant(ConstantPool.StringConstantEntry info)
     {
       this.codeStream.writeByte(info.getTag().value);
       this.codeStream.writeShort(info.stringIndex);
     }
     
     public void visitUtf8StringConstant(ConstantPool.Utf8StringConstantEntry info)
     {
       this.codeStream.writeByte(info.getTag().value);
       this.codeStream.writeUtf8(info.value);
     }
     
 
     public void visitEnd() {}
   }
   
 
   public static final class TypeInfoEntry
     extends ConstantPool.Entry
   {
     public final int nameIndex;
     
 
     public TypeInfoEntry(ConstantPool owner, int nameIndex)
     {
       super();
       this.nameIndex = nameIndex;
       owner._newKey.set(getTag(), nameIndex);
       owner._entryMap.put(owner._newKey.clone(), this);
       owner._newKey.clear();
     }
     
     public String getName() {
       return ((ConstantPool.Utf8StringConstantEntry)this.owner.get(this.nameIndex, ConstantPool.Tag.Utf8StringConstant)).value;
     }
     
     void fixupKey(ConstantPool.Key key)
     {
       key.set(ConstantPool.Tag.TypeInfo, this.nameIndex);
     }
     
     public ConstantPool.Tag getTag()
     {
       return ConstantPool.Tag.TypeInfo;
     }
     
     public int byteLength()
     {
       return 3;
     }
     
     public void accept(ConstantPool.Visitor visitor)
     {
       visitor.visitTypeInfo(this);
     }
     
     public String toString()
     {
       return "TypeIndex[index: " + this.index + ", nameIndex: " + this.nameIndex + "]";
     }
   }
   
   public static final class MethodTypeEntry extends ConstantPool.Entry {
     public final int descriptorIndex;
     
     public MethodTypeEntry(ConstantPool owner, int descriptorIndex) {
       super();
       this.descriptorIndex = descriptorIndex;
       owner._newKey.set(getTag(), descriptorIndex);
       owner._entryMap.put(owner._newKey.clone(), this);
       owner._newKey.clear();
     }
     
     public String getType() {
       return ((ConstantPool.Utf8StringConstantEntry)this.owner.get(this.descriptorIndex, ConstantPool.Tag.Utf8StringConstant)).value;
     }
     
     void fixupKey(ConstantPool.Key key)
     {
       key.set(ConstantPool.Tag.MethodType, this.descriptorIndex);
     }
     
     public ConstantPool.Tag getTag()
     {
       return ConstantPool.Tag.MethodType;
     }
     
     public int byteLength()
     {
       return 3;
     }
     
     public void accept(ConstantPool.Visitor visitor)
     {
       visitor.visitMethodType(this);
     }
     
     public String toString()
     {
       return "MethodTypeEntry[index: " + this.index + ", descriptorIndex: " + this.descriptorIndex + "]";
     }
   }
   
   public static abstract class ReferenceEntry extends ConstantPool.Entry {
     public final ConstantPool.Tag tag;
     public final int typeInfoIndex;
     public final int nameAndTypeDescriptorIndex;
     
     protected ReferenceEntry(ConstantPool cp, ConstantPool.Tag tag, int typeInfoIndex, int nameAndTypeDescriptorIndex) {
       super();
       this.tag = tag;
       this.typeInfoIndex = typeInfoIndex;
       this.nameAndTypeDescriptorIndex = nameAndTypeDescriptorIndex;
       this.owner._newKey.set(tag, typeInfoIndex, nameAndTypeDescriptorIndex);
       this.owner._entryMap.put(this.owner._newKey.clone(), this);
       this.owner._newKey.clear();
     }
     
     public ConstantPool.Tag getTag() {
       return this.tag;
     }
     
     public int byteLength() {
       return 5;
     }
     
     public ConstantPool.TypeInfoEntry getClassInfo() {
       return (ConstantPool.TypeInfoEntry)this.owner.get(this.typeInfoIndex, ConstantPool.Tag.TypeInfo);
     }
     
     public String getClassName() {
       return getClassInfo().getName();
     }
     
     public ConstantPool.NameAndTypeDescriptorEntry getNameAndTypeInfo() {
       return (ConstantPool.NameAndTypeDescriptorEntry)this.owner.get(this.nameAndTypeDescriptorIndex, ConstantPool.Tag.NameAndTypeDescriptor);
     }
     
     public String toString()
     {
       return getClass().getSimpleName() + "[index: " + this.index + ", typeInfoIndex: " + this.typeInfoIndex + ", nameAndTypeDescriptorIndex: " + this.nameAndTypeDescriptorIndex + "]";
     }
   }
   
 
 
 
   public static final class FieldReferenceEntry
     extends ConstantPool.ReferenceEntry
   {
     public FieldReferenceEntry(ConstantPool owner, int typeIndex, int nameAndTypeDescriptorIndex)
     {
       super(ConstantPool.Tag.FieldReference, typeIndex, nameAndTypeDescriptorIndex);
     }
     
     void fixupKey(ConstantPool.Key key)
     {
       key.set(ConstantPool.Tag.FieldReference, this.typeInfoIndex, this.nameAndTypeDescriptorIndex);
     }
     
     public void accept(ConstantPool.Visitor visitor)
     {
       visitor.visitFieldReference(this);
     }
   }
   
   public static final class MethodReferenceEntry extends ConstantPool.ReferenceEntry {
     public MethodReferenceEntry(ConstantPool owner, int typeIndex, int nameAndTypeDescriptorIndex) {
       super(ConstantPool.Tag.MethodReference, typeIndex, nameAndTypeDescriptorIndex);
     }
     
     void fixupKey(ConstantPool.Key key)
     {
       key.set(ConstantPool.Tag.MethodReference, this.typeInfoIndex, this.nameAndTypeDescriptorIndex);
     }
     
     public void accept(ConstantPool.Visitor visitor)
     {
       visitor.visitMethodReference(this);
     }
   }
   
   public static final class InterfaceMethodReferenceEntry extends ConstantPool.ReferenceEntry {
     public InterfaceMethodReferenceEntry(ConstantPool owner, int typeIndex, int nameAndTypeDescriptorIndex) {
       super(ConstantPool.Tag.InterfaceMethodReference, typeIndex, nameAndTypeDescriptorIndex);
     }
     
     void fixupKey(ConstantPool.Key key)
     {
       key.set(ConstantPool.Tag.InterfaceMethodReference, this.typeInfoIndex, this.nameAndTypeDescriptorIndex);
     }
     
     public void accept(ConstantPool.Visitor visitor)
     {
       visitor.visitInterfaceMethodReference(this);
     }
   }
   
   public static class MethodHandleEntry extends ConstantPool.Entry {
     public final ConstantPool.ReferenceKind referenceKind;
     public final int referenceIndex;
     
     public MethodHandleEntry(ConstantPool owner, ConstantPool.ReferenceKind referenceKind, int referenceIndex) {
       super();
       this.referenceKind = referenceKind;
       this.referenceIndex = referenceIndex;
       owner._newKey.set(getTag(), referenceIndex, referenceKind);
       owner._entryMap.put(owner._newKey.clone(), this);
       owner._newKey.clear();
     }
     
     public ConstantPool.ReferenceEntry getReference() {
       ConstantPool.Tag actual = this.owner.get(this.referenceIndex).getTag();
       
       ConstantPool.Tag expected = ConstantPool.Tag.MethodReference;
       
 
       switch (ConstantPool.1.$SwitchMap$com$strobel$assembler$ir$ConstantPool$Tag[actual.ordinal()]) {
       case 8: 
       case 10: 
         expected = actual;
       }
       
       return (ConstantPool.ReferenceEntry)this.owner.get(this.referenceIndex, expected);
     }
     
     void fixupKey(ConstantPool.Key key)
     {
       key.set(ConstantPool.Tag.MethodHandle, this.referenceIndex, this.referenceKind);
     }
     
     public ConstantPool.Tag getTag()
     {
       return ConstantPool.Tag.MethodHandle;
     }
     
     public int byteLength()
     {
       return 4;
     }
     
     public void accept(ConstantPool.Visitor visitor)
     {
       visitor.visitMethodHandle(this);
     }
   }
   
   public static class NameAndTypeDescriptorEntry extends ConstantPool.Entry {
     public final int nameIndex;
     public final int typeDescriptorIndex;
     
     public NameAndTypeDescriptorEntry(ConstantPool owner, int nameIndex, int typeDescriptorIndex) {
       super();
       this.nameIndex = nameIndex;
       this.typeDescriptorIndex = typeDescriptorIndex;
       owner._newKey.set(getTag(), nameIndex, typeDescriptorIndex);
       owner._entryMap.put(owner._newKey.clone(), this);
       owner._newKey.clear();
     }
     
     void fixupKey(ConstantPool.Key key)
     {
       key.set(ConstantPool.Tag.NameAndTypeDescriptor, this.nameIndex, this.typeDescriptorIndex);
     }
     
     public ConstantPool.Tag getTag() {
       return ConstantPool.Tag.NameAndTypeDescriptor;
     }
     
     public int byteLength() {
       return 5;
     }
     
     public String getName() {
       return ((ConstantPool.Utf8StringConstantEntry)this.owner.get(this.nameIndex, ConstantPool.Tag.Utf8StringConstant)).value;
     }
     
     public String getType() {
       return ((ConstantPool.Utf8StringConstantEntry)this.owner.get(this.typeDescriptorIndex, ConstantPool.Tag.Utf8StringConstant)).value;
     }
     
     public void accept(ConstantPool.Visitor visitor) {
       visitor.visitNameAndTypeDescriptor(this);
     }
     
     public String toString()
     {
       return "NameAndTypeDescriptorEntry[index: " + this.index + ", descriptorIndex: " + this.nameIndex + ", typeDescriptorIndex: " + this.typeDescriptorIndex + "]";
     }
   }
   
 
   public static class InvokeDynamicInfoEntry
     extends ConstantPool.Entry
   {
     public final int bootstrapMethodAttributeIndex;
     
     public final int nameAndTypeDescriptorIndex;
     
     public InvokeDynamicInfoEntry(ConstantPool owner, int bootstrapMethodAttributeIndex, int nameAndTypeDescriptorIndex)
     {
       super();
       this.bootstrapMethodAttributeIndex = bootstrapMethodAttributeIndex;
       this.nameAndTypeDescriptorIndex = nameAndTypeDescriptorIndex;
       owner._newKey.set(getTag(), bootstrapMethodAttributeIndex, nameAndTypeDescriptorIndex);
       owner._entryMap.put(owner._newKey.clone(), this);
       owner._newKey.clear();
     }
     
     void fixupKey(ConstantPool.Key key)
     {
       key.set(ConstantPool.Tag.InvokeDynamicInfo, this.bootstrapMethodAttributeIndex, this.nameAndTypeDescriptorIndex);
     }
     
     public ConstantPool.Tag getTag() {
       return ConstantPool.Tag.InvokeDynamicInfo;
     }
     
     public int byteLength() {
       return 5;
     }
     
     public ConstantPool.NameAndTypeDescriptorEntry getNameAndTypeDescriptor() {
       return (ConstantPool.NameAndTypeDescriptorEntry)this.owner.get(this.nameAndTypeDescriptorIndex, ConstantPool.Tag.NameAndTypeDescriptor);
     }
     
     public void accept(ConstantPool.Visitor visitor) {
       visitor.visitInvokeDynamicInfo(this);
     }
     
     public String toString()
     {
       return "InvokeDynamicInfoEntry[bootstrapMethodAttributeIndex: " + this.bootstrapMethodAttributeIndex + ", nameAndTypeDescriptorIndex: " + this.nameAndTypeDescriptorIndex + "]";
     }
   }
   
 
   public static final class DoubleConstantEntry
     extends ConstantPool.ConstantEntry
   {
     public final double value;
     
     public DoubleConstantEntry(ConstantPool owner, double value)
     {
       super();
       this.value = value;
       owner._newKey.set(value);
       owner._entryMap.put(owner._newKey.clone(), this);
       owner._newKey.clear();
     }
     
     void fixupKey(ConstantPool.Key key)
     {
       key.set(this.value);
     }
     
     public ConstantPool.Tag getTag()
     {
       return ConstantPool.Tag.DoubleConstant;
     }
     
     public int size()
     {
       return 2;
     }
     
     public int byteLength()
     {
       return 9;
     }
     
     public void accept(ConstantPool.Visitor visitor)
     {
       visitor.visitDoubleConstant(this);
     }
     
     public String toString()
     {
       return "DoubleConstantEntry[index: " + this.index + ", value: " + this.value + "]";
     }
     
     public Object getConstantValue()
     {
       return Double.valueOf(this.value);
     }
   }
   
   public static final class FloatConstantEntry extends ConstantPool.ConstantEntry {
     public final float value;
     
     public FloatConstantEntry(ConstantPool owner, float value) {
       super();
       this.value = value;
       owner._newKey.set(value);
       owner._entryMap.put(owner._newKey.clone(), this);
       owner._newKey.clear();
     }
     
     void fixupKey(ConstantPool.Key key)
     {
       key.set(this.value);
     }
     
     public ConstantPool.Tag getTag()
     {
       return ConstantPool.Tag.FloatConstant;
     }
     
     public int byteLength()
     {
       return 5;
     }
     
     public void accept(ConstantPool.Visitor visitor)
     {
       visitor.visitFloatConstant(this);
     }
     
     public String toString()
     {
       return "FloatConstantEntry[index: " + this.index + ", value: " + this.value + "]";
     }
     
     public Object getConstantValue()
     {
       return Float.valueOf(this.value);
     }
   }
   
   public static final class IntegerConstantEntry extends ConstantPool.ConstantEntry {
     public final int value;
     
     public IntegerConstantEntry(ConstantPool owner, int value) {
       super();
       this.value = value;
       owner._newKey.set(value);
       owner._entryMap.put(owner._newKey.clone(), this);
       owner._newKey.clear();
     }
     
     void fixupKey(ConstantPool.Key key)
     {
       key.set(this.value);
     }
     
     public ConstantPool.Tag getTag()
     {
       return ConstantPool.Tag.IntegerConstant;
     }
     
     public int byteLength()
     {
       return 5;
     }
     
     public void accept(ConstantPool.Visitor visitor)
     {
       visitor.visitIntegerConstant(this);
     }
     
     public String toString()
     {
       return "IntegerConstantEntry[index: " + this.index + ", value: " + this.value + "]";
     }
     
     public Object getConstantValue()
     {
       return Integer.valueOf(this.value);
     }
   }
   
   public static final class LongConstantEntry extends ConstantPool.ConstantEntry {
     public final long value;
     
     public LongConstantEntry(ConstantPool owner, long value) {
       super();
       this.value = value;
       owner._newKey.set(value);
       owner._entryMap.put(owner._newKey.clone(), this);
       owner._newKey.clear();
     }
     
     void fixupKey(ConstantPool.Key key)
     {
       key.set(this.value);
     }
     
     public ConstantPool.Tag getTag()
     {
       return ConstantPool.Tag.LongConstant;
     }
     
     public int byteLength()
     {
       return 9;
     }
     
     public int size()
     {
       return 2;
     }
     
     public void accept(ConstantPool.Visitor visitor)
     {
       visitor.visitLongConstant(this);
     }
     
     public String toString()
     {
       return "LongConstantEntry[index: " + this.index + ", value: " + this.value + "]";
     }
     
     public Object getConstantValue()
     {
       return Long.valueOf(this.value);
     }
   }
   
   public static final class StringConstantEntry extends ConstantPool.ConstantEntry {
     public final int stringIndex;
     
     public StringConstantEntry(ConstantPool owner, int stringIndex) {
       super();
       this.stringIndex = stringIndex;
       owner._newKey.set(getTag(), stringIndex);
       owner._entryMap.put(owner._newKey.clone(), this);
       owner._newKey.clear();
     }
     
     public String getValue() {
       return ((ConstantPool.Utf8StringConstantEntry)this.owner.get(this.stringIndex)).value;
     }
     
     void fixupKey(ConstantPool.Key key)
     {
       key.set(ConstantPool.Tag.StringConstant, this.stringIndex);
     }
     
     public ConstantPool.Tag getTag()
     {
       return ConstantPool.Tag.StringConstant;
     }
     
     public int byteLength()
     {
       return 3;
     }
     
     public void accept(ConstantPool.Visitor visitor)
     {
       visitor.visitStringConstant(this);
     }
     
     public String toString()
     {
       return "StringConstantEntry[index: " + this.index + ", stringIndex: " + this.stringIndex + "]";
     }
     
     public Object getConstantValue()
     {
       return getValue();
     }
   }
   
   public static final class Utf8StringConstantEntry extends ConstantPool.ConstantEntry {
     public final String value;
     
     public Utf8StringConstantEntry(ConstantPool owner, String value) {
       super();
       this.value = value;
       owner._newKey.set(getTag(), value);
       owner._entryMap.put(owner._newKey.clone(), this);
       owner._newKey.clear();
     }
     
     void fixupKey(ConstantPool.Key key)
     {
       key.set(this.value);
     }
     
     public ConstantPool.Tag getTag()
     {
       return ConstantPool.Tag.Utf8StringConstant;
     }
     
 
 
 
 
 
 
 
 
 
     public int byteLength()
     {
       OutputStream sizeOut = new OutputStream()
       {
         private int size;
         
         public void write(int b)
         {
           this.size += 1;
 
         }
         
 
 
       };
       DataOutputStream out = new DataOutputStream(sizeOut);
       try
       {
         out.writeUTF(this.value);
       }
       catch (IOException ignore) {}
       
 
       return 1 + sizeOut.size;
     }
     
     public void accept(ConstantPool.Visitor visitor)
     {
       visitor.visitUtf8StringConstant(this);
     }
     
     public String toString()
     {
       return "Utf8StringConstantEntry[index: " + this.index + ", value: " + this.value + "]";
     }
     
     public Object getConstantValue()
     {
       return this.value;
     }
   }
   
 
   private static final class Key
   {
     private ConstantPool.Tag _tag;
     
     private int _intValue;
     
     private long _longValue;
     private String _stringValue1;
     private String _stringValue2;
     private int _refIndex1 = -1;
     private int _refIndex2 = -1;
     private int _hashCode;
     
     public void clear() {
       this._tag = null;
       this._intValue = 0;
       this._longValue = 0L;
       this._stringValue1 = null;
       this._stringValue2 = null;
       this._refIndex1 = -1;
       this._refIndex2 = -1;
     }
     
     public void set(int intValue) {
       this._tag = ConstantPool.Tag.IntegerConstant;
       this._intValue = intValue;
       this._hashCode = (0x7FFFFFFF & this._tag.value + this._intValue);
     }
     
     public void set(long longValue) {
       this._tag = ConstantPool.Tag.LongConstant;
       this._longValue = longValue;
       this._hashCode = (0x7FFFFFFF & this._tag.value + (int)longValue);
     }
     
     public void set(float floatValue) {
       this._tag = ConstantPool.Tag.FloatConstant;
       this._intValue = Float.floatToIntBits(floatValue);
       this._hashCode = (0x7FFFFFFF & this._tag.value + this._intValue);
     }
     
     public void set(double doubleValue) {
       this._tag = ConstantPool.Tag.DoubleConstant;
       this._longValue = Double.doubleToLongBits(doubleValue);
       this._hashCode = (0x7FFFFFFF & this._tag.value + (int)this._longValue);
     }
     
     public void set(String utf8Value) {
       this._tag = ConstantPool.Tag.Utf8StringConstant;
       this._stringValue1 = utf8Value;
       this._hashCode = HashUtilities.combineHashCodes(this._tag, utf8Value);
     }
     
 
 
 
     public void set(ConstantPool.Tag tag, int refIndex1, ConstantPool.ReferenceKind refKind)
     {
       this._tag = tag;
       this._refIndex1 = refIndex1;
       this._refIndex2 = refKind.tag;
       this._hashCode = HashUtilities.combineHashCodes(tag, Integer.valueOf(refIndex1));
     }
     
 
 
     public void set(ConstantPool.Tag tag, int refIndex1)
     {
       this._tag = tag;
       this._refIndex1 = refIndex1;
       this._hashCode = HashUtilities.combineHashCodes(tag, Integer.valueOf(refIndex1));
     }
     
 
 
 
     public void set(ConstantPool.Tag tag, int refIndex1, int refIndex2)
     {
       this._tag = tag;
       this._refIndex1 = refIndex1;
       this._refIndex2 = refIndex2;
       this._hashCode = HashUtilities.combineHashCodes(tag, Integer.valueOf(refIndex1), Integer.valueOf(refIndex2));
     }
     
 
 
     public void set(ConstantPool.Tag tag, String stringValue1)
     {
       this._tag = tag;
       this._stringValue1 = stringValue1;
       this._hashCode = HashUtilities.combineHashCodes(tag, stringValue1);
     }
     
 
 
 
     public void set(ConstantPool.Tag tag, String stringValue1, String stringValue2)
     {
       this._tag = tag;
       this._stringValue1 = stringValue1;
       this._stringValue2 = stringValue2;
       this._hashCode = HashUtilities.combineHashCodes(tag, stringValue1, stringValue2);
     }
     
 
     protected Key clone()
     {
       Key key = new Key();
       key._tag = this._tag;
       key._hashCode = this._hashCode;
       key._intValue = this._intValue;
       key._longValue = this._longValue;
       key._stringValue1 = this._stringValue1;
       key._stringValue2 = this._stringValue2;
       key._refIndex1 = this._refIndex1;
       key._refIndex2 = this._refIndex2;
       return key;
     }
     
     public int hashCode()
     {
       return this._hashCode;
     }
     
     public boolean equals(Object obj)
     {
       if (!(obj instanceof Key)) {
         return false;
       }
       
       Key key = (Key)obj;
       if (key._tag != this._tag) {
         return false;
       }
       
       switch (ConstantPool.1.$SwitchMap$com$strobel$assembler$ir$ConstantPool$Tag[this._tag.ordinal()]) {
       case 1: 
         return StringUtilities.equals(key._stringValue1, this._stringValue1);
       
       case 2: 
       case 3: 
         return key._intValue == this._intValue;
       
       case 4: 
       case 5: 
         return key._longValue == this._longValue;
       
       case 6: 
       case 7: 
       case 13: 
         return key._refIndex1 == this._refIndex1;
       
       case 8: 
       case 9: 
       case 10: 
       case 11: 
       case 12: 
       case 14: 
         return (key._refIndex1 == this._refIndex1) && (key._refIndex2 == this._refIndex2);
       }
       
       
       return false;
     }
   }
 }


