/* Processor$InputSlicingHandler - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.xml.sax.Attributes;
import com.javosize.thirdparty.org.xml.sax.ContentHandler;
import com.javosize.thirdparty.org.xml.sax.SAXException;
import com.javosize.thirdparty.org.xml.sax.helpers.DefaultHandler;

final class Processor$InputSlicingHandler extends DefaultHandler
{
    private String subdocumentRoot;
    private final ContentHandler rootHandler;
    private Processor$ContentHandlerFactory subdocumentHandlerFactory;
    private boolean subdocument = false;
    private ContentHandler subdocumentHandler;
    
    Processor$InputSlicingHandler
	(String string, ContentHandler contenthandler,
	 Processor$ContentHandlerFactory contenthandlerfactory) {
	subdocumentRoot = string;
	rootHandler = contenthandler;
	subdocumentHandlerFactory = contenthandlerfactory;
    }
    
    public final void startElement(String string, String string_0_,
				   String string_1_, Attributes attributes)
	throws SAXException {
	if (!subdocument) {
	    if (!string_0_.equals(subdocumentRoot)) {
		if (rootHandler != null)
		    rootHandler.startElement(string, string_0_, string_1_,
					     attributes);
	    } else {
		subdocumentHandler
		    = subdocumentHandlerFactory.createContentHandler();
		subdocumentHandler.startDocument();
		subdocumentHandler.startElement(string, string_0_, string_1_,
						attributes);
		subdocument = true;
	    }
	} else
	    subdocumentHandler.startElement(string, string_0_, string_1_,
					    attributes);
	return;
    }
    
    public final void endElement(String string, String string_2_,
				 String string_3_)
	throws SAXException {
	if (!subdocument) {
	    if (rootHandler != null)
		rootHandler.endElement(string, string_2_, string_3_);
	} else {
	    subdocumentHandler.endElement(string, string_2_, string_3_);
	    if (string_2_.equals(subdocumentRoot)) {
		subdocumentHandler.endDocument();
		subdocument = false;
	    }
	}
	return;
    }
    
    public final void startDocument() throws SAXException {
	if (rootHandler != null)
	    rootHandler.startDocument();
	return;
    }
    
    public final void endDocument() throws SAXException {
	if (rootHandler != null)
	    rootHandler.endDocument();
	return;
    }
    
    public final void characters(char[] cs, int i, int i_4_)
	throws SAXException {
	if (!subdocument) {
	    if (rootHandler != null)
		rootHandler.characters(cs, i, i_4_);
	} else
	    subdocumentHandler.characters(cs, i, i_4_);
	return;
    }
}
