/* ASMContentHandler$LocalVariableAnnotationRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$LocalVariableAnnotationRule
    extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$LocalVariableAnnotationRule
	(ASMContentHandler asmcontenthandler) {
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
	String[] strings = attributes.getValue("start").split(" ");
	Label[] labels = new Label[strings.length];
	int i_1_ = 0;
	for (;;) {
	    if (i_1_ >= labels.length) {
		String[] strings_2_ = attributes.getValue("end").split(" ");
		Label[] labels_3_ = new Label[strings_2_.length];
		int i_4_ = 0;
		for (;;) {
		    if (i_4_ >= labels_3_.length) {
			String[] strings_5_
			    = attributes.getValue("index").split(" ");
			int[] is = new int[strings_5_.length];
			int i_6_ = 0;
			for (;;) {
			    if (i_6_ >= is.length)
				this$0.push(((MethodVisitor) this$0.peek())
						.visitLocalVariableAnnotation
					    (i, typepath, labels, labels_3_,
					     is, string_0_, bool));
			    is[i_6_] = Integer.parseInt(strings_5_[i_6_]);
			    i_6_++;
			}
		    }
		    labels_3_[i_4_] = getLabel(strings_2_[i_4_]);
		    i_4_++;
		}
	    }
	    labels[i_1_] = getLabel(strings[i_1_]);
	    i_1_++;
	}
    }
    
    public void end(String string) {
	AnnotationVisitor annotationvisitor = (AnnotationVisitor) this$0.pop();
	if (annotationvisitor != null)
	    annotationvisitor.visitEnd();
	return;
    }
}
