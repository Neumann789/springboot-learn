/* TypeVisitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import com.strobel.assembler.ir.ConstantPool;
import com.strobel.assembler.ir.attributes.SourceAttribute;
import com.strobel.assembler.metadata.annotations.CustomAnnotation;

public interface TypeVisitor
{
    public void visitParser(MetadataParser metadataparser);
    
    public void visit(int i, int i_0_, long l, String string, String string_1_,
		      String string_2_, String[] strings);
    
    public void visitDeclaringMethod(MethodReference methodreference);
    
    public void visitOuterType(TypeReference typereference);
    
    public void visitInnerType(TypeDefinition typedefinition);
    
    public void visitAttribute(SourceAttribute sourceattribute);
    
    public void visitAnnotation(CustomAnnotation customannotation,
				boolean bool);
    
    public FieldVisitor visitField(long l, String string,
				   TypeReference typereference);
    
    public transient MethodVisitor visitMethod
	(long l, String string, IMethodSignature imethodsignature,
	 TypeReference[] typereferences);
    
    public ConstantPool.Visitor visitConstantPool();
    
    public void visitEnd();
}
