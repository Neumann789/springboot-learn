/* ASMContentHandler$AnnotationValueEnumRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$AnnotationValueEnumRule
    extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$AnnotationValueEnumRule
	(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public void begin(String string, Attributes attributes) {
	AnnotationVisitor annotationvisitor
	    = (AnnotationVisitor) this$0.peek();
	if (annotationvisitor != null)
	    annotationvisitor.visitEnum(attributes.getValue("name"),
					attributes.getValue("desc"),
					attributes.getValue("value"));
	return;
    }
}
