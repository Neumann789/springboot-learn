/* SAXFieldAdapter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.FieldVisitor;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;
import com.javosize.thirdparty.org.xml.sax.Attributes;

public final class SAXFieldAdapter extends FieldVisitor
{
    SAXAdapter sa;
    
    public SAXFieldAdapter(SAXAdapter saxadapter, Attributes attributes) {
	super(327680);
	sa = saxadapter;
	saxadapter.addStart("field", attributes);
    }
    
    public AnnotationVisitor visitAnnotation(String string, boolean bool) {
	PUSH new SAXAnnotationAdapter;
	DUP
	PUSH sa;
    label_788:
	{
	    PUSH "annotation";
	    if (!bool)
		PUSH -1;
	    else
		PUSH true;
	    break label_788;
	}
	((UNCONSTRUCTED)POP).SAXAnnotationAdapter(POP, POP, POP, null, string);
	return POP;
    }
    
    public AnnotationVisitor visitTypeAnnotation(int i, TypePath typepath,
						 String string, boolean bool) {
	PUSH new SAXAnnotationAdapter;
	DUP
	PUSH sa;
    label_789:
	{
	    PUSH "typeAnnotation";
	    if (!bool)
		PUSH -1;
	    else
		PUSH true;
	    break label_789;
	}
	((UNCONSTRUCTED)POP).SAXAnnotationAdapter(POP, POP, POP, null, string,
						  i, typepath);
	return POP;
    }
    
    public void visitEnd() {
	sa.addEnd("field");
    }
}
