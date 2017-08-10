/* MarkReferencedSyntheticsTransform - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import com.strobel.assembler.metadata.FieldReference;
import com.strobel.assembler.metadata.Flags;
import com.strobel.assembler.metadata.IMemberDefinition;
import com.strobel.assembler.metadata.MemberReference;
import com.strobel.assembler.metadata.MethodReference;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.AstBuilder;
import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
import com.strobel.decompiler.languages.java.ast.Keys;
import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;

public class MarkReferencedSyntheticsTransform extends ContextTrackingVisitor
{
    public MarkReferencedSyntheticsTransform(DecompilerContext context) {
	super(context);
    }
    
    public Void visitMemberReferenceExpression(MemberReferenceExpression node,
					       Void data) {
    label_1774:
	{
	    super.visitMemberReferenceExpression(node, data);
	    if (isCurrentMemberVisible()) {
		MemberReference member;
	    label_1772:
		{
		    member = ((MemberReference)
			      node.getUserData(Keys.MEMBER_REFERENCE));
		    if (member == null && node.getParent() != null)
			member = ((MemberReference)
				  node.getParent()
				      .getUserData(Keys.MEMBER_REFERENCE));
		    break label_1772;
		}
		IMemberDefinition resolvedMember;
	    label_1773:
		{
		    if (member != null) {
			if (!(member instanceof FieldReference))
			    resolvedMember
				= ((MethodReference) member).resolve();
			else
			    resolvedMember
				= ((FieldReference) member).resolve();
			break label_1773;
		    }
		    break label_1774;
		}
		if (resolvedMember != null && resolvedMember.isSynthetic()
		    && !Flags.testAny(resolvedMember.getFlags(), 2147483648L))
		    context.getForcedVisibleMembers().add(resolvedMember);
	    }
	    break label_1774;
	}
	return null;
    }
    
    private boolean isCurrentMemberVisible() {
	com.strobel.assembler.metadata.MethodDefinition currentMethod
	    = context.getCurrentMethod();
	if (currentMethod == null
	    || !AstBuilder.isMemberHidden(currentMethod, context)) {
	    com.strobel.assembler.metadata.TypeDefinition currentType
		= context.getCurrentType();
	    if (currentType == null
		|| !AstBuilder.isMemberHidden(currentType, context))
		return true;
	    return false;
	}
	return false;
    }
}
