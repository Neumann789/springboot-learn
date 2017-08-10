package com.strobel.assembler.ir;

import com.strobel.core.Comparer;
import com.strobel.core.Predicate;
import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.ast.Range;

public final class InstructionBlock {
	private final Instruction _firstInstruction;
	private final Instruction _lastInstruction;

	public InstructionBlock(Instruction firstInstruction, Instruction lastInstruction) {
		this._firstInstruction = ((Instruction) VerifyArgument.notNull(firstInstruction, "firstInstruction"));
		this._lastInstruction = lastInstruction;
	}

	public final Instruction getFirstInstruction() {
		return this._firstInstruction;
	}

	public final Instruction getLastInstruction() {
		return this._lastInstruction;
	}

	public final boolean contains(Instruction instruction) {
		return (instruction != null) && (instruction.getOffset() >= getFirstInstruction().getOffset())
				&& (instruction.getOffset() <= getLastInstruction().getOffset());
	}

	public final boolean contains(InstructionBlock block) {
		return (block != null) && (block.getFirstInstruction().getOffset() >= getFirstInstruction().getOffset())
				&& (block.getLastInstruction().getOffset() <= getLastInstruction().getOffset());
	}

	public final boolean contains(Range range) {
		return (range != null) && (range.getStart() >= getFirstInstruction().getOffset())
				&& (range.getEnd() <= getLastInstruction().getEndOffset());
	}

	public final boolean intersects(InstructionBlock block) {
		return (block != null) && (block.getFirstInstruction().getOffset() <= getLastInstruction().getOffset())
				&& (block.getLastInstruction().getOffset() >= getFirstInstruction().getOffset());
	}

	public final boolean intersects(Range range) {
		return (range != null) && (range.getStart() <= getLastInstruction().getOffset())
				&& (range.getEnd() >= getFirstInstruction().getOffset());
	}

	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if ((o instanceof InstructionBlock)) {
			InstructionBlock block = (InstructionBlock) o;

			return (Comparer.equals(this._firstInstruction, block._firstInstruction))
					&& (Comparer.equals(this._lastInstruction, block._lastInstruction));
		}

		return false;
	}

	public final int hashCode() {
		int result = this._firstInstruction != null ? this._firstInstruction.hashCode() : 0;
		result = 31 * result + (this._lastInstruction != null ? this._lastInstruction.hashCode() : 0);
		return result;
	}

	public static final Predicate<InstructionBlock> containsInstructionPredicate(final Instruction instruction) {
		return new Predicate<InstructionBlock>() {
			public boolean test(InstructionBlock b) {
				return b.contains(instruction);
			}
		};
	}

	public static final Predicate<InstructionBlock> containsBlockPredicate(final InstructionBlock block) {
		return new Predicate<InstructionBlock>() {
			public boolean test(InstructionBlock b) {
				return b.contains(block);
			}
		};
	}
}
