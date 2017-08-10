 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ArraySpecifier
   extends AstNode
 {
   public final JavaTokenNode getLeftBracketToken()
   {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_BRACKET);
   }
   
   public final JavaTokenNode getRightBracketToken() {
     return (JavaTokenNode)getChildByRole(Roles.RIGHT_BRACKET);
   }
   
   public NodeType getNodeType()
   {
     return NodeType.UNKNOWN;
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitArraySpecifier(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return other instanceof ArraySpecifier;
   }
   
   public String toString()
   {
     return "[]";
   }
 }


