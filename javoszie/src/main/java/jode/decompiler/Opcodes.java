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

public abstract class Opcodes
  implements jode.bytecode.Opcodes
{
  private static final Type tIntHint = new IntegerType(2, 30);
  private static final Type tBoolIntHint = new IntegerType(3, 31);
  private static final int LOCAL_TYPES = 0;
  private static final int ARRAY_TYPES = 1;
  private static final int UNARY_TYPES = 2;
  private static final int I2BCS_TYPES = 3;
  private static final int BIN_TYPES = 4;
  private static final int ZBIN_TYPES = 5;
  private static final Type[][] types = { { Type.tBoolUInt, Type.tLong, Type.tFloat, Type.tDouble, Type.tUObject }, { Type.tInt, Type.tLong, Type.tFloat, Type.tDouble, Type.tUObject, Type.tBoolByte, Type.tChar, Type.tShort }, { Type.tInt, Type.tLong, Type.tFloat, Type.tDouble, Type.tUObject }, { Type.tByte, Type.tChar, Type.tShort }, { tIntHint, Type.tLong, Type.tFloat, Type.tDouble, Type.tUObject }, { tBoolIntHint, Type.tLong, Type.tFloat, Type.tDouble, Type.tUObject } };
  
  private static StructuredBlock createNormal(MethodAnalyzer paramMethodAnalyzer, Instruction paramInstruction, Expression paramExpression)
  {
    return new InstructionBlock(paramExpression, new Jump(FlowBlock.NEXT_BY_ADDR));
  }
  
  private static StructuredBlock createSpecial(MethodAnalyzer paramMethodAnalyzer, Instruction paramInstruction, int paramInt1, int paramInt2, int paramInt3)
  {
    return new SpecialBlock(paramInt1, paramInt2, paramInt3, new Jump(FlowBlock.NEXT_BY_ADDR));
  }
  
  private static StructuredBlock createGoto(MethodAnalyzer paramMethodAnalyzer, Instruction paramInstruction)
  {
    return new EmptyBlock(new Jump((FlowBlock)paramInstruction.getSingleSucc().getTmpInfo()));
  }
  
  private static StructuredBlock createJsr(MethodAnalyzer paramMethodAnalyzer, Instruction paramInstruction)
  {
    return new JsrBlock(new Jump((FlowBlock)paramInstruction.getSingleSucc().getTmpInfo()), new Jump(FlowBlock.NEXT_BY_ADDR));
  }
  
  private static StructuredBlock createIfGoto(MethodAnalyzer paramMethodAnalyzer, Instruction paramInstruction, Expression paramExpression)
  {
    return new ConditionalBlock(paramExpression, new Jump((FlowBlock)paramInstruction.getSingleSucc().getTmpInfo()), new Jump(FlowBlock.NEXT_BY_ADDR));
  }
  
  private static StructuredBlock createSwitch(MethodAnalyzer paramMethodAnalyzer, Instruction paramInstruction, int[] paramArrayOfInt, FlowBlock[] paramArrayOfFlowBlock)
  {
    return new SwitchBlock(new NopOperator(Type.tUInt), paramArrayOfInt, paramArrayOfFlowBlock);
  }
  
  private static StructuredBlock createBlock(MethodAnalyzer paramMethodAnalyzer, Instruction paramInstruction, StructuredBlock paramStructuredBlock)
  {
    return paramStructuredBlock;
  }
  
  private static StructuredBlock createRet(MethodAnalyzer paramMethodAnalyzer, Instruction paramInstruction, LocalInfo paramLocalInfo)
  {
    return new RetBlock(paramLocalInfo);
  }
  
  public static StructuredBlock readOpcode(Instruction paramInstruction, MethodAnalyzer paramMethodAnalyzer)
    throws ClassFormatError
  {
    int i = paramInstruction.getOpcode();
    int j;
    int k;
    Object localObject;
    int m;
    switch (i)
    {
    case 0: 
      return createBlock(paramMethodAnalyzer, paramInstruction, new EmptyBlock(new Jump(FlowBlock.NEXT_BY_ADDR)));
    case 18: 
    case 20: 
      return createNormal(paramMethodAnalyzer, paramInstruction, new ConstOperator(paramInstruction.getConstant()));
    case 21: 
    case 22: 
    case 23: 
    case 24: 
    case 25: 
      return createNormal(paramMethodAnalyzer, paramInstruction, new LocalLoadOperator(types[0][(i - 21)], paramMethodAnalyzer, paramMethodAnalyzer.getLocalInfo(paramInstruction.getAddr(), paramInstruction.getLocalSlot())));
    case 46: 
    case 47: 
    case 48: 
    case 49: 
    case 50: 
    case 51: 
    case 52: 
    case 53: 
      return createNormal(paramMethodAnalyzer, paramInstruction, new ArrayLoadOperator(types[1][(i - 46)]));
    case 54: 
    case 55: 
    case 56: 
    case 57: 
    case 58: 
      return createNormal(paramMethodAnalyzer, paramInstruction, new StoreInstruction(new LocalStoreOperator(types[0][(i - 54)], paramMethodAnalyzer.getLocalInfo(paramInstruction.getNextByAddr().getAddr(), paramInstruction.getLocalSlot()))));
    case 79: 
    case 80: 
    case 81: 
    case 82: 
    case 83: 
    case 84: 
    case 85: 
    case 86: 
      return createNormal(paramMethodAnalyzer, paramInstruction, new StoreInstruction(new ArrayStoreOperator(types[1][(i - 79)])));
    case 87: 
    case 88: 
      return createSpecial(paramMethodAnalyzer, paramInstruction, SpecialBlock.POP, i - 87 + 1, 0);
    case 89: 
    case 90: 
    case 91: 
    case 92: 
    case 93: 
    case 94: 
      return createSpecial(paramMethodAnalyzer, paramInstruction, SpecialBlock.DUP, (i - 89) / 3 + 1, (i - 89) % 3);
    case 95: 
      return createSpecial(paramMethodAnalyzer, paramInstruction, SpecialBlock.SWAP, 1, 0);
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
      return createNormal(paramMethodAnalyzer, paramInstruction, new BinaryOperator(types[4][((i - 96) % 4)], (i - 96) / 4 + 1));
    case 116: 
    case 117: 
    case 118: 
    case 119: 
      return createNormal(paramMethodAnalyzer, paramInstruction, new UnaryOperator(types[2][(i - 116)], 36));
    case 120: 
    case 121: 
    case 122: 
    case 123: 
    case 124: 
    case 125: 
      return createNormal(paramMethodAnalyzer, paramInstruction, new ShiftOperator(types[2][((i - 120) % 2)], (i - 120) / 2 + 6));
    case 126: 
    case 127: 
    case 128: 
    case 129: 
    case 130: 
    case 131: 
      return createNormal(paramMethodAnalyzer, paramInstruction, new BinaryOperator(types[5][((i - 126) % 2)], (i - 126) / 2 + 9));
    case 132: 
      j = paramInstruction.getIncrement();
      k = 1;
      if (j < 0)
      {
        j = -j;
        k = 2;
      }
      LocalInfo localLocalInfo = paramMethodAnalyzer.getLocalInfo(paramInstruction.getAddr(), paramInstruction.getLocalSlot());
      return createNormal(paramMethodAnalyzer, paramInstruction, new IIncOperator(new LocalStoreOperator(Type.tInt, localLocalInfo), j, k + 12));
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
    case 144: 
      j = (i - 133) / 3;
      k = (i - 133) % 3;
      if (k >= j) {
        k++;
      }
      return createNormal(paramMethodAnalyzer, paramInstruction, new ConvertOperator(types[2][j], types[2][k]));
    case 145: 
    case 146: 
    case 147: 
      return createNormal(paramMethodAnalyzer, paramInstruction, new ConvertOperator(types[2][0], types[3][(i - 145)]));
    case 148: 
    case 149: 
    case 150: 
    case 151: 
    case 152: 
      return createNormal(paramMethodAnalyzer, paramInstruction, new CompareToIntOperator(types[4][((i - 145) / 2)], (i == 150) || (i == 152)));
    case 153: 
    case 154: 
      return createIfGoto(paramMethodAnalyzer, paramInstruction, new CompareUnaryOperator(Type.tBoolInt, i - 127));
    case 155: 
    case 156: 
    case 157: 
    case 158: 
      return createIfGoto(paramMethodAnalyzer, paramInstruction, new CompareUnaryOperator(Type.tInt, i - 127));
    case 159: 
    case 160: 
      return createIfGoto(paramMethodAnalyzer, paramInstruction, new CompareBinaryOperator(tBoolIntHint, i - 133));
    case 161: 
    case 162: 
    case 163: 
    case 164: 
      return createIfGoto(paramMethodAnalyzer, paramInstruction, new CompareBinaryOperator(tIntHint, i - 133));
    case 165: 
    case 166: 
      return createIfGoto(paramMethodAnalyzer, paramInstruction, new CompareBinaryOperator(Type.tUObject, i - 139));
    case 167: 
      return createGoto(paramMethodAnalyzer, paramInstruction);
    case 168: 
      return createJsr(paramMethodAnalyzer, paramInstruction);
    case 169: 
      return createRet(paramMethodAnalyzer, paramInstruction, paramMethodAnalyzer.getLocalInfo(paramInstruction.getAddr(), paramInstruction.getLocalSlot()));
    case 171: 
      localObject = paramInstruction.getValues();
      FlowBlock[] arrayOfFlowBlock = new FlowBlock[paramInstruction.getSuccs().length];
      for (int n = 0; n < arrayOfFlowBlock.length; n++) {
        arrayOfFlowBlock[n] = ((FlowBlock)paramInstruction.getSuccs()[n].getTmpInfo());
      }
      arrayOfFlowBlock[localObject.length] = ((FlowBlock)paramInstruction.getSuccs()[localObject.length].getTmpInfo());
      return createSwitch(paramMethodAnalyzer, paramInstruction, (int[])localObject, arrayOfFlowBlock);
    case 172: 
    case 173: 
    case 174: 
    case 175: 
    case 176: 
      localObject = Type.tSubType(paramMethodAnalyzer.getReturnType());
      return createBlock(paramMethodAnalyzer, paramInstruction, new ReturnBlock(new NopOperator((Type)localObject)));
    case 177: 
      return createBlock(paramMethodAnalyzer, paramInstruction, new EmptyBlock(new Jump(FlowBlock.END_OF_METHOD)));
    case 178: 
    case 180: 
      localObject = paramInstruction.getReference();
      return createNormal(paramMethodAnalyzer, paramInstruction, new GetFieldOperator(paramMethodAnalyzer, i == 178, (Reference)localObject));
    case 179: 
    case 181: 
      localObject = paramInstruction.getReference();
      return createNormal(paramMethodAnalyzer, paramInstruction, new StoreInstruction(new PutFieldOperator(paramMethodAnalyzer, i == 179, (Reference)localObject)));
    case 182: 
    case 183: 
    case 184: 
    case 185: 
      localObject = paramInstruction.getReference();
      m = i == 183 ? 1 : i == 184 ? 2 : ((Reference)localObject).getName().equals("<init>") ? 3 : 0;
      StructuredBlock localStructuredBlock = createNormal(paramMethodAnalyzer, paramInstruction, new InvokeOperator(paramMethodAnalyzer, m, (Reference)localObject));
      return localStructuredBlock;
    case 187: 
      localObject = Type.tType(paramInstruction.getClazzType());
      paramMethodAnalyzer.useType((Type)localObject);
      return createNormal(paramMethodAnalyzer, paramInstruction, new NewOperator((Type)localObject));
    case 190: 
      return createNormal(paramMethodAnalyzer, paramInstruction, new ArrayLengthOperator());
    case 191: 
      return createBlock(paramMethodAnalyzer, paramInstruction, new ThrowBlock(new NopOperator(Type.tUObject)));
    case 192: 
      localObject = Type.tType(paramInstruction.getClazzType());
      paramMethodAnalyzer.useType((Type)localObject);
      return createNormal(paramMethodAnalyzer, paramInstruction, new CheckCastOperator((Type)localObject));
    case 193: 
      localObject = Type.tType(paramInstruction.getClazzType());
      paramMethodAnalyzer.useType((Type)localObject);
      return createNormal(paramMethodAnalyzer, paramInstruction, new InstanceOfOperator((Type)localObject));
    case 194: 
      return createNormal(paramMethodAnalyzer, paramInstruction, new MonitorEnterOperator());
    case 195: 
      return createNormal(paramMethodAnalyzer, paramInstruction, new MonitorExitOperator());
    case 197: 
      localObject = Type.tType(paramInstruction.getClazzType());
      paramMethodAnalyzer.useType((Type)localObject);
      m = paramInstruction.getDimensions();
      return createNormal(paramMethodAnalyzer, paramInstruction, new NewArrayOperator((Type)localObject, m));
    case 198: 
    case 199: 
      return createIfGoto(paramMethodAnalyzer, paramInstruction, new CompareUnaryOperator(Type.tUObject, i - 172));
    }
    throw new AssertError("Invalid opcode " + i);
  }
}


