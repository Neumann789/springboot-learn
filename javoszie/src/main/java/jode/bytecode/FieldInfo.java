package jode.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;

public class FieldInfo
  extends BinaryInfo
{
  ClassInfo clazzInfo;
  int modifier;
  String name;
  String typeSig;
  Object constant;
  boolean syntheticFlag;
  boolean deprecatedFlag;
  
  public FieldInfo(ClassInfo paramClassInfo)
  {
    this.clazzInfo = paramClassInfo;
  }
  
  public FieldInfo(ClassInfo paramClassInfo, String paramString1, String paramString2, int paramInt)
  {
    this.clazzInfo = paramClassInfo;
    this.name = paramString1;
    this.typeSig = paramString2;
    this.modifier = paramInt;
  }
  
  protected void readAttribute(String paramString, int paramInt1, ConstantPool paramConstantPool, DataInputStream paramDataInputStream, int paramInt2)
    throws IOException
  {
    if (((paramInt2 & 0x10) != 0) && (paramString.equals("ConstantValue")))
    {
      if (paramInt1 != 2) {
        throw new ClassFormatException("ConstantValue attribute has wrong length");
      }
      int i = paramDataInputStream.readUnsignedShort();
      this.constant = paramConstantPool.getConstant(i);
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
  
  public void reserveSmallConstants(GrowableConstantPool paramGrowableConstantPool) {}
  
  public void prepareWriting(GrowableConstantPool paramGrowableConstantPool)
  {
    paramGrowableConstantPool.putUTF8(this.name);
    paramGrowableConstantPool.putUTF8(this.typeSig);
    if (this.constant != null)
    {
      paramGrowableConstantPool.putUTF8("ConstantValue");
      if ((this.typeSig.charAt(0) == 'J') || (this.typeSig.charAt(0) == 'D')) {
        paramGrowableConstantPool.putLongConstant(this.constant);
      } else {
        paramGrowableConstantPool.putConstant(this.constant);
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
    if (this.constant != null) {
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
    if (this.constant != null)
    {
      paramDataOutputStream.writeShort(paramGrowableConstantPool.putUTF8("ConstantValue"));
      paramDataOutputStream.writeInt(2);
      int i;
      if ((this.typeSig.charAt(0) == 'J') || (this.typeSig.charAt(0) == 'D')) {
        i = paramGrowableConstantPool.putLongConstant(this.constant);
      } else {
        i = paramGrowableConstantPool.putConstant(this.constant);
      }
      paramDataOutputStream.writeShort(i);
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
    if ((paramInt & 0x10) != 0) {
      this.constant = null;
    }
    super.dropInfo(paramInt);
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
  
  public boolean isSynthetic()
  {
    return this.syntheticFlag;
  }
  
  public boolean isDeprecated()
  {
    return this.deprecatedFlag;
  }
  
  public Object getConstant()
  {
    this.clazzInfo.loadInfo(16);
    return this.constant;
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
  
  public void setConstant(Object paramObject)
  {
    this.constant = paramObject;
  }
  
  public String toString()
  {
    return "Field " + Modifier.toString(this.modifier) + " " + this.typeSig + " " + this.name;
  }
}


