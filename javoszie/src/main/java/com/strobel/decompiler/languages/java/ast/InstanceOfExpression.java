 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class InstanceOfExpression
   extends Expression
 {
   public static final TokenRole INSTANCE_OF_KEYWORD_ROLE = new TokenRole("instanceof", 3);
   
   public InstanceOfExpression(int offset, Expression expression, AstType type) {
     super(offset);
     setExpression(expression);
     setType(type);
   }
   
   public final AstType getType() {
     return (AstType)getChildByRole(Roles.TYPE);
   }
   
   public final void setType(AstType type) {
     setChildByRole(Roles.TYPE, type);
   }
   
   public final JavaTokenNode getInstanceOfToken() {
     return (JavaTokenNode)getChildByRole(INSTANCE_OF_KEYWORD_ROLE);
   }
   
   public final Expression getExpression() {
     return (Expression)getChildByRole(Roles.EXPRESSION);
   }
   
   public final void setExpression(Expression value) {
     setChildByRole(Roles.EXPRESSION, value);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitInstanceOfExpression(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof InstanceOfExpression)) {
       InstanceOfExpression otherExpression = (InstanceOfExpression)other;
       
       return (!otherExpression.isNull()) && (getExpression().matches(otherExpression.getExpression(), match)) && (getType().matches(otherExpression.getType(), match));
     }
     
 
 
     return false;
   }
 }


