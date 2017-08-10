 package com.strobel.decompiler.patterns;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorType;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class SingleOrBinaryAggregateNode
   extends Pattern
 {
   private final INode _pattern;
   private final BinaryOperatorType _operator;
   
   public SingleOrBinaryAggregateNode(BinaryOperatorType operator, INode pattern)
   {
     this._pattern = ((INode)VerifyArgument.notNull(pattern, "pattern"));
     this._operator = ((BinaryOperatorType)VerifyArgument.notNull(operator, "operator"));
   }
   
   public boolean matches(INode other, Match match)
   {
     if (this._pattern.matches(other, match)) {
       return true;
     }
     
     if ((other instanceof BinaryOperatorExpression)) {
       BinaryOperatorExpression binary = (BinaryOperatorExpression)other;
       
       if ((this._operator != BinaryOperatorType.ANY) && (binary.getOperator() != this._operator)) {
         return false;
       }
       
       int checkPoint = match.getCheckPoint();
       
       if ((matches(binary.getLeft(), match)) && (matches(binary.getRight(), match))) {
         return true;
       }
       
       match.restoreCheckPoint(checkPoint);
     }
     
     return false;
   }
 }


