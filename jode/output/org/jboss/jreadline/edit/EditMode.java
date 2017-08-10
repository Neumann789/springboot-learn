/* EditMode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit;
import org.jboss.jreadline.edit.actions.Action;
import org.jboss.jreadline.edit.actions.Operation;

public interface EditMode
{
    public Operation parseInput(int[] is);
    
    public Action getCurrentAction();
    
    public Mode getMode();
}
