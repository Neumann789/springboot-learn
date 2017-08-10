 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.languages.EntityType;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class TypeDeclaration
   extends EntityDeclaration
 {
   private ClassType _classType;
   
   public final JavaTokenNode getTypeKeyword()
   {
     switch (this._classType) {
     case CLASS: 
       return (JavaTokenNode)getChildByRole(Roles.CLASS_KEYWORD);
     case INTERFACE: 
       return (JavaTokenNode)getChildByRole(Roles.INTERFACE_KEYWORD);
     case ANNOTATION: 
       return (JavaTokenNode)getChildByRole(Roles.ANNOTATION_KEYWORD);
     case ENUM: 
       return (JavaTokenNode)getChildByRole(Roles.ENUM_KEYWORD);
     }
     return JavaTokenNode.NULL;
   }
   
   public final ClassType getClassType()
   {
     return this._classType;
   }
   
   public final void setClassType(ClassType classType) {
     verifyNotFrozen();
     this._classType = classType;
   }
   
   public final AstNodeCollection<TypeParameterDeclaration> getTypeParameters() {
     return getChildrenByRole(Roles.TYPE_PARAMETER);
   }
   
   public final AstNodeCollection<AstType> getInterfaces() {
     return getChildrenByRole(Roles.IMPLEMENTED_INTERFACE);
   }
   
   public final AstType getBaseType() {
     return (AstType)getChildByRole(Roles.BASE_TYPE);
   }
   
   public final void setBaseType(AstType value) {
     setChildByRole(Roles.BASE_TYPE, value);
   }
   
   public final JavaTokenNode getLeftBraceToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_BRACE);
   }
   
   public final AstNodeCollection<EntityDeclaration> getMembers() {
     return getChildrenByRole(Roles.TYPE_MEMBER);
   }
   
   public final JavaTokenNode getRightBraceToken() {
     return (JavaTokenNode)getChildByRole(Roles.RIGHT_BRACE);
   }
   
   public NodeType getNodeType()
   {
     return NodeType.TYPE_DECLARATION;
   }
   
   public EntityType getEntityType()
   {
     return EntityType.TYPE_DEFINITION;
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitTypeDeclaration(this, data);
   }
   
   public TypeDeclaration clone()
   {
     TypeDeclaration copy = (TypeDeclaration)super.clone();
     copy._classType = this._classType;
     return copy;
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof TypeDeclaration)) {
       TypeDeclaration otherDeclaration = (TypeDeclaration)other;
       
       return (!otherDeclaration.isNull()) && (this._classType == otherDeclaration._classType) && (matchString(getName(), otherDeclaration.getName())) && (matchAnnotationsAndModifiers(otherDeclaration, match)) && (getTypeParameters().matches(otherDeclaration.getTypeParameters(), match)) && (getBaseType().matches(otherDeclaration.getBaseType(), match)) && (getInterfaces().matches(otherDeclaration.getInterfaces(), match)) && (getMembers().matches(otherDeclaration.getMembers(), match));
     }
     
 
 
 
 
 
 
 
     return false;
   }
   
 
 
   public static final TypeDeclaration NULL = new NullTypeDeclaration(null);
   
   private static final class NullTypeDeclaration extends TypeDeclaration
   {
     public final boolean isNull() {
       return true;
     }
     
     public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
     {
       return null;
     }
     
     public boolean matches(INode other, Match match)
     {
       return (other == null) || (other.isNull());
     }
   }
 }


