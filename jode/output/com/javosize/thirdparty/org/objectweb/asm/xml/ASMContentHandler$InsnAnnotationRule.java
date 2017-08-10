/* ASMContentHandler$InsnAnnotationRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$InsnAnnotationRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$InsnAnnotationRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public void begin(String string, Attributes attributes) {
	String string_0_ = attributes.getValue("desc");
	boolean bool
	    = Boolean.valueOf(attributes.getValue("visible")).booleanValue();
	int i = Integer.parseInt(attributes.getValue("typeRef"));
	TypePath typepath
	    = TypePath.fromString(attributes.getValue("typePath"));
	this$0.push(((MethodVisitor) this$0.peek())
			.visitInsnAnnotation(i, typepath, string_0_, bool));
    }
    
    public void end(String string) {
	AnnotationVisitor annotationvisitor = (AnnotationVisitor) this$0.pop();
	if (annotationvisitor != null)
	    annotationvisitor.visitEnd();
	return;
    }
}
