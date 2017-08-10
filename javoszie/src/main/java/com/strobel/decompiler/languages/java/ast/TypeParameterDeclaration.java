 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Role;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class TypeParameterDeclaration
   extends AstNode
 {
   public static final Role<Annotation> ANNOTATION_ROLE = EntityDeclaration.ANNOTATION_ROLE;
   
   public TypeParameterDeclaration() {}
   
   public TypeParameterDeclaration(String name)
   {
     setName(name);
   }
   
   public final AstNodeCollection<Annotation> getAnnotations() {
     return getChildrenByRole(ANNOTATION_ROLE);
   }
   
   public final String getName() {
     return ((Identifier)getChildByRole(Roles.IDENTIFIER)).getName();
   }
   
   public final void setName(String value) {
     setChildByRole(Roles.IDENTIFIER, Identifier.create(value));
   }
   
   public final AstType getExtendsBound() {
     return (AstType)getChildByRole(Roles.EXTENDS_BOUND);
   }
   
   public final void setExtendsBound(AstType value) {
     setChildByRole(Roles.EXTENDS_BOUND, value);
   }
   
   public final Identifier getNameToken() {
     return (Identifier)getChildByRole(Roles.IDENTIFIER);
   }
   
   public final void setNameToken(Identifier value) {
     setChildByRole(Roles.IDENTIFIER, value);
   }
   
   public NodeType getNodeType()
   {
     return NodeType.UNKNOWN;
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitTypeParameterDeclaration(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof TypeParameterDeclaration)) {
       TypeParameterDeclaration otherDeclaration = (TypeParameterDeclaration)other;
       
       return (!otherDeclaration.isNull()) && (matchString(getName(), otherDeclaration.getName())) && (getExtendsBound().matches(otherDeclaration.getExtendsBound(), match)) && (getAnnotations().matches(otherDeclaration.getAnnotations(), match));
     }
     
 
 
 
     return false;
   }
 }


