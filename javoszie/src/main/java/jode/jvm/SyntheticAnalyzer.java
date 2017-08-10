package jode.jvm;

import java.util.Iterator;
import java.util.List;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.ClassInfo;
import jode.bytecode.FieldInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.MethodInfo;
import jode.bytecode.Opcodes;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;
import jode.type.MethodType;
import jode.type.Type;

public class SyntheticAnalyzer
  implements Opcodes
{
  public static final int UNKNOWN = 0;
  public static final int GETCLASS = 1;
  public static final int ACCESSGETFIELD = 2;
  public static final int ACCESSPUTFIELD = 3;
  public static final int ACCESSMETHOD = 4;
  public static final int ACCESSGETSTATIC = 5;
  public static final int ACCESSPUTSTATIC = 6;
  public static final int ACCESSSTATICMETHOD = 7;
  public static final int ACCESSCONSTRUCTOR = 8;
  public static final int ACCESSDUPPUTFIELD = 9;
  public static final int ACCESSDUPPUTSTATIC = 10;
  int kind = 0;
  Reference reference;
  MethodInfo method;
  int unifyParam = -1;
  private static final int[] getClassOpcodes = { 25, 184, 176, 58, 187, 89, 25, 182, 183, 191 };
  private static final Reference[] getClassRefs = { null, Reference.getReference("Ljava/lang/Class;", "forName", "(Ljava/lang/String;)Ljava/lang/Class;"), null, null, null, null, null, Reference.getReference("Ljava/lang/Throwable;", "getMessage", "()Ljava/lang/String;"), Reference.getReference("Ljava/lang/NoClassDefFoundError;", "<init>", "(Ljava/lang/String;)V"), null };
  private final int modifierMask = 9;
  
  public SyntheticAnalyzer(MethodInfo paramMethodInfo, boolean paramBoolean)
  {
    this.method = paramMethodInfo;
    if (paramMethodInfo.getBytecode() == null) {
      return;
    }
    if (((!paramBoolean) || (paramMethodInfo.getName().equals("class$"))) && (checkGetClass())) {
      return;
    }
    if (((!paramBoolean) || (paramMethodInfo.getName().startsWith("access$"))) && (checkAccess())) {
      return;
    }
    if ((paramMethodInfo.getName().equals("<init>")) && (checkConstructorAccess())) {}
  }
  
  public int getKind()
  {
    return this.kind;
  }
  
  public Reference getReference()
  {
    return this.reference;
  }
  
  public int getUnifyParam()
  {
    return this.unifyParam;
  }
  
  boolean checkGetClass()
  {
    if ((!this.method.isStatic()) || (!this.method.getType().equals("(Ljava/lang/String;)Ljava/lang/Class;"))) {
      return false;
    }
    BytecodeInfo localBytecodeInfo = this.method.getBytecode();
    Handler[] arrayOfHandler = localBytecodeInfo.getExceptionHandlers();
    if ((arrayOfHandler.length != 1) || (!"java.lang.ClassNotFoundException".equals(arrayOfHandler[0].type))) {
      return false;
    }
    int i = -1;
    int j = 0;
    Iterator localIterator = localBytecodeInfo.getInstructions().iterator();
    while (localIterator.hasNext())
    {
      for (Instruction localInstruction = (Instruction)localIterator.next(); (localInstruction.getOpcode() == 0) && (localIterator.hasNext()); localInstruction = (Instruction)localIterator.next()) {}
      if ((j == getClassOpcodes.length) || (localInstruction.getOpcode() != getClassOpcodes[j])) {
        return false;
      }
      if ((j == 0) && ((localInstruction.getLocalSlot() != 0) || (arrayOfHandler[0].start != localInstruction))) {
        return false;
      }
      if ((j == 2) && (arrayOfHandler[0].end != localInstruction)) {
        return false;
      }
      if (j == 3)
      {
        if (arrayOfHandler[0].catcher != localInstruction) {
          return false;
        }
        i = localInstruction.getLocalSlot();
      }
      if ((j == 4) && (!localInstruction.getClazzType().equals("Ljava/lang/NoClassDefFoundError;"))) {
        return false;
      }
      if ((j == 6) && (localInstruction.getLocalSlot() != i)) {
        return false;
      }
      if ((getClassRefs[j] != null) && (!getClassRefs[j].equals(localInstruction.getReference()))) {
        return false;
      }
      j++;
    }
    this.kind = 1;
    return true;
  }
  
  public boolean checkStaticAccess()
  {
    ClassInfo localClassInfo1 = this.method.getClazzInfo();
    BytecodeInfo localBytecodeInfo = this.method.getBytecode();
    Iterator localIterator = localBytecodeInfo.getInstructions().iterator();
    int i = 0;
    for (Instruction localInstruction = (Instruction)localIterator.next(); (localInstruction.getOpcode() == 0) && (localIterator.hasNext()); localInstruction = (Instruction)localIterator.next()) {}
    Object localObject1;
    if (localInstruction.getOpcode() == 178)
    {
      Reference localReference = localInstruction.getReference();
      ClassInfo localClassInfo2 = TypeSignature.getClassInfo(localReference.getClazz());
      if (!localClassInfo2.superClassOf(localClassInfo1)) {
        return false;
      }
      localObject1 = localClassInfo2.findField(localReference.getName(), localReference.getType());
      if ((localObject1 != null) && ((((FieldInfo)localObject1).getModifiers() & 0x9) != 0)) {
        return false;
      }
      for (localInstruction = (Instruction)localIterator.next(); (localInstruction.getOpcode() == 0) && (localIterator.hasNext()); localInstruction = (Instruction)localIterator.next()) {}
      if ((localInstruction.getOpcode() < 172) || (localInstruction.getOpcode() > 176)) {
        return false;
      }
      this.reference = localReference;
      this.kind = 5;
      return true;
    }
    int j = 0;
    int k = 0;
    while ((localInstruction.getOpcode() >= 21) && (localInstruction.getOpcode() <= 25) && (localInstruction.getLocalSlot() == k))
    {
      j++;
      k += ((localInstruction.getOpcode() == 22) || (localInstruction.getOpcode() == 24) ? 2 : 1);
      for (localInstruction = (Instruction)localIterator.next(); (localInstruction.getOpcode() == 0) && (localIterator.hasNext()); localInstruction = (Instruction)localIterator.next()) {}
    }
    if (localInstruction.getOpcode() == 86 + 3 * k)
    {
      for (localInstruction = (Instruction)localIterator.next(); (localInstruction.getOpcode() == 0) && (localIterator.hasNext()); localInstruction = (Instruction)localIterator.next()) {}
      if (localInstruction.getOpcode() != 179) {
        return false;
      }
      i = 1;
    }
    ClassInfo localClassInfo3;
    Object localObject2;
    if (localInstruction.getOpcode() == 179)
    {
      if (j != 1) {
        return false;
      }
      localObject1 = localInstruction.getReference();
      localClassInfo3 = TypeSignature.getClassInfo(((Reference)localObject1).getClazz());
      if (!localClassInfo3.superClassOf(localClassInfo1)) {
        return false;
      }
      localObject2 = localClassInfo3.findField(((Reference)localObject1).getName(), ((Reference)localObject1).getType());
      if ((localObject2 != null) && ((((FieldInfo)localObject2).getModifiers() & 0x9) != 0)) {
        return false;
      }
      for (localInstruction = (Instruction)localIterator.next(); (localInstruction.getOpcode() == 0) && (localIterator.hasNext()); localInstruction = (Instruction)localIterator.next()) {}
      if (i != 0)
      {
        if ((localInstruction.getOpcode() < 172) || (localInstruction.getOpcode() > 176)) {
          return false;
        }
        this.kind = 10;
      }
      else
      {
        if (localInstruction.getOpcode() != 177) {
          return false;
        }
        this.kind = 6;
      }
      this.reference = ((Reference)localObject1);
      return true;
    }
    if (localInstruction.getOpcode() == 184)
    {
      localObject1 = localInstruction.getReference();
      localClassInfo3 = TypeSignature.getClassInfo(((Reference)localObject1).getClazz());
      if (!localClassInfo3.superClassOf(localClassInfo1)) {
        return false;
      }
      localObject2 = localClassInfo3.findMethod(((Reference)localObject1).getName(), ((Reference)localObject1).getType());
      MethodType localMethodType = Type.tMethod(((Reference)localObject1).getType());
      if (((((MethodInfo)localObject2).getModifiers() & 0x9) != 8) || (localMethodType.getParameterTypes().length != j)) {
        return false;
      }
      for (localInstruction = (Instruction)localIterator.next(); (localInstruction.getOpcode() == 0) && (localIterator.hasNext()); localInstruction = (Instruction)localIterator.next()) {}
      if (localMethodType.getReturnType() == Type.tVoid)
      {
        if (localInstruction.getOpcode() != 177) {
          return false;
        }
      }
      else if ((localInstruction.getOpcode() < 172) || (localInstruction.getOpcode() > 176)) {
        return false;
      }
      this.reference = ((Reference)localObject1);
      this.kind = 7;
      return true;
    }
    return false;
  }
  
  public boolean checkAccess()
  {
    ClassInfo localClassInfo1 = this.method.getClazzInfo();
    BytecodeInfo localBytecodeInfo = this.method.getBytecode();
    Handler[] arrayOfHandler = localBytecodeInfo.getExceptionHandlers();
    int i = 0;
    if ((arrayOfHandler != null) && (arrayOfHandler.length != 0)) {
      return false;
    }
    if ((this.method.isStatic()) && (checkStaticAccess())) {
      return true;
    }
    Iterator localIterator = localBytecodeInfo.getInstructions().iterator();
    for (Instruction localInstruction = (Instruction)localIterator.next(); (localInstruction.getOpcode() == 0) && (localIterator.hasNext()); localInstruction = (Instruction)localIterator.next()) {}
    if ((localInstruction.getOpcode() != 25) || (localInstruction.getLocalSlot() != 0)) {
      return false;
    }
    for (localInstruction = (Instruction)localIterator.next(); (localInstruction.getOpcode() == 0) && (localIterator.hasNext()); localInstruction = (Instruction)localIterator.next()) {}
    Object localObject1;
    if (localInstruction.getOpcode() == 180)
    {
      Reference localReference = localInstruction.getReference();
      ClassInfo localClassInfo2 = TypeSignature.getClassInfo(localReference.getClazz());
      if (!localClassInfo2.superClassOf(localClassInfo1)) {
        return false;
      }
      localObject1 = localClassInfo2.findField(localReference.getName(), localReference.getType());
      if ((localObject1 != null) && ((((FieldInfo)localObject1).getModifiers() & 0x9) != 0)) {
        return false;
      }
      for (localInstruction = (Instruction)localIterator.next(); (localInstruction.getOpcode() == 0) && (localIterator.hasNext()); localInstruction = (Instruction)localIterator.next()) {}
      if ((localInstruction.getOpcode() < 172) || (localInstruction.getOpcode() > 176)) {
        return false;
      }
      this.reference = localReference;
      this.kind = 2;
      return true;
    }
    int j = 0;
    int k = 1;
    while ((localInstruction.getOpcode() >= 21) && (localInstruction.getOpcode() <= 25) && (localInstruction.getLocalSlot() == k))
    {
      j++;
      k += ((localInstruction.getOpcode() == 22) || (localInstruction.getOpcode() == 24) ? 2 : 1);
      for (localInstruction = (Instruction)localIterator.next(); (localInstruction.getOpcode() == 0) && (localIterator.hasNext()); localInstruction = (Instruction)localIterator.next()) {}
    }
    if (localInstruction.getOpcode() == 84 + 3 * k)
    {
      for (localInstruction = (Instruction)localIterator.next(); (localInstruction.getOpcode() == 0) && (localIterator.hasNext()); localInstruction = (Instruction)localIterator.next()) {}
      if (localInstruction.getOpcode() != 181) {
        return false;
      }
      i = 1;
    }
    ClassInfo localClassInfo3;
    Object localObject2;
    if (localInstruction.getOpcode() == 181)
    {
      if (j != 1) {
        return false;
      }
      localObject1 = localInstruction.getReference();
      localClassInfo3 = TypeSignature.getClassInfo(((Reference)localObject1).getClazz());
      if (!localClassInfo3.superClassOf(localClassInfo1)) {
        return false;
      }
      localObject2 = localClassInfo3.findField(((Reference)localObject1).getName(), ((Reference)localObject1).getType());
      if ((localObject2 != null) && ((((FieldInfo)localObject2).getModifiers() & 0x9) != 0)) {
        return false;
      }
      for (localInstruction = (Instruction)localIterator.next(); (localInstruction.getOpcode() == 0) && (localIterator.hasNext()); localInstruction = (Instruction)localIterator.next()) {}
      if (i != 0)
      {
        if ((localInstruction.getOpcode() < 172) || (localInstruction.getOpcode() > 176)) {
          return false;
        }
        this.kind = 9;
      }
      else
      {
        if (localInstruction.getOpcode() != 177) {
          return false;
        }
        this.kind = 3;
      }
      this.reference = ((Reference)localObject1);
      return true;
    }
    if (localInstruction.getOpcode() == 183)
    {
      localObject1 = localInstruction.getReference();
      localClassInfo3 = TypeSignature.getClassInfo(((Reference)localObject1).getClazz());
      if (!localClassInfo3.superClassOf(localClassInfo1)) {
        return false;
      }
      localObject2 = localClassInfo3.findMethod(((Reference)localObject1).getName(), ((Reference)localObject1).getType());
      MethodType localMethodType = Type.tMethod(((Reference)localObject1).getType());
      if (((((MethodInfo)localObject2).getModifiers() & 0x9) != 0) || (localMethodType.getParameterTypes().length != j)) {
        return false;
      }
      for (localInstruction = (Instruction)localIterator.next(); (localInstruction.getOpcode() == 0) && (localIterator.hasNext()); localInstruction = (Instruction)localIterator.next()) {}
      if (localMethodType.getReturnType() == Type.tVoid)
      {
        if (localInstruction.getOpcode() != 177) {
          return false;
        }
      }
      else if ((localInstruction.getOpcode() < 172) || (localInstruction.getOpcode() > 176)) {
        return false;
      }
      this.reference = ((Reference)localObject1);
      this.kind = 4;
      return true;
    }
    return false;
  }
  
  public boolean checkConstructorAccess()
  {
    ClassInfo localClassInfo1 = this.method.getClazzInfo();
    BytecodeInfo localBytecodeInfo = this.method.getBytecode();
    String[] arrayOfString = TypeSignature.getParameterTypes(this.method.getType());
    Handler[] arrayOfHandler = localBytecodeInfo.getExceptionHandlers();
    if ((arrayOfHandler != null) && (arrayOfHandler.length != 0)) {
      return false;
    }
    Iterator localIterator = localBytecodeInfo.getInstructions().iterator();
    for (Instruction localInstruction = (Instruction)localIterator.next(); (localInstruction.getOpcode() == 0) && (localIterator.hasNext()); localInstruction = (Instruction)localIterator.next()) {}
    int i = 0;
    int j = 0;
    while ((localInstruction.getOpcode() >= 21) && (localInstruction.getOpcode() <= 25))
    {
      if ((localInstruction.getLocalSlot() > j) && (this.unifyParam == -1) && (i > 0) && (arrayOfString[(i - 1)].charAt(0) == 'L'))
      {
        this.unifyParam = i;
        i++;
        j++;
      }
      if (localInstruction.getLocalSlot() != j) {
        return false;
      }
      i++;
      j += ((localInstruction.getOpcode() == 22) || (localInstruction.getOpcode() == 24) ? 2 : 1);
      localInstruction = (Instruction)localIterator.next();
    }
    if ((i > 0) && (localInstruction.getOpcode() == 183))
    {
      if ((this.unifyParam == -1) && (i <= arrayOfString.length) && (arrayOfString[(i - 1)].charAt(0) == 'L')) {
        this.unifyParam = (i++);
      }
      Reference localReference = localInstruction.getReference();
      ClassInfo localClassInfo2 = TypeSignature.getClassInfo(localReference.getClazz());
      if (localClassInfo2 != localClassInfo1) {
        return false;
      }
      MethodInfo localMethodInfo = localClassInfo2.findMethod(localReference.getName(), localReference.getType());
      MethodType localMethodType = Type.tMethod(localReference.getType());
      if (((localMethodInfo.getModifiers() & 0x9) != 0) || (!localMethodInfo.getName().equals("<init>")) || (this.unifyParam == -1) || (localMethodType.getParameterTypes().length != i - 2)) {
        return false;
      }
      localInstruction = (Instruction)localIterator.next();
      if (localInstruction.getOpcode() != 177) {
        return false;
      }
      this.reference = localReference;
      this.kind = 8;
      return true;
    }
    return false;
  }
}


