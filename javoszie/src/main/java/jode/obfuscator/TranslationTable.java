package jode.obfuscator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class TranslationTable
  extends TreeMap
{
  public void load(InputStream paramInputStream)
    throws IOException
  {
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
    String str1;
    while ((str1 = localBufferedReader.readLine()) != null) {
      if (str1.charAt(0) != '#')
      {
        int i = str1.indexOf('=');
        String str2 = str1.substring(0, i);
        String str3 = str1.substring(i + 1);
        put(str2, str3);
      }
    }
  }
  
  public void store(OutputStream paramOutputStream)
    throws IOException
  {
    PrintWriter localPrintWriter = new PrintWriter(paramOutputStream);
    Iterator localIterator = entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localPrintWriter.println(localEntry.getKey() + "=" + localEntry.getValue());
    }
    localPrintWriter.flush();
  }
}


