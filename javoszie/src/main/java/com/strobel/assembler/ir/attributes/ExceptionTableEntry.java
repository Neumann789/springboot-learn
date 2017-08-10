 package com.strobel.assembler.ir.attributes;
 
 import com.strobel.assembler.metadata.TypeReference;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ExceptionTableEntry
 {
   private final int _startOffset;
   private final int _endOffset;
   private final int _handlerOffset;
   private final TypeReference _catchType;
   
   public ExceptionTableEntry(int startOffset, int endOffset, int handlerOffset, TypeReference catchType)
   {
     this._startOffset = startOffset;
     this._endOffset = endOffset;
     this._handlerOffset = handlerOffset;
     this._catchType = catchType;
   }
   
   public int getStartOffset() {
     return this._startOffset;
   }
   
   public int getEndOffset() {
     return this._endOffset;
   }
   
   public int getHandlerOffset() {
     return this._handlerOffset;
   }
   
   public TypeReference getCatchType() {
     return this._catchType;
   }
   
   public String toString()
   {
     return "Handler{From=" + this._startOffset + ", To=" + this._endOffset + ", Target=" + this._handlerOffset + ", Type=" + this._catchType + '}';
   }
 }


