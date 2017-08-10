 package com.strobel.decompiler.languages.java;
 
 import com.strobel.assembler.ir.attributes.LineNumberTableAttribute;
 import com.strobel.assembler.ir.attributes.LineNumberTableEntry;
 import com.strobel.core.VerifyArgument;
 import java.util.Arrays;
 
 
 
 public class LineNumberTableConverter
   implements OffsetToLineNumberConverter
 {
   private final int[] _offset2LineNo;
   private final int _maxOffset;
   
   public LineNumberTableConverter(LineNumberTableAttribute lineNumberTable)
   {
     VerifyArgument.notNull(lineNumberTable, "lineNumberTable");
     
     this._maxOffset = lineNumberTable.getMaxOffset();
     this._offset2LineNo = new int[this._maxOffset + 1];
     
     Arrays.fill(this._offset2LineNo, -100);
     
     for (LineNumberTableEntry entry : lineNumberTable.getEntries()) {
       this._offset2LineNo[entry.getOffset()] = entry.getLineNumber();
     }
     
 
 
 
 
 
 
     int lastLine = this._offset2LineNo[0];
     
     for (int i = 1; i < this._maxOffset + 1; i++) {
       int thisLine = this._offset2LineNo[i];
       
       if (thisLine == -100) {
         this._offset2LineNo[i] = lastLine;
       }
       else {
         lastLine = thisLine;
       }
     }
   }
   
   public int getLineForOffset(int offset)
   {
     VerifyArgument.isNonNegative(offset, "offset");
     assert (offset >= 0) : ("offset must be >= 0; received an offset of " + offset);
     
 
 
 
 
 
 
     if (offset > this._maxOffset) {
       offset = this._maxOffset;
     }
     
     return this._offset2LineNo[offset];
   }
 }


