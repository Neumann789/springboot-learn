package jode.obfuscator;

import jode.bytecode.BytecodeInfo;

public abstract interface CodeTransformer
{
  public abstract void transformCode(BytecodeInfo paramBytecodeInfo);
}


