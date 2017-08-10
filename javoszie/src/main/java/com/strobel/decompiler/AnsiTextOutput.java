 package com.strobel.decompiler;
 
 import com.strobel.assembler.ir.Instruction;
 import com.strobel.assembler.ir.OpCode;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.IMethodSignature;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.PackageReference;
 import com.strobel.assembler.metadata.ParameterReference;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.assembler.metadata.VariableReference;
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.ast.AstCode;
 import com.strobel.decompiler.ast.Variable;
 import com.strobel.io.Ansi;
 import com.strobel.io.Ansi.AnsiColor;
 import com.strobel.io.Ansi.Attribute;
 import java.io.StringWriter;
 import java.io.Writer;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class AnsiTextOutput
   extends PlainTextOutput
 {
   private final Ansi _keyword;
   private final Ansi _instruction;
   private final Ansi _label;
   private final Ansi _type;
   private final Ansi _typeVariable;
   private final Ansi _package;
   private final Ansi _method;
   private final Ansi _field;
   private final Ansi _local;
   private final Ansi _literal;
   private final Ansi _textLiteral;
   private final Ansi _comment;
   private final Ansi _operator;
   private final Ansi _delimiter;
   private final Ansi _attribute;
   private final Ansi _error;
   
   public AnsiTextOutput()
   {
     this(new StringWriter(), ColorScheme.DARK);
   }
   
   public AnsiTextOutput(ColorScheme colorScheme) {
     this(new StringWriter(), colorScheme);
   }
   
   public AnsiTextOutput(Writer writer) {
     this(writer, ColorScheme.DARK);
   }
   
   public AnsiTextOutput(Writer writer, ColorScheme colorScheme) {
     super(writer);
     
     boolean light = colorScheme == ColorScheme.LIGHT;
     
     this._keyword = new Ansi(Ansi.Attribute.NORMAL, new Ansi.AnsiColor(light ? 21 : 33), null);
     this._instruction = new Ansi(Ansi.Attribute.NORMAL, new Ansi.AnsiColor(light ? 91 : 141), null);
     this._label = new Ansi(Ansi.Attribute.NORMAL, new Ansi.AnsiColor(light ? 249 : 249), null);
     this._type = new Ansi(Ansi.Attribute.NORMAL, new Ansi.AnsiColor(light ? 25 : 45), null);
     this._typeVariable = new Ansi(Ansi.Attribute.NORMAL, new Ansi.AnsiColor(light ? 29 : 79), null);
     this._package = new Ansi(Ansi.Attribute.NORMAL, new Ansi.AnsiColor(light ? 32 : 111), null);
     this._method = new Ansi(Ansi.Attribute.NORMAL, new Ansi.AnsiColor(light ? 162 : 212), null);
     this._field = new Ansi(Ansi.Attribute.NORMAL, new Ansi.AnsiColor(light ? 136 : 222), null);
     this._local = new Ansi(Ansi.Attribute.NORMAL, (Ansi.AnsiColor)null, null);
     this._literal = new Ansi(Ansi.Attribute.NORMAL, new Ansi.AnsiColor(light ? 197 : 204), null);
     this._textLiteral = new Ansi(Ansi.Attribute.NORMAL, new Ansi.AnsiColor(light ? 28 : 42), null);
     this._comment = new Ansi(Ansi.Attribute.NORMAL, new Ansi.AnsiColor(light ? 244 : 244), null);
     this._operator = new Ansi(Ansi.Attribute.NORMAL, new Ansi.AnsiColor(light ? 242 : 247), null);
     this._delimiter = new Ansi(Ansi.Attribute.NORMAL, new Ansi.AnsiColor(light ? 242 : 252), null);
     this._attribute = new Ansi(Ansi.Attribute.NORMAL, new Ansi.AnsiColor(light ? 166 : 214), null);
     this._error = new Ansi(Ansi.Attribute.NORMAL, new Ansi.AnsiColor(light ? 196 : 196), null);
   }
   
   private String colorize(String value, Ansi ansi) {
     return ansi.colorize(StringUtilities.escape(value, false, isUnicodeOutputEnabled()));
   }
   
   public void writeError(String value)
   {
     writeAnsi(value, colorize(value, this._error));
   }
   
   public void writeLabel(String value)
   {
     writeAnsi(value, colorize(value, this._label));
   }
   
   protected final void writeAnsi(String originalText, String ansiText) {
     super.writeRaw(ansiText);
     
     if ((originalText != null) && (ansiText != null)) {
       this.column -= ansiText.length() - originalText.length();
     }
   }
   
   public void writeLiteral(Object value)
   {
     String literal = String.valueOf(value);
     writeAnsi(literal, colorize(literal, this._literal));
   }
   
   public void writeTextLiteral(Object value)
   {
     String literal = String.valueOf(value);
     writeAnsi(literal, colorize(literal, this._textLiteral));
   }
   
   public void writeComment(String value)
   {
     writeAnsi(value, colorize(value, this._comment));
   }
   
   public void writeComment(String format, Object... args)
   {
     String text = String.format(format, args);
     writeAnsi(text, colorize(text, this._comment));
   }
   
   public void writeDelimiter(String text)
   {
     writeAnsi(text, colorize(text, this._delimiter));
   }
   
   public void writeAttribute(String text)
   {
     writeAnsi(text, colorize(text, this._attribute));
   }
   
   public void writeOperator(String text)
   {
     writeAnsi(text, colorize(text, this._operator));
   }
   
   public void writeKeyword(String text)
   {
     writeAnsi(text, colorize(text, this._keyword));
   }
   
   public void writeDefinition(String text, Object definition, boolean isLocal)
   {
     if (text == null) {
       super.write(text); return;
     }
     
     String colorizedText;
     
     String colorizedText;
     if (((definition instanceof Instruction)) || ((definition instanceof OpCode)) || ((definition instanceof AstCode)))
     {
 
 
       colorizedText = colorize(text, this._instruction);
     } else { String colorizedText;
       if ((definition instanceof TypeReference)) {
         colorizedText = colorizeType(text, (TypeReference)definition);
       } else { String colorizedText;
         if (((definition instanceof MethodReference)) || ((definition instanceof IMethodSignature)))
         {
           colorizedText = colorize(text, this._method);
         } else { String colorizedText;
           if ((definition instanceof FieldReference)) {
             colorizedText = colorize(text, this._field);
           } else { String colorizedText;
             if (((definition instanceof VariableReference)) || ((definition instanceof ParameterReference)) || ((definition instanceof Variable)))
             {
 
 
               colorizedText = colorize(text, this._local);
             } else { String colorizedText;
               if ((definition instanceof PackageReference)) {
                 colorizedText = colorizePackage(text);
               } else { String colorizedText;
                 if (((definition instanceof com.strobel.assembler.metadata.Label)) || ((definition instanceof com.strobel.decompiler.ast.Label)))
                 {
 
                   colorizedText = colorize(text, this._label);
                 }
                 else
                   colorizedText = text;
               }
             } } } } }
     writeAnsi(text, colorizedText);
   }
   
   public void writeReference(String text, Object reference, boolean isLocal)
   {
     if (text == null) {
       super.write(text); return;
     }
     
     String colorizedText;
     
     String colorizedText;
     if (((reference instanceof Instruction)) || ((reference instanceof OpCode)) || ((reference instanceof AstCode)))
     {
 
 
       colorizedText = colorize(text, this._instruction);
     } else { String colorizedText;
       if ((reference instanceof TypeReference)) {
         colorizedText = colorizeType(text, (TypeReference)reference);
       } else { String colorizedText;
         if (((reference instanceof MethodReference)) || ((reference instanceof IMethodSignature)))
         {
           colorizedText = colorize(text, this._method);
         } else { String colorizedText;
           if ((reference instanceof FieldReference)) {
             colorizedText = colorize(text, this._field);
           } else { String colorizedText;
             if (((reference instanceof VariableReference)) || ((reference instanceof ParameterReference)) || ((reference instanceof Variable)))
             {
 
 
               colorizedText = colorize(text, this._local);
             } else { String colorizedText;
               if ((reference instanceof PackageReference)) {
                 colorizedText = colorizePackage(text);
               } else { String colorizedText;
                 if (((reference instanceof com.strobel.assembler.metadata.Label)) || ((reference instanceof com.strobel.decompiler.ast.Label)))
                 {
 
                   colorizedText = colorize(text, this._label);
                 }
                 else
                   colorizedText = StringUtilities.escape(text, false, isUnicodeOutputEnabled());
               }
             } } } } }
     writeAnsi(text, colorizedText);
   }
   
   private String colorizeType(String text, TypeReference type)
   {
     if (type.isPrimitive()) {
       return colorize(text, this._keyword);
     }
     
     String packageName = type.getPackageName();
     TypeDefinition resolvedType = type.resolve();
     
     Ansi typeColor = type.isGenericParameter() ? this._typeVariable : this._type;
     
     if (StringUtilities.isNullOrEmpty(packageName)) {
       if ((resolvedType != null) && (resolvedType.isAnnotation())) {
         return colorize(text, this._attribute);
       }
       
       return colorize(text, typeColor);
     }
     
 
     String s = text;
     char delimiter = '.';
     String packagePrefix = packageName + delimiter;
     
     int arrayDepth = 0;
     
     while ((arrayDepth < s.length()) && (s.charAt(arrayDepth) == '[')) {
       arrayDepth++;
     }
     
     if (arrayDepth > 0) {
       s = s.substring(arrayDepth);
     }
     
     boolean isTypeVariable = (s.startsWith("T")) && (s.endsWith(";"));
     boolean isSignature = (isTypeVariable) || ((s.startsWith("L")) && (s.endsWith(";")));
     
     if (isSignature) {
       s = s.substring(1, s.length() - 1);
     }
     
     if (!StringUtilities.startsWith(s, packagePrefix)) {
       delimiter = '/';
       packagePrefix = packageName.replace('.', delimiter) + delimiter;
     }
     
 
     StringBuilder sb = new StringBuilder();
     String typeName;
     String typeName; if (StringUtilities.startsWith(s, packagePrefix)) {
       String[] packageParts = packageName.split("\\.");
       
       for (int i = 0; i < arrayDepth; i++) {
         sb.append(colorize("[", this._delimiter));
       }
       
       if (isSignature) {
         sb.append(colorize(isTypeVariable ? "T" : "L", this._delimiter));
       }
       
       for (int i = 0; i < packageParts.length; i++) {
         if (i != 0) {
           sb.append(colorize(String.valueOf(delimiter), this._delimiter));
         }
         
         sb.append(colorize(packageParts[i], this._package));
       }
       
       sb.append(colorize(String.valueOf(delimiter), this._delimiter));
       
       typeName = s.substring(packagePrefix.length());
     }
     else {
       typeName = text;
     }
     
     typeColor = (resolvedType != null) && (resolvedType.isAnnotation()) ? this._attribute : typeColor;
     
     colorizeDelimitedName(sb, typeName, typeColor);
     
     if (isSignature) {
       sb.append(colorize(";", this._delimiter));
     }
     
     return sb.toString();
   }
   
   private StringBuilder colorizeDelimitedName(StringBuilder sb, String typeName, Ansi typeColor) {
     int end = typeName.length();
     
     if (end == 0) {
       return sb;
     }
     
     int start = 0;
     int i = start;
     
     while (i < end) {
       char ch = typeName.charAt(i);
       
       switch (ch) {
       case '$': 
       case '.': 
         sb.append(colorize(typeName.substring(start, i), typeColor));
         sb.append(colorize(ch == '.' ? "." : "$", this._delimiter));
         start = i + 1;
       }
       
       
       i++;
     }
     
     if (start < end) {
       sb.append(colorize(typeName.substring(start, end), typeColor));
     }
     
     return sb;
   }
   
   private String colorizePackage(String text) {
     String[] packageParts = text.split("\\.");
     StringBuilder sb = new StringBuilder(text.length() * 2);
     
     for (int i = 0; i < packageParts.length; i++) {
       if (i != 0) {
         sb.append(colorize(".", this._delimiter));
       }
       
       String packagePart = packageParts[i];
       
       if ("*".equals(packagePart)) {
         sb.append(packagePart);
       }
       else {
         sb.append(colorize(packagePart, this._package));
       }
     }
     
     return sb.toString();
   }
   
   public static enum ColorScheme {
     DARK, 
     LIGHT;
     
     private ColorScheme() {}
   }
   
   private static final class Delimiters
   {
     static final String L = "L";
     static final String T = "T";
     static final String DOLLAR = "$";
     static final String DOT = ".";
     static final String SLASH = "/";
     static final String LEFT_BRACKET = "[";
     static final String SEMICOLON = ";";
   }
 }


