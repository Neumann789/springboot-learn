 package com.strobel.assembler.metadata;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.assembler.ir.ConstantPool;
 import com.strobel.assembler.ir.ConstantPool.TypeInfoEntry;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import java.util.Arrays;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ArrayTypeLoader
   implements ITypeLoader
 {
   private static final Logger LOG = Logger.getLogger(ArrayTypeLoader.class.getSimpleName());
   private final Buffer _buffer;
   private Throwable _parseError;
   private boolean _parsed;
   private String _className;
   
   public ArrayTypeLoader(@NotNull byte[] bytes)
   {
     VerifyArgument.notNull(bytes, "bytes");
     this._buffer = new Buffer(Arrays.copyOf(bytes, bytes.length));
   }
   
   public String getClassNameFromArray() {
     ensureParsed(true);
     return this._className;
   }
   
   public boolean tryLoadType(String internalName, Buffer buffer)
   {
     ensureParsed(false);
     
     if (StringUtilities.equals(internalName, this._className)) {
       buffer.reset(this._buffer.size());
       buffer.putByteArray(this._buffer.array(), 0, this._buffer.size());
       buffer.position(0);
       return true;
     }
     
     return false;
   }
   
   private void ensureParsed(boolean throwOnError) {
     if (this._parsed) {
       if ((throwOnError) && (this._parseError != null)) {
         throw new IllegalStateException("Error parsing classfile header.", this._parseError);
       }
       return;
     }
     
     if (LOG.isLoggable(Level.FINE)) {
       LOG.log(Level.FINE, "Parsing classfile header from user-provided buffer...");
     }
     try
     {
       this._className = getInternalNameFromClassFile(this._buffer);
       
       if (LOG.isLoggable(Level.FINE)) {
         LOG.log(Level.FINE, "Parsed header for class: " + this._className);
       }
     }
     catch (Throwable t) {
       this._parseError = t;
       
       if (LOG.isLoggable(Level.FINE)) {
         LOG.log(Level.FINE, "Error parsing classfile header.", t);
       }
       
       if (throwOnError) {
         throw new IllegalStateException("Error parsing classfile header.", t);
       }
     }
     finally {
       this._parsed = true;
     }
   }
   
   private static String getInternalNameFromClassFile(Buffer b) {
     long magic = b.readInt() & 0xFFFFFFFF;
     
     if (magic != 3405691582L) {
       throw new IllegalStateException("Bad magic number: 0x" + Long.toHexString(magic));
     }
     
     b.readUnsignedShort();
     b.readUnsignedShort();
     
     ConstantPool constantPool = ConstantPool.read(b);
     
     b.readUnsignedShort();
     
     ConstantPool.TypeInfoEntry thisClass = (ConstantPool.TypeInfoEntry)constantPool.getEntry(b.readUnsignedShort());
     
     b.position(0);
     
     return thisClass.getName();
   }
 }


