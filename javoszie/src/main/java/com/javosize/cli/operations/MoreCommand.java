 package com.javosize.cli.operations;
 
 import com.javosize.cli.Command;
 import com.javosize.cli.CommandType;
 import com.javosize.cli.InvalidParamsException;
 import com.javosize.cli.Main;
 import com.javosize.cli.StateHandler;
 import com.javosize.log.Log;
 import java.io.IOException;
 import org.jboss.jreadline.terminal.Terminal;
 
 
 
 
 
 
 
 
 
 
 public class MoreCommand
   extends Command
 {
   private static Log log = new Log(MoreCommand.class.getName());
   
   private String textToPaginate = null;
   
   public MoreCommand(String[] args) {
     setArgs(args);
     setType(CommandType.more);
   }
   
   public MoreCommand(String textToPaginate) {
     this.textToPaginate = textToPaginate;
     setType(CommandType.more);
   }
   
   public String execute(StateHandler handler) throws InvalidParamsException
   {
     if ((this.textToPaginate == null) && (handler.getPreviousCommandResult() == null))
       throw new InvalidParamsException(getManText(handler));
     if (this.textToPaginate == null) {
       validateArgs(this.args, handler);
       this.textToPaginate = handler.getPreviousCommandResult();
     }
     
     String finalText = this.textToPaginate;
     try
     {
       Terminal terminal = Main.getTerminal();
       int terminalHeight = Main.getTerminalHeight();
       
 
       if ((terminal != null) && (!this.textToPaginate.isEmpty()) && (!this.textToPaginate.equals(""))) {
         String[] lines = this.textToPaginate.split("\\n");
         
         if (lines.length > terminalHeight) {
           int startLine = 0;
           int finalLine = terminalHeight - 2;
           boolean printLines = true;
           
           while (finalLine < lines.length - 1) {
             if (printLines) {
               printLines(terminal, lines, startLine, finalLine);
             }
             int[] chars = terminal.read(true);
             if ((chars.length > 0) && (('q' == (char)chars[0]) || ('q' == (char)chars[0]))) {
               finalText = "\n";
               break; }
             if ((chars.length > 0) && (' ' == (char)chars[0])) {
               startLine = finalLine + 1;
               if (finalLine + terminalHeight - 1 > lines.length) {
                 finalLine = lines.length - 1;
               } else {
                 finalLine = finalLine + terminalHeight - 1;
               }
               printLines = true;
             } else if ((chars.length > 0) && ('\n' == (char)chars[0])) {
               startLine = finalLine + 1;
               finalLine += 1;
               printLines = true;
             } else {
               printLines = false;
               terminal.writeToStdOut("\nInvalid option.\n - 'q' for quit\n - Space for one more page\n - Enter for one more line\n");
             }
           }
         }
       }
     } catch (Throwable th) {
       log.error("Error executing more command: " + th, th);
       return "Error executing more command " + th + "\n";
     }
     
     return finalText;
   }
   
   private void printLines(Terminal terminal, String[] text, int firstLine, int lastLine) throws IOException {
     for (int i = firstLine; (i < text.length) && (i <= lastLine); i++) {
       terminal.writeToStdOut(text[i] + "\n");
     }
   }
   
   protected boolean validArgs(String[] args, StateHandler handler)
   {
     return args.length == 1;
   }
 }


