 package com.strobel.assembler.ir.attributes;
 
 import com.strobel.core.VerifyArgument;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class InnerClassesAttribute
   extends SourceAttribute
 {
   private final List<InnerClassEntry> _entries;
   
   public InnerClassesAttribute(int length, List<InnerClassEntry> entries)
   {
     super("InnerClasses", length);
     this._entries = ((List)VerifyArgument.notNull(entries, "entries"));
   }
   
   public List<InnerClassEntry> getEntries() {
     return this._entries;
   }
 }


