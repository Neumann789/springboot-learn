/* NoArgOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import jode.type.Type;

public abstract class NoArgOperator extends Operator
{
    public NoArgOperator(Type type, int i) {
	super(type, i);
	initOperands(0);
    }
    
    public NoArgOperator(Type type) {
	this(type, 0);
    }
    
    public void updateType() {
	/* empty */
    }
    
    public void updateSubTypes() {
	/* empty */
    }
}
