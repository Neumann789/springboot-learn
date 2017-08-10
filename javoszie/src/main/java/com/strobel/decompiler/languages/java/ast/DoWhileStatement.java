 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class DoWhileStatement
   extends Statement
 {
   public static final TokenRole DO_KEYWORD_ROLE = new TokenRole("do", 1);
   public static final TokenRole WHILE_KEYWORD_ROLE = new TokenRole("while", 1);
   
   public DoWhileStatement(int offset) {
     super(offset);
   }
   
   public final Statement getEmbeddedStatement() {
     return (Statement)getChildByRole(Roles.EMBEDDED_STATEMENT);
   }
   
   public final void setEmbeddedStatement(Statement value) {
     setChildByRole(Roles.EMBEDDED_STATEMENT, value);
   }
   
   public final Expression getCondition() {
     return (Expression)getChildByRole(Roles.CONDITION);
   }
   
   public final void setCondition(Expression value) {
     setChildByRole(Roles.CONDITION, value);
   }
   
   public final JavaTokenNode getDoToken() {
     return (JavaTokenNode)getChildByRole(DO_KEYWORD_ROLE);
   }
   
   public final JavaTokenNode getWhileToken() {
     return (JavaTokenNode)getChildByRole(WHILE_KEYWORD_ROLE);
   }
   
   public final JavaTokenNode getLeftParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_PARENTHESIS);
   }
   
   public final JavaTokenNode getRightParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.RIGHT_PARENTHESIS);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitDoWhileStatement(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof DoWhileStatement)) {
       DoWhileStatement otherStatement = (DoWhileStatement)other;
       
       return (!other.isNull()) && (getEmbeddedStatement().matches(otherStatement.getEmbeddedStatement(), match)) && (getCondition().matches(otherStatement.getCondition(), match));
     }
     
 
 
     return false;
   }
 }


