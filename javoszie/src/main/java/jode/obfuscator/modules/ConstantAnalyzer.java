package jode.obfuscator.modules;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import jode.AssertError;
import jode.GlobalOptions;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.MethodInfo;
import jode.bytecode.Opcodes;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;
import jode.jvm.InterpreterException;
import jode.obfuscator.ClassBundle;
import jode.obfuscator.CodeAnalyzer;
import jode.obfuscator.ConstantRuntimeEnvironment;
import jode.obfuscator.FieldIdentifier;
import jode.obfuscator.Identifier;
import jode.obfuscator.Main;
import jode.obfuscator.MethodIdentifier;

public class ConstantAnalyzer
  extends SimpleAnalyzer
  implements Opcodes, CodeAnalyzer
{
  BytecodeInfo bytecode;
  private static ConstantRuntimeEnvironment runtime = new ConstantRuntimeEnvironment();
  private static final int CMP_EQ = 0;
  private static final int CMP_NE = 1;
  private static final int CMP_LT = 2;
  private static final int CMP_GE = 3;
  private static final int CMP_GT = 4;
  private static final int CMP_LE = 5;
  private static final int CMP_GREATER_MASK = 26;
  private static final int CMP_LESS_MASK = 38;
  private static final int CMP_EQUAL_MASK = 41;
  static final int CONSTANT = 2;
  static final int CONSTANTFLOW = 4;
  static final int RETASTORE = 8;
  static final int RETURNINGJSR = 16;
  private static ConstValue[] unknownValue = { new ConstValue(1), new ConstValue(2) };
  private static ConstantInfo unknownConstInfo = new ConstantInfo();
  
  public void mergeInfo(Instruction paramInstruction, StackLocalInfo paramStackLocalInfo)
  {
    if (paramInstruction.getTmpInfo() == null)
    {
      paramInstruction.setTmpInfo(paramStackLocalInfo);
      paramStackLocalInfo.instr = paramInstruction;
      paramStackLocalInfo.enqueue();
    }
    else
    {
      ((StackLocalInfo)paramInstruction.getTmpInfo()).merge(paramStackLocalInfo);
    }
  }
  
  public void handleReference(Reference paramReference, boolean paramBoolean)
  {
    Main.getClassBundle().reachableReference(paramReference, paramBoolean);
  }
  
  public void handleClass(String paramString)
  {
    for (int i = 0; (i < paramString.length()) && (paramString.charAt(i) == '['); i++) {}
    if ((i < paramString.length()) && (paramString.charAt(i) == 'L'))
    {
      paramString = paramString.substring(i + 1, paramString.length() - 1);
      Main.getClassBundle().reachableClass(paramString);
    }
  }
  
  public void handleOpcode(StackLocalInfo paramStackLocalInfo, Identifier paramIdentifier)
  {
    Instruction localInstruction1 = paramStackLocalInfo.instr;
    paramStackLocalInfo.constInfo = unknownConstInfo;
    int i = localInstruction1.getOpcode();
    Handler[] arrayOfHandler = this.bytecode.getExceptionHandlers();
    for (int j = 0; j < arrayOfHandler.length; j++) {
      if ((arrayOfHandler[j].start.getAddr() <= localInstruction1.getAddr()) && (arrayOfHandler[j].end.getAddr() >= localInstruction1.getAddr())) {
        mergeInfo(arrayOfHandler[j].catcher, paramStackLocalInfo.poppush(paramStackLocalInfo.stack.length, unknownValue[0]));
      }
    }
    ConstValue localConstValue1;
    int k;
    ConstValue localConstValue4;
    Object localObject4;
    ConstValue localConstValue3;
    Object localObject2;
    Integer localInteger2;
    int i12;
    Object localObject1;
    Object localObject7;
    Object localObject3;
    Object localObject5;
    Object localObject9;
    Object[] arrayOfObject;
    Object localObject11;
    switch (i)
    {
    case 0: 
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.pop(0));
      break;
    case 18: 
    case 20: 
      localConstValue1 = new ConstValue(localInstruction1.getConstant());
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.poppush(0, localConstValue1));
      break;
    case 21: 
    case 22: 
    case 23: 
    case 24: 
    case 25: 
      localConstValue1 = paramStackLocalInfo.getLocal(localInstruction1.getLocalSlot());
      if (localConstValue1 == null)
      {
        dumpStackLocalInfo();
        System.err.println(paramStackLocalInfo);
        System.err.println(localInstruction1);
      }
      if (localConstValue1.value != ConstValue.VOLATILE)
      {
        paramStackLocalInfo.constInfo = new ConstantInfo(2, localConstValue1.value);
        localConstValue1.addConstantListener(paramStackLocalInfo.constInfo);
      }
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.poppush(0, localConstValue1).setLocal(localInstruction1.getLocalSlot(), localConstValue1.copy()));
      break;
    case 46: 
    case 47: 
    case 48: 
    case 49: 
    case 50: 
    case 51: 
    case 52: 
    case 53: 
      localConstValue1 = unknownValue[0];
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.poppush(2, localConstValue1));
      break;
    case 54: 
    case 56: 
    case 58: 
      localConstValue1 = paramStackLocalInfo.getStack(1);
      if ((localConstValue1.value instanceof JSRTargetInfo)) {
        paramStackLocalInfo.constInfo.flags |= 0x8;
      }
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.pop(1).setLocal(localInstruction1.getLocalSlot(), localConstValue1));
      break;
    case 55: 
    case 57: 
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.pop(2).setLocal(localInstruction1.getLocalSlot(), paramStackLocalInfo.getStack(2)));
      break;
    case 79: 
    case 80: 
    case 81: 
    case 82: 
    case 83: 
    case 84: 
    case 85: 
    case 86: 
      k = (i == 80) || (i == 82) ? 2 : 1;
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.pop(2 + k));
      break;
    case 87: 
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.pop(1));
      break;
    case 88: 
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.pop(2));
      break;
    case 89: 
    case 90: 
    case 91: 
    case 92: 
    case 93: 
    case 94: 
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.dup((i - 86) / 3, (i - 86) % 3));
      break;
    case 95: 
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.swap());
      break;
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
    case 126: 
    case 127: 
    case 128: 
    case 129: 
    case 130: 
    case 131: 
      k = 1 + (i - 96 & 0x1);
      localConstValue4 = paramStackLocalInfo.getStack(2 * k);
      localObject4 = paramStackLocalInfo.getStack(1 * k);
      int i6 = (localConstValue4.value != ConstValue.VOLATILE) && (((ConstValue)localObject4).value != ConstValue.VOLATILE) ? 1 : 0;
      if ((i6 != 0) && (((i != 108) && (i != 112)) || ((((Integer)((ConstValue)localObject4).value).intValue() == 0) || (((i == 109) || (i == 113)) && (((Long)((ConstValue)localObject4).value).longValue() == 0L))))) {
        i6 = 0;
      }
      if (i6 != 0)
      {
        Object localObject8;
        switch (i)
        {
        case 96: 
          localObject8 = new Integer(((Integer)localConstValue4.value).intValue() + ((Integer)((ConstValue)localObject4).value).intValue());
          break;
        case 100: 
          localObject8 = new Integer(((Integer)localConstValue4.value).intValue() - ((Integer)((ConstValue)localObject4).value).intValue());
          break;
        case 104: 
          localObject8 = new Integer(((Integer)localConstValue4.value).intValue() * ((Integer)((ConstValue)localObject4).value).intValue());
          break;
        case 108: 
          localObject8 = new Integer(((Integer)localConstValue4.value).intValue() / ((Integer)((ConstValue)localObject4).value).intValue());
          break;
        case 112: 
          localObject8 = new Integer(((Integer)localConstValue4.value).intValue() % ((Integer)((ConstValue)localObject4).value).intValue());
          break;
        case 126: 
          localObject8 = new Integer(((Integer)localConstValue4.value).intValue() & ((Integer)((ConstValue)localObject4).value).intValue());
          break;
        case 128: 
          localObject8 = new Integer(((Integer)localConstValue4.value).intValue() | ((Integer)((ConstValue)localObject4).value).intValue());
          break;
        case 130: 
          localObject8 = new Integer(((Integer)localConstValue4.value).intValue() ^ ((Integer)((ConstValue)localObject4).value).intValue());
          break;
        case 97: 
          localObject8 = new Long(((Long)localConstValue4.value).longValue() + ((Long)((ConstValue)localObject4).value).longValue());
          break;
        case 101: 
          localObject8 = new Long(((Long)localConstValue4.value).longValue() - ((Long)((ConstValue)localObject4).value).longValue());
          break;
        case 105: 
          localObject8 = new Long(((Long)localConstValue4.value).longValue() * ((Long)((ConstValue)localObject4).value).longValue());
          break;
        case 109: 
          localObject8 = new Long(((Long)localConstValue4.value).longValue() / ((Long)((ConstValue)localObject4).value).longValue());
          break;
        case 113: 
          localObject8 = new Long(((Long)localConstValue4.value).longValue() % ((Long)((ConstValue)localObject4).value).longValue());
          break;
        case 127: 
          localObject8 = new Long(((Long)localConstValue4.value).longValue() & ((Long)((ConstValue)localObject4).value).longValue());
          break;
        case 129: 
          localObject8 = new Long(((Long)localConstValue4.value).longValue() | ((Long)((ConstValue)localObject4).value).longValue());
          break;
        case 131: 
          localObject8 = new Long(((Long)localConstValue4.value).longValue() ^ ((Long)((ConstValue)localObject4).value).longValue());
          break;
        case 98: 
          localObject8 = new Float(((Float)localConstValue4.value).floatValue() + ((Float)((ConstValue)localObject4).value).floatValue());
          break;
        case 102: 
          localObject8 = new Float(((Float)localConstValue4.value).floatValue() - ((Float)((ConstValue)localObject4).value).floatValue());
          break;
        case 106: 
          localObject8 = new Float(((Float)localConstValue4.value).floatValue() * ((Float)((ConstValue)localObject4).value).floatValue());
          break;
        case 110: 
          localObject8 = new Float(((Float)localConstValue4.value).floatValue() / ((Float)((ConstValue)localObject4).value).floatValue());
          break;
        case 114: 
          localObject8 = new Float(((Float)localConstValue4.value).floatValue() % ((Float)((ConstValue)localObject4).value).floatValue());
          break;
        case 99: 
          localObject8 = new Double(((Double)localConstValue4.value).doubleValue() + ((Double)((ConstValue)localObject4).value).doubleValue());
          break;
        case 103: 
          localObject8 = new Double(((Double)localConstValue4.value).doubleValue() - ((Double)((ConstValue)localObject4).value).doubleValue());
          break;
        case 107: 
          localObject8 = new Double(((Double)localConstValue4.value).doubleValue() * ((Double)((ConstValue)localObject4).value).doubleValue());
          break;
        case 111: 
          localObject8 = new Double(((Double)localConstValue4.value).doubleValue() / ((Double)((ConstValue)localObject4).value).doubleValue());
          break;
        case 115: 
          localObject8 = new Double(((Double)localConstValue4.value).doubleValue() % ((Double)((ConstValue)localObject4).value).doubleValue());
          break;
        case 116: 
        case 117: 
        case 118: 
        case 119: 
        case 120: 
        case 121: 
        case 122: 
        case 123: 
        case 124: 
        case 125: 
        default: 
          throw new AssertError("Can't happen.");
        }
        paramStackLocalInfo.constInfo = new ConstantInfo(2, localObject8);
        localConstValue1 = new ConstValue(localObject8);
        localConstValue1.addConstantListener(paramStackLocalInfo.constInfo);
        localConstValue4.addConstantListener(localConstValue1);
        ((ConstValue)localObject4).addConstantListener(localConstValue1);
      }
      else
      {
        localConstValue1 = unknownValue[(k - 1)];
      }
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.poppush(2 * k, localConstValue1));
      break;
    case 116: 
    case 117: 
    case 118: 
    case 119: 
      k = 1 + (i - 116 & 0x1);
      localConstValue4 = paramStackLocalInfo.getStack(k);
      if (localConstValue4.value != ConstValue.VOLATILE)
      {
        switch (i)
        {
        case 116: 
          localObject4 = new Integer(-((Integer)localConstValue4.value).intValue());
          break;
        case 117: 
          localObject4 = new Long(-((Long)localConstValue4.value).longValue());
          break;
        case 118: 
          localObject4 = new Float(-((Float)localConstValue4.value).floatValue());
          break;
        case 119: 
          localObject4 = new Double(-((Double)localConstValue4.value).doubleValue());
          break;
        default: 
          throw new AssertError("Can't happen.");
        }
        paramStackLocalInfo.constInfo = new ConstantInfo(2, localObject4);
        localConstValue1 = new ConstValue(localObject4);
        localConstValue1.addConstantListener(paramStackLocalInfo.constInfo);
        localConstValue4.addConstantListener(localConstValue1);
      }
      else
      {
        localConstValue1 = unknownValue[(k - 1)];
      }
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.poppush(k, localConstValue1));
      break;
    case 120: 
    case 121: 
    case 122: 
    case 123: 
    case 124: 
    case 125: 
      k = 1 + (i - 96 & 0x1);
      localConstValue4 = paramStackLocalInfo.getStack(k + 1);
      localObject4 = paramStackLocalInfo.getStack(1);
      if ((localConstValue4.value != ConstValue.VOLATILE) && (((ConstValue)localObject4).value != ConstValue.VOLATILE))
      {
        Object localObject6;
        switch (i)
        {
        case 120: 
          localObject6 = new Integer(((Integer)localConstValue4.value).intValue() << ((Integer)((ConstValue)localObject4).value).intValue());
          break;
        case 122: 
          localObject6 = new Integer(((Integer)localConstValue4.value).intValue() >> ((Integer)((ConstValue)localObject4).value).intValue());
          break;
        case 124: 
          localObject6 = new Integer(((Integer)localConstValue4.value).intValue() >>> ((Integer)((ConstValue)localObject4).value).intValue());
          break;
        case 121: 
          localObject6 = new Long(((Long)localConstValue4.value).longValue() << ((Integer)((ConstValue)localObject4).value).intValue());
          break;
        case 123: 
          localObject6 = new Long(((Long)localConstValue4.value).longValue() >> ((Integer)((ConstValue)localObject4).value).intValue());
          break;
        case 125: 
          localObject6 = new Long(((Long)localConstValue4.value).longValue() >>> ((Integer)((ConstValue)localObject4).value).intValue());
          break;
        default: 
          throw new AssertError("Can't happen.");
        }
        paramStackLocalInfo.constInfo = new ConstantInfo(2, localObject6);
        localConstValue1 = new ConstValue(localObject6);
        localConstValue1.addConstantListener(paramStackLocalInfo.constInfo);
        localConstValue4.addConstantListener(localConstValue1);
        ((ConstValue)localObject4).addConstantListener(localConstValue1);
      }
      else
      {
        localConstValue1 = unknownValue[(k - 1)];
      }
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.poppush(k + 1, localConstValue1));
      break;
    case 132: 
      ConstValue localConstValue2 = paramStackLocalInfo.getLocal(localInstruction1.getLocalSlot());
      if (localConstValue2.value != ConstValue.VOLATILE)
      {
        localConstValue1 = new ConstValue(new Integer(((Integer)localConstValue2.value).intValue() + localInstruction1.getIncrement()));
        localConstValue2.addConstantListener(localConstValue1);
      }
      else
      {
        localConstValue1 = unknownValue[0];
      }
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.copy().setLocal(localInstruction1.getLocalSlot(), localConstValue1));
      break;
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
      int m = 1 + ((i - 133) / 3 & 0x1);
      localConstValue4 = paramStackLocalInfo.getStack(m);
      if (localConstValue4.value != ConstValue.VOLATILE)
      {
        switch (i)
        {
        case 136: 
        case 139: 
        case 142: 
          localObject4 = new Integer(((Number)localConstValue4.value).intValue());
          break;
        case 133: 
        case 140: 
        case 143: 
          localObject4 = new Long(((Number)localConstValue4.value).longValue());
          break;
        case 134: 
        case 137: 
        case 144: 
          localObject4 = new Float(((Number)localConstValue4.value).floatValue());
          break;
        case 135: 
        case 138: 
        case 141: 
          localObject4 = new Double(((Number)localConstValue4.value).doubleValue());
          break;
        default: 
          throw new AssertError("Can't happen.");
        }
        paramStackLocalInfo.constInfo = new ConstantInfo(2, localObject4);
        localConstValue1 = new ConstValue(localObject4);
        localConstValue1.addConstantListener(paramStackLocalInfo.constInfo);
        localConstValue4.addConstantListener(localConstValue1);
      }
      else
      {
        switch (i)
        {
        case 133: 
        case 135: 
        case 138: 
        case 140: 
        case 141: 
        case 143: 
          localConstValue1 = unknownValue[1];
          break;
        case 134: 
        case 136: 
        case 137: 
        case 139: 
        case 142: 
        default: 
          localConstValue1 = unknownValue[0];
        }
      }
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.poppush(m, localConstValue1));
      break;
    case 145: 
    case 146: 
    case 147: 
      localConstValue3 = paramStackLocalInfo.getStack(1);
      if (localConstValue3.value != ConstValue.VOLATILE)
      {
        int i1 = ((Integer)localConstValue3.value).intValue();
        switch (i)
        {
        case 145: 
          i1 = (byte)i1;
          break;
        case 146: 
          i1 = (char)i1;
          break;
        case 147: 
          i1 = (short)i1;
        }
        localObject4 = new Integer(i1);
        paramStackLocalInfo.constInfo = new ConstantInfo(2, localObject4);
        localConstValue1 = new ConstValue(localObject4);
        localConstValue3.addConstantListener(paramStackLocalInfo.constInfo);
        localConstValue3.addConstantListener(localConstValue1);
      }
      else
      {
        localConstValue1 = unknownValue[0];
      }
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.poppush(1, localConstValue1));
      break;
    case 148: 
      localConstValue3 = paramStackLocalInfo.getStack(4);
      localObject2 = paramStackLocalInfo.getStack(2);
      if ((localConstValue3.value != ConstValue.VOLATILE) && (((ConstValue)localObject2).value != ConstValue.VOLATILE))
      {
        long l1 = ((Long)localConstValue3.value).longValue();
        long l2 = ((Long)localConstValue3.value).longValue();
        localInteger2 = new Integer(l1 < l2 ? -1 : l1 == l2 ? 0 : 1);
        paramStackLocalInfo.constInfo = new ConstantInfo(2, localInteger2);
        localConstValue1 = new ConstValue(localInteger2);
        localConstValue1.addConstantListener(paramStackLocalInfo.constInfo);
        localConstValue3.addConstantListener(localConstValue1);
        ((ConstValue)localObject2).addConstantListener(localConstValue1);
      }
      else
      {
        localConstValue1 = unknownValue[0];
      }
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.poppush(4, localConstValue1));
      break;
    case 149: 
    case 150: 
      localConstValue3 = paramStackLocalInfo.getStack(2);
      localObject2 = paramStackLocalInfo.getStack(1);
      if ((localConstValue3.value != ConstValue.VOLATILE) && (((ConstValue)localObject2).value != ConstValue.VOLATILE))
      {
        float f1 = ((Float)localConstValue3.value).floatValue();
        float f2 = ((Float)localConstValue3.value).floatValue();
        Integer localInteger1 = new Integer(f1 > f2 ? 1 : i == 150 ? 1 : f1 < f2 ? -1 : f1 == f2 ? 0 : -1);
        paramStackLocalInfo.constInfo = new ConstantInfo(2, localInteger1);
        localConstValue1 = new ConstValue(localInteger1);
        localConstValue1.addConstantListener(paramStackLocalInfo.constInfo);
        localConstValue3.addConstantListener(localConstValue1);
        ((ConstValue)localObject2).addConstantListener(localConstValue1);
      }
      else
      {
        localConstValue1 = unknownValue[0];
      }
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.poppush(2, localConstValue1));
      break;
    case 151: 
    case 152: 
      localConstValue3 = paramStackLocalInfo.getStack(4);
      localObject2 = paramStackLocalInfo.getStack(2);
      if ((localConstValue3.value != ConstValue.VOLATILE) && (((ConstValue)localObject2).value != ConstValue.VOLATILE))
      {
        double d1 = ((Double)localConstValue3.value).doubleValue();
        double d2 = ((Double)localConstValue3.value).doubleValue();
        localInteger2 = new Integer(d1 > d2 ? 1 : i == 152 ? 1 : d1 < d2 ? -1 : d1 == d2 ? 0 : -1);
        paramStackLocalInfo.constInfo = new ConstantInfo(2, localInteger2);
        localConstValue1 = new ConstValue(localInteger2);
        localConstValue1.addConstantListener(paramStackLocalInfo.constInfo);
        localConstValue3.addConstantListener(localConstValue1);
        ((ConstValue)localObject2).addConstantListener(localConstValue1);
      }
      else
      {
        localConstValue1 = unknownValue[0];
      }
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.poppush(4, localConstValue1));
      break;
    case 153: 
    case 154: 
    case 155: 
    case 156: 
    case 157: 
    case 158: 
    case 159: 
    case 160: 
    case 161: 
    case 162: 
    case 163: 
    case 164: 
    case 165: 
    case 166: 
    case 198: 
    case 199: 
      int n = 1;
      localObject2 = paramStackLocalInfo.getStack(1);
      ConstValue localConstValue5 = null;
      int i7 = ((ConstValue)localObject2).value != ConstValue.VOLATILE ? 1 : 0;
      if ((i >= 159) && (i <= 166))
      {
        localConstValue5 = paramStackLocalInfo.getStack(2);
        n = 2;
        i7 &= (localConstValue5.value != ConstValue.VOLATILE ? 1 : 0);
      }
      if (i7 != 0)
      {
        ((ConstValue)localObject2).addConstantListener(paramStackLocalInfo);
        if (localConstValue5 != null) {
          localConstValue5.addConstantListener(paramStackLocalInfo);
        }
        Instruction localInstruction2 = localInstruction1.getNextByAddr();
        if (i >= 165)
        {
          if (i >= 198)
          {
            i12 = ((ConstValue)localObject2).value == null ? 41 : 26;
            i -= 198;
          }
          else
          {
            i12 = ((ConstValue)localObject2).value == localConstValue5.value ? 41 : 26;
            i -= 165;
          }
        }
        else
        {
          int i13 = ((Integer)((ConstValue)localObject2).value).intValue();
          if (i >= 159)
          {
            int i14 = ((Integer)localConstValue5.value).intValue();
            i12 = i14 < i13 ? 38 : i14 == i13 ? 41 : 26;
            i -= 159;
          }
          else
          {
            i12 = i13 < 0 ? 38 : i13 == 0 ? 41 : 26;
            i -= 153;
          }
        }
        if ((i12 & 1 << i) != 0) {
          localInstruction2 = localInstruction1.getSingleSucc();
        }
        paramStackLocalInfo.constInfo = new ConstantInfo(4, localInstruction2);
        mergeInfo(localInstruction2, paramStackLocalInfo.pop(n));
      }
      else
      {
        mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.pop(n));
        mergeInfo(localInstruction1.getSingleSucc(), paramStackLocalInfo.pop(n));
      }
      break;
    case 167: 
      mergeInfo(localInstruction1.getSingleSucc(), paramStackLocalInfo.copy());
      break;
    case 171: 
      localObject1 = paramStackLocalInfo.getStack(1);
      if (((ConstValue)localObject1).value != ConstValue.VOLATILE)
      {
        ((ConstValue)localObject1).addConstantListener(paramStackLocalInfo);
        int i4 = ((Integer)((ConstValue)localObject1).value).intValue();
        localObject7 = localInstruction1.getValues();
        localObject2 = localInstruction1.getSuccs()[localObject7.length];
        for (int i10 = 0; i10 < localObject7.length; i10++) {
          if (localObject7[i10] == i4)
          {
            localObject2 = localInstruction1.getSuccs()[i10];
            break;
          }
        }
        paramStackLocalInfo.constInfo = new ConstantInfo(4, localObject2);
        mergeInfo((Instruction)localObject2, paramStackLocalInfo.pop(1));
      }
      else
      {
        for (int i2 = 0; i2 < localInstruction1.getSuccs().length; i2++) {
          mergeInfo(localInstruction1.getSuccs()[i2], paramStackLocalInfo.pop(1));
        }
      }
      break;
    case 168: 
      if (localInstruction1.getSingleSucc().getOpcode() != 58) {
        throw new RuntimeException("Can't handle jsr to non astores");
      }
      localObject1 = (StackLocalInfo)localInstruction1.getSingleSucc().getTmpInfo();
      if (localObject1 != null)
      {
        localConstValue1 = ((StackLocalInfo)localObject1).getStack(1);
        if ((((StackLocalInfo)localObject1).retInfo != null) && ((localConstValue1.value instanceof JSRTargetInfo))) {
          mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.copy().mergeRetLocals((JSRTargetInfo)localConstValue1.value, ((StackLocalInfo)localObject1).retInfo));
        }
      }
      else
      {
        localConstValue1 = new ConstValue(new JSRTargetInfo(localInstruction1.getSingleSucc()));
      }
      mergeInfo(localInstruction1.getSingleSucc(), paramStackLocalInfo.poppush(0, localConstValue1));
      break;
    case 169: 
      localConstValue1 = paramStackLocalInfo.getLocal(localInstruction1.getLocalSlot());
      localObject3 = (JSRTargetInfo)localConstValue1.value;
      ((JSRTargetInfo)localObject3).setRetInfo(paramStackLocalInfo);
      localConstValue1.addConstantListener(paramStackLocalInfo);
      localObject5 = ((JSRTargetInfo)localObject3).jsrTarget;
      localObject7 = (StackLocalInfo)((Instruction)localObject5).getTmpInfo();
      ((StackLocalInfo)localObject7).retInfo = paramStackLocalInfo;
      ((StackLocalInfo)localObject7).constInfo.flags |= 0x10;
      localObject9 = ((Instruction)localObject5).getPreds();
      for (i12 = 0; i12 < localObject9.length; i12++)
      {
        arrayOfObject = localObject9[i12];
        if (arrayOfObject.getTmpInfo() != null) {
          mergeInfo(arrayOfObject.getNextByAddr(), ((StackLocalInfo)arrayOfObject.getTmpInfo()).copy().mergeRetLocals((JSRTargetInfo)localObject3, paramStackLocalInfo));
        }
      }
      break;
    case 172: 
    case 173: 
    case 174: 
    case 175: 
    case 176: 
    case 177: 
    case 191: 
      break;
    case 179: 
    case 181: 
      localObject3 = (FieldIdentifier)canonizeReference(localInstruction1);
      localObject5 = localInstruction1.getReference();
      int i8 = TypeSignature.getTypeSize(((Reference)localObject5).getType());
      if ((localObject3 != null) && (!((FieldIdentifier)localObject3).isNotConstant()))
      {
        localObject9 = paramStackLocalInfo.getStack(i8);
        localObject11 = ((FieldIdentifier)localObject3).getConstant();
        if (localObject11 == null) {
          localObject11 = ConstantRuntimeEnvironment.getDefaultValue(((Reference)localObject5).getType());
        }
        if (((ConstValue)localObject9).value == null ? localObject11 == null : ((ConstValue)localObject9).value.equals(localObject11))
        {
          ((ConstValue)localObject9).addConstantListener(paramStackLocalInfo);
        }
        else
        {
          ((FieldIdentifier)localObject3).setNotConstant();
          fieldNotConstant((FieldIdentifier)localObject3);
        }
      }
      i8 += (i == 179 ? 0 : 1);
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.pop(i8));
      break;
    case 178: 
    case 180: 
      int i3 = i == 178 ? 0 : 1;
      localObject5 = (FieldIdentifier)canonizeReference(localInstruction1);
      Reference localReference2 = localInstruction1.getReference();
      int i11 = TypeSignature.getTypeSize(localReference2.getType());
      if (localObject5 != null)
      {
        if (((FieldIdentifier)localObject5).isNotConstant())
        {
          ((FieldIdentifier)localObject5).setReachable();
          localConstValue1 = unknownValue[(i11 - 1)];
        }
        else
        {
          localObject11 = ((FieldIdentifier)localObject5).getConstant();
          if (localObject11 == null) {
            localObject11 = ConstantRuntimeEnvironment.getDefaultValue(localReference2.getType());
          }
          paramStackLocalInfo.constInfo = new ConstantInfo(2, localObject11);
          localConstValue1 = new ConstValue(localObject11);
          localConstValue1.addConstantListener(paramStackLocalInfo.constInfo);
          ((FieldIdentifier)localObject5).addFieldListener(paramIdentifier);
        }
      }
      else {
        localConstValue1 = unknownValue[(i11 - 1)];
      }
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.poppush(i3, localConstValue1));
      break;
    case 182: 
    case 183: 
    case 184: 
    case 185: 
      canonizeReference(localInstruction1);
      Reference localReference1 = localInstruction1.getReference();
      int i5 = 1;
      int i9 = 0;
      Object localObject10 = null;
      localObject11 = TypeSignature.getParameterTypes(localReference1.getType());
      arrayOfObject = new Object[localObject11.length];
      ConstValue localConstValue6 = null;
      ConstValue[] arrayOfConstValue = new ConstValue[localObject11.length];
      Object localObject12;
      for (int i15 = localObject11.length - 1; i15 >= 0; i15--)
      {
        i9 += TypeSignature.getTypeSize(localObject11[i15]);
        localObject12 = (arrayOfConstValue[i15] = paramStackLocalInfo.getStack(i9)).value;
        if (localObject12 != ConstValue.VOLATILE) {
          arrayOfObject[i15] = localObject12;
        } else {
          i5 = 0;
        }
      }
      if (i != 184)
      {
        i9++;
        localConstValue6 = paramStackLocalInfo.getStack(i9);
        localObject10 = localConstValue6.value;
        if ((localObject10 == ConstValue.VOLATILE) || (localObject10 == null)) {
          i5 = 0;
        }
      }
      String str = TypeSignature.getReturnType(localReference1.getType());
      if (str.equals("V"))
      {
        handleReference(localReference1, (i == 182) || (i == 185));
        mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.pop(i9));
      }
      else
      {
        if ((i5 != 0) && (!ConstantRuntimeEnvironment.isWhite(str))) {
          i5 = 0;
        }
        localObject12 = null;
        if (i5 != 0) {
          try
          {
            localObject12 = runtime.invokeMethod(localReference1, i != 183, localObject10, arrayOfObject);
          }
          catch (InterpreterException localInterpreterException)
          {
            i5 = 0;
            if (GlobalOptions.verboseLevel > 3) {
              GlobalOptions.err.println("Can't interpret " + localReference1 + ": " + localInterpreterException.getMessage());
            }
          }
          catch (InvocationTargetException localInvocationTargetException)
          {
            i5 = 0;
            if (GlobalOptions.verboseLevel > 3) {
              GlobalOptions.err.println("Method " + localReference1 + " throwed exception: " + localInvocationTargetException.getTargetException());
            }
          }
        }
        int i16;
        ConstValue localConstValue7;
        if (i5 == 0)
        {
          handleReference(localReference1, (i == 182) || (i == 185));
          i16 = TypeSignature.getTypeSize(str);
          localConstValue7 = unknownValue[(i16 - 1)];
        }
        else
        {
          paramStackLocalInfo.constInfo = new ConstantInfo(2, localObject12);
          localConstValue7 = new ConstValue(localObject12);
          localConstValue7.addConstantListener(paramStackLocalInfo.constInfo);
          if (localConstValue6 != null) {
            localConstValue6.addConstantListener(localConstValue7);
          }
          for (i16 = 0; i16 < arrayOfConstValue.length; i16++) {
            arrayOfConstValue[i16].addConstantListener(localConstValue7);
          }
        }
        mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.poppush(i9, localConstValue7));
      }
      break;
    case 187: 
      handleClass(localInstruction1.getClazzType());
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.poppush(0, unknownValue[0]));
      break;
    case 190: 
      localConstValue1 = unknownValue[0];
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.poppush(1, localConstValue1));
      break;
    case 192: 
      handleClass(localInstruction1.getClazzType());
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.pop(0));
      break;
    case 193: 
      handleClass(localInstruction1.getClazzType());
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.poppush(1, unknownValue[0]));
      break;
    case 194: 
    case 195: 
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.pop(1));
      break;
    case 197: 
      handleClass(localInstruction1.getClazzType());
      mergeInfo(localInstruction1.getNextByAddr(), paramStackLocalInfo.poppush(localInstruction1.getDimensions(), unknownValue[0]));
      break;
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 14: 
    case 15: 
    case 16: 
    case 17: 
    case 19: 
    case 26: 
    case 27: 
    case 28: 
    case 29: 
    case 30: 
    case 31: 
    case 32: 
    case 33: 
    case 34: 
    case 35: 
    case 36: 
    case 37: 
    case 38: 
    case 39: 
    case 40: 
    case 41: 
    case 42: 
    case 43: 
    case 44: 
    case 45: 
    case 59: 
    case 60: 
    case 61: 
    case 62: 
    case 63: 
    case 64: 
    case 65: 
    case 66: 
    case 67: 
    case 68: 
    case 69: 
    case 70: 
    case 71: 
    case 72: 
    case 73: 
    case 74: 
    case 75: 
    case 76: 
    case 77: 
    case 78: 
    case 170: 
    case 186: 
    case 188: 
    case 189: 
    case 196: 
    default: 
      throw new IllegalArgumentException("Invalid opcode " + i);
    }
  }
  
  public void fieldNotConstant(FieldIdentifier paramFieldIdentifier)
  {
    Iterator localIterator = this.bytecode.getInstructions().iterator();
    while (localIterator.hasNext())
    {
      Instruction localInstruction = (Instruction)localIterator.next();
      if ((localInstruction.getOpcode() == 180) || (localInstruction.getOpcode() == 178))
      {
        Reference localReference = localInstruction.getReference();
        if ((localReference.getName().equals(paramFieldIdentifier.getName())) && (localReference.getType().equals(paramFieldIdentifier.getType())) && (localInstruction.getTmpInfo() != null)) {
          ((StackLocalInfo)localInstruction.getTmpInfo()).enqueue();
        }
      }
    }
  }
  
  public void dumpStackLocalInfo()
  {
    Iterator localIterator = this.bytecode.getInstructions().iterator();
    while (localIterator.hasNext())
    {
      Instruction localInstruction = (Instruction)localIterator.next();
      System.err.println("" + localInstruction.getTmpInfo());
      System.err.println(localInstruction.getDescription());
    }
  }
  
  public void analyzeCode(MethodIdentifier paramMethodIdentifier, BytecodeInfo paramBytecodeInfo)
  {
    this.bytecode = paramBytecodeInfo;
    TodoQueue localTodoQueue = new TodoQueue(null);
    MethodInfo localMethodInfo = paramBytecodeInfo.getMethodInfo();
    Object localObject1 = paramBytecodeInfo.getInstructions().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Instruction)((Iterator)localObject1).next();
      ((Instruction)localObject2).setTmpInfo(null);
    }
    localObject1 = new StackLocalInfo(paramBytecodeInfo.getMaxLocals(), localMethodInfo.isStatic(), localMethodInfo.getType(), localTodoQueue);
    ((StackLocalInfo)localObject1).instr = ((Instruction)paramBytecodeInfo.getInstructions().get(0));
    ((StackLocalInfo)localObject1).instr.setTmpInfo(localObject1);
    ((StackLocalInfo)localObject1).enqueue();
    runtime.setFieldListener(paramMethodIdentifier);
    while (localTodoQueue.first != null)
    {
      localObject2 = localTodoQueue.first;
      localTodoQueue.first = ((StackLocalInfo)localObject2).nextOnQueue;
      ((StackLocalInfo)localObject2).nextOnQueue = null;
      handleOpcode((StackLocalInfo)localObject2, paramMethodIdentifier);
    }
    runtime.setFieldListener(null);
    Object localObject2 = paramBytecodeInfo.getExceptionHandlers();
    for (int i = 0; i < localObject2.length; i++) {
      if ((localObject2[i].catcher.getTmpInfo() != null) && (localObject2[i].type != null)) {
        Main.getClassBundle().reachableClass(localObject2[i].type);
      }
    }
    Iterator localIterator = paramBytecodeInfo.getInstructions().iterator();
    while (localIterator.hasNext())
    {
      Instruction localInstruction = (Instruction)localIterator.next();
      StackLocalInfo localStackLocalInfo = (StackLocalInfo)localInstruction.getTmpInfo();
      if (localStackLocalInfo != null) {
        if (localStackLocalInfo.constInfo.flags == 0) {
          localInstruction.setTmpInfo(unknownConstInfo);
        } else {
          localInstruction.setTmpInfo(localStackLocalInfo.constInfo);
        }
      }
    }
  }
  
  public static void replaceWith(ListIterator paramListIterator, Instruction paramInstruction1, Instruction paramInstruction2)
  {
    switch (paramInstruction1.getOpcode())
    {
    case 18: 
    case 20: 
    case 21: 
    case 22: 
    case 23: 
    case 24: 
    case 25: 
    case 167: 
    case 178: 
      if (paramInstruction2 == null) {
        paramListIterator.remove();
      } else {
        paramListIterator.set(paramInstruction2);
      }
      return;
    case 116: 
    case 118: 
    case 133: 
    case 134: 
    case 135: 
    case 139: 
    case 140: 
    case 141: 
    case 145: 
    case 146: 
    case 147: 
    case 153: 
    case 154: 
    case 155: 
    case 156: 
    case 157: 
    case 158: 
    case 180: 
    case 190: 
    case 198: 
    case 199: 
      paramListIterator.set(new Instruction(87));
      break;
    case 97: 
    case 99: 
    case 101: 
    case 103: 
    case 105: 
    case 107: 
    case 109: 
    case 111: 
    case 113: 
    case 115: 
    case 127: 
    case 129: 
    case 131: 
    case 148: 
    case 151: 
    case 152: 
      paramListIterator.set(new Instruction(88));
      paramListIterator.add(new Instruction(88));
      break;
    case 46: 
    case 47: 
    case 48: 
    case 49: 
    case 50: 
    case 51: 
    case 52: 
    case 53: 
    case 96: 
    case 98: 
    case 100: 
    case 102: 
    case 104: 
    case 106: 
    case 108: 
    case 110: 
    case 112: 
    case 114: 
    case 117: 
    case 119: 
    case 120: 
    case 122: 
    case 124: 
    case 126: 
    case 128: 
    case 130: 
    case 136: 
    case 137: 
    case 138: 
    case 142: 
    case 143: 
    case 144: 
    case 149: 
    case 150: 
    case 159: 
    case 160: 
    case 161: 
    case 162: 
    case 163: 
    case 164: 
    case 165: 
    case 166: 
      paramListIterator.set(new Instruction(88));
      break;
    case 121: 
    case 123: 
    case 125: 
      paramListIterator.set(new Instruction(87));
      paramListIterator.add(new Instruction(88));
      break;
    case 179: 
    case 181: 
      if (TypeSignature.getTypeSize(paramInstruction1.getReference().getType()) == 2)
      {
        paramListIterator.set(new Instruction(88));
        if (paramInstruction1.getOpcode() == 181) {
          paramListIterator.add(new Instruction(87));
        }
      }
      else
      {
        paramListIterator.set(new Instruction(paramInstruction1.getOpcode() == 181 ? 88 : 87));
      }
      break;
    case 182: 
    case 183: 
    case 184: 
    case 185: 
      Reference localReference = paramInstruction1.getReference();
      String[] arrayOfString = TypeSignature.getParameterTypes(localReference.getType());
      int i = arrayOfString.length;
      if (i > 0)
      {
        paramListIterator.set(new Instruction(TypeSignature.getTypeSize(arrayOfString[(--i)]) + 87 - 1));
        for (int j = i - 1; j >= 0; j--) {
          paramListIterator.add(new Instruction(TypeSignature.getTypeSize(arrayOfString[j]) + 87 - 1));
        }
        if (paramInstruction1.getOpcode() != 184) {
          paramListIterator.add(new Instruction(87));
        }
      }
      else if (paramInstruction1.getOpcode() != 184)
      {
        paramListIterator.set(new Instruction(87));
      }
      else
      {
        if (paramInstruction2 == null) {
          paramListIterator.remove();
        } else {
          paramListIterator.set(paramInstruction2);
        }
        return;
      }
      break;
    }
    if (paramInstruction2 != null) {
      paramListIterator.add(paramInstruction2);
    }
  }
  
  public void appendJump(ListIterator paramListIterator, Instruction paramInstruction)
  {
    Instruction localInstruction = new Instruction(167);
    localInstruction.setSuccs(paramInstruction);
    paramListIterator.add(localInstruction);
  }
  
  public void transformCode(BytecodeInfo paramBytecodeInfo)
  {
    ListIterator localListIterator = paramBytecodeInfo.getInstructions().listIterator();
    while (localListIterator.hasNext())
    {
      Instruction localInstruction1 = (Instruction)localListIterator.next();
      ConstantInfo localConstantInfo1 = (ConstantInfo)localInstruction1.getTmpInfo();
      localInstruction1.setTmpInfo(null);
      if ((localConstantInfo1 == null) || ((localConstantInfo1.flags & 0x18) == 8))
      {
        localListIterator.remove();
      }
      else
      {
        Instruction localInstruction2;
        if ((localConstantInfo1.flags & 0x2) != 0)
        {
          if (localInstruction1.getOpcode() > 20)
          {
            localInstruction2 = new Instruction(((localConstantInfo1.constant instanceof Long)) || ((localConstantInfo1.constant instanceof Double)) ? 20 : 18);
            localInstruction2.setConstant(localConstantInfo1.constant);
            replaceWith(localListIterator, localInstruction1, localInstruction2);
            if (GlobalOptions.verboseLevel > 2) {
              GlobalOptions.err.println(paramBytecodeInfo + ": Replacing " + localInstruction1 + " with constant " + localConstantInfo1.constant);
            }
          }
        }
        else
        {
          ConstantInfo localConstantInfo2;
          Instruction localInstruction3;
          if ((localConstantInfo1.flags & 0x4) != 0)
          {
            localInstruction2 = (Instruction)localConstantInfo1.constant;
            if ((localInstruction1.getOpcode() >= 159) && (localInstruction1.getOpcode() <= 166)) {
              localListIterator.set(new Instruction(88));
            } else {
              localListIterator.set(new Instruction(87));
            }
            if (GlobalOptions.verboseLevel > 2) {
              GlobalOptions.err.println(paramBytecodeInfo + ": Replacing " + localInstruction1 + " with goto " + localInstruction2.getAddr());
            }
            while (localListIterator.hasNext())
            {
              localConstantInfo2 = (ConstantInfo)((Instruction)localListIterator.next()).getTmpInfo();
              if (localConstantInfo2 != null)
              {
                localInstruction3 = (Instruction)localListIterator.previous();
                if (localInstruction2 == localInstruction3) {
                  break;
                }
                appendJump(localListIterator, localInstruction2);
                break;
              }
              localListIterator.remove();
            }
          }
          else
          {
            int i = localInstruction1.getOpcode();
            switch (i)
            {
            case 0: 
              localListIterator.remove();
              break;
            case 168: 
              localConstantInfo2 = (ConstantInfo)localInstruction1.getSingleSucc().getTmpInfo();
              if ((localConstantInfo2.flags & 0x10) == 0)
              {
                localInstruction3 = new Instruction(167);
                localInstruction3.setSuccs(localInstruction1.getSingleSucc());
                localListIterator.set(localInstruction3);
              }
              break;
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 167: 
            case 198: 
            case 199: 
            case 179: 
            case 181: 
              while (localListIterator.hasNext())
              {
                Object localObject1 = (ConstantInfo)((Instruction)localListIterator.next()).getTmpInfo();
                Object localObject2;
                if ((localObject1 != null) && ((((ConstantInfo)localObject1).flags & 0x18) != 8))
                {
                  localObject2 = (Instruction)localListIterator.previous();
                  if (localInstruction1.getSingleSucc() == localObject2)
                  {
                    localListIterator.previous();
                    localListIterator.next();
                    replaceWith(localListIterator, localInstruction1, null);
                  }
                }
                else
                {
                  localListIterator.remove();
                  continue;
                  localObject1 = localInstruction1.getReference();
                  localObject2 = (FieldIdentifier)Main.getClassBundle().getIdentifier((Reference)localObject1);
                  if ((localObject2 != null) && ((Main.stripping & 0x1) != 0) && (!((FieldIdentifier)localObject2).isReachable())) {
                    replaceWith(localListIterator, localInstruction1, null);
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  private static class ConstantInfo
    implements ConstantAnalyzer.ConstantListener
  {
    int flags;
    Object constant;
    
    ConstantInfo()
    {
      this(0, null);
    }
    
    ConstantInfo(int paramInt)
    {
      this(paramInt, null);
    }
    
    ConstantInfo(int paramInt, Object paramObject)
    {
      this.flags = paramInt;
      this.constant = paramObject;
    }
    
    public void constantChanged()
    {
      this.constant = null;
      this.flags &= 0xFFFFFFF9;
    }
  }
  
  private static class StackLocalInfo
    implements ConstantAnalyzer.ConstantListener
  {
    ConstantAnalyzer.ConstValue[] stack;
    ConstantAnalyzer.ConstValue[] locals;
    Instruction instr;
    ConstantAnalyzer.ConstantInfo constInfo;
    StackLocalInfo retInfo;
    StackLocalInfo nextOnQueue;
    ConstantAnalyzer.TodoQueue notifyQueue;
    
    public ConstantAnalyzer.ConstValue copy(ConstantAnalyzer.ConstValue paramConstValue)
    {
      return paramConstValue == null ? null : paramConstValue.copy();
    }
    
    private StackLocalInfo(ConstantAnalyzer.ConstValue[] paramArrayOfConstValue1, ConstantAnalyzer.ConstValue[] paramArrayOfConstValue2, ConstantAnalyzer.TodoQueue paramTodoQueue)
    {
      this.stack = paramArrayOfConstValue1;
      this.locals = new ConstantAnalyzer.ConstValue[paramArrayOfConstValue2.length];
      for (int i = 0; i < paramArrayOfConstValue2.length; i++) {
        this.locals[i] = copy(paramArrayOfConstValue2[i]);
      }
      this.notifyQueue = paramTodoQueue;
    }
    
    public StackLocalInfo(int paramInt, boolean paramBoolean, String paramString, ConstantAnalyzer.TodoQueue paramTodoQueue)
    {
      String[] arrayOfString = TypeSignature.getParameterTypes(paramString);
      this.locals = new ConstantAnalyzer.ConstValue[paramInt];
      this.stack = new ConstantAnalyzer.ConstValue[0];
      this.notifyQueue = paramTodoQueue;
      int i = 0;
      if (!paramBoolean) {
        this.locals[(i++)] = new ConstantAnalyzer.ConstValue(1);
      }
      for (int j = 0; j < arrayOfString.length; j++)
      {
        int k = TypeSignature.getTypeSize(arrayOfString[j]);
        this.locals[i] = ConstantAnalyzer.unknownValue[(k - 1)];
        i += k;
      }
    }
    
    public final void enqueue()
    {
      if (this.nextOnQueue == null)
      {
        this.nextOnQueue = this.notifyQueue.first;
        this.notifyQueue.first = this;
      }
    }
    
    public void constantChanged()
    {
      enqueue();
    }
    
    public StackLocalInfo poppush(int paramInt, ConstantAnalyzer.ConstValue paramConstValue)
    {
      ConstantAnalyzer.ConstValue[] arrayOfConstValue1 = new ConstantAnalyzer.ConstValue[this.stack.length - paramInt + paramConstValue.stackSize];
      ConstantAnalyzer.ConstValue[] arrayOfConstValue2 = (ConstantAnalyzer.ConstValue[])this.locals.clone();
      System.arraycopy(this.stack, 0, arrayOfConstValue1, 0, this.stack.length - paramInt);
      arrayOfConstValue1[(this.stack.length - paramInt)] = paramConstValue.copy();
      return new StackLocalInfo(arrayOfConstValue1, arrayOfConstValue2, this.notifyQueue);
    }
    
    public StackLocalInfo pop(int paramInt)
    {
      ConstantAnalyzer.ConstValue[] arrayOfConstValue1 = new ConstantAnalyzer.ConstValue[this.stack.length - paramInt];
      ConstantAnalyzer.ConstValue[] arrayOfConstValue2 = (ConstantAnalyzer.ConstValue[])this.locals.clone();
      System.arraycopy(this.stack, 0, arrayOfConstValue1, 0, this.stack.length - paramInt);
      return new StackLocalInfo(arrayOfConstValue1, arrayOfConstValue2, this.notifyQueue);
    }
    
    public StackLocalInfo dup(int paramInt1, int paramInt2)
    {
      ConstantAnalyzer.ConstValue[] arrayOfConstValue1 = new ConstantAnalyzer.ConstValue[this.stack.length + paramInt1];
      ConstantAnalyzer.ConstValue[] arrayOfConstValue2 = (ConstantAnalyzer.ConstValue[])this.locals.clone();
      if (paramInt2 == 0)
      {
        System.arraycopy(this.stack, 0, arrayOfConstValue1, 0, this.stack.length);
      }
      else
      {
        i = this.stack.length - paramInt1 - paramInt2;
        System.arraycopy(this.stack, 0, arrayOfConstValue1, 0, i);
        for (int j = 0; j < paramInt1; j++) {
          arrayOfConstValue1[(i++)] = copy(this.stack[(this.stack.length - paramInt1 + j)]);
        }
        for (j = 0; j < paramInt2; j++) {
          arrayOfConstValue1[(i++)] = copy(this.stack[(this.stack.length - paramInt1 - paramInt2 + j)]);
        }
      }
      for (int i = 0; i < paramInt1; i++) {
        arrayOfConstValue1[(this.stack.length + i)] = copy(this.stack[(this.stack.length - paramInt1 + i)]);
      }
      return new StackLocalInfo(arrayOfConstValue1, arrayOfConstValue2, this.notifyQueue);
    }
    
    public StackLocalInfo swap()
    {
      ConstantAnalyzer.ConstValue[] arrayOfConstValue1 = new ConstantAnalyzer.ConstValue[this.stack.length];
      ConstantAnalyzer.ConstValue[] arrayOfConstValue2 = (ConstantAnalyzer.ConstValue[])this.locals.clone();
      System.arraycopy(this.stack, 0, arrayOfConstValue1, 0, this.stack.length - 2);
      arrayOfConstValue1[(this.stack.length - 2)] = this.stack[(this.stack.length - 1)].copy();
      arrayOfConstValue1[(this.stack.length - 1)] = this.stack[(this.stack.length - 2)].copy();
      return new StackLocalInfo(arrayOfConstValue1, arrayOfConstValue2, this.notifyQueue);
    }
    
    public StackLocalInfo copy()
    {
      ConstantAnalyzer.ConstValue[] arrayOfConstValue1 = (ConstantAnalyzer.ConstValue[])this.stack.clone();
      ConstantAnalyzer.ConstValue[] arrayOfConstValue2 = (ConstantAnalyzer.ConstValue[])this.locals.clone();
      return new StackLocalInfo(arrayOfConstValue1, arrayOfConstValue2, this.notifyQueue);
    }
    
    public ConstantAnalyzer.ConstValue getLocal(int paramInt)
    {
      return this.locals[paramInt];
    }
    
    public ConstantAnalyzer.ConstValue getStack(int paramInt)
    {
      return this.stack[(this.stack.length - paramInt)];
    }
    
    public StackLocalInfo setLocal(int paramInt, ConstantAnalyzer.ConstValue paramConstValue)
    {
      this.locals[paramInt] = paramConstValue;
      if ((paramConstValue != null) && (paramConstValue.stackSize == 2)) {
        this.locals[(paramInt + 1)] = null;
      }
      ConstantAnalyzer.JSRTargetInfo localJSRTargetInfo;
      for (int i = 0; i < this.locals.length; i++) {
        if ((this.locals[i] != null) && ((this.locals[i].value instanceof ConstantAnalyzer.JSRTargetInfo)))
        {
          localJSRTargetInfo = (ConstantAnalyzer.JSRTargetInfo)this.locals[i].value;
          if (!localJSRTargetInfo.uses(paramInt))
          {
            localJSRTargetInfo = localJSRTargetInfo.copy();
            this.locals[i] = this.locals[i].copy();
            this.locals[i].value = localJSRTargetInfo;
            localJSRTargetInfo.addUsed(paramInt);
          }
        }
      }
      for (i = 0; i < this.stack.length; i++) {
        if ((this.stack[i] != null) && ((this.stack[i].value instanceof ConstantAnalyzer.JSRTargetInfo)))
        {
          localJSRTargetInfo = (ConstantAnalyzer.JSRTargetInfo)this.stack[i].value;
          if (!localJSRTargetInfo.uses(paramInt))
          {
            localJSRTargetInfo = localJSRTargetInfo.copy();
            this.stack[i] = this.stack[i].copy();
            this.stack[i].value = localJSRTargetInfo;
            localJSRTargetInfo.addUsed(paramInt);
          }
        }
      }
      return this;
    }
    
    public StackLocalInfo mergeRetLocals(ConstantAnalyzer.JSRTargetInfo paramJSRTargetInfo, StackLocalInfo paramStackLocalInfo)
    {
      for (int i = 0; i < this.locals.length; i++) {
        if (paramJSRTargetInfo.uses(i)) {
          this.locals[i] = paramStackLocalInfo.locals[i];
        }
      }
      this.locals[paramStackLocalInfo.instr.getLocalSlot()] = null;
      ConstantAnalyzer.JSRTargetInfo localJSRTargetInfo;
      int j;
      for (i = 0; i < this.locals.length; i++) {
        if ((this.locals[i] != null) && ((this.locals[i].value instanceof ConstantAnalyzer.JSRTargetInfo)))
        {
          localJSRTargetInfo = (ConstantAnalyzer.JSRTargetInfo)this.locals[i].value;
          localJSRTargetInfo = localJSRTargetInfo.copy();
          this.locals[i] = this.locals[i].copy();
          this.locals[i].value = localJSRTargetInfo;
          for (j = 0; j < this.locals.length; j++) {
            if (paramJSRTargetInfo.uses(j)) {
              localJSRTargetInfo.addUsed(j);
            }
          }
        }
      }
      for (i = 0; i < this.stack.length; i++) {
        if ((this.stack[i] != null) && ((this.stack[i].value instanceof ConstantAnalyzer.JSRTargetInfo)))
        {
          localJSRTargetInfo = (ConstantAnalyzer.JSRTargetInfo)this.stack[i].value;
          localJSRTargetInfo = localJSRTargetInfo.copy();
          this.stack[i] = this.stack[i].copy();
          this.stack[i].value = localJSRTargetInfo;
          for (j = 0; j < this.locals.length; j++) {
            if (paramJSRTargetInfo.uses(j)) {
              localJSRTargetInfo.addUsed(j);
            }
          }
        }
      }
      return this;
    }
    
    public void merge(StackLocalInfo paramStackLocalInfo)
    {
      for (int i = 0; i < this.locals.length; i++) {
        if (this.locals[i] != null) {
          if (paramStackLocalInfo.locals[i] == null)
          {
            this.locals[i].constantChanged();
            this.locals[i] = null;
            enqueue();
          }
          else
          {
            this.locals[i].merge(paramStackLocalInfo.locals[i]);
          }
        }
      }
      if (this.stack.length != paramStackLocalInfo.stack.length) {
        throw new AssertError("stack length differs");
      }
      for (i = 0; i < this.stack.length; i++)
      {
        if ((paramStackLocalInfo.stack[i] == null ? 1 : 0) != (this.stack[i] == null ? 1 : 0)) {
          throw new AssertError("stack types differ");
        }
        if (this.stack[i] != null) {
          this.stack[i].merge(paramStackLocalInfo.stack[i]);
        }
      }
    }
    
    public String toString()
    {
      return "Locals: " + Arrays.asList(this.locals) + "Stack: " + Arrays.asList(this.stack) + "Instr: " + this.instr;
    }
  }
  
  private static class TodoQueue
  {
    ConstantAnalyzer.StackLocalInfo first;
  }
  
  private static class ConstValue
    implements ConstantAnalyzer.ConstantListener
  {
    public static final Object VOLATILE = new Object();
    Object value;
    int stackSize;
    Set listeners;
    
    public ConstValue(Object paramObject)
    {
      this.value = paramObject;
      this.stackSize = (((paramObject instanceof Double)) || ((paramObject instanceof Long)) ? 2 : 1);
      this.listeners = new HashSet();
    }
    
    public ConstValue(ConstValue paramConstValue)
    {
      this.value = paramConstValue.value;
      this.stackSize = paramConstValue.stackSize;
      this.listeners = new HashSet();
      paramConstValue.addConstantListener(this);
    }
    
    public ConstValue(int paramInt)
    {
      this.value = VOLATILE;
      this.stackSize = paramInt;
    }
    
    public ConstValue copy()
    {
      return this.value == VOLATILE ? this : new ConstValue(this);
    }
    
    public void addConstantListener(ConstantAnalyzer.ConstantListener paramConstantListener)
    {
      this.listeners.add(paramConstantListener);
    }
    
    public void removeConstantListener(ConstantAnalyzer.ConstantListener paramConstantListener)
    {
      this.listeners.remove(paramConstantListener);
    }
    
    public void fireChanged()
    {
      this.value = VOLATILE;
      Iterator localIterator = this.listeners.iterator();
      while (localIterator.hasNext()) {
        ((ConstantAnalyzer.ConstantListener)localIterator.next()).constantChanged();
      }
      this.listeners = null;
    }
    
    public void constantChanged()
    {
      if (this.value != VOLATILE) {
        fireChanged();
      }
    }
    
    public void merge(ConstValue paramConstValue)
    {
      if (this == paramConstValue) {
        return;
      }
      if (this.value == null ? paramConstValue.value == null : this.value.equals(paramConstValue.value))
      {
        if (this.value != VOLATILE)
        {
          paramConstValue.addConstantListener(this);
          addConstantListener(paramConstValue);
        }
        return;
      }
      if (((this.value instanceof ConstantAnalyzer.JSRTargetInfo)) && ((paramConstValue.value instanceof ConstantAnalyzer.JSRTargetInfo)) && (((ConstantAnalyzer.JSRTargetInfo)this.value).jsrTarget == ((ConstantAnalyzer.JSRTargetInfo)paramConstValue.value).jsrTarget))
      {
        ((ConstantAnalyzer.JSRTargetInfo)this.value).merge((ConstantAnalyzer.JSRTargetInfo)paramConstValue.value);
        return;
      }
      if (this.value != VOLATILE) {
        fireChanged();
      }
    }
    
    public String toString()
    {
      return "" + this.value;
    }
  }
  
  private static final class JSRTargetInfo
    implements Cloneable
  {
    Instruction jsrTarget;
    BitSet usedLocals;
    Object dependent;
    
    public JSRTargetInfo(Instruction paramInstruction)
    {
      this.jsrTarget = paramInstruction;
      this.usedLocals = new BitSet();
    }
    
    public JSRTargetInfo copy()
    {
      try
      {
        JSRTargetInfo localJSRTargetInfo = (JSRTargetInfo)clone();
        localJSRTargetInfo.usedLocals = ((BitSet)this.usedLocals.clone());
        addDependent(localJSRTargetInfo);
        return localJSRTargetInfo;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new IncompatibleClassChangeError(localCloneNotSupportedException.getMessage());
      }
    }
    
    private void addDependent(JSRTargetInfo paramJSRTargetInfo)
    {
      if ((this.dependent == null) || (this.dependent == paramJSRTargetInfo))
      {
        this.dependent = paramJSRTargetInfo;
      }
      else if ((this.dependent instanceof JSRTargetInfo))
      {
        HashSet localHashSet = new HashSet();
        localHashSet.add(this.dependent);
        localHashSet.add(paramJSRTargetInfo);
      }
      else if ((this.dependent instanceof Collection))
      {
        ((Collection)this.dependent).add(paramJSRTargetInfo);
      }
    }
    
    public void setRetInfo(ConstantAnalyzer.StackLocalInfo paramStackLocalInfo)
    {
      this.dependent = paramStackLocalInfo;
    }
    
    public boolean uses(int paramInt)
    {
      return this.usedLocals.get(paramInt);
    }
    
    public void addUsed(int paramInt)
    {
      if (this.usedLocals.get(paramInt)) {
        return;
      }
      this.usedLocals.set(paramInt);
      if ((this.dependent instanceof ConstantAnalyzer.StackLocalInfo))
      {
        ((ConstantAnalyzer.StackLocalInfo)this.dependent).enqueue();
      }
      else if ((this.dependent instanceof JSRTargetInfo))
      {
        ((JSRTargetInfo)this.dependent).addUsed(paramInt);
      }
      else if ((this.dependent instanceof Collection))
      {
        Iterator localIterator = ((Collection)this.dependent).iterator();
        while (localIterator.hasNext())
        {
          JSRTargetInfo localJSRTargetInfo = (JSRTargetInfo)localIterator.next();
          localJSRTargetInfo.addUsed(paramInt);
        }
      }
    }
    
    public void merge(JSRTargetInfo paramJSRTargetInfo)
    {
      paramJSRTargetInfo.addDependent(this);
      for (int i = 0; i < paramJSRTargetInfo.usedLocals.size(); i++) {
        if (paramJSRTargetInfo.usedLocals.get(i)) {
          addUsed(i);
        }
      }
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer(String.valueOf(this.jsrTarget));
      if ((this.dependent instanceof ConstantAnalyzer.StackLocalInfo)) {
        localStringBuffer.append("->").append(((ConstantAnalyzer.StackLocalInfo)this.dependent).instr);
      }
      return this.usedLocals + '_' + hashCode();
    }
  }
  
  private static abstract interface ConstantListener
  {
    public abstract void constantChanged();
  }
}


