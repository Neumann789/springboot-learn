 package com.javosize.log;
 
 import com.javosize.agent.Agent;
 import com.javosize.agent.CacheManager;
 import com.javosize.cli.Main;
 import com.javosize.communication.client.AgentReportSender;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.Method;
 import java.text.Format;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.Map;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Log
 {
   private static LogLevel logLevel = LogLevel.INFO;
   
 
 
   private static final int REPORT_SIZE = 10;
   
 
 
   private static Map lastLogTraces = CacheManager.getCacheLRU(10, true);
   
 
   private static final String ERROR_REPORT_ENABLED = "javosize.error.report.enabled";
   
 
   private static boolean isErrorReportEnabled = true;
   
   static { if (System.getProperty("javosize.error.report.enabled", "true").equals("false")) {
       isErrorReportEnabled = false;
     }
   }
   
   public static void setErrorReportEnabled(boolean errorReportEnabled) {
     isErrorReportEnabled = errorReportEnabled;
   }
   
   public static boolean isErrorReportEnabled() {
     return isErrorReportEnabled;
   }
   
   public static void setLogLevel(LogLevel level) {
     logLevel = level;
   }
   
   public static void setLogLevel(String level) throws Exception {
     setLogLevel(level, false);
   }
   
   public static LogLevel getLogLevel() {
     return logLevel;
   }
   
   public static void setLogLevel(String level, boolean discardSilently) throws Exception {
     try {
       LogLevel newLogLevel = LogLevel.valueOf(level);
       setLogLevel(newLogLevel);
     } catch (Throwable th) {
       if (!discardSilently) {
         throw new Exception("Invalid log level " + level + ". Possible values: TRACE, DEBUG, INFO, WARN, ERROR, FATAL");
       }
     }
   }
   
   private String name = "";
   private Format formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
   
   public Log(String name) {
     this.name = name;
   }
   
   private void log(LogLevel level, String message) {
     log(level, message, null);
   }
   
   private void log(LogLevel level, String message, Throwable t)
   {
     if (isLoggable(level)) {
       System.out.println(this.formatter.format(new Date()) + " [javOSize] [" + this.name + "] [" + level + "] " + message);
       if (t != null) {
         t.printStackTrace();
       }
     }
     
 
     addLogToCache("[" + this.formatter.format(new Date()) + "] [" + this.name + "] [" + level + "] " + message, t);
     
 
 
 
 
     if (hasToSendReport(level)) {
       sendReport();
       
 
 
 
 
       if (!Agent.isAgent()) {
         System.out.println("Oops! This shouldn't have happened. Please, feel free to send us an email to info@javosize.com with the log traces and the steps to reproduce them. ");
       }
     }
   }
   
 
 
 
 
 
 
 
   private static void addLogToCache(String messageToPrint, Throwable t)
   {
     lastLogTraces.put(Long.valueOf(System.currentTimeMillis()), messageToPrint + getStackTrace(t));
   }
   
   private boolean isLoggable(LogLevel messageLogLevel) {
     if (messageLogLevel.ordinal() >= logLevel.ordinal()) {
       return true;
     }
     return false;
   }
   
   private boolean hasToSendReport(LogLevel messageLogLevel) {
     if (messageLogLevel.ordinal() >= LogLevel.ERROR.ordinal()) {
       return true;
     }
     return false;
   }
   
   public void debug(String message) {
     log(LogLevel.DEBUG, message);
   }
   
   public void debug(String message, Throwable t) {
     log(LogLevel.DEBUG, message, t);
   }
   
   public void error(String message) {
     log(LogLevel.ERROR, message);
   }
   
   public void error(String message, Throwable t) {
     log(LogLevel.ERROR, message, t);
   }
   
   public void fatal(String message) {
     log(LogLevel.FATAL, message);
   }
   
   public void fatal(String message, Throwable t) {
     log(LogLevel.FATAL, message, t);
   }
   
   public void info(String message) {
     log(LogLevel.INFO, message);
   }
   
   public void info(String message, Throwable t) {
     log(LogLevel.INFO, message, t);
   }
   
   public void trace(String message) {
     log(LogLevel.TRACE, message);
   }
   
   public void trace(String message, Throwable t) {
     log(LogLevel.TRACE, message, t);
   }
   
   public void warn(String message) {
     log(LogLevel.WARN, message);
   }
   
   public void warn(String message, Throwable t) {
     log(LogLevel.WARN, message, t);
   }
   
   public boolean isDebugEnabled() {
     return isLoggable(LogLevel.DEBUG);
   }
   
   public boolean isErrorEnabled() {
     return isLoggable(LogLevel.ERROR);
   }
   
   public boolean isFatalEnabled() {
     return isLoggable(LogLevel.FATAL);
   }
   
   public boolean isInfoEnabled() {
     return isLoggable(LogLevel.INFO);
   }
   
   public boolean isTraceEnabled() {
     return isLoggable(LogLevel.TRACE);
   }
   
   public boolean isWarnEnabled() {
     return isLoggable(LogLevel.WARN);
   }
   
 
   private static volatile long lastReportTS = -1L;
   private static final long TIME_BETWEEN_REPORTS = 60000L;
   private static volatile int numberOfReportsSent = 0;
   
 
 
 
 
 
 
 
   private void sendReport()
   {
     if (!isErrorReportEnabled) {
       return;
     }
     
     if ((numberOfReportsSent > 0) && (System.currentTimeMillis() > lastReportTS + 60000L)) {
       numberOfReportsSent = 0;
     }
     
 
     if (numberOfReportsSent < 3) {
       try {
         numberOfReportsSent += 1;
         lastReportTS = System.currentTimeMillis();
         
         String command = "NULL";
         if (Agent.isAgent()) {
           command = "Agent Execution";
         } else {
           command = Main.getLastCommand();
         }
         
         Map tmp = lastLogTraces;
         lastLogTraces = CacheManager.getCacheLRU(10, true);
         StringBuffer message = new StringBuffer();
         long reportTS = System.currentTimeMillis();
         for (Object logTrace : tmp.values()) {
           message.append("\n [ReportID=" + reportTS + "] ");
           message.append(logTrace.toString().replace("\n", "\n [ReportID=" + reportTS + "] "));
         }
         
         if (Agent.isAgent()) {
           AgentReportSender report = new AgentReportSender(command, message.toString());
           report.start();
         } else {
           Object report = Class.forName("com.javosize.communication.client.ReportSender").getConstructor(new Class[] { String.class, String.class }).newInstance(new Object[] { command, message.toString() });
           Method method = report.getClass().getMethod("start", new Class[0]);
           method.invoke(report, new Object[0]);
         }
       } catch (Throwable th) {
         trace("Error sending report " + th, th);
       }
     }
   }
   
 
 
 
 
 
   public static String getStackTrace(Throwable th)
   {
     String message = "";
     if (th != null) {
       StringWriter sw = new StringWriter();
       PrintWriter pw = new PrintWriter(sw);
       th.printStackTrace(pw);
       message = message + "\n" + sw.toString();
     }
     return message;
   }
 }


