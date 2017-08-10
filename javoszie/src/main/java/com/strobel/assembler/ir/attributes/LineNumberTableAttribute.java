 package com.strobel.assembler.ir.attributes;
 
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.VerifyArgument;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class LineNumberTableAttribute
   extends SourceAttribute
 {
   private final List<LineNumberTableEntry> _entries;
   private final int _maxOffset;
   
   public LineNumberTableAttribute(LineNumberTableEntry[] entries)
   {
     super("LineNumberTable", 2 + ((LineNumberTableEntry[])VerifyArgument.notNull(entries, "entries")).length * 4);
     
     this._entries = ArrayUtilities.asUnmodifiableList((Object[])entries.clone());
     
     int max = Integer.MIN_VALUE;
     
     for (LineNumberTableEntry entry : entries) {
       int offset = entry.getOffset();
       
       if (offset > max) {
         max = offset;
       }
     }
     
     this._maxOffset = max;
   }
   
   public List<LineNumberTableEntry> getEntries() {
     return this._entries;
   }
   
 
 
   public int getMaxOffset()
   {
     return this._maxOffset;
   }
 }


