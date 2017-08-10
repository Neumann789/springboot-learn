 package com.strobel.assembler.ir.attributes;
 
 import com.strobel.core.ArrayUtilities;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class LocalVariableTableAttribute
   extends SourceAttribute
 {
   private final List<LocalVariableTableEntry> _entries;
   
   public LocalVariableTableAttribute(String name, LocalVariableTableEntry[] entries)
   {
     super(name, 2 + entries.length * 10);
     this._entries = ArrayUtilities.asUnmodifiableList((Object[])entries.clone());
   }
   
   public List<LocalVariableTableEntry> getEntries() {
     return this._entries;
   }
 }


