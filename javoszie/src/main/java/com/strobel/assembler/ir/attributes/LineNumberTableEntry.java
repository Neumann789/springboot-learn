 package com.strobel.assembler.ir.attributes;
 
 
 
 
 
 
 
 public final class LineNumberTableEntry
 {
   private final int _offset;
   
 
 
 
 
 
   private final int _lineNumber;
   
 
 
 
 
 
   public LineNumberTableEntry(int offset, int lineNumber)
   {
     this._offset = offset;
     this._lineNumber = lineNumber;
   }
   
   public int getOffset() {
     return this._offset;
   }
   
   public int getLineNumber() {
     return this._lineNumber;
   }
 }


