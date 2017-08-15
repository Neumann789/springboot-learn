/* PrevWordAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit.actions;

public class PrevWordAction extends EditAction
{
    public PrevWordAction(int start, Action action) {
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
	if (cursor > 0 && isDelimiter(buffer.charAt(cursor - 1))) {
	    for (/**/; cursor > 0 && isDelimiter(buffer.charAt(cursor - 1));
		 cursor--) {
		/* empty */
	    }
	} else {
	    for (/**/; cursor > 0 && !isDelimiter(buffer.charAt(cursor - 1));
		 cursor--) {
		/* empty */
	    }
	}
	setEnd(cursor);
    }
}
