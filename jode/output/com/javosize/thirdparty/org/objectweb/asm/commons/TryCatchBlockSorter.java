/* TryCatchBlockSorter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import java.util.Collections;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.tree.MethodNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.TryCatchBlockNode;

public class TryCatchBlockSorter extends MethodNode
{
    public TryCatchBlockSorter(MethodVisitor methodvisitor, int i,
			       String string, String string_0_,
			       String string_1_, String[] strings) {
	this(327680, methodvisitor, i, string, string_0_, string_1_, strings);
    }
    
    protected TryCatchBlockSorter(int i, MethodVisitor methodvisitor, int i_2_,
				  String string, String string_3_,
				  String string_4_, String[] strings) {
	super(i, i_2_, string, string_3_, string_4_, strings);
	mv = methodvisitor;
    }
    
    public void visitEnd() {
	TryCatchBlockSorter$1 var_1 = new TryCatchBlockSorter$1(this);
	Collections.sort(tryCatchBlocks, var_1);
	int i = 0;
	for (;;) {
	    if (i >= tryCatchBlocks.size()) {
		if (mv != null)
		    accept(mv);
		return;
	    }
	    ((TryCatchBlockNode) tryCatchBlocks.get(i)).updateIndex(i);
	    i++;
	}
	return;
    }
}
