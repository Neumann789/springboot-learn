/* NamedNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.patterns;
import com.strobel.core.VerifyArgument;

public final class NamedNode extends Pattern
{
    private final String _groupName;
    private final INode _node;
    
    public NamedNode(String groupName, INode node) {
	_groupName = groupName;
	_node = (INode) VerifyArgument.notNull(node, "node");
    }
    
    public final String getGroupName() {
	return _groupName;
    }
    
    public final INode getNode() {
	return _node;
    }
    
    public final boolean matches(INode other, Match match) {
	match.add(_groupName, other);
	return _node.matches(other, match);
    }
}
