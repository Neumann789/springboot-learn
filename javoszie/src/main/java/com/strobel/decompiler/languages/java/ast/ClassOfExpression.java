 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ClassOfExpression
   extends Expression
 {
   public static final TokenRole ClassKeywordRole = new TokenRole("class", 1);
   
   public ClassOfExpression(int offset, AstType type) {
     super(offset);
     addChild(type, Roles.TYPE);
   }
   
   public final AstType getType() {
     return (AstType)getChildByRole(Roles.TYPE);
   }
   
   public final void setType(AstType type) {
     setChildByRole(Roles.TYPE, type);
   }
   
   public final JavaTokenNode getDotToken() {
     return (JavaTokenNode)getChildByRole(Roles.DOT);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitClassOfExpression(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof ClassOfExpression)) && (getType().matches(((ClassOfExpression)other).getType(), match));
   }
   
 
   public boolean isReference()
   {
     return true;
   }
 }


