/* MetadataFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.signatures;
import java.util.List;

import com.strobel.assembler.metadata.GenericParameter;
import com.strobel.assembler.metadata.IClassSignature;
import com.strobel.assembler.metadata.IMethodSignature;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.assembler.metadata.WildcardType;

public interface MetadataFactory
{
    public GenericParameter makeTypeVariable
	(String string, FieldTypeSignature[] fieldtypesignatures);
    
    public transient TypeReference makeParameterizedType
	(TypeReference typereference, TypeReference typereference_0_,
	 TypeReference[] typereferences);
    
    public GenericParameter findTypeVariable(String string);
    
    public WildcardType makeWildcard(FieldTypeSignature fieldtypesignature,
				     FieldTypeSignature fieldtypesignature_1_);
    
    public TypeReference makeNamedType(String string);
    
    public TypeReference makeArrayType(TypeReference typereference);
    
    public TypeReference makeByte();
    
    public TypeReference makeBoolean();
    
    public TypeReference makeShort();
    
    public TypeReference makeChar();
    
    public TypeReference makeInt();
    
    public TypeReference makeLong();
    
    public TypeReference makeFloat();
    
    public TypeReference makeDouble();
    
    public TypeReference makeVoid();
    
    public IMethodSignature makeMethodSignature(TypeReference typereference,
						List list, List list_2_,
						List list_3_);
    
    public IClassSignature makeClassSignature(TypeReference typereference,
					      List list, List list_4_);
}
