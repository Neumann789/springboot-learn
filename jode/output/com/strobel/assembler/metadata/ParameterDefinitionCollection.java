/* ParameterDefinitionCollection - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import com.strobel.assembler.Collection;

public final class ParameterDefinitionCollection extends Collection
{
    final IMethodSignature signature;
    private TypeReference _declaringType;
    
    ParameterDefinitionCollection(IMethodSignature signature) {
	this.signature = signature;
    }
    
    public final TypeReference getDeclaringType() {
	return _declaringType;
    }
    
    final void setDeclaringType(TypeReference declaringType) {
	_declaringType = declaringType;
	int i = 0;
	for (;;) {
	    IF (i >= size())
		/* empty */
	    ((ParameterDefinition) get(i)).setDeclaringType(declaringType);
	    i++;
	}
    }
    
    protected void afterAdd(int index, ParameterDefinition p,
			    boolean appended) {
	p.setMethod(signature);
	p.setPosition(index);
	p.setDeclaringType(_declaringType);
	if (!appended) {
	    for (int i = index + 1; i < size(); i++)
		((ParameterDefinition) get(i)).setPosition(i + 1);
	}
	return;
    }
    
    protected void beforeSet(int index, ParameterDefinition p) {
	ParameterDefinition current = (ParameterDefinition) get(index);
	current.setMethod(null);
	current.setPosition(-1);
	current.setDeclaringType(null);
	p.setMethod(signature);
	p.setPosition(index);
	p.setDeclaringType(_declaringType);
    }
    
    protected void afterRemove(int index, ParameterDefinition p) {
	p.setMethod(null);
	p.setPosition(-1);
	p.setDeclaringType(null);
	int i = index;
	for (;;) {
	    IF (i >= size())
		/* empty */
	    ((ParameterDefinition) get(i)).setPosition(i);
	    i++;
	}
    }
    
    protected void beforeClear() {
	int i = 0;
	for (;;) {
	    IF (i >= size())
		/* empty */
	    ((ParameterDefinition) get(i)).setMethod(null);
	    ((ParameterDefinition) get(i)).setPosition(-1);
	    ((ParameterDefinition) get(i)).setDeclaringType(null);
	    i++;
	}
    }
}
