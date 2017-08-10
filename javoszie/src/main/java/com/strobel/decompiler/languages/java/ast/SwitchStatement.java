 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Role;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class SwitchStatement
   extends Statement
 {
   public static final TokenRole SWITCH_KEYWORD_ROLE = new TokenRole("switch", 1);
   public static final Role<SwitchSection> SWITCH_SECTION_ROLE = new Role("SwitchSection", SwitchSection.class);
   
   public SwitchStatement(Expression testExpression) {
     super(testExpression.getOffset());
     setExpression(testExpression);
   }
   
   public final JavaTokenNode getReturnToken() {
     return (JavaTokenNode)getChildByRole(SWITCH_KEYWORD_ROLE);
   }
   
   public final Expression getExpression() {
     return (Expression)getChildByRole(Roles.EXPRESSION);
   }
   
   public final void setExpression(Expression value) {
     setChildByRole(Roles.EXPRESSION, value);
   }
   
   public final JavaTokenNode getLeftParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_PARENTHESIS);
   }
   
   public final JavaTokenNode getRightParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.RIGHT_PARENTHESIS);
   }
   
   public final JavaTokenNode getLeftBraceToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_BRACE);
   }
   
   public final AstNodeCollection<SwitchSection> getSwitchSections() {
     return getChildrenByRole(SWITCH_SECTION_ROLE);
   }
   
   public final JavaTokenNode getRightBraceToken() {
     return (JavaTokenNode)getChildByRole(Roles.RIGHT_BRACE);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitSwitchStatement(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof SwitchStatement)) {
       SwitchStatement otherStatement = (SwitchStatement)other;
       
       return (!otherStatement.isNull()) && (getExpression().matches(otherStatement.getExpression(), match)) && (getSwitchSections().matches(otherStatement.getSwitchSections(), match));
     }
     
 
 
     return false;
   }
 }


