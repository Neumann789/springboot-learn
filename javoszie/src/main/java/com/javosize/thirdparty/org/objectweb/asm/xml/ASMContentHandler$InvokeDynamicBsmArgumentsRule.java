package com.javosize.thirdparty.org.objectweb.asm.xml;

import com.javosize.thirdparty.org.xml.sax.Attributes;
import com.javosize.thirdparty.org.xml.sax.SAXException;
import java.util.ArrayList;

final class ASMContentHandler$InvokeDynamicBsmArgumentsRule
  extends ASMContentHandler.Rule
{
  final ASMContentHandler this$0;
  
  ASMContentHandler$InvokeDynamicBsmArgumentsRule(ASMContentHandler paramASMContentHandler)
  {
    super(paramASMContentHandler);
  }
  
  public final void begin(String paramString, Attributes paramAttributes)
    throws SAXException
  {
    ArrayList localArrayList = (ArrayList)this.this$0.peek();
    localArrayList.add(getValue(paramAttributes.getValue("desc"), paramAttributes.getValue("cst")));
  }
}


