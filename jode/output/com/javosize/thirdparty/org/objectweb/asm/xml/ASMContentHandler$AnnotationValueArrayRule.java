/* ASMContentHandler$AnnotationValueArrayRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$AnnotationValueArrayRule
    extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$AnnotationValueArrayRule
	(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public void begin(String string, Attributes attributes) {
	AnnotationVisitor annotationvisitor
	    = (AnnotationVisitor) this$0.peek();
    label_721:
	{
	    PUSH this$0;
	    if (annotationvisitor != null)
		PUSH annotationvisitor.visitArray(attributes.getValue("name"));
	    else
		PUSH null;
	    break label_721;
	}
	((ASMContentHandler) POP).push(POP);
    }
    
    public void end(String string) {
	AnnotationVisitor annotationvisitor = (AnnotationVisitor) this$0.pop();
	if (annotationvisitor != null)
	    annotationvisitor.visitEnd();
	return;
    }
}
