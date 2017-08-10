package com.javosize.thirdparty.org.objectweb.asm.commons;

import com.javosize.thirdparty.org.objectweb.asm.Label;

public abstract interface TableSwitchGenerator
{
  public abstract void generateCase(int paramInt, Label paramLabel);
  
  public abstract void generateDefault();
}


