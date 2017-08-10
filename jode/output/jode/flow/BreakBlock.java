/* BreakBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;

public class BreakBlock extends StructuredBlock
{
    StructuredBlock breaksBlock;
    String label;
    
    public BreakBlock(BreakableBlock breakableblock, boolean bool) {
	breaksBlock = (StructuredBlock) breakableblock;
	breakableblock.setBreaked();
	if (!bool)
	    label = null;
	else
	    label = breakableblock.getLabel();
	return;
    }
    
    public void checkConsistent() {
	super.checkConsistent();
	StructuredBlock structuredblock = outer;
	for (;;) {
	    IF (structuredblock == breaksBlock)
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
	return breaksBlock.getNextBlock();
    }
    
    public FlowBlock getNextFlowBlock() {
	return breaksBlock.getNextFlowBlock();
    }
    
    public VariableStack mapStackToLocal(VariableStack variablestack) {
	((BreakableBlock) breaksBlock).mergeBreakedStack(variablestack);
	return null;
    }
    
    public boolean needsBraces() {
	return false;
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	PUSH tabbedprintwriter;
    label_932:
	{
	    PUSH new StringBuilder().append("break");
	    if (label != null)
		PUSH " " + label;
	    else
		PUSH "";
	    break label_932;
	}
	((TabbedPrintWriter) POP)
	    .println(((StringBuilder) POP).append(POP).append(";").toString());
    }
    
    public boolean jumpMayBeChanged() {
	return true;
    }
}
