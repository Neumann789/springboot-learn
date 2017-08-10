/* RedundantCastUtility - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.utilities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.strobel.assembler.metadata.BuiltinTypes;
import com.strobel.assembler.metadata.CompoundTypeReference;
import com.strobel.assembler.metadata.ConversionType;
import com.strobel.assembler.metadata.DynamicCallSite;
import com.strobel.assembler.metadata.IGenericInstance;
import com.strobel.assembler.metadata.IMetadataResolver;
import com.strobel.assembler.metadata.MemberReference;
import com.strobel.assembler.metadata.MetadataFilters;
import com.strobel.assembler.metadata.MetadataHelper;
import com.strobel.assembler.metadata.MetadataParser;
import com.strobel.assembler.metadata.MethodBinder;
import com.strobel.assembler.metadata.MethodDefinition;
import com.strobel.assembler.metadata.MethodReference;
import com.strobel.assembler.metadata.ParameterDefinition;
import com.strobel.assembler.metadata.PrimitiveType;
import com.strobel.assembler.metadata.RawType;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.assembler.metadata.annotations.CustomAnnotation;
import com.strobel.core.CollectionUtilities;
import com.strobel.core.StringUtilities;
import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.languages.java.ast.AnonymousObjectCreationExpression;
import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
import com.strobel.decompiler.languages.java.ast.AstNode;
import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
import com.strobel.decompiler.languages.java.ast.AstType;
import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
import com.strobel.decompiler.languages.java.ast.BinaryOperatorType;
import com.strobel.decompiler.languages.java.ast.CastExpression;
import com.strobel.decompiler.languages.java.ast.ConditionalExpression;
import com.strobel.decompiler.languages.java.ast.ConstructorDeclaration;
import com.strobel.decompiler.languages.java.ast.DepthFirstAstVisitor;
import com.strobel.decompiler.languages.java.ast.Expression;
import com.strobel.decompiler.languages.java.ast.FieldDeclaration;
import com.strobel.decompiler.languages.java.ast.IndexerExpression;
import com.strobel.decompiler.languages.java.ast.InvocationExpression;
import com.strobel.decompiler.languages.java.ast.Keys;
import com.strobel.decompiler.languages.java.ast.LambdaExpression;
import com.strobel.decompiler.languages.java.ast.LocalTypeDeclarationStatement;
import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
import com.strobel.decompiler.languages.java.ast.MethodGroupExpression;
import com.strobel.decompiler.languages.java.ast.ObjectCreationExpression;
import com.strobel.decompiler.languages.java.ast.ParenthesizedExpression;
import com.strobel.decompiler.languages.java.ast.PrimitiveExpression;
import com.strobel.decompiler.languages.java.ast.ReturnStatement;
import com.strobel.decompiler.languages.java.ast.Roles;
import com.strobel.decompiler.languages.java.ast.SynchronizedStatement;
import com.strobel.decompiler.languages.java.ast.TypeDeclaration;
import com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement;
import com.strobel.decompiler.languages.java.ast.VariableInitializer;
import com.strobel.decompiler.semantics.ResolveResult;
import com.strobel.functions.Function;

public final class RedundantCastUtility
{
    private static class IsRedundantVisitor extends DepthFirstAstVisitor
    {
	private final boolean _isRecursive;
	private final Function _resolver;
	private boolean _isRedundant;
	
	IsRedundantVisitor(Function resolver, boolean recursive) {
	    _isRecursive = recursive;
	    _resolver = resolver;
	}
	
	public final boolean isRedundant() {
	    return _isRedundant;
	}
	
	protected Void visitChildren(AstNode node, Void data) {
	    if (!_isRecursive)
		return null;
	    return (Void) super.visitChildren(node, data);
	}
	
	public Void visitAssignmentExpression(AssignmentExpression node,
					      Void data) {
	    processPossibleTypeCast(node.getRight(), getType(node.getLeft()));
	    return (Void) super.visitAssignmentExpression(node, data);
	}
	
	public Void visitVariableDeclaration(VariableDeclarationStatement node,
					     Void data) {
	label_1812:
	    {
		TypeReference leftType = getType(node.getType());
		if (leftType != null) {
		    Iterator i$ = node.getVariables().iterator();
		    while (i$.hasNext()) {
			VariableInitializer initializer
			    = (VariableInitializer) i$.next();
			processPossibleTypeCast(initializer.getInitializer(),
						leftType);
		    }
		}
		break label_1812;
	    }
	    return (Void) super.visitVariableDeclaration(node, data);
	}
	
	public Void visitFieldDeclaration(FieldDeclaration node, Void data) {
	label_1813:
	    {
		TypeReference leftType = getType(node.getReturnType());
		if (leftType != null) {
		    Iterator i$ = node.getVariables().iterator();
		    while (i$.hasNext()) {
			VariableInitializer initializer
			    = (VariableInitializer) i$.next();
			processPossibleTypeCast(initializer.getInitializer(),
						leftType);
		    }
		}
		break label_1813;
	    }
	    return (Void) super.visitFieldDeclaration(node, data);
	}
	
	public Void visitReturnStatement(ReturnStatement node, Void data) {
	label_1814:
	    {
		MethodDeclaration methodDeclaration
		    = ((MethodDeclaration)
		       (CollectionUtilities.firstOrDefault
			(node.getAncestors(MethodDeclaration.class))));
		if (methodDeclaration != null && !methodDeclaration.isNull()) {
		    TypeReference returnType
			= getType(methodDeclaration.getReturnType());
		    Expression returnValue = node.getExpression();
		    if (returnType != null && returnValue != null
			&& !returnValue.isNull())
			processPossibleTypeCast(returnValue, returnType);
		}
		break label_1814;
	    }
	    return (Void) super.visitReturnStatement(node, data);
	}
	
	public Void visitBinaryOperatorExpression
	    (BinaryOperatorExpression node, Void data) {
	    TypeReference leftType = getType(node.getLeft());
	    TypeReference rightType = getType(node.getRight());
	    processBinaryExpressionOperand(node.getLeft(), rightType,
					   node.getOperator());
	    processBinaryExpressionOperand(node.getRight(), leftType,
					   node.getOperator());
	    return (Void) super.visitBinaryOperatorExpression(node, data);
	}
	
	public Void visitInvocationExpression(InvocationExpression node,
					      Void data) {
	    super.visitInvocationExpression(node, data);
	    processCall(node);
	    return null;
	}
	
	public Void visitObjectCreationExpression
	    (ObjectCreationExpression node, Void data) {
	    Iterator i$ = node.getArguments().iterator();
	    for (;;) {
		if (!i$.hasNext()) {
		    processCall(node);
		    return null;
		}
		Expression argument = (Expression) i$.next();
		argument.acceptVisitor(this, data);
	    }
	}
	
	public Void visitAnonymousObjectCreationExpression
	    (AnonymousObjectCreationExpression node, Void data) {
	    Iterator i$ = node.getArguments().iterator();
	    for (;;) {
		if (!i$.hasNext()) {
		    processCall(node);
		    return null;
		}
		Expression argument = (Expression) i$.next();
		argument.acceptVisitor(this, data);
	    }
	}
	
	public Void visitCastExpression(CastExpression node, Void data) {
	    Expression operand = node.getExpression();
	label_1819:
	    {
	    label_1818:
		{
		    TypeReference conditionalType;
		label_1815:
		    {
			if (operand != null && !operand.isNull()) {
			    TypeReference topCastType = getType(node);
			    if (topCastType != null) {
				Expression e = removeParentheses(operand);
				if (!(e instanceof CastExpression)) {
				    AstNode parent = node.getParent();
				    if (!(parent
					  instanceof ConditionalExpression)) {
					TypeReference functionalInterfaceType;
				    label_1817:
					{
					label_1816:
					    {
						if (!(parent
						      instanceof SynchronizedStatement)
						    || !(getType(e)
							 instanceof PrimitiveType)) {
						    if (!(e
							  instanceof LambdaExpression)
							&& !(e
							     instanceof MethodGroupExpression))
							break label_1818;
						    if (!(parent
							  instanceof ParenthesizedExpression)
							|| (parent.getParent()
							    == null)
							|| !(parent.getParent
								 ().isReference
							     ())) {
							ResolveResult lambdaResult
							    = ((ResolveResult)
							       _resolver
								   .apply(e));
							if ((lambdaResult
							     == null)
							    || (lambdaResult
								    .getType()
								== null)) {
							    DynamicCallSite callSite
								= ((DynamicCallSite)
								   (e.getUserData
								    (Keys
								     .DYNAMIC_CALL_SITE)));
							    if (callSite
								!= null)
								functionalInterfaceType
								    = (callSite
									   .getMethodType
									   ()
									   .getReturnType
								       ());
							    else
								return null;
							    break label_1817;
							}
							TypeReference asSubType
							    = (MetadataHelper
								   .asSubType
							       (lambdaResult
								    .getType(),
								topCastType));
							if (asSubType == null)
							    PUSH (lambdaResult
								      .getType
								  ());
							else
							    PUSH asSubType;
						    } else
							return null;
						} else
						    return null;
					    }
					    functionalInterfaceType = POP;
					}
					if (!MetadataHelper.isAssignableFrom
					     (topCastType,
					      functionalInterfaceType, false))
					    return null;
					break label_1818;
					break label_1816;
				    }
				    TypeReference operandType
					= getType(operand);
				    conditionalType = getType(parent);
				    if (MetadataHelper.isSameType
					(operandType, conditionalType, true)) {
					if (topCastType.isPrimitive()
					    && !operandType.isPrimitive())
					    return null;
					break label_1818;
				    }
				    if (checkResolveAfterRemoveCast(parent)) {
					Expression thenExpression
					    = ((ConditionalExpression) parent)
						  .getTrueExpression();
					Expression elseExpression
					    = ((ConditionalExpression) parent)
						  .getFalseExpression();
					if (thenExpression != node)
					    PUSH thenExpression;
					else
					    PUSH elseExpression;
				    } else
					return null;
				} else {
				    CastExpression innerCast
					= (CastExpression) e;
				    TypeReference innerCastType
					= getType(innerCast.getType());
				    if (innerCastType != null) {
					Expression innerOperand
					    = innerCast.getExpression();
					TypeReference innerOperandType
					    = getType(innerOperand);
					if (innerCastType.isPrimitive()) {
					    ConversionType valueToInner
						= (MetadataHelper
						       .getNumericConversionType
						   (innerCastType,
						    innerOperandType));
					    ConversionType outerToInner
						= (MetadataHelper
						       .getNumericConversionType
						   (innerCastType,
						    topCastType));
					    if (outerToInner
						!= ConversionType.IDENTITY) {
						if (outerToInner
						    != (ConversionType
							.IMPLICIT)) {
						    if ((valueToInner
							 == (ConversionType
							     .IMPLICIT))
							&& ((MetadataHelper
								 .getNumericConversionType
							     (topCastType,
							      innerOperandType))
							    == (ConversionType
								.IMPLICIT)))
							addToResults(innerCast,
								     true);
						} else {
						    ConversionType valueToOuter
							= (MetadataHelper
							       .getNumericConversionType
							   (topCastType,
							    innerOperandType));
						    if (valueToOuter
							!= ConversionType.NONE)
							addToResults(innerCast,
								     true);
						}
					    } else if (valueToInner
						       != (ConversionType
							   .IDENTITY))
						addToResults(innerCast, true);
					    else {
						addToResults(node, false);
						addToResults(innerCast, true);
					    }
					} else if (innerOperandType != null
						   && ((MetadataHelper
							    .getConversionType
							(topCastType,
							 innerOperandType))
						       != ConversionType.NONE))
					    addToResults(innerCast, false);
				    } else
					return null;
				    break label_1819;
				}
			    } else
				return null;
			} else
			    return null;
		    }
		    Expression opposite = POP;
		    TypeReference oppositeType = getType(opposite);
		    if (oppositeType == null
			|| !MetadataHelper.isSameType(conditionalType,
						      oppositeType, true))
			return null;
		}
		processAlreadyHasTypeCast(node);
	    }
	    return (Void) super.visitCastExpression(node, data);
	    break label_1815;
	}
	
	protected TypeReference getType(AstNode node) {
	label_1820:
	    {
		ResolveResult result = (ResolveResult) _resolver.apply(node);
		if (result == null)
		    PUSH null;
		else
		    PUSH result.getType();
		break label_1820;
	    }
	    return POP;
	}
	
	protected List getTypes(AstNodeCollection nodes) {
	    if (nodes != null && !nodes.isEmpty()) {
		List types = new ArrayList();
		Iterator i$ = nodes.iterator();
		for (;;) {
		    if (!i$.hasNext())
			return types;
		    AstNode node = (AstNode) i$.next();
		    TypeReference nodeType = getType(node);
		    if (nodeType != null)
			types.add(nodeType);
		    return Collections.emptyList();
		}
	    }
	    return Collections.emptyList();
	}
	
	protected void processPossibleTypeCast(Expression rightExpression,
					       TypeReference leftType) {
	    if (leftType != null) {
		Expression r = removeParentheses(rightExpression);
		if (r instanceof CastExpression) {
		label_1821:
		    {
			AstType castAstType = ((CastExpression) r).getType();
			if (castAstType == null)
			    PUSH null;
			else
			    PUSH castAstType.toTypeReference();
			break label_1821;
		    }
		    TypeReference castType = POP;
		    Expression castOperand
			= ((CastExpression) r).getExpression();
		    if (castOperand != null && !castOperand.isNull()
			&& castType != null) {
			TypeReference operandType = getType(castOperand);
			if (operandType != null) {
			    if (!MetadataHelper.isAssignableFrom(leftType,
								 operandType,
								 false)) {
				TypeReference unboxedCastType
				    = (MetadataHelper
					   .getUnderlyingPrimitiveTypeOrSelf
				       (castType));
				TypeReference unboxedLeftType
				    = (MetadataHelper
					   .getUnderlyingPrimitiveTypeOrSelf
				       (leftType));
				if (castOperand instanceof PrimitiveExpression
				    && (TypeUtilities
					    .isValidPrimitiveLiteralAssignment
					(unboxedCastType,
					 ((PrimitiveExpression) castOperand)
					     .getValue()))
				    && (TypeUtilities
					    .isValidPrimitiveLiteralAssignment
					(unboxedLeftType,
					 ((PrimitiveExpression) castOperand)
					     .getValue())))
				    addToResults((CastExpression) r, true);
			    } else
				addToResults((CastExpression) r, false);
			}
		    }
		}
	    }
	    return;
	}
	
	protected void addToResults(CastExpression cast, boolean force) {
	    if (force || !isTypeCastSemantic(cast))
		_isRedundant = true;
	    return;
	}
	
	protected void processBinaryExpressionOperand(Expression operand,
						      TypeReference otherType,
						      BinaryOperatorType op) {
	    if (operand instanceof CastExpression) {
		CastExpression cast = (CastExpression) operand;
		Expression toCast = cast.getExpression();
		TypeReference castType = getType(cast);
		TypeReference innerType = getType(toCast);
		if (castType != null && innerType != null
		    && TypeUtilities.isBinaryOperatorApplicable(op, innerType,
								otherType,
								false))
		    addToResults(cast, false);
	    }
	    return;
	}
	
	protected void processCall(Expression e) {
	    AstNodeCollection arguments = e.getChildrenByRole(Roles.ARGUMENT);
	    if (!arguments.isEmpty()) {
		MemberReference reference;
	    label_1822:
		{
		    reference = ((MemberReference)
				 e.getUserData(Keys.MEMBER_REFERENCE));
		    if (reference == null
			&& e.getParent() instanceof MemberReferenceExpression)
			reference = ((MemberReference)
				     e.getParent()
					 .getUserData(Keys.MEMBER_REFERENCE));
		    break label_1822;
		}
		if (reference instanceof MethodReference) {
		    MethodReference method = (MethodReference) reference;
		    Expression target;
		label_1823:
		    {
			target = ((Expression)
				  e.getChildByRole(Roles.TARGET_EXPRESSION));
			if (target instanceof MemberReferenceExpression)
			    target
				= ((Expression)
				   target.getChildByRole(Roles
							 .TARGET_EXPRESSION));
			break label_1823;
		    }
		    TypeReference targetType = getType(target);
		label_1826:
		    {
		    label_1824:
			{
			    if (targetType != null) {
				if (targetType instanceof RawType
				    || !MetadataHelper.isRawType(targetType)) {
				    TypeReference asSuper
					= (MetadataHelper.asSuper
					   (method.getDeclaringType(),
					    targetType));
				    if (asSuper == null)
					PUSH null;
				    else
					PUSH (MetadataHelper.asSubType
					      (method.getDeclaringType(),
					       asSuper));
				} else {
				    targetType
					= MetadataHelper
					      .eraseRecursive(targetType);
				    break label_1826;
				}
			    } else {
				targetType = method.getDeclaringType();
				break label_1826;
			    }
			}
		    label_1825:
			{
			    TypeReference asSubType = POP;
			    if (asSubType == null)
				PUSH targetType;
			    else
				PUSH asSubType;
			    break label_1825;
			}
			targetType = POP;
		    }
		    List candidates
			= (MetadataHelper.findMethods
			   (targetType,
			    MetadataFilters.matchName(method.getName())));
		    MethodDefinition resolvedMethod = method.resolve();
		    List originalTypes = new ArrayList();
		    List parameters = method.getParameters();
		    Expression lastArgument
			= (Expression) arguments.lastOrNullObject();
		    List newTypes = null;
		    int syntheticLeadingCount = 0;
		    int syntheticTrailingCount = 0;
		    Iterator i$ = parameters.iterator();
		    while (i$.hasNext()) {
			ParameterDefinition parameter;
			parameter = (ParameterDefinition) i$.next();
			if (parameter.isSynthetic()) {
			    syntheticLeadingCount++;
			    originalTypes.add(parameter.getParameterType());
			}
			break;
		    }
		    int i = parameters.size() - 1;
		    for (;;) {
			if (i < 0 || !((ParameterDefinition) parameters.get(i))
					  .isSynthetic()) {
			    i$ = arguments.iterator();
			    for (;;) {
				if (!i$.hasNext()) {
				    int realParametersEnd
					= (parameters.size()
					   - syntheticTrailingCount);
				    int i_0_ = realParametersEnd;
				    for (;;) {
					if (i_0_ >= parameters.size()) {
					    int i_1_ = syntheticLeadingCount;
					    for (Expression a
						     = ((Expression)
							(arguments
							     .firstOrNullObject
							 ()));
						 (i_1_ < realParametersEnd
						  && a != null && !a.isNull());
						 i_1_++) {
					    label_1831:
						{
						    Expression arg
							= removeParentheses(a);
						    if ((arg
							 instanceof CastExpression)
							&& (a != lastArgument
							    || (i_1_
								!= (parameters
									.size()
								    - 1))
							    || (resolvedMethod
								== null)
							    || !(resolvedMethod
								     .isVarArgs
								 ()))) {
							CastExpression cast
							    = ((CastExpression)
							       arg);
							Expression castOperand
							    = (cast.getExpression
							       ());
							TypeReference castType
							    = getType(cast);
							TypeReference operandType
							    = (getType
							       (castOperand));
						    label_1828:
							{
							label_1827:
							    {
								if ((castType
								     != null)
								    && (operandType
									!= null)) {
								    if (castType
									    .isPrimitive()
									&& !operandType.isPrimitive()) {
									ParameterDefinition p
									    = (ParameterDefinition) parameters.get(i_1_);
									TypeReference parameterType
									    = p.getParameterType();
									if (!parameterType.isPrimitive
									     ())
									    break label_1831;
								    }
								    break label_1827;
								}
								break label_1831;
							    }
							    if (newTypes
								!= null) {
								newTypes
								    .clear();
								newTypes.addAll
								    (originalTypes);
							    } else
								newTypes
								    = (new ArrayList
								       (originalTypes));
							    break label_1828;
							}
							newTypes.set
							    (i_1_,
							     operandType);
							MethodBinder.BindResult result
							    = (MethodBinder
								   .selectMethod
							       (candidates,
								newTypes));
							if (!result.isFailure()
							    && !(result
								     .isAmbiguous
								 ())) {
							    boolean sameMethod
								= (StringUtilities
								       .equals
								   ((method
									 .getErasedSignature
								     ()),
								    (result
									 .getMethod
									 ()
									 .getErasedSignature
								     ())));
							    if (sameMethod) {
								ParameterDefinition newParameter
								    = ((ParameterDefinition)
								       (result
									    .getMethod
									    ()
									    .getParameters
									    ()
									    .get
									(i_1_)));
							    label_1830:
								{
								label_1829:
								    {
									if (castType.isPrimitive
									    ()) {
									    if (MetadataHelper.isSameType(castType, MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(newParameter.getParameterType())))
										PUSH false;
									    else
										PUSH true;
									    break label_1829;
									}
									break label_1830;
								    }
								    boolean castNeeded
									= POP;
								    if (castNeeded)
									break label_1831;
								}
								if (MetadataHelper
									.isAssignableFrom
								    ((newParameter
									  .getParameterType
								      ()),
								     castType))
								    addToResults
									(cast,
									 false);
							    }
							}
						    }
						    break label_1831;
						}
						a = ((Expression)
						     (a.getNextSibling
						      (Roles.ARGUMENT)));
					    }
					    return;
					}
					originalTypes.add
					    (((ParameterDefinition)
					      parameters.get(i_0_))
						 .getParameterType());
					i_0_++;
				    }
				    return;
				}
				Expression argument = (Expression) i$.next();
				TypeReference argumentType = getType(argument);
				if (argumentType == null)
				    break;
				originalTypes.add(argumentType);
			    }
			    break;
			}
			i--;
			syntheticTrailingCount++;
		    }
		    return;
		    break label_1824;
		} else {
		    /* empty */
		}
	    }
	    return;
	}
	
	protected void processAlreadyHasTypeCast(CastExpression cast) {
	    AstNode parent = cast.getParent();
	    TypeReference castTo;
	    Expression operand;
	    TypeReference operandType;
	    TypeReference expectedType;
	label_1833:
	    {
	    label_1832:
		{
		    for (;;) {
			if (!(parent instanceof ParenthesizedExpression)) {
			    if (parent != null
				&& (cast.getRole() != Roles.ARGUMENT
				    || parent instanceof IndexerExpression)
				&& !(parent instanceof AssignmentExpression)
				&& !(parent instanceof ReturnStatement)
				&& !(parent instanceof CastExpression)
				&& !(parent
				     instanceof BinaryOperatorExpression)
				&& !isTypeCastSemantic(cast)) {
				castTo = getType(cast.getType());
				operand = cast.getExpression();
				operandType = getType(operand);
				if (castTo != null && operandType != null) {
				    expectedType
					= (TypeUtilities
					       .getExpectedTypeByParent
					   (_resolver, cast));
				    if (operandType != BuiltinTypes.Character)
					PUSH false;
				    else
					PUSH true;
				    break label_1832;
				}
			    }
			    break;
			}
			parent = parent.getParent();
		    }
		    return;
		}
		if (castTo != BuiltinTypes.Character)
		    PUSH false;
		else
		    PUSH true;
		break label_1833;
	    }
	label_1834:
	    {
		boolean isCharConversion = POP ^ POP;
		if (expectedType == null) {
		    if (isCharConversion)
			return;
		} else {
		    if (isCharConversion && !expectedType.isPrimitive())
			return;
		    operandType = expectedType;
		}
		break label_1834;
	    }
	label_1835:
	    {
		if (operandType != BuiltinTypes.Null
		    || !castTo.isPrimitive()) {
		    if (parent.isReference()) {
			if (operandType.isPrimitive() && !castTo.isPrimitive())
			    return;
			TypeReference referenceType = getType(parent);
			if (!operandType.isPrimitive() && referenceType != null
			    && !(isCastRedundantInReferenceExpression
				 (referenceType, operand)))
			    return;
		    }
		    break label_1835;
		}
		return;
	    }
	    if (!arrayAccessAtTheLeftSideOfAssignment(parent)) {
		if (MetadataHelper.isAssignableFrom(castTo, operandType,
						    false))
		    addToResults(cast, false);
	    } else if (MetadataHelper.isAssignableFrom(operandType, castTo,
						       false)
		       && (MetadataHelper.getArrayRank(operandType)
			   == MetadataHelper.getArrayRank(castTo)))
		addToResults(cast, false);
	    break label_1832;
	}
	
	protected boolean arrayAccessAtTheLeftSideOfAssignment(AstNode node) {
	    AssignmentExpression assignment
		= ((AssignmentExpression)
		   (CollectionUtilities.firstOrDefault
		    (node.getAncestors(AssignmentExpression.class))));
	label_1836:
	    {
		if (assignment != null) {
		    Expression left = assignment.getLeft();
		    if (!left.isAncestorOf(node)
			|| !(left instanceof IndexerExpression))
			PUSH false;
		    else
			PUSH true;
		} else
		    return false;
	    }
	    return POP;
	    break label_1836;
	}
	
	protected boolean isCastRedundantInReferenceExpression
	    (TypeReference type, Expression operand) {
	    return false;
	}
	
	protected boolean checkResolveAfterRemoveCast(AstNode parent) {
	    AstNode grandParent = parent.getParent();
	    TypeReference targetType;
	label_1837:
	    {
		if (grandParent != null
		    && parent.getRole() == Roles.ARGUMENT) {
		    if (!(grandParent instanceof InvocationExpression))
			targetType = getType(grandParent);
		    else
			targetType
			    = getType(((InvocationExpression) grandParent)
					  .getTarget());
		} else
		    return true;
	    }
	    AstNodeCollection arguments;
	    List argumentTypes;
	    MemberReference memberReference;
	label_1838:
	    {
		if (targetType != null) {
		    Expression expression = (Expression) grandParent.clone();
		    arguments = expression.getChildrenByRole(Roles.ARGUMENT);
		    argumentTypes = getTypes(arguments);
		    if (!argumentTypes.isEmpty()) {
			memberReference
			    = ((MemberReference)
			       grandParent.getUserData(Keys.MEMBER_REFERENCE));
			if (!(memberReference instanceof MethodReference)
			    && grandParent.getParent() != null)
			    memberReference
				= ((MemberReference)
				   grandParent.getParent()
				       .getUserData(Keys.MEMBER_REFERENCE));
		    } else
			return arguments.isEmpty();
		} else
		    return false;
	    }
	    MethodDefinition resolvedMethod;
	label_1840:
	    {
		int argumentIndex;
		Expression toReplace;
	    label_1839:
		{
		    if (memberReference instanceof MethodReference) {
			MethodReference method
			    = (MethodReference) memberReference;
			resolvedMethod = method.resolve();
			if (resolvedMethod != null) {
			    argumentIndex
				= (CollectionUtilities.indexOf
				   (grandParent
					.getChildrenByRole(Roles.ARGUMENT),
				    (Expression) parent));
			    toReplace
				= ((Expression)
				   CollectionUtilities.get(arguments,
							   argumentIndex));
			    if (!(toReplace instanceof ConditionalExpression))
				break label_1840;
			    Expression trueExpression
				= ((ConditionalExpression) toReplace)
				      .getTrueExpression();
			    Expression falseExpression
				= ((ConditionalExpression) toReplace)
				      .getFalseExpression();
			    if (!(trueExpression instanceof CastExpression)) {
				if (falseExpression
				    instanceof CastExpression) {
				    Expression falseOperand
					= ((CastExpression) falseExpression)
					      .getExpression();
				    TypeReference operandType
					= getType(falseOperand);
				    if (operandType != null)
					falseExpression
					    .replaceWith(falseOperand);
				}
			    } else {
				Expression trueOperand
				    = ((CastExpression) trueExpression)
					  .getExpression();
				TypeReference operandType
				    = getType(trueOperand);
				if (operandType != null)
				    trueExpression.replaceWith(trueOperand);
			    }
			} else
			    return false;
		    } else
			return false;
		}
		TypeReference newArgumentType = getType(toReplace);
		if (newArgumentType != null)
		    argumentTypes.set(argumentIndex, newArgumentType);
		else
		    return false;
	    }
	    List candidates
		= (MetadataHelper.findMethods
		   (targetType,
		    MetadataFilters.matchName(resolvedMethod.getName())));
	label_1841:
	    {
		MethodBinder.BindResult result
		    = MethodBinder.selectMethod(candidates, argumentTypes);
		if (result.isFailure() || result.isAmbiguous()
		    || !StringUtilities.equals(resolvedMethod
						   .getErasedSignature(),
					       result.getMethod()
						   .getErasedSignature()))
		    PUSH false;
		else
		    PUSH true;
		break label_1841;
	    }
	    return POP;
	    break label_1839;
	    break label_1838;
	    break label_1837;
	}
	
	public boolean isTypeCastSemantic(CastExpression cast) {
	    Expression operand = cast.getExpression();
	    TypeReference opType;
	label_1846:
	    {
		TypeReference castType;
	    label_1844:
		{
		label_1843:
		    {
		    label_1842:
			{
			    if (!operand.isNull()) {
				if (!isInPolymorphicCall(cast)) {
				    opType = getType(operand);
				    castType = getType(cast.getType());
				    if (opType != null && castType != null) {
					if (!(castType
					      instanceof PrimitiveType)) {
					    if (!(castType
						  instanceof IGenericInstance)) {
						if (MetadataHelper
							.isRawType(castType)
						    && (opType
							instanceof IGenericInstance)
						    && !(MetadataHelper
							     .isAssignableFrom
							 (castType, opType)))
						    return true;
					    } else if (MetadataHelper
							   .isRawType(opType)
						       && !(MetadataHelper
								.isAssignableFrom
							    (castType,
							     opType)))
						return true;
					    break label_1844;
					}
					if (!(opType instanceof PrimitiveType))
					    break label_1843;
					if (operand
					    instanceof PrimitiveExpression) {
					    TypeReference unboxedCastType
						= (MetadataHelper
						       .getUnderlyingPrimitiveTypeOrSelf
						   (castType));
					    TypeReference unboxedOpType
						= (MetadataHelper
						       .getUnderlyingPrimitiveTypeOrSelf
						   (opType));
					    if ((TypeUtilities
						     .isValidPrimitiveLiteralAssignment
						 (unboxedCastType,
						  ((PrimitiveExpression)
						   operand)
						      .getValue()))
						&& (TypeUtilities
							.isValidPrimitiveLiteralAssignment
						    (unboxedOpType,
						     ((PrimitiveExpression)
						      operand)
							 .getValue())))
						return false;
					}
				    } else
					return false;
				} else
				    return true;
			    } else
				return false;
			}
			ConversionType conversionType
			    = MetadataHelper.getNumericConversionType(castType,
								      opType);
			if (conversionType != ConversionType.IDENTITY
			    && conversionType != ConversionType.IMPLICIT)
			    return true;
		    }
		    TypeReference unboxedOpType
			= MetadataHelper
			      .getUnderlyingPrimitiveTypeOrSelf(opType);
		    if (unboxedOpType.isPrimitive()) {
			ConversionType conversionType
			    = (MetadataHelper.getNumericConversionType
			       (castType, unboxedOpType));
			if (conversionType != ConversionType.IDENTITY
			    && conversionType != ConversionType.IMPLICIT)
			    return true;
		    }
		}
		if (operand instanceof LambdaExpression
		    || operand instanceof MethodGroupExpression) {
		    MetadataParser parser
			= new MetadataParser(IMetadataResolver.EMPTY);
		    TypeReference serializable
			= parser.parseTypeDescriptor("java/lang/Serializable");
		    boolean redundant;
		    List interfaces;
		    int start;
		    TypeReference baseType;
		label_1845:
		    {
			if (castType.isPrimitive()
			    || !MetadataHelper.isSubType(castType,
							 serializable)) {
			    if (!(castType instanceof CompoundTypeReference))
				break label_1846;
			    redundant = false;
			    CompoundTypeReference compoundType
				= (CompoundTypeReference) castType;
			    interfaces = compoundType.getInterfaces();
			    start = 0;
			    baseType = compoundType.getBaseType();
			    if (baseType == null) {
				baseType
				    = ((TypeReference)
				       CollectionUtilities.first(interfaces));
				start = 1;
			    }
			} else
			    return true;
		    }
		    for (int i = start; i < interfaces.size(); i++) {
			TypeReference conjunct;
			conjunct = (TypeReference) interfaces.get(i);
			if (!MetadataHelper.isAssignableFrom(baseType,
							     conjunct)) {
			    /* empty */
			}
			redundant = true;
			break;
		    }
		    if (!redundant)
			return true;
		    break label_1845;
		} else
		    break label_1846;
		break label_1846;
	    }
	    AstNode parent = cast.getParent();
	label_1848:
	    {
		BinaryOperatorExpression expression;
		Expression firstOperand;
		Expression otherOperand;
	    label_1847:
		{
		    for (;;) {
			if (!(parent instanceof ParenthesizedExpression)) {
			    if (!(parent
				  instanceof BinaryOperatorExpression)) {
				if (parent instanceof ConditionalExpression
				    && opType.isPrimitive()
				    && !(getType(parent)
					 instanceof PrimitiveType)) {
				    TypeReference expectedType
					= (TypeUtilities
					       .getExpectedTypeByParent
					   (_resolver, (Expression) parent));
				    if (expectedType != null
					&& MetadataHelper
					       .getUnderlyingPrimitiveTypeOrSelf
					       (expectedType).isPrimitive())
					return true;
				}
			    } else {
				expression = (BinaryOperatorExpression) parent;
				firstOperand = expression.getLeft();
				otherOperand = expression.getRight();
				if (otherOperand.isAncestorOf(cast)) {
				    Expression temp = otherOperand;
				    otherOperand = firstOperand;
				    firstOperand = temp;
				}
				break label_1847;
			    }
			    break;
			}
			parent = parent.getParent();
		    }
		    break label_1848;
		}
		if (firstOperand != null && otherOperand != null
		    && castChangesComparisonSemantics(firstOperand,
						      otherOperand, operand,
						      expression
							  .getOperator()))
		    return true;
	    }
	    return false;
	    break label_1847;
	    break label_1842;
	}
	
	public boolean isInPolymorphicCall(CastExpression cast) {
	    Expression operand = cast.getExpression();
	label_1849:
	    {
		if ((!(operand instanceof InvocationExpression)
		     && (!(operand instanceof MemberReferenceExpression)
			 || !(operand.getParent()
			      instanceof InvocationExpression))
		     && !(operand instanceof ObjectCreationExpression))
		    || !isPolymorphicMethod(operand)) {
		    if (cast.getRole() != Roles.ARGUMENT
			|| !(isPolymorphicMethod
			     (skipParenthesesUp(cast.getParent()))))
			PUSH false;
		    else
			PUSH true;
		} else
		    return true;
	    }
	    return POP;
	    break label_1849;
	}
	
	private static boolean isPolymorphicMethod(AstNode expression) {
	label_1851:
	    {
		MemberReference memberReference;
	    label_1850:
		{
		    if (expression != null) {
			memberReference
			    = ((MemberReference)
			       expression.getUserData(Keys.MEMBER_REFERENCE));
			if (memberReference == null
			    && (expression.getParent()
				instanceof MemberReferenceExpression))
			    memberReference
				= ((MemberReference)
				   expression.getParent()
				       .getUserData(Keys.MEMBER_REFERENCE));
		    } else
			return false;
		}
		if (memberReference != null) {
		    List annotations = memberReference.getAnnotations();
		    Iterator i$ = annotations.iterator();
		    while (i$.hasNext()) {
			CustomAnnotation annotation
			    = (CustomAnnotation) i$.next();
			String typeName
			    = annotation.getAnnotationType().getInternalName();
			IF (!StringUtilities.equals
			     (typeName,
			      "java.lang.invoke.MethodHandle.PolymorphicSignature"))
			    /* empty */
			return true;
		    }
		}
		break label_1851;
	    }
	    return false;
	    break label_1850;
	}
	
	private boolean castChangesComparisonSemantics
	    (Expression operand, Expression otherOperand, Expression toCast,
	     BinaryOperatorType operator) {
	    TypeReference operandType = getType(operand);
	    TypeReference otherType = getType(otherOperand);
	    TypeReference castType = getType(toCast);
	label_1855:
	    {
		boolean isPrimitiveComparisonWithCast;
		boolean isPrimitiveComparisonWithoutCast;
	    label_1854:
		{
		label_1852:
		    {
			if (operator != BinaryOperatorType.EQUALITY
			    && operator != BinaryOperatorType.INEQUALITY) {
			    if ((operandType == null
				 || !operandType.isPrimitive())
				&& (otherType == null
				    || !otherType.isPrimitive()))
				PUSH false;
			    else
				PUSH true;
			} else {
			    if (!TypeUtilities.isPrimitive(otherType)) {
				isPrimitiveComparisonWithCast
				    = TypeUtilities.isPrimitive(operandType);
				isPrimitiveComparisonWithoutCast
				    = TypeUtilities.isPrimitive(castType);
			    } else {
				isPrimitiveComparisonWithCast
				    = TypeUtilities
					  .isPrimitiveOrWrapper(operandType);
				isPrimitiveComparisonWithoutCast
				    = TypeUtilities
					  .isPrimitiveOrWrapper(castType);
			    }
			    break label_1854;
			}
		    }
		label_1853:
		    {
			isPrimitiveComparisonWithCast = POP;
			if ((castType == null || !castType.isPrimitive())
			    && (operandType == null
				|| !operandType.isPrimitive()))
			    PUSH false;
			else
			    PUSH true;
			break label_1853;
		    }
		    isPrimitiveComparisonWithoutCast = POP;
		}
		if (isPrimitiveComparisonWithCast
		    == isPrimitiveComparisonWithoutCast)
		    PUSH false;
		else
		    PUSH true;
		break label_1855;
	    }
	    return POP;
	    break label_1852;
	}
    }
    
    private static class CastCollector extends IsRedundantVisitor
    {
	private final Set _foundCasts = new HashSet();
	
	CastCollector(Function resolver) {
	    super(resolver, true);
	}
	
	private Set getFoundCasts() {
	    return _foundCasts;
	}
	
	public Void visitAnonymousObjectCreationExpression
	    (AnonymousObjectCreationExpression node, Void data) {
	    Iterator i$ = node.getArguments().iterator();
	    for (;;) {
		if (!i$.hasNext())
		    return null;
		Expression argument = (Expression) i$.next();
		argument.acceptVisitor(this, data);
	    }
	}
	
	public Void visitTypeDeclaration(TypeDeclaration typeDeclaration,
					 Void _) {
	    return null;
	}
	
	public Void visitLocalTypeDeclarationStatement
	    (LocalTypeDeclarationStatement node, Void data) {
	    return null;
	}
	
	public Void visitMethodDeclaration(MethodDeclaration node, Void _) {
	    return null;
	}
	
	public Void visitConstructorDeclaration(ConstructorDeclaration node,
						Void _) {
	    return null;
	}
	
	protected void addToResults(CastExpression cast, boolean force) {
	    if (force || !isTypeCastSemantic(cast))
		_foundCasts.add(cast);
	    return;
	}
    }
    
    public static List getRedundantCastsInside(Function resolver,
					       AstNode site) {
	VerifyArgument.notNull(resolver, "resolver");
	if (site != null) {
	    CastCollector visitor = new CastCollector(resolver);
	    site.acceptVisitor(visitor, null);
	    return new ArrayList(visitor.getFoundCasts());
	}
	return Collections.emptyList();
    }
    
    public static boolean isCastRedundant(Function resolver,
					  CastExpression cast) {
	AstNode parent = skipParenthesesUp(cast.getParent());
    label_1810:
	{
	    if (parent != null) {
		if (parent.getRole() == Roles.ARGUMENT || parent.isReference())
		    parent = parent.getParent();
	    } else
		return false;
	}
	IsRedundantVisitor visitor = new IsRedundantVisitor(resolver, false);
	parent.acceptVisitor(visitor, null);
	return visitor.isRedundant();
	break label_1810;
    }
    
    public static void removeCast(CastExpression castExpression) {
	if (castExpression != null && !castExpression.isNull()) {
	    Expression operand;
	label_1811:
	    {
		operand = castExpression.getExpression();
		if (operand instanceof ParenthesizedExpression)
		    operand
			= ((ParenthesizedExpression) operand).getExpression();
		break label_1811;
	    }
	    if (operand != null && !operand.isNull()) {
		AstNode toBeReplaced = castExpression;
		AstNode parent = castExpression.getParent();
		for (;;) {
		    if (!(parent instanceof ParenthesizedExpression)) {
			toBeReplaced.replaceWith(operand);
			return;
		    }
		    toBeReplaced = parent;
		    parent = parent.getParent();
		}
	    }
	}
	return;
    }
    
    private static Expression removeParentheses(Expression e) {
	Expression result = e;
	for (;;) {
	    if (!(result instanceof ParenthesizedExpression))
		return result;
	    result = ((ParenthesizedExpression) result).getExpression();
	}
    }
    
    private static AstNode skipParenthesesUp(AstNode e) {
	AstNode result = e;
	for (;;) {
	    if (!(result instanceof ParenthesizedExpression))
		return result;
	    result = result.getParent();
	}
    }
}
