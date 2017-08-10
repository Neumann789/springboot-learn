 package com.javosize.breakpoints;
 
 import com.javosize.log.Log;
 import java.io.IOException;
 import java.net.ServerSocket;
 import java.net.Socket;
 import java.net.SocketException;
 import java.util.concurrent.BlockingQueue;
 import java.util.concurrent.LinkedBlockingQueue;
 import java.util.concurrent.TimeUnit;
 
 
 public class BreakPointServer
   extends Thread
 {
   private BlockingQueue<Socket> pending = new LinkedBlockingQueue();
   private BreakPointProcessor processor = new BreakPointProcessor();
   private boolean finished = false;
   private int port = 0;
   private static Log log = new Log(BreakPointServer.class.getName());
   private ServerSocket serverSocket;
   private BreakPoint breakPoint = null;
   
   public BreakPointServer(BreakPoint breakPoint) throws IOException {
     this.breakPoint = breakPoint;
     this.serverSocket = new ServerSocket(0);
     this.port = this.serverSocket.getLocalPort();
   }
   
   public void run() {
     try {
       this.processor.start();
       while (!this.finished) {
         Socket s = null;
         try {
           s = this.serverSocket.accept();
         } catch (SocketException se) {
           break;
         }
         this.pending.add(s);
         this.breakPoint.addWaiter();
       }
     } catch (IOException e) {
       log.error("Creating breakpoint server: " + e, e);
     }
   }
   
   public int getPort() {
     return this.port;
   }
   
   public void next() {
     synchronized (this.processor.monitor) {
       this.processor.monitor.notify();
     }
   }
   
   public void finish() {
     this.finished = true;
     try {
       this.serverSocket.close();
     }
     catch (IOException localIOException) {}
     next(); }
   
   private class BreakPointProcessor extends Thread { private BreakPointProcessor() {}
     
     private Object monitor = new Object();
     
     public void run() {
       while (!BreakPointServer.this.finished) {
         Socket s = null;
         try
         {
           while ((!BreakPointServer.this.finished) && ((s = (Socket)BreakPointServer.this.pending.poll(500L, TimeUnit.MILLISECONDS)) == null)) {}
           if (BreakPointServer.this.finished)
           {
 
 
 
             try
             {
 
 
 
 
               s.close();
             }
             catch (Throwable localThrowable) {}
           }
           BreakPointServer.this.breakPoint.addExecuting();
           synchronized (this.monitor) {
             this.monitor.wait();
           }
           
 
           try
           {
             s.close();
           }
           catch (Throwable localThrowable1) {}
           
 
           BreakPointServer.this.breakPoint.addExecuted();
         }
         catch (Throwable localThrowable2) {}finally
         {
           try
           {
             s.close();
           }
           catch (Throwable localThrowable4) {}
         }
       }
     }
   }
 }


