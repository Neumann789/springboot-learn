/* Processor$ASMContentHandlerFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.io.OutputStream;

import com.javosize.thirdparty.org.objectweb.asm.ClassWriter;
import com.javosize.thirdparty.org.xml.sax.ContentHandler;

final class Processor$ASMContentHandlerFactory
    implements Processor$ContentHandlerFactory
{
    final OutputStream os;
    
    Processor$ASMContentHandlerFactory(OutputStream outputstream) {
	os = outputstream;
    }
    
    public final ContentHandler createContentHandler() {
	ClassWriter classwriter = new ClassWriter(1);
	return new Processor$ASMContentHandlerFactory$1(this, classwriter,
							classwriter);
    }
}
