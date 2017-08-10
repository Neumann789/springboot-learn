 package com.strobel.decompiler.languages;
 
 import com.strobel.core.StringUtilities;
 import java.io.Serializable;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Region
   implements Serializable
 {
   private static final long serialVersionUID = -7580225960304530502L;
   public static final Region EMPTY = new Region(0, 0, 0, 0);
   private final String _fileName;
   private final int _beginLine;
   private final int _endLine;
   private final int _beginColumn;
   private final int _endColumn;
   
   public Region(TextLocation begin, TextLocation end)
   {
     this(null, begin, end);
   }
   
   public Region(String fileName, TextLocation begin, TextLocation end) {
     this(fileName, begin != null ? begin.line() : TextLocation.EMPTY.line(), end != null ? end.line() : TextLocation.EMPTY.line(), begin != null ? begin.column() : TextLocation.EMPTY.column(), end != null ? end.column() : TextLocation.EMPTY.column());
   }
   
 
 
 
 
 
   public Region(int beginLine, int endLine, int beginColumn, int endColumn)
   {
     this(null, beginLine, endLine, beginColumn, endColumn);
   }
   
   public Region(String fileName, int beginLine, int endLine, int beginColumn, int endColumn) {
     this._fileName = fileName;
     this._beginLine = beginLine;
     this._endLine = endLine;
     this._beginColumn = beginColumn;
     this._endColumn = endColumn;
   }
   
   public final String getFileName() {
     return this._fileName;
   }
   
   public final int getBeginLine() {
     return this._beginLine;
   }
   
 
 
   public final int getEndLine()
   {
     return this._endLine;
   }
   
   public final int getBeginColumn() {
     return this._beginColumn;
   }
   
 
 
   public final int getEndColumn()
   {
     return this._endColumn;
   }
   
   public final boolean isEmpty() {
     return this._beginColumn <= 0;
   }
   
   public final boolean isInside(int line, int column) {
     if (isEmpty()) {
       return false;
     }
     
     return (line >= this._beginLine) && ((line <= this._endLine) || (this._endLine == -1)) && ((line != this._beginLine) || (column >= this._beginColumn)) && ((line != this._endLine) || (column <= this._endColumn));
   }
   
 
 
   public final boolean IsInside(TextLocation location)
   {
     return isInside(location != null ? location.line() : TextLocation.EMPTY.line(), location != null ? location.column() : TextLocation.EMPTY.column());
   }
   
 
 
 
   public final int hashCode()
   {
     return (this._fileName != null ? this._fileName.hashCode() : 0) ^ this._beginColumn + 1100009 * this._beginLine + 1200007 * this._endLine + 1300021 * this._endColumn;
   }
   
 
   public final boolean equals(Object obj)
   {
     if ((obj instanceof Region)) {
       Region other = (Region)obj;
       
       return (other._beginLine == this._beginLine) && (other._beginColumn == this._beginColumn) && (other._endLine == this._endLine) && (other._endColumn == this._endColumn) && (StringUtilities.equals(other._fileName, this._fileName));
     }
     
 
 
 
 
     return false;
   }
   
   public final String toString() {
     return String.format("[Region FileName=%s, Begin=(%d, %d), End=(%d, %d)]", new Object[] { this._fileName, Integer.valueOf(this._beginLine), Integer.valueOf(this._beginColumn), Integer.valueOf(this._endLine), Integer.valueOf(this._endColumn) });
   }
 }


