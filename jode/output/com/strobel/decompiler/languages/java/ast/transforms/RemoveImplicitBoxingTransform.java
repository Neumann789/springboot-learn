/* RemoveImplicitBoxingTransform - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

public class RemoveImplicitBoxingTransform extends ContextTrackingVisitor
{
    private static final Set BOX_METHODS = new HashSet();
    private static final Set UNBOX_METHODS = new HashSet();
    private final JavaResolver _resolver;
    
    public RemoveImplicitBoxingTransform(DecompilerContext context) {
	super(context);
	_resolver = new JavaResolver(context);
    }
    
    public Void visitInvocationExpression(InvocationExpression node,
					  Void data) {
    label_1775:
	{
	    super.visitInvocationExpression(node, data);
	    if (node.getArguments().size() != 1
		|| !(node.getTarget() instanceof MemberReferenceExpression))
		removeUnboxing(node);
	    else
		removeBoxing(node);
	    break label_1775;
	}
	return null;
    }
    
    private boolean isValidPrimitiveParent(InvocationExpression node,
					   AstNode parent) {
    label_1778:
	{
	label_1776:
	    {
		if (parent != null && !parent.isNull()) {
		label_1780:
		    {
		    label_1779:
			{
			    if (!(parent
				  instanceof BinaryOperatorExpression)) {
				if (node.getRole() == Roles.ARGUMENT) {
				    MemberReference member
					= ((MemberReference)
					   (parent.getUserData
					    (Keys.MEMBER_REFERENCE)));
				    if (member instanceof MethodReference) {
					MethodReference method
					    = ((MethodReference)
					       (parent.getUserData
						(Keys.MEMBER_REFERENCE)));
					if (method == null
					    || (MetadataHelper
						    .isOverloadCheckingRequired
						(method)))
					    return false;
				    }
				}
			    } else {
				BinaryOperatorExpression binary
				    = (BinaryOperatorExpression) parent;
				if (!(binary.getLeft()
				      instanceof NullReferenceExpression)
				    && !(binary.getRight()
					 instanceof NullReferenceExpression)) {
				    ResolveResult leftResult
					= _resolver.apply(binary.getLeft());
				label_1777:
				    {
					ResolveResult rightResult
					    = _resolver
						  .apply(binary.getRight());
					if (leftResult != null
					    && rightResult != null
					    && leftResult.getType() != null
					    && rightResult.getType() != null) {
					    if (node != binary.getLeft()) {
						if (!leftResult.getType()
							 .isPrimitive())
						    break label_1777;
					    } else if (!rightResult.getType
							    ().isPrimitive())
						break label_1777;
					}
					break label_1777;
				    }
				    PUSH false;
				    break label_1778;
				} else
				    return false;
			    }
			}
			if (node.getRole() == Roles.TARGET_EXPRESSION
			    || parent instanceof ClassOfExpression
			    || parent instanceof SynchronizedStatement
			    || parent instanceof ThrowStatement)
			    PUSH false;
			else
			    PUSH true;
			break label_1780;
		    }
		    return POP;
		    break label_1779;
		} else
		    return false;
	    }
	    PUSH true;
	}
	return POP;
	break label_1776;
    }
    
    private void removeUnboxing(InvocationExpression e) {
	if (e != null && !e.isNull()) {
	    Expression target = e.getTarget();
	    if (target instanceof MemberReferenceExpression) {
		MemberReference reference
		    = (MemberReference) e.getUserData(Keys.MEMBER_REFERENCE);
		if (reference instanceof MethodReference) {
		    String key = (reference.getFullName() + ":"
				  + reference.getSignature());
		    if (UNBOX_METHODS.contains(key))
			performUnboxingRemoval(e, ((MemberReferenceExpression)
						   target));
		}
	    }
	}
	return;
    }
    
    private void removeUnboxingForCondition(InvocationExpression e,
					    MemberReferenceExpression target,
					    ConditionalExpression parent) {
    label_1781:
	{
	    boolean leftSide = parent.getTrueExpression().isAncestorOf(e);
	    if (!leftSide)
		PUSH parent.getTrueExpression();
	    else
		PUSH parent.getFalseExpression();
	    break label_1781;
	}
	Expression otherSide = POP;
	ResolveResult otherResult = _resolver.apply(otherSide);
	if (otherResult != null && otherResult.getType() != null
	    && otherResult.getType().isPrimitive())
	    performUnboxingRemoval(e, target);
	return;
    }
    
    private void performUnboxingRemoval(InvocationExpression e,
					MemberReferenceExpression target) {
	Expression boxedValue = target.getTarget();
	MethodReference unboxMethod
	    = (MethodReference) e.getUserData(Keys.MEMBER_REFERENCE);
	AstBuilder astBuilder
	    = (AstBuilder) context.getUserData(Keys.AST_BUILDER);
	boxedValue.remove();
	e.replaceWith(new CastExpression
		      (astBuilder.convertType(unboxMethod.getReturnType()),
		       boxedValue));
    }
    
    private void removeUnboxingForArgument(InvocationExpression e) {
	AstNode parent = e.getParent();
	MemberReference unboxMethod
	    = (MemberReference) e.getUserData(Keys.MEMBER_REFERENCE);
	MemberReference outerBoxMethod
	    = (MemberReference) parent.getUserData(Keys.MEMBER_REFERENCE);
	if (unboxMethod instanceof MethodReference
	    && outerBoxMethod instanceof MethodReference) {
	    String unboxMethodKey
		= unboxMethod.getFullName() + ":" + unboxMethod.getSignature();
	    String boxMethodKey = (outerBoxMethod.getFullName() + ":"
				   + outerBoxMethod.getSignature());
	    if (UNBOX_METHODS.contains(unboxMethodKey)) {
		Expression boxedValue
		    = ((MemberReferenceExpression) e.getTarget()).getTarget();
		if (BOX_METHODS.contains(boxMethodKey)
		    && parent instanceof InvocationExpression
		    && isValidPrimitiveParent((InvocationExpression) parent,
					      parent.getParent())) {
		    ResolveResult boxedValueResult
			= _resolver.apply(boxedValue);
		    if (boxedValueResult != null
			&& boxedValueResult.getType() != null) {
			TypeReference targetType
			    = ((MethodReference) outerBoxMethod)
				  .getReturnType();
			TypeReference sourceType = boxedValueResult.getType();
			switch (ANONYMOUS CLASS com.strobel.decompiler.languages.java.ast.transforms.RemoveImplicitBoxingTransform$1.$SwitchMap$com$strobel$assembler$metadata$ConversionType[MetadataHelper.getNumericConversionType(targetType, sourceType).ordinal()]) {
			case 3: {
			    AstBuilder astBuilder
				= ((AstBuilder)
				   context.getUserData(Keys.AST_BUILDER));
			    if (astBuilder != null) {
				boxedValue.remove();
				TypeReference castType
				    = ((ParameterDefinition)
				       ((MethodReference) outerBoxMethod)
					   .getParameters
					   ().get(0))
					  .getParameterType();
				CastExpression cast
				    = (new CastExpression
				       (astBuilder.convertType(castType),
					boxedValue));
				parent.replaceWith(cast);
			    }
			    return;
			}
			}
		    }
		} else {
		    boxedValue.remove();
		    e.replaceWith(boxedValue);
		}
	    }
	}
	return;
    }
    
    private void removeUnboxingForCast(InvocationExpression e,
				       MemberReferenceExpression target,
				       CastExpression parent) {
	TypeReference targetType = parent.getType().toTypeReference();
	if (targetType != null && targetType.isPrimitive()) {
	    Expression boxedValue = target.getTarget();
	    ResolveResult boxedValueResult = _resolver.apply(boxedValue);
	    if (boxedValueResult != null
		&& boxedValueResult.getType() != null) {
		TypeReference sourceType = boxedValueResult.getType();
		ConversionType conversionType
		    = MetadataHelper.getNumericConversionType(targetType,
							      sourceType);
		switch (ANONYMOUS CLASS com.strobel.decompiler.languages.java.ast.transforms.RemoveImplicitBoxingTransform$1.$SwitchMap$com$strobel$assembler$metadata$ConversionType[conversionType.ordinal()]) {
		case 2:
		case 3:
		case 4:
		    boxedValue.remove();
		    e.replaceWith(boxedValue);
		    return;
		}
	    }
	}
	return;
    }
    
    private void removeBoxing(InvocationExpression node) {
	if (isValidPrimitiveParent(node, node.getParent())) {
	    MemberReference reference
		= (MemberReference) node.getUserData(Keys.MEMBER_REFERENCE);
	    if (reference instanceof MethodReference) {
		String key
		    = reference.getFullName() + ":" + reference.getSignature();
		if (BOX_METHODS.contains(key)) {
		    AstNodeCollection arguments = node.getArguments();
		    Expression underlyingValue
			= (Expression) arguments.firstOrNullObject();
		    ResolveResult valueResult
			= _resolver.apply(underlyingValue);
		    if (valueResult != null && valueResult.getType() != null) {
			TypeReference sourceType = valueResult.getType();
			TypeReference targetType
			    = ((MethodReference) reference).getReturnType();
			ConversionType conversionType
			    = (MetadataHelper.getNumericConversionType
			       (targetType, sourceType));
			switch (ANONYMOUS CLASS com.strobel.decompiler.languages.java.ast.transforms.RemoveImplicitBoxingTransform$1.$SwitchMap$com$strobel$assembler$metadata$ConversionType[conversionType.ordinal()]) {
			case 2:
			    underlyingValue.remove();
			    node.replaceWith(underlyingValue);
			    return;
			case 3:
			case 4: {
			    AstBuilder astBuilder
				= ((AstBuilder)
				   context.getUserData(Keys.AST_BUILDER));
			    TypeReference castType;
			label_1782:
			    {
				if (astBuilder != null) {
				    if (conversionType
					!= ConversionType.EXPLICIT_TO_UNBOXED)
					castType = targetType;
				    else
					castType
					    = (MetadataHelper
						   .getUnderlyingPrimitiveTypeOrSelf
					       (targetType));
				    break label_1782;
				}
				return;
			    }
			    underlyingValue.remove();
			    node.replaceWith(new CastExpression
					     (astBuilder.convertType(castType),
					      underlyingValue));
			    return;
			}
			}
		    }
		}
	    }
	}
	return;
    }
    
    static {
	String[] boxTypes
	    = { "java/lang/Byte", "java/lang/Short", "java/lang/Integer",
		"java/lang/Long", "java/lang/Float", "java/lang/Double" };
	String[] unboxMethods
	    = { "byteValue:()B", "shortValue:()S", "intValue:()I",
		"longValue:()J", "floatValue:()F", "doubleValue:()D" };
	String[] boxMethods
	    = { "java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;",
		"java/lang/Character.valueOf:(C)Ljava/lang/Character;",
		"java/lang/Byte.valueOf:(B)Ljava/lang/Byte;",
		"java/lang/Short.valueOf:(S)Ljava/lang/Short;",
		"java/lang/Integer.valueOf:(I)Ljava/lang/Integer;",
		"java/lang/Long.valueOf:(J)Ljava/lang/Long;",
		"java/lang/Float.valueOf:(F)Ljava/lang/Float;",
		"java/lang/Double.valueOf:(D)Ljava/lang/Double;" };
	Collections.addAll(BOX_METHODS, boxMethods);
	String[] arr$ = boxTypes;
	int len$ = arr$.length;
	int i$ = 0;
	for (;;) {
	    if (i$ >= len$) {
		UNBOX_METHODS.add("java/lang/Character.charValue:()C");
		UNBOX_METHODS.add("java/lang/Boolean.booleanValue:()Z");
	    }
	    String boxType = arr$[i$];
	    String[] arr$_0_ = unboxMethods;
	    int len$_1_ = arr$_0_.length;
	    int i$_2_ = 0;
	    for (;;) {
		if (i$_2_ >= len$_1_)
		    i$++;
		String unboxMethod = arr$_0_[i$_2_];
		UNBOX_METHODS.add(boxType + "." + unboxMethod);
		i$_2_++;
	    }
	}
    }
}
