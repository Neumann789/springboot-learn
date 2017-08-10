 package com.strobel.assembler.ir;
 
 import com.strobel.assembler.metadata.DynamicCallSite;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.JvmType;
 import com.strobel.assembler.metadata.Label;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.SwitchInfo;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.assembler.metadata.VariableReference;
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilerHelpers;
 import com.strobel.decompiler.PlainTextOutput;
 import com.strobel.util.ContractUtils;
 import java.lang.reflect.Array;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Instruction
   implements Comparable<Instruction>
 {
   private int _offset = -1;
   
   private OpCode _opCode;
   private Object _operand;
   private Label _label;
   private Instruction _previous;
   
   public Instruction(int offset, OpCode opCode)
   {
     this._offset = offset;
     this._opCode = opCode;
   }
   
   public Instruction(OpCode opCode) {
     this._opCode = opCode;
     this._operand = null;
   }
   
   public Instruction(OpCode opCode, Object operand) {
     this._opCode = opCode;
     this._operand = operand;
   }
   
   public Instruction(OpCode opCode, Object... operands) {
     this._opCode = opCode;
     this._operand = VerifyArgument.notNull(operands, "operands");
   }
   
   public boolean hasOffset() {
     return this._offset >= 0;
   }
   
   public boolean hasOperand() {
     return this._operand != null;
   }
   
   public int getOffset() {
     return this._offset;
   }
   
   public void setOffset(int offset) {
     this._offset = offset;
   }
   
   public int getEndOffset() {
     return this._offset + getSize();
   }
   
   public OpCode getOpCode() {
     return this._opCode;
   }
   
   public void setOpCode(OpCode opCode) {
     this._opCode = opCode;
   }
   
   public int getOperandCount() {
     Object operand = this._operand;
     
     if (operand == null) {
       return 0;
     }
     
     if (ArrayUtilities.isArray(operand)) {
       return Array.getLength(operand);
     }
     
     return 1;
   }
   
   public <T> T getOperand(int index)
   {
     Object operand = this._operand;
     
     if (ArrayUtilities.isArray(operand)) {
       VerifyArgument.inRange(0, Array.getLength(operand) - 1, index, "index");
       return (T)Array.get(operand, index);
     }
     
     VerifyArgument.inRange(0, 0, index, "index");
     return (T)operand;
   }
   
   public void setOperand(Object operand)
   {
     this._operand = operand;
   }
   
   public boolean hasLabel() {
     return this._label != null;
   }
   
   public Label getLabel() {
     return this._label;
   }
   
   public void setLabel(Label label) {
     this._label = label;
   }
   
   public Instruction getPrevious() {
     return this._previous;
   }
   
   public void setPrevious(Instruction previous) {
     this._previous = previous;
   }
   
   public Instruction getNext() {
     return this._next;
   }
   
   public void setNext(Instruction next) {
     this._next = next;
   }
   
 
   public Instruction clone()
   {
     Instruction copy = new Instruction(this._opCode, (Object)null);
     
     copy._offset = this._offset;
     copy._label = (this._label != null ? new Label(this._label.getIndex()) : null);
     
     if (ArrayUtilities.isArray(this._operand)) {
       copy._operand = ((Object[])this._operand).clone();
     }
     else {
       copy._operand = this._operand;
     }
     
     return copy;
   }
   
   public String toString()
   {
     PlainTextOutput output = new PlainTextOutput();
     DecompilerHelpers.writeInstruction(output, this);
     return output.toString();
   }
   
 
   public int getSize()
   {
     int opCodeSize = this._opCode.getSize();
     OperandType operandType = this._opCode.getOperandType();
     
     switch (operandType) {
     case None: 
       return opCodeSize;
     
     case PrimitiveTypeCode: 
     case TypeReference: 
     case TypeReferenceU1: 
       return opCodeSize + operandType.getBaseSize();
     
     case DynamicCallSite: 
       return opCodeSize + operandType.getBaseSize();
     
 
     case MethodReference: 
       switch (this._opCode) {
       case INVOKEVIRTUAL: 
       case INVOKESPECIAL: 
       case INVOKESTATIC: 
         return opCodeSize + operandType.getBaseSize();
       case INVOKEINTERFACE: 
         return opCodeSize + operandType.getBaseSize() + 2;
       }
       break;
     
     case FieldReference: 
     case BranchTarget: 
     case BranchTargetWide: 
     case I1: 
     case I2: 
     case I8: 
     case Constant: 
     case WideConstant: 
       return opCodeSize + operandType.getBaseSize();
     
     case Switch: 
       Instruction[] targets = ((SwitchInfo)this._operand).getTargets();
       int relativeOffset = this._offset + opCodeSize;
       int padding = this._offset >= 0 ? (4 - relativeOffset % 4) % 4 : 0;
       switch (this._opCode)
       {
       case TABLESWITCH: 
         return opCodeSize + padding + 12 + targets.length * 4;
       
       case LOOKUPSWITCH: 
         return opCodeSize + padding + 8 + targets.length * 8;
       }
       break;
     
     case Local: 
       return opCodeSize + (this._opCode.isWide() ? 2 : 1);
     
     case LocalI1: 
     case LocalI2: 
       return opCodeSize + operandType.getBaseSize();
     }
     
     throw ContractUtils.unreachable();
   }
   
 
 
 
   public static Instruction create(OpCode opCode)
   {
     VerifyArgument.notNull(opCode, "opCode");
     
     if (opCode.getOperandType() != OperandType.None) {
       throw new IllegalArgumentException(String.format("Invalid operand for OpCode %s.", new Object[] { opCode }));
     }
     
     return new Instruction(opCode);
   }
   
   public static Instruction create(OpCode opCode, Instruction target) {
     VerifyArgument.notNull(opCode, "opCode");
     VerifyArgument.notNull(target, "target");
     
     if ((opCode.getOperandType() != OperandType.BranchTarget) && (opCode.getOperandType() != OperandType.BranchTargetWide))
     {
 
       throw new IllegalArgumentException(String.format("Invalid operand for OpCode %s.", new Object[] { opCode }));
     }
     
     return new Instruction(opCode, target);
   }
   
   public static Instruction create(OpCode opCode, SwitchInfo switchInfo) {
     VerifyArgument.notNull(opCode, "opCode");
     VerifyArgument.notNull(switchInfo, "switchInfo");
     
     if (opCode.getOperandType() != OperandType.Switch) {
       throw new IllegalArgumentException(String.format("Invalid operand for OpCode %s.", new Object[] { opCode }));
     }
     
     return new Instruction(opCode, switchInfo);
   }
   
   public static Instruction create(OpCode opCode, int value) {
     VerifyArgument.notNull(opCode, "opCode");
     
     if (!checkOperand(opCode.getOperandType(), value)) {
       throw new IllegalArgumentException(String.format("Invalid operand for OpCode %s.", new Object[] { opCode }));
     }
     
     return new Instruction(opCode, Integer.valueOf(value));
   }
   
   public static Instruction create(OpCode opCode, short value) {
     VerifyArgument.notNull(opCode, "opCode");
     
     if (!checkOperand(opCode.getOperandType(), value)) {
       throw new IllegalArgumentException(String.format("Invalid operand for OpCode %s.", new Object[] { opCode }));
     }
     
     return new Instruction(opCode, Short.valueOf(value));
   }
   
   public static Instruction create(OpCode opCode, float value) {
     VerifyArgument.notNull(opCode, "opCode");
     
     if ((opCode.getOperandType() != OperandType.Constant) && (opCode.getOperandType() != OperandType.WideConstant)) {
       throw new IllegalArgumentException(String.format("Invalid operand for OpCode %s.", new Object[] { opCode }));
     }
     
     return new Instruction(opCode, Float.valueOf(value));
   }
   
   public static Instruction create(OpCode opCode, double value) {
     VerifyArgument.notNull(opCode, "opCode");
     
     if (opCode.getOperandType() != OperandType.WideConstant) {
       throw new IllegalArgumentException(String.format("Invalid operand for OpCode %s.", new Object[] { opCode }));
     }
     
     return new Instruction(opCode, Double.valueOf(value));
   }
   
   public static Instruction create(OpCode opCode, long value) {
     VerifyArgument.notNull(opCode, "opCode");
     
     if ((opCode.getOperandType() != OperandType.I8) && (opCode.getOperandType() != OperandType.WideConstant)) {
       throw new IllegalArgumentException(String.format("Invalid operand for OpCode %s.", new Object[] { opCode }));
     }
     
     return new Instruction(opCode, Long.valueOf(value));
   }
   
   public static Instruction create(OpCode opCode, VariableReference variable) {
     VerifyArgument.notNull(opCode, "opCode");
     
     if (opCode.getOperandType() != OperandType.Local) {
       throw new IllegalArgumentException(String.format("Invalid operand for OpCode %s.", new Object[] { opCode }));
     }
     
     return new Instruction(opCode, variable);
   }
   
   public static Instruction create(OpCode opCode, VariableReference variable, int operand) {
     VerifyArgument.notNull(opCode, "opCode");
     VerifyArgument.notNull(variable, "variable");
     
     if (!checkOperand(opCode.getOperandType(), operand)) {
       throw new IllegalArgumentException(String.format("Invalid operand for OpCode %s.", new Object[] { opCode }));
     }
     
     return new Instruction(opCode, new Object[] { variable, Integer.valueOf(operand) });
   }
   
   public static Instruction create(OpCode opCode, TypeReference type) {
     VerifyArgument.notNull(opCode, "opCode");
     VerifyArgument.notNull(type, "type");
     
     if (!checkOperand(opCode.getOperandType(), type)) {
       throw new IllegalArgumentException(String.format("Invalid operand for OpCode %s.", new Object[] { opCode }));
     }
     
     return new Instruction(opCode, type);
   }
   
   public static Instruction create(OpCode opCode, TypeReference type, int operand) {
     VerifyArgument.notNull(opCode, "opCode");
     
     if ((!checkOperand(opCode.getOperandType(), type)) || (!checkOperand(opCode.getOperandType(), operand))) {
       throw new IllegalArgumentException(String.format("Invalid operand for OpCode %s.", new Object[] { opCode }));
     }
     
     return new Instruction(opCode, new Object[] { type, Integer.valueOf(operand) });
   }
   
   public static Instruction create(OpCode opCode, MethodReference method) {
     VerifyArgument.notNull(opCode, "opCode");
     
     if (!checkOperand(opCode.getOperandType(), method)) {
       throw new IllegalArgumentException(String.format("Invalid operand for OpCode %s.", new Object[] { opCode }));
     }
     
     return new Instruction(opCode, method);
   }
   
   public static Instruction create(OpCode opCode, DynamicCallSite callSite) {
     VerifyArgument.notNull(opCode, "opCode");
     
     if (!checkOperand(opCode.getOperandType(), callSite)) {
       throw new IllegalArgumentException(String.format("Invalid operand for OpCode %s.", new Object[] { opCode }));
     }
     
     return new Instruction(opCode, callSite);
   }
   
   public static Instruction create(OpCode opCode, FieldReference field) {
     VerifyArgument.notNull(opCode, "opCode");
     
     if (!checkOperand(opCode.getOperandType(), field)) {
       throw new IllegalArgumentException(String.format("Invalid operand for OpCode %s.", new Object[] { opCode }));
     }
     
     return new Instruction(opCode, field);
   }
   
 
   private Instruction _next;
   
   private static final int U1_MIN_VALUE = 0;
   
   private static final int U1_MAX_VALUE = 255;
   
   private static final int U2_MIN_VALUE = 0;
   
   private static final int U2_MAX_VALUE = 65535;
   private static boolean checkOperand(OperandType operandType, int value)
   {
     switch (operandType) {
     case I1: 
     case LocalI1: 
       return (value >= -128) && (value <= 127);
     case I2: 
     case LocalI2: 
       return (value >= 32768) && (value <= 32767);
     case TypeReferenceU1: 
       return (value >= 0) && (value <= 255);
     }
     return false;
   }
   
   private static boolean checkOperand(OperandType operandType, TypeReference type)
   {
     VerifyArgument.notNull(type, "type");
     
     switch (operandType) {
     case PrimitiveTypeCode: 
       return type.getSimpleType().isPrimitive();
     case TypeReference: 
     case TypeReferenceU1: 
       return true;
     }
     return false;
   }
   
   private static boolean checkOperand(OperandType operandType, DynamicCallSite callSite)
   {
     VerifyArgument.notNull(callSite, "callSite");
     
     switch (operandType) {
     case DynamicCallSite: 
       return true;
     }
     return false;
   }
   
   private static boolean checkOperand(OperandType operandType, MethodReference method)
   {
     VerifyArgument.notNull(method, "method");
     
     switch (operandType) {
     case MethodReference: 
       return true;
     }
     return false;
   }
   
   private static boolean checkOperand(OperandType operandType, FieldReference field)
   {
     VerifyArgument.notNull(field, "field");
     
     switch (operandType) {
     case FieldReference: 
       return true;
     }
     return false;
   }
   
 
 
 
 
   public void accept(InstructionVisitor visitor)
   {
     if (hasLabel()) {
       visitor.visitLabel(this._label);
     }
     
     switch (this._opCode.getOperandType()) {
     case None: 
       visitor.visit(this._opCode);
       break;
     
     case PrimitiveTypeCode: 
     case TypeReference: 
     case TypeReferenceU1: 
       visitor.visitType(this._opCode, (TypeReference)getOperand(0));
       break;
     
     case DynamicCallSite: 
       visitor.visitDynamicCallSite(this._opCode, (DynamicCallSite)this._operand);
       break;
     
     case MethodReference: 
       visitor.visitMethod(this._opCode, (MethodReference)this._operand);
       break;
     
     case FieldReference: 
       visitor.visitField(this._opCode, (FieldReference)this._operand);
       break;
     
     case BranchTarget: 
     case BranchTargetWide: 
       visitor.visitBranch(this._opCode, (Instruction)this._operand);
       break;
     
     case I1: 
     case I2: 
       visitor.visitConstant(this._opCode, ((Number)this._operand).intValue());
       break;
     
     case I8: 
       visitor.visitConstant(this._opCode, ((Number)this._operand).longValue());
       break;
     
     case Constant: 
     case WideConstant: 
       if ((this._operand instanceof String)) {
         visitor.visitConstant(this._opCode, (String)this._operand);
       }
       else if ((this._operand instanceof TypeReference)) {
         visitor.visitConstant(this._opCode, (TypeReference)this._operand);
       }
       else {
         Number number = (Number)this._operand;
         
         if ((this._operand instanceof Long)) {
           visitor.visitConstant(this._opCode, number.longValue());
         }
         else if ((this._operand instanceof Float)) {
           visitor.visitConstant(this._opCode, number.floatValue());
         }
         else if ((this._operand instanceof Double)) {
           visitor.visitConstant(this._opCode, number.doubleValue());
         }
         else {
           visitor.visitConstant(this._opCode, number.intValue());
         }
       }
       break;
     
     case Switch: 
       visitor.visitSwitch(this._opCode, (SwitchInfo)this._operand);
       break;
     
     case Local: 
       visitor.visitVariable(this._opCode, (VariableReference)this._operand);
       break;
     
     case LocalI1: 
     case LocalI2: 
       visitor.visitVariable(this._opCode, (VariableReference)getOperand(0), ((Number)getOperand(1)).intValue());
     }
     
   }
   
 
 
 
 
   public final int compareTo(Instruction o)
   {
     if (o == null) {
       return 1;
     }
     
     return Integer.compare(this._offset, o._offset);
   }
 }


