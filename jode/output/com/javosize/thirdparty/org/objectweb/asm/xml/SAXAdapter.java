/* SAXAdapter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.xml.sax.Attributes;
import com.javosize.thirdparty.org.xml.sax.ContentHandler;

public class SAXAdapter
{
    private final ContentHandler h;
    
    protected SAXAdapter(ContentHandler contenthandler) {
	h = contenthandler;
    }
    
    protected ContentHandler getContentHandler() {
	return h;
    }
    
    protected void addDocumentStart() {
	try {
	    h.startDocument();
	} catch (com.javosize.thirdparty.org.xml.sax.SAXException PUSH) {
	    com.javosize.thirdparty.org.xml.sax.SAXException saxexception
		= POP;
	    throw new RuntimeException(saxexception.getMessage(),
				       saxexception.getException());
	}
    }
    
    protected void addDocumentEnd() {
	try {
	    h.endDocument();
	} catch (com.javosize.thirdparty.org.xml.sax.SAXException PUSH) {
	    com.javosize.thirdparty.org.xml.sax.SAXException saxexception
		= POP;
	    throw new RuntimeException(saxexception.getMessage(),
				       saxexception.getException());
	}
    }
    
    protected final void addStart(String string, Attributes attributes) {
	try {
	    h.startElement("", string, string, attributes);
	} catch (com.javosize.thirdparty.org.xml.sax.SAXException PUSH) {
	    com.javosize.thirdparty.org.xml.sax.SAXException saxexception
		= POP;
	    throw new RuntimeException(saxexception.getMessage(),
				       saxexception.getException());
	}
    }
    
    protected final void addEnd(String string) {
	try {
	    h.endElement("", string, string);
	} catch (com.javosize.thirdparty.org.xml.sax.SAXException PUSH) {
	    com.javosize.thirdparty.org.xml.sax.SAXException saxexception
		= POP;
	    throw new RuntimeException(saxexception.getMessage(),
				       saxexception.getException());
	}
    }
    
    protected final void addElement(String string, Attributes attributes) {
	addStart(string, attributes);
	addEnd(string);
    }
}
