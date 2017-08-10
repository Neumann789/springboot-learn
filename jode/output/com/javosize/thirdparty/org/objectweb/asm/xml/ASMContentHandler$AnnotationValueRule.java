/* ASMContentHandler$AnnotationValueRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.xml.sax.Attributes;
import com.javosize.thirdparty.org.xml.sax.SAXException;

final class ASMContentHandler$AnnotationValueRule
    extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$AnnotationValueRule
	(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public void begin(String string, Attributes attributes)
	throws SAXException {
	AnnotationVisitor annotationvisitor
	    = (AnnotationVisitor) this$0.peek();
	if (annotationvisitor != null)
	    annotationvisitor.visit(attributes.getValue("name"),
				    getValue(attributes.getValue("desc"),
					     attributes.getValue("value")));
	return;
    }
}
