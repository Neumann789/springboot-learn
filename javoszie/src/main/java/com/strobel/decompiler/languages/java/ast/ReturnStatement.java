 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ReturnStatement
   extends Statement
 {
   public static final TokenRole RETURN_KEYWORD_ROLE = new TokenRole("return", 1);
   
   public ReturnStatement(int offset) {
     super(offset);
   }
   
   public ReturnStatement(int offset, Expression returnValue) {
     super(offset);
     setExpression(returnValue);
   }
   
   public final JavaTokenNode getReturnToken() {
     return (JavaTokenNode)getChildByRole(RETURN_KEYWORD_ROLE);
   }
   
   public final Expression getExpression() {
     return (Expression)getChildByRole(Roles.EXPRESSION);
   }
   
   public final void setExpression(Expression value) {
     setChildByRole(Roles.EXPRESSION, value);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitReturnStatement(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof ReturnStatement)) && (!other.isNull()) && (getExpression().matches(((ReturnStatement)other).getExpression(), match));
   }
 }


