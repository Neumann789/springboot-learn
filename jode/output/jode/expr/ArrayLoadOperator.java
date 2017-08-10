/* ArrayLoadOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.ArrayType;
import jode.type.Type;

public class ArrayLoadOperator extends Operator
{
    public ArrayLoadOperator(Type type) {
	super(type, 0);
	initOperands(2);
    }
    
    public int getPriority() {
	return 950;
    }
    
    public void updateSubTypes() {
	subExpressions[0].setType(Type.tSubType(Type.tArray(type)));
	subExpressions[1].setType(Type.tSubType(Type.tInt));
    }
    
    public void updateType() {
	Type type = Type.tSuperType(subExpressions[0].getType())
			.intersection(Type.tArray(this.type));
	if (type instanceof ArrayType)
	    updateParentType(((ArrayType) type).getElementType());
	else
	    updateParentType(Type.tError);
	return;
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	subExpressions[0].dumpExpression(tabbedprintwriter, 950);
	tabbedprintwriter.breakOp();
	tabbedprintwriter.print("[");
	subExpressions[1].dumpExpression(tabbedprintwriter, 0);
	tabbedprintwriter.print("]");
    }
}
