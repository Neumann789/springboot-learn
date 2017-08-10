package jode.jvm;

import java.io.PrintWriter;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import jode.AssertError;
import jode.GlobalOptions;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.ClassInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.MethodInfo;
import jode.bytecode.Opcodes;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;

public class CodeVerifier
  implements Opcodes
{
  ClassInfo ci;
  MethodInfo mi;
  BytecodeInfo bi;
  String methodType;
  String returnType;
  static Type tNull = Type.tType("0");
  static Type tInt = Type.tType("I");
  static Type tLong = Type.tType("J");
  static Type tFloat = Type.tType("F");
  static Type tDouble = Type.tType("D");
  static Type tString = Type.tType("Ljava/lang/String;");
  static Type tNone = Type.tType("?");
  static Type tSecondPart = new Type("2");
  static Type tObject = new Type("Ljava/lang/Object;");
  String[] types = { "I", "J", "F", "D", "+", "B", "C", "S" };
  String[] arrayTypes = { "[I", "[J", "[F", "[D", "[Ljava/lang/Object;", "[B", "[C", "[S" };
  
  public CodeVerifier(ClassInfo paramClassInfo, MethodInfo paramMethodInfo, BytecodeInfo paramBytecodeInfo)
  {
    this.ci = paramClassInfo;
    this.mi = paramMethodInfo;
    this.bi = paramBytecodeInfo;
    this.methodType = paramMethodInfo.getType();
    this.returnType = TypeSignature.getReturnType(this.methodType);
  }
  
  public VerifyInfo initInfo()
  {
    VerifyInfo localVerifyInfo = new VerifyInfo();
    String str1 = 1;
    int i = 0;
    String str2;
    if (!this.mi.isStatic())
    {
      str2 = this.ci.getName().replace('.', '/');
      if (this.mi.getName().equals("<init>")) {
        localVerifyInfo.locals[(i++)] = Type.tType("N" + str2 + ";", null);
      } else {
        localVerifyInfo.locals[(i++)] = Type.tType("L" + str2 + ";");
      }
    }
    while (this.methodType.charAt(str1) != ')')
    {
      str2 = str1;
      str1 = TypeSignature.skipType(this.methodType, str1);
      String str3 = this.methodType.substring(str2, str1);
      localVerifyInfo.locals[(i++)] = Type.tType(str3);
      if (TypeSignature.getTypeSize(str3) == 2) {
        localVerifyInfo.locals[(i++)] = tSecondPart;
      }
    }
    while (i < this.bi.getMaxLocals()) {
      localVerifyInfo.locals[(i++)] = tNone;
    }
    return localVerifyInfo;
  }
  
  public boolean mergeInfo(Instruction paramInstruction, VerifyInfo paramVerifyInfo)
    throws VerifyException
  {
    if (paramInstruction.getTmpInfo() == null)
    {
      paramInstruction.setTmpInfo(paramVerifyInfo);
      return true;
    }
    boolean bool = false;
    VerifyInfo localVerifyInfo = (VerifyInfo)paramInstruction.getTmpInfo();
    if (localVerifyInfo.stackHeight != paramVerifyInfo.stackHeight) {
      throw new VerifyException("Stack height differ at: " + paramInstruction.getDescription());
    }
    Type localType;
    for (int i = 0; i < localVerifyInfo.stackHeight; i++)
    {
      localType = localVerifyInfo.stack[i].mergeType(paramVerifyInfo.stack[i]);
      if (!localType.equals(localVerifyInfo.stack[i]))
      {
        if (localType == tNone) {
          throw new VerifyException("Type error while merging: " + localVerifyInfo.stack[i] + " and " + paramVerifyInfo.stack[i]);
        }
        bool = true;
        localVerifyInfo.stack[i] = localType;
      }
    }
    for (i = 0; i < this.bi.getMaxLocals(); i++)
    {
      localType = localVerifyInfo.locals[i].mergeType(paramVerifyInfo.locals[i]);
      if (!localType.equals(localVerifyInfo.locals[i]))
      {
        bool = true;
        localVerifyInfo.locals[i] = localType;
      }
    }
    if (localVerifyInfo.jsrTargets != null)
    {
      if (paramVerifyInfo.jsrTargets == null)
      {
        i = 0;
      }
      else
      {
        i = paramVerifyInfo.jsrTargets.length;
        int j = 0;
        for (int k = 0; k < localVerifyInfo.jsrTargets.length; k++) {
          for (int m = j; m < i; m++) {
            if (localVerifyInfo.jsrTargets[k] == paramVerifyInfo.jsrTargets[m])
            {
              System.arraycopy(paramVerifyInfo.jsrTargets, m, paramVerifyInfo.jsrTargets, j, i - m);
              i -= m - j;
              j++;
              break;
            }
          }
        }
        i = j;
      }
      if (i != localVerifyInfo.jsrTargets.length)
      {
        if (i == 0)
        {
          localVerifyInfo.jsrTargets = null;
        }
        else
        {
          localVerifyInfo.jsrTargets = new Instruction[i];
          System.arraycopy(paramVerifyInfo.jsrTargets, 0, localVerifyInfo.jsrTargets, 0, i);
        }
        bool = true;
      }
    }
    return bool;
  }
  
  public VerifyInfo modelEffect(Instruction paramInstruction, VerifyInfo paramVerifyInfo)
    throws VerifyException
  {
    int i = paramVerifyInfo.jsrTargets != null ? paramVerifyInfo.jsrTargets.length : 0;
    VerifyInfo localVerifyInfo = (VerifyInfo)paramVerifyInfo.clone();
    int j = paramInstruction.getOpcode();
    Object localObject3;
    Type localType;
    Object localObject4;
    int k;
    int i1;
    int i3;
    int i4;
    Object localObject1;
    Object localObject2;
    String str1;
    Object localObject5;
    switch (j)
    {
    case 0: 
    case 167: 
      break;
    case 18: 
      localObject3 = paramInstruction.getConstant();
      if (localObject3 == null) {
        localType = tNull;
      } else if ((localObject3 instanceof Integer)) {
        localType = tInt;
      } else if ((localObject3 instanceof Float)) {
        localType = tFloat;
      } else {
        localType = tString;
      }
      localVerifyInfo.push(localType);
      break;
    case 20: 
      localObject3 = paramInstruction.getConstant();
      if ((localObject3 instanceof Long)) {
        localType = tLong;
      } else {
        localType = tDouble;
      }
      localVerifyInfo.push(localType);
      localVerifyInfo.push(tSecondPart);
      break;
    case 21: 
    case 22: 
    case 23: 
    case 24: 
    case 25: 
      if ((i > 0) && ((!localVerifyInfo.jsrLocals[(i - 1)].get(paramInstruction.getLocalSlot())) || (((j & 0x1) == 0) && (!localVerifyInfo.jsrLocals[(i - 1)].get(paramInstruction.getLocalSlot() + 1)))))
      {
        localVerifyInfo.jsrLocals = ((BitSet[])localVerifyInfo.jsrLocals.clone());
        localVerifyInfo.jsrLocals[(i - 1)] = ((BitSet)localVerifyInfo.jsrLocals[(i - 1)].clone());
        localVerifyInfo.jsrLocals[(i - 1)].set(paramInstruction.getLocalSlot());
        if ((j & 0x1) == 0) {
          localVerifyInfo.jsrLocals[(i - 1)].set(paramInstruction.getLocalSlot() + 1);
        }
      }
      if (((j & 0x1) == 0) && (localVerifyInfo.locals[(paramInstruction.getLocalSlot() + 1)] != tSecondPart)) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localType = localVerifyInfo.locals[paramInstruction.getLocalSlot()];
      if (!localType.isOfType(this.types[(j - 21)])) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localVerifyInfo.push(localType);
      if ((j & 0x1) == 0) {
        localVerifyInfo.push(tSecondPart);
      }
      break;
    case 46: 
    case 47: 
    case 48: 
    case 49: 
    case 50: 
    case 51: 
    case 52: 
    case 53: 
      if (!localVerifyInfo.pop().isOfType("I")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localType = localVerifyInfo.pop();
      if ((!localType.isOfType(this.arrayTypes[(j - 46)])) && ((j != 51) || (!localType.isOfType("[Z")))) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localObject3 = localType.getTypeSig();
      localObject4 = j == 50 ? tNull : ((String)localObject3).charAt(0) == '[' ? Type.tType(((String)localObject3).substring(1)) : Type.tType(this.types[(j - 46)]);
      localVerifyInfo.push((Type)localObject4);
      if ((1 << j - 46 & 0xA) != 0) {
        localVerifyInfo.push(tSecondPart);
      }
      break;
    case 54: 
    case 55: 
    case 56: 
    case 57: 
    case 58: 
      if ((i > 0) && ((!localVerifyInfo.jsrLocals[(i - 1)].get(paramInstruction.getLocalSlot())) || (((j & 0x1) != 0) && (!localVerifyInfo.jsrLocals[(i - 1)].get(paramInstruction.getLocalSlot() + 1)))))
      {
        localVerifyInfo.jsrLocals = ((BitSet[])localVerifyInfo.jsrLocals.clone());
        localVerifyInfo.jsrLocals[(i - 1)] = ((BitSet)localVerifyInfo.jsrLocals[(i - 1)].clone());
        localVerifyInfo.jsrLocals[(i - 1)].set(paramInstruction.getLocalSlot());
        if ((j & 0x1) != 0) {
          localVerifyInfo.jsrLocals[(i - 1)].set(paramInstruction.getLocalSlot() + 1);
        }
      }
      if (((j & 0x1) != 0) && (localVerifyInfo.pop() != tSecondPart)) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localType = localVerifyInfo.pop();
      if ((!localType.isOfType(this.types[(j - 54)])) && ((j != 58) || (!localType.isOfType("R")))) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localVerifyInfo.locals[paramInstruction.getLocalSlot()] = localType;
      if ((j & 0x1) != 0) {
        localVerifyInfo.locals[(paramInstruction.getLocalSlot() + 1)] = tSecondPart;
      }
      break;
    case 79: 
    case 80: 
    case 81: 
    case 82: 
    case 83: 
    case 84: 
    case 85: 
    case 86: 
      if (((1 << j - 79 & 0xA) != 0) && (localVerifyInfo.pop() != tSecondPart)) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localType = localVerifyInfo.pop();
      if (!localVerifyInfo.pop().isOfType("I")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localObject3 = localVerifyInfo.pop();
      if ((!((Type)localObject3).isOfType(this.arrayTypes[(j - 79)])) && ((j != 84) || (!((Type)localObject3).isOfType("[Z")))) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localObject4 = j >= 84 ? "I" : this.types[(j - 79)];
      if (!localType.isOfType((String)localObject4)) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      break;
    case 87: 
    case 88: 
      k = j - 86;
      localVerifyInfo.need(k);
      localVerifyInfo.stackHeight -= k;
      break;
    case 89: 
    case 90: 
    case 91: 
      k = j - 89;
      localVerifyInfo.reserve(1);
      localVerifyInfo.need(k + 1);
      if (localVerifyInfo.stack[(localVerifyInfo.stackHeight - 1)] == tSecondPart) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      i1 = localVerifyInfo.stackHeight - (k + 1);
      if (localVerifyInfo.stack[i1] == tSecondPart) {
        throw new VerifyException(paramInstruction.getDescription() + " on long or double");
      }
      for (i3 = localVerifyInfo.stackHeight; i3 > i1; i3--) {
        localVerifyInfo.stack[i3] = localVerifyInfo.stack[(i3 - 1)];
      }
      localVerifyInfo.stack[i1] = localVerifyInfo.stack[(localVerifyInfo.stackHeight++)];
      break;
    case 92: 
    case 93: 
    case 94: 
      k = j - 92;
      localVerifyInfo.reserve(2);
      localVerifyInfo.need(k + 2);
      if (localVerifyInfo.stack[(localVerifyInfo.stackHeight - 2)] == tSecondPart) {
        throw new VerifyException(paramInstruction.getDescription() + " on misaligned long or double");
      }
      i1 = localVerifyInfo.stackHeight;
      i3 = i1 - (k + 2);
      if (localVerifyInfo.stack[i3] == tSecondPart) {
        throw new VerifyException(paramInstruction.getDescription() + " on long or double");
      }
      for (i4 = i1; i4 > i3; i4--) {
        localVerifyInfo.stack[(i4 + 1)] = localVerifyInfo.stack[(i4 - 1)];
      }
      localVerifyInfo.stack[(i3 + 1)] = localVerifyInfo.stack[(i1 + 1)];
      localVerifyInfo.stack[i3] = localVerifyInfo.stack[i1];
      localVerifyInfo.stackHeight += 2;
      break;
    case 95: 
      localVerifyInfo.need(2);
      if ((localVerifyInfo.stack[(localVerifyInfo.stackHeight - 2)] == tSecondPart) || (localVerifyInfo.stack[(localVerifyInfo.stackHeight - 1)] == tSecondPart)) {
        throw new VerifyException(paramInstruction.getDescription() + " on misaligned long or double");
      }
      localObject1 = localVerifyInfo.stack[(localVerifyInfo.stackHeight - 1)];
      localVerifyInfo.stack[(localVerifyInfo.stackHeight - 1)] = localVerifyInfo.stack[(localVerifyInfo.stackHeight - 2)];
      localVerifyInfo.stack[(localVerifyInfo.stackHeight - 2)] = localObject1;
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
      localObject1 = this.types[(j - 96 & 0x3)];
      if (((j & 0x1) != 0) && (localVerifyInfo.pop() != tSecondPart)) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      if (!localVerifyInfo.pop().isOfType((String)localObject1)) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      if ((j & 0x1) != 0)
      {
        localVerifyInfo.need(2);
        if ((localVerifyInfo.stack[(localVerifyInfo.stackHeight - 1)] != tSecondPart) || (!localVerifyInfo.stack[(localVerifyInfo.stackHeight - 2)].isOfType((String)localObject1))) {
          throw new VerifyException(paramInstruction.getDescription());
        }
      }
      else
      {
        localVerifyInfo.need(1);
        if (!localVerifyInfo.stack[(localVerifyInfo.stackHeight - 1)].isOfType((String)localObject1)) {
          throw new VerifyException(paramInstruction.getDescription());
        }
      }
      break;
    case 116: 
    case 117: 
    case 118: 
    case 119: 
      localObject1 = this.types[(j - 116 & 0x3)];
      if ((j & 0x1) != 0)
      {
        localVerifyInfo.need(2);
        if ((localVerifyInfo.stack[(localVerifyInfo.stackHeight - 1)] != tSecondPart) || (!localVerifyInfo.stack[(localVerifyInfo.stackHeight - 2)].isOfType((String)localObject1))) {
          throw new VerifyException(paramInstruction.getDescription());
        }
      }
      else
      {
        localVerifyInfo.need(1);
        if (!localVerifyInfo.stack[(localVerifyInfo.stackHeight - 1)].isOfType((String)localObject1)) {
          throw new VerifyException(paramInstruction.getDescription());
        }
      }
      break;
    case 120: 
    case 121: 
    case 122: 
    case 123: 
    case 124: 
    case 125: 
      if (!localVerifyInfo.pop().isOfType("I")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      if ((j & 0x1) != 0)
      {
        localVerifyInfo.need(2);
        if ((localVerifyInfo.stack[(localVerifyInfo.stackHeight - 1)] != tSecondPart) || (!localVerifyInfo.stack[(localVerifyInfo.stackHeight - 2)].isOfType("J"))) {
          throw new VerifyException(paramInstruction.getDescription());
        }
      }
      else
      {
        localVerifyInfo.need(1);
        if (!localVerifyInfo.stack[(localVerifyInfo.stackHeight - 1)].isOfType("I")) {
          throw new VerifyException(paramInstruction.getDescription());
        }
      }
      break;
    case 126: 
    case 127: 
    case 128: 
    case 129: 
    case 130: 
    case 131: 
      if (((j & 0x1) != 0) && (localVerifyInfo.pop() != tSecondPart)) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      if (!localVerifyInfo.pop().isOfType(this.types[(j & 0x1)])) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      if ((j & 0x1) != 0)
      {
        localVerifyInfo.need(2);
        if ((localVerifyInfo.stack[(localVerifyInfo.stackHeight - 1)] != tSecondPart) || (!localVerifyInfo.stack[(localVerifyInfo.stackHeight - 2)].isOfType("J"))) {
          throw new VerifyException(paramInstruction.getDescription());
        }
      }
      else
      {
        localVerifyInfo.need(1);
        if (!localVerifyInfo.stack[(localVerifyInfo.stackHeight - 1)].isOfType("I")) {
          throw new VerifyException(paramInstruction.getDescription());
        }
      }
      break;
    case 132: 
      if (!localVerifyInfo.locals[paramInstruction.getLocalSlot()].isOfType("I")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
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
      int m = (j - 133) / 3;
      i1 = (j - 133) % 3;
      if (i1 >= m) {
        i1++;
      }
      if (((m & 0x1) != 0) && (localVerifyInfo.pop() != tSecondPart)) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      if (!localVerifyInfo.pop().isOfType(this.types[m])) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localVerifyInfo.push(Type.tType(this.types[i1]));
      if ((i1 & 0x1) != 0) {
        localVerifyInfo.push(tSecondPart);
      }
      break;
    case 145: 
    case 146: 
    case 147: 
      localVerifyInfo.need(1);
      if (!localVerifyInfo.stack[(localVerifyInfo.stackHeight - 1)].isOfType("I")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      break;
    case 148: 
      if (localVerifyInfo.pop() != tSecondPart) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      if (!localVerifyInfo.pop().isOfType("J")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      if (localVerifyInfo.pop() != tSecondPart) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      if (!localVerifyInfo.pop().isOfType("J")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localVerifyInfo.push(tInt);
      break;
    case 151: 
    case 152: 
      if (localVerifyInfo.pop() != tSecondPart) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      if (!localVerifyInfo.pop().isOfType("D")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      if (localVerifyInfo.pop() != tSecondPart) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      if (!localVerifyInfo.pop().isOfType("D")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localVerifyInfo.push(tInt);
      break;
    case 149: 
    case 150: 
      if (!localVerifyInfo.pop().isOfType("F")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      if (!localVerifyInfo.pop().isOfType("F")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localVerifyInfo.push(tInt);
      break;
    case 153: 
    case 154: 
    case 155: 
    case 156: 
    case 157: 
    case 158: 
    case 170: 
    case 171: 
      if (!localVerifyInfo.pop().isOfType("I")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      break;
    case 159: 
    case 160: 
    case 161: 
    case 162: 
    case 163: 
    case 164: 
      if (!localVerifyInfo.pop().isOfType("I")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      if (!localVerifyInfo.pop().isOfType("I")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      break;
    case 165: 
    case 166: 
      if (!localVerifyInfo.pop().isOfType("+")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      if (!localVerifyInfo.pop().isOfType("+")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      break;
    case 198: 
    case 199: 
      if (!localVerifyInfo.pop().isOfType("+")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      break;
    case 172: 
    case 173: 
    case 174: 
    case 175: 
    case 176: 
      if (((1 << j - 172 & 0xA) != 0) && (localVerifyInfo.pop() != tSecondPart)) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localObject2 = localVerifyInfo.pop();
      if ((!((Type)localObject2).isOfType(this.types[(j - 172)])) || (!((Type)localObject2).isOfType(TypeSignature.getReturnType(this.methodType)))) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      break;
    case 168: 
      localObject2 = paramInstruction.getSingleSucc();
      localVerifyInfo.stack[(localVerifyInfo.stackHeight++)] = Type.tType("R", (Instruction)localObject2);
      localVerifyInfo.jsrTargets = new Instruction[i + 1];
      localVerifyInfo.jsrLocals = new BitSet[i + 1];
      if (i > 0)
      {
        for (i1 = 0; i1 < paramVerifyInfo.jsrTargets.length; i1++) {
          if (paramVerifyInfo.jsrTargets[i1] == paramInstruction.getSingleSucc()) {
            throw new VerifyException(paramInstruction.getDescription() + " is recursive");
          }
        }
        System.arraycopy(paramVerifyInfo.jsrTargets, 0, localVerifyInfo.jsrTargets, 0, i);
        System.arraycopy(paramVerifyInfo.jsrLocals, 0, localVerifyInfo.jsrLocals, 0, i);
      }
      localVerifyInfo.jsrTargets[i] = paramInstruction.getSingleSucc();
      localVerifyInfo.jsrLocals[i] = new BitSet();
      break;
    case 177: 
      if (!this.returnType.equals("V")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      break;
    case 178: 
      localObject2 = paramInstruction.getReference();
      str1 = ((Reference)localObject2).getType();
      localVerifyInfo.push(Type.tType(str1));
      if (TypeSignature.getTypeSize(str1) == 2) {
        localVerifyInfo.push(tSecondPart);
      }
      break;
    case 180: 
      localObject2 = paramInstruction.getReference();
      str1 = ((Reference)localObject2).getClazz();
      if (!localVerifyInfo.pop().isOfType(str1)) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localObject5 = ((Reference)localObject2).getType();
      localVerifyInfo.push(Type.tType((String)localObject5));
      if (TypeSignature.getTypeSize((String)localObject5) == 2) {
        localVerifyInfo.push(tSecondPart);
      }
      break;
    case 179: 
      localObject2 = paramInstruction.getReference();
      str1 = ((Reference)localObject2).getType();
      if ((TypeSignature.getTypeSize(str1) == 2) && (localVerifyInfo.pop() != tSecondPart)) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      if (!localVerifyInfo.pop().isOfType(str1)) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      break;
    case 181: 
      localObject2 = paramInstruction.getReference();
      str1 = ((Reference)localObject2).getType();
      if ((TypeSignature.getTypeSize(str1) == 2) && (localVerifyInfo.pop() != tSecondPart)) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      if (!localVerifyInfo.pop().isOfType(str1)) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localObject5 = ((Reference)localObject2).getClazz();
      if (!localVerifyInfo.pop().isOfType((String)localObject5)) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      break;
    case 182: 
    case 183: 
    case 184: 
    case 185: 
      localObject2 = paramInstruction.getReference();
      str1 = ((Reference)localObject2).getType();
      localObject5 = TypeSignature.getParameterTypes(str1);
      for (i4 = localObject5.length - 1; i4 >= 0; i4--)
      {
        if ((TypeSignature.getTypeSize(localObject5[i4]) == 2) && (localVerifyInfo.pop() != tSecondPart)) {
          throw new VerifyException(paramInstruction.getDescription());
        }
        if (!localVerifyInfo.pop().isOfType(localObject5[i4])) {
          throw new VerifyException(paramInstruction.getDescription());
        }
      }
      if (((Reference)localObject2).getName().equals("<init>"))
      {
        localObject6 = localVerifyInfo.pop();
        String str3 = ((Type)localObject6).getTypeSig();
        String str4 = ((Reference)localObject2).getClazz();
        if ((j != 183) || (str3.charAt(0) != 'N') || (str4.charAt(0) != 'L')) {
          throw new VerifyException(paramInstruction.getDescription());
        }
        if (!str3.substring(1).equals(str4.substring(1)))
        {
          localObject7 = ClassInfo.forName(str3.substring(1, str3.length() - 1).replace('/', '.'));
          if ((((ClassInfo)localObject7).getSuperclass() != TypeSignature.getClassInfo(str4)) || (((Type)localObject6).getInstruction() != null)) {
            throw new VerifyException(paramInstruction.getDescription());
          }
        }
        Object localObject7 = Type.tType("L" + str3.substring(1));
        for (int i5 = 0; i5 < localVerifyInfo.stackHeight; i5++) {
          if (localVerifyInfo.stack[i5] == localObject6) {
            localVerifyInfo.stack[i5] = localObject7;
          }
        }
        for (i5 = 0; i5 < localVerifyInfo.locals.length; i5++) {
          if (localVerifyInfo.locals[i5] == localObject6) {
            localVerifyInfo.locals[i5] = localObject7;
          }
        }
      }
      else if (j != 184)
      {
        localObject6 = ((Reference)localObject2).getClazz();
        if (!localVerifyInfo.pop().isOfType((String)localObject6)) {
          throw new VerifyException(paramInstruction.getDescription());
        }
      }
      Object localObject6 = TypeSignature.getReturnType(str1);
      if (!((String)localObject6).equals("V"))
      {
        localVerifyInfo.push(Type.tType((String)localObject6));
        if (TypeSignature.getTypeSize((String)localObject6) == 2) {
          localVerifyInfo.push(tSecondPart);
        }
      }
      break;
    case 187: 
      localObject2 = paramInstruction.getClazzType();
      localVerifyInfo.stack[(localVerifyInfo.stackHeight++)] = Type.tType("N" + ((String)localObject2).substring(1), paramInstruction);
      break;
    case 190: 
      if (!localVerifyInfo.pop().isOfType("[*")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localVerifyInfo.push(tInt);
      break;
    case 191: 
      if (!localVerifyInfo.pop().isOfType("Ljava/lang/Throwable;")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      break;
    case 192: 
      localObject2 = paramInstruction.getClazzType();
      if (!localVerifyInfo.pop().isOfType("+")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localVerifyInfo.push(Type.tType((String)localObject2));
      break;
    case 193: 
      if (!localVerifyInfo.pop().isOfType("Ljava/lang/Object;")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      localVerifyInfo.push(tInt);
      break;
    case 194: 
    case 195: 
      if (!localVerifyInfo.pop().isOfType("Ljava/lang/Object;")) {
        throw new VerifyException(paramInstruction.getDescription());
      }
      break;
    case 197: 
      int n = paramInstruction.getDimensions();
      for (int i2 = n - 1; i2 >= 0; i2--) {
        if (!localVerifyInfo.pop().isOfType("I")) {
          throw new VerifyException(paramInstruction.getDescription());
        }
      }
      String str2 = paramInstruction.getClazzType();
      localVerifyInfo.push(Type.tType(str2));
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
    case 169: 
    case 186: 
    case 188: 
    case 189: 
    case 196: 
    default: 
      throw new AssertError("Invalid opcode " + j);
    }
    return localVerifyInfo;
  }
  
  public void doVerify()
    throws VerifyException
  {
    HashSet localHashSet = new HashSet();
    Instruction localInstruction1 = (Instruction)this.bi.getInstructions().get(0);
    localInstruction1.setTmpInfo(initInfo());
    localHashSet.add(localInstruction1);
    Handler[] arrayOfHandler = this.bi.getExceptionHandlers();
    Instruction localInstruction2;
    VerifyInfo localVerifyInfo1;
    while (!localHashSet.isEmpty())
    {
      localIterator = localHashSet.iterator();
      localInstruction2 = (Instruction)localIterator.next();
      localIterator.remove();
      if ((!localInstruction2.doesAlwaysJump()) && (localInstruction2.getNextByAddr() == null)) {
        throw new VerifyException("Flow can fall off end of method");
      }
      localVerifyInfo1 = (VerifyInfo)localInstruction2.getTmpInfo();
      int i = localInstruction2.getOpcode();
      Object localObject1;
      Object localObject2;
      VerifyInfo localVerifyInfo3;
      Object localObject3;
      if (i == 169)
      {
        localObject1 = localVerifyInfo1.locals[localInstruction2.getLocalSlot()];
        if ((localVerifyInfo1.jsrTargets == null) || (!((Type)localObject1).isOfType("R"))) {
          throw new VerifyException(localInstruction2.getDescription());
        }
        int j = localVerifyInfo1.jsrTargets.length - 1;
        localObject2 = ((Type)localObject1).getInstruction();
        while (localObject2 != localVerifyInfo1.jsrTargets[j])
        {
          j--;
          if (j < 0) {
            throw new VerifyException(localInstruction2.getDescription());
          }
        }
        localVerifyInfo3 = (VerifyInfo)((Instruction)localObject2).getTmpInfo();
        if (localVerifyInfo3.retInstr == null) {
          localVerifyInfo3.retInstr = localInstruction2;
        } else if (localVerifyInfo3.retInstr != localInstruction2) {
          throw new VerifyException("JsrTarget has more than one ret: " + ((Instruction)localObject2).getDescription());
        }
        BitSet[] arrayOfBitSet;
        if (j > 0)
        {
          localObject3 = new Instruction[j];
          arrayOfBitSet = new BitSet[j];
          System.arraycopy(localVerifyInfo1.jsrTargets, 0, localObject3, 0, j);
          System.arraycopy(localVerifyInfo1.jsrLocals, 0, arrayOfBitSet, 0, j);
        }
        else
        {
          localObject3 = null;
          arrayOfBitSet = null;
        }
        for (int n = 0; n < ((Instruction)localObject2).getPreds().length; n++)
        {
          Instruction localInstruction3 = localObject2.getPreds()[n];
          if (localInstruction3.getTmpInfo() != null) {
            localHashSet.add(localInstruction3);
          }
        }
      }
      else
      {
        localObject1 = modelEffect(localInstruction2, localVerifyInfo1);
        if ((!localInstruction2.doesAlwaysJump()) && (mergeInfo(localInstruction2.getNextByAddr(), (VerifyInfo)localObject1))) {
          localHashSet.add(localInstruction2.getNextByAddr());
        }
        if (i == 168)
        {
          VerifyInfo localVerifyInfo2 = (VerifyInfo)localInstruction2.getSingleSucc().getTmpInfo();
          if ((localVerifyInfo2 != null) && (localVerifyInfo2.retInstr != null))
          {
            localObject2 = (VerifyInfo)localVerifyInfo1.clone();
            localVerifyInfo3 = (VerifyInfo)localVerifyInfo2.retInstr.getTmpInfo();
            localObject3 = localVerifyInfo3.jsrLocals[(localVerifyInfo3.jsrLocals.length - 1)];
            for (int m = 0; m < this.bi.getMaxLocals(); m++) {
              if (((BitSet)localObject3).get(m)) {
                ((VerifyInfo)localObject2).locals[m] = localVerifyInfo3.locals[m];
              }
            }
            if (mergeInfo(localInstruction2.getNextByAddr(), (VerifyInfo)localObject2)) {
              localHashSet.add(localInstruction2.getNextByAddr());
            }
          }
        }
        if (localInstruction2.getSuccs() != null) {
          for (k = 0; k < localInstruction2.getSuccs().length; k++) {
            if (mergeInfo(localInstruction2.getSuccs()[k], (VerifyInfo)((VerifyInfo)localObject1).clone())) {
              localHashSet.add(localInstruction2.getSuccs()[k]);
            }
          }
        }
        for (int k = 0; k < arrayOfHandler.length; k++) {
          if ((arrayOfHandler[k].start.compareTo(localInstruction2) <= 0) && (arrayOfHandler[k].end.compareTo(localInstruction2) >= 0))
          {
            localObject2 = (VerifyInfo)localVerifyInfo1.clone();
            ((VerifyInfo)localObject2).stackHeight = 1;
            if (arrayOfHandler[k].type != null) {
              ((VerifyInfo)localObject2).stack[0] = Type.tType("L" + arrayOfHandler[k].type.replace('.', '/') + ";");
            } else {
              ((VerifyInfo)localObject2).stack[0] = Type.tType("Ljava/lang/Throwable;");
            }
            if (mergeInfo(arrayOfHandler[k].catcher, (VerifyInfo)localObject2)) {
              localHashSet.add(arrayOfHandler[k].catcher);
            }
          }
        }
      }
    }
    if ((GlobalOptions.debuggingFlags & 0x2) != 0)
    {
      localIterator = this.bi.getInstructions().iterator();
      while (localIterator.hasNext())
      {
        localInstruction2 = (Instruction)localIterator.next();
        localVerifyInfo1 = (VerifyInfo)localInstruction2.getTmpInfo();
        if (localVerifyInfo1 != null) {
          GlobalOptions.err.println(localVerifyInfo1.toString());
        }
        GlobalOptions.err.println(localInstruction2.getDescription());
      }
    }
    Iterator localIterator = this.bi.getInstructions().iterator();
    while (localIterator.hasNext())
    {
      localInstruction2 = (Instruction)localIterator.next();
      localInstruction2.setTmpInfo(null);
    }
  }
  
  public void verify()
    throws VerifyException
  {
    try
    {
      doVerify();
    }
    catch (VerifyException localVerifyException)
    {
      Iterator localIterator = this.bi.getInstructions().iterator();
      while (localIterator.hasNext())
      {
        Instruction localInstruction = (Instruction)localIterator.next();
        VerifyInfo localVerifyInfo = (VerifyInfo)localInstruction.getTmpInfo();
        if (localVerifyInfo != null) {
          GlobalOptions.err.println(localVerifyInfo.toString());
        }
        GlobalOptions.err.println(localInstruction.getDescription());
        localInstruction.setTmpInfo(null);
      }
      throw localVerifyException;
    }
  }
  
  class VerifyInfo
    implements Cloneable
  {
    CodeVerifier.Type[] stack = new CodeVerifier.Type[CodeVerifier.this.bi.getMaxStack()];
    CodeVerifier.Type[] locals = new CodeVerifier.Type[CodeVerifier.this.bi.getMaxLocals()];
    Instruction[] jsrTargets = null;
    BitSet[] jsrLocals = null;
    int stackHeight = 0;
    int maxHeight = 0;
    Instruction retInstr = null;
    
    VerifyInfo() {}
    
    public Object clone()
    {
      try
      {
        VerifyInfo localVerifyInfo = (VerifyInfo)super.clone();
        localVerifyInfo.stack = ((CodeVerifier.Type[])this.stack.clone());
        localVerifyInfo.locals = ((CodeVerifier.Type[])this.locals.clone());
        return localVerifyInfo;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new AssertError("Clone not supported?");
      }
    }
    
    public final void reserve(int paramInt)
      throws VerifyException
    {
      if (this.stackHeight + paramInt > this.maxHeight)
      {
        this.maxHeight = (this.stackHeight + paramInt);
        if (this.maxHeight > this.stack.length) {
          throw new VerifyException("stack overflow");
        }
      }
    }
    
    public final void need(int paramInt)
      throws VerifyException
    {
      if (this.stackHeight < paramInt) {
        throw new VerifyException("stack underflow");
      }
    }
    
    public final void push(CodeVerifier.Type paramType)
      throws VerifyException
    {
      reserve(1);
      this.stack[(this.stackHeight++)] = paramType;
    }
    
    public final CodeVerifier.Type pop()
      throws VerifyException
    {
      need(1);
      return this.stack[(--this.stackHeight)];
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer("locals:[");
      String str = "";
      for (int i = 0; i < this.locals.length; i++)
      {
        localStringBuffer.append(str).append(i).append(':');
        localStringBuffer.append(this.locals[i]);
        str = ",";
      }
      localStringBuffer.append("], stack:[");
      str = "";
      for (i = 0; i < this.stackHeight; i++)
      {
        localStringBuffer.append(str).append(this.stack[i]);
        str = ",";
      }
      if (this.jsrTargets != null)
      {
        localStringBuffer.append("], jsrs:[");
        str = "";
        for (i = 0; i < this.jsrTargets.length; i++)
        {
          localStringBuffer.append(str).append(this.jsrTargets[i]).append(this.jsrLocals[i]);
          str = ",";
        }
      }
      return "]";
    }
  }
  
  private static class Type
  {
    private String typeSig;
    private Instruction instr;
    
    public Type(String paramString)
    {
      this.typeSig = paramString;
    }
    
    public Type(String paramString, Instruction paramInstruction)
    {
      this.typeSig = paramString;
      this.instr = paramInstruction;
    }
    
    public static Type tType(String paramString)
    {
      return new Type(paramString);
    }
    
    public static Type tType(String paramString, Instruction paramInstruction)
    {
      return new Type(paramString, paramInstruction);
    }
    
    public String getTypeSig()
    {
      return this.typeSig;
    }
    
    public Instruction getInstruction()
    {
      return this.instr;
    }
    
    public boolean isOfType(String paramString)
    {
      String str = this.typeSig;
      if ((GlobalOptions.debuggingFlags & 0x2) != 0) {
        GlobalOptions.err.println("isOfType(" + str + "," + paramString + ")");
      }
      if (str.equals(paramString)) {
        return true;
      }
      int i = str.charAt(0);
      int j = paramString.charAt(0);
      switch (j)
      {
      case 66: 
      case 67: 
      case 73: 
      case 83: 
      case 90: 
        return "ZBCSI".indexOf(i) >= 0;
      case 43: 
        return "L[nNR0".indexOf(i) >= 0;
      case 91: 
        if (i == 48) {
          return true;
        }
        while ((i == 91) && (j == 91))
        {
          str = str.substring(1);
          paramString = paramString.substring(1);
          i = str.charAt(0);
          j = paramString.charAt(0);
        }
        if (j == 42) {
          return true;
        }
        if (j != 76) {
          return false;
        }
      case 76: 
        if (i == 48) {
          return true;
        }
        if ("L[".indexOf(i) < 0) {
          return false;
        }
        ClassInfo localClassInfo = TypeSignature.getClassInfo(paramString);
        if ((localClassInfo.isInterface()) || (localClassInfo == ClassInfo.javaLangObject)) {
          return true;
        }
        if (i == 76) {
          return localClassInfo.superClassOf(TypeSignature.getClassInfo(str));
        }
        break;
      }
      return false;
    }
    
    public Type mergeType(Type paramType)
    {
      String str1 = this.typeSig;
      String str2 = paramType.typeSig;
      if (equals(paramType)) {
        return this;
      }
      int i = str1.charAt(0);
      int j = str2.charAt(0);
      if (i == 42) {
        return paramType;
      }
      if (j == 42) {
        return this;
      }
      if (("ZBCSI".indexOf(i) >= 0) && ("ZBCSI".indexOf(j) >= 0)) {
        return this;
      }
      if (i == 48) {
        return "L[0".indexOf(j) >= 0 ? paramType : CodeVerifier.tNone;
      }
      if (j == 48) {
        return "L[".indexOf(i) >= 0 ? this : CodeVerifier.tNone;
      }
      for (int k = 0; (i == 91) && (j == 91); k++)
      {
        str1 = str1.substring(1);
        str2 = str2.substring(1);
        i = str1.charAt(0);
        j = str2.charAt(0);
      }
      Object localObject;
      if (((i == 91) && (j == 76)) || ((i == 76) && (j == 91)))
      {
        if (k == 0) {
          return CodeVerifier.tObject;
        }
        localObject = new StringBuffer(k + 18);
        for (int m = 0; m < k; m++) {
          ((StringBuffer)localObject).append("[");
        }
        ((StringBuffer)localObject).append("Ljava/lang/Object;");
        return tType(((StringBuffer)localObject).toString());
      }
      if ((i == 76) && (j == 76))
      {
        localObject = TypeSignature.getClassInfo(str1);
        ClassInfo localClassInfo = TypeSignature.getClassInfo(str2);
        if (((ClassInfo)localObject).superClassOf(localClassInfo)) {
          return this;
        }
        if (localClassInfo.superClassOf((ClassInfo)localObject)) {
          return paramType;
        }
        do
        {
          localObject = ((ClassInfo)localObject).getSuperclass();
        } while (!((ClassInfo)localObject).superClassOf(localClassInfo));
        StringBuffer localStringBuffer = new StringBuffer(k + ((ClassInfo)localObject).getName().length() + 2);
        for (int i1 = 0; i1 < k; i1++) {
          localStringBuffer.append("[");
        }
        localStringBuffer.append("L").append(((ClassInfo)localObject).getName().replace('.', '/')).append(";");
        return tType(localStringBuffer.toString());
      }
      if (k > 0)
      {
        if (k == 1) {
          return CodeVerifier.tObject;
        }
        localObject = new StringBuffer(k + 17);
        for (int n = 0; n < k - 1; n++) {
          ((StringBuffer)localObject).append("[");
        }
        ((StringBuffer)localObject).append("Ljava/lang/Object;");
        return tType(((StringBuffer)localObject).toString());
      }
      return CodeVerifier.tNone;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof Type))
      {
        Type localType = (Type)paramObject;
        return (this.typeSig.equals(localType.typeSig)) && (this.instr == localType.instr);
      }
      return false;
    }
    
    public String toString()
    {
      if (this.instr != null) {
        return this.typeSig + "@" + this.instr.getAddr();
      }
      return this.typeSig;
    }
  }
}


