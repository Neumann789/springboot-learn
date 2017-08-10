 package com.javosize.remote;
 
 import com.javosize.actions.Action;
 import com.javosize.actions.TerminateAction;
 import com.javosize.remote.server.Server;
 
 public class CliRemoteController extends Controller
 {
   private Server server = null;
   
   public CliRemoteController()
   {
     this.server = Server.getInstance();
     this.server.start();
   }
   
   public int getPort() {
     return this.server.getPort();
   }
   
   public String execute(Action a)
   {
     try {
       synchronized (a) {
         this.server.enqueueAction(a);
         a.wait();
       }
     }
     catch (InterruptedException localInterruptedException) {}
     return a.getResult();
   }
   
   public void finish()
   {
     this.server.enqueueAction(new TerminateAction());
     try {
       Thread.sleep(500L);
     }
     catch (InterruptedException localInterruptedException) {}
     
     this.server.finish();
   }
   
   public boolean isConnected()
   {
     return this.server.isConnected();
   }
 }


