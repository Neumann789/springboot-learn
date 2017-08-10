 package com.javosize.remote;
 
 import com.javosize.actions.Action;
 
 public abstract class Controller
 {
   private static volatile Controller instance;
   
   public static synchronized Controller getInstance()
   {
     if (instance == null) {
       if (com.javosize.agent.Agent.isAgent()) {
         instance = new JavaAgentController();
       } else {
         instance = new CliRemoteController();
       }
     }
     return instance;
   }
   
   public abstract int getPort();
   
   public abstract String execute(Action paramAction);
   
   public abstract void finish();
   
   public abstract boolean isConnected();
 }


