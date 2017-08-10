package jode.decompiler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import jode.GlobalOptions;
import jode.bytecode.ClassInfo;
import jode.bytecode.InnerClassInfo;
import jode.type.ArrayType;
import jode.type.ClassInterfacesType;
import jode.type.NullType;
import jode.type.Type;

public class ImportHandler
{
  public static final int DEFAULT_PACKAGE_LIMIT = Integer.MAX_VALUE;
  public static final int DEFAULT_CLASS_LIMIT = 1;
  SortedMap imports;
  Hashtable cachedClassNames = null;
  ClassAnalyzer main;
  String className;
  String pkg;
  int importPackageLimit;
  int importClassLimit;
  static Comparator comparator = new Comparator()
  {
    public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
    {
      String str1 = (String)paramAnonymousObject1;
      String str2 = (String)paramAnonymousObject2;
      boolean bool1 = str1.startsWith("java");
      boolean bool2 = str2.startsWith("java");
      if (bool1 != bool2) {
        return bool1 ? -1 : 1;
      }
      return str1.compareTo(str2);
    }
  };
  
  public ImportHandler()
  {
    this(Integer.MAX_VALUE, 1);
  }
  
  public ImportHandler(int paramInt1, int paramInt2)
  {
    this.importPackageLimit = paramInt1;
    this.importClassLimit = paramInt2;
  }
  
  private boolean conflictsImport(String paramString)
  {
    int i = paramString.lastIndexOf('.');
    if (i != -1)
    {
      String str1 = paramString.substring(0, i);
      if (str1.equals(this.pkg)) {
        return false;
      }
      paramString = paramString.substring(i);
      if (this.pkg.length() != 0)
      {
        if (ClassInfo.exists(this.pkg + paramString)) {
          return true;
        }
      }
      else if (ClassInfo.exists(paramString.substring(1))) {
        return true;
      }
      Iterator localIterator = this.imports.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str2 = (String)localIterator.next();
        if (str2.endsWith(".*"))
        {
          str2 = str2.substring(0, str2.length() - 2);
          if ((!str2.equals(str1)) && (ClassInfo.exists(str2 + paramString))) {
            return true;
          }
        }
        else if ((str2.endsWith(paramString)) || (str2.equals(paramString.substring(1))))
        {
          return true;
        }
      }
    }
    return false;
  }
  
  private void cleanUpImports()
  {
    Integer localInteger = new Integer(Integer.MAX_VALUE);
    TreeMap localTreeMap = new TreeMap(comparator);
    LinkedList localLinkedList = new LinkedList();
    Iterator localIterator = this.imports.keySet().iterator();
    String str;
    Object localObject;
    while (localIterator.hasNext())
    {
      str = (String)localIterator.next();
      localObject = (Integer)this.imports.get(str);
      if (!str.endsWith(".*"))
      {
        if (((Integer)localObject).intValue() >= this.importClassLimit)
        {
          int i = str.lastIndexOf(".");
          if (i != -1)
          {
            if (localTreeMap.containsKey(str.substring(0, i) + ".*")) {
              continue;
            }
            localLinkedList.add(str);
          }
          else if (this.pkg.length() != 0)
          {
            localTreeMap.put(str, localInteger);
          }
        }
      }
      else if (((Integer)localObject).intValue() >= this.importPackageLimit) {
        localTreeMap.put(str, localInteger);
      }
    }
    this.imports = localTreeMap;
    this.cachedClassNames = new Hashtable();
    localIterator = localLinkedList.iterator();
    while (localIterator.hasNext())
    {
      str = (String)localIterator.next();
      if (!conflictsImport(str))
      {
        this.imports.put(str, localInteger);
        localObject = str.substring(str.lastIndexOf('.') + 1);
        this.cachedClassNames.put(str, localObject);
      }
    }
  }
  
  public void dumpHeader(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.println("/* " + this.className + " - Decompiled by JODE");
    paramTabbedPrintWriter.println(" * Visit http://jode.sourceforge.net/");
    paramTabbedPrintWriter.println(" */");
    if (this.pkg.length() != 0) {
      paramTabbedPrintWriter.println("package " + this.pkg + ";");
    }
    cleanUpImports();
    Iterator localIterator = this.imports.keySet().iterator();
    Object localObject = null;
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      if (!str1.equals("java.lang.*"))
      {
        int i = str1.indexOf('.');
        if (i != -1)
        {
          String str2 = str1.substring(0, i);
          if ((localObject != null) && (!((String)localObject).equals(str2))) {
            paramTabbedPrintWriter.println("");
          }
          localObject = str2;
        }
        paramTabbedPrintWriter.println("import " + str1 + ";");
      }
    }
    paramTabbedPrintWriter.println("");
  }
  
  public void error(String paramString)
  {
    GlobalOptions.err.println(paramString);
  }
  
  public void init(String paramString)
  {
    this.imports = new TreeMap(comparator);
    this.imports.put("java.lang.*", new Integer(Integer.MAX_VALUE));
    int i = paramString.lastIndexOf('.');
    this.pkg = (i == -1 ? "" : paramString.substring(0, i));
    this.className = (i == -1 ? paramString : paramString.substring(i + 1));
  }
  
  public void useClass(ClassInfo paramClassInfo)
  {
    for (;;)
    {
      localObject = paramClassInfo.getOuterClasses();
      if (localObject == null) {
        break;
      }
      if ((localObject[0].name == null) || (localObject[0].outer == null)) {
        return;
      }
      paramClassInfo = ClassInfo.forName(localObject[0].outer);
    }
    Object localObject = paramClassInfo.getName();
    Integer localInteger1 = (Integer)this.imports.get(localObject);
    if (localInteger1 == null)
    {
      int i = ((String)localObject).lastIndexOf('.');
      if (i != -1)
      {
        String str = ((String)localObject).substring(0, i);
        if (str.equals(this.pkg)) {
          return;
        }
        Integer localInteger2 = (Integer)this.imports.get(str + ".*");
        if ((localInteger2 != null) && (localInteger2.intValue() >= this.importPackageLimit)) {
          return;
        }
        localInteger2 = localInteger2 == null ? new Integer(1) : new Integer(localInteger2.intValue() + 1);
        this.imports.put(str + ".*", localInteger2);
      }
      localInteger1 = new Integer(1);
    }
    else
    {
      if (localInteger1.intValue() >= this.importClassLimit) {
        return;
      }
      localInteger1 = new Integer(localInteger1.intValue() + 1);
    }
    this.imports.put(localObject, localInteger1);
  }
  
  public final void useType(Type paramType)
  {
    if ((paramType instanceof ArrayType)) {
      useType(((ArrayType)paramType).getElementType());
    } else if ((paramType instanceof ClassInterfacesType)) {
      useClass(((ClassInterfacesType)paramType).getClassInfo());
    }
  }
  
  public String getClassString(ClassInfo paramClassInfo)
  {
    String str1 = paramClassInfo.getName();
    if (this.cachedClassNames == null) {
      return str1;
    }
    String str2 = (String)this.cachedClassNames.get(str1);
    if (str2 != null) {
      return str2;
    }
    int i = str1.lastIndexOf('.');
    if (i != -1)
    {
      String str3 = str1.substring(0, i);
      if ((str3.equals(this.pkg)) || ((this.imports.get(str3 + ".*") != null) && (!conflictsImport(str1))))
      {
        String str4 = str1.substring(i + 1);
        this.cachedClassNames.put(str1, str4);
        return str4;
      }
    }
    this.cachedClassNames.put(str1, str1);
    return str1;
  }
  
  public String getTypeString(Type paramType)
  {
    if ((paramType instanceof ArrayType)) {
      return getTypeString(((ArrayType)paramType).getElementType()) + "[]";
    }
    if ((paramType instanceof ClassInterfacesType)) {
      return getClassString(((ClassInterfacesType)paramType).getClassInfo());
    }
    if ((paramType instanceof NullType)) {
      return "Object";
    }
    return paramType.toString();
  }
  
  protected int loadFileFlags()
  {
    return 1;
  }
}


