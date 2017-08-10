/* NewLineNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.core.Environment;
import com.strobel.decompiler.languages.TextLocation;

public abstract class NewLineNode extends AstNode
{
    private final TextLocation _startLocation;
    private final TextLocation _endLocation;
    
    protected NewLineNode() {
	this(TextLocation.EMPTY);
    }
    
    protected NewLineNode(TextLocation startLocation) {
    label_1667:
	{
	    PUSH this;
	    if (startLocation == null)
		PUSH TextLocation.EMPTY;
	    else
		PUSH startLocation;
	    break label_1667;
	}
	((NewLineNode) POP)._startLocation = POP;
	_endLocation = new TextLocation(_startLocation.line() + 1, 1);
    }
    
    public abstract NewLineType getNewLineType();
    
    public TextLocation getStartLocation() {
	return _startLocation;
    }
    
    public TextLocation getEndLocation() {
	return _endLocation;
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitNewLine(this, data);
    }
    
    public NodeType getNodeType() {
	return NodeType.WHITESPACE;
    }
    
    public static NewLineNode create() {
	if (!Environment.isWindows() && !Environment.isOS2()) {
	    if (!Environment.isMac())
		return new UnixNewLine();
	    return new MacNewLine();
	}
	return new WindowsNewLine();
    }
}
