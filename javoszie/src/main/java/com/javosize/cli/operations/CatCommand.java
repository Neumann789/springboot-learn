 package com.javosize.cli.operations;
 
 import com.javosize.actions.CatInterceptionAction;
 import com.javosize.actions.GetClassByteCodeAction;
 import com.javosize.actions.ListApplicationsDetailAction;
 import com.javosize.actions.MBeanDetailAction;
 import com.javosize.actions.SessionsActionDetail;
 import com.javosize.actions.ShellAction;
 import com.javosize.actions.ThreadDetailAction;
 import com.javosize.breakpoints.BreakPoint;
 import com.javosize.breakpoints.BreakPointManger;
 import com.javosize.classutils.NestedClassesFinder;
 import com.javosize.cli.Command;
 import com.javosize.cli.CommandType;
 import com.javosize.cli.Environment;
 import com.javosize.cli.InvalidParamsException;
 import com.javosize.cli.Main;
 import com.javosize.cli.State;
 import com.javosize.cli.StateHandler;
 import com.javosize.decompile.DecompilationResult;
 import com.javosize.decompile.Decompiler;
 import com.javosize.encoding.Base64;
 import com.javosize.log.Log;
 import com.javosize.print.TextReport;
 import com.javosize.recipes.Recipe;
 import com.javosize.recipes.RecipeParam;
 import com.javosize.recipes.Repository;
 import com.javosize.remote.Controller;
 import com.javosize.scheduler.Schedule;
 import com.javosize.scheduler.Scheduler;
 import com.sun.org.apache.xml.internal.security.Init;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 
 public class CatCommand extends Command
 {
   public static Log log = new Log(CatCommand.class.getName());
   
   public CatCommand(String[] args) {
     setArgs(args);
     setType(CommandType.cat);
   }
   
   public String execute(StateHandler handler) throws InvalidParamsException
   {
     if (!handler.getStateHolder().equals(State.jmx))
     {
 
       validateArgs(this.args, handler);
     }
     if (handler.getStateHolder().equals(State.threads))
       return executeInThreads(handler);
     if (handler.getStateHolder().equals(State.jmx))
       return executeInJmx(handler);
     if (handler.getStateHolder().equals(State.sessions))
       return executeInSession(handler);
     if (handler.getStateHolder().equals(State.repository))
       return executeInRepository(handler);
     if (handler.getStateHolder().equals(State.classes))
       return executeInClasses(handler);
     if (handler.getStateHolder().equals(State.breakpoints))
       return executeInBreakPoints(handler);
     if (handler.getStateHolder().equals(State.interceptor))
       return executeInInterceptor(handler);
     if (handler.getStateHolder().equals(State.scheduler))
       return executeInScheduler(handler);
     if (handler.getStateHolder().equals(State.apps)) {
       return executeInApps(handler);
     }
     return "Command not available at current entity.\nFor info about available commands type \"help\".\n";
   }
   
 
   private String executeInInterceptor(StateHandler handler)
   {
     CatInterceptionAction cia = new CatInterceptionAction(this.args[1]);
     return Controller.getInstance().execute(cia);
   }
   
   private String executeInBreakPoints(StateHandler handler) {
     TextReport report = new TextReport();
     
     BreakPoint bp = BreakPointManger.getBreakPoint(this.args[1]);
     if (bp == null)
       return "BreakPoint:" + this.args[1] + " not found\n";
     if (bp.getExecuting() == 0) {
       return "Currently there is no active breakpoint\n";
     }
     
     String intro = "You can access the next breakpoint environmental params from /SH. (Object[] com.javosize.agent.session.UserThreadSessionTracker.getBreakpointParams(String breakPointName))\n";
     report.addSection("General", new String[] { intro });
     
     String javaCode = "return com.javosize.agent.session.UserThreadSessionTracker.getBreakpointParamsSerial(\"" + this.args[1] + "\");";
     ShellAction sh = new ShellAction(javaCode, Environment.get("APPCLASSLOADER"));
     String params = Controller.getInstance().execute(sh);
     report.addSection("Parameters", new String[] { params });
     javaCode = "return com.javosize.agent.session.UserThreadSessionTracker.getBreakpointStack(\"" + this.args[1] + "\");";
     sh = new ShellAction(javaCode, Environment.get("APPCLASSLOADER"));
     String stack = Controller.getInstance().execute(sh);
     report.addSection("Stack", new String[] { stack });
     return report.toString();
   }
   
   private String executeInRepository(StateHandler handler)
   {
     TextReport report = new TextReport();
     StringBuffer sb = new StringBuffer("");
     for (int i = 1; i < this.args.length; i++) {
       sb.append(this.args[i] + " ");
     }
     String recipeName = sb.toString().trim();
     try
     {
       Recipe r = Repository.getRecipe(recipeName);
       if (r == null) {
         return "Recipe " + recipeName + " not found.\n";
       }
       report.addSection("General", new String[] {"Name: " + r
       
 
         .getName(), "Description: " + r
         .getDescription(), "Parameters: " + 
         getParamDescriptionsToString(r.getParamDescriptions()) });
       
       report.addSection("Source", new String[] {r
         .getCode() });
     } catch (Throwable th) {
       return "Error getting recipe: \"" + recipeName + "\". " + th.toString();
     }
     return report.toString();
   }
   
   private String getParamDescriptionsToString(List<RecipeParam> paramDescriptions) {
     if ((paramDescriptions == null) || (paramDescriptions.size() == 0)) {
       return "";
     }
     StringBuffer params = new StringBuffer();
     for (RecipeParam paramInfo : paramDescriptions) {
       params.append("\n    " + paramInfo.getId() + ": " + paramInfo.getDescription());
     }
     return params.toString();
   }
   
   private String executeInScheduler(StateHandler handler) {
     TextReport report = new TextReport();
     StringBuffer sb = new StringBuffer("");
     for (int i = 1; i < this.args.length; i++) {
       sb.append(this.args[i] + " ");
     }
     String scheduleName = sb.toString().trim();
     try
     {
       Scheduler.getScheduler(true);Schedule s = Scheduler.getSchedule(scheduleName);
       report.addSection("General", new String[] {"Name: " + s
       
 
         .getName(), "Freq (Min): " + s
         .getFrequency(), "Recipe: " + s
         .getRecipeName(), "Params: " + s
         .getParams() });
       report.addSection("Last Execution", new String[] {"Last Execution time: " + s
       
 
         .getLastExecutedAsString(), "Last Execution result: " + (
         (s.getLastExecutionResult() == null) || (s.getLastExecutionResult().equals("")) ? "-" : new StringBuilder().append("\n    ").append(s.getLastExecutionResult().replace("\n", "\n    ")).toString()) });
     } catch (Throwable th) {
       return "Error getting schedule: \"" + scheduleName + "\". " + th.toString();
     }
     return report.toString();
   }
   
   private String executeInSession(StateHandler handler) {
     SessionsActionDetail tda = new SessionsActionDetail(this.args[1]);
     return Controller.getInstance().execute(tda);
   }
   
   private String executeInJmx(StateHandler handler) {
     MBeanDetailAction mba = new MBeanDetailAction();
     StringBuffer sb = new StringBuffer("");
     for (int i = 1; i < this.args.length; i++) {
       sb.append(this.args[i] + " ");
     }
     mba.setMBean(sb.toString().trim());
     return Controller.getInstance().execute(mba);
   }
   
   private String executeInThreads(StateHandler handler) {
     ThreadDetailAction tda = new ThreadDetailAction();
     tda.setThreadId(this.args[1]);
     return Controller.getInstance().execute(tda);
   }
   
   private String executeInApps(StateHandler handler) {
     StringBuffer sb = new StringBuffer("");
     for (int i = 1; i < this.args.length; i++) {
       sb.append(this.args[i] + " ");
     }
     ListApplicationsDetailAction lda = new ListApplicationsDetailAction(sb.toString().trim(), Main.getTerminalWidth());
     return Controller.getInstance().execute(lda);
   }
   
   private String executeInClasses(StateHandler handler) {
     String result = "Not available\n";
     
     if ((this.args[1] == null) || (!this.args[1].contains(".class"))) {
       return "Invalid class name: " + this.args[1] + "\n" + " - Usage: cat package.className.class\n" + " - Example: cat com.javosize.examples.example.class\n" + " - Hint: You can use autocomplete or the ls command to search the class name.\n";
     }
     
 
 
 
     String byteCodeStr = "";
     try
     {
       List<String> classesNames = NestedClassesFinder.checkNestedClassesForDecompilation(this.args[1]);
       
       if (classesNames == null)
         return "\n";
       if (classesNames.size() == 0) {
         return "Class " + this.args[1] + " not found.\n";
       }
       
       Map<String, byte[]> byteCodeOfClasses = new LinkedHashMap();
       
       for (String className : classesNames) {
         GetClassByteCodeAction gcb = new GetClassByteCodeAction(className);
         byteCodeStr = Controller.getInstance().execute(gcb);
         
         if (byteCodeStr == null) {
           return "No code available. ByteCode of class " + className + " not found at agent. More info at agent log.\n";
         }
         Init.init();
         byte[] byteCode = Base64.decodeBytesFromString(byteCodeStr);
         byteCodeOfClasses.put(className, byteCode);
       }
       
 
       DecompilationResult resultOfDecompilation = Decompiler.decompileAndValidate((String)classesNames.get(0), byteCodeOfClasses);
       
 
       if (!resultOfDecompilation.isDecompilationOK()) {
         result = resultOfDecompilation.getDecompilationErrors();
 
 
       }
       else if (!resultOfDecompilation.isCompilationOK())
       {
         if (Main.askForConfirmation("Decompilation of class has returned some errors. The returned code may not be 100% correct. Do you want to see it anyway? [y/n]")) {
           result = resultOfDecompilation.getDecompilation();
         } else {
           result = "\n";
         }
         
       }
       else if ((resultOfDecompilation.getTargetVersion() != null) && (
         (resultOfDecompilation.getTargetVersion().equals("1.1")) || 
         (resultOfDecompilation.getTargetVersion().equals("1.2")) || 
         (resultOfDecompilation.getTargetVersion().equals("1.3"))))
       {
 
         if (Main.askForConfirmation("The class that you want to see was compiled using an unsupported Java version (" + resultOfDecompilation
           .getTargetVersion() + "). " + "The returned code may not be 100% valid for hot replacement. " + "Do you want to see it anyway? [y/n]"))
         {
 
           result = resultOfDecompilation.getDecompilation();
         } else {
           result = "\n";
         }
         
       }
       else {
         result = resultOfDecompilation.getDecompilation();
       }
     }
     catch (Throwable th) {
       log.error("Error at decompile: " + th + " returnedByteCodeB64: " + byteCodeStr, th);
     }
     
     return result;
   }
   
 
 
 
   protected boolean validArgs(String[] args, StateHandler handler)
   {
     return args.length >= 2;
   }
 }


