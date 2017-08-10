/* CheckCastOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class CheckCastOperator extends Operator
{
    Type castType;
    
    public CheckCastOperator(Type type) {
	super(type, 0);
	castType = type;
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
    
    public Expression simplify() {
	if (!subExpressions[0].getType().getCanonic()
		 .isOfType(Type.tSubType(castType)))
	    return super.simplify();
	return subExpressions[0].simplify();
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.print("(");
	tabbedprintwriter.printType(castType);
	tabbedprintwriter.print(") ");
	tabbedprintwriter.breakOp();
    label_891:
	{
	    Type type = castType.getCastHelper(subExpressions[0].getType());
	    if (type != null) {
		tabbedprintwriter.print("(");
		tabbedprintwriter.printType(type);
		tabbedprintwriter.print(") ");
		tabbedprintwriter.breakOp();
	    }
	    break label_891;
	}
	subExpressions[0].dumpExpression(tabbedprintwriter, 700);
    }
}
