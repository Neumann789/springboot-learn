/* TryCatchBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.ast;
import java.util.Iterator;
import java.util.List;

import com.strobel.assembler.Collection;
import com.strobel.core.ArrayUtilities;
import com.strobel.decompiler.ITextOutput;

public final class TryCatchBlock extends Node
{
    private final List _catchBlocks = new Collection();
    private Block _tryBlock;
    private Block _finallyBlock;
    private boolean _synchronized;
    
    public final Block getTryBlock() {
	return _tryBlock;
    }
    
    public final void setTryBlock(Block tryBlock) {
	_tryBlock = tryBlock;
    }
    
    public final List getCatchBlocks() {
	return _catchBlocks;
    }
    
    public final Block getFinallyBlock() {
	return _finallyBlock;
    }
    
    public final void setFinallyBlock(Block finallyBlock) {
	_finallyBlock = finallyBlock;
    }
    
    public final boolean isSynchronized() {
	return _synchronized;
    }
    
    public final void setSynchronized(boolean simpleSynchronized) {
	_synchronized = simpleSynchronized;
    }
    
    public final List getChildren() {
    label_1513:
	{
	    PUSH _catchBlocks.size();
	    if (_tryBlock == null)
		PUSH false;
	    else
		PUSH true;
	    break label_1513;
	}
    label_1514:
	{
	    PUSH POP + POP;
	    if (_finallyBlock == null)
		PUSH false;
	    else
		PUSH true;
	    break label_1514;
	}
	int size = POP + POP;
	Node[] children = new Node[size];
	int i;
    label_1515:
	{
	    i = 0;
	    if (_tryBlock != null)
		children[i++] = _tryBlock;
	    break label_1515;
	}
	Iterator i$ = _catchBlocks.iterator();
    label_1516:
	{
	    for (;;) {
		if (!i$.hasNext()) {
		    if (_finallyBlock != null)
			children[i++] = _finallyBlock;
		    break label_1516;
		}
		CatchBlock catchBlock = (CatchBlock) i$.next();
		children[i++] = catchBlock;
	    }
	}
	return ArrayUtilities.asUnmodifiableList(children);
	break label_1516;
    }
    
    public final void writeTo(ITextOutput output) {
	output.writeKeyword("try");
	output.writeLine(" {");
    label_1517:
	{
	    output.indent();
	    if (_tryBlock != null)
		_tryBlock.writeTo(output);
	    break label_1517;
	}
	output.unindent();
	output.writeLine("}");
	Iterator i$ = _catchBlocks.iterator();
	for (;;) {
	    if (!i$.hasNext()) {
		if (_finallyBlock != null) {
		    output.writeKeyword("finally");
		    output.writeLine(" {");
		    output.indent();
		    _finallyBlock.writeTo(output);
		    output.unindent();
		    output.writeLine("}");
		}
		return;
	    }
	    CatchBlock catchBlock = (CatchBlock) i$.next();
	    catchBlock.writeTo(output);
	}
	return;
    }
}
