package com.javosize.thirdparty.org.objectweb.asm.xml;

import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$AnnotationValueEnumRule
  extends ASMContentHandler.Rule
{
  final ASMContentHandler this$0;
  
  ASMContentHandler$AnnotationValueEnumRule(ASMContentHandler paramASMContentHandler)
  {
    super(paramASMContentHandler);
  }
  
  public void begin(String paramString, Attributes paramAttributes)
  {
    AnnotationVisitor localAnnotationVisitor = (AnnotationVisitor)this.this$0.peek();
    if (localAnnotationVisitor != null) {
      localAnnotationVisitor.visitEnum(paramAttributes.getValue("name"), paramAttributes.getValue("desc"), paramAttributes.getValue("value"));
    }
  }
}


