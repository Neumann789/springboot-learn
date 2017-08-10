/* ContinueBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;

public class ContinueBlock extends StructuredBlock
{
    LoopBlock continuesBlock;
    String continueLabel;
    
    public ContinueBlock(LoopBlock loopblock, boolean bool) {
	continuesBlock = loopblock;
	if (!bool)
	    continueLabel = null;
	else
	    continueLabel = loopblock.getLabel();
	return;
    }
    
    public void checkConsistent() {
	super.checkConsistent();
	StructuredBlock structuredblock = outer;
	for (;;) {
	    IF (structuredblock == continuesBlock)
		/* empty */
	    if (structuredblock != null)
		structuredblock = structuredblock.outer;
	    throw new RuntimeException("Inconsistency");
	}
    }
    
    public boolean isEmpty() {
	return true;
    }
    
    public StructuredBlock getNextBlock() {
	return continuesBlock;
    }
    
    public FlowBlock getNextFlowBlock() {
	return null;
    }
    
    public VariableStack mapStackToLocal(VariableStack variablestack) {
	continuesBlock.mergeContinueStack(variablestack);
	return null;
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	PUSH tabbedprintwriter;
    label_937:
	{
	    PUSH new StringBuilder().append("continue");
	    if (continueLabel != null)
		PUSH " " + continueLabel;
	    else
		PUSH "";
	    break label_937;
	}
	((TabbedPrintWriter) POP)
	    .println(((StringBuilder) POP).append(POP).append(";").toString());
    }
    
    public boolean needsBraces() {
	return false;
    }
    
    public boolean jumpMayBeChanged() {
	return true;
    }
}
