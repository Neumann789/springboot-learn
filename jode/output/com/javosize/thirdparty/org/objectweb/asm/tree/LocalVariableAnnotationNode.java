/* LocalVariableAnnotationNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;

public class LocalVariableAnnotationNode extends TypeAnnotationNode
{
    public List start;
    public List end;
    public List index;
    
    public LocalVariableAnnotationNode(int i, TypePath typepath,
				       LabelNode[] labelnodes,
				       LabelNode[] labelnodes_0_, int[] is,
				       String string) {
	this(327680, i, typepath, labelnodes, labelnodes_0_, is, string);
    }
    
    public LocalVariableAnnotationNode(int i, int i_1_, TypePath typepath,
				       LabelNode[] labelnodes,
				       LabelNode[] labelnodes_2_, int[] is,
				       String string) {
	super(i, i_1_, typepath, string);
	start = new ArrayList(labelnodes.length);
	start.addAll(Arrays.asList(labelnodes));
	end = new ArrayList(labelnodes_2_.length);
	end.addAll(Arrays.asList(labelnodes_2_));
	index = new ArrayList(is.length);
	int[] is_3_ = is;
	int i_4_ = is_3_.length;
	int i_5_ = 0;
	for (;;) {
	    IF (i_5_ >= i_4_)
		/* empty */
	    int i_6_ = is_3_[i_5_];
	    index.add(Integer.valueOf(i_6_));
	    i_5_++;
	}
    }
    
    public void accept(MethodVisitor methodvisitor, boolean bool) {
	Label[] labels = new Label[start.size()];
	Label[] labels_7_ = new Label[end.size()];
	int[] is = new int[index.size()];
	int i = 0;
	for (;;) {
	    if (i >= labels.length)
		this.accept(methodvisitor.visitLocalVariableAnnotation
			    (typeRef, typePath, labels, labels_7_, is, desc,
			     true));
	    labels[i] = ((LabelNode) start.get(i)).getLabel();
	    labels_7_[i] = ((LabelNode) end.get(i)).getLabel();
	    is[i] = ((Integer) index.get(i)).intValue();
	    i++;
	}
    }
}
