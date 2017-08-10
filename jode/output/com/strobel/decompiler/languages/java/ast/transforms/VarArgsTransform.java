/* VarArgsTransform - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.strobel.assembler.metadata.MetadataFilters;
import com.strobel.assembler.metadata.MetadataHelper;
import com.strobel.assembler.metadata.MethodBinder;
import com.strobel.assembler.metadata.MethodDefinition;
import com.strobel.assembler.metadata.MethodReference;
import com.strobel.core.StringUtilities;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.ArrayCreationExpression;
import com.strobel.decompiler.languages.java.ast.ArrayInitializerExpression;
import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
import com.strobel.decompiler.languages.java.ast.CastExpression;
import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
import com.strobel.decompiler.languages.java.ast.Expression;
import com.strobel.decompiler.languages.java.ast.InvocationExpression;
import com.strobel.decompiler.languages.java.ast.JavaResolver;
import com.strobel.decompiler.languages.java.ast.Keys;
import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
import com.strobel.decompiler.semantics.ResolveResult;

public class VarArgsTransform extends ContextTrackingVisitor
{
    private final JavaResolver _resolver;
    
    public VarArgsTransform(DecompilerContext context) {
	super(context);
	_resolver = new JavaResolver(context);
    }
    
    public Void visitInvocationExpression(InvocationExpression node,
					  Void data) {
	super.visitInvocationExpression(node, data);
	AstNodeCollection arguments = node.getArguments();
	Expression lastArgument = (Expression) arguments.lastOrNullObject();
	Expression arrayArg;
    label_1806:
	{
	    arrayArg = lastArgument;
	    if (arrayArg instanceof CastExpression)
		arrayArg = ((CastExpression) arrayArg).getExpression();
	    break label_1806;
	}
	ArrayCreationExpression newArray;
	List candidates;
    label_1807:
	{
	    if (arrayArg != null && !arrayArg.isNull()
		&& arrayArg instanceof ArrayCreationExpression
		&& node.getTarget() instanceof MemberReferenceExpression) {
		newArray = (ArrayCreationExpression) arrayArg;
		MemberReferenceExpression target
		    = (MemberReferenceExpression) node.getTarget();
		if (newArray.getAdditionalArraySpecifiers()
			.hasSingleElement()) {
		    MethodReference method
			= ((MethodReference)
			   node.getUserData(Keys.MEMBER_REFERENCE));
		    if (method != null) {
			MethodDefinition resolved = method.resolve();
			if (resolved != null && resolved.isVarArgs()) {
			    Expression invocationTarget = target.getTarget();
			    if (invocationTarget != null
				&& !invocationTarget.isNull()) {
				ResolveResult targetResult
				    = _resolver.apply(invocationTarget);
				if (targetResult != null
				    && targetResult.getType() != null)
				    candidates = (MetadataHelper.findMethods
						  (targetResult.getType(),
						   (MetadataFilters.matchName
						    (resolved.getName()))));
				else
				    return null;
			    } else
				candidates
				    = (MetadataHelper.findMethods
				       (context.getCurrentType(),
					MetadataFilters
					    .matchName(resolved.getName())));
			} else
			    return null;
		    } else
			return null;
		} else
		    return null;
	    } else
		return null;
	}
	List argTypes = new ArrayList();
	Iterator i$ = arguments.iterator();
	MethodBinder.BindResult c1;
	ArrayInitializerExpression initializer;
    label_1808:
	{
	    for (;;) {
		if (!i$.hasNext()) {
		    c1 = MethodBinder.selectMethod(candidates, argTypes);
		    if (!c1.isFailure() && !c1.isAmbiguous()) {
			argTypes.remove(argTypes.size() - 1);
			initializer = newArray.getInitializer();
			if (initializer.isNull()
			    || initializer.getElements().isEmpty())
			    PUSH false;
			else
			    PUSH true;
		    } else
			return null;
		} else {
		    Expression argument = (Expression) i$.next();
		    ResolveResult argResult = _resolver.apply(argument);
		    if (argResult != null && argResult.getType() != null)
			argTypes.add(argResult.getType());
		    return null;
		}
		break label_1808;
	    }
	}
	boolean hasElements;
    label_1809:
	{
	    hasElements = POP;
	    if (hasElements) {
		Iterator i$_0_ = initializer.getElements().iterator();
		while (i$_0_.hasNext()) {
		    Expression argument = (Expression) i$_0_.next();
		    ResolveResult argResult = _resolver.apply(argument);
		    if (argResult != null && argResult.getType() != null)
			argTypes.add(argResult.getType());
		    return null;
		}
	    }
	    break label_1809;
	}
	MethodBinder.BindResult c2
	    = MethodBinder.selectMethod(candidates, argTypes);
	if (!c2.isFailure() && !c2.isAmbiguous()
	    && StringUtilities.equals(c2.getMethod().getErasedSignature(),
				      c1.getMethod().getErasedSignature())) {
	    lastArgument.remove();
	    if (hasElements) {
		Iterator i$_1_ = initializer.getElements().iterator();
		for (;;) {
		    if (!i$_1_.hasNext())
			return null;
		    Expression newArg = (Expression) i$_1_.next();
		    newArg.remove();
		    arguments.add(newArg);
		}
	    }
	    return null;
	}
	return null;
	break label_1808;
	break label_1807;
    }
}
