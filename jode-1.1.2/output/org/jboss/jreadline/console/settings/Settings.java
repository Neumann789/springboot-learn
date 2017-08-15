/* Settings - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.console.settings;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

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
    
    private Settings() {
	/* empty */
    }
    
    public static Settings getInstance() {
	return INSTANCE;
    }
    
    public void resetToDefaults() {
	setName("jreadline");
	editMode = Mode.EMACS;
	historyFile = null;
	historySize = 500;
	historyDisabled = false;
	historyPersistent = true;
	bellStyle = null;
	ansiConsole = true;
	inputStream = null;
	setStdOut(null);
	setStdErr(null);
	terminal = null;
	readInputrc = true;
	logFile = null;
	disableCompletion = false;
	setQuitHandler(null);
	operationManager.clear();
	setAliasEnabled(true);
    }
    
    public void setName(String name) {
	this.name = name;
    }
    
    public String getName() {
	if (name == null)
	    name = "jreadline";
	return name;
    }
    
    public Mode getEditMode() {
	return editMode;
    }
    
    public void setEditMode(Mode editMode) {
	this.editMode = editMode;
    }
    
    public EditMode getFullEditMode() {
	if (Config.isOSPOSIXCompatible()) {
	    if (getEditMode() == Mode.EMACS)
		return new EmacsEditMode(getOperationManager());
	    return new ViEditMode(getOperationManager());
	}
	if (getEditMode() == Mode.EMACS)
	    return new EmacsEditMode(getOperationManager());
	return new ViEditMode(getOperationManager());
    }
    
    public void resetEditMode() {
	operationManager.clear();
    }
    
    public KeyOperationManager getOperationManager() {
	if (operationManager.getOperations().size() < 1) {
	    if (Config.isOSPOSIXCompatible()) {
		if (getEditMode() == Mode.EMACS)
		    operationManager.addOperations
			(KeyOperationFactory.generatePOSIXEmacsMode());
		else
		    operationManager.addOperations(KeyOperationFactory
						       .generatePOSIXViMode());
	    } else if (getEditMode() == Mode.EMACS)
		operationManager.addOperations
		    (KeyOperationFactory.generateWindowsEmacsMode());
	    else
		operationManager.addOperations(KeyOperationFactory
						   .generateWindowsViMode());
	}
	return operationManager;
    }
    
    public File getHistoryFile() {
	if (historyFile == null)
	    return new File(new StringBuilder().append
				(System.getProperty("user.home")).append
				(Config.getPathSeparator()).append
				(".jreadline_history").toString());
	return historyFile;
    }
    
    public void setHistoryFile(File historyFile) {
	this.historyFile = historyFile;
    }
    
    public int getHistorySize() {
	return historySize;
    }
    
    public void setHistorySize(int historySize) {
	this.historySize = historySize;
    }
    
    public String getBellStyle() {
	return bellStyle;
    }
    
    public void setBellStyle(String bellStyle) {
	this.bellStyle = bellStyle;
    }
    
    public boolean isAnsiConsole() {
	return ansiConsole;
    }
    
    public void setAnsiConsole(boolean ansiConsole) {
	this.ansiConsole = ansiConsole;
    }
    
    public InputStream getInputStream() {
	if (inputStream == null) {
	    if (Config.isOSPOSIXCompatible())
		inputStream = new ConsoleInputSession(System.in)
				  .getExternalInputStream();
	    else
		inputStream = System.in;
	}
	return inputStream;
    }
    
    public void setInputStream(InputStream inputStream) {
	this.inputStream = inputStream;
    }
    
    public OutputStream getStdOut() {
	if (stdOut == null)
	    return System.out;
	return stdOut;
    }
    
    public void setStdOut(OutputStream stdOut) {
	this.stdOut = stdOut;
    }
    
    public OutputStream getStdErr() {
	if (stdErr == null)
	    return System.err;
	return stdErr;
    }
    
    public void setStdErr(OutputStream stdErr) {
	this.stdErr = stdErr;
    }
    
    public Terminal getTerminal() {
	if (terminal == null) {
	    if (Config.isOSPOSIXCompatible())
		terminal = new POSIXTerminal();
	    else
		terminal = new WindowsTerminal();
	}
	return terminal;
    }
    
    public void setTerminal(Terminal terminal) {
	this.terminal = terminal;
    }
    
    public File getInputrc() {
	if (inputrc == null)
	    inputrc = new File(new StringBuilder().append
				   (System.getProperty("user.home")).append
				   (Config.getPathSeparator()).append
				   (".inputrc").toString());
	return inputrc;
    }
    
    public void setInputrc(File inputrc) {
	this.inputrc = inputrc;
    }
    
    public boolean isLogging() {
	return isLogging;
    }
    
    public void setLogging(boolean logging) {
	isLogging = logging;
    }
    
    public boolean isDisableCompletion() {
	return disableCompletion;
    }
    
    public void setDisableCompletion(boolean disableCompletion) {
	this.disableCompletion = disableCompletion;
    }
    
    public String getLogFile() {
	if (logFile == null) {
	    if (Config.isOSPOSIXCompatible())
		logFile = "/tmp/jreadline.log";
	    else
		logFile = "jreadline.log";
	}
	return logFile;
    }
    
    public void setLogFile(String logFile) {
	this.logFile = logFile;
    }
    
    public boolean doReadInputrc() {
	return readInputrc;
    }
    
    public void setReadInputrc(boolean readInputrc) {
	this.readInputrc = readInputrc;
    }
    
    public boolean isHistoryDisabled() {
	return historyDisabled;
    }
    
    public void setHistoryDisabled(boolean historyDisabled) {
	this.historyDisabled = historyDisabled;
    }
    
    public boolean isHistoryPersistent() {
	return historyPersistent;
    }
    
    public void setHistoryPersistent(boolean historyPersistent) {
	this.historyPersistent = historyPersistent;
    }
    
    public boolean isReadAhead() {
	return readAhead;
    }
    
    public void setReadAhead(boolean readAhead) {
	this.readAhead = readAhead;
    }
    
    public void setAliasFile(File file) {
	aliasFile = file;
    }
    
    public File getAliasFile() {
	if (aliasFile == null)
	    aliasFile = new File(new StringBuilder().append
				     (System.getProperty("user.home")).append
				     (Config.getPathSeparator()).append
				     (".jreadlie_aliases").toString());
	return aliasFile;
    }
    
    public boolean isAliasEnabled() {
	return aliasEnabled;
    }
    
    public void setAliasEnabled(boolean enabled) {
	aliasEnabled = enabled;
    }
    
    public void setQuitHandler(QuitHandler qh) {
	quitHandler = qh;
    }
    
    public void quit() {
	if (quitHandler != null)
	    quitHandler.quit();
    }
}
