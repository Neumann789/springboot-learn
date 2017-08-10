/* ArrayLengthOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class ArrayLengthOperator extends Operator
{
    public ArrayLengthOperator() {
	super(Type.tInt, 0);
	initOperands(1);
    }
    
    public int getPriority() {
	return 950;
    }
    
    public void updateSubTypes() {
	subExpressions[0].setType(Type.tArray(Type.tUnknown));
    }
    
    public void updateType() {
	/* empty */
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	subExpressions[0].dumpExpression(tabbedprintwriter, 900);
	tabbedprintwriter.print(".length");
    }
}
