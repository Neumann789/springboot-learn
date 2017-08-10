 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.languages.EntityType;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class EnumValueDeclaration
   extends EntityDeclaration
 {
   public final AstNodeCollection<Expression> getArguments()
   {
     return getChildrenByRole(Roles.ARGUMENT);
   }
   
   public EntityType getEntityType()
   {
     return EntityType.ENUM_VALUE;
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
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitEnumValueDeclaration(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof FieldDeclaration)) {
       FieldDeclaration otherDeclaration = (FieldDeclaration)other;
       
       return (!otherDeclaration.isNull()) && (matchString(getName(), otherDeclaration.getName())) && (matchAnnotationsAndModifiers(otherDeclaration, match)) && (getReturnType().matches(otherDeclaration.getReturnType(), match)) && (getMembers().matches(getMembers(), match));
     }
     
 
 
 
 
     return false;
   }
 }


