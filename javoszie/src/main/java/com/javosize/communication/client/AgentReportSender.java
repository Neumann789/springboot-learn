 package com.javosize.communication.client;
 
 import com.javosize.actions.GetJavaInfoAction;
 import com.javosize.log.Log;
 import java.util.HashMap;
 
 
 
 
 
 
 
 
 public class AgentReportSender
   implements Runnable
 {
   private static Log log = new Log(AgentReportSender.class.getName());
   
   private String command = null;
   private String message = null;
   
   public AgentReportSender(String command, String message)
   {
     this.command = command;
     this.message = message;
   }
   
   public void start() {
     new Thread(this, "ReportSender" + System.currentTimeMillis()).start();
   }
   
   public void run()
   {
     try {
       log.trace("Starting to send \"report\" message to collector.");
       
       GetJavaInfoAction agentInfoAction = new GetJavaInfoAction();
       agentInfoAction.execute();
       
       HashMap<String, String> params = new HashMap();
       params.put("hostname", agentInfoAction.getHostname());
       params.put("jvmName", agentInfoAction.getJvmName());
       params.put("javaVersion", agentInfoAction.getJavaVersion());
       params.put("javaVendor", agentInfoAction.getJavaVendor());
       params.put("os", agentInfoAction.getOs());
       params.put("javosizeVersion", "v.1.1.3");
       params.put("id", agentInfoAction.getId());
       params.put("command", this.command);
       params.put("message", this.message);
       
       String result = RestAPIClient.executeRestApiCall("POST", "/agentReport.jsp", params);
       
       if (result.contains("OK")) {
         log.trace("\"report\" message sent properly.");
       } else {
         log.trace("Error sending \"report\" message: " + result);
       }
     } catch (Throwable th) {
       log.trace("Unable to send report message: " + th, th);
     }
   }
 }


