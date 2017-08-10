/* MonitorEnterOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class MonitorEnterOperator extends Operator
{
    public MonitorEnterOperator() {
	super(Type.tVoid, 0);
	initOperands(1);
    }
    
    public int getPriority() {
	return 700;
    }
    
    public void updateSubTypes() {
	subExpressions[0].setType(Type.tUObject);
    }
    
    public void updateType() {
	/* empty */
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.print("MONITORENTER ");
	subExpressions[0].dumpExpression(tabbedprintwriter, 700);
    }
}
