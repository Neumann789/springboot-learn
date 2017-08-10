 package com.javosize.communication.client;
 
 import com.javosize.actions.GetJavaInfoAction;
 import com.javosize.log.Log;
 import com.javosize.remote.Controller;
 import java.util.HashMap;
 import org.json.JSONObject;
 
 
 
 
 
 
 
 
 
 public class ReportSender
   implements Runnable
 {
   private static Log log = new Log(ReportSender.class.getName());
   
   private String command = null;
   private String message = null;
   
   public ReportSender(String command, String message)
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
       String agentInfoString = Controller.getInstance().execute(agentInfoAction);
       
       JSONObject agentInfoJson = new JSONObject(agentInfoString);
       
       HashMap<String, String> params = new HashMap();
       params.put("hostname", agentInfoJson.getString("hostname"));
       params.put("jvmName", agentInfoJson.getString("jvmName"));
       params.put("javaVersion", agentInfoJson.getString("javaVersion"));
       params.put("javaVendor", agentInfoJson.getString("javaVendor"));
       params.put("os", agentInfoJson.getString("os"));
       params.put("javosizeVersion", "v.1.1.3");
       params.put("id", agentInfoJson.getString("id"));
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


