/* TryCatchStatement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;
import com.strobel.decompiler.patterns.Role;

public class TryCatchStatement extends Statement
{
    public static final TokenRole TRY_KEYWORD_ROLE = new TokenRole("try", 1);
    public static final Role TRY_BLOCK_ROLE
	= new Role("TryBlock", BlockStatement.class, BlockStatement.NULL);
    public static final Role CATCH_CLAUSE_ROLE
	= new Role("CatchClause", CatchClause.class);
    public static final TokenRole FINALLY_KEYWORD_ROLE
	= new TokenRole("finally", 1);
    public static final Role FINALLY_BLOCK_ROLE
	= new Role("FinallyBlock", BlockStatement.class, BlockStatement.NULL);
    public static final Role TRY_RESOURCE_ROLE
	= new Role("TryResource", VariableDeclarationStatement.class);
    
    public TryCatchStatement(int offset) {
	super(offset);
    }
    
    public final JavaTokenNode getTryToken() {
	return (JavaTokenNode) getChildByRole(TRY_KEYWORD_ROLE);
    }
    
    public final JavaTokenNode getFinallyToken() {
	return (JavaTokenNode) getChildByRole(FINALLY_KEYWORD_ROLE);
    }
    
    public final AstNodeCollection getCatchClauses() {
	return getChildrenByRole(CATCH_CLAUSE_ROLE);
    }
    
    public final AstNodeCollection getResources() {
	return getChildrenByRole(TRY_RESOURCE_ROLE);
    }
    
    public final BlockStatement getTryBlock() {
	return (BlockStatement) getChildByRole(TRY_BLOCK_ROLE);
    }
    
    public final void setTryBlock(BlockStatement value) {
	setChildByRole(TRY_BLOCK_ROLE, value);
    }
    
    public final BlockStatement getFinallyBlock() {
	return (BlockStatement) getChildByRole(FINALLY_BLOCK_ROLE);
    }
    
    public final void setFinallyBlock(BlockStatement value) {
	setChildByRole(FINALLY_BLOCK_ROLE, value);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitTryCatchStatement(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof TryCatchStatement))
	    return false;
    label_1679:
	{
	    TryCatchStatement otherStatement = (TryCatchStatement) other;
	    if (otherStatement.isNull()
		|| !getTryBlock().matches(otherStatement.getTryBlock(), match)
		|| !getCatchClauses().matches(otherStatement.getCatchClauses(),
					      match)
		|| !getFinallyBlock().matches(otherStatement.getFinallyBlock(),
					      match))
		PUSH false;
	    else
		PUSH true;
	    break label_1679;
	}
	return POP;
    }
}
