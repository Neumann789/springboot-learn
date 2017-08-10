 package org.jboss.jreadline.console;
 
 import com.javosize.cli.Main;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.logging.Logger;
 import org.jboss.jreadline.complete.CompleteOperation;
 import org.jboss.jreadline.complete.Completion;
 import org.jboss.jreadline.console.alias.Alias;
 import org.jboss.jreadline.console.alias.AliasCompletion;
 import org.jboss.jreadline.console.alias.AliasManager;
 import org.jboss.jreadline.console.helper.Search;
 import org.jboss.jreadline.console.operator.ControlOperator;
 import org.jboss.jreadline.console.operator.ControlOperatorParser;
 import org.jboss.jreadline.console.operator.RedirectionCompletion;
 import org.jboss.jreadline.console.settings.Settings;
 import org.jboss.jreadline.edit.EditMode;
 import org.jboss.jreadline.edit.Mode;
 import org.jboss.jreadline.edit.PasteManager;
 import org.jboss.jreadline.edit.ViEditMode;
 import org.jboss.jreadline.edit.actions.Action;
 import org.jboss.jreadline.edit.actions.EditAction;
 import org.jboss.jreadline.edit.actions.EditActionManager;
 import org.jboss.jreadline.edit.actions.Movement;
 import org.jboss.jreadline.edit.actions.Operation;
 import org.jboss.jreadline.edit.actions.PrevWordAction;
 import org.jboss.jreadline.history.FileHistory;
 import org.jboss.jreadline.history.History;
 import org.jboss.jreadline.history.InMemoryHistory;
 import org.jboss.jreadline.history.SearchDirection;
 import org.jboss.jreadline.terminal.Terminal;
 import org.jboss.jreadline.undo.UndoAction;
 import org.jboss.jreadline.undo.UndoManager;
 import org.jboss.jreadline.util.ANSI;
 import org.jboss.jreadline.util.FileUtils;
 import org.jboss.jreadline.util.LoggerUtil;
 import org.jboss.jreadline.util.Parser;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Console
 {
   public static final String ANSI_RESET = "\033[0m";
   public static final String ANSI_BLACK = "\033[30m";
   public static final String ANSI_RED = "\033[31m";
   public static final String ANSI_GREEN = "\033[32m";
   public static final String ANSI_YELLOW = "\033[33m";
   public static final String ANSI_BLUE = "\033[34m";
   public static final String ANSI_PURPLE = "\033[35m";
   public static final String ANSI_CYAN = "\033[36m";
   public static final String ANSI_WHITE = "\033[37m";
   public static final String RECORD_ICON = "\033[31m[REC] \033[0m";
   public static final String RECORD_ICON_NO_COLORS = "[REC] ";
   public static final int TERMINAL_DEFAULT_WIDTH = 150;
   public static final int TERMINAL_DEFAULT_HEITH = 40;
   private Buffer buffer;
   private Terminal terminal;
   private UndoManager undoManager;
   private PasteManager pasteManager;
   private EditMode editMode;
   private History history;
   private List<Completion> completionList;
   private Settings settings;
   private Search search;
   private Action prevAction = Action.EDIT;
   
   private boolean inEscapeMode = false;
   
   private boolean inRecordMode = false;
   private String recordName = null;
   private List<String> recordedCommands = null;
   
   private ConsoleCommand command;
   
   private boolean displayCompletion = false;
   private boolean askDisplayCompletion = false;
   private boolean running = false;
   
   private StringBuilder redirectPipeOutBuffer;
   private StringBuilder redirectPipeErrBuffer;
   private List<ConsoleOperation> operations;
   private ConsoleOperation currentOperation;
   private AliasManager aliasManager;
   private Logger logger = LoggerUtil.getLogger(getClass().getName());
   
   public Console() throws IOException {
     this(Settings.getInstance());
   }
   
   public Console(Settings settings) throws IOException {
     reset(settings);
     
     Runtime.getRuntime().addShutdownHook(new Thread() {
       public void start() {
         try {
           if (Settings.getInstance().isAliasEnabled())
             Console.this.aliasManager.persist();
           Settings.getInstance().getTerminal().reset();
           Settings.getInstance().quit();
         }
         catch (Exception e) {
           e.printStackTrace();
         }
       }
     });
   }
   
 
 
 
 
 
   public void reset(Settings settings)
     throws IOException
   {
     if (this.running)
       throw new RuntimeException("Cant reset an already running Console, must stop if first!");
     if (Settings.getInstance().doReadInputrc()) {
       Config.parseInputrc(Settings.getInstance());
     }
     
 
     Config.readRuntimeProperties(Settings.getInstance());
     
     setTerminal(settings.getTerminal(), settings
       .getInputStream(), settings.getStdOut(), settings.getStdErr());
     
     this.editMode = settings.getFullEditMode();
     
     this.undoManager = new UndoManager();
     this.pasteManager = new PasteManager();
     this.buffer = new Buffer(null, null);
     if (this.history == null) {
       if (settings.isHistoryPersistent())
       {
         this.history = new FileHistory(settings.getHistoryFile().getAbsolutePath(), settings.getHistorySize());
       } else {
         this.history = new InMemoryHistory(settings.getHistorySize());
       }
     }
     
 
     this.completionList = new ArrayList();
     
     this.completionList.add(new RedirectionCompletion());
     
 
     if (Settings.getInstance().isAliasEnabled()) {
       this.aliasManager = new AliasManager(Settings.getInstance().getAliasFile());
       this.completionList.add(new AliasCompletion(this.aliasManager));
     }
     
     this.operations = new ArrayList();
     this.currentOperation = null;
     
     this.redirectPipeOutBuffer = new StringBuilder();
     this.redirectPipeErrBuffer = new StringBuilder();
     
     this.settings = settings;
     this.running = true;
   }
   
   private void setTerminal(Terminal term, InputStream in, OutputStream stdOut, OutputStream stdErr) {
     this.terminal = term;
     this.terminal.init(in, stdOut, stdErr);
   }
   
 
 
 
 
   public int getTerminalHeight()
   {
     return this.terminal.getHeight();
   }
   
 
 
 
 
   public int getTerminalWidth()
   {
     return this.terminal.getWidth();
   }
   
 
 
 
 
   public History getHistory()
   {
     return this.history;
   }
   
 
 
 
 
 
   public void pushToStdOut(String input)
     throws IOException
   {
     if ((input != null) && (input.length() > 0))
     {
       if ((this.currentOperation != null) && 
         (ControlOperator.isRedirectionOut(this.currentOperation.getControlOperator()))) {
         this.redirectPipeOutBuffer.append(input);
       } else {
         this.terminal.writeToStdOut(input);
       }
     }
   }
   
 
 
 
   public void pushToStdOut(char[] input)
     throws IOException
   {
     if ((input != null) && (input.length > 0))
     {
       if ((this.currentOperation != null) && 
         (ControlOperator.isRedirectionOut(this.currentOperation.getControlOperator()))) {
         this.redirectPipeOutBuffer.append(input);
       } else
         this.terminal.writeToStdOut(input);
     }
   }
   
   public void pushToStdErr(String input) throws IOException {
     if ((input != null) && (input.length() > 0)) {
       if ((this.currentOperation != null) && 
         (ControlOperator.isRedirectionErr(this.currentOperation.getControlOperator()))) {
         this.redirectPipeErrBuffer.append(input);
       } else
         this.terminal.writeToStdErr(input);
     }
   }
   
   public void pushToStdErr(char[] input) throws IOException {
     if ((input != null) && (input.length > 0)) {
       if ((this.currentOperation != null) && 
         (ControlOperator.isRedirectionErr(this.currentOperation.getControlOperator()))) {
         this.redirectPipeErrBuffer.append(input);
       } else {
         this.terminal.writeToStdErr(input);
       }
     }
   }
   
 
 
 
   public void addCompletion(Completion completion)
   {
     this.completionList.add(completion);
   }
   
 
 
 
 
   public void addCompletions(List<Completion> completionList)
   {
     this.completionList.addAll(completionList);
   }
   
 
 
 
 
   public void stop()
     throws IOException
   {
     this.settings.getInputStream().close();
     
     this.settings.setInputStream(null);
     this.terminal.reset();
     this.terminal = null;
     this.running = false;
   }
   
 
 
 
 
 
   public void restart()
     throws IOException
   {
     if (this.running) {
       stop();
     }
     
     reset(Settings.getInstance());
   }
   
 
 
 
 
   protected void attachProcess(ConsoleCommand cc)
     throws IOException
   {
     this.command = cc;
   }
   
 
 
 
   private void detachProcess()
     throws IOException
   {
     this.command = null;
     this.terminal.writeToStdOut(this.buffer.getPrompt());
   }
   
 
 
 
 
 
 
 
 
   public ConsoleOutput read(String prompt, String promptWithoutColors)
     throws IOException
   {
     return read(prompt, promptWithoutColors, null);
   }
   
 
 
 
 
 
 
 
 
   public ConsoleOutput read(String prompt, String promptWithoutColors, Character mask)
     throws IOException
   {
     if (!this.running) {
       throw new RuntimeException("Cant reuse a stopped Console before its reset again!");
     }
     if (this.currentOperation != null) {
       ConsoleOutput output = parseCurrentOperation();
       if (output != null) {
         return output;
       }
     }
     this.buffer.reset(prompt, promptWithoutColors, mask);
     if (this.command == null)
       this.terminal.writeToStdOut(this.buffer.getPrompt());
     this.search = null;
     for (;;)
     {
       if ((this.command != null) && (!this.command.isAttached())) {
         detachProcess();
       }
       
       int[] in = this.terminal.read(this.settings.isReadAhead());
       if (in[0] == -1) {
         return null;
       }
       Operation operation = this.editMode.parseInput(in);
       operation.setInput(in);
       
       String result = null;
       if (this.command != null) {
         this.command.processOperation(operation);
       } else {
         result = parseOperation(operation, mask);
       }
       if (result != null) {
         this.inEscapeMode = false;
         this.operations = ControlOperatorParser.findAllControlOperators(result);
         ConsoleOutput output = parseOperations();
         output = processInternalCommands(output);
         if (output.getBuffer() != null) {
           return output;
         }
         
         this.buffer.reset(prompt, promptWithoutColors, mask);
         this.terminal.writeToStdOut(this.buffer.getPrompt());
         this.search = null;
       }
     }
   }
   
   public boolean askForConfirmation(String message)
   {
     try {
       this.terminal.writeToStdOut(message);
       int[] chars = this.terminal.read(true);
       if ((chars.length > 0) && (('y' == (char)chars[0]) || ('Y' == (char)chars[0]))) {
         this.terminal.writeToStdOut(Config.getLineSeparator());
         return true;
       }
     } catch (Throwable th) {
       th.printStackTrace();
     }
     
     return false;
   }
   
   public Terminal getTerminal() {
     return this.terminal;
   }
   
 
 
 
 
 
 
 
   private String parseOperation(Operation operation, Character mask)
     throws IOException
   {
     Action action = operation.getAction();
     
     if (this.askDisplayCompletion) {
       this.askDisplayCompletion = false;
       if ('y' == (char)operation.getInput()[0]) {
         this.displayCompletion = true;
         complete();
 
       }
       else
       {
         this.terminal.writeToStdOut(Config.getLineSeparator());
         this.terminal.writeToStdOut(this.buffer.getLineWithPrompt());
         syncCursor();
       }
     }
     else if (action == Action.EDIT) {
       writeChars(operation.getInput(), mask);
       
       if (this.buffer.getLine().endsWith("<%")) {
         this.inEscapeMode = true;
         return null; }
       if (this.buffer.getLine().endsWith("%>")) {
         this.inEscapeMode = false;
       }
       
 
     }
     else if ((action == Action.SEARCH) && (!this.settings.isHistoryDisabled())) {
       if (this.search == null) {
         this.search = new Search(operation, operation.getInput()[0]);
       } else {
         this.search.setOperation(operation);
         this.search.setInput(operation.getInput()[0]);
       }
       doSearch(this.search);
       if (this.search.isFinished()) {
         return this.search.getResult();
       }
     } else if ((action == Action.MOVE) || (action == Action.DELETE) || (action == Action.CHANGE) || (action == Action.YANK))
     {
       performAction(EditActionManager.parseAction(operation, this.buffer.getCursor(), this.buffer.length()));
     }
     else if (action != Action.ABORT)
     {
 
       if (action == Action.CASE) {
         addActionToUndoStack();
         changeCase();
       }
       else if (action == Action.COMPLETE) {
         complete();
       }
       else if (action != Action.EXIT)
       {
 
         if (action == Action.HISTORY) {
           if (operation.getMovement() == Movement.NEXT) {
             getHistoryElement(true);
           } else if (operation.getMovement() == Movement.PREV) {
             getHistoryElement(false);
           }
         } else if (action == Action.NEWLINE)
         {
           if (this.inEscapeMode) {
             this.terminal.writeToStdOut(Config.getLineSeparator());
             if (this.inRecordMode) {
               this.terminal.writeToStdOut("\033[31m[REC] \033[0m>");
             } else {
               this.terminal.writeToStdOut(">");
             }
             this.buffer.newLine(">", ">", mask);
             syncCursor();
           }
           else {
             clearUndoStack();
             if (mask == null)
               addToHistory(this.buffer.getCompleteText());
             this.prevAction = Action.NEWLINE;
             
             printNewline();
             return this.buffer.getCompleteText();
           }
           
         }
         else if (action == Action.UNDO) {
           undo();
         }
         else if (action == Action.PASTE_FROM_CLIPBOARD) {
           addActionToUndoStack();
 
         }
         else if (action == Action.PASTE) {
           if (operation.getMovement() == Movement.NEXT) {
             doPaste(0, true);
           } else {
             doPaste(0, false);
           }
         } else if (action == Action.CHANGE_EDITMODE) {
           changeEditMode(operation.getMovement());
         }
         else if (action == Action.CLEAR) {
           clear(true);
         }
         else if (action == Action.REPLACE) {
           replace(operation.getInput()[0]);
         }
         else if (action != Action.NO_ACTION) {}
       }
     }
     
 
     if ((action == Action.HISTORY) && (!this.settings.isHistoryDisabled())) {
       this.prevAction = action;
     }
     return null;
   }
   
 
 
 
 
 
 
   private void doSearch(Search search)
     throws IOException
   {
     switch (search.getOperation().getMovement())
     {
     case PREV: 
       this.history.setSearchDirection(SearchDirection.REVERSE);
       search.setSearchTerm(new StringBuilder(this.buffer.getLine()));
       if (search.getSearchTerm().length() > 0) {
         search.setResult(this.history.search(search.getSearchTerm().toString()));
       }
       
       break;
     case NEXT: 
       this.history.setSearchDirection(SearchDirection.FORWARD);
       search.setSearchTerm(new StringBuilder(this.buffer.getLine()));
       if (search.getSearchTerm().length() > 0) {
         search.setResult(this.history.search(search.getSearchTerm().toString()));
       }
       
       break;
     case PREV_WORD: 
       this.history.setSearchDirection(SearchDirection.REVERSE);
       if (search.getSearchTerm().length() > 0) {
         search.setResult(this.history.search(search.getSearchTerm().toString()));
       }
       break;
     case NEXT_WORD: 
       this.history.setSearchDirection(SearchDirection.FORWARD);
       if (search.getSearchTerm().length() > 0) {
         search.setResult(this.history.search(search.getSearchTerm().toString()));
       }
       break;
     case PREV_BIG_WORD: 
       if (search.getSearchTerm().length() > 0) {
         search.getSearchTerm().deleteCharAt(search.getSearchTerm().length() - 1);
       }
       break;
     case ALL: 
       search.getSearchTerm().appendCodePoint(search.getInput());
       
       String tmpResult = this.history.search(search.getSearchTerm().toString());
       if (tmpResult == null) {
         search.getSearchTerm().deleteCharAt(search.getSearchTerm().length() - 1);
       }
       else {
         search.setResult(tmpResult);
       }
       break;
     
 
     case END: 
       if (search.getResult() != null) {
         moveCursor(-this.buffer.getCursor());
         setBufferLine(search.getResult());
         redrawLine();
         printNewline();
         search.setResult(this.buffer.getLineNoMask());
         search.setFinished(true);
         return;
       }
       
       moveCursor(-this.buffer.getCursor());
       setBufferLine("");
       redrawLine();
       
       break;
     
 
     case NEXT_BIG_WORD: 
       if (search.getResult() != null) {
         moveCursor(-this.buffer.getCursor());
         setBufferLine(search.getResult());
         search.setResult(null);
       }
       else {
         moveCursor(-this.buffer.getCursor());
         setBufferLine("");
       }
       
       break;
     }
     
     if (this.editMode.getCurrentAction() == Action.SEARCH) {
       if (search.getSearchTerm().length() == 0) {
         if (search.getResult() != null) {
           printSearch("", search.getResult());
         } else {
           printSearch("", "");
         }
       }
       else if (search.getResult() != null)
       {
 
 
         printSearch(search.getSearchTerm().toString(), search
           .getResult());
       }
       
     }
     else
     {
       redrawLine();
       this.terminal.writeToStdOut(Buffer.printAnsi(this.buffer.getPromptLength() + 1 + "G"));
     }
   }
   
 
 
 
 
 
   private void changeEditMode(Movement movement)
   {
     if ((this.editMode.getMode() == Mode.EMACS) && (movement == Movement.PREV)) {
       Settings.getInstance().setEditMode(Mode.VI);
       Settings.getInstance().resetEditMode();
     }
     else if ((this.editMode.getMode() == Mode.VI) && (movement == Movement.NEXT)) {
       Settings.getInstance().setEditMode(Mode.EMACS);
       Settings.getInstance().resetEditMode();
     }
     this.editMode = Settings.getInstance().getFullEditMode();
   }
   
   private void getHistoryElement(boolean first) throws IOException {
     if (this.settings.isHistoryDisabled()) {
       return;
     }
     if (this.prevAction == Action.NEWLINE) {
       this.history.setCurrent(this.buffer.getLine());
     }
     String fromHistory;
     String fromHistory;
     if (first) {
       fromHistory = this.history.getNextFetch();
     }
     else {
       fromHistory = this.history.getPreviousFetch();
     }
     if (fromHistory != null) {
       setBufferLine(fromHistory);
       moveCursor(-this.buffer.getCursor() + this.buffer.length());
       redrawLine();
     }
     this.prevAction = Action.HISTORY;
   }
   
   private void setBufferLine(String newLine)
     throws IOException
   {
     if ((newLine.length() + this.buffer.getPromptLength() >= getTerminalWidth()) && 
       (newLine.length() >= this.buffer.getLine().length())) {
       int currentRow = getCurrentRow();
       if (currentRow > -1) {
         int cursorRow = this.buffer.getCursorWithPrompt() / getTerminalWidth();
         if (currentRow + newLine.length() / getTerminalWidth() - cursorRow >= getTerminalHeight()) {
           int numNewRows = currentRow + (newLine.length() + this.buffer.getPromptLength()) / getTerminalWidth() - cursorRow - getTerminalHeight();
           
           if ((newLine.length() + this.buffer.getPromptLength()) % getTerminalWidth() == 0)
             numNewRows++;
           if (numNewRows > 0)
           {
 
 
             this.terminal.writeToStdOut(Buffer.printAnsi(numNewRows + "S"));
             this.terminal.writeToStdOut(Buffer.printAnsi(numNewRows + "A"));
           }
         }
       }
     } else if (newLine.length() < this.buffer.getLine().length()) {
       StringBuffer sb = new StringBuffer();
       for (int i = 0; i < this.buffer.getLine().length(); i++) {
         sb.append(" ");
       }
       this.buffer.setLine(sb.toString());
       redrawLine();
     }
     this.buffer.setLine(newLine);
   }
   
   private void insertBufferLine(String insert, int position) throws IOException {
     if (insert.length() + this.buffer.totalLength() >= getTerminalWidth())
     {
       int currentRow = getCurrentRow();
       if (currentRow > -1) {
         int newLine = insert.length() + this.buffer.totalLength();
         int cursorRow = this.buffer.getCursorWithPrompt() / getTerminalWidth();
         if (currentRow + newLine / getTerminalWidth() - cursorRow >= getTerminalHeight()) {
           int numNewRows = currentRow + newLine / getTerminalWidth() - cursorRow - getTerminalHeight();
           
           if ((insert.length() + this.buffer.totalLength()) % getTerminalWidth() == 0)
             numNewRows++;
           if (numNewRows > 0) {
             this.terminal.writeToStdOut(Buffer.printAnsi(numNewRows + "S"));
             this.terminal.writeToStdOut(Buffer.printAnsi(numNewRows + "A"));
           }
         }
       }
     }
     this.buffer.insert(position, insert);
   }
   
   private void addToHistory(String line) {
     if (!this.settings.isHistoryDisabled()) {
       this.history.push(line);
     }
     if ((this.inRecordMode) && (!line.isEmpty())) {
       this.recordedCommands.add(line);
     }
   }
   
   private void writeChars(int[] chars, Character mask) throws IOException {
     for (int c : chars) {
       writeChar(c, mask);
     }
   }
   
   private void writeChar(int c, Character mask) throws IOException {
     this.buffer.write((char)c);
     if (mask != null) {
       if (mask.charValue() == 0) {
         this.terminal.writeToStdOut(' ');
       } else {
         this.terminal.writeToStdOut(mask.charValue());
       }
     } else {
       this.terminal.writeToStdOut((char)c);
     }
     
 
     if ((!Main.isWindowsTerminal()) && (this.buffer.getCursorWithPrompt() > getTerminalWidth()) && 
       (this.buffer.getCursorWithPrompt() % getTerminalWidth() == 1)) {
       this.terminal.writeToStdOut(Config.getLineSeparator());
     }
     
 
     if (this.buffer.getCursor() < this.buffer.length())
     {
       if ((this.buffer.totalLength() > getTerminalWidth()) && 
         ((this.buffer.totalLength() - 1) % getTerminalWidth() == 1)) {
         int ansiCurrentRow = getCurrentRow();
         int currentRow = this.buffer.getCursorWithPrompt() / getTerminalWidth();
         if ((currentRow > 0) && (this.buffer.getCursorWithPrompt() % getTerminalWidth() == 0)) {
           currentRow--;
         }
         int totalRows = this.buffer.totalLength() / getTerminalWidth();
         if ((totalRows > 0) && (this.buffer.totalLength() % getTerminalWidth() == 0)) {
           totalRows--;
         }
         if (ansiCurrentRow + (totalRows - currentRow) > getTerminalHeight()) {
           this.terminal.writeToStdOut(Buffer.printAnsi("1S"));
           this.terminal.writeToStdOut(Buffer.printAnsi("1A"));
         }
       }
       redrawLine();
     }
   }
   
 
 
 
 
 
   private boolean performAction(EditAction action)
     throws IOException
   {
     action.doAction(this.buffer.getLine());
     if (action.getAction() == Action.MOVE) {
       moveCursor(action.getEnd() - action.getStart());
       return true;
     }
     if ((action.getAction() == Action.DELETE) || (action.getAction() == Action.CHANGE))
     {
       addActionToUndoStack();
       
       if (action.getEnd() > action.getStart())
       {
         if (action.getStart() != this.buffer.getCursor()) {
           moveCursor(action.getStart() - this.buffer.getCursor());
         }
         addToPaste(this.buffer.getLine().substring(action.getStart(), action.getEnd()));
         this.buffer.delete(action.getStart(), action.getEnd());
       }
       else {
         addToPaste(this.buffer.getLine().substring(action.getEnd(), action.getStart()));
         this.buffer.delete(action.getEnd(), action.getStart());
         moveCursor(action.getEnd() - action.getStart());
       }
       
       if ((this.editMode.getMode() == Mode.VI) && (this.buffer.getCursor() == this.buffer.length()) && 
         (!((ViEditMode)this.editMode).isInEditMode())) {
         moveCursor(-1);
       }
       redrawLine();
     }
     else if (action.getAction() == Action.YANK) {
       if (action.getEnd() > action.getStart()) {
         addToPaste(this.buffer.getLine().substring(action.getStart(), action.getEnd()));
       }
       else {
         addToPaste(this.buffer.getLine().substring(action.getEnd(), action.getStart()));
       }
     }
     
     return true;
   }
   
 
 
 
   private void addActionToUndoStack()
     throws IOException
   {
     UndoAction ua = new UndoAction(this.buffer.getCursor(), this.buffer.getLine());
     this.undoManager.addUndo(ua);
   }
   
   private void clearUndoStack()
   {
     this.undoManager.clear();
   }
   
   private void addToPaste(String buffer) {
     this.pasteManager.addText(new StringBuilder(buffer));
   }
   
 
 
 
 
 
 
   private boolean doPaste(int index, boolean before)
     throws IOException
   {
     StringBuilder pasteBuffer = this.pasteManager.get(index);
     if (pasteBuffer == null) {
       return false;
     }
     addActionToUndoStack();
     if ((before) || (this.buffer.getCursor() >= this.buffer.getLine().length())) {
       insertBufferLine(pasteBuffer.toString(), this.buffer.getCursor());
       redrawLine();
     }
     else {
       insertBufferLine(pasteBuffer.toString(), this.buffer.getCursor() + 1);
       redrawLine();
       
       moveCursor(1);
     }
     return true;
   }
   
   public final void moveCursor(int where) throws IOException {
     if ((this.editMode.getMode() == Mode.VI) && (
       (this.editMode.getCurrentAction() == Action.MOVE) || 
       (this.editMode.getCurrentAction() == Action.DELETE)))
     {
       this.terminal.writeToStdOut(this.buffer.move(where, getTerminalWidth(), true));
     }
     else {
       this.terminal.writeToStdOut(this.buffer.move(where, getTerminalWidth()));
     }
   }
   
   private void redrawLine() throws IOException {
     drawLine(this.buffer.getPrompt() + this.buffer.getLine(), this.buffer.totalLength());
   }
   
   private void drawLine(String line, int lineRealLength) throws IOException
   {
     if ((lineRealLength > getTerminalWidth()) || 
       (lineRealLength + Math.abs(this.buffer.getDelta()) > getTerminalWidth()))
     {
       int maxRow = lineRealLength / getTerminalWidth();
       
       int cursorRow = 0;
       if (this.buffer.getCursorWithPrompt() > 0)
         cursorRow = this.buffer.getCursorWithPrompt() / getTerminalWidth();
       if ((cursorRow > 0) && (this.buffer.getCursorWithPrompt() % getTerminalWidth() == 0)) {
         cursorRow--;
       }
       
       for (int row = 0; row <= maxRow; row++)
       {
         this.terminal.writeToStdOut(Buffer.printAnsi("s"));
         
         if (row < cursorRow) {
           for (int i = row; i < cursorRow; i++)
             this.terminal.writeToStdOut(Buffer.printAnsi("A"));
         }
         if (row > cursorRow) {
           for (int i = cursorRow; i < row; i++)
             this.terminal.writeToStdOut(Buffer.printAnsi("B"));
         }
         this.terminal.writeToStdOut(Buffer.printAnsi("0G"));
         
         int startIndex = row * getTerminalWidth();
         if ((row > 0) && (lineRealLength == getTerminalWidth())) {
           startIndex = lineRealLength - 1;
         } else if (row > 0) {
           startIndex += line.length() - lineRealLength + 1;
         }
         int endIndex = Math.min((row + 1) * getTerminalWidth() + 1, lineRealLength) + (line.length() - lineRealLength);
         
         String rowText = line.substring(startIndex, endIndex);
         this.terminal.writeToStdOut(rowText);
         
 
 
 
         if ((row > 0) && (rowText.length() < getTerminalWidth())) {
           StringBuilder sb = new StringBuilder();
           
           int toOverwrite = rowText.length() - getTerminalWidth();
           if (Main.isWindowsTerminal()) {
             toOverwrite++;
           }
           
           for (int i = 0; i > toOverwrite; i--)
             sb.append(' ');
           this.terminal.writeToStdOut(sb.toString());
         }
         
 
 
 
 
 
 
 
         this.terminal.writeToStdOut(Buffer.printAnsi("u"));
       }
       
     }
     else
     {
       this.terminal.writeToStdOut(Buffer.printAnsi("s"));
       
       this.terminal.writeToStdOut(Buffer.printAnsi("0G"));
       this.terminal.writeToStdOut(Buffer.printAnsi("2K"));
       
       this.terminal.writeToStdOut(line);
       
 
       this.terminal.writeToStdOut(Buffer.printAnsi("u"));
     }
   }
   
   private void printSearch(String searchTerm, String result) throws IOException
   {
     int cursor = result.indexOf(searchTerm);
     StringBuilder out;
     StringBuilder out;
     if (this.history.getSearchDirection() == SearchDirection.REVERSE) {
       out = new StringBuilder("(reverse-i-search) `");
     } else
       out = new StringBuilder("(forward-i-search) `");
     out.append(searchTerm).append("': ");
     cursor += out.length();
     out.append(result);
     this.buffer.disablePrompt(true);
     moveCursor(-this.buffer.getCursor());
     this.terminal.writeToStdOut(ANSI.moveCursorToBeginningOfLine());
     setBufferLine(out.toString());
     moveCursor(cursor);
     drawLine(this.buffer.getLine(), this.buffer.length());
     this.buffer.disablePrompt(false);
   }
   
 
 
 
   private void printNewline()
     throws IOException
   {
     moveCursor(this.buffer.totalLength());
     this.terminal.writeToStdOut(Config.getLineSeparator());
   }
   
 
 
 
   private void changeCase()
     throws IOException
   {
     if (this.buffer.changeCase()) {
       moveCursor(1);
       redrawLine();
     }
   }
   
 
 
 
   private void undo()
     throws IOException
   {
     UndoAction ua = this.undoManager.getNext();
     if (ua != null) {
       setBufferLine(ua.getBuffer());
       redrawLine();
       moveCursor(ua.getCursorPosition() - this.buffer.getCursor());
     }
   }
   
 
 
 
 
 
 
 
   private void complete()
     throws IOException
   {
     if (this.completionList.size() < 1) {
       return;
     }
     List<CompleteOperation> possibleCompletions = new ArrayList();
     int pipeLinePos = 0;
     if (ControlOperatorParser.doStringContainPipeline(this.buffer.getLine())) {
       pipeLinePos = ControlOperatorParser.findLastPipelinePositionBeforeCursor(this.buffer.getLine(), this.buffer.getCursor());
       if (ControlOperatorParser.findLastRedirectionPositionBeforeCursor(this.buffer.getLine(), this.buffer.getCursor()) > pipeLinePos) {
         pipeLinePos = 0;
       }
     }
     boolean alreadyAskedForDisplayAll = false;
     boolean notDisplayOptions = false;
     for (Completion completion : this.completionList) {
       CompleteOperation co;
       if (pipeLinePos > 0) {
         co = findAliases(this.buffer.getLine().substring(pipeLinePos, this.buffer.getCursor()), this.buffer.getCursor() - pipeLinePos);
       }
       else {
         co = findAliases(this.buffer.getLine(), this.buffer.getCursor());
       }
       completion.complete(co, pipeLinePos > 0);
       
 
       if (!alreadyAskedForDisplayAll) {
         alreadyAskedForDisplayAll = !completion.hasToAskForConfirmation();
       }
       
       if (!notDisplayOptions) {
         notDisplayOptions = !completion.getAskForConfirmationResponse();
       }
       if ((co.getCompletionCandidates() != null) && (co.getCompletionCandidates().size() > 0)) {
         possibleCompletions.add(co);
       }
     }
     
     CompleteOperation co;
     if (notDisplayOptions) {
       noDisplayCompletions();
 
     }
     else if (possibleCompletions.size() >= 1)
     {
 
 
       if ((possibleCompletions.size() == 1) && 
         (((CompleteOperation)possibleCompletions.get(0)).getCompletionCandidates().size() == 1))
       {
         displayCompletion((String)((CompleteOperation)possibleCompletions.get(0)).getCompletionCandidates().get(0), 
           (String)((CompleteOperation)possibleCompletions.get(0)).getFormattedCompletionCandidates().get(0), true);
 
       }
       else
       {
         String startsWith = Parser.findStartsWithOperation(possibleCompletions);
         
         if (startsWith.length() > 0) {
           displayCompletion("", startsWith, false);
         }
         else
         {
           List<String> completions = new ArrayList();
           for (CompleteOperation co : possibleCompletions) {
             completions.addAll(co.getCompletionCandidates());
           }
           if (completions.size() > 100) {
             if ((this.displayCompletion) || (alreadyAskedForDisplayAll)) {
               displayCompletions(completions);
               this.displayCompletion = false;
             }
             else if (!alreadyAskedForDisplayAll) {
               this.askDisplayCompletion = true;
               this.terminal.writeToStdOut(Config.getLineSeparator() + "Display all " + completions.size() + " possibilities? (y or n)");
             }
             
           }
           else {
             displayCompletions(completions);
           }
         }
       }
     }
   }
   
 
 
 
 
 
 
 
   private void displayCompletion(String fullCompletion, String completion, boolean appendSpace)
     throws IOException
   {
     if (completion.startsWith(this.buffer.getLine())) {
       performAction(new PrevWordAction(this.buffer.getCursor(), Action.DELETE));
       this.buffer.write(completion);
       this.terminal.writeToStdOut(completion);
 
     }
     else
     {
       this.buffer.write(completion);
       this.terminal.writeToStdOut(completion);
     }
     if ((appendSpace) && (fullCompletion.startsWith(this.buffer.getLine()))) {
       this.buffer.write(' ');
       this.terminal.writeToStdOut(' ');
     }
     
     redrawLine();
   }
   
 
 
 
 
 
   private void displayCompletions(List<String> completions)
     throws IOException
   {
     int oldCursorPos = this.buffer.getCursor();
     printNewline();
     this.buffer.setCursor(oldCursorPos);
     this.terminal.writeToStdOut(Parser.formatDisplayList(completions, this.terminal.getHeight(), this.terminal.getWidth()));
     this.terminal.writeToStdOut(this.buffer.getLineWithPrompt());
     
 
     syncCursor();
   }
   
   private void noDisplayCompletions() throws IOException
   {
     int oldCursorPos = this.buffer.getCursor();
     printNewline();
     this.buffer.setCursor(oldCursorPos);
     this.terminal.writeToStdOut(this.buffer.getLineWithPrompt());
     
 
     syncCursor();
   }
   
   private void syncCursor() throws IOException {
     if (this.buffer.getCursor() != this.buffer.getLine().length()) {
       this.terminal.writeToStdOut(Buffer.printAnsi(
         Math.abs(this.buffer.getCursor() - this.buffer
         .getLine().length()) + "D"));
     }
   }
   
   private void replace(int rChar) throws IOException
   {
     addActionToUndoStack();
     this.buffer.replaceChar((char)rChar);
     redrawLine();
   }
   
 
 
 
 
 
 
 
   private int getCurrentRow()
   {
     if ((this.settings.isAnsiConsole()) && (Config.isOSPOSIXCompatible())) {
       try {
         this.terminal.writeToStdOut(ANSI.getCurrentCursorPos());
         StringBuilder builder = new StringBuilder(8);
         int row;
         while (((row = this.terminal.read(false)[0]) > -1) && (row != 82)) {
           if ((row != 27) && (row != 91)) {
             builder.append((char)row);
           }
         }
         return Integer.parseInt(builder.substring(0, builder.indexOf(";")));
       }
       catch (Exception e) {
         if (this.settings.isLogging())
           this.logger.warning("Failed to find current row with ansi code: " + e.getMessage());
         return -1;
       }
     }
     return -1;
   }
   
   private int getCurrentColumn() {
     if ((this.settings.isAnsiConsole()) && (Config.isOSPOSIXCompatible())) {
       try {
         this.terminal.writeToStdOut(ANSI.getCurrentCursorPos());
         StringBuilder builder = new StringBuilder(8);
         int row;
         while (((row = this.settings.getInputStream().read()) > -1) && (row != 82)) {
           if ((row != 27) && (row != 91)) {
             builder.append((char)row);
           }
         }
         return Integer.parseInt(builder.substring(builder.lastIndexOf(";") + 1, builder.length()));
       }
       catch (Exception e) {
         if (this.settings.isLogging())
           this.logger.warning("Failed to find current column with ansi code: " + e.getMessage());
         return -1;
       }
     }
     return -1;
   }
   
 
 
 
   public void clear()
     throws IOException
   {
     clear(false);
   }
   
 
 
 
 
 
 
 
   public void clear(boolean includeBuffer)
     throws IOException
   {
     this.terminal.writeToStdOut(ANSI.clearScreen());
     
     this.terminal.writeToStdOut(Buffer.printAnsi("1;1H"));
     
     if (includeBuffer)
       this.terminal.writeToStdOut(this.buffer.getLineWithPrompt());
   }
   
   private ConsoleOutput parseCurrentOperation() throws IOException {
     if ((this.currentOperation.getControlOperator() == ControlOperator.OVERWRITE_OUT) || 
       (this.currentOperation.getControlOperator() == ControlOperator.OVERWRITE_ERR) || 
       (this.currentOperation.getControlOperator() == ControlOperator.APPEND_OUT) || 
       (this.currentOperation.getControlOperator() == ControlOperator.APPEND_ERR) || 
       (this.currentOperation.getControlOperator() == ControlOperator.OVERWRITE_OUT_AND_ERR))
     {
       ConsoleOperation nextOperation = (ConsoleOperation)this.operations.remove(0);
       persistRedirection(nextOperation.getBuffer(), this.currentOperation.getControlOperator());
       if (nextOperation.getControlOperator() == ControlOperator.NONE) {
         this.redirectPipeErrBuffer = new StringBuilder();
         this.redirectPipeOutBuffer = new StringBuilder();
         this.currentOperation = null;
         return null;
       }
       
       this.redirectPipeErrBuffer = new StringBuilder();
       this.redirectPipeOutBuffer = new StringBuilder();
       this.currentOperation = nextOperation;
       return parseCurrentOperation();
     }
     
     if ((this.currentOperation.getControlOperator() == ControlOperator.PIPE) || 
       (this.currentOperation.getControlOperator() == ControlOperator.PIPE_OUT_AND_ERR)) {
       return parseOperations();
     }
     
     if (this.currentOperation.getControlOperator() == ControlOperator.OVERWRITE_IN) {
       pushToStdErr(this.settings.getName() + ": syntax error while reading token: '<'");
       return null;
     }
     
 
 
     return null;
   }
   
 
 
 
 
 
   private ConsoleOutput parseOperations()
     throws IOException
   {
     ConsoleOutput output = null;
     ConsoleOperation op = (ConsoleOperation)this.operations.remove(0);
     
     if ((op.getControlOperator() == ControlOperator.OVERWRITE_OUT) || 
       (op.getControlOperator() == ControlOperator.OVERWRITE_ERR) || 
       (op.getControlOperator() == ControlOperator.APPEND_OUT) || 
       (op.getControlOperator() == ControlOperator.APPEND_ERR) || 
       (op.getControlOperator() == ControlOperator.OVERWRITE_OUT_AND_ERR) || 
       (op.getControlOperator() == ControlOperator.PIPE_OUT_AND_ERR) || 
       (op.getControlOperator() == ControlOperator.PIPE)) {
       if (this.operations.size() != 0)
       {
 
 
         this.currentOperation = op;
         
         output = new ConsoleOutput(op, this.redirectPipeOutBuffer.toString(), this.redirectPipeErrBuffer.toString());
       }
     }
     else if (op.getControlOperator() == ControlOperator.OVERWRITE_IN)
     {
 
 
       if (this.operations.size() > 0) {
         ConsoleOperation nextOperation = (ConsoleOperation)this.operations.remove(0);
         if (nextOperation.getBuffer().length() > 0) {
           List<String> files = Parser.findAllWords(nextOperation.getBuffer());
           this.currentOperation = new ConsoleOperation(nextOperation.getControlOperator(), op.getBuffer());
           
           try
           {
             output = new ConsoleOutput(new ConsoleOperation(nextOperation.getControlOperator(), op.getBuffer()), FileUtils.readFile(new File(Parser.switchEscapedSpacesToSpacesInWord((String)files.get(0)))), this.redirectPipeErrBuffer.toString());
           }
           catch (IOException ioe)
           {
             pushToStdErr(this.settings.getName() + ": " + ioe.getMessage() + Config.getLineSeparator());
             this.currentOperation = null;
             output = new ConsoleOutput(new ConsoleOperation(ControlOperator.NONE, ""));
           }
         }
         else {
           pushToStdErr(this.settings.getName() + ": syntax error near unexpected token '<'" + Config.getLineSeparator());
           this.currentOperation = null;
           output = new ConsoleOutput(new ConsoleOperation(ControlOperator.NONE, ""));
         }
       }
       else {
         pushToStdErr(this.settings.getName() + ": syntax error near unexpected token 'newline'" + Config.getLineSeparator());
         this.currentOperation = null;
         output = new ConsoleOutput(new ConsoleOperation(ControlOperator.NONE, ""));
       }
     }
     else {
       this.currentOperation = null;
       
       output = new ConsoleOutput(op, this.redirectPipeOutBuffer.toString(), this.redirectPipeErrBuffer.toString());
     }
     
     if (this.redirectPipeOutBuffer.length() > 0)
       this.redirectPipeOutBuffer = new StringBuilder();
     if (this.redirectPipeErrBuffer.length() > 0) {
       this.redirectPipeErrBuffer = new StringBuilder();
     }
     return findAliases(output);
   }
   
   private ConsoleOutput processInternalCommands(ConsoleOutput output) throws IOException {
     if (output.getBuffer() != null) {
       if ((this.settings.isAliasEnabled()) && 
         (output.getBuffer().startsWith(InternalCommands.ALIAS.getCommand()))) {
         String out = this.aliasManager.parseAlias(output.getBuffer());
         if (out != null) {
           pushToStdOut(out);
         }
         
         return new ConsoleOutput(new ConsoleOperation(ControlOperator.NONE, null));
       }
       if ((this.settings.isAliasEnabled()) && 
         (output.getBuffer().startsWith(InternalCommands.UNALIAS.getCommand()))) {
         String out = this.aliasManager.removeAlias(output.getBuffer());
         if (out != null) {
           pushToStdOut(out);
         }
         return new ConsoleOutput(new ConsoleOperation(ControlOperator.NONE, null));
       }
     }
     return output;
   }
   
   private ConsoleOutput findAliases(ConsoleOutput operation) {
     if (this.settings.isAliasEnabled()) {
       String command = Parser.findFirstWord(operation.getBuffer());
       Alias alias = this.aliasManager.getAlias(command);
       
       if (alias != null) {
         operation.setConsoleOperation(new ConsoleOperation(operation.getControlOperator(), alias
           .getValue() + operation.getBuffer().substring(command.length())));
       }
     }
     return operation;
   }
   
   private CompleteOperation findAliases(String buffer, int cursor) {
     if (this.settings.isAliasEnabled()) {
       String command = Parser.findFirstWord(buffer);
       Alias alias = this.aliasManager.getAlias(command);
       if (alias != null)
       {
         return new CompleteOperation(alias.getValue() + buffer.substring(command.length()), cursor + (alias.getValue().length() - command.length()));
       }
     }
     
     return new CompleteOperation(buffer, cursor);
   }
   
   private void persistRedirection(String fileName, ControlOperator redirection) throws IOException {
     List<String> fileNames = Parser.findAllWords(fileName);
     if (fileNames.size() > 1) {
       pushToStdErr(this.settings.getName() + ": can't redirect to more than one file." + Config.getLineSeparator());
       return;
     }
     
 
     fileName = (String)fileNames.get(0);
     try
     {
       if (redirection == ControlOperator.OVERWRITE_OUT) {
         FileUtils.saveFile(new File(Parser.switchEscapedSpacesToSpacesInWord(fileName)), this.redirectPipeOutBuffer.toString(), false);
       } else if (redirection == ControlOperator.OVERWRITE_ERR) {
         FileUtils.saveFile(new File(Parser.switchEscapedSpacesToSpacesInWord(fileName)), this.redirectPipeErrBuffer.toString(), false);
       } else if (redirection == ControlOperator.APPEND_OUT) {
         FileUtils.saveFile(new File(Parser.switchEscapedSpacesToSpacesInWord(fileName)), this.redirectPipeOutBuffer.toString(), true);
       } else if (redirection == ControlOperator.APPEND_ERR) {
         FileUtils.saveFile(new File(Parser.switchEscapedSpacesToSpacesInWord(fileName)), this.redirectPipeErrBuffer.toString(), true);
       }
     } catch (IOException e) {
       pushToStdErr(e.getMessage());
     }
     this.redirectPipeOutBuffer = new StringBuilder();
     this.redirectPipeErrBuffer = new StringBuilder();
   }
   
   public Settings getSettings() {
     return this.settings;
   }
   
   public boolean isRecording() {
     return this.inRecordMode;
   }
   
   public String getRecordName() {
     return this.recordName;
   }
   
   public void startRecordMode(String name) {
     this.recordName = name;
     this.inRecordMode = true;
     this.recordedCommands = new ArrayList();
     this.buffer.setRecordMode(true);
   }
   
   public List<String> stopRecordMode() {
     this.inRecordMode = false;
     this.buffer.setRecordMode(false);
     List<String> result = this.recordedCommands;
     this.recordedCommands = null;
     this.recordName = null;
     return result;
   }
 }


