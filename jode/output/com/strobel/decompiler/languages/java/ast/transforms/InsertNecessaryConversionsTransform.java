/* InsertNecessaryConversionsTransform - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import java.util.Iterator;

import com.strobel.assembler.metadata.BuiltinTypes;
import com.strobel.assembler.metadata.ConversionType;
import com.strobel.assembler.metadata.IMethodSignature;
import com.strobel.assembler.metadata.JvmType;
import com.strobel.assembler.metadata.MemberReference;
import com.strobel.assembler.metadata.MetadataHelper;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.core.CollectionUtilities;
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
import com.strobel.decompiler.languages.java.ast.UnaryOperatorType;
import com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement;
import com.strobel.decompiler.languages.java.ast.VariableInitializer;
import com.strobel.decompiler.languages.java.utilities.RedundantCastUtility;
import com.strobel.decompiler.languages.java.utilities.TypeUtilities;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.semantics.ResolveResult;
import com.strobel.functions.Function;

public class InsertNecessaryConversionsTransform extends ContextTrackingVisitor
{
    private static final ConvertTypeOptions NO_IMPORT_OPTIONS
	= new ConvertTypeOptions();
    private static final INode TRUE_NODE;
    private static final INode FALSE_NODE;
    private final JavaResolver _resolver;
    
    public InsertNecessaryConversionsTransform(DecompilerContext context) {
	super(context);
	_resolver = new JavaResolver(context);
    }
    
    public Void visitCastExpression(CastExpression node, Void data) {
	super.visitCastExpression(node, data);
	Expression operand = node.getExpression();
	ResolveResult targetResult = _resolver.apply(node.getType());
    label_1725:
	{
	label_1724:
	    {
		if (targetResult != null && targetResult.getType() != null) {
		    ResolveResult valueResult = _resolver.apply(operand);
		    if (valueResult != null && valueResult.getType() != null) {
			ConversionType conversionType
			    = MetadataHelper.getConversionType(targetResult
								   .getType(),
							       valueResult
								   .getType());
			if (conversionType == ConversionType.NONE)
			    addCastForAssignment(node.getType(),
						 node.getExpression());
		    } else
			return null;
		} else
		    return null;
	    }
	    if (RedundantCastUtility.isCastRedundant(_resolver, node))
		RedundantCastUtility.removeCast(node);
	    break label_1725;
	}
	return null;
	break label_1724;
    }
    
    public Void visitMemberReferenceExpression(MemberReferenceExpression node,
					       Void data) {
	super.visitMemberReferenceExpression(node, data);
	MemberReference member;
    label_1726:
	{
	    member = (MemberReference) node.getUserData(Keys.MEMBER_REFERENCE);
	    if (member == null && node.getParent() != null
		&& node.getRole() == Roles.TARGET_EXPRESSION)
		member = ((MemberReference)
			  node.getParent().getUserData(Keys.MEMBER_REFERENCE));
	    break label_1726;
	}
	AstBuilder astBuilder;
	TypeReference declaringType;
    label_1727:
	{
	    if (member != null) {
		astBuilder
		    = (AstBuilder) context.getUserData(Keys.AST_BUILDER);
		if (astBuilder != null) {
		    ResolveResult valueResult
			= _resolver.apply(node.getTarget());
		    declaringType = member.getDeclaringType();
		    if (valueResult != null && valueResult.getType() != null) {
			if (!MetadataHelper.isAssignableFrom(declaringType,
							     valueResult
								 .getType())) {
			    if (valueResult.getType().isGenericType()
				&& (declaringType.isGenericType()
				    || MetadataHelper
					   .isRawType(declaringType))) {
				TypeReference asSuper
				    = MetadataHelper.asSuper(declaringType,
							     valueResult
								 .getType());
				if (asSuper != null)
				    declaringType = asSuper;
			    }
			} else
			    return null;
		    }
		} else
		    return null;
	    } else
		return null;
	}
	addCastForAssignment(astBuilder.convertType(declaringType,
						    NO_IMPORT_OPTIONS),
			     node.getTarget());
	return null;
	break label_1727;
    }
    
    public Void visitAssignmentExpression(AssignmentExpression node,
					  Void data) {
	super.visitAssignmentExpression(node, data);
	addCastForAssignment(node.getLeft(), node.getRight());
	return null;
    }
    
    public Void visitVariableDeclaration(VariableDeclarationStatement node,
					 Void data) {
	super.visitVariableDeclaration(node, data);
	Iterator i$ = node.getVariables().iterator();
	for (;;) {
	    if (!i$.hasNext())
		return null;
	    VariableInitializer initializer = (VariableInitializer) i$.next();
	    addCastForAssignment(node, initializer.getInitializer());
	}
    }
    
    public Void visitReturnStatement(ReturnStatement node, Void data) {
	super.visitReturnStatement(node, data);
	AstNode function
	    = ((AstNode)
	       (CollectionUtilities.firstOrDefault
		(node.getAncestors(),
		 Predicates.or(Predicates.instanceOf(MethodDeclaration.class),
			       Predicates
				   .instanceOf(LambdaExpression.class)))));
	AstType left;
    label_1728:
	{
	    if (function != null) {
		if (!(function instanceof MethodDeclaration)) {
		    TypeReference expectedType
			= TypeUtilities.getExpectedTypeByParent(_resolver,
								((Expression)
								 function));
		    if (expectedType != null) {
			AstBuilder astBuilder
			    = ((AstBuilder)
			       context.getUserData(Keys.AST_BUILDER));
			if (astBuilder != null) {
			    IMethodSignature method
				= (TypeUtilities.getLambdaSignature
				   ((LambdaExpression) function));
			    if (method != null)
				left = (astBuilder.convertType
					(method.getReturnType(),
					 NO_IMPORT_OPTIONS));
			    else
				return null;
			} else
			    return null;
		    } else
			return null;
		} else
		    left = ((MethodDeclaration) function).getReturnType();
	    } else
		return null;
	}
	Expression right = node.getExpression();
	addCastForAssignment(left, right);
	return null;
	break label_1728;
    }
    
    private boolean addCastForAssignment(AstNode left,
					 final Expression right) {
	final ResolveResult targetResult = _resolver.apply(left);
	AstNode replacement;
    label_1729:
	{
	    if (targetResult != null && targetResult.getType() != null) {
		ResolveResult valueResult = _resolver.apply(right);
		if (valueResult != null && valueResult.getType() != null) {
		    TypeReference unboxedTargetType
			= (MetadataHelper.getUnderlyingPrimitiveTypeOrSelf
			   (targetResult.getType()));
		    if (!(right instanceof PrimitiveExpression)
			|| !(TypeUtilities.isValidPrimitiveLiteralAssignment
			     (unboxedTargetType,
			      ((PrimitiveExpression) right).getValue()))) {
			ConversionType conversionType
			    = MetadataHelper.getConversionType(targetResult
								   .getType(),
							       valueResult
								   .getType());
			replacement = null;
			if (conversionType != ConversionType.EXPLICIT
			    && (conversionType
				!= ConversionType.EXPLICIT_TO_UNBOXED)) {
			    if (conversionType == ConversionType.NONE) {
				if ((valueResult.getType().getSimpleType()
				     != JvmType.Boolean)
				    || (targetResult.getType().getSimpleType()
					== JvmType.Boolean)
				    || !targetResult.getType().getSimpleType
					    ().isNumeric()) {
				    if ((targetResult.getType().getSimpleType()
					 != JvmType.Boolean)
					|| (valueResult.getType()
						.getSimpleType()
					    == JvmType.Boolean)
					|| !valueResult.getType().getSimpleType
						().isNumeric()) {
					final AstBuilder astBuilder
					    = ((AstBuilder)
					       (context.getUserData
						(Keys.AST_BUILDER)));
					if (astBuilder != null)
					    replacement = right
							      .replaceWith(new Function() {
						{
						    super();
						}
						
						public AstNode apply
						    (AstNode input) {
						    return (new CastExpression
							    ((astBuilder
								  .convertType
							      (BuiltinTypes
							       .Object)),
							     right));
						}
					    });
				    } else
					replacement
					    = (convertNumericToBoolean
					       (right, valueResult.getType()));
				} else {
				    replacement
					= convertBooleanToNumeric(right);
				    if (targetResult.getType().getSimpleType
					    ().bitWidth()
					< 32) {
					final AstBuilder astBuilder
					    = ((AstBuilder)
					       (context.getUserData
						(Keys.AST_BUILDER)));
					if (astBuilder != null)
					    replacement = replacement
							      .replaceWith(new Function() {
						{
						    super();
						}
						
						public AstNode apply
						    (AstNode input) {
						    return (new CastExpression
							    ((astBuilder
								  .convertType
							      (targetResult
								   .getType
							       ())),
							     ((Expression)
							      input)));
						}
					    });
				    }
				}
			    }
			} else {
			    AstBuilder astBuilder
				= ((AstBuilder)
				   context.getUserData(Keys.AST_BUILDER));
			    if (astBuilder != null) {
				ConvertTypeOptions convertTypeOptions
				    = new ConvertTypeOptions();
				convertTypeOptions.setAllowWildcards(false);
				final AstType castToType
				    = (astBuilder.convertType
				       (targetResult.getType(),
					convertTypeOptions));
				replacement = right
						  .replaceWith(new Function() {
				    {
					super();
				    }
				    
				    public Expression apply(AstNode e) {
					return new CastExpression(castToType,
								  right);
				    }
				});
			    } else
				return false;
			}
		    } else
			return false;
		} else
		    return false;
	    } else
		return false;
	}
	if (replacement == null)
	    return false;
	recurse(replacement);
	return true;
	break label_1729;
    }
    
    public Void visitUnaryOperatorExpression(UnaryOperatorExpression node,
					     Void data) {
	super.visitUnaryOperatorExpression(node, data);
	switch (ANONYMOUS CLASS com.strobel.decompiler.languages.java.ast.transforms.InsertNecessaryConversionsTransform$7.$SwitchMap$com$strobel$decompiler$languages$java$ast$UnaryOperatorType[node.getOperator().ordinal()]) {
	case 1: {
	    final Expression operand = node.getExpression();
	label_1730:
	    {
		ResolveResult result = _resolver.apply(operand);
		if (result != null && result.getType() != null
		    && !TypeUtilities.isBoolean(result.getType())
		    && MetadataHelper.getUnderlyingPrimitiveTypeOrSelf
			   (result.getType()).getSimpleType
			   ().isNumeric()) {
		    final TypeReference comparandType
			= (MetadataHelper.getUnderlyingPrimitiveTypeOrSelf
			   (result.getType()));
		    operand.replaceWith(new Function() {
			{
			    super();
			}
			
			public AstNode apply(AstNode input) {
			    return (new BinaryOperatorExpression
				    (operand, BinaryOperatorType.INEQUALITY,
				     (new PrimitiveExpression
				      (-34, (JavaPrimitiveCast.cast
					     (comparandType.getSimpleType(),
					      Integer.valueOf(0)))))));
			}
		    });
		}
		break label_1730;
	    }
	}
	    /* fall through */
	default:
	    return null;
	}
    }
    
    public Void visitBinaryOperatorExpression(BinaryOperatorExpression node,
					      Void data) {
	super.visitBinaryOperatorExpression(node, data);
	switch (ANONYMOUS CLASS com.strobel.decompiler.languages.java.ast.transforms.InsertNecessaryConversionsTransform$7.$SwitchMap$com$strobel$decompiler$languages$java$ast$BinaryOperatorType[node.getOperator().ordinal()]) {
	case 1:
	case 2:
	case 3:
	case 4:
	case 5:
	case 6:
	case 7:
	case 8:
	case 9:
	case 10:
	case 11:
	case 12:
	case 13:
	case 14: {
	    Expression left = node.getLeft();
	    Expression right = node.getRight();
	    ResolveResult leftResult = _resolver.apply(left);
	    ResolveResult rightResult = _resolver.apply(right);
	    if (leftResult != null && rightResult != null
		&& (TypeUtilities.isBoolean(leftResult.getType())
		    ^ TypeUtilities.isBoolean(rightResult.getType()))) {
		if (!TypeUtilities.isArithmetic(rightResult.getType())) {
		    if (TypeUtilities.isArithmetic(leftResult.getType()))
			convertBooleanToNumeric(right);
		} else
		    convertBooleanToNumeric(left);
	    }
	    break;
	}
	case 15:
	case 16:
	case 17: {
	    Expression left = node.getLeft();
	    Expression right = node.getRight();
	    ResolveResult leftResult = _resolver.apply(left);
	    ResolveResult rightResult = _resolver.apply(right);
	    if (leftResult == null || leftResult.getType() == null
		|| rightResult == null || rightResult.getType() == null
		|| !(TypeUtilities.isBoolean(leftResult.getType())
		     ^ TypeUtilities.isBoolean(rightResult.getType()))) {
		TypeReference expectedType
		    = TypeUtilities.getExpectedTypeByParent(_resolver, node);
		if (expectedType != null
		    && TypeUtilities.isBoolean(expectedType)) {
		    ResolveResult result = _resolver.apply(node);
		    if (result != null && result.getType() != null
			&& TypeUtilities.isArithmetic(result.getType()))
			convertNumericToBoolean(node, result.getType());
		}
	    } else if (!TypeUtilities.isBoolean(leftResult.getType())
		       || !TypeUtilities.isArithmetic(rightResult.getType())) {
		if (TypeUtilities.isArithmetic(leftResult.getType())) {
		    TypeReference comparandType
			= (MetadataHelper.getUnderlyingPrimitiveTypeOrSelf
			   (leftResult.getType()));
		    if (!TRUE_NODE.matches(right)) {
			if (!FALSE_NODE.matches(right))
			    convertBooleanToNumeric(right);
			else
			    ((PrimitiveExpression) right).setValue
				(JavaPrimitiveCast.cast(comparandType
							    .getSimpleType(),
							Integer.valueOf(0)));
		    } else
			((PrimitiveExpression) right).setValue
			    (JavaPrimitiveCast.cast(comparandType
							.getSimpleType(),
						    Integer.valueOf(1)));
		}
	    } else {
		TypeReference comparandType
		    = (MetadataHelper.getUnderlyingPrimitiveTypeOrSelf
		       (rightResult.getType()));
		if (!TRUE_NODE.matches(left)) {
		    if (!FALSE_NODE.matches(left))
			convertBooleanToNumeric(left);
		    else
			((PrimitiveExpression) left).setValue
			    (JavaPrimitiveCast.cast(comparandType
							.getSimpleType(),
						    Integer.valueOf(0)));
		} else
		    ((PrimitiveExpression) left).setValue
			(JavaPrimitiveCast.cast(comparandType.getSimpleType(),
						Integer.valueOf(1)));
	    }
	    break;
	}
	}
	return null;
    }
    
    public Void visitIfElseStatement(IfElseStatement node, Void data) {
	super.visitIfElseStatement(node, data);
	Expression condition = node.getCondition();
    label_1731:
	{
	    ResolveResult conditionResult = _resolver.apply(condition);
	    if (conditionResult != null
		&& TypeUtilities.isArithmetic(conditionResult.getType()))
		convertNumericToBoolean(condition, conditionResult.getType());
	    break label_1731;
	}
	return null;
    }
    
    public Void visitConditionalExpression(ConditionalExpression node,
					   Void data) {
	super.visitConditionalExpression(node, data);
	Expression condition = node.getCondition();
    label_1732:
	{
	    ResolveResult conditionResult = _resolver.apply(condition);
	    if (conditionResult != null
		&& TypeUtilities.isArithmetic(conditionResult.getType()))
		convertNumericToBoolean(condition, conditionResult.getType());
	    break label_1732;
	}
	return null;
    }
    
    private Expression convertNumericToBoolean(final Expression node,
					       final TypeReference type) {
	return (Expression) node.replaceWith(new Function() {
	    {
		super();
	    }
	    
	    public Expression apply(AstNode input) {
		return (new BinaryOperatorExpression
			(node, BinaryOperatorType.INEQUALITY,
			 (new PrimitiveExpression
			  (-34,
			   (JavaPrimitiveCast.cast
			    (MetadataHelper.getUnderlyingPrimitiveTypeOrSelf
				 (type).getSimpleType(),
			     Integer.valueOf(0)))))));
	    }
	});
    }
    
    private Expression convertBooleanToNumeric(Expression operand) {
	Expression e;
	boolean invert;
    label_1733:
	{
	    e = operand;
	    if (!(e instanceof UnaryOperatorExpression)
		|| (((UnaryOperatorExpression) e).getOperator()
		    != UnaryOperatorType.NOT))
		invert = false;
	    else {
		Expression inner
		    = ((UnaryOperatorExpression) e).getExpression();
		inner.remove();
		e.replaceWith(inner);
		e = inner;
		invert = true;
	    }
	    break label_1733;
	}
	return (Expression) e.replaceWith(new Function(invert) {
	    /*synthetic*/ final boolean val$invert;
	    
	    null(boolean bool) {
		val$invert = bool;
		super();
	    }
	    
	    public AstNode apply(AstNode input) {
		PUSH new ConditionalExpression;
		DUP
		PUSH (Expression) input;
		PUSH new PrimitiveExpression;
		DUP
	    label_1734:
		{
		    PUSH -34;
		    if (!val$invert)
			PUSH true;
		    else
			PUSH false;
		    break label_1734;
		}
		((UNCONSTRUCTED)POP).PrimitiveExpression(POP,
							 Integer.valueOf(POP));
		PUSH new PrimitiveExpression;
		DUP
	    label_1735:
		{
		    PUSH -34;
		    if (!val$invert)
			PUSH false;
		    else
			PUSH true;
		    break label_1735;
		}
		((UNCONSTRUCTED)POP).PrimitiveExpression(POP,
							 Integer.valueOf(POP));
		((UNCONSTRUCTED)POP).ConditionalExpression(POP, POP, POP);
		return POP;
	    }
	});
    }
    
    private void recurse(AstNode replacement) {
	AstNode parent = replacement.getParent();
	if (parent == null)
	    replacement.acceptVisitor(this, null);
	else
	    parent.acceptVisitor(this, null);
	return;
    }
    
    static {
	NO_IMPORT_OPTIONS.setAddImports(false);
	TRUE_NODE = new PrimitiveExpression(-34, Boolean.valueOf(true));
	FALSE_NODE = new PrimitiveExpression(-34, Boolean.valueOf(false));
    }
}
