package com.javosize.thirdparty.org.objectweb.asm.xml;

import com.javosize.thirdparty.org.objectweb.asm.ClassVisitor;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$SourceRule
  extends ASMContentHandler.Rule
{
  final ASMContentHandler this$0;
  
  ASMContentHandler$SourceRule(ASMContentHandler paramASMContentHandler)
  {
    super(paramASMContentHandler);
  }
  
  public void begin(String paramString, Attributes paramAttributes)
  {
    String str1 = paramAttributes.getValue("file");
    String str2 = paramAttributes.getValue("debug");
    this.this$0.cv.visitSource(str1, str2);
  }
}


