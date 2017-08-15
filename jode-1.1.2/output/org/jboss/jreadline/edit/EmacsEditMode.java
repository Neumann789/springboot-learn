/* EmacsEditMode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.jreadline.console.Config;
import org.jboss.jreadline.edit.actions.Action;
import org.jboss.jreadline.edit.actions.Operation;

public class EmacsEditMode implements EditMode
{
    private Action mode = Action.EDIT;
    private KeyOperationManager operationManager;
    private List currentOperations = new ArrayList();
    private int operationLevel = 0;
    
    public EmacsEditMode(KeyOperationManager operations) {
	operationManager = operations;
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
		if (input == ko.getFirstValue())
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
		    == Operation.SEARCH_NEXT_WORD) {
		    currentOperations.clear();
		    return Operation.SEARCH_NEXT_WORD;
		}
		if (((KeyOperation) currentOperations.get(0)).getOperation()
		    == Operation.DELETE_PREV_CHAR) {
		    currentOperations.clear();
		    return Operation.SEARCH_DELETE;
		}
		currentOperations.clear();
		return Operation.NO_ACTION;
	    }
	    if (currentOperations.size() > 1) {
		mode = Action.EDIT;
		currentOperations.clear();
		return Operation.SEARCH_EXIT;
	    }
	    currentOperations.clear();
	    return Operation.SEARCH_INPUT;
	}
	if (currentOperations.isEmpty()) {
	    if (operationLevel > 0) {
		operationLevel = 0;
		currentOperations.clear();
		return Operation.NO_ACTION;
	    }
	    return Operation.EDIT;
	}
	if (currentOperations.size() == 1) {
	    int level = operationLevel + 1;
	    if (in.length > level)
		level = in.length;
	    if (((KeyOperation) currentOperations.get(0)).getKeyValues().length
		> level) {
		operationLevel++;
		return Operation.NO_ACTION;
	    }
	    Operation currentOperation
		= ((KeyOperation) currentOperations.get(0)).getOperation();
	    if (currentOperation == Operation.SEARCH_PREV
		|| currentOperation == Operation.SEARCH_NEXT_WORD)
		mode = Action.SEARCH;
	    operationLevel = 0;
	    currentOperations.clear();
	    return currentOperation;
	}
	operationLevel++;
	return Operation.NO_ACTION;
    }
    
    public Action getCurrentAction() {
	return mode;
    }
    
    public Mode getMode() {
	return Mode.EMACS;
    }
}
