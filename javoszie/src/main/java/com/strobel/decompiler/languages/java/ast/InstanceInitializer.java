 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.languages.EntityType;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class InstanceInitializer
   extends EntityDeclaration
 {
   public final AstNodeCollection<AstType> getThrownTypes()
   {
     return getChildrenByRole(Roles.THROWN_TYPE);
   }
   
   public final AstNodeCollection<TypeDeclaration> getDeclaredTypes() {
     return getChildrenByRole(Roles.LOCAL_TYPE_DECLARATION);
   }
   
   public final BlockStatement getBody() {
     return (BlockStatement)getChildByRole(Roles.BODY);
   }
   
   public final void setBody(BlockStatement value) {
     setChildByRole(Roles.BODY, value);
   }
   
   public EntityType getEntityType()
   {
     return EntityType.METHOD;
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitInitializerBlock(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof InstanceInitializer)) && (!other.isNull()) && (getBody().matches(((InstanceInitializer)other).getBody(), match));
   }
 }


