/* JSRInlinerAdapter$Instantiation - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import java.util.AbstractMap;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.javosize.thirdparty.org.objectweb.asm.tree.AbstractInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.LabelNode;

class JSRInlinerAdapter$Instantiation extends AbstractMap
{
    final JSRInlinerAdapter$Instantiation previous;
    public final BitSet subroutine;
    public final Map rangeTable;
    public final LabelNode returnLabel;
    /*synthetic*/ final JSRInlinerAdapter this$0;
    
    JSRInlinerAdapter$Instantiation
	(JSRInlinerAdapter jsrinlineradapter,
	 JSRInlinerAdapter$Instantiation instantiation_0_, BitSet bitset) {
	this$0 = jsrinlineradapter;
	super();
	rangeTable = new HashMap();
	previous = instantiation_0_;
	subroutine = bitset;
	JSRInlinerAdapter$Instantiation instantiation_1_ = instantiation_0_;
    label_388:
	{
	    for (;;) {
		if (instantiation_1_ == null) {
		    if (instantiation_0_ == null)
			returnLabel = null;
		    else
			returnLabel = new LabelNode();
		} else {
		    if (instantiation_1_.subroutine != bitset)
			instantiation_1_ = instantiation_1_.previous;
		    throw new RuntimeException("Recursive invocation of "
					       + bitset);
		}
		break label_388;
	    }
	}
	LabelNode labelnode = null;
	int i = 0;
	int i_2_ = (RUNTIME ERROR IN EXPRESSION).size();
	for (;;) {
	    IF (i >= i_2_)
		/* empty */
	label_390:
	    {
		AbstractInsnNode abstractinsnnode
		    = (RUNTIME ERROR IN EXPRESSION).get(i);
		if (abstractinsnnode.getType() != 8) {
		    if (findOwner(i) == this)
			labelnode = null;
		} else {
		    LabelNode labelnode_3_;
		label_389:
		    {
			labelnode_3_ = (LabelNode) abstractinsnnode;
			if (labelnode == null)
			    labelnode = new LabelNode();
			break label_389;
		    }
		    rangeTable.put(labelnode_3_, labelnode);
		}
		break label_390;
	    }
	    i++;
	}
	break label_388;
    }
    
    public JSRInlinerAdapter$Instantiation findOwner(int i) {
	if (subroutine.get(i)) {
	    if (this$0.dualCitizens.get(i)) {
		JSRInlinerAdapter$Instantiation instantiation_4_ = this;
		JSRInlinerAdapter$Instantiation instantiation_5_ = previous;
		for (;;) {
		label_391:
		    {
			if (instantiation_5_ == null)
			    return instantiation_4_;
			if (instantiation_5_.subroutine.get(i))
			    instantiation_4_ = instantiation_5_;
			break label_391;
		    }
		    instantiation_5_ = instantiation_5_.previous;
		}
	    }
	    return this;
	}
	return null;
    }
    
    public LabelNode gotoLabel(LabelNode labelnode) {
	JSRInlinerAdapter$Instantiation instantiation_6_
	    = findOwner((RUNTIME ERROR IN EXPRESSION).indexOf(labelnode));
	return (LabelNode) instantiation_6_.rangeTable.get(labelnode);
    }
    
    public LabelNode rangeLabel(LabelNode labelnode) {
	return (LabelNode) rangeTable.get(labelnode);
    }
    
    public Set entrySet() {
	return null;
    }
    
    public LabelNode get(Object object) {
	return gotoLabel((LabelNode) object);
    }
}
