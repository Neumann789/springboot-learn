 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ParenthesizedExpression
   extends Expression
 {
   public ParenthesizedExpression(Expression expression)
   {
     super(expression.getOffset());
     setExpression(expression);
   }
   
   public final JavaTokenNode getLeftParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_PARENTHESIS);
   }
   
   public final JavaTokenNode getRightParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.RIGHT_PARENTHESIS);
   }
   
   public final Expression getExpression() { return (Expression)getChildByRole(Roles.EXPRESSION); }
   
   public final void setExpression(Expression value)
   {
     setChildByRole(Roles.EXPRESSION, value);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitParenthesizedExpression(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof ParenthesizedExpression)) && (getExpression().matches(((ParenthesizedExpression)other).getExpression(), match));
   }
 }


