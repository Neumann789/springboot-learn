 package com.strobel.decompiler.patterns;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorType;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class LeftmostBinaryOperandNode
   extends Pattern
 {
   private final boolean _matchWithoutOperator;
   private final BinaryOperatorType _operatorType;
   private final INode _operandPattern;
   
   public LeftmostBinaryOperandNode(INode pattern)
   {
     this(pattern, BinaryOperatorType.ANY, false);
   }
   
   public LeftmostBinaryOperandNode(INode pattern, BinaryOperatorType type, boolean matchWithoutOperator) {
     this._matchWithoutOperator = matchWithoutOperator;
     this._operatorType = ((BinaryOperatorType)VerifyArgument.notNull(type, "type"));
     this._operandPattern = ((INode)VerifyArgument.notNull(pattern, "pattern"));
   }
   
   public final INode getOperandPattern() {
     return this._operandPattern;
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((this._matchWithoutOperator) || ((other instanceof BinaryOperatorExpression))) {
       INode current = other;
       
       while (((current instanceof BinaryOperatorExpression)) && ((this._operatorType == BinaryOperatorType.ANY) || (((BinaryOperatorExpression)current).getOperator() == this._operatorType)))
       {
 
 
         current = ((BinaryOperatorExpression)current).getLeft();
       }
       
       return (current != null) && (this._operandPattern.matches(current, match));
     }
     
     return false;
   }
 }


