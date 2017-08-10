/* TryCatchBlockNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import java.util.Iterator;
import java.util.List;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;

public class TryCatchBlockNode
{
    public LabelNode start;
    public LabelNode end;
    public LabelNode handler;
    public String type;
    public List visibleTypeAnnotations;
    public List invisibleTypeAnnotations;
    
    public TryCatchBlockNode(LabelNode labelnode, LabelNode labelnode_0_,
			     LabelNode labelnode_1_, String string) {
	start = labelnode;
	end = labelnode_0_;
	handler = labelnode_1_;
	type = string;
    }
    
    public void updateIndex(int i) {
	int i_2_;
    label_479:
	{
	    i_2_ = 0x42000000 | i << 8;
	    if (visibleTypeAnnotations != null) {
		Iterator iterator = visibleTypeAnnotations.iterator();
		while (iterator.hasNext()) {
		    TypeAnnotationNode typeannotationnode
			= (TypeAnnotationNode) iterator.next();
		    typeannotationnode.typeRef = i_2_;
		}
	    }
	    break label_479;
	}
	if (invisibleTypeAnnotations != null) {
	    Iterator iterator = invisibleTypeAnnotations.iterator();
	    while (iterator.hasNext()) {
		TypeAnnotationNode typeannotationnode
		    = (TypeAnnotationNode) iterator.next();
		typeannotationnode.typeRef = i_2_;
	    }
	}
	return;
    }
    
    public void accept(MethodVisitor methodvisitor) {
	PUSH methodvisitor;
	PUSH start.getLabel();
    label_480:
	{
	    PUSH end.getLabel();
	    if (handler != null)
		PUSH handler.getLabel();
	    else
		PUSH null;
	    break label_480;
	}
    label_481:
	{
	    ((MethodVisitor) POP).visitTryCatchBlock(POP, POP, POP, type);
	    if (visibleTypeAnnotations != null)
		PUSH visibleTypeAnnotations.size();
	    else
		PUSH false;
	    break label_481;
	}
	int i = POP;
	int i_3_ = 0;
    label_482:
	{
	    for (;;) {
		if (i_3_ >= i) {
		    if (invisibleTypeAnnotations != null)
			PUSH invisibleTypeAnnotations.size();
		    else
			PUSH false;
		    break label_482;
		}
		TypeAnnotationNode typeannotationnode
		    = (TypeAnnotationNode) visibleTypeAnnotations.get(i_3_);
		typeannotationnode.accept(methodvisitor.visitTryCatchAnnotation
					  (typeannotationnode.typeRef,
					   typeannotationnode.typePath,
					   typeannotationnode.desc, true));
		i_3_++;
	    }
	}
	i = POP;
	i_3_ = 0;
	for (;;) {
	    IF (i_3_ >= i)
		/* empty */
	    TypeAnnotationNode typeannotationnode
		= (TypeAnnotationNode) invisibleTypeAnnotations.get(i_3_);
	    typeannotationnode.accept(methodvisitor.visitTryCatchAnnotation
				      (typeannotationnode.typeRef,
				       typeannotationnode.typePath,
				       typeannotationnode.desc, false));
	    i_3_++;
	}
	break label_482;
    }
}
