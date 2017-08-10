 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.Environment;
 import com.strobel.decompiler.languages.TextLocation;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class NewLineNode
   extends AstNode
 {
   private final TextLocation _startLocation;
   private final TextLocation _endLocation;
   
   protected NewLineNode()
   {
     this(TextLocation.EMPTY);
   }
   
   protected NewLineNode(TextLocation startLocation) {
     this._startLocation = (startLocation != null ? startLocation : TextLocation.EMPTY);
     this._endLocation = new TextLocation(this._startLocation.line() + 1, 1);
   }
   
   public abstract NewLineType getNewLineType();
   
   public TextLocation getStartLocation()
   {
     return this._startLocation;
   }
   
   public TextLocation getEndLocation()
   {
     return this._endLocation;
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitNewLine(this, data);
   }
   
   public NodeType getNodeType()
   {
     return NodeType.WHITESPACE;
   }
   
   public static NewLineNode create() {
     if ((Environment.isWindows()) || (Environment.isOS2())) {
       return new WindowsNewLine();
     }
     if (Environment.isMac()) {
       return new MacNewLine();
     }
     return new UnixNewLine();
   }
 }


