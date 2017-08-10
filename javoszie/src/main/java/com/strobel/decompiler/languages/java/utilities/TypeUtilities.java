 package com.strobel.decompiler.languages.java.utilities;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.annotations.Nullable;
 import com.strobel.assembler.metadata.CommonTypeReferences;
 import com.strobel.assembler.metadata.DynamicCallSite;
 import com.strobel.assembler.metadata.IMethodSignature;
 import com.strobel.assembler.metadata.JvmType;
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.Predicates;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorType;
 import com.strobel.decompiler.languages.java.ast.ConditionalExpression;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.LambdaExpression;
 import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
 import com.strobel.decompiler.languages.java.ast.MethodGroupExpression;
 import com.strobel.decompiler.languages.java.ast.ParenthesizedExpression;
 import com.strobel.decompiler.languages.java.ast.Roles;
 import com.strobel.decompiler.languages.java.ast.VariableInitializer;
 import com.strobel.decompiler.semantics.ResolveResult;
 import com.strobel.functions.Function;
 import java.util.Collections;
 import java.util.EnumMap;
 import java.util.List;
 import java.util.Map;
 
 public final class TypeUtilities
 {
   private static final String OBJECT_DESCRIPTOR = "java/lang/Object";
   private static final String STRING_DESCRIPTOR = "java/lang/String";
   private static final Map<JvmType, Integer> TYPE_TO_RANK_MAP;
   private static final int BYTE_RANK = 1;
   private static final int SHORT_RANK = 2;
   private static final int CHAR_RANK = 3;
   private static final int INT_RANK = 4;
   private static final int LONG_RANK = 5;
   private static final int FLOAT_RANK = 6;
   private static final int DOUBLE_RANK = 7;
   private static final int BOOL_RANK = 10;
   private static final int STRING_RANK = 100;
   private static final int MAX_NUMERIC_RANK = 7;
   
   static
   {
     Map<JvmType, Integer> rankMap = new EnumMap(JvmType.class);
     
     rankMap.put(JvmType.Byte, Integer.valueOf(1));
     rankMap.put(JvmType.Short, Integer.valueOf(2));
     rankMap.put(JvmType.Character, Integer.valueOf(3));
     rankMap.put(JvmType.Integer, Integer.valueOf(4));
     rankMap.put(JvmType.Long, Integer.valueOf(5));
     rankMap.put(JvmType.Float, Integer.valueOf(6));
     rankMap.put(JvmType.Double, Integer.valueOf(7));
     rankMap.put(JvmType.Boolean, Integer.valueOf(10));
     
     TYPE_TO_RANK_MAP = Collections.unmodifiableMap(rankMap);
   }
   
   private static int getTypeRank(@NotNull TypeReference type) {
     TypeReference unboxedType = MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(type);
     Integer rank = (Integer)TYPE_TO_RANK_MAP.get(unboxedType.getSimpleType());
     
     if (rank != null) {
       return rank.intValue();
     }
     
     if (StringUtilities.equals(type.getInternalName(), "java/lang/String")) {
       return 100;
     }
     
     return Integer.MAX_VALUE;
   }
   
   public static boolean isPrimitive(@Nullable TypeReference type) {
     return (type != null) && (type.isPrimitive());
   }
   
   public static boolean isPrimitiveOrWrapper(@Nullable TypeReference type) {
     if (type == null) {
       return false;
     }
     return MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(type).isPrimitive();
   }
   
   public static boolean isBoolean(@Nullable TypeReference type) {
     if (type == null) {
       return false;
     }
     return MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(type).getSimpleType() == JvmType.Boolean;
   }
   
   public static boolean isArithmetic(@Nullable TypeReference type) {
     if (type == null) {
       return false;
     }
     JvmType jvmType = MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(type).getSimpleType();
     return (jvmType.isNumeric()) && (jvmType != JvmType.Boolean);
   }
   
 
 
 
 
   public static boolean isBinaryOperatorApplicable(@NotNull BinaryOperatorType op, @NotNull AstType lType, @NotNull AstType rType, boolean strict)
   {
     return isBinaryOperatorApplicable(op, ((AstType)VerifyArgument.notNull(lType, "lType")).toTypeReference(), ((AstType)VerifyArgument.notNull(rType, "rType")).toTypeReference(), strict);
   }
   
 
 
 
 
 
 
 
 
 
   public static boolean isBinaryOperatorApplicable(@NotNull BinaryOperatorType op, @Nullable TypeReference lType, @Nullable TypeReference rType, boolean strict)
   {
     if ((lType == null) || (rType == null)) {
       return true;
     }
     
     VerifyArgument.notNull(op, "op");
     
     int lRank = getTypeRank(lType);
     int rRank = getTypeRank(rType);
     
     TypeReference lUnboxed = MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(lType);
     TypeReference rUnboxed = MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(rType);
     
     int resultRank = 10;
     boolean isApplicable = false;
     
     switch (op) {
     case BITWISE_AND: 
     case BITWISE_OR: 
     case EXCLUSIVE_OR: 
       if ((lUnboxed.isPrimitive()) && (rUnboxed.isPrimitive())) {
         isApplicable = ((lRank <= 5) && (rRank <= 5)) || (isBoolean(lUnboxed)) || (isBoolean(rUnboxed));
         
 
         resultRank = lRank <= 5 ? 4 : 10;
       }
       
 
       break;
     case LOGICAL_AND: 
     case LOGICAL_OR: 
       if ((lUnboxed.isPrimitive()) && (rUnboxed.isPrimitive())) {
         isApplicable = (isBoolean(lType)) && (isBoolean(rType));
       }
       
 
       break;
     case GREATER_THAN: 
     case GREATER_THAN_OR_EQUAL: 
     case LESS_THAN: 
     case LESS_THAN_OR_EQUAL: 
       if ((lUnboxed.isPrimitive()) && (rUnboxed.isPrimitive())) {
         isApplicable = (lRank <= 7) && (rRank <= 7);
         resultRank = 4;
       }
       
 
       break;
     case EQUALITY: 
     case INEQUALITY: 
       if ((lUnboxed.isPrimitive()) && (rUnboxed.isPrimitive()) && ((lType.isPrimitive()) || (rType.isPrimitive())))
       {
         isApplicable = ((lRank <= 7) && (rRank <= 7)) || ((lRank == 10) && (rRank == 10));
       }
       else
       {
         if (lType.isPrimitive()) {
           return MetadataHelper.isConvertible(lType, rType);
         }
         if (rType.isPrimitive()) {
           return MetadataHelper.isConvertible(rType, lType);
         }
         isApplicable = (MetadataHelper.isConvertible(lType, rType)) || (MetadataHelper.isConvertible(rType, lType));
       }
       
       break;
     
 
     case ADD: 
       if (StringUtilities.equals(lType.getInternalName(), "java/lang/String")) {
         isApplicable = !rType.isVoid();
         resultRank = 100;
 
       }
       else if (StringUtilities.equals(rType.getInternalName(), "java/lang/String")) {
         isApplicable = !lType.isVoid();
         resultRank = 100;
 
 
       }
       else if ((lUnboxed.isPrimitive()) && (rUnboxed.isPrimitive())) {
         resultRank = Math.max(lRank, rRank);
         isApplicable = (lRank <= 7) && (rRank <= 7);
       }
       
 
 
       break;
     case SUBTRACT: 
     case MULTIPLY: 
     case DIVIDE: 
     case MODULUS: 
       if ((lUnboxed.isPrimitive()) && (rUnboxed.isPrimitive())) {
         resultRank = Math.max(lRank, rRank);
         isApplicable = (lRank <= 7) && (rRank <= 7);
       }
       
 
       break;
     case SHIFT_LEFT: 
     case SHIFT_RIGHT: 
     case UNSIGNED_SHIFT_RIGHT: 
       if ((lUnboxed.isPrimitive()) && (rUnboxed.isPrimitive())) {
         isApplicable = (lRank <= 5) && (rRank <= 5);
         resultRank = 4;
       }
       
       break;
     }
     
     if ((isApplicable) && (strict)) {
       if (resultRank > 7) {
         isApplicable = (lRank == resultRank) || (StringUtilities.equals(lType.getInternalName(), "java/lang/Object"));
       }
       else
       {
         isApplicable = lRank <= 7;
       }
     }
     
     return isApplicable;
   }
   
   @Nullable
   public static AstNode skipParenthesesUp(AstNode e) {
     AstNode result = e;
     
     while ((result instanceof ParenthesizedExpression)) {
       result = result.getParent();
     }
     
     return result;
   }
   
   @Nullable
   public static AstNode skipParenthesesDown(AstNode e) {
     AstNode result = e;
     
     while ((result instanceof ParenthesizedExpression)) {
       result = ((ParenthesizedExpression)result).getExpression();
     }
     
     return result;
   }
   
   @Nullable
   public static Expression skipParenthesesDown(Expression e) {
     Expression result = e;
     
     while ((result instanceof ParenthesizedExpression)) {
       result = ((ParenthesizedExpression)result).getExpression();
     }
     
     return result;
   }
   
   private static boolean checkSameExpression(Expression template, Expression expression) {
     return com.strobel.core.Comparer.equals(template, skipParenthesesDown(expression));
   }
   
   private static TypeReference getType(@NotNull Function<AstNode, ResolveResult> resolver, @NotNull AstNode node) {
     ResolveResult result = (ResolveResult)resolver.apply(node);
     return result != null ? result.getType() : null;
   }
   
   @Nullable
   public static TypeReference getExpectedTypeByParent(Function<AstNode, ResolveResult> resolver, Expression expression) {
     VerifyArgument.notNull(resolver, "resolver");
     VerifyArgument.notNull(expression, "expression");
     
     AstNode parent = skipParenthesesUp(expression.getParent());
     
     if (expression.getRole() == Roles.CONDITION) {
       return CommonTypeReferences.Boolean;
     }
     
     if ((parent instanceof VariableInitializer)) {
       if ((checkSameExpression(expression, ((VariableInitializer)parent).getInitializer())) && 
         ((parent.getParent() instanceof com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement))) {
         return getType(resolver, parent.getParent());
       }
       
     }
     else if ((parent instanceof AssignmentExpression)) {
       if (checkSameExpression(expression, ((AssignmentExpression)parent).getRight())) {
         return getType(resolver, ((AssignmentExpression)parent).getLeft());
       }
     }
     else if ((parent instanceof com.strobel.decompiler.languages.java.ast.ReturnStatement)) {
       LambdaExpression lambdaExpression = (LambdaExpression)CollectionUtilities.firstOrDefault(parent.getAncestors(LambdaExpression.class));
       
       if (lambdaExpression != null) {
         DynamicCallSite callSite = (DynamicCallSite)lambdaExpression.getUserData(Keys.DYNAMIC_CALL_SITE);
         
         if (callSite == null) {
           return null;
         }
         
         MethodReference method = (MethodReference)callSite.getBootstrapArguments().get(0);
         
         return method.getDeclaringType();
       }
       
       MethodDeclaration method = (MethodDeclaration)CollectionUtilities.firstOrDefault(parent.getAncestors(MethodDeclaration.class));
       
       if (method != null) {
         return getType(resolver, method.getReturnType());
       }
       
     }
     else if ((parent instanceof ConditionalExpression)) {
       if (checkSameExpression(expression, ((ConditionalExpression)parent).getTrueExpression())) {
         return getType(resolver, ((ConditionalExpression)parent).getFalseExpression());
       }
       if (checkSameExpression(expression, ((ConditionalExpression)parent).getFalseExpression())) {
         return getType(resolver, ((ConditionalExpression)parent).getTrueExpression());
       }
     }
     
     return null;
   }
   
   public static IMethodSignature getLambdaSignature(MethodGroupExpression node) {
     return getLambdaSignatureCore(node);
   }
   
   public static IMethodSignature getLambdaSignature(LambdaExpression node) {
     return getLambdaSignatureCore(node);
   }
   
   public static boolean isValidPrimitiveLiteralAssignment(TypeReference targetType, Object value) {
     VerifyArgument.notNull(targetType, "targetType");
     
     if (targetType.getSimpleType() == JvmType.Boolean) {
       return value instanceof Boolean;
     }
     
     if ((!targetType.isPrimitive()) || ((!(value instanceof Number)) && (!(value instanceof Character)))) {
       return false;
     }
     
     Number n = (value instanceof Character) ? Integer.valueOf(((Character)value).charValue()) : (Number)value;
     
     if (((n instanceof Float)) || ((n instanceof Double))) {
       if (targetType.getSimpleType() == JvmType.Float) {
         return (n.doubleValue() >= 1.401298464324817E-45D) && (n.doubleValue() <= 3.4028234663852886E38D);
       }
       return targetType.getSimpleType() == JvmType.Double;
     }
     
     if ((n instanceof Long)) {
       switch (targetType.getSimpleType()) {
       case Long: 
       case Float: 
       case Double: 
         return true;
       }
       
       return false;
     }
     
 
     switch (targetType.getSimpleType()) {
     case Byte: 
       return (n.intValue() >= -128) && (n.intValue() <= 127);
     
     case Character: 
       return (n.intValue() >= 0) && (n.intValue() <= 65535);
     
     case Short: 
       return (n.intValue() >= 32768) && (n.intValue() <= 32767);
     
     case Integer: 
       return (n.longValue() >= -2147483648L) && (n.longValue() <= 2147483647L);
     
     case Long: 
     case Float: 
     case Double: 
       return true;
     }
     
     return false;
   }
   
   private static IMethodSignature getLambdaSignatureCore(Expression node)
   {
     VerifyArgument.notNull(node, "node");
     
     TypeReference lambdaType = (TypeReference)node.getUserData(Keys.TYPE_REFERENCE);
     DynamicCallSite callSite = (DynamicCallSite)node.getUserData(Keys.DYNAMIC_CALL_SITE);
     
     if (lambdaType == null) {
       if (callSite == null) {
         return null;
       }
       
       return (IMethodSignature)callSite.getBootstrapArguments().get(2);
     }
     
     com.strobel.assembler.metadata.TypeDefinition resolvedType = lambdaType.resolve();
     
     if (resolvedType == null) {
       if (callSite == null) {
         return null;
       }
       
       return (IMethodSignature)callSite.getBootstrapArguments().get(2);
     }
     
     MethodReference functionMethod = null;
     
     List<MethodReference> methods = MetadataHelper.findMethods(resolvedType, callSite != null ? com.strobel.assembler.metadata.MetadataFilters.matchName(callSite.getMethodName()) : Predicates.alwaysTrue());
     
 
 
 
 
     for (MethodReference m : methods) {
       MethodDefinition r = m.resolve();
       
       if ((r != null) && (r.isAbstract()) && (!r.isStatic()) && (!r.isDefault())) {
         functionMethod = r;
         break;
       }
     }
     
     if (functionMethod != null) {
       TypeReference asMemberOf = MetadataHelper.asSuper(functionMethod.getDeclaringType(), lambdaType);
       TypeReference effectiveType = asMemberOf != null ? asMemberOf : lambdaType;
       
       if (MetadataHelper.isRawType(effectiveType)) {
         return MetadataHelper.erase(functionMethod);
       }
       
       functionMethod = MetadataHelper.asMemberOf(functionMethod, effectiveType);
     }
     
     return functionMethod;
   }
 }


