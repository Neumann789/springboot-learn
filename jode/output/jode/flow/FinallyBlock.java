/* FinallyBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;

public class FinallyBlock extends StructuredBlock
{
    StructuredBlock subBlock;
    
    public void setCatchBlock(StructuredBlock structuredblock) {
	subBlock = structuredblock;
	structuredblock.outer = this;
	structuredblock.setFlowBlock(flowBlock);
    }
    
    public boolean replaceSubBlock(StructuredBlock structuredblock,
				   StructuredBlock structuredblock_0_) {
	if (subBlock != structuredblock)
	    return false;
	subBlock = structuredblock_0_;
	return true;
    }
    
    public StructuredBlock[] getSubBlocks() {
	return new StructuredBlock[] { subBlock };
    }
    
    public VariableStack mapStackToLocal(VariableStack variablestack) {
	super.mapStackToLocal(variablestack);
	return null;
    }
    
    public StructuredBlock getNextBlock(StructuredBlock structuredblock) {
	return null;
    }
    
    public FlowBlock getNextFlowBlock(StructuredBlock structuredblock) {
	return null;
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.closeBraceContinue();
	tabbedprintwriter.print("finally");
	tabbedprintwriter.openBrace();
	tabbedprintwriter.tab();
	subBlock.dumpSource(tabbedprintwriter);
	tabbedprintwriter.untab();
    }
}
