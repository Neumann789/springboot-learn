package jode.decompiler;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Set;
import java.util.Vector;
import jode.AssertError;
import jode.GlobalOptions;
import jode.bytecode.ClassInfo;
import jode.bytecode.FieldInfo;
import jode.bytecode.InnerClassInfo;
import jode.bytecode.MethodInfo;
import jode.expr.Expression;
import jode.expr.ThisOperator;
import jode.flow.StructuredBlock;
import jode.flow.TransformConstructors;
import jode.type.MethodType;
import jode.type.Type;
import jode.util.SimpleSet;

public class ClassAnalyzer
  implements Scope, Declarable, ClassDeclarer
{
  ImportHandler imports;
  ClassInfo clazz;
  ClassDeclarer parent;
  ProgressListener progressListener;
  private static double INITIALIZE_COMPLEXITY = 0.03D;
  private static double STEP_COMPLEXITY = 0.03D;
  private static int STRICTFP = 2048;
  double methodComplexity = 0.0D;
  double innerComplexity = 0.0D;
  String name;
  StructuredBlock[] blockInitializers;
  FieldAnalyzer[] fields;
  MethodAnalyzer[] methods;
  ClassAnalyzer[] inners;
  int modifiers;
  TransformConstructors constrAna;
  MethodAnalyzer staticConstructor;
  MethodAnalyzer[] constructors;
  OuterValues outerValues;
  static int serialnr = 0;
  
  public ClassAnalyzer(ClassDeclarer paramClassDeclarer, ClassInfo paramClassInfo, ImportHandler paramImportHandler, Expression[] paramArrayOfExpression)
  {
    paramClassInfo.loadInfo(127);
    this.parent = paramClassDeclarer;
    this.clazz = paramClassInfo;
    this.imports = paramImportHandler;
    if (paramArrayOfExpression != null) {
      this.outerValues = new OuterValues(this, paramArrayOfExpression);
    }
    this.modifiers = paramClassInfo.getModifiers();
    if (paramClassDeclarer != null)
    {
      InnerClassInfo[] arrayOfInnerClassInfo = paramClassInfo.getOuterClasses();
      if ((arrayOfInnerClassInfo[0].outer == null) || (arrayOfInnerClassInfo[0].name == null))
      {
        if ((paramClassDeclarer instanceof ClassAnalyzer)) {
          throw new AssertError("ClassInfo Attributes are inconsistent: " + paramClassInfo.getName());
        }
      }
      else if ((!(paramClassDeclarer instanceof ClassAnalyzer)) || (!((ClassAnalyzer)paramClassDeclarer).clazz.getName().equals(arrayOfInnerClassInfo[0].outer)) || (arrayOfInnerClassInfo[0].name == null)) {
        throw new AssertError("ClassInfo Attributes are inconsistent: " + paramClassInfo.getName());
      }
      this.name = arrayOfInnerClassInfo[0].name;
      this.modifiers = arrayOfInnerClassInfo[0].modifiers;
    }
    else
    {
      this.name = paramClassInfo.getName();
      int i = this.name.lastIndexOf('.');
      if (i >= 0) {
        this.name = this.name.substring(i + 1);
      }
    }
  }
  
  public ClassAnalyzer(ClassDeclarer paramClassDeclarer, ClassInfo paramClassInfo, ImportHandler paramImportHandler)
  {
    this(paramClassDeclarer, paramClassInfo, paramImportHandler, null);
  }
  
  public ClassAnalyzer(ClassInfo paramClassInfo, ImportHandler paramImportHandler)
  {
    this(null, paramClassInfo, paramImportHandler);
  }
  
  public final boolean isStatic()
  {
    return Modifier.isStatic(this.modifiers);
  }
  
  public final boolean isStrictFP()
  {
    return (this.modifiers & STRICTFP) != 0;
  }
  
  public FieldAnalyzer getField(int paramInt)
  {
    return this.fields[paramInt];
  }
  
  public int getFieldIndex(String paramString, Type paramType)
  {
    for (int i = 0; i < this.fields.length; i++) {
      if ((this.fields[i].getName().equals(paramString)) && (this.fields[i].getType().equals(paramType))) {
        return i;
      }
    }
    return -1;
  }
  
  public MethodAnalyzer getMethod(String paramString, MethodType paramMethodType)
  {
    for (int i = 0; i < this.methods.length; i++) {
      if ((this.methods[i].getName().equals(paramString)) && (this.methods[i].getType().equals(paramMethodType))) {
        return this.methods[i];
      }
    }
    return null;
  }
  
  public int getModifiers()
  {
    return this.modifiers;
  }
  
  public ClassDeclarer getParent()
  {
    return this.parent;
  }
  
  public void setParent(ClassDeclarer paramClassDeclarer)
  {
    this.parent = paramClassDeclarer;
  }
  
  public ClassInfo getClazz()
  {
    return this.clazz;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String paramString)
  {
    this.name = paramString;
  }
  
  public OuterValues getOuterValues()
  {
    return this.outerValues;
  }
  
  public void addBlockInitializer(int paramInt, StructuredBlock paramStructuredBlock)
  {
    if (this.blockInitializers[paramInt] == null) {
      this.blockInitializers[paramInt] = paramStructuredBlock;
    } else {
      this.blockInitializers[paramInt].appendBlock(paramStructuredBlock);
    }
  }
  
  public void initialize()
  {
    FieldInfo[] arrayOfFieldInfo = this.clazz.getFields();
    MethodInfo[] arrayOfMethodInfo = this.clazz.getMethods();
    InnerClassInfo[] arrayOfInnerClassInfo = this.clazz.getInnerClasses();
    if (arrayOfFieldInfo == null) {
      return;
    }
    if (((Options.options & 0x2) != 0) && (arrayOfInnerClassInfo != null))
    {
      Expression[] arrayOfExpression = { new ThisOperator(this.clazz) };
      j = arrayOfInnerClassInfo.length;
      this.inners = new ClassAnalyzer[j];
      for (int k = 0; k < j; k++)
      {
        ClassInfo localClassInfo = ClassInfo.forName(arrayOfInnerClassInfo[k].inner);
        this.inners[k] = new ClassAnalyzer(this, localClassInfo, this.imports, Modifier.isStatic(arrayOfInnerClassInfo[k].modifiers) ? null : arrayOfExpression);
      }
    }
    else
    {
      this.inners = new ClassAnalyzer[0];
    }
    this.fields = new FieldAnalyzer[arrayOfFieldInfo.length];
    this.methods = new MethodAnalyzer[arrayOfMethodInfo.length];
    this.blockInitializers = new StructuredBlock[arrayOfFieldInfo.length + 1];
    for (int i = 0; i < arrayOfFieldInfo.length; i++) {
      this.fields[i] = new FieldAnalyzer(this, arrayOfFieldInfo[i], this.imports);
    }
    this.staticConstructor = null;
    Vector localVector = new Vector();
    for (int j = 0; j < this.methods.length; j++)
    {
      this.methods[j] = new MethodAnalyzer(this, arrayOfMethodInfo[j], this.imports);
      if (this.methods[j].isConstructor())
      {
        if (this.methods[j].isStatic()) {
          this.staticConstructor = this.methods[j];
        } else {
          localVector.addElement(this.methods[j]);
        }
        if (this.methods[j].isStrictFP()) {
          this.modifiers |= STRICTFP;
        }
      }
      this.methodComplexity += this.methods[j].getComplexity();
    }
    this.constructors = new MethodAnalyzer[localVector.size()];
    localVector.copyInto(this.constructors);
    for (j = 0; j < this.inners.length; j++)
    {
      this.inners[j].initialize();
      this.innerComplexity += this.inners[j].getComplexity();
    }
  }
  
  public double getComplexity()
  {
    return this.methodComplexity + this.innerComplexity;
  }
  
  public void analyze(ProgressListener paramProgressListener, double paramDouble1, double paramDouble2)
  {
    if (GlobalOptions.verboseLevel > 0) {
      GlobalOptions.err.println("Class " + this.name);
    }
    double d1 = paramDouble2 / this.methodComplexity;
    if (paramProgressListener != null) {
      paramProgressListener.updateProgress(paramDouble1, this.name);
    }
    this.imports.useClass(this.clazz);
    if (this.clazz.getSuperclass() != null) {
      this.imports.useClass(this.clazz.getSuperclass());
    }
    ClassInfo[] arrayOfClassInfo = this.clazz.getInterfaces();
    for (int i = 0; i < arrayOfClassInfo.length; i++) {
      this.imports.useClass(arrayOfClassInfo[i]);
    }
    if (this.fields == null) {
      return;
    }
    this.constrAna = null;
    double d3;
    if (this.constructors.length > 0)
    {
      for (i = 0; i < this.constructors.length; i++) {
        if (paramProgressListener != null)
        {
          d3 = this.constructors[i].getComplexity() * d1;
          if (d3 > STEP_COMPLEXITY)
          {
            this.constructors[i].analyze(paramProgressListener, paramDouble1, d3);
          }
          else
          {
            paramProgressListener.updateProgress(paramDouble1, this.name);
            this.constructors[i].analyze(null, 0.0D, 0.0D);
          }
          paramDouble1 += d3;
        }
        else
        {
          this.constructors[i].analyze(null, 0.0D, 0.0D);
        }
      }
      this.constrAna = new TransformConstructors(this, false, this.constructors);
      this.constrAna.removeSynthInitializers();
    }
    if (this.staticConstructor != null) {
      if (paramProgressListener != null)
      {
        double d2 = this.staticConstructor.getComplexity() * d1;
        if (d2 > STEP_COMPLEXITY)
        {
          this.staticConstructor.analyze(paramProgressListener, paramDouble1, d2);
        }
        else
        {
          paramProgressListener.updateProgress(paramDouble1, this.name);
          this.staticConstructor.analyze(null, 0.0D, 0.0D);
        }
        paramDouble1 += d2;
      }
      else
      {
        this.staticConstructor.analyze(null, 0.0D, 0.0D);
      }
    }
    if ((Options.options & 0x80) != 0) {
      return;
    }
    for (int j = 0; j < this.fields.length; j++) {
      this.fields[j].analyze();
    }
    for (j = 0; j < this.methods.length; j++) {
      if (!this.methods[j].isConstructor()) {
        if (paramProgressListener != null)
        {
          d3 = this.methods[j].getComplexity() * d1;
          if (d3 > STEP_COMPLEXITY)
          {
            this.methods[j].analyze(paramProgressListener, paramDouble1, d3);
          }
          else
          {
            paramProgressListener.updateProgress(paramDouble1, this.methods[j].getName());
            this.methods[j].analyze(null, 0.0D, 0.0D);
          }
          paramDouble1 += d3;
        }
        else
        {
          this.methods[j].analyze(null, 0.0D, 0.0D);
        }
      }
    }
  }
  
  public void analyzeInnerClasses(ProgressListener paramProgressListener, double paramDouble1, double paramDouble2)
  {
    double d1 = paramDouble2 / this.innerComplexity;
    if ((Options.options & 0x80) != 0) {
      return;
    }
    for (int i = 0; i < this.inners.length; i++) {
      if (paramProgressListener != null)
      {
        double d2 = this.inners[i].getComplexity() * d1;
        if (d2 > STEP_COMPLEXITY)
        {
          double d3 = d1 * this.inners[i].methodComplexity;
          this.inners[i].analyze(paramProgressListener, paramDouble1, d3);
          this.inners[i].analyzeInnerClasses(null, paramDouble1 + d3, d2 - d3);
        }
        else
        {
          paramProgressListener.updateProgress(paramDouble1, this.inners[i].name);
          this.inners[i].analyze(null, 0.0D, 0.0D);
          this.inners[i].analyzeInnerClasses(null, 0.0D, 0.0D);
        }
        paramDouble1 += d2;
      }
      else
      {
        this.inners[i].analyze(null, 0.0D, 0.0D);
        this.inners[i].analyzeInnerClasses(null, 0.0D, 0.0D);
      }
    }
    for (i = 0; i < this.methods.length; i++) {
      this.methods[i].analyzeInnerClasses();
    }
  }
  
  public void makeDeclaration(Set paramSet)
  {
    if (this.constrAna != null) {
      this.constrAna.transform();
    }
    if (this.staticConstructor != null) {
      new TransformConstructors(this, true, new MethodAnalyzer[] { this.staticConstructor }).transform();
    }
    if ((Options.options & 0x80) != 0) {
      return;
    }
    for (int i = 0; i < this.fields.length; i++) {
      this.fields[i].makeDeclaration(paramSet);
    }
    for (i = 0; i < this.inners.length; i++) {
      this.inners[i].makeDeclaration(paramSet);
    }
    for (i = 0; i < this.methods.length; i++) {
      this.methods[i].makeDeclaration(paramSet);
    }
  }
  
  public void dumpDeclaration(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    dumpDeclaration(paramTabbedPrintWriter, null, 0.0D, 0.0D);
  }
  
  public void dumpDeclaration(TabbedPrintWriter paramTabbedPrintWriter, ProgressListener paramProgressListener, double paramDouble1, double paramDouble2)
    throws IOException
  {
    if (this.fields == null) {
      return;
    }
    paramTabbedPrintWriter.startOp(1, 0);
    int i = this.modifiers & ((0x20 | STRICTFP) ^ 0xFFFFFFFF);
    if (this.clazz.isInterface()) {
      i &= 0xFBFF;
    }
    if ((this.parent instanceof MethodAnalyzer))
    {
      i &= 0xFFFFFFFD;
      if (this.name == null) {
        i &= 0xFFFFFFEF;
      }
    }
    String str = Modifier.toString(i);
    if (str.length() > 0) {
      paramTabbedPrintWriter.print(str + " ");
    }
    if (isStrictFP()) {
      paramTabbedPrintWriter.print("strictfp ");
    }
    if (!this.clazz.isInterface()) {
      paramTabbedPrintWriter.print("class ");
    }
    paramTabbedPrintWriter.print(this.name);
    ClassInfo localClassInfo = this.clazz.getSuperclass();
    if ((localClassInfo != null) && (localClassInfo != ClassInfo.javaLangObject))
    {
      paramTabbedPrintWriter.breakOp();
      paramTabbedPrintWriter.print(" extends " + paramTabbedPrintWriter.getClassString(localClassInfo, 1));
    }
    ClassInfo[] arrayOfClassInfo = this.clazz.getInterfaces();
    if (arrayOfClassInfo.length > 0)
    {
      paramTabbedPrintWriter.breakOp();
      paramTabbedPrintWriter.print(this.clazz.isInterface() ? " extends " : " implements ");
      paramTabbedPrintWriter.startOp(0, 1);
      for (int j = 0; j < arrayOfClassInfo.length; j++)
      {
        if (j > 0)
        {
          paramTabbedPrintWriter.print(", ");
          paramTabbedPrintWriter.breakOp();
        }
        paramTabbedPrintWriter.print(paramTabbedPrintWriter.getClassString(arrayOfClassInfo[j], 1));
      }
      paramTabbedPrintWriter.endOp();
    }
    paramTabbedPrintWriter.println();
    paramTabbedPrintWriter.openBraceClass();
    paramTabbedPrintWriter.tab();
    dumpBlock(paramTabbedPrintWriter, paramProgressListener, paramDouble1, paramDouble2);
    paramTabbedPrintWriter.untab();
    paramTabbedPrintWriter.closeBraceClass();
  }
  
  public void dumpBlock(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    dumpBlock(paramTabbedPrintWriter, null, 0.0D, 0.0D);
  }
  
  public void dumpBlock(TabbedPrintWriter paramTabbedPrintWriter, ProgressListener paramProgressListener, double paramDouble1, double paramDouble2)
    throws IOException
  {
    double d1 = paramDouble2 / getComplexity();
    paramTabbedPrintWriter.pushScope(this);
    int i = 0;
    int j = 0;
    SimpleSet localSimpleSet = null;
    if ((Options.options & 0x80) != 0) {
      localSimpleSet = new SimpleSet();
    }
    for (int k = 0; k < this.fields.length; k++)
    {
      if (this.blockInitializers[k] != null)
      {
        if (j != 0) {
          paramTabbedPrintWriter.println();
        }
        paramTabbedPrintWriter.openBrace();
        paramTabbedPrintWriter.tab();
        this.blockInitializers[k].dumpSource(paramTabbedPrintWriter);
        paramTabbedPrintWriter.untab();
        paramTabbedPrintWriter.closeBrace();
        i = j = 1;
      }
      if ((Options.options & 0x80) != 0)
      {
        this.fields[k].analyze();
        this.fields[k].makeDeclaration(localSimpleSet);
      }
      if (!this.fields[k].skipWriting())
      {
        if (i != 0) {
          paramTabbedPrintWriter.println();
        }
        this.fields[k].dumpSource(paramTabbedPrintWriter);
        j = 1;
      }
    }
    if (this.blockInitializers[this.fields.length] != null)
    {
      if (j != 0) {
        paramTabbedPrintWriter.println();
      }
      paramTabbedPrintWriter.openBrace();
      paramTabbedPrintWriter.tab();
      this.blockInitializers[this.fields.length].dumpSource(paramTabbedPrintWriter);
      paramTabbedPrintWriter.untab();
      paramTabbedPrintWriter.closeBrace();
      j = 1;
    }
    double d2;
    for (k = 0; k < this.inners.length; k++)
    {
      if (j != 0) {
        paramTabbedPrintWriter.println();
      }
      if ((Options.options & 0x80) != 0)
      {
        this.inners[k].analyze(null, 0.0D, 0.0D);
        this.inners[k].analyzeInnerClasses(null, 0.0D, 0.0D);
        this.inners[k].makeDeclaration(localSimpleSet);
      }
      if (paramProgressListener != null)
      {
        d2 = this.inners[k].getComplexity() * d1;
        if (d2 > STEP_COMPLEXITY)
        {
          this.inners[k].dumpSource(paramTabbedPrintWriter, paramProgressListener, paramDouble1, d2);
        }
        else
        {
          paramProgressListener.updateProgress(paramDouble1, this.name);
          this.inners[k].dumpSource(paramTabbedPrintWriter);
        }
        paramDouble1 += d2;
      }
      else
      {
        this.inners[k].dumpSource(paramTabbedPrintWriter);
      }
      j = 1;
    }
    for (k = 0; k < this.methods.length; k++)
    {
      if ((Options.options & 0x80) != 0)
      {
        if (!this.methods[k].isConstructor()) {
          this.methods[k].analyze(null, 0.0D, 0.0D);
        }
        this.methods[k].analyzeInnerClasses();
        this.methods[k].makeDeclaration(localSimpleSet);
      }
      if (!this.methods[k].skipWriting())
      {
        if (j != 0) {
          paramTabbedPrintWriter.println();
        }
        if (paramProgressListener != null)
        {
          d2 = this.methods[k].getComplexity() * d1;
          paramProgressListener.updateProgress(paramDouble1, this.methods[k].getName());
          this.methods[k].dumpSource(paramTabbedPrintWriter);
          paramDouble1 += d2;
        }
        else
        {
          this.methods[k].dumpSource(paramTabbedPrintWriter);
        }
        j = 1;
      }
    }
    paramTabbedPrintWriter.popScope();
    this.clazz.dropInfo(0x10 | 0x80);
  }
  
  public void dumpSource(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    dumpSource(paramTabbedPrintWriter, null, 0.0D, 0.0D);
  }
  
  public void dumpSource(TabbedPrintWriter paramTabbedPrintWriter, ProgressListener paramProgressListener, double paramDouble1, double paramDouble2)
    throws IOException
  {
    dumpDeclaration(paramTabbedPrintWriter, paramProgressListener, paramDouble1, paramDouble2);
    paramTabbedPrintWriter.println();
  }
  
  public void dumpJavaFile(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    dumpJavaFile(paramTabbedPrintWriter, null);
  }
  
  public void dumpJavaFile(TabbedPrintWriter paramTabbedPrintWriter, ProgressListener paramProgressListener)
    throws IOException
  {
    this.imports.init(this.clazz.getName());
    LocalInfo.init();
    initialize();
    double d1 = 0.05D;
    double d2 = 0.75D * this.methodComplexity / (this.methodComplexity + this.innerComplexity);
    analyze(paramProgressListener, INITIALIZE_COMPLEXITY, d2);
    d1 += d2;
    analyzeInnerClasses(paramProgressListener, d1, 0.8D - d1);
    makeDeclaration(new SimpleSet());
    this.imports.dumpHeader(paramTabbedPrintWriter);
    dumpSource(paramTabbedPrintWriter, paramProgressListener, 0.8D, 0.2D);
    if (paramProgressListener != null) {
      paramProgressListener.updateProgress(1.0D, this.name);
    }
  }
  
  public boolean isScopeOf(Object paramObject, int paramInt)
  {
    return (this.clazz.equals(paramObject)) && (paramInt == 1);
  }
  
  public void makeNameUnique()
  {
    this.name = (this.name + "_" + serialnr++ + "_");
  }
  
  public boolean conflicts(String paramString, int paramInt)
  {
    return conflicts(this.clazz, paramString, paramInt);
  }
  
  private static boolean conflicts(ClassInfo paramClassInfo, String paramString, int paramInt)
  {
    while (paramClassInfo != null)
    {
      if ((paramInt == 12) || (paramInt == 2))
      {
        localObject = paramClassInfo.getMethods();
        for (i = 0; i < localObject.length; i++) {
          if (localObject[i].getName().equals(paramString)) {
            return true;
          }
        }
      }
      if ((paramInt == 13) || (paramInt == 3) || (paramInt == 4))
      {
        localObject = paramClassInfo.getFields();
        for (i = 0; i < localObject.length; i++) {
          if (localObject[i].getName().equals(paramString)) {
            return true;
          }
        }
      }
      if ((paramInt == 1) || (paramInt == 4))
      {
        localObject = paramClassInfo.getInnerClasses();
        if (localObject != null) {
          for (i = 0; i < localObject.length; i++) {
            if (localObject[i].name.equals(paramString)) {
              return true;
            }
          }
        }
      }
      if ((paramInt == 13) || (paramInt == 12)) {
        return false;
      }
      Object localObject = paramClassInfo.getInterfaces();
      for (int i = 0; i < localObject.length; i++) {
        if (conflicts(localObject[i], paramString, paramInt)) {
          return true;
        }
      }
      paramClassInfo = paramClassInfo.getSuperclass();
    }
    return false;
  }
  
  public ClassAnalyzer getClassAnalyzer(ClassInfo paramClassInfo)
  {
    if (paramClassInfo == getClazz()) {
      return this;
    }
    if (this.parent == null) {
      return null;
    }
    return getParent().getClassAnalyzer(paramClassInfo);
  }
  
  public ClassAnalyzer getInnerClassAnalyzer(String paramString)
  {
    int i = this.inners.length;
    for (int j = 0; j < i; j++) {
      if (this.inners[j].name.equals(paramString)) {
        return this.inners[j];
      }
    }
    return null;
  }
  
  public void fillDeclarables(Collection paramCollection)
  {
    for (int i = 0; i < this.methods.length; i++) {
      this.methods[i].fillDeclarables(paramCollection);
    }
  }
  
  public void addClassAnalyzer(ClassAnalyzer paramClassAnalyzer)
  {
    if (this.parent != null) {
      this.parent.addClassAnalyzer(paramClassAnalyzer);
    }
  }
  
  public String toString()
  {
    return getClass().getName() + "[" + getClazz() + "]";
  }
}


