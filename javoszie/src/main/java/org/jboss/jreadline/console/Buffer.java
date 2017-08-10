 package org.jboss.jreadline.console;
 
 import com.javosize.cli.Main;
 import java.util.Arrays;
 import org.jboss.jreadline.console.settings.Settings;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Buffer
 {
   private int cursor = 0;
   private StringBuilder line;
   private StringBuilder completeText;
   private String prompt;
   private String promptWithoutColors;
   private int delta;
   private Character mask;
   private boolean disablePrompt = false;
   
   private boolean recordMode = false;
   private static final int TAB = 4;
   
   protected Buffer()
   {
     this(null, null);
   }
   
 
 
 
 
   protected Buffer(String promptText, String promptWithoutColors)
   {
     this(promptText, promptWithoutColors, null);
   }
   
   protected Buffer(String promptText, String promptWithoutColors, Character mask) {
     if (promptText != null) {
       this.prompt = promptText;
       this.promptWithoutColors = promptWithoutColors;
     } else {
       this.prompt = "";
       this.promptWithoutColors = "";
     }
     
     this.completeText = null;
     this.line = new StringBuilder();
     this.delta = 0;
     this.mask = mask;
   }
   
 
 
 
 
   protected void reset(String promptText, String promptWithoutColors)
   {
     reset(promptText, null);
   }
   
   protected void reset(String promptText, String promptWithoutColors, Character mask) {
     if (promptText != null) {
       this.prompt = promptText;
       this.promptWithoutColors = promptWithoutColors;
     } else {
       this.prompt = "";
       this.promptWithoutColors = "";
     }
     
     this.cursor = 0;
     this.completeText = null;
     this.line = new StringBuilder();
     this.delta = 0;
     this.mask = mask;
   }
   
   protected void newLine(String promptText, String promptWithoutColors, Character mask) {
     if (promptText != null) {
       this.prompt = promptText;
       this.promptWithoutColors = promptWithoutColors;
     } else {
       this.prompt = "";
       this.promptWithoutColors = "";
     }
     
     this.cursor = 0;
     if (this.completeText == null) {
       this.completeText = this.line;
     } else {
       this.completeText.append(this.line);
     }
     this.line = new StringBuilder();
     this.delta = 0;
     this.mask = mask;
   }
   
 
 
   protected int length()
   {
     return this.line.length();
   }
   
   protected int totalLength() {
     if (this.disablePrompt)
       return this.line.length() + 1;
     if (this.recordMode) {
       return this.line.length() + this.promptWithoutColors.length() + 1 + "[REC] ".length();
     }
     return this.line.length() + this.promptWithoutColors.length() + 1;
   }
   
   protected int getCursor() {
     return this.cursor;
   }
   
   protected int getCursorWithPrompt() {
     if (this.disablePrompt)
       return getCursor() + 1;
     if (this.recordMode) {
       return getCursor() + this.promptWithoutColors.length() + 1 + "[REC] ".length();
     }
     return getCursor() + this.promptWithoutColors.length() + 1;
   }
   
   protected String getPrompt() {
     if (this.recordMode) {
       return "\033[31m[REC] \033[0m" + this.prompt;
     }
     return this.prompt;
   }
   
   protected int getPromptLength() {
     if (this.recordMode) {
       return "[REC] ".length() + this.promptWithoutColors.length();
     }
     return this.promptWithoutColors.length();
   }
   
   protected void setCursor(int cursor) {
     this.cursor = cursor;
   }
   
 
 
 
 
   protected void disablePrompt(boolean disable)
   {
     this.disablePrompt = disable;
   }
   
 
 
 
 
 
 
 
 
   protected char[] move(int move, int termWidth)
   {
     return move(move, termWidth, false);
   }
   
   protected char[] move(int move, int termWidth, boolean viMode) {
     move = moveCursor(move, viMode);
     
     int currentRow = getCursorWithPrompt() / termWidth;
     if ((currentRow > 0) && (getCursorWithPrompt() % termWidth == 0)) {
       currentRow--;
     }
     int newRow = (move + getCursorWithPrompt()) / termWidth;
     if ((newRow > 0) && ((move + getCursorWithPrompt()) % termWidth == 0)) {
       newRow--;
     }
     int row = newRow - currentRow;
     
     setCursor(getCursor() + move);
     int cursor = getCursorWithPrompt() % termWidth;
     if ((cursor == 0) && (getCursorWithPrompt() > 0)) {
       cursor = termWidth;
     }
     if (row > 0) {
       StringBuilder sb = new StringBuilder();
       
       if (Main.isWindowsTerminal()) {
         for (int i = 0; i < row; i++) {
           sb.append(Config.getLineSeparator());
         }
       } else {
         sb.append(printAnsi(row + "B"));
       }
       
       sb.append(printAnsi(cursor + "G"));
       return sb.toString().toCharArray();
     }
     
     if (row < 0)
     {
       if (getCursor() <= termWidth) {}
       
 
       StringBuilder sb = new StringBuilder();
       sb.append(printAnsi(Math.abs(row) + "A")).append(printAnsi(cursor + "G"));
       return sb.toString().toCharArray();
     }
     
 
     if (move < 0) {
       return printAnsi(Math.abs(move) + "D");
     }
     if (move > 0) {
       return printAnsi(move + "C");
     }
     return new char[0];
   }
   
 
 
 
 
 
 
   public static char[] printAnsi(String out)
   {
     return printAnsi(out.toCharArray());
   }
   
 
 
 
 
 
   public static char[] printAnsi(char[] out)
   {
     if (!Settings.getInstance().isAnsiConsole()) {
       return new char[0];
     }
     int length = 0;
     for (c : out) {
       if (c == '\t') {
         length += 4;
       }
       else {
         length++;
       }
     }
     char[] ansi = new char[length + 2];
     ansi[0] = '\033';
     ansi[1] = '[';
     int counter = 0;
     char[] arrayOfChar2 = out;char c = arrayOfChar2.length; for (char c1 = '\000'; c1 < c; c1++) { char anOut = arrayOfChar2[c1];
       if (anOut == '\t') {
         Arrays.fill(ansi, counter + 2, counter + 2 + 4, ' ');
         counter += 3;
       } else {
         ansi[(counter + 2)] = anOut;
       }
       counter++;
     }
     
     return ansi;
   }
   
 
 
 
 
 
 
 
 
   private int moveCursor(int move, boolean viMode)
   {
     if ((getCursor() == 0) && (move <= 0)) {
       return 0;
     }
     if (viMode) {
       if ((getCursor() == this.line.length() - 1) && (move > 0)) {
         return 0;
       }
     }
     else if ((getCursor() == this.line.length()) && (move > 0)) {
       return 0;
     }
     
 
     if (getCursor() + move <= 0) {
       return -getCursor();
     }
     if (viMode) {
       if (getCursor() + move > this.line.length() - 1) {
         return this.line.length() - 1 - getCursor();
       }
     }
     else if (getCursor() + move > this.line.length()) {
       return this.line.length() - getCursor();
     }
     
     return move;
   }
   
 
 
 
 
 
   protected char[] getLineFrom(int position)
   {
     return this.line.substring(position).toCharArray();
   }
   
   public String getLine() {
     if (this.mask == null) {
       return this.line.toString();
     }
     if (this.line.length() > 0) {
       return String.format("%" + this.line.length() + "s", new Object[] { "" }).replace(' ', this.mask.charValue());
     }
     return "";
   }
   
   public String getCompleteText()
   {
     if (this.completeText != null) {
       return this.completeText.toString() + this.line.toString();
     }
     return this.line.toString();
   }
   
   public String getLineNoMask() {
     return this.line.toString();
   }
   
   protected void setLine(String line) {
     this.delta = (line.length() - this.line.length());
     this.line = new StringBuilder(line);
   }
   
   protected void delete(int start, int end) {
     this.delta = (start - end);
     this.line.delete(start, end);
   }
   
   protected void insert(int start, String in) {
     this.line.insert(start, in);
   }
   
 
 
 
 
   public String getLineWithPrompt()
   {
     if (this.recordMode) {
       return "\033[31m[REC] \033[0m" + this.prompt + this.line;
     }
     return this.prompt + this.line;
   }
   
 
 
 
 
   public void write(char c)
   {
     this.line.insert(this.cursor++, c);
     this.delta = 1;
   }
   
 
 
 
 
   public void write(String str)
   {
     assert (str != null);
     
     if (this.line.length() == 0) {
       this.line.append(str);
     }
     else {
       this.line.insert(getCursor(), str);
     }
     
     this.cursor += str.length();
     this.delta = str.length();
   }
   
   protected void clear() {
     this.line = new StringBuilder();
     this.delta = 0;
   }
   
   public int getDelta() {
     return this.delta;
   }
   
 
 
 
 
   protected boolean changeCase()
   {
     char c = getLine().charAt(getCursor());
     if (Character.isLetter(c)) {
       if (Character.isLowerCase(c)) {
         this.line.setCharAt(getCursor(), Character.toUpperCase(c));
       } else {
         this.line.setCharAt(getCursor(), Character.toLowerCase(c));
       }
       return true;
     }
     
     return false;
   }
   
   protected void replaceChar(char rChar) {
     this.line.setCharAt(getCursor(), rChar);
   }
   
   protected boolean containRedirection() {
     return this.line.indexOf(">") > -1;
   }
   
   protected int getRedirectionPosition() {
     return this.line.indexOf(">");
   }
   
   public void setRecordMode(boolean recordMode) {
     this.recordMode = recordMode;
   }
   
   public boolean getRecordMode() {
     return this.recordMode;
   }
 }


