 package com.strobel.assembler.metadata;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.util.EmptyArrayCache;
 import java.nio.BufferUnderflowException;
 import java.util.Arrays;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Buffer
 {
   private static final int DEFAULT_SIZE = 64;
   private byte[] _data;
   private int _length;
   private int _position;
   
   public Buffer()
   {
     this._data = new byte[64];
     this._length = 64;
   }
   
   public Buffer(byte[] data) {
     this._data = ((byte[])VerifyArgument.notNull(data, "data"));
     this._length = data.length;
   }
   
   public Buffer(int initialSize) {
     this._data = new byte[initialSize];
     this._length = initialSize;
   }
   
   public int size() {
     return this._length;
   }
   
   public void flip() {
     this._length = this._position;
     this._position = 0;
   }
   
   public int position() {
     return this._position;
   }
   
   public void position(int position) {
     if (position > this._length) {
       throw new BufferUnderflowException();
     }
     this._position = position;
   }
   
   public void advance(int length) {
     if (this._position + length > this._length) {
       this._position = this._length;
       throw new BufferUnderflowException();
     }
     this._position += length;
   }
   
   public void reset() {
     reset(64);
   }
   
   public void reset(int initialSize) {
     if (VerifyArgument.isNonNegative(initialSize, "initialSize") == 0) {
       this._data = EmptyArrayCache.EMPTY_BYTE_ARRAY;
     }
     else if ((initialSize > this._data.length) || (initialSize < this._data.length / 4)) {
       this._data = new byte[initialSize];
     }
     this._length = initialSize;
     this._position = 0;
   }
   
   public byte[] array() {
     return this._data;
   }
   
   public int read(byte[] buffer, int offset, int length) {
     if (buffer == null) {
       throw new NullPointerException();
     }
     
     if ((offset < 0) || (length < 0) || (length > buffer.length - offset)) {
       throw new IndexOutOfBoundsException();
     }
     
     if (this._position >= this._length) {
       return -1;
     }
     
     int available = this._length - this._position;
     int actualLength = Math.min(length, available);
     
     if (actualLength <= 0) {
       return 0;
     }
     
     System.arraycopy(this._data, this._position, buffer, offset, actualLength);
     
     this._position += actualLength;
     
     return actualLength;
   }
   
   public String readUtf8() {
     int utfLength = readUnsignedShort();
     byte[] byteBuffer = new byte[utfLength];
     char[] charBuffer = new char[utfLength];
     
 
     int count = 0;
     int charactersRead = 0;
     
     read(byteBuffer, 0, utfLength);
     
     while (count < utfLength) {
       int ch = byteBuffer[count] & 0xFF;
       if (ch > 127) {
         break;
       }
       count++;
       charBuffer[(charactersRead++)] = ((char)ch);
     }
     
     while (count < utfLength) {
       int ch = byteBuffer[count] & 0xFF;
       int ch2;
       switch (ch & 0xE0)
       {
       case 0: 
       case 16: 
       case 32: 
       case 48: 
       case 64: 
       case 80: 
       case 96: 
       case 112: 
         count++;
         charBuffer[(charactersRead++)] = ((char)ch);
         break;
       
 
       case 192: 
         count += 2;
         
         if (count > utfLength) {
           throw new IllegalStateException("malformed input: partial character at end");
         }
         
         ch2 = byteBuffer[(count - 1)];
         
         if ((ch2 & 0xC0) != 128) {
           throw new IllegalStateException("malformed input around byte " + count);
         }
         
         charBuffer[(charactersRead++)] = ((char)((ch & 0x1F) << 6 | ch2 & 0x3F));
         break;
       
 
       case 224: 
         count += 3;
         
         if (count > utfLength) {
           throw new IllegalStateException("malformed input: partial character at end");
         }
         
         ch2 = byteBuffer[(count - 2)];
         int ch3 = byteBuffer[(count - 1)];
         
         if (((ch2 & 0xC0) != 128) || ((ch3 & 0xC0) != 128)) {
           throw new IllegalStateException("malformed input around byte " + (count - 1));
         }
         
         charBuffer[(charactersRead++)] = ((char)((ch & 0xF) << 12 | (ch2 & 0x3F) << 6 | (ch3 & 0x3F) << 0));
         
 
         break;
       
 
       default: 
         throw new IllegalStateException("malformed input around byte " + count);
       }
       
     }
     
     return new String(charBuffer, 0, charactersRead);
   }
   
   public byte readByte() {
     verifyReadableBytes(1);
     return this._data[(this._position++)];
   }
   
   public int readUnsignedByte() {
     verifyReadableBytes(1);
     return this._data[(this._position++)] & 0xFF;
   }
   
   public short readShort() {
     verifyReadableBytes(2);
     return (short)((readUnsignedByte() << 8) + (readUnsignedByte() << 0));
   }
   
   public int readUnsignedShort()
   {
     verifyReadableBytes(2);
     return (readUnsignedByte() << 8) + (readUnsignedByte() << 0);
   }
   
   public int readInt()
   {
     verifyReadableBytes(4);
     return (readUnsignedByte() << 24) + (readUnsignedByte() << 16) + (readUnsignedByte() << 8) + (readUnsignedByte() << 0);
   }
   
 
 
   public long readLong()
   {
     verifyReadableBytes(8);
     return (readUnsignedByte() << 56) + (readUnsignedByte() << 48) + (readUnsignedByte() << 40) + (readUnsignedByte() << 32) + (readUnsignedByte() << 24) + (readUnsignedByte() << 16) + (readUnsignedByte() << 8) + (readUnsignedByte() << 0);
   }
   
 
 
 
 
 
 
   public float readFloat()
   {
     return Float.intBitsToFloat(readInt());
   }
   
   public double readDouble() {
     return Double.longBitsToDouble(readLong());
   }
   
   public Buffer writeByte(int b) {
     ensureWriteableBytes(1);
     
     this._data[(this._position++)] = ((byte)(b & 0xFF));
     
     return this;
   }
   
   public Buffer writeShort(int s) {
     ensureWriteableBytes(2);
     
     this._data[(this._position++)] = ((byte)(s >>> 8 & 0xFF));
     this._data[(this._position++)] = ((byte)(s & 0xFF));
     
     return this;
   }
   
   public Buffer writeInt(int i) {
     ensureWriteableBytes(4);
     
     this._data[(this._position++)] = ((byte)(i >>> 24 & 0xFF));
     this._data[(this._position++)] = ((byte)(i >>> 16 & 0xFF));
     this._data[(this._position++)] = ((byte)(i >>> 8 & 0xFF));
     this._data[(this._position++)] = ((byte)(i & 0xFF));
     
     return this;
   }
   
   public Buffer writeLong(long l) {
     ensureWriteableBytes(8);
     
     int i = (int)(l >>> 32);
     
     this._data[(this._position++)] = ((byte)(i >>> 24 & 0xFF));
     this._data[(this._position++)] = ((byte)(i >>> 16 & 0xFF));
     this._data[(this._position++)] = ((byte)(i >>> 8 & 0xFF));
     this._data[(this._position++)] = ((byte)(i & 0xFF));
     
     i = (int)l;
     
     this._data[(this._position++)] = ((byte)(i >>> 24 & 0xFF));
     this._data[(this._position++)] = ((byte)(i >>> 16 & 0xFF));
     this._data[(this._position++)] = ((byte)(i >>> 8 & 0xFF));
     this._data[(this._position++)] = ((byte)(i & 0xFF));
     
     return this;
   }
   
   public Buffer writeFloat(float f) {
     return writeInt(Float.floatToRawIntBits(f));
   }
   
   public Buffer writeDouble(double d) {
     return writeLong(Double.doubleToRawLongBits(d));
   }
   
   public Buffer writeUtf8(String s)
   {
     int charLength = s.length();
     
     ensureWriteableBytes(2 + charLength);
     
 
 
 
 
 
 
     this._data[(this._position++)] = ((byte)(charLength >>> 8));
     this._data[(this._position++)] = ((byte)charLength);
     
     for (int i = 0; i < charLength; i++) {
       char c = s.charAt(i);
       if ((c >= '\001') && (c <= '')) {
         this._data[(this._position++)] = ((byte)c);
       }
       else {
         int byteLength = i;
         for (int j = i; j < charLength; j++) {
           c = s.charAt(j);
           if ((c >= '\001') && (c <= '')) {
             byteLength++;
           }
           else if (c > '߿') {
             byteLength += 3;
           }
           else {
             byteLength += 2;
           }
         }
         
         this._data[this._position] = ((byte)(byteLength >>> 8));
         this._data[(this._position + 1)] = ((byte)byteLength);
         
         ensureWriteableBytes(2 + byteLength);
         
         for (int j = i; j < charLength; j++) {
           c = s.charAt(j);
           if ((c >= '\001') && (c <= '')) {
             this._data[(this._position++)] = ((byte)c);
           }
           else if (c > '߿') {
             this._data[(this._position++)] = ((byte)(0xE0 | c >> '\f' & 0xF));
             this._data[(this._position++)] = ((byte)(0x80 | c >> '\006' & 0x3F));
             this._data[(this._position++)] = ((byte)(0x80 | c & 0x3F));
           }
           else {
             this._data[(this._position++)] = ((byte)(0xC0 | c >> '\006' & 0x1F));
             this._data[(this._position++)] = ((byte)(0x80 | c & 0x3F));
           }
         }
         break;
       }
     }
     
     return this;
   }
   
   public Buffer putByteArray(byte[] b, int offset, int length) {
     ensureWriteableBytes(length);
     if (b != null) {
       System.arraycopy(b, offset, this._data, this._position, length);
     }
     this._position += length;
     return this;
   }
   
   protected void verifyReadableBytes(int size) {
     if ((VerifyArgument.isNonNegative(size, "size") > 0) && (this._position + size > this._length)) {
       throw new BufferUnderflowException();
     }
   }
   
   protected void ensureWriteableBytes(int size) {
     int minLength = this._position + size;
     
     if (minLength > this._data.length) {
       int length1 = 2 * this._data.length;
       int length2 = this._position + size;
       
       this._data = Arrays.copyOf(this._data, Math.max(length1, length2));
     }
     
     this._length = Math.max(minLength, this._length);
   }
 }


