package jode.obfuscator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.zip.ZipOutputStream;
import jode.GlobalOptions;
import jode.bytecode.ClassInfo;
import jode.bytecode.Reference;
import jode.obfuscator.modules.MultiIdentifierMatcher;
import jode.obfuscator.modules.SimpleAnalyzer;
import jode.obfuscator.modules.WildCard;

public class ClassBundle
  implements OptionHandler
{
  PackageIdentifier basePackage = new PackageIdentifier(this, null, "", "");
  Set toAnalyze = new HashSet();
  String classPath = System.getProperty("java.class.path").replace(File.pathSeparatorChar, ',');
  String destDir = ".";
  String inTableFile;
  String outTableFile;
  String outRevTableFile;
  IdentifierMatcher loading;
  IdentifierMatcher preserving;
  IdentifierMatcher reaching;
  CodeTransformer[] preTrafos;
  CodeAnalyzer analyzer;
  CodeTransformer[] postTrafos;
  Renamer renamer;
  private static final Map aliasesHash = new WeakHashMap();
  private static final Map clazzCache = new HashMap();
  private static final Map referenceCache = new HashMap();
  
  public ClassBundle()
  {
    this.basePackage.setReachable();
    this.basePackage.setPreserved();
  }
  
  public static void setStripOptions(Collection paramCollection) {}
  
  public void setOption(String paramString, Collection paramCollection)
  {
    Object localObject1;
    Object localObject2;
    if (paramString.equals("classpath"))
    {
      localObject1 = paramCollection.iterator();
      localObject2 = new StringBuffer((String)((Iterator)localObject1).next());
      while (((Iterator)localObject1).hasNext()) {
        ((StringBuffer)localObject2).append(',').append((String)((Iterator)localObject1).next());
      }
      ClassInfo.setClassPath(((StringBuffer)localObject2).toString());
      return;
    }
    if (paramString.equals("dest"))
    {
      if (paramCollection.size() != 1) {
        throw new IllegalArgumentException("Only one destination path allowed");
      }
      this.destDir = ((String)paramCollection.iterator().next());
      return;
    }
    if (paramString.equals("verbose"))
    {
      if (paramCollection.size() != 1) {
        throw new IllegalArgumentException("Verbose takes one int parameter");
      }
      GlobalOptions.verboseLevel = ((Integer)paramCollection.iterator().next()).intValue();
      return;
    }
    if ((paramString.equals("intable")) || (paramString.equals("table")))
    {
      if (paramCollection.size() != 1) {
        throw new IllegalArgumentException("Only one destination path allowed");
      }
      this.inTableFile = ((String)paramCollection.iterator().next());
      return;
    }
    if (paramString.equals("outtable"))
    {
      if (paramCollection.size() != 1) {
        throw new IllegalArgumentException("Only one destination path allowed");
      }
      this.outTableFile = ((String)paramCollection.iterator().next());
      return;
    }
    if ((paramString.equals("outrevtable")) || (paramString.equals("revtable")))
    {
      if (paramCollection.size() != 1) {
        throw new IllegalArgumentException("Only one destination path allowed");
      }
      this.outRevTableFile = ((String)paramCollection.iterator().next());
      return;
    }
    if (paramString.equals("strip"))
    {
      localObject1 = paramCollection.iterator();
      if (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        for (int j = 0;; j++)
        {
          if (j >= Main.stripNames.length) {
            break label417;
          }
          if (((String)localObject2).equals(Main.stripNames[j]))
          {
            Main.stripping |= 1 << j;
            break;
          }
        }
        label417:
        throw new IllegalArgumentException("Unknown strip option: `" + (String)localObject2 + "'");
      }
      return;
    }
    int i;
    Iterator localIterator;
    Object localObject3;
    if (paramString.equals("load"))
    {
      if (paramCollection.size() == 1)
      {
        localObject1 = paramCollection.iterator().next();
        if ((localObject1 instanceof String)) {
          this.loading = new WildCard((String)localObject1);
        } else {
          this.loading = ((IdentifierMatcher)localObject1);
        }
      }
      else
      {
        localObject1 = new IdentifierMatcher[paramCollection.size()];
        i = 0;
        localIterator = paramCollection.iterator();
        while (localIterator.hasNext())
        {
          localObject3 = localIterator.next();
          localObject1[(i++)] = ((localObject3 instanceof String) ? new WildCard((String)localObject3) : (IdentifierMatcher)localObject3);
        }
        this.loading = new MultiIdentifierMatcher(MultiIdentifierMatcher.OR, (IdentifierMatcher[])localObject1);
      }
      return;
    }
    if (paramString.equals("preserve"))
    {
      if (paramCollection.size() == 1)
      {
        localObject1 = paramCollection.iterator().next();
        if ((localObject1 instanceof String)) {
          this.preserving = new WildCard((String)localObject1);
        } else {
          this.preserving = ((IdentifierMatcher)localObject1);
        }
      }
      else
      {
        localObject1 = new IdentifierMatcher[paramCollection.size()];
        i = 0;
        localIterator = paramCollection.iterator();
        while (localIterator.hasNext())
        {
          localObject3 = localIterator.next();
          localObject1[(i++)] = ((localObject3 instanceof String) ? new WildCard((String)localObject3) : (IdentifierMatcher)localObject3);
        }
        this.preserving = new MultiIdentifierMatcher(MultiIdentifierMatcher.OR, (IdentifierMatcher[])localObject1);
      }
      return;
    }
    if (paramString.equals("reach")) {
      if (paramCollection.size() == 1)
      {
        localObject1 = paramCollection.iterator().next();
        if ((localObject1 instanceof String)) {
          this.reaching = new WildCard((String)localObject1);
        } else {
          this.reaching = ((IdentifierMatcher)localObject1);
        }
      }
      else
      {
        localObject1 = new IdentifierMatcher[paramCollection.size()];
        i = 0;
        localIterator = paramCollection.iterator();
        while (localIterator.hasNext())
        {
          localObject3 = localIterator.next();
          localObject1[(i++)] = ((localObject3 instanceof String) ? new WildCard((String)localObject3) : (IdentifierMatcher)localObject3);
        }
        this.reaching = new MultiIdentifierMatcher(MultiIdentifierMatcher.OR, (IdentifierMatcher[])localObject1);
      }
    }
    if (paramString.equals("pre"))
    {
      this.preTrafos = ((CodeTransformer[])paramCollection.toArray(new CodeTransformer[paramCollection.size()]));
      return;
    }
    if (paramString.equals("analyzer"))
    {
      if (paramCollection.size() != 1) {
        throw new IllegalArgumentException("Only one analyzer is allowed");
      }
      this.analyzer = ((CodeAnalyzer)paramCollection.iterator().next());
      return;
    }
    if (paramString.equals("post"))
    {
      this.postTrafos = ((CodeTransformer[])paramCollection.toArray(new CodeTransformer[paramCollection.size()]));
      return;
    }
    if (paramString.equals("renamer"))
    {
      if (paramCollection.size() != 1) {
        throw new IllegalArgumentException("Only one renamer allowed");
      }
      this.renamer = ((Renamer)paramCollection.iterator().next());
      return;
    }
    throw new IllegalArgumentException("Invalid option `" + paramString + "'.");
  }
  
  public Reference getReferenceAlias(Reference paramReference)
  {
    Reference localReference = (Reference)aliasesHash.get(paramReference);
    if (localReference == null)
    {
      Identifier localIdentifier = getIdentifier(paramReference);
      String str = getTypeAlias(paramReference.getType());
      if (localIdentifier == null) {
        localReference = Reference.getReference(paramReference.getClazz(), paramReference.getName(), str);
      } else {
        localReference = Reference.getReference("L" + localIdentifier.getParent().getFullAlias().replace('.', '/') + ';', localIdentifier.getAlias(), str);
      }
      aliasesHash.put(paramReference, localReference);
    }
    return localReference;
  }
  
  public String getClassAlias(String paramString)
  {
    ClassIdentifier localClassIdentifier = getClassIdentifier(paramString);
    if (localClassIdentifier == null) {
      return paramString;
    }
    return localClassIdentifier.getFullAlias();
  }
  
  public String getTypeAlias(String paramString)
  {
    String str1 = (String)aliasesHash.get(paramString);
    if (str1 == null)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      int i = 0;
      int j;
      while ((j = paramString.indexOf('L', i)) != -1)
      {
        localStringBuffer.append(paramString.substring(i, j + 1));
        i = paramString.indexOf(';', j);
        String str2 = getClassAlias(paramString.substring(j + 1, i).replace('/', '.'));
        localStringBuffer.append(str2.replace('.', '/'));
      }
      str1 = paramString.substring(i).intern();
      aliasesHash.put(paramString, str1);
    }
    return str1;
  }
  
  public void addClassIdentifier(Identifier paramIdentifier) {}
  
  public ClassIdentifier getClassIdentifier(String paramString)
  {
    if (clazzCache.containsKey(paramString)) {
      return (ClassIdentifier)clazzCache.get(paramString);
    }
    ClassIdentifier localClassIdentifier = (ClassIdentifier)this.basePackage.getIdentifier(paramString);
    clazzCache.put(paramString, localClassIdentifier);
    return localClassIdentifier;
  }
  
  public Identifier getIdentifier(Reference paramReference)
  {
    if (referenceCache.containsKey(paramReference)) {
      return (Identifier)referenceCache.get(paramReference);
    }
    String str = paramReference.getClazz();
    if (str.charAt(0) == '[') {
      return null;
    }
    ClassIdentifier localClassIdentifier = getClassIdentifier(str.substring(1, str.length() - 1).replace('/', '.'));
    Identifier localIdentifier = localClassIdentifier == null ? null : localClassIdentifier.getIdentifier(paramReference.getName(), paramReference.getType());
    referenceCache.put(paramReference, localIdentifier);
    return localIdentifier;
  }
  
  public void reachableClass(String paramString)
  {
    ClassIdentifier localClassIdentifier = getClassIdentifier(paramString);
    if (localClassIdentifier != null) {
      localClassIdentifier.setReachable();
    }
  }
  
  public void reachableReference(Reference paramReference, boolean paramBoolean)
  {
    String str = paramReference.getClazz();
    if (str.charAt(0) == '[') {
      return;
    }
    ClassIdentifier localClassIdentifier = getClassIdentifier(str.substring(1, str.length() - 1).replace('/', '.'));
    if (localClassIdentifier != null) {
      localClassIdentifier.reachableReference(paramReference, paramBoolean);
    }
  }
  
  public void analyzeIdentifier(Identifier paramIdentifier)
  {
    if (paramIdentifier == null) {
      throw new NullPointerException();
    }
    this.toAnalyze.add(paramIdentifier);
  }
  
  public void analyze()
  {
    while (!this.toAnalyze.isEmpty())
    {
      Identifier localIdentifier = (Identifier)this.toAnalyze.iterator().next();
      this.toAnalyze.remove(localIdentifier);
      localIdentifier.analyze();
    }
  }
  
  public IdentifierMatcher getPreserveRule()
  {
    return this.preserving;
  }
  
  public CodeAnalyzer getCodeAnalyzer()
  {
    return this.analyzer;
  }
  
  public CodeTransformer[] getPreTransformers()
  {
    return this.preTrafos;
  }
  
  public CodeTransformer[] getPostTransformers()
  {
    return this.postTrafos;
  }
  
  public void buildTable(Renamer paramRenamer)
  {
    this.basePackage.buildTable(paramRenamer);
  }
  
  public void readTable()
  {
    try
    {
      TranslationTable localTranslationTable = new TranslationTable();
      FileInputStream localFileInputStream = new FileInputStream(this.inTableFile);
      localTranslationTable.load(localFileInputStream);
      localFileInputStream.close();
      this.basePackage.readTable(localTranslationTable);
    }
    catch (IOException localIOException)
    {
      GlobalOptions.err.println("Can't read rename table " + this.inTableFile);
      localIOException.printStackTrace(GlobalOptions.err);
    }
  }
  
  public void writeTable()
  {
    TranslationTable localTranslationTable = new TranslationTable();
    this.basePackage.writeTable(localTranslationTable, false);
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(this.outTableFile);
      localTranslationTable.store(localFileOutputStream);
      localFileOutputStream.close();
    }
    catch (IOException localIOException)
    {
      GlobalOptions.err.println("Can't write rename table " + this.outTableFile);
      localIOException.printStackTrace(GlobalOptions.err);
    }
  }
  
  public void writeRevTable()
  {
    TranslationTable localTranslationTable = new TranslationTable();
    this.basePackage.writeTable(localTranslationTable, true);
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(this.outRevTableFile);
      localTranslationTable.store(localFileOutputStream);
      localFileOutputStream.close();
    }
    catch (IOException localIOException)
    {
      GlobalOptions.err.println("Can't write rename table " + this.outRevTableFile);
      localIOException.printStackTrace(GlobalOptions.err);
    }
  }
  
  public void doTransformations()
  {
    this.basePackage.doTransformations();
  }
  
  public void storeClasses()
  {
    if ((this.destDir.endsWith(".jar")) || (this.destDir.endsWith(".zip")))
    {
      try
      {
        ZipOutputStream localZipOutputStream = new ZipOutputStream(new FileOutputStream(this.destDir));
        this.basePackage.storeClasses(localZipOutputStream);
        localZipOutputStream.close();
      }
      catch (IOException localIOException)
      {
        GlobalOptions.err.println("Can't write zip file: " + this.destDir);
        localIOException.printStackTrace(GlobalOptions.err);
      }
    }
    else
    {
      File localFile = new File(this.destDir);
      if (!localFile.exists())
      {
        GlobalOptions.err.println("Destination directory " + localFile.getPath() + " doesn't exists.");
        return;
      }
      this.basePackage.storeClasses(new File(this.destDir));
    }
  }
  
  public void run()
  {
    if (this.analyzer == null) {
      this.analyzer = new SimpleAnalyzer();
    }
    if (this.preTrafos == null) {
      this.preTrafos = new CodeTransformer[0];
    }
    if (this.postTrafos == null) {
      this.postTrafos = new CodeTransformer[0];
    }
    if (this.renamer == null) {
      this.renamer = new Renamer()
      {
        public Iterator generateNames(Identifier paramAnonymousIdentifier)
        {
          final String str = paramAnonymousIdentifier.getName();
          new Iterator()
          {
            int last = 0;
            
            public boolean hasNext()
            {
              return true;
            }
            
            public Object next()
            {
              return str + this.last;
            }
            
            public void remove()
            {
              throw new UnsupportedOperationException();
            }
          };
        }
      };
    }
    Runtime localRuntime = Runtime.getRuntime();
    long l1 = localRuntime.freeMemory();
    long l2;
    do
    {
      l2 = l1;
      localRuntime.gc();
      localRuntime.runFinalization();
      l1 = localRuntime.freeMemory();
    } while (l1 < l2);
    System.err.println("used before: " + (localRuntime.totalMemory() - l1));
    GlobalOptions.err.println("Loading and preserving classes");
    long l3 = System.currentTimeMillis();
    this.basePackage.loadMatchingClasses(this.loading);
    this.basePackage.applyPreserveRule(this.preserving);
    System.err.println("Time used: " + (System.currentTimeMillis() - l3));
    GlobalOptions.err.println("Computing reachability");
    l3 = System.currentTimeMillis();
    analyze();
    System.err.println("Time used: " + (System.currentTimeMillis() - l3));
    l1 = localRuntime.freeMemory();
    do
    {
      l2 = l1;
      localRuntime.gc();
      localRuntime.runFinalization();
      l1 = localRuntime.freeMemory();
    } while (l1 < l2);
    System.err.println("used after analyze: " + (localRuntime.totalMemory() - l1));
    GlobalOptions.err.println("Renaming methods");
    l3 = System.currentTimeMillis();
    if (this.inTableFile != null) {
      readTable();
    }
    buildTable(this.renamer);
    if (this.outTableFile != null) {
      writeTable();
    }
    if (this.outRevTableFile != null) {
      writeRevTable();
    }
    System.err.println("Time used: " + (System.currentTimeMillis() - l3));
    GlobalOptions.err.println("Transforming the classes");
    l3 = System.currentTimeMillis();
    doTransformations();
    System.err.println("Time used: " + (System.currentTimeMillis() - l3));
    l1 = localRuntime.freeMemory();
    do
    {
      l2 = l1;
      localRuntime.gc();
      localRuntime.runFinalization();
      l1 = localRuntime.freeMemory();
    } while (l1 < l2);
    System.err.println("used after transform: " + (localRuntime.totalMemory() - l1));
    GlobalOptions.err.println("Writing new classes");
    l3 = System.currentTimeMillis();
    storeClasses();
    System.err.println("Time used: " + (System.currentTimeMillis() - l3));
  }
}


