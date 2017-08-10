 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import java.util.List;
 import javax.lang.model.element.Modifier;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ForEachStatement
   extends Statement
 {
   public static final TokenRole FOR_KEYWORD_ROLE = ForStatement.FOR_KEYWORD_ROLE;
   public static final TokenRole COLON_ROLE = new TokenRole(":", 2);
   
   public ForEachStatement(int offset) {
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
   
   public final AstType getVariableType() {
     return (AstType)getChildByRole(Roles.TYPE);
   }
   
   public final void setVariableType(AstType value) {
     setChildByRole(Roles.TYPE, value);
   }
   
   public final String getVariableName() {
     return ((Identifier)getChildByRole(Roles.IDENTIFIER)).getName();
   }
   
   public final void setVariableName(String value) {
     setChildByRole(Roles.IDENTIFIER, Identifier.create(value));
   }
   
   public final Identifier getVariableNameToken() {
     return (Identifier)getChildByRole(Roles.IDENTIFIER);
   }
   
   public final void setVariableNameToken(Identifier value) {
     setChildByRole(Roles.IDENTIFIER, value);
   }
   
   public final List<Modifier> getVariableModifiers() {
     return EntityDeclaration.getModifiers(this);
   }
   
   public final void addVariableModifier(Modifier modifier) {
     EntityDeclaration.addModifier(this, modifier);
   }
   
   public final void removeVariableModifier(Modifier modifier) {
     EntityDeclaration.removeModifier(this, modifier);
   }
   
   public final void setVariableModifiers(List<Modifier> modifiers) {
     EntityDeclaration.setModifiers(this, modifiers);
   }
   
   public final JavaTokenNode getLeftParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_PARENTHESIS);
   }
   
   public final JavaTokenNode getRightParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.RIGHT_PARENTHESIS);
   }
   
   public final Expression getInExpression() {
     return (Expression)getChildByRole(Roles.EXPRESSION);
   }
   
   public final void setInExpression(Expression value) {
     setChildByRole(Roles.EXPRESSION, value);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitForEachStatement(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof ForEachStatement)) {
       ForEachStatement otherStatement = (ForEachStatement)other;
       
       return (!other.isNull()) && (getVariableType().matches(otherStatement.getVariableType(), match)) && (matchString(getVariableName(), otherStatement.getVariableName())) && (getInExpression().matches(otherStatement.getInExpression(), match)) && (getEmbeddedStatement().matches(otherStatement.getEmbeddedStatement(), match));
     }
     
 
 
 
 
     return false;
   }
 }


