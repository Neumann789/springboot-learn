/* CutCommand - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.cli.operations;
import java.util.ArrayList;

import com.javosize.cli.Command;
import com.javosize.cli.CommandType;
import com.javosize.cli.InvalidParamsException;
import com.javosize.cli.StateHandler;

public class CutCommand extends Command
{
    private String delimiter = " ";
    private int position = 0;
    
    public CutCommand(String[] args) {
	setArgs(args);
	setType(CommandType.cut);
	position = Integer.valueOf(args[2]).intValue();
    }
    
    public String execute(StateHandler handler) throws InvalidParamsException {
	if (handler.getPreviousCommandResult() == null)
	    throw new InvalidParamsException(getManText(handler));
	validateArgs(args, handler);
	StringBuffer result = new StringBuffer();
	String textToFilter = handler.getPreviousCommandResult();
	String[] rows = textToFilter.split("\n");
	for (int i = 2; i < rows.length; i++) {
	    String[] colums = getColums(rows[i], delimiter);
	    result.append(colums[position]);
	    result.append("\n");
	}
	return result.toString();
    }
    
    private String[] getColums(String row, String delimiter) {
	ArrayList result = new ArrayList();
	String[] colums = row.split(delimiter);
	for (int i = 0; i < colums.length; i++) {
	    String value = colums[i].replaceAll(delimiter, "");
	    if (!"".equals(value))
		result.add(colums[i]);
	}
	String[] s = new String[result.size()];
	return (String[]) result.toArray(s);
    }
    
    public boolean validArgs(String[] args, StateHandler handler) {
	return args.length == 3;
    }
}
