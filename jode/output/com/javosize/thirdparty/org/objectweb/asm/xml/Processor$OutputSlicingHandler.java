/* Processor$OutputSlicingHandler - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.xml.sax.Attributes;
import com.javosize.thirdparty.org.xml.sax.ContentHandler;
import com.javosize.thirdparty.org.xml.sax.SAXException;
import com.javosize.thirdparty.org.xml.sax.helpers.DefaultHandler;

final class Processor$OutputSlicingHandler extends DefaultHandler
{
    private final String subdocumentRoot;
    private Processor$ContentHandlerFactory subdocumentHandlerFactory;
    private final Processor$EntryElement entryElement;
    private boolean isXml;
    private boolean subdocument = false;
    private ContentHandler subdocumentHandler;
    
    Processor$OutputSlicingHandler
	(Processor$ContentHandlerFactory contenthandlerfactory,
	 Processor$EntryElement entryelement, boolean bool) {
	subdocumentRoot = "class";
	subdocumentHandlerFactory = contenthandlerfactory;
	entryElement = entryelement;
	isXml = bool;
    }
    
    public final void startElement(String string, String string_0_,
				   String string_1_, Attributes attributes)
	throws SAXException {
    label_749:
	{
	label_748:
	    {
		if (!subdocument) {
		    if (!string_0_.equals(subdocumentRoot))
			return;
		    String string_2_ = attributes.getValue("name");
		    if (string_2_ != null && string_2_.length() != 0) {
		    label_747:
			{
			    try {
				PUSH entryElement;
				if (!isXml)
				    break label_747;
			    } catch (java.io.IOException PUSH) {
				break label_749;
			    }
			}
			try {
			    PUSH string_2_ + ".class";
			} catch (java.io.IOException PUSH) {
			    break label_749;
			}
			try {
			    PUSH string_2_ + ".class.xml";
			} catch (java.io.IOException PUSH) {
			    break label_749;
			}
		    } else
			throw new SAXException
				  ("Class element without name attribute.");
		} else {
		    subdocumentHandler.startElement(string, string_0_,
						    string_1_, attributes);
		    return;
		}
	    }
	    try {
		((Processor$EntryElement) POP).openEntry(POP);
		subdocumentHandler
		    = subdocumentHandlerFactory.createContentHandler();
		subdocumentHandler.startDocument();
		subdocumentHandler.startElement(string, string_0_, string_1_,
						attributes);
		subdocument = true;
	    } catch (java.io.IOException PUSH) {
		/* empty */
	    }
	}
	java.io.IOException ioexception = POP;
	throw new SAXException(ioexception.toString(), ioexception);
	break label_748;
    }
    
    public final void endElement(String string, String string_3_,
				 String string_4_)
	throws SAXException {
	if (subdocument) {
	    subdocumentHandler.endElement(string, string_3_, string_4_);
	    if (string_3_.equals(subdocumentRoot)) {
		subdocumentHandler.endDocument();
		subdocument = false;
		try {
		    entryElement.closeEntry();
		} catch (java.io.IOException PUSH) {
		    java.io.IOException ioexception = POP;
		    throw new SAXException(ioexception.toString(),
					   ioexception);
		}
	    }
	}
	return;
    }
    
    public final void startDocument() throws SAXException {
	/* empty */
    }
    
    public final void endDocument() throws SAXException {
	/* empty */
    }
    
    public final void characters(char[] cs, int i, int i_5_)
	throws SAXException {
	if (subdocument)
	    subdocumentHandler.characters(cs, i, i_5_);
	return;
    }
}
