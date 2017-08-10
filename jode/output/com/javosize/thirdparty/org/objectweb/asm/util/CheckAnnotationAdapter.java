/* CheckAnnotationAdapter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.util;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.Type;

public class CheckAnnotationAdapter extends AnnotationVisitor
{
    private final boolean named;
    private boolean end;
    
    public CheckAnnotationAdapter(AnnotationVisitor annotationvisitor) {
	this(annotationvisitor, true);
    }
    
    CheckAnnotationAdapter(AnnotationVisitor annotationvisitor, boolean bool) {
	super(327680, annotationvisitor);
	named = bool;
    }
    
    public void visit(String string, Object object) {
	checkEnd();
	checkName(string);
    label_604:
	{
	    if (object instanceof Byte || object instanceof Boolean
		|| object instanceof Character || object instanceof Short
		|| object instanceof Integer || object instanceof Long
		|| object instanceof Float || object instanceof Double
		|| object instanceof String || object instanceof Type
		|| object instanceof byte[] || object instanceof boolean[]
		|| object instanceof char[] || object instanceof short[]
		|| object instanceof int[] || object instanceof long[]
		|| object instanceof float[] || object instanceof double[]) {
		if (object instanceof Type) {
		    int i = ((Type) object).getSort();
		    if (i == 11)
			throw new IllegalArgumentException
				  ("Invalid annotation value");
		}
	    } else
		throw new IllegalArgumentException("Invalid annotation value");
	}
	if (av != null)
	    av.visit(string, object);
	return;
	break label_604;
    }
    
    public void visitEnum(String string, String string_0_, String string_1_) {
	checkEnd();
	checkName(string);
	CheckMethodAdapter.checkDesc(string_0_, false);
	if (string_1_ != null) {
	    if (av != null)
		av.visitEnum(string, string_0_, string_1_);
	} else
	    throw new IllegalArgumentException("Invalid enum value");
	return;
    }
    
    public AnnotationVisitor visitAnnotation(String string, String string_2_) {
	checkEnd();
	checkName(string);
	CheckMethodAdapter.checkDesc(string_2_, false);
	PUSH new CheckAnnotationAdapter;
    label_605:
	{
	    DUP
	    if (av != null)
		PUSH av.visitAnnotation(string, string_2_);
	    else
		PUSH null;
	    break label_605;
	}
	((UNCONSTRUCTED)POP).CheckAnnotationAdapter(POP);
	return POP;
    }
    
    public AnnotationVisitor visitArray(String string) {
	checkEnd();
	checkName(string);
	PUSH new CheckAnnotationAdapter;
    label_606:
	{
	    DUP
	    if (av != null)
		PUSH av.visitArray(string);
	    else
		PUSH null;
	    break label_606;
	}
	((UNCONSTRUCTED)POP).CheckAnnotationAdapter(POP, false);
	return POP;
    }
    
    public void visitEnd() {
	checkEnd();
	end = true;
	if (av != null)
	    av.visitEnd();
	return;
    }
    
    private void checkEnd() {
	IF (!end)
	    /* empty */
	throw new IllegalStateException
		  ("Cannot call a visit method after visitEnd has been called");
    }
    
    private void checkName(String string) {
	IF (!named || string != null)
	    /* empty */
	throw new IllegalArgumentException
		  ("Annotation value name must not be null");
    }
}
