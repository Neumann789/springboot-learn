/* RetBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;

public class RetBlock extends StructuredBlock
{
    LocalInfo local;
    
    public RetBlock(LocalInfo localinfo) {
	local = localinfo;
    }
    
    public void fillInGenSet(Set set, Set set_0_) {
	set.add(local);
	set_0_.add(local);
    }
    
    public VariableStack mapStackToLocal(VariableStack variablestack) {
	if (variablestack.isEmpty())
	    return null;
	throw new IllegalArgumentException("stack is not empty at RET");
    }
    
    public Set getDeclarables() {
	return Collections.singleton(local);
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.println("RET " + local);
    }
}
