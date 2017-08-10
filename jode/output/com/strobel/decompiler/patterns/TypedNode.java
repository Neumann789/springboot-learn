/* TypedNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.patterns;
import com.strobel.core.VerifyArgument;

public class TypedNode extends Pattern
{
    private final Class _nodeType;
    private final String _groupName;
    
    public TypedNode(Class nodeType) {
	_nodeType = (Class) VerifyArgument.notNull(nodeType, "nodeType");
	_groupName = null;
    }
    
    public TypedNode(String groupName, Class nodeType) {
	_groupName = groupName;
	_nodeType = (Class) VerifyArgument.notNull(nodeType, "nodeType");
    }
    
    public final Class getNodeType() {
	return _nodeType;
    }
    
    public final String getGroupName() {
	return _groupName;
    }
    
    public final boolean matches(INode other, Match match) {
	if (!_nodeType.isInstance(other))
	    return false;
    label_1865:
	{
	    match.add(_groupName, other);
	    if (other.isNull())
		PUSH false;
	    else
		PUSH true;
	    break label_1865;
	}
	return POP;
    }
}
