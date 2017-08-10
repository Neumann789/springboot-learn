package com.javosize.thirdparty.org.objectweb.asm.xml;

import com.javosize.thirdparty.org.xml.sax.Attributes;
import java.util.ArrayList;
import java.util.HashMap;

final class ASMContentHandler$LookupSwitchLabelRule
  extends ASMContentHandler.Rule
{
  final ASMContentHandler this$0;
  
  ASMContentHandler$LookupSwitchLabelRule(ASMContentHandler paramASMContentHandler)
  {
    super(paramASMContentHandler);
  }
  
  public final void begin(String paramString, Attributes paramAttributes)
  {
    HashMap localHashMap = (HashMap)this.this$0.peek();
    ((ArrayList)localHashMap.get("labels")).add(getLabel(paramAttributes.getValue("name")));
    ((ArrayList)localHashMap.get("keys")).add(paramAttributes.getValue("key"));
  }
}


