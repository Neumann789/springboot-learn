/* RewriteNewArrayLambdas - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import com.strobel.assembler.metadata.DynamicCallSite;
import com.strobel.assembler.metadata.IMethodSignature;
import com.strobel.assembler.metadata.JvmType;
import com.strobel.assembler.metadata.ParameterDefinition;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.core.CollectionUtilities;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.ArrayCreationExpression;
import com.strobel.decompiler.languages.java.ast.AstType;
import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
import com.strobel.decompiler.languages.java.ast.Keys;
import com.strobel.decompiler.languages.java.ast.LambdaExpression;
import com.strobel.decompiler.languages.java.ast.MethodGroupExpression;
import com.strobel.decompiler.languages.java.ast.ParameterDeclaration;
import com.strobel.decompiler.languages.java.ast.SimpleType;
import com.strobel.decompiler.languages.java.ast.TypeReferenceExpression;
import com.strobel.decompiler.patterns.AnyNode;
import com.strobel.decompiler.patterns.IdentifierExpressionBackReference;
import com.strobel.decompiler.patterns.Match;
import com.strobel.decompiler.patterns.NamedNode;
import com.strobel.decompiler.patterns.OptionalNode;

public class RewriteNewArrayLambdas extends ContextTrackingVisitor
{
    protected RewriteNewArrayLambdas(DecompilerContext context) {
	super(context);
    }
    
    public Void visitLambdaExpression(LambdaExpression node, Void data) {
	super.visitLambdaExpression(node, data);
    label_1799:
	{
	    DynamicCallSite callSite
		= (DynamicCallSite) node.getUserData(Keys.DYNAMIC_CALL_SITE);
	    if (callSite != null
		&& callSite.getBootstrapArguments().size() >= 3
		&& (callSite.getBootstrapArguments().get(2)
		    instanceof IMethodSignature)) {
		IMethodSignature signature
		    = ((IMethodSignature)
		       callSite.getBootstrapArguments().get(2));
		if (signature.getParameters().size() == 1
		    && ((ParameterDefinition) signature.getParameters().get(0))
			   .getParameterType
			   ().getSimpleType() == JvmType.Integer
		    && signature.getReturnType().isArray()
		    && !signature.getReturnType().getElementType()
			    .isGenericType()) {
		    LambdaExpression pattern = new LambdaExpression(-34);
		    ParameterDeclaration size = new ParameterDeclaration();
		    size.setName("$any$");
		    size.setAnyModifiers(true);
		    size.setType(new OptionalNode(new SimpleType("int"))
				     .toType());
		    pattern.getParameters().add(new NamedNode("size", size)
						    .toParameterDeclaration());
		    ArrayCreationExpression arrayCreation
			= new ArrayCreationExpression(-34);
		    arrayCreation.getDimensions().add
			(new IdentifierExpressionBackReference("size")
			     .toExpression());
		    arrayCreation.setType(new NamedNode
					      ("type", new AnyNode())
					      .toType());
		    pattern.setBody(arrayCreation);
		    Match match = pattern.match(node);
		    if (match.success()) {
			AstType type
			    = ((AstType)
			       CollectionUtilities.first(match.get("type")));
			if (signature.getReturnType().getElementType()
				.isEquivalentTo(type.toTypeReference())) {
			    MethodGroupExpression replacement
				= (new MethodGroupExpression
				   (node.getOffset(),
				    (new TypeReferenceExpression
				     (-34, type.clone().makeArrayType())),
				    "new"));
			label_1798:
			    {
				TypeReference lambdaType
				    = ((TypeReference)
				       node.getUserData(Keys.TYPE_REFERENCE));
				if (lambdaType != null)
				    replacement.putUserData((Keys
							     .TYPE_REFERENCE),
							    lambdaType);
				break label_1798;
			    }
			    replacement.putUserData(Keys.DYNAMIC_CALL_SITE,
						    callSite);
			    node.replaceWith(replacement);
			}
		    }
		}
	    }
	    break label_1799;
	}
	return null;
    }
}
