 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.ConversionType;
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AstBuilder;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
 import com.strobel.decompiler.languages.java.ast.CastExpression;
 import com.strobel.decompiler.languages.java.ast.ClassOfExpression;
 import com.strobel.decompiler.languages.java.ast.ConditionalExpression;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.InvocationExpression;
 import com.strobel.decompiler.languages.java.ast.JavaResolver;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.NullReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.Roles;
 import com.strobel.decompiler.languages.java.ast.SynchronizedStatement;
 import com.strobel.decompiler.languages.java.ast.ThrowStatement;
 import com.strobel.decompiler.semantics.ResolveResult;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 public class RemoveImplicitBoxingTransform
   extends ContextTrackingVisitor<Void>
 {
   private static final Set<String> BOX_METHODS = new HashSet();
   private static final Set<String> UNBOX_METHODS = new HashSet();
   
   static { String[] boxTypes = { "java/lang/Byte", "java/lang/Short", "java/lang/Integer", "java/lang/Long", "java/lang/Float", "java/lang/Double" };
     
 
 
 
 
 
 
 
     String[] unboxMethods = { "byteValue:()B", "shortValue:()S", "intValue:()I", "longValue:()J", "floatValue:()F", "doubleValue:()D" };
     
 
 
 
 
 
 
 
     String[] boxMethods = { "java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;", "java/lang/Character.valueOf:(C)Ljava/lang/Character;", "java/lang/Byte.valueOf:(B)Ljava/lang/Byte;", "java/lang/Short.valueOf:(S)Ljava/lang/Short;", "java/lang/Integer.valueOf:(I)Ljava/lang/Integer;", "java/lang/Long.valueOf:(J)Ljava/lang/Long;", "java/lang/Float.valueOf:(F)Ljava/lang/Float;", "java/lang/Double.valueOf:(D)Ljava/lang/Double;" };
     
 
 
 
 
 
 
 
 
 
     Collections.addAll(BOX_METHODS, boxMethods);
     
 
 
 
 
 
 
     for (String boxType : boxTypes) {
       for (String unboxMethod : unboxMethods) {
         UNBOX_METHODS.add(boxType + "." + unboxMethod);
       }
     }
     
     UNBOX_METHODS.add("java/lang/Character.charValue:()C");
     UNBOX_METHODS.add("java/lang/Boolean.booleanValue:()Z");
   }
   
 
   public RemoveImplicitBoxingTransform(DecompilerContext context)
   {
     super(context);
     this._resolver = new JavaResolver(context);
   }
   
   public Void visitInvocationExpression(InvocationExpression node, Void data)
   {
     super.visitInvocationExpression(node, data);
     
     if ((node.getArguments().size() == 1) && ((node.getTarget() instanceof MemberReferenceExpression)))
     {
 
       removeBoxing(node);
     }
     else {
       removeUnboxing(node);
     }
     
     return null;
   }
   
   private boolean isValidPrimitiveParent(InvocationExpression node, AstNode parent) {
     if ((parent == null) || (parent.isNull())) {
       return false;
     }
     
     if ((parent instanceof BinaryOperatorExpression)) {
       BinaryOperatorExpression binary = (BinaryOperatorExpression)parent;
       
 
       if (((binary.getLeft() instanceof NullReferenceExpression)) || ((binary.getRight() instanceof NullReferenceExpression)))
       {
 
         return false;
       }
       
       ResolveResult leftResult = this._resolver.apply(binary.getLeft());
       ResolveResult rightResult = this._resolver.apply(binary.getRight());
       
       return (leftResult != null) && (rightResult != null) && (leftResult.getType() != null) && (rightResult.getType() != null) && (node == binary.getLeft() ? rightResult.getType().isPrimitive() : leftResult.getType().isPrimitive());
     }
     
 
 
 
 
 
 
 
 
     if (node.getRole() == Roles.ARGUMENT) {
       MemberReference member = (MemberReference)parent.getUserData(Keys.MEMBER_REFERENCE);
       
       if ((member instanceof MethodReference)) {
         MethodReference method = (MethodReference)parent.getUserData(Keys.MEMBER_REFERENCE);
         
         if ((method == null) || (MetadataHelper.isOverloadCheckingRequired(method))) {
           return false;
         }
       }
     }
     
     return (node.getRole() != Roles.TARGET_EXPRESSION) && (!(parent instanceof ClassOfExpression)) && (!(parent instanceof SynchronizedStatement)) && (!(parent instanceof ThrowStatement));
   }
   
 
 
 
 
   private void removeUnboxing(InvocationExpression e)
   {
     if ((e == null) || (e.isNull())) {
       return;
     }
     
     Expression target = e.getTarget();
     
     if (!(target instanceof MemberReferenceExpression)) {
       return;
     }
     
     MemberReference reference = (MemberReference)e.getUserData(Keys.MEMBER_REFERENCE);
     
     if (!(reference instanceof MethodReference)) {
       return;
     }
     
     String key = reference.getFullName() + ":" + reference.getSignature();
     
     if (!UNBOX_METHODS.contains(key)) {
       return;
     }
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     performUnboxingRemoval(e, (MemberReferenceExpression)target);
   }
   
 
 
 
   private void removeUnboxingForCondition(InvocationExpression e, MemberReferenceExpression target, ConditionalExpression parent)
   {
     boolean leftSide = parent.getTrueExpression().isAncestorOf(e);
     Expression otherSide = leftSide ? parent.getFalseExpression() : parent.getTrueExpression();
     ResolveResult otherResult = this._resolver.apply(otherSide);
     
     if ((otherResult == null) || (otherResult.getType() == null) || (!otherResult.getType().isPrimitive())) {
       return;
     }
     
     performUnboxingRemoval(e, target);
   }
   
   private void performUnboxingRemoval(InvocationExpression e, MemberReferenceExpression target) {
     Expression boxedValue = target.getTarget();
     MethodReference unboxMethod = (MethodReference)e.getUserData(Keys.MEMBER_REFERENCE);
     AstBuilder astBuilder = (AstBuilder)this.context.getUserData(Keys.AST_BUILDER);
     
     boxedValue.remove();
     
     e.replaceWith(new CastExpression(astBuilder.convertType(unboxMethod.getReturnType()), boxedValue));
   }
   
 
 
 
 
   private void removeUnboxingForArgument(InvocationExpression e)
   {
     AstNode parent = e.getParent();
     
     MemberReference unboxMethod = (MemberReference)e.getUserData(Keys.MEMBER_REFERENCE);
     MemberReference outerBoxMethod = (MemberReference)parent.getUserData(Keys.MEMBER_REFERENCE);
     
     if ((!(unboxMethod instanceof MethodReference)) || (!(outerBoxMethod instanceof MethodReference))) {
       return;
     }
     
     String unboxMethodKey = unboxMethod.getFullName() + ":" + unboxMethod.getSignature();
     String boxMethodKey = outerBoxMethod.getFullName() + ":" + outerBoxMethod.getSignature();
     
     if (!UNBOX_METHODS.contains(unboxMethodKey)) {
       return;
     }
     
     Expression boxedValue = ((MemberReferenceExpression)e.getTarget()).getTarget();
     
     if ((!BOX_METHODS.contains(boxMethodKey)) || (!(parent instanceof InvocationExpression)) || (!isValidPrimitiveParent((InvocationExpression)parent, parent.getParent())))
     {
 
 
       boxedValue.remove();
       e.replaceWith(boxedValue);
       return;
     }
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     ResolveResult boxedValueResult = this._resolver.apply(boxedValue);
     
     if ((boxedValueResult == null) || (boxedValueResult.getType() == null)) {
       return;
     }
     
     TypeReference targetType = ((MethodReference)outerBoxMethod).getReturnType();
     TypeReference sourceType = boxedValueResult.getType();
     
     switch (MetadataHelper.getNumericConversionType(targetType, sourceType))
     {
     case IDENTITY: 
     case IMPLICIT: 
       
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     case EXPLICIT_TO_UNBOXED: 
       AstBuilder astBuilder = (AstBuilder)this.context.getUserData(Keys.AST_BUILDER);
       
       if (astBuilder == null) {
         return;
       }
       
       boxedValue.remove();
       
       TypeReference castType = ((ParameterDefinition)((MethodReference)outerBoxMethod).getParameters().get(0)).getParameterType();
       CastExpression cast = new CastExpression(astBuilder.convertType(castType), boxedValue);
       
       parent.replaceWith(cast);
       
       break;
     }
     
   }
   
 
 
 
   private final JavaResolver _resolver;
   
 
 
   private void removeUnboxingForCast(InvocationExpression e, MemberReferenceExpression target, CastExpression parent)
   {
     TypeReference targetType = parent.getType().toTypeReference();
     
     if ((targetType == null) || (!targetType.isPrimitive())) {
       return;
     }
     
     Expression boxedValue = target.getTarget();
     ResolveResult boxedValueResult = this._resolver.apply(boxedValue);
     
     if ((boxedValueResult == null) || (boxedValueResult.getType() == null)) {
       return;
     }
     
     TypeReference sourceType = boxedValueResult.getType();
     ConversionType conversionType = MetadataHelper.getNumericConversionType(targetType, sourceType);
     
     switch (conversionType) {
     case IMPLICIT: 
     case EXPLICIT_TO_UNBOXED: 
     case EXPLICIT: 
       boxedValue.remove();
       e.replaceWith(boxedValue);
       return;
     }
     
   }
   
 
 
   private void removeBoxing(InvocationExpression node)
   {
     if (!isValidPrimitiveParent(node, node.getParent())) {
       return;
     }
     
     MemberReference reference = (MemberReference)node.getUserData(Keys.MEMBER_REFERENCE);
     
     if (!(reference instanceof MethodReference)) {
       return;
     }
     
     String key = reference.getFullName() + ":" + reference.getSignature();
     
     if (!BOX_METHODS.contains(key)) {
       return;
     }
     
     AstNodeCollection<Expression> arguments = node.getArguments();
     Expression underlyingValue = (Expression)arguments.firstOrNullObject();
     ResolveResult valueResult = this._resolver.apply(underlyingValue);
     
     if ((valueResult == null) || (valueResult.getType() == null)) {
       return;
     }
     
     TypeReference sourceType = valueResult.getType();
     TypeReference targetType = ((MethodReference)reference).getReturnType();
     ConversionType conversionType = MetadataHelper.getNumericConversionType(targetType, sourceType);
     
     switch (conversionType) {
     case IMPLICIT: 
       underlyingValue.remove();
       node.replaceWith(underlyingValue);
       break;
     
 
     case EXPLICIT_TO_UNBOXED: 
     case EXPLICIT: 
       AstBuilder astBuilder = (AstBuilder)this.context.getUserData(Keys.AST_BUILDER);
       
       if (astBuilder == null) {
         return;
       }
       
       TypeReference castType;
       TypeReference castType;
       if (conversionType == ConversionType.EXPLICIT_TO_UNBOXED) {
         castType = MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(targetType);
       }
       else {
         castType = targetType;
       }
       
       underlyingValue.remove();
       node.replaceWith(new CastExpression(astBuilder.convertType(castType), underlyingValue));
       
       break;
     }
   }
 }


