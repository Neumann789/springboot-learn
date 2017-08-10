/* RangeType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.type;
import jode.AssertError;
import jode.GlobalOptions;

public class RangeType extends Type
{
    final ReferenceType bottomType;
    final ReferenceType topType;
    
    public RangeType(ReferenceType referencetype,
		     ReferenceType referencetype_0_) {
	super(103);
	if (referencetype != tNull) {
	    bottomType = referencetype;
	    topType = referencetype_0_;
	}
	throw new AssertError("bottom is NULL");
    }
    
    public ReferenceType getBottom() {
	return bottomType;
    }
    
    public ReferenceType getTop() {
	return topType;
    }
    
    public Type getHint() {
	Type type = bottomType.getHint();
	Type type_1_ = topType.getHint();
	if (topType != tNull || !bottomType.equals(type))
	    return type_1_;
	return type;
    }
    
    public Type getCanonic() {
	return topType.getCanonic();
    }
    
    public Type getSuperType() {
	return topType.getSuperType();
    }
    
    public Type getSubType() {
	return tRange(bottomType, tNull);
    }
    
    public Type getCastHelper(Type type) {
	return topType.getCastHelper(type);
    }
    
    public String getTypeSignature() {
	if (!topType.isClassType() && bottomType.isValidType())
	    return bottomType.getTypeSignature();
	return topType.getTypeSignature();
    }
    
    public Class getTypeClass() throws ClassNotFoundException {
	if (!topType.isClassType() && bottomType.isValidType())
	    return bottomType.getTypeClass();
	return topType.getTypeClass();
    }
    
    public String toString() {
	return "<" + bottomType + "-" + topType + ">";
    }
    
    public String getDefaultName() {
	throw new AssertError("getDefaultName() called on range");
    }
    
    public int hashCode() {
	int i = topType.hashCode();
	return (i << 16 | i >>> 16) ^ bottomType.hashCode();
    }
    
    public boolean equals(Object object) {
	if (!(object instanceof RangeType))
	    return false;
    label_1103:
	{
	    RangeType rangetype_2_ = (RangeType) object;
	    if (!topType.equals(rangetype_2_.topType)
		|| !bottomType.equals(rangetype_2_.bottomType))
		PUSH false;
	    else
		PUSH true;
	    break label_1103;
	}
	return POP;
    }
    
    public Type intersection(Type type) {
	Type type_3_;
    label_1105:
	{
	label_1104:
	    {
		if (type != tError) {
		    if (type != Type.tUnknown) {
			Type type_4_ = bottomType.getSpecializedType(type);
			Type type_5_ = topType.getGeneralizedType(type);
			if (!type_5_.equals(type_4_)) {
			    if (!(type_5_ instanceof ReferenceType)
				|| !(type_4_ instanceof ReferenceType))
				type_3_ = tError;
			    else
				type_3_ = (((ReferenceType) type_5_)
					       .createRangeType
					   ((ReferenceType) type_4_));
			} else
			    type_3_ = type_5_;
		    } else
			return this;
		} else
		    return type;
	    }
	    if ((GlobalOptions.debuggingFlags & 0x4) != 0)
		GlobalOptions.err.println("intersecting " + this + " and "
					  + type + " to " + type_3_);
	    break label_1105;
	}
	return type_3_;
	break label_1104;
    }
}
