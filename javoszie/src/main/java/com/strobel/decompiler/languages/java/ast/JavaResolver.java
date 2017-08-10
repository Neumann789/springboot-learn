 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.assembler.metadata.BuiltinTypes;
 import com.strobel.assembler.metadata.DynamicCallSite;
 import com.strobel.assembler.metadata.FieldDefinition;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.IMetadataResolver;
 import com.strobel.assembler.metadata.IMethodSignature;
 import com.strobel.assembler.metadata.JvmType;
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.MetadataSystem;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.assembler.metadata.VariableDefinition;
 import com.strobel.core.Comparer;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.ast.Variable;
 import com.strobel.decompiler.semantics.ResolveResult;
 
 public class JavaResolver implements com.strobel.functions.Function<AstNode, ResolveResult>
 {
   private final DecompilerContext _context;
   
   public JavaResolver(DecompilerContext context)
   {
     this._context = ((DecompilerContext)VerifyArgument.notNull(context, "context"));
   }
   
   public ResolveResult apply(AstNode input)
   {
     return (ResolveResult)input.acceptVisitor(new ResolveVisitor(this._context), null);
   }
   
   private static final class ResolveVisitor extends ContextTrackingVisitor<ResolveResult> {
     protected ResolveVisitor(DecompilerContext context) {
       super();
     }
     
     public ResolveResult visitVariableDeclaration(VariableDeclarationStatement node, Void data)
     {
       return JavaResolver.resolveType(node.getType());
     }
     
     public ResolveResult visitVariableInitializer(VariableInitializer node, Void data)
     {
       return (ResolveResult)node.getInitializer().acceptVisitor(this, data);
     }
     
 
 
 
 
 
 
 
     public ResolveResult visitObjectCreationExpression(ObjectCreationExpression node, Void _)
     {
       return (ResolveResult)node.getType().acceptVisitor(this, _);
     }
     
     public ResolveResult visitAnonymousObjectCreationExpression(AnonymousObjectCreationExpression node, Void _)
     {
       ResolveResult result = JavaResolver.resolveTypeFromMember((MemberReference)node.getUserData(Keys.MEMBER_REFERENCE));
       
       if (result != null) {
         return result;
       }
       
       return (ResolveResult)node.getType().acceptVisitor(this, _);
     }
     
     public ResolveResult visitComposedType(ComposedType node, Void _)
     {
       return JavaResolver.resolveType(node.toTypeReference());
     }
     
     public ResolveResult visitSimpleType(SimpleType node, Void _)
     {
       return JavaResolver.resolveType(node.toTypeReference());
     }
     
     public ResolveResult visitThisReferenceExpression(ThisReferenceExpression node, Void data)
     {
       if (node.getTarget().isNull()) {
         return JavaResolver.resolveType((TypeReference)node.getUserData(Keys.TYPE_REFERENCE));
       }
       return (ResolveResult)node.getTarget().acceptVisitor(this, data);
     }
     
     public ResolveResult visitSuperReferenceExpression(SuperReferenceExpression node, Void data)
     {
       if (node.getTarget().isNull()) {
         return JavaResolver.resolveType((TypeReference)node.getUserData(Keys.TYPE_REFERENCE));
       }
       return (ResolveResult)node.getTarget().acceptVisitor(this, data);
     }
     
     public ResolveResult visitTypeReference(TypeReferenceExpression node, Void _)
     {
       return JavaResolver.resolveType((TypeReference)node.getType().getUserData(Keys.TYPE_REFERENCE));
     }
     
     public ResolveResult visitWildcardType(WildcardType node, Void _)
     {
       return JavaResolver.resolveType(node.toTypeReference());
     }
     
     public ResolveResult visitIdentifier(Identifier node, Void _)
     {
       ResolveResult result = JavaResolver.resolveTypeFromMember((MemberReference)node.getUserData(Keys.MEMBER_REFERENCE));
       
       if (result != null) {
         return result;
       }
       
       return JavaResolver.resolveTypeFromVariable((Variable)node.getUserData(Keys.VARIABLE));
     }
     
     public ResolveResult visitIdentifierExpression(IdentifierExpression node, Void data)
     {
       ResolveResult result = JavaResolver.resolveTypeFromMember((MemberReference)node.getUserData(Keys.MEMBER_REFERENCE));
       
       if (result != null) {
         return result;
       }
       
       Variable variable = (Variable)node.getUserData(Keys.VARIABLE);
       
       if (variable == null) {
         return null;
       }
       
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
       result = JavaResolver.resolveTypeFromVariable(variable);
       
       if (result != null) {
         return result;
       }
       
       return (ResolveResult)super.visitIdentifierExpression(node, data);
     }
     
     protected ResolveResult resolveLambda(AstNode node) {
       TypeReference lambdaType = (TypeReference)node.getUserData(Keys.TYPE_REFERENCE);
       
       if (lambdaType != null) {
         return JavaResolver.resolveType(lambdaType);
       }
       
       DynamicCallSite callSite = (DynamicCallSite)node.getUserData(Keys.DYNAMIC_CALL_SITE);
       
       if (callSite != null) {
         return JavaResolver.resolveType(callSite.getMethodType().getReturnType());
       }
       
       return null;
     }
     
     public ResolveResult visitMethodGroupExpression(MethodGroupExpression node, Void data)
     {
       return resolveLambda(node);
     }
     
     public ResolveResult visitLambdaExpression(LambdaExpression node, Void data)
     {
       return resolveLambda(node);
     }
     
     public ResolveResult visitMemberReferenceExpression(MemberReferenceExpression node, Void _)
     {
       ResolveResult targetResult = (ResolveResult)node.getTarget().acceptVisitor(this, _);
       
       MemberReference memberReference = (MemberReference)node.getUserData(Keys.MEMBER_REFERENCE);
       
       if (memberReference == null) {
         if ((StringUtilities.equals(node.getMemberName(), "length")) && 
           (targetResult != null) && (targetResult.getType() != null) && (targetResult.getType().isArray()))
         {
 
 
           return new ResolveResult(BuiltinTypes.Integer);
         }
         
         if ((node.getParent() instanceof InvocationExpression)) {
           memberReference = (MemberReference)node.getParent().getUserData(Keys.MEMBER_REFERENCE);
         }
       }
       else if ((targetResult != null) && (targetResult.getType() != null))
       {
 
         if ((memberReference instanceof FieldReference)) {
           FieldDefinition resolvedField = ((FieldReference)memberReference).resolve();
           
           memberReference = MetadataHelper.asMemberOf(resolvedField != null ? resolvedField : (FieldReference)memberReference, targetResult.getType());
 
         }
         else
         {
 
           MethodDefinition resolvedMethod = ((MethodReference)memberReference).resolve();
           
           memberReference = MetadataHelper.asMemberOf(resolvedMethod != null ? resolvedMethod : (MethodReference)memberReference, targetResult.getType());
         }
       }
       
 
 
 
       return JavaResolver.resolveTypeFromMember(memberReference);
     }
     
     public ResolveResult visitInvocationExpression(InvocationExpression node, Void _)
     {
       ResolveResult result = JavaResolver.resolveTypeFromMember((MemberReference)node.getUserData(Keys.MEMBER_REFERENCE));
       
       if (result != null) {
         return result;
       }
       
       return (ResolveResult)node.getTarget().acceptVisitor(this, _);
     }
     
     protected ResolveResult visitChildren(AstNode node, Void _)
     {
       ResolveResult result = null;
       
       AstNode next;
       
       for (AstNode child = node.getFirstChild(); child != null; child = next)
       {
 
 
         next = child.getNextSibling();
         
         if (!(child instanceof JavaTokenNode))
         {
 
 
           ResolveResult childResult = (ResolveResult)child.acceptVisitor(this, _);
           
           if (childResult == null) {
             return null;
           }
           if (result == null) {
             result = childResult;
           }
           else if ((!result.isCompileTimeConstant()) || (!childResult.isCompileTimeConstant()) || (!Comparer.equals(result.getConstantValue(), childResult.getConstantValue())))
           {
 
 
 
 
 
 
             TypeReference commonSuperType = doBinaryPromotion(result, childResult);
             
             if (commonSuperType != null) {
               result = new ResolveResult(commonSuperType);
             }
             else {
               return null;
             }
           }
         }
       }
       return null;
     }
     
     private TypeReference doBinaryPromotion(ResolveResult left, ResolveResult right) {
       TypeReference leftType = left.getType();
       TypeReference rightType = right.getType();
       
       if (leftType == null) {
         return rightType;
       }
       
       if (rightType == null) {
         return leftType;
       }
       
       if (StringUtilities.equals(leftType.getInternalName(), "java/lang/String")) {
         return leftType;
       }
       
       if (StringUtilities.equals(rightType.getInternalName(), "java/lang/String")) {
         return rightType;
       }
       
       return MetadataHelper.findCommonSuperType(leftType, rightType);
     }
     
     private TypeReference doBinaryPromotionStrict(ResolveResult left, ResolveResult right) {
       if ((left == null) || (right == null)) {
         return null;
       }
       
       TypeReference leftType = left.getType();
       TypeReference rightType = right.getType();
       
       if ((leftType == null) || (rightType == null)) {
         return null;
       }
       
       leftType = MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(leftType);
       rightType = MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(rightType);
       
       if (StringUtilities.equals(leftType.getInternalName(), "java/lang/String")) {
         return leftType;
       }
       
       if (StringUtilities.equals(rightType.getInternalName(), "java/lang/String")) {
         return rightType;
       }
       
       return MetadataHelper.findCommonSuperType(leftType, rightType);
     }
     
     public ResolveResult visitPrimitiveExpression(PrimitiveExpression node, Void _)
     {
       String literalValue = node.getLiteralValue();
       Object value = node.getValue();
       
       TypeReference primitiveType;
       TypeReference primitiveType;
       if (((value instanceof String)) || ((value == null) && (literalValue != null))) {
         TypeDefinition currentType = this.context.getCurrentType();
         IMetadataResolver resolver = currentType != null ? currentType.getResolver() : MetadataSystem.instance();
         
         primitiveType = resolver.lookupType("java/lang/String");
       } else { TypeReference primitiveType;
         if ((value instanceof Number)) { TypeReference primitiveType;
           if ((value instanceof Byte)) {
             primitiveType = BuiltinTypes.Byte;
           } else { TypeReference primitiveType;
             if ((value instanceof Short)) {
               primitiveType = BuiltinTypes.Short;
             } else { TypeReference primitiveType;
               if ((value instanceof Integer)) {
                 primitiveType = BuiltinTypes.Integer;
               } else { TypeReference primitiveType;
                 if ((value instanceof Long)) {
                   primitiveType = BuiltinTypes.Long;
                 } else { TypeReference primitiveType;
                   if ((value instanceof Float)) {
                     primitiveType = BuiltinTypes.Float;
                   } else { TypeReference primitiveType;
                     if ((value instanceof Double)) {
                       primitiveType = BuiltinTypes.Double;
                     }
                     else
                       primitiveType = null;
                   }
                 } } } } } else { TypeReference primitiveType;
           if ((value instanceof Character)) {
             primitiveType = BuiltinTypes.Character;
           } else { TypeReference primitiveType;
             if ((value instanceof Boolean)) {
               primitiveType = BuiltinTypes.Boolean;
             }
             else
               primitiveType = null;
           }
         } }
       if (primitiveType == null) {
         return null;
       }
       
       return new JavaResolver.PrimitiveResolveResult(primitiveType, value != null ? value : literalValue, null);
     }
     
 
 
 
     public ResolveResult visitClassOfExpression(ClassOfExpression node, Void data)
     {
       TypeReference type = (TypeReference)node.getType().getUserData(Keys.TYPE_REFERENCE);
       
       if (type == null) {
         return null;
       }
       
       if (BuiltinTypes.Class.isGenericType()) {
         return new ResolveResult(BuiltinTypes.Class.makeGenericType(new TypeReference[] { type }));
       }
       
       return new ResolveResult(BuiltinTypes.Class);
     }
     
     public ResolveResult visitCastExpression(CastExpression node, Void data)
     {
       ResolveResult childResult = (ResolveResult)node.getExpression().acceptVisitor(this, data);
       ResolveResult typeResult = JavaResolver.resolveType(node.getType());
       
       if (typeResult == null) {
         return childResult;
       }
       
       TypeReference resolvedType = typeResult.getType();
       
       if (resolvedType != null) {
         if ((resolvedType.isPrimitive()) && (childResult != null) && (childResult.isCompileTimeConstant()))
         {
 
 
           return new JavaResolver.PrimitiveResolveResult(resolvedType, JavaPrimitiveCast.cast(resolvedType.getSimpleType(), childResult.getConstantValue()), null);
         }
         
 
 
 
         return new ResolveResult(resolvedType);
       }
       
       return typeResult;
     }
     
     public ResolveResult visitNullReferenceExpression(NullReferenceExpression node, Void data)
     {
       return new ResolveResult(BuiltinTypes.Null);
     }
     
     public ResolveResult visitBinaryOperatorExpression(BinaryOperatorExpression node, Void data)
     {
       ResolveResult leftResult = (ResolveResult)node.getLeft().acceptVisitor(this, data);
       ResolveResult rightResult = (ResolveResult)node.getRight().acceptVisitor(this, data);
       
       if ((leftResult == null) || (rightResult == null)) {
         return null;
       }
       
       TypeReference leftType = leftResult.getType();
       TypeReference rightType = rightResult.getType();
       
       if ((leftType == null) || (rightType == null)) {
         return null;
       }
       
       TypeReference operandType = doBinaryPromotionStrict(leftResult, rightResult);
       
       if (operandType == null) {
         return null;
       }
       
       TypeReference resultType;
       
       switch (JavaResolver.1.$SwitchMap$com$strobel$decompiler$languages$java$ast$BinaryOperatorType[node.getOperator().ordinal()]) {
       case 1: 
       case 2: 
       case 3: 
       case 4: 
       case 5: 
       case 6: 
       case 7: 
       case 8: 
         resultType = BuiltinTypes.Boolean;
         break;
       
 
       default: 
         switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[operandType.getSimpleType().ordinal()]) {
         case 1: 
         case 2: 
         case 3: 
           resultType = BuiltinTypes.Integer;
           break;
         default: 
           resultType = operandType;
         }
         
         break;
       }
       
       if ((leftResult.isCompileTimeConstant()) && (rightResult.isCompileTimeConstant()) && 
         (operandType.isPrimitive())) {
         Object result = JavaResolver.BinaryOperations.doBinary(node.getOperator(), operandType.getSimpleType(), leftResult.getConstantValue(), rightResult.getConstantValue());
         
 
 
 
 
 
         if (result != null) {
           return new JavaResolver.PrimitiveResolveResult(resultType, result, null);
         }
       }
       
 
       return new ResolveResult(resultType);
     }
     
     public ResolveResult visitInstanceOfExpression(InstanceOfExpression node, Void data)
     {
       ResolveResult childResult = (ResolveResult)node.getExpression().acceptVisitor(this, data);
       
       if (childResult == null) {
         return new ResolveResult(BuiltinTypes.Boolean);
       }
       
       TypeReference childType = childResult.getType();
       ResolveResult typeResult = JavaResolver.resolveType(node.getType());
       
       if ((childType == null) || (typeResult == null) || (typeResult.getType() == null)) {
         return new ResolveResult(BuiltinTypes.Boolean);
       }
       
       return new JavaResolver.PrimitiveResolveResult(BuiltinTypes.Boolean, Boolean.valueOf(MetadataHelper.isSubType(typeResult.getType(), childType)), null);
     }
     
 
 
 
     public ResolveResult visitIndexerExpression(IndexerExpression node, Void data)
     {
       ResolveResult childResult = (ResolveResult)node.getTarget().acceptVisitor(this, data);
       
       if ((childResult == null) || (childResult.getType() == null) || (!childResult.getType().isArray())) {
         return null;
       }
       
       TypeReference elementType = childResult.getType().getElementType();
       
       if (elementType == null) {
         return null;
       }
       
       return new ResolveResult(elementType);
     }
     
     public ResolveResult visitUnaryOperatorExpression(UnaryOperatorExpression node, Void data)
     {
       ResolveResult childResult = (ResolveResult)node.getExpression().acceptVisitor(this, data);
       
       if ((childResult == null) || (childResult.getType() == null)) {
         return null;
       }
       
       TypeReference resultType;
       
       switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[childResult.getType().getSimpleType().ordinal()]) {
       case 1: 
       case 2: 
       case 3: 
       case 4: 
         resultType = BuiltinTypes.Integer;
         break;
       
       default: 
         resultType = childResult.getType();
       }
       
       if (childResult.isCompileTimeConstant()) {
         Object resultValue = JavaResolver.UnaryOperations.doUnary(node.getOperator(), childResult.getConstantValue());
         
         if (resultValue != null) {
           return new JavaResolver.PrimitiveResolveResult(resultType, resultValue, null);
         }
       }
       
       return new ResolveResult(resultType);
     }
     
     public ResolveResult visitConditionalExpression(ConditionalExpression node, Void data)
     {
       ResolveResult conditionResult = (ResolveResult)node.getCondition().acceptVisitor(this, data);
       
       if ((conditionResult != null) && (conditionResult.isCompileTimeConstant()))
       {
 
         if (Boolean.TRUE.equals(conditionResult.getConstantValue())) {
           return (ResolveResult)node.getTrueExpression().acceptVisitor(this, data);
         }
         
         if (Boolean.FALSE.equals(conditionResult.getConstantValue())) {
           return (ResolveResult)node.getFalseExpression().acceptVisitor(this, data);
         }
       }
       
       ResolveResult leftResult = (ResolveResult)node.getTrueExpression().acceptVisitor(this, data);
       
       if ((leftResult == null) || (leftResult.getType() == null)) {
         return null;
       }
       
       ResolveResult rightResult = (ResolveResult)node.getFalseExpression().acceptVisitor(this, data);
       
       if ((rightResult == null) || (rightResult.getType() == null)) {
         return null;
       }
       
       TypeReference resultType = MetadataHelper.findCommonSuperType(leftResult.getType(), rightResult.getType());
       
 
 
 
       if (resultType != null) {
         if ((leftResult.getType().isPrimitive()) || (rightResult.getType().isPrimitive())) {
           return new ResolveResult(MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(resultType));
         }
         return new ResolveResult(resultType);
       }
       
       return null;
     }
     
     public ResolveResult visitArrayCreationExpression(ArrayCreationExpression node, Void data)
     {
       TypeReference elementType = node.getType().toTypeReference();
       
       if (elementType == null) {
         return null;
       }
       
       int rank = node.getDimensions().size() + node.getAdditionalArraySpecifiers().size();
       
       TypeReference arrayType = elementType;
       
       for (int i = 0; i < rank; i++) {
         arrayType = arrayType.makeArrayType();
       }
       
       return new ResolveResult(arrayType);
     }
     
     public ResolveResult visitAssignmentExpression(AssignmentExpression node, Void data)
     {
       ResolveResult leftResult = (ResolveResult)node.getLeft().acceptVisitor(this, data);
       
       if ((leftResult != null) && (leftResult.getType() != null)) {
         return new ResolveResult(leftResult.getType());
       }
       
       return null;
     }
     
     public ResolveResult visitParenthesizedExpression(ParenthesizedExpression node, Void data)
     {
       return (ResolveResult)node.getExpression().acceptVisitor(this, data);
     }
   }
   
   private static ResolveResult resolveTypeFromVariable(Variable variable) {
     if (variable == null) {
       return null;
     }
     
     TypeReference type = variable.getType();
     
     if (type == null) {
       type = variable.isParameter() ? variable.getOriginalParameter().getParameterType() : variable.getOriginalVariable().getVariableType();
     }
     
 
     if (type != null) {
       return new ResolveResult(type);
     }
     
     return null;
   }
   
   private static ResolveResult resolveType(AstType type) {
     if ((type == null) || (type.isNull())) {
       return null;
     }
     
     return resolveType(type.toTypeReference());
   }
   
   private static ResolveResult resolveType(TypeReference type) {
     return type == null ? null : new ResolveResult(type);
   }
   
   private static ResolveResult resolveTypeFromMember(MemberReference member) {
     if (member == null) {
       return null;
     }
     
     if ((member instanceof FieldReference)) {
       return new ResolveResult(((FieldReference)member).getFieldType());
     }
     
     if ((member instanceof MethodReference)) {
       MethodReference method = (MethodReference)member;
       
       if (method.isConstructor()) {
         return new ResolveResult(method.getDeclaringType());
       }
       
       return new ResolveResult(method.getReturnType());
     }
     
     return null;
   }
   
   private static final class PrimitiveResolveResult extends ResolveResult {
     private final Object _value;
     
     private PrimitiveResolveResult(TypeReference type, Object value) {
       super();
       this._value = value;
     }
     
     public boolean isCompileTimeConstant()
     {
       return true;
     }
     
     public Object getConstantValue()
     {
       return this._value;
     }
   }
   
   private static final class BinaryOperations {
     static Object doBinary(BinaryOperatorType operator, JvmType type, Object left, Object right) {
       switch (JavaResolver.1.$SwitchMap$com$strobel$decompiler$languages$java$ast$BinaryOperatorType[operator.ordinal()]) {
       case 9: 
         return and(type, left, right);
       case 10: 
         return or(type, left, right);
       case 11: 
         return xor(type, left, right);
       
       case 1: 
         return andAlso(left, right);
       case 2: 
         return orElse(left, right);
       
       case 3: 
         return greaterThan(type, left, right);
       case 4: 
         return greaterThanOrEqual(type, left, right);
       case 5: 
         return equal(type, left, right);
       case 6: 
         return notEqual(type, left, right);
       case 7: 
         return lessThan(type, left, right);
       case 8: 
         return lessThanOrEqual(type, left, right);
       
       case 12: 
         return add(type, left, right);
       case 13: 
         return subtract(type, left, right);
       case 14: 
         return multiply(type, left, right);
       case 15: 
         return divide(type, left, right);
       case 16: 
         return remainder(type, left, right);
       
       case 17: 
         return leftShift(type, left, right);
       case 18: 
         return rightShift(type, left, right);
       case 19: 
         return unsignedRightShift(type, left, right);
       }
       
       return null;
     }
     
     private static Object add(JvmType type, Object left, Object right)
     {
       if (((left instanceof Number)) && ((right instanceof Number))) {
         switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[type.ordinal()]) {
         case 1: 
           return Byte.valueOf((byte)(((Number)left).intValue() + ((Number)right).intValue()));
         case 2: 
           return Integer.valueOf((char)((Number)left).intValue() + ((Number)right).intValue());
         case 3: 
           return Integer.valueOf((short)((Number)left).intValue() + ((Number)right).intValue());
         case 4: 
           return Integer.valueOf(((Number)left).intValue() + ((Number)right).intValue());
         case 5: 
           return Long.valueOf(((Number)left).longValue() + ((Number)right).longValue());
         case 6: 
           return Float.valueOf(((Number)left).floatValue() + ((Number)right).floatValue());
         case 7: 
           return Double.valueOf(((Number)left).doubleValue() + ((Number)right).doubleValue());
         }
         
       }
       return null;
     }
     
     private static Object subtract(JvmType type, Object left, Object right) {
       if (((left instanceof Number)) && ((right instanceof Number))) {
         switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[type.ordinal()]) {
         case 1: 
           return Byte.valueOf((byte)(((Number)left).intValue() - ((Number)right).intValue()));
         case 2: 
           return Integer.valueOf((char)((Number)left).intValue() - ((Number)right).intValue());
         case 3: 
           return Integer.valueOf((short)((Number)left).intValue() - ((Number)right).intValue());
         case 4: 
           return Integer.valueOf(((Number)left).intValue() - ((Number)right).intValue());
         case 5: 
           return Long.valueOf(((Number)left).longValue() - ((Number)right).longValue());
         case 6: 
           return Float.valueOf(((Number)left).floatValue() - ((Number)right).floatValue());
         case 7: 
           return Double.valueOf(((Number)left).doubleValue() - ((Number)right).doubleValue());
         }
         
       }
       return null;
     }
     
     private static Object multiply(JvmType type, Object left, Object right) {
       if (((left instanceof Number)) && ((right instanceof Number))) {
         switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[type.ordinal()]) {
         case 1: 
           return Byte.valueOf((byte)(((Number)left).intValue() * ((Number)right).intValue()));
         case 2: 
           return Integer.valueOf((char)((Number)left).intValue() * ((Number)right).intValue());
         case 3: 
           return Integer.valueOf((short)((Number)left).intValue() * ((Number)right).intValue());
         case 4: 
           return Integer.valueOf(((Number)left).intValue() * ((Number)right).intValue());
         case 5: 
           return Long.valueOf(((Number)left).longValue() * ((Number)right).longValue());
         case 6: 
           return Float.valueOf(((Number)left).floatValue() * ((Number)right).floatValue());
         case 7: 
           return Double.valueOf(((Number)left).doubleValue() * ((Number)right).doubleValue());
         }
         
       }
       return null;
     }
     
     private static Object divide(JvmType type, Object left, Object right) {
       if (((left instanceof Number)) && ((right instanceof Number))) {
         if ((type.isIntegral()) && (((Number)right).longValue() == 0L)) {
           return null;
         }
         switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[type.ordinal()]) {
         case 1: 
           return Byte.valueOf((byte)(((Number)left).intValue() / ((Number)right).intValue()));
         case 2: 
           return Integer.valueOf((char)((Number)left).intValue() / ((Number)right).intValue());
         case 3: 
           return Integer.valueOf((short)((Number)left).intValue() / ((Number)right).intValue());
         case 4: 
           return Integer.valueOf(((Number)left).intValue() / ((Number)right).intValue());
         case 5: 
           return Long.valueOf(((Number)left).longValue() / ((Number)right).longValue());
         case 6: 
           return Float.valueOf(((Number)left).floatValue() / ((Number)right).floatValue());
         case 7: 
           return Double.valueOf(((Number)left).doubleValue() / ((Number)right).doubleValue());
         }
         
       }
       return null;
     }
     
     private static Object remainder(JvmType type, Object left, Object right) {
       if (((left instanceof Number)) && ((right instanceof Number))) {
         switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[type.ordinal()]) {
         case 1: 
           return Byte.valueOf((byte)(((Number)left).intValue() % ((Number)right).intValue()));
         case 2: 
           return Integer.valueOf((char)((Number)left).intValue() % ((Number)right).intValue());
         case 3: 
           return Integer.valueOf((short)((Number)left).intValue() % ((Number)right).intValue());
         case 4: 
           return Integer.valueOf(((Number)left).intValue() % ((Number)right).intValue());
         case 5: 
           return Long.valueOf(((Number)left).longValue() % ((Number)right).longValue());
         case 6: 
           return Float.valueOf(((Number)left).floatValue() % ((Number)right).floatValue());
         case 7: 
           return Double.valueOf(((Number)left).doubleValue() % ((Number)right).doubleValue());
         }
         
       }
       return null;
     }
     
     private static Object and(JvmType type, Object left, Object right) {
       if (((left instanceof Number)) && ((right instanceof Number))) {
         switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[type.ordinal()]) {
         case 1: 
           return Byte.valueOf((byte)(((Number)left).intValue() & ((Number)right).intValue()));
         case 2: 
           return Integer.valueOf((char)((Number)left).intValue() & ((Number)right).intValue());
         case 3: 
           return Integer.valueOf((short)((Number)left).intValue() & ((Number)right).intValue());
         case 4: 
           return Integer.valueOf(((Number)left).intValue() & ((Number)right).intValue());
         case 5: 
           return Long.valueOf(((Number)left).longValue() & ((Number)right).longValue());
         }
         
       }
       return null;
     }
     
     private static Object or(JvmType type, Object left, Object right) {
       if (((left instanceof Number)) && ((right instanceof Number))) {
         switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[type.ordinal()]) {
         case 1: 
           return Byte.valueOf((byte)(((Number)left).intValue() | ((Number)right).intValue()));
         case 2: 
           return Integer.valueOf((char)((Number)left).intValue() | ((Number)right).intValue());
         case 3: 
           return Integer.valueOf((short)((Number)left).intValue() | ((Number)right).intValue());
         case 4: 
           return Integer.valueOf(((Number)left).intValue() | ((Number)right).intValue());
         case 5: 
           return Long.valueOf(((Number)left).longValue() | ((Number)right).longValue());
         }
         
       }
       return null;
     }
     
     private static Object xor(JvmType type, Object left, Object right) {
       if (((left instanceof Number)) && ((right instanceof Number))) {
         switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[type.ordinal()]) {
         case 1: 
           return Byte.valueOf((byte)(((Number)left).intValue() ^ ((Number)right).intValue()));
         case 2: 
           return Integer.valueOf((char)((Number)left).intValue() ^ ((Number)right).intValue());
         case 3: 
           return Integer.valueOf((short)((Number)left).intValue() ^ ((Number)right).intValue());
         case 4: 
           return Integer.valueOf(((Number)left).intValue() ^ ((Number)right).intValue());
         case 5: 
           return Long.valueOf(((Number)left).longValue() ^ ((Number)right).longValue());
         }
         
       }
       return null;
     }
     
     private static Object leftShift(JvmType type, Object left, Object right) {
       if (((left instanceof Number)) && ((right instanceof Number))) {
         switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[type.ordinal()]) {
         case 1: 
           return Byte.valueOf((byte)(((Number)left).intValue() << ((Number)right).intValue()));
         case 2: 
           return Integer.valueOf((char)((Number)left).intValue() << ((Number)right).intValue());
         case 3: 
           return Integer.valueOf((short)((Number)left).intValue() << ((Number)right).intValue());
         case 4: 
           return Integer.valueOf(((Number)left).intValue() << ((Number)right).intValue());
         case 5: 
           return Long.valueOf(((Number)left).longValue() << (int)((Number)right).longValue());
         }
         
       }
       return null;
     }
     
     private static Object rightShift(JvmType type, Object left, Object right) {
       if (((left instanceof Number)) && ((right instanceof Number))) {
         switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[type.ordinal()]) {
         case 1: 
           return Byte.valueOf((byte)(((Number)left).intValue() >> ((Number)right).intValue()));
         case 2: 
           return Integer.valueOf((char)((Number)left).intValue() >> ((Number)right).intValue());
         case 3: 
           return Integer.valueOf((short)((Number)left).intValue() >> ((Number)right).intValue());
         case 4: 
           return Integer.valueOf(((Number)left).intValue() >> ((Number)right).intValue());
         case 5: 
           return Long.valueOf(((Number)left).longValue() >> (int)((Number)right).longValue());
         }
         
       }
       return null;
     }
     
     private static Object unsignedRightShift(JvmType type, Object left, Object right) {
       if (((left instanceof Number)) && ((right instanceof Number))) {
         switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[type.ordinal()]) {
         case 1: 
           return Byte.valueOf((byte)(((Number)left).intValue() >>> ((Number)right).intValue()));
         case 2: 
           return Integer.valueOf((char)((Number)left).intValue() >>> ((Number)right).intValue());
         case 3: 
           return Integer.valueOf((short)((Number)left).intValue() >>> ((Number)right).intValue());
         case 4: 
           return Integer.valueOf(((Number)left).intValue() >>> ((Number)right).intValue());
         case 5: 
           return Long.valueOf(((Number)left).longValue() >>> (int)((Number)right).longValue());
         }
         
       }
       return null;
     }
     
     private static Object andAlso(Object left, Object right) {
       return Boolean.valueOf((Boolean.TRUE.equals(asBoolean(left))) && (Boolean.TRUE.equals(asBoolean(right))));
     }
     
     private static Object orElse(Object left, Object right)
     {
       return Boolean.valueOf((Boolean.TRUE.equals(asBoolean(left))) || (Boolean.TRUE.equals(asBoolean(right))));
     }
     
     private static Boolean asBoolean(Object o)
     {
       if ((o instanceof Boolean)) {
         return (Boolean)o;
       }
       if ((o instanceof Number)) {
         Number n = (Number)o;
         
         if ((o instanceof Float)) {
           return Boolean.valueOf(n.floatValue() != 0.0F);
         }
         if ((o instanceof Double)) {
           return Boolean.valueOf(n.doubleValue() != 0.0D);
         }
         
         return Boolean.valueOf(n.longValue() != 0L);
       }
       
 
       return null;
     }
     
     private static Boolean lessThan(JvmType type, Object left, Object right) {
       if (((left instanceof Number)) && ((right instanceof Number))) {
         switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[type.ordinal()]) {
         case 1: 
         case 2: 
         case 3: 
         case 4: 
         case 5: 
           return Boolean.valueOf(((Number)left).longValue() < ((Number)right).longValue());
         case 6: 
           return Boolean.valueOf(((Number)left).floatValue() < ((Number)right).floatValue());
         case 7: 
           return Boolean.valueOf(((Number)left).doubleValue() < ((Number)right).doubleValue());
         }
         
       }
       return null;
     }
     
     private static Boolean lessThanOrEqual(JvmType type, Object left, Object right) {
       if (((left instanceof Number)) && ((right instanceof Number))) {
         switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[type.ordinal()]) {
         case 1: 
         case 2: 
         case 3: 
         case 4: 
         case 5: 
           return Boolean.valueOf(((Number)left).longValue() <= ((Number)right).longValue());
         case 6: 
           return Boolean.valueOf(((Number)left).floatValue() <= ((Number)right).floatValue());
         case 7: 
           return Boolean.valueOf(((Number)left).doubleValue() <= ((Number)right).doubleValue());
         }
         
       }
       return null;
     }
     
     private static Boolean greaterThan(JvmType type, Object left, Object right) {
       if (((left instanceof Number)) && ((right instanceof Number))) {
         switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[type.ordinal()]) {
         case 1: 
         case 2: 
         case 3: 
         case 4: 
         case 5: 
           return Boolean.valueOf(((Number)left).longValue() > ((Number)right).longValue());
         case 6: 
           return Boolean.valueOf(((Number)left).floatValue() > ((Number)right).floatValue());
         case 7: 
           return Boolean.valueOf(((Number)left).doubleValue() > ((Number)right).doubleValue());
         }
         
       }
       return null;
     }
     
     private static Boolean greaterThanOrEqual(JvmType type, Object left, Object right) {
       if (((left instanceof Number)) && ((right instanceof Number))) {
         switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[type.ordinal()]) {
         case 1: 
         case 2: 
         case 3: 
         case 4: 
         case 5: 
           return Boolean.valueOf(((Number)left).longValue() >= ((Number)right).longValue());
         case 6: 
           return Boolean.valueOf(((Number)left).floatValue() >= ((Number)right).floatValue());
         case 7: 
           return Boolean.valueOf(((Number)left).doubleValue() >= ((Number)right).doubleValue());
         }
         
       }
       return null;
     }
     
     private static Boolean equal(JvmType type, Object left, Object right) {
       if (((left instanceof Number)) && ((right instanceof Number))) {
         switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[type.ordinal()]) {
         case 1: 
         case 2: 
         case 3: 
         case 4: 
         case 5: 
           return Boolean.valueOf(((Number)left).longValue() == ((Number)right).longValue());
         case 6: 
           return Boolean.valueOf(((Number)left).floatValue() == ((Number)right).floatValue());
         case 7: 
           return Boolean.valueOf(((Number)left).doubleValue() == ((Number)right).doubleValue());
         }
         
       }
       return null;
     }
     
     private static Boolean notEqual(JvmType type, Object left, Object right) {
       if (((left instanceof Number)) && ((right instanceof Number))) {
         switch (JavaResolver.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[type.ordinal()]) {
         case 1: 
         case 2: 
         case 3: 
         case 4: 
         case 5: 
           return Boolean.valueOf(((Number)left).longValue() != ((Number)right).longValue());
         case 6: 
           return Boolean.valueOf(((Number)left).floatValue() != ((Number)right).floatValue());
         case 7: 
           return Boolean.valueOf(((Number)left).doubleValue() != ((Number)right).doubleValue());
         }
         
       }
       return null;
     }
   }
   
   private static final class UnaryOperations {
     static Object doUnary(UnaryOperatorType operator, Object operand) {
       switch (JavaResolver.1.$SwitchMap$com$strobel$decompiler$languages$java$ast$UnaryOperatorType[operator.ordinal()]) {
       case 1: 
         return isFalse(operand);
       case 2: 
         return not(operand);
       case 3: 
         return minus(operand);
       case 4: 
         return plus(operand);
       case 5: 
         return preIncrement(operand);
       case 6: 
         return preDecrement(operand);
       case 7: 
         return postIncrement(operand);
       case 8: 
         return postDecrement(operand);
       }
       
       return null;
     }
     
     private static Object isFalse(Object operand) {
       if (Boolean.TRUE.equals(operand)) {
         return Boolean.FALSE;
       }
       
       if (Boolean.FALSE.equals(operand)) {
         return Boolean.TRUE;
       }
       
       if ((operand instanceof Number)) {
         Number n = (Number)operand;
         
         if ((n instanceof Float)) {
           return Boolean.valueOf(n.floatValue() != 0.0F);
         }
         
         if ((n instanceof Double)) {
           return Boolean.valueOf(n.doubleValue() != 0.0D);
         }
         
         return Boolean.valueOf(n.longValue() != 0L);
       }
       
       return null;
     }
     
     private static Object not(Object operand) {
       if ((operand instanceof Number)) {
         Number n = (Number)operand;
         
         if ((n instanceof Byte)) {
           return Integer.valueOf(n.byteValue() ^ 0xFFFFFFFF);
         }
         
         if ((n instanceof Short)) {
           return Integer.valueOf(n.shortValue() ^ 0xFFFFFFFF);
         }
         
         if ((n instanceof Integer)) {
           return Integer.valueOf(n.intValue() ^ 0xFFFFFFFF);
         }
         
         if ((n instanceof Long)) {
           return Long.valueOf(n.longValue() ^ 0xFFFFFFFFFFFFFFFF);
         }
       }
       else if ((operand instanceof Character)) {
         return Integer.valueOf(((Character)operand).charValue() ^ 0xFFFFFFFF);
       }
       
       return null;
     }
     
     private static Object minus(Object operand) {
       if ((operand instanceof Number)) {
         Number n = (Number)operand;
         
         if ((n instanceof Byte)) {
           return Integer.valueOf(-n.byteValue());
         }
         
         if ((n instanceof Short)) {
           return Integer.valueOf(-n.shortValue());
         }
         
         if ((n instanceof Integer)) {
           return Integer.valueOf(-n.intValue());
         }
         
         if ((n instanceof Long)) {
           return Long.valueOf(-n.longValue());
         }
       }
       else if ((operand instanceof Character)) {
         return Integer.valueOf(-((Character)operand).charValue());
       }
       
       return null;
     }
     
     private static Object plus(Object operand) {
       if ((operand instanceof Number)) {
         Number n = (Number)operand;
         
         if ((n instanceof Byte)) {
           return Integer.valueOf(n.byteValue());
         }
         
         if ((n instanceof Short)) {
           return Integer.valueOf(n.shortValue());
         }
         
         if ((n instanceof Integer)) {
           return Integer.valueOf(n.intValue());
         }
         
         if ((n instanceof Long)) {
           return Long.valueOf(n.longValue());
         }
       }
       else if ((operand instanceof Character)) {
         return Integer.valueOf(((Character)operand).charValue());
       }
       
       return null;
     }
     
     private static Object preIncrement(Object operand) {
       if ((operand instanceof Number)) {
         Number n = (Number)operand;
         
         if ((n instanceof Byte)) {
           byte b = n.byteValue();
           b = (byte)(b + 1);return Byte.valueOf(b);
         }
         
         if ((n instanceof Short)) {
           short s = n.shortValue();
           s = (short)(s + 1);return Short.valueOf(s);
         }
         
         if ((n instanceof Integer)) {
           int i = n.intValue();
           i++;return Integer.valueOf(i);
         }
         
         if ((n instanceof Long)) {
           long l = n.longValue();
           return Long.valueOf(++l);
         }
       }
       else if ((operand instanceof Character)) {
         char c = ((Character)operand).charValue();
         c = (char)(c + '\001');return Character.valueOf(c);
       }
       
       return null;
     }
     
     private static Object preDecrement(Object operand) {
       if ((operand instanceof Number)) {
         Number n = (Number)operand;
         
         if ((n instanceof Byte)) {
           byte b = n.byteValue();
           b = (byte)(b - 1);return Byte.valueOf(b);
         }
         
         if ((n instanceof Short)) {
           short s = n.shortValue();
           s = (short)(s - 1);return Short.valueOf(s);
         }
         
         if ((n instanceof Integer)) {
           int i = n.intValue();
           i--;return Integer.valueOf(i);
         }
         
         if ((n instanceof Long)) {
           long l = n.longValue();
           return Long.valueOf(--l);
         }
       }
       else if ((operand instanceof Character)) {
         char c = ((Character)operand).charValue();
         c = (char)(c - '\001');return Character.valueOf(c);
       }
       
       return null;
     }
     
     private static Object postIncrement(Object operand) {
       if ((operand instanceof Number)) {
         return operand;
       }
       if ((operand instanceof Character)) {
         return operand;
       }
       
       return null;
     }
     
     private static Object postDecrement(Object operand) {
       if ((operand instanceof Number)) {
         return operand;
       }
       if ((operand instanceof Character)) {
         return operand;
       }
       
       return null;
     }
   }
 }


