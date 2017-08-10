 package com.javosize.compiler;
 
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.net.URI;
 import java.util.LinkedHashMap;
 import java.util.Map;
 import java.util.Map.Entry;
 import javax.tools.FileObject;
 import javax.tools.ForwardingJavaFileManager;
 import javax.tools.JavaFileManager;
 import javax.tools.JavaFileManager.Location;
 import javax.tools.JavaFileObject;
 import javax.tools.JavaFileObject.Kind;
 import javax.tools.StandardLocation;
 
 
 
 
 public class ExtendedStandardJavaFileManager
   extends ForwardingJavaFileManager<JavaFileManager>
 {
   private final Map<String, ByteArrayOutputStream> buffers = new LinkedHashMap();
   
 
 
 
 
   protected ExtendedStandardJavaFileManager(JavaFileManager fileManager)
   {
     super(fileManager);
   }
   
   public JavaFileObject getJavaFileForInput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind) throws IOException
   {
     if ((location == StandardLocation.CLASS_OUTPUT) && (this.buffers.containsKey(className)) && (kind == JavaFileObject.Kind.CLASS)) {
       byte[] bytes = ((ByteArrayOutputStream)this.buffers.get(className)).toByteArray();
       return new SourceCode(URI.create(className), kind, bytes);
     }
     return this.fileManager.getJavaFileForInput(location, className, kind);
   }
   
   public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException
   {
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     this.buffers.put(className, baos);
     return new SourceCode(URI.create(className), kind, baos);
   }
   
   public void clearBuffers() {
     this.buffers.clear();
   }
   
   public Map<String, byte[]> getAllBuffers() {
     Map<String, byte[]> ret = new LinkedHashMap(this.buffers.size() * 2);
     for (Map.Entry<String, ByteArrayOutputStream> entry : this.buffers.entrySet()) {
       ret.put(entry.getKey(), ((ByteArrayOutputStream)entry.getValue()).toByteArray());
     }
     return ret;
   }
 }


