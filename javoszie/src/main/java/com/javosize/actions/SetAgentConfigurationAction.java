 package com.javosize.actions;
 
 import com.javosize.log.Log;
 import com.javosize.log.LogLevel;
 
 public class SetAgentConfigurationAction
   extends Action
 {
   private static final long serialVersionUID = -2368200046848456461L;
   private static Log log = new Log(SetAgentConfigurationAction.class.getName());
   
   private LogLevel logLevel = null;
   private boolean errorReportEnabled = true;
   
   public SetAgentConfigurationAction(LogLevel logLevel, boolean errorReportEnabled) {
     this.logLevel = logLevel;
     this.errorReportEnabled = errorReportEnabled;
   }
   
   public String execute()
   {
     Log.setLogLevel(this.logLevel);
     log.info("Updated log level to " + this.logLevel);
     Log.setErrorReportEnabled(this.errorReportEnabled);
     log.info("Error report " + (this.errorReportEnabled ? "enabled" : "disabled"));
     return "Done!";
   }
 }


