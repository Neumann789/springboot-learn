 package com.javosize.compiler;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.net.URI;

import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
 import javax.tools.SimpleJavaFileObject;
 
 
 public class SourceCode
   extends SimpleJavaFileObject
 {
   private String contents = null;
   private byte[] bytes = null;
   private ByteArrayOutputStream baos = null;
   
   public SourceCode(String className, String contents) throws Exception {
     super(URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
     this.contents = contents;
   }
   
   public SourceCode(URI uri, JavaFileObject.Kind kind, byte[] bytes) {
     super(uri, kind);
     this.bytes = bytes;
   }
   
   public SourceCode(URI uri, JavaFileObject.Kind kind, ByteArrayOutputStream baos) {
     super(uri, kind);
     this.baos = baos;
   }
   
   public InputStream openInputStream() throws IOException {
     if (this.bytes != null) {
       return new ByteArrayInputStream(this.bytes);
     }
     return super.openInputStream();
   }
   
   public OutputStream openOutputStream() throws IOException
   {
     if (this.baos != null) {
       return this.baos;
     }
     return super.openOutputStream();
   }
   
   public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException
   {
     return this.contents;
   }
 }


