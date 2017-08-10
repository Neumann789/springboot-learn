/* DescriptionBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;

public class DescriptionBlock extends StructuredBlock
{
    String description;
    
    public DescriptionBlock(String string) {
	description = string;
    }
    
    public boolean isEmpty() {
	return true;
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.println(description);
    }
}
