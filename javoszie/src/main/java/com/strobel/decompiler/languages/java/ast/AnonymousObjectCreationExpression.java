 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class AnonymousObjectCreationExpression
   extends ObjectCreationExpression
 {
   public AnonymousObjectCreationExpression(int offset, TypeDeclaration typeDeclaration, AstType type)
   {
     super(offset, type);
     setTypeDeclaration(typeDeclaration);
   }
   
 
 
 
 
   public AnonymousObjectCreationExpression(int offset, TypeDeclaration typeDeclaration, AstType type, Expression... arguments)
   {
     super(offset, type, arguments);
     setTypeDeclaration(typeDeclaration);
   }
   
 
 
 
 
   public AnonymousObjectCreationExpression(int offset, TypeDeclaration typeDeclaration, AstType type, Iterable<Expression> arguments)
   {
     super(offset, type, arguments);
     setTypeDeclaration(typeDeclaration);
   }
   
   public final TypeDeclaration getTypeDeclaration() {
     return (TypeDeclaration)getChildByRole(Roles.LOCAL_TYPE_DECLARATION);
   }
   
   public final void setTypeDeclaration(TypeDeclaration value) {
     setChildByRole(Roles.LOCAL_TYPE_DECLARATION, value);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitAnonymousObjectCreationExpression(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof AnonymousObjectCreationExpression)) {
       AnonymousObjectCreationExpression otherExpression = (AnonymousObjectCreationExpression)other;
       
       return (super.matches(other, match)) && (getTypeDeclaration().matches(otherExpression.getTypeDeclaration(), match));
     }
     
 
     return false;
   }
 }


