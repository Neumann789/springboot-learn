/* LabeledStatement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class LabeledStatement extends Statement
{
    public LabeledStatement(int offset) {
	super(offset);
    }
    
    public LabeledStatement(int offset, String name) {
	super(offset);
	setLabel(name);
    }
    
    public LabeledStatement(String name, Statement statement) {
	this(statement.getOffset());
	setLabel(name);
	setStatement(statement);
    }
    
    public final String getLabel() {
	return ((Identifier) getChildByRole(Roles.LABEL)).getName();
    }
    
    public final void setLabel(String value) {
	setChildByRole(Roles.LABEL, Identifier.create(value));
    }
    
    public final Identifier getLabelToken() {
	return (Identifier) getChildByRole(Roles.LABEL);
    }
    
    public final void setLabelToken(Identifier value) {
	setChildByRole(Roles.LABEL, value);
    }
    
    public final JavaTokenNode getColonToken() {
	return (JavaTokenNode) getChildByRole(Roles.COLON);
    }
    
    public final Statement getStatement() {
	return (Statement) getChildByRole(Roles.EMBEDDED_STATEMENT);
    }
    
    public final void setStatement(Statement value) {
	setChildByRole(Roles.EMBEDDED_STATEMENT, value);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitLabeledStatement(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof LabeledStatement))
	    return false;
    label_1650:
	{
	    LabeledStatement otherStatement = (LabeledStatement) other;
	    if (!matchString(getLabel(), otherStatement.getLabel())
		|| !getStatement().matches(otherStatement.getStatement(),
					   match))
		PUSH false;
	    else
		PUSH true;
	    break label_1650;
	}
	return POP;
    }
}
