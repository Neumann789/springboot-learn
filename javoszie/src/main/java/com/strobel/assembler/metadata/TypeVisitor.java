package com.strobel.assembler.metadata;

import com.strobel.assembler.ir.ConstantPool.Visitor;
import com.strobel.assembler.ir.attributes.SourceAttribute;
import com.strobel.assembler.metadata.annotations.CustomAnnotation;

public abstract interface TypeVisitor
{
  public abstract void visitParser(MetadataParser paramMetadataParser);
  
  public abstract void visit(int paramInt1, int paramInt2, long paramLong, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString);
  
  public abstract void visitDeclaringMethod(MethodReference paramMethodReference);
  
  public abstract void visitOuterType(TypeReference paramTypeReference);
  
  public abstract void visitInnerType(TypeDefinition paramTypeDefinition);
  
  public abstract void visitAttribute(SourceAttribute paramSourceAttribute);
  
  public abstract void visitAnnotation(CustomAnnotation paramCustomAnnotation, boolean paramBoolean);
  
  public abstract FieldVisitor visitField(long paramLong, String paramString, TypeReference paramTypeReference);
  
  public abstract MethodVisitor visitMethod(long paramLong, String paramString, IMethodSignature paramIMethodSignature, TypeReference... paramVarArgs);
  
  public abstract ConstantPool.Visitor visitConstantPool();
  
  public abstract void visitEnd();
}


