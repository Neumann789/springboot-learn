/* LineNumberPosition - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages;
import java.util.Iterator;
import java.util.List;

public class LineNumberPosition
{
    private final int _originalLine;
    private final int _emittedLine;
    private final int _emittedColumn;
    
    public LineNumberPosition(int originalLine, int emittedLine,
			      int emittedColumn) {
	_originalLine = originalLine;
	_emittedLine = emittedLine;
	_emittedColumn = emittedColumn;
    }
    
    public int getOriginalLine() {
	return _originalLine;
    }
    
    public int getEmittedLine() {
	return _emittedLine;
    }
    
    public int getEmittedColumn() {
	return _emittedColumn;
    }
    
    public static int computeMaxLineNumber(List lineNumPositions) {
	int maxLineNo = 1;
	Iterator i$ = lineNumPositions.iterator();
	for (;;) {
	    if (!i$.hasNext())
		return maxLineNo;
	    LineNumberPosition pos = (LineNumberPosition) i$.next();
	    int originalLine = pos.getOriginalLine();
	    maxLineNo = Math.max(maxLineNo, originalLine);
	}
    }
    
    public String toString() {
	return ("Line # Position : {orig=" + _originalLine + ", " + "emitted="
		+ _emittedLine + "/" + _emittedColumn + "}");
    }
}
