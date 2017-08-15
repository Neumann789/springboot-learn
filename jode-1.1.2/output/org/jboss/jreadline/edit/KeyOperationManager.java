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
	while (iterator.hasNext()) {
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
	while (iterator.hasNext()) {
	    KeyOperation ko = (KeyOperation) iterator.next();
	    if (Arrays.equals(ko.getKeyValues(), operation.getKeyValues()))
		return true;
	}
	return false;
    }
    
    private void checkAndRemove(KeyOperation ko) {
	Iterator iter = operations.iterator();
	while (iter.hasNext()) {
	    KeyOperation operation = (KeyOperation) iter.next();
	    if (Arrays.equals(operation.getKeyValues(), ko.getKeyValues())
		&& operation.getWorkingMode().equals(ko.getWorkingMode())) {
		iter.remove();
		break;
	    }
	}
    }
    
    public KeyOperation findOperation(int[] input) {
	Iterator iterator = operations.iterator();
	while (iterator.hasNext()) {
	    KeyOperation operation = (KeyOperation) iterator.next();
	    if (operation.equalValues(input))
		return operation;
	}
	return null;
    }
}
