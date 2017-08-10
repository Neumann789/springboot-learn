package jode.decompiler;

import jode.bytecode.ClassInfo;

public abstract interface ClassDeclarer
{
  public abstract ClassDeclarer getParent();
  
  public abstract ClassAnalyzer getClassAnalyzer(ClassInfo paramClassInfo);
  
  public abstract void addClassAnalyzer(ClassAnalyzer paramClassAnalyzer);
}


