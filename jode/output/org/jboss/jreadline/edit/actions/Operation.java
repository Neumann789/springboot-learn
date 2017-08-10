/* Operation - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit.actions;

public final class Operation extends Enum
{
    public static final Operation SEARCH_PREV
	= new Operation("SEARCH_PREV", 0, Movement.PREV, Action.SEARCH);
    public static final Operation SEARCH_NEXT
	= new Operation("SEARCH_NEXT", 1, Movement.NEXT, Action.SEARCH);
    public static final Operation SEARCH_END
	= new Operation("SEARCH_END", 2, Movement.END, Action.SEARCH);
    public static final Operation SEARCH_PREV_WORD
	= new Operation("SEARCH_PREV_WORD", 3, Movement.PREV_WORD,
			Action.SEARCH);
    public static final Operation SEARCH_NEXT_WORD
	= new Operation("SEARCH_NEXT_WORD", 4, Movement.NEXT_WORD,
			Action.SEARCH);
    public static final Operation SEARCH_DELETE
	= new Operation("SEARCH_DELETE", 5, Movement.PREV_BIG_WORD,
			Action.SEARCH);
    public static final Operation SEARCH_EXIT
	= new Operation("SEARCH_EXIT", 6, Movement.NEXT_BIG_WORD,
			Action.SEARCH);
    public static final Operation SEARCH_INPUT
	= new Operation("SEARCH_INPUT", 7, Movement.ALL, Action.SEARCH);
    public static final Operation NEW_LINE
	= new Operation("NEW_LINE", 8, Action.NEWLINE);
    public static final Operation NO_ACTION
	= new Operation("NO_ACTION", 9, Movement.PREV, Action.NO_ACTION);
    public static final Operation COMPLETE
	= new Operation("COMPLETE", 10, Movement.NEXT, Action.COMPLETE);
    public static final Operation EDIT
	= new Operation("EDIT", 11, Action.EDIT);
    public static final Operation CLEAR
	= new Operation("CLEAR", 12, Action.CLEAR);
    public static final Operation HISTORY_NEXT
	= new Operation("HISTORY_NEXT", 13, Movement.NEXT, Action.HISTORY);
    public static final Operation HISTORY_PREV
	= new Operation("HISTORY_PREV", 14, Movement.PREV, Action.HISTORY);
    public static final Operation PREV_CHAR
	= new Operation("PREV_CHAR", 15, Movement.PREV, Action.NO_ACTION);
    public static final Operation NEXT_CHAR
	= new Operation("NEXT_CHAR", 16, Movement.NEXT, Action.NO_ACTION);
    public static final Operation NEXT_WORD
	= new Operation("NEXT_WORD", 17, Movement.NEXT_WORD, Action.NO_ACTION);
    public static final Operation PREV_WORD
	= new Operation("PREV_WORD", 18, Movement.PREV_WORD, Action.NO_ACTION);
    public static final Operation NEXT_BIG_WORD
	= new Operation("NEXT_BIG_WORD", 19, Movement.NEXT_BIG_WORD,
			Action.NO_ACTION);
    public static final Operation PREV_BIG_WORD
	= new Operation("PREV_BIG_WORD", 20, Movement.PREV_BIG_WORD,
			Action.NO_ACTION);
    public static final Operation MOVE_PREV_CHAR
	= new Operation("MOVE_PREV_CHAR", 21, Movement.PREV, Action.MOVE);
    public static final Operation MOVE_NEXT_CHAR
	= new Operation("MOVE_NEXT_CHAR", 22, Movement.NEXT, Action.MOVE);
    public static final Operation MOVE_PREV_WORD
	= new Operation("MOVE_PREV_WORD", 23, Movement.PREV_WORD, Action.MOVE);
    public static final Operation MOVE_PREV_BIG_WORD
	= new Operation("MOVE_PREV_BIG_WORD", 24, Movement.PREV_BIG_WORD,
			Action.MOVE);
    public static final Operation MOVE_NEXT_WORD
	= new Operation("MOVE_NEXT_WORD", 25, Movement.NEXT_WORD, Action.MOVE);
    public static final Operation MOVE_NEXT_BIG_WORD
	= new Operation("MOVE_NEXT_BIG_WORD", 26, Movement.NEXT_BIG_WORD,
			Action.MOVE);
    public static final Operation MOVE_BEGINNING
	= new Operation("MOVE_BEGINNING", 27, Movement.BEGINNING, Action.MOVE);
    public static final Operation MOVE_END
	= new Operation("MOVE_END", 28, Movement.END, Action.MOVE);
    public static final Operation DELETE_PREV_CHAR
	= new Operation("DELETE_PREV_CHAR", 29, Movement.PREV, Action.DELETE);
    public static final Operation DELETE_NEXT_CHAR
	= new Operation("DELETE_NEXT_CHAR", 30, Movement.NEXT, Action.DELETE);
    public static final Operation DELETE_PREV_WORD
	= new Operation("DELETE_PREV_WORD", 31, Movement.PREV_WORD,
			Action.DELETE);
    public static final Operation DELETE_PREV_BIG_WORD
	= new Operation("DELETE_PREV_BIG_WORD", 32, Movement.PREV_BIG_WORD,
			Action.DELETE);
    public static final Operation DELETE_NEXT_WORD
	= new Operation("DELETE_NEXT_WORD", 33, Movement.NEXT_WORD,
			Action.DELETE);
    public static final Operation DELETE_NEXT_BIG_WORD
	= new Operation("DELETE_NEXT_BIG_WORD", 34, Movement.NEXT_BIG_WORD,
			Action.DELETE);
    public static final Operation DELETE_BEGINNING
	= new Operation("DELETE_BEGINNING", 35, Movement.BEGINNING,
			Action.DELETE);
    public static final Operation DELETE_END
	= new Operation("DELETE_END", 36, Movement.END, Action.DELETE);
    public static final Operation DELETE_ALL
	= new Operation("DELETE_ALL", 37, Movement.ALL, Action.DELETE);
    public static final Operation CHANGE_PREV_CHAR
	= new Operation("CHANGE_PREV_CHAR", 38, Movement.PREV, Action.CHANGE);
    public static final Operation CHANGE_NEXT_CHAR
	= new Operation("CHANGE_NEXT_CHAR", 39, Movement.NEXT, Action.CHANGE);
    public static final Operation CHANGE_PREV_WORD
	= new Operation("CHANGE_PREV_WORD", 40, Movement.PREV_WORD,
			Action.CHANGE);
    public static final Operation CHANGE_PREV_BIG_WORD
	= new Operation("CHANGE_PREV_BIG_WORD", 41, Movement.PREV_BIG_WORD,
			Action.CHANGE);
    public static final Operation CHANGE_NEXT_WORD
	= new Operation("CHANGE_NEXT_WORD", 42, Movement.NEXT_WORD,
			Action.CHANGE);
    public static final Operation CHANGE_NEXT_BIG_WORD
	= new Operation("CHANGE_NEXT_BIG_WORD", 43, Movement.NEXT_BIG_WORD,
			Action.CHANGE);
    public static final Operation CHANGE_BEGINNING
	= new Operation("CHANGE_BEGINNING", 44, Movement.BEGINNING,
			Action.CHANGE);
    public static final Operation CHANGE_END
	= new Operation("CHANGE_END", 45, Movement.END, Action.CHANGE);
    public static final Operation CHANGE_ALL
	= new Operation("CHANGE_ALL", 46, Movement.ALL, Action.CHANGE);
    public static final Operation CHANGE
	= new Operation("CHANGE", 47, Action.NO_ACTION);
    public static final Operation YANK_PREV_CHAR
	= new Operation("YANK_PREV_CHAR", 48, Movement.PREV, Action.YANK);
    public static final Operation YANK_NEXT_CHAR
	= new Operation("YANK_NEXT_CHAR", 49, Movement.NEXT, Action.YANK);
    public static final Operation YANK_PREV_WORD
	= new Operation("YANK_PREV_WORD", 50, Movement.PREV_WORD, Action.YANK);
    public static final Operation YANK_PREV_BIG_WORD
	= new Operation("YANK_PREV_BIG_WORD", 51, Movement.PREV_BIG_WORD,
			Action.YANK);
    public static final Operation YANK_NEXT_WORD
	= new Operation("YANK_NEXT_WORD", 52, Movement.NEXT_WORD, Action.YANK);
    public static final Operation YANK_NEXT_BIG_WORD
	= new Operation("YANK_NEXT_BIG_WORD", 53, Movement.NEXT_BIG_WORD,
			Action.YANK);
    public static final Operation YANK_BEGINNING
	= new Operation("YANK_BEGINNING", 54, Movement.BEGINNING, Action.YANK);
    public static final Operation YANK_END
	= new Operation("YANK_END", 55, Movement.END, Action.YANK);
    public static final Operation YANK_ALL
	= new Operation("YANK_ALL", 56, Movement.ALL, Action.YANK);
    public static final Operation BEGINNING
	= new Operation("BEGINNING", 57, Movement.BEGINNING, Action.NO_ACTION);
    public static final Operation END
	= new Operation("END", 58, Movement.BEGINNING, Action.NO_ACTION);
    public static final Operation INSERT
	= new Operation("INSERT", 59, Action.NO_ACTION);
    public static final Operation INSERT_BEGINNING
	= new Operation("INSERT_BEGINNING", 60, Action.NO_ACTION);
    public static final Operation ESCAPE
	= new Operation("ESCAPE", 61, Action.NO_ACTION);
    public static final Operation PGUP
	= new Operation("PGUP", 62, Action.NO_ACTION);
    public static final Operation PGDOWN
	= new Operation("PGDOWN", 63, Action.NO_ACTION);
    public static final Operation PASTE_BEFORE
	= new Operation("PASTE_BEFORE", 64, Movement.NEXT, Action.PASTE);
    public static final Operation PASTE_AFTER
	= new Operation("PASTE_AFTER", 65, Movement.PREV, Action.PASTE);
    public static final Operation PASTE_FROM_CLIPBOARD
	= new Operation("PASTE_FROM_CLIPBOARD", 66, Movement.NEXT,
			Action.PASTE_FROM_CLIPBOARD);
    public static final Operation UNDO
	= new Operation("UNDO", 67, Action.UNDO);
    public static final Operation CASE
	= new Operation("CASE", 68, Action.CASE);
    public static final Operation ABORT
	= new Operation("ABORT", 69, Action.ABORT);
    public static final Operation REPEAT
	= new Operation("REPEAT", 70, Action.NO_ACTION);
    public static final Operation EXIT
	= new Operation("EXIT", 71, Action.EXIT);
    public static final Operation VI_EDIT_MODE
	= new Operation("VI_EDIT_MODE", 72, Movement.PREV,
			Action.CHANGE_EDITMODE);
    public static final Operation EMACS_EDIT_MODE
	= new Operation("EMACS_EDIT_MODE", 73, Movement.NEXT,
			Action.CHANGE_EDITMODE);
    public static final Operation REPLACE
	= new Operation("REPLACE", 74, Movement.NEXT, Action.REPLACE);
    private Movement movement;
    private Action action;
    private int[] input;
    /*synthetic*/ private static final Operation[] $VALUES
		      = { SEARCH_PREV, SEARCH_NEXT, SEARCH_END,
			  SEARCH_PREV_WORD, SEARCH_NEXT_WORD, SEARCH_DELETE,
			  SEARCH_EXIT, SEARCH_INPUT, NEW_LINE, NO_ACTION,
			  COMPLETE, EDIT, CLEAR, HISTORY_NEXT, HISTORY_PREV,
			  PREV_CHAR, NEXT_CHAR, NEXT_WORD, PREV_WORD,
			  NEXT_BIG_WORD, PREV_BIG_WORD, MOVE_PREV_CHAR,
			  MOVE_NEXT_CHAR, MOVE_PREV_WORD, MOVE_PREV_BIG_WORD,
			  MOVE_NEXT_WORD, MOVE_NEXT_BIG_WORD, MOVE_BEGINNING,
			  MOVE_END, DELETE_PREV_CHAR, DELETE_NEXT_CHAR,
			  DELETE_PREV_WORD, DELETE_PREV_BIG_WORD,
			  DELETE_NEXT_WORD, DELETE_NEXT_BIG_WORD,
			  DELETE_BEGINNING, DELETE_END, DELETE_ALL,
			  CHANGE_PREV_CHAR, CHANGE_NEXT_CHAR, CHANGE_PREV_WORD,
			  CHANGE_PREV_BIG_WORD, CHANGE_NEXT_WORD,
			  CHANGE_NEXT_BIG_WORD, CHANGE_BEGINNING, CHANGE_END,
			  CHANGE_ALL, CHANGE, YANK_PREV_CHAR, YANK_NEXT_CHAR,
			  YANK_PREV_WORD, YANK_PREV_BIG_WORD, YANK_NEXT_WORD,
			  YANK_NEXT_BIG_WORD, YANK_BEGINNING, YANK_END,
			  YANK_ALL, BEGINNING, END, INSERT, INSERT_BEGINNING,
			  ESCAPE, PGUP, PGDOWN, PASTE_BEFORE, PASTE_AFTER,
			  PASTE_FROM_CLIPBOARD, UNDO, CASE, ABORT, REPEAT,
			  EXIT, VI_EDIT_MODE, EMACS_EDIT_MODE, REPLACE };
    
    public static Operation[] values() {
	return (Operation[]) $VALUES.clone();
    }
    
    public static Operation valueOf(String name) {
	return (Operation) Enum.valueOf(Operation.class, name);
    }
    
    private Operation(String string, int i, Action action) {
	super(string, i);
	this.action = action;
    }
    
    private Operation(String string, int i, Movement movement, Action action) {
	super(string, i);
	this.movement = movement;
	this.action = action;
    }
    
    public Movement getMovement() {
	return movement;
    }
    
    public Action getAction() {
	return action;
    }
    
    public void setInput(int[] input) {
	this.input = input;
    }
    
    public int[] getInput() {
	return input;
    }
}
