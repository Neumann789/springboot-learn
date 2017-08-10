 package com.strobel.assembler.ir.attributes;
 
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class BlobAttribute
   extends SourceAttribute
 {
   private final int _dataOffset;
   private final byte[] _data;
   
   public BlobAttribute(String name, byte[] data)
   {
     this(name, data, -1);
   }
   
   public BlobAttribute(String name, byte[] data, int dataOffset) {
     super(name, data.length);
     this._dataOffset = dataOffset;
     this._data = ((byte[])VerifyArgument.notNull(data, "data"));
   }
   
   public int getDataOffset() {
     return this._dataOffset;
   }
   
   public byte[] getData() {
     return this._data;
   }
 }


