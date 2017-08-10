package jode.decompiler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import jode.bytecode.ClassInfo;
import jode.bytecode.SearchPath;

public class Decompiler
{
  private SearchPath searchPath = null;
  private int importPackageLimit = Integer.MAX_VALUE;
  private int importClassLimit = 1;
  public static final char altPathSeparatorChar = ',';
  private static final String[] optionStrings = { "lvt", "inner", "anonymous", "push", "pretty", "decrypt", "onetime", "immediate", "verify", "contrafo" };
  
  public void setClassPath(String paramString)
  {
    this.searchPath = new SearchPath(paramString);
  }
  
  public void setClassPath(String[] paramArrayOfString)
  {
    StringBuffer localStringBuffer = new StringBuffer(paramArrayOfString[0]);
    for (int i = 1; i < paramArrayOfString.length; i++) {
      localStringBuffer.append(',').append(paramArrayOfString[i]);
    }
    this.searchPath = new SearchPath(localStringBuffer.toString());
  }
  
  public void setOption(String paramString1, String paramString2)
  {
    if (paramString1.equals("style"))
    {
      if (paramString2.equals("gnu")) {
        Options.outputStyle = 66;
      } else if (paramString2.equals("sun")) {
        Options.outputStyle = 20;
      } else if (paramString2.equals("pascal")) {
        Options.outputStyle = 36;
      } else {
        throw new IllegalArgumentException("Invalid style " + paramString2);
      }
      return;
    }
    if (paramString1.equals("import"))
    {
      i = paramString2.indexOf(',');
      int j = Integer.parseInt(paramString2.substring(0, i));
      if (j == 0) {
        j = Integer.MAX_VALUE;
      }
      int k = Integer.parseInt(paramString2.substring(i + 1));
      if (k == 0) {
        k = Integer.MAX_VALUE;
      }
      if ((k < 0) || (j < 0)) {
        throw new IllegalArgumentException("Option import doesn't allow negative parameters");
      }
      this.importPackageLimit = j;
      this.importClassLimit = k;
      return;
    }
    if (paramString1.equals("verbose"))
    {
      jode.GlobalOptions.verboseLevel = Integer.parseInt(paramString2);
      return;
    }
    for (int i = 0; i < optionStrings.length; i++) {
      if (paramString1.equals(optionStrings[i]))
      {
        if ((paramString2.equals("0")) || (paramString2.equals("off")) || (paramString2.equals("no"))) {
          Options.options &= (1 << i ^ 0xFFFFFFFF);
        } else if ((paramString2.equals("1")) || (paramString2.equals("on")) || (paramString2.equals("yes"))) {
          Options.options |= 1 << i;
        } else {
          throw new IllegalArgumentException("Illegal value for " + paramString1);
        }
        return;
      }
    }
    throw new IllegalArgumentException("Illegal option: " + paramString1);
  }
  
  public void setErr(PrintWriter paramPrintWriter)
  {
    jode.GlobalOptions.err = paramPrintWriter;
  }
  
  public void decompile(String paramString, Writer paramWriter, ProgressListener paramProgressListener)
    throws IOException
  {
    if (this.searchPath == null)
    {
      localObject = System.getProperty("java.class.path").replace(File.pathSeparatorChar, ',');
      this.searchPath = new SearchPath((String)localObject);
    }
    ClassInfo.setClassPath(this.searchPath);
    Object localObject = ClassInfo.forName(paramString);
    ImportHandler localImportHandler = new ImportHandler(this.importPackageLimit, this.importClassLimit);
    TabbedPrintWriter localTabbedPrintWriter = new TabbedPrintWriter(paramWriter, localImportHandler, false);
    ClassAnalyzer localClassAnalyzer = new ClassAnalyzer(null, (ClassInfo)localObject, localImportHandler);
    localClassAnalyzer.dumpJavaFile(localTabbedPrintWriter, paramProgressListener);
    paramWriter.flush();
  }
}


