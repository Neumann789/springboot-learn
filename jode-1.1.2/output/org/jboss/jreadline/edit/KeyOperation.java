/* KeyOperation - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit;
import java.util.Arrays;

import org.jboss.jreadline.edit.actions.Action;
import org.jboss.jreadline.edit.actions.Operation;

public class KeyOperation
{
    private int[] keyValues;
    private Operation operation;
    private Action workingMode = Action.NO_ACTION;
    
    public KeyOperation(int value, Operation operation) {
	keyValues = new int[] { value };
	this.operation = operation;
    }
    
    public KeyOperation(int[] value, Operation operation) {
	keyValues = value;
	this.operation = operation;
    }
    
    public KeyOperation(int value, Operation operation, Action workingMode) {
	keyValues = new int[] { value };
	this.operation = operation;
	this.workingMode = workingMode;
    }
    
    public KeyOperation(int[] value, Operation operation, Action workingMode) {
	keyValues = value;
	this.operation = operation;
	this.workingMode = workingMode;
    }
    
    public int[] getKeyValues() {
	return keyValues;
    }
    
    public int getFirstValue() {
	return keyValues[0];
    }
    
    public boolean hasMoreThanOneKeyValue() {
	return keyValues.length > 1;
    }
    
    public Operation getOperation() {
	return operation;
    }
    
    public Action getWorkingMode() {
	return workingMode;
    }
    
    public boolean equals(Object o) {
	if (o instanceof KeyOperation) {
	    KeyOperation ko = (KeyOperation) o;
	    if (ko.getOperation() == operation
		&& ko.getKeyValues().length == keyValues.length) {
		for (int i = 0; i < keyValues.length; i++) {
		    if (ko.getKeyValues()[i] != keyValues[i])
			return false;
		}
		return true;
	    }
	}
	return false;
    }
    
    public int hashCode() {
	return 1481003;
    }
    
    public String toString() {
	return new StringBuilder().append("Operation: ").append(operation)
		   .append
		   (", ").append
		   (Arrays.toString(keyValues)).toString();
    }
    
    public boolean equalValues(int[] values) {
	return Arrays.equals(keyValues, values);
    }
}
