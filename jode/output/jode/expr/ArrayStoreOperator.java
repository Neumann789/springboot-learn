/* ArrayStoreOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.ArrayType;
import jode.type.Type;

public class ArrayStoreOperator extends ArrayLoadOperator
    implements LValueExpression
{
    public ArrayStoreOperator(Type type) {
	super(type);
    }
    
    public boolean matches(Operator operator) {
	return operator instanceof ArrayLoadOperator;
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
    label_890:
	{
	    Type type = subExpressions[0].getType().getHint();
	    if (type instanceof ArrayType) {
		Type type_0_ = ((ArrayType) type).getElementType();
		if (!type_0_.isOfType(getType())) {
		    tabbedprintwriter.print("(");
		    PUSH tabbedprintwriter;
		    if (tabbedprintwriter != null) {
			/* empty */
		    }
		    ((TabbedPrintWriter) POP).startOp(0, 1);
		    tabbedprintwriter.print("(");
		    tabbedprintwriter
			.printType(Type.tArray(getType().getHint()));
		    tabbedprintwriter.print(") ");
		    tabbedprintwriter.breakOp();
		    subExpressions[0].dumpExpression(tabbedprintwriter, 700);
		    tabbedprintwriter.print(")");
		    tabbedprintwriter.breakOp();
		    tabbedprintwriter.print("[");
		    subExpressions[1].dumpExpression(tabbedprintwriter, 0);
		    tabbedprintwriter.print("]");
		    return;
		}
	    }
	    break label_890;
	}
	super.dumpExpression(tabbedprintwriter);
    }
}
