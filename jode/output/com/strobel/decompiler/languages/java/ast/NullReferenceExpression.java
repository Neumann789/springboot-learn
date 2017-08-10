/* NullReferenceExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.languages.TextLocation;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public final class NullReferenceExpression extends Expression
{
    private static final String NULL_TEXT = "null";
    private TextLocation _startLocation;
    private TextLocation _endLocation;
    
    public NullReferenceExpression(int offset) {
	this(offset, TextLocation.EMPTY);
    }
    
    public NullReferenceExpression(int offset, TextLocation startLocation) {
	super(offset);
	_startLocation
	    = (TextLocation) VerifyArgument.notNull(startLocation,
						    "startLocation");
	_endLocation
	    = new TextLocation(startLocation.line(),
			       startLocation.column() + "null".length());
    }
    
    public TextLocation getStartLocation() {
	return _startLocation;
    }
    
    public TextLocation getEndLocation() {
	return _endLocation;
    }
    
    public void setStartLocation(TextLocation startLocation) {
	_startLocation
	    = (TextLocation) VerifyArgument.notNull(startLocation,
						    "startLocation");
	_endLocation
	    = new TextLocation(startLocation.line(),
			       startLocation.column() + "null".length());
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitNullReferenceExpression(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	return other instanceof NullReferenceExpression;
    }
}
