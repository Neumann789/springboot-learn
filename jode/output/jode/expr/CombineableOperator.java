/* CombineableOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;

public interface CombineableOperator
{
    public LValueExpression getLValue();
    
    public boolean lvalueMatches(Operator operator);
    
    public void makeNonVoid();
}
