 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.ConversionType;
 import com.strobel.assembler.metadata.IMethodSignature;
 import com.strobel.assembler.metadata.JvmType;
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.Predicates;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
 import com.strobel.decompiler.languages.java.ast.AstBuilder;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorType;
 import com.strobel.decompiler.languages.java.ast.CastExpression;
 import com.strobel.decompiler.languages.java.ast.ConditionalExpression;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.ConvertTypeOptions;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.IfElseStatement;
 import com.strobel.decompiler.languages.java.ast.JavaPrimitiveCast;
 import com.strobel.decompiler.languages.java.ast.JavaResolver;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.LambdaExpression;
 import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
 import com.strobel.decompiler.languages.java.ast.PrimitiveExpression;
 import com.strobel.decompiler.languages.java.ast.ReturnStatement;
 import com.strobel.decompiler.languages.java.ast.Roles;
 import com.strobel.decompiler.languages.java.ast.UnaryOperatorExpression;
 import com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement;
 import com.strobel.decompiler.languages.java.ast.VariableInitializer;
 import com.strobel.decompiler.languages.java.utilities.RedundantCastUtility;
 import com.strobel.decompiler.languages.java.utilities.TypeUtilities;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.semantics.ResolveResult;
 import com.strobel.functions.Function;
 
 public class InsertNecessaryConversionsTransform extends ContextTrackingVisitor<Void>
 {
   private static final ConvertTypeOptions NO_IMPORT_OPTIONS = new ConvertTypeOptions();
   static { NO_IMPORT_OPTIONS.setAddImports(false); }
   
   private static final INode TRUE_NODE = new PrimitiveExpression(-34, Boolean.valueOf(true));
   private static final INode FALSE_NODE = new PrimitiveExpression(-34, Boolean.valueOf(false));
   
   private final JavaResolver _resolver;
   
   public InsertNecessaryConversionsTransform(DecompilerContext context)
   {
     super(context);
     this._resolver = new JavaResolver(context);
   }
   
   public Void visitCastExpression(CastExpression node, Void data)
   {
     super.visitCastExpression(node, data);
     
     Expression operand = node.getExpression();
     ResolveResult targetResult = this._resolver.apply(node.getType());
     
     if ((targetResult == null) || (targetResult.getType() == null)) {
       return null;
     }
     
     ResolveResult valueResult = this._resolver.apply(operand);
     
     if ((valueResult == null) || (valueResult.getType() == null)) {
       return null;
     }
     
     ConversionType conversionType = MetadataHelper.getConversionType(targetResult.getType(), valueResult.getType());
     
     if (conversionType == ConversionType.NONE) {
       addCastForAssignment(node.getType(), node.getExpression());
     }
     
     if (RedundantCastUtility.isCastRedundant(this._resolver, node)) {
       RedundantCastUtility.removeCast(node);
     }
     
     return null;
   }
   
   public Void visitMemberReferenceExpression(MemberReferenceExpression node, Void data)
   {
     super.visitMemberReferenceExpression(node, data);
     
     MemberReference member = (MemberReference)node.getUserData(Keys.MEMBER_REFERENCE);
     
     if ((member == null) && (node.getParent() != null) && (node.getRole() == Roles.TARGET_EXPRESSION)) {
       member = (MemberReference)node.getParent().getUserData(Keys.MEMBER_REFERENCE);
     }
     
     if (member == null) {
       return null;
     }
     AstBuilder astBuilder = (AstBuilder)this.context.getUserData(Keys.AST_BUILDER);
     
     if (astBuilder == null) {
       return null;
     }
     
     ResolveResult valueResult = this._resolver.apply(node.getTarget());
     
     TypeReference declaringType = member.getDeclaringType();
     
     if ((valueResult != null) && (valueResult.getType() != null))
     {
 
       if (MetadataHelper.isAssignableFrom(declaringType, valueResult.getType())) {
         return null;
       }
       
       if ((valueResult.getType().isGenericType()) && ((declaringType.isGenericType()) || (MetadataHelper.isRawType(declaringType))))
       {
 
 
         TypeReference asSuper = MetadataHelper.asSuper(declaringType, valueResult.getType());
         
         if (asSuper != null) {
           declaringType = asSuper;
         }
       }
     }
     
     addCastForAssignment(astBuilder.convertType(declaringType, NO_IMPORT_OPTIONS), node.getTarget());
     
     return null;
   }
   
   public Void visitAssignmentExpression(AssignmentExpression node, Void data)
   {
     super.visitAssignmentExpression(node, data);
     
     addCastForAssignment(node.getLeft(), node.getRight());
     
     return null;
   }
   
   public Void visitVariableDeclaration(VariableDeclarationStatement node, Void data)
   {
     super.visitVariableDeclaration(node, data);
     
     for (VariableInitializer initializer : node.getVariables()) {
       addCastForAssignment(node, initializer.getInitializer());
     }
     
     return null;
   }
   
   public Void visitReturnStatement(ReturnStatement node, Void data)
   {
     super.visitReturnStatement(node, data);
     
     AstNode function = (AstNode)com.strobel.core.CollectionUtilities.firstOrDefault(node.getAncestors(), Predicates.or(Predicates.instanceOf(MethodDeclaration.class), Predicates.instanceOf(LambdaExpression.class)));
     
 
 
 
 
 
 
     if (function == null) {
       return null;
     }
     
     AstType left;
     AstType left;
     if ((function instanceof MethodDeclaration)) {
       left = ((MethodDeclaration)function).getReturnType();
     }
     else {
       TypeReference expectedType = TypeUtilities.getExpectedTypeByParent(this._resolver, (Expression)function);
       
       if (expectedType == null) {
         return null;
       }
       
       AstBuilder astBuilder = (AstBuilder)this.context.getUserData(Keys.AST_BUILDER);
       
       if (astBuilder == null) {
         return null;
       }
       
       IMethodSignature method = TypeUtilities.getLambdaSignature((LambdaExpression)function);
       
       if (method == null) {
         return null;
       }
       
       left = astBuilder.convertType(method.getReturnType(), NO_IMPORT_OPTIONS);
     }
     
     Expression right = node.getExpression();
     
     addCastForAssignment(left, right);
     
     return null;
   }
   
   private boolean addCastForAssignment(AstNode left, final Expression right) {
     final ResolveResult targetResult = this._resolver.apply(left);
     
     if ((targetResult == null) || (targetResult.getType() == null)) {
       return false;
     }
     
     ResolveResult valueResult = this._resolver.apply(right);
     
     if ((valueResult == null) || (valueResult.getType() == null)) {
       return false;
     }
     
     TypeReference unboxedTargetType = MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(targetResult.getType());
     
     if (((right instanceof PrimitiveExpression)) && (TypeUtilities.isValidPrimitiveLiteralAssignment(unboxedTargetType, ((PrimitiveExpression)right).getValue())))
     {
 
       return false;
     }
     
     ConversionType conversionType = MetadataHelper.getConversionType(targetResult.getType(), valueResult.getType());
     
     AstNode replacement = null;
     
     if ((conversionType == ConversionType.EXPLICIT) || (conversionType == ConversionType.EXPLICIT_TO_UNBOXED)) {
       AstBuilder astBuilder = (AstBuilder)this.context.getUserData(Keys.AST_BUILDER);
       
       if (astBuilder == null) {
         return false;
       }
       
       ConvertTypeOptions convertTypeOptions = new ConvertTypeOptions();
       
       convertTypeOptions.setAllowWildcards(false);
       
       final AstType castToType = astBuilder.convertType(targetResult.getType(), convertTypeOptions);
       
       replacement = right.replaceWith(new Function()
       {
         public Expression apply(AstNode e)
         {
           return new CastExpression(castToType, right);
         }
         
       });
     }
     else if (conversionType == ConversionType.NONE) {
       if ((valueResult.getType().getSimpleType() == JvmType.Boolean) && (targetResult.getType().getSimpleType() != JvmType.Boolean) && (targetResult.getType().getSimpleType().isNumeric()))
       {
 
 
         replacement = convertBooleanToNumeric(right);
         
         if (targetResult.getType().getSimpleType().bitWidth() < 32) {
           final AstBuilder astBuilder = (AstBuilder)this.context.getUserData(Keys.AST_BUILDER);
           
           if (astBuilder != null) {
             replacement = replacement.replaceWith(new Function()
             {
               public AstNode apply(AstNode input)
               {
                 return new CastExpression(astBuilder.convertType(targetResult.getType()), (Expression)input);
               }
               
             });
           }
         }
       }
       else if ((targetResult.getType().getSimpleType() == JvmType.Boolean) && (valueResult.getType().getSimpleType() != JvmType.Boolean) && (valueResult.getType().getSimpleType().isNumeric()))
       {
 
 
         replacement = convertNumericToBoolean(right, valueResult.getType());
       }
       else {
         final AstBuilder astBuilder = (AstBuilder)this.context.getUserData(Keys.AST_BUILDER);
         
         if (astBuilder != null) {
           replacement = right.replaceWith(new Function()
           {
             public AstNode apply(AstNode input)
             {
               return new CastExpression(astBuilder.convertType(com.strobel.assembler.metadata.BuiltinTypes.Object), right);
             }
           });
         }
       }
     }
     
 
     if (replacement != null) {
       recurse(replacement);
       return true;
     }
     
     return false;
   }
   
   public Void visitUnaryOperatorExpression(UnaryOperatorExpression node, Void data)
   {
     super.visitUnaryOperatorExpression(node, data);
     
     switch (node.getOperator()) {
     case NOT: 
       final Expression operand = node.getExpression();
       ResolveResult result = this._resolver.apply(operand);
       
       if ((result != null) && (result.getType() != null) && (!TypeUtilities.isBoolean(result.getType())) && (MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(result.getType()).getSimpleType().isNumeric()))
       {
 
 
 
         final TypeReference comparandType = MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(result.getType());
         
         operand.replaceWith(new Function()
         {
           public AstNode apply(AstNode input)
           {
             return new BinaryOperatorExpression(operand, BinaryOperatorType.INEQUALITY, new PrimitiveExpression(-34, JavaPrimitiveCast.cast(comparandType.getSimpleType(), Integer.valueOf(0))));
           }
         });
       }
       
 
 
 
       break;
     }
     
     
 
 
     return null;
   }
   
   public Void visitBinaryOperatorExpression(BinaryOperatorExpression node, Void data)
   {
     super.visitBinaryOperatorExpression(node, data);
     
     switch (node.getOperator()) {
     case EQUALITY: 
     case INEQUALITY: 
     case GREATER_THAN: 
     case GREATER_THAN_OR_EQUAL: 
     case LESS_THAN: 
     case LESS_THAN_OR_EQUAL: 
     case ADD: 
     case SUBTRACT: 
     case MULTIPLY: 
     case DIVIDE: 
     case MODULUS: 
     case SHIFT_LEFT: 
     case SHIFT_RIGHT: 
     case UNSIGNED_SHIFT_RIGHT: 
       Expression left = node.getLeft();
       Expression right = node.getRight();
       
       ResolveResult leftResult = this._resolver.apply(left);
       ResolveResult rightResult = this._resolver.apply(right);
       
       if ((leftResult != null) && (rightResult != null) && ((TypeUtilities.isBoolean(leftResult.getType()) ^ TypeUtilities.isBoolean(rightResult.getType()))))
       {
 
 
         if (TypeUtilities.isArithmetic(rightResult.getType())) {
           convertBooleanToNumeric(left);
         }
         else if (TypeUtilities.isArithmetic(leftResult.getType())) {
           convertBooleanToNumeric(right);
         }
       }
       
 
 
       break;
     case BITWISE_AND: 
     case BITWISE_OR: 
     case EXCLUSIVE_OR: 
       Expression left = node.getLeft();
       Expression right = node.getRight();
       
       ResolveResult leftResult = this._resolver.apply(left);
       ResolveResult rightResult = this._resolver.apply(right);
       
       if ((leftResult != null) && (leftResult.getType() != null) && (rightResult != null) && (rightResult.getType() != null) && ((TypeUtilities.isBoolean(leftResult.getType()) ^ TypeUtilities.isBoolean(rightResult.getType()))))
       {
 
 
 
 
         if ((TypeUtilities.isBoolean(leftResult.getType())) && (TypeUtilities.isArithmetic(rightResult.getType())))
         {
 
           TypeReference comparandType = MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(rightResult.getType());
           
           if (TRUE_NODE.matches(left)) {
             ((PrimitiveExpression)left).setValue(JavaPrimitiveCast.cast(comparandType.getSimpleType(), Integer.valueOf(1)));
           }
           else if (FALSE_NODE.matches(left)) {
             ((PrimitiveExpression)left).setValue(JavaPrimitiveCast.cast(comparandType.getSimpleType(), Integer.valueOf(0)));
           }
           else {
             convertBooleanToNumeric(left);
           }
         }
         else if (TypeUtilities.isArithmetic(leftResult.getType())) {
           TypeReference comparandType = MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(leftResult.getType());
           
           if (TRUE_NODE.matches(right)) {
             ((PrimitiveExpression)right).setValue(JavaPrimitiveCast.cast(comparandType.getSimpleType(), Integer.valueOf(1)));
           }
           else if (FALSE_NODE.matches(right)) {
             ((PrimitiveExpression)right).setValue(JavaPrimitiveCast.cast(comparandType.getSimpleType(), Integer.valueOf(0)));
           }
           else {
             convertBooleanToNumeric(right);
           }
         }
       }
       else {
         TypeReference expectedType = TypeUtilities.getExpectedTypeByParent(this._resolver, node);
         
         if ((expectedType != null) && (TypeUtilities.isBoolean(expectedType))) {
           ResolveResult result = this._resolver.apply(node);
           
           if ((result != null) && (result.getType() != null) && (TypeUtilities.isArithmetic(result.getType())))
           {
 
 
             convertNumericToBoolean(node, result.getType());
           }
         }
       }
       
       break;
     }
     
     return null;
   }
   
   public Void visitIfElseStatement(IfElseStatement node, Void data)
   {
     super.visitIfElseStatement(node, data);
     
     Expression condition = node.getCondition();
     ResolveResult conditionResult = this._resolver.apply(condition);
     
     if ((conditionResult != null) && (TypeUtilities.isArithmetic(conditionResult.getType())))
     {
 
       convertNumericToBoolean(condition, conditionResult.getType());
     }
     
     return null;
   }
   
   public Void visitConditionalExpression(ConditionalExpression node, Void data)
   {
     super.visitConditionalExpression(node, data);
     
     Expression condition = node.getCondition();
     ResolveResult conditionResult = this._resolver.apply(condition);
     
     if ((conditionResult != null) && (TypeUtilities.isArithmetic(conditionResult.getType())))
     {
 
       convertNumericToBoolean(condition, conditionResult.getType());
     }
     
     return null;
   }
   
   private Expression convertNumericToBoolean(final Expression node, final TypeReference type) {
     (Expression)node.replaceWith(new Function()
     {
       public Expression apply(AstNode input)
       {
         return new BinaryOperatorExpression(node, BinaryOperatorType.INEQUALITY, new PrimitiveExpression(-34, JavaPrimitiveCast.cast(MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(type).getSimpleType(), Integer.valueOf(0))));
       }
     });
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
   private Expression convertBooleanToNumeric(Expression operand)
   {
     Expression e = operand;
     boolean invert;
     final boolean invert; if (((e instanceof UnaryOperatorExpression)) && (((UnaryOperatorExpression)e).getOperator() == com.strobel.decompiler.languages.java.ast.UnaryOperatorType.NOT))
     {
 
       Expression inner = ((UnaryOperatorExpression)e).getExpression();
       
       inner.remove();
       e.replaceWith(inner);
       e = inner;
       invert = true;
     }
     else {
       invert = false;
     }
     
     (Expression)e.replaceWith(new Function()
     {
       public AstNode apply(AstNode input)
       {
         return new ConditionalExpression((Expression)input, new PrimitiveExpression(-34, Integer.valueOf(invert ? 0 : 1)), new PrimitiveExpression(-34, Integer.valueOf(invert ? 1 : 0)));
       }
     });
   }
   
 
 
 
 
   private void recurse(AstNode replacement)
   {
     AstNode parent = replacement.getParent();
     
     if (parent != null) {
       parent.acceptVisitor(this, null);
     }
     else {
       replacement.acceptVisitor(this, null);
     }
   }
 }


