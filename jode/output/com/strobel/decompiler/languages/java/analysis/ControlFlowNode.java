/* ControlFlowNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.analysis;
import java.util.ArrayList;
import java.util.List;

import com.strobel.decompiler.languages.java.ast.Statement;

public class ControlFlowNode
{
    private final Statement _previousStatement;
    private final Statement _nextStatement;
    private final ControlFlowNodeType _type;
    private final List _outgoing = new ArrayList();
    private final List _incoming = new ArrayList();
    
    public ControlFlowNode(Statement previousStatement,
			   Statement nextStatement, ControlFlowNodeType type) {
	if (previousStatement != null || nextStatement != null) {
	    _previousStatement = previousStatement;
	    _nextStatement = nextStatement;
	    _type = type;
	}
	throw new IllegalArgumentException
		  ("previousStatement and nextStatement must not be both null");
    }
    
    public Statement getPreviousStatement() {
	return _previousStatement;
    }
    
    public Statement getNextStatement() {
	return _nextStatement;
    }
    
    public ControlFlowNodeType getType() {
	return _type;
    }
    
    public List getOutgoing() {
	return _outgoing;
    }
    
    public List getIncoming() {
	return _incoming;
    }
}
