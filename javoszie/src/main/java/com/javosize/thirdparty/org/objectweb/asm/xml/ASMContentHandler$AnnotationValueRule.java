package com.javosize.thirdparty.org.objectweb.asm.xml;

import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.xml.sax.Attributes;
import com.javosize.thirdparty.org.xml.sax.SAXException;

final class ASMContentHandler$AnnotationValueRule
  extends ASMContentHandler.Rule
{
  final ASMContentHandler this$0;
  
  ASMContentHandler$AnnotationValueRule(ASMContentHandler paramASMContentHandler)
  {
    super(paramASMContentHandler);
  }
  
  public void begin(String paramString, Attributes paramAttributes)
    throws SAXException
  {
    AnnotationVisitor localAnnotationVisitor = (AnnotationVisitor)this.this$0.peek();
    if (localAnnotationVisitor != null) {
      localAnnotationVisitor.visit(paramAttributes.getValue("name"), getValue(paramAttributes.getValue("desc"), paramAttributes.getValue("value")));
    }
  }
}


