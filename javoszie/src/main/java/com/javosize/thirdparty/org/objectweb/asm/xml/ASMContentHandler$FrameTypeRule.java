package com.javosize.thirdparty.org.objectweb.asm.xml;

import com.javosize.thirdparty.org.xml.sax.Attributes;
import java.util.ArrayList;
import java.util.HashMap;

final class ASMContentHandler$FrameTypeRule
  extends ASMContentHandler.Rule
{
  final ASMContentHandler this$0;
  
  ASMContentHandler$FrameTypeRule(ASMContentHandler paramASMContentHandler)
  {
    super(paramASMContentHandler);
  }
  
  public void begin(String paramString, Attributes paramAttributes)
  {
    ArrayList localArrayList = (ArrayList)((HashMap)this.this$0.peek()).get(paramString);
    String str = paramAttributes.getValue("type");
    if ("uninitialized".equals(str))
    {
      localArrayList.add(getLabel(paramAttributes.getValue("label")));
    }
    else
    {
      Integer localInteger = (Integer)ASMContentHandler.TYPES.get(str);
      if (localInteger == null) {
        localArrayList.add(str);
      } else {
        localArrayList.add(localInteger);
      }
    }
  }
}


