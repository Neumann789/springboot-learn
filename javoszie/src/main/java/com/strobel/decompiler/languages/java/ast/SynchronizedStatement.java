 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class SynchronizedStatement
   extends Statement
 {
   public static final TokenRole SYNCHRONIZED_KEYWORD_ROLE = new TokenRole("synchronized", 1);
   
   public SynchronizedStatement(int offset) {
     super(offset);
   }
   
   public final Statement getEmbeddedStatement() {
     return (Statement)getChildByRole(Roles.EMBEDDED_STATEMENT);
   }
   
   public final void setEmbeddedStatement(Statement value) {
     setChildByRole(Roles.EMBEDDED_STATEMENT, value);
   }
   
   public final Expression getExpression() {
     return (Expression)getChildByRole(Roles.EXPRESSION);
   }
   
   public final void setExpression(Expression value) {
     setChildByRole(Roles.EXPRESSION, value);
   }
   
   public final JavaTokenNode getSynchronizedToken() {
     return (JavaTokenNode)getChildByRole(SYNCHRONIZED_KEYWORD_ROLE);
   }
   
   public final JavaTokenNode getLeftParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_PARENTHESIS);
   }
   
   public final JavaTokenNode getRightParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.RIGHT_PARENTHESIS);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitSynchronizedStatement(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof SynchronizedStatement)) {
       SynchronizedStatement otherStatement = (SynchronizedStatement)other;
       
       return (!otherStatement.isNull()) && (getExpression().matches(otherStatement.getExpression(), match)) && (getEmbeddedStatement().matches(otherStatement.getEmbeddedStatement(), match));
     }
     
 
 
     return false;
   }
 }


