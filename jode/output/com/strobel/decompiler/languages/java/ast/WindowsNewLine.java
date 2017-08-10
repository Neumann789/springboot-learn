/* WindowsNewLine - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.languages.TextLocation;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public final class WindowsNewLine extends NewLineNode
{
    public NewLineType getNewLineType() {
	return NewLineType.WINDOWS;
    }
    
    public WindowsNewLine() {
	/* empty */
    }
    
    public WindowsNewLine(TextLocation startLocation) {
	super(startLocation);
    }
    
    public boolean matches(INode other, Match match) {
	return other instanceof UnixNewLine;
    }
}
