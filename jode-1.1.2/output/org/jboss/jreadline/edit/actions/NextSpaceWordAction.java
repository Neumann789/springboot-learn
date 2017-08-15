/* NextSpaceWordAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit.actions;

public class NextSpaceWordAction extends EditAction
{
    public NextSpaceWordAction(int start, Action action) {
	super(start, action);
    }
    
    public void doAction(String buffer) {
	int cursor = getStart();
	if (cursor < buffer.length() && isDelimiter(buffer.charAt(cursor))) {
	    for (/**/; (cursor < buffer.length()
			&& isDelimiter(buffer.charAt(cursor))); cursor++) {
		/* empty */
	    }
	} else {
	    for (/**/;
		 cursor < buffer.length() && !isSpace(buffer.charAt(cursor));
		 cursor++) {
		/* empty */
	    }
	    if (cursor < buffer.length() && isSpace(buffer.charAt(cursor))) {
		for (/**/; (cursor < buffer.length()
			    && isSpace(buffer.charAt(cursor))); cursor++) {
		    /* empty */
		}
	    }
	}
	setEnd(cursor);
    }
}
