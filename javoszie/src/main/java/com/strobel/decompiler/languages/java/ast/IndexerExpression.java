 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class IndexerExpression
   extends Expression
 {
   public IndexerExpression(int offset, Expression target, Expression argument)
   {
     super(offset);
     setTarget(target);
     setArgument(argument);
   }
   
   public final Expression getTarget() {
     return (Expression)getChildByRole(Roles.TARGET_EXPRESSION);
   }
   
   public final void setTarget(Expression value) {
     setChildByRole(Roles.TARGET_EXPRESSION, value);
   }
   
   public final Expression getArgument() {
     return (Expression)getChildByRole(Roles.ARGUMENT);
   }
   
   public final void setArgument(Expression value) {
     setChildByRole(Roles.ARGUMENT, value);
   }
   
   public final JavaTokenNode getLeftBracketToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_BRACKET);
   }
   
   public final JavaTokenNode getRightBracketToken() {
     return (JavaTokenNode)getChildByRole(Roles.RIGHT_BRACKET);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitIndexerExpression(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof IndexerExpression)) {
       IndexerExpression otherIndexer = (IndexerExpression)other;
       
       return (!otherIndexer.isNull()) && (getTarget().matches(otherIndexer.getTarget(), match)) && (getArgument().matches(otherIndexer.getArgument(), match));
     }
     
 
 
     return false;
   }
 }


