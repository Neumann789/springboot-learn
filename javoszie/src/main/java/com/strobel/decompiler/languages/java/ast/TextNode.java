 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.languages.TextLocation;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class TextNode
   extends AstNode
 {
   private String _text;
   private TextLocation _startLocation;
   private TextLocation _endLocation;
   
   public TextNode() {}
   
   public TextNode(String text)
   {
     this(text, TextLocation.EMPTY, TextLocation.EMPTY);
   }
   
   public TextNode(String text, TextLocation startLocation, TextLocation endLocation) {
     this._text = text;
     this._startLocation = startLocation;
     this._endLocation = endLocation;
   }
   
   public final String getText() {
     return this._text;
   }
   
   public final void setText(String text) {
     verifyNotFrozen();
     this._text = text;
   }
   
   public final TextLocation getStartLocation() {
     return this._startLocation;
   }
   
   public final void setStartLocation(TextLocation startLocation) {
     verifyNotFrozen();
     this._startLocation = startLocation;
   }
   
   public final TextLocation getEndLocation() {
     return this._endLocation;
   }
   
   public final void setEndLocation(TextLocation endLocation) {
     verifyNotFrozen();
     this._endLocation = endLocation;
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitText(this, data);
   }
   
   public NodeType getNodeType()
   {
     return NodeType.WHITESPACE;
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof TextNode)) && (StringUtilities.equals(getText(), ((TextNode)other).getText()));
   }
 }


