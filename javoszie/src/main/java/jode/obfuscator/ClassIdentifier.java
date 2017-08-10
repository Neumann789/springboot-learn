package jode.obfuscator;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import jode.GlobalOptions;
import jode.bytecode.ClassInfo;
import jode.bytecode.FieldInfo;
import jode.bytecode.InnerClassInfo;
import jode.bytecode.MethodInfo;
import jode.bytecode.Reference;
import jode.obfuscator.modules.ModifierMatcher;

public class ClassIdentifier
  extends Identifier
{
  PackageIdentifier pack;
  String name;
  String fullName;
  ClassInfo info;
  String superName;
  String[] ifaceNames;
  List fieldIdents;
  List methodIdents;
  List knownSubClasses = new LinkedList();
  List virtualReachables = new LinkedList();
  
  public ClassIdentifier(PackageIdentifier paramPackageIdentifier, String paramString1, String paramString2, ClassInfo paramClassInfo)
  {
    super(paramString2);
    this.pack = paramPackageIdentifier;
    this.fullName = paramString1;
    this.name = paramString2;
    this.info = paramClassInfo;
  }
  
  public void addSubClass(ClassIdentifier paramClassIdentifier)
  {
    this.knownSubClasses.add(paramClassIdentifier);
    Iterator localIterator = this.virtualReachables.iterator();
    while (localIterator.hasNext()) {
      paramClassIdentifier.reachableReference((Reference)localIterator.next(), true);
    }
  }
  
  private FieldIdentifier findField(String paramString1, String paramString2)
  {
    Iterator localIterator = this.fieldIdents.iterator();
    while (localIterator.hasNext())
    {
      FieldIdentifier localFieldIdentifier = (FieldIdentifier)localIterator.next();
      if ((localFieldIdentifier.getName().equals(paramString1)) && (localFieldIdentifier.getType().equals(paramString2))) {
        return localFieldIdentifier;
      }
    }
    return null;
  }
  
  private MethodIdentifier findMethod(String paramString1, String paramString2)
  {
    Iterator localIterator = this.methodIdents.iterator();
    while (localIterator.hasNext())
    {
      MethodIdentifier localMethodIdentifier = (MethodIdentifier)localIterator.next();
      if ((localMethodIdentifier.getName().equals(paramString1)) && (localMethodIdentifier.getType().equals(paramString2))) {
        return localMethodIdentifier;
      }
    }
    return null;
  }
  
  public void reachableReference(Reference paramReference, boolean paramBoolean)
  {
    int i = 0;
    Object localObject1 = getChilds();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Identifier)((Iterator)localObject1).next();
      if ((paramReference.getName().equals(((Identifier)localObject2).getName())) && (paramReference.getType().equals(((Identifier)localObject2).getType())))
      {
        ((Identifier)localObject2).setReachable();
        i = 1;
      }
    }
    if (i == 0)
    {
      localObject1 = Main.getClassBundle().getClassIdentifier(this.info.getSuperclass().getName());
      if (localObject1 != null) {
        ((ClassIdentifier)localObject1).reachableReference(paramReference, false);
      }
    }
    if (paramBoolean)
    {
      localObject1 = this.virtualReachables.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Reference)((Iterator)localObject1).next();
        if ((((Reference)localObject2).getName().equals(paramReference.getName())) && (((Reference)localObject2).getType().equals(paramReference.getType()))) {
          return;
        }
      }
      localObject1 = this.knownSubClasses.iterator();
      while (((Iterator)localObject1).hasNext()) {
        ((ClassIdentifier)((Iterator)localObject1).next()).reachableReference(paramReference, false);
      }
      this.virtualReachables.add(paramReference);
    }
  }
  
  public void chainMethodIdentifier(Identifier paramIdentifier)
  {
    String str1 = paramIdentifier.getName();
    String str2 = paramIdentifier.getType();
    Iterator localIterator = this.methodIdents.iterator();
    while (localIterator.hasNext())
    {
      Identifier localIdentifier = (Identifier)localIterator.next();
      if ((localIdentifier.getName().equals(str1)) && (localIdentifier.getType().equals(str2))) {
        paramIdentifier.addShadow(localIdentifier);
      }
    }
  }
  
  public long calcSerialVersionUID()
  {
    final MessageDigest localMessageDigest;
    try
    {
      localMessageDigest = MessageDigest.getInstance("SHA");
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      localNoSuchAlgorithmException.printStackTrace();
      GlobalOptions.err.println("Can't calculate serialVersionUID");
      return 0L;
    }
    OutputStream local1 = new OutputStream()
    {
      public void write(int paramAnonymousInt)
      {
        localMessageDigest.update((byte)paramAnonymousInt);
      }
      
      public void write(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        localMessageDigest.update(paramAnonymousArrayOfByte, paramAnonymousInt1, paramAnonymousInt2);
      }
    };
    DataOutputStream localDataOutputStream = new DataOutputStream(local1);
    try
    {
      localDataOutputStream.writeUTF(this.info.getName());
      int i = this.info.getModifiers();
      i &= 0x611;
      localDataOutputStream.writeInt(i);
      ClassInfo[] arrayOfClassInfo = (ClassInfo[])this.info.getInterfaces().clone();
      Arrays.sort(arrayOfClassInfo, new Comparator()
      {
        public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
        {
          return ((ClassInfo)paramAnonymousObject1).getName().compareTo(((ClassInfo)paramAnonymousObject2).getName());
        }
      });
      for (int j = 0; j < arrayOfClassInfo.length; j++) {
        localDataOutputStream.writeUTF(arrayOfClassInfo[j].getName());
      }
      Comparator local3 = new Comparator()
      {
        public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
        {
          Identifier localIdentifier1 = (Identifier)paramAnonymousObject1;
          Identifier localIdentifier2 = (Identifier)paramAnonymousObject2;
          String str1 = localIdentifier1.getName();
          String str2 = localIdentifier2.getName();
          int i = (str1.equals("<init>")) || (str1.equals("<clinit>")) ? 1 : 0;
          int j = (str2.equals("<init>")) || (str2.equals("<clinit>")) ? 1 : 0;
          if (i != j) {
            return i != 0 ? -1 : 1;
          }
          int k = localIdentifier1.getName().compareTo(localIdentifier2.getName());
          if (k != 0) {
            return k;
          }
          return localIdentifier1.getType().compareTo(localIdentifier2.getType());
        }
      };
      List localList1 = Arrays.asList(this.fieldIdents.toArray());
      List localList2 = Arrays.asList(this.methodIdents.toArray());
      Collections.sort(localList1, local3);
      Collections.sort(localList2, local3);
      Object localObject1 = localList1.iterator();
      Object localObject2;
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (FieldIdentifier)((Iterator)localObject1).next();
        i = ((FieldIdentifier)localObject2).info.getModifiers();
        if (((i & 0x2) == 0) || ((i & 0x88) == 0))
        {
          localDataOutputStream.writeUTF(((FieldIdentifier)localObject2).getName());
          localDataOutputStream.writeInt(i);
          localDataOutputStream.writeUTF(((FieldIdentifier)localObject2).getType());
        }
      }
      localObject1 = localList2.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (MethodIdentifier)((Iterator)localObject1).next();
        i = ((MethodIdentifier)localObject2).info.getModifiers();
        if (!Modifier.isPrivate(i))
        {
          localDataOutputStream.writeUTF(((MethodIdentifier)localObject2).getName());
          localDataOutputStream.writeInt(i);
          localDataOutputStream.writeUTF(((MethodIdentifier)localObject2).getType().replace('/', '.'));
        }
      }
      localDataOutputStream.close();
      localObject1 = localMessageDigest.digest();
      long l = 0L;
      for (int k = 0; k < 8; k++) {
        l += ((localObject1[k] & 0xFF) << 8 * k);
      }
      return l;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
      GlobalOptions.err.println("Can't calculate serialVersionUID");
    }
    return 0L;
  }
  
  public void addSUID()
  {
    long l = calcSerialVersionUID();
    FieldInfo localFieldInfo = new FieldInfo(this.info, "serialVersionUID", "J", 25);
    localFieldInfo.setConstant(new Long(l));
    FieldIdentifier localFieldIdentifier = new FieldIdentifier(this, localFieldInfo);
    this.fieldIdents.add(localFieldIdentifier);
    localFieldIdentifier.setPreserved();
  }
  
  public boolean isSerializable()
  {
    return ClassInfo.forName("java.io.Serializable").implementedBy(this.info);
  }
  
  public boolean hasSUID()
  {
    return findField("serialVersionUID", "J") != null;
  }
  
  protected void setSinglePreserved()
  {
    this.pack.setPreserved();
  }
  
  public void setSingleReachable()
  {
    super.setSingleReachable();
    Main.getClassBundle().analyzeIdentifier(this);
  }
  
  public void analyzeSuperClasses(ClassInfo paramClassInfo)
  {
    while (paramClassInfo != null)
    {
      ClassIdentifier localClassIdentifier = Main.getClassBundle().getClassIdentifier(paramClassInfo.getName());
      if (localClassIdentifier != null)
      {
        localClassIdentifier.addSubClass(this);
      }
      else
      {
        localObject = ("L" + paramClassInfo.getName().replace('.', '/') + ";").intern();
        MethodInfo[] arrayOfMethodInfo = paramClassInfo.getMethods();
        for (int j = 0; j < arrayOfMethodInfo.length; j++)
        {
          int k = arrayOfMethodInfo[j].getModifiers();
          if (((0x1A & k) == 0) && (!arrayOfMethodInfo[j].getName().equals("<init>"))) {
            reachableReference(Reference.getReference((String)localObject, arrayOfMethodInfo[j].getName(), arrayOfMethodInfo[j].getType()), true);
          }
        }
      }
      Object localObject = paramClassInfo.getInterfaces();
      for (int i = 0; i < localObject.length; i++) {
        analyzeSuperClasses(localObject[i]);
      }
      paramClassInfo = paramClassInfo.getSuperclass();
    }
  }
  
  public void analyze()
  {
    if (GlobalOptions.verboseLevel > 0) {
      GlobalOptions.err.println("Reachable: " + this);
    }
    ClassInfo[] arrayOfClassInfo = this.info.getInterfaces();
    for (int i = 0; i < arrayOfClassInfo.length; i++) {
      analyzeSuperClasses(arrayOfClassInfo[i]);
    }
    analyzeSuperClasses(this.info.getSuperclass());
  }
  
  public void initSuperClasses(ClassInfo paramClassInfo)
  {
    while (paramClassInfo != null)
    {
      ClassIdentifier localClassIdentifier = Main.getClassBundle().getClassIdentifier(paramClassInfo.getName());
      int j;
      if (localClassIdentifier != null)
      {
        localObject = localClassIdentifier.getMethodIdents().iterator();
        while (((Iterator)localObject).hasNext())
        {
          MethodIdentifier localMethodIdentifier1 = (MethodIdentifier)((Iterator)localObject).next();
          j = localMethodIdentifier1.info.getModifiers();
          if (((0x1A & j) == 0) && (!localMethodIdentifier1.getName().equals("<init>"))) {
            chainMethodIdentifier(localMethodIdentifier1);
          }
        }
      }
      else
      {
        localObject = paramClassInfo.getMethods();
        for (i = 0; i < localObject.length; i++)
        {
          j = localObject[i].getModifiers();
          if (((0x1A & j) == 0) && (!localObject[i].getName().equals("<init>")))
          {
            MethodIdentifier localMethodIdentifier2 = findMethod(localObject[i].getName(), localObject[i].getType());
            if (localMethodIdentifier2 != null) {
              localMethodIdentifier2.setPreserved();
            }
          }
        }
      }
      Object localObject = paramClassInfo.getInterfaces();
      for (int i = 0; i < localObject.length; i++) {
        initSuperClasses(localObject[i]);
      }
      paramClassInfo = paramClassInfo.getSuperclass();
    }
  }
  
  public void initClass()
  {
    this.info.loadInfo(255);
    FieldInfo[] arrayOfFieldInfo = this.info.getFields();
    MethodInfo[] arrayOfMethodInfo = this.info.getMethods();
    if (Main.swapOrder)
    {
      Collections.shuffle(Arrays.asList(arrayOfFieldInfo), Main.rand);
      Collections.shuffle(Arrays.asList(arrayOfMethodInfo), Main.rand);
    }
    this.fieldIdents = new ArrayList(arrayOfFieldInfo.length);
    this.methodIdents = new ArrayList(arrayOfMethodInfo.length);
    for (int i = 0; i < arrayOfFieldInfo.length; i++) {
      this.fieldIdents.add(new FieldIdentifier(this, arrayOfFieldInfo[i]));
    }
    for (i = 0; i < arrayOfMethodInfo.length; i++)
    {
      MethodIdentifier localMethodIdentifier = new MethodIdentifier(this, arrayOfMethodInfo[i]);
      this.methodIdents.add(localMethodIdentifier);
      if (localMethodIdentifier.getName().equals("<clinit>"))
      {
        localMethodIdentifier.setPreserved();
        localMethodIdentifier.setReachable();
      }
      else if (localMethodIdentifier.getName().equals("<init>"))
      {
        localMethodIdentifier.setPreserved();
      }
    }
    ClassInfo[] arrayOfClassInfo = this.info.getInterfaces();
    this.ifaceNames = new String[arrayOfClassInfo.length];
    for (int j = 0; j < arrayOfClassInfo.length; j++)
    {
      this.ifaceNames[j] = arrayOfClassInfo[j].getName();
      initSuperClasses(arrayOfClassInfo[j]);
    }
    if (this.info.getSuperclass() != null)
    {
      this.superName = this.info.getSuperclass().getName();
      initSuperClasses(this.info.getSuperclass());
    }
    if ((Main.stripping & 0x10) != 0) {
      this.info.setSourceFile(null);
    }
    if ((Main.stripping & 0x2) != 0)
    {
      this.info.setInnerClasses(new InnerClassInfo[0]);
      this.info.setOuterClasses(new InnerClassInfo[0]);
      this.info.setExtraClasses(new InnerClassInfo[0]);
    }
    InnerClassInfo[] arrayOfInnerClassInfo1 = this.info.getInnerClasses();
    InnerClassInfo[] arrayOfInnerClassInfo2 = this.info.getOuterClasses();
    InnerClassInfo[] arrayOfInnerClassInfo3 = this.info.getExtraClasses();
    int k;
    if (arrayOfInnerClassInfo2 != null) {
      for (k = 0; k < arrayOfInnerClassInfo2.length; k++) {
        if (arrayOfInnerClassInfo2[k].outer != null) {
          Main.getClassBundle().getClassIdentifier(arrayOfInnerClassInfo2[k].outer);
        }
      }
    }
    if (arrayOfInnerClassInfo1 != null) {
      for (k = 0; k < arrayOfInnerClassInfo1.length; k++) {
        Main.getClassBundle().getClassIdentifier(arrayOfInnerClassInfo1[k].inner);
      }
    }
    if (arrayOfInnerClassInfo3 != null) {
      for (k = 0; k < arrayOfInnerClassInfo3.length; k++)
      {
        Main.getClassBundle().getClassIdentifier(arrayOfInnerClassInfo3[k].inner);
        if (arrayOfInnerClassInfo3[k].outer != null) {
          Main.getClassBundle().getClassIdentifier(arrayOfInnerClassInfo3[k].outer);
        }
      }
    }
  }
  
  public void addIfaces(Collection paramCollection, ClassIdentifier paramClassIdentifier)
  {
    ClassInfo[] arrayOfClassInfo = paramClassIdentifier.info.getInterfaces();
    for (int i = 0; i < arrayOfClassInfo.length; i++)
    {
      ClassIdentifier localClassIdentifier = Main.getClassBundle().getClassIdentifier(arrayOfClassInfo[i].getName());
      if ((localClassIdentifier != null) && (!localClassIdentifier.isReachable())) {
        addIfaces(paramCollection, localClassIdentifier);
      } else {
        paramCollection.add(arrayOfClassInfo[i]);
      }
    }
  }
  
  public void transformSuperIfaces()
  {
    if ((Main.stripping & 0x1) == 0) {
      return;
    }
    LinkedList localLinkedList = new LinkedList();
    for (Object localObject1 = this;; localObject1 = localObject2)
    {
      addIfaces(localLinkedList, (ClassIdentifier)localObject1);
      localObject2 = Main.getClassBundle().getClassIdentifier(((ClassIdentifier)localObject1).superName);
      if ((localObject2 == null) || (((ClassIdentifier)localObject2).isReachable())) {
        break;
      }
    }
    Object localObject2 = ((ClassIdentifier)localObject1).info.getSuperclass();
    ClassInfo[] arrayOfClassInfo = (ClassInfo[])localLinkedList.toArray(new ClassInfo[localLinkedList.size()]);
    this.info.setSuperclass((ClassInfo)localObject2);
    this.info.setInterfaces(arrayOfClassInfo);
  }
  
  public void transformInnerClasses()
  {
    InnerClassInfo[] arrayOfInnerClassInfo1 = this.info.getOuterClasses();
    int m;
    Object localObject1;
    int i2;
    ClassIdentifier localClassIdentifier2;
    Object localObject3;
    Object localObject4;
    String str1;
    if (arrayOfInnerClassInfo1 != null)
    {
      int i = arrayOfInnerClassInfo1.length;
      if ((Main.stripping & 0x1) != 0) {
        for (int j = 0; j < arrayOfInnerClassInfo1.length; j++) {
          if (arrayOfInnerClassInfo1[j].outer != null)
          {
            ClassIdentifier localClassIdentifier1 = Main.getClassBundle().getClassIdentifier(arrayOfInnerClassInfo1[j].outer);
            if ((localClassIdentifier1 != null) && (!localClassIdentifier1.isReachable())) {
              i--;
            }
          }
        }
      }
      if (i == 0)
      {
        this.info.setOuterClasses(null);
      }
      else
      {
        InnerClassInfo[] arrayOfInnerClassInfo3 = new InnerClassInfo[i];
        m = 0;
        localObject1 = getFullAlias();
        for (i2 = 0; i2 < arrayOfInnerClassInfo1.length; i2++)
        {
          localClassIdentifier2 = arrayOfInnerClassInfo1[i2].outer != null ? Main.getClassBundle().getClassIdentifier(arrayOfInnerClassInfo1[i2].outer) : null;
          if (((Main.stripping & 0x1) == 0) || (localClassIdentifier2 == null) || (localClassIdentifier2.isReachable()))
          {
            localObject3 = localObject1;
            localObject4 = localClassIdentifier2 == null ? arrayOfInnerClassInfo1[i2].outer : localClassIdentifier2.getFullAlias();
            str1 = (localObject4 != null) && (((String)localObject3).startsWith((String)localObject4 + "$")) ? ((String)localObject3).substring(((String)localObject4).length() + 1) : arrayOfInnerClassInfo1[i2].name == null ? null : ((String)localObject3).substring(((String)localObject3).lastIndexOf('.') + 1);
            arrayOfInnerClassInfo3[(m++)] = new InnerClassInfo((String)localObject3, (String)localObject4, str1, arrayOfInnerClassInfo1[i2].modifiers);
            localObject1 = localObject4;
          }
        }
        this.info.setOuterClasses(arrayOfInnerClassInfo3);
      }
    }
    InnerClassInfo[] arrayOfInnerClassInfo2 = this.info.getInnerClasses();
    int i1;
    if (arrayOfInnerClassInfo2 != null)
    {
      int k = arrayOfInnerClassInfo2.length;
      if ((Main.stripping & 0x1) != 0) {
        for (m = 0; m < arrayOfInnerClassInfo2.length; m++)
        {
          localObject1 = Main.getClassBundle().getClassIdentifier(arrayOfInnerClassInfo2[m].inner);
          if ((localObject1 != null) && (!((ClassIdentifier)localObject1).isReachable())) {
            k--;
          }
        }
      }
      if (k == 0)
      {
        this.info.setInnerClasses(null);
      }
      else
      {
        InnerClassInfo[] arrayOfInnerClassInfo5 = new InnerClassInfo[k];
        i1 = 0;
        for (i2 = 0; i2 < arrayOfInnerClassInfo2.length; i2++)
        {
          localClassIdentifier2 = Main.getClassBundle().getClassIdentifier(arrayOfInnerClassInfo2[i2].inner);
          if (((Main.stripping & 0x1) == 0) || (localClassIdentifier2 == null) || (localClassIdentifier2.isReachable()))
          {
            localObject3 = localClassIdentifier2 == null ? arrayOfInnerClassInfo2[i2].inner : localClassIdentifier2.getFullAlias();
            localObject4 = getFullAlias();
            str1 = (localObject4 != null) && (((String)localObject3).startsWith((String)localObject4 + "$")) ? ((String)localObject3).substring(((String)localObject4).length() + 1) : arrayOfInnerClassInfo2[i2].name == null ? null : ((String)localObject3).substring(((String)localObject3).lastIndexOf('.') + 1);
            arrayOfInnerClassInfo5[(i1++)] = new InnerClassInfo((String)localObject3, (String)localObject4, str1, arrayOfInnerClassInfo2[i2].modifiers);
          }
        }
        this.info.setInnerClasses(arrayOfInnerClassInfo5);
      }
    }
    InnerClassInfo[] arrayOfInnerClassInfo4 = this.info.getExtraClasses();
    if (arrayOfInnerClassInfo4 != null)
    {
      int n = arrayOfInnerClassInfo4.length;
      if ((Main.stripping & 0x1) != 0) {
        for (i1 = 0; i1 < arrayOfInnerClassInfo4.length; i1++)
        {
          Object localObject2 = arrayOfInnerClassInfo4[i1].outer != null ? Main.getClassBundle().getClassIdentifier(arrayOfInnerClassInfo4[i1].outer) : null;
          localClassIdentifier2 = Main.getClassBundle().getClassIdentifier(arrayOfInnerClassInfo4[i1].inner);
          if (((localObject2 != null) && (!((ClassIdentifier)localObject2).isReachable())) || ((localClassIdentifier2 != null) && (!localClassIdentifier2.isReachable()))) {
            n--;
          }
        }
      }
      if (n == 0)
      {
        this.info.setExtraClasses(null);
      }
      else
      {
        InnerClassInfo[] arrayOfInnerClassInfo6 = n > 0 ? new InnerClassInfo[n] : null;
        int i3 = 0;
        for (int i4 = 0; i4 < arrayOfInnerClassInfo4.length; i4++)
        {
          localObject3 = arrayOfInnerClassInfo4[i4].outer != null ? Main.getClassBundle().getClassIdentifier(arrayOfInnerClassInfo4[i4].outer) : null;
          localObject4 = Main.getClassBundle().getClassIdentifier(arrayOfInnerClassInfo4[i4].inner);
          if (((Main.stripping & 0x1) == 0) || (((localObject4 == null) || (((ClassIdentifier)localObject4).isReachable())) && ((localObject3 == null) || (((ClassIdentifier)localObject3).isReachable()))))
          {
            str1 = localObject4 == null ? arrayOfInnerClassInfo4[i4].inner : ((ClassIdentifier)localObject4).getFullAlias();
            String str2 = localObject3 == null ? arrayOfInnerClassInfo4[i4].outer : ((ClassIdentifier)localObject3).getFullAlias();
            String str3 = (str2 != null) && (str1.startsWith(str2 + "$")) ? str1.substring(str2.length() + 1) : arrayOfInnerClassInfo4[i4].name == null ? null : str1.substring(str1.lastIndexOf('.') + 1);
            arrayOfInnerClassInfo6[(i3++)] = new InnerClassInfo(str1, str2, str3, arrayOfInnerClassInfo4[i4].modifiers);
          }
        }
        this.info.setExtraClasses(arrayOfInnerClassInfo6);
      }
    }
  }
  
  public void doTransformations()
  {
    if (GlobalOptions.verboseLevel > 0) {
      GlobalOptions.err.println("Transforming " + this);
    }
    this.info.setName(getFullAlias());
    transformSuperIfaces();
    transformInnerClasses();
    ArrayList localArrayList1 = new ArrayList(this.fieldIdents.size());
    ArrayList localArrayList2 = new ArrayList(this.methodIdents.size());
    Iterator localIterator = this.fieldIdents.iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (FieldIdentifier)localIterator.next();
      if (((Main.stripping & 0x1) == 0) || (((FieldIdentifier)localObject).isReachable()))
      {
        ((FieldIdentifier)localObject).doTransformations();
        localArrayList1.add(((FieldIdentifier)localObject).info);
      }
    }
    localIterator = this.methodIdents.iterator();
    while (localIterator.hasNext())
    {
      localObject = (MethodIdentifier)localIterator.next();
      if (((Main.stripping & 0x1) == 0) || (((MethodIdentifier)localObject).isReachable()))
      {
        ((MethodIdentifier)localObject).doTransformations();
        localArrayList2.add(((MethodIdentifier)localObject).info);
      }
    }
    this.info.setFields((FieldInfo[])localArrayList1.toArray(new FieldInfo[localArrayList1.size()]));
    this.info.setMethods((MethodInfo[])localArrayList2.toArray(new MethodInfo[localArrayList2.size()]));
  }
  
  public void storeClass(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    if (GlobalOptions.verboseLevel > 0) {
      GlobalOptions.err.println("Writing " + this);
    }
    this.info.write(paramDataOutputStream);
    this.info = null;
    this.fieldIdents = (this.methodIdents = null);
  }
  
  public Identifier getParent()
  {
    return this.pack;
  }
  
  public String getFullName()
  {
    return this.fullName;
  }
  
  public String getFullAlias()
  {
    if (this.pack.parent == null) {
      return getAlias();
    }
    return this.pack.getFullAlias() + "." + getAlias();
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getType()
  {
    return "Ljava/lang/Class;";
  }
  
  public int getModifiers()
  {
    return this.info.getModifiers();
  }
  
  public List getFieldIdents()
  {
    return this.fieldIdents;
  }
  
  public List getMethodIdents()
  {
    return this.methodIdents;
  }
  
  public Iterator getChilds()
  {
    final Iterator localIterator1 = this.fieldIdents.iterator();
    final Iterator localIterator2 = this.methodIdents.iterator();
    new Iterator()
    {
      boolean fieldsNext = localIterator1.hasNext();
      
      public boolean hasNext()
      {
        return this.fieldsNext ? true : localIterator2.hasNext();
      }
      
      public Object next()
      {
        if (this.fieldsNext)
        {
          Object localObject = localIterator1.next();
          this.fieldsNext = localIterator1.hasNext();
          return localObject;
        }
        return localIterator2.next();
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
  
  public String toString()
  {
    return "ClassIdentifier " + getFullName();
  }
  
  public Identifier getIdentifier(String paramString1, String paramString2)
  {
    Object localObject = getChilds();
    Identifier localIdentifier;
    while (((Iterator)localObject).hasNext())
    {
      localIdentifier = (Identifier)((Iterator)localObject).next();
      if ((localIdentifier.getName().equals(paramString1)) && (localIdentifier.getType().startsWith(paramString2))) {
        return localIdentifier;
      }
    }
    if (this.superName != null)
    {
      localObject = Main.getClassBundle().getClassIdentifier(this.superName);
      if (localObject != null)
      {
        localIdentifier = ((ClassIdentifier)localObject).getIdentifier(paramString1, paramString2);
        if (localIdentifier != null) {
          return localIdentifier;
        }
      }
    }
    return null;
  }
  
  public boolean containsFieldAliasDirectly(String paramString1, String paramString2, IdentifierMatcher paramIdentifierMatcher)
  {
    Iterator localIterator = this.fieldIdents.iterator();
    while (localIterator.hasNext())
    {
      Identifier localIdentifier = (Identifier)localIterator.next();
      if ((((Main.stripping & 0x1) == 0) || (localIdentifier.isReachable())) && (localIdentifier.wasAliased()) && (localIdentifier.getAlias().equals(paramString1)) && (localIdentifier.getType().startsWith(paramString2)) && (paramIdentifierMatcher.matches(localIdentifier))) {
        return true;
      }
    }
    return false;
  }
  
  public boolean containsMethodAliasDirectly(String paramString1, String paramString2, IdentifierMatcher paramIdentifierMatcher)
  {
    Iterator localIterator = this.methodIdents.iterator();
    while (localIterator.hasNext())
    {
      Identifier localIdentifier = (Identifier)localIterator.next();
      if ((((Main.stripping & 0x1) == 0) || (localIdentifier.isReachable())) && (localIdentifier.wasAliased()) && (localIdentifier.getAlias().equals(paramString1)) && (localIdentifier.getType().startsWith(paramString2)) && (paramIdentifierMatcher.matches(localIdentifier))) {
        return true;
      }
    }
    return false;
  }
  
  public boolean fieldConflicts(FieldIdentifier paramFieldIdentifier, String paramString)
  {
    String str = (Main.options & 0x1) != 0 ? paramFieldIdentifier.getType() : "";
    ModifierMatcher localModifierMatcher = ModifierMatcher.allowAll;
    return containsFieldAliasDirectly(paramString, str, localModifierMatcher);
  }
  
  public boolean methodConflicts(MethodIdentifier paramMethodIdentifier, String paramString)
  {
    String str = paramMethodIdentifier.getType();
    if ((Main.options & 0x1) == 0) {
      str = str.substring(0, str.indexOf(')') + 1);
    }
    ModifierMatcher localModifierMatcher1 = ModifierMatcher.allowAll;
    if (containsMethodAliasDirectly(paramString, str, localModifierMatcher1)) {
      return true;
    }
    ModifierMatcher localModifierMatcher2 = localModifierMatcher1.forceAccess(0, true);
    if (paramMethodIdentifier.info.isStatic()) {
      localModifierMatcher2.forbidModifier(8);
    }
    ClassInfo localClassInfo = this.info.getSuperclass();
    ClassIdentifier localClassIdentifier = this;
    Object localObject1;
    Object localObject2;
    while (localClassInfo != null)
    {
      localObject1 = Main.getClassBundle().getClassIdentifier(localClassInfo.getName());
      if (localObject1 != null)
      {
        if (((ClassIdentifier)localObject1).containsMethodAliasDirectly(paramString, str, localModifierMatcher2)) {
          return true;
        }
      }
      else
      {
        localObject2 = localClassInfo.getMethods();
        for (int i = 0; i < localObject2.length; i++) {
          if ((localObject2[i].getName().equals(paramString)) && (localObject2[i].getType().startsWith(str)) && (localModifierMatcher2.matches(localObject2[i].getModifiers()))) {
            return true;
          }
        }
      }
      localClassInfo = localClassInfo.getSuperclass();
    }
    if (localModifierMatcher2.matches(paramMethodIdentifier))
    {
      localObject1 = this.knownSubClasses.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (ClassIdentifier)((Iterator)localObject1).next();
        if (((ClassIdentifier)localObject2).containsMethodAliasDirectly(paramString, str, localModifierMatcher2)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean conflicting(String paramString)
  {
    return this.pack.contains(paramString, this);
  }
}


