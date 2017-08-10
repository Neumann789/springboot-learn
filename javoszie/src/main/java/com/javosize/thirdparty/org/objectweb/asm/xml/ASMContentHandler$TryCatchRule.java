package com.javosize.thirdparty.org.objectweb.asm.xml;

import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$TryCatchRule
  extends ASMContentHandler.Rule
{
  final ASMContentHandler this$0;
  
  ASMContentHandler$TryCatchRule(ASMContentHandler paramASMContentHandler)
  {
    super(paramASMContentHandler);
  }
  
  public final void begin(String paramString, Attributes paramAttributes)
  {
    Label localLabel1 = getLabel(paramAttributes.getValue("start"));
    Label localLabel2 = getLabel(paramAttributes.getValue("end"));
    Label localLabel3 = getLabel(paramAttributes.getValue("handler"));
    String str = paramAttributes.getValue("type");
    getCodeVisitor().visitTryCatchBlock(localLabel1, localLabel2, localLabel3, str);
  }
}


