 package com.strobel.assembler.ir.attributes;
 
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class LocalVariableTableEntry
 {
   private final int _index;
   private final String _name;
   private final TypeReference _type;
   private final int _scopeOffset;
   private final int _scopeLength;
   
   public LocalVariableTableEntry(int index, String name, TypeReference type, int scopeOffset, int scopeLength)
   {
     this._index = VerifyArgument.isNonNegative(index, "index");
     this._name = ((String)VerifyArgument.notNull(name, "name"));
     this._type = ((TypeReference)VerifyArgument.notNull(type, "type"));
     this._scopeOffset = VerifyArgument.isNonNegative(scopeOffset, "scopeOffset");
     this._scopeLength = VerifyArgument.isNonNegative(scopeLength, "scopeLength");
   }
   
   public int getIndex() {
     return this._index;
   }
   
   public String getName() {
     return this._name;
   }
   
   public TypeReference getType() {
     return this._type;
   }
   
   public int getScopeOffset() {
     return this._scopeOffset;
   }
   
   public int getScopeLength() {
     return this._scopeLength;
   }
   
   public String toString()
   {
     return "LocalVariableTableEntry{Index=" + this._index + ", Name='" + this._name + '\'' + ", Type=" + this._type + ", ScopeOffset=" + this._scopeOffset + ", ScopeLength=" + this._scopeLength + '}';
   }
 }


