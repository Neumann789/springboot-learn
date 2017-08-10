/* NextWordAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit.actions;

public class NextWordAction extends EditAction
{
    private boolean removeTrailingSpaces = true;
    
    public NextWordAction(int start, Action action) {
	super(start, action);
	if (getAction() == Action.CHANGE)
	    removeTrailingSpaces = false;
	return;
    }
    
    public void doAction(String buffer) {
	int cursor;
    label_244:
	{
	    cursor = getStart();
	    if (cursor >= buffer.length()
		|| !isDelimiter(buffer.charAt(cursor))) {
		for (;;) {
		    if (cursor >= buffer.length()
			|| isDelimiter(buffer.charAt(cursor))) {
			if (removeTrailingSpaces && cursor < buffer.length()
			    && isSpace(buffer.charAt(cursor))) {
			    for (/**/;
				 (cursor < buffer.length()
				  && isSpace(buffer.charAt(cursor)));
				 cursor++) {
				/* empty */
			    }
			}
			break label_244;
		    }
		    cursor++;
		}
	    } else {
		for (/**/; (cursor < buffer.length()
			    && isDelimiter(buffer.charAt(cursor))); cursor++) {
		    /* empty */
		}
	    }
	    break label_244;
	}
	setEnd(cursor);
    }
}
