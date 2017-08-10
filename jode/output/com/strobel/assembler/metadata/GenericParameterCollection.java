/* GenericParameterCollection - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import com.strobel.assembler.Collection;
import com.strobel.core.VerifyArgument;

public final class GenericParameterCollection extends Collection
{
    private final IGenericParameterProvider _owner;
    
    public GenericParameterCollection(IGenericParameterProvider owner) {
	_owner = (IGenericParameterProvider) VerifyArgument.notNull(owner,
								    "owner");
    }
    
    private void updateGenericParameter(int index, GenericParameter p) {
	p.setOwner(_owner);
	p.setPosition(index);
    }
    
    protected void afterAdd(int index, GenericParameter p, boolean appended) {
	updateGenericParameter(index, p);
	if (!appended) {
	    for (int i = index + 1; i < size(); i++)
		((GenericParameter) get(i)).setPosition(i + 1);
	}
	return;
    }
    
    protected void beforeSet(int index, GenericParameter p) {
	GenericParameter current = (GenericParameter) get(index);
	current.setOwner(null);
	current.setPosition(-1);
	updateGenericParameter(index, p);
    }
    
    protected void afterRemove(int index, GenericParameter p) {
	p.setOwner(null);
	p.setPosition(-1);
	int i = index;
	for (;;) {
	    IF (i >= size())
		/* empty */
	    ((GenericParameter) get(i)).setPosition(i);
	    i++;
	}
    }
    
    protected void beforeClear() {
	super.beforeClear();
    }
}
