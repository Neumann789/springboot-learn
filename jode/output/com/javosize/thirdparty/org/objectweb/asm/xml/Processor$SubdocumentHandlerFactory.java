/* Processor$SubdocumentHandlerFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.xml.sax.ContentHandler;

final class Processor$SubdocumentHandlerFactory
    implements Processor$ContentHandlerFactory
{
    private final ContentHandler subdocumentHandler;
    
    Processor$SubdocumentHandlerFactory(ContentHandler contenthandler) {
	subdocumentHandler = contenthandler;
    }
    
    public final ContentHandler createContentHandler() {
	return subdocumentHandler;
    }
}
