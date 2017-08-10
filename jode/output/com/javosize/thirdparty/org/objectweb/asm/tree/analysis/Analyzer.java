/* Analyzer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree.analysis;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.javosize.thirdparty.org.objectweb.asm.Opcodes;
import com.javosize.thirdparty.org.objectweb.asm.Type;
import com.javosize.thirdparty.org.objectweb.asm.tree.AbstractInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.IincInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.InsnList;
import com.javosize.thirdparty.org.objectweb.asm.tree.JumpInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.LabelNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.LookupSwitchInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.MethodNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.TableSwitchInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.TryCatchBlockNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.VarInsnNode;

public class Analyzer implements Opcodes
{
    private final Interpreter interpreter;
    private int n;
    private InsnList insns;
    private List[] handlers;
    private Frame[] frames;
    private Subroutine[] subroutines;
    private boolean[] queued;
    private int[] queue;
    private int top;
    
    public Analyzer(Interpreter interpreter) {
	this.interpreter = interpreter;
    }
    
    public Frame[] analyze(String string, MethodNode methodnode)
	throws AnalyzerException {
	Frame frame_10_;
	Type[] types;
	int i;
    label_485:
	{
	    if ((methodnode.access & 0x500) == 0) {
		n = methodnode.instructions.size();
		insns = methodnode.instructions;
		handlers = new List[n];
		frames = new Frame[n];
		subroutines = new Subroutine[n];
		queued = new boolean[n];
		queue = new int[n];
		top = 0;
		int i_0_ = 0;
		for (;;) {
		    if (i_0_ >= methodnode.tryCatchBlocks.size()) {
			Subroutine subroutine
			    = new Subroutine(null, methodnode.maxLocals, null);
			ArrayList arraylist = new ArrayList();
			HashMap hashmap = new HashMap();
			findSubroutine(0, subroutine, arraylist);
			for (;;) {
			    if (arraylist.isEmpty()) {
				int i_1_ = 0;
			    while_3_:
				for (;;) {
				label_484:
				    {
					if (i_1_ >= n) {
					    frame_10_ = newFrame((methodnode
								  .maxLocals),
								 (methodnode
								  .maxStack));
					    Frame frame_2_
						= newFrame((methodnode
							    .maxLocals),
							   (methodnode
							    .maxStack));
					    frame_10_.setReturn
						(interpreter.newValue
						 (Type.getReturnType(methodnode
								     .desc)));
					    types = (Type.getArgumentTypes
						     (methodnode.desc));
					    i = 0;
					    if ((methodnode.access & 0x8)
						== 0) {
						Type type = (Type.getObjectType
							     (string));
						frame_10_.setLocal
						    (i++, interpreter
							      .newValue(type));
					    }
					} else {
					    if (subroutines[i_1_] != null
						&& (subroutines[i_1_].start
						    == null))
						subroutines[i_1_] = null;
					    break label_484;
					}
					break while_3_;
				    }
				    i_1_++;
				}
			    } else {
				JumpInsnNode jumpinsnnode
				    = (JumpInsnNode) arraylist.remove(0);
				Subroutine subroutine_3_
				    = ((Subroutine)
				       hashmap.get(jumpinsnnode.label));
				if (subroutine_3_ != null)
				    subroutine_3_.callers.add(jumpinsnnode);
				else {
				    subroutine_3_
					= new Subroutine(jumpinsnnode.label,
							 methodnode.maxLocals,
							 jumpinsnnode);
				    hashmap.put(jumpinsnnode.label,
						subroutine_3_);
				    findSubroutine(insns.indexOf(jumpinsnnode
								 .label),
						   subroutine_3_, arraylist);
				}
				continue;
			    }
			    break;
			}
		    } else {
			TryCatchBlockNode trycatchblocknode
			    = ((TryCatchBlockNode)
			       methodnode.tryCatchBlocks.get(i_0_));
			int i_4_ = insns.indexOf(trycatchblocknode.start);
			int i_5_ = insns.indexOf(trycatchblocknode.end);
			int i_6_ = i_4_;
			for (;;) {
			    if (i_6_ >= i_5_)
				i_0_++;
			    List list;
			label_483:
			    {
				list = handlers[i_6_];
				if (list == null) {
				    list = new ArrayList();
				    handlers[i_6_] = list;
				}
				break label_483;
			    }
			    list.add(trycatchblocknode);
			    i_6_++;
			}
		    }
		    break label_485;
		}
	    } else {
		frames = new Frame[0];
		return frames;
	    }
	}
	int i_7_ = 0;
	for (;;) {
	    if (i_7_ >= types.length) {
		for (;;) {
		    if (i >= methodnode.maxLocals) {
			merge(0, frame_10_, null);
			init(string, methodnode);
			GOTO flow_31_61_
		    }
		    frame_10_.setLocal(i++, interpreter.newValue(null));
		}
	    }
	label_486:
	    {
		frame_10_.setLocal(i++, interpreter.newValue(types[i_7_]));
		if (types[i_7_].getSize() == 2)
		    frame_10_.setLocal(i++, interpreter.newValue(null));
		break label_486;
	    }
	    i_7_++;
	}
	break label_485;
    flow_31_61_:
	IF (top <= 0)
	    GOTO flow_88_62_
	int i_21_ = queue[--top];
	Frame frame = frames[i_21_];
	Subroutine subroutine = subroutines[i_21_];
	queued[i_21_] = false;
	AbstractInsnNode abstractinsnnode = null;
    label_501:
	{
	label_487:
	    {
		int i_8_;
		int i_9_;
		try {
		    abstractinsnnode = methodnode.instructions.get(i_21_);
		    i_8_ = abstractinsnnode.getOpcode();
		    i_9_ = abstractinsnnode.getType();
		    if (i_9_ == 8)
			break label_487;
		} catch (AnalyzerException PUSH) {
		    GOTO flow_86_63_
		} catch (Exception PUSH) {
		    GOTO flow_87_64_
		}
	    label_488:
		{
		    try {
			if (i_9_ != 15) {
			    try {
				if (i_9_ != 14)
				    break label_488;
			    } catch (AnalyzerException PUSH) {
				GOTO flow_86_63_
			    } catch (Exception PUSH) {
				GOTO flow_87_64_
			    }
			}
		    } catch (AnalyzerException PUSH) {
			GOTO flow_86_63_
		    } catch (Exception PUSH) {
			GOTO flow_87_64_
		    }
		}
		Frame frame_10_;
	    label_490:
		{
		label_489:
		    {
			try {
			    frame_10_.init(frame).execute(abstractinsnnode,
							  interpreter);
			    if (subroutine != null)
				break label_489;
			} catch (AnalyzerException PUSH) {
			    GOTO flow_86_63_
			} catch (Exception PUSH) {
			    GOTO flow_87_64_
			}
		    }
		    try {
			PUSH subroutine.copy();
		    } catch (AnalyzerException PUSH) {
			GOTO flow_86_63_
		    } catch (Exception PUSH) {
			GOTO flow_87_64_
		    }
		    break label_490;
		    try {
			PUSH null;
		    } catch (AnalyzerException PUSH) {
			GOTO flow_86_63_
		    } catch (Exception PUSH) {
			GOTO flow_87_64_
		    }
		}
	    label_494:
		{
		    try {
			subroutine = POP;
			if (!(abstractinsnnode instanceof JumpInsnNode))
			    break label_494;
		    } catch (AnalyzerException PUSH) {
			GOTO flow_86_63_
		    } catch (Exception PUSH) {
			GOTO flow_87_64_
		    }
		}
		LookupSwitchInsnNode lookupswitchinsnnode;
		int i_11_;
		try {
		    if (abstractinsnnode instanceof LookupSwitchInsnNode) {
			try {
			    lookupswitchinsnnode
				= (LookupSwitchInsnNode) abstractinsnnode;
			    int i_12_
				= insns.indexOf(lookupswitchinsnnode.dflt);
			    merge(i_12_, frame_10_, subroutine);
			    newControlFlowEdge(i_21_, i_12_);
			    i_11_ = 0;
			} catch (AnalyzerException PUSH) {
			    GOTO flow_86_63_
			} catch (Exception PUSH) {
			    GOTO flow_87_64_
			}
		    }
		} catch (AnalyzerException PUSH) {
		    GOTO flow_86_63_
		} catch (Exception PUSH) {
		    GOTO flow_87_64_
		}
		TableSwitchInsnNode tableswitchinsnnode;
		try {
		    if (abstractinsnnode instanceof TableSwitchInsnNode) {
			try {
			    tableswitchinsnnode
				= (TableSwitchInsnNode) abstractinsnnode;
			    int i_13_
				= insns.indexOf(tableswitchinsnnode.dflt);
			    merge(i_13_, frame_10_, subroutine);
			    newControlFlowEdge(i_21_, i_13_);
			    i_11_ = 0;
			} catch (AnalyzerException PUSH) {
			    GOTO flow_86_63_
			} catch (Exception PUSH) {
			    GOTO flow_87_64_
			}
		    }
		} catch (AnalyzerException PUSH) {
		    GOTO flow_86_63_
		} catch (Exception PUSH) {
		    GOTO flow_87_64_
		}
	    label_495:
		{
		    try {
			if (i_8_ == 169) {
			    try {
				if (subroutine != null)
				    break label_495;
			    } catch (AnalyzerException PUSH) {
				GOTO flow_86_63_
			    } catch (Exception PUSH) {
				GOTO flow_87_64_
			    }
			}
		    } catch (AnalyzerException PUSH) {
			GOTO flow_86_63_
		    } catch (Exception PUSH) {
			GOTO flow_87_64_
		    }
		label_497:
		    {
			try {
			    if (i_8_ == 191)
				break label_501;
			    try {
				if (i_8_ < 172)
				    break label_497;
			    } catch (AnalyzerException PUSH) {
				GOTO flow_86_63_
			    } catch (Exception PUSH) {
				GOTO flow_87_64_
			    }
			} catch (AnalyzerException PUSH) {
			    GOTO flow_86_63_
			} catch (Exception PUSH) {
			    GOTO flow_87_64_
			}
			try {
			    if (i_8_ <= 177)
				break label_501;
			} catch (AnalyzerException PUSH) {
			    GOTO flow_86_63_
			} catch (Exception PUSH) {
			    GOTO flow_87_64_
			}
		    }
		label_500:
		    {
		    label_499:
			{
			    try {
				if (subroutine == null)
				    break label_500;
				try {
				    if (!(abstractinsnnode
					  instanceof VarInsnNode))
					break label_499;
				} catch (AnalyzerException PUSH) {
				    GOTO flow_86_63_
				} catch (Exception PUSH) {
				    GOTO flow_87_64_
				}
			    } catch (AnalyzerException PUSH) {
				GOTO flow_86_63_
			    } catch (Exception PUSH) {
				GOTO flow_87_64_
			    }
			}
			try {
			    if (abstractinsnnode instanceof IincInsnNode) {
				try {
				    int i_14_
					= (((IincInsnNode) abstractinsnnode)
					   .var);
				    subroutine.access[i_14_] = true;
				} catch (AnalyzerException PUSH) {
				    GOTO flow_86_63_
				} catch (Exception PUSH) {
				    GOTO flow_87_64_
				}
			    }
			} catch (AnalyzerException PUSH) {
			    GOTO flow_86_63_
			} catch (Exception PUSH) {
			    GOTO flow_87_64_
			}
			break label_500;
			int i_15_;
		    label_498:
			{
			    try {
				i_15_ = ((VarInsnNode) abstractinsnnode).var;
				subroutine.access[i_15_] = true;
				if (i_8_ == 22)
				    break label_498;
			    } catch (AnalyzerException PUSH) {
				GOTO flow_86_63_
			    } catch (Exception PUSH) {
				GOTO flow_87_64_
			    }
			    try {
				if (i_8_ == 24)
				    break label_498;
				try {
				    if (i_8_ == 55)
					break label_498;
				} catch (AnalyzerException PUSH) {
				    GOTO flow_86_63_
				} catch (Exception PUSH) {
				    GOTO flow_87_64_
				}
			    } catch (AnalyzerException PUSH) {
				GOTO flow_86_63_
			    } catch (Exception PUSH) {
				GOTO flow_87_64_
			    }
			    try {
				if (i_8_ != 57)
				    break label_500;
			    } catch (AnalyzerException PUSH) {
				GOTO flow_86_63_
			    } catch (Exception PUSH) {
				GOTO flow_87_64_
			    }
			}
			try {
			    subroutine.access[i_15_ + 1] = true;
			} catch (AnalyzerException PUSH) {
			    GOTO flow_86_63_
			} catch (Exception PUSH) {
			    GOTO flow_87_64_
			}
		    }
		    try {
			merge(i_21_ + 1, frame_10_, subroutine);
			newControlFlowEdge(i_21_, i_21_ + 1);
		    } catch (AnalyzerException PUSH) {
			GOTO flow_86_63_
		    } catch (Exception PUSH) {
			GOTO flow_87_64_
		    }
		    break label_501;
		}
		try {
		    int i_16_ = 0;
		    try {
			for (/**/; i_16_ < subroutine.callers.size();
			     i_16_++) {
			    JumpInsnNode jumpinsnnode
				= (JumpInsnNode) subroutine.callers.get(i_16_);
			label_496:
			    {
				i_11_ = insns.indexOf(jumpinsnnode);
				if (frames[i_11_] != null) {
				    merge(i_11_ + 1, frames[i_11_], frame_10_,
					  subroutines[i_11_],
					  subroutine.access);
				    newControlFlowEdge(i_21_, i_11_ + 1);
				}
				break label_496;
			    }
			}
		    } catch (AnalyzerException PUSH) {
			GOTO flow_86_63_
		    } catch (Exception PUSH) {
			GOTO flow_87_64_
		    }
		} catch (AnalyzerException PUSH) {
		    GOTO flow_86_63_
		} catch (Exception PUSH) {
		    GOTO flow_87_64_
		}
		break label_501;
		try {
		    throw new AnalyzerException
			      (abstractinsnnode,
			       "RET instruction outside of a sub routine");
		} catch (AnalyzerException PUSH) {
		    GOTO flow_86_63_
		} catch (Exception PUSH) {
		    GOTO flow_87_64_
		}
		try {
		    for (/**/; i_11_ < tableswitchinsnnode.labels.size();
			 i_11_++) {
			LabelNode labelnode
			    = ((LabelNode)
			       tableswitchinsnnode.labels.get(i_11_));
			int i_17_ = insns.indexOf(labelnode);
			merge(i_17_, frame_10_, subroutine);
			newControlFlowEdge(i_21_, i_17_);
		    }
		} catch (AnalyzerException PUSH) {
		    GOTO flow_86_63_
		} catch (Exception PUSH) {
		    GOTO flow_87_64_
		}
		break label_501;
		try {
		    for (/**/; i_11_ < lookupswitchinsnnode.labels.size();
			 i_11_++) {
			LabelNode labelnode
			    = ((LabelNode)
			       lookupswitchinsnnode.labels.get(i_11_));
			int i_18_ = insns.indexOf(labelnode);
			merge(i_18_, frame_10_, subroutine);
			newControlFlowEdge(i_21_, i_18_);
		    }
		} catch (AnalyzerException PUSH) {
		    GOTO flow_86_63_
		} catch (Exception PUSH) {
		    GOTO flow_87_64_
		}
		break label_501;
		JumpInsnNode jumpinsnnode;
	    label_491:
		{
		    try {
			jumpinsnnode = (JumpInsnNode) abstractinsnnode;
			if (i_8_ == 167)
			    break label_491;
		    } catch (AnalyzerException PUSH) {
			GOTO flow_86_63_
		    } catch (Exception PUSH) {
			GOTO flow_87_64_
		    }
		    try {
			if (i_8_ != 168) {
			    try {
				merge(i_21_ + 1, frame_10_, subroutine);
				newControlFlowEdge(i_21_, i_21_ + 1);
			    } catch (AnalyzerException PUSH) {
				GOTO flow_86_63_
			    } catch (Exception PUSH) {
				GOTO flow_87_64_
			    }
			}
		    } catch (AnalyzerException PUSH) {
			GOTO flow_86_63_
		    } catch (Exception PUSH) {
			GOTO flow_87_64_
		    }
		}
		int i_19_;
	    label_493:
		{
		label_492:
		    {
			try {
			    i_19_ = insns.indexOf(jumpinsnnode.label);
			    if (i_8_ != 168)
				break label_492;
			} catch (AnalyzerException PUSH) {
			    GOTO flow_86_63_
			} catch (Exception PUSH) {
			    GOTO flow_87_64_
			}
		    }
		    try {
			merge(i_19_, frame_10_, subroutine);
		    } catch (AnalyzerException PUSH) {
			GOTO flow_86_63_
		    } catch (Exception PUSH) {
			GOTO flow_87_64_
		    }
		    break label_493;
		    try {
			merge(i_19_, frame_10_,
			      new Subroutine(jumpinsnnode.label,
					     methodnode.maxLocals,
					     jumpinsnnode));
		    } catch (AnalyzerException PUSH) {
			GOTO flow_86_63_
		    } catch (Exception PUSH) {
			GOTO flow_87_64_
		    }
		}
		try {
		    newControlFlowEdge(i_21_, i_19_);
		} catch (AnalyzerException PUSH) {
		    GOTO flow_86_63_
		} catch (Exception PUSH) {
		    GOTO flow_87_64_
		}
		break label_501;
	    }
	    try {
		merge(i_21_ + 1, frame, subroutine);
		newControlFlowEdge(i_21_, i_21_ + 1);
	    } catch (AnalyzerException PUSH) {
		GOTO flow_86_63_
	    } catch (Exception PUSH) {
		GOTO flow_87_64_
	    }
	}
	try {
	    List list = handlers[i_21_];
	    IF (list == null)
		GOTO flow_31_61_
	} catch (AnalyzerException PUSH) {
	    GOTO flow_86_63_
	} catch (Exception PUSH) {
	    GOTO flow_87_64_
	}
	try {
	    int i_20_ = 0;
	    GOTO flow_79_65_
	} catch (AnalyzerException PUSH) {
	    GOTO flow_86_63_
	} catch (Exception PUSH) {
	    GOTO flow_87_64_
	}
    flow_79_65_:
	int i_20_;
	List list;
	IF (i_20_ >= list.size())
	    GOTO flow_31_61_
	TryCatchBlockNode trycatchblocknode;
	Type type;
    label_502:
	{
	    trycatchblocknode = (TryCatchBlockNode) list.get(i_20_);
	    if (trycatchblocknode.type != null)
		type = Type.getObjectType(trycatchblocknode.type);
	    else
		type = Type.getObjectType("java/lang/Throwable");
	    break label_502;
	}
    label_503:
	{
	    int i = insns.indexOf(trycatchblocknode.handler);
	    int i_21_;
	    if (newControlFlowExceptionEdge(i_21_, trycatchblocknode)) {
		Frame frame_2_;
		Frame frame;
		frame_2_.init(frame);
		frame_2_.clearStack();
		frame_2_.push(interpreter.newValue(type));
		Subroutine subroutine;
		merge(i, frame_2_, subroutine);
	    }
	    break label_503;
	}
	i_20_++;
	GOTO flow_79_65_
    flow_86_63_:
	AnalyzerException analyzerexception = POP;
	int i_21_;
	throw new AnalyzerException(analyzerexception.node,
				    ("Error at instruction " + i_21_ + ": "
				     + analyzerexception.getMessage()),
				    analyzerexception);
    flow_87_64_:
	Exception exception = POP;
	AbstractInsnNode abstractinsnnode;
	int i_21_;
	throw new AnalyzerException(abstractinsnnode,
				    ("Error at instruction " + i_21_ + ": "
				     + exception.getMessage()),
				    exception);
    flow_88_62_:
	return frames;
	GOTO END_OF_METHOD
    }
    
    private void findSubroutine(int i, Subroutine subroutine, List list)
	throws AnalyzerException {
	for (;;) {
	    AbstractInsnNode abstractinsnnode;
	label_504:
	    {
		if (i >= 0 && i < n) {
		    if (subroutines[i] != null)
			return;
		    subroutines[i] = subroutine.copy();
		    abstractinsnnode = insns.get(i);
		    if (!(abstractinsnnode instanceof JumpInsnNode)) {
			if (!(abstractinsnnode
			      instanceof TableSwitchInsnNode)) {
			    if (abstractinsnnode
				instanceof LookupSwitchInsnNode) {
				LookupSwitchInsnNode lookupswitchinsnnode
				    = (LookupSwitchInsnNode) abstractinsnnode;
				findSubroutine
				    (insns.indexOf(lookupswitchinsnnode.dflt),
				     subroutine, list);
				for (int i_22_ = lookupswitchinsnnode
						     .labels.size() - 1;
				     i_22_ >= 0; i_22_--) {
				    LabelNode labelnode
					= (LabelNode) lookupswitchinsnnode
							  .labels.get(i_22_);
				    findSubroutine(insns.indexOf(labelnode),
						   subroutine, list);
				}
			    }
			} else {
			    TableSwitchInsnNode tableswitchinsnnode
				= (TableSwitchInsnNode) abstractinsnnode;
			    findSubroutine(insns.indexOf(tableswitchinsnnode
							 .dflt),
					   subroutine, list);
			    for (int i_23_
				     = tableswitchinsnnode.labels.size() - 1;
				 i_23_ >= 0; i_23_--) {
				LabelNode labelnode
				    = ((LabelNode)
				       tableswitchinsnnode.labels.get(i_23_));
				findSubroutine(insns.indexOf(labelnode),
					       subroutine, list);
			    }
			}
		    } else if (abstractinsnnode.getOpcode() != 168) {
			JumpInsnNode jumpinsnnode
			    = (JumpInsnNode) abstractinsnnode;
			findSubroutine(insns.indexOf(jumpinsnnode.label),
				       subroutine, list);
		    } else
			list.add(abstractinsnnode);
		} else
		    throw new AnalyzerException
			      (null, "Execution can fall off end of the code");
	    }
	label_505:
	    {
		List list_24_ = handlers[i];
		if (list_24_ != null) {
		    for (int i_25_ = 0; i_25_ < list_24_.size(); i_25_++) {
			TryCatchBlockNode trycatchblocknode
			    = (TryCatchBlockNode) list_24_.get(i_25_);
			findSubroutine(insns
					   .indexOf(trycatchblocknode.handler),
				       subroutine, list);
		    }
		}
		break label_505;
	    }
	    switch (abstractinsnnode.getOpcode()) {
	    default:
		i++;
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
	    }
	    break label_504;
	}
    }
    
    public Frame[] getFrames() {
	return frames;
    }
    
    public List getHandlers(int i) {
	return handlers[i];
    }
    
    protected void init(String string, MethodNode methodnode)
	throws AnalyzerException {
	/* empty */
    }
    
    protected Frame newFrame(int i, int i_26_) {
	return new Frame(i, i_26_);
    }
    
    protected Frame newFrame(Frame frame) {
	return new Frame(frame);
    }
    
    protected void newControlFlowEdge(int i, int i_27_) {
	/* empty */
    }
    
    protected boolean newControlFlowExceptionEdge(int i, int i_28_) {
	return true;
    }
    
    protected boolean newControlFlowExceptionEdge
	(int i, TryCatchBlockNode trycatchblocknode) {
	return newControlFlowExceptionEdge(i, insns.indexOf(trycatchblocknode
							    .handler));
    }
    
    private void merge(int i, Frame frame, Subroutine subroutine)
	throws AnalyzerException {
	Frame frame_29_ = frames[i];
	boolean bool;
    label_507:
	{
	    Subroutine subroutine_30_;
	label_506:
	    {
		subroutine_30_ = subroutines[i];
		if (frame_29_ != null)
		    bool = frame_29_.merge(frame, interpreter);
		else {
		    frames[i] = newFrame(frame);
		    bool = true;
		}
		break label_506;
	    }
	    if (subroutine_30_ != null) {
		if (subroutine != null)
		    bool |= subroutine_30_.merge(subroutine);
	    } else if (subroutine != null) {
		subroutines[i] = subroutine.copy();
		bool = true;
	    }
	    break label_507;
	}
	if (bool && !queued[i]) {
	    queued[i] = true;
	    queue[top++] = i;
	}
	return;
    }
    
    private void merge(int i, Frame frame, Frame frame_31_,
		       Subroutine subroutine, boolean[] bools)
	throws AnalyzerException {
	Frame frame_32_ = frames[i];
	Subroutine subroutine_33_ = subroutines[i];
	boolean bool;
    label_509:
	{
	label_508:
	    {
		frame_31_.merge(frame, bools);
		if (frame_32_ != null)
		    bool = frame_32_.merge(frame_31_, interpreter);
		else {
		    frames[i] = newFrame(frame_31_);
		    bool = true;
		}
		break label_508;
	    }
	    if (subroutine_33_ != null && subroutine != null)
		bool |= subroutine_33_.merge(subroutine);
	    break label_509;
	}
	if (bool && !queued[i]) {
	    queued[i] = true;
	    queue[top++] = i;
	}
	return;
    }
}
