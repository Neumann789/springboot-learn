/* NopOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class NopOperator extends Expression
{
    public NopOperator(Type type) {
	super(type);
    }
    
    public int getFreeOperandCount() {
	return 1;
    }
    
    public int getPriority() {
	return 1000;
    }
    
    public void updateSubTypes() {
	/* empty */
    }
    
    public void updateType() {
	/* empty */
    }
    
    public Expression addOperand(Expression expression) {
	expression.setType(type);
	expression.parent = parent;
	return expression;
    }
    
    public boolean isConstant() {
	return false;
    }
    
    public boolean equals(Object object) {
	return object instanceof NopOperator;
    }
    
    public Expression simplify() {
	return this;
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.print("POP");
    }
}
