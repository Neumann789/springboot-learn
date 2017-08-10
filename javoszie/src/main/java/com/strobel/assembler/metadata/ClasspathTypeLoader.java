 package com.strobel.assembler.metadata;
 
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import java.io.File;
 import java.io.IOException;
 import java.lang.reflect.UndeclaredThrowableException;
 import java.net.MalformedURLException;
 import java.net.URI;
 import java.net.URL;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 import java.util.regex.Pattern;
 import sun.misc.Resource;
 import sun.misc.URLClassPath;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ClasspathTypeLoader
   implements ITypeLoader
 {
   private static final Logger LOG = Logger.getLogger(ClasspathTypeLoader.class.getSimpleName());
   private final URLClassPath _classPath;
   
   public ClasspathTypeLoader()
   {
     this(StringUtilities.join(System.getProperty("path.separator"), new String[] { System.getProperty("java.class.path"), System.getProperty("sun.boot.class.path") }));
   }
   
 
 
 
 
 
   public ClasspathTypeLoader(String classPath)
   {
     String[] parts = ((String)VerifyArgument.notNull(classPath, "classPath")).split(Pattern.quote(System.getProperty("path.separator")));
     
 
     URL[] urls = new URL[parts.length];
     
     for (int i = 0; i < parts.length; i++) {
       try {
         urls[i] = new File(parts[i]).toURI().toURL();
       }
       catch (MalformedURLException e) {
         throw new UndeclaredThrowableException(e);
       }
     }
     
     this._classPath = new URLClassPath(urls);
   }
   
   public boolean tryLoadType(String internalName, Buffer buffer)
   {
     if (LOG.isLoggable(Level.FINE)) {
       LOG.fine("Attempting to load type: " + internalName + "...");
     }
     
     String path = internalName.concat(".class");
     Resource resource = this._classPath.getResource(path, false);
     
     if (resource == null) {
       return false;
     }
     
     byte[] data;
     try
     {
       data = resource.getBytes();
       assert data.length==resource.getContentLength();
     }
     catch (IOException e) {
       return false;
     }
     
     buffer.reset(data.length);
     System.arraycopy(data, 0, buffer.array(), 0, data.length);
     
     if (LOG.isLoggable(Level.FINE)) {
       LOG.fine("Type loaded from " + resource.getURL() + ".");
     }
     
     return true;
   }
 }


