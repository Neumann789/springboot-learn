/* SwitchSection - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;
import com.strobel.decompiler.patterns.Role;

public class SwitchSection extends AstNode
{
    public static final Role CaseLabelRole
	= new Role("CaseLabel", CaseLabel.class);
    
    public final AstNodeCollection getStatements() {
	return getChildrenByRole(Roles.EMBEDDED_STATEMENT);
    }
    
    public final AstNodeCollection getCaseLabels() {
	return getChildrenByRole(CaseLabelRole);
    }
    
    public NodeType getNodeType() {
	return NodeType.UNKNOWN;
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitSwitchSection(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof SwitchSection))
	    return false;
    label_1676:
	{
	    SwitchSection otherSection = (SwitchSection) other;
	    if (otherSection.isNull()
		|| !getCaseLabels().matches(otherSection.getCaseLabels(),
					    match)
		|| !getStatements().matches(otherSection.getStatements(),
					    match))
		PUSH false;
	    else
		PUSH true;
	    break label_1676;
	}
	return POP;
    }
}
