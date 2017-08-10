/* ASMContentHandler$AnnotationParameterRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$AnnotationParameterRule
    extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$AnnotationParameterRule
	(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public void begin(String string, Attributes attributes) {
	int i = Integer.parseInt(attributes.getValue("parameter"));
	String string_0_ = attributes.getValue("desc");
	boolean bool
	    = Boolean.valueOf(attributes.getValue("visible")).booleanValue();
	this$0.push(((MethodVisitor) this$0.peek())
			.visitParameterAnnotation(i, string_0_, bool));
    }
    
    public void end(String string) {
	AnnotationVisitor annotationvisitor = (AnnotationVisitor) this$0.pop();
	if (annotationvisitor != null)
	    annotationvisitor.visitEnd();
	return;
    }
}
