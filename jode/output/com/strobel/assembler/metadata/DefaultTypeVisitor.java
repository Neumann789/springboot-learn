/* DefaultTypeVisitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;

public abstract class DefaultTypeVisitor implements TypeMetadataVisitor
{
    public Object visit(TypeReference t) {
	return visit(t, null);
    }
    
    public Object visit(TypeReference t, Object p) {
	return t.accept(this, p);
    }
    
    public Object visitType(TypeReference t, Object p) {
	return null;
    }
    
    public Object visitArrayType(ArrayType t, Object p) {
	return visitType(t, p);
    }
    
    public Object visitBottomType(TypeReference t, Object p) {
	return visitType(t, p);
    }
    
    public Object visitClassType(TypeReference t, Object p) {
	return visitType(t, p);
    }
    
    public Object visitCompoundType(CompoundTypeReference t, Object p) {
	return visitType(t, p);
    }
    
    public Object visitGenericParameter(GenericParameter t, Object p) {
	return visitType(t, p);
    }
    
    public Object visitNullType(TypeReference t, Object p) {
	return visitType(t, p);
    }
    
    public Object visitParameterizedType(TypeReference t, Object p) {
	return visitClassType(t, p);
    }
    
    public Object visitPrimitiveType(PrimitiveType t, Object p) {
	return visitType(t, p);
    }
    
    public Object visitRawType(RawType t, Object p) {
	return visitClassType(t, p);
    }
    
    public Object visitWildcard(WildcardType t, Object p) {
	return visitType(t, p);
    }
    
    public Object visitCapturedType(CapturedType t, Object p) {
	return visitType(t, p);
    }
}
