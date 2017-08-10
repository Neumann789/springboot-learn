 package com.javosize.compiler;
 
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.net.URI;

import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
 import javax.tools.SimpleJavaFileObject;
 
 
 public class CompiledCode
   extends SimpleJavaFileObject
 {
   private ByteArrayOutputStream baos = new ByteArrayOutputStream();
   
   public CompiledCode(String className) throws Exception {
     super(new URI(className), JavaFileObject.Kind.CLASS);
   }
   
   public OutputStream openOutputStream() throws IOException
   {
     return this.baos;
   }
   
   public byte[] getByteCode() {
     return this.baos.toByteArray();
   }
 }


