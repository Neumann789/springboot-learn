 package com.javosize.remote.server;
 
 import com.javosize.actions.Action;
 import com.javosize.actions.TerminateAction;
 import com.javosize.cli.Main;
 import com.javosize.log.Log;
 import java.io.EOFException;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.net.ServerSocket;
 import java.net.Socket;
 import java.util.concurrent.LinkedBlockingQueue;
 import java.util.concurrent.TimeUnit;
 
 public class Server extends Thread
 {
   private static volatile Server instance = null;
   private ServerSocket serverSocket;
   private LinkedBlockingQueue<Action> queue = new LinkedBlockingQueue();
   private boolean finished = false;
   
   private boolean connected = false;
   
   private static Log log = new Log(Server.class.getName());
   
   public void finish() {
     this.finished = true;
   }
   
   private Server() {
     try {
       this.serverSocket = new ServerSocket(Main.getListeningPort());
     } catch (IOException ioe) {
       log.error("ERROR: " + ioe, ioe);
     }
   }
   
   public int getPort() {
     return this.serverSocket.getLocalPort();
   }
   
   public static synchronized Server getInstance() {
     if (instance == null) {
       instance = new Server();
     }
     return instance;
   }
   
   public void run()
   {
     try
     {
       Socket clientSocket = this.serverSocket.accept();
       ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
       ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
       
       this.connected = true;
       
       while (!this.finished) {
         Action a = (Action)this.queue.poll(1L, TimeUnit.SECONDS);
         processAction(a, inputStream, outputStream);
       }
     } catch (Exception e) {
       log.error("ERROR: " + e, e);
     }
   }
   
   public void enqueueAction(Action a) {
     this.queue.add(a);
   }
   
   private void processAction(Action a, ObjectInputStream ois, ObjectOutputStream oos) throws ClassNotFoundException, IOException {
     if (a == null) {
       return;
     }
     try {
       String result = remoteExecute(a, ois, oos);
       a.setResult(result);
     }
     catch (EOFException eofe) {
       if (!(a instanceof TerminateAction)) {
         log.error("Connection unexpectedly closed by Peer: " + eofe);
       }
     }
     
     synchronized (a) {
       a.notify();
     }
   }
   
   private String remoteExecute(Action a, ObjectInputStream ois, ObjectOutputStream oos) throws IOException, ClassNotFoundException
   {
     oos.writeObject(a);
     oos.flush();
     String res = (String)ois.readObject();
     return res;
   }
   
   public boolean isConnected() {
     return (this.connected) && (!this.finished);
   }
 }


