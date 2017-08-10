package com.javosize.thirdparty.org.objectweb.asm.xml;

import com.javosize.thirdparty.org.xml.sax.Attributes;
import java.util.ArrayList;
import java.util.HashMap;

final class ASMContentHandler$ExceptionRule
  extends ASMContentHandler.Rule
{
  final ASMContentHandler this$0;
  
  ASMContentHandler$ExceptionRule(ASMContentHandler paramASMContentHandler)
  {
    super(paramASMContentHandler);
  }
  
  public final void begin(String paramString, Attributes paramAttributes)
  {
    ((ArrayList)((HashMap)this.this$0.peek()).get("exceptions")).add(paramAttributes.getValue("name"));
  }
}


