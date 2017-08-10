package jode.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;

public class MethodInfo
  extends BinaryInfo
{
  ClassInfo clazzInfo;
  int modifier;
  String name;
  String typeSig;
  BytecodeInfo bytecode;
  String[] exceptions;
  boolean syntheticFlag;
  boolean deprecatedFlag;
  
  public MethodInfo(ClassInfo paramClassInfo)
  {
    this.clazzInfo = paramClassInfo;
  }
  
  public MethodInfo(ClassInfo paramClassInfo, String paramString1, String paramString2, int paramInt)
  {
    this.clazzInfo = paramClassInfo;
    this.name = paramString1;
    this.typeSig = paramString2;
    this.modifier = paramInt;
  }
  
  protected void readAttribute(String paramString, int paramInt1, ConstantPool paramConstantPool, DataInputStream paramDataInputStream, int paramInt2)
    throws IOException
  {
    if (((paramInt2 & 0x10) != 0) && (paramString.equals("Code")))
    {
      this.bytecode = new BytecodeInfo(this);
      this.bytecode.read(paramConstantPool, paramDataInputStream);
    }
    else if (paramString.equals("Exceptions"))
    {
      int i = paramDataInputStream.readUnsignedShort();
      this.exceptions = new String[i];
      for (int j = 0; j < i; j++) {
        this.exceptions[j] = paramConstantPool.getClassName(paramDataInputStream.readUnsignedShort());
      }
      if (paramInt1 != 2 * (i + 1)) {
        throw new ClassFormatException("Exceptions attribute has wrong length");
      }
    }
    else if (paramString.equals("Synthetic"))
    {
      this.syntheticFlag = true;
      if (paramInt1 != 0) {
        throw new ClassFormatException("Synthetic attribute has wrong length");
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
  
  public void read(ConstantPool paramConstantPool, DataInputStream paramDataInputStream, int paramInt)
    throws IOException
  {
    this.modifier = paramDataInputStream.readUnsignedShort();
    this.name = paramConstantPool.getUTF8(paramDataInputStream.readUnsignedShort());
    this.typeSig = paramConstantPool.getUTF8(paramDataInputStream.readUnsignedShort());
    readAttributes(paramConstantPool, paramDataInputStream, paramInt);
  }
  
  public void reserveSmallConstants(GrowableConstantPool paramGrowableConstantPool)
  {
    if (this.bytecode != null) {
      this.bytecode.reserveSmallConstants(paramGrowableConstantPool);
    }
  }
  
  public void prepareWriting(GrowableConstantPool paramGrowableConstantPool)
  {
    paramGrowableConstantPool.putUTF8(this.name);
    paramGrowableConstantPool.putUTF8(this.typeSig);
    if (this.bytecode != null)
    {
      paramGrowableConstantPool.putUTF8("Code");
      this.bytecode.prepareWriting(paramGrowableConstantPool);
    }
    if (this.exceptions != null)
    {
      paramGrowableConstantPool.putUTF8("Exceptions");
      for (int i = 0; i < this.exceptions.length; i++) {
        paramGrowableConstantPool.putClassName(this.exceptions[i]);
      }
    }
    if (this.syntheticFlag) {
      paramGrowableConstantPool.putUTF8("Synthetic");
    }
    if (this.deprecatedFlag) {
      paramGrowableConstantPool.putUTF8("Deprecated");
    }
    prepareAttributes(paramGrowableConstantPool);
  }
  
  protected int getKnownAttributeCount()
  {
    int i = 0;
    if (this.bytecode != null) {
      i++;
    }
    if (this.exceptions != null) {
      i++;
    }
    if (this.syntheticFlag) {
      i++;
    }
    if (this.deprecatedFlag) {
      i++;
    }
    return i;
  }
  
  public void writeKnownAttributes(GrowableConstantPool paramGrowableConstantPool, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    if (this.bytecode != null)
    {
      paramDataOutputStream.writeShort(paramGrowableConstantPool.putUTF8("Code"));
      paramDataOutputStream.writeInt(this.bytecode.getSize());
      this.bytecode.write(paramGrowableConstantPool, paramDataOutputStream);
    }
    if (this.exceptions != null)
    {
      int i = this.exceptions.length;
      paramDataOutputStream.writeShort(paramGrowableConstantPool.putUTF8("Exceptions"));
      paramDataOutputStream.writeInt(2 + i * 2);
      paramDataOutputStream.writeShort(i);
      for (int j = 0; j < i; j++) {
        paramDataOutputStream.writeShort(paramGrowableConstantPool.putClassName(this.exceptions[j]));
      }
    }
    if (this.syntheticFlag)
    {
      paramDataOutputStream.writeShort(paramGrowableConstantPool.putUTF8("Synthetic"));
      paramDataOutputStream.writeInt(0);
    }
    if (this.deprecatedFlag)
    {
      paramDataOutputStream.writeShort(paramGrowableConstantPool.putUTF8("Deprecated"));
      paramDataOutputStream.writeInt(0);
    }
  }
  
  public void write(GrowableConstantPool paramGrowableConstantPool, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeShort(this.modifier);
    paramDataOutputStream.writeShort(paramGrowableConstantPool.putUTF8(this.name));
    paramDataOutputStream.writeShort(paramGrowableConstantPool.putUTF8(this.typeSig));
    writeAttributes(paramGrowableConstantPool, paramDataOutputStream);
  }
  
  public void dropInfo(int paramInt)
  {
    if ((paramInt & 0x10) != 0)
    {
      this.bytecode = null;
      this.exceptions = null;
    }
    if (this.bytecode != null) {
      this.bytecode.dropInfo(paramInt);
    }
    super.dropInfo(paramInt);
  }
  
  public ClassInfo getClazzInfo()
  {
    return this.clazzInfo;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getType()
  {
    return this.typeSig;
  }
  
  public int getModifiers()
  {
    return this.modifier;
  }
  
  public boolean isStatic()
  {
    return Modifier.isStatic(this.modifier);
  }
  
  public boolean isSynthetic()
  {
    return this.syntheticFlag;
  }
  
  public boolean isDeprecated()
  {
    return this.deprecatedFlag;
  }
  
  public BytecodeInfo getBytecode()
  {
    return this.bytecode;
  }
  
  public String[] getExceptions()
  {
    return this.exceptions;
  }
  
  public void setName(String paramString)
  {
    this.name = paramString;
  }
  
  public void setType(String paramString)
  {
    this.typeSig = paramString;
  }
  
  public void setModifiers(int paramInt)
  {
    this.modifier = paramInt;
  }
  
  public void setSynthetic(boolean paramBoolean)
  {
    this.syntheticFlag = paramBoolean;
  }
  
  public void setDeprecated(boolean paramBoolean)
  {
    this.deprecatedFlag = paramBoolean;
  }
  
  public void setBytecode(BytecodeInfo paramBytecodeInfo)
  {
    this.clazzInfo.loadInfo(16);
    this.bytecode = paramBytecodeInfo;
  }
  
  public void setExceptions(String[] paramArrayOfString)
  {
    this.clazzInfo.loadInfo(16);
    this.exceptions = paramArrayOfString;
  }
  
  public String toString()
  {
    return "Method " + Modifier.toString(this.modifier) + " " + this.typeSig + " " + this.clazzInfo.getName() + "." + this.name;
  }
}


