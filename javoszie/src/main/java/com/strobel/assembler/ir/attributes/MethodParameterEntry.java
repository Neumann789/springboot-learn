 package com.strobel.assembler.ir.attributes;
 
 import com.strobel.assembler.metadata.Flags;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class MethodParameterEntry
 {
   private final String _name;
   private final int _flags;
   
   public MethodParameterEntry(String name, int flags)
   {
     this._name = name;
     this._flags = flags;
   }
   
   public String getName() {
     return this._name;
   }
   
   public int getFlags() {
     return this._flags;
   }
   
   public String toString()
   {
     return "MethodParameterEntry{name='" + this._name + "'" + ", flags=" + Flags.toString(this._flags) + '}';
   }
 }


