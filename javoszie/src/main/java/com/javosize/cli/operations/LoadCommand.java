 package com.javosize.cli.operations;
 
 import com.javosize.cli.Command;
 import com.javosize.cli.CommandType;
 import com.javosize.cli.InvalidParamsException;
 import com.javosize.cli.State;
 import com.javosize.cli.StateHandler;
 import com.javosize.recipes.Repository;
 import java.io.IOException;
 import org.json.JSONException;
 
 public class LoadCommand
   extends Command
 {
   public LoadCommand(String[] args)
   {
     setArgs(args);
     setType(CommandType.create);
   }
   
   public String execute(StateHandler handler) throws InvalidParamsException
   {
     validateArgs(this.args, handler);
     if (handler.getStateHolder().equals(State.repository)) {
       return executeInRepository(handler);
     }
     return "Command not available at current entity.\nFor info about available commands type \"help\".\n";
   }
   
   private String executeInRepository(StateHandler handler)
   {
     String url = this.args[1];
     String result = null;
     try {
       result = Repository.importFromCloud(url);
     } catch (IOException e) {
       return "Error importing recipe: " + e.getMessage() + "\n";
     } catch (JSONException je) {
       return "Invalid recipe: " + je.getMessage() + "\n";
     }
     return result;
   }
   
   public boolean validArgs(String[] args, StateHandler handler)
   {
     return args.length == 2;
   }
 }


