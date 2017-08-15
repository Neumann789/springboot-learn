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
	int cursor = getStart();
	if (cursor > buffer.length())
	    cursor = buffer.length() - 1;
	for (/**/; cursor > 0 && isSpace(buffer.charAt(cursor - 1));
	     cursor--) {
	    /* empty */
	}
	for (/**/; cursor > 0 && !isSpace(buffer.charAt(cursor - 1));
	     cursor--) {
	    /* empty */
	}
	setEnd(cursor);
    }
}
