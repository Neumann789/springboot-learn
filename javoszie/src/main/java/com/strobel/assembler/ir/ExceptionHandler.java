 package com.strobel.assembler.ir;
 
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilerHelpers;
 import com.strobel.decompiler.PlainTextOutput;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ExceptionHandler
   implements Comparable<ExceptionHandler>
 {
   private final InstructionBlock _tryBlock;
   private final InstructionBlock _handlerBlock;
   private final ExceptionHandlerType _handlerType;
   private final TypeReference _catchType;
   
   private ExceptionHandler(InstructionBlock tryBlock, InstructionBlock handlerBlock, ExceptionHandlerType handlerType, TypeReference catchType)
   {
     this._tryBlock = tryBlock;
     this._handlerBlock = handlerBlock;
     this._handlerType = handlerType;
     this._catchType = catchType;
   }
   
 
 
 
   public static ExceptionHandler createCatch(InstructionBlock tryBlock, InstructionBlock handlerBlock, TypeReference catchType)
   {
     VerifyArgument.notNull(tryBlock, "tryBlock");
     VerifyArgument.notNull(handlerBlock, "handlerBlock");
     VerifyArgument.notNull(catchType, "catchType");
     
     return new ExceptionHandler(tryBlock, handlerBlock, ExceptionHandlerType.Catch, catchType);
   }
   
 
 
 
 
 
 
 
   public static ExceptionHandler createFinally(InstructionBlock tryBlock, InstructionBlock handlerBlock)
   {
     VerifyArgument.notNull(tryBlock, "tryBlock");
     VerifyArgument.notNull(handlerBlock, "handlerBlock");
     
     return new ExceptionHandler(tryBlock, handlerBlock, ExceptionHandlerType.Finally, null);
   }
   
 
 
 
 
   public final boolean isFinally()
   {
     return this._handlerType == ExceptionHandlerType.Finally;
   }
   
   public final boolean isCatch() {
     return this._handlerType == ExceptionHandlerType.Catch;
   }
   
   public final InstructionBlock getTryBlock() {
     return this._tryBlock;
   }
   
   public final InstructionBlock getHandlerBlock() {
     return this._handlerBlock;
   }
   
   public final ExceptionHandlerType getHandlerType() {
     return this._handlerType;
   }
   
   public final TypeReference getCatchType() {
     return this._catchType;
   }
   
   public final String toString()
   {
     PlainTextOutput output = new PlainTextOutput();
     DecompilerHelpers.writeExceptionHandler(output, this);
     return output.toString();
   }
   
   public int compareTo(ExceptionHandler o)
   {
     if (o == null) {
       return 1;
     }
     
 
 
     InstructionBlock h1 = this._handlerBlock;
     InstructionBlock h2 = o._handlerBlock;
     
     int result = h1.getFirstInstruction().compareTo(h2.getFirstInstruction());
     
     if (result != 0) {
       return result;
     }
     
     InstructionBlock t1 = this._tryBlock;
     InstructionBlock t2 = o._tryBlock;
     
     result = t1.getFirstInstruction().compareTo(t2.getFirstInstruction());
     
     if (result != 0) {
       return result;
     }
     
     result = t2.getLastInstruction().compareTo(t1.getLastInstruction());
     
     if (result != 0) {
       return result;
     }
     
     return h2.getLastInstruction().compareTo(h1.getLastInstruction());
   }
 }


