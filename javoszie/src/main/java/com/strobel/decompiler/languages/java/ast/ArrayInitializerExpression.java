 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import java.util.Collections;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ArrayInitializerExpression
   extends Expression
 {
   public ArrayInitializerExpression()
   {
     super(-34);
   }
   
   public ArrayInitializerExpression(Iterable<Expression> elements) {
     super(-34);
     AstNodeCollection<Expression> elementsCollection;
     if (elements != null) {
       elementsCollection = getElements();
       
       for (Expression element : elements) {
         elementsCollection.add(element);
       }
     }
   }
   
   public ArrayInitializerExpression(Expression... elements) {
     super(-34);
     
     if (elements != null) {
       Collections.addAll(getElements(), elements);
     }
   }
   
   public final JavaTokenNode getLeftBraceToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_BRACE);
   }
   
   public final AstNodeCollection<Expression> getElements() {
     return getChildrenByRole(Roles.EXPRESSION);
   }
   
   public final JavaTokenNode getRightBraceToken() {
     return (JavaTokenNode)getChildByRole(Roles.RIGHT_BRACE);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitArrayInitializerExpression(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof ArrayInitializerExpression)) {
       ArrayInitializerExpression otherInitializer = (ArrayInitializerExpression)other;
       
       return (!otherInitializer.isNull()) && (getElements().matches(otherInitializer.getElements(), match));
     }
     
 
     return false;
   }
   
 
 
   public static final ArrayInitializerExpression NULL = new NullArrayInitializerExpression(null);
   
   private static final class NullArrayInitializerExpression extends ArrayInitializerExpression
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


