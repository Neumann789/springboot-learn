package jode.obfuscator;

import jode.bytecode.BytecodeInfo;

public abstract interface CodeAnalyzer
  extends CodeTransformer
{
  public abstract void analyzeCode(MethodIdentifier paramMethodIdentifier, BytecodeInfo paramBytecodeInfo);
}


