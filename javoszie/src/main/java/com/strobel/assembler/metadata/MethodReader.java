 package com.strobel.assembler.metadata;
 
 import com.strobel.assembler.ir.ErrorOperand;
 import com.strobel.assembler.ir.Instruction;
 import com.strobel.assembler.ir.InstructionCollection;
 import com.strobel.assembler.ir.OpCode;
 import com.strobel.assembler.ir.OpCodeHelpers;
 import com.strobel.assembler.ir.OperandType;
 import com.strobel.assembler.ir.attributes.CodeAttribute;
 import com.strobel.assembler.ir.attributes.ExceptionTableEntry;
 import com.strobel.assembler.ir.attributes.LocalVariableTableAttribute;
 import com.strobel.assembler.ir.attributes.LocalVariableTableEntry;
 import com.strobel.assembler.ir.attributes.SourceAttribute;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import java.lang.reflect.Modifier;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class MethodReader
 {
   private final MethodDefinition _methodDefinition;
   private final CodeAttribute _code;
   private final IMetadataScope _scope;
   private final MethodBody _methodBody;
   private final TypeReference _declaringType;
   private final int _modifiers;
   
   public MethodReader(MethodDefinition methodDefinition, IMetadataScope scope)
   {
     this._methodDefinition = ((MethodDefinition)VerifyArgument.notNull(methodDefinition, "methodDefinition"));
     this._scope = ((IMetadataScope)VerifyArgument.notNull(scope, "scope"));
     this._declaringType = methodDefinition.getDeclaringType();
     this._modifiers = methodDefinition.getModifiers();
     this._code = ((CodeAttribute)SourceAttribute.find("Code", methodDefinition.getSourceAttributes()));
     this._methodBody = new MethodBody(methodDefinition);
     this._methodBody.setCodeSize(this._code.getCode().size());
     this._methodBody.setMaxStackSize(this._code.getMaxStack());
     this._methodBody.setMaxLocals(this._code.getMaxLocals());
   }
   
   public MethodBody readBody()
   {
     try {
       return readBodyCore();
     }
     catch (Throwable t) {
       throw new MethodBodyParseException(String.format("An error occurred while parsing the bytecode of method '%s:%s'.", new Object[] { this._methodDefinition.getFullName(), this._methodDefinition.getSignature() }), t);
     }
   }
   
 
 
 
 
 
   private MethodBody readBodyCore()
   {
     Buffer b = this._code.getCode();
     
     b.position(0);
     
     InstructionCollection body = this._methodBody.getInstructions();
     VariableDefinitionCollection variables = this._methodBody.getVariables();
     
     LocalVariableTableAttribute localVariableTable = (LocalVariableTableAttribute)SourceAttribute.find("LocalVariableTable", this._code.getAttributes());
     
 
 
 
     LocalVariableTableAttribute localVariableTypeTable = (LocalVariableTableAttribute)SourceAttribute.find("LocalVariableTypeTable", this._code.getAttributes());
     
 
 
 
     boolean hasThis = !Modifier.isStatic(this._modifiers);
     List<ParameterDefinition> parameters = this._methodDefinition.getParameters();
     
     if (hasThis) {
       ParameterDefinition thisParameter = new ParameterDefinition(0, "this", this._declaringType);
       
 
 
 
 
       VariableDefinition thisVariable = new VariableDefinition(0, "this", this._methodDefinition, this._declaringType);
       
 
 
 
 
 
       thisVariable.setScopeStart(0);
       thisVariable.setScopeEnd(this._code.getCodeSize());
       thisVariable.setFromMetadata(false);
       thisVariable.setParameter(thisParameter);
       
       variables.add(thisVariable);
       
       this._methodBody.setThisParameter(thisParameter);
     }
     
     for (int i = 0; i < parameters.size(); i++) {
       ParameterDefinition parameter = (ParameterDefinition)parameters.get(i);
       int variableSlot = parameter.getSlot();
       
       VariableDefinition variable = new VariableDefinition(variableSlot, parameter.getName(), this._methodDefinition, parameter.getParameterType());
       
 
 
 
 
 
       variable.setScopeStart(0);
       variable.setScopeEnd(this._code.getCodeSize());
       variable.setTypeKnown(true);
       variable.setFromMetadata(false);
       variable.setParameter(parameter);
       
       variables.add(variable);
     }
     
     if (localVariableTable != null) {
       processLocalVariableTable(variables, localVariableTable, parameters);
     }
     
     if (localVariableTypeTable != null) {
       processLocalVariableTable(variables, localVariableTypeTable, parameters);
     }
     
     for (VariableDefinition variable : variables) {
       if (!variable.isFromMetadata()) {
         variable.setScopeStart(-1);
         variable.setScopeEnd(-1);
       }
     }
     
 
     Fixup[] fixups = new Fixup[b.size()];
     
     while (b.position() < b.size()) {
       int offset = b.position();
       
       int code = b.readUnsignedByte();
       
       if (code == 196) {
         code = code << 8 | b.readUnsignedByte();
       }
       
       OpCode op = OpCode.get(code);
       final Instruction instruction;
       Instruction instruction;
       Instruction instruction; Instruction instruction; switch (op.getOperandType()) {
       case None: 
         if ((op.isLoad()) || (op.isStore())) {
           variables.reference(OpCodeHelpers.getLoadStoreMacroArgumentIndex(op), op, offset);
         }
         instruction = Instruction.create(op);
         break;
       
 
       case PrimitiveTypeCode: 
         instruction = Instruction.create(op, BuiltinTypes.fromPrimitiveTypeCode(b.readUnsignedByte()));
         break;
       
 
       case TypeReference: 
         int typeToken = b.readUnsignedShort();
         instruction = Instruction.create(op, this._scope.lookupType(typeToken));
         break;
       
 
       case TypeReferenceU1: 
         instruction = Instruction.create(op, this._scope.lookupType(b.readUnsignedShort()), b.readUnsignedByte());
         break;
       
 
       case DynamicCallSite: 
         instruction = Instruction.create(op, this._scope.lookupDynamicCallSite(b.readUnsignedShort()));
         b.readUnsignedByte();
         b.readUnsignedByte();
         break;
       
 
       case MethodReference: 
         instruction = Instruction.create(op, this._scope.lookupMethod(b.readUnsignedShort()));
         
         if (op == OpCode.INVOKEINTERFACE) {
           b.readUnsignedByte();
           b.readUnsignedByte();
         }
         
 
 
         break;
       case FieldReference: 
         instruction = Instruction.create(op, this._scope.lookupField(b.readUnsignedShort()));
         break;
       
 
 
 
       case BranchTarget: 
       case BranchTargetWide: 
         instruction = new Instruction(op);
         int targetOffset;
         int targetOffset; if (op.isWide()) {
           targetOffset = offset + ((Integer)this._scope.lookupConstant(b.readUnsignedShort())).intValue();
         } else { int targetOffset;
           if (op.getOperandType() == OperandType.BranchTargetWide) {
             targetOffset = offset + b.readInt();
           }
           else {
             targetOffset = offset + b.readShort();
           }
         }
         if (targetOffset < offset) {
           Instruction target = body.atOffset(targetOffset);
           
           if (!target.hasLabel()) {
             target.setLabel(new Label(targetOffset));
           }
           
           instruction.setOperand(target);
         }
         else if (targetOffset == offset) {
           instruction.setOperand(instruction);
           instruction.setLabel(new Label(offset));
         }
         else if (targetOffset > b.size())
         {
 
 
           instruction.setOperand(new Instruction(targetOffset, OpCode.NOP));
         }
         else {
           Fixup oldFixup = fixups[targetOffset];
           Fixup newFixup = new Fixup(instruction)
           {
             public void fix(Instruction target) {
               instruction.setOperand(target);
             }
             
           };
           fixups[targetOffset] = (oldFixup != null ? Fixup.combine(oldFixup, newFixup) : newFixup);
         }
         
 
         break;
       
 
       case I1: 
         instruction = Instruction.create(op, (short)b.readByte());
         break;
       
 
       case I2: 
         instruction = Instruction.create(op, b.readShort());
         break;
       
 
       case I8: 
         instruction = Instruction.create(op, b.readLong());
         break;
       
 
 
       case Constant: 
         instruction = new Instruction(op, this._scope.lookupConstant(b.readUnsignedByte()));
         break;
       
 
       case WideConstant: 
         int constantToken = b.readUnsignedShort();
         
         instruction = new Instruction(op, this._scope.lookupConstant(constantToken));
         break;
       
 
       case Switch: 
         while (b.position() % 4 != 0) {
           b.readByte();
         }
         
         final SwitchInfo switchInfo = new SwitchInfo();
         int defaultOffset = offset + b.readInt();
         
         instruction = Instruction.create(op, switchInfo);
         
         if (defaultOffset < offset) {
           switchInfo.setDefaultTarget(body.atOffset(defaultOffset));
         }
         else if (defaultOffset == offset) {
           switchInfo.setDefaultTarget(instruction);
         }
         else {
           switchInfo.setDefaultTarget(new Instruction(defaultOffset, OpCode.NOP));
           
           Fixup oldFixup = fixups[defaultOffset];
           Fixup newFixup = new Fixup(switchInfo)
           {
             public void fix(Instruction target) {
               switchInfo.setDefaultTarget(target);
             }
             
           };
           fixups[defaultOffset] = (oldFixup != null ? Fixup.combine(oldFixup, newFixup) : newFixup);
         }
         
 
         if (op == OpCode.TABLESWITCH) {
           int low = b.readInt();
           int high = b.readInt();
           final Instruction[] targets = new Instruction[high - low + 1];
           
           switchInfo.setLowValue(low);
           switchInfo.setHighValue(high);
           
           for (int i = 0; i < targets.length; i++) {
             final int targetIndex = i;
             int targetOffset = offset + b.readInt();
             
             if (targetOffset < offset) {
               targets[targetIndex] = body.atOffset(targetOffset);
             }
             else if (targetOffset == offset) {
               targets[targetIndex] = instruction;
             }
             else {
               targets[targetIndex] = new Instruction(targetOffset, OpCode.NOP);
               
               Fixup oldFixup = fixups[targetOffset];
               Fixup newFixup = new Fixup(targets)
               {
                 public void fix(Instruction target) {
                   targets[targetIndex] = target;
                 }
                 
               };
               fixups[targetOffset] = (oldFixup != null ? Fixup.combine(oldFixup, newFixup) : newFixup);
             }
           }
           
 
           switchInfo.setTargets(targets);
         }
         else {
           int pairCount = b.readInt();
           int[] keys = new int[pairCount];
           final Instruction[] targets = new Instruction[pairCount];
           
           for (int i = 0; i < pairCount; i++) {
             final int targetIndex = i;
             
             keys[targetIndex] = b.readInt();
             
             int targetOffset = offset + b.readInt();
             
             if (targetOffset < offset) {
               targets[targetIndex] = body.atOffset(targetOffset);
             }
             else if (targetOffset == offset) {
               targets[targetIndex] = instruction;
             }
             else {
               targets[targetIndex] = new Instruction(targetOffset, OpCode.NOP);
               
               Fixup oldFixup = fixups[targetOffset];
               Fixup newFixup = new Fixup(targets)
               {
                 public void fix(Instruction target) {
                   targets[targetIndex] = target;
                 }
                 
               };
               fixups[targetOffset] = (oldFixup != null ? Fixup.combine(oldFixup, newFixup) : newFixup);
             }
           }
           
 
           switchInfo.setKeys(keys);
           switchInfo.setTargets(targets);
         }
         
         break;
       case Local: 
         int variableSlot;
         
         int variableSlot;
         
         if (op.isWide()) {
           variableSlot = b.readUnsignedShort();
         }
         else {
           variableSlot = b.readUnsignedByte();
         }
         
         VariableReference variable = variables.reference(variableSlot, op, offset);
         
         if (variableSlot < 0) {
           instruction = new Instruction(op, new ErrorOperand("!!! BAD LOCAL: " + variableSlot + " !!!"));
         }
         else {
           instruction = Instruction.create(op, variable);
         }
         
         break;
       case LocalI1: 
         int variableSlot;
         
 
         int variableSlot;
         
         if (op.isWide()) {
           variableSlot = b.readUnsignedShort();
         }
         else {
           variableSlot = b.readUnsignedByte();
         }
         
         VariableReference variable = variables.reference(variableSlot, op, offset);
         
         int operand = b.readByte();
         
         if (variableSlot < 0) {
           instruction = new Instruction(op, new Object[] { new ErrorOperand("!!! BAD LOCAL: " + variableSlot + " !!!"), Integer.valueOf(operand) });
 
 
         }
         else
         {
 
           instruction = Instruction.create(op, variable, operand);
         }
         
         break;
       case LocalI2: 
         int variableSlot;
         
 
         int variableSlot;
         
         if (op.isWide()) {
           variableSlot = b.readUnsignedShort();
         }
         else {
           variableSlot = b.readUnsignedByte();
         }
         
         VariableReference variable = variables.reference(variableSlot, op, offset);
         
         int operand = b.readShort();
         
         if (variableSlot < 0) {
           instruction = new Instruction(op, new Object[] { new ErrorOperand("!!! BAD LOCAL: " + variableSlot + " !!!"), Integer.valueOf(operand) });
 
 
         }
         else
         {
 
           instruction = Instruction.create(op, variable, operand);
         }
         
         break;
       
 
       default: 
         throw new IllegalStateException("Unrecognized opcode: " + code);
       }
       
       
       instruction.setOffset(offset);
       body.add(instruction);
       
       Fixup fixup = fixups[offset];
       
       if (fixup != null) {
         if (!instruction.hasLabel()) {
           instruction.setLabel(new Label(offset));
         }
         fixup.fix(instruction);
       }
     }
     
     int labelCount = 0;
     
     for (int i = 0; i < body.size(); i++) {
       Instruction instruction = (Instruction)body.get(i);
       OpCode code = instruction.getOpCode();
       Object operand = instruction.hasOperand() ? instruction.getOperand(0) : null;
       
       if ((operand instanceof VariableDefinition)) {
         VariableDefinition currentVariable = (VariableDefinition)operand;
         int effectiveOffset;
         int effectiveOffset;
         if (code.isStore()) {
           effectiveOffset = instruction.getOffset() + code.getSize() + code.getOperandType().getBaseSize();
         }
         else {
           effectiveOffset = instruction.getOffset();
         }
         
         VariableDefinition actualVariable = variables.tryFind(currentVariable.getSlot(), effectiveOffset);
         
         if ((actualVariable == null) && (code.isStore())) {
           actualVariable = variables.find(currentVariable.getSlot(), effectiveOffset + code.getSize() + code.getOperandType().getBaseSize());
         }
         
 
 
 
         if (actualVariable != currentVariable) {
           if (instruction.getOperandCount() > 1) {
             Object[] operands = new Object[instruction.getOperandCount()];
             
             operands[0] = actualVariable;
             
             for (int j = 1; j < operands.length; j++) {
               operands[j] = instruction.getOperand(j);
             }
             
             instruction.setOperand(operands);
           }
           else {
             instruction.setOperand(actualVariable);
           }
         }
       }
       
       if (instruction.hasLabel()) {
         instruction.getLabel().setIndex(labelCount++);
       }
     }
     
     List<ExceptionTableEntry> exceptionTable = this._code.getExceptionTableEntries();
     
     if (!exceptionTable.isEmpty())
     {
       this._methodBody.getExceptionHandlers().addAll(ExceptionHandlerMapper.run(body, exceptionTable));
     }
     
 
 
 
 
 
 
 
 
     return this._methodBody;
   }
   
 
 
 
   private void processLocalVariableTable(VariableDefinitionCollection variables, LocalVariableTableAttribute table, List<ParameterDefinition> parameters)
   {
     for (LocalVariableTableEntry entry : table.getEntries()) {
       int slot = entry.getIndex();
       int scopeStart = entry.getScopeOffset();
       int scopeEnd = scopeStart + entry.getScopeLength();
       
       VariableDefinition variable = variables.tryFind(slot, scopeStart);
       
       if (variable == null) {
         variable = new VariableDefinition(slot, entry.getName(), this._methodDefinition, entry.getType());
         
 
 
 
 
 
         variables.add(variable);
       }
       else if (!StringUtilities.isNullOrEmpty(entry.getName())) {
         variable.setName(entry.getName());
       }
       
       variable.setVariableType(entry.getType());
       variable.setTypeKnown(true);
       variable.setFromMetadata(true);
       variable.setScopeStart(scopeStart);
       variable.setScopeEnd(scopeEnd);
       
       if (entry.getScopeOffset() == 0) {
         ParameterDefinition parameter = null;
         
         for (int j = 0; j < parameters.size(); j++) {
           if (((ParameterDefinition)parameters.get(j)).getSlot() == entry.getIndex()) {
             parameter = (ParameterDefinition)parameters.get(j);
             break;
           }
         }
         
         if ((parameter != null) && (!parameter.hasName())) {
           parameter.setName(entry.getName());
         }
       }
     }
   }
   
 
   private static abstract class Fixup
   {
     public abstract void fix(Instruction paramInstruction);
     
     public static Fixup combine(Fixup first, Fixup second)
     {
       Fixup[] fixups;
       if ((first instanceof MultiFixup)) {
         MultiFixup m1 = (MultiFixup)first;
         Fixup[] fixups;
         if ((second instanceof MultiFixup)) {
           MultiFixup m2 = (MultiFixup)second;
           
           Fixup[] fixups = new Fixup[m1._fixups.length + m2._fixups.length];
           
           System.arraycopy(m2._fixups, 0, fixups, m1._fixups.length, m2._fixups.length);
 
 
 
         }
         else
         {
 
 
           fixups = new Fixup[m1._fixups.length + 1];
           fixups[m1._fixups.length] = second;
         }
         
         System.arraycopy(m1._fixups, 0, fixups, 0, m1._fixups.length);
 
 
 
 
 
 
 
       }
       else if ((second instanceof MultiFixup)) {
         MultiFixup m2 = (MultiFixup)second;
         
         Fixup[] fixups = new Fixup[1 + m2._fixups.length];
         
         System.arraycopy(m2._fixups, 0, fixups, 1, m2._fixups.length);
 
 
 
       }
       else
       {
 
 
         fixups = new Fixup[] { first, second };
       }
       
 
       return new MultiFixup(fixups, null);
     }
     
     private static final class MultiFixup extends MethodReader.Fixup {
       private final MethodReader.Fixup[] _fixups;
       
       private MultiFixup(MethodReader.Fixup... fixups) { super();
         this._fixups = ((MethodReader.Fixup[])VerifyArgument.noNullElements(fixups, "fixups"));
       }
       
       public void fix(Instruction target)
       {
         for (MethodReader.Fixup fixup : this._fixups) {
           fixup.fix(target);
         }
       }
     }
   }
 }


