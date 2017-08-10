/* MethodVisitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import com.strobel.assembler.ir.Frame;
import com.strobel.assembler.ir.Instruction;
import com.strobel.assembler.ir.InstructionVisitor;
import com.strobel.assembler.ir.attributes.SourceAttribute;
import com.strobel.assembler.metadata.annotations.CustomAnnotation;

public interface MethodVisitor
{
    public boolean canVisitBody();
    
    public InstructionVisitor visitBody(MethodBody methodbody);
    
    public void visitEnd();
    
    public void visitFrame(Frame frame);
    
    public void visitLineNumber(Instruction instruction, int i);
    
    public void visitAttribute(SourceAttribute sourceattribute);
    
    public void visitAnnotation(CustomAnnotation customannotation,
				boolean bool);
    
    public void visitParameterAnnotation(int i,
					 CustomAnnotation customannotation,
					 boolean bool);
}
