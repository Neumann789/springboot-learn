 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.functions.Function;
 import com.strobel.util.ContractUtils;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class InsertParenthesesVisitor
   extends DepthFirstAstVisitor<Void, Void>
 {
   private static final int PRIMARY = 16;
   private static final int CAST = 15;
   private static final int UNARY = 14;
   private static final int MULTIPLICATIVE = 13;
   private static final int ADDITIVE = 12;
   private static final int SHIFT = 11;
   private static final int RELATIONAL_AND_TYPE_TESTING = 10;
   private static final int EQUALITY = 9;
   private static final int BITWISE_AND = 8;
   private static final int EXCLUSIVE_OR = 7;
   private static final int BITWISE_OR = 6;
   private static final int LOGICAL_AND = 5;
   private static final int LOGICAL_OR = 4;
   private static final int CONDITIONAL = 2;
   private static final int ASSIGNMENT = 1;
   private static final Function<AstNode, AstNode> PARENTHESIZE_FUNCTION = new Function()
   {
     public AstNode apply(AstNode input) {
       return new ParenthesizedExpression((Expression)input);
     }
   };
   
   private boolean _insertParenthesesForReadability = true;
   
   public final boolean getInsertParenthesesForReadability() {
     return this._insertParenthesesForReadability;
   }
   
   public final void setInsertParenthesesForReadability(boolean insertParenthesesForReadability) {
     this._insertParenthesesForReadability = insertParenthesesForReadability;
   }
   
   private static int getPrecedence(Expression e) {
     if ((e instanceof UnaryOperatorExpression)) {
       UnaryOperatorExpression unary = (UnaryOperatorExpression)e;
       
       if ((unary.getOperator() == UnaryOperatorType.POST_DECREMENT) || (unary.getOperator() == UnaryOperatorType.POST_INCREMENT))
       {
 
         return 16;
       }
       
       return 14;
     }
     
     if ((e instanceof CastExpression)) {
       return 15;
     }
     
     if ((e instanceof BinaryOperatorExpression)) {
       BinaryOperatorExpression binary = (BinaryOperatorExpression)e;
       
       switch (binary.getOperator()) {
       case MULTIPLY: 
       case DIVIDE: 
       case MODULUS: 
         return 13;
       
       case ADD: 
       case SUBTRACT: 
         return 12;
       
       case SHIFT_LEFT: 
       case SHIFT_RIGHT: 
       case UNSIGNED_SHIFT_RIGHT: 
         return 11;
       
       case GREATER_THAN: 
       case GREATER_THAN_OR_EQUAL: 
       case LESS_THAN: 
       case LESS_THAN_OR_EQUAL: 
         return 10;
       
       case EQUALITY: 
       case INEQUALITY: 
         return 9;
       
       case BITWISE_AND: 
         return 8;
       case EXCLUSIVE_OR: 
         return 7;
       case BITWISE_OR: 
         return 6;
       case LOGICAL_AND: 
         return 5;
       case LOGICAL_OR: 
         return 4;
       }
       
       throw ContractUtils.unsupported();
     }
     
 
     if ((e instanceof InstanceOfExpression)) {
       return 10;
     }
     
     if ((e instanceof ConditionalExpression)) {
       return 2;
     }
     
     if (((e instanceof AssignmentExpression)) || ((e instanceof LambdaExpression))) {
       return 1;
     }
     
     return 16;
   }
   
   private static BinaryOperatorType getBinaryOperatorType(Expression e) {
     if ((e instanceof BinaryOperatorExpression)) {
       return ((BinaryOperatorExpression)e).getOperator();
     }
     return null;
   }
   
   private static void parenthesizeIfRequired(Expression expression, int minimumPrecedence) {
     if (getPrecedence(expression) < minimumPrecedence) {
       parenthesize(expression);
     }
   }
   
   private static void parenthesize(Expression expression) {
     expression.replaceWith(PARENTHESIZE_FUNCTION);
   }
   
   private static boolean canTypeBeMisinterpretedAsExpression(AstType type) {
     return type instanceof SimpleType;
   }
   
   public Void visitMemberReferenceExpression(MemberReferenceExpression node, Void data)
   {
     parenthesizeIfRequired(node.getTarget(), 16);
     return (Void)super.visitMemberReferenceExpression(node, data);
   }
   
   public Void visitInvocationExpression(InvocationExpression node, Void data)
   {
     parenthesizeIfRequired(node.getTarget(), 16);
     return (Void)super.visitInvocationExpression(node, data);
   }
   
   public Void visitIndexerExpression(IndexerExpression node, Void data)
   {
     parenthesizeIfRequired(node.getTarget(), 16);
     
     if ((node.getTarget() instanceof ArrayCreationExpression)) {
       ArrayCreationExpression arrayCreation = (ArrayCreationExpression)node.getTarget();
       
       if ((this._insertParenthesesForReadability) || (arrayCreation.getInitializer().isNull()))
       {
         parenthesize(arrayCreation);
       }
     }
     
     return (Void)super.visitIndexerExpression(node, data);
   }
   
   public Void visitUnaryOperatorExpression(UnaryOperatorExpression node, Void data)
   {
     Expression child = node.getExpression();
     
     parenthesizeIfRequired(child, getPrecedence(node));
     
     if ((this._insertParenthesesForReadability) && ((child instanceof UnaryOperatorExpression))) {
       parenthesize(child);
     }
     
     return (Void)super.visitUnaryOperatorExpression(node, data);
   }
   
   public Void visitCastExpression(CastExpression node, Void data)
   {
     Expression child = node.getExpression();
     
     parenthesizeIfRequired(child, 14);
     
     if ((child instanceof UnaryOperatorExpression)) {
       UnaryOperatorExpression childUnary = (UnaryOperatorExpression)child;
       
       if ((childUnary.getOperator() != UnaryOperatorType.BITWISE_NOT) && (childUnary.getOperator() != UnaryOperatorType.NOT))
       {
 
         if (canTypeBeMisinterpretedAsExpression(node.getType())) {
           parenthesize(child);
         }
       }
     }
     
     if ((child instanceof PrimitiveExpression)) {
       PrimitiveExpression primitive = (PrimitiveExpression)child;
       Object primitiveValue = primitive.getValue();
       
       if ((primitiveValue instanceof Number)) {
         Number number = (Number)primitiveValue;
         
         if (((primitiveValue instanceof Float)) || ((primitiveValue instanceof Double))) {
           if (number.doubleValue() < 0.0D) {
             parenthesize(child);
           }
         }
         else if (number.longValue() < 0L) {
           parenthesize(child);
         }
       }
     }
     
     return (Void)super.visitCastExpression(node, data);
   }
   
   public Void visitBinaryOperatorExpression(BinaryOperatorExpression node, Void data)
   {
     int precedence = getPrecedence(node);
     
     if ((this._insertParenthesesForReadability) && (precedence < 9)) {
       if (getBinaryOperatorType(node.getLeft()) == node.getOperator()) {
         parenthesizeIfRequired(node.getLeft(), precedence);
       }
       else {
         parenthesizeIfRequired(node.getLeft(), 9);
       }
       parenthesizeIfRequired(node.getRight(), 9);
     }
     else {
       parenthesizeIfRequired(node.getLeft(), precedence);
       parenthesizeIfRequired(node.getRight(), precedence + 1);
     }
     
     return (Void)super.visitBinaryOperatorExpression(node, data);
   }
   
   public Void visitInstanceOfExpression(InstanceOfExpression node, Void data)
   {
     if (this._insertParenthesesForReadability) {
       parenthesizeIfRequired(node.getExpression(), 16);
     }
     else {
       parenthesizeIfRequired(node.getExpression(), 10);
     }
     return (Void)super.visitInstanceOfExpression(node, data);
   }
   
 
 
 
 
 
 
 
 
 
   public Void visitConditionalExpression(ConditionalExpression node, Void data)
   {
     if (this._insertParenthesesForReadability)
     {
       parenthesizeIfRequired(node.getCondition(), 16);
       parenthesizeIfRequired(node.getTrueExpression(), 16);
       parenthesizeIfRequired(node.getFalseExpression(), 16);
     }
     else {
       parenthesizeIfRequired(node.getCondition(), 3);
       parenthesizeIfRequired(node.getTrueExpression(), 2);
       parenthesizeIfRequired(node.getFalseExpression(), 2);
     }
     
     return (Void)super.visitConditionalExpression(node, data);
   }
   
   public Void visitAssignmentExpression(AssignmentExpression node, Void data)
   {
     parenthesizeIfRequired(node.getLeft(), 2);
     
     if (this._insertParenthesesForReadability) {
       parenthesizeIfRequired(node.getRight(), 11);
     }
     else {
       parenthesizeIfRequired(node.getRight(), 1);
     }
     
     return (Void)super.visitAssignmentExpression(node, data);
   }
 }


