/* TypeMapper - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import java.util.List;

import com.strobel.core.ArrayUtilities;

public abstract class TypeMapper extends DefaultTypeVisitor
{
    public TypeReference visitType(TypeReference type, Object parameter) {
	return type;
    }
    
    public List visit(List types, Object parameter) {
	TypeReference[] newTypes = null;
	int i = 0;
	int n = types.size();
	for (;;) {
	    if (i >= n) {
		if (newTypes == null)
		    return types;
		return ArrayUtilities.asUnmodifiableList(newTypes);
	    }
	    TypeReference oldType = (TypeReference) types.get(i);
	    TypeReference newType
		= (TypeReference) this.visit(oldType, parameter);
	label_1357:
	    {
	    label_1356:
		{
		    if (newType != oldType) {
			if (newTypes == null)
			    newTypes = ((TypeReference[])
					types.toArray(new TypeReference
						      [types.size()]));
			break label_1356;
		    }
		    break label_1357;
		}
		newTypes[i] = newType;
	    }
	    i++;
	}
    }
    
    public List visit(List types) {
	return visit(types, null);
    }
}
