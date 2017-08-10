/* ResolveResult - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.semantics;
import java.util.Collections;

import com.strobel.assembler.metadata.TypeReference;
import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.languages.Region;

public class ResolveResult
{
    private final TypeReference _type;
    
    public ResolveResult(TypeReference type) {
	_type = (TypeReference) VerifyArgument.notNull(type, "type");
    }
    
    public final TypeReference getType() {
	return _type;
    }
    
    public boolean isCompileTimeConstant() {
	return false;
    }
    
    public Object getConstantValue() {
	return null;
    }
    
    public boolean isError() {
	return false;
    }
    
    public String toString() {
	return "[" + this.getClass().getSimpleName() + " " + _type + "]";
    }
    
    public final Iterable getChildResults() {
	return Collections.emptyList();
    }
    
    public final Region getDefinitionRegion() {
	return Region.EMPTY;
    }
}
