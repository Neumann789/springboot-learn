package jode.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;

public class GrowableConstantPool
  extends ConstantPool
{
  Hashtable entryToIndex = new Hashtable();
  boolean written = false;
  
  public GrowableConstantPool()
  {
    this.count = 1;
    this.tags = new int[''];
    this.indices1 = new int[''];
    this.indices2 = new int[''];
    this.constants = new Object[''];
  }
  
  public final void grow(int paramInt)
  {
    if (this.written) {
      throw new IllegalStateException("adding to written ConstantPool");
    }
    if (this.tags.length < paramInt)
    {
      int i = Math.max(this.tags.length * 2, paramInt);
      int[] arrayOfInt = new int[i];
      System.arraycopy(this.tags, 0, arrayOfInt, 0, this.count);
      this.tags = arrayOfInt;
      arrayOfInt = new int[i];
      System.arraycopy(this.indices1, 0, arrayOfInt, 0, this.count);
      this.indices1 = arrayOfInt;
      arrayOfInt = new int[i];
      System.arraycopy(this.indices2, 0, arrayOfInt, 0, this.count);
      this.indices2 = arrayOfInt;
      Object[] arrayOfObject = new Object[i];
      System.arraycopy(this.constants, 0, arrayOfObject, 0, this.count);
      this.constants = arrayOfObject;
    }
  }
  
  private int putConstant(int paramInt, Object paramObject)
  {
    Key localKey = new Key(paramInt, paramObject, 0);
    Integer localInteger = (Integer)this.entryToIndex.get(localKey);
    if (localInteger != null) {
      return localInteger.intValue();
    }
    int i = this.count;
    grow(this.count + 1);
    this.tags[i] = paramInt;
    this.constants[i] = paramObject;
    this.entryToIndex.put(localKey, new Integer(i));
    this.count += 1;
    return i;
  }
  
  private int putLongConstant(int paramInt, Object paramObject)
  {
    Key localKey = new Key(paramInt, paramObject, 0);
    Integer localInteger = (Integer)this.entryToIndex.get(localKey);
    if (localInteger != null) {
      return localInteger.intValue();
    }
    int i = this.count;
    grow(this.count + 2);
    this.tags[i] = paramInt;
    this.tags[(i + 1)] = (-paramInt);
    this.constants[i] = paramObject;
    this.entryToIndex.put(localKey, new Integer(i));
    this.count += 2;
    return i;
  }
  
  int putIndexed(int paramInt1, Object paramObject, int paramInt2, int paramInt3)
  {
    Key localKey = new Key(paramInt1, paramObject, paramInt3);
    Integer localInteger = (Integer)this.entryToIndex.get(localKey);
    if (localInteger != null)
    {
      int i = localInteger.intValue();
      this.indices1[i] = paramInt2;
      this.indices2[i] = paramInt3;
      return i;
    }
    grow(this.count + 1);
    this.tags[this.count] = paramInt1;
    this.indices1[this.count] = paramInt2;
    this.indices2[this.count] = paramInt3;
    this.entryToIndex.put(localKey, new Integer(this.count));
    return this.count++;
  }
  
  public final int putUTF8(String paramString)
  {
    return putConstant(1, paramString);
  }
  
  public int putClassName(String paramString)
  {
    paramString = paramString.replace('.', '/');
    TypeSignature.checkTypeSig("L" + paramString + ";");
    return putIndexed(7, paramString, putUTF8(paramString), 0);
  }
  
  public int putClassType(String paramString)
  {
    TypeSignature.checkTypeSig(paramString);
    if (paramString.charAt(0) == 'L') {
      paramString = paramString.substring(1, paramString.length() - 1);
    } else if (paramString.charAt(0) != '[') {
      throw new IllegalArgumentException("wrong class type: " + paramString);
    }
    return putIndexed(7, paramString, putUTF8(paramString), 0);
  }
  
  public int putRef(int paramInt, Reference paramReference)
  {
    String str1 = paramReference.getClazz();
    String str2 = paramReference.getType();
    if (paramInt == 9) {
      TypeSignature.checkTypeSig(str2);
    } else {
      TypeSignature.checkMethodTypeSig(str2);
    }
    int i = putClassType(str1);
    int j = putUTF8(paramReference.getName());
    int k = putUTF8(str2);
    int m = putIndexed(12, paramReference.getName(), j, k);
    return putIndexed(paramInt, str1, i, m);
  }
  
  public int putConstant(Object paramObject)
  {
    if ((paramObject instanceof String)) {
      return putIndexed(8, paramObject, putUTF8((String)paramObject), 0);
    }
    int i;
    if ((paramObject instanceof Integer)) {
      i = 3;
    } else if ((paramObject instanceof Float)) {
      i = 4;
    } else {
      throw new IllegalArgumentException("illegal constant " + paramObject + " of type: " + paramObject.getClass());
    }
    return putConstant(i, paramObject);
  }
  
  public int putLongConstant(Object paramObject)
  {
    int i;
    if ((paramObject instanceof Long)) {
      i = 5;
    } else if ((paramObject instanceof Double)) {
      i = 6;
    } else {
      throw new IllegalArgumentException("illegal long constant " + paramObject + " of type: " + paramObject.getClass());
    }
    return putLongConstant(i, paramObject);
  }
  
  public int reserveConstant(Object paramObject)
  {
    if ((paramObject instanceof String)) {
      return putIndexed(8, paramObject, -1, 0);
    }
    return putConstant(paramObject);
  }
  
  public int reserveLongConstant(Object paramObject)
  {
    return putLongConstant(paramObject);
  }
  
  public int copyConstant(ConstantPool paramConstantPool, int paramInt)
    throws ClassFormatException
  {
    return putConstant(paramConstantPool.getConstant(paramInt));
  }
  
  public void write(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    this.written = true;
    paramDataOutputStream.writeShort(this.count);
    for (int i = 1; i < this.count; i++)
    {
      int j = this.tags[i];
      paramDataOutputStream.writeByte(j);
      switch (j)
      {
      case 7: 
        paramDataOutputStream.writeShort(this.indices1[i]);
        break;
      case 9: 
      case 10: 
      case 11: 
        paramDataOutputStream.writeShort(this.indices1[i]);
        paramDataOutputStream.writeShort(this.indices2[i]);
        break;
      case 8: 
        paramDataOutputStream.writeShort(this.indices1[i]);
        break;
      case 3: 
        paramDataOutputStream.writeInt(((Integer)this.constants[i]).intValue());
        break;
      case 4: 
        paramDataOutputStream.writeFloat(((Float)this.constants[i]).floatValue());
        break;
      case 5: 
        paramDataOutputStream.writeLong(((Long)this.constants[i]).longValue());
        i++;
        break;
      case 6: 
        paramDataOutputStream.writeDouble(((Double)this.constants[i]).doubleValue());
        i++;
        break;
      case 12: 
        paramDataOutputStream.writeShort(this.indices1[i]);
        paramDataOutputStream.writeShort(this.indices2[i]);
        break;
      case 1: 
        paramDataOutputStream.writeUTF((String)this.constants[i]);
        break;
      case 2: 
      default: 
        throw new ClassFormatException("unknown constant tag");
      }
    }
  }
  
  private class Key
  {
    int tag;
    Object objData;
    int intData;
    
    public Key(int paramInt1, Object paramObject, int paramInt2)
    {
      this.tag = paramInt1;
      this.objData = paramObject;
      this.intData = paramInt2;
    }
    
    public int hashCode()
    {
      return this.tag ^ this.objData.hashCode() ^ this.intData;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof Key))
      {
        Key localKey = (Key)paramObject;
        return (this.tag == localKey.tag) && (this.intData == localKey.intData) && (this.objData.equals(localKey.objData));
      }
      return false;
    }
  }
}


