 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class CastExpression
   extends Expression
 {
   public CastExpression(AstType castToType, Expression expression)
   {
     super(expression.getOffset());
     setType(castToType);
     setExpression(expression);
   }
   
   public final AstType getType() {
     return (AstType)getChildByRole(Roles.TYPE);
   }
   
   public final void setType(AstType type) {
     setChildByRole(Roles.TYPE, type);
   }
   
   public final JavaTokenNode getLeftParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_PARENTHESIS);
   }
   
   public final JavaTokenNode getRightParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.RIGHT_PARENTHESIS);
   }
   
   public final Expression getExpression() {
     return (Expression)getChildByRole(Roles.EXPRESSION);
   }
   
   public final void setExpression(Expression value) {
     setChildByRole(Roles.EXPRESSION, value);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitCastExpression(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof CastExpression)) {
       CastExpression otherCast = (CastExpression)other;
       
       return (!otherCast.isNull()) && (getType().matches(otherCast.getType(), match)) && (getExpression().matches(otherCast.getExpression(), match));
     }
     
 
 
     return false;
   }
 }


