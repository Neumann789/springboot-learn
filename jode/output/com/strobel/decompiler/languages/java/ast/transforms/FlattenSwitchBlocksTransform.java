/* FlattenSwitchBlocksTransform - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import com.strobel.core.CollectionUtilities;
import com.strobel.core.Predicate;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.AstNode;
import com.strobel.decompiler.languages.java.ast.BlockStatement;
import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
import com.strobel.decompiler.languages.java.ast.Statement;
import com.strobel.decompiler.languages.java.ast.SwitchSection;
import com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement;

public class FlattenSwitchBlocksTransform extends ContextTrackingVisitor
    implements IAstTransform
{
    public FlattenSwitchBlocksTransform(DecompilerContext context) {
	super(context);
    }
    
    public void run(AstNode compilationUnit) {
	if (context.getSettings().getFlattenSwitchBlocks())
	    compilationUnit.acceptVisitor(this, null);
	return;
    }
    
    public AstNode visitSwitchSection(SwitchSection node, Void _) {
    label_1722:
	{
	    if (node.getStatements().size() == 1) {
		Statement firstStatement
		    = (Statement) node.getStatements().firstOrNullObject();
		if (firstStatement instanceof BlockStatement) {
		    BlockStatement block = (BlockStatement) firstStatement;
		    boolean declaresVariables = CollectionUtilities
						    .any(block.getStatements(), new Predicate() {
			{
			    super();
			}
			
			public boolean test(Statement s) {
			    return s instanceof VariableDeclarationStatement;
			}
		    });
		    if (!declaresVariables) {
			block.remove();
			block.getStatements().moveTo(node.getStatements());
		    }
		}
	    } else
		return (AstNode) super.visitSwitchSection(node, _);
	}
	return (AstNode) super.visitSwitchSection(node, _);
	break label_1722;
    }
}
