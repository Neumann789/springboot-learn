/* Processor$SAXWriterFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.io.Writer;

import com.javosize.thirdparty.org.xml.sax.ContentHandler;

final class Processor$SAXWriterFactory
    implements Processor$ContentHandlerFactory
{
    private final Writer w;
    private final boolean optimizeEmptyElements;
    
    Processor$SAXWriterFactory(Writer writer, boolean bool) {
	w = writer;
	optimizeEmptyElements = bool;
    }
    
    public final ContentHandler createContentHandler() {
	return new Processor$SAXWriter(w, optimizeEmptyElements);
    }
}
