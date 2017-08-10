package jode.obfuscator.modules;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import jode.GlobalOptions;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.ClassInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.Opcodes;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;
import jode.obfuscator.ClassBundle;
import jode.obfuscator.ClassIdentifier;
import jode.obfuscator.CodeAnalyzer;
import jode.obfuscator.FieldIdentifier;
import jode.obfuscator.Identifier;
import jode.obfuscator.Main;
import jode.obfuscator.MethodIdentifier;

public class SimpleAnalyzer
  implements CodeAnalyzer, Opcodes
{
  private ClassInfo canonizeIfaceRef(ClassInfo paramClassInfo, Reference paramReference)
  {
    while (paramClassInfo != null)
    {
      if (paramClassInfo.findMethod(paramReference.getName(), paramReference.getType()) != null) {
        return paramClassInfo;
      }
      ClassInfo[] arrayOfClassInfo = paramClassInfo.getInterfaces();
      for (int i = 0; i < arrayOfClassInfo.length; i++)
      {
        ClassInfo localClassInfo = canonizeIfaceRef(arrayOfClassInfo[i], paramReference);
        if (localClassInfo != null) {
          return localClassInfo;
        }
      }
      paramClassInfo = paramClassInfo.getSuperclass();
    }
    return null;
  }
  
  public Identifier canonizeReference(Instruction paramInstruction)
  {
    Reference localReference = paramInstruction.getReference();
    Identifier localIdentifier = Main.getClassBundle().getIdentifier(localReference);
    String str1 = localReference.getClazz();
    Object localObject;
    String str2;
    if (localIdentifier != null)
    {
      localObject = (ClassIdentifier)localIdentifier.getParent();
      str2 = "L" + ((ClassIdentifier)localObject).getFullName().replace('.', '/') + ";";
    }
    else
    {
      if (str1.charAt(0) == '[') {
        localObject = ClassInfo.javaLangObject;
      } else {
        localObject = ClassInfo.forName(str1.substring(1, str1.length() - 1).replace('/', '.'));
      }
      if (paramInstruction.getOpcode() == 185)
      {
        localObject = canonizeIfaceRef((ClassInfo)localObject, localReference);
      }
      else
      {
        if (paramInstruction.getOpcode() >= 182) {
          while ((localObject != null) && (((ClassInfo)localObject).findMethod(localReference.getName(), localReference.getType()) == null)) {
            localObject = ((ClassInfo)localObject).getSuperclass();
          }
        }
        while ((localObject != null) && (((ClassInfo)localObject).findField(localReference.getName(), localReference.getType()) == null)) {
          localObject = ((ClassInfo)localObject).getSuperclass();
        }
      }
      if (localObject == null)
      {
        GlobalOptions.err.println("WARNING: Can't find reference: " + localReference);
        str2 = str1;
      }
      else
      {
        str2 = "L" + ((ClassInfo)localObject).getName().replace('.', '/') + ";";
      }
    }
    if (!str2.equals(localReference.getClazz()))
    {
      localReference = Reference.getReference(str2, localReference.getName(), localReference.getType());
      paramInstruction.setReference(localReference);
    }
    return localIdentifier;
  }
  
  public void analyzeCode(MethodIdentifier paramMethodIdentifier, BytecodeInfo paramBytecodeInfo)
  {
    Object localObject1 = paramBytecodeInfo.getInstructions().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Instruction localInstruction = (Instruction)((Iterator)localObject1).next();
      Object localObject2;
      switch (localInstruction.getOpcode())
      {
      case 192: 
      case 193: 
      case 197: 
        localObject2 = localInstruction.getClazzType();
        for (int j = 0; (j < ((String)localObject2).length()) && (((String)localObject2).charAt(j) == '['); j++) {}
        if ((j < ((String)localObject2).length()) && (((String)localObject2).charAt(j) == 'L'))
        {
          localObject2 = ((String)localObject2).substring(j + 1, ((String)localObject2).length() - 1).replace('/', '.');
          Main.getClassBundle().reachableClass((String)localObject2);
        }
        break;
      case 179: 
      case 181: 
      case 182: 
      case 183: 
      case 184: 
      case 185: 
        paramMethodIdentifier.setGlobalSideEffects();
      case 178: 
      case 180: 
        localObject2 = canonizeReference(localInstruction);
        if (localObject2 != null) {
          if ((localInstruction.getOpcode() == 179) || (localInstruction.getOpcode() == 181))
          {
            FieldIdentifier localFieldIdentifier = (FieldIdentifier)localObject2;
            if ((localFieldIdentifier != null) && (!localFieldIdentifier.isNotConstant())) {
              localFieldIdentifier.setNotConstant();
            }
          }
          else if ((localInstruction.getOpcode() == 182) || (localInstruction.getOpcode() == 185))
          {
            ((ClassIdentifier)((Identifier)localObject2).getParent()).reachableReference(localInstruction.getReference(), true);
          }
          else
          {
            ((Identifier)localObject2).setReachable();
          }
        }
        break;
      }
    }
    localObject1 = paramBytecodeInfo.getExceptionHandlers();
    for (int i = 0; i < localObject1.length; i++) {
      if (localObject1[i].type != null) {
        Main.getClassBundle().reachableClass(localObject1[i].type);
      }
    }
  }
  
  public void transformCode(BytecodeInfo paramBytecodeInfo)
  {
    ListIterator localListIterator = paramBytecodeInfo.getInstructions().listIterator();
    while (localListIterator.hasNext())
    {
      Instruction localInstruction = (Instruction)localListIterator.next();
      if ((localInstruction.getOpcode() == 179) || (localInstruction.getOpcode() == 181))
      {
        Reference localReference = localInstruction.getReference();
        FieldIdentifier localFieldIdentifier = (FieldIdentifier)Main.getClassBundle().getIdentifier(localReference);
        if ((localFieldIdentifier != null) && ((Main.stripping & 0x1) != 0) && (!localFieldIdentifier.isReachable()))
        {
          int i = localInstruction.getOpcode() == 179 ? 0 : 1;
          i += TypeSignature.getTypeSize(localReference.getType());
          switch (i)
          {
          case 1: 
            localListIterator.set(new Instruction(87));
            break;
          case 2: 
            localListIterator.set(new Instruction(88));
            break;
          case 3: 
            localListIterator.set(new Instruction(88));
            localListIterator.add(new Instruction(87));
          }
        }
      }
    }
  }
}


