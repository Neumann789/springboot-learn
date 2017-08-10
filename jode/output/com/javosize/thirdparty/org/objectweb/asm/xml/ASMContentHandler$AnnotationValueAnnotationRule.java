/* ASMContentHandler$AnnotationValueAnnotationRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$AnnotationValueAnnotationRule
    extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$AnnotationValueAnnotationRule
	(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public void begin(String string, Attributes attributes) {
	AnnotationVisitor annotationvisitor
	    = (AnnotationVisitor) this$0.peek();
    label_720:
	{
	    PUSH this$0;
	    if (annotationvisitor != null)
		PUSH annotationvisitor.visitAnnotation(attributes
							   .getValue("name"),
						       attributes
							   .getValue("desc"));
	    else
		PUSH null;
	    break label_720;
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
