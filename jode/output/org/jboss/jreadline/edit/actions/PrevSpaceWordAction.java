/* PrevSpaceWordAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit.actions;

public class PrevSpaceWordAction extends EditAction
{
    public PrevSpaceWordAction(int start, Action action) {
	super(start, action);
    }
    
    public void doAction(String buffer) {
	int cursor;
    label_245:
	{
	    cursor = getStart();
	    if (cursor > buffer.length())
		cursor = buffer.length() - 1;
	    break label_245;
	}
	for (;;) {
	    if (cursor <= 0 || !isSpace(buffer.charAt(cursor - 1))) {
		for (;;) {
		    if (cursor <= 0 || isSpace(buffer.charAt(cursor - 1)))
			setEnd(cursor);
		    cursor--;
		}
	    }
	    cursor--;
	}
    }
}
