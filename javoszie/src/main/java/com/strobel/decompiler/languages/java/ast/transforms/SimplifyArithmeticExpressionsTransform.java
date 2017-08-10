 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.CommonTypeReferences;
 import com.strobel.assembler.metadata.JvmType;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
 import com.strobel.decompiler.languages.java.ast.AssignmentOperatorType;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorType;
 import com.strobel.decompiler.languages.java.ast.CastExpression;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.JavaPrimitiveCast;
 import com.strobel.decompiler.languages.java.ast.JavaResolver;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.PrimitiveExpression;
 import com.strobel.decompiler.languages.java.ast.UnaryOperatorExpression;
 import com.strobel.decompiler.languages.java.ast.UnaryOperatorType;
 import com.strobel.decompiler.semantics.ResolveResult;
 
 public class SimplifyArithmeticExpressionsTransform
   extends ContextTrackingVisitor<Void>
 {
   private final JavaResolver _resolver;
   
   public SimplifyArithmeticExpressionsTransform(DecompilerContext context)
   {
     super(context);
     this._resolver = new JavaResolver(context);
   }
   
   public Void visitUnaryOperatorExpression(UnaryOperatorExpression node, Void data)
   {
     super.visitUnaryOperatorExpression(node, data);
     
     UnaryOperatorType operator = node.getOperator();
     
     switch (operator) {
     case PLUS: 
     case MINUS: 
       boolean minus = operator == UnaryOperatorType.MINUS;
       
       if ((node.getExpression() instanceof PrimitiveExpression)) {
         PrimitiveExpression operand = (PrimitiveExpression)node.getExpression();
         
 
 
         if ((operand.getValue() instanceof Number)) { Number negatedValue;
           boolean isNegative; Number negatedValue; if (((operand.getValue() instanceof Float)) || ((operand.getValue() instanceof Double))) {
             double value = ((Double)JavaPrimitiveCast.cast(JvmType.Double, operand.getValue())).doubleValue();
             
 
 
 
 
             boolean isNegative = (!Double.isNaN(value)) && ((Double.doubleToRawLongBits(value) & 0x8000000000000000) != 0L);
             
 
             negatedValue = (Number)JavaPrimitiveCast.cast(JvmType.forValue(operand.getValue(), true), Double.valueOf(-value));
           }
           else {
             long value = ((Long)JavaPrimitiveCast.cast(JvmType.Long, operand.getValue())).longValue();
             
             isNegative = value < 0L;
             negatedValue = (Number)JavaPrimitiveCast.cast(JvmType.forValue(operand.getValue(), true), Long.valueOf(-value));
           }
           
           if (minus == isNegative) {
             operand.remove();
             node.replaceWith(operand);
             
             if (isNegative) {
               operand.setValue(negatedValue);
             }
           }
         }
       }
       break;
     }
     
     return null;
   }
   
   public Void visitBinaryOperatorExpression(BinaryOperatorExpression node, Void data)
   {
     super.visitBinaryOperatorExpression(node, data);
     
     BinaryOperatorType operator = node.getOperator();
     
     switch (operator) {
     case ADD: 
     case SUBTRACT: 
       ResolveResult leftResult = this._resolver.apply(node.getLeft());
       
       if ((leftResult == null) || (leftResult.getType() == null) || (leftResult.getType().isEquivalentTo(CommonTypeReferences.String)))
       {
 
 
         return null;
       }
       
       if ((node.getRight() instanceof PrimitiveExpression)) {
         PrimitiveExpression right = (PrimitiveExpression)node.getRight();
         
 
         if ((right.getValue() instanceof Number)) { Number negatedValue;
           boolean isNegative;
           Number negatedValue;
           if (((right.getValue() instanceof Float)) || ((right.getValue() instanceof Double))) {
             double value = ((Double)JavaPrimitiveCast.cast(JvmType.Double, right.getValue())).doubleValue();
             
 
 
 
 
             boolean isNegative = (!Double.isNaN(value)) && ((Double.doubleToRawLongBits(value) & 0x8000000000000000) != 0L);
             
 
             negatedValue = (Number)JavaPrimitiveCast.cast(JvmType.forValue(right.getValue(), true), Double.valueOf(-value));
           }
           else {
             long value = ((Long)JavaPrimitiveCast.cast(JvmType.Long, right.getValue())).longValue();
             
             isNegative = value < 0L;
             negatedValue = (Number)JavaPrimitiveCast.cast(JvmType.forValue(right.getValue(), true), Long.valueOf(-value));
           }
           
           if (isNegative) {
             right.setValue(negatedValue);
             
             node.setOperator(operator == BinaryOperatorType.ADD ? BinaryOperatorType.SUBTRACT : BinaryOperatorType.ADD);
           }
         }
       }
       
 
       break;
     
 
 
 
     case EXCLUSIVE_OR: 
       if ((node.getRight() instanceof PrimitiveExpression)) {
         Expression left = node.getLeft();
         PrimitiveExpression right = (PrimitiveExpression)node.getRight();
         
         if ((right.getValue() instanceof Number)) {
           long value = ((Long)JavaPrimitiveCast.cast(JvmType.Long, right.getValue())).longValue();
           
           if (value == -1L) {
             left.remove();
             
             UnaryOperatorExpression replacement = new UnaryOperatorExpression(UnaryOperatorType.BITWISE_NOT, left);
             
 
 
 
             node.replaceWith(replacement);
           }
         }
       }
       
       break;
     }
     
     
     return null;
   }
   
   public Void visitAssignmentExpression(AssignmentExpression node, Void data)
   {
     super.visitAssignmentExpression(node, data);
     
     AssignmentOperatorType operator = node.getOperator();
     
     switch (operator) {
     case ADD: 
     case SUBTRACT: 
       ResolveResult leftResult = this._resolver.apply(node.getLeft());
       
       if ((leftResult == null) || (leftResult.getType() == null) || (leftResult.getType().isEquivalentTo(CommonTypeReferences.String)))
       {
 
 
         return null;
       }
       
       Expression rValue = node.getRight();
       boolean dropCast = false;
       
       if ((rValue instanceof CastExpression)) {
         CastExpression cast = (CastExpression)rValue;
         AstType castType = cast.getType();
         
         if ((castType != null) && (!castType.isNull())) {
           TypeReference typeReference = (TypeReference)castType.getUserData(Keys.TYPE_REFERENCE);
           
           if (typeReference != null) {
             JvmType jvmType = typeReference.getSimpleType();
             
             switch (jvmType) {
             case Byte: 
             case Short: 
             case Character: 
               if ((cast.getExpression() instanceof PrimitiveExpression)) {
                 rValue = cast.getExpression();
                 dropCast = true;
               }
               break;
             }
             
           }
         }
       }
       if ((rValue instanceof PrimitiveExpression)) {
         PrimitiveExpression right = (PrimitiveExpression)rValue;
         
 
         if ((right.getValue() instanceof Number)) { Number negatedValue;
           boolean isNegative;
           Number negatedValue;
           if (((right.getValue() instanceof Float)) || ((right.getValue() instanceof Double))) {
             double value = ((Double)JavaPrimitiveCast.cast(JvmType.Double, right.getValue())).doubleValue();
             
 
 
 
 
             boolean isNegative = (!Double.isNaN(value)) && ((Double.doubleToRawLongBits(value) & 0x8000000000000000) != 0L);
             
 
             negatedValue = (Number)JavaPrimitiveCast.cast(JvmType.forValue(right.getValue(), true), Double.valueOf(-value));
           }
           else {
             long value = ((Long)JavaPrimitiveCast.cast(JvmType.Long, right.getValue())).longValue();
             
             isNegative = value < 0L;
             negatedValue = (Number)JavaPrimitiveCast.cast(JvmType.forValue(right.getValue(), true), Long.valueOf(-value));
           }
           
           if (isNegative) {
             right.setValue(negatedValue);
             
             node.setOperator(operator == AssignmentOperatorType.ADD ? AssignmentOperatorType.SUBTRACT : AssignmentOperatorType.ADD);
           }
           
 
 
 
           if (dropCast) {
             rValue.remove();
             node.setRight(rValue);
           }
         }
       }
       break;
     }
     
     return null;
   }
 }


