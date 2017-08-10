 package org.jboss.jreadline.console.reader;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.concurrent.ArrayBlockingQueue;
 import java.util.concurrent.TimeUnit;
 
 
 
 
 
 
 
 
 
 
 
 
 public class ConsoleInputSession
 {
   private InputStream consoleStream;
   private InputStream externalInputStream;
   private ArrayBlockingQueue<String> blockingQueue = new ArrayBlockingQueue(1000);
   private volatile boolean connected;
   
   public ConsoleInputSession(InputStream consoleStream)
   {
     this.consoleStream = consoleStream;
     this.connected = true;
     
     this.externalInputStream = new InputStream()
     {
       private String b;
       private int c;
       
       public int read() throws IOException {
         try {
           if ((this.b == null) || (this.c == this.b.length())) {
             this.b = ((String)ConsoleInputSession.this.blockingQueue.poll(365L, TimeUnit.DAYS));
             this.c = 0;
           }
           
           if ((this.b != null) && (!this.b.isEmpty())) {
             return this.b.charAt(this.c++);
           }
         }
         catch (InterruptedException localInterruptedException) {}
         
 
         return -1;
       }
       
       public int available()
       {
         if (this.b != null) {
           return this.b.length();
         }
         return 0;
       }
       
       public void close() throws IOException
       {
         ConsoleInputSession.this.stop();
       }
       
     };
     startReader();
   }
   
   private void startReader() {
     Thread readerThread = new Thread()
     {
       public void run() {
         while (ConsoleInputSession.this.connected) {
           try {
             byte[] bBuf = new byte[20];
             int read = ConsoleInputSession.this.consoleStream.read(bBuf);
             
             if (read > 0) {
               ConsoleInputSession.this.blockingQueue.put(new String(bBuf, 0, read));
             }
             
             Thread.sleep(10L);
           }
           catch (IOException e) {
             if (ConsoleInputSession.this.connected) {
               ConsoleInputSession.this.connected = false;
               throw new RuntimeException("broken pipe");
             }
             
 
           }
           catch (InterruptedException localInterruptedException) {}
         }
         
       }
     };
     readerThread.start();
   }
   
   public void interruptPipe() {
     this.blockingQueue.offer("\n");
   }
   
   public void stop() {
     this.connected = false;
     this.blockingQueue.offer("");
   }
   
   public InputStream getExternalInputStream() {
     return this.externalInputStream;
   }
 }


