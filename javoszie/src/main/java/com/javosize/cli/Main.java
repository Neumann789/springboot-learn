 package com.javosize.cli;
 
 import com.javosize.actions.SetAgentConfigurationAction;
 import com.javosize.agent.AgentLoader;
 import com.javosize.agent.AgentShutdownHook;
 import com.javosize.breakpoints.BreakPoint;
 import com.javosize.breakpoints.BreakPointManger;
 import com.javosize.communication.client.HelloSender;
 import com.javosize.log.Log;
 import com.javosize.log.LogLevel;
 import com.javosize.recipes.Recipe;
 import com.javosize.recipes.Repository;
 import com.javosize.remote.Controller;
 import com.javosize.scheduler.Scheduler;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.lang.reflect.InvocationTargetException;
 import java.util.List;
import java.util.Map;
 import java.util.Map.Entry;
 import java.util.concurrent.ConcurrentHashMap;
 import org.jboss.jreadline.complete.Completion;
 import org.jboss.jreadline.console.Console;
 import org.jboss.jreadline.console.ConsoleOutput;
 import org.jboss.jreadline.console.operator.ControlOperator;
 import org.jboss.jreadline.console.settings.Settings;
 import org.jboss.jreadline.terminal.Terminal;
 import org.jboss.jreadline.terminal.WindowsTerminal;
 
 
 
 
 public class Main
 {
   public static final String JAVOSIZE_VERSION = "v.1.1.3";
   private static final StateHandler stateHandler = new StateHandler();
   private static Log log = new Log(Main.class.getName());
   private static boolean backgroundMode = false;
   private static State entity = null;
   private static String command = null;
   private static String jvmPID = null;
   private static boolean executingRecipe = false;
   
   private static boolean waitForConnectionsMode = false;
   private static int listeningPort = 0;
   
   private static final long MAX_TIME_WAITING_FOR_AGENT_CONNECTION = 30000L;
   
   private static ConcurrentHashMap<Long, String> pendingNotifications = new ConcurrentHashMap();
   
   private static final int MAX_NUMBER_OF_PENDING_NOTIFICATIONS = 10;
   
   private static Controller controller;
   private static Console console;
   private static String lastCommand = null;
   
   public static void main(String[] args) throws Exception {
     if (!validateArgs(args)) {
       System.out.println("usage: java -jar javOSize.jar [-entity=\"<entity_name>\" -command=\"<javOSize_command>\"] [-waitForConnections] [-port=<listening_port>] [-b] [PID]");
       System.exit(0);
     }
     
     boolean comandLineMode = (entity != null) && (command != null);
     
     if (comandLineMode) {
       Log.setLogLevel(LogLevel.FATAL);
     } else {
       Log.setLogLevel(Environment.get("LOG_LEVEL"), true);
     }
     
     controller = Controller.getInstance();
     
     if (waitForConnectionsMode) {
       waitingForConnection();
     } else {
       try {
         AgentLoader.injectToPid(jvmPID, controller.getPort());
       } catch (InvocationTargetException ite) {
         handleError(args, ite);
       }
     }
     
     updateAgentConfiguration();
     
     if (comandLineMode) {
       parseComandLineInvocation();
     } else {
       initThreads();
       if (!backgroundMode) {
         dispatchConsole();
         shutdown();
       }
     }
   }
   
   private static void shutdown() {
     try {
       console.stop();
       for (BreakPoint b : BreakPointManger.getBreakPoints()) {
         b.finish();
       }
       Controller.getInstance().finish();
     } catch (Exception e) {
       System.out.println("ERROR " + e);
       e.printStackTrace();
     }
   }
   
   private static void dispatchConsole() throws IOException {
     setupConsole();
     
     welcomeMessage();
     
 
     ConsoleOutput line;
     
     while ((line = console.read("\033[33m[javOSize@JVM \033[32m/" + stateHandler
     
       .getStateHolder() + "\033[33m" + "]~> " + "\033[0m", "[javOSize@JVM /" + stateHandler
       
       .getStateHolder() + "]~> ")) != null)
     {
 
       lastCommand = line.getBuffer();
       
 
       if ((line.getBuffer().equalsIgnoreCase("quit")) || 
         (line.getBuffer().equalsIgnoreCase("exit"))) {
         break;
       }
       
 
 
       if (line.getControlOperator() == ControlOperator.PIPE) {
         stateHandler.setPreviousCommandResult(stateHandler
           .executeOperation(line.getBuffer(), false));
       }
       else
       {
         console.pushToStdOut(stateHandler
           .executeOperation(line.getBuffer(), false));
       }
       
 
       if ((line.getControlOperator() == ControlOperator.NONE) && 
         (!pendingNotifications.isEmpty())) {
         ConcurrentHashMap<Long, String> messages = pendingNotifications;
         pendingNotifications = new ConcurrentHashMap();
         for ( Object obj: messages.entrySet()) {
					Map.Entry<Long, String> message = (Map.Entry<Long, String>)obj;
           console.pushToStdOut("\nNOTIFICATION\n------------\n" + (String)message.getValue() + "\n");
         }
       }
     }
   }
   
   private static void setupConsole() throws IOException
   {
     Settings.getInstance().setReadInputrc(false);
     console = new Console();
     registerCompleter(console);
   }
   
 
 
 
 
   private static void welcomeMessage()
   {
     if (!waitForConnectionsMode) {
       System.out.println("Welcome to javOSize v.1.1.3 :)");
       System.out.println("We are committed to make this the best tool for you. Please, send us feedback or bugs to info@javosize.com. Thanks!!");
       System.out.println("Agent has been injected in PID " + jvmPID);
     }
   }
   
   private static void handleError(String[] args, InvocationTargetException ite)
   {
     if (ite.getCause().getMessage().equals("No such process")) {
       System.out.println("Invalid PID: " + args[0] + ", please use a valid Process ID for a running JVM.");
       System.exit(0);
     } else if (ite.getCause().getMessage().equals("Operation not permitted")) {
       System.out.println("ITE: " + ite);
       ite.printStackTrace();
       System.out.println("PID: " + args[0] + " is running with a different user than javOSize. Be sure you attach javOSize with the same user than the JVM you want to access is started with.");
       System.exit(0);
     } else if (ite.getCause().getMessage().equals("Connection refused")) {
       System.out.println("PID: " + args[0] + " doesn't seem to be a JVM. Be sure you attach javOSize to a JVM.");
       System.exit(0);
     } else if (ite.getCause().getMessage().startsWith("Unable to open socket file")) {
       System.out.println("Invalid user. Assure the user you are running javOSize with is the same one you are running the monitored application with. Other reason to get this error is you are running with JVM that does not support attach protocol, for instance 1.6.0 IBM J9, try upgrading to a newer JVM version in that case. Final reason why this could happen is you run the AppServer with one JVM and javosize with a different one: trying running me with the same JVM than the AppServer");
       System.exit(0);
     } else if ((ite.getCause().getMessage().contains("Agent JAR loaded but agent failed to initialize")) && (AgentLoader.getAgentPath() != null)) {
       String path = AgentLoader.getAgentPath();
       if ((path.indexOf("/") >= 0) && (path.indexOf("/", path.indexOf("/") + 1) > 0) && (path.indexOf("/", path.indexOf("/", path.indexOf("/") + 1) + 1) > 0)) {
         path = path.substring(0, path.indexOf("/", path.indexOf("/", path.indexOf("/") + 1) + 2));
       } else if ((path.indexOf("/") >= 0) && (path.indexOf("/", path.indexOf("/") + 1) > 0)) {
         path = path.substring(0, path.indexOf("/", path.indexOf("/") + 2));
       }
       
       System.out.println("ERROR: Unable to initialize agent!! \nYou can obtain more info at standard output of your Java application. \n\nOne possible cause is that your security policies are not allowing the agent to be initialized. \nThe agent is located at a temporary file like " + 
       
 
         AgentLoader.getAgentPath() + ". \n" + "So, you can add to your security policies the following grant: \n" + "    grant codeBase \"file:" + path + "-\" { \n" + "         permission java.security.AllPermission; \n" + "    };\n" + "");
       
 
 
 
 
 
       System.exit(0);
     } else {
       System.out.println("Generic error trying to attach: " + ite.getCause().getMessage());
       ite.printStackTrace();
       System.exit(0);
     }
   }
   
   private static void waitingForConnection() {
     System.out.println("Welcome to javOSize v.1.1.3 :)");
     System.out.println("We are committed to make this the best tool for you. Please, send us feedback or bugs to info@javosize.com. Thanks!!");
     System.out.print("Waiting for agent connection at port " + listeningPort + "...");
     long startTime = System.currentTimeMillis();
     while (System.currentTimeMillis() < 30000L + startTime) {
       if (controller.isConnected()) {
         System.out.println("Agent connected using port " + listeningPort + "!!");
         return;
       }
       System.out.print(".");
       try {
         Thread.sleep(1000L);
       }
       catch (Throwable localThrowable) {}
     }
     
 
     System.out.println("Time out waiting for agent connection!!");
     System.out.println("Please, check that you have installed javOSize using javaagent option in your application.");
     System.out.println("Also, don't forget to check the connection properties, if you are not running both processes at the same server.");
     System.exit(0);
   }
   
   public static void updateAgentConfiguration() {
     try {
       log.debug("Updating agent configuration.");
       SetAgentConfigurationAction setConfig = new SetAgentConfigurationAction(Log.getLogLevel(), Log.isErrorReportEnabled());
       Controller.getInstance().execute(setConfig);
     } catch (Throwable th) {
       log.error("Error updating agent configuration: " + th, th);
     }
   }
   
   public static void initThreads() throws IOException, ClassNotFoundException
   {
     HelloSender hello = new HelloSender();
     hello.start();
     Repository.getRepository();
     if (backgroundMode) {
       Scheduler.getScheduler();
     }
     registerShutdownHook();
   }
   
   private static void parseComandLineInvocation() {
     stateHandler.setStateHolder(entity);
     String result = stateHandler.executeParsingConsoleControlOperators(command, true);
     System.out.println(result);
     System.exit(0);
   }
   
   private static boolean validateArgs(String[] args) {
     boolean validArgs = true;
     
 
     if (args.length == 0) {
       return false;
     }
     
 
     Integer pid = getIntFromString(args[(args.length - 1)]);
     
     jvmPID = "" + pid;
     backgroundMode = false;
     waitForConnectionsMode = false;
     entity = null;
     command = null;
     
 
 
     int argsToCheck = pid != null ? args.length - 1 : args.length;
     
     for (int i = 0; i < argsToCheck; i++) {
       if (args[i].equals("-b")) {
         backgroundMode = true;
       } else if (args[i].startsWith("-entity")) {
         entity = State.valueOf(args[i].substring(args[i].indexOf("=") + 1, args[i].length()).replace("\"", ""));
         if (entity == null) {
           System.out.println("Invalid entity: " + args[i]);
           return false;
         }
       } else if (args[i].startsWith("-command")) {
         command = args[i].substring(args[i].indexOf("=") + 1, args[i].length()).replace("\"", "");
       } else if (args[i].equals("-waitForConnections")) {
         waitForConnectionsMode = true;
         listeningPort = 6666;
       } else if (args[i].startsWith("-port")) {
         String listeningPortStr = args[i].substring(args[i].indexOf("=") + 1, args[i].length()).replace("\"", "");
         Integer portValue = getIntFromString(listeningPortStr);
         if (portValue == null) {
           System.out.println("Invalid value for parameter \"port\": " + args[i]);
           return false;
         }
         listeningPort = portValue.intValue();
       }
       else {
         System.out.println("Invalid parameter: " + args[i]);
         return false;
       }
     }
     
     if ((pid == null) && (!waitForConnectionsMode))
     {
       return false;
     }
     
     if (((entity != null) && (command == null)) || ((entity == null) && (command != null))) {
       return false;
     }
     
     if ((backgroundMode) && ((entity != null) || (command != null))) {
       System.out.println("Background mode not compatible with just one command execution mode.");
       return false;
     }
     
     return validArgs;
   }
   
   private static Integer getIntFromString(String val) {
     try {
       return Integer.valueOf(val);
     } catch (Throwable th) {}
     return null;
   }
   
   private static void registerCompleter(Console console)
   {
     Completion completer = new ConsoleCompletion(stateHandler);
     console.addCompletion(completer);
   }
   
   public static boolean isBackgroundMode() {
     return backgroundMode;
   }
   
   public static void setBackgroundMode(boolean backgroundModeValue) {
     backgroundMode = backgroundModeValue;
   }
   
   private static void registerShutdownHook() {
     Runtime.getRuntime().addShutdownHook(new AgentShutdownHook(backgroundMode, false));
   }
   
   public static String executeRecipe(Recipe r, String params) {
     return stateHandler.executeRecipe(r, params);
   }
   
   public static Terminal getTerminal() {
     if (console == null) {
       return null;
     }
     return console.getTerminal();
   }
   
   public static boolean isWindowsTerminal() {
     if ((console == null) || (console.getTerminal() == null)) {
       return false;
     }
     return console.getTerminal() instanceof WindowsTerminal;
   }
   
   public static int getTerminalWidth() {
     if (console == null) {
       return 150;
     }
     return console.getTerminalWidth();
   }
   
   public static int getTerminalHeight() {
     if (console == null) {
       return 40;
     }
     return console.getTerminalHeight();
   }
   
   public static boolean askForConfirmation(String message) {
     if ((console == null) || ((entity != null) && (command != null)) || (isExecutingRecipe())) {
       return true;
     }
     return console.askForConfirmation(message);
   }
   
   public static boolean isRecording() {
     if (console == null) {
       return false;
     }
     return console.isRecording();
   }
   
   public static String getRecordName() {
     return console.getRecordName();
   }
   
   public static void startRecordMode(String name) {
     console.startRecordMode(name);
   }
   
   public static List<String> stopRecordMode() {
     return console.stopRecordMode();
   }
   
   public static boolean isExecutingRecipe() {
     return executingRecipe;
   }
   
   public static void setExecutingRecipe(boolean executingRecipeEnabled) {
     executingRecipe = executingRecipeEnabled;
   }
   
   public static StateHandler getStateHandler() {
     return stateHandler;
   }
   
   public static void stopConsole() {
     try {
       if (console != null) console.stop();
     } catch (Throwable th) {
       log.error("Error stopping console: " + th, th);
     }
   }
   
   public static void restartConsole() {
     try {
       if (console != null) {
         console.restart();
         registerCompleter(console);
       }
     } catch (Throwable th) {
       log.error("Error stopping console: " + th, th);
     }
   }
   
 
 
 
 
 
 
   public static void addNotification(Long id, String notification)
   {
     if (pendingNotifications.size() < 10) {
       pendingNotifications.put(id, notification);
     } else {
       log.debug("Notification discarded because queue is full. [ID=" + id + "][Message=" + notification + "]");
     }
   }
   
   public static String getLastCommand() {
     if ((console == null) && (entity != null) && (command != null))
       return "[Entity=" + entity + "] " + command;
     if (console == null) {
       return "Not in console mode";
     }
     return lastCommand;
   }
   
   public static int getListeningPort() {
     return listeningPort;
   }
 }


