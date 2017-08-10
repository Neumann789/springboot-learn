 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Role;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class IfElseStatement
   extends Statement
 {
   public static final TokenRole IF_KEYWORD_ROLE = new TokenRole("if", 1);
   public static final TokenRole ELSE_KEYWORD_ROLE = new TokenRole("else", 1);
   public static final Role<Expression> CONDITION_ROLE = Roles.CONDITION;
   public static final Role<Statement> TRUE_ROLE = new Role("True", Statement.class, Statement.NULL);
   public static final Role<Statement> FALSE_ROLE = new Role("False", Statement.class, Statement.NULL);
   
   public IfElseStatement(int offset, Expression condition, Statement trueStatement) {
     this(offset, condition, trueStatement, null);
   }
   
   public IfElseStatement(int offset, Expression condition, Statement trueStatement, Statement falseStatement) {
     super(offset);
     setCondition(condition);
     setTrueStatement(trueStatement);
     setFalseStatement(falseStatement);
   }
   
   public final JavaTokenNode getIfToken() {
     return (JavaTokenNode)getChildByRole(IF_KEYWORD_ROLE);
   }
   
   public final JavaTokenNode getElseToken() {
     return (JavaTokenNode)getChildByRole(IF_KEYWORD_ROLE);
   }
   
   public final JavaTokenNode getLeftParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_PARENTHESIS);
   }
   
   public final JavaTokenNode getRightParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.RIGHT_PARENTHESIS);
   }
   
   public final Expression getCondition() {
     return (Expression)getChildByRole(CONDITION_ROLE);
   }
   
   public final void setCondition(Expression value) {
     setChildByRole(CONDITION_ROLE, value);
   }
   
   public final Statement getTrueStatement() {
     return (Statement)getChildByRole(TRUE_ROLE);
   }
   
   public final void setTrueStatement(Statement value) {
     setChildByRole(TRUE_ROLE, value);
   }
   
   public final Statement getFalseStatement() {
     return (Statement)getChildByRole(FALSE_ROLE);
   }
   
   public final void setFalseStatement(Statement value) {
     setChildByRole(FALSE_ROLE, value);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitIfElseStatement(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof IfElseStatement)) {
       IfElseStatement otherStatement = (IfElseStatement)other;
       
       return (!other.isNull()) && (getCondition().matches(otherStatement.getCondition(), match)) && (getTrueStatement().matches(otherStatement.getTrueStatement(), match)) && (getFalseStatement().matches(otherStatement.getFalseStatement(), match));
     }
     
 
 
 
     return false;
   }
 }


