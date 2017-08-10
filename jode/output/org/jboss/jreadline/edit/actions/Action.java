/* Action - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit.actions;

public final class Action extends Enum
{
    public static final Action DELETE = new Action("DELETE", 0);
    public static final Action MOVE = new Action("MOVE", 1);
    public static final Action YANK = new Action("YANK", 2);
    public static final Action CHANGE = new Action("CHANGE", 3);
    public static final Action EDIT = new Action("EDIT", 4);
    public static final Action COMMAND = new Action("COMMAND", 5);
    public static final Action HISTORY = new Action("HISTORY", 6);
    public static final Action SEARCH = new Action("SEARCH", 7);
    public static final Action NEWLINE = new Action("NEWLINE", 8);
    public static final Action PASTE = new Action("PASTE", 9);
    public static final Action PASTE_FROM_CLIPBOARD
	= new Action("PASTE_FROM_CLIPBOARD", 10);
    public static final Action COMPLETE = new Action("COMPLETE", 11);
    public static final Action UNDO = new Action("UNDO", 12);
    public static final Action CASE = new Action("CASE", 13);
    public static final Action EXIT = new Action("EXIT", 14);
    public static final Action CLEAR = new Action("CLEAR", 15);
    public static final Action ABORT = new Action("ABORT", 16);
    public static final Action CHANGE_EDITMODE
	= new Action("CHANGE_EDITMODE", 17);
    public static final Action NO_ACTION = new Action("NO_ACTION", 18);
    public static final Action REPLACE = new Action("REPLACE", 19);
    /*synthetic*/ private static final Action[] $VALUES
		      = { DELETE, MOVE, YANK, CHANGE, EDIT, COMMAND, HISTORY,
			  SEARCH, NEWLINE, PASTE, PASTE_FROM_CLIPBOARD,
			  COMPLETE, UNDO, CASE, EXIT, CLEAR, ABORT,
			  CHANGE_EDITMODE, NO_ACTION, REPLACE };
    
    public static Action[] values() {
	return (Action[]) $VALUES.clone();
    }
    
    public static Action valueOf(String name) {
	return (Action) Enum.valueOf(Action.class, name);
    }
    
    private Action(String string, int i) {
	super(string, i);
    }
}
