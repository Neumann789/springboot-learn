/* Processor$TransformerHandlerFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import javax.xml.transform.Templates;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import com.javosize.thirdparty.org.xml.sax.ContentHandler;

final class Processor$TransformerHandlerFactory
    implements Processor$ContentHandlerFactory
{
    private SAXTransformerFactory saxtf;
    private final Templates templates;
    private ContentHandler outputHandler;
    
    Processor$TransformerHandlerFactory
	(SAXTransformerFactory saxtransformerfactory, Templates templates,
	 ContentHandler contenthandler) {
	saxtf = saxtransformerfactory;
	this.templates = templates;
	outputHandler = contenthandler;
    }
    
    public final ContentHandler createContentHandler() {
	try {
	    TransformerHandler transformerhandler
		= saxtf.newTransformerHandler(templates);
	    transformerhandler.setResult(new SAXResult(outputHandler));
	    return transformerhandler;
	} catch (javax.xml.transform.TransformerConfigurationException PUSH) {
	    javax.xml.transform.TransformerConfigurationException transformerconfigurationexception
		= POP;
	    throw new RuntimeException(transformerconfigurationexception
					   .toString());
	}
    }
}
