/* BackReference - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.patterns;
import com.strobel.core.CollectionUtilities;

public final class BackReference extends Pattern
{
    private final String _referencedGroupName;
    
    public BackReference(String referencedGroupName) {
	_referencedGroupName = referencedGroupName;
    }
    
    public final String getReferencedGroupName() {
	return _referencedGroupName;
    }
    
    public final boolean matches(INode other, Match match) {
    label_1857:
	{
	    INode node
		= (INode) CollectionUtilities
			      .lastOrDefault(match.get(_referencedGroupName));
	    if (node == null || !node.matches(other))
		PUSH false;
	    else
		PUSH true;
	    break label_1857;
	}
	return POP;
    }
}
