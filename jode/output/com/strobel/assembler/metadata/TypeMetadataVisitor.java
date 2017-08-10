/* TypeMetadataVisitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;

public interface TypeMetadataVisitor
{
    public Object visitType(TypeReference typereference, Object object);
    
    public Object visitArrayType(ArrayType arraytype, Object object);
    
    public Object visitGenericParameter(GenericParameter genericparameter,
					Object object);
    
    public Object visitWildcard(WildcardType wildcardtype, Object object);
    
    public Object visitCapturedType(CapturedType capturedtype, Object object);
    
    public Object visitCompoundType
	(CompoundTypeReference compoundtypereference, Object object);
    
    public Object visitParameterizedType(TypeReference typereference,
					 Object object);
    
    public Object visitPrimitiveType(PrimitiveType primitivetype,
				     Object object);
    
    public Object visitClassType(TypeReference typereference, Object object);
    
    public Object visitNullType(TypeReference typereference, Object object);
    
    public Object visitBottomType(TypeReference typereference, Object object);
    
    public Object visitRawType(RawType rawtype, Object object);
}
