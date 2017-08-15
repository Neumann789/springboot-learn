/* JSRInlinerAdapter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.Opcodes;
import com.javosize.thirdparty.org.objectweb.asm.tree.AbstractInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.InsnList;
import com.javosize.thirdparty.org.objectweb.asm.tree.InsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.JumpInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.LabelNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.LocalVariableNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.LookupSwitchInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.MethodNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.TableSwitchInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.TryCatchBlockNode;

public class JSRInlinerAdapter extends MethodNode implements Opcodes
{
    private final Map subroutineHeads = new HashMap();
    private final BitSet mainSubroutine = new BitSet();
    final BitSet dualCitizens = new BitSet();
    /*synthetic*/ static Class class$org$objectweb$asm$commons$JSRInlinerAdapter
		      = (class$
			 ("com.javosize.thirdparty.org.objectweb.asm.commons.JSRInlinerAdapter"));
    
    public JSRInlinerAdapter(MethodVisitor methodvisitor, int i, String string,
			     String string_0_, String string_1_,
			     String[] strings) {
	this(327680, methodvisitor, i, string, string_0_, string_1_, strings);
	if (this.getClass()
	    != class$org$objectweb$asm$commons$JSRInlinerAdapter)
	    throw new IllegalStateException();
    }
    
    protected JSRInlinerAdapter(int i, MethodVisitor methodvisitor, int i_2_,
				String string, String string_3_,
				String string_4_, String[] strings) {
	super(i, i_2_, string, string_3_, string_4_, strings);
	mv = methodvisitor;
    }
    
    public void visitJumpInsn(int i, Label label) {
	super.visitJumpInsn(i, label);
	LabelNode labelnode = ((JumpInsnNode) instructions.getLast()).label;
	if (i == 168 && !subroutineHeads.containsKey(labelnode))
	    subroutineHeads.put(labelnode, new BitSet());
    }
    
    public void visitEnd() {
	if (!subroutineHeads.isEmpty()) {
	    markSubroutines();
	    emitCode();
	}
	if (mv != null)
	    accept(mv);
    }
    
    private void markSubroutines() {
	BitSet bitset = new BitSet();
	markSubroutineWalk(mainSubroutine, 0, bitset);
	Iterator iterator = subroutineHeads.entrySet().iterator();
	while (iterator.hasNext()) {
	    Map.Entry entry = (Map.Entry) iterator.next();
	    LabelNode labelnode = (LabelNode) entry.getKey();
	    BitSet bitset_5_ = (BitSet) entry.getValue();
	    int i = instructions.indexOf(labelnode);
	    markSubroutineWalk(bitset_5_, i, bitset);
	}
    }
    
    private void markSubroutineWalk(BitSet bitset, int i, BitSet bitset_6_) {
	markSubroutineWalkDFS(bitset, i, bitset_6_);
	boolean bool = true;
	while (bool) {
	    bool = false;
	    Iterator iterator = tryCatchBlocks.iterator();
	    while (iterator.hasNext()) {
		TryCatchBlockNode trycatchblocknode
		    = (TryCatchBlockNode) iterator.next();
		int i_7_ = instructions.indexOf(trycatchblocknode.handler);
		if (!bitset.get(i_7_)) {
		    int i_8_ = instructions.indexOf(trycatchblocknode.start);
		    int i_9_ = instructions.indexOf(trycatchblocknode.end);
		    int i_10_ = bitset.nextSetBit(i_8_);
		    if (i_10_ != -1 && i_10_ < i_9_) {
			markSubroutineWalkDFS(bitset, i_7_, bitset_6_);
			bool = true;
		    }
		}
	    }
	}
    }
    
    private void markSubroutineWalkDFS(BitSet bitset, int i,
				       BitSet bitset_11_) {
	for (;;) {
	    AbstractInsnNode abstractinsnnode = instructions.get(i);
	    if (bitset.get(i))
		break;
	    bitset.set(i);
	    if (bitset_11_.get(i))
		dualCitizens.set(i);
	    bitset_11_.set(i);
	    if (abstractinsnnode.getType() == 7
		&& abstractinsnnode.getOpcode() != 168) {
		JumpInsnNode jumpinsnnode = (JumpInsnNode) abstractinsnnode;
		int i_12_ = instructions.indexOf(jumpinsnnode.label);
		markSubroutineWalkDFS(bitset, i_12_, bitset_11_);
	    }
	    if (abstractinsnnode.getType() == 11) {
		TableSwitchInsnNode tableswitchinsnnode
		    = (TableSwitchInsnNode) abstractinsnnode;
		int i_13_ = instructions.indexOf(tableswitchinsnnode.dflt);
		markSubroutineWalkDFS(bitset, i_13_, bitset_11_);
		for (int i_14_ = tableswitchinsnnode.labels.size() - 1;
		     i_14_ >= 0; i_14_--) {
		    LabelNode labelnode
			= (LabelNode) tableswitchinsnnode.labels.get(i_14_);
		    i_13_ = instructions.indexOf(labelnode);
		    markSubroutineWalkDFS(bitset, i_13_, bitset_11_);
		}
	    }
	    if (abstractinsnnode.getType() == 12) {
		LookupSwitchInsnNode lookupswitchinsnnode
		    = (LookupSwitchInsnNode) abstractinsnnode;
		int i_15_ = instructions.indexOf(lookupswitchinsnnode.dflt);
		markSubroutineWalkDFS(bitset, i_15_, bitset_11_);
		for (int i_16_ = lookupswitchinsnnode.labels.size() - 1;
		     i_16_ >= 0; i_16_--) {
		    LabelNode labelnode
			= (LabelNode) lookupswitchinsnnode.labels.get(i_16_);
		    i_15_ = instructions.indexOf(labelnode);
		    markSubroutineWalkDFS(bitset, i_15_, bitset_11_);
		}
	    }
	    switch (instructions.get(i).getOpcode()) {
	    case 167:
	    case 169:
	    case 170:
	    case 171:
	    case 172:
	    case 173:
	    case 174:
	    case 175:
	    case 176:
	    case 177:
	    case 191:
		return;
	    default:
		if (++i >= instructions.size())
		    return;
	    }
	}
    }
    
    private void emitCode() {
	LinkedList linkedlist = new LinkedList();
	linkedlist.add(new JSRInlinerAdapter$Instantiation(this, null,
							   mainSubroutine));
	InsnList insnlist = new InsnList();
	ArrayList arraylist = new ArrayList();
	ArrayList arraylist_17_ = new ArrayList();
	while (!linkedlist.isEmpty()) {
	    JSRInlinerAdapter$Instantiation instantiation
		= (JSRInlinerAdapter$Instantiation) linkedlist.removeFirst();
	    emitSubroutine(instantiation, linkedlist, insnlist, arraylist,
			   arraylist_17_);
	}
	instructions = insnlist;
	tryCatchBlocks = arraylist;
	localVariables = arraylist_17_;
    }
    
    private void emitSubroutine(JSRInlinerAdapter$Instantiation instantiation,
				List list, InsnList insnlist, List list_18_,
				List list_19_) {
	LabelNode labelnode = null;
	int i = 0;
	for (int i_20_ = instructions.size(); i < i_20_; i++) {
	    AbstractInsnNode abstractinsnnode = instructions.get(i);
	    JSRInlinerAdapter$Instantiation instantiation_21_
		= instantiation.findOwner(i);
	    if (abstractinsnnode.getType() == 8) {
		LabelNode labelnode_22_ = (LabelNode) abstractinsnnode;
		LabelNode labelnode_23_
		    = instantiation.rangeLabel(labelnode_22_);
		if (labelnode_23_ != labelnode) {
		    insnlist.add(labelnode_23_);
		    labelnode = labelnode_23_;
		}
	    } else if (instantiation_21_ == instantiation) {
		if (abstractinsnnode.getOpcode() == 169) {
		    LabelNode labelnode_24_ = null;
		    for (JSRInlinerAdapter$Instantiation instantiation_25_
			     = instantiation;
			 instantiation_25_ != null;
			 instantiation_25_ = instantiation_25_.previous) {
			if (instantiation_25_.subroutine.get(i))
			    labelnode_24_ = instantiation_25_.returnLabel;
		    }
		    if (labelnode_24_ == null)
			throw new RuntimeException
				  ("Instruction #" + i
				   + " is a RET not owned by any subroutine");
		    insnlist.add(new JumpInsnNode(167, labelnode_24_));
		} else if (abstractinsnnode.getOpcode() == 168) {
		    LabelNode labelnode_26_
			= ((JumpInsnNode) abstractinsnnode).label;
		    BitSet bitset
			= (BitSet) subroutineHeads.get(labelnode_26_);
		    JSRInlinerAdapter$Instantiation instantiation_27_
			= new JSRInlinerAdapter$Instantiation(this,
							      instantiation,
							      bitset);
		    LabelNode labelnode_28_
			= instantiation_27_.gotoLabel(labelnode_26_);
		    insnlist.add(new InsnNode(1));
		    insnlist.add(new JumpInsnNode(167, labelnode_28_));
		    insnlist.add(instantiation_27_.returnLabel);
		    list.add(instantiation_27_);
		} else
		    insnlist.add(abstractinsnnode.clone(instantiation));
	    }
	}
	Iterator iterator = tryCatchBlocks.iterator();
	while (iterator.hasNext()) {
	    TryCatchBlockNode trycatchblocknode
		= (TryCatchBlockNode) iterator.next();
	    LabelNode labelnode_29_
		= instantiation.rangeLabel(trycatchblocknode.start);
	    LabelNode labelnode_30_
		= instantiation.rangeLabel(trycatchblocknode.end);
	    if (labelnode_29_ != labelnode_30_) {
		LabelNode labelnode_31_
		    = instantiation.gotoLabel(trycatchblocknode.handler);
		if (labelnode_29_ == null || labelnode_30_ == null
		    || labelnode_31_ == null)
		    throw new RuntimeException("Internal error!");
		list_18_.add(new TryCatchBlockNode(labelnode_29_,
						   labelnode_30_,
						   labelnode_31_,
						   trycatchblocknode.type));
	    }
	}
	iterator = localVariables.iterator();
	while (iterator.hasNext()) {
	    LocalVariableNode localvariablenode
		= (LocalVariableNode) iterator.next();
	    LabelNode labelnode_32_
		= instantiation.rangeLabel(localvariablenode.start);
	    LabelNode labelnode_33_
		= instantiation.rangeLabel(localvariablenode.end);
	    if (labelnode_32_ != labelnode_33_)
		list_19_.add(new LocalVariableNode(localvariablenode.name,
						   localvariablenode.desc,
						   localvariablenode.signature,
						   labelnode_32_,
						   labelnode_33_,
						   localvariablenode.index));
	}
    }
    
    private static void log(String string) {
	System.err.println(string);
    }
    
    /*synthetic*/ static Class class$(String string) {
	Class var_class;
	try {
	    var_class = Class.forName(string);
	} catch (ClassNotFoundException classnotfoundexception) {
	    String string_34_ = classnotfoundexception.getMessage();
	    throw new NoClassDefFoundError(string_34_);
	}
	return var_class;
    }
}
