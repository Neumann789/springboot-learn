/* NewArrayOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.ArrayType;
import jode.type.Type;

public class NewArrayOperator extends Operator
{
    String baseTypeString;
    
    public NewArrayOperator(Type type, int i) {
	super(type, 0);
	initOperands(i);
    }
    
    public int getDimensions() {
	return subExpressions.length;
    }
    
    public int getPriority() {
	return 900;
    }
    
    public void updateSubTypes() {
	int i = 0;
	for (;;) {
	    IF (i >= subExpressions.length)
		/* empty */
	    subExpressions[i].setType(Type.tUInt);
	    i++;
	}
    }
    
    public void updateType() {
	/* empty */
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	Type type = this.type.getCanonic();
	int i = 0;
	for (;;) {
	    if (!(type instanceof ArrayType)) {
		tabbedprintwriter.print("new ");
		tabbedprintwriter.printType(type.getHint());
		int i_0_ = 0;
		for (;;) {
		    IF (i_0_ >= i)
			/* empty */
		    tabbedprintwriter.breakOp();
		label_930:
		    {
			tabbedprintwriter.print("[");
			if (i_0_ < subExpressions.length)
			    subExpressions[i_0_]
				.dumpExpression(tabbedprintwriter, 0);
			break label_930;
		    }
		    tabbedprintwriter.print("]");
		    i_0_++;
		}
	    }
	    type = ((ArrayType) type).getElementType();
	    i++;
	}
    }
}
