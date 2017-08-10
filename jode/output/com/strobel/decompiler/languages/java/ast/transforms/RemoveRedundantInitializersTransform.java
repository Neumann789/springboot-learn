/* RemoveRedundantInitializersTransform - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import com.strobel.assembler.metadata.FieldDefinition;
import com.strobel.assembler.metadata.FieldReference;
import com.strobel.assembler.metadata.MemberReference;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
import com.strobel.decompiler.languages.java.ast.AstNode;
import com.strobel.decompiler.languages.java.ast.ConstructorDeclaration;
import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
import com.strobel.decompiler.languages.java.ast.ExpressionStatement;
import com.strobel.decompiler.languages.java.ast.Keys;
import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
import com.strobel.decompiler.languages.java.ast.ThisReferenceExpression;

public class RemoveRedundantInitializersTransform
    extends ContextTrackingVisitor
{
    private boolean _inConstructor;
    
    public RemoveRedundantInitializersTransform(DecompilerContext context) {
	super(context);
    }
    
    public Void visitConstructorDeclaration(ConstructorDeclaration node,
					    Void _) {
	boolean wasInConstructor = _inConstructor;
	_inConstructor = true;
	try {
	    Void var_void = (Void) super.visitConstructorDeclaration(node, _);
	    _inConstructor = wasInConstructor;
	    return var_void;
	} finally {
	    Object object = POP;
	    _inConstructor = wasInConstructor;
	    throw object;
	}
    }
    
    public Void visitAssignmentExpression(AssignmentExpression node,
					  Void data) {
    label_1784:
	{
	    super.visitAssignmentExpression(node, data);
	    if (_inConstructor) {
		com.strobel.decompiler.languages.java.ast.Expression left
		    = node.getLeft();
		if (left instanceof MemberReferenceExpression
		    && (((MemberReferenceExpression) left).getTarget()
			instanceof ThisReferenceExpression)) {
		    MemberReferenceExpression reference
			= (MemberReferenceExpression) left;
		    MemberReference memberReference
			= ((MemberReference)
			   reference.getUserData(Keys.MEMBER_REFERENCE));
		    if (memberReference instanceof FieldReference) {
			FieldDefinition resolvedField
			    = ((FieldReference) memberReference).resolve();
			if (resolvedField != null
			    && resolvedField.hasConstantValue()) {
			    AstNode parent = node.getParent();
			    if (!(parent instanceof ExpressionStatement)) {
				reference.remove();
				node.replaceWith(reference);
			    } else
				parent.remove();
			}
		    }
		}
	    }
	    break label_1784;
	}
	return null;
    }
}
