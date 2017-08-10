/* RemoveRedundantCastsTransform - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import java.util.List;

import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.AstNode;
import com.strobel.decompiler.languages.java.ast.CastExpression;
import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
import com.strobel.decompiler.languages.java.ast.JavaResolver;
import com.strobel.decompiler.languages.java.ast.ParenthesizedExpression;
import com.strobel.decompiler.languages.java.utilities.RedundantCastUtility;

public class RemoveRedundantCastsTransform extends ContextTrackingVisitor
{
    private final JavaResolver _resolver;
    
    public RemoveRedundantCastsTransform(DecompilerContext context) {
	super(context);
	_resolver = new JavaResolver(context);
    }
    
    public void run(AstNode compilationUnit) {
	if (!context.getSettings().getRetainRedundantCasts())
	    super.run(compilationUnit);
	return;
    }
    
    public Void visitCastExpression(CastExpression node, Void data) {
	super.visitCastExpression(node, data);
    label_1783:
	{
	    List redundantCasts
		= (RedundantCastUtility.getRedundantCastsInside
		   (_resolver, skipParenthesesUp(node.getParent())));
	    if (redundantCasts.contains(node))
		RedundantCastUtility.removeCast(node);
	    break label_1783;
	}
	return null;
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
