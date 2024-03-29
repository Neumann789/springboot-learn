 package org.jboss.jreadline.console;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.IOException;
 import java.util.List;
 import java.util.logging.Logger;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import org.jboss.jreadline.console.settings.Settings;
 import org.jboss.jreadline.console.settings.VariableSettings;
 import org.jboss.jreadline.edit.KeyOperationManager;
 import org.jboss.jreadline.edit.Mode;
 import org.jboss.jreadline.edit.mapper.KeyMapper;
 import org.jboss.jreadline.terminal.Terminal;
 import org.jboss.jreadline.util.LoggerUtil;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Config
 {
   private static String lineSeparator = System.getProperty("line.separator");
   private static String pathSeparator = System.getProperty("file.separator");
   
   private static boolean posixCompatible = (!System.getProperty("os.name").startsWith("Windows")) && 
     (!System.getProperty("os.name").startsWith("OS/2"));
   
   public static boolean isOSPOSIXCompatible() {
     return posixCompatible;
   }
   
   public static String getLineSeparator() {
     return lineSeparator;
   }
   
   public static String getPathSeparator() {
     return pathSeparator;
   }
   
   public static String getPathSeparatorForRegexExpressions() {
     if (pathSeparator.equals("\\")) {
       return "\\\\";
     }
     return pathSeparator;
   }
   
 
 
 
 
 
 
 
 
   protected static void parseInputrc(Settings settings)
     throws IOException
   {
     Logger logger = LoggerUtil.getLogger("Config");
     if (!settings.getInputrc().isFile()) {
       logger.info("Error while parsing: " + settings.getInputrc().getAbsolutePath() + " couldn't find file.");
       return;
     }
     
     Pattern variablePattern = Pattern.compile("^set\\s+(\\S+)\\s+(\\S+)$");
     Pattern commentPattern = Pattern.compile("^#.*");
     
     Pattern keyQuoteNamePattern = Pattern.compile("(^\\\"\\S+)(\\\":\\s+)(\\S+)");
     Pattern keyNamePattern = Pattern.compile("(^\\S+)(:\\s+)(\\S+)");
     Pattern keySeqPattern = Pattern.compile("^\"keyseq:\\s+\\b");
     Pattern startConstructs = Pattern.compile("^\\$if");
     Pattern endConstructs = Pattern.compile("^\\$endif");
     
     Pattern keyOperationPattern = Pattern.compile("(^\\\"\\\\M-\\[D:)(\\s+)(\\S+)");
     
 
     BufferedReader reader = new BufferedReader(new FileReader(settings.getInputrc()));
     
 
     boolean constructMode = false;
     String line; while ((line = reader.readLine()) != null) {
       if ((line.trim().length() >= 1) && 
       
 
         (!commentPattern.matcher(line).matches()))
       {
 
         if (startConstructs.matcher(line).matches()) {
           constructMode = true;
 
         }
         else if (endConstructs.matcher(line).matches()) {
           constructMode = false;
 
 
         }
         else if (!constructMode)
         {
 
 
 
 
           Matcher variableMatcher = variablePattern.matcher(line);
           if (variableMatcher.matches()) {
             parseVariables(variableMatcher.group(1), variableMatcher.group(2), settings);
           }
           
           if (isOSPOSIXCompatible()) {
             Matcher keyQuoteMatcher = keyQuoteNamePattern.matcher(line);
             if (keyQuoteMatcher.matches()) {
               settings.getOperationManager().addOperation(
                 KeyMapper.mapQuoteKeys(keyQuoteMatcher.group(1), keyQuoteMatcher
                 .group(3)));
             }
             Matcher keyMatcher = keyNamePattern.matcher(line);
             if (keyMatcher.matches()) {
               settings.getOperationManager().addOperation(KeyMapper.mapKeys(keyMatcher.group(1), keyMatcher.group(3)));
             }
           }
         }
       }
     }
   }
   
   private static void parseVariables(String variable, String value, Settings settings)
   {
     Logger logger = LoggerUtil.getLogger("Config");
     if (variable.equals(VariableSettings.EDITING_MODE.getVariable())) {
       if (VariableSettings.EDITING_MODE.getValues().contains(value)) {
         if (value.equals("vi")) {
           settings.setEditMode(Mode.VI);
         } else {
           settings.setEditMode(Mode.EMACS);
         }
       }
       else {
         logger.warning("Value " + value + " not accepted for: " + variable + ", only: " + VariableSettings.EDITING_MODE
           .getValues());
       }
     }
     else if (variable.equals(VariableSettings.BELL_STYLE.getVariable())) {
       if (VariableSettings.BELL_STYLE.getValues().contains(value)) {
         settings.setBellStyle(value);
       } else {
         logger.warning("Value " + value + " not accepted for: " + variable + ", only: " + VariableSettings.BELL_STYLE
           .getValues());
       }
     } else if (variable.equals(VariableSettings.HISTORY_SIZE.getVariable())) {
       try {
         settings.setHistorySize(Integer.parseInt(value));
       }
       catch (NumberFormatException nfe) {
         logger.warning("Value " + value + " not accepted for: " + variable + ", it must be an integer.");
       }
       
     }
     else if (variable.equals(VariableSettings.DISABLE_COMPLETION.getVariable())) {
       if (VariableSettings.DISABLE_COMPLETION.getValues().contains(value)) {
         if (value.equals("on")) {
           settings.setDisableCompletion(true);
         } else {
           settings.setDisableCompletion(false);
         }
       } else
         logger.warning("Value " + value + " not accepted for: " + variable + ", only: " + VariableSettings.DISABLE_COMPLETION
           .getValues());
     }
   }
   
   protected static void readRuntimeProperties(Settings settings) {
     try {
       String term = System.getProperty("jreadline.terminal");
       if ((term != null) && (term.length() > 0)) {
         settings.setTerminal((Terminal)settings.getClass().getClassLoader().loadClass(term).newInstance());
       }
       String editMode = System.getProperty("jreadline.editmode");
       if ((editMode != null) && (editMode.length() > 0)) {
         if (editMode.equalsIgnoreCase("VI")) {
           settings.setEditMode(Mode.VI);
         } else if (editMode.equalsIgnoreCase("EMACS"))
           settings.setEditMode(Mode.EMACS);
       }
       String readInputrc = System.getProperty("jreadline.readinputrc");
       if ((readInputrc != null) && (readInputrc.length() > 0) && (
         (readInputrc.equalsIgnoreCase("true")) || 
         (readInputrc.equalsIgnoreCase("false")))) {
         settings.setReadInputrc(Boolean.parseBoolean(readInputrc));
       }
       String inputrc = System.getProperty("jreadline.inputrc");
       if ((inputrc != null) && (inputrc.length() > 0) && 
         (new File(inputrc).isFile())) {
         settings.setInputrc(new File(inputrc));
       }
       String historyFile = System.getProperty("jreadline.historyfile");
       if ((historyFile != null) && (historyFile.length() > 0) && 
         (new File(historyFile).isFile())) {
         settings.setHistoryFile(new File(historyFile));
       }
       String historyPersistent = System.getProperty("jreadline.historypersistent");
       if ((historyPersistent != null) && (historyPersistent.length() > 0) && (
         (historyPersistent.equalsIgnoreCase("true")) || 
         (historyPersistent.equalsIgnoreCase("false")))) {
         settings.setHistoryPersistent(Boolean.parseBoolean(historyPersistent));
       }
       String historyDisabled = System.getProperty("jreadline.historydisabled");
       if ((historyDisabled != null) && (historyDisabled.length() > 0) && (
         (historyDisabled.equalsIgnoreCase("true")) || 
         (historyDisabled.equalsIgnoreCase("false")))) {
         settings.setHistoryDisabled(Boolean.parseBoolean(historyDisabled));
       }
       String historySize = System.getProperty("jreadline.historysize");
       if ((historySize != null) && (historySize.length() > 0)) {
         settings.setHistorySize(Integer.parseInt(historySize));
       }
       String doLogging = System.getProperty("jreadline.logging");
       if ((doLogging != null) && (doLogging.length() > 0) && (
         (doLogging.equalsIgnoreCase("true")) || 
         (doLogging.equalsIgnoreCase("false")))) {
         settings.setLogging(Boolean.parseBoolean(doLogging));
       }
       String logFile = System.getProperty("jreadline.logfile");
       if ((logFile != null) && (logFile.length() > 0)) {
         settings.setLogFile(logFile);
       }
       String disableCompletion = System.getProperty("jreadline.disablecompletion");
       if ((disableCompletion != null) && (disableCompletion.length() > 0) && (
         (disableCompletion.equalsIgnoreCase("true")) || 
         (disableCompletion.equalsIgnoreCase("false")))) {
         settings.setDisableCompletion(Boolean.parseBoolean(disableCompletion));
       }
     }
     catch (ClassNotFoundException e) {
       e.printStackTrace();
     } catch (InstantiationException e) {
       e.printStackTrace();
     } catch (IllegalAccessException e) {
       e.printStackTrace();
     }
   }
 }


