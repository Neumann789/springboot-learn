 package com.strobel.assembler.ir.attributes;
 
 import com.strobel.assembler.metadata.Buffer;
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.VerifyArgument;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class CodeAttribute
   extends SourceAttribute
 {
   private final int _maxStack;
   private final int _maxLocals;
   private final int _codeSize;
   private final int _codeOffset;
   private final Buffer _code;
   private final List<ExceptionTableEntry> _exceptionTableEntriesView;
   private final List<SourceAttribute> _attributesView;
   
   public CodeAttribute(int size, int maxStack, int maxLocals, int codeOffset, int codeSize, Buffer buffer, ExceptionTableEntry[] exceptionTableEntries, SourceAttribute[] attributes)
   {
     super("Code", size);
     
     VerifyArgument.notNull(buffer, "buffer");
     VerifyArgument.notNull(exceptionTableEntries, "exceptionTableEntries");
     VerifyArgument.notNull(attributes, "attributes");
     
     this._codeOffset = 0;
     this._maxStack = maxStack;
     this._maxLocals = maxLocals;
     this._codeSize = codeSize;
     
     Buffer code = new Buffer(codeSize);
     
     System.arraycopy(buffer.array(), codeOffset, code.array(), 0, codeSize);
     
 
 
 
 
 
 
     this._code = code;
     this._attributesView = ArrayUtilities.asUnmodifiableList((Object[])attributes.clone());
     this._exceptionTableEntriesView = ArrayUtilities.asUnmodifiableList((Object[])exceptionTableEntries.clone());
   }
   
 
 
 
 
 
 
 
   public CodeAttribute(int size, int codeOffset, int codeSize, int maxStack, int maxLocals, ExceptionTableEntry[] exceptionTableEntries, SourceAttribute[] attributes)
   {
     super("Code", size);
     
     VerifyArgument.notNull(attributes, "attributes");
     VerifyArgument.notNull(exceptionTableEntries, "exceptionTableEntries");
     
     this._maxStack = maxStack;
     this._maxLocals = maxLocals;
     this._codeOffset = codeOffset;
     this._codeSize = codeSize;
     this._code = null;
     this._attributesView = ArrayUtilities.asUnmodifiableList((Object[])attributes.clone());
     this._exceptionTableEntriesView = ArrayUtilities.asUnmodifiableList((Object[])exceptionTableEntries.clone());
   }
   
   public int getMaxStack() {
     return this._maxStack;
   }
   
   public int getMaxLocals() {
     return this._maxLocals;
   }
   
   public int getCodeSize() {
     return this._codeSize;
   }
   
   public boolean hasCode() {
     return this._code != null;
   }
   
   public Buffer getCode() {
     return this._code;
   }
   
   public List<ExceptionTableEntry> getExceptionTableEntries() {
     return this._exceptionTableEntriesView;
   }
   
   public List<SourceAttribute> getAttributes() {
     return this._attributesView;
   }
   
   public int getCodeOffset() {
     return this._codeOffset;
   }
 }


