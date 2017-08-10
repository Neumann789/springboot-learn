package com.javosize.thirdparty.org.objectweb.asm.xml;

import com.javosize.thirdparty.org.xml.sax.Attributes;
import java.util.ArrayList;
import java.util.HashMap;

final class ASMContentHandler$TableSwitchLabelRule
  extends ASMContentHandler.Rule
{
  final ASMContentHandler this$0;
  
  ASMContentHandler$TableSwitchLabelRule(ASMContentHandler paramASMContentHandler)
  {
    super(paramASMContentHandler);
  }
  
  public final void begin(String paramString, Attributes paramAttributes)
  {
    ((ArrayList)((HashMap)this.this$0.peek()).get("labels")).add(getLabel(paramAttributes.getValue("name")));
  }
}


