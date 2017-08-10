 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Annotation
   extends Expression
 {
   private boolean _hasArgumentList;
   
   public Annotation()
   {
     super(-34);
   }
   
   public final AstType getType() {
     return (AstType)getChildByRole(Roles.TYPE);
   }
   
   public final void setType(AstType type) {
     setChildByRole(Roles.TYPE, type);
   }
   
   public final boolean hasArgumentList() {
     return this._hasArgumentList;
   }
   
   public final void setHasArgumentList(boolean hasArgumentList) {
     verifyNotFrozen();
     this._hasArgumentList = hasArgumentList;
   }
   
   public final AstNodeCollection<Expression> getArguments() {
     return getChildrenByRole(Roles.ARGUMENT);
   }
   
   public NodeType getNodeType()
   {
     return NodeType.UNKNOWN;
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitAnnotation(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof Annotation)) {
       Annotation otherAnnotation = (Annotation)other;
       
       return (!otherAnnotation.isNull()) && (getType().matches(otherAnnotation.getType(), match)) && (getArguments().matches(otherAnnotation.getArguments(), match));
     }
     
 
 
     return false;
   }
   
   public String toString()
   {
     return isNull() ? "Null" : getText();
   }
 }


