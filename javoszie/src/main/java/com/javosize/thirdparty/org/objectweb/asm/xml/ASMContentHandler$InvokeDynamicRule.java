package com.javosize.thirdparty.org.objectweb.asm.xml;

import com.javosize.thirdparty.org.objectweb.asm.Handle;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.xml.sax.Attributes;
import com.javosize.thirdparty.org.xml.sax.SAXException;
import java.util.ArrayList;

final class ASMContentHandler$InvokeDynamicRule
  extends ASMContentHandler.Rule
{
  final ASMContentHandler this$0;
  
  ASMContentHandler$InvokeDynamicRule(ASMContentHandler paramASMContentHandler)
  {
    super(paramASMContentHandler);
  }
  
  public final void begin(String paramString, Attributes paramAttributes)
    throws SAXException
  {
    this.this$0.push(paramAttributes.getValue("name"));
    this.this$0.push(paramAttributes.getValue("desc"));
    this.this$0.push(decodeHandle(paramAttributes.getValue("bsm")));
    this.this$0.push(new ArrayList());
  }
  
  public final void end(String paramString)
  {
    ArrayList localArrayList = (ArrayList)this.this$0.pop();
    Handle localHandle = (Handle)this.this$0.pop();
    String str1 = (String)this.this$0.pop();
    String str2 = (String)this.this$0.pop();
    getCodeVisitor().visitInvokeDynamicInsn(str2, str1, localHandle, localArrayList.toArray());
  }
}


