 package org.jboss.jreadline.terminal;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.OutputStreamWriter;
 import java.io.PrintWriter;
 import java.io.Writer;
 
 
 
 
 
 
 
 
 
 
 
 public class TestTerminal
   implements Terminal
 {
   private InputStream input;
   private Writer writer;
   
   public void init(InputStream inputStream, OutputStream stdOut, OutputStream stdErr)
   {
     this.input = inputStream;
     this.writer = new PrintWriter(new OutputStreamWriter(stdOut));
   }
   
   public int[] read(boolean readAhead) throws IOException
   {
     int input = this.input.read();
     int available = this.input.available();
     if ((available > 1) && (readAhead)) {
       int[] in = new int[available];
       in[0] = input;
       for (int c = 1; c < available; c++) {
         in[c] = this.input.read();
       }
       return in;
     }
     
     return new int[] { input };
   }
   
   public void writeToStdOut(String out) throws IOException
   {
     if ((out != null) && (out.length() > 0)) {
       this.writer.write(out);
       this.writer.flush();
     }
   }
   
   public void writeToStdOut(char[] out) throws IOException
   {
     if ((out != null) && (out.length > 0)) {
       this.writer.write(out);
       this.writer.flush();
     }
   }
   
   public void writeToStdOut(char out) throws IOException
   {
     this.writer.write(out);
     this.writer.flush();
   }
   
   public void writeToStdErr(String err) throws IOException
   {
     if ((err != null) && (err.length() > 0)) {
       this.writer.write(err);
       this.writer.flush();
     }
   }
   
   public void writeToStdErr(char[] err) throws IOException
   {
     if ((err != null) && (err.length > 0)) {
       this.writer.write(err);
       this.writer.flush();
     }
   }
   
   public void writeToStdErr(char err) throws IOException
   {
     this.writer.write(err);
     this.writer.flush();
   }
   
   public int getHeight()
   {
     return 24;
   }
   
   public int getWidth()
   {
     return 80;
   }
   
   public boolean isEchoEnabled()
   {
     return false;
   }
   
   public void reset()
     throws IOException
   {}
 }


