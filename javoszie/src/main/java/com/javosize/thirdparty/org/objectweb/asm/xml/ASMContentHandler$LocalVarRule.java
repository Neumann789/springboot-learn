package com.javosize.thirdparty.org.objectweb.asm.xml;

import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$LocalVarRule
  extends ASMContentHandler.Rule
{
  final ASMContentHandler this$0;
  
  ASMContentHandler$LocalVarRule(ASMContentHandler paramASMContentHandler)
  {
    super(paramASMContentHandler);
  }
  
  public final void begin(String paramString, Attributes paramAttributes)
  {
    String str1 = paramAttributes.getValue("name");
    String str2 = paramAttributes.getValue("desc");
    String str3 = paramAttributes.getValue("signature");
    Label localLabel1 = getLabel(paramAttributes.getValue("start"));
    Label localLabel2 = getLabel(paramAttributes.getValue("end"));
    int i = Integer.parseInt(paramAttributes.getValue("var"));
    getCodeVisitor().visitLocalVariable(str1, str2, str3, localLabel1, localLabel2, i);
  }
}


