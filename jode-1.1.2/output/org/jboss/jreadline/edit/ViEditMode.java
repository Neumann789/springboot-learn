/* ViEditMode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.jreadline.console.Config;
import org.jboss.jreadline.edit.actions.Action;
import org.jboss.jreadline.edit.actions.Operation;

public class ViEditMode implements EditMode
{
    private Action mode;
    private Action previousMode;
    private Operation previousAction;
    private KeyOperationManager operationManager;
    private List currentOperations = new ArrayList();
    private int operationLevel = 0;
    
    public ViEditMode(KeyOperationManager operations) {
	mode = Action.EDIT;
	previousMode = Action.EDIT;
	operationManager = operations;
    }
    
    public boolean isInEditMode() {
	return mode == Action.EDIT;
    }
    
    private void switchEditMode() {
	if (mode == Action.EDIT)
	    mode = Action.MOVE;
	else
	    mode = Action.EDIT;
    }
    
    private boolean isDeleteMode() {
	return mode == Action.DELETE;
    }
    
    private boolean isChangeMode() {
	return mode == Action.CHANGE;
    }
    
    private boolean isInReplaceMode() {
	return mode == Action.REPLACE;
    }
    
    private boolean isYankMode() {
	return mode == Action.YANK;
    }
    
    private Operation saveAction(Operation action) {
	previousMode = mode;
	if (action.getAction() != Action.MOVE)
	    previousAction = action;
	if (isDeleteMode() || isYankMode())
	    mode = Action.MOVE;
	if (isChangeMode())
	    mode = Action.EDIT;
	return action;
    }
    
    public Operation parseInput(int[] in) {
	int input = in[0];
	if (Config.isOSPOSIXCompatible() && in.length > 1) {
	    KeyOperation ko
		= KeyOperationFactory
		      .findOperation(operationManager.getOperations(), in);
	    if (ko != null) {
		currentOperations.clear();
		currentOperations.add(ko);
	    }
	} else if (operationLevel > 0) {
	    Iterator operationIterator = currentOperations.iterator();
	    while (operationIterator.hasNext()) {
		if (input != ((KeyOperation) operationIterator.next())
				 .getKeyValues()[operationLevel])
		    operationIterator.remove();
	    }
	} else {
	    Iterator iterator = operationManager.getOperations().iterator();
	    while (iterator.hasNext()) {
		KeyOperation ko = (KeyOperation) iterator.next();
		if (input == ko.getFirstValue()
		    && ko.getKeyValues().length == in.length)
		    currentOperations.add(ko);
	    }
	}
	if (mode == Action.SEARCH) {
	    if (currentOperations.size() == 1) {
		if (((KeyOperation) currentOperations.get(0)).getOperation()
		    == Operation.NEW_LINE) {
		    mode = Action.EDIT;
		    currentOperations.clear();
		    return Operation.SEARCH_END;
		}
		if (((KeyOperation) currentOperations.get(0)).getOperation()
		    == Operation.SEARCH_PREV) {
		    currentOperations.clear();
		    return Operation.SEARCH_PREV_WORD;
		}
		if (((KeyOperation) currentOperations.get(0)).getOperation()
		    == Operation.DELETE_PREV_CHAR) {
		    currentOperations.clear();
		    return Operation.SEARCH_DELETE;
		}
		if (((KeyOperation) currentOperations.get(0)).getOperation()
		    == Operation.ESCAPE) {
		    mode = Action.EDIT;
		    currentOperations.clear();
		    return Operation.SEARCH_EXIT;
		}
		currentOperations.clear();
		return Operation.SEARCH_INPUT;
	    }
	    if (currentOperations.size() > 1) {
		mode = Action.EDIT;
		currentOperations.clear();
		return Operation.SEARCH_EXIT;
	    }
	    currentOperations.clear();
	    return Operation.SEARCH_INPUT;
	}
	if (isInReplaceMode()) {
	    if (currentOperations.size() == 1
		&& (((KeyOperation) currentOperations.get(0)).getOperation()
		    == Operation.ESCAPE)) {
		operationLevel = 0;
		currentOperations.clear();
		mode = Action.MOVE;
		return Operation.NO_ACTION;
	    }
	    operationLevel = 0;
	    currentOperations.clear();
	    mode = Action.MOVE;
	    return saveAction(Operation.REPLACE);
	}
	if (currentOperations.isEmpty()) {
	    if (isInEditMode())
		return Operation.EDIT;
	    return Operation.NO_ACTION;
	}
	if (currentOperations.size() == 1) {
	    Operation operation
		= ((KeyOperation) currentOperations.get(0)).getOperation();
	    Action workingMode
		= ((KeyOperation) currentOperations.get(0)).getWorkingMode();
	    operationLevel = 0;
	    currentOperations.clear();
	    if (operation == Operation.NEW_LINE) {
		mode = Action.EDIT;
		return Operation.NEW_LINE;
	    }
	    if (operation == Operation.REPLACE && !isInEditMode()) {
		mode = Action.REPLACE;
		return Operation.NO_ACTION;
	    }
	    if (operation == Operation.DELETE_PREV_CHAR
		&& workingMode == Action.NO_ACTION) {
		if (isInEditMode())
		    return Operation.DELETE_PREV_CHAR;
		return Operation.MOVE_PREV_CHAR;
	    }
	    if (operation == Operation.DELETE_NEXT_CHAR
		&& workingMode == Action.COMMAND) {
		if (isInEditMode())
		    return Operation.NO_ACTION;
		return saveAction(Operation.DELETE_NEXT_CHAR);
	    }
	    if (operation == Operation.COMPLETE) {
		if (isInEditMode())
		    return Operation.COMPLETE;
		return Operation.NO_ACTION;
	    }
	    if (operation == Operation.ESCAPE) {
		switchEditMode();
		if (isInEditMode())
		    return Operation.NO_ACTION;
		return Operation.MOVE_PREV_CHAR;
	    }
	    if (operation == Operation.SEARCH_PREV) {
		mode = Action.SEARCH;
		return Operation.SEARCH_PREV;
	    }
	    if (operation == Operation.CLEAR)
		return Operation.CLEAR;
	    if (operation == Operation.MOVE_PREV_CHAR
		&& workingMode.equals(Action.EDIT))
		return Operation.MOVE_PREV_CHAR;
	    if (operation == Operation.MOVE_NEXT_CHAR
		&& workingMode.equals(Action.EDIT))
		return Operation.MOVE_NEXT_CHAR;
	    if (operation == Operation.HISTORY_PREV
		&& workingMode.equals(Action.EDIT))
		return operation;
	    if (operation == Operation.HISTORY_NEXT
		&& workingMode.equals(Action.EDIT))
		return operation;
	    if (!isInEditMode())
		return inCommandMode(operation, workingMode);
	    return Operation.EDIT;
	}
	operationLevel++;
	return Operation.NO_ACTION;
    }
    
    private Operation inCommandMode(Operation operation, Action workingMode) {
	if (operation == Operation.PREV_CHAR) {
	    if (mode == Action.MOVE)
		return saveAction(Operation.MOVE_PREV_CHAR);
	    if (mode == Action.DELETE)
		return saveAction(Operation.DELETE_PREV_CHAR);
	    if (mode == Action.CHANGE)
		return saveAction(Operation.CHANGE_PREV_CHAR);
	    return saveAction(Operation.YANK_PREV_CHAR);
	}
	if (operation == Operation.NEXT_CHAR) {
	    if (mode == Action.MOVE)
		return saveAction(Operation.MOVE_NEXT_CHAR);
	    if (mode == Action.DELETE)
		return saveAction(Operation.DELETE_NEXT_CHAR);
	    if (mode == Action.CHANGE)
		return saveAction(Operation.CHANGE_NEXT_CHAR);
	    return saveAction(Operation.YANK_NEXT_CHAR);
	}
	if (operation == Operation.HISTORY_NEXT)
	    return saveAction(Operation.HISTORY_NEXT);
	if (operation == Operation.HISTORY_PREV)
	    return saveAction(Operation.HISTORY_PREV);
	if (operation == Operation.PREV_WORD) {
	    if (mode == Action.MOVE)
		return saveAction(Operation.MOVE_PREV_WORD);
	    if (mode == Action.DELETE)
		return saveAction(Operation.DELETE_PREV_WORD);
	    if (mode == Action.CHANGE)
		return saveAction(Operation.CHANGE_PREV_WORD);
	    return saveAction(Operation.YANK_PREV_WORD);
	}
	if (operation == Operation.PREV_BIG_WORD) {
	    if (mode == Action.MOVE)
		return saveAction(Operation.MOVE_PREV_BIG_WORD);
	    if (mode == Action.DELETE)
		return saveAction(Operation.DELETE_PREV_BIG_WORD);
	    if (mode == Action.CHANGE)
		return saveAction(Operation.CHANGE_PREV_BIG_WORD);
	    return saveAction(Operation.YANK_PREV_BIG_WORD);
	}
	if (operation == Operation.NEXT_WORD) {
	    if (mode == Action.MOVE)
		return saveAction(Operation.MOVE_NEXT_WORD);
	    if (mode == Action.DELETE)
		return saveAction(Operation.DELETE_NEXT_WORD);
	    if (mode == Action.CHANGE)
		return saveAction(Operation.CHANGE_NEXT_WORD);
	    return saveAction(Operation.YANK_NEXT_WORD);
	}
	if (operation == Operation.NEXT_BIG_WORD) {
	    if (mode == Action.MOVE)
		return saveAction(Operation.MOVE_NEXT_BIG_WORD);
	    if (mode == Action.DELETE)
		return saveAction(Operation.DELETE_NEXT_BIG_WORD);
	    if (mode == Action.CHANGE)
		return saveAction(Operation.CHANGE_NEXT_BIG_WORD);
	    return saveAction(Operation.YANK_NEXT_BIG_WORD);
	}
	if (operation == Operation.BEGINNING) {
	    if (mode == Action.MOVE)
		return saveAction(Operation.MOVE_BEGINNING);
	    if (mode == Action.DELETE)
		return saveAction(Operation.DELETE_BEGINNING);
	    if (mode == Action.CHANGE)
		return saveAction(Operation.CHANGE_BEGINNING);
	    return saveAction(Operation.YANK_BEGINNING);
	}
	if (operation == Operation.END) {
	    if (mode == Action.MOVE)
		return saveAction(Operation.MOVE_END);
	    if (mode == Action.DELETE)
		return saveAction(Operation.DELETE_END);
	    if (mode == Action.CHANGE)
		return saveAction(Operation.CHANGE_END);
	    return saveAction(Operation.YANK_END);
	}
	if (operation == Operation.DELETE_NEXT_CHAR)
	    return saveAction(operation);
	if (operation == Operation.DELETE_PREV_CHAR
	    && workingMode == Action.COMMAND)
	    return saveAction(operation);
	if (operation == Operation.PASTE_AFTER)
	    return saveAction(operation);
	if (operation == Operation.PASTE_BEFORE)
	    return saveAction(operation);
	if (operation == Operation.CHANGE_NEXT_CHAR) {
	    switchEditMode();
	    return saveAction(operation);
	}
	if (operation == Operation.CHANGE_ALL) {
	    mode = Action.CHANGE;
	    return saveAction(operation);
	}
	if (operation == Operation.MOVE_NEXT_CHAR) {
	    switchEditMode();
	    return saveAction(operation);
	}
	if (operation == Operation.MOVE_END) {
	    switchEditMode();
	    return saveAction(operation);
	}
	if (operation == Operation.INSERT) {
	    switchEditMode();
	    return saveAction(Operation.NO_ACTION);
	}
	if (operation == Operation.INSERT_BEGINNING) {
	    switchEditMode();
	    return saveAction(Operation.MOVE_BEGINNING);
	}
	if (operation == Operation.DELETE_ALL) {
	    if (isDeleteMode())
		return saveAction(operation);
	    mode = Action.DELETE;
	} else {
	    if (operation == Operation.DELETE_END) {
		mode = Action.DELETE;
		return saveAction(operation);
	    }
	    if (operation == Operation.CHANGE) {
		if (isChangeMode())
		    return saveAction(Operation.CHANGE_ALL);
		mode = Action.CHANGE;
	    } else {
		if (operation == Operation.CHANGE_END) {
		    mode = Action.CHANGE;
		    return saveAction(operation);
		}
		if (operation == Operation.REPEAT) {
		    mode = previousMode;
		    return previousAction;
		}
		if (operation == Operation.UNDO)
		    return saveAction(operation);
		if (operation == Operation.CASE)
		    return saveAction(operation);
		if (operation == Operation.YANK_ALL) {
		    if (isYankMode())
			return saveAction(operation);
		    mode = Action.YANK;
		} else if (operation == Operation.VI_EDIT_MODE
			   || operation == Operation.EMACS_EDIT_MODE)
		    return operation;
	    }
	}
	return Operation.NO_ACTION;
    }
    
    public Action getCurrentAction() {
	return mode;
    }
    
    public Mode getMode() {
	return Mode.VI;
    }
}
