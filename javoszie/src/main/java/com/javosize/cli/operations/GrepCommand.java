package com.javosize.cli.operations;

import com.javosize.cli.Command;
import com.javosize.cli.CommandType;
import com.javosize.cli.InvalidParamsException;
import com.javosize.cli.StateHandler;
import com.javosize.log.Log;
import com.javosize.print.Table;
import java.util.Iterator;
import java.util.List;

public class GrepCommand extends Command {
	private static Log log = new Log(GrepCommand.class.getName());

	private boolean isCaseSentive = true;

	private boolean isDiscardFilter = false;

	private String pattern = "";

	public GrepCommand(String[] args) {
		setArgs(args);
		setType(CommandType.grep);
	}

	public String execute(StateHandler handler) throws InvalidParamsException {
		if (handler.getPreviousCommandResult() == null) {
			throw new InvalidParamsException(getManText(handler));
		}
		validateArgs(this.args, handler);

		String textToFilter = handler.getPreviousCommandResult();

		String result = "\n";

		if ((!textToFilter.isEmpty()) && (!textToFilter.equals(""))) {
			if (Table.isTablePrinted(textToFilter)) {
				try {
					result = applyGrepToTable(textToFilter);
				} catch (Throwable th) {
					log.warn("Unable to apply grep using table format. Using default grep: " + th, th);
					result = applyGrepToPlainText(textToFilter);
				}
			} else {
				result = applyGrepToPlainText(textToFilter);
			}
		}

		return result;
	}

	private String applyGrepToTable(String textToFilter) throws Exception
   {
     Table table = Table.getTableFromString(textToFilter);
     
     List<String[]> rows = table.getRows();
     table.clearRows();
     
     for (Iterator localIterator = rows.iterator(); localIterator.hasNext();)
     {
       String[] rowInfo = (String[])localIterator.next();
       String filter = this.isCaseSentive ? this.pattern : this.pattern.toLowerCase();
       
       boolean added = false;
       int i = 0; if ((i < rowInfo.length) && (!added)) {
         if (((this.isDiscardFilter) && (this.isCaseSentive) && 
           (!rowInfo[i].contains(filter))) || ((this.isDiscardFilter) && (!this.isCaseSentive) && 
           (!rowInfo[i].toLowerCase().contains(filter))) || ((!this.isDiscardFilter) && (this.isCaseSentive) && 
           (rowInfo[i].contains(filter))) || ((!this.isDiscardFilter) && (!this.isCaseSentive) && 
           (rowInfo[i].toLowerCase().contains(filter))))
         {
 
           table.addRow(rowInfo);
           added = true;
         }
         i++;
       }
     }
     
 
 
 
 
 
 
 
 
 
 
     return table.toString();
   }

	private String applyGrepToPlainText(String textToFilter) {
		StringBuffer filteredTextBuffer = new StringBuffer();

		String[] lines = textToFilter.split("\\n");

		int lineNumber = 0;

		if ((lines.length >= 2) && (lines[1].trim().matches("[=]+"))) {
			filteredTextBuffer.append(lines[(lineNumber++)]);
			filteredTextBuffer.append("\n");
			filteredTextBuffer.append(lines[(lineNumber++)]);
			filteredTextBuffer.append("\n");
		}

		String filter = this.isCaseSentive ? this.pattern : this.pattern.toLowerCase();

		for (int i = lineNumber; i < lines.length; i++) {
			if (((this.isDiscardFilter) && (this.isCaseSentive) && (!lines[i].contains(filter)))
					|| ((this.isDiscardFilter) && (!this.isCaseSentive) && (!lines[i].toLowerCase().contains(filter)))
					|| ((!this.isDiscardFilter) && (this.isCaseSentive) && (lines[i].contains(filter)))
					|| ((!this.isDiscardFilter) && (!this.isCaseSentive)
							&& (lines[i].toLowerCase().contains(filter)))) {

				filteredTextBuffer.append(lines[i]);
				filteredTextBuffer.append("\n");
			}
		}

		return filteredTextBuffer.toString();
	}

	protected boolean validArgs(String[] args, StateHandler handler) {
		if (args.length == 2)
			return readPattern(args[1]);
		if ((args.length == 3) && (readGrepArguments(args[1]))) {
			return readPattern(args[2]);
		}
		return false;
	}

	private boolean readGrepArguments(String arguments) {
		if ((arguments != null) && (!arguments.equals("")) && (arguments.startsWith("-"))) {
			if (arguments.equals("-i")) {
				this.isCaseSentive = false;
				this.isDiscardFilter = false;
				return true;
			}
			if (arguments.equals("-v")) {
				this.isCaseSentive = true;
				this.isDiscardFilter = true;
				return true;
			}
			if ((arguments.equals("-iv")) || (arguments.equals("-vi"))) {
				this.isCaseSentive = false;
				this.isDiscardFilter = true;
				return true;
			}
			return false;
		}

		return false;
	}

	private boolean readPattern(String patternArgument) {
		if ((patternArgument != null) && (!patternArgument.equals("")) && (!patternArgument.startsWith("-"))) {
			this.pattern = patternArgument;
			return true;
		}
		return false;
	}
}
