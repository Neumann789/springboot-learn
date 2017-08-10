 package com.javosize.cli.operations;
 
 import com.javosize.actions.AddBreakPointAction;
 import com.javosize.actions.AddInterceptionAction;
 import com.javosize.breakpoints.BreakPoint;
 import com.javosize.breakpoints.BreakPointManger;
 import com.javosize.cli.Command;
 import com.javosize.cli.CommandType;
 import com.javosize.cli.Environment;
 import com.javosize.cli.InvalidParamsException;
 import com.javosize.cli.Main;
 import com.javosize.cli.State;
 import com.javosize.cli.StateHandler;
 import com.javosize.log.Log;
 import com.javosize.metrics.Monitors;
 import com.javosize.recipes.Recipe;
 import com.javosize.recipes.Repository;
 import com.javosize.remote.Controller;
 import com.javosize.scheduler.Schedule;
 import com.javosize.scheduler.Scheduler;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.List;
 
 
 public class CreateCommand
   extends Command
 {
   private static Log log = new Log(CreateCommand.class.getName());
   
   public CreateCommand(String[] args) {
     setArgs(args);
     setType(CommandType.create);
   }
   
   public String execute(StateHandler handler) throws InvalidParamsException
   {
     validateArgs(this.args, handler);
     if (handler.getStateHolder().equals(State.interceptor))
       return executeInInterceptor(handler);
     if (handler.getStateHolder().equals(State.custommetrics))
       return executeInCustomMetrics(handler);
     if (handler.getStateHolder().equals(State.scheduler))
       return executeInScheduler(handler);
     if (handler.getStateHolder().equals(State.breakpoints))
       return executeInBreakPoints(handler);
     if (handler.getStateHolder().equals(State.repository)) {
       return executeInRepository(handler);
     }
     return "Command not available at current entity.\nFor info about available commands type \"help\".\n";
   }
   
   private String executeInBreakPoints(StateHandler handler) throws InvalidParamsException
   {
     if (this.args.length < 7) {
       throw new InvalidParamsException(getManText(handler));
     }
     try
     {
       String sessionRegexp = this.args[5];
       String userRegexp = this.args[6];
       String threadRegexp = this.args[4];
       String classNameRegexp = this.args[2];
       String methodRegexp = this.args[3];
       String name = this.args[1];
       BreakPoint bp = new BreakPoint();
       bp.setName(name);
       bp.setSessionRegexp(sessionRegexp);
       bp.setThreadRegexp(threadRegexp);
       bp.setUserRegexp(userRegexp);
       bp.setMethodRegexp(methodRegexp);
       bp.setClassNameRegexp(classNameRegexp);
       BreakPointManger.addBreakPoint(bp);
       AddBreakPointAction tda = new AddBreakPointAction(name, classNameRegexp, methodRegexp, threadRegexp, sessionRegexp, userRegexp, bp.getPort());
       bp.setInterception(tda.getInterception());
       return Controller.getInstance().execute(tda);
     } catch (IOException e) {
       return "Error creating BreakPoint: " + e;
     }
   }
   
   private String executeInScheduler(StateHandler handler) throws InvalidParamsException {
     if (this.args.length < 4) {
       throw new InvalidParamsException(getManText(handler));
     }
     
     String finalValue = "";
     if (this.args.length >= 5) {
       finalValue = this.args[4];
       for (int i = 5; i < this.args.length; i++) {
         finalValue = finalValue + " " + this.args[i];
       }
     }
     try
     {
       Schedule s = new Schedule();
       s.setName(this.args[1]);
       s.setFrequency(this.args[2]);
       s.setRecipeName(this.args[3]);
       s.setAutoStartup("0".equals(this.args[2]));
       s.setParams(finalValue);
       
       Scheduler.getScheduler();Scheduler.addSchedule(s);
     } catch (Throwable th) {
       return "Error creating schedule: " + th;
     }
     return "Schedule created\n";
   }
   
   private String executeInInterceptor(StateHandler handler) throws InvalidParamsException {
     if (this.args.length < 6) {
       throw new InvalidParamsException(getManText(handler));
     }
     
     String finalValue = this.args[5];
     for (int i = 6; i < this.args.length; i++) {
       finalValue = finalValue + " " + this.args[i];
     }
     try
     {
       AddInterceptionAction tda = new AddInterceptionAction(this.args[1], this.args[2], this.args[3], this.args[4], finalValue, Environment.get("APPCLASSLOADER"));
       return Controller.getInstance().execute(tda);
     } catch (IllegalArgumentException iae) {
       return "Invalid parameters: " + iae.getMessage() + "\n";
     }
   }
   
 
 
   private String executeInCustomMetrics(StateHandler handler)
     throws InvalidParamsException
   {
     if (this.args.length < 6) {
       throw new InvalidParamsException(getManText(handler));
     }
     
     String type = this.args[1];
     String metricPath = this.args[2];
     String metricName = this.args[3];
     String packRegexp = this.args[4];
     String methodRegexp = this.args[5];
     String units = null;
     String expr = null;
     if (this.args.length > 7) {
       units = this.args[6];
       expr = this.args[7];
       for (int i = 8; i < this.args.length; i++) {
         expr = expr + " " + this.args[i];
       }
     }
     
     if (type.equalsIgnoreCase("timer"))
     {
       String srcCode = Monitors.METHOD_TIMER_MONITOR.getBeginCode(metricPath, metricName, null, null);
       AddInterceptionAction tda = new AddInterceptionAction("CustomMetric-" + metricName + "-begin", "begin", packRegexp, methodRegexp, srcCode, Environment.get("APPCLASSLOADER"));
       Controller.getInstance().execute(tda);
       srcCode = Monitors.METHOD_TIMER_MONITOR.getEndCode(metricPath, metricName, null, null);
       tda = new AddInterceptionAction("CustomMetric-" + metricName + "-end", "end", packRegexp, methodRegexp, srcCode, Environment.get("APPCLASSLOADER"));
       return Controller.getInstance().execute(tda); }
     if (type.equalsIgnoreCase("counter"))
     {
       String srcCode = Monitors.METHOD_COUNTER_MONITOR.getBeginCode(metricPath, metricName, null, null);
       AddInterceptionAction tda = new AddInterceptionAction("CustomMetric-" + metricName + "-begin", "begin", packRegexp, methodRegexp, srcCode, Environment.get("APPCLASSLOADER"));
       return Controller.getInstance().execute(tda); }
     if (type.equalsIgnoreCase("exprAvg"))
     {
       String srcCode = Monitors.METHOD_EXPR_VALUE_MONITOR_AVG.getBeginCode(metricPath, metricName, units, expr);
       AddInterceptionAction tda = new AddInterceptionAction("CustomMetric-" + metricName + "-begin", "begin", packRegexp, methodRegexp, srcCode, Environment.get("APPCLASSLOADER"));
       return Controller.getInstance().execute(tda); }
     if (type.equalsIgnoreCase("exprAdd"))
     {
       String srcCode = Monitors.METHOD_EXPR_VALUE_MONITOR_ADD.getBeginCode(metricPath, metricName, units, expr);
       AddInterceptionAction tda = new AddInterceptionAction("CustomMetric-" + metricName + "-begin", "begin", packRegexp, methodRegexp, srcCode, Environment.get("APPCLASSLOADER"));
       return Controller.getInstance().execute(tda); }
     if (type.equalsIgnoreCase("exprVal"))
     {
       String srcCode = Monitors.METHOD_EXPR_VALUE_MONITOR_VAL.getBeginCode(metricPath, metricName, units, expr);
       AddInterceptionAction tda = new AddInterceptionAction("CustomMetric-" + metricName + "-begin", "begin", packRegexp, methodRegexp, srcCode, Environment.get("APPCLASSLOADER"));
       return Controller.getInstance().execute(tda);
     }
     throw new InvalidParamsException("Unknown type " + type + " valid values: [timer|counter|exprAvg|exprAdd|exprVal] ");
   }
   
 
 
 
 
 
 
 
 
 
   private String executeInRepository(StateHandler handler)
     throws InvalidParamsException
   {
     if (this.args.length < 2) {
       throw new InvalidParamsException(getManText(handler));
     }
     
     if (this.args[1].equals("stop")) {
       if (!Main.isRecording())
         return "There is no record process active. If you want to start recording a new recipe type: create <recipe_name>\n";
       if (Main.askForConfirmation("Stop recording? [y/n]")) {
         String name = Main.getRecordName();
         List<String> commands = Main.stopRecordMode();
         Recipe recipe = new Recipe();
         recipe.setName(name);
         recipe.setAuthor("javOSize recorded recipe");
         recipe.setDescription("javOSize recorded recipe");
         recipe.setNumberOfParameters(0);
         recipe.setParamDescriptions(new ArrayList());
         StringBuffer code = new StringBuffer();
         for (String command : commands) {
           if (!command.trim().equals("create stop"))
             code.append(command + "\n");
         }
         recipe.setCode(code.toString());
         try {
           if (!Repository.addRecipe(recipe)) {
             if (Main.askForConfirmation("Recipe " + name + " already exists. Do you want to overwrite it? [y/n]")) {
               Repository.addRecipe(recipe, true);
               return "Recipe created successfully\n";
             }
             return "Recorded commands discarded. \n";
           }
           
           return "Recipe created successfully\n";
         }
         catch (Throwable th) {
           log.error("Error storing recorded recipe: " + th, th);
           return "Recipe not recorded due to an error: " + th + "\n";
         }
       }
       
       return "\nContinue recording.\n";
     }
     
 
     String name = this.args[1];
     if (Main.askForConfirmation("Start recording commands for recipe " + name + "? [y/n]")) {
       handler.setStateHolder(State.root);
       Main.startRecordMode(name);
       return "Recording started.\n";
     }
     
 
     return "\n";
   }
   
   public boolean validArgs(String[] args, StateHandler handler)
   {
     return args.length >= 2;
   }
 }


