package jode.bytecode;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Iterator;
import jode.GlobalOptions;
import jode.util.UnifyHash;

public class ClassInfo
  extends BinaryInfo
{
  private static SearchPath classpath;
  private static final UnifyHash classes = new UnifyHash();
  private int status = 0;
  private boolean modified = false;
  private int modifiers = -1;
  private boolean deprecatedFlag;
  private String name;
  private ClassInfo superclass;
  private ClassInfo[] interfaces;
  private FieldInfo[] fields;
  private MethodInfo[] methods;
  private InnerClassInfo[] outerClasses;
  private InnerClassInfo[] innerClasses;
  private InnerClassInfo[] extraClasses;
  private String sourceFile;
  public static final ClassInfo javaLangObject = forName("java.lang.Object");
  
  public static void setClassPath(String paramString)
  {
    setClassPath(new SearchPath(paramString));
  }
  
  public static void setClassPath(SearchPath paramSearchPath)
  {
    if (classpath != paramSearchPath)
    {
      classpath = paramSearchPath;
      Iterator localIterator = classes.iterator();
      while (localIterator.hasNext())
      {
        ClassInfo localClassInfo = (ClassInfo)localIterator.next();
        localClassInfo.status = 0;
        localClassInfo.superclass = null;
        localClassInfo.fields = null;
        localClassInfo.interfaces = null;
        localClassInfo.methods = null;
        localClassInfo.removeAllAttributes();
      }
    }
  }
  
  public static boolean exists(String paramString)
  {
    return classpath.exists(paramString.replace('.', '/') + ".class");
  }
  
  public static boolean isPackage(String paramString)
  {
    return classpath.isDirectory(paramString.replace('.', '/'));
  }
  
  public static Enumeration getClassesAndPackages(String paramString)
  {
    Enumeration localEnumeration = classpath.listFiles(paramString.replace('.', '/'));
    new Enumeration()
    {
      public boolean hasMoreElements()
      {
        return this.val$enume.hasMoreElements();
      }
      
      public Object nextElement()
      {
        String str = (String)this.val$enume.nextElement();
        if (!str.endsWith(".class")) {
          return str;
        }
        return str.substring(0, str.length() - 6);
      }
    };
  }
  
  public static ClassInfo forName(String paramString)
  {
    if ((paramString == null) || (paramString.indexOf(';') != -1) || (paramString.indexOf('[') != -1) || (paramString.indexOf('/') != -1)) {
      throw new IllegalArgumentException("Illegal class name: " + paramString);
    }
    int i = paramString.hashCode();
    Iterator localIterator = classes.iterateHashCode(i);
    while (localIterator.hasNext())
    {
      localClassInfo = (ClassInfo)localIterator.next();
      if (localClassInfo.name.equals(paramString)) {
        return localClassInfo;
      }
    }
    ClassInfo localClassInfo = new ClassInfo(paramString);
    classes.put(i, localClassInfo);
    return localClassInfo;
  }
  
  private ClassInfo(String paramString)
  {
    this.name = paramString;
  }
  
  protected void readAttribute(String paramString, int paramInt1, ConstantPool paramConstantPool, DataInputStream paramDataInputStream, int paramInt2)
    throws IOException
  {
    if (((paramInt2 & 0x10) != 0) && (paramString.equals("SourceFile")))
    {
      if (paramInt1 != 2) {
        throw new ClassFormatException("SourceFile attribute has wrong length");
      }
      this.sourceFile = paramConstantPool.getUTF8(paramDataInputStream.readUnsignedShort());
    }
    else if (((paramInt2 & 0x60) != 0) && (paramString.equals("InnerClasses")))
    {
      int i = paramDataInputStream.readUnsignedShort();
      if (paramInt1 != 2 + 8 * i) {
        throw new ClassFormatException("InnerClasses attribute has wrong length");
      }
      int j = 0;
      int k = 0;
      int m = 0;
      InnerClassInfo[] arrayOfInnerClassInfo = new InnerClassInfo[i];
      for (int n = 0; n < i; n++)
      {
        i2 = paramDataInputStream.readUnsignedShort();
        int i3 = paramDataInputStream.readUnsignedShort();
        int i5 = paramDataInputStream.readUnsignedShort();
        String str3 = paramConstantPool.getClassName(i2);
        String str4 = i3 != 0 ? paramConstantPool.getClassName(i3) : null;
        String str5 = i5 != 0 ? paramConstantPool.getUTF8(i5) : null;
        int i6 = paramDataInputStream.readUnsignedShort();
        if ((str5 != null) && (str5.length() == 0)) {
          str5 = null;
        }
        if ((str4 != null) && (str5 != null) && (str3.length() > str4.length() + 2 + str5.length()) && (str3.startsWith(str4 + "$")) && (str3.endsWith("$" + str5)) && (Character.isDigit(str3.charAt(str4.length() + 1)))) {
          str4 = null;
        }
        InnerClassInfo localInnerClassInfo3 = new InnerClassInfo(str3, str4, str5, i6);
        if ((str4 != null) && (str4.equals(getName())) && (str5 != null)) {
          arrayOfInnerClassInfo[(j++)] = localInnerClassInfo3;
        } else {
          arrayOfInnerClassInfo[(i - ++m)] = localInnerClassInfo3;
        }
      }
      String str1 = getName();
      for (int i2 = i - m; (i2 < i) && (str1 != null); i2++)
      {
        InnerClassInfo localInnerClassInfo1 = arrayOfInnerClassInfo[i2];
        if (localInnerClassInfo1.inner.equals(str1))
        {
          k++;
          m--;
          str1 = localInnerClassInfo1.outer;
        }
      }
      if (j > 0)
      {
        this.innerClasses = new InnerClassInfo[j];
        System.arraycopy(arrayOfInnerClassInfo, 0, this.innerClasses, 0, j);
      }
      else
      {
        this.innerClasses = null;
      }
      if (k > 0) {
        this.outerClasses = new InnerClassInfo[k];
      } else {
        this.outerClasses = null;
      }
      if (m > 0) {
        this.extraClasses = new InnerClassInfo[m];
      } else {
        this.extraClasses = null;
      }
      int i1 = 0;
      String str2 = getName();
      for (int i4 = i - m - k; i4 < i; i4++)
      {
        InnerClassInfo localInnerClassInfo2 = arrayOfInnerClassInfo[i4];
        if (localInnerClassInfo2.inner.equals(str2))
        {
          this.outerClasses[(i1++)] = localInnerClassInfo2;
          str2 = localInnerClassInfo2.outer;
        }
        else
        {
          this.extraClasses[(--m)] = localInnerClassInfo2;
        }
      }
    }
    else if (paramString.equals("Deprecated"))
    {
      this.deprecatedFlag = true;
      if (paramInt1 != 0) {
        throw new ClassFormatException("Deprecated attribute has wrong length");
      }
    }
    else
    {
      super.readAttribute(paramString, paramInt1, paramConstantPool, paramDataInputStream, paramInt2);
    }
  }
  
  public void read(DataInputStream paramDataInputStream, int paramInt)
    throws IOException
  {
    paramInt |= 0x61;
    paramInt &= (this.status ^ 0xFFFFFFFF);
    if (paramDataInputStream.readInt() != -889275714) {
      throw new ClassFormatException("Wrong magic");
    }
    int i = paramDataInputStream.readUnsignedShort();
    i |= paramDataInputStream.readUnsignedShort() << 16;
    if (i < 2949120) {
      throw new ClassFormatException("Wrong class version");
    }
    ConstantPool localConstantPool = new ConstantPool();
    localConstantPool.read(paramDataInputStream);
    this.modifiers = paramDataInputStream.readUnsignedShort();
    String str1 = localConstantPool.getClassName(paramDataInputStream.readUnsignedShort());
    if (!this.name.equals(str1)) {
      throw new ClassFormatException("wrong name " + str1);
    }
    String str2 = localConstantPool.getClassName(paramDataInputStream.readUnsignedShort());
    this.superclass = (str2 != null ? forName(str2) : null);
    int n = paramDataInputStream.readUnsignedShort();
    this.interfaces = new ClassInfo[n];
    for (int i1 = 0; i1 < n; i1++) {
      this.interfaces[i1] = forName(localConstantPool.getClassName(paramDataInputStream.readUnsignedShort()));
    }
    this.status |= 0x1;
    int m;
    if ((paramInt & 0x92) != 0)
    {
      int j = paramDataInputStream.readUnsignedShort();
      if ((this.status & 0x2) == 0) {
        this.fields = new FieldInfo[j];
      }
      for (m = 0; m < j; m++)
      {
        if ((this.status & 0x2) == 0) {
          this.fields[m] = new FieldInfo(this);
        }
        this.fields[m].read(localConstantPool, paramDataInputStream, paramInt);
      }
    }
    else
    {
      byte[] arrayOfByte1 = new byte[6];
      m = paramDataInputStream.readUnsignedShort();
      for (n = 0; n < m; n++)
      {
        paramDataInputStream.readFully(arrayOfByte1);
        skipAttributes(paramDataInputStream);
      }
    }
    if ((paramInt & 0x94) != 0)
    {
      int k = paramDataInputStream.readUnsignedShort();
      if ((this.status & 0x4) == 0) {
        this.methods = new MethodInfo[k];
      }
      for (m = 0; m < k; m++)
      {
        if ((this.status & 0x4) == 0) {
          this.methods[m] = new MethodInfo(this);
        }
        this.methods[m].read(localConstantPool, paramDataInputStream, paramInt);
      }
    }
    else
    {
      byte[] arrayOfByte2 = new byte[6];
      m = paramDataInputStream.readUnsignedShort();
      for (n = 0; n < m; n++)
      {
        paramDataInputStream.readFully(arrayOfByte2);
        skipAttributes(paramDataInputStream);
      }
    }
    readAttributes(localConstantPool, paramDataInputStream, paramInt);
    this.status |= paramInt;
  }
  
  public void reserveSmallConstants(GrowableConstantPool paramGrowableConstantPool)
  {
    for (int i = 0; i < this.fields.length; i++) {
      this.fields[i].reserveSmallConstants(paramGrowableConstantPool);
    }
    for (i = 0; i < this.methods.length; i++) {
      this.methods[i].reserveSmallConstants(paramGrowableConstantPool);
    }
  }
  
  public void prepareWriting(GrowableConstantPool paramGrowableConstantPool)
  {
    paramGrowableConstantPool.putClassName(this.name);
    paramGrowableConstantPool.putClassName(this.superclass.getName());
    for (int i = 0; i < this.interfaces.length; i++) {
      paramGrowableConstantPool.putClassName(this.interfaces[i].getName());
    }
    for (i = 0; i < this.fields.length; i++) {
      this.fields[i].prepareWriting(paramGrowableConstantPool);
    }
    for (i = 0; i < this.methods.length; i++) {
      this.methods[i].prepareWriting(paramGrowableConstantPool);
    }
    if (this.sourceFile != null)
    {
      paramGrowableConstantPool.putUTF8("SourceFile");
      paramGrowableConstantPool.putUTF8(this.sourceFile);
    }
    if ((this.outerClasses != null) || (this.innerClasses != null) || (this.extraClasses != null))
    {
      paramGrowableConstantPool.putUTF8("InnerClasses");
      i = this.outerClasses != null ? this.outerClasses.length : 0;
      int j = i;
      while (j-- > 0)
      {
        paramGrowableConstantPool.putClassName(this.outerClasses[j].inner);
        if (this.outerClasses[j].outer != null) {
          paramGrowableConstantPool.putClassName(this.outerClasses[j].outer);
        }
        if (this.outerClasses[j].name != null) {
          paramGrowableConstantPool.putUTF8(this.outerClasses[j].name);
        }
      }
      j = this.innerClasses != null ? this.innerClasses.length : 0;
      for (int k = 0; k < j; k++)
      {
        paramGrowableConstantPool.putClassName(this.innerClasses[k].inner);
        if (this.innerClasses[k].outer != null) {
          paramGrowableConstantPool.putClassName(this.innerClasses[k].outer);
        }
        if (this.innerClasses[k].name != null) {
          paramGrowableConstantPool.putUTF8(this.innerClasses[k].name);
        }
      }
      k = this.extraClasses != null ? this.extraClasses.length : 0;
      for (int m = 0; m < k; m++)
      {
        paramGrowableConstantPool.putClassName(this.extraClasses[m].inner);
        if (this.extraClasses[m].outer != null) {
          paramGrowableConstantPool.putClassName(this.extraClasses[m].outer);
        }
        if (this.extraClasses[m].name != null) {
          paramGrowableConstantPool.putUTF8(this.extraClasses[m].name);
        }
      }
    }
    if (this.deprecatedFlag) {
      paramGrowableConstantPool.putUTF8("Deprecated");
    }
    prepareAttributes(paramGrowableConstantPool);
  }
  
  protected int getKnownAttributeCount()
  {
    int i = 0;
    if (this.sourceFile != null) {
      i++;
    }
    if ((this.innerClasses != null) || (this.outerClasses != null) || (this.extraClasses != null)) {
      i++;
    }
    return i;
  }
  
  public void writeKnownAttributes(GrowableConstantPool paramGrowableConstantPool, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    if (this.sourceFile != null)
    {
      paramDataOutputStream.writeShort(paramGrowableConstantPool.putUTF8("SourceFile"));
      paramDataOutputStream.writeInt(2);
      paramDataOutputStream.writeShort(paramGrowableConstantPool.putUTF8(this.sourceFile));
    }
    if ((this.outerClasses != null) || (this.innerClasses != null) || (this.extraClasses != null))
    {
      paramDataOutputStream.writeShort(paramGrowableConstantPool.putUTF8("InnerClasses"));
      int i = this.outerClasses != null ? this.outerClasses.length : 0;
      int j = this.innerClasses != null ? this.innerClasses.length : 0;
      int k = this.extraClasses != null ? this.extraClasses.length : 0;
      int m = i + j + k;
      paramDataOutputStream.writeInt(2 + m * 8);
      paramDataOutputStream.writeShort(m);
      int n = i;
      while (n-- > 0)
      {
        paramDataOutputStream.writeShort(paramGrowableConstantPool.putClassName(this.outerClasses[n].inner));
        paramDataOutputStream.writeShort(this.outerClasses[n].outer != null ? paramGrowableConstantPool.putClassName(this.outerClasses[n].outer) : 0);
        paramDataOutputStream.writeShort(this.outerClasses[n].name != null ? paramGrowableConstantPool.putUTF8(this.outerClasses[n].name) : 0);
        paramDataOutputStream.writeShort(this.outerClasses[n].modifiers);
      }
      for (n = 0; n < j; n++)
      {
        paramDataOutputStream.writeShort(paramGrowableConstantPool.putClassName(this.innerClasses[n].inner));
        paramDataOutputStream.writeShort(this.innerClasses[n].outer != null ? paramGrowableConstantPool.putClassName(this.innerClasses[n].outer) : 0);
        paramDataOutputStream.writeShort(this.innerClasses[n].name != null ? paramGrowableConstantPool.putUTF8(this.innerClasses[n].name) : 0);
        paramDataOutputStream.writeShort(this.innerClasses[n].modifiers);
      }
      for (n = 0; n < k; n++)
      {
        paramDataOutputStream.writeShort(paramGrowableConstantPool.putClassName(this.extraClasses[n].inner));
        paramDataOutputStream.writeShort(this.extraClasses[n].outer != null ? paramGrowableConstantPool.putClassName(this.extraClasses[n].outer) : 0);
        paramDataOutputStream.writeShort(this.extraClasses[n].name != null ? paramGrowableConstantPool.putUTF8(this.extraClasses[n].name) : 0);
        paramDataOutputStream.writeShort(this.extraClasses[n].modifiers);
      }
    }
    if (this.deprecatedFlag)
    {
      paramDataOutputStream.writeShort(paramGrowableConstantPool.putUTF8("Deprecated"));
      paramDataOutputStream.writeInt(0);
    }
  }
  
  public void write(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    GrowableConstantPool localGrowableConstantPool = new GrowableConstantPool();
    reserveSmallConstants(localGrowableConstantPool);
    prepareWriting(localGrowableConstantPool);
    paramDataOutputStream.writeInt(-889275714);
    paramDataOutputStream.writeShort(3);
    paramDataOutputStream.writeShort(45);
    localGrowableConstantPool.write(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.modifiers);
    paramDataOutputStream.writeShort(localGrowableConstantPool.putClassName(this.name));
    paramDataOutputStream.writeShort(localGrowableConstantPool.putClassName(this.superclass.getName()));
    paramDataOutputStream.writeShort(this.interfaces.length);
    for (int i = 0; i < this.interfaces.length; i++) {
      paramDataOutputStream.writeShort(localGrowableConstantPool.putClassName(this.interfaces[i].getName()));
    }
    paramDataOutputStream.writeShort(this.fields.length);
    for (i = 0; i < this.fields.length; i++) {
      this.fields[i].write(localGrowableConstantPool, paramDataOutputStream);
    }
    paramDataOutputStream.writeShort(this.methods.length);
    for (i = 0; i < this.methods.length; i++) {
      this.methods[i].write(localGrowableConstantPool, paramDataOutputStream);
    }
    writeAttributes(localGrowableConstantPool, paramDataOutputStream);
  }
  
  public void loadInfoReflection(Class paramClass, int paramInt)
    throws SecurityException
  {
    Object localObject1;
    if ((paramInt & 0x1) != 0)
    {
      this.modifiers = paramClass.getModifiers();
      if (paramClass.getSuperclass() == null) {
        this.superclass = (paramClass == Object.class ? null : javaLangObject);
      } else {
        this.superclass = forName(paramClass.getSuperclass().getName());
      }
      localObject1 = paramClass.getInterfaces();
      this.interfaces = new ClassInfo[localObject1.length];
      for (int j = 0; j < localObject1.length; j++) {
        this.interfaces[j] = forName(localObject1[j].getName());
      }
      this.status |= 0x1;
    }
    if (((paramInt & 0x2) != 0) && (this.fields == null))
    {
      try
      {
        localObject1 = paramClass.getDeclaredFields();
      }
      catch (SecurityException localSecurityException1)
      {
        localObject1 = paramClass.getFields();
        GlobalOptions.err.println("Could only get public fields of class " + this.name + ".");
      }
      this.fields = new FieldInfo[localObject1.length];
      int k = localObject1.length;
      for (;;)
      {
        k--;
        if (k < 0) {
          break;
        }
        String str1 = TypeSignature.getSignature(localObject1[k].getType());
        this.fields[k] = new FieldInfo(this, localObject1[k].getName(), str1, localObject1[k].getModifiers());
      }
    }
    if (((paramInt & 0x4) != 0) && (this.methods == null))
    {
      Method[] arrayOfMethod;
      try
      {
        localObject1 = paramClass.getDeclaredConstructors();
        arrayOfMethod = paramClass.getDeclaredMethods();
      }
      catch (SecurityException localSecurityException3)
      {
        localObject1 = paramClass.getConstructors();
        arrayOfMethod = paramClass.getMethods();
        GlobalOptions.err.println("Could only get public methods of class " + this.name + ".");
      }
      this.methods = new MethodInfo[localObject1.length + arrayOfMethod.length];
      int n = localObject1.length;
      String str2;
      for (;;)
      {
        n--;
        if (n < 0) {
          break;
        }
        str2 = TypeSignature.getSignature(localObject1[n].getParameterTypes(), Void.TYPE);
        this.methods[n] = new MethodInfo(this, "<init>", str2, localObject1[n].getModifiers());
      }
      n = arrayOfMethod.length;
      for (;;)
      {
        n--;
        if (n < 0) {
          break;
        }
        str2 = TypeSignature.getSignature(arrayOfMethod[n].getParameterTypes(), arrayOfMethod[n].getReturnType());
        this.methods[(localObject1.length + n)] = new MethodInfo(this, arrayOfMethod[n].getName(), str2, arrayOfMethod[n].getModifiers());
      }
    }
    Object localObject2;
    int i1;
    String str3;
    if (((paramInt & 0x20) != 0) && (this.innerClasses == null))
    {
      try
      {
        localObject1 = paramClass.getDeclaredClasses();
      }
      catch (SecurityException localSecurityException2)
      {
        localObject1 = paramClass.getClasses();
        GlobalOptions.err.println("Could only get public inner classes of class " + this.name + ".");
      }
      if (localObject1.length > 0)
      {
        this.innerClasses = new InnerClassInfo[localObject1.length];
        int m = localObject1.length;
        for (;;)
        {
          m--;
          if (m < 0) {
            break;
          }
          localObject2 = localObject1[m].getName();
          i1 = ((String)localObject2).lastIndexOf('$');
          str3 = ((String)localObject2).substring(i1 + 1);
          this.innerClasses[m] = new InnerClassInfo((String)localObject2, getName(), str3, localObject1[m].getModifiers());
        }
      }
    }
    if (((paramInt & 0x40) != 0) && (this.outerClasses == null))
    {
      int i = 0;
      for (Class localClass = paramClass.getDeclaringClass(); localClass != null; localClass = localClass.getDeclaringClass()) {
        i++;
      }
      if (i > 0)
      {
        this.outerClasses = new InnerClassInfo[i];
        localObject2 = paramClass;
        for (i1 = 0; i1 < i; i1++)
        {
          localClass = ((Class)localObject2).getDeclaringClass();
          str3 = ((Class)localObject2).getName();
          int i2 = str3.lastIndexOf('$');
          this.outerClasses[i1] = new InnerClassInfo(str3, localClass.getName(), str3.substring(i2 + 1), ((Class)localObject2).getModifiers());
          localObject2 = localClass;
        }
      }
    }
    this.status |= paramInt;
  }
  
  public void loadInfo(int paramInt)
  {
    if ((this.status & paramInt) == paramInt) {
      return;
    }
    if (this.modified)
    {
      System.err.println("Allocating info 0x" + Integer.toHexString(paramInt) + " (status 0x" + Integer.toHexString(this.status) + ") in class " + this);
      Thread.dumpStack();
      return;
    }
    try
    {
      DataInputStream localDataInputStream = new DataInputStream(new BufferedInputStream(classpath.getFile(this.name.replace('.', '/') + ".class")));
      read(localDataInputStream, paramInt);
    }
    catch (IOException localIOException)
    {
      String str = localIOException.getMessage();
      if ((paramInt & 0xFFFFFF98) != 0) {
        throw new NoClassDefFoundError(this.name);
      }
      Class localClass = null;
      try
      {
        localClass = Class.forName(this.name);
      }
      catch (ClassNotFoundException localClassNotFoundException) {}catch (NoClassDefFoundError localNoClassDefFoundError) {}
      try
      {
        if (localClass != null)
        {
          loadInfoReflection(localClass, paramInt);
          return;
        }
      }
      catch (SecurityException localSecurityException)
      {
        GlobalOptions.err.println(localSecurityException + " while collecting info about class " + this.name + ".");
      }
      GlobalOptions.err.println("Can't read class " + this.name + ", types may be incorrect. (" + localIOException.getClass().getName() + (str != null ? ": " + str : "") + ")");
      localIOException.printStackTrace(GlobalOptions.err);
      if ((paramInt & 0x1) != 0)
      {
        this.modifiers = 1;
        if (this.name.equals("java.lang.Object")) {
          this.superclass = null;
        } else {
          this.superclass = javaLangObject;
        }
        this.interfaces = new ClassInfo[0];
      }
      if ((paramInt & 0x4) != 0) {
        this.methods = new MethodInfo[0];
      }
      if ((paramInt & 0x2) != 0) {
        this.fields = new FieldInfo[0];
      }
      this.status |= paramInt;
    }
  }
  
  public void dropInfo(int paramInt)
  {
    if ((this.status & paramInt) == 0) {
      return;
    }
    if (this.modified)
    {
      System.err.println("Dropping info 0x" + Integer.toHexString(paramInt) + " (status 0x" + Integer.toHexString(this.status) + ") in class " + this);
      Thread.dumpStack();
      return;
    }
    paramInt &= this.status;
    int i;
    if ((paramInt & 0x2) != 0) {
      this.fields = null;
    } else if (((this.status & 0x2) != 0) && ((paramInt & 0x90) != 0)) {
      for (i = 0; i < this.fields.length; i++) {
        this.fields[i].dropInfo(paramInt);
      }
    }
    if ((paramInt & 0x4) != 0) {
      this.methods = null;
    } else if (((this.status & 0x4) != 0) && ((paramInt & 0x90) != 0)) {
      for (i = 0; i < this.methods.length; i++) {
        this.methods[i].dropInfo(paramInt);
      }
    }
    if ((paramInt & 0x10) != 0) {
      this.sourceFile = null;
    }
    if ((paramInt & 0x40) != 0) {
      this.outerClasses = null;
    }
    if ((paramInt & 0x20) != 0)
    {
      this.innerClasses = null;
      this.extraClasses = null;
    }
    super.dropInfo(paramInt);
    this.status &= (paramInt ^ 0xFFFFFFFF);
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getJavaName()
  {
    if (this.name.indexOf('$') == -1) {
      return getName();
    }
    if (getOuterClasses() != null)
    {
      int i = this.outerClasses.length - 1;
      StringBuffer localStringBuffer = new StringBuffer(this.outerClasses[i].outer != null ? this.outerClasses[i].outer : "METHOD");
      for (int j = i; j >= 0; j--) {
        localStringBuffer.append(".").append(this.outerClasses[j].name != null ? this.outerClasses[j].name : "ANONYMOUS");
      }
      return localStringBuffer.toString();
    }
    return getName();
  }
  
  public ClassInfo getSuperclass()
  {
    if ((this.status & 0x1) == 0) {
      loadInfo(1);
    }
    return this.superclass;
  }
  
  public ClassInfo[] getInterfaces()
  {
    if ((this.status & 0x1) == 0) {
      loadInfo(1);
    }
    return this.interfaces;
  }
  
  public int getModifiers()
  {
    if ((this.status & 0x1) == 0) {
      loadInfo(1);
    }
    return this.modifiers;
  }
  
  public boolean isInterface()
  {
    return Modifier.isInterface(getModifiers());
  }
  
  public boolean isDeprecated()
  {
    return this.deprecatedFlag;
  }
  
  public FieldInfo findField(String paramString1, String paramString2)
  {
    if ((this.status & 0x2) == 0) {
      loadInfo(2);
    }
    for (int i = 0; i < this.fields.length; i++) {
      if ((this.fields[i].getName().equals(paramString1)) && (this.fields[i].getType().equals(paramString2))) {
        return this.fields[i];
      }
    }
    return null;
  }
  
  public MethodInfo findMethod(String paramString1, String paramString2)
  {
    if ((this.status & 0x4) == 0) {
      loadInfo(4);
    }
    for (int i = 0; i < this.methods.length; i++) {
      if ((this.methods[i].getName().equals(paramString1)) && (this.methods[i].getType().equals(paramString2))) {
        return this.methods[i];
      }
    }
    return null;
  }
  
  public MethodInfo[] getMethods()
  {
    if ((this.status & 0x4) == 0) {
      loadInfo(4);
    }
    return this.methods;
  }
  
  public FieldInfo[] getFields()
  {
    if ((this.status & 0x2) == 0) {
      loadInfo(2);
    }
    return this.fields;
  }
  
  public InnerClassInfo[] getOuterClasses()
  {
    if ((this.status & 0x40) == 0) {
      loadInfo(64);
    }
    return this.outerClasses;
  }
  
  public InnerClassInfo[] getInnerClasses()
  {
    if ((this.status & 0x20) == 0) {
      loadInfo(32);
    }
    return this.innerClasses;
  }
  
  public InnerClassInfo[] getExtraClasses()
  {
    if ((this.status & 0x20) == 0) {
      loadInfo(32);
    }
    return this.extraClasses;
  }
  
  public String getSourceFile()
  {
    return this.sourceFile;
  }
  
  public void setName(String paramString)
  {
    this.name = paramString;
    this.modified = true;
  }
  
  public void setSuperclass(ClassInfo paramClassInfo)
  {
    this.superclass = paramClassInfo;
    this.modified = true;
  }
  
  public void setInterfaces(ClassInfo[] paramArrayOfClassInfo)
  {
    this.interfaces = paramArrayOfClassInfo;
    this.modified = true;
  }
  
  public void setModifiers(int paramInt)
  {
    this.modifiers = paramInt;
    this.modified = true;
  }
  
  public void setDeprecated(boolean paramBoolean)
  {
    this.deprecatedFlag = paramBoolean;
  }
  
  public void setMethods(MethodInfo[] paramArrayOfMethodInfo)
  {
    this.methods = paramArrayOfMethodInfo;
    this.modified = true;
  }
  
  public void setFields(FieldInfo[] paramArrayOfFieldInfo)
  {
    this.fields = paramArrayOfFieldInfo;
    this.modified = true;
  }
  
  public void setOuterClasses(InnerClassInfo[] paramArrayOfInnerClassInfo)
  {
    this.outerClasses = paramArrayOfInnerClassInfo;
    this.modified = true;
  }
  
  public void setInnerClasses(InnerClassInfo[] paramArrayOfInnerClassInfo)
  {
    this.innerClasses = paramArrayOfInnerClassInfo;
    this.modified = true;
  }
  
  public void setExtraClasses(InnerClassInfo[] paramArrayOfInnerClassInfo)
  {
    this.extraClasses = paramArrayOfInnerClassInfo;
    this.modified = true;
  }
  
  public void setSourceFile(String paramString)
  {
    this.sourceFile = paramString;
    this.modified = true;
  }
  
  public boolean superClassOf(ClassInfo paramClassInfo)
  {
    while ((paramClassInfo != this) && (paramClassInfo != null)) {
      paramClassInfo = paramClassInfo.getSuperclass();
    }
    return paramClassInfo == this;
  }
  
  public boolean implementedBy(ClassInfo paramClassInfo)
  {
    while ((paramClassInfo != this) && (paramClassInfo != null))
    {
      ClassInfo[] arrayOfClassInfo = paramClassInfo.getInterfaces();
      for (int i = 0; i < arrayOfClassInfo.length; i++) {
        if (implementedBy(arrayOfClassInfo[i])) {
          return true;
        }
      }
      paramClassInfo = paramClassInfo.getSuperclass();
    }
    return paramClassInfo == this;
  }
  
  public String toString()
  {
    return this.name;
  }
}


