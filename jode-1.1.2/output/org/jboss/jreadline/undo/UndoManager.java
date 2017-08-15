/* UndoManager - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.undo;
import java.util.Stack;

public class UndoManager
{
    private static short UNDO_SIZE = 50;
    private Stack undoStack = new Stack();
    private int counter;
    
    public UndoManager() {
	undoStack.setSize(UNDO_SIZE);
	counter = 0;
    }
    
    public UndoAction getNext() {
	if (counter > 0) {
	    counter--;
	    return (UndoAction) undoStack.pop();
	}
	return null;
    }
    
    public void addUndo(UndoAction u) {
	if (counter <= UNDO_SIZE) {
	    counter++;
	    undoStack.push(u);
	} else {
	    undoStack.remove(UNDO_SIZE);
	    undoStack.push(u);
	}
    }
    
    public void clear() {
	undoStack.clear();
	counter = 0;
    }
    
    public boolean isEmpty() {
	return counter == 0;
    }
    
    public int size() {
	return counter;
    }
}
