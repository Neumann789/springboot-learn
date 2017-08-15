/* SimpleAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit.actions;

public class SimpleAction extends EditAction
{
    public SimpleAction(int start, Action action) {
	super(start, action);
    }
    
    public SimpleAction(int start, Action action, int end) {
	super(start, action);
	setEnd(end);
    }
    
    public void doAction(String buffer) {
	if (buffer.length() < getEnd())
	    setEnd(buffer.length());
	if (getEnd() < 0)
	    setEnd(0);
    }
}
