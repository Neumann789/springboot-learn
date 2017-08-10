 package com.strobel.decompiler.languages.java.analysis;
 
 import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
 import com.strobel.decompiler.languages.java.ast.AssignmentOperatorType;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.UnaryOperatorExpression;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class UsageClassifier
 {
   public static UsageType getUsageType(Expression expression)
   {
     AstNode parent = expression.getParent();
     
     if ((parent instanceof BinaryOperatorExpression)) {
       return UsageType.Read;
     }
     
     if ((parent instanceof AssignmentExpression)) {
       if (expression.matches(((AssignmentExpression)parent).getLeft())) {
         AssignmentOperatorType operator = ((AssignmentExpression)parent).getOperator();
         
         if ((operator == AssignmentOperatorType.ANY) || (operator == AssignmentOperatorType.ASSIGN)) {
           return UsageType.Write;
         }
         
         return UsageType.ReadWrite;
       }
       return UsageType.Read;
     }
     
     if ((parent instanceof UnaryOperatorExpression)) {
       UnaryOperatorExpression unary = (UnaryOperatorExpression)parent;
       
       switch (unary.getOperator()) {
       case ANY: 
         return UsageType.ReadWrite;
       
       case NOT: 
       case BITWISE_NOT: 
       case MINUS: 
       case PLUS: 
         return UsageType.Read;
       
       case INCREMENT: 
       case DECREMENT: 
       case POST_INCREMENT: 
       case POST_DECREMENT: 
         return UsageType.ReadWrite;
       }
       
     }
     return UsageType.Read;
   }
 }


