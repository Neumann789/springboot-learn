package com.javosize.thirdparty.org.objectweb.asm.xml;

import com.javosize.thirdparty.org.objectweb.asm.ClassWriter;
import com.javosize.thirdparty.org.xml.sax.ContentHandler;
import java.io.OutputStream;

final class Processor$ASMContentHandlerFactory
  implements Processor.ContentHandlerFactory
{
  final OutputStream os;
  
  Processor$ASMContentHandlerFactory(OutputStream paramOutputStream)
  {
    this.os = paramOutputStream;
  }
  
  public final ContentHandler createContentHandler()
  {
    ClassWriter localClassWriter = new ClassWriter(1);
    return new Processor.ASMContentHandlerFactory.1(this, localClassWriter, localClassWriter);
  }
}


