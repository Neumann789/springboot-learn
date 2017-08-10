package com.javosize.cli.operations;

import com.javosize.actions.FullThreadDumpAction;
import com.javosize.cli.Command;
import com.javosize.cli.CommandType;
import com.javosize.cli.InvalidParamsException;
import com.javosize.cli.State;
import com.javosize.cli.StateHandler;
import com.javosize.log.Log;
import com.javosize.recipes.Recipe;
import com.javosize.recipes.Repository;
import com.javosize.remote.Controller;

public class DumpCommand extends Command {
	private static Log log = new Log(DumpCommand.class.getName());

	public DumpCommand(String[] args) {
		setArgs(args);
		setType(CommandType.dump);
	}

	public String execute(StateHandler handler) throws InvalidParamsException {
		validateArgs(this.args, handler);
		if (handler.getStateHolder().equals(State.threads))
			return executeInThreads(handler);
		if (handler.getStateHolder().equals(State.repository)) {
			return executeInRepository(handler);
		}
		return "Command not available at current entity.\nFor info about available commands type \"help\".\n";
	}

	private String executeInThreads(StateHandler handler) {
		FullThreadDumpAction th = new FullThreadDumpAction();
		return Controller.getInstance().execute(th);
	}

	private String executeInRepository(StateHandler handler) throws InvalidParamsException {
		if (this.args.length != 3) {
			throw new InvalidParamsException(getManText(handler));
		}
		boolean jsonType;
		if (this.args[2].toLowerCase().equals("json")) {
			jsonType = true;
		} else {
			if (this.args[2].toLowerCase().equals("xml")) {
				jsonType = false;
			} else {
				return "Invalid params. Usage: dump <recipe_name> [json|xml]\nDetail: Dump format " + this.args[2]
						+ " not supported. Available formats: xml or json.\n";
			}
		}
		String name = this.args[1];
		try {
			Recipe recipe = Repository.getRecipe(name);
			if (recipe == null) {
				return "Recipe " + name + " not found.\n";
			}

			if (jsonType) {
				return recipe.toJSON() + "\n";
			}
			return recipe.toXML();
		} catch (Throwable th) {
			log.error("Unexpected error executing dump for recipe " + name + ": " + th, th);

			FullThreadDumpAction fda = new FullThreadDumpAction();
			return Controller.getInstance().execute(fda);
		}
	}

	protected boolean validArgs(String[] args, StateHandler handler) {
		return args.length >= 1;
	}
}
