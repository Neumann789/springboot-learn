 package com.strobel.decompiler.languages.java.utilities;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.assembler.metadata.CompoundTypeReference;
 import com.strobel.assembler.metadata.ConversionType;
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.MethodBinder.BindResult;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.decompiler.languages.java.ast.AnonymousObjectCreationExpression;
 import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorType;
 import com.strobel.decompiler.languages.java.ast.CastExpression;
 import com.strobel.decompiler.languages.java.ast.ConditionalExpression;
 import com.strobel.decompiler.languages.java.ast.DepthFirstAstVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.FieldDeclaration;
 import com.strobel.decompiler.languages.java.ast.InvocationExpression;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
 import com.strobel.decompiler.languages.java.ast.ParenthesizedExpression;
 import com.strobel.decompiler.languages.java.ast.PrimitiveExpression;
 import com.strobel.decompiler.languages.java.ast.ReturnStatement;
 import com.strobel.decompiler.languages.java.ast.Roles;
 import com.strobel.decompiler.semantics.ResolveResult;
 import com.strobel.functions.Function;
 import java.util.List;
 
 public final class RedundantCastUtility
 {
   @NotNull
   public static List<CastExpression> getRedundantCastsInside(Function<AstNode, ResolveResult> resolver, AstNode site)
   {
     com.strobel.core.VerifyArgument.notNull(resolver, "resolver");
     
     if (site == null) {
       return java.util.Collections.emptyList();
     }
     
     CastCollector visitor = new CastCollector(resolver);
     
     site.acceptVisitor(visitor, null);
     
     return new java.util.ArrayList(visitor.getFoundCasts());
   }
   
   public static boolean isCastRedundant(Function<AstNode, ResolveResult> resolver, CastExpression cast) {
     AstNode parent = skipParenthesesUp(cast.getParent());
     
     if (parent == null) {
       return false;
     }
     
     if ((parent.getRole() == Roles.ARGUMENT) || (parent.isReference())) {
       parent = parent.getParent();
     }
     
     IsRedundantVisitor visitor = new IsRedundantVisitor(resolver, false);
     
     parent.acceptVisitor(visitor, null);
     
     return visitor.isRedundant();
   }
   
   public static void removeCast(CastExpression castExpression) {
     if ((castExpression == null) || (castExpression.isNull())) {
       return;
     }
     
     Expression operand = castExpression.getExpression();
     
     if ((operand instanceof ParenthesizedExpression)) {
       operand = ((ParenthesizedExpression)operand).getExpression();
     }
     
     if ((operand == null) || (operand.isNull())) {
       return;
     }
     
     AstNode toBeReplaced = castExpression;
     AstNode parent = castExpression.getParent();
     
     while ((parent instanceof ParenthesizedExpression)) {
       toBeReplaced = parent;
       parent = parent.getParent();
     }
     
     toBeReplaced.replaceWith(operand);
   }
   
   @com.strobel.annotations.Nullable
   private static Expression removeParentheses(Expression e) {
     Expression result = e;
     
     while ((result instanceof ParenthesizedExpression)) {
       result = ((ParenthesizedExpression)result).getExpression();
     }
     
     return result;
   }
   
   @com.strobel.annotations.Nullable
   private static AstNode skipParenthesesUp(AstNode e) {
     AstNode result = e;
     
     while ((result instanceof ParenthesizedExpression)) {
       result = result.getParent();
     }
     
     return result;
   }
   
   private static class CastCollector extends RedundantCastUtility.IsRedundantVisitor {
     private final java.util.Set<CastExpression> _foundCasts = new java.util.HashSet();
     
     CastCollector(Function<AstNode, ResolveResult> resolver) {
       super(true);
     }
     
     private java.util.Set<CastExpression> getFoundCasts() {
       return this._foundCasts;
     }
     
     public Void visitAnonymousObjectCreationExpression(AnonymousObjectCreationExpression node, Void data)
     {
       for (Expression argument : node.getArguments()) {
         argument.acceptVisitor(this, data);
       }
       return null;
     }
     
     public Void visitTypeDeclaration(com.strobel.decompiler.languages.java.ast.TypeDeclaration typeDeclaration, Void _)
     {
       return null;
     }
     
     public Void visitLocalTypeDeclarationStatement(com.strobel.decompiler.languages.java.ast.LocalTypeDeclarationStatement node, Void data)
     {
       return null;
     }
     
     public Void visitMethodDeclaration(MethodDeclaration node, Void _)
     {
       return null;
     }
     
     public Void visitConstructorDeclaration(com.strobel.decompiler.languages.java.ast.ConstructorDeclaration node, Void _)
     {
       return null;
     }
     
     protected void addToResults(@NotNull CastExpression cast, boolean force)
     {
       if ((force) || (!isTypeCastSemantic(cast))) {
         this._foundCasts.add(cast);
       }
     }
   }
   
   private static class IsRedundantVisitor extends DepthFirstAstVisitor<Void, Void>
   {
     private final boolean _isRecursive;
     private final Function<AstNode, ResolveResult> _resolver;
     private boolean _isRedundant;
     
     IsRedundantVisitor(Function<AstNode, ResolveResult> resolver, boolean recursive) {
       this._isRecursive = recursive;
       this._resolver = resolver;
     }
     
     public final boolean isRedundant() {
       return this._isRedundant;
     }
     
 
 
     protected Void visitChildren(AstNode node, Void data)
     {
       if (this._isRecursive) {
         return (Void)super.visitChildren(node, data);
       }
       return null;
     }
     
     public Void visitAssignmentExpression(AssignmentExpression node, Void data)
     {
       processPossibleTypeCast(node.getRight(), getType(node.getLeft()));
       return (Void)super.visitAssignmentExpression(node, data);
     }
     
     public Void visitVariableDeclaration(com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement node, Void data)
     {
       TypeReference leftType = getType(node.getType());
       
       if (leftType != null) {
         for (com.strobel.decompiler.languages.java.ast.VariableInitializer initializer : node.getVariables()) {
           processPossibleTypeCast(initializer.getInitializer(), leftType);
         }
       }
       
       return (Void)super.visitVariableDeclaration(node, data);
     }
     
     public Void visitFieldDeclaration(FieldDeclaration node, Void data)
     {
       TypeReference leftType = getType(node.getReturnType());
       
       if (leftType != null) {
         for (com.strobel.decompiler.languages.java.ast.VariableInitializer initializer : node.getVariables()) {
           processPossibleTypeCast(initializer.getInitializer(), leftType);
         }
       }
       
       return (Void)super.visitFieldDeclaration(node, data);
     }
     
     public Void visitReturnStatement(ReturnStatement node, Void data)
     {
       MethodDeclaration methodDeclaration = (MethodDeclaration)CollectionUtilities.firstOrDefault(node.getAncestors(MethodDeclaration.class));
       
       if ((methodDeclaration != null) && (!methodDeclaration.isNull())) {
         TypeReference returnType = getType(methodDeclaration.getReturnType());
         Expression returnValue = node.getExpression();
         
         if ((returnType != null) && (returnValue != null) && (!returnValue.isNull())) {
           processPossibleTypeCast(returnValue, returnType);
         }
       }
       
       return (Void)super.visitReturnStatement(node, data);
     }
     
     public Void visitBinaryOperatorExpression(BinaryOperatorExpression node, Void data)
     {
       TypeReference leftType = getType(node.getLeft());
       TypeReference rightType = getType(node.getRight());
       
       processBinaryExpressionOperand(node.getLeft(), rightType, node.getOperator());
       processBinaryExpressionOperand(node.getRight(), leftType, node.getOperator());
       
       return (Void)super.visitBinaryOperatorExpression(node, data);
     }
     
     public Void visitInvocationExpression(InvocationExpression node, Void data)
     {
       super.visitInvocationExpression(node, data);
       processCall(node);
       return null;
     }
     
     public Void visitObjectCreationExpression(com.strobel.decompiler.languages.java.ast.ObjectCreationExpression node, Void data)
     {
       for (Expression argument : node.getArguments()) {
         argument.acceptVisitor(this, data);
       }
       processCall(node);
       return null;
     }
     
     public Void visitAnonymousObjectCreationExpression(AnonymousObjectCreationExpression node, Void data)
     {
       for (Expression argument : node.getArguments()) {
         argument.acceptVisitor(this, data);
       }
       processCall(node);
       return null;
     }
     
     public Void visitCastExpression(CastExpression node, Void data)
     {
       Expression operand = node.getExpression();
       
       if ((operand == null) || (operand.isNull())) {
         return null;
       }
       
       TypeReference topCastType = getType(node);
       
       if (topCastType == null) {
         return null;
       }
       
       Expression e = RedundantCastUtility.removeParentheses(operand);
       
       if ((e instanceof CastExpression)) {
         CastExpression innerCast = (CastExpression)e;
         TypeReference innerCastType = getType(innerCast.getType());
         
         if (innerCastType == null) {
           return null;
         }
         
         Expression innerOperand = innerCast.getExpression();
         TypeReference innerOperandType = getType(innerOperand);
         
         if (!innerCastType.isPrimitive()) {
           if ((innerOperandType != null) && (MetadataHelper.getConversionType(topCastType, innerOperandType) != ConversionType.NONE))
           {
 
             addToResults(innerCast, false);
           }
         }
         else {
           ConversionType valueToInner = MetadataHelper.getNumericConversionType(innerCastType, innerOperandType);
           ConversionType outerToInner = MetadataHelper.getNumericConversionType(innerCastType, topCastType);
           
           if (outerToInner == ConversionType.IDENTITY) {
             if (valueToInner == ConversionType.IDENTITY)
             {
 
 
               addToResults(node, false);
               addToResults(innerCast, true);
 
             }
             else
             {
 
               addToResults(innerCast, true);
             }
           }
           else if (outerToInner == ConversionType.IMPLICIT) {
             ConversionType valueToOuter = MetadataHelper.getNumericConversionType(topCastType, innerOperandType);
             
             if (valueToOuter != ConversionType.NONE)
             {
 
 
 
               addToResults(innerCast, true);
             }
           }
           else if ((valueToInner == ConversionType.IMPLICIT) && (MetadataHelper.getNumericConversionType(topCastType, innerOperandType) == ConversionType.IMPLICIT))
           {
 
             addToResults(innerCast, true);
           }
         }
       }
       else {
         AstNode parent = node.getParent();
         
         if ((parent instanceof ConditionalExpression))
         {
 
 
 
           TypeReference operandType = getType(operand);
           TypeReference conditionalType = getType(parent);
           
           if (!MetadataHelper.isSameType(operandType, conditionalType, true)) {
             if (!checkResolveAfterRemoveCast(parent)) {
               return null;
             }
             
             Expression thenExpression = ((ConditionalExpression)parent).getTrueExpression();
             Expression elseExpression = ((ConditionalExpression)parent).getFalseExpression();
             Expression opposite = thenExpression == node ? elseExpression : thenExpression;
             TypeReference oppositeType = getType(opposite);
             
             if ((oppositeType == null) || (!MetadataHelper.isSameType(conditionalType, oppositeType, true))) {
               return null;
             }
           }
           else if ((topCastType.isPrimitive()) && (!operandType.isPrimitive()))
           {
 
 
 
             return null;
           }
         } else {
           if (((parent instanceof com.strobel.decompiler.languages.java.ast.SynchronizedStatement)) && ((getType(e) instanceof com.strobel.assembler.metadata.PrimitiveType))) {
             return null;
           }
           if (((e instanceof com.strobel.decompiler.languages.java.ast.LambdaExpression)) || ((e instanceof com.strobel.decompiler.languages.java.ast.MethodGroupExpression))) {
             if (((parent instanceof ParenthesizedExpression)) && (parent.getParent() != null) && (parent.getParent().isReference()))
             {
 
 
               return null;
             }
             
             ResolveResult lambdaResult = (ResolveResult)this._resolver.apply(e);
             TypeReference functionalInterfaceType;
             TypeReference functionalInterfaceType;
             if ((lambdaResult != null) && (lambdaResult.getType() != null)) {
               TypeReference asSubType = MetadataHelper.asSubType(lambdaResult.getType(), topCastType);
               
               functionalInterfaceType = asSubType != null ? asSubType : lambdaResult.getType();
 
 
             }
             else
             {
 
 
               com.strobel.assembler.metadata.DynamicCallSite callSite = (com.strobel.assembler.metadata.DynamicCallSite)e.getUserData(Keys.DYNAMIC_CALL_SITE);
               
               if (callSite == null) {
                 return null;
               }
               
               functionalInterfaceType = callSite.getMethodType().getReturnType();
             }
             
             if (!MetadataHelper.isAssignableFrom(topCastType, functionalInterfaceType, false)) {
               return null;
             }
           }
         }
         processAlreadyHasTypeCast(node);
       }
       
       return (Void)super.visitCastExpression(node, data);
     }
     
 
 
 
     protected TypeReference getType(AstNode node)
     {
       ResolveResult result = (ResolveResult)this._resolver.apply(node);
       return result != null ? result.getType() : null;
     }
     
     @NotNull
     protected List<TypeReference> getTypes(AstNodeCollection<? extends AstNode> nodes) {
       if ((nodes == null) || (nodes.isEmpty())) {
         return java.util.Collections.emptyList();
       }
       
       List<TypeReference> types = new java.util.ArrayList();
       
       for (AstNode node : nodes) {
         TypeReference nodeType = getType(node);
         
         if (nodeType == null) {
           return java.util.Collections.emptyList();
         }
         
         types.add(nodeType);
       }
       
       return types;
     }
     
     protected void processPossibleTypeCast(Expression rightExpression, @com.strobel.annotations.Nullable TypeReference leftType) {
       if (leftType == null) {
         return;
       }
       
       Expression r = RedundantCastUtility.removeParentheses(rightExpression);
       
       if ((r instanceof CastExpression)) {
         com.strobel.decompiler.languages.java.ast.AstType castAstType = ((CastExpression)r).getType();
         TypeReference castType = castAstType != null ? castAstType.toTypeReference() : null;
         Expression castOperand = ((CastExpression)r).getExpression();
         
         if ((castOperand != null) && (!castOperand.isNull()) && (castType != null)) {
           TypeReference operandType = getType(castOperand);
           
           if (operandType != null) {
             if (MetadataHelper.isAssignableFrom(leftType, operandType, false)) {
               addToResults((CastExpression)r, false);
             }
             else {
               TypeReference unboxedCastType = MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(castType);
               TypeReference unboxedLeftType = MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(leftType);
               
               if (((castOperand instanceof PrimitiveExpression)) && (TypeUtilities.isValidPrimitiveLiteralAssignment(unboxedCastType, ((PrimitiveExpression)castOperand).getValue())) && (TypeUtilities.isValidPrimitiveLiteralAssignment(unboxedLeftType, ((PrimitiveExpression)castOperand).getValue())))
               {
 
 
                 addToResults((CastExpression)r, true);
               }
             }
           }
         }
       }
     }
     
     protected void addToResults(@NotNull CastExpression cast, boolean force) {
       if ((force) || (!isTypeCastSemantic(cast))) {
         this._isRedundant = true;
       }
     }
     
 
 
 
     protected void processBinaryExpressionOperand(Expression operand, TypeReference otherType, BinaryOperatorType op)
     {
       if ((operand instanceof CastExpression)) {
         CastExpression cast = (CastExpression)operand;
         Expression toCast = cast.getExpression();
         TypeReference castType = getType(cast);
         TypeReference innerType = getType(toCast);
         
         if ((castType != null) && (innerType != null) && (TypeUtilities.isBinaryOperatorApplicable(op, innerType, otherType, false)))
         {
 
 
           addToResults(cast, false);
         }
       }
     }
     
     protected void processCall(@NotNull Expression e)
     {
       AstNodeCollection<Expression> arguments = e.getChildrenByRole(Roles.ARGUMENT);
       
       if (arguments.isEmpty()) {
         return;
       }
       
       MemberReference reference = (MemberReference)e.getUserData(Keys.MEMBER_REFERENCE);
       
       if ((reference == null) && ((e.getParent() instanceof com.strobel.decompiler.languages.java.ast.MemberReferenceExpression))) {
         reference = (MemberReference)e.getParent().getUserData(Keys.MEMBER_REFERENCE);
       }
       
       MethodReference method;
       
       if ((reference instanceof MethodReference)) {
         method = (MethodReference)reference;
       } else {
         return;
       }
       
       MethodReference method;
       Expression target = (Expression)e.getChildByRole(Roles.TARGET_EXPRESSION);
       
       if ((target instanceof com.strobel.decompiler.languages.java.ast.MemberReferenceExpression)) {
         target = (Expression)target.getChildByRole(Roles.TARGET_EXPRESSION);
       }
       
       TypeReference targetType = getType(target);
       
       if (targetType == null) {
         targetType = method.getDeclaringType();
       }
       else if ((!(targetType instanceof com.strobel.assembler.metadata.RawType)) && (MetadataHelper.isRawType(targetType))) {
         targetType = MetadataHelper.eraseRecursive(targetType);
       }
       else {
         TypeReference asSuper = MetadataHelper.asSuper(method.getDeclaringType(), targetType);
         TypeReference asSubType = asSuper != null ? MetadataHelper.asSubType(method.getDeclaringType(), asSuper) : null;
         
         targetType = asSubType != null ? asSubType : targetType;
       }
       
       List<MethodReference> candidates = MetadataHelper.findMethods(targetType, com.strobel.assembler.metadata.MetadataFilters.matchName(method.getName()));
       
 
 
 
       MethodDefinition resolvedMethod = method.resolve();
       List<TypeReference> originalTypes = new java.util.ArrayList();
       List<ParameterDefinition> parameters = method.getParameters();
       Expression lastArgument = (Expression)arguments.lastOrNullObject();
       
       List<TypeReference> newTypes = null;
       int syntheticLeadingCount = 0;
       int syntheticTrailingCount = 0;
       
       for (ParameterDefinition parameter : parameters) {
         if (!parameter.isSynthetic()) break;
         syntheticLeadingCount++;
         originalTypes.add(parameter.getParameterType());
       }
       
 
 
 
 
 
       for (int i = parameters.size() - 1; (i >= 0) && (((ParameterDefinition)parameters.get(i)).isSynthetic()); syntheticTrailingCount++) { i--;
       }
       
       for (Expression argument : arguments) {
         TypeReference argumentType = getType(argument);
         
         if (argumentType == null) {
           return;
         }
         
         originalTypes.add(argumentType);
       }
       
       int realParametersEnd = parameters.size() - syntheticTrailingCount;
       
       for (int i = realParametersEnd; i < parameters.size(); i++) {
         originalTypes.add(((ParameterDefinition)parameters.get(i)).getParameterType());
       }
       
       int i = syntheticLeadingCount;
       
       Expression a = (Expression)arguments.firstOrNullObject();
       for (; (i < realParametersEnd) && (a != null) && (!a.isNull()); 
           i++)
       {
         Expression arg = RedundantCastUtility.removeParentheses(a);
         
         if ((arg instanceof CastExpression))
         {
 
 
           if ((a != lastArgument) || (i != parameters.size() - 1) || (resolvedMethod == null) || (!resolvedMethod.isVarArgs()))
           {
 
 
 
 
 
 
 
 
 
 
 
             CastExpression cast = (CastExpression)arg;
             Expression castOperand = cast.getExpression();
             TypeReference castType = getType(cast);
             TypeReference operandType = getType(castOperand);
             
             if ((castType != null) && (operandType != null))
             {
 
 
               if ((castType.isPrimitive()) && (!operandType.isPrimitive())) {
                 ParameterDefinition p = (ParameterDefinition)parameters.get(i);
                 TypeReference parameterType = p.getParameterType();
                 
                 if (!parameterType.isPrimitive()) {}
 
 
               }
               else
               {
 
 
                 if (newTypes == null) {
                   newTypes = new java.util.ArrayList(originalTypes);
                 }
                 else {
                   newTypes.clear();
                   newTypes.addAll(originalTypes);
                 }
                 
                 newTypes.set(i, operandType);
                 
                 MethodBinder.BindResult result = com.strobel.assembler.metadata.MethodBinder.selectMethod(candidates, newTypes);
                 
                 if ((!result.isFailure()) && (!result.isAmbiguous()))
                 {
 
 
                   boolean sameMethod = com.strobel.core.StringUtilities.equals(method.getErasedSignature(), result.getMethod().getErasedSignature());
                   
 
 
 
                   if (sameMethod) {
                     ParameterDefinition newParameter = (ParameterDefinition)result.getMethod().getParameters().get(i);
                     
                     if (castType.isPrimitive())
                     {
 
 
 
 
                       boolean castNeeded = !MetadataHelper.isSameType(castType, MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(newParameter.getParameterType()));
                       
 
 
 
                       if (castNeeded) {}
 
 
 
 
 
 
 
 
                     }
                     else if (MetadataHelper.isAssignableFrom(newParameter.getParameterType(), castType)) {
                       addToResults(cast, false);
                     }
                   }
                 }
               }
             }
           }
         }
         a = (Expression)a.getNextSibling(Roles.ARGUMENT);
       }
     }
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     protected void processAlreadyHasTypeCast(CastExpression cast)
     {
       AstNode parent = cast.getParent();
       
       while ((parent instanceof ParenthesizedExpression)) {
         parent = parent.getParent();
       }
       
       if ((parent == null) || ((cast.getRole() == Roles.ARGUMENT) && (!(parent instanceof com.strobel.decompiler.languages.java.ast.IndexerExpression))) || ((parent instanceof AssignmentExpression)) || ((parent instanceof ReturnStatement)) || ((parent instanceof CastExpression)) || ((parent instanceof BinaryOperatorExpression)))
       {
 
 
 
 
 
 
 
 
 
         return;
       }
       
       if (isTypeCastSemantic(cast)) {
         return;
       }
       
       TypeReference castTo = getType(cast.getType());
       Expression operand = cast.getExpression();
       
       TypeReference operandType = getType(operand);
       
       if ((castTo == null) || (operandType == null)) {
         return;
       }
       
       TypeReference expectedType = TypeUtilities.getExpectedTypeByParent(this._resolver, cast);
       boolean isCharConversion = (operandType == com.strobel.assembler.metadata.BuiltinTypes.Character ? 1 : 0) ^ (castTo == com.strobel.assembler.metadata.BuiltinTypes.Character ? 1 : 0);
       
       if (expectedType != null) {
         if ((isCharConversion) && (!expectedType.isPrimitive())) {
           return;
         }
         
         operandType = expectedType;
       }
       else if (isCharConversion) {
         return;
       }
       
       if ((operandType == com.strobel.assembler.metadata.BuiltinTypes.Null) && (castTo.isPrimitive())) {
         return;
       }
       
       if (parent.isReference()) {
         if ((operandType.isPrimitive()) && (!castTo.isPrimitive()))
         {
 
 
           return;
         }
         
         TypeReference referenceType = getType(parent);
         
         if ((!operandType.isPrimitive()) && (referenceType != null) && (!isCastRedundantInReferenceExpression(referenceType, operand)))
         {
 
 
           return;
         }
       }
       
       if (arrayAccessAtTheLeftSideOfAssignment(parent)) {
         if ((MetadataHelper.isAssignableFrom(operandType, castTo, false)) && (MetadataHelper.getArrayRank(operandType) == MetadataHelper.getArrayRank(castTo)))
         {
 
           addToResults(cast, false);
         }
       }
       else if (MetadataHelper.isAssignableFrom(castTo, operandType, false)) {
         addToResults(cast, false);
       }
     }
     
     protected boolean arrayAccessAtTheLeftSideOfAssignment(AstNode node) {
       AssignmentExpression assignment = (AssignmentExpression)CollectionUtilities.firstOrDefault(node.getAncestors(AssignmentExpression.class));
       
       if (assignment == null) {
         return false;
       }
       
       Expression left = assignment.getLeft();
       
       return (left.isAncestorOf(node)) && ((left instanceof com.strobel.decompiler.languages.java.ast.IndexerExpression));
     }
     
 
     protected boolean isCastRedundantInReferenceExpression(TypeReference type, Expression operand)
     {
       return false;
     }
     
     protected boolean checkResolveAfterRemoveCast(AstNode parent) {
       AstNode grandParent = parent.getParent();
       
       if ((grandParent == null) || (parent.getRole() != Roles.ARGUMENT)) {
         return true;
       }
       
       TypeReference targetType;
       TypeReference targetType;
       if ((grandParent instanceof InvocationExpression)) {
         targetType = getType(((InvocationExpression)grandParent).getTarget());
       }
       else {
         targetType = getType(grandParent);
       }
       
       if (targetType == null) {
         return false;
       }
       
       Expression expression = (Expression)grandParent.clone();
       AstNodeCollection<Expression> arguments = expression.getChildrenByRole(Roles.ARGUMENT);
       List<TypeReference> argumentTypes = getTypes(arguments);
       
       if (argumentTypes.isEmpty()) {
         return arguments.isEmpty();
       }
       
       MemberReference memberReference = (MemberReference)grandParent.getUserData(Keys.MEMBER_REFERENCE);
       
       if ((!(memberReference instanceof MethodReference)) && (grandParent.getParent() != null)) {
         memberReference = (MemberReference)grandParent.getParent().getUserData(Keys.MEMBER_REFERENCE);
       }
       
       if (!(memberReference instanceof MethodReference)) {
         return false;
       }
       
       MethodReference method = (MethodReference)memberReference;
       MethodDefinition resolvedMethod = method.resolve();
       
       if (resolvedMethod == null) {
         return false;
       }
       
       int argumentIndex = CollectionUtilities.indexOf(grandParent.getChildrenByRole(Roles.ARGUMENT), (Expression)parent);
       Expression toReplace = (Expression)CollectionUtilities.get(arguments, argumentIndex);
       
       if ((toReplace instanceof ConditionalExpression)) {
         Expression trueExpression = ((ConditionalExpression)toReplace).getTrueExpression();
         Expression falseExpression = ((ConditionalExpression)toReplace).getFalseExpression();
         
         if ((trueExpression instanceof CastExpression)) {
           Expression trueOperand = ((CastExpression)trueExpression).getExpression();
           TypeReference operandType = getType(trueOperand);
           
           if (operandType != null) {
             trueExpression.replaceWith(trueOperand);
           }
         }
         else if ((falseExpression instanceof CastExpression)) {
           Expression falseOperand = ((CastExpression)falseExpression).getExpression();
           TypeReference operandType = getType(falseOperand);
           
           if (operandType != null) {
             falseExpression.replaceWith(falseOperand);
           }
         }
         
         TypeReference newArgumentType = getType(toReplace);
         
         if (newArgumentType == null) {
           return false;
         }
         
         argumentTypes.set(argumentIndex, newArgumentType);
       }
       
       List<MethodReference> candidates = MetadataHelper.findMethods(targetType, com.strobel.assembler.metadata.MetadataFilters.matchName(resolvedMethod.getName()));
       
 
 
 
       MethodBinder.BindResult result = com.strobel.assembler.metadata.MethodBinder.selectMethod(candidates, argumentTypes);
       
       return (!result.isFailure()) && (!result.isAmbiguous()) && (com.strobel.core.StringUtilities.equals(resolvedMethod.getErasedSignature(), result.getMethod().getErasedSignature()));
     }
     
 
     public boolean isTypeCastSemantic(CastExpression cast)
     {
       Expression operand = cast.getExpression();
       
       if (operand.isNull()) {
         return false;
       }
       
       if (isInPolymorphicCall(cast)) {
         return true;
       }
       
       TypeReference opType = getType(operand);
       TypeReference castType = getType(cast.getType());
       
       if ((opType == null) || (castType == null)) {
         return false;
       }
       
       if ((castType instanceof com.strobel.assembler.metadata.PrimitiveType)) {
         if ((opType instanceof com.strobel.assembler.metadata.PrimitiveType)) {
           if ((operand instanceof PrimitiveExpression)) {
             TypeReference unboxedCastType = MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(castType);
             TypeReference unboxedOpType = MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(opType);
             
             if ((TypeUtilities.isValidPrimitiveLiteralAssignment(unboxedCastType, ((PrimitiveExpression)operand).getValue())) && (TypeUtilities.isValidPrimitiveLiteralAssignment(unboxedOpType, ((PrimitiveExpression)operand).getValue())))
             {
 
               return false;
             }
           }
           
           ConversionType conversionType = MetadataHelper.getNumericConversionType(castType, opType);
           
           if ((conversionType != ConversionType.IDENTITY) && (conversionType != ConversionType.IMPLICIT))
           {
 
             return true;
           }
         }
         
         TypeReference unboxedOpType = MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(opType);
         
         if (unboxedOpType.isPrimitive()) {
           ConversionType conversionType = MetadataHelper.getNumericConversionType(castType, unboxedOpType);
           
           if ((conversionType != ConversionType.IDENTITY) && (conversionType != ConversionType.IMPLICIT))
           {
 
             return true;
           }
         }
       }
       else if ((castType instanceof com.strobel.assembler.metadata.IGenericInstance)) {
         if ((MetadataHelper.isRawType(opType)) && (!MetadataHelper.isAssignableFrom(castType, opType))) {
           return true;
         }
       }
       else if ((MetadataHelper.isRawType(castType)) && 
         ((opType instanceof com.strobel.assembler.metadata.IGenericInstance)) && (!MetadataHelper.isAssignableFrom(castType, opType))) {
         return true;
       }
       
 
       if (((operand instanceof com.strobel.decompiler.languages.java.ast.LambdaExpression)) || ((operand instanceof com.strobel.decompiler.languages.java.ast.MethodGroupExpression))) {
         com.strobel.assembler.metadata.MetadataParser parser = new com.strobel.assembler.metadata.MetadataParser(com.strobel.assembler.metadata.IMetadataResolver.EMPTY);
         TypeReference serializable = parser.parseTypeDescriptor("java/lang/Serializable");
         
         if ((!castType.isPrimitive()) && (MetadataHelper.isSubType(castType, serializable))) {
           return true;
         }
         
         if ((castType instanceof CompoundTypeReference)) {
           boolean redundant = false;
           
           CompoundTypeReference compoundType = (CompoundTypeReference)castType;
           List<TypeReference> interfaces = compoundType.getInterfaces();
           
           int start = 0;
           TypeReference baseType = compoundType.getBaseType();
           
           if (baseType == null) {
             baseType = (TypeReference)CollectionUtilities.first(interfaces);
             start = 1;
           }
           
           for (int i = start; i < interfaces.size(); i++) {
             TypeReference conjunct = (TypeReference)interfaces.get(i);
             
             if (MetadataHelper.isAssignableFrom(baseType, conjunct)) {
               redundant = true;
               break;
             }
           }
           
           if (!redundant) {
             return true;
           }
         }
       }
       
       AstNode parent = cast.getParent();
       
       while ((parent instanceof ParenthesizedExpression)) {
         parent = parent.getParent();
       }
       
       if ((parent instanceof BinaryOperatorExpression)) {
         BinaryOperatorExpression expression = (BinaryOperatorExpression)parent;
         
         Expression firstOperand = expression.getLeft();
         Expression otherOperand = expression.getRight();
         
         if (otherOperand.isAncestorOf(cast)) {
           Expression temp = otherOperand;
           otherOperand = firstOperand;
           firstOperand = temp;
         }
         
         if ((firstOperand != null) && (otherOperand != null) && (castChangesComparisonSemantics(firstOperand, otherOperand, operand, expression.getOperator())))
         {
 
 
           return true;
         }
       }
       else if (((parent instanceof ConditionalExpression)) && 
         (opType.isPrimitive()) && (!(getType(parent) instanceof com.strobel.assembler.metadata.PrimitiveType))) {
         TypeReference expectedType = TypeUtilities.getExpectedTypeByParent(this._resolver, (Expression)parent);
         
         if ((expectedType != null) && (MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(expectedType).isPrimitive()))
         {
 
           return true;
         }
       }
       
 
       return false;
     }
     
 
 
 
     public boolean isInPolymorphicCall(CastExpression cast)
     {
       Expression operand = cast.getExpression();
       
       if (((operand instanceof InvocationExpression)) || (((operand instanceof com.strobel.decompiler.languages.java.ast.MemberReferenceExpression)) && ((operand.getParent() instanceof InvocationExpression))) || ((operand instanceof com.strobel.decompiler.languages.java.ast.ObjectCreationExpression)))
       {
 
 
         if (isPolymorphicMethod(operand)) {
           return true;
         }
       }
       
       return (cast.getRole() == Roles.ARGUMENT) && (isPolymorphicMethod(RedundantCastUtility.skipParenthesesUp(cast.getParent())));
     }
     
     private static boolean isPolymorphicMethod(AstNode expression)
     {
       if (expression == null) {
         return false;
       }
       
       MemberReference memberReference = (MemberReference)expression.getUserData(Keys.MEMBER_REFERENCE);
       
       if ((memberReference == null) && ((expression.getParent() instanceof com.strobel.decompiler.languages.java.ast.MemberReferenceExpression))) {
         memberReference = (MemberReference)expression.getParent().getUserData(Keys.MEMBER_REFERENCE);
       }
       
       if (memberReference != null) {
         List<com.strobel.assembler.metadata.annotations.CustomAnnotation> annotations = memberReference.getAnnotations();
         
         for (com.strobel.assembler.metadata.annotations.CustomAnnotation annotation : annotations) {
           String typeName = annotation.getAnnotationType().getInternalName();
           
           if (com.strobel.core.StringUtilities.equals(typeName, "java.lang.invoke.MethodHandle.PolymorphicSignature")) {
             return true;
           }
         }
       }
       
       return false;
     }
     
 
 
 
 
     private boolean castChangesComparisonSemantics(Expression operand, Expression otherOperand, Expression toCast, BinaryOperatorType operator)
     {
       TypeReference operandType = getType(operand);
       TypeReference otherType = getType(otherOperand);
       TypeReference castType = getType(toCast);
       
       boolean isPrimitiveComparisonWithoutCast;
       boolean isPrimitiveComparisonWithCast;
       boolean isPrimitiveComparisonWithoutCast;
       if ((operator == BinaryOperatorType.EQUALITY) || (operator == BinaryOperatorType.INEQUALITY))
       {
         boolean isPrimitiveComparisonWithoutCast;
         
 
         if (TypeUtilities.isPrimitive(otherType)) {
           boolean isPrimitiveComparisonWithCast = TypeUtilities.isPrimitiveOrWrapper(operandType);
           isPrimitiveComparisonWithoutCast = TypeUtilities.isPrimitiveOrWrapper(castType);
 
 
         }
         else
         {
 
           boolean isPrimitiveComparisonWithCast = TypeUtilities.isPrimitive(operandType);
           isPrimitiveComparisonWithoutCast = TypeUtilities.isPrimitive(castType);
         }
       }
       else {
         isPrimitiveComparisonWithCast = ((operandType != null) && (operandType.isPrimitive())) || ((otherType != null) && (otherType.isPrimitive()));
         
 
         isPrimitiveComparisonWithoutCast = ((castType != null) && (castType.isPrimitive())) || ((operandType != null) && (operandType.isPrimitive()));
       }
       
 
 
 
 
 
       return isPrimitiveComparisonWithCast != isPrimitiveComparisonWithoutCast;
     }
   }
 }


