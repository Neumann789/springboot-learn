package com.strobel.assembler.metadata;

import com.strobel.assembler.ir.Frame;
import com.strobel.assembler.ir.Instruction;
import com.strobel.assembler.ir.InstructionVisitor;
import com.strobel.assembler.ir.attributes.SourceAttribute;
import com.strobel.assembler.metadata.annotations.CustomAnnotation;

public abstract interface MethodVisitor
{
  public abstract boolean canVisitBody();
  
  public abstract InstructionVisitor visitBody(MethodBody paramMethodBody);
  
  public abstract void visitEnd();
  
  public abstract void visitFrame(Frame paramFrame);
  
  public abstract void visitLineNumber(Instruction paramInstruction, int paramInt);
  
  public abstract void visitAttribute(SourceAttribute paramSourceAttribute);
  
  public abstract void visitAnnotation(CustomAnnotation paramCustomAnnotation, boolean paramBoolean);
  
  public abstract void visitParameterAnnotation(int paramInt, CustomAnnotation paramCustomAnnotation, boolean paramBoolean);
}


