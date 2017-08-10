 package com.strobel.decompiler.languages;
 
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class LineNumberPosition
 {
   private final int _originalLine;
   private final int _emittedLine;
   private final int _emittedColumn;
   
   public LineNumberPosition(int originalLine, int emittedLine, int emittedColumn)
   {
     this._originalLine = originalLine;
     this._emittedLine = emittedLine;
     this._emittedColumn = emittedColumn;
   }
   
   public int getOriginalLine() {
     return this._originalLine;
   }
   
   public int getEmittedLine() {
     return this._emittedLine;
   }
   
   public int getEmittedColumn() {
     return this._emittedColumn;
   }
   
   public static int computeMaxLineNumber(List<LineNumberPosition> lineNumPositions) {
     int maxLineNo = 1;
     for (LineNumberPosition pos : lineNumPositions) {
       int originalLine = pos.getOriginalLine();
       maxLineNo = Math.max(maxLineNo, originalLine);
     }
     return maxLineNo;
   }
   
   public String toString()
   {
     return "Line # Position : {orig=" + this._originalLine + ", " + "emitted=" + this._emittedLine + "/" + this._emittedColumn + "}";
   }
 }


