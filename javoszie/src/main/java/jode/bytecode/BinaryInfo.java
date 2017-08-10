package jode.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import jode.util.SimpleMap;

public class BinaryInfo
{
  public static final int HIERARCHY = 1;
  public static final int FIELDS = 2;
  public static final int METHODS = 4;
  public static final int CONSTANTS = 8;
  public static final int KNOWNATTRIBS = 16;
  public static final int INNERCLASSES = 32;
  public static final int OUTERCLASSES = 64;
  public static final int UNKNOWNATTRIBS = 128;
  public static final int FULLINFO = 255;
  public static final int MOSTINFO = 127;
  public static final int REFLECTINFO = 111;
  private Map unknownAttributes = null;
  
  protected void skipAttributes(DataInputStream paramDataInputStream)
    throws IOException
  {
    int i = paramDataInputStream.readUnsignedShort();
    for (int j = 0; j < i; j++)
    {
      paramDataInputStream.readUnsignedShort();
      long l2;
      for (long l1 = paramDataInputStream.readInt(); l1 > 0L; l1 -= l2)
      {
        l2 = paramDataInputStream.skip(l1);
        if (l2 == 0L) {
          throw new EOFException("Can't skip. EOF?");
        }
      }
    }
  }
  
  protected int getKnownAttributeCount()
  {
    return 0;
  }
  
  protected void readAttribute(String paramString, int paramInt1, ConstantPool paramConstantPool, DataInputStream paramDataInputStream, int paramInt2)
    throws IOException
  {
    byte[] arrayOfByte = new byte[paramInt1];
    paramDataInputStream.readFully(arrayOfByte);
    if ((paramInt2 & 0x80) != 0)
    {
      if (this.unknownAttributes == null) {
        this.unknownAttributes = new SimpleMap();
      }
      this.unknownAttributes.put(paramString, arrayOfByte);
    }
  }
  
  protected void readAttributes(ConstantPool paramConstantPool, DataInputStream paramDataInputStream, int paramInt)
    throws IOException
  {
    int i = paramDataInputStream.readUnsignedShort();
    this.unknownAttributes = null;
    for (int j = 0; j < i; j++)
    {
      String str = paramConstantPool.getUTF8(paramDataInputStream.readUnsignedShort());
      int k = paramDataInputStream.readInt();
      ConstrainedInputStream localConstrainedInputStream = new ConstrainedInputStream(k, paramDataInputStream);
      readAttribute(str, k, paramConstantPool, new DataInputStream(localConstrainedInputStream), paramInt);
      localConstrainedInputStream.skipRemaining();
    }
  }
  
  public void dropInfo(int paramInt)
  {
    if ((paramInt & 0x80) != 0) {
      this.unknownAttributes = null;
    }
  }
  
  protected void prepareAttributes(GrowableConstantPool paramGrowableConstantPool)
  {
    if (this.unknownAttributes == null) {
      return;
    }
    Iterator localIterator = this.unknownAttributes.keySet().iterator();
    while (localIterator.hasNext()) {
      paramGrowableConstantPool.putUTF8((String)localIterator.next());
    }
  }
  
  protected void writeKnownAttributes(GrowableConstantPool paramGrowableConstantPool, DataOutputStream paramDataOutputStream)
    throws IOException
  {}
  
  protected void writeAttributes(GrowableConstantPool paramGrowableConstantPool, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    int i = getKnownAttributeCount();
    if (this.unknownAttributes != null) {
      i += this.unknownAttributes.size();
    }
    paramDataOutputStream.writeShort(i);
    writeKnownAttributes(paramGrowableConstantPool, paramDataOutputStream);
    if (this.unknownAttributes != null)
    {
      Iterator localIterator = this.unknownAttributes.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        String str = (String)localEntry.getKey();
        byte[] arrayOfByte = (byte[])localEntry.getValue();
        paramDataOutputStream.writeShort(paramGrowableConstantPool.putUTF8(str));
        paramDataOutputStream.writeInt(arrayOfByte.length);
        paramDataOutputStream.write(arrayOfByte);
      }
    }
  }
  
  public int getAttributeSize()
  {
    int i = 2;
    if (this.unknownAttributes != null)
    {
      Iterator localIterator = this.unknownAttributes.values().iterator();
      while (localIterator.hasNext()) {
        i += 6 + ((byte[])localIterator.next()).length;
      }
    }
    return i;
  }
  
  public byte[] findAttribute(String paramString)
  {
    if (this.unknownAttributes != null) {
      return (byte[])this.unknownAttributes.get(paramString);
    }
    return null;
  }
  
  public Iterator getAttributes()
  {
    if (this.unknownAttributes != null) {
      return this.unknownAttributes.values().iterator();
    }
    return Collections.EMPTY_SET.iterator();
  }
  
  public void setAttribute(String paramString, byte[] paramArrayOfByte)
  {
    if (this.unknownAttributes == null) {
      this.unknownAttributes = new SimpleMap();
    }
    this.unknownAttributes.put(paramString, paramArrayOfByte);
  }
  
  public byte[] removeAttribute(String paramString)
  {
    if (this.unknownAttributes != null) {
      return (byte[])this.unknownAttributes.remove(paramString);
    }
    return null;
  }
  
  public void removeAllAttributes()
  {
    this.unknownAttributes = null;
  }
  
  static class ConstrainedInputStream
    extends FilterInputStream
  {
    int length;
    
    public ConstrainedInputStream(int paramInt, InputStream paramInputStream)
    {
      super();
      this.length = paramInt;
    }
    
    public int read()
      throws IOException
    {
      if (this.length > 0)
      {
        int i = super.read();
        this.length -= 1;
        return i;
      }
      throw new EOFException();
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (this.length < paramInt2) {
        paramInt2 = this.length;
      }
      if (paramInt2 == 0) {
        return -1;
      }
      int i = super.read(paramArrayOfByte, paramInt1, paramInt2);
      this.length -= i;
      return i;
    }
    
    public int read(byte[] paramArrayOfByte)
      throws IOException
    {
      return read(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      if (this.length < paramLong) {
        paramLong = this.length;
      }
      paramLong = super.skip(paramLong);
      this.length -= (int)paramLong;
      return paramLong;
    }
    
    public void skipRemaining()
      throws IOException
    {
      while (this.length > 0)
      {
        int i = (int)skip(this.length);
        if (i == 0) {
          throw new EOFException();
        }
        this.length -= i;
      }
    }
  }
}


