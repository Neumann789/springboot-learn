/* CompareToIntOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class CompareToIntOperator extends Operator
{
    boolean allowsNaN;
    boolean greaterOnNaN;
    Type compareType;
    
    public CompareToIntOperator(Type type, boolean bool) {
	super(Type.tInt, 0);
	compareType = type;
    label_892:
	{
	    PUSH this;
	    if (type != Type.tFloat && type != Type.tDouble)
		PUSH false;
	    else
		PUSH true;
	    break label_892;
	}
	((CompareToIntOperator) POP).allowsNaN = POP;
	greaterOnNaN = bool;
	initOperands(2);
    }
    
    public int getPriority() {
	return 499;
    }
    
    public void updateSubTypes() {
	subExpressions[0].setType(Type.tSubType(compareType));
	subExpressions[1].setType(Type.tSubType(compareType));
    }
    
    public void updateType() {
	/* empty */
    }
    
    public boolean opEquals(Operator operator) {
	return operator instanceof CompareToIntOperator;
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	subExpressions[0].dumpExpression(tabbedprintwriter, 550);
	tabbedprintwriter.breakOp();
    label_894:
	{
	    tabbedprintwriter.print(" <=>");
	    if (allowsNaN) {
	    label_893:
		{
		    PUSH tabbedprintwriter;
		    if (!greaterOnNaN)
			PUSH "l";
		    else
			PUSH "g";
		    break label_893;
		}
		((TabbedPrintWriter) POP).print(POP);
	    }
	    break label_894;
	}
	tabbedprintwriter.print(" ");
	subExpressions[1].dumpExpression(tabbedprintwriter, 551);
    }
}
