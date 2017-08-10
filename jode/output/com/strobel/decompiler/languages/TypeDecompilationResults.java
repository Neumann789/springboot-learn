/* TypeDecompilationResults - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages;
import java.util.Collections;
import java.util.List;

public class TypeDecompilationResults
{
    private final List _lineNumberPositions;
    
    public TypeDecompilationResults(List lineNumberPositions) {
	_lineNumberPositions = lineNumberPositions;
    }
    
    public List getLineNumberPositions() {
	if (_lineNumberPositions != null)
	    return Collections.unmodifiableList(_lineNumberPositions);
	return Collections.emptyList();
    }
}
