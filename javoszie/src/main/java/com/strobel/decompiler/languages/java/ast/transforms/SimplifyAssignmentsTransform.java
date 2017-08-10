 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.CommonTypeReferences;
 import com.strobel.assembler.metadata.MetadataResolver;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
 import com.strobel.decompiler.languages.java.ast.AssignmentOperatorType;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorType;
 import com.strobel.decompiler.languages.java.ast.CastExpression;
 import com.strobel.decompiler.languages.java.ast.ConditionalExpression;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.JavaResolver;
 import com.strobel.decompiler.languages.java.ast.PrimitiveExpression;
 import com.strobel.decompiler.languages.java.ast.UnaryOperatorExpression;
 import com.strobel.decompiler.languages.java.ast.UnaryOperatorType;
 import com.strobel.decompiler.languages.java.utilities.RedundantCastUtility;
 import com.strobel.decompiler.semantics.ResolveResult;
 import com.strobel.functions.Function;
 
 public class SimplifyAssignmentsTransform
   extends ContextTrackingVisitor<AstNode>
   implements IAstTransform
 {
   private static final Function<AstNode, AstNode> NEGATE_FUNCTION = new Function()
   {
     public AstNode apply(AstNode n) {
       if ((n instanceof UnaryOperatorExpression)) {
         UnaryOperatorExpression unary = (UnaryOperatorExpression)n;
         
         if (unary.getOperator() == UnaryOperatorType.NOT) {
           Expression operand = unary.getExpression();
           operand.remove();
           return operand;
         }
       }
       return new UnaryOperatorExpression(UnaryOperatorType.NOT, (Expression)n);
     }
   };
   private final JavaResolver _resolver;
   
   public SimplifyAssignmentsTransform(DecompilerContext context)
   {
     super(context);
     this._resolver = new JavaResolver(context);
   }
   
   private static final PrimitiveExpression TRUE_CONSTANT = new PrimitiveExpression(-34, Boolean.valueOf(true));
   private static final PrimitiveExpression FALSE_CONSTANT = new PrimitiveExpression(-34, Boolean.valueOf(false));
   
   public AstNode visitConditionalExpression(ConditionalExpression node, Void data)
   {
     Expression condition = node.getCondition();
     Expression trueExpression = node.getTrueExpression();
     Expression falseExpression = node.getFalseExpression();
     
     if ((TRUE_CONSTANT.matches(trueExpression)) && (FALSE_CONSTANT.matches(falseExpression)))
     {
 
       condition.remove();
       trueExpression.remove();
       falseExpression.remove();
       
       node.replaceWith(condition);
       
       return (AstNode)condition.acceptVisitor(this, data);
     }
     if ((TRUE_CONSTANT.matches(trueExpression)) && (FALSE_CONSTANT.matches(falseExpression)))
     {
 
       condition.remove();
       trueExpression.remove();
       falseExpression.remove();
       
       Expression negatedCondition = new UnaryOperatorExpression(UnaryOperatorType.NOT, condition);
       
       node.replaceWith(negatedCondition);
       
       return (AstNode)negatedCondition.acceptVisitor(this, data);
     }
     
     return (AstNode)super.visitConditionalExpression(node, data);
   }
   
   public AstNode visitBinaryOperatorExpression(BinaryOperatorExpression node, Void data)
   {
     BinaryOperatorType operator = node.getOperator();
     
     if ((operator == BinaryOperatorType.EQUALITY) || (operator == BinaryOperatorType.INEQUALITY))
     {
 
       Expression left = node.getLeft();
       Expression right = node.getRight();
       
       if ((TRUE_CONSTANT.matches(left)) || (FALSE_CONSTANT.matches(left))) {
         if ((TRUE_CONSTANT.matches(right)) || (FALSE_CONSTANT.matches(right))) {
           return new PrimitiveExpression(node.getOffset(), Boolean.valueOf((TRUE_CONSTANT.matches(left) == TRUE_CONSTANT.matches(right) ? 1 : 0) ^ (operator == BinaryOperatorType.INEQUALITY ? 1 : 0)));
         }
         
 
 
 
         boolean negate = FALSE_CONSTANT.matches(left) ^ operator == BinaryOperatorType.INEQUALITY;
         
         right.remove();
         
         Expression replacement = negate ? new UnaryOperatorExpression(UnaryOperatorType.NOT, right) : right;
         
 
         node.replaceWith(replacement);
         
         return (AstNode)replacement.acceptVisitor(this, data);
       }
       if ((TRUE_CONSTANT.matches(right)) || (FALSE_CONSTANT.matches(right))) {
         boolean negate = FALSE_CONSTANT.matches(right) ^ operator == BinaryOperatorType.INEQUALITY;
         
         left.remove();
         
         Expression replacement = negate ? new UnaryOperatorExpression(UnaryOperatorType.NOT, left) : left;
         
 
         node.replaceWith(replacement);
         
         return (AstNode)replacement.acceptVisitor(this, data);
       }
     }
     
     return (AstNode)super.visitBinaryOperatorExpression(node, data);
   }
   
   public AstNode visitUnaryOperatorExpression(UnaryOperatorExpression node, Void _)
   {
     if ((node.getOperator() == UnaryOperatorType.NOT) && ((node.getExpression() instanceof BinaryOperatorExpression)))
     {
 
       BinaryOperatorExpression binary = (BinaryOperatorExpression)node.getExpression();
       
       boolean successful = true;
       
       switch (binary.getOperator()) {
       case EQUALITY: 
         binary.setOperator(BinaryOperatorType.INEQUALITY);
         break;
       
       case INEQUALITY: 
         binary.setOperator(BinaryOperatorType.EQUALITY);
         break;
       
       case GREATER_THAN: 
         binary.setOperator(BinaryOperatorType.LESS_THAN_OR_EQUAL);
         break;
       
       case GREATER_THAN_OR_EQUAL: 
         binary.setOperator(BinaryOperatorType.LESS_THAN);
         break;
       
       case LESS_THAN: 
         binary.setOperator(BinaryOperatorType.GREATER_THAN_OR_EQUAL);
         break;
       
       case LESS_THAN_OR_EQUAL: 
         binary.setOperator(BinaryOperatorType.GREATER_THAN);
         break;
       
       default: 
         successful = false;
       }
       
       
       if (successful) {
         node.replaceWith(binary);
         return (AstNode)binary.acceptVisitor(this, _);
       }
       
       successful = true;
       
       switch (binary.getOperator()) {
       case LOGICAL_AND: 
         binary.setOperator(BinaryOperatorType.LOGICAL_OR);
         break;
       
       case LOGICAL_OR: 
         binary.setOperator(BinaryOperatorType.LOGICAL_AND);
         break;
       
       default: 
         successful = false;
       }
       
       
       if (successful) {
         binary.getLeft().replaceWith(NEGATE_FUNCTION);
         binary.getRight().replaceWith(NEGATE_FUNCTION);
         node.replaceWith(binary);
         return (AstNode)binary.acceptVisitor(this, _);
       }
     }
     
     return (AstNode)super.visitUnaryOperatorExpression(node, _);
   }
   
   public AstNode visitAssignmentExpression(AssignmentExpression node, Void data)
   {
     Expression left = node.getLeft();
     Expression right = node.getRight();
     
     if (node.getOperator() == AssignmentOperatorType.ASSIGN) {
       if ((right instanceof CastExpression))
       {
 
 
 
         CastExpression castExpression = (CastExpression)right;
         TypeReference castType = castExpression.getType().toTypeReference();
         Expression castedValue = castExpression.getExpression();
         
         if ((castType != null) && (castType.isPrimitive()) && ((castedValue instanceof BinaryOperatorExpression)))
         {
 
 
           ResolveResult leftResult = this._resolver.apply(left);
           
           if ((leftResult != null) && (MetadataResolver.areEquivalent(castType, leftResult.getType())) && (tryRewriteBinaryAsAssignment(node, left, castedValue)))
           {
 
 
             Expression newValue = castExpression.getExpression();
             
             newValue.remove();
             right.replaceWith(newValue);
             
             return (AstNode)newValue.acceptVisitor(this, data);
           }
         }
       }
       
       if (tryRewriteBinaryAsAssignment(node, left, right)) {
         return (AstNode)left.getParent().acceptVisitor(this, data);
       }
     }
     else if (tryRewriteBinaryAsUnary(node, left, right)) {
       return (AstNode)left.getParent().acceptVisitor(this, data);
     }
     
     return (AstNode)super.visitAssignmentExpression(node, data);
   }
   
   private boolean tryRewriteBinaryAsAssignment(AssignmentExpression node, Expression left, Expression right) {
     if ((right instanceof BinaryOperatorExpression)) {
       BinaryOperatorExpression binary = (BinaryOperatorExpression)right;
       Expression innerLeft = binary.getLeft();
       Expression innerRight = binary.getRight();
       BinaryOperatorType binaryOp = binary.getOperator();
       
       if (innerLeft.matches(left)) {
         AssignmentOperatorType assignOp = AssignmentExpression.getCorrespondingAssignmentOperator(binaryOp);
         
         if (assignOp != null) {
           innerRight.remove();
           right.replaceWith(innerRight);
           node.setOperator(assignOp);
           
 
 
 
 
           tryRewriteBinaryAsUnary(node, node.getLeft(), node.getRight());
           return true;
         }
       }
       else if ((binaryOp.isCommutative()) && (innerRight.matches(left))) {
         ResolveResult leftResult = this._resolver.apply(left);
         ResolveResult innerLeftResult = this._resolver.apply(innerLeft);
         
 
 
 
 
 
 
 
 
         if ((leftResult == null) || (leftResult.getType() == null) || (innerLeftResult == null) || (innerLeftResult.getType() == null))
         {
 
           return false;
         }
         
         if ((CommonTypeReferences.String.isEquivalentTo(leftResult.getType())) || (CommonTypeReferences.String.isEquivalentTo(innerLeftResult.getType())))
         {
 
           return false;
         }
         
         AssignmentOperatorType assignOp = AssignmentExpression.getCorrespondingAssignmentOperator(binaryOp);
         
 
 
 
 
         innerLeft.remove();
         right.replaceWith(innerLeft);
         node.setOperator(assignOp);
         return true;
       }
     }
     
     return false;
   }
   
   private boolean tryRewriteBinaryAsUnary(AssignmentExpression node, Expression left, Expression right) {
     AssignmentOperatorType op = node.getOperator();
     
     if ((op == AssignmentOperatorType.ADD) || (op == AssignmentOperatorType.SUBTRACT))
     {
 
       Expression innerRight = right;
       
       while (((innerRight instanceof CastExpression)) && (RedundantCastUtility.isCastRedundant(this._resolver, (CastExpression)innerRight)))
       {
 
         innerRight = ((CastExpression)innerRight).getExpression();
       }
       
       if (!(innerRight instanceof PrimitiveExpression)) {
         return false;
       }
       
       Object value = ((PrimitiveExpression)innerRight).getValue();
       
       long delta = 0L;
       
       if ((value instanceof Number)) {
         Number n = (Number)value;
         
         if (((value instanceof Float)) || ((value instanceof Double))) {
           double d = n.doubleValue();
           
           if (Math.abs(d) == 1.0D) {
             delta = d;
           }
         }
         else {
           delta = n.longValue();
         }
       }
       else if ((value instanceof Character)) {
         delta = ((Character)value).charValue();
       }
       
       if (Math.abs(delta) == 1L)
       {
         boolean increment = (delta == 1L ? 1 : 0) ^ (op == AssignmentOperatorType.SUBTRACT ? 1 : 0);
         
 
 
 
 
 
 
 
         UnaryOperatorType unaryOp = increment ? UnaryOperatorType.INCREMENT : UnaryOperatorType.DECREMENT;
         
 
         left.remove();
         node.replaceWith(new UnaryOperatorExpression(unaryOp, left));
         
         return true;
       }
     }
     
     return false;
   }
 }


