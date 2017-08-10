 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class MemberReferenceExpression
   extends Expression
 {
   public MemberReferenceExpression(int offset, Expression target, String memberName, Iterable<AstType> typeArguments)
   {
     super(offset);
     addChild(target, Roles.TARGET_EXPRESSION);
     
     setMemberName(memberName);
     
     if (typeArguments != null) {
       for (AstType argument : typeArguments) {
         addChild(argument, Roles.TYPE_ARGUMENT);
       }
     }
   }
   
   public MemberReferenceExpression(int offset, Expression target, String memberName, AstType... typeArguments) {
     super(offset);
     addChild(target, Roles.TARGET_EXPRESSION);
     
     setMemberName(memberName);
     
     if (typeArguments != null) {
       for (AstType argument : typeArguments) {
         addChild(argument, Roles.TYPE_ARGUMENT);
       }
     }
   }
   
   public final String getMemberName() {
     return ((Identifier)getChildByRole(Roles.IDENTIFIER)).getName();
   }
   
   public final void setMemberName(String name) {
     setChildByRole(Roles.IDENTIFIER, Identifier.create(name));
   }
   
   public final Identifier getMemberNameToken() {
     return (Identifier)getChildByRole(Roles.IDENTIFIER);
   }
   
   public final void setMemberNameToken(Identifier token) {
     setChildByRole(Roles.IDENTIFIER, token);
   }
   
   public final Expression getTarget() {
     return (Expression)getChildByRole(Roles.TARGET_EXPRESSION);
   }
   
   public final void setTarget(Expression value) {
     setChildByRole(Roles.TARGET_EXPRESSION, value);
   }
   
   public final AstNodeCollection<AstType> getTypeArguments() {
     return getChildrenByRole(Roles.TYPE_ARGUMENT);
   }
   
   public final JavaTokenNode getDotToken() {
     return (JavaTokenNode)getChildByRole(Roles.DOT);
   }
   
   public final JavaTokenNode getLeftChevronToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_CHEVRON);
   }
   
   public final JavaTokenNode getRightChevronToken() {
     return (JavaTokenNode)getChildByRole(Roles.RIGHT_CHEVRON);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitMemberReferenceExpression(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof MemberReferenceExpression)) {
       MemberReferenceExpression otherExpression = (MemberReferenceExpression)other;
       
       return (!otherExpression.isNull()) && (getTarget().matches(otherExpression.getTarget(), match)) && (matchString(getMemberName(), otherExpression.getMemberName())) && (getTypeArguments().matches(otherExpression.getTypeArguments(), match));
     }
     
 
 
 
     return false;
   }
 }


