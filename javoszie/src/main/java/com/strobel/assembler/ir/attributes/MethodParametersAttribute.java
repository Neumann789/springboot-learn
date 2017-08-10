 package com.strobel.assembler.ir.attributes;
 
 import com.strobel.core.VerifyArgument;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class MethodParametersAttribute
   extends SourceAttribute
 {
   private final List<MethodParameterEntry> _entries;
   
   public MethodParametersAttribute(List<MethodParameterEntry> entries)
   {
     super("MethodParameters", 1 + entries.size() * 4);
     this._entries = ((List)VerifyArgument.notNull(entries, "entries"));
   }
   
   public List<MethodParameterEntry> getEntries() {
     return this._entries;
   }
 }


