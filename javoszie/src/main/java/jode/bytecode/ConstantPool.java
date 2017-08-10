package jode.bytecode;

import java.io.DataInputStream;
import java.io.IOException;

public class ConstantPool
{
  public static final int CLASS = 7;
  public static final int FIELDREF = 9;
  public static final int METHODREF = 10;
  public static final int INTERFACEMETHODREF = 11;
  public static final int STRING = 8;
  public static final int INTEGER = 3;
  public static final int FLOAT = 4;
  public static final int LONG = 5;
  public static final int DOUBLE = 6;
  public static final int NAMEANDTYPE = 12;
  public static final int UTF8 = 1;
  int count;
  int[] tags;
  int[] indices1;
  int[] indices2;
  Object[] constants;
  
  public void read(DataInputStream paramDataInputStream)
    throws IOException
  {
    this.count = paramDataInputStream.readUnsignedShort();
    this.tags = new int[this.count];
    this.indices1 = new int[this.count];
    this.indices2 = new int[this.count];
    this.constants = new Object[this.count];
    for (int i = 1; i < this.count; i++)
    {
      int j = paramDataInputStream.readUnsignedByte();
      this.tags[i] = j;
      switch (j)
      {
      case 7: 
        this.indices1[i] = paramDataInputStream.readUnsignedShort();
        break;
      case 9: 
      case 10: 
      case 11: 
        this.indices1[i] = paramDataInputStream.readUnsignedShort();
        this.indices2[i] = paramDataInputStream.readUnsignedShort();
        break;
      case 8: 
        this.indices1[i] = paramDataInputStream.readUnsignedShort();
        break;
      case 3: 
        this.constants[i] = new Integer(paramDataInputStream.readInt());
        break;
      case 4: 
        this.constants[i] = new Float(paramDataInputStream.readFloat());
        break;
      case 5: 
        this.constants[i] = new Long(paramDataInputStream.readLong());
        this.tags[(++i)] = -5;
        break;
      case 6: 
        this.constants[i] = new Double(paramDataInputStream.readDouble());
        this.tags[(++i)] = -6;
        break;
      case 12: 
        this.indices1[i] = paramDataInputStream.readUnsignedShort();
        this.indices2[i] = paramDataInputStream.readUnsignedShort();
        break;
      case 1: 
        this.constants[i] = paramDataInputStream.readUTF().intern();
        break;
      case 2: 
      default: 
        throw new ClassFormatException("unknown constant tag");
      }
    }
  }
  
  public int getTag(int paramInt)
    throws ClassFormatException
  {
    if (paramInt == 0) {
      throw new ClassFormatException("null tag");
    }
    return this.tags[paramInt];
  }
  
  public String getUTF8(int paramInt)
    throws ClassFormatException
  {
    if (paramInt == 0) {
      return null;
    }
    if (this.tags[paramInt] != 1) {
      throw new ClassFormatException("Tag mismatch");
    }
    return (String)this.constants[paramInt];
  }
  
  public Reference getRef(int paramInt)
    throws ClassFormatException
  {
    if (paramInt == 0) {
      return null;
    }
    if ((this.tags[paramInt] != 9) && (this.tags[paramInt] != 10) && (this.tags[paramInt] != 11)) {
      throw new ClassFormatException("Tag mismatch");
    }
    if (this.constants[paramInt] == null)
    {
      int i = this.indices1[paramInt];
      int j = this.indices2[paramInt];
      if (this.tags[j] != 12) {
        throw new ClassFormatException("Tag mismatch");
      }
      String str1 = getUTF8(this.indices2[j]);
      try
      {
        if (this.tags[paramInt] == 9) {
          TypeSignature.checkTypeSig(str1);
        } else {
          TypeSignature.checkMethodTypeSig(str1);
        }
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        throw new ClassFormatException(localIllegalArgumentException.getMessage());
      }
      String str2 = getClassType(i);
      this.constants[paramInt] = Reference.getReference(str2, getUTF8(this.indices1[j]), str1);
    }
    return (Reference)this.constants[paramInt];
  }
  
  public Object getConstant(int paramInt)
    throws ClassFormatException
  {
    if (paramInt == 0) {
      throw new ClassFormatException("null constant");
    }
    switch (this.tags[paramInt])
    {
    case 3: 
    case 4: 
    case 5: 
    case 6: 
      return this.constants[paramInt];
    case 8: 
      return getUTF8(this.indices1[paramInt]);
    case 7: 
      return this.constants[this.indices1[paramInt]];
    }
    throw new ClassFormatException("Tag mismatch: " + this.tags[paramInt]);
  }
  
  public String getClassType(int paramInt)
    throws ClassFormatException
  {
    if (paramInt == 0) {
      return null;
    }
    if (this.tags[paramInt] != 7) {
      throw new ClassFormatException("Tag mismatch");
    }
    String str = getUTF8(this.indices1[paramInt]);
    if (str.charAt(0) != '[') {
      str = ("L" + str + ';').intern();
    }
    try
    {
      TypeSignature.checkTypeSig(str);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new ClassFormatException(localIllegalArgumentException.getMessage());
    }
    return str;
  }
  
  public String getClassName(int paramInt)
    throws ClassFormatException
  {
    if (paramInt == 0) {
      return null;
    }
    if (this.tags[paramInt] != 7) {
      throw new ClassFormatException("Tag mismatch");
    }
    String str = getUTF8(this.indices1[paramInt]);
    try
    {
      TypeSignature.checkTypeSig("L" + str + ";");
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new ClassFormatException(localIllegalArgumentException.getMessage());
    }
    return str.replace('/', '.').intern();
  }
  
  public String toString(int paramInt)
  {
    switch (this.tags[paramInt])
    {
    case 7: 
      return "Class " + toString(this.indices1[paramInt]);
    case 8: 
      return "String \"" + toString(this.indices1[paramInt]) + "\"";
    case 3: 
      return "Int " + this.constants[paramInt].toString();
    case 4: 
      return "Float " + this.constants[paramInt].toString();
    case 5: 
      return "Long " + this.constants[paramInt].toString();
    case 6: 
      return "Double " + this.constants[paramInt].toString();
    case 1: 
      return this.constants[paramInt].toString();
    case 9: 
      return "Fieldref: " + toString(this.indices1[paramInt]) + "; " + toString(this.indices2[paramInt]);
    case 10: 
      return "Methodref: " + toString(this.indices1[paramInt]) + "; " + toString(this.indices2[paramInt]);
    case 11: 
      return "Interfaceref: " + toString(this.indices1[paramInt]) + "; " + toString(this.indices2[paramInt]);
    case 12: 
      return "Name " + toString(this.indices1[paramInt]) + "; Type " + toString(this.indices2[paramInt]);
    }
    return "unknown tag: " + this.tags[paramInt];
  }
  
  public int size()
  {
    return this.count;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("[ null");
    for (int i = 1; i < this.count; i++) {
      localStringBuffer.append(", ").append(i).append(" = ").append(toString(i));
    }
    localStringBuffer.append(" ]");
    return localStringBuffer.toString();
  }
}


