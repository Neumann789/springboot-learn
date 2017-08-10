/* StackMapFrame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir;
import com.strobel.core.VerifyArgument;

public final class StackMapFrame
{
    private final Frame _frame;
    private final Instruction _startInstruction;
    
    public StackMapFrame(Frame frame, Instruction startInstruction) {
	_frame = (Frame) VerifyArgument.notNull(frame, "frame");
	_startInstruction
	    = (Instruction) VerifyArgument.notNull(startInstruction,
						   "startInstruction");
    }
    
    public final Frame getFrame() {
	return _frame;
    }
    
    public final Instruction getStartInstruction() {
	return _startInstruction;
    }
    
    public final String toString() {
	return String.format("#%1$04d: %2$s",
			     (new Object[]
			      { Integer.valueOf(_startInstruction.getOffset()),
				_frame }));
    }
}
