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
    label_240:
	{
	    int input = in[0];
	    if (!Config.isOSPOSIXCompatible() || in.length <= 1) {
		if (operationLevel <= 0) {
		    Iterator iterator
			= operationManager.getOperations().iterator();
		    while (iterator.hasNext()) {
			KeyOperation ko = (KeyOperation) iterator.next();
			if (input == ko.getFirstValue())
			    currentOperations.add(ko);
			continue;
		    }
		} else {
		    Iterator operationIterator = currentOperations.iterator();
		    while (operationIterator.hasNext()) {
			if (input != ((KeyOperation) operationIterator.next())
					 .getKeyValues()[operationLevel])
			    operationIterator.remove();
			continue;
		    }
		}
	    } else {
		KeyOperation ko
		    = KeyOperationFactory
			  .findOperation(operationManager.getOperations(), in);
		if (ko != null) {
		    currentOperations.clear();
		    currentOperations.add(ko);
		}
	    }
	    break label_240;
	}
	int level;
    label_241:
	{
	    if (mode != Action.SEARCH) {
		if (!currentOperations.isEmpty()) {
		    if (currentOperations.size() != 1) {
			operationLevel++;
			return Operation.NO_ACTION;
		    }
		    level = operationLevel + 1;
		    if (in.length > level)
			level = in.length;
		} else {
		    if (operationLevel <= 0)
			return Operation.EDIT;
		    operationLevel = 0;
		    currentOperations.clear();
		    return Operation.NO_ACTION;
		}
	    } else {
		if (currentOperations.size() != 1) {
		    if (currentOperations.size() <= 1) {
			currentOperations.clear();
			return Operation.SEARCH_INPUT;
		    }
		    mode = Action.EDIT;
		    currentOperations.clear();
		    return Operation.SEARCH_EXIT;
		}
		if (((KeyOperation) currentOperations.get(0)).getOperation()
		    != Operation.NEW_LINE) {
		    if (((KeyOperation) currentOperations.get(0))
			    .getOperation()
			!= Operation.SEARCH_PREV) {
			if (((KeyOperation) currentOperations.get(0))
				.getOperation()
			    != Operation.SEARCH_NEXT_WORD) {
			    if (((KeyOperation) currentOperations.get(0))
				    .getOperation()
				!= Operation.DELETE_PREV_CHAR) {
				currentOperations.clear();
				return Operation.NO_ACTION;
			    }
			    currentOperations.clear();
			    return Operation.SEARCH_DELETE;
			}
			currentOperations.clear();
			return Operation.SEARCH_NEXT_WORD;
		    }
		    currentOperations.clear();
		    return Operation.SEARCH_PREV_WORD;
		}
		mode = Action.EDIT;
		currentOperations.clear();
		return Operation.SEARCH_END;
	    }
	}
	Operation currentOperation;
    label_242:
	{
	    if (((KeyOperation) currentOperations.get(0)).getKeyValues().length
		<= level) {
		currentOperation
		    = ((KeyOperation) currentOperations.get(0)).getOperation();
		if (currentOperation == Operation.SEARCH_PREV
		    || currentOperation == Operation.SEARCH_NEXT_WORD)
		    mode = Action.SEARCH;
	    } else {
		operationLevel++;
		return Operation.NO_ACTION;
	    }
	}
	operationLevel = 0;
	currentOperations.clear();
	return currentOperation;
	break label_242;
	break label_241;
    }
    
    public Action getCurrentAction() {
	return mode;
    }
    
    public Mode getMode() {
	return Mode.EMACS;
    }
}
