package jode.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractSequentialList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Stack;
import jode.GlobalOptions;

public class BytecodeInfo
  extends BinaryInfo
  implements Opcodes
{
  private MethodInfo methodInfo;
  private int maxStack;
  private int maxLocals;
  private Handler[] exceptionHandlers;
  private LocalVariableInfo[] lvt;
  private LineNumber[] lnt;
  private Instruction[] instrs;
  private InstructionList instructions;
  private static final Object[] constants = { null, new Integer(-1), new Integer(0), new Integer(1), new Integer(2), new Integer(3), new Integer(4), new Integer(5), new Long(0L), new Long(1L), new Float(0.0F), new Float(1.0F), new Float(2.0F), new Double(0.0D), new Double(1.0D) };
  
  public BytecodeInfo(MethodInfo paramMethodInfo)
  {
    this.methodInfo = paramMethodInfo;
  }
  
  protected void readAttribute(String paramString, int paramInt1, ConstantPool paramConstantPool, DataInputStream paramDataInputStream, int paramInt2)
    throws IOException
  {
    int i;
    int j;
    int k;
    if (((paramInt2 & 0x10) != 0) && (paramString.equals("LocalVariableTable")))
    {
      if ((GlobalOptions.debuggingFlags & 0x40) != 0) {
        GlobalOptions.err.println("LocalVariableTable of " + this.methodInfo);
      }
      i = paramDataInputStream.readUnsignedShort();
      if (paramInt1 != 2 + i * 10)
      {
        if ((GlobalOptions.debuggingFlags & 0x40) != 0) {
          GlobalOptions.err.println("Illegal LVT length, ignoring it");
        }
        return;
      }
      this.lvt = new LocalVariableInfo[i];
      for (j = 0; j < i; j++)
      {
        this.lvt[j] = new LocalVariableInfo();
        k = paramDataInputStream.readUnsignedShort();
        int m = k + paramDataInputStream.readUnsignedShort();
        int n = paramDataInputStream.readUnsignedShort();
        int i1 = paramDataInputStream.readUnsignedShort();
        int i2 = paramDataInputStream.readUnsignedShort();
        Instruction localInstruction2 = (k >= 0) && (k < this.instrs.length) ? this.instrs[k] : null;
        Instruction localInstruction3;
        if ((m >= 0) && (m < this.instrs.length))
        {
          localInstruction3 = this.instrs[m] == null ? null : this.instrs[m].getPrevByAddr();
        }
        else
        {
          localInstruction3 = null;
          for (int i3 = this.instrs.length - 1; i3 >= 0; i3--) {
            if (this.instrs[i3] != null)
            {
              if (this.instrs[i3].getNextAddr() != m) {
                break;
              }
              localInstruction3 = this.instrs[i3];
              break;
            }
          }
        }
        if ((localInstruction2 == null) || (localInstruction3 == null) || (n == 0) || (i1 == 0) || (i2 >= this.maxLocals) || (paramConstantPool.getTag(n) != 1) || (paramConstantPool.getTag(i1) != 1))
        {
          if ((GlobalOptions.debuggingFlags & 0x40) != 0) {
            GlobalOptions.err.println("Illegal entry, ignoring LVT");
          }
          this.lvt = null;
          return;
        }
        this.lvt[j].start = localInstruction2;
        this.lvt[j].end = localInstruction3;
        this.lvt[j].name = paramConstantPool.getUTF8(n);
        this.lvt[j].type = paramConstantPool.getUTF8(i1);
        this.lvt[j].slot = i2;
        if ((GlobalOptions.debuggingFlags & 0x40) != 0) {
          GlobalOptions.err.println("\t" + this.lvt[j].name + ": " + this.lvt[j].type + " range " + k + " - " + m + " slot " + i2);
        }
      }
    }
    else if (((paramInt2 & 0x10) != 0) && (paramString.equals("LineNumberTable")))
    {
      i = paramDataInputStream.readUnsignedShort();
      if (paramInt1 != 2 + i * 4)
      {
        GlobalOptions.err.println("Illegal LineNumberTable, ignoring it");
        return;
      }
      this.lnt = new LineNumber[i];
      for (j = 0; j < i; j++)
      {
        this.lnt[j] = new LineNumber();
        k = paramDataInputStream.readUnsignedShort();
        Instruction localInstruction1 = this.instrs[k];
        if (localInstruction1 == null)
        {
          GlobalOptions.err.println("Illegal entry, ignoring LineNumberTable table");
          this.lnt = null;
          return;
        }
        this.lnt[j].start = localInstruction1;
        this.lnt[j].linenr = paramDataInputStream.readUnsignedShort();
      }
    }
    else
    {
      super.readAttribute(paramString, paramInt1, paramConstantPool, paramDataInputStream, paramInt2);
    }
  }
  
  public void read(ConstantPool paramConstantPool, DataInputStream paramDataInputStream)
    throws IOException
  {
    this.maxStack = paramDataInputStream.readUnsignedShort();
    this.maxLocals = paramDataInputStream.readUnsignedShort();
    this.instructions = new InstructionList();
    int i = paramDataInputStream.readInt();
    this.instrs = new Instruction[i];
    int[][] arrayOfInt = new int[i][];
    int j = 0;
    int i1;
    Instruction localInstruction;
    int n;
    int i7;
    while (j < i)
    {
      i1 = paramDataInputStream.readUnsignedByte();
      if (((GlobalOptions.debuggingFlags & 0x1) != 0) && ((GlobalOptions.debuggingFlags & 0x1) != 0)) {
        GlobalOptions.err.print(j + ": " + opcodeString[i1]);
      }
      int i2;
      int i4;
      Object localObject1;
      Object localObject3;
      Object localObject2;
      int i8;
      String str1;
      switch (i1)
      {
      case 196: 
        i2 = paramDataInputStream.readUnsignedByte();
        switch (i2)
        {
        case 21: 
        case 23: 
        case 25: 
        case 54: 
        case 56: 
        case 58: 
          i4 = paramDataInputStream.readUnsignedShort();
          if (i4 >= this.maxLocals) {
            throw new ClassFormatError("Invalid local slot " + i4);
          }
          localInstruction = new Instruction(i2);
          localInstruction.setLocalSlot(i4);
          n = 4;
          if ((GlobalOptions.debuggingFlags & 0x1) != 0) {
            GlobalOptions.err.print(" " + opcodeString[i2] + " " + i4);
          }
          break;
        case 22: 
        case 24: 
        case 55: 
        case 57: 
          i4 = paramDataInputStream.readUnsignedShort();
          if (i4 >= this.maxLocals - 1) {
            throw new ClassFormatError("Invalid local slot " + i4);
          }
          localInstruction = new Instruction(i2);
          localInstruction.setLocalSlot(i4);
          n = 4;
          if ((GlobalOptions.debuggingFlags & 0x1) != 0) {
            GlobalOptions.err.print(" " + opcodeString[i2] + " " + i4);
          }
          break;
        case 169: 
          i4 = paramDataInputStream.readUnsignedShort();
          if (i4 >= this.maxLocals) {
            throw new ClassFormatError("Invalid local slot " + i4);
          }
          localInstruction = new Instruction(i2);
          localInstruction.setLocalSlot(i4);
          n = 4;
          if ((GlobalOptions.debuggingFlags & 0x1) != 0) {
            GlobalOptions.err.print(" ret " + i4);
          }
          break;
        case 132: 
          i4 = paramDataInputStream.readUnsignedShort();
          if (i4 >= this.maxLocals) {
            throw new ClassFormatError("Invalid local slot " + i4);
          }
          localInstruction = new Instruction(i2);
          localInstruction.setLocalSlot(i4);
          localInstruction.setIncrement(paramDataInputStream.readShort());
          n = 6;
          if ((GlobalOptions.debuggingFlags & 0x1) != 0) {
            GlobalOptions.err.print(" iinc " + i4 + " " + localInstruction.getIncrement());
          }
          break;
        default: 
          throw new ClassFormatError("Invalid wide opcode " + i2);
        }
        break;
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
        i2 = i1 - 26 & 0x3;
        if (i2 >= this.maxLocals) {
          throw new ClassFormatError("Invalid local slot " + i2);
        }
        localInstruction = new Instruction(21 + (i1 - 26) / 4);
        localInstruction.setLocalSlot(i2);
        n = 1;
        break;
      case 59: 
      case 60: 
      case 61: 
      case 62: 
      case 67: 
      case 68: 
      case 69: 
      case 70: 
      case 75: 
      case 76: 
      case 77: 
      case 78: 
        i2 = i1 - 59 & 0x3;
        if (i2 >= this.maxLocals) {
          throw new ClassFormatError("Invalid local slot " + i2);
        }
        localInstruction = new Instruction(54 + (i1 - 59) / 4);
        localInstruction.setLocalSlot(i2);
        n = 1;
        break;
      case 63: 
      case 64: 
      case 65: 
      case 66: 
      case 71: 
      case 72: 
      case 73: 
      case 74: 
        i2 = i1 - 59 & 0x3;
        if (i2 >= this.maxLocals - 1) {
          throw new ClassFormatError("Invalid local slot " + i2);
        }
        localInstruction = new Instruction(54 + (i1 - 59) / 4);
        localInstruction.setLocalSlot(i2);
        n = 1;
        break;
      case 21: 
      case 23: 
      case 25: 
      case 54: 
      case 56: 
      case 58: 
        i2 = paramDataInputStream.readUnsignedByte();
        if (i2 >= this.maxLocals) {
          throw new ClassFormatError("Invalid local slot " + i2);
        }
        localInstruction = new Instruction(i1);
        localInstruction.setLocalSlot(i2);
        n = 2;
        if ((GlobalOptions.debuggingFlags & 0x1) != 0) {
          GlobalOptions.err.print(" " + i2);
        }
        break;
      case 22: 
      case 24: 
      case 55: 
      case 57: 
        i2 = paramDataInputStream.readUnsignedByte();
        if (i2 >= this.maxLocals - 1) {
          throw new ClassFormatError("Invalid local slot " + i2);
        }
        localInstruction = new Instruction(i1);
        localInstruction.setLocalSlot(i2);
        n = 2;
        if ((GlobalOptions.debuggingFlags & 0x1) != 0) {
          GlobalOptions.err.print(" " + i2);
        }
        break;
      case 169: 
        i2 = paramDataInputStream.readUnsignedByte();
        if (i2 >= this.maxLocals) {
          throw new ClassFormatError("Invalid local slot " + i2);
        }
        localInstruction = new Instruction(i1);
        localInstruction.setLocalSlot(i2);
        n = 2;
        if ((GlobalOptions.debuggingFlags & 0x1) != 0) {
          GlobalOptions.err.print(" " + i2);
        }
        break;
      case 1: 
      case 2: 
      case 3: 
      case 4: 
      case 5: 
      case 6: 
      case 7: 
      case 8: 
      case 11: 
      case 12: 
      case 13: 
        localInstruction = new Instruction(18);
        localInstruction.setConstant(constants[(i1 - 1)]);
        n = 1;
        break;
      case 9: 
      case 10: 
      case 14: 
      case 15: 
        localInstruction = new Instruction(20);
        localInstruction.setConstant(constants[(i1 - 1)]);
        n = 1;
        break;
      case 16: 
        localInstruction = new Instruction(18);
        localInstruction.setConstant(new Integer(paramDataInputStream.readByte()));
        n = 2;
        break;
      case 17: 
        localInstruction = new Instruction(18);
        localInstruction.setConstant(new Integer(paramDataInputStream.readShort()));
        n = 3;
        break;
      case 18: 
        i2 = paramDataInputStream.readUnsignedByte();
        localInstruction = new Instruction(i1);
        i4 = paramConstantPool.getTag(i2);
        localObject1 = paramConstantPool.getConstant(i2);
        if (7 == i4)
        {
          localObject3 = (String)localObject1;
          localInstruction.setClazzType(((String)localObject3).replace('/', '.').intern() + ".class");
        }
        else
        {
          localInstruction.setConstant(localObject1);
        }
        n = 2;
        break;
      case 19: 
        i2 = paramDataInputStream.readUnsignedShort();
        localInstruction = new Instruction(18);
        i4 = paramConstantPool.getTag(i2);
        localObject1 = paramConstantPool.getConstant(i2);
        if (7 == i4)
        {
          localObject3 = (String)localObject1;
          localInstruction.setClazzType(((String)localObject3).replace('/', '.').intern() + ".class");
        }
        else
        {
          localInstruction.setConstant(localObject1);
        }
        n = 3;
        break;
      case 20: 
        i2 = paramDataInputStream.readUnsignedShort();
        localInstruction = new Instruction(i1);
        i4 = paramConstantPool.getTag(i2);
        localObject1 = paramConstantPool.getConstant(i2);
        if (7 == i4)
        {
          localObject3 = (String)localObject1;
          localInstruction.setClazzType(((String)localObject3).replace('/', '.').intern() + ".class");
        }
        else
        {
          localInstruction.setConstant(localObject1);
        }
        n = 3;
        break;
      case 132: 
        i2 = paramDataInputStream.readUnsignedByte();
        if (i2 >= this.maxLocals) {
          throw new ClassFormatError("Invalid local slot " + i2);
        }
        localInstruction = new Instruction(i1);
        localInstruction.setLocalSlot(i2);
        localInstruction.setIncrement(paramDataInputStream.readByte());
        n = 3;
        if ((GlobalOptions.debuggingFlags & 0x1) != 0) {
          GlobalOptions.err.print(" " + i2 + " " + localInstruction.getIncrement());
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
      case 168: 
      case 198: 
      case 199: 
        localInstruction = new Instruction(i1);
        n = 3;
        arrayOfInt[j] = { j + paramDataInputStream.readShort() };
        if ((GlobalOptions.debuggingFlags & 0x1) != 0) {
          GlobalOptions.err.print(" " + arrayOfInt[j][0]);
        }
        break;
      case 200: 
      case 201: 
        localInstruction = new Instruction(i1 - 33);
        n = 5;
        arrayOfInt[j] = { j + paramDataInputStream.readInt() };
        if ((GlobalOptions.debuggingFlags & 0x1) != 0) {
          GlobalOptions.err.print(" " + arrayOfInt[j][0]);
        }
        break;
      case 170: 
        n = 3 - j % 4;
        paramDataInputStream.readFully(new byte[n]);
        i2 = paramDataInputStream.readInt();
        i4 = paramDataInputStream.readInt();
        int i6 = paramDataInputStream.readInt();
        localObject3 = new int[i6 - i4 + 1];
        int i9 = 0;
        for (int i10 = 0; i10 < localObject3.length; i10++)
        {
          localObject3[i10] = paramDataInputStream.readInt();
          if (localObject3[i10] != i2) {
            i9++;
          }
        }
        localInstruction = new Instruction(171);
        arrayOfInt[j] = new int[i9 + 1];
        int[] arrayOfInt1 = new int[i9];
        int i11 = 0;
        for (int i12 = 0; i12 < localObject3.length; i12++) {
          if (localObject3[i12] != i2)
          {
            arrayOfInt1[i11] = (i12 + i4);
            arrayOfInt[j][i11] = (j + localObject3[i12]);
            i11++;
          }
        }
        arrayOfInt[j][i9] = (j + i2);
        localInstruction.setValues(arrayOfInt1);
        n += 13 + 4 * (i6 - i4 + 1);
        break;
      case 171: 
        n = 3 - j % 4;
        paramDataInputStream.readFully(new byte[n]);
        i2 = paramDataInputStream.readInt();
        i4 = paramDataInputStream.readInt();
        localInstruction = new Instruction(i1);
        arrayOfInt[j] = new int[i4 + 1];
        localObject2 = new int[i4];
        for (i8 = 0; i8 < i4; i8++)
        {
          localObject2[i8] = paramDataInputStream.readInt();
          if ((i8 > 0) && (localObject2[(i8 - 1)] >= localObject2[i8])) {
            throw new ClassFormatException("lookupswitch not sorted");
          }
          arrayOfInt[j][i8] = (j + paramDataInputStream.readInt());
        }
        arrayOfInt[j][i4] = (j + i2);
        localInstruction.setValues((int[])localObject2);
        n += 9 + 8 * i4;
        break;
      case 178: 
      case 179: 
      case 180: 
      case 181: 
      case 182: 
      case 183: 
      case 184: 
        i2 = paramDataInputStream.readUnsignedShort();
        i4 = paramConstantPool.getTag(i2);
        if (i1 < 182)
        {
          if (i4 != 9) {
            throw new ClassFormatException("field tag mismatch: " + i4);
          }
        }
        else if (i4 != 10) {
          throw new ClassFormatException("method tag mismatch: " + i4);
        }
        localObject2 = paramConstantPool.getRef(i2);
        if ((((Reference)localObject2).getName().charAt(0) == '<') && ((!((Reference)localObject2).getName().equals("<init>")) || (i1 != 183))) {
          throw new ClassFormatException("Illegal call of special method/field " + localObject2);
        }
        localInstruction = new Instruction(i1);
        localInstruction.setReference((Reference)localObject2);
        n = 3;
        if ((GlobalOptions.debuggingFlags & 0x1) != 0) {
          GlobalOptions.err.print(" " + localObject2);
        }
        break;
      case 185: 
        i2 = paramDataInputStream.readUnsignedShort();
        i4 = paramConstantPool.getTag(i2);
        if (i4 != 11) {
          throw new ClassFormatException("interface tag mismatch: " + i4);
        }
        localObject2 = paramConstantPool.getRef(i2);
        if (((Reference)localObject2).getName().charAt(0) == '<') {
          throw new ClassFormatException("Illegal call of special method " + localObject2);
        }
        i8 = paramDataInputStream.readUnsignedByte();
        if (TypeSignature.getArgumentSize(((Reference)localObject2).getType()) != i8 - 1) {
          throw new ClassFormatException("Interface nargs mismatch: " + localObject2 + " vs. " + i8);
        }
        if (paramDataInputStream.readUnsignedByte() != 0) {
          throw new ClassFormatException("Interface reserved param not zero");
        }
        localInstruction = new Instruction(i1);
        localInstruction.setReference((Reference)localObject2);
        n = 5;
        if ((GlobalOptions.debuggingFlags & 0x1) != 0) {
          GlobalOptions.err.print(" " + localObject2);
        }
        break;
      case 187: 
      case 192: 
      case 193: 
        str1 = paramConstantPool.getClassType(paramDataInputStream.readUnsignedShort());
        if ((i1 == 187) && (str1.charAt(0) == '[')) {
          throw new ClassFormatException("Can't create array with opc_new");
        }
        localInstruction = new Instruction(i1);
        localInstruction.setClazzType(str1);
        n = 3;
        if ((GlobalOptions.debuggingFlags & 0x1) != 0) {
          GlobalOptions.err.print(" " + str1);
        }
        break;
      case 197: 
        str1 = paramConstantPool.getClassType(paramDataInputStream.readUnsignedShort());
        i4 = paramDataInputStream.readUnsignedByte();
        if (i4 == 0) {
          throw new ClassFormatException("multianewarray dimension is 0.");
        }
        for (i7 = 0; i7 < i4; i7++) {
          if (str1.charAt(i7) != '[') {
            throw new ClassFormatException("multianewarray called for non array:" + str1);
          }
        }
        localInstruction = new Instruction(i1);
        localInstruction.setClazzType(str1);
        localInstruction.setDimensions(i4);
        n = 4;
        if ((GlobalOptions.debuggingFlags & 0x1) != 0) {
          GlobalOptions.err.print(" " + str1 + " " + i4);
        }
        break;
      case 189: 
        str1 = paramConstantPool.getClassType(paramDataInputStream.readUnsignedShort());
        localInstruction = new Instruction(197);
        localInstruction.setClazzType(("[" + str1).intern());
        localInstruction.setDimensions(1);
        n = 3;
        if ((GlobalOptions.debuggingFlags & 0x1) != 0) {
          GlobalOptions.err.print(" " + str1);
        }
        break;
      case 188: 
        int i3 = "ZCFDBSIJ".charAt(paramDataInputStream.readUnsignedByte() - 4);
        String str2 = new String(new char[] { '[', i3 });
        if ((GlobalOptions.debuggingFlags & 0x1) != 0) {
          GlobalOptions.err.print(" " + str2);
        }
        localInstruction = new Instruction(197);
        localInstruction.setClazzType(str2);
        localInstruction.setDimensions(1);
        n = 2;
        break;
      case 0: 
      case 46: 
      case 47: 
      case 48: 
      case 49: 
      case 50: 
      case 51: 
      case 52: 
      case 53: 
      case 79: 
      case 80: 
      case 81: 
      case 82: 
      case 83: 
      case 84: 
      case 85: 
      case 86: 
      case 87: 
      case 88: 
      case 89: 
      case 90: 
      case 91: 
      case 92: 
      case 93: 
      case 94: 
      case 95: 
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
      case 126: 
      case 127: 
      case 128: 
      case 129: 
      case 130: 
      case 131: 
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
      case 145: 
      case 146: 
      case 147: 
      case 148: 
      case 149: 
      case 150: 
      case 151: 
      case 152: 
      case 172: 
      case 173: 
      case 174: 
      case 175: 
      case 176: 
      case 177: 
      case 190: 
      case 191: 
      case 194: 
      case 195: 
        localInstruction = new Instruction(i1);
        n = 1;
        break;
      case 186: 
      default: 
        throw new ClassFormatError("Invalid opcode " + i1);
      }
      if ((GlobalOptions.debuggingFlags & 0x1) != 0) {
        GlobalOptions.err.println();
      }
      this.instrs[j] = localInstruction;
      this.instructions.add(localInstruction);
      j += n;
      this.instructions.setLastAddr(j);
    }
    if (j != i) {
      throw new ClassFormatError("last instruction too long");
    }
    Iterator localIterator = this.instructions.iterator();
    while (localIterator.hasNext())
    {
      localInstruction = (Instruction)localIterator.next();
      n = localInstruction.getAddr();
      if (arrayOfInt[n] != null)
      {
        i1 = arrayOfInt[n].length;
        Instruction[] arrayOfInstruction = new Instruction[i1];
        for (int i5 = 0; i5 < i1; i5++)
        {
          i7 = arrayOfInt[n][i5];
          if ((i7 < 0) || (i7 > i) || (this.instrs[i7] == null)) {
            throw new ClassFormatException("Illegal jump target at " + this + "@" + n);
          }
          arrayOfInstruction[i5] = this.instrs[i7];
        }
        localInstruction.setSuccs(arrayOfInstruction);
      }
    }
    arrayOfInt = (int[][])null;
    int k = paramDataInputStream.readUnsignedShort();
    this.exceptionHandlers = new Handler[k];
    for (int m = 0; m < k; m++)
    {
      this.exceptionHandlers[m] = new Handler();
      this.exceptionHandlers[m].start = this.instrs[paramDataInputStream.readUnsignedShort()];
      this.exceptionHandlers[m].end = this.instrs[paramDataInputStream.readUnsignedShort()].getPrevByAddr();
      this.exceptionHandlers[m].catcher = this.instrs[paramDataInputStream.readUnsignedShort()];
      n = paramDataInputStream.readUnsignedShort();
      this.exceptionHandlers[m].type = (n == 0 ? null : paramConstantPool.getClassName(n));
      if (this.exceptionHandlers[m].catcher.getOpcode() == 191)
      {
        k--;
        m--;
      }
      else if ((this.exceptionHandlers[m].start.getAddr() <= this.exceptionHandlers[m].catcher.getAddr()) && (this.exceptionHandlers[m].end.getAddr() >= this.exceptionHandlers[m].catcher.getAddr()))
      {
        if (this.exceptionHandlers[m].start == this.exceptionHandlers[m].catcher)
        {
          k--;
          m--;
        }
        else
        {
          this.exceptionHandlers[m].end = this.exceptionHandlers[m].catcher.getPrevByAddr();
        }
      }
    }
    if (k < this.exceptionHandlers.length)
    {
      Handler[] arrayOfHandler = new Handler[k];
      System.arraycopy(this.exceptionHandlers, 0, arrayOfHandler, 0, k);
      this.exceptionHandlers = arrayOfHandler;
    }
    readAttributes(paramConstantPool, paramDataInputStream, 255);
    this.instrs = null;
  }
  
  public void dumpCode(PrintWriter paramPrintWriter)
  {
    Iterator localIterator = this.instructions.iterator();
    while (localIterator.hasNext())
    {
      Instruction localInstruction = (Instruction)localIterator.next();
      paramPrintWriter.println(localInstruction.getDescription() + " " + Integer.toHexString(hashCode()));
      Instruction[] arrayOfInstruction = localInstruction.getSuccs();
      int j;
      if (arrayOfInstruction != null)
      {
        paramPrintWriter.print("\tsuccs: " + arrayOfInstruction[0]);
        for (j = 1; j < arrayOfInstruction.length; j++) {
          paramPrintWriter.print(", " + arrayOfInstruction[j]);
        }
        paramPrintWriter.println();
      }
      if (localInstruction.getPreds() != null)
      {
        paramPrintWriter.print("\tpreds: " + localInstruction.getPreds()[0]);
        for (j = 1; j < localInstruction.getPreds().length; j++) {
          paramPrintWriter.print(", " + localInstruction.getPreds()[j]);
        }
        paramPrintWriter.println();
      }
    }
    for (int i = 0; i < this.exceptionHandlers.length; i++) {
      paramPrintWriter.println("catch " + this.exceptionHandlers[i].type + " from " + this.exceptionHandlers[i].start + " to " + this.exceptionHandlers[i].end + " catcher " + this.exceptionHandlers[i].catcher);
    }
  }
  
  public void reserveSmallConstants(GrowableConstantPool paramGrowableConstantPool)
  {
    Iterator localIterator = this.instructions.iterator();
    while (localIterator.hasNext())
    {
      Instruction localInstruction = (Instruction)localIterator.next();
      if (localInstruction.getOpcode() == 18)
      {
        Object localObject = localInstruction.getConstant();
        if (localObject != null)
        {
          for (int i = 1;; i++)
          {
            if (i >= constants.length) {
              break label85;
            }
            if (localObject.equals(constants[i])) {
              break;
            }
          }
          label85:
          if ((localObject instanceof Integer))
          {
            i = ((Integer)localObject).intValue();
            if ((i >= 32768) && (i <= 32767)) {}
          }
          else
          {
            paramGrowableConstantPool.reserveConstant(localObject);
          }
        }
      }
    }
  }
  
  private void calculateMaxStack()
  {
    this.maxStack = 0;
    int[] arrayOfInt1 = new int[this.instructions.getCodeLength()];
    int[] arrayOfInt2 = new int[2];
    Stack localStack = new Stack();
    for (int i = 0; i < arrayOfInt1.length; i++) {
      arrayOfInt1[i] = -1;
    }
    arrayOfInt1[0] = 0;
    localStack.push(this.instructions.get(0));
    while (!localStack.isEmpty())
    {
      Instruction localInstruction1 = (Instruction)localStack.pop();
      Instruction localInstruction2 = localInstruction1.getNextByAddr();
      Instruction[] arrayOfInstruction = localInstruction1.getSuccs();
      int j = localInstruction1.getAddr();
      localInstruction1.getStackPopPush(arrayOfInt2);
      int k = arrayOfInt1[j] - arrayOfInt2[0] + arrayOfInt2[1];
      if (this.maxStack < k) {
        this.maxStack = k;
      }
      if (localInstruction1.getOpcode() == 168)
      {
        if (arrayOfInt1[localInstruction2.getAddr()] == -1)
        {
          arrayOfInt1[localInstruction2.getAddr()] = (k - 1);
          localStack.push(localInstruction2);
        }
        if (arrayOfInt1[arrayOfInstruction[0].getAddr()] == -1)
        {
          arrayOfInt1[arrayOfInstruction[0].getAddr()] = k;
          localStack.push(arrayOfInstruction[0]);
        }
      }
      else
      {
        if (arrayOfInstruction != null) {
          for (m = 0; m < arrayOfInstruction.length; m++) {
            if (arrayOfInt1[arrayOfInstruction[m].getAddr()] == -1)
            {
              arrayOfInt1[arrayOfInstruction[m].getAddr()] = k;
              localStack.push(arrayOfInstruction[m]);
            }
          }
        }
        if ((!localInstruction1.doesAlwaysJump()) && (arrayOfInt1[localInstruction2.getAddr()] == -1))
        {
          arrayOfInt1[localInstruction2.getAddr()] = k;
          localStack.push(localInstruction2);
        }
      }
      for (int m = 0; m < this.exceptionHandlers.length; m++) {
        if ((this.exceptionHandlers[m].start.compareTo(localInstruction1) <= 0) && (this.exceptionHandlers[m].end.compareTo(localInstruction1) >= 0))
        {
          int n = this.exceptionHandlers[m].catcher.getAddr();
          if (arrayOfInt1[n] == -1)
          {
            arrayOfInt1[n] = 1;
            localStack.push(this.exceptionHandlers[m].catcher);
          }
        }
      }
    }
  }
  
  public void prepareWriting(GrowableConstantPool paramGrowableConstantPool)
  {
    int i = 0;
    this.maxLocals = ((this.methodInfo.isStatic() ? 0 : 1) + TypeSignature.getArgumentSize(this.methodInfo.getType()));
    Iterator localIterator = this.instructions.iterator();
    while (localIterator.hasNext())
    {
      Instruction localInstruction = (Instruction)localIterator.next();
      int k = localInstruction.getOpcode();
      localInstruction.setAddr(i);
      int m;
      int i2;
      int n;
      switch (k)
      {
      case 18: 
      case 20: 
        Object localObject = localInstruction.getConstant();
        if (localObject == null)
        {
          m = 1;
        }
        else
        {
          for (i2 = 1; i2 < constants.length; i2++) {
            if (localObject.equals(constants[i2]))
            {
              m = 1;
              break label1581;
            }
          }
          if (k == 20)
          {
            paramGrowableConstantPool.putLongConstant(localObject);
            m = 3;
          }
          else
          {
            if ((localObject instanceof Integer))
            {
              i2 = ((Integer)localObject).intValue();
              if ((i2 >= -128) && (i2 <= 127))
              {
                m = 2;
                break label1581;
              }
              if ((i2 >= 32768) && (i2 <= 32767))
              {
                m = 3;
                break label1581;
              }
            }
            if (paramGrowableConstantPool.putConstant(localObject) < 256) {
              m = 2;
            } else {
              m = 3;
            }
          }
        }
        break;
      case 132: 
        n = localInstruction.getLocalSlot();
        i2 = localInstruction.getIncrement();
        if ((n < 256) && (i2 >= -128) && (i2 <= 127)) {
          m = 3;
        } else {
          m = 6;
        }
        if (n < this.maxLocals) {
          break label1581;
        }
        this.maxLocals = (n + 1);
        break;
      case 21: 
      case 23: 
      case 25: 
      case 54: 
      case 56: 
      case 58: 
        n = localInstruction.getLocalSlot();
        if (n < 4) {
          m = 1;
        } else if (n < 256) {
          m = 2;
        } else {
          m = 4;
        }
        if (n < this.maxLocals) {
          break label1581;
        }
        this.maxLocals = (n + 1);
        break;
      case 22: 
      case 24: 
      case 55: 
      case 57: 
        n = localInstruction.getLocalSlot();
        if (n < 4) {
          m = 1;
        } else if (n < 256) {
          m = 2;
        } else {
          m = 4;
        }
        if (n + 1 < this.maxLocals) {
          break label1581;
        }
        this.maxLocals = (n + 2);
        break;
      case 169: 
        n = localInstruction.getLocalSlot();
        if (n < 256) {
          m = 2;
        } else {
          m = 4;
        }
        if (n < this.maxLocals) {
          break label1581;
        }
        this.maxLocals = (n + 1);
        break;
      case 171: 
        m = 3 - i % 4;
        int[] arrayOfInt = localInstruction.getValues();
        i2 = arrayOfInt.length;
        if (i2 > 0)
        {
          int i3 = arrayOfInt[(i2 - 1)] - arrayOfInt[0] + 1;
          if (4 + i3 * 4 < 8 * i2)
          {
            m += 13 + 4 * i3;
            break label1581;
          }
        }
        m += 9 + 8 * i2;
        break;
      case 167: 
      case 168: 
        int i1 = localInstruction.getSingleSucc().getAddr() - localInstruction.getAddr();
        if ((i1 < 32768) || (i1 > 32767)) {
          m = 5;
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
      case 198: 
      case 199: 
        m = 3;
        break;
      case 197: 
        if (localInstruction.getDimensions() == 1)
        {
          String str = localInstruction.getClazzType().substring(1);
          if ("ZCFDBSIJ".indexOf(str.charAt(0)) != -1)
          {
            m = 2;
          }
          else
          {
            paramGrowableConstantPool.putClassType(str);
            m = 3;
          }
        }
        else
        {
          paramGrowableConstantPool.putClassType(localInstruction.getClazzType());
          m = 4;
        }
        break;
      case 178: 
      case 179: 
      case 180: 
      case 181: 
        paramGrowableConstantPool.putRef(9, localInstruction.getReference());
        m = 3;
        break;
      case 182: 
      case 183: 
      case 184: 
        paramGrowableConstantPool.putRef(10, localInstruction.getReference());
        m = 3;
        break;
      case 185: 
        paramGrowableConstantPool.putRef(11, localInstruction.getReference());
        m = 5;
        break;
      case 187: 
      case 192: 
      case 193: 
        paramGrowableConstantPool.putClassType(localInstruction.getClazzType());
        m = 3;
        break;
      case 0: 
      case 46: 
      case 47: 
      case 48: 
      case 49: 
      case 50: 
      case 51: 
      case 52: 
      case 53: 
      case 79: 
      case 80: 
      case 81: 
      case 82: 
      case 83: 
      case 84: 
      case 85: 
      case 86: 
      case 87: 
      case 88: 
      case 89: 
      case 90: 
      case 91: 
      case 92: 
      case 93: 
      case 94: 
      case 95: 
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
      case 126: 
      case 127: 
      case 128: 
      case 129: 
      case 130: 
      case 131: 
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
      case 145: 
      case 146: 
      case 147: 
      case 148: 
      case 149: 
      case 150: 
      case 151: 
      case 152: 
      case 172: 
      case 173: 
      case 174: 
      case 175: 
      case 176: 
      case 177: 
      case 190: 
      case 191: 
      case 194: 
      case 195: 
        m = 1;
        break;
      }
      throw new ClassFormatError("Invalid opcode " + k);
      label1581:
      i += m;
    }
    this.instructions.setLastAddr(i);
    try
    {
      calculateMaxStack();
    }
    catch (RuntimeException localRuntimeException)
    {
      localRuntimeException.printStackTrace();
      dumpCode(GlobalOptions.err);
    }
    for (int j = 0; j < this.exceptionHandlers.length; j++) {
      if (this.exceptionHandlers[j].type != null) {
        paramGrowableConstantPool.putClassName(this.exceptionHandlers[j].type);
      }
    }
    if (this.lvt != null)
    {
      paramGrowableConstantPool.putUTF8("LocalVariableTable");
      for (j = 0; j < this.lvt.length; j++)
      {
        paramGrowableConstantPool.putUTF8(this.lvt[j].name);
        paramGrowableConstantPool.putUTF8(this.lvt[j].type);
      }
    }
    if (this.lnt != null) {
      paramGrowableConstantPool.putUTF8("LineNumberTable");
    }
    prepareAttributes(paramGrowableConstantPool);
  }
  
  protected int getKnownAttributeCount()
  {
    int i = 0;
    if (this.lvt != null) {
      i++;
    }
    if (this.lnt != null) {
      i++;
    }
    return i;
  }
  
  public void writeKnownAttributes(GrowableConstantPool paramGrowableConstantPool, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    int i;
    int j;
    int k;
    if (this.lvt != null)
    {
      paramDataOutputStream.writeShort(paramGrowableConstantPool.putUTF8("LocalVariableTable"));
      i = this.lvt.length;
      j = 2 + 10 * i;
      paramDataOutputStream.writeInt(j);
      paramDataOutputStream.writeShort(i);
      for (k = 0; k < i; k++)
      {
        paramDataOutputStream.writeShort(this.lvt[k].start.getAddr());
        paramDataOutputStream.writeShort(this.lvt[k].end.getAddr() + this.lvt[k].end.getLength() - this.lvt[k].start.getAddr());
        paramDataOutputStream.writeShort(paramGrowableConstantPool.putUTF8(this.lvt[k].name));
        paramDataOutputStream.writeShort(paramGrowableConstantPool.putUTF8(this.lvt[k].type));
        paramDataOutputStream.writeShort(this.lvt[k].slot);
      }
    }
    if (this.lnt != null)
    {
      paramDataOutputStream.writeShort(paramGrowableConstantPool.putUTF8("LineNumberTable"));
      i = this.lnt.length;
      j = 2 + 4 * i;
      paramDataOutputStream.writeInt(j);
      paramDataOutputStream.writeShort(i);
      for (k = 0; k < i; k++)
      {
        paramDataOutputStream.writeShort(this.lnt[k].start.getAddr());
        paramDataOutputStream.writeShort(this.lnt[k].linenr);
      }
    }
  }
  
  public void write(GrowableConstantPool paramGrowableConstantPool, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeShort(this.maxStack);
    paramDataOutputStream.writeShort(this.maxLocals);
    paramDataOutputStream.writeInt(this.instructions.getCodeLength());
    Iterator localIterator = this.instructions.iterator();
    label1929:
    while (localIterator.hasNext())
    {
      Instruction localInstruction = (Instruction)localIterator.next();
      int j = localInstruction.getOpcode();
      int k;
      int n;
      int m;
      Object localObject2;
      switch (j)
      {
      case 21: 
      case 22: 
      case 23: 
      case 24: 
      case 25: 
      case 54: 
      case 55: 
      case 56: 
      case 57: 
      case 58: 
        k = localInstruction.getLocalSlot();
        if (k < 4)
        {
          if (j < 54) {
            paramDataOutputStream.writeByte(26 + 4 * (j - 21) + k);
          } else {
            paramDataOutputStream.writeByte(59 + 4 * (j - 54) + k);
          }
        }
        else if (k < 256)
        {
          paramDataOutputStream.writeByte(j);
          paramDataOutputStream.writeByte(k);
        }
        else
        {
          paramDataOutputStream.writeByte(196);
          paramDataOutputStream.writeByte(j);
          paramDataOutputStream.writeShort(k);
        }
        break;
      case 169: 
        k = localInstruction.getLocalSlot();
        if (k < 256)
        {
          paramDataOutputStream.writeByte(j);
          paramDataOutputStream.writeByte(k);
        }
        else
        {
          paramDataOutputStream.writeByte(196);
          paramDataOutputStream.writeByte(j);
          paramDataOutputStream.writeShort(k);
        }
        break;
      case 18: 
      case 20: 
        Object localObject1 = localInstruction.getConstant();
        if (localObject1 == null)
        {
          paramDataOutputStream.writeByte(1);
        }
        else
        {
          for (n = 1; n < constants.length; n++) {
            if (localObject1.equals(constants[n]))
            {
              paramDataOutputStream.writeByte(1 + n);
              break label1929;
            }
          }
          if (j == 20)
          {
            paramDataOutputStream.writeByte(j);
            paramDataOutputStream.writeShort(paramGrowableConstantPool.putLongConstant(localObject1));
          }
          else
          {
            if ((localObject1 instanceof Integer))
            {
              n = ((Integer)localObject1).intValue();
              if ((n >= -128) && (n <= 127))
              {
                paramDataOutputStream.writeByte(16);
                paramDataOutputStream.writeByte(((Integer)localObject1).intValue());
                break label1929;
              }
              if ((n >= 32768) && (n <= 32767))
              {
                paramDataOutputStream.writeByte(17);
                paramDataOutputStream.writeShort(((Integer)localObject1).intValue());
                break label1929;
              }
            }
            if (localInstruction.getLength() == 2)
            {
              paramDataOutputStream.writeByte(18);
              paramDataOutputStream.writeByte(paramGrowableConstantPool.putConstant(localObject1));
            }
            else
            {
              paramDataOutputStream.writeByte(19);
              paramDataOutputStream.writeShort(paramGrowableConstantPool.putConstant(localObject1));
            }
          }
        }
        break;
      case 132: 
        m = localInstruction.getLocalSlot();
        n = localInstruction.getIncrement();
        if (localInstruction.getLength() == 3)
        {
          paramDataOutputStream.writeByte(j);
          paramDataOutputStream.writeByte(m);
          paramDataOutputStream.writeByte(n);
        }
        else
        {
          paramDataOutputStream.writeByte(196);
          paramDataOutputStream.writeByte(j);
          paramDataOutputStream.writeShort(m);
          paramDataOutputStream.writeShort(n);
        }
        break;
      case 167: 
      case 168: 
        if (localInstruction.getLength() == 5)
        {
          paramDataOutputStream.writeByte(j + 33);
          paramDataOutputStream.writeInt(localInstruction.getSingleSucc().getAddr() - localInstruction.getAddr());
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
      case 198: 
      case 199: 
        paramDataOutputStream.writeByte(j);
        paramDataOutputStream.writeShort(localInstruction.getSingleSucc().getAddr() - localInstruction.getAddr());
        break;
      case 171: 
        m = 3 - localInstruction.getAddr() % 4;
        int[] arrayOfInt = localInstruction.getValues();
        int i2 = arrayOfInt.length;
        int i3 = localInstruction.getSuccs()[i2].getAddr() - localInstruction.getAddr();
        if (i2 > 0)
        {
          i4 = arrayOfInt[(i2 - 1)] - arrayOfInt[0] + 1;
          if (4 + i4 * 4 < 8 * i2)
          {
            paramDataOutputStream.writeByte(170);
            paramDataOutputStream.write(new byte[m]);
            paramDataOutputStream.writeInt(i3);
            paramDataOutputStream.writeInt(arrayOfInt[0]);
            paramDataOutputStream.writeInt(arrayOfInt[(i2 - 1)]);
            int i5 = arrayOfInt[0];
            for (int i6 = 0; i6 < i2; i6++)
            {
              while (i5++ < arrayOfInt[i6]) {
                paramDataOutputStream.writeInt(i3);
              }
              paramDataOutputStream.writeInt(localInstruction.getSuccs()[i6].getAddr() - localInstruction.getAddr());
            }
            break label1929;
          }
        }
        paramDataOutputStream.writeByte(171);
        paramDataOutputStream.write(new byte[m]);
        paramDataOutputStream.writeInt(i3);
        paramDataOutputStream.writeInt(i2);
        for (int i4 = 0; i4 < i2; i4++)
        {
          paramDataOutputStream.writeInt(arrayOfInt[i4]);
          paramDataOutputStream.writeInt(localInstruction.getSuccs()[i4].getAddr() - localInstruction.getAddr());
        }
        break;
      case 178: 
      case 179: 
      case 180: 
      case 181: 
        paramDataOutputStream.writeByte(j);
        paramDataOutputStream.writeShort(paramGrowableConstantPool.putRef(9, localInstruction.getReference()));
        break;
      case 182: 
      case 183: 
      case 184: 
      case 185: 
        localObject2 = localInstruction.getReference();
        paramDataOutputStream.writeByte(j);
        if (j == 185)
        {
          paramDataOutputStream.writeShort(paramGrowableConstantPool.putRef(11, (Reference)localObject2));
          paramDataOutputStream.writeByte(TypeSignature.getArgumentSize(((Reference)localObject2).getType()) + 1);
          paramDataOutputStream.writeByte(0);
        }
        else
        {
          paramDataOutputStream.writeShort(paramGrowableConstantPool.putRef(10, (Reference)localObject2));
        }
        break;
      case 187: 
      case 192: 
      case 193: 
        paramDataOutputStream.writeByte(j);
        paramDataOutputStream.writeShort(paramGrowableConstantPool.putClassType(localInstruction.getClazzType()));
        break;
      case 197: 
        if (localInstruction.getDimensions() == 1)
        {
          localObject2 = localInstruction.getClazzType().substring(1);
          int i1 = "ZCFDBSIJ".indexOf(((String)localObject2).charAt(0));
          if (i1 != -1)
          {
            paramDataOutputStream.writeByte(188);
            paramDataOutputStream.writeByte(i1 + 4);
          }
          else
          {
            paramDataOutputStream.writeByte(189);
            paramDataOutputStream.writeShort(paramGrowableConstantPool.putClassType((String)localObject2));
          }
        }
        else
        {
          paramDataOutputStream.writeByte(j);
          paramDataOutputStream.writeShort(paramGrowableConstantPool.putClassType(localInstruction.getClazzType()));
          paramDataOutputStream.writeByte(localInstruction.getDimensions());
        }
        break;
      case 0: 
      case 46: 
      case 47: 
      case 48: 
      case 49: 
      case 50: 
      case 51: 
      case 52: 
      case 53: 
      case 79: 
      case 80: 
      case 81: 
      case 82: 
      case 83: 
      case 84: 
      case 85: 
      case 86: 
      case 87: 
      case 88: 
      case 89: 
      case 90: 
      case 91: 
      case 92: 
      case 93: 
      case 94: 
      case 95: 
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
      case 126: 
      case 127: 
      case 128: 
      case 129: 
      case 130: 
      case 131: 
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
      case 145: 
      case 146: 
      case 147: 
      case 148: 
      case 149: 
      case 150: 
      case 151: 
      case 152: 
      case 172: 
      case 173: 
      case 174: 
      case 175: 
      case 176: 
      case 177: 
      case 190: 
      case 191: 
      case 194: 
      case 195: 
        paramDataOutputStream.writeByte(j);
        break;
      }
      throw new ClassFormatError("Invalid opcode " + j);
    }
    paramDataOutputStream.writeShort(this.exceptionHandlers.length);
    for (int i = 0; i < this.exceptionHandlers.length; i++)
    {
      paramDataOutputStream.writeShort(this.exceptionHandlers[i].start.getAddr());
      paramDataOutputStream.writeShort(this.exceptionHandlers[i].end.getNextByAddr().getAddr());
      paramDataOutputStream.writeShort(this.exceptionHandlers[i].catcher.getAddr());
      paramDataOutputStream.writeShort(this.exceptionHandlers[i].type == null ? 0 : paramGrowableConstantPool.putClassName(this.exceptionHandlers[i].type));
    }
    writeAttributes(paramGrowableConstantPool, paramDataOutputStream);
  }
  
  public void dropInfo(int paramInt)
  {
    if ((paramInt & 0x10) != 0)
    {
      this.lvt = null;
      this.lnt = null;
    }
    super.dropInfo(paramInt);
  }
  
  public int getSize()
  {
    int i = 0;
    if (this.lvt != null) {
      i += 8 + this.lvt.length * 10;
    }
    if (this.lnt != null) {
      i += 8 + this.lnt.length * 4;
    }
    return 10 + this.instructions.getCodeLength() + this.exceptionHandlers.length * 8 + getAttributeSize() + i;
  }
  
  public int getMaxStack()
  {
    return this.maxStack;
  }
  
  public int getMaxLocals()
  {
    return this.maxLocals;
  }
  
  public MethodInfo getMethodInfo()
  {
    return this.methodInfo;
  }
  
  public List getInstructions()
  {
    return this.instructions;
  }
  
  public Handler[] getExceptionHandlers()
  {
    return this.exceptionHandlers;
  }
  
  public LocalVariableInfo[] getLocalVariableTable()
  {
    return this.lvt;
  }
  
  public LineNumber[] getLineNumberTable()
  {
    return this.lnt;
  }
  
  public void setExceptionHandlers(Handler[] paramArrayOfHandler)
  {
    this.exceptionHandlers = paramArrayOfHandler;
  }
  
  public void setLocalVariableTable(LocalVariableInfo[] paramArrayOfLocalVariableInfo)
  {
    this.lvt = paramArrayOfLocalVariableInfo;
  }
  
  public void setLineNumberTable(LineNumber[] paramArrayOfLineNumber)
  {
    this.lnt = paramArrayOfLineNumber;
  }
  
  public String toString()
  {
    return "Bytecode " + this.methodInfo;
  }
  
  private class InstructionList
    extends AbstractSequentialList
  {
    Instruction borderInstr = new Instruction(254);
    int instructionCount = 0;
    
    InstructionList()
    {
      this.borderInstr.nextByAddr = (this.borderInstr.prevByAddr = this.borderInstr);
    }
    
    public int size()
    {
      return this.instructionCount;
    }
    
    Instruction get0(int paramInt)
    {
      Instruction localInstruction = this.borderInstr;
      int i;
      if (paramInt < this.instructionCount / 2) {
        for (i = 0; i <= paramInt; i++) {
          localInstruction = localInstruction.nextByAddr;
        }
      } else {
        for (i = this.instructionCount; i > paramInt; i--) {
          localInstruction = localInstruction.prevByAddr;
        }
      }
      return localInstruction;
    }
    
    public Object get(int paramInt)
    {
      if ((paramInt < 0) || (paramInt >= this.instructionCount)) {
        throw new IllegalArgumentException();
      }
      return get0(paramInt);
    }
    
    public boolean add(Object paramObject)
    {
      this.instructionCount += 1;
      this.borderInstr.prevByAddr.appendInstruction((Instruction)paramObject, BytecodeInfo.this);
      return true;
    }
    
    public ListIterator listIterator(final int paramInt)
    {
      if ((paramInt < 0) || (paramInt > this.instructionCount)) {
        throw new IllegalArgumentException();
      }
      new ListIterator()
      {
        Instruction instr = BytecodeInfo.InstructionList.this.get0(paramInt);
        Instruction toRemove = null;
        int index = paramInt;
        
        public boolean hasNext()
        {
          return this.index < BytecodeInfo.InstructionList.this.instructionCount;
        }
        
        public boolean hasPrevious()
        {
          return this.index > 0;
        }
        
        public Object next()
        {
          if (this.index >= BytecodeInfo.InstructionList.this.instructionCount) {
            throw new NoSuchElementException();
          }
          this.index += 1;
          this.toRemove = this.instr;
          this.instr = this.instr.nextByAddr;
          return this.toRemove;
        }
        
        public Object previous()
        {
          if (this.index == 0) {
            throw new NoSuchElementException();
          }
          this.index -= 1;
          this.instr = this.instr.prevByAddr;
          this.toRemove = this.instr;
          return this.toRemove;
        }
        
        public int nextIndex()
        {
          return this.index;
        }
        
        public int previousIndex()
        {
          return this.index - 1;
        }
        
        public void remove()
        {
          if (this.toRemove == null) {
            throw new IllegalStateException();
          }
          BytecodeInfo.InstructionList.this.instructionCount -= 1;
          if (this.instr == this.toRemove) {
            this.instr = this.instr.nextByAddr;
          } else {
            this.index -= 1;
          }
          this.toRemove.removeInstruction(BytecodeInfo.this);
          this.toRemove = null;
        }
        
        public void add(Object paramAnonymousObject)
        {
          BytecodeInfo.InstructionList.this.instructionCount += 1;
          this.index += 1;
          this.instr.prevByAddr.appendInstruction((Instruction)paramAnonymousObject, BytecodeInfo.this);
          this.toRemove = null;
        }
        
        public void set(Object paramAnonymousObject)
        {
          if (this.toRemove == null) {
            throw new IllegalStateException();
          }
          this.toRemove.replaceInstruction((Instruction)paramAnonymousObject, BytecodeInfo.this);
          if (this.instr == this.toRemove) {
            this.instr = ((Instruction)paramAnonymousObject);
          }
          this.toRemove = ((Instruction)paramAnonymousObject);
        }
      };
    }
    
    void setLastAddr(int paramInt)
    {
      this.borderInstr.setAddr(paramInt);
    }
    
    int getCodeLength()
    {
      return this.borderInstr.getAddr();
    }
  }
}


