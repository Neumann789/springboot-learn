/* ThrowBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;

public class ThrowBlock extends ReturnBlock
{
    public ThrowBlock(Expression expression) {
	super(expression);
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.print("throw ");
	PUSH instr;
	if (tabbedprintwriter != null) {
	    /* empty */
	}
	((Expression) POP).dumpExpression(1, tabbedprintwriter);
	tabbedprintwriter.println(";");
    }
}
