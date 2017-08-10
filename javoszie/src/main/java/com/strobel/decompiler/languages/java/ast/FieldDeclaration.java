 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.languages.EntityType;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class FieldDeclaration
   extends EntityDeclaration
 {
   public final AstNodeCollection<VariableInitializer> getVariables()
   {
     return getChildrenByRole(Roles.VARIABLE);
   }
   
   public EntityType getEntityType()
   {
     return EntityType.FIELD;
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitFieldDeclaration(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof FieldDeclaration)) {
       FieldDeclaration otherDeclaration = (FieldDeclaration)other;
       
       return (!otherDeclaration.isNull()) && (matchString(getName(), otherDeclaration.getName())) && (matchAnnotationsAndModifiers(otherDeclaration, match)) && (getReturnType().matches(otherDeclaration.getReturnType(), match));
     }
     
 
 
 
     return false;
   }
 }


