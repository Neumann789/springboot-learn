 package com.strobel.assembler.metadata;
 
 import com.strobel.assembler.ir.ConstantPool;
 import com.strobel.assembler.ir.ConstantPool.TypeInfoEntry;
 import com.strobel.core.ExceptionUtilities;
 import com.strobel.core.VerifyArgument;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.jar.JarEntry;
 import java.util.jar.JarFile;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class JarTypeLoader
   implements ITypeLoader
 {
   private static final Logger LOG = Logger.getLogger(JarTypeLoader.class.getSimpleName());
   private final JarFile _jarFile;
   private final Map<String, String> _knownMappings;
   
   public JarTypeLoader(JarFile jarFile)
   {
     this._jarFile = ((JarFile)VerifyArgument.notNull(jarFile, "jarFile"));
     this._knownMappings = new HashMap();
   }
   
   public boolean tryLoadType(String internalName, Buffer buffer)
   {
     try {
       if (LOG.isLoggable(Level.FINE)) {
         LOG.fine("Attempting to load type: " + internalName + "...");
       }
       
       JarEntry entry = this._jarFile.getJarEntry(internalName + ".class");
       
       if (entry == null) {
         String mappedName = (String)this._knownMappings.get(internalName);
         
         return (mappedName != null) && (!mappedName.equals(internalName)) && (tryLoadType(mappedName, buffer));
       }
       
 
       InputStream inputStream = this._jarFile.getInputStream(entry);
       
       int remainingBytes = inputStream.available();
       
       buffer.reset(remainingBytes);
       
       while (remainingBytes > 0) {
         int bytesRead = inputStream.read(buffer.array(), buffer.position(), remainingBytes);
         
         if (bytesRead < 0) {
           break;
         }
         
         buffer.position(buffer.position() + bytesRead);
         remainingBytes -= bytesRead;
       }
       
       buffer.position(0);
       
       String actualName = getInternalNameFromClassFile(buffer);
       
       if ((actualName != null) && (!actualName.equals(internalName))) {
         this._knownMappings.put(actualName, internalName);
       }
       
       if (LOG.isLoggable(Level.FINE)) {
         LOG.fine("Type loaded from " + this._jarFile.getName() + "!" + entry.getName() + ".");
       }
       
       return true;
     }
     catch (IOException e) {
       throw ExceptionUtilities.asRuntimeException(e);
     }
   }
   
   private static String getInternalNameFromClassFile(Buffer b) {
     long magic = b.readInt() & 0xFFFFFFFF;
     
     if (magic != 3405691582L) {
       return null;
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


