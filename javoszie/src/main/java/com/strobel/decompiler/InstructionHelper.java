 package com.strobel.decompiler;
 
 import com.strobel.assembler.ir.FlowControl;
 import com.strobel.assembler.ir.Instruction;
 import com.strobel.assembler.ir.OpCode;
 import com.strobel.assembler.ir.OpCodeHelpers;
 import com.strobel.assembler.ir.OpCodeType;
 import com.strobel.assembler.metadata.DynamicCallSite;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.IMethodSignature;
 import com.strobel.assembler.metadata.JvmType;
 import com.strobel.assembler.metadata.MethodBody;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.assembler.metadata.VariableReference;
 import com.strobel.core.VerifyArgument;
 import com.strobel.util.ContractUtils;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 public final class InstructionHelper
 {
   public static int getLoadOrStoreSlot(Instruction instruction)
   {
     OpCode code = instruction.getOpCode();
     
     if ((!code.isLoad()) && (!code.isStore())) {
       return -1;
     }
     
     if (code.getOpCodeType() == OpCodeType.Macro) {
       return OpCodeHelpers.getLoadStoreMacroArgumentIndex(code);
     }
     
     VariableReference variable = (VariableReference)instruction.getOperand(0);
     
     return variable.getSlot();
   }
   
   public static int getPopDelta(Instruction instruction, MethodBody body) {
     VerifyArgument.notNull(instruction, "instruction");
     VerifyArgument.notNull(body, "body");
     
     OpCode code = instruction.getOpCode();
     
     switch (code.getStackBehaviorPop()) {
     case Pop0: 
       return 0;
     
     case Pop1: 
       if (code == OpCode.PUTSTATIC) {
         FieldReference field = (FieldReference)instruction.getOperand(0);
         if (field.getFieldType().getSimpleType().isDoubleWord()) {
           return 2;
         }
       }
       return 1;
     
 
     case Pop2: 
       return 2;
     
     case Pop1_Pop1: 
       return 2;
     
     case Pop1_Pop2: 
       return 3;
     
 
     case Pop1_PopA: 
       if (code == OpCode.PUTFIELD) {
         FieldReference field = (FieldReference)instruction.getOperand(0);
         if (field.getFieldType().getSimpleType().isDoubleWord()) {
           return 3;
         }
       }
       return 2;
     
 
     case Pop2_Pop1: 
       return 3;
     
 
     case Pop2_Pop2: 
       return 4;
     
 
     case PopI4: 
       return 1;
     
     case PopI8: 
       return 2;
     
 
     case PopR4: 
       return 1;
     
     case PopR8: 
       return 2;
     
 
     case PopA: 
       return 1;
     
     case PopI4_PopI4: 
       return 2;
     
     case PopI4_PopI8: 
       return 3;
     
 
     case PopI8_PopI8: 
       return 4;
     
 
     case PopR4_PopR4: 
       return 2;
     
     case PopR8_PopR8: 
       return 4;
     
 
     case PopI4_PopA: 
       return 2;
     
     case PopI4_PopI4_PopA: 
       return 3;
     
     case PopI8_PopI4_PopA: 
       return 4;
     
 
     case PopR4_PopI4_PopA: 
       return 3;
     
     case PopR8_PopI4_PopA: 
       return 4;
     
 
     case PopA_PopI4_PopA: 
       return 3;
     
     case PopA_PopA: 
       return 2;
     
     case VarPop: 
       if (code == OpCode.ATHROW) {
         return 1;
       }
       
       if (code == OpCode.MULTIANEWARRAY) {
         return ((Integer)instruction.getOperand(1)).intValue();
       }
       
       if (code.getFlowControl() == FlowControl.Call)
       {
         IMethodSignature signature;
         
         IMethodSignature signature;
         
         if (code == OpCode.INVOKEDYNAMIC) {
           signature = ((DynamicCallSite)instruction.getOperand(0)).getMethodType();
         }
         else {
           signature = (IMethodSignature)instruction.getOperand(0);
         }
         
         List<ParameterDefinition> parameters = signature.getParameters();
         
         int count = parameters.size();
         
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
         if ((code != OpCode.INVOKESTATIC) && (code != OpCode.INVOKEDYNAMIC)) {
           count++;
         }
         
         for (int i = 0; i < parameters.size(); i++) {
           if (((ParameterDefinition)parameters.get(i)).getParameterType().getSimpleType().isDoubleWord()) {
             count++;
           }
         }
         
         return count;
       }
       break;
     }
     throw ContractUtils.unsupported();
   }
   
   public static int getPushDelta(Instruction instruction, MethodBody body) {
     VerifyArgument.notNull(instruction, "instruction");
     VerifyArgument.notNull(body, "body");
     
     OpCode code = instruction.getOpCode();
     
     switch (code.getStackBehaviorPush()) {
     case Push0: 
       return 0;
     
     case Push1: 
       if ((code == OpCode.GETFIELD) || (code == OpCode.GETSTATIC)) {
         FieldReference field = (FieldReference)instruction.getOperand(0);
         if (field.getFieldType().getSimpleType().isDoubleWord()) {
           return 2;
         }
       }
       return 1;
     
 
     case Push1_Push1: 
       return 2;
     
     case Push1_Push1_Push1: 
       return 3;
     
     case Push1_Push2_Push1: 
       return 4;
     
 
     case Push2: 
       return 2;
     
 
     case Push2_Push2: 
       return 4;
     
 
     case Push2_Push1_Push2: 
       return 5;
     
 
     case Push2_Push2_Push2: 
       return 6;
     
 
     case PushI4: 
       return 1;
     
     case PushI8: 
       return 2;
     
 
     case PushR4: 
       return 1;
     
     case PushR8: 
       return 2;
     
 
     case PushA: 
       return 1;
     
     case PushAddress: 
       return 1;
     
     case VarPush: 
       if (code.getFlowControl() == FlowControl.Call)
       {
         IMethodSignature signature;
         
         IMethodSignature signature;
         
         if (code == OpCode.INVOKEDYNAMIC) {
           signature = ((DynamicCallSite)instruction.getOperand(0)).getMethodType();
         }
         else {
           signature = (IMethodSignature)instruction.getOperand(0);
         }
         
         TypeReference returnType = signature.getReturnType();
         JvmType jvmType = returnType.getSimpleType();
         
         if (jvmType == JvmType.Void) {
           return 0;
         }
         
         return jvmType.isDoubleWord() ? 2 : 1;
       }
       break;
     }
     throw ContractUtils.unsupported();
   }
   
   public static Instruction reverseLoadOrStore(Instruction instruction) {
     VerifyArgument.notNull(instruction, "instruction");
     
     OpCode oldCode = instruction.getOpCode();
     
     OpCode newCode;
     if (oldCode.isStore()) {
       newCode = OpCode.valueOf(oldCode.name().replace("STORE", "LOAD"));
     } else { OpCode newCode;
       if (oldCode.isLoad()) {
         newCode = OpCode.valueOf(oldCode.name().replace("LOAD", "STORE"));
       }
       else
         throw new IllegalArgumentException("Instruction is neither a load nor store: " + instruction.getOpCode());
     }
     OpCode newCode;
     if (instruction.getOperandCount() == 1) {
       return new Instruction(newCode, instruction.getOperand(0));
     }
     
     return new Instruction(newCode);
   }
 }


