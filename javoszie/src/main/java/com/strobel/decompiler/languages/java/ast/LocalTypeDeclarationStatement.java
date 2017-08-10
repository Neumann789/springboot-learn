 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class LocalTypeDeclarationStatement
   extends Statement
 {
   public LocalTypeDeclarationStatement(int offset, TypeDeclaration type)
   {
     super(offset);
     setChildByRole(Roles.LOCAL_TYPE_DECLARATION, type);
   }
   
   public final TypeDeclaration getTypeDeclaration() {
     return (TypeDeclaration)getChildByRole(Roles.LOCAL_TYPE_DECLARATION);
   }
   
   public final void setTypeDeclaration(TypeDeclaration type) {
     setChildByRole(Roles.LOCAL_TYPE_DECLARATION, type);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitLocalTypeDeclarationStatement(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return false;
   }
 }


