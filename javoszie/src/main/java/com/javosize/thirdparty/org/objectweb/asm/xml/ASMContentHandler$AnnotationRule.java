package com.javosize.thirdparty.org.objectweb.asm.xml;

import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.ClassVisitor;
import com.javosize.thirdparty.org.objectweb.asm.FieldVisitor;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$AnnotationRule
  extends ASMContentHandler.Rule
{
  final ASMContentHandler this$0;
  
  ASMContentHandler$AnnotationRule(ASMContentHandler paramASMContentHandler)
  {
    super(paramASMContentHandler);
  }
  
  public void begin(String paramString, Attributes paramAttributes)
  {
    String str = paramAttributes.getValue("desc");
    boolean bool = Boolean.valueOf(paramAttributes.getValue("visible")).booleanValue();
    Object localObject = this.this$0.peek();
    if ((localObject instanceof ClassVisitor)) {
      this.this$0.push(((ClassVisitor)localObject).visitAnnotation(str, bool));
    } else if ((localObject instanceof FieldVisitor)) {
      this.this$0.push(((FieldVisitor)localObject).visitAnnotation(str, bool));
    } else if ((localObject instanceof MethodVisitor)) {
      this.this$0.push(((MethodVisitor)localObject).visitAnnotation(str, bool));
    }
  }
  
  public void end(String paramString)
  {
    AnnotationVisitor localAnnotationVisitor = (AnnotationVisitor)this.this$0.pop();
    if (localAnnotationVisitor != null) {
      localAnnotationVisitor.visitEnd();
    }
  }
}


