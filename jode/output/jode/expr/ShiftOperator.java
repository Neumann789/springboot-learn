/* ShiftOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import jode.type.Type;

public class ShiftOperator extends BinaryOperator
{
    public ShiftOperator(Type type, int i) {
	super(type, i);
    }
    
    public void updateSubTypes() {
	subExpressions[0].setType(Type.tSubType(type));
	subExpressions[1].setType(Type.tSubType(Type.tInt));
    }
    
    public void updateType() {
	updateParentType(Type.tSuperType(subExpressions[0].getType()));
    }
}
