 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Role;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ForStatement
   extends Statement
 {
   public static final TokenRole FOR_KEYWORD_ROLE = new TokenRole("for", 1);
   public static final Role<Statement> INITIALIZER_ROLE = new Role("Initializer", Statement.class, Statement.NULL);
   public static final Role<Statement> ITERATOR_ROLE = new Role("Iterator", Statement.class, Statement.NULL);
   
   public ForStatement(int offset) {
     super(offset);
   }
   
   public final JavaTokenNode getForToken() {
     return (JavaTokenNode)getChildByRole(FOR_KEYWORD_ROLE);
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
   
   public final JavaTokenNode getLeftParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_PARENTHESIS);
   }
   
   public final JavaTokenNode getRightParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.RIGHT_PARENTHESIS);
   }
   
   public final AstNodeCollection<Statement> getInitializers() {
     return getChildrenByRole(INITIALIZER_ROLE);
   }
   
   public final AstNodeCollection<Statement> getIterators() {
     return getChildrenByRole(ITERATOR_ROLE);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitForStatement(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof ForStatement)) {
       ForStatement otherStatement = (ForStatement)other;
       
       return (!other.isNull()) && (getInitializers().matches(otherStatement.getInitializers(), match)) && (getCondition().matches(otherStatement.getCondition(), match)) && (getIterators().matches(otherStatement.getIterators(), match)) && (getEmbeddedStatement().matches(otherStatement.getEmbeddedStatement(), match));
     }
     
 
 
 
 
     return false;
   }
 }


