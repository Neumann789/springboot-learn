/* KeyOperationFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.jreadline.edit.actions.Action;
import org.jboss.jreadline.edit.actions.Operation;

public class KeyOperationFactory
{
    public static List generatePOSIXEmacsMode() {
	List keys = generateGenericEmacsKeys();
	keys.add(new KeyOperation(10, Operation.NEW_LINE));
	keys.add(new KeyOperation(31, Operation.UNDO));
	keys.add(new KeyOperation(127, Operation.DELETE_PREV_CHAR));
	keys.add(new KeyOperation(new int[] { 27, 91, 65 },
				  Operation.HISTORY_PREV));
	keys.add(new KeyOperation(new int[] { 27, 91, 66 },
				  Operation.HISTORY_NEXT));
	keys.add(new KeyOperation(new int[] { 27, 91, 67 },
				  Operation.MOVE_NEXT_CHAR));
	keys.add(new KeyOperation(new int[] { 27, 91, 68 },
				  Operation.MOVE_PREV_CHAR));
	keys.add(new KeyOperation(new int[] { 27, 102 },
				  Operation.MOVE_NEXT_WORD));
	keys.add(new KeyOperation(new int[] { 27, 98 },
				  Operation.MOVE_PREV_WORD));
	keys.add(new KeyOperation(new int[] { 27, 100 },
				  Operation.DELETE_NEXT_WORD));
	keys.add(new KeyOperation(new int[] { 27, 91, 51, 126 },
				  Operation.DELETE_NEXT_CHAR));
	keys.add(new KeyOperation(new int[] { 27, 91, 53, 126 },
				  Operation.PGUP));
	keys.add(new KeyOperation(new int[] { 27, 91, 54, 126 },
				  Operation.PGDOWN));
	keys.add(new KeyOperation(new int[] { 27, 91, 72 },
				  Operation.MOVE_BEGINNING));
	keys.add(new KeyOperation(new int[] { 27, 79, 72 },
				  Operation.MOVE_BEGINNING));
	keys.add(new KeyOperation(new int[] { 27, 91, 70 },
				  Operation.MOVE_END));
	keys.add(new KeyOperation(new int[] { 27, 79, 70 },
				  Operation.MOVE_END));
	keys.add(new KeyOperation(new int[] { 27, 10 },
				  Operation.VI_EDIT_MODE));
	return keys;
    }
    
    public static List generateWindowsEmacsMode() {
	List keys = generateGenericEmacsKeys();
	keys.add(new KeyOperation(3, Operation.EXIT));
	keys.add(new KeyOperation(8, Operation.DELETE_PREV_CHAR));
	keys.add(new KeyOperation(13, Operation.NEW_LINE));
	keys.add(new KeyOperation(new int[] { 224, 72 },
				  Operation.HISTORY_PREV));
	keys.add(new KeyOperation(new int[] { 224, 80 },
				  Operation.HISTORY_NEXT));
	keys.add(new KeyOperation(new int[] { 224, 77 },
				  Operation.MOVE_NEXT_CHAR));
	keys.add(new KeyOperation(new int[] { 224, 75 },
				  Operation.MOVE_PREV_CHAR));
	keys.add(new KeyOperation(new int[] { 0, 33 },
				  Operation.MOVE_NEXT_WORD));
	keys.add(new KeyOperation(new int[] { 0, 48 },
				  Operation.MOVE_PREV_WORD));
	keys.add(new KeyOperation(new int[] { 0, 32 },
				  Operation.DELETE_NEXT_WORD));
	keys.add(new KeyOperation(new int[] { 224, 83 },
				  Operation.DELETE_NEXT_CHAR));
	keys.add(new KeyOperation(new int[] { 224, 73 }, Operation.PGUP));
	keys.add(new KeyOperation(new int[] { 224, 81 }, Operation.PGDOWN));
	keys.add(new KeyOperation(new int[] { 224, 71 },
				  Operation.MOVE_BEGINNING));
	keys.add(new KeyOperation(new int[] { 224, 79 }, Operation.MOVE_END));
	keys.add(new KeyOperation(new int[] { 224, 83 },
				  Operation.DELETE_NEXT_CHAR));
	keys.add(new KeyOperation(new int[] { 0, 36 },
				  Operation.VI_EDIT_MODE));
	return keys;
    }
    
    private static List generateGenericEmacsKeys() {
	List keys = new ArrayList();
	keys.add(new KeyOperation(1, Operation.MOVE_BEGINNING));
	keys.add(new KeyOperation(2, Operation.MOVE_PREV_CHAR));
	keys.add(new KeyOperation(4, Operation.DELETE_NEXT_CHAR));
	keys.add(new KeyOperation(5, Operation.MOVE_END));
	keys.add(new KeyOperation(6, Operation.MOVE_NEXT_CHAR));
	keys.add(new KeyOperation(7, Operation.ABORT));
	keys.add(new KeyOperation(8, Operation.DELETE_PREV_CHAR));
	keys.add(new KeyOperation(9, Operation.COMPLETE));
	keys.add(new KeyOperation(11, Operation.DELETE_END));
	keys.add(new KeyOperation(12, Operation.CLEAR));
	keys.add(new KeyOperation(14, Operation.HISTORY_NEXT));
	keys.add(new KeyOperation(16, Operation.HISTORY_PREV));
	keys.add(new KeyOperation(18, Operation.SEARCH_PREV));
	keys.add(new KeyOperation(19, Operation.SEARCH_NEXT_WORD));
	keys.add(new KeyOperation(21, Operation.DELETE_BEGINNING));
	keys.add(new KeyOperation(22, Operation.PASTE_FROM_CLIPBOARD));
	keys.add(new KeyOperation(23, Operation.DELETE_PREV_BIG_WORD));
	keys.add(new KeyOperation(25, Operation.PASTE_BEFORE));
	keys.add(new KeyOperation(new int[] { 24, 21 }, Operation.UNDO));
	return keys;
    }
    
    public static List generatePOSIXViMode() {
	List keys = generateGenericViMode();
	keys.add(new KeyOperation(10, Operation.NEW_LINE));
	keys.add(new KeyOperation(new int[] { 27, 91, 65 },
				  Operation.HISTORY_PREV, Action.EDIT));
	keys.add(new KeyOperation(new int[] { 27, 91, 66 },
				  Operation.HISTORY_NEXT, Action.EDIT));
	keys.add(new KeyOperation(new int[] { 27, 91, 67 },
				  Operation.MOVE_NEXT_CHAR, Action.EDIT));
	keys.add(new KeyOperation(new int[] { 27, 91, 68 },
				  Operation.MOVE_PREV_CHAR, Action.EDIT));
	keys.add(new KeyOperation(new int[] { 27, 91, 51, 126 },
				  Operation.DELETE_NEXT_CHAR, Action.COMMAND));
	keys.add(new KeyOperation(new int[] { 27, 91, 53, 126 },
				  Operation.PGUP));
	keys.add(new KeyOperation(new int[] { 27, 91, 54, 126 },
				  Operation.PGDOWN));
	return keys;
    }
    
    public static List generateWindowsViMode() {
	List keys = generateGenericViMode();
	keys.add(new KeyOperation(13, Operation.NEW_LINE));
	keys.add(new KeyOperation(new int[] { 224, 83 },
				  Operation.DELETE_NEXT_CHAR, Action.COMMAND));
	keys.add(new KeyOperation(new int[] { 224, 73 }, Operation.PGUP));
	keys.add(new KeyOperation(new int[] { 224, 81 }, Operation.PGDOWN));
	return keys;
    }
    
    private static List generateGenericViMode() {
	List keys = new ArrayList();
	keys.add(new KeyOperation(5, Operation.EMACS_EDIT_MODE));
	keys.add(new KeyOperation(9, Operation.COMPLETE));
	keys.add(new KeyOperation(12, Operation.CLEAR));
	keys.add(new KeyOperation(18, Operation.SEARCH_PREV));
	keys.add(new KeyOperation(27, Operation.ESCAPE));
	keys.add(new KeyOperation(115, Operation.CHANGE_NEXT_CHAR));
	keys.add(new KeyOperation(83, Operation.CHANGE_ALL));
	keys.add(new KeyOperation(100, Operation.DELETE_ALL));
	keys.add(new KeyOperation(68, Operation.DELETE_END));
	keys.add(new KeyOperation(99, Operation.CHANGE));
	keys.add(new KeyOperation(67, Operation.CHANGE_END));
	keys.add(new KeyOperation(97, Operation.MOVE_NEXT_CHAR));
	keys.add(new KeyOperation(65, Operation.MOVE_END));
	keys.add(new KeyOperation(48, Operation.BEGINNING));
	keys.add(new KeyOperation(36, Operation.END));
	keys.add(new KeyOperation(120, Operation.DELETE_NEXT_CHAR));
	keys.add(new KeyOperation(88, Operation.DELETE_PREV_CHAR,
				  Action.COMMAND));
	keys.add(new KeyOperation(112, Operation.PASTE_AFTER));
	keys.add(new KeyOperation(80, Operation.PASTE_BEFORE));
	keys.add(new KeyOperation(105, Operation.INSERT));
	keys.add(new KeyOperation(73, Operation.INSERT_BEGINNING));
	keys.add(new KeyOperation(126, Operation.CASE));
	keys.add(new KeyOperation(121, Operation.YANK_ALL));
	keys.add(new KeyOperation(114, Operation.REPLACE));
	keys.add(new KeyOperation(104, Operation.PREV_CHAR));
	keys.add(new KeyOperation(108, Operation.NEXT_CHAR));
	keys.add(new KeyOperation(106, Operation.HISTORY_NEXT));
	keys.add(new KeyOperation(107, Operation.HISTORY_PREV));
	keys.add(new KeyOperation(98, Operation.PREV_WORD));
	keys.add(new KeyOperation(66, Operation.PREV_BIG_WORD));
	keys.add(new KeyOperation(119, Operation.NEXT_WORD));
	keys.add(new KeyOperation(87, Operation.NEXT_BIG_WORD));
	keys.add(new KeyOperation(32, Operation.NEXT_CHAR));
	keys.add(new KeyOperation(46, Operation.REPEAT));
	keys.add(new KeyOperation(117, Operation.UNDO));
	keys.add(new KeyOperation(127, Operation.DELETE_PREV_CHAR));
	return keys;
    }
    
    public static KeyOperation findOperation(List operations, int[] input) {
	Iterator iterator = operations.iterator();
	while (iterator.hasNext()) {
	    KeyOperation operation = (KeyOperation) iterator.next();
	    if (operation.equalValues(input))
		return operation;
	}
	return null;
    }
}
