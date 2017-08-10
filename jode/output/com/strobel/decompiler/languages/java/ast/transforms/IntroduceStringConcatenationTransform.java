/* IntroduceStringConcatenationTransform - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.strobel.assembler.metadata.CommonTypeReferences;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.core.CollectionUtilities;
import com.strobel.core.StringUtilities;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.AstNode;
import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
import com.strobel.decompiler.languages.java.ast.BinaryOperatorType;
import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
import com.strobel.decompiler.languages.java.ast.Expression;
import com.strobel.decompiler.languages.java.ast.ExpressionStatement;
import com.strobel.decompiler.languages.java.ast.InvocationExpression;
import com.strobel.decompiler.languages.java.ast.JavaResolver;
import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
import com.strobel.decompiler.languages.java.ast.ObjectCreationExpression;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;
import com.strobel.decompiler.patterns.OptionalNode;
import com.strobel.decompiler.patterns.TypedExpression;
import com.strobel.decompiler.semantics.ResolveResult;

public class IntroduceStringConcatenationTransform
    extends ContextTrackingVisitor
{
    private final INode _stringBuilderArgumentPattern;
    
    public IntroduceStringConcatenationTransform(DecompilerContext context) {
	super(context);
	_stringBuilderArgumentPattern
	    = new OptionalNode(new TypedExpression("firstArgument",
						   CommonTypeReferences.String,
						   new JavaResolver(context)));
    }
    
    public Void visitObjectCreationExpression(ObjectCreationExpression node,
					      Void data) {
	AstNodeCollection arguments = node.getArguments();
    label_1754:
	{
	    Expression firstArgument;
	label_1753:
	    {
		if (arguments.isEmpty() || arguments.hasSingleElement()) {
		    if (!arguments.hasSingleElement())
			firstArgument = null;
		    else {
			Match m = _stringBuilderArgumentPattern
				      .match(arguments.firstOrNullObject());
			if (m.success())
			    firstArgument
				= (Expression) (CollectionUtilities
						    .firstOrDefault
						(m.get("firstArgument")));
			else
			    return ((Void)
				    super.visitObjectCreationExpression(node,
									data));
		    }
		    break label_1753;
		}
		break label_1754;
	    }
	    TypeReference typeReference = node.getType().toTypeReference();
	    if (typeReference != null && isStringBuilder(typeReference))
		convertStringBuilderToConcatenation(node, firstArgument);
	}
	return (Void) super.visitObjectCreationExpression(node, data);
    }
    
    private boolean isStringBuilder(TypeReference typeReference) {
    label_1755:
	{
	    if (!StringUtilities.equals(typeReference.getInternalName(),
					"java/lang/StringBuilder")) {
		if (context.getCurrentType() == null
		    || context.getCurrentType().getCompilerMajorVersion() >= 49
		    || !StringUtilities.equals(typeReference.getInternalName(),
					       "java/lang/StringBuffer"))
		    PUSH false;
		else
		    PUSH true;
	    } else
		return true;
	}
	return POP;
	break label_1755;
    }
    
    private void convertStringBuilderToConcatenation
	(ObjectCreationExpression node, Expression firstArgument) {
	if (node.getParent() != null && node.getParent().getParent() != null) {
	    ArrayList operands;
	label_1756:
	    {
		operands = new ArrayList();
		if (firstArgument != null)
		    operands.add(firstArgument);
		break label_1756;
	    }
	    AstNode current = node.getParent();
	    AstNode parent;
	    for (parent = current.getParent();
		 (current instanceof MemberReferenceExpression
		  && parent instanceof InvocationExpression
		  && parent.getParent() != null);
		 parent = current.getParent()) {
		String memberName;
		memberName
		    = ((MemberReferenceExpression) current).getMemberName();
		AstNodeCollection arguments
		    = ((InvocationExpression) parent).getArguments();
		if (StringUtilities.equals(memberName, "append")
		    && arguments.size() == 1) {
		    operands.add(arguments.firstOrNullObject());
		    current = parent.getParent();
		}
		break;
	    }
	    if (operands.size() > 1 && anyIsString(operands.subList(0, 2))
		&& current instanceof MemberReferenceExpression
		&& parent instanceof InvocationExpression
		&& !(parent.getParent() instanceof ExpressionStatement)
		&& StringUtilities.equals(((MemberReferenceExpression) current)
					      .getMemberName(),
					  "toString")
		&& ((InvocationExpression) parent).getArguments().isEmpty()) {
		Iterator i$ = operands.iterator();
		for (;;) {
		    if (!i$.hasNext()) {
			Expression concatenation
			    = (new BinaryOperatorExpression
			       ((Expression) operands.get(0),
				BinaryOperatorType.ADD,
				(Expression) operands.get(1)));
			int i = 2;
			for (;;) {
			    if (i >= operands.size()) {
				parent.replaceWith(concatenation);
				return;
			    }
			    concatenation
				= (new BinaryOperatorExpression
				   (concatenation, BinaryOperatorType.ADD,
				    (Expression) operands.get(i)));
			    i++;
			}
			return;
		    }
		    Expression operand = (Expression) i$.next();
		    operand.remove();
		}
	    }
	}
	return;
    }
    
    private boolean anyIsString(List expressions) {
	JavaResolver resolver = new JavaResolver(context);
	int i = 0;
	for (;;) {
	    if (i >= expressions.size())
		return false;
	    ResolveResult result
		= resolver.apply((AstNode) expressions.get(i));
	    if (result == null || result.getType() == null
		|| !CommonTypeReferences.String
			.isEquivalentTo(result.getType()))
		i++;
	    return true;
	}
    }
}
