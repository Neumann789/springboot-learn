 package com.javosize.communication.client;
 
 import com.javosize.actions.GetJavaInfoAction;
 import com.javosize.cli.Main;
 import com.javosize.log.Log;
 import com.javosize.remote.Controller;
 import java.util.HashMap;
 import org.json.JSONObject;
 
 
 
 
 
 
 
 
 public class HelloSender
   implements Runnable
 {
   private static Log log = new Log(HelloSender.class.getName());
   
 
 
 
   public void start()
   {
     new Thread(this, "HelloSender").start();
   }
   
   public void run()
   {
     try {
       log.trace("Starting to send \"hello\" message to collector.");
       
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
       
       String result = RestAPIClient.executeRestApiCall("GET", "/hello.jsp", params);
       
       JSONObject jsonObject = new JSONObject(result);
       if (jsonObject.has("status")) {
         String status = jsonObject.getString("status");
         if (status.contains("OK")) {
           log.trace("\"hello\" message sent properly.");
           
           if ((jsonObject.has("newVersion")) && (jsonObject.has("newVersionChangeLog"))) {
             String newVersion = jsonObject.getString("newVersion");
             String newVersionChangeLog = jsonObject.getString("newVersionChangeLog");
             log.trace("New version info received. [Version=" + newVersion + "][ChangeLog=" + newVersionChangeLog + "]");
             Main.addNotification(Long.valueOf(System.currentTimeMillis()), "There is a new version of javOSize available.\nYou can download it from http://www.javosize.com\nThe main improvements from your current version are:\n  - " + newVersionChangeLog
             
 
 
 
               .replace(";", "\n  - "));
           }
           else {
             log.trace("\"hello\" message doesn't include new version info.");
           }
         } else {
           log.trace("Error sending \"hello\" message: " + result);
         }
       } else {
         log.trace("Error sending \"hello\" message: " + result);
       }
     } catch (Throwable th) {
       log.trace("Unable to send hello message: " + th, th);
     }
   }
 }


