 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class AssertStatement
   extends Statement
 {
   public static final TokenRole ASSERT_KEYWORD_ROLE = new TokenRole("assert", 1);
   
   public AssertStatement(int offset) {
     super(offset);
   }
   
   public final JavaTokenNode getColon() {
     return (JavaTokenNode)getChildByRole(Roles.COLON);
   }
   
   public final Expression getCondition() {
     return (Expression)getChildByRole(Roles.CONDITION);
   }
   
   public final void setCondition(Expression value) {
     setChildByRole(Roles.CONDITION, value);
   }
   
   public final Expression getMessage() {
     return (Expression)getChildByRole(Roles.EXPRESSION);
   }
   
   public final void setMessage(Expression message) {
     setChildByRole(Roles.EXPRESSION, message);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitAssertStatement(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof AssertStatement)) {
       AssertStatement otherAssert = (AssertStatement)other;
       
       return (getCondition().matches(otherAssert.getCondition(), match)) && (getMessage().matches(otherAssert.getMessage()));
     }
     
 
     return false;
   }
 }


