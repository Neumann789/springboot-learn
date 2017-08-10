/* Choice - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.patterns;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.strobel.core.VerifyArgument;

public final class Choice extends Pattern implements Iterable
{
    private final ArrayList _alternatives;
    
    public Choice() {
	_alternatives = new ArrayList();
    }
    
    public transient Choice(INode[] alternatives) {
	_alternatives = new ArrayList();
	Collections.addAll(_alternatives,
			   (Object[]) VerifyArgument.notNull(alternatives,
							     "alternatives"));
    }
    
    public final void add(INode alternative) {
	_alternatives.add(VerifyArgument.notNull(alternative, "alternative"));
    }
    
    public final void add(String name, INode alternative) {
	_alternatives.add
	    (new NamedNode(name,
			   (INode) VerifyArgument.notNull(alternative,
							  "alternative")));
    }
    
    public final Iterator iterator() {
	return _alternatives.iterator();
    }
    
    public final boolean matches(INode other, Match match) {
	int checkpoint = match.getCheckPoint();
	Iterator i$ = _alternatives.iterator();
	for (;;) {
	    if (!i$.hasNext())
		return false;
	    INode alternative = (INode) i$.next();
	    if (!alternative.matches(other, match))
		match.restoreCheckPoint(checkpoint);
	    return true;
	}
    }
}
