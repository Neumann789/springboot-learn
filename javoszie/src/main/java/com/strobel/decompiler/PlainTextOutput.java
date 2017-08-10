 package com.strobel.decompiler;
 
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import java.io.IOException;
 import java.io.StringWriter;
 import java.io.Writer;
 import java.lang.reflect.UndeclaredThrowableException;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class PlainTextOutput
   implements ITextOutput
 {
   private static final String NULL_TEXT = String.valueOf((Object)null);
   
   private final Writer _writer;
   private String _indentToken = "    ";
   
   private int _indent;
   private boolean _needsIndent;
   private boolean _isUnicodeOutputEnabled;
   protected int line = 1;
   protected int column = 1;
   
   public PlainTextOutput() {
     this._writer = new StringWriter();
   }
   
   public PlainTextOutput(Writer writer) {
     this._writer = ((Writer)VerifyArgument.notNull(writer, "writer"));
   }
   
   public final String getIndentToken()
   {
     String indentToken = this._indentToken;
     return indentToken != null ? indentToken : "";
   }
   
   public final void setIndentToken(String indentToken)
   {
     this._indentToken = indentToken;
   }
   
   public final boolean isUnicodeOutputEnabled() {
     return this._isUnicodeOutputEnabled;
   }
   
   public final void setUnicodeOutputEnabled(boolean unicodeOutputEnabled) {
     this._isUnicodeOutputEnabled = unicodeOutputEnabled;
   }
   
   protected void writeIndent() {
     if (this._needsIndent) {
       this._needsIndent = false;
       
       String indentToken = getIndentToken();
       
       for (int i = 0; i < this._indent; i++) {
         try {
           this._writer.write(indentToken);
         }
         catch (IOException e) {
           throw new UndeclaredThrowableException(e);
         }
       }
       
       this.column += indentToken.length() * this._indent;
     }
   }
   
   public int getRow()
   {
     return this.line;
   }
   
   public int getColumn()
   {
     return this._needsIndent ? this.column + this._indent * getIndentToken().length() : this.column;
   }
   
   public void indent()
   {
     this._indent += 1;
   }
   
   public void unindent()
   {
     this._indent -= 1;
   }
   
   public void write(char ch)
   {
     writeIndent();
     try {
       if (isUnicodeOutputEnabled()) {
         this._writer.write(ch);
       }
       else {
         this._writer.write(StringUtilities.escape(ch));
       }
       this.column += 1;
     }
     catch (IOException e) {
       throw new UndeclaredThrowableException(e);
     }
   }
   
   public void write(String text)
   {
     writeRaw(isUnicodeOutputEnabled() ? text : StringUtilities.escape(text));
   }
   
 
 
 
 
 
   protected void writeRaw(String text)
   {
     writeIndent();
     try
     {
       int length = text != null ? text.length() : NULL_TEXT.length();
       
       this._writer.write(text);
       
       this.column += length;
       
       if (text == null) {
         return;
       }
       
       boolean newLineSeen = false;
       
       for (int i = 0; i < length; i++) {
         if (text.charAt(i) == '\n') {
           this.line += 1;
           this.column = 0;
           newLineSeen = true;
         }
         else if (newLineSeen) {
           this.column += 1;
         }
       }
     }
     catch (IOException e) {
       throw new UndeclaredThrowableException(e);
     }
   }
   
   public void writeError(String value)
   {
     write(value);
   }
   
   public void writeLabel(String value)
   {
     write(value);
   }
   
   public void writeLiteral(Object value)
   {
     write(String.valueOf(value));
   }
   
   public void writeTextLiteral(Object value)
   {
     write(String.valueOf(value));
   }
   
   public void writeComment(String value)
   {
     write(value);
   }
   
   public void writeComment(String format, Object... args)
   {
     write(format, args);
   }
   
   public void write(String format, Object... args)
   {
     write(String.format(format, args));
   }
   
   public void writeLine(String text)
   {
     write(text);
     writeLine();
   }
   
   public void writeLine(String format, Object... args)
   {
     write(String.format(format, args));
     writeLine();
   }
   
   public void writeLine()
   {
     writeIndent();
     try {
       this._writer.write("\n");
     }
     catch (IOException e) {
       throw new UndeclaredThrowableException(e);
     }
     this._needsIndent = true;
     this.line += 1;
     this.column = 1;
   }
   
   public void writeDelimiter(String text)
   {
     write(text);
   }
   
   public void writeOperator(String text)
   {
     write(text);
   }
   
   public void writeKeyword(String text)
   {
     write(text);
   }
   
   public void writeAttribute(String text)
   {
     write(text);
   }
   
   public void writeDefinition(String text, Object definition)
   {
     writeDefinition(text, definition, true);
   }
   
   public void writeDefinition(String text, Object definition, boolean isLocal)
   {
     write(text);
   }
   
   public void writeReference(String text, Object reference)
   {
     writeReference(text, reference, false);
   }
   
   public void writeReference(String text, Object reference, boolean isLocal)
   {
     write(text);
   }
   
   public boolean isFoldingSupported()
   {
     return false;
   }
   
 
 
   public void markFoldStart(String collapsedText, boolean defaultCollapsed) {}
   
 
   public void markFoldEnd() {}
   
 
   public String toString()
   {
     return this._writer.toString();
   }
 }


