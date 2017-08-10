package com.javosize.thirdparty.org.objectweb.asm.xml;

import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$AnnotationParameterRule
  extends ASMContentHandler.Rule
{
  final ASMContentHandler this$0;
  
  ASMContentHandler$AnnotationParameterRule(ASMContentHandler paramASMContentHandler)
  {
    super(paramASMContentHandler);
  }
  
  public void begin(String paramString, Attributes paramAttributes)
  {
    int i = Integer.parseInt(paramAttributes.getValue("parameter"));
    String str = paramAttributes.getValue("desc");
    boolean bool = Boolean.valueOf(paramAttributes.getValue("visible")).booleanValue();
    this.this$0.push(((MethodVisitor)this.this$0.peek()).visitParameterAnnotation(i, str, bool));
  }
  
  public void end(String paramString)
  {
    AnnotationVisitor localAnnotationVisitor = (AnnotationVisitor)this.this$0.pop();
    if (localAnnotationVisitor != null) {
      localAnnotationVisitor.visitEnd();
    }
  }
}


