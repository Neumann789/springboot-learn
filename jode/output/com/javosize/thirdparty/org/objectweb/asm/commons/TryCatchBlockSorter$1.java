/* TryCatchBlockSorter$1 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import java.util.Comparator;

import com.javosize.thirdparty.org.objectweb.asm.tree.TryCatchBlockNode;

class TryCatchBlockSorter$1 implements Comparator
{
    /*synthetic*/ final TryCatchBlockSorter this$0;
    
    TryCatchBlockSorter$1(TryCatchBlockSorter trycatchblocksorter) {
	this$0 = trycatchblocksorter;
	super();
    }
    
    public int compare(TryCatchBlockNode trycatchblocknode,
		       TryCatchBlockNode trycatchblocknode_0_) {
	int i = blockLength(trycatchblocknode);
	int i_1_ = blockLength(trycatchblocknode_0_);
	return i - i_1_;
    }
    
    private int blockLength(TryCatchBlockNode trycatchblocknode) {
	int i = (RUNTIME ERROR IN EXPRESSION).indexOf(trycatchblocknode.start);
	int i_2_
	    = (RUNTIME ERROR IN EXPRESSION).indexOf(trycatchblocknode.end);
	return i_2_ - i;
    }
}
