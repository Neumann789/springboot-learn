package com.strobel.assembler.metadata;

import com.strobel.assembler.Collection;
import com.strobel.assembler.ir.OpCode;
import com.strobel.assembler.ir.OperandType;
import com.strobel.core.VerifyArgument;
import com.strobel.util.ContractUtils;
import java.util.NoSuchElementException;

public final class VariableDefinitionCollection extends Collection<VariableDefinition> {
	private final MethodDefinition _declaringMethod;

	public VariableDefinitionCollection(MethodDefinition declaringMethod) {
		this._declaringMethod = ((MethodDefinition) VerifyArgument.notNull(declaringMethod, "declaringMethod"));
	}

	public int slotCount() {
		int count = 0;

		for (int i = 0; i < size(); i++) {
			VariableDefinition v = (VariableDefinition) get(i);
			count = Math.max(v.getSlot() + v.getSize(), count);
		}

		return count;
	}

	public VariableDefinition tryFind(int slot) {
		return find(slot, -1);
	}

	public VariableDefinition tryFind(int slot, int instructionOffset) {
		VariableDefinition result = null;

		for (int i = 0; i < size(); i++) {
			VariableDefinition variable = (VariableDefinition) get(i);

			if ((variable.getSlot() == slot)
					&& ((instructionOffset < 0)
							|| ((variable.getScopeStart() >= 0) && (variable.getScopeStart() <= instructionOffset)
									&& ((variable.getScopeEnd() < 0) || (variable.getScopeEnd() >= instructionOffset))))
					&& ((result == null) || (variable.getScopeStart() > result.getScopeStart()))) {

				result = variable;
			}
		}

		return result;
	}

	public VariableDefinition find(int slot) {
		return find(slot, -1);
	}

	public VariableDefinition find(int slot, int instructionOffset) {
		VariableDefinition variable = tryFind(slot, instructionOffset);

		if (variable != null) {
			return variable;
		}

		throw new NoSuchElementException(String.format("Could not find variable at slot %d and offset %d.",
				new Object[] { Integer.valueOf(slot), Integer.valueOf(instructionOffset) }));
	}

	public VariableReference tryFind(int slot, OpCode op, int instructionOffset) {
		int effectiveOffset;

		if (op.isStore()) {
			effectiveOffset = instructionOffset + op.getSize() + op.getOperandType().getBaseSize();
		} else {
			effectiveOffset = instructionOffset;
		}

		return tryFind(slot, effectiveOffset);
	}

	public VariableReference reference(int slot, OpCode op, int instructionOffset) {
		VariableReference variable = tryFind(slot, op, instructionOffset);

		if (variable != null) {
			return variable;
		}

		TypeReference variableType;

		switch (op) {
		case ISTORE:
		case ISTORE_0:
		case ISTORE_1:
		case ISTORE_2:
		case ISTORE_3:
		case ISTORE_W:
		case ILOAD:
		case ILOAD_0:
		case ILOAD_1:
		case ILOAD_2:
		case ILOAD_3:
		case ILOAD_W:
			variableType = BuiltinTypes.Integer;
			break;

		case LSTORE:
		case LSTORE_0:
		case LSTORE_1:
		case LSTORE_2:
		case LSTORE_3:
		case LSTORE_W:
		case LLOAD:
		case LLOAD_0:
		case LLOAD_1:
		case LLOAD_2:
		case LLOAD_3:
		case LLOAD_W:
			variableType = BuiltinTypes.Long;
			break;

		case FSTORE:
		case FSTORE_0:
		case FSTORE_1:
		case FSTORE_2:
		case FSTORE_3:
		case FSTORE_W:
		case FLOAD:
		case FLOAD_0:
		case FLOAD_1:
		case FLOAD_2:
		case FLOAD_3:
		case FLOAD_W:
			variableType = BuiltinTypes.Float;
			break;

		case DSTORE:
		case DSTORE_0:
		case DSTORE_1:
		case DSTORE_2:
		case DSTORE_3:
		case DSTORE_W:
		case DLOAD:
		case DLOAD_0:
		case DLOAD_1:
		case DLOAD_2:
		case DLOAD_3:
		case DLOAD_W:
			variableType = BuiltinTypes.Double;
			break;

		case IINC:
		case IINC_W:
			variableType = BuiltinTypes.Integer;
			break;

		default:
			variableType = BuiltinTypes.Object;
		}

		return makeReference(slot, variableType);
	}

	public VariableReference makeReference(int slot, TypeReference variableType) {
		return new UnknownVariableReference(variableType, slot, this._declaringMethod.getDeclaringType());
	}

	private static final class UnknownVariableReference extends VariableReference {
		private final int _slot;

		private final TypeReference _declaringType;

		UnknownVariableReference(TypeReference variableType, int slot, TypeReference declaringType) {
			//super();
			super(variableType);

			this._slot = slot;
			this._declaringType = ((TypeReference) VerifyArgument.notNull(declaringType, "declaringType"));
		}

		public final TypeReference getDeclaringType() {
			return this._declaringType;
		}

		public final int getSlot() {
			return this._slot;
		}

		public final VariableDefinition resolve() {
			throw ContractUtils.unsupported();
		}
	}
}
