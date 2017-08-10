/* AssertStatement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class AssertStatement extends Statement
{
    public static final TokenRole ASSERT_KEYWORD_ROLE
	= new TokenRole("assert", 1);
    
    public AssertStatement(int offset) {
	super(offset);
    }
    
    public final JavaTokenNode getColon() {
	return (JavaTokenNode) getChildByRole(Roles.COLON);
    }
    
    public final Expression getCondition() {
	return (Expression) getChildByRole(Roles.CONDITION);
    }
    
    public final void setCondition(Expression value) {
	setChildByRole(Roles.CONDITION, value);
    }
    
    public final Expression getMessage() {
	return (Expression) getChildByRole(Roles.EXPRESSION);
    }
    
    public final void setMessage(Expression message) {
	setChildByRole(Roles.EXPRESSION, message);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitAssertStatement(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof AssertStatement))
	    return false;
    label_1587:
	{
	    AssertStatement otherAssert = (AssertStatement) other;
	    if (!getCondition().matches(otherAssert.getCondition(), match)
		|| !getMessage().matches(otherAssert.getMessage()))
		PUSH false;
	    else
		PUSH true;
	    break label_1587;
	}
	return POP;
    }
}
