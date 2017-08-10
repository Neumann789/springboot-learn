 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ThrowStatement
   extends Statement
 {
   public static final TokenRole THROW_KEYWORD_ROLE = new TokenRole("throw", 1);
   
   public ThrowStatement(Expression expression) {
     super(expression.getOffset());
     setExpression(expression);
   }
   
   public final JavaTokenNode getThrowToken() {
     return (JavaTokenNode)getChildByRole(THROW_KEYWORD_ROLE);
   }
   
   public final JavaTokenNode getSemicolonToken() {
     return (JavaTokenNode)getChildByRole(Roles.SEMICOLON);
   }
   
   public final Expression getExpression() {
     return (Expression)getChildByRole(Roles.EXPRESSION);
   }
   
   public final void setExpression(Expression value) {
     setChildByRole(Roles.EXPRESSION, value);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitThrowStatement(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof ThrowStatement)) && (!other.isNull()) && (getExpression().matches(((ThrowStatement)other).getExpression(), match));
   }
 }


