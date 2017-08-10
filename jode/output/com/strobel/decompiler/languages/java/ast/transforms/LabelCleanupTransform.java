/* LabelCleanupTransform - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import java.util.Iterator;

import com.strobel.core.StringUtilities;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.AstNode;
import com.strobel.decompiler.languages.java.ast.BlockStatement;
import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
import com.strobel.decompiler.languages.java.ast.Identifier;
import com.strobel.decompiler.languages.java.ast.LabelStatement;
import com.strobel.decompiler.languages.java.ast.LabeledStatement;
import com.strobel.decompiler.languages.java.ast.Roles;
import com.strobel.decompiler.languages.java.ast.Statement;

public class LabelCleanupTransform extends ContextTrackingVisitor
{
    public LabelCleanupTransform(DecompilerContext context) {
	super(context);
    }
    
    public Void visitLabeledStatement(LabeledStatement node, Void data) {
    label_1757:
	{
	    super.visitLabeledStatement(node, data);
	    if (node.getStatement() instanceof BlockStatement) {
		BlockStatement block = (BlockStatement) node.getStatement();
		if (block.getStatements().hasSingleElement()
		    && (block.getStatements().firstOrNullObject()
			instanceof LabeledStatement)) {
		    LabeledStatement nestedLabeledStatement
			= ((LabeledStatement)
			   block.getStatements().firstOrNullObject());
		    String nextLabel
			= ((Identifier)
			   nestedLabeledStatement.getChildByRole(Roles.LABEL))
			      .getName();
		    redirectLabels(node, node.getLabel(), nextLabel);
		    nestedLabeledStatement.remove();
		    node.replaceWith(nestedLabeledStatement);
		}
	    }
	    break label_1757;
	}
	return null;
    }
    
    public Void visitLabelStatement(LabelStatement node, Void data) {
	super.visitLabelStatement(node, data);
	Statement next = node.getNextStatement();
    label_1759:
	{
	label_1758:
	    {
		if (next != null) {
		    if (!(next instanceof LabelStatement)
			&& !(next instanceof LabeledStatement)) {
			next.remove();
			PUSH node;
			PUSH new LabeledStatement;
			DUP
			PUSH node.getLabel();
			if (!AstNode.isLoop(next))
			    PUSH new BlockStatement(new Statement[] { next });
			else
			    PUSH next;
		    } else {
			String nextLabel
			    = ((Identifier) next.getChildByRole(Roles.LABEL))
				  .getName();
			redirectLabels(node.getParent(), node.getLabel(),
				       nextLabel);
			node.remove();
			break label_1759;
		    }
		} else
		    return null;
	    }
	    ((UNCONSTRUCTED)POP).LabeledStatement(POP, POP);
	    ((LabelStatement) POP).replaceWith(POP);
	}
	return null;
	break label_1758;
    }
    
    private void redirectLabels(AstNode node, String labelName,
				String nextLabel) {
	Iterator i$ = node.getDescendantsAndSelf().iterator();
	for (;;) {
	    IF (!i$.hasNext())
		/* empty */
	    AstNode n = (AstNode) i$.next();
	    if (AstNode.isUnconditionalBranch(n)) {
		Identifier label
		    = (Identifier) n.getChildByRole(Roles.IDENTIFIER);
		if (!label.isNull()
		    && StringUtilities.equals(label.getName(), labelName))
		    label.setName(nextLabel);
	    }
	    continue;
	}
    }
}
