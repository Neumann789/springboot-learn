package com.javosize.thirdparty.org.objectweb.asm.xml;

import com.javosize.thirdparty.org.objectweb.asm.ClassVisitor;
import com.javosize.thirdparty.org.objectweb.asm.ClassWriter;
import com.javosize.thirdparty.org.xml.sax.SAXException;
import java.io.IOException;
import java.io.OutputStream;

class Processor$ASMContentHandlerFactory$1
  extends ASMContentHandler
{
  final Processor.ASMContentHandlerFactory this$0;
  
  Processor$ASMContentHandlerFactory$1(Processor.ASMContentHandlerFactory paramASMContentHandlerFactory, ClassVisitor paramClassVisitor, ClassWriter paramClassWriter)
  {
    super(paramClassVisitor);
  }
  
  public void endDocument()
    throws SAXException
  {
    try
    {
      this.this$0.os.write(this.val$cw.toByteArray());
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
}


