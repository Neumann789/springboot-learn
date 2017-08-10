/* CatchBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.ast;
import java.util.List;

import com.strobel.assembler.Collection;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.decompiler.ITextOutput;

public final class CatchBlock extends Block
{
    private final Collection _caughtTypes = new Collection();
    private TypeReference _exceptionType;
    private Variable _exceptionVariable;
    
    public final List getCaughtTypes() {
	return _caughtTypes;
    }
    
    public final TypeReference getExceptionType() {
	return _exceptionType;
    }
    
    public final void setExceptionType(TypeReference exceptionType) {
	_exceptionType = exceptionType;
    }
    
    public final Variable getExceptionVariable() {
	return _exceptionVariable;
    }
    
    public final void setExceptionVariable(Variable exceptionVariable) {
	_exceptionVariable = exceptionVariable;
    }
    
    public final void writeTo(ITextOutput output) {
	output.writeKeyword("catch");
    label_1497:
	{
	label_1496:
	    {
		if (_caughtTypes.isEmpty()) {
		    if (_exceptionType == null)
			break label_1497;
		    output.write(" (");
		    output.writeReference(_exceptionType.getFullName(),
					  _exceptionType);
		    if (_exceptionVariable != null)
			output.write(" %s", new Object[] { _exceptionVariable
							       .getName() });
		} else {
		    output.write(" (");
		    int i = 0;
		label_1495:
		    {
			for (;;) {
			    if (i >= _caughtTypes.size()) {
				if (_exceptionVariable != null)
				    output.write(" %s", (new Object[]
							 { _exceptionVariable
							       .getName() }));
				break label_1495;
			    }
			    TypeReference caughtType;
			label_1494:
			    {
				caughtType
				    = (TypeReference) _caughtTypes.get(i);
				if (i != 0)
				    output.write(" | ");
				break label_1494;
			    }
			    output.writeReference(caughtType.getFullName(),
						  caughtType);
			    i++;
			}
		    }
		    output.write(')');
		    break label_1497;
		    break label_1495;
		}
	    }
	    output.write(')');
	}
	output.writeLine(" {");
	output.indent();
	super.writeTo(output);
	output.unindent();
	output.writeLine("}");
	break label_1496;
    }
}
