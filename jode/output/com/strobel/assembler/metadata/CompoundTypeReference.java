/* CompoundTypeReference - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import java.util.Iterator;
import java.util.List;

public final class CompoundTypeReference extends TypeReference
{
    private final TypeReference _baseType;
    private final List _interfaces;
    
    public CompoundTypeReference(TypeReference baseType, List interfaces) {
	_baseType = baseType;
	_interfaces = interfaces;
    }
    
    public final TypeReference getBaseType() {
	return _baseType;
    }
    
    public final List getInterfaces() {
	return _interfaces;
    }
    
    public TypeReference getDeclaringType() {
	return null;
    }
    
    public String getSimpleName() {
	if (_baseType == null)
	    return ((TypeReference) _interfaces.get(0)).getSimpleName();
	return _baseType.getSimpleName();
    }
    
    public boolean containsGenericParameters() {
	TypeReference baseType = getBaseType();
	if (baseType == null || !baseType.containsGenericParameters()) {
	    Iterator i$ = _interfaces.iterator();
	    for (;;) {
		if (!i$.hasNext())
		    return false;
		TypeReference t = (TypeReference) i$.next();
		IF (!t.containsGenericParameters())
		    /* empty */
		return true;
	    }
	}
	return true;
    }
    
    public String getName() {
	if (_baseType == null)
	    return ((TypeReference) _interfaces.get(0)).getName();
	return _baseType.getName();
    }
    
    public String getFullName() {
	if (_baseType == null)
	    return ((TypeReference) _interfaces.get(0)).getFullName();
	return _baseType.getFullName();
    }
    
    public String getInternalName() {
	if (_baseType == null)
	    return ((TypeReference) _interfaces.get(0)).getInternalName();
	return _baseType.getInternalName();
    }
    
    public final Object accept(TypeMetadataVisitor visitor, Object parameter) {
	return visitor.visitCompoundType(this, parameter);
    }
    
    public StringBuilder appendBriefDescription(StringBuilder sb) {
	TypeReference baseType = _baseType;
	List interfaces = _interfaces;
	StringBuilder s;
    label_1183:
	{
	    s = sb;
	    if (baseType != null) {
		s = baseType.appendBriefDescription(s);
		if (!interfaces.isEmpty())
		    s.append(" & ");
	    }
	    break label_1183;
	}
	int i = 0;
	int n = interfaces.size();
	for (;;) {
	label_1184:
	    {
		if (i >= n)
		    return s;
		if (i != 0)
		    s.append(" & ");
		break label_1184;
	    }
	    s = ((TypeReference) interfaces.get(i)).appendBriefDescription(s);
	    i++;
	}
    }
    
    public StringBuilder appendSimpleDescription(StringBuilder sb) {
	TypeReference baseType = _baseType;
	List interfaces = _interfaces;
	StringBuilder s;
    label_1185:
	{
	    s = sb;
	    if (baseType != null) {
		s = baseType.appendSimpleDescription(s);
		if (!interfaces.isEmpty())
		    s.append(" & ");
	    }
	    break label_1185;
	}
	int i = 0;
	int n = interfaces.size();
	for (;;) {
	label_1186:
	    {
		if (i >= n)
		    return s;
		if (i != 0)
		    s.append(" & ");
		break label_1186;
	    }
	    s = ((TypeReference) interfaces.get(i)).appendSimpleDescription(s);
	    i++;
	}
    }
    
    public StringBuilder appendErasedDescription(StringBuilder sb) {
	TypeReference baseType = _baseType;
	List interfaces = _interfaces;
	StringBuilder s;
    label_1187:
	{
	    s = sb;
	    if (baseType != null) {
		s = baseType.appendErasedDescription(s);
		if (!interfaces.isEmpty())
		    s.append(" & ");
	    }
	    break label_1187;
	}
	int i = 0;
	int n = interfaces.size();
	for (;;) {
	label_1188:
	    {
		if (i >= n)
		    return s;
		if (i != 0)
		    s.append(" & ");
		break label_1188;
	    }
	    s = ((TypeReference) interfaces.get(i)).appendErasedDescription(s);
	    i++;
	}
    }
    
    public StringBuilder appendDescription(StringBuilder sb) {
	return appendBriefDescription(sb);
    }
    
    public StringBuilder appendSignature(StringBuilder sb) {
	StringBuilder s;
    label_1189:
	{
	    s = sb;
	    if (_baseType != null)
		s = _baseType.appendSignature(s);
	    break label_1189;
	}
	if (!_interfaces.isEmpty()) {
	    Iterator i$ = _interfaces.iterator();
	    for (;;) {
		if (!i$.hasNext())
		    return s;
		TypeReference interfaceType = (TypeReference) i$.next();
		s.append(':');
		s = interfaceType.appendSignature(s);
	    }
	}
	return s;
    }
    
    public StringBuilder appendErasedSignature(StringBuilder sb) {
	if (_baseType == null) {
	    if (_interfaces.isEmpty())
		return BuiltinTypes.Object.appendErasedSignature(sb);
	    return ((TypeReference) _interfaces.get(0))
		       .appendErasedSignature(sb);
	}
	return _baseType.appendErasedSignature(sb);
    }
}
