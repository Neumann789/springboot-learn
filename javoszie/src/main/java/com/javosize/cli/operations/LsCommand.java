 package com.javosize.cli.operations;
 
 import com.javosize.actions.CountClassesAction;
 import com.javosize.actions.InterceptionAction;
 import com.javosize.actions.JmxDumpAction;
 import com.javosize.actions.ListApplicationsAction;
 import com.javosize.actions.ListApplicationsThreadsAction;
 import com.javosize.actions.ListClassesAction;
 import com.javosize.actions.ListCustomMetricsAction;
 import com.javosize.actions.ListUsersAction;
 import com.javosize.actions.MemoryAction;
 import com.javosize.actions.PerfCounterAction;
 import com.javosize.actions.ProblemDetectorAction;
 import com.javosize.actions.SessionsAction;
 import com.javosize.actions.ThreadDumpAction;
 import com.javosize.breakpoints.BreakPoint;
 import com.javosize.breakpoints.BreakPointManger;
 import com.javosize.cli.Command;
 import com.javosize.cli.CommandType;
 import com.javosize.cli.InvalidParamsException;
 import com.javosize.cli.Main;
 import com.javosize.cli.State;
 import com.javosize.cli.StateHandler;
 import com.javosize.log.Log;
 import com.javosize.print.Table;
 import com.javosize.recipes.Recipe;
 import com.javosize.recipes.Repository;
 import com.javosize.remote.Controller;
 import com.javosize.scheduler.Schedule;
 import com.javosize.scheduler.Scheduler;
 import java.util.Map;
 
 public class LsCommand
   extends Command
 {
   private static Log log = new Log(LsCommand.class.getName());
   
   private boolean regexFilters = false;
   private String[] filters = new String[0];
   private String cachedResult = null;
   private int cachedCount = -1;
   private boolean tableResult = true;
   
   public LsCommand(String[] args) {
     this(args, true);
   }
   
   public LsCommand(String[] args, boolean tableResult) {
     setArgs(args);
     setType(CommandType.ls);
     this.tableResult = tableResult;
   }
   
   public int executeCount(StateHandler handler) throws InvalidParamsException {
     validateArgs(this.args, handler);
     int num = 0;
     if (handler.getStateHolder().equals(State.classes)) {
       return executeCountInClasses(handler);
     }
     this.cachedResult = execute(handler);
     num = this.cachedResult.split("\n").length;
     if (this.cachedResult.contains("=======")) {
       num -= 2;
     }
     
     return num;
   }
   
   public String execute(StateHandler handler) throws InvalidParamsException
   {
     if (this.cachedResult != null) {
       return this.cachedResult;
     }
     validateArgs(this.args, handler);
     if (handler.getStateHolder().equals(State.root))
       return executeInRoot(handler);
     if (handler.getStateHolder().equals(State.threads))
       return executeInThreads(handler);
     if (handler.getStateHolder().equals(State.jmx))
       return executeInJmx(handler);
     if (handler.getStateHolder().equals(State.interceptor))
       return executeInInterceptor(handler);
     if (handler.getStateHolder().equals(State.sessions))
       return executeInSessions(handler);
     if (handler.getStateHolder().equals(State.repository))
       return executeInRepository(handler);
     if (handler.getStateHolder().equals(State.classes))
       return executeInClasses(handler);
     if (handler.getStateHolder().equals(State.users))
       return executeInUsers(handler);
     if (handler.getStateHolder().equals(State.apps))
       return executeInApps(handler);
     if (handler.getStateHolder().equals(State.appthreads))
       return executeInAppThreads(handler);
     if (handler.getStateHolder().equals(State.scheduler))
       return executeInScheduler(handler);
     if (handler.getStateHolder().equals(State.breakpoints))
       return executeInBreakpoints(handler);
     if (handler.getStateHolder().equals(State.perfcounter))
       return executeInPerfcounters(handler);
     if (handler.getStateHolder().equals(State.memory))
       return executeInMemory(handler);
     if (handler.getStateHolder().equals(State.problems))
       return executeInProblems(handler);
     if (handler.getStateHolder().equals(State.custommetrics)) {
       return executeInCustomMetrics(handler);
     }
     return "Command not available at current entity.\nFor info about available commands type \"help\".\n";
   }
   
 
   private String executeInProblems(StateHandler handler)
   {
     ProblemDetectorAction pd = new ProblemDetectorAction(Main.getTerminalWidth());
     return Controller.getInstance().execute(pd);
   }
   
   private String executeInCustomMetrics(StateHandler handler) {
     ListCustomMetricsAction lcma = new ListCustomMetricsAction(Main.getTerminalWidth());
     return Controller.getInstance().execute(lcma);
   }
   
   private String executeInMemory(StateHandler handler) {
     MemoryAction ma = new MemoryAction(Main.getTerminalWidth());
     return Controller.getInstance().execute(ma);
   }
   
   private String executeInPerfcounters(StateHandler handler) {
     PerfCounterAction pca = new PerfCounterAction(Main.getTerminalWidth());
     return Controller.getInstance().execute(pca);
   }
   
   private String executeInBreakpoints(StateHandler handler) {
     Table table = new Table(Main.getTerminalWidth());
     table.addColum("Name", 10);
     table.addColum("Class", 30);
     table.addColum("Method", 10);
     table.addColum("Thread", 15);
     table.addColum("Session", 10);
     table.addColum("User", 10);
     table.addColum("BKRun", 5);
     table.addColum("#Queue", 5);
     table.addColum("#Done", 5);
     try
     {
       for (BreakPoint r : BreakPointManger.getBreakPoints()) {
         table.addRow(new String[] { r.getName(), r.getClassNameRegexp(), r.getMethodRegexp(), r.getThreadRegexp(), r.getSessionRegexp(), r.getUserRegexp(), String.valueOf(r.getExecuting()), String.valueOf(r.getWaiters()), String.valueOf(r.getExecuted()) });
       }
     } catch (Throwable th) {
       return th.toString();
     }
     return table.toString();
   }
   
   private String executeInApps(StateHandler handler) {
     ListApplicationsAction laa = new ListApplicationsAction(Main.getTerminalWidth(), Main.getTerminalHeight());
     return Controller.getInstance().execute(laa);
   }
   
   private String executeInAppThreads(StateHandler handler) {
     ListApplicationsThreadsAction lat = new ListApplicationsThreadsAction(Main.getTerminalWidth(), Main.getTerminalHeight());
     return Controller.getInstance().execute(lat);
   }
   
   private String executeInRepository(StateHandler handler) {
     Table table = new Table(Main.getTerminalWidth());
     table.addColum("Name", 20);
     table.addColum("Author", 10);
     table.addColum("Description", 70);
     try {
       for (Recipe r : Repository.getRepository(true).getRecipes().values()) {
         table.addRow(new String[] { r.getName(), r.getAuthor(), r.getDescription() });
       }
     } catch (Throwable th) {
       return th.toString();
     }
     return table.toString();
   }
   
   private String executeInScheduler(StateHandler handler) {
     Table table = new Table(Main.getTerminalWidth());
     table.addColum("Name", 30);
     table.addColum("Freq(mins)", 10);
     table.addColum("Recipe", 20);
     table.addColum("Last Executed", 20);
     table.addColum("Recipe params", 20);
     try
     {
       for (Schedule r : Scheduler.getScheduler(true).getSchedules().values()) {
         table.addRow(new String[] { r.getName(), r.getFrequency(), r.getRecipeName(), r.getLastExecutedAsString(), r.getParams() });
       }
     } catch (Throwable th) {
       return th.toString();
     }
     return table.toString();
   }
   
   private String executeInSessions(StateHandler handler) {
     SessionsAction lsi = new SessionsAction(Main.getTerminalWidth(), Main.getTerminalHeight());
     return Controller.getInstance().execute(lsi);
   }
   
   private String executeInInterceptor(StateHandler handler) {
     InterceptionAction lsi = new InterceptionAction(Main.getTerminalWidth(), Main.getTerminalHeight());
     return Controller.getInstance().execute(lsi);
   }
   
   private String executeInJmx(StateHandler handler) {
     JmxDumpAction jda = new JmxDumpAction();
     return Controller.getInstance().execute(jda);
   }
   
   private String executeInThreads(StateHandler handler) {
     ThreadDumpAction th = new ThreadDumpAction(Main.getTerminalWidth(), Main.getTerminalHeight());
     return Controller.getInstance().execute(th);
   }
   
   private int executeCountInClasses(StateHandler handler) {
     int num = 0;
     if ((this.filters == null) || (this.filters.length == 0)) {
       CountClassesAction countClasses = new CountClassesAction();
       String count = Controller.getInstance().execute(countClasses);
       try {
         num = Integer.parseInt(count);
       } catch (Throwable th) {
         log.error("Error obtaining number of classes: " + th, th);
       }
     }
     this.cachedCount = num;
     return num;
   }
   
   private String executeInClasses(StateHandler handler) {
     boolean obtainList = true;
     if (this.cachedCount < 0) {
       int num = executeCountInClasses(handler);
       if (num >= 1000) {
         obtainList = Main.askForConfirmation("Found " + num + " classes, do you want to list all? (Please, note that listing all the classes may take a while) [y/n]");
       }
       else {
         obtainList = true;
       }
     }
     
     String result = "\n";
     if (obtainList) {
       ListClassesAction lsClasses = new ListClassesAction(Main.getTerminalWidth(), Main.getTerminalHeight(), this.regexFilters, this.filters, !this.tableResult);
       result = Controller.getInstance().execute(lsClasses);
     }
     
     return result;
   }
   
   private String executeInUsers(StateHandler handler) {
     ListUsersAction laa = new ListUsersAction(Main.getTerminalWidth(), Main.getTerminalHeight());
     return Controller.getInstance().execute(laa);
   }
   
   private String executeInRoot(StateHandler handler) {
     StringBuffer sb = new StringBuffer();
     sb.append("\033[32m");
     for (State state : State.values())
       if (!state.equals(State.root))
       {
 
         sb.append(state.toString());
         sb.append("\n");
       }
     sb.append("\033[0m");
     
     return sb.toString();
   }
   
   public boolean validArgs(String[] args, StateHandler handler)
   {
     if (args.length == 1)
       return true;
     if ((args.length >= 2) && (args[1].equals("-r"))) {
       loadFilters(args, 2);
       this.regexFilters = true;
       return true; }
     if (args.length >= 2) {
       loadFilters(args, 1);
       this.regexFilters = false;
       return true;
     }
     return false;
   }
   
   private void loadFilters(String[] args, int firstFilter) {
     this.filters = new String[args.length - firstFilter];
     int i = firstFilter; for (int j = 0; i < args.length; j++) {
       this.filters[j] = args[i];i++;
     }
   }
 }


