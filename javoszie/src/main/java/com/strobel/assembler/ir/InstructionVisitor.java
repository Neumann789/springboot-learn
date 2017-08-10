 package com.strobel.assembler.ir;
 
 import com.strobel.assembler.metadata.DynamicCallSite;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.Label;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.SwitchInfo;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.assembler.metadata.VariableReference;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract interface InstructionVisitor
 {
   public static final InstructionVisitor EMPTY = new InstructionVisitor()
   {
     public void visit(Instruction instruction) {}
     
     public void visit(OpCode opCode) {}
     
     public void visitConstant(OpCode opCode, TypeReference value) {}
     
     public void visitConstant(OpCode opCode, int value) {}
     
     public void visitConstant(OpCode opCode, long value) {}
     
     public void visitConstant(OpCode opCode, float value) {}
     
     public void visitConstant(OpCode opCode, double value) {}
     
     public void visitConstant(OpCode opCode, String value) {}
     
     public void visitBranch(OpCode opCode, Instruction target) {}
     
     public void visitVariable(OpCode opCode, VariableReference variable) {}
     
     public void visitVariable(OpCode opCode, VariableReference variable, int operand) {}
     
     public void visitType(OpCode opCode, TypeReference type) {}
     
     public void visitMethod(OpCode opCode, MethodReference method) {}
     
     public void visitDynamicCallSite(OpCode opCode, DynamicCallSite callSite) {}
     
     public void visitField(OpCode opCode, FieldReference field) {}
     
     public void visitLabel(Label label) {}
     
     public void visitSwitch(OpCode opCode, SwitchInfo switchInfo) {}
     
     public void visitEnd() {}
   };
   
   public abstract void visit(Instruction paramInstruction);
   
   public abstract void visit(OpCode paramOpCode);
   
   public abstract void visitConstant(OpCode paramOpCode, TypeReference paramTypeReference);
   
   public abstract void visitConstant(OpCode paramOpCode, int paramInt);
   
   public abstract void visitConstant(OpCode paramOpCode, long paramLong);
   
   public abstract void visitConstant(OpCode paramOpCode, float paramFloat);
   
   public abstract void visitConstant(OpCode paramOpCode, double paramDouble);
   
   public abstract void visitConstant(OpCode paramOpCode, String paramString);
   
   public abstract void visitBranch(OpCode paramOpCode, Instruction paramInstruction);
   
   public abstract void visitVariable(OpCode paramOpCode, VariableReference paramVariableReference);
   
   public abstract void visitVariable(OpCode paramOpCode, VariableReference paramVariableReference, int paramInt);
   
   public abstract void visitType(OpCode paramOpCode, TypeReference paramTypeReference);
   
   public abstract void visitMethod(OpCode paramOpCode, MethodReference paramMethodReference);
   
   public abstract void visitDynamicCallSite(OpCode paramOpCode, DynamicCallSite paramDynamicCallSite);
   
   public abstract void visitField(OpCode paramOpCode, FieldReference paramFieldReference);
   
   public abstract void visitLabel(Label paramLabel);
   
   public abstract void visitSwitch(OpCode paramOpCode, SwitchInfo paramSwitchInfo);
   
   public abstract void visitEnd();
 }


