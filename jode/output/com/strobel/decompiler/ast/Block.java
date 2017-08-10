/* Block - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.ast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.strobel.assembler.Collection;
import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.ITextOutput;

public class Block extends Node
{
    private final Collection _body = new Collection();
    private Expression _entryGoto;
    
    public Block() {
	/* empty */
    }
    
    public Block(Iterable body) {
	this();
	Iterator i$
	    = ((Iterable) VerifyArgument.notNull(body, "body")).iterator();
	for (;;) {
	    IF (!i$.hasNext())
		/* empty */
	    Node node = (Node) i$.next();
	    _body.add(node);
	}
    }
    
    public transient Block(Node[] body) {
	this();
	Collections.addAll(_body,
			   (Object[]) VerifyArgument.notNull(body, "body"));
    }
    
    public final Expression getEntryGoto() {
	return _entryGoto;
    }
    
    public final void setEntryGoto(Expression entryGoto) {
	_entryGoto = entryGoto;
    }
    
    public final List getBody() {
	return _body;
    }
    
    public final List getChildren() {
	ArrayList childrenCopy;
    label_1490:
	{
	    childrenCopy = new ArrayList(_body.size() + 1);
	    if (_entryGoto != null)
		childrenCopy.add(_entryGoto);
	    break label_1490;
	}
	childrenCopy.addAll(_body);
	return childrenCopy;
    }
    
    public void writeTo(ITextOutput output) {
	List children = getChildren();
	boolean previousWasSimpleNode = true;
	int i = 0;
	int childrenSize = children.size();
	for (;;) {
	    IF (i >= childrenSize)
		/* empty */
	    Node child;
	label_1491:
	    {
		child = (Node) children.get(i);
		if (!(child instanceof Expression)
		    && !(child instanceof Label))
		    PUSH false;
		else
		    PUSH true;
		break label_1491;
	    }
	    boolean isSimpleNode;
	label_1492:
	    {
		isSimpleNode = POP;
		if (i != 0 && !isSimpleNode || !previousWasSimpleNode)
		    output.writeLine();
		break label_1492;
	    }
	label_1493:
	    {
		child.writeTo(output);
		if (isSimpleNode)
		    output.writeLine();
		break label_1493;
	    }
	    previousWasSimpleNode = isSimpleNode;
	    i++;
	}
    }
}
