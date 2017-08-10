/* MethodGroupExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;
import com.strobel.decompiler.patterns.Role;

public class MethodGroupExpression extends Expression
{
    public static final Role CLOSURE_ARGUMENT_RULE
	= new Role("ClosureArgument", Expression.class, Expression.NULL);
    public static final TokenRole DOUBLE_COLON_ROLE = new TokenRole("::", 2);
    
    public MethodGroupExpression(int offset, Expression target,
				 String methodName) {
	super(offset);
	setTarget(target);
	setMethodName(methodName);
    }
    
    public final AstNodeCollection getClosureArguments() {
	return getChildrenByRole(CLOSURE_ARGUMENT_RULE);
    }
    
    public final JavaTokenNode getDoubleColonToken() {
	return (JavaTokenNode) getChildByRole(DOUBLE_COLON_ROLE);
    }
    
    public final String getMethodName() {
	return ((Identifier) getChildByRole(Roles.IDENTIFIER)).getName();
    }
    
    public final void setMethodName(String name) {
	setChildByRole(Roles.IDENTIFIER, Identifier.create(name));
    }
    
    public final Identifier getMethodNameToken() {
	return (Identifier) getChildByRole(Roles.IDENTIFIER);
    }
    
    public final void setMethodNameToken(Identifier token) {
	setChildByRole(Roles.IDENTIFIER, token);
    }
    
    public final Expression getTarget() {
	return (Expression) getChildByRole(Roles.TARGET_EXPRESSION);
    }
    
    public final void setTarget(Expression value) {
	setChildByRole(Roles.TARGET_EXPRESSION, value);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitMethodGroupExpression(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	return false;
    }
    
    public boolean isReference() {
	return true;
    }
}
