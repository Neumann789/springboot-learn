 package com.javosize.remote.client;
 
 import com.javosize.actions.Action;
 import com.javosize.actions.TerminateAction;
 import com.javosize.log.Log;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import java.net.Socket;
 import java.util.TimerTask;
 
 public class Client
   extends TimerTask
 {
   private static Log log = new Log(Client.class.getName());
   
   private String ip = null;
   private int port = 0;
   private boolean javaagentMode = false;
   
   public Client(String ip, int port) {
     this.ip = ip;
     this.port = port;
   }
   
   public Client(String ip, int port, boolean javaagentMode) {
     this(ip, port);
     this.javaagentMode = javaagentMode;
   }
   
   public void run()
   {
     Socket clientSocket = null;
     try {
       log.trace("Trying to start connection with client. [IP=" + this.ip + "][Port=" + this.port + "]");
       
       clientSocket = new Socket(this.ip, this.port);
       ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
       ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
       
       log.info("Connection started successfully with CLI. [IP=" + this.ip + "][Port=" + this.port + "]");
       for (;;)
       {
         Action a = (Action)inputStream.readObject();
         
         if ((a instanceof TerminateAction)) {
           log.info("javOSize -> Connection closed from Console");
           break;
         }
         
         String result = null;
         try {
           result = a.execute();
         } catch (Throwable th) {
           result = getStringFromException(th);
         }
         outputStream.writeObject(result);
         outputStream.flush();
       }
       
       log.info("Closed connection with client. [IP=" + this.ip + "][Port=" + this.port + "]");
     }
     catch (IOException ioe) {
       String message = "javOSize -> Connection closed from Console";
       if (this.javaagentMode) {
         log.trace(message);
       } else {
         log.warn(message);
       }
     } catch (ClassNotFoundException cnfe) {
       String message = "javOSize -> Version mismatch. Please assure you are not running an outdated agent manually installed in application server: " + cnfe;
       if (this.javaagentMode) {
         log.trace(message, cnfe);
       } else {
         log.error(message, cnfe);
       }
     }
     finally {
       closeSocket(clientSocket);
     }
   }
   
   private String getStringFromException(Throwable th) {
     StringBuffer sb = new StringBuffer();
     sb.append("General javOSize exception: ");
     sb.append(th);
     sb.append("\n");
     StringWriter sw = new StringWriter();
     PrintWriter pw = new PrintWriter(sw);
     th.printStackTrace(pw);
     sb.append(sw.toString());
     sb.append("\n");
     return sb.toString();
   }
   
   private void closeSocket(Socket s) {
     try {
       s.close();
     }
     catch (Throwable localThrowable) {}
   }
 }


