package com.javosize.thirdparty.org.objectweb.asm.util;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.tree.MethodNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.analysis.Analyzer;
import com.javosize.thirdparty.org.objectweb.asm.tree.analysis.BasicVerifier;
import java.io.PrintWriter;
import java.io.StringWriter;

class CheckMethodAdapter$1
  extends MethodNode
{
  CheckMethodAdapter$1(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString, MethodVisitor paramMethodVisitor)
  {
    super(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
  }
  
  public void visitEnd()
  {
    Analyzer localAnalyzer = new Analyzer(new BasicVerifier());
    try
    {
      localAnalyzer.analyze("dummy", this);
    }
    catch (Exception localException)
    {
      if (((localException instanceof IndexOutOfBoundsException)) && (this.maxLocals == 0) && (this.maxStack == 0)) {
        throw new RuntimeException("Data flow checking option requires valid, non zero maxLocals and maxStack values.");
      }
      localException.printStackTrace();
      StringWriter localStringWriter = new StringWriter();
      PrintWriter localPrintWriter = new PrintWriter(localStringWriter, true);
      CheckClassAdapter.printAnalyzerResult(this, localAnalyzer, localPrintWriter);
      localPrintWriter.close();
      throw new RuntimeException(localException.getMessage() + ' ' + localStringWriter.toString());
    }
    accept(this.val$cmv);
  }
}


