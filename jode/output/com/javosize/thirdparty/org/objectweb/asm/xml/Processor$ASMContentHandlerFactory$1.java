/* Processor$ASMContentHandlerFactory$1 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.objectweb.asm.ClassVisitor;
import com.javosize.thirdparty.org.objectweb.asm.ClassWriter;
import com.javosize.thirdparty.org.xml.sax.SAXException;

class Processor$ASMContentHandlerFactory$1 extends ASMContentHandler
{
    /*synthetic*/ final ClassWriter val$cw;
    /*synthetic*/ final Processor$ASMContentHandlerFactory this$0;
    
    Processor$ASMContentHandlerFactory$1
	(Processor$ASMContentHandlerFactory asmcontenthandlerfactory,
	 ClassVisitor classvisitor, ClassWriter classwriter) {
	this$0 = asmcontenthandlerfactory;
	val$cw = classwriter;
	super(classvisitor);
    }
    
    public void endDocument() throws SAXException {
	try {
	    this$0.os.write(val$cw.toByteArray());
	} catch (java.io.IOException PUSH) {
	    Exception exception = POP;
	    throw new SAXException(exception);
	}
    }
}
