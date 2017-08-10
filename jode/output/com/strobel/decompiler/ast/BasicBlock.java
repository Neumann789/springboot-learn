/* BasicBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.ast;
import java.util.ArrayList;
import java.util.List;

import com.strobel.assembler.Collection;
import com.strobel.decompiler.ITextOutput;

public final class BasicBlock extends Node
{
    private final Collection _body = new Collection();
    
    public final List getBody() {
	return _body;
    }
    
    public final List getChildren() {
	ArrayList childrenCopy = new ArrayList(_body.size());
	childrenCopy.addAll(_body);
	return childrenCopy;
    }
    
    public final void writeTo(ITextOutput output) {
	List children = getChildren();
	boolean previousWasSimpleNode = true;
	int i = 0;
	int childrenSize = children.size();
	for (;;) {
	    IF (i >= childrenSize)
		/* empty */
	    Node child;
	label_1487:
	    {
		child = (Node) children.get(i);
		if (!(child instanceof Expression)
		    && !(child instanceof Label))
		    PUSH false;
		else
		    PUSH true;
		break label_1487;
	    }
	    boolean isSimpleNode;
	label_1488:
	    {
		isSimpleNode = POP;
		if (i != 0 && !isSimpleNode || !previousWasSimpleNode)
		    output.writeLine();
		break label_1488;
	    }
	label_1489:
	    {
		child.writeTo(output);
		if (isSimpleNode)
		    output.writeLine();
		break label_1489;
	    }
	    previousWasSimpleNode = isSimpleNode;
	    i++;
	}
    }
}
