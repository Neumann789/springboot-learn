 package com.javosize.cli;
 
 import com.javosize.cli.operations.CatCommand;
 import com.javosize.cli.operations.CdCommand;
 import com.javosize.cli.operations.CreateCommand;
 import com.javosize.cli.operations.CutCommand;
 import com.javosize.cli.operations.DuCommand;
 import com.javosize.cli.operations.DumpCommand;
 import com.javosize.cli.operations.EchoCommand;
 import com.javosize.cli.operations.EditCommand;
 import com.javosize.cli.operations.EnterKeyCommand;
 import com.javosize.cli.operations.ExecCommand;
 import com.javosize.cli.operations.GrepCommand;
 import com.javosize.cli.operations.HelpCommand;
 import com.javosize.cli.operations.KillCommand;
 import com.javosize.cli.operations.LoadCommand;
 import com.javosize.cli.operations.LsCommand;
 import com.javosize.cli.operations.ManCommand;
 import com.javosize.cli.operations.MoreCommand;
 import com.javosize.cli.operations.MvCommand;
 import com.javosize.cli.operations.RmCommand;
 import com.javosize.cli.operations.SetCommand;
 import com.javosize.cli.operations.ViCommand;
 import java.util.ArrayList;
 
 public abstract class Command
 {
   protected CommandType type;
   protected String[] args;
   
   public abstract String execute(StateHandler paramStateHandler) throws InvalidParamsException;
   
   protected abstract boolean validArgs(String[] paramArrayOfString, StateHandler paramStateHandler);
   
   public void validateArgs(String[] args, StateHandler handler) throws InvalidParamsException
   {
     if (!validArgs(args, handler)) {
       throw new InvalidParamsException(getManText(handler));
     }
   }
   
   public CommandType getType() {
     return this.type;
   }
   
   public void setType(CommandType type) {
     this.type = type;
   }
   
   public String[] getArgs() {
     return this.args;
   }
   
   public void setArgs(String[] args) {
     ArrayList<String> arL = new ArrayList();
     
 
     for (int i = 0; i < args.length; i++) {
       if ((!" ".equals(args[i])) && (!"".equals(args[i]))) {
         arL.add(args[i]);
       }
     }
     
     String[] result = new String[arL.size()];
     result = (String[])arL.toArray(result);
     this.args = result;
   }
   
   public static Command createOperation(String operationCommand) {
     String[] args = operationCommand.trim().split(" ");
     
     if (operationCommand.trim().equals(""))
       return new EnterKeyCommand(args);
     if (CommandType.ls.toString().equals(args[0]))
       return new LsCommand(args);
     if (CommandType.cd.toString().equals(args[0]))
       return new CdCommand(args);
     if (CommandType.kill.toString().equals(args[0]))
       return new KillCommand(args);
     if (CommandType.mv.toString().equals(args[0]))
       return new MvCommand(args);
     if (CommandType.cat.toString().equals(args[0]))
       return new CatCommand(args);
     if (CommandType.man.toString().equals(args[0]))
       return new ManCommand(args);
     if (CommandType.exec.toString().equals(args[0]))
       return new ExecCommand(args);
     if (CommandType.set.toString().equals(args[0]))
       return new SetCommand(args);
     if (CommandType.dump.toString().equals(args[0]))
       return new DumpCommand(args);
     if (CommandType.create.toString().equals(args[0]))
       return new CreateCommand(args);
     if (CommandType.rm.toString().equals(args[0]))
       return new RmCommand(args);
     if (CommandType.load.toString().equals(args[0]))
       return new LoadCommand(args);
     if (CommandType.grep.toString().equals(args[0]))
       return new GrepCommand(args);
     if (CommandType.cut.toString().equals(args[0]))
       return new CutCommand(args);
     if (CommandType.next.toString().equals(args[0]))
       return new com.javosize.cli.operations.NextCommand(args);
     if (CommandType.vi.toString().equals(args[0]))
       return new ViCommand(args);
     if (CommandType.du.toString().equals(args[0]))
       return new DuCommand(args);
     if (CommandType.help.toString().equals(args[0]))
       return new HelpCommand(args);
     if (CommandType.more.toString().equals(args[0]))
       return new MoreCommand(args);
     if (CommandType.edit.toString().equals(args[0]))
       return new EditCommand(args);
     if (CommandType.echo.toString().equals(args[0])) {
       return new EchoCommand(args);
     }
     return new com.javosize.cli.operations.InvalidCommand(args);
   }
   
   public String getManText(StateHandler handler)
   {
     return this.type.getManPage();
   }
 }


