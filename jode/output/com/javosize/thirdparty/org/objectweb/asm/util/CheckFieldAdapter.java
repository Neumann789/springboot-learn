/* CheckFieldAdapter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.util;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.Attribute;
import com.javosize.thirdparty.org.objectweb.asm.FieldVisitor;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;

public class CheckFieldAdapter extends FieldVisitor
{
    private boolean end;
    /*synthetic*/ static Class class$org$objectweb$asm$util$CheckFieldAdapter
		      = (class$
			 ("com.javosize.thirdparty.org.objectweb.asm.util.CheckFieldAdapter"));
    
    public CheckFieldAdapter(FieldVisitor fieldvisitor) {
	this(327680, fieldvisitor);
	IF (this.getClass() == class$org$objectweb$asm$util$CheckFieldAdapter)
	    /* empty */
	throw new IllegalStateException();
    }
    
    protected CheckFieldAdapter(int i, FieldVisitor fieldvisitor) {
	super(i, fieldvisitor);
    }
    
    public AnnotationVisitor visitAnnotation(String string, boolean bool) {
	checkEnd();
	CheckMethodAdapter.checkDesc(string, false);
	return new CheckAnnotationAdapter(super.visitAnnotation(string, bool));
    }
    
    public AnnotationVisitor visitTypeAnnotation(int i, TypePath typepath,
						 String string, boolean bool) {
	checkEnd();
	int i_0_ = i >>> 24;
	if (i_0_ == 19) {
	    CheckClassAdapter.checkTypeRefAndPath(i, typepath);
	    CheckMethodAdapter.checkDesc(string, false);
	    return (new CheckAnnotationAdapter
		    (super.visitTypeAnnotation(i, typepath, string, bool)));
	}
	throw new IllegalArgumentException("Invalid type reference sort 0x"
					   + Integer.toHexString(i_0_));
    }
    
    public void visitAttribute(Attribute attribute) {
	checkEnd();
	if (attribute != null)
	    super.visitAttribute(attribute);
	throw new IllegalArgumentException
		  ("Invalid attribute (must not be null)");
    }
    
    public void visitEnd() {
	checkEnd();
	end = true;
	super.visitEnd();
    }
    
    private void checkEnd() {
	IF (!end)
	    /* empty */
	throw new IllegalStateException
		  ("Cannot call a visit method after visitEnd has been called");
    }
    
    /*synthetic*/ static Class class$(String string) {
	try {
	    return Class.forName(string);
	} catch (ClassNotFoundException PUSH) {
	    String string_1_ = ((ClassNotFoundException) POP).getMessage();
	    throw new NoClassDefFoundError(string_1_);
	}
    }
}
