/* Opcodes - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import jode.AssertError;
import jode.bytecode.Instruction;
import jode.bytecode.Reference;
import jode.expr.ArrayLengthOperator;
import jode.expr.ArrayLoadOperator;
import jode.expr.ArrayStoreOperator;
import jode.expr.BinaryOperator;
import jode.expr.CheckCastOperator;
import jode.expr.CompareBinaryOperator;
import jode.expr.CompareToIntOperator;
import jode.expr.CompareUnaryOperator;
import jode.expr.ConstOperator;
import jode.expr.ConvertOperator;
import jode.expr.Expression;
import jode.expr.GetFieldOperator;
import jode.expr.IIncOperator;
import jode.expr.InstanceOfOperator;
import jode.expr.InvokeOperator;
import jode.expr.LocalLoadOperator;
import jode.expr.LocalStoreOperator;
import jode.expr.MonitorEnterOperator;
import jode.expr.MonitorExitOperator;
import jode.expr.NewArrayOperator;
import jode.expr.NewOperator;
import jode.expr.NopOperator;
import jode.expr.PutFieldOperator;
import jode.expr.ShiftOperator;
import jode.expr.StoreInstruction;
import jode.expr.UnaryOperator;
import jode.flow.ConditionalBlock;
import jode.flow.EmptyBlock;
import jode.flow.FlowBlock;
import jode.flow.InstructionBlock;
import jode.flow.JsrBlock;
import jode.flow.Jump;
import jode.flow.RetBlock;
import jode.flow.ReturnBlock;
import jode.flow.SpecialBlock;
import jode.flow.StructuredBlock;
import jode.flow.SwitchBlock;
import jode.flow.ThrowBlock;
import jode.type.IntegerType;
import jode.type.Type;

public abstract class Opcodes implements jode.bytecode.Opcodes
{
    private static final Type tIntHint = new IntegerType(2, 30);
    private static final Type tBoolIntHint = new IntegerType(3, 31);
    private static final int LOCAL_TYPES = 0;
    private static final int ARRAY_TYPES = 1;
    private static final int UNARY_TYPES = 2;
    private static final int I2BCS_TYPES = 3;
    private static final int BIN_TYPES = 4;
    private static final int ZBIN_TYPES = 5;
    private static final Type[][] types
	= { { Type.tBoolUInt, Type.tLong, Type.tFloat, Type.tDouble,
	      Type.tUObject },
	    { Type.tInt, Type.tLong, Type.tFloat, Type.tDouble, Type.tUObject,
	      Type.tBoolByte, Type.tChar, Type.tShort },
	    { Type.tInt, Type.tLong, Type.tFloat, Type.tDouble,
	      Type.tUObject },
	    { Type.tByte, Type.tChar, Type.tShort },
	    { tIntHint, Type.tLong, Type.tFloat, Type.tDouble, Type.tUObject },
	    { tBoolIntHint, Type.tLong, Type.tFloat, Type.tDouble,
	      Type.tUObject } };
    
    private static StructuredBlock createNormal(MethodAnalyzer methodanalyzer,
						Instruction instruction,
						Expression expression) {
	return new InstructionBlock(expression,
				    new Jump(FlowBlock.NEXT_BY_ADDR));
    }
    
    private static StructuredBlock createSpecial(MethodAnalyzer methodanalyzer,
						 Instruction instruction,
						 int i, int i_0_, int i_1_) {
	return new SpecialBlock(i, i_0_, i_1_,
				new Jump(FlowBlock.NEXT_BY_ADDR));
    }
    
    private static StructuredBlock createGoto(MethodAnalyzer methodanalyzer,
					      Instruction instruction) {
	return new EmptyBlock(new Jump((FlowBlock) instruction.getSingleSucc
						       ().getTmpInfo()));
    }
    
    private static StructuredBlock createJsr(MethodAnalyzer methodanalyzer,
					     Instruction instruction) {
	return new JsrBlock(new Jump((FlowBlock)
				     instruction.getSingleSucc().getTmpInfo()),
			    new Jump(FlowBlock.NEXT_BY_ADDR));
    }
    
    private static StructuredBlock createIfGoto(MethodAnalyzer methodanalyzer,
						Instruction instruction,
						Expression expression) {
	return new ConditionalBlock(expression,
				    new Jump((FlowBlock) instruction
							     .getSingleSucc
							     ().getTmpInfo()),
				    new Jump(FlowBlock.NEXT_BY_ADDR));
    }
    
    private static StructuredBlock createSwitch(MethodAnalyzer methodanalyzer,
						Instruction instruction,
						int[] is,
						FlowBlock[] flowblocks) {
	return new SwitchBlock(new NopOperator(Type.tUInt), is, flowblocks);
    }
    
    private static StructuredBlock createBlock
	(MethodAnalyzer methodanalyzer, Instruction instruction,
	 StructuredBlock structuredblock) {
	return structuredblock;
    }
    
    private static StructuredBlock createRet(MethodAnalyzer methodanalyzer,
					     Instruction instruction,
					     LocalInfo localinfo) {
	return new RetBlock(localinfo);
    }
    
    public static StructuredBlock readOpcode(Instruction instruction,
					     MethodAnalyzer methodanalyzer)
	throws ClassFormatError {
	int i = instruction.getOpcode();
	switch (i) {
	case 0:
	    return createBlock(methodanalyzer, instruction,
			       new EmptyBlock(new Jump(FlowBlock
						       .NEXT_BY_ADDR)));
	case 18:
	case 20:
	    return createNormal(methodanalyzer, instruction,
				new ConstOperator(instruction.getConstant()));
	case 21:
	case 22:
	case 23:
	case 24:
	case 25:
	    return (createNormal
		    (methodanalyzer, instruction,
		     new LocalLoadOperator(types[0][i - 21], methodanalyzer,
					   (methodanalyzer.getLocalInfo
					    (instruction.getAddr(),
					     instruction.getLocalSlot())))));
	case 46:
	case 47:
	case 48:
	case 49:
	case 50:
	case 51:
	case 52:
	case 53:
	    return createNormal(methodanalyzer, instruction,
				new ArrayLoadOperator(types[1][i - 46]));
	case 54:
	case 55:
	case 56:
	case 57:
	case 58:
	    return createNormal(methodanalyzer, instruction,
				(new StoreInstruction
				 (new LocalStoreOperator
				  (types[0][i - 54],
				   (methodanalyzer.getLocalInfo
				    (instruction.getNextByAddr().getAddr(),
				     instruction.getLocalSlot()))))));
	case 79:
	case 80:
	case 81:
	case 82:
	case 83:
	case 84:
	case 85:
	case 86:
	    return createNormal(methodanalyzer, instruction,
				(new StoreInstruction
				 (new ArrayStoreOperator(types[1][i - 79]))));
	case 87:
	case 88:
	    return createSpecial(methodanalyzer, instruction, SpecialBlock.POP,
				 i - 87 + 1, 0);
	case 89:
	case 90:
	case 91:
	case 92:
	case 93:
	case 94:
	    return createSpecial(methodanalyzer, instruction, SpecialBlock.DUP,
				 (i - 89) / 3 + 1, (i - 89) % 3);
	case 95:
	    return createSpecial(methodanalyzer, instruction,
				 SpecialBlock.SWAP, 1, 0);
	case 96:
	case 97:
	case 98:
	case 99:
	case 100:
	case 101:
	case 102:
	case 103:
	case 104:
	case 105:
	case 106:
	case 107:
	case 108:
	case 109:
	case 110:
	case 111:
	case 112:
	case 113:
	case 114:
	case 115:
	    return createNormal(methodanalyzer, instruction,
				new BinaryOperator(types[4][(i - 96) % 4],
						   (i - 96) / 4 + 1));
	case 116:
	case 117:
	case 118:
	case 119:
	    return createNormal(methodanalyzer, instruction,
				new UnaryOperator(types[2][i - 116], 36));
	case 120:
	case 121:
	case 122:
	case 123:
	case 124:
	case 125:
	    return createNormal(methodanalyzer, instruction,
				new ShiftOperator(types[2][(i - 120) % 2],
						  (i - 120) / 2 + 6));
	case 126:
	case 127:
	case 128:
	case 129:
	case 130:
	case 131:
	    return createNormal(methodanalyzer, instruction,
				new BinaryOperator(types[5][(i - 126) % 2],
						   (i - 126) / 2 + 9));
	case 132: {
	    int i_2_ = instruction.getIncrement();
	    int i_3_;
	label_871:
	    {
		i_3_ = 1;
		if (i_2_ < 0) {
		    i_2_ = -i_2_;
		    i_3_ = 2;
		}
		break label_871;
	    }
	    LocalInfo localinfo
		= methodanalyzer.getLocalInfo(instruction.getAddr(),
					      instruction.getLocalSlot());
	    return (createNormal
		    (methodanalyzer, instruction,
		     new IIncOperator(new LocalStoreOperator(Type.tInt,
							     localinfo),
				      i_2_, i_3_ + 12)));
	}
	case 133:
	case 134:
	case 135:
	case 136:
	case 137:
	case 138:
	case 139:
	case 140:
	case 141:
	case 142:
	case 143:
	case 144: {
	    int i_4_ = (i - 133) / 3;
	    int i_5_;
	label_872:
	    {
		i_5_ = (i - 133) % 3;
		if (i_5_ >= i_4_)
		    i_5_++;
		break label_872;
	    }
	    return createNormal(methodanalyzer, instruction,
				new ConvertOperator(types[2][i_4_],
						    types[2][i_5_]));
	}
	case 145:
	case 146:
	case 147:
	    return createNormal(methodanalyzer, instruction,
				new ConvertOperator(types[2][0],
						    types[3][i - 145]));
	case 148:
	case 149:
	case 150:
	case 151:
	case 152:
	    PUSH methodanalyzer;
	    PUSH instruction;
	    PUSH new CompareToIntOperator;
	    DUP
	label_873:
	    {
		PUSH types[4][(i - 145) / 2];
		if (i != 150 && i != 152)
		    PUSH false;
		else
		    PUSH true;
		break label_873;
	    }
	    ((UNCONSTRUCTED)POP).CompareToIntOperator(POP, POP);
	    return createNormal(POP, POP, POP);
	case 153:
	case 154:
	    return createIfGoto(methodanalyzer, instruction,
				new CompareUnaryOperator(Type.tBoolInt,
							 i - 127));
	case 155:
	case 156:
	case 157:
	case 158:
	    return createIfGoto(methodanalyzer, instruction,
				new CompareUnaryOperator(Type.tInt, i - 127));
	case 159:
	case 160:
	    return createIfGoto(methodanalyzer, instruction,
				new CompareBinaryOperator(tBoolIntHint,
							  i - 133));
	case 161:
	case 162:
	case 163:
	case 164:
	    return createIfGoto(methodanalyzer, instruction,
				new CompareBinaryOperator(tIntHint, i - 133));
	case 165:
	case 166:
	    return createIfGoto(methodanalyzer, instruction,
				new CompareBinaryOperator(Type.tUObject,
							  i - 139));
	case 167:
	    return createGoto(methodanalyzer, instruction);
	case 168:
	    return createJsr(methodanalyzer, instruction);
	case 169:
	    return createRet(methodanalyzer, instruction,
			     methodanalyzer.getLocalInfo(instruction.getAddr(),
							 instruction
							     .getLocalSlot()));
	case 171: {
	    int[] is = instruction.getValues();
	    FlowBlock[] flowblocks
		= new FlowBlock[instruction.getSuccs().length];
	    int i_6_ = 0;
	    for (;;) {
		if (i_6_ >= flowblocks.length) {
		    flowblocks[is.length]
			= ((FlowBlock)
			   instruction.getSuccs()[is.length].getTmpInfo());
		    return createSwitch(methodanalyzer, instruction, is,
					flowblocks);
		}
		flowblocks[i_6_]
		    = (FlowBlock) instruction.getSuccs()[i_6_].getTmpInfo();
		i_6_++;
	    }
	}
	case 172:
	case 173:
	case 174:
	case 175:
	case 176: {
	    Type type = Type.tSubType(methodanalyzer.getReturnType());
	    return createBlock(methodanalyzer, instruction,
			       new ReturnBlock(new NopOperator(type)));
	}
	case 177:
	    return createBlock(methodanalyzer, instruction,
			       new EmptyBlock(new Jump(FlowBlock
						       .END_OF_METHOD)));
	case 178:
	case 180: {
	    Reference reference = instruction.getReference();
	    PUSH methodanalyzer;
	    PUSH instruction;
	    PUSH new GetFieldOperator;
	    DUP
	label_874:
	    {
		PUSH methodanalyzer;
		if (i != 178)
		    PUSH false;
		else
		    PUSH true;
		break label_874;
	    }
	    ((UNCONSTRUCTED)POP).GetFieldOperator(POP, POP, reference);
	    return createNormal(POP, POP, POP);
	}
	case 179:
	case 181: {
	    Reference reference = instruction.getReference();
	    PUSH methodanalyzer;
	    PUSH instruction;
	    PUSH new StoreInstruction;
	    DUP
	    PUSH new PutFieldOperator;
	    DUP
	label_875:
	    {
		PUSH methodanalyzer;
		if (i != 179)
		    PUSH false;
		else
		    PUSH true;
		break label_875;
	    }
	    ((UNCONSTRUCTED)POP).PutFieldOperator(POP, POP, reference);
	    ((UNCONSTRUCTED)POP).StoreInstruction(POP);
	    return createNormal(POP, POP, POP);
	}
	case 182:
	case 183:
	case 184:
	case 185: {
	    Reference reference;
	label_876:
	    {
		reference = instruction.getReference();
		if (!reference.getName().equals("<init>")) {
		    if (i != 184) {
			if (i != 183)
			    PUSH false;
			else
			    PUSH true;
		    } else
			PUSH 2;
		} else
		    PUSH 3;
		break label_876;
	    }
	    int i_7_ = POP;
	    StructuredBlock structuredblock
		= createNormal(methodanalyzer, instruction,
			       new InvokeOperator(methodanalyzer, i_7_,
						  reference));
	    return structuredblock;
	}
	case 187: {
	    Type type = Type.tType(instruction.getClazzType());
	    methodanalyzer.useType(type);
	    return createNormal(methodanalyzer, instruction,
				new NewOperator(type));
	}
	case 190:
	    return createNormal(methodanalyzer, instruction,
				new ArrayLengthOperator());
	case 191:
	    return createBlock(methodanalyzer, instruction,
			       new ThrowBlock(new NopOperator(Type.tUObject)));
	case 192: {
	    Type type = Type.tType(instruction.getClazzType());
	    methodanalyzer.useType(type);
	    return createNormal(methodanalyzer, instruction,
				new CheckCastOperator(type));
	}
	case 193: {
	    Type type = Type.tType(instruction.getClazzType());
	    methodanalyzer.useType(type);
	    return createNormal(methodanalyzer, instruction,
				new InstanceOfOperator(type));
	}
	case 194:
	    return createNormal(methodanalyzer, instruction,
				new MonitorEnterOperator());
	case 195:
	    return createNormal(methodanalyzer, instruction,
				new MonitorExitOperator());
	case 197: {
	    Type type = Type.tType(instruction.getClazzType());
	    methodanalyzer.useType(type);
	    int i_8_ = instruction.getDimensions();
	    return createNormal(methodanalyzer, instruction,
				new NewArrayOperator(type, i_8_));
	}
	case 198:
	case 199:
	    return createIfGoto(methodanalyzer, instruction,
				new CompareUnaryOperator(Type.tUObject,
							 i - 172));
	default:
	    throw new AssertError("Invalid opcode " + i);
	}
    }
}
