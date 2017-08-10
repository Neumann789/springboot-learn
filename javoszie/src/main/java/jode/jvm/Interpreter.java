package jode.jvm;

import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import jode.AssertError;
import jode.GlobalOptions;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.MethodInfo;
import jode.bytecode.Opcodes;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;

public class Interpreter
  implements Opcodes
{
  private static final int CMP_EQ = 0;
  private static final int CMP_NE = 1;
  private static final int CMP_LT = 2;
  private static final int CMP_GE = 3;
  private static final int CMP_GT = 4;
  private static final int CMP_LE = 5;
  private static final int CMP_GREATER_MASK = 26;
  private static final int CMP_LESS_MASK = 38;
  private static final int CMP_EQUAL_MASK = 41;
  private RuntimeEnvironment env;
  
  public Interpreter(RuntimeEnvironment paramRuntimeEnvironment)
  {
    this.env = paramRuntimeEnvironment;
  }
  
  private Value[] fillParameters(BytecodeInfo paramBytecodeInfo, Object paramObject, Object[] paramArrayOfObject)
  {
    Value[] arrayOfValue = new Value[paramBytecodeInfo.getMaxLocals()];
    for (int i = 0; i < arrayOfValue.length; i++) {
      arrayOfValue[i] = new Value();
    }
    String str = paramBytecodeInfo.getMethodInfo().getType();
    String[] arrayOfString = TypeSignature.getParameterTypes(str);
    int j = 0;
    if (!paramBytecodeInfo.getMethodInfo().isStatic()) {
      arrayOfValue[(j++)].setObject(paramObject);
    }
    for (int k = 0; k < arrayOfString.length; k++)
    {
      arrayOfValue[j].setObject(paramArrayOfObject[k]);
      j += TypeSignature.getTypeSize(arrayOfString[k]);
    }
    return arrayOfValue;
  }
  
  public Object interpretMethod(BytecodeInfo paramBytecodeInfo, Object paramObject, Object[] paramArrayOfObject)
    throws InterpreterException, InvocationTargetException
  {
    if ((GlobalOptions.debuggingFlags & 0x400) != 0) {
      GlobalOptions.err.println("Interpreting " + paramBytecodeInfo);
    }
    Value[] arrayOfValue1 = fillParameters(paramBytecodeInfo, paramObject, paramArrayOfObject);
    Value[] arrayOfValue2 = new Value[paramBytecodeInfo.getMaxStack()];
    for (int i = 0; i < arrayOfValue2.length; i++) {
      arrayOfValue2[i] = new Value();
    }
    Instruction localInstruction1 = (Instruction)paramBytecodeInfo.getInstructions().get(0);
    int j = 0;
    try
    {
      for (;;)
      {
        Instruction localInstruction2 = localInstruction1;
        if ((GlobalOptions.debuggingFlags & 0x400) != 0)
        {
          GlobalOptions.err.println(localInstruction2.getDescription());
          GlobalOptions.err.print("stack: [");
          for (k = 0; k < j; k++)
          {
            if (k > 0) {
              GlobalOptions.err.print(",");
            }
            GlobalOptions.err.print(arrayOfValue2[k]);
            if ((arrayOfValue2[k].objectValue() instanceof char[])) {
              GlobalOptions.err.print(new String((char[])arrayOfValue2[k].objectValue()));
            }
          }
          GlobalOptions.err.println("]");
          GlobalOptions.err.print("local: [");
          for (k = 0; k < arrayOfValue1.length; k++) {
            GlobalOptions.err.print(arrayOfValue1[k] + ",");
          }
          GlobalOptions.err.println("]");
        }
        localInstruction1 = localInstruction2.getNextByAddr();
        int k = localInstruction2.getOpcode();
        Object localObject5;
        int i3;
        int n;
        int i1;
        Object localObject4;
        Object localObject1;
        Object localObject6;
        switch (k)
        {
        case 0: 
          break;
        case 18: 
          arrayOfValue2[(j++)].setObject(localInstruction2.getConstant());
          break;
        case 20: 
          arrayOfValue2[j].setObject(localInstruction2.getConstant());
          j += 2;
          break;
        case 21: 
        case 23: 
        case 25: 
          arrayOfValue2[(j++)].setValue(arrayOfValue1[localInstruction2.getLocalSlot()]);
          break;
        case 22: 
        case 24: 
          arrayOfValue2[j].setValue(arrayOfValue1[localInstruction2.getLocalSlot()]);
          j += 2;
          break;
        case 46: 
        case 47: 
        case 48: 
        case 49: 
        case 50: 
        case 51: 
        case 52: 
        case 53: 
          int m = arrayOfValue2[(--j)].intValue();
          Object localObject2 = arrayOfValue2[(--j)].objectValue();
          try
          {
            switch (k)
            {
            case 51: 
              localObject5 = new Integer(((boolean[])(boolean[])localObject2)[m] != 0 ? 1 : (localObject2 instanceof byte[]) ? ((byte[])(byte[])localObject2)[m] : 0);
              break;
            case 52: 
              localObject5 = new Integer(((char[])(char[])localObject2)[m]);
              break;
            case 53: 
              localObject5 = new Integer(((short[])(short[])localObject2)[m]);
              break;
            default: 
              localObject5 = Array.get(localObject2, m);
            }
          }
          catch (NullPointerException localNullPointerException1)
          {
            throw new InvocationTargetException(localNullPointerException1);
          }
          catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException1)
          {
            throw new InvocationTargetException(localArrayIndexOutOfBoundsException1);
          }
          arrayOfValue2[(j++)].setObject(localObject5);
          if ((k == 47) || (k == 49)) {
            j++;
          }
          break;
        case 54: 
        case 56: 
        case 58: 
          arrayOfValue1[localInstruction2.getLocalSlot()].setValue(arrayOfValue2[(--j)]);
          break;
        case 55: 
        case 57: 
          j -= 2;
          arrayOfValue1[localInstruction2.getLocalSlot()].setValue(arrayOfValue2[j]);
          break;
        case 80: 
        case 82: 
          j--;
        case 79: 
        case 81: 
        case 83: 
        case 84: 
        case 85: 
        case 86: 
          Value localValue1 = arrayOfValue2[(--j)];
          i3 = arrayOfValue2[(--j)].intValue();
          localObject5 = arrayOfValue2[(--j)].objectValue();
          try
          {
            switch (k)
            {
            case 84: 
              if ((localObject5 instanceof byte[])) {
                ((byte[])localObject5)[i3] = ((byte)localValue1.intValue());
              } else {
                ((boolean[])localObject5)[i3] = (localValue1.intValue() != 0 ? 1 : 0);
              }
              break;
            case 85: 
              ((char[])localObject5)[i3] = ((char)localValue1.intValue());
              break;
            case 86: 
              ((short[])localObject5)[i3] = ((short)localValue1.intValue());
              break;
            default: 
              Array.set(localObject5, i3, localValue1.objectValue());
            }
          }
          catch (NullPointerException localNullPointerException2)
          {
            throw new InvocationTargetException(localNullPointerException2);
          }
          catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException2)
          {
            throw new InvocationTargetException(localArrayIndexOutOfBoundsException2);
          }
          catch (ArrayStoreException localArrayStoreException)
          {
            throw new InvocationTargetException(localArrayStoreException);
          }
        case 87: 
        case 88: 
          j -= k - 86;
          break;
        case 89: 
        case 90: 
        case 91: 
          n = k - 89;
          for (i3 = 0; i3 < n + 1; i3++) {
            arrayOfValue2[(j - i3)].setValue(arrayOfValue2[(j - i3 - 1)]);
          }
          arrayOfValue2[(j - n - 1)].setValue(arrayOfValue2[j]);
          j++;
          break;
        case 92: 
        case 93: 
        case 94: 
          n = k - 92;
          for (i3 = 0; i3 < n + 2; i3++) {
            arrayOfValue2[(j + 1 - i3)].setValue(arrayOfValue2[(j - 1 - i3)]);
          }
          arrayOfValue2[(j - n - 1)].setValue(arrayOfValue2[(j + 1)]);
          arrayOfValue2[(j - n - 2)].setValue(arrayOfValue2[j]);
          j += 2;
          break;
        case 95: 
          Value localValue2 = arrayOfValue2[(j - 1)];
          arrayOfValue2[(j - 1)] = arrayOfValue2[(j - 2)];
          arrayOfValue2[(j - 2)] = localValue2;
          break;
        case 96: 
          arrayOfValue2[(j - 2)].setInt(arrayOfValue2[(j - 2)].intValue() + arrayOfValue2[(j - 1)].intValue());
          j--;
          break;
        case 100: 
          arrayOfValue2[(j - 2)].setInt(arrayOfValue2[(j - 2)].intValue() - arrayOfValue2[(j - 1)].intValue());
          j--;
          break;
        case 104: 
          arrayOfValue2[(j - 2)].setInt(arrayOfValue2[(j - 2)].intValue() * arrayOfValue2[(j - 1)].intValue());
          j--;
          break;
        case 108: 
          try
          {
            arrayOfValue2[(j - 2)].setInt(arrayOfValue2[(j - 2)].intValue() / arrayOfValue2[(j - 1)].intValue());
          }
          catch (ArithmeticException localArithmeticException1)
          {
            throw new InvocationTargetException(localArithmeticException1);
          }
          j--;
          break;
        case 112: 
          try
          {
            arrayOfValue2[(j - 2)].setInt(arrayOfValue2[(j - 2)].intValue() % arrayOfValue2[(j - 1)].intValue());
          }
          catch (ArithmeticException localArithmeticException2)
          {
            throw new InvocationTargetException(localArithmeticException2);
          }
          j--;
          break;
        case 97: 
          j -= 2;
          arrayOfValue2[(j - 2)].setLong(arrayOfValue2[(j - 2)].longValue() + arrayOfValue2[j].longValue());
          break;
        case 101: 
          j -= 2;
          arrayOfValue2[(j - 2)].setLong(arrayOfValue2[(j - 2)].longValue() - arrayOfValue2[j].longValue());
          break;
        case 105: 
          j -= 2;
          arrayOfValue2[(j - 2)].setLong(arrayOfValue2[(j - 2)].longValue() * arrayOfValue2[j].longValue());
          break;
        case 109: 
          j -= 2;
          try
          {
            arrayOfValue2[(j - 2)].setLong(arrayOfValue2[(j - 2)].longValue() / arrayOfValue2[j].longValue());
          }
          catch (ArithmeticException localArithmeticException3)
          {
            throw new InvocationTargetException(localArithmeticException3);
          }
        case 113: 
          j -= 2;
          try
          {
            arrayOfValue2[(j - 2)].setLong(arrayOfValue2[(j - 2)].longValue() % arrayOfValue2[j].longValue());
          }
          catch (ArithmeticException localArithmeticException4)
          {
            throw new InvocationTargetException(localArithmeticException4);
          }
        case 98: 
          arrayOfValue2[(j - 2)].setFloat(arrayOfValue2[(j - 2)].floatValue() + arrayOfValue2[(j - 1)].floatValue());
          j--;
          break;
        case 102: 
          arrayOfValue2[(j - 2)].setFloat(arrayOfValue2[(j - 2)].floatValue() - arrayOfValue2[(j - 1)].floatValue());
          j--;
          break;
        case 106: 
          arrayOfValue2[(j - 2)].setFloat(arrayOfValue2[(j - 2)].floatValue() * arrayOfValue2[(j - 1)].floatValue());
          j--;
          break;
        case 110: 
          arrayOfValue2[(j - 2)].setFloat(arrayOfValue2[(j - 2)].floatValue() / arrayOfValue2[(j - 1)].floatValue());
          j--;
          break;
        case 114: 
          arrayOfValue2[(j - 2)].setFloat(arrayOfValue2[(j - 2)].floatValue() % arrayOfValue2[(j - 1)].floatValue());
          j--;
          break;
        case 99: 
          j -= 2;
          arrayOfValue2[(j - 2)].setDouble(arrayOfValue2[(j - 2)].doubleValue() + arrayOfValue2[j].doubleValue());
          break;
        case 103: 
          j -= 2;
          arrayOfValue2[(j - 2)].setDouble(arrayOfValue2[(j - 2)].doubleValue() - arrayOfValue2[j].doubleValue());
          break;
        case 107: 
          j -= 2;
          arrayOfValue2[(j - 2)].setDouble(arrayOfValue2[(j - 2)].doubleValue() * arrayOfValue2[j].doubleValue());
          break;
        case 111: 
          j -= 2;
          arrayOfValue2[(j - 2)].setDouble(arrayOfValue2[(j - 2)].doubleValue() / arrayOfValue2[j].doubleValue());
          break;
        case 115: 
          j -= 2;
          arrayOfValue2[(j - 2)].setDouble(arrayOfValue2[(j - 2)].doubleValue() % arrayOfValue2[j].doubleValue());
          break;
        case 116: 
          arrayOfValue2[(j - 1)].setInt(-arrayOfValue2[(j - 1)].intValue());
          break;
        case 117: 
          arrayOfValue2[(j - 2)].setLong(-arrayOfValue2[(j - 2)].longValue());
          break;
        case 118: 
          arrayOfValue2[(j - 1)].setFloat(-arrayOfValue2[(j - 1)].floatValue());
          break;
        case 119: 
          arrayOfValue2[(j - 2)].setDouble(-arrayOfValue2[(j - 2)].doubleValue());
          break;
        case 120: 
          arrayOfValue2[(j - 2)].setInt(arrayOfValue2[(j - 2)].intValue() << arrayOfValue2[(j - 1)].intValue());
          j--;
          break;
        case 122: 
          arrayOfValue2[(j - 2)].setInt(arrayOfValue2[(j - 2)].intValue() >> arrayOfValue2[(j - 1)].intValue());
          j--;
          break;
        case 124: 
          arrayOfValue2[(j - 2)].setInt(arrayOfValue2[(j - 2)].intValue() >>> arrayOfValue2[(j - 1)].intValue());
          j--;
          break;
        case 126: 
          arrayOfValue2[(j - 2)].setInt(arrayOfValue2[(j - 2)].intValue() & arrayOfValue2[(j - 1)].intValue());
          j--;
          break;
        case 128: 
          arrayOfValue2[(j - 2)].setInt(arrayOfValue2[(j - 2)].intValue() | arrayOfValue2[(j - 1)].intValue());
          j--;
          break;
        case 130: 
          arrayOfValue2[(j - 2)].setInt(arrayOfValue2[(j - 2)].intValue() ^ arrayOfValue2[(j - 1)].intValue());
          j--;
          break;
        case 121: 
          arrayOfValue2[(j - 3)].setLong(arrayOfValue2[(j - 3)].longValue() << arrayOfValue2[(j - 1)].intValue());
          j--;
          break;
        case 123: 
          arrayOfValue2[(j - 3)].setLong(arrayOfValue2[(j - 3)].longValue() >> arrayOfValue2[(j - 1)].intValue());
          j--;
          break;
        case 125: 
          arrayOfValue2[(j - 3)].setLong(arrayOfValue2[(j - 3)].longValue() >>> arrayOfValue2[(j - 1)].intValue());
          j--;
          break;
        case 127: 
          j -= 2;
          arrayOfValue2[(j - 2)].setLong(arrayOfValue2[(j - 2)].longValue() & arrayOfValue2[j].longValue());
          break;
        case 129: 
          j -= 2;
          arrayOfValue2[(j - 2)].setLong(arrayOfValue2[(j - 2)].longValue() | arrayOfValue2[j].longValue());
          break;
        case 131: 
          j -= 2;
          arrayOfValue2[(j - 2)].setLong(arrayOfValue2[(j - 2)].longValue() ^ arrayOfValue2[j].longValue());
          break;
        case 132: 
          arrayOfValue1[localInstruction2.getLocalSlot()].setInt(arrayOfValue1[localInstruction2.getLocalSlot()].intValue() + localInstruction2.getIncrement());
          break;
        case 133: 
          arrayOfValue2[(j - 1)].setLong(arrayOfValue2[(j - 1)].intValue());
          j++;
          break;
        case 134: 
          arrayOfValue2[(j - 1)].setFloat(arrayOfValue2[(j - 1)].intValue());
          break;
        case 135: 
          arrayOfValue2[(j - 1)].setDouble(arrayOfValue2[(j - 1)].intValue());
          j++;
          break;
        case 136: 
          j--;
          arrayOfValue2[(j - 1)].setInt((int)arrayOfValue2[(j - 1)].longValue());
          break;
        case 137: 
          j--;
          arrayOfValue2[(j - 1)].setFloat((float)arrayOfValue2[(j - 1)].longValue());
          break;
        case 138: 
          arrayOfValue2[(j - 2)].setDouble(arrayOfValue2[(j - 2)].longValue());
          break;
        case 139: 
          arrayOfValue2[(j - 1)].setInt((int)arrayOfValue2[(j - 1)].floatValue());
          break;
        case 140: 
          arrayOfValue2[(j - 1)].setLong(arrayOfValue2[(j - 1)].floatValue());
          j++;
          break;
        case 141: 
          arrayOfValue2[(j - 1)].setDouble(arrayOfValue2[(j - 1)].floatValue());
          j++;
          break;
        case 142: 
          j--;
          arrayOfValue2[(j - 1)].setInt((int)arrayOfValue2[(j - 1)].doubleValue());
          break;
        case 143: 
          arrayOfValue2[(j - 2)].setLong(arrayOfValue2[(j - 2)].doubleValue());
          break;
        case 144: 
          j--;
          arrayOfValue2[(j - 1)].setFloat((float)arrayOfValue2[(j - 1)].doubleValue());
          break;
        case 145: 
          arrayOfValue2[(j - 1)].setInt((byte)arrayOfValue2[(j - 1)].intValue());
          break;
        case 146: 
          arrayOfValue2[(j - 1)].setInt((char)arrayOfValue2[(j - 1)].intValue());
          break;
        case 147: 
          arrayOfValue2[(j - 1)].setInt((short)arrayOfValue2[(j - 1)].intValue());
          break;
        case 148: 
          j -= 3;
          long l1 = arrayOfValue2[(j - 1)].longValue();
          long l2 = arrayOfValue2[(j + 1)].longValue();
          arrayOfValue2[(j - 1)].setInt(l1 < l2 ? -1 : l1 == l2 ? 0 : 1);
          break;
        case 149: 
        case 150: 
          float f1 = arrayOfValue2[(j - 2)].floatValue();
          float f2 = arrayOfValue2[(--j)].floatValue();
          arrayOfValue2[(j - 1)].setInt(f1 > f2 ? 1 : k == 150 ? 1 : f1 < f2 ? -1 : f1 == f2 ? 0 : -1);
          break;
        case 151: 
        case 152: 
          j -= 3;
          double d1 = arrayOfValue2[(j - 1)].doubleValue();
          double d2 = arrayOfValue2[(j + 1)].doubleValue();
          arrayOfValue2[(j - 1)].setInt(d1 > d2 ? 1 : k == 152 ? 1 : d1 < d2 ? -1 : d1 == d2 ? 0 : -1);
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
          if (k >= 165)
          {
            Object localObject3 = arrayOfValue2[(--j)].objectValue();
            if (k >= 198)
            {
              i1 = localObject3 == null ? 0 : 1;
              k -= 198;
            }
            else
            {
              i1 = localObject3 == arrayOfValue2[(--j)].objectValue() ? 0 : 1;
              k -= 165;
            }
          }
          else
          {
            i1 = arrayOfValue2[(--j)].intValue();
            if (k >= 159)
            {
              i4 = arrayOfValue2[(--j)].intValue();
              i1 = i4 < i1 ? -1 : i4 == i1 ? 0 : 1;
              k -= 159;
            }
            else
            {
              k -= 153;
            }
          }
          int i4 = 1 << k;
          if (((i1 > 0) && ((i4 & 0x1A) != 0)) || ((i1 < 0) && ((i4 & 0x26) != 0)) || ((i1 == 0) && ((i4 & 0x29) != 0))) {
            localInstruction1 = localInstruction2.getSingleSucc();
          }
          break;
        case 168: 
        case 201: 
          arrayOfValue2[(j++)].setObject(localInstruction2);
        case 167: 
        case 200: 
          localInstruction1 = localInstruction2.getSingleSucc();
          break;
        case 169: 
          localInstruction1 = (Instruction)arrayOfValue1[localInstruction2.getLocalSlot()].objectValue();
          break;
        case 171: 
          i1 = arrayOfValue2[(--j)].intValue();
          localObject4 = localInstruction2.getValues();
          int i6 = Arrays.binarySearch((int[])localObject4, i1);
          localInstruction1 = i6 < 0 ? localInstruction2.getSuccs()[localObject4.length] : localInstruction2.getSuccs()[i6];
          break;
        case 172: 
        case 174: 
        case 176: 
          return arrayOfValue2[(--j)].objectValue();
        case 173: 
        case 175: 
          j -= 2;
          return arrayOfValue2[j].objectValue();
        case 177: 
          return Void.TYPE;
        case 178: 
          localObject1 = localInstruction2.getReference();
          localObject4 = this.env.getField(localInstruction2.getReference(), null);
          arrayOfValue2[j].setObject(localObject4);
          j += TypeSignature.getTypeSize(((Reference)localObject1).getType());
          break;
        case 180: 
          localObject1 = localInstruction2.getReference();
          localObject4 = arrayOfValue2[(--j)].objectValue();
          if (localObject4 == null) {
            throw new InvocationTargetException(new NullPointerException());
          }
          localObject6 = this.env.getField(localInstruction2.getReference(), localObject4);
          arrayOfValue2[j].setObject(localObject6);
          j += TypeSignature.getTypeSize(((Reference)localObject1).getType());
          break;
        case 179: 
          localObject1 = localInstruction2.getReference();
          j -= TypeSignature.getTypeSize(((Reference)localObject1).getType());
          localObject4 = arrayOfValue2[j].objectValue();
          this.env.putField(localInstruction2.getReference(), null, localObject4);
          break;
        case 181: 
          localObject1 = localInstruction2.getReference();
          j -= TypeSignature.getTypeSize(((Reference)localObject1).getType());
          localObject4 = arrayOfValue2[j].objectValue();
          localObject6 = arrayOfValue2[(--j)].objectValue();
          if (localObject6 == null) {
            throw new InvocationTargetException(new NullPointerException());
          }
          this.env.putField(localInstruction2.getReference(), localObject6, localObject4);
          break;
        case 182: 
        case 183: 
        case 184: 
        case 185: 
          localObject1 = localInstruction2.getReference();
          localObject4 = TypeSignature.getParameterTypes(((Reference)localObject1).getType());
          localObject6 = new Object[localObject4.length];
          for (int i8 = localObject4.length - 1; i8 >= 0; i8--)
          {
            j -= TypeSignature.getTypeSize(localObject4[i8]);
            localObject6[i8] = arrayOfValue2[j].objectValue();
          }
          Object localObject7 = null;
          if ((k == 183) && (((Reference)localObject1).getName().equals("<init>")) && (arrayOfValue2[(--j)].getNewObject() != null))
          {
            localObject8 = arrayOfValue2[j].getNewObject();
            if (!((NewObject)localObject8).getType().equals(((Reference)localObject1).getClazz())) {
              throw new InterpreterException("constructor doesn't match new");
            }
            ((NewObject)localObject8).setObject(this.env.invokeConstructor((Reference)localObject1, (Object[])localObject6));
          }
          else if (k == 184)
          {
            localObject7 = this.env.invokeMethod((Reference)localObject1, false, null, (Object[])localObject6);
          }
          else
          {
            localObject8 = arrayOfValue2[(--j)].objectValue();
            if (localObject8 == null) {
              throw new InvocationTargetException(new NullPointerException());
            }
            localObject7 = this.env.invokeMethod((Reference)localObject1, k != 183, localObject8, (Object[])localObject6);
          }
          Object localObject8 = TypeSignature.getReturnType(((Reference)localObject1).getType());
          if (!((String)localObject8).equals("V"))
          {
            arrayOfValue2[j].setObject(localObject7);
            j += TypeSignature.getTypeSize((String)localObject8);
          }
          break;
        case 187: 
          localObject1 = localInstruction2.getClazzType();
          arrayOfValue2[(j++)].setNewObject(new NewObject((String)localObject1));
          break;
        case 190: 
          localObject1 = arrayOfValue2[(--j)].objectValue();
          if (localObject1 == null) {
            throw new InvocationTargetException(new NullPointerException());
          }
          arrayOfValue2[(j++)].setInt(Array.getLength(localObject1));
          break;
        case 191: 
          localObject1 = (Throwable)arrayOfValue2[(--j)].objectValue();
          throw new InvocationTargetException(localObject1 == null ? new NullPointerException() : (Throwable)localObject1);
        case 192: 
          localObject1 = arrayOfValue2[(j - 1)].objectValue();
          if ((localObject1 != null) && (!this.env.instanceOf(localObject1, localInstruction2.getClazzType()))) {
            throw new InvocationTargetException(new ClassCastException(localObject1.getClass().getName()));
          }
          break;
        case 193: 
          localObject1 = arrayOfValue2[(--j)].objectValue();
          arrayOfValue2[(j++)].setInt(this.env.instanceOf(localObject1, localInstruction2.getClazzType()) ? 1 : 0);
          break;
        case 194: 
          this.env.enterMonitor(arrayOfValue2[(--j)].objectValue());
          break;
        case 195: 
          this.env.exitMonitor(arrayOfValue2[(--j)].objectValue());
          break;
        case 197: 
          int i2 = localInstruction2.getDimensions();
          localObject4 = new int[i2];
          for (int i7 = i2 - 1; i7 >= 0; i7--) {
            localObject4[i7] = arrayOfValue2[(--j)].intValue();
          }
          try
          {
            arrayOfValue2[(j++)].setObject(this.env.newArray(localInstruction2.getClazzType(), (int[])localObject4));
          }
          catch (NegativeArraySizeException localNegativeArraySizeException)
          {
            throw new InvocationTargetException(localNegativeArraySizeException);
          }
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
          throw new AssertError("Invalid opcode " + k);
        }
      }
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Handler[] arrayOfHandler = paramBytecodeInfo.getExceptionHandlers();
      Throwable localThrowable = localInvocationTargetException.getTargetException();
      for (int i5 = 0;; i5++)
      {
        if (i5 >= arrayOfHandler.length) {
          break label5294;
        }
        if ((arrayOfHandler[i5].start.compareTo(localInstruction1) <= 0) && (arrayOfHandler[i5].end.compareTo(localInstruction1) >= 0) && ((arrayOfHandler[i5].type == null) || (this.env.instanceOf(localThrowable, arrayOfHandler[i5].type))))
        {
          j = 0;
          arrayOfValue2[(j++)].setObject(localThrowable);
          localInstruction1 = arrayOfHandler[i5].catcher;
          break;
        }
      }
      label5294:
      throw localInvocationTargetException;
    }
  }
}


