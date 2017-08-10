 package com.strobel.assembler.ir;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public enum OperandType
 {
   None(0), 
   
 
 
   PrimitiveTypeCode(1), 
   
 
 
   TypeReference(2), 
   
 
 
   TypeReferenceU1(3), 
   
 
 
   DynamicCallSite(4), 
   
 
 
   MethodReference(2), 
   
 
 
   FieldReference(2), 
   
 
 
   BranchTarget(2), 
   
 
 
   BranchTargetWide(4), 
   
 
 
   I1(1), 
   
 
 
   I2(2), 
   
 
 
   I8(8), 
   
 
 
   Constant(1), 
   
 
 
   WideConstant(2), 
   
 
 
 
   Switch(-1), 
   
 
 
   Local(1), 
   
 
 
 
   LocalI1(2), 
   
 
 
 
   LocalI2(4);
   
   private final int size;
   
   private OperandType(int size) {
     this.size = size;
   }
   
   public final int getBaseSize() {
     return this.size;
   }
 }


