 package org.jboss.jreadline.terminal;
 
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.OutputStreamWriter;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.io.Writer;
 import java.util.logging.Logger;
 import org.jboss.jreadline.util.LoggerUtil;
 
 
 
 
 
 
 
 
 
 
 
 public class POSIXTerminal
   implements Terminal
 {
   private int height = -1;
   private int width = -1;
   private boolean echoEnabled;
   private String ttyConfig;
   private String ttyProps;
   private long ttyPropsLastFetched;
   private boolean restored = false;
   
   private InputStream input;
   
   private Writer stdOut;
   private Writer stdErr;
   private static final Logger logger = LoggerUtil.getLogger(POSIXTerminal.class.getName());
   
 
   public void init(InputStream inputStream, OutputStream stdOut, OutputStream stdErr)
   {
     try
     {
       this.ttyConfig = stty("-g");
       
 
       if ((this.ttyConfig.length() == 0) || (
         (!this.ttyConfig.contains("=")) && (!this.ttyConfig.contains(":")))) {
         throw new RuntimeException("Unrecognized stty code: " + this.ttyConfig);
       }
       
 
 
       stty("-ixon -icanon min 1");
       
 
       stty("-echo");
       this.echoEnabled = false;
       
 
       this.input = inputStream;
     }
     catch (IOException ioe) {
       System.err.println("TTY failed with: " + ioe.getMessage());
     }
     catch (InterruptedException e) {
       e.printStackTrace();
     }
     
     this.stdOut = new PrintWriter(new OutputStreamWriter(stdOut));
     this.stdErr = new PrintWriter(new OutputStreamWriter(stdErr));
   }
   
   public void cleanUp()
   {
     try {
       stty("icanon echo");
     }
     catch (Exception localException) {}
   }
   
 
 
 
   public int[] read(boolean readAhead)
     throws IOException
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
   
 
 
   public void writeToStdOut(String out)
     throws IOException
   {
     if ((out != null) && (out.length() > 0)) {
       this.stdOut.write(out);
       this.stdOut.flush();
     }
   }
   
 
 
   public void writeToStdOut(char[] out)
     throws IOException
   {
     if ((out != null) && (out.length > 0)) {
       this.stdOut.write(out);
       this.stdOut.flush();
     }
   }
   
 
 
   public void writeToStdOut(char out)
     throws IOException
   {
     this.stdOut.write(out);
     this.stdOut.flush();
   }
   
 
 
   public void writeToStdErr(String err)
     throws IOException
   {
     if ((err != null) && (err.length() > 0)) {
       this.stdErr.write(err);
       this.stdErr.flush();
     }
   }
   
 
 
   public void writeToStdErr(char[] err)
     throws IOException
   {
     if ((err != null) && (err.length > 0)) {
       this.stdErr.write(err);
       this.stdErr.flush();
     }
   }
   
 
 
   public void writeToStdErr(char err)
     throws IOException
   {
     this.stdErr.write(err);
     this.stdErr.flush();
   }
   
 
 
 
   public int getHeight()
   {
     if (this.height < 0) {
       try {
         this.height = getTerminalProperty("rows");
       }
       catch (Exception e) {
         logger.severe("Failed to fetch terminal height: " + e.getMessage());
       }
     }
     
     if (this.height < 0) {
       this.height = 24;
     }
     return this.height;
   }
   
 
 
 
   public int getWidth()
   {
     if (this.width < 0) {
       try {
         this.width = getTerminalProperty("columns");
       }
       catch (Exception e) {
         logger.severe("Failed to fetch terminal width: " + e.getMessage());
       }
     }
     
     if (this.width < 0) {
       this.width = 80;
     }
     return this.width;
   }
   
 
 
 
   public boolean isEchoEnabled()
   {
     return this.echoEnabled;
   }
   
 
 
   public void reset()
     throws IOException
   {
     if ((!this.restored) && 
       (this.ttyConfig != null)) {
       try {
         stty(this.ttyConfig);
         this.ttyConfig = null;
         this.restored = true;
       }
       catch (InterruptedException e) {
         logger.severe("Failed to reset terminal: " + e.getMessage());
       }
     }
   }
   
   private int getTerminalProperty(String prop)
     throws IOException, InterruptedException
   {
     if ((this.ttyProps == null) || (System.currentTimeMillis() - this.ttyPropsLastFetched > 1000L)) {
       this.ttyProps = stty("-a");
       this.ttyPropsLastFetched = System.currentTimeMillis();
     }
     
 
 
 
     for (String str : this.ttyProps.split(";")) {
       str = str.trim();
       
       if (str.startsWith(prop)) {
         int index = str.lastIndexOf(" ");
         
         return Integer.parseInt(str.substring(index).trim());
       }
       if (str.endsWith(prop)) {
         int index = str.indexOf(" ");
         
         return Integer.parseInt(str.substring(0, index).trim());
       }
     }
     
     return -1;
   }
   
 
 
 
 
 
 
 
   protected static String stty(String args)
     throws IOException, InterruptedException
   {
     return exec("stty " + args + " < /dev/tty").trim();
   }
   
 
 
 
 
 
 
   private static String exec(String cmd)
     throws IOException, InterruptedException
   {
     return exec(new String[] { "sh", "-c", cmd });
   }
   
 
 
 
 
 
 
   private static String exec(String[] cmd)
     throws IOException, InterruptedException
   {
     bout = new ByteArrayOutputStream();
     
     Process p = Runtime.getRuntime().exec(cmd);
     
     InputStream in = null;
     InputStream err = null;
     OutputStream out = null;
     try
     {
       in = p.getInputStream();
       int c;
       while ((c = in.read()) != -1) {
         bout.write(c);
       }
       
       err = p.getErrorStream();
       
       while ((c = err.read()) != -1) {
         bout.write(c);
       }
       
       out = p.getOutputStream();
       
       p.waitFor();
       
 
 
 
 
 
 
 
 
 
 
 
 
 
 
       return new String(bout.toByteArray());
     }
     finally
     {
       try
       {
         if (in != null)
           in.close();
         if (err != null)
           err.close();
         if (out != null) {
           out.close();
         }
       } catch (Exception e) {
         logger.warning("Failed to close streams");
       }
     }
   }
 }


