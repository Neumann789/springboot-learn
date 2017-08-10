package jode.obfuscator;

import java.io.PrintWriter;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import jode.AssertError;
import jode.GlobalOptions;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.MethodInfo;
import jode.bytecode.Opcodes;

public class MethodIdentifier
  extends Identifier
  implements Opcodes
{
  ClassIdentifier clazz;
  MethodInfo info;
  String name;
  String type;
  boolean globalSideEffects;
  BitSet localSideEffects;
  CodeAnalyzer codeAnalyzer;
  boolean wasTransformed = false;
  
  public MethodIdentifier(ClassIdentifier paramClassIdentifier, MethodInfo paramMethodInfo)
  {
    super(paramMethodInfo.getName());
    this.name = paramMethodInfo.getName();
    this.type = paramMethodInfo.getType();
    this.clazz = paramClassIdentifier;
    this.info = paramMethodInfo;
    BytecodeInfo localBytecodeInfo = paramMethodInfo.getBytecode();
    if (localBytecodeInfo != null)
    {
      if ((Main.stripping & 0x4) != 0) {
        paramMethodInfo.getBytecode().setLocalVariableTable(null);
      }
      if ((Main.stripping & 0x8) != 0) {
        paramMethodInfo.getBytecode().setLineNumberTable(null);
      }
      this.codeAnalyzer = Main.getClassBundle().getCodeAnalyzer();
      CodeTransformer[] arrayOfCodeTransformer = Main.getClassBundle().getPreTransformers();
      for (int i = 0; i < arrayOfCodeTransformer.length; i++) {
        arrayOfCodeTransformer[i].transformCode(localBytecodeInfo);
      }
      paramMethodInfo.setBytecode(localBytecodeInfo);
    }
  }
  
  public Iterator getChilds()
  {
    return Collections.EMPTY_LIST.iterator();
  }
  
  public void setSingleReachable()
  {
    super.setSingleReachable();
    Main.getClassBundle().analyzeIdentifier(this);
  }
  
  public void analyze()
  {
    if (GlobalOptions.verboseLevel > 1) {
      GlobalOptions.err.println("Analyze: " + this);
    }
    String str = getType();
    int j;
    for (int i = str.indexOf('L'); i != -1; i = str.indexOf('L', j))
    {
      j = str.indexOf(';', i);
      Main.getClassBundle().reachableClass(str.substring(i + 1, j).replace('/', '.'));
    }
    String[] arrayOfString = this.info.getExceptions();
    if (arrayOfString != null) {
      for (int k = 0; k < arrayOfString.length; k++) {
        Main.getClassBundle().reachableClass(arrayOfString[k]);
      }
    }
    BytecodeInfo localBytecodeInfo = this.info.getBytecode();
    if (localBytecodeInfo != null) {
      this.codeAnalyzer.analyzeCode(this, localBytecodeInfo);
    }
  }
  
  public Identifier getParent()
  {
    return this.clazz;
  }
  
  public String getFullName()
  {
    return this.clazz.getFullName() + "." + getName() + "." + getType();
  }
  
  public String getFullAlias()
  {
    return this.clazz.getFullAlias() + "." + getAlias() + "." + Main.getClassBundle().getTypeAlias(getType());
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public int getModifiers()
  {
    return this.info.getModifiers();
  }
  
  public boolean conflicting(String paramString)
  {
    return this.clazz.methodConflicts(this, paramString);
  }
  
  public String toString()
  {
    return "MethodIdentifier " + getFullName();
  }
  
  public boolean hasGlobalSideEffects()
  {
    return this.globalSideEffects;
  }
  
  public boolean getLocalSideEffects(int paramInt)
  {
    return (this.globalSideEffects) || (this.localSideEffects.get(paramInt));
  }
  
  public void setGlobalSideEffects()
  {
    this.globalSideEffects = true;
  }
  
  public void setLocalSideEffects(int paramInt)
  {
    this.localSideEffects.set(paramInt);
  }
  
  public void doTransformations()
  {
    if (this.wasTransformed) {
      throw new AssertError("doTransformation called on transformed method");
    }
    this.wasTransformed = true;
    this.info.setName(getAlias());
    ClassBundle localClassBundle = Main.getClassBundle();
    this.info.setType(localClassBundle.getTypeAlias(this.type));
    if (this.codeAnalyzer != null)
    {
      localObject1 = this.info.getBytecode();
      try
      {
        if (isReachable()) {
          this.codeAnalyzer.transformCode((BytecodeInfo)localObject1);
        }
        CodeTransformer[] arrayOfCodeTransformer = localClassBundle.getPostTransformers();
        for (int j = 0; j < arrayOfCodeTransformer.length; j++) {
          arrayOfCodeTransformer[j].transformCode((BytecodeInfo)localObject1);
        }
      }
      catch (RuntimeException localRuntimeException)
      {
        localRuntimeException.printStackTrace(GlobalOptions.err);
        ((BytecodeInfo)localObject1).dumpCode(GlobalOptions.err);
      }
      Object localObject2 = ((BytecodeInfo)localObject1).getInstructions().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        Instruction localInstruction = (Instruction)((Iterator)localObject2).next();
        switch (localInstruction.getOpcode())
        {
        case 182: 
        case 183: 
        case 184: 
        case 185: 
          localInstruction.setReference(Main.getClassBundle().getReferenceAlias(localInstruction.getReference()));
          break;
        case 178: 
        case 179: 
        case 180: 
        case 181: 
          localInstruction.setReference(Main.getClassBundle().getReferenceAlias(localInstruction.getReference()));
          break;
        case 187: 
        case 192: 
        case 193: 
        case 197: 
          localInstruction.setClazzType(Main.getClassBundle().getTypeAlias(localInstruction.getClazzType()));
        }
      }
      localObject2 = ((BytecodeInfo)localObject1).getExceptionHandlers();
      for (int k = 0; k < localObject2.length; k++) {
        if (localObject2[k].type != null)
        {
          ClassIdentifier localClassIdentifier2 = Main.getClassBundle().getClassIdentifier(localObject2[k].type);
          if (localClassIdentifier2 != null) {
            localObject2[k].type = localClassIdentifier2.getFullAlias();
          }
        }
      }
      this.info.setBytecode((BytecodeInfo)localObject1);
    }
    Object localObject1 = this.info.getExceptions();
    if (localObject1 != null) {
      for (int i = 0; i < localObject1.length; i++)
      {
        ClassIdentifier localClassIdentifier1 = Main.getClassBundle().getClassIdentifier(localObject1[i]);
        if (localClassIdentifier1 != null) {
          localObject1[i] = localClassIdentifier1.getFullAlias();
        }
      }
    }
  }
}


