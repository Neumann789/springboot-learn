/* AnyNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.patterns;

public final class AnyNode extends Pattern
{
    private final String _groupName;
    
    public AnyNode() {
	_groupName = null;
    }
    
    public AnyNode(String groupName) {
	_groupName = groupName;
    }
    
    public final String getGroupName() {
	return _groupName;
    }
    
    public final boolean matches(INode other, Match match) {
    label_1856:
	{
	    match.add(_groupName, other);
	    if (other == null || other.isNull())
		PUSH false;
	    else
		PUSH true;
	    break label_1856;
	}
	return POP;
    }
}
