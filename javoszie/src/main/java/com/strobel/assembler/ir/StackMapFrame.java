 package com.strobel.assembler.ir;
 
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class StackMapFrame
 {
   private final Frame _frame;
   private final Instruction _startInstruction;
   
   public StackMapFrame(Frame frame, Instruction startInstruction)
   {
     this._frame = ((Frame)VerifyArgument.notNull(frame, "frame"));
     this._startInstruction = ((Instruction)VerifyArgument.notNull(startInstruction, "startInstruction"));
   }
   
   public final Frame getFrame() {
     return this._frame;
   }
   
   public final Instruction getStartInstruction() {
     return this._startInstruction;
   }
   
   public final String toString()
   {
     return String.format("#%1$04d: %2$s", new Object[] { Integer.valueOf(this._startInstruction.getOffset()), this._frame });
   }
 }


