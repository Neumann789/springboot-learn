/* FieldReference - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import com.strobel.core.StringUtilities;
import com.strobel.util.ContractUtils;

public abstract class FieldReference extends MemberReference
{
    public abstract TypeReference getFieldType();
    
    public boolean containsGenericParameters() {
    label_1201:
	{
	    TypeReference fieldType = getFieldType();
	    if ((fieldType == null || !fieldType.containsGenericParameters())
		&& !super.containsGenericParameters())
		PUSH false;
	    else
		PUSH true;
	    break label_1201;
	}
	return POP;
    }
    
    public boolean isEquivalentTo(MemberReference member) {
    label_1202:
	{
	    if (!super.isEquivalentTo(member)) {
		if (!(member instanceof FieldReference))
		    return false;
		FieldReference field = (FieldReference) member;
		if (!StringUtilities.equals(field.getName(), getName())
		    || !MetadataResolver.areEquivalent(field
							   .getDeclaringType(),
						       getDeclaringType()))
		    PUSH false;
		else
		    PUSH true;
	    } else
		return true;
	}
	return POP;
	break label_1202;
    }
    
    public FieldDefinition resolve() {
	TypeReference declaringType = getDeclaringType();
	if (declaringType != null)
	    return declaringType.resolve(this);
	throw ContractUtils.unsupported();
    }
    
    protected abstract StringBuilder appendName(StringBuilder stringbuilder,
						boolean bool, boolean bool_0_);
    
    protected StringBuilder appendSignature(StringBuilder sb) {
	return getFieldType().appendSignature(sb);
    }
    
    protected StringBuilder appendErasedSignature(StringBuilder sb) {
	return getFieldType().appendErasedSignature(sb);
    }
}
