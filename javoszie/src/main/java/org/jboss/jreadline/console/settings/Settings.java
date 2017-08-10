 package org.jboss.jreadline.console.settings;
 
 import java.io.File;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.util.List;
 import org.jboss.jreadline.console.Config;
 import org.jboss.jreadline.console.reader.ConsoleInputSession;
 import org.jboss.jreadline.edit.EditMode;
 import org.jboss.jreadline.edit.EmacsEditMode;
 import org.jboss.jreadline.edit.KeyOperationFactory;
 import org.jboss.jreadline.edit.KeyOperationManager;
 import org.jboss.jreadline.edit.Mode;
 import org.jboss.jreadline.edit.ViEditMode;
 import org.jboss.jreadline.terminal.POSIXTerminal;
 import org.jboss.jreadline.terminal.Terminal;
 import org.jboss.jreadline.terminal.WindowsTerminal;
 
 
 
 
 
 
 
 
 
 
 
 
 public class Settings
 {
   private String name;
   private Mode editMode = Mode.EMACS;
   private File historyFile;
   private int historySize = 500;
   private boolean historyDisabled = false;
   private boolean historyPersistent = true;
   private String bellStyle;
   private boolean ansiConsole = true;
   private InputStream inputStream;
   private OutputStream stdOut;
   private OutputStream stdErr;
   private Terminal terminal;
   private boolean readInputrc = true;
   private File inputrc;
   private boolean isLogging = true;
   private String logFile;
   private boolean disableCompletion = false;
   private boolean readAhead = true;
   private QuitHandler quitHandler;
   private KeyOperationManager operationManager = new KeyOperationManager();
   private File aliasFile;
   private boolean aliasEnabled = true;
   
   private static final Settings INSTANCE = new Settings();
   
 
 
   public static Settings getInstance()
   {
     return INSTANCE;
   }
   
   public void resetToDefaults() {
     setName("jreadline");
     this.editMode = Mode.EMACS;
     this.historyFile = null;
     this.historySize = 500;
     this.historyDisabled = false;
     this.historyPersistent = true;
     this.bellStyle = null;
     this.ansiConsole = true;
     this.inputStream = null;
     setStdOut(null);
     setStdErr(null);
     this.terminal = null;
     this.readInputrc = true;
     this.logFile = null;
     this.disableCompletion = false;
     setQuitHandler(null);
     this.operationManager.clear();
     setAliasEnabled(true);
   }
   
 
 
 
 
   public void setName(String name)
   {
     this.name = name;
   }
   
 
 
 
 
   public String getName()
   {
     if (this.name == null)
       this.name = "jreadline";
     return this.name;
   }
   
 
 
 
 
 
   public Mode getEditMode()
   {
     return this.editMode;
   }
   
   public void setEditMode(Mode editMode) {
     this.editMode = editMode;
   }
   
 
 
 
 
   public EditMode getFullEditMode()
   {
     if (Config.isOSPOSIXCompatible()) {
       if (getEditMode() == Mode.EMACS) {
         return new EmacsEditMode(getOperationManager());
       }
       return new ViEditMode(getOperationManager());
     }
     
     if (getEditMode() == Mode.EMACS) {
       return new EmacsEditMode(getOperationManager());
     }
     return new ViEditMode(getOperationManager());
   }
   
   public void resetEditMode()
   {
     this.operationManager.clear();
   }
   
   public KeyOperationManager getOperationManager() {
     if (this.operationManager.getOperations().size() < 1) {
       if (Config.isOSPOSIXCompatible()) {
         if (getEditMode() == Mode.EMACS) {
           this.operationManager.addOperations(KeyOperationFactory.generatePOSIXEmacsMode());
         } else {
           this.operationManager.addOperations(KeyOperationFactory.generatePOSIXViMode());
         }
         
       }
       else if (getEditMode() == Mode.EMACS) {
         this.operationManager.addOperations(KeyOperationFactory.generateWindowsEmacsMode());
       } else {
         this.operationManager.addOperations(KeyOperationFactory.generateWindowsViMode());
       }
     }
     return this.operationManager;
   }
   
 
 
 
 
 
 
 
 
 
   public File getHistoryFile()
   {
     if (this.historyFile == null)
     {
       return new File(System.getProperty("user.home") + Config.getPathSeparator() + ".jreadline_history");
     }
     
     return this.historyFile;
   }
   
   public void setHistoryFile(File historyFile) {
     this.historyFile = historyFile;
   }
   
 
 
 
 
 
   public int getHistorySize()
   {
     return this.historySize;
   }
   
 
 
 
 
 
   public void setHistorySize(int historySize)
   {
     this.historySize = historySize;
   }
   
 
 
 
 
 
   public String getBellStyle()
   {
     return this.bellStyle;
   }
   
   public void setBellStyle(String bellStyle) {
     this.bellStyle = bellStyle;
   }
   
 
 
   public boolean isAnsiConsole()
   {
     return this.ansiConsole;
   }
   
 
 
 
 
   public void setAnsiConsole(boolean ansiConsole)
   {
     this.ansiConsole = ansiConsole;
   }
   
 
 
 
 
   public InputStream getInputStream()
   {
     if (this.inputStream == null) {
       if (Config.isOSPOSIXCompatible()) {
         this.inputStream = new ConsoleInputSession(System.in).getExternalInputStream();
       } else
         this.inputStream = System.in;
     }
     return this.inputStream;
   }
   
 
 
 
 
   public void setInputStream(InputStream inputStream)
   {
     this.inputStream = inputStream;
   }
   
 
 
 
   public OutputStream getStdOut()
   {
     if (this.stdOut == null) {
       return System.out;
     }
     return this.stdOut;
   }
   
 
 
 
   public void setStdOut(OutputStream stdOut)
   {
     this.stdOut = stdOut;
   }
   
 
 
 
   public OutputStream getStdErr()
   {
     if (this.stdErr == null) {
       return System.err;
     }
     return this.stdErr;
   }
   
 
 
 
   public void setStdErr(OutputStream stdErr)
   {
     this.stdErr = stdErr;
   }
   
 
 
 
 
 
   public Terminal getTerminal()
   {
     if (this.terminal == null) {
       if (Config.isOSPOSIXCompatible()) {
         this.terminal = new POSIXTerminal();
       } else {
         this.terminal = new WindowsTerminal();
       }
     }
     return this.terminal;
   }
   
 
 
 
   public void setTerminal(Terminal terminal)
   {
     this.terminal = terminal;
   }
   
 
 
 
 
 
   public File getInputrc()
   {
     if (this.inputrc == null) {
       this.inputrc = new File(System.getProperty("user.home") + Config.getPathSeparator() + ".inputrc");
     }
     return this.inputrc;
   }
   
   public void setInputrc(File inputrc) {
     this.inputrc = inputrc;
   }
   
 
 
 
 
   public boolean isLogging()
   {
     return this.isLogging;
   }
   
 
 
 
 
   public void setLogging(boolean logging)
   {
     this.isLogging = logging;
   }
   
 
 
 
 
 
   public boolean isDisableCompletion()
   {
     return this.disableCompletion;
   }
   
 
 
 
 
 
   public void setDisableCompletion(boolean disableCompletion)
   {
     this.disableCompletion = disableCompletion;
   }
   
 
 
 
 
   public String getLogFile()
   {
     if (this.logFile == null) {
       if (Config.isOSPOSIXCompatible()) {
         this.logFile = "/tmp/jreadline.log";
       } else
         this.logFile = "jreadline.log";
     }
     return this.logFile;
   }
   
 
 
 
 
   public void setLogFile(String logFile)
   {
     this.logFile = logFile;
   }
   
 
 
 
 
 
   public boolean doReadInputrc()
   {
     return this.readInputrc;
   }
   
 
 
 
 
 
   public void setReadInputrc(boolean readInputrc)
   {
     this.readInputrc = readInputrc;
   }
   
 
 
 
 
 
   public boolean isHistoryDisabled()
   {
     return this.historyDisabled;
   }
   
 
 
 
 
 
   public void setHistoryDisabled(boolean historyDisabled)
   {
     this.historyDisabled = historyDisabled;
   }
   
 
 
 
 
 
   public boolean isHistoryPersistent()
   {
     return this.historyPersistent;
   }
   
 
 
 
 
 
   public void setHistoryPersistent(boolean historyPersistent)
   {
     this.historyPersistent = historyPersistent;
   }
   
 
 
 
 
 
 
   public boolean isReadAhead()
   {
     return this.readAhead;
   }
   
 
 
 
 
 
 
   public void setReadAhead(boolean readAhead)
   {
     this.readAhead = readAhead;
   }
   
   public void setAliasFile(File file) {
     this.aliasFile = file;
   }
   
   public File getAliasFile() {
     if (this.aliasFile == null) {
       this.aliasFile = new File(System.getProperty("user.home") + Config.getPathSeparator() + ".jreadlie_aliases");
     }
     return this.aliasFile;
   }
   
   public boolean isAliasEnabled() {
     return this.aliasEnabled;
   }
   
   public void setAliasEnabled(boolean enabled) {
     this.aliasEnabled = enabled;
   }
   
   public void setQuitHandler(QuitHandler qh) {
     this.quitHandler = qh;
   }
   
   public void quit() {
     if (this.quitHandler != null) {
       this.quitHandler.quit();
     }
   }
 }


