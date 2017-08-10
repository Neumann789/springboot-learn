/* SourceValue - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree.analysis;
import java.util.Set;

import com.javosize.thirdparty.org.objectweb.asm.tree.AbstractInsnNode;

public class SourceValue implements Value
{
    public final int size;
    public final Set insns;
    
    public SourceValue(int i) {
	this(i, SmallSet.emptySet());
    }
    
    public SourceValue(int i, AbstractInsnNode abstractinsnnode) {
	size = i;
	insns = new SmallSet(abstractinsnnode, null);
    }
    
    public SourceValue(int i, Set set) {
	size = i;
	insns = set;
    }
    
    public int getSize() {
	return size;
    }
    
    public boolean equals(Object object) {
    label_531:
	{
	    if (object instanceof SourceValue) {
		SourceValue sourcevalue_0_ = (SourceValue) object;
		if (size != sourcevalue_0_.size
		    || !insns.equals(sourcevalue_0_.insns))
		    PUSH false;
		else
		    PUSH true;
	    } else
		return false;
	}
	return POP;
	break label_531;
    }
    
    public int hashCode() {
	return insns.hashCode();
    }
}
