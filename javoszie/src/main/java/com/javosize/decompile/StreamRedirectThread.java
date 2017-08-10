 package com.javosize.decompile;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.Reader;
 import java.io.Writer;
 
 
 public class StreamRedirectThread
   extends Thread
 {
   private final Reader in;
   private final Writer out;
   private Exception ex;
   
   public StreamRedirectThread(String name, InputStream in, Writer out)
   {
     super(name);
     this.in = new InputStreamReader(in);
     this.out = out;
     setPriority(9);
   }
   
   public void run() {
     try {
       char[] cbuf = new char['ࠀ'];
       int count;
       while ((count = this.in.read(cbuf, 0, 2048)) >= 0)
       {
         this.out.write(cbuf, 0, count);
         this.out.flush();
       }
     } catch (IOException exc) {
       this.ex = exc;
     }
   }
   
   public Exception getException() {
     return this.ex;
   }
 }


