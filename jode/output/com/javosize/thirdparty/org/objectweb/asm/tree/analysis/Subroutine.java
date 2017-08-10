/* Subroutine - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree.analysis;
import java.util.ArrayList;
import java.util.List;

import com.javosize.thirdparty.org.objectweb.asm.tree.JumpInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.LabelNode;

class Subroutine
{
    LabelNode start;
    boolean[] access;
    List callers;
    
    private Subroutine() {
	/* empty */
    }
    
    Subroutine(LabelNode labelnode, int i, JumpInsnNode jumpinsnnode) {
	start = labelnode;
	access = new boolean[i];
	callers = new ArrayList();
	callers.add(jumpinsnnode);
    }
    
    public Subroutine copy() {
	Subroutine subroutine_0_ = new Subroutine();
	subroutine_0_.start = start;
	subroutine_0_.access = new boolean[access.length];
	System.arraycopy(access, 0, subroutine_0_.access, 0, access.length);
	subroutine_0_.callers = new ArrayList(callers);
	return subroutine_0_;
    }
    
    public boolean merge(Subroutine subroutine_1_) throws AnalyzerException {
	boolean bool = false;
	int i = 0;
    label_534:
	{
	    for (;;) {
	    label_532:
		{
		    if (i >= access.length) {
			if (subroutine_1_.start == start) {
			    for (i = 0; i < subroutine_1_.callers.size();
				 i++) {
			    label_533:
				{
				    JumpInsnNode jumpinsnnode
					= ((JumpInsnNode)
					   subroutine_1_.callers.get(i));
				    if (!callers.contains(jumpinsnnode)) {
					callers.add(jumpinsnnode);
					bool = true;
				    }
				    break label_533;
				}
			    }
			}
		    } else {
			if (subroutine_1_.access[i] && !access[i]) {
			    access[i] = true;
			    bool = true;
			}
			break label_532;
		    }
		    break label_534;
		}
		i++;
	    }
	}
	return bool;
	break label_534;
    }
}
