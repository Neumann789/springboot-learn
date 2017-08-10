 package org.jboss.jreadline.console;
 
 
 
 
 
 
 
 
 
 
 
 public enum InternalCommands
 {
   ALIAS("alias"), 
   UNALIAS("unalias"), 
   ECHO("echo");
   
   private String command;
   
   private InternalCommands(String alias) { this.command = alias; }
   
   public String getCommand()
   {
     return this.command;
   }
 }


