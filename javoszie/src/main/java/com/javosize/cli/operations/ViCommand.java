 package com.javosize.cli.operations;
 
 import com.javosize.cli.CommandType;
 import com.javosize.log.Log;
 
 
 
 
 
 
 public class ViCommand
   extends EditCommand
 {
   public static Log log = new Log(ViCommand.class.getName());
   
   public ViCommand(String[] args) {
     super(args);
     setType(CommandType.vi);
   }
 }


