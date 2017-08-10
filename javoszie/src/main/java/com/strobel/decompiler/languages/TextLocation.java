 package com.strobel.decompiler.languages;
 
 import java.io.Serializable;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class TextLocation
   implements Comparable<TextLocation>, Serializable
 {
   private static final long serialVersionUID = -165593440170614692L;
   public static final int MIN_LINE = 1;
   public static final int MIN_COLUMN = 1;
   public static final TextLocation EMPTY = new TextLocation();
   private final int _line;
   private final int _column;
   
   private TextLocation()
   {
     this._line = 0;
     this._column = 0;
   }
   
   public TextLocation(int line, int column) {
     this._line = line;
     this._column = column;
   }
   
   public final int line() {
     return this._line;
   }
   
   public final int column() {
     return this._column;
   }
   
   public final boolean isEmpty() {
     return (this._line < 1) && (this._column < 1);
   }
   
   public final boolean isBefore(TextLocation other)
   {
     if ((other == null) || (other.isEmpty())) {
       return false;
     }
     
     return (this._line < other._line) || ((this._line == other._line) && (this._column < other._column));
   }
   
   public final boolean isAfter(TextLocation other)
   {
     return (other == null) || (other.isEmpty()) || (this._line > other._line) || ((this._line == other._line) && (this._column > other._column));
   }
   
 
 
 
   public final String toString()
   {
     return String.format("(Line %d, Column %d)", new Object[] { Integer.valueOf(this._line), Integer.valueOf(this._column) });
   }
   
   public int hashCode()
   {
     return super.hashCode();
   }
   
   public boolean equals(Object obj)
   {
     return super.equals(obj);
   }
   
   public final int compareTo(TextLocation o)
   {
     if (isBefore(o)) {
       return -1;
     }
     
     if (isAfter(o)) {
       return 1;
     }
     
     return 0;
   }
 }


