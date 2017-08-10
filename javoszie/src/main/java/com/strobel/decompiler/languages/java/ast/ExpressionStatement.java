 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ExpressionStatement
   extends Statement
 {
   public ExpressionStatement(Expression expression)
   {
     super(expression.getOffset());
     setExpression(expression);
   }
   
   public boolean isEmbeddable()
   {
     return true;
   }
   
   public final Expression getExpression() {
     return (Expression)getChildByRole(Roles.EXPRESSION);
   }
   
   public final void setExpression(Expression value) {
     setChildByRole(Roles.EXPRESSION, value);
   }
   
   public final JavaTokenNode getSemicolonToken() {
     return (JavaTokenNode)getChildByRole(Roles.SEMICOLON);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitExpressionStatement(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof ExpressionStatement)) && (!other.isNull()) && (getExpression().matches(((ExpressionStatement)other).getExpression(), match));
   }
 }


