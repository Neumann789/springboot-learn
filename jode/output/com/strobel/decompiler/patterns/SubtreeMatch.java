/* SubtreeMatch - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.patterns;
import java.util.Iterator;

import com.strobel.core.CollectionUtilities;
import com.strobel.core.Predicate;
import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.utilities.TreeTraversal;

public final class SubtreeMatch extends Pattern
{
    private final boolean _matchMultiple;
    private final INode _target;
    
    public SubtreeMatch(INode target) {
	this(target, false);
    }
    
    public SubtreeMatch(INode target, boolean matchMultiple) {
	_matchMultiple = matchMultiple;
	_target = (INode) VerifyArgument.notNull(target, "target");
    }
    
    public final INode getTarget() {
	return _target;
    }
    
    public final boolean matches(INode other, final Match match) {
	if (!_matchMultiple)
	    return CollectionUtilities.any((TreeTraversal.preOrder
					    (other,
					     (INode
					      .CHILD_ITERATOR))), new Predicate() {
		{
		    super();
		}
		
		public boolean test(INode n) {
		    return _target.matches(n, match);
		}
	    });
	boolean result = false;
	Iterator i$
	    = TreeTraversal.preOrder(other, INode.CHILD_ITERATOR).iterator();
	for (;;) {
	    if (!i$.hasNext())
		return result;
	    INode n = (INode) i$.next();
	    if (_target.matches(n, match))
		result = true;
	    continue;
	}
    }
}
