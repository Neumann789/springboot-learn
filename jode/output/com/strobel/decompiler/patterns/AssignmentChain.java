/* AssignmentChain - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.patterns;
import java.util.ArrayDeque;

import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
import com.strobel.decompiler.languages.java.ast.AssignmentOperatorType;

public class AssignmentChain extends Pattern
{
    private final INode _valuePattern;
    private final INode _targetPattern;
    
    public AssignmentChain(INode targetPattern, INode valuePattern) {
	_targetPattern
	    = (INode) VerifyArgument.notNull(targetPattern, "targetPattern");
	_valuePattern
	    = (INode) VerifyArgument.notNull(valuePattern, "valuePattern");
    }
    
    public final INode getTargetPattern() {
	return _targetPattern;
    }
    
    public final INode getValuePattern() {
	return _valuePattern;
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof AssignmentExpression))
	    return false;
	ArrayDeque assignments = new ArrayDeque();
	INode current = other;
	int checkPoint = match.getCheckPoint();
	AssignmentExpression assignment;
	for (/**/;
	     (current instanceof AssignmentExpression
	      && (((AssignmentExpression) current).getOperator()
		  == AssignmentOperatorType.ASSIGN));
	     current = assignment.getRight()) {
	    assignment = (AssignmentExpression) current;
	    com.strobel.decompiler.languages.java.ast.Expression target
		= assignment.getLeft();
	    if (_targetPattern.matches(target, match))
		assignments.addLast(assignment);
	    assignments.clear();
	    match.restoreCheckPoint(checkPoint);
	    break;
	}
	if (!assignments.isEmpty()
	    && _valuePattern.matches(((AssignmentExpression)
				      assignments.getLast())
					 .getRight(),
				     match))
	    return true;
	match.restoreCheckPoint(checkPoint);
	return false;
    }
}
