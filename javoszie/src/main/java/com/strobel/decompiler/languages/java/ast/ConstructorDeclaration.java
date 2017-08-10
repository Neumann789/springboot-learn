 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.languages.EntityType;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ConstructorDeclaration
   extends EntityDeclaration
 {
   public static final TokenRole THROWS_KEYWORD = MethodDeclaration.THROWS_KEYWORD;
   
   public final AstNodeCollection<ParameterDeclaration> getParameters() {
     return getChildrenByRole(Roles.PARAMETER);
   }
   
   public final AstNodeCollection<AstType> getThrownTypes() {
     return getChildrenByRole(Roles.THROWN_TYPE);
   }
   
   public final BlockStatement getBody() {
     return (BlockStatement)getChildByRole(Roles.BODY);
   }
   
   public final void setBody(BlockStatement value) {
     setChildByRole(Roles.BODY, value);
   }
   
   public final JavaTokenNode getLeftParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_PARENTHESIS);
   }
   
   public final JavaTokenNode getRightParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.RIGHT_PARENTHESIS);
   }
   
   public EntityType getEntityType()
   {
     return EntityType.CONSTRUCTOR;
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitConstructorDeclaration(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof MethodDeclaration)) {
       MethodDeclaration otherDeclaration = (MethodDeclaration)other;
       
       return (!otherDeclaration.isNull()) && (matchString(getName(), otherDeclaration.getName())) && (matchAnnotationsAndModifiers(otherDeclaration, match)) && (getParameters().matches(otherDeclaration.getParameters(), match)) && (getBody().matches(otherDeclaration.getBody(), match));
     }
     
 
 
 
 
     return false;
   }
 }


