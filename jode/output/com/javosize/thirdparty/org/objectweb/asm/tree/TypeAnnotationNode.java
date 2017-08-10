/* TypeAnnotationNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;

public class TypeAnnotationNode extends AnnotationNode
{
    public int typeRef;
    public TypePath typePath;
    /*synthetic*/ static Class class$org$objectweb$asm$tree$TypeAnnotationNode
		      = (class$
			 ("com.javosize.thirdparty.org.objectweb.asm.tree.TypeAnnotationNode"));
    
    public TypeAnnotationNode(int i, TypePath typepath, String string) {
	this(327680, i, typepath, string);
	IF (this.getClass() == class$org$objectweb$asm$tree$TypeAnnotationNode)
	    /* empty */
	throw new IllegalStateException();
    }
    
    public TypeAnnotationNode(int i, int i_0_, TypePath typepath,
			      String string) {
	super(i, string);
	typeRef = i_0_;
	typePath = typepath;
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
