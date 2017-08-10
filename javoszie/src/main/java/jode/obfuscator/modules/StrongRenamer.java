package jode.obfuscator.modules;

import java.util.Collection;
import java.util.Iterator;
import jode.obfuscator.ClassIdentifier;
import jode.obfuscator.FieldIdentifier;
import jode.obfuscator.Identifier;
import jode.obfuscator.LocalIdentifier;
import jode.obfuscator.MethodIdentifier;
import jode.obfuscator.OptionHandler;
import jode.obfuscator.PackageIdentifier;
import jode.obfuscator.Renamer;

public class StrongRenamer
  implements Renamer, OptionHandler
{
  static String[] idents = { "Package", "Class", "Field", "Method", "Local" };
  static String[] parts = { "Start", "Part" };
  String[][] charsets = new String[idents.length][parts.length];
  String[] javaKeywords = { "abstract", "default", "if", "private", "throw", "boolean", "do", "implements", "protected", "throws", "break", "double", "import", "public", "transient", "byte", "else", "instanceof", "return", "try", "case", "extends", "int", "short", "void", "catch", "final", "interface", "static", "volatile", "char", "finally", "long", "super", "while", "class", "float", "native", "switch", "const", "for", "new", "synchronized", "continue", "goto", "package", "this", "strictfp", "null", "true", "false" };
  
  public StrongRenamer()
  {
    for (int i = 0; i < idents.length; i++) {
      for (int j = 0; j < parts.length; j++) {
        this.charsets[i][j] = "abcdefghijklmnopqrstuvwxyz";
      }
    }
  }
  
  public void setOption(String paramString, Collection paramCollection)
  {
    if (paramString.startsWith("charset"))
    {
      Object localObject = paramCollection.iterator().next();
      if ((paramCollection.size() != 1) || (!(localObject instanceof String))) {
        throw new IllegalArgumentException("Only string parameter are supported.");
      }
      String str1 = (String)localObject;
      String str2 = paramString.substring("charset".length());
      int i = -1;
      int j = -1;
      if (str2.length() > 0) {
        for (k = 0; k < idents.length; k++) {
          if (str2.startsWith(idents[k]))
          {
            str2 = str2.substring(idents[k].length());
            j = k;
            break;
          }
        }
      }
      if (str2.length() > 0) {
        for (k = 0; k < parts.length; k++) {
          if (str2.startsWith(parts[k]))
          {
            str2 = str2.substring(parts[k].length());
            i = k;
            break;
          }
        }
      }
      if (str2.length() > 0) {
        throw new IllegalArgumentException("Invalid charset `" + paramString + "'");
      }
      for (int k = 0; k < idents.length; k++) {
        if ((j < 0) || (j == k)) {
          for (int m = 0; m < parts.length; m++) {
            if ((i < 0) || (i == m)) {
              this.charsets[k][m] = str1;
            }
          }
        }
      }
    }
    else
    {
      throw new IllegalArgumentException("Invalid option `" + paramString + "'");
    }
  }
  
  public Iterator generateNames(Identifier paramIdentifier)
  {
    int i;
    if ((paramIdentifier instanceof PackageIdentifier)) {
      i = 0;
    } else if ((paramIdentifier instanceof ClassIdentifier)) {
      i = 1;
    } else if ((paramIdentifier instanceof FieldIdentifier)) {
      i = 2;
    } else if ((paramIdentifier instanceof MethodIdentifier)) {
      i = 3;
    } else if ((paramIdentifier instanceof LocalIdentifier)) {
      i = 4;
    } else {
      throw new IllegalArgumentException(paramIdentifier.getClass().getName());
    }
    final String[] arrayOfString = this.charsets[i];
    new Iterator()
    {
      char[] name = null;
      int headIndex;
      
      public boolean hasNext()
      {
        return true;
      }
      
      public Object next()
      {
        if (this.name == null)
        {
          this.name = new char[] { arrayOfString[0].charAt(0) };
          this.headIndex = 0;
          return new String(this.name);
        }
        if (++this.headIndex < arrayOfString[0].length())
        {
          this.name[0] = arrayOfString[0].charAt(this.headIndex);
          return new String(this.name);
        }
        this.headIndex = 0;
        this.name[0] = arrayOfString[0].charAt(0);
        String str1 = arrayOfString[1];
        for (int i = 1; i < this.name.length; i++)
        {
          j = str1.indexOf(this.name[i]) + 1;
          if (j < str1.length())
          {
            this.name[i] = str1.charAt(j);
            return new String(this.name);
          }
          this.name[i] = str1.charAt(0);
        }
        this.name = new char[this.name.length + 1];
        this.name[0] = arrayOfString[0].charAt(0);
        i = arrayOfString[1].charAt(0);
        for (int j = 1; j < this.name.length; j++) {
          this.name[j] = i;
        }
        String str2 = new String(this.name);
        for (int k = 0;; k++)
        {
          if (k >= StrongRenamer.this.javaKeywords.length) {
            return str2;
          }
          if (str2.equals(StrongRenamer.this.javaKeywords[k])) {
            break;
          }
        }
        return str2;
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
}


