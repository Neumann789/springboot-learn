/* Variable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.ast;
import com.strobel.assembler.metadata.ParameterDefinition;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.assembler.metadata.VariableDefinition;

public final class Variable
{
    private String _name;
    private boolean _isGenerated;
    private boolean _isLambdaParameter;
    private TypeReference _type;
    private VariableDefinition _originalVariable;
    private ParameterDefinition _originalParameter;
    
    public final String getName() {
	return _name;
    }
    
    public final void setName(String name) {
	_name = name;
    }
    
    public final boolean isParameter() {
    label_1518:
	{
	    if (_originalParameter == null) {
		VariableDefinition originalVariable = _originalVariable;
		if (originalVariable == null
		    || !originalVariable.isParameter())
		    PUSH false;
		else
		    PUSH true;
	    } else
		return true;
	}
	return POP;
	break label_1518;
    }
    
    public final boolean isGenerated() {
	return _isGenerated;
    }
    
    public final void setGenerated(boolean generated) {
	_isGenerated = generated;
    }
    
    public final TypeReference getType() {
	return _type;
    }
    
    public final void setType(TypeReference type) {
	_type = type;
    }
    
    public final VariableDefinition getOriginalVariable() {
	return _originalVariable;
    }
    
    public final void setOriginalVariable
	(VariableDefinition originalVariable) {
	_originalVariable = originalVariable;
    }
    
    public final ParameterDefinition getOriginalParameter() {
	ParameterDefinition originalParameter = _originalParameter;
	if (originalParameter == null) {
	    VariableDefinition originalVariable = _originalVariable;
	    if (originalVariable == null)
		return null;
	    return originalVariable.getParameter();
	}
	return originalParameter;
    }
    
    public final void setOriginalParameter
	(ParameterDefinition originalParameter) {
	_originalParameter = originalParameter;
    }
    
    public final boolean isLambdaParameter() {
	return _isLambdaParameter;
    }
    
    public final void setLambdaParameter(boolean lambdaParameter) {
	_isLambdaParameter = lambdaParameter;
    }
    
    public final String toString() {
	return _name;
    }
}
