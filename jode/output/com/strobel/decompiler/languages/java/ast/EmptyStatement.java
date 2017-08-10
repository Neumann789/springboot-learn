/* EmptyStatement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.languages.TextLocation;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public final class EmptyStatement extends Statement
{
    private TextLocation _location;
    
    public EmptyStatement() {
	super(-34);
    }
    
    public TextLocation getLocation() {
	return _location;
    }
    
    public void setLocation(TextLocation location) {
	verifyNotFrozen();
	_location = location;
    }
    
    public TextLocation getStartLocation() {
	return getLocation();
    }
    
    public TextLocation getEndLocation() {
	TextLocation location = getLocation();
	return new TextLocation(location.line(), location.column() + 1);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitEmptyStatement(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	return other instanceof EmptyStatement;
    }
}
