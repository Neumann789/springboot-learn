/* TableSwitchInsnNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;

public class TableSwitchInsnNode extends AbstractInsnNode
{
    public int min;
    public int max;
    public LabelNode dflt;
    public List labels;
    
    public transient TableSwitchInsnNode(int i, int i_0_, LabelNode labelnode,
					 LabelNode[] labelnodes) {
	super(170);
	min = i;
	max = i_0_;
	dflt = labelnode;
	labels = new ArrayList();
	if (labelnodes != null)
	    labels.addAll(Arrays.asList(labelnodes));
	return;
    }
    
    public int getType() {
	return 11;
    }
    
    public void accept(MethodVisitor methodvisitor) {
	Label[] labels = new Label[this.labels.size()];
	int i = 0;
	for (;;) {
	    if (i >= labels.length) {
		methodvisitor.visitTableSwitchInsn(min, max, dflt.getLabel(),
						   labels);
		acceptAnnotations(methodvisitor);
	    }
	    labels[i] = ((LabelNode) this.labels.get(i)).getLabel();
	    i++;
	}
    }
    
    public AbstractInsnNode clone(Map map) {
	return new TableSwitchInsnNode
		   (min, max, clone(dflt, map), clone(labels, map))
		   .cloneAnnotations(this);
    }
}
