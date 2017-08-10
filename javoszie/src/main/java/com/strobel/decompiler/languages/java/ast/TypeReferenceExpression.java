 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class TypeReferenceExpression
   extends Expression
 {
   public TypeReferenceExpression(int offset, AstType type)
   {
     super(offset);
     addChild((AstNode)VerifyArgument.notNull(type, "type"), Roles.TYPE);
   }
   
   public final AstType getType() {
     return (AstType)getChildByRole(Roles.TYPE);
   }
   
   public final void setType(AstType type) {
     setChildByRole(Roles.TYPE, type);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitTypeReference(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof TypeReferenceExpression)) && (!other.isNull()) && (getType().matches(((TypeReferenceExpression)other).getType(), match));
   }
   
 
 
   public boolean isReference()
   {
     return true;
   }
 }


