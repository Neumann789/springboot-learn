/* Jump - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;

public class Jump
{
    StructuredBlock prev;
    FlowBlock destination;
    Jump next;
    VariableStack stackMap;
    
    public Jump(FlowBlock flowblock) {
	destination = flowblock;
    }
    
    public Jump(Jump jump_0_) {
	destination = jump_0_.destination;
	next = jump_0_.next;
	jump_0_.next = this;
    }
    
    public void dumpSource(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	if (destination != null)
	    tabbedprintwriter.println("GOTO " + destination.getLabel());
	else
	    tabbedprintwriter.println("GOTO null-ptr!!!!!");
	return;
    }
}
