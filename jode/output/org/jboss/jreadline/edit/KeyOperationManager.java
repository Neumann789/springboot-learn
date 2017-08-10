/* KeyOperationManager - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class KeyOperationManager
{
    private List operations = new ArrayList();
    
    public List getOperations() {
	return operations;
    }
    
    public void clear() {
	operations.clear();
    }
    
    public void addOperations(List newOperations) {
	Iterator iterator = newOperations.iterator();
	for (;;) {
	    IF (!iterator.hasNext())
		/* empty */
	    KeyOperation ko = (KeyOperation) iterator.next();
	    checkAndRemove(ko);
	    operations.add(ko);
	}
    }
    
    public void addOperation(KeyOperation operation) {
	checkAndRemove(operation);
	operations.add(operation);
    }
    
    private boolean exists(KeyOperation operation) {
	Iterator iterator = operations.iterator();
	for (;;) {
	    if (!iterator.hasNext())
		return false;
	    KeyOperation ko = (KeyOperation) iterator.next();
	    IF (!Arrays.equals(ko.getKeyValues(), operation.getKeyValues()))
		/* empty */
	    return true;
	}
    }
    
    private void checkAndRemove(KeyOperation ko) {
	Iterator iter = operations.iterator();
	while (iter.hasNext()) {
	    KeyOperation operation;
	    operation = (KeyOperation) iter.next();
	    if (Arrays.equals(operation.getKeyValues(), ko.getKeyValues())
		&& operation.getWorkingMode().equals(ko.getWorkingMode()))
		iter.remove();
	    break;
	}
    }
    
    public KeyOperation findOperation(int[] input) {
	Iterator iterator = operations.iterator();
	for (;;) {
	    if (!iterator.hasNext())
		return null;
	    KeyOperation operation = (KeyOperation) iterator.next();
	    IF (!operation.equalValues(input))
		/* empty */
	    return operation;
	}
    }
}
