 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.languages.EntityType;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Role;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class MethodDeclaration
   extends EntityDeclaration
 {
   public static final Role<Expression> DEFAULT_VALUE_ROLE = new Role("DefaultValue", Expression.class, Expression.NULL);
   
   public static final TokenRole DEFAULT_KEYWORD = new TokenRole("default", 1);
   public static final TokenRole THROWS_KEYWORD = new TokenRole("throws", 1);
   
   public final AstType getPrivateImplementationType() {
     return (AstType)getChildByRole(PRIVATE_IMPLEMENTATION_TYPE_ROLE);
   }
   
   public final void setPrivateImplementationType(AstType type) {
     setChildByRole(PRIVATE_IMPLEMENTATION_TYPE_ROLE, type);
   }
   
   public final Expression getDefaultValue() {
     return (Expression)getChildByRole(DEFAULT_VALUE_ROLE);
   }
   
   public final void setDefaultValue(Expression value) {
     setChildByRole(DEFAULT_VALUE_ROLE, value);
   }
   
   public final AstNodeCollection<AstType> getThrownTypes() {
     return getChildrenByRole(Roles.THROWN_TYPE);
   }
   
   public final AstNodeCollection<TypeDeclaration> getDeclaredTypes() {
     return getChildrenByRole(Roles.LOCAL_TYPE_DECLARATION);
   }
   
   public final AstNodeCollection<TypeParameterDeclaration> getTypeParameters() {
     return getChildrenByRole(Roles.TYPE_PARAMETER);
   }
   
   public final AstNodeCollection<ParameterDeclaration> getParameters() {
     return getChildrenByRole(Roles.PARAMETER);
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
     return EntityType.METHOD;
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitMethodDeclaration(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof MethodDeclaration)) {
       MethodDeclaration otherDeclaration = (MethodDeclaration)other;
       
       return (!otherDeclaration.isNull()) && (matchString(getName(), otherDeclaration.getName())) && (matchAnnotationsAndModifiers(otherDeclaration, match)) && (getPrivateImplementationType().matches(otherDeclaration.getPrivateImplementationType(), match)) && (getTypeParameters().matches(otherDeclaration.getTypeParameters(), match)) && (getReturnType().matches(otherDeclaration.getReturnType(), match)) && (getParameters().matches(otherDeclaration.getParameters(), match)) && (getBody().matches(otherDeclaration.getBody(), match));
     }
     
 
 
 
 
 
 
 
     return false;
   }
 }


