/* BreakableBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;

public interface BreakableBlock
{
    public String getLabel();
    
    public void setBreaked();
    
    public void mergeBreakedStack(VariableStack variablestack);
}
