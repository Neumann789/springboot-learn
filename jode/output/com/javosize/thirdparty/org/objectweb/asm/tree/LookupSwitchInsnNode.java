/* LookupSwitchInsnNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;

public class LookupSwitchInsnNode extends AbstractInsnNode
{
    public LabelNode dflt;
    public List keys;
    public List labels;
    
    public LookupSwitchInsnNode(LabelNode labelnode, int[] is,
				LabelNode[] labelnodes) {
	super(171);
	dflt = labelnode;
	PUSH this;
	PUSH new ArrayList;
    label_471:
	{
	    DUP
	    if (is != null)
		PUSH is.length;
	    else
		PUSH false;
	    break label_471;
	}
	((UNCONSTRUCTED)POP).ArrayList(POP);
	((LookupSwitchInsnNode) POP).keys = POP;
	PUSH this;
	PUSH new ArrayList;
    label_472:
	{
	    DUP
	    if (labelnodes != null)
		PUSH labelnodes.length;
	    else
		PUSH false;
	    break label_472;
	}
	((UNCONSTRUCTED)POP).ArrayList(POP);
    label_473:
	{
	    ((LookupSwitchInsnNode) POP).labels = POP;
	    if (is != null) {
		for (int i = 0; i < is.length; i++)
		    keys.add(new Integer(is[i]));
	    }
	    break label_473;
	}
	if (labelnodes != null)
	    labels.addAll(Arrays.asList(labelnodes));
	return;
    }
    
    public int getType() {
	return 12;
    }
    
    public void accept(MethodVisitor methodvisitor) {
	int[] is = new int[keys.size()];
	int i = 0;
	for (;;) {
	    if (i >= is.length) {
		Label[] labels = new Label[this.labels.size()];
		int i_0_ = 0;
		for (;;) {
		    if (i_0_ >= labels.length) {
			methodvisitor.visitLookupSwitchInsn(dflt.getLabel(),
							    is, labels);
			acceptAnnotations(methodvisitor);
		    }
		    labels[i_0_]
			= ((LabelNode) this.labels.get(i_0_)).getLabel();
		    i_0_++;
		}
	    }
	    is[i] = ((Integer) keys.get(i)).intValue();
	    i++;
	}
    }
    
    public AbstractInsnNode clone(Map map) {
	LookupSwitchInsnNode lookupswitchinsnnode_1_
	    = new LookupSwitchInsnNode(clone(dflt, map), null,
				       clone(labels, map));
	lookupswitchinsnnode_1_.keys.addAll(keys);
	return lookupswitchinsnnode_1_.cloneAnnotations(this);
    }
}
