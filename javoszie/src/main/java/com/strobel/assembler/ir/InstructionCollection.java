package com.strobel.assembler.ir;

import com.strobel.annotations.NotNull;
import com.strobel.assembler.Collection;
import com.strobel.core.CollectionUtilities;
import java.util.Collections;
import java.util.Comparator;

public final class InstructionCollection extends Collection<Instruction> {
	public Instruction atOffset(int offset) {
		Instruction result = tryGetAtOffset(offset);

		if (result != null) {
			return result;
		}

		throw new IndexOutOfBoundsException("No instruction found at offset " + offset + '.');
	}

	public Instruction tryGetAtOffset(int offset) {
		int index = Collections.binarySearch(this, new Instruction(offset, OpCode.NOP), new Comparator<Instruction>() {

			public int compare(@NotNull Instruction o1, @NotNull Instruction o2) {

				return Integer.compare(o1.getOffset(), o2.getOffset());
			}
		});

		if (index < 0) {
			Instruction last = (Instruction) CollectionUtilities.lastOrDefault(this);

			if ((last != null) && (last.getNext() != null) && (last.getNext().getOffset() == offset)) {

				return last.getNext();
			}

			return null;
		}

		return (Instruction) get(index);
	}

	protected void afterAdd(int index, Instruction item, boolean appended) {
		Instruction next = index < size() - 1 ? (Instruction) get(index + 1) : null;
		Instruction previous = index > 0 ? (Instruction) get(index - 1) : null;

		if (previous != null) {
			previous.setNext(item);
		}

		if (next != null) {
			next.setPrevious(item);
		}

		item.setPrevious(previous);
		item.setNext(next);
	}

	protected void beforeSet(int index, Instruction item) {
		Instruction current = (Instruction) get(index);

		item.setPrevious(current.getPrevious());
		item.setNext(current.getNext());

		current.setPrevious(null);
		current.setNext(null);
	}

	protected void afterRemove(int index, Instruction item) {
		Instruction current = item.getNext();
		Instruction previous = item.getPrevious();

		if (previous != null) {
			previous.setNext(current);
		}

		if (current != null) {
			current.setPrevious(previous);
		}

		item.setPrevious(null);
		item.setNext(null);
	}

	protected void beforeClear() {
		for (int i = 0; i < size(); i++) {
			((Instruction) get(i)).setNext(null);
			((Instruction) get(i)).setPrevious(null);
		}
	}

	public void recomputeOffsets() {
		if (isEmpty()) {
			return;
		}

		Instruction previous = (Instruction) get(0);

		previous.setOffset(0);

		for (int i = 1; i < size(); i++) {
			Instruction current = (Instruction) get(i);
			current.setOffset(previous.getOffset() + previous.getSize());
			previous = current;
		}
	}
}
