/* IIncOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.AssertError;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class IIncOperator extends Operator implements CombineableOperator
{
    int value;
    
    public IIncOperator(LocalStoreOperator localstoreoperator, int i,
			int i_0_) {
	super(Type.tVoid, i_0_);
	value = i;
	initOperands(1);
	setSubExpressions(0, localstoreoperator);
    }
    
    public LValueExpression getLValue() {
	return (LValueExpression) subExpressions[0];
    }
    
    public int getValue() {
	return value;
    }
    
    public int getPriority() {
	return 100;
    }
    
    public void updateSubTypes() {
    label_923:
	{
	    PUSH subExpressions[0];
	    if (type == Type.tVoid)
		PUSH Type.tInt;
	    else
		PUSH type;
	    break label_923;
	}
	((Expression) POP).setType(POP);
    }
    
    public void updateType() {
	if (type != Type.tVoid)
	    updateParentType(subExpressions[0].getType());
	return;
    }
    
    public void makeNonVoid() {
	if (type == Type.tVoid)
	    type = subExpressions[0].getType();
	throw new AssertError("already non void");
    }
    
    public boolean lvalueMatches(Operator operator) {
	return getLValue().matches(operator);
    }
    
    public Expression simplify() {
    label_924:
	{
	    if (value != 1)
		return super.simplify();
	    if (getOperatorIndex() != 13)
		PUSH 25;
	    else
		PUSH 24;
	    break label_924;
	}
	int i = POP;
	return new PrePostFixOperator(getType(), i, getLValue(), isVoid())
		   .simplify();
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	PUSH tabbedprintwriter;
	if (tabbedprintwriter != null) {
	    /* empty */
	}
	((TabbedPrintWriter) POP).startOp(1, 2);
	subExpressions[0].dumpExpression(tabbedprintwriter);
	tabbedprintwriter.endOp();
	tabbedprintwriter.print(getOperatorString() + value);
    }
}
