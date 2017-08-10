 package com.javosize.scheduler;
 
 import com.javosize.cli.Main;
 import com.javosize.log.Log;
 import com.javosize.recipes.Repository;
 import java.io.Serializable;
 import java.util.Date;
 import org.json.JSONObject;
 
 
 public class Schedule
   implements Serializable
 {
   private static final long serialVersionUID = 4911159269086058370L;
   private String name;
   private String crontExp;
   private String frequency;
   private long lastExecuted = 0L;
   private String lastExecutionResult = null;
   private static Log log = new Log(Schedule.class.getName());
   private String params;
   private String recipeName;
   private boolean autoStartup = false;
   
 
   public String getFrequency()
   {
     return this.frequency;
   }
   
   public void setFrequency(String frequency) { this.frequency = frequency; }
   
   public boolean isAutoStartup()
   {
     return this.autoStartup;
   }
   
   public void setAutoStartup(boolean autoStartup) {
     this.autoStartup = autoStartup;
   }
   
   public String getName() { return this.name; }
   
   public void setName(String name) {
     this.name = name;
   }
   
   public String getCrontExp() { return this.crontExp; }
   
   public void setCrontExp(String crontExp) {
     this.crontExp = crontExp;
   }
   
   public String getParams() { return this.params; }
   
   public void setParams(String params) {
     this.params = params;
   }
   
   public String getRecipeName() { return this.recipeName; }
   
   public void setRecipeName(String recipeName) {
     this.recipeName = recipeName;
   }
   
   public String toJSON() {
     JSONObject json = new JSONObject(this);
     return json.toString();
   }
   
   public static Schedule fromJSON(String json) { Schedule r = new Schedule();
     JSONObject jsonObject = new JSONObject(json);
     r.setName(jsonObject.getString("name"));
     r.setAutoStartup(jsonObject.getBoolean("autoStartup"));
     r.setCrontExp(jsonObject.getString("cronExp"));
     r.setParams(jsonObject.getString("params"));
     r.setRecipeName(jsonObject.getString("recipeName"));
     r.setFrequency(jsonObject.getString("frequency"));
     return r;
   }
   
   public boolean triggerIfRequired() { if ("0".equals(this.frequency))
     {
       return false;
     }
     
     if (System.currentTimeMillis() - this.lastExecuted >= Integer.valueOf(this.frequency).intValue() * 60 * 1000) {
       run();
       return true;
     }
     
     return false;
   }
   
   public long getLastExecuted() { return this.lastExecuted; }
   
   public String getLastExecutedAsString()
   {
     if (this.lastExecuted == 0L) {
       return "-";
     }
     return new Date(this.lastExecuted).toString();
   }
   
   public void setLastExecuted(long lastExecuted)
   {
     this.lastExecuted = lastExecuted;
   }
   
   public void setLastExecutionResult(String lastExecutionResult) {
     this.lastExecutionResult = lastExecutionResult;
   }
   
   public String getLastExecutionResult() {
     return this.lastExecutionResult;
   }
   
   public void run() {
     log.info("Running task: " + getName());
     this.lastExecuted = System.currentTimeMillis();
     try {
       this.lastExecutionResult = Main.executeRecipe(Repository.getRecipe(getRecipeName()), getParams());
       log.info("Task: " + getName() + " executed sucessfully. ");
     } catch (Throwable th) {
       this.lastExecutionResult = ("ERROR: " + th);
       log.error("Error executing task " + getName() + ": " + th, th);
     }
   }
 }


