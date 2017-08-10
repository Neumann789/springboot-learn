 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.Comparer;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Comment
   extends AstNode
 {
   private CommentType _commentType;
   private boolean _startsLine;
   private String _content;
   
   public Comment(String content)
   {
     this(content, CommentType.SingleLine);
   }
   
   public Comment(String content, CommentType commentType) {
     this._commentType = commentType;
     this._content = content;
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitComment(this, data);
   }
   
   public NodeType getNodeType()
   {
     return NodeType.WHITESPACE;
   }
   
   public final CommentType getCommentType() {
     return this._commentType;
   }
   
   public final void setCommentType(CommentType commentType) {
     verifyNotFrozen();
     this._commentType = commentType;
   }
   
   public final boolean getStartsLine() {
     return this._startsLine;
   }
   
   public final void setStartsLine(boolean startsLine) {
     verifyNotFrozen();
     this._startsLine = startsLine;
   }
   
   public final String getContent() {
     return this._content;
   }
   
   public final void setContent(String content) {
     verifyNotFrozen();
     this._content = content;
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof Comment)) {
       Comment otherComment = (Comment)other;
       
       return (otherComment._commentType == this._commentType) && (Comparer.equals(otherComment._content, this._content));
     }
     
 
     return false;
   }
 }


