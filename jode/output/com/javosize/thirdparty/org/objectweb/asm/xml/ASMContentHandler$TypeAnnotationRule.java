/* ASMContentHandler$TypeAnnotationRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.ClassVisitor;
import com.javosize.thirdparty.org.objectweb.asm.FieldVisitor;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$TypeAnnotationRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$TypeAnnotationRule(ASMContentHandler asmcontenthandler) {
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
	Object object = this$0.peek();
	if (!(object instanceof ClassVisitor)) {
	    if (!(object instanceof FieldVisitor)) {
		if (object instanceof MethodVisitor)
		    this$0.push(((MethodVisitor) object).visitTypeAnnotation
				(i, typepath, string_0_, bool));
	    } else
		this$0.push(((FieldVisitor) object).visitTypeAnnotation
			    (i, typepath, string_0_, bool));
	} else
	    this$0.push(((ClassVisitor) object).visitTypeAnnotation(i,
								    typepath,
								    string_0_,
								    bool));
	return;
    }
    
    public void end(String string) {
	AnnotationVisitor annotationvisitor = (AnnotationVisitor) this$0.pop();
	if (annotationvisitor != null)
	    annotationvisitor.visitEnd();
	return;
    }
}
