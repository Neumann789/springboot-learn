package com.javosize.thirdparty.org.objectweb.asm.xml;

import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$TryCatchAnnotationRule
  extends ASMContentHandler.Rule
{
  final ASMContentHandler this$0;
  
  ASMContentHandler$TryCatchAnnotationRule(ASMContentHandler paramASMContentHandler)
  {
    super(paramASMContentHandler);
  }
  
  public void begin(String paramString, Attributes paramAttributes)
  {
    String str = paramAttributes.getValue("desc");
    boolean bool = Boolean.valueOf(paramAttributes.getValue("visible")).booleanValue();
    int i = Integer.parseInt(paramAttributes.getValue("typeRef"));
    TypePath localTypePath = TypePath.fromString(paramAttributes.getValue("typePath"));
    this.this$0.push(((MethodVisitor)this.this$0.peek()).visitTryCatchAnnotation(i, localTypePath, str, bool));
  }
  
  public void end(String paramString)
  {
    AnnotationVisitor localAnnotationVisitor = (AnnotationVisitor)this.this$0.pop();
    if (localAnnotationVisitor != null) {
      localAnnotationVisitor.visitEnd();
    }
  }
}


