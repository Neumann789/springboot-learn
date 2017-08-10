/* Switch - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.ast;
import java.util.Iterator;
import java.util.List;

import com.strobel.assembler.Collection;
import com.strobel.core.ArrayUtilities;
import com.strobel.decompiler.ITextOutput;

public final class Switch extends Node
{
    private final List _caseBlocks = new Collection();
    private Expression _condition;
    
    public final Expression getCondition() {
	return _condition;
    }
    
    public final void setCondition(Expression condition) {
	_condition = condition;
    }
    
    public final List getCaseBlocks() {
	return _caseBlocks;
    }
    
    public final List getChildren() {
    label_1509:
	{
	    PUSH _caseBlocks.size();
	    if (_condition == null)
		PUSH false;
	    else
		PUSH true;
	    break label_1509;
	}
	int size = POP + POP;
	Node[] children = new Node[size];
	int i;
    label_1510:
	{
	    i = 0;
	    if (_condition != null)
		children[i++] = _condition;
	    break label_1510;
	}
	Iterator i$ = _caseBlocks.iterator();
	for (;;) {
	    if (!i$.hasNext())
		return ArrayUtilities.asUnmodifiableList(children);
	    CaseBlock caseBlock = (CaseBlock) i$.next();
	    children[i++] = caseBlock;
	}
    }
    
    public final void writeTo(ITextOutput output) {
	output.writeKeyword("switch");
    label_1511:
	{
	    output.write(" (");
	    if (_condition == null)
		output.write("...");
	    else
		_condition.writeTo(output);
	    break label_1511;
	}
	output.writeLine(") {");
	output.indent();
	int i = 0;
	int n = _caseBlocks.size();
	for (;;) {
	    if (i >= n) {
		output.unindent();
		output.writeLine("}");
	    }
	    CaseBlock caseBlock;
	label_1512:
	    {
		caseBlock = (CaseBlock) _caseBlocks.get(i);
		if (i != 0)
		    output.writeLine();
		break label_1512;
	    }
	    caseBlock.writeTo(output);
	    i++;
	}
    }
}
