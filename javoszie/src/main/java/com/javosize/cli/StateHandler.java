package com.javosize.cli;

import com.javosize.agent.Utils;
import com.javosize.cli.operations.LsCommand;
import com.javosize.print.Table;
import com.javosize.recipes.Recipe;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jboss.jreadline.complete.CompleteOperation;
import org.jboss.jreadline.complete.Completion;

public class StateHandler {
	private static final int MAXIMUM_ROWS = 100;
	private State stateHolder = State.root;
	private static final int MAXCAPACITY = 10;
	private LinkedList<String> results = new LinkedList();

	private String previousCommandResult = null;

	public void getCommandList(Completion completion, CompleteOperation co, String beginingOp, boolean afterPipe) {
		ArrayList<String> acList = new ArrayList();

		if (beginingOp.startsWith(CommandType.cd.toString() + " ")) {
			handleCdCommandCompleteOptions(beginingOp, acList);
		} else {
			for (CommandType candidateCommand : CommandType.values()) {
				boolean currentCandidateApplyAfterPipe = (afterPipe) && (candidateCommand.isAfterPipeCommand())
						&& (candidateCommand.isValidAsState(this.stateHolder))
						&& (candidateCommand.toString().startsWith(beginingOp))
						&& (candidateCommand != CommandType.invalid);
				if (currentCandidateApplyAfterPipe) {

					acList.add(candidateCommand.toString());
				} else if ((!afterPipe) && (!candidateCommand.isAfterPipeCommand())) {
					boolean incompleteCommandOrMan = (candidateCommand.isValidAsState(this.stateHolder))
							&& (candidateCommand != CommandType.invalid)
							&& ((candidateCommand.toString().startsWith(beginingOp))
									|| (beginingOp.startsWith(CommandType.man.toString() + " ")));
					boolean validCompleteCommand = (candidateCommand.isValidAsState(this.stateHolder))
							&& (beginingOp.startsWith(candidateCommand.toString() + " "));

					if (incompleteCommandOrMan) {
						acList.add(candidateCommand.toString());
					} else if (validCompleteCommand) {
						handleCompleteOptions(completion, beginingOp, acList, candidateCommand);
					}
				}
			}
		}

		co.setCompletionCandidates(acList);
	}

	private void handleCdCommandCompleteOptions(String beginingOp, ArrayList<String> acList) {
		beginingOp = beginingOp.toLowerCase();

		if (this.stateHolder.equals(State.root)) {
			for (State state : State.values()) {
				if ((state != State.root) && (("cd " + state.toString()).startsWith(beginingOp))) {
					acList.add("   " + state.toString());
				}

			}
		} else {
			for (State state : State.values()) {
				if (("cd ../" + state.toString()).startsWith(beginingOp)) {
					acList.add("   ../" + state.toString());
				}
			}
		}
	}

	private void handleCompleteOptions(Completion completion, String beginingOp, ArrayList<String> acList,
			CommandType candidateCommand) {
		try {
			LsCommand ls = getAvailableOptions(beginingOp, candidateCommand);
			int numResults = ls.executeCount(this);
			boolean displayResults = true;
			if (numResults > 100) {
				completion.setHasToAskForConfirmation(false);
				displayResults = Main.askForConfirmation(
						"\nFound more than 100 alternatives, do you want to list all? (Please, note that obtaining all the alternatives may take a while) [y/n]");
				completion.setAskForConfirmationResponse(displayResults);
			} else {
				completion.setHasToAskForConfirmation(true);
			}

			if (displayResults) {
				List<String> ids = getAvailableIDs(ls.execute(this), candidateCommand.toString(), beginingOp, true);
				acList.addAll(ids);
			}
		} catch (InvalidParamsException localInvalidParamsException) {
		}
	}

	private LsCommand getAvailableOptions(String beginingOp, CommandType ct) {
		String filter = beginingOp.substring(ct.toString().length() + 1);
		String[] lsArgs = { "ls" };
		if (filter.trim().length() > 0) {
			lsArgs = new String[] { "ls", filter.trim() + "*" };
		}
		LsCommand ls = new LsCommand(lsArgs, false);
		return ls;
	}

	public static List<String> getAvailableIDs(String result, String command, String beginingOp, boolean includePad) {
		List<String> ids = new ArrayList();
		boolean tableMode = false;

		if ((result == null) || (result.equals(""))) {
			return ids;
		}
		if (result.contains("===")) {
			tableMode = true;
		}

		String pad = "";
		if (includePad) {
			for (int i = 0; i <= command.length(); i++) {
				pad = pad + " ";
			}
		}

		int start = 0;
		String[] lines;
		if (!tableMode) {
			start = 0;
			lines = result.split("\n");
		} else {
			lines = Table.getFirstColumnValues(result);
		}

		for (int i = start; i < lines.length; i++) {
			lines[i] = lines[i].replace("\033[32m", "");
			lines[i] = lines[i].replace("\033[0m", "");

			String[] colums = lines[i].split(" ");
			if ((!colums[0].startsWith(" ")) && (!colums[0].startsWith("\t")) && (!colums[0].startsWith("\n"))
					&& (!colums[0].startsWith("\r")) && (!"".equals(colums[0].trim()))) {

				if ((command + " " + colums[0]).startsWith(beginingOp)) {
					ids.add(pad + lines[i]);
				}
			}
		}
		return ids;
	}

	public String executeRecipe(Recipe r, String params) {
		String[] splitedParams = null;
		if (params != null) {
			splitedParams = params.split(",");
		}

		if ((r.getNumberOfParameters() > 0) && (splitedParams.length != r.getNumberOfParameters())) {
			throw new RuntimeException("The number of required parameters (" + r.getNumberOfParameters()
					+ ") for receipe does not match provided params.");
		}

		State prevState = this.stateHolder;
		setStateHolder(State.root);

		StringBuffer sb = new StringBuffer();
		String code = r.getCode();
		code = replaceLineBreaksInsideEscapeCharacters(code);
		String[] commandLines = code.split("\n");
		for (String cl : commandLines) {
			try {
				String result = executeParsingConsoleControlOperators(replaceParams(cl, splitedParams), true);
				sb.append(result);
			} catch (Throwable th) {
				sb.append("Error: ");
				sb.append(th.toString());
				sb.append("\n");
				sb.append(Utils.stackTraceToString(th.getStackTrace(), 0));
				sb.append("\n");
			}
		}

		setStateHolder(prevState);

		return sb.toString();
	}

	private void addResult(String result) {
		if (this.results.size() >= 10) {
			this.results.removeLast();
		}
		if ((result == null) || ("".equals(result.trim()))) {
			return;
		}
		this.results.addFirst(result.trim());
	}

	private String replaceParams(String command, String[] params) {
		if ((params == null) || (params.length == 0)) {
			return command;
		}
		String result = command;
		for (int i = 0; i < params.length; i++) {
			result = command.replaceAll("\\{" + i + "\\}", params[i].trim());
			command = result;
		}
		return result;
	}

	public String executeParsingConsoleControlOperators(String commandline, boolean inRecipe) {
		Pattern controlOperatorPattern = Pattern.compile("(?!<%.*)([^%]>)(?!.*%>)|(?!<%.*)(\\|)(?!.*%>)");
		Matcher matcher = controlOperatorPattern.matcher(commandline);

		String result = null;

		while (matcher.find()) {
			if (matcher.group(1) != null) {
				String tmpResult = executeOperation(commandline.substring(0, matcher.start(1)), inRecipe);
				String fileName = commandline.substring(matcher.end(1));
				return writeResultToFile(tmpResult, fileName);
			}
			if (matcher.group(2) != null) {
				String command = commandline.substring(0, matcher.start(2));
				String tmpResult = executeOperation(command, inRecipe);
				setPreviousCommandResult(tmpResult);
				commandline = commandline.substring(matcher.end(2));
				matcher = controlOperatorPattern.matcher(commandline);
			}
		}

		if (result == null) {
			result = executeOperation(commandline, inRecipe);
		}

		return result;
	}

	private String writeResultToFile(String result, String fileName) {
		String ret = "";
		PrintWriter out = null;
		try {
			out = new PrintWriter(fileName.trim());
			out.println(result);
			out.close();

			return ret;
		} catch (FileNotFoundException e) {
			ret = "Error. File " + fileName + " not found or it is not possible to create it.\n";
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Throwable localThrowable2) {
				}
			}
		}
		
		return ret;
	}

	public String executeOperation(String commandline, boolean inRecipe) {
		String toExecute = replaceHistoryValues(commandline);
		toExecute = replaceEscapeCharacters(toExecute);
		Command c = Command.createOperation(toExecute);
		String result;
		try {
			if (inRecipe) {
				Main.setExecutingRecipe(true);
			}
			result = c.execute(this);
		} catch (InvalidParamsException e) {
			result = e.getMessage() + "\n";
		} finally {
			if (inRecipe) {
				Main.setExecutingRecipe(false);
			}
		}
		addResult(result);
		return result;
	}

	private String replaceEscapeCharacters(String commandline) {
		String result = commandline;
		while (result.contains("<%")) {
			String start = commandline.substring(0, commandline.indexOf("<%"));
			String toEscape = commandline.substring(commandline.indexOf("<%") + 2, commandline.indexOf("%>"));
			toEscape = toEscape.replace("\n", "");
			String end = commandline.substring(commandline.indexOf("%>") + 2, commandline.length());
			result = start + toEscape + end;
		}
		return result;
	}

	private String replaceLineBreaksInsideEscapeCharacters(String commandline) {
		String result = commandline;
		String end = result;
		while (end.contains("<%")) {
			String start = commandline.substring(0, commandline.indexOf("<%"));
			String toEscape = commandline.substring(commandline.indexOf("<%"), commandline.indexOf("%>"));
			toEscape = toEscape.replace("\n", "");
			end = commandline.substring(commandline.indexOf("%>"), commandline.length());
			result = start + toEscape + end;
		}
		return result;
	}

	private String replaceHistoryValues(String commandline) {
		String result = commandline;
		for (int i = 0; i < this.results.size(); i++) {
			result = result.replaceAll("%" + i, (String) this.results.get(i));
		}
		return result;
	}

	public State getStateHolder() {
		return this.stateHolder;
	}

	public void setStateHolder(State stateHolder) {
		this.stateHolder = stateHolder;
	}

	public String getPreviousCommandResult() {
		return this.previousCommandResult;
	}

	public void setPreviousCommandResult(String previousCommandResult) {
		this.previousCommandResult = previousCommandResult;
	}
}
