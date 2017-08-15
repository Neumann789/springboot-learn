/* EnterKeyCommand - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.cli.operations;
import com.javosize.cli.Command;
import com.javosize.cli.StateHandler;

public class EnterKeyCommand extends Command
{
    public EnterKeyCommand(String[] args) {
	setArgs(args);
	setType(null);
    }
    
    public String execute(StateHandler handler) {
	return "";
    }
    
    public boolean validArgs(String[] args, StateHandler handler) {
	return true;
    }
    
    public String getManText(StateHandler handler) {
	return "\"Intro\" just returns shell prompt again.";
    }
}
