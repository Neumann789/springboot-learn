/* AnonymousLocalTypeCollection - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import com.strobel.assembler.Collection;
import com.strobel.core.VerifyArgument;

public final class AnonymousLocalTypeCollection extends Collection
{
    private final MethodDefinition _owner;
    
    public AnonymousLocalTypeCollection(MethodDefinition owner) {
	_owner = (MethodDefinition) VerifyArgument.notNull(owner, "owner");
    }
    
    protected void afterAdd(int index, TypeDefinition type, boolean appended) {
	type.setDeclaringMethod(_owner);
    }
    
    protected void beforeSet(int index, TypeDefinition type) {
	TypeDefinition current = (TypeDefinition) get(index);
	current.setDeclaringMethod(null);
	type.setDeclaringMethod(_owner);
    }
    
    protected void afterRemove(int index, TypeDefinition type) {
	type.setDeclaringMethod(null);
    }
    
    protected void beforeClear() {
	int i = 0;
	for (;;) {
	    IF (i >= size())
		/* empty */
	    ((TypeDefinition) get(i)).setDeclaringMethod(null);
	    i++;
	}
    }
}
