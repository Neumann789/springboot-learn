/* ControlFlowEdge - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.flowanalysis;
import com.strobel.core.VerifyArgument;

public final class ControlFlowEdge
{
    private final ControlFlowNode _source;
    private final ControlFlowNode _target;
    private final JumpType _type;
    
    public ControlFlowEdge(ControlFlowNode source, ControlFlowNode target,
			   JumpType type) {
	_source = (ControlFlowNode) VerifyArgument.notNull(source, "source");
	_target = (ControlFlowNode) VerifyArgument.notNull(target, "target");
	_type = (JumpType) VerifyArgument.notNull(type, "type");
    }
    
    public final ControlFlowNode getSource() {
	return _source;
    }
    
    public final ControlFlowNode getTarget() {
	return _target;
    }
    
    public final JumpType getType() {
	return _type;
    }
    
    public boolean equals(Object obj) {
	if (!(obj instanceof ControlFlowEdge))
	    return false;
    label_1140:
	{
	    ControlFlowEdge other = (ControlFlowEdge) obj;
	    if (other._source != _source || other._target != _target)
		PUSH false;
	    else
		PUSH true;
	    break label_1140;
	}
	return POP;
    }
    
    public final String toString() {
	switch (ANONYMOUS CLASS com.strobel.assembler.flowanalysis.ControlFlowEdge$1.$SwitchMap$com$strobel$assembler$flowanalysis$JumpType[_type.ordinal()]) {
	case 1:
	    return "#" + _target.getBlockIndex();
	case 2:
	    return "e:#" + _target.getBlockIndex();
	default:
	    return _type + ":#" + _target.getBlockIndex();
	}
    }
}
