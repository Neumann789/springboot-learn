/* SimpleOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import jode.type.Type;

public abstract class SimpleOperator extends Operator
{
    public SimpleOperator(Type type, int i, int i_0_) {
	super(type, i);
	initOperands(i_0_);
    }
}
