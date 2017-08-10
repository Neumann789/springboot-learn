/* DeadCodeAnalysis - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import java.util.Iterator;

import jode.bytecode.BytecodeInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;

public class DeadCodeAnalysis
{
    private static final String REACHABLE = "R";
    private static final String REACHCHANGED = "C";
    
    private static void propagateReachability(BytecodeInfo bytecodeinfo) {
	for (;;) {
	    boolean bool = false;
	    Iterator iterator = bytecodeinfo.getInstructions().iterator();
	    for (;;) {
		if (!iterator.hasNext()) {
		    IF (!bool)
			/* empty */
		}
		Instruction instruction = (Instruction) iterator.next();
		if (instruction.getTmpInfo() == "C") {
		    bool = true;
		    instruction.setTmpInfo("R");
		label_842:
		    {
		    label_841:
			{
			    Instruction[] instructions
				= instruction.getSuccs();
			    if (instructions != null) {
				int i = 0;
			    while_4_:
				for (;;) {
				label_840:
				    {
					if (i < instructions.length) {
					    if (instructions[i].getTmpInfo()
						== null)
						instructions[i]
						    .setTmpInfo("C");
					    break label_840;
					}
					break while_4_;
				    }
				    i++;
				}
			    }
			    break label_841;
			}
			if (!instruction.doesAlwaysJump()
			    && instruction.getNextByAddr() != null
			    && (instruction.getNextByAddr().getTmpInfo()
				== null))
			    instruction.getNextByAddr().setTmpInfo("C");
			break label_842;
		    }
		    if (instruction.getOpcode() == 168
			&& instruction.getNextByAddr().getTmpInfo() == null)
			instruction.getNextByAddr().setTmpInfo("C");
		}
		continue;
	    }
	}
    }
    
    public static void removeDeadCode(BytecodeInfo bytecodeinfo) {
	((Instruction) bytecodeinfo.getInstructions().get(0)).setTmpInfo("C");
	propagateReachability(bytecodeinfo);
	Handler[] handlers = bytecodeinfo.getExceptionHandlers();
	for (;;) {
	    boolean bool = false;
	    int i = 0;
	    for (;;) {
	    label_843:
		{
		    if (i >= handlers.length) {
			if (!bool) {
			    i = 0;
			    for (;;) {
			    label_844:
				{
				    if (i >= handlers.length) {
					Iterator iterator
					    = bytecodeinfo.getInstructions
						  ().iterator();
					for (;;) {
					    IF (!iterator.hasNext())
						/* empty */
					    Instruction instruction
						= ((Instruction)
						   iterator.next());
					    if (instruction.getTmpInfo()
						== null)
						iterator.remove();
					    else
						instruction.setTmpInfo(null);
					    continue;
					}
				    }
				    if (handlers[i].catcher.getTmpInfo()
					!= null) {
					for (;;) {
					    if (handlers[i].start.getTmpInfo()
						!= null) {
						while (handlers[i].end
							   .getTmpInfo()
						       == null)
						    handlers[i].end
							= handlers[i].end
							      .getPrevByAddr();
						break label_844;
					    }
					    handlers[i].start
						= handlers[i].start
						      .getNextByAddr();
					}
				    } else {
					Handler[] handlers_0_
					    = new Handler[handlers.length - 1];
					System.arraycopy(handlers, 0,
							 handlers_0_, 0, i);
					System.arraycopy(handlers, i + 1,
							 handlers_0_, i,
							 (handlers.length
							  - (i + 1)));
					handlers = handlers_0_;
					bytecodeinfo
					    .setExceptionHandlers(handlers_0_);
					i--;
				    }
				    break label_844;
				}
				i++;
			    }
			}
		    }
		    if (handlers[i].catcher.getTmpInfo() == null) {
			for (Instruction instruction = handlers[i].start;
			     instruction != null;
			     instruction = instruction.getNextByAddr()) {
			    if (instruction.getTmpInfo() == null) {
				if (instruction == handlers[i].end)
				    break;
			    }
			    handlers[i].catcher.setTmpInfo("C");
			    propagateReachability(bytecodeinfo);
			    bool = true;
			    break;
			}
		    }
		    break label_843;
		}
		i++;
	    }
	}
    }
}
