/* InstructionVisitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir;
import com.strobel.assembler.metadata.DynamicCallSite;
import com.strobel.assembler.metadata.FieldReference;
import com.strobel.assembler.metadata.Label;
import com.strobel.assembler.metadata.MethodReference;
import com.strobel.assembler.metadata.SwitchInfo;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.assembler.metadata.VariableReference;

public interface InstructionVisitor
{
    public static final InstructionVisitor EMPTY
	= new ANONYMOUS CLASS com.strobel.assembler.ir.InstructionVisitor$1();
    
    public void visit(Instruction instruction);
    
    public void visit(OpCode opcode);
    
    public void visitConstant(OpCode opcode, TypeReference typereference);
    
    public void visitConstant(OpCode opcode, int i);
    
    public void visitConstant(OpCode opcode, long l);
    
    public void visitConstant(OpCode opcode, float f);
    
    public void visitConstant(OpCode opcode, double d);
    
    public void visitConstant(OpCode opcode, String string);
    
    public void visitBranch(OpCode opcode, Instruction instruction);
    
    public void visitVariable(OpCode opcode,
			      VariableReference variablereference);
    
    public void visitVariable(OpCode opcode,
			      VariableReference variablereference, int i);
    
    public void visitType(OpCode opcode, TypeReference typereference);
    
    public void visitMethod(OpCode opcode, MethodReference methodreference);
    
    public void visitDynamicCallSite(OpCode opcode,
				     DynamicCallSite dynamiccallsite);
    
    public void visitField(OpCode opcode, FieldReference fieldreference);
    
    public void visitLabel(Label label);
    
    public void visitSwitch(OpCode opcode, SwitchInfo switchinfo);
    
    public void visitEnd();
}
