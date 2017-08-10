/* NewOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class NewOperator extends NoArgOperator
{
    public NewOperator(Type type) {
	super(type);
    }
    
    public int getPriority() {
	return 950;
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.print("new ");
	tabbedprintwriter.printType(type);
    }
}
