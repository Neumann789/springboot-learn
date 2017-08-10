 package org.jboss.jreadline.terminal;
 
 import com.javosize.log.Log;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.OutputStreamWriter;
 import java.io.PrintWriter;
 import java.io.Writer;
 import org.fusesource.jansi.AnsiOutputStream;
 import org.fusesource.jansi.WindowsAnsiOutputStream;
 import org.fusesource.jansi.internal.WindowsSupport;
 import org.jboss.jreadline.console.settings.Settings;
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class WindowsTerminal
   implements Terminal
 {
   private Writer stdOut;
   private Writer stdErr;
   private InputStream input;
   private static Log log = new Log(WindowsTerminal.class.getName());
   
   public void init(InputStream inputStream, OutputStream stdOut, OutputStream stdErr)
   {
     if (inputStream == System.in) {
       log.trace("Using System.in");
     }
     
 
     try
     {
       this.stdOut = new PrintWriter(new OutputStreamWriter(new WindowsAnsiOutputStream(stdOut)));
       this.stdErr = new PrintWriter(new OutputStreamWriter(new WindowsAnsiOutputStream(stdErr)));
     }
     catch (Exception ioe) {
       this.stdOut = new PrintWriter(new OutputStreamWriter(new AnsiOutputStream(stdOut)));
       this.stdErr = new PrintWriter(new OutputStreamWriter(new AnsiOutputStream(stdErr)));
     }
     
     this.input = inputStream;
   }
   
   public int[] read(boolean readAhead) throws IOException
   {
     if (Settings.getInstance().isAnsiConsole()) {
       return new int[] { WindowsSupport.readByte() };
     }
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
   
   public void writeToStdOut(String out)
     throws IOException
   {
     if ((out != null) && (out.length() > 0)) {
       this.stdOut.write(out);
       this.stdOut.flush();
     }
   }
   
   public void writeToStdOut(char[] out) throws IOException
   {
     if ((out != null) && (out.length > 0)) {
       this.stdOut.write(out);
       this.stdOut.flush();
     }
   }
   
   public void writeToStdOut(char out) throws IOException
   {
     this.stdOut.write(out);
     this.stdOut.flush();
   }
   
   public void writeToStdErr(String err) throws IOException
   {
     if ((err != null) && (err.length() > 0)) {
       this.stdOut.write(err);
       this.stdOut.flush();
     }
   }
   
   public void writeToStdErr(char[] err) throws IOException
   {
     if ((err != null) && (err.length > 0)) {
       this.stdOut.write(err);
       this.stdOut.flush();
     }
   }
   
   public void writeToStdErr(char err) throws IOException
   {
     this.stdOut.write(err);
     this.stdOut.flush();
   }
   
   public int getHeight()
   {
     return WindowsSupport.getWindowsTerminalHeight();
   }
   
   public int getWidth()
   {
     return WindowsSupport.getWindowsTerminalWidth();
   }
   
   public boolean isEchoEnabled()
   {
     return false;
   }
   
   public void reset()
     throws IOException
   {}
 }


